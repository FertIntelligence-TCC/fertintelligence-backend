package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.Cargo; // <-- IMPORT ADICIONADO
import com.migueltcc.fertintelligence.composedAttributes.Localizacao;
import com.migueltcc.fertintelligence.dto.property.PropertyCreateRequestDto;
import com.migueltcc.fertintelligence.dto.property.PropertyPostRequestDto;
import com.migueltcc.fertintelligence.dto.property.PropertyResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.PropertyRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.PropertyService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException; // <-- IMPORT ADICIONADO
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyServiceImpl implements PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public PropertyResponseDto createProperty(PropertyCreateRequestDto createRequestDto, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        String cnpj = createRequestDto.getCnpj();
        String nome = createRequestDto.getNome();

        propertyRepository.findByCnpj(cnpj).ifPresent(p -> {
            throw new EntityExistsException("Uma propriedade com este CNPJ já existe: " + cnpj);
        });

        propertyRepository.findByNome(nome).ifPresent(p -> {
            throw new EntityExistsException("Uma propriedade com este Nome já existe: " + nome);
        });

        Localizacao localizacao = new Localizacao(
                createRequestDto.getLocalizacao().getLatitude(),
                createRequestDto.getLocalizacao().getLatitudeDirection(),
                createRequestDto.getLocalizacao().getLongitude(),
                createRequestDto.getLocalizacao().getLongitudeDirection(),
                createRequestDto.getLocalizacao().getAltitude()
        );

        PropertyModel property = PropertyModel.builder()
                .nome(nome)
                .endereco(createRequestDto.getEndereco())
                .cnpj(cnpj)
                .localizacao(localizacao)
                .owner(owner)
                .build();

        PropertyModel savedProperty = propertyRepository.save(property);
        return savedProperty.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public PropertyResponseDto getPropertyById(Long propertyId, String username) {
        UserModel requestingUser = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(requestingUser);

        PropertyModel property = findPropertyByIdOrThrow(propertyId);
        checkOwnerPermission(property, requestingUser);
        return property.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyResponseDto> getAllPropertiesByOwner(String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner); // <-- NOVA VERIFICAÇÃO DE CARGO

        return propertyRepository.findAllByOwner(owner).stream()
                .map(PropertyModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PropertyResponseDto updateProperty(Long propertyId, PropertyPostRequestDto updateRequestDto, String username) {
        UserModel requestingUser = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(requestingUser);

        PropertyModel property = findPropertyByIdOrThrow(propertyId);
        checkOwnerPermission(property, requestingUser);

        if (updateRequestDto.getNome() != null && !updateRequestDto.getNome().equals(property.getNome())) {
            propertyRepository.findByNome(updateRequestDto.getNome()).ifPresent(p -> {
                if (!p.getId().equals(propertyId)) {
                    throw new EntityExistsException("O nome '" + updateRequestDto.getNome() + "' já está em uso.");
                }
            });
            property.setNome(updateRequestDto.getNome());
        }

        if (updateRequestDto.getCnpj() != null && !property.getCnpj().equals(updateRequestDto.getCnpj())) {
            propertyRepository.findByCnpj(updateRequestDto.getCnpj()).ifPresent(p -> {
                if (!p.getId().equals(propertyId)) {
                    throw new EntityExistsException("O CNPJ '" + updateRequestDto.getCnpj() + "' já está em uso.");
                }
            });
            property.setCnpj(updateRequestDto.getCnpj());
        }

        if (updateRequestDto.getEndereco() != null) {
            property.setEndereco(updateRequestDto.getEndereco());
        }

        if (updateRequestDto.getLocalizacao() != null) {
            property.setLocalizacao(new Localizacao(
                    updateRequestDto.getLocalizacao().getLatitude(),
                    updateRequestDto.getLocalizacao().getLatitudeDirection(),
                    updateRequestDto.getLocalizacao().getLongitude(),
                    updateRequestDto.getLocalizacao().getLongitudeDirection(),
                    updateRequestDto.getLocalizacao().getAltitude()
            ));
        }

        PropertyModel updatedProperty = propertyRepository.save(property);
        return updatedProperty.toDto();
    }

    @Override
    @Transactional
    public void deleteProperty(Long propertyId, String username) {
        UserModel requestingUser = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(requestingUser); // <-- NOVA VERIFICAÇÃO DE CARGO

        PropertyModel property = findPropertyByIdOrThrow(propertyId);
        checkOwnerPermission(property, requestingUser); // Verifica se ele é dono DESSA propriedade
        propertyRepository.delete(property);
    }

    // --- Métodos Utilitários ---

    private void checkUserIsProprietario(UserModel user) {
        if (user.getCargo() != Cargo.PROPRIETARIO) {
            throw new AccessDeniedException("Acesso negado. Apenas usuários com o cargo 'PROPRIETARIO' podem gerenciar propriedades.");
        }
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private PropertyModel findPropertyByIdOrThrow(Long propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Propriedade não encontrada com o ID: " + propertyId));
    }

    private void checkOwnerPermission(PropertyModel property, UserModel requestingUser) {
        if (!property.getOwner().getId().equals(requestingUser.getId())) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este recurso.");
        }
    }
}