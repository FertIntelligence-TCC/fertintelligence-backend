package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organicFertilizer.OrganicFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organicFertilizer.OrganicFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organicFertilizer.OrganicFertilizerResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.OrganicFertilizerModel;
import com.migueltcc.fertintelligence.repository.OrganicFertilizerRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.OrganicFertilizerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrganicFertilizerServiceImpl implements OrganicFertilizerService {

    private final OrganicFertilizerRepository organicFertilizerRepository;
    private final UserRepository userRepository;

    public OrganicFertilizerServiceImpl(OrganicFertilizerRepository organicFertilizerRepository, UserRepository userRepository) {
        this.organicFertilizerRepository = organicFertilizerRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public OrganicFertilizerResponseDto createOrganicFertilizer(
            OrganicFertilizerCreateRequestDto dto,
            String username
    ) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        OrganicFertilizerModel fertilizer = OrganicFertilizerModel.builder()
                .user(owner)
                .name(dto.getName())
                // Nutrientes Essenciais
                .C(getOrDefault(dto.getC()))
                .N(getOrDefault(dto.getN()))
                .P2O5(getOrDefault(dto.getP2o5()))
                .K2O(getOrDefault(dto.getK2o()))
                .Ca(getOrDefault(dto.getCa()))
                .Mg(getOrDefault(dto.getMg()))
                .S(getOrDefault(dto.getS()))
                .B(getOrDefault(dto.getB()))
                .Cu(getOrDefault(dto.getCu()))
                .Fe(getOrDefault(dto.getFe()))
                .Mn(getOrDefault(dto.getMn()))
                .Mo(getOrDefault(dto.getMo()))
                .Zn(getOrDefault(dto.getZn()))
                .teorUmidade(getOrDefault(dto.getTeorUmidade()))
                .teorCinzas(getOrDefault(dto.getTeorCinzas()))
                .publico(Boolean.TRUE.equals(dto.getPublico()))
                .idsFotos(copyIdsFotos(dto.getIdsFotos()))
                .observation(dto.getObservation())
                .source(dto.getSource())
                .build();

        return organicFertilizerRepository.save(fertilizer).toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public OrganicFertilizerResponseDto getOrganicFertilizerById(Long id, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        OrganicFertilizerModel fertilizer = findFertilizerByIdOrThrow(id);
        checkReadAccess(fertilizer, owner);
        return fertilizer.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganicFertilizerResponseDto> getAllOrganicFertilizers(String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        return organicFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(owner)
                .stream()
                .map(OrganicFertilizerModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganicFertilizerResponseDto> getAllPublicOrganicFertilizers(String username) {
        findUserByUsernameOrThrow(username);
        return organicFertilizerRepository.findAllByPublicoTrueOrderByNameAsc()
                .stream()
                .map(OrganicFertilizerModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganicFertilizerResponseDto> getAllDefaultOrganicFertilizers(String username) {
        findUserByUsernameOrThrow(username);
        return organicFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO)
                .stream()
                .map(OrganicFertilizerModel::toDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<OrganicFertilizerResponseDto> getOrganicFertilizersByName(String name, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        return organicFertilizerRepository.findAllByNameContainingIgnoreCaseAndUserOrDefaultCreator(name, owner, Cargo.USUARIO_SUPREMO)
                .stream()
                .map(OrganicFertilizerModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrganicFertilizerResponseDto updateOrganicFertilizer(
            Long id,
            OrganicFertilizerPostRequestDto dto,
            String username
    ) {
        UserModel owner = findUserByUsernameOrThrow(username);
        OrganicFertilizerModel fertilizer = findFertilizerByIdOrThrow(id);
        checkOwnership(fertilizer, owner);

        if (dto.getName() != null) fertilizer.setName(dto.getName());
        if (dto.getC() != null) fertilizer.setC(dto.getC());

        // Updates de Nutrientes
        if (dto.getN() != null) fertilizer.setN(dto.getN());
        if (dto.getP2o5() != null) fertilizer.setP2O5(dto.getP2o5());
        if (dto.getK2o() != null) fertilizer.setK2O(dto.getK2o());
        if (dto.getCa() != null) fertilizer.setCa(dto.getCa());
        if (dto.getMg() != null) fertilizer.setMg(dto.getMg());
        if (dto.getS() != null) fertilizer.setS(dto.getS());
        if (dto.getB() != null) fertilizer.setB(dto.getB());
        if (dto.getCu() != null) fertilizer.setCu(dto.getCu());
        if (dto.getFe() != null) fertilizer.setFe(dto.getFe());
        if (dto.getMn() != null) fertilizer.setMn(dto.getMn());
        if (dto.getMo() != null) fertilizer.setMo(dto.getMo());
        if (dto.getZn() != null) fertilizer.setZn(dto.getZn());

        if (dto.getTeorUmidade() != null) fertilizer.setTeorUmidade(dto.getTeorUmidade());
        if (dto.getTeorCinzas() != null) fertilizer.setTeorCinzas(dto.getTeorCinzas());
        if (dto.getNovoPublico() != null) fertilizer.setPublico(dto.getNovoPublico());
        if (dto.getIdsFotos() != null) fertilizer.setIdsFotos(copyIdsFotos(dto.getIdsFotos()));
        if (dto.getObservation() != null) fertilizer.setObservation(dto.getObservation());
        if (dto.getSource() != null) fertilizer.setSource(dto.getSource());

        return organicFertilizerRepository.save(fertilizer).toDto();
    }

    @Override
    @Transactional
    public void deleteOrganicFertilizer(Long id, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        OrganicFertilizerModel fertilizer = findFertilizerByIdOrThrow(id);
        checkOwnership(fertilizer, owner);
        organicFertilizerRepository.delete(fertilizer);
    }

    // --- Helpers ---

    private void checkOwnership(OrganicFertilizerModel fertilizer, UserModel owner) {
        if (!fertilizer.getUser().getId().equals(owner.getId())) {
            throw new AccessDeniedException("Acesso negado.");
        }
    }

    private void checkReadAccess(OrganicFertilizerModel fertilizer, UserModel owner) {
        if (!fertilizer.getUser().getId().equals(owner.getId())
                && !Boolean.TRUE.equals(fertilizer.getPublico())
                && fertilizer.getUser().getCargo() != Cargo.USUARIO_SUPREMO) {
            throw new AccessDeniedException("Acesso negado.");
        }
    }

    private void checkUserRole(UserModel user) {
        if (user.getCargo() != Cargo.USUARIO_SUPREMO && user.getCargo() != Cargo.PROPRIETARIO && user.getCargo() != Cargo.GERENTE) {
            throw new AccessDeniedException("Permissão insuficiente.");
        }
    }

    private List<String> copyIdsFotos(List<String> idsFotos) {
        if (idsFotos == null) {
            return new ArrayList<>();
        }
        if (idsFotos.size() > 5) {
            throw new IllegalArgumentException("Um adubo pode ter no máximo 5 fotos");
        }
        return new ArrayList<>(idsFotos);
    }

    private double getOrDefault(Double value) {
        return value != null ? value : 0.0;
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private OrganicFertilizerModel findFertilizerByIdOrThrow(Long id) {
        return organicFertilizerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Adubo orgânico não encontrado com o ID: " + id));
    }
}
