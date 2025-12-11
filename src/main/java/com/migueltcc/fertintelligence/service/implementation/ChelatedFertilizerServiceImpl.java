package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.chelatedFertilizer.ChelatedFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.chelatedFertilizer.ChelatedFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.chelatedFertilizer.ChelatedFertilizerResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels.ChelatedFertilizerModel;
import com.migueltcc.fertintelligence.repository.ChelatedFertilizerRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.ChelatedFertilizerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChelatedFertilizerServiceImpl implements ChelatedFertilizerService {

    @Autowired
    private ChelatedFertilizerRepository chelatedFertilizerRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public ChelatedFertilizerResponseDto createChelatedFertilizer(ChelatedFertilizerCreateRequestDto createRequestDto,
                                                                  String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        ChelatedFertilizerModel fertilizer = ChelatedFertilizerModel.builder()
                .user(owner)
                .name(createRequestDto.getName())
                .N(getOrDefault(createRequestDto.getN()))
                .P2O5(getOrDefault(createRequestDto.getP2o5()))
                .K2O(getOrDefault(createRequestDto.getK2o()))
                .Ca(getOrDefault(createRequestDto.getCa()))
                .Mg(getOrDefault(createRequestDto.getMg()))
                .S(getOrDefault(createRequestDto.getS()))
                .B(getOrDefault(createRequestDto.getB()))
                .Cu(getOrDefault(createRequestDto.getCu()))
                .Fe(getOrDefault(createRequestDto.getFe()))
                .Mn(getOrDefault(createRequestDto.getMn()))
                .Mo(getOrDefault(createRequestDto.getMo()))
                .Zn(getOrDefault(createRequestDto.getZn()))
                .indiceSalino(getOrDefault(createRequestDto.getIndiceSalino()))
                .indiceAcidez(getOrDefault(createRequestDto.getIndiceAcidez()))
                .build();

        ChelatedFertilizerModel savedFertilizer = chelatedFertilizerRepository.save(fertilizer);
        return savedFertilizer.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public ChelatedFertilizerResponseDto getChelatedFertilizerById(Long chelatedFertilizerId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        ChelatedFertilizerModel fertilizer = findFertilizerByIdOrThrow(chelatedFertilizerId);
        checkOwnership(fertilizer, owner);

        return fertilizer.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChelatedFertilizerResponseDto> getChelatedFertilizersByUser(String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        return chelatedFertilizerRepository.findAllByUser(owner).stream()
                .map(ChelatedFertilizerModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChelatedFertilizerResponseDto> getChelatedFertilizersByName(String name, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        return chelatedFertilizerRepository.findAllByNameContainingIgnoreCaseAndUser(name, owner)
                .stream()
                .map(ChelatedFertilizerModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChelatedFertilizerResponseDto updateChelatedFertilizer(Long chelatedFertilizerId,
                                                                  ChelatedFertilizerPostRequestDto updateRequestDto,
                                                                  String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        ChelatedFertilizerModel fertilizer = findFertilizerByIdOrThrow(chelatedFertilizerId);
        checkOwnership(fertilizer, owner);

        if (updateRequestDto.getName() != null) {
            fertilizer.setName(updateRequestDto.getName());
        }
        if (updateRequestDto.getN() != null) {
            fertilizer.setN(updateRequestDto.getN());
        }
        if (updateRequestDto.getP2o5() != null) {
            fertilizer.setP2O5(updateRequestDto.getP2o5());
        }
        if (updateRequestDto.getK2o() != null) {
            fertilizer.setK2O(updateRequestDto.getK2o());
        }
        if (updateRequestDto.getCa() != null) {
            fertilizer.setCa(updateRequestDto.getCa());
        }
        if (updateRequestDto.getMg() != null) {
            fertilizer.setMg(updateRequestDto.getMg());
        }
        if (updateRequestDto.getS() != null) {
            fertilizer.setS(updateRequestDto.getS());
        }
        if (updateRequestDto.getB() != null) {
            fertilizer.setB(updateRequestDto.getB());
        }
        if (updateRequestDto.getCu() != null) {
            fertilizer.setCu(updateRequestDto.getCu());
        }
        if (updateRequestDto.getFe() != null) {
            fertilizer.setFe(updateRequestDto.getFe());
        }
        if (updateRequestDto.getMn() != null) {
            fertilizer.setMn(updateRequestDto.getMn());
        }
        if (updateRequestDto.getMo() != null) {
            fertilizer.setMo(updateRequestDto.getMo());
        }
        if (updateRequestDto.getZn() != null) {
            fertilizer.setZn(updateRequestDto.getZn());
        }
        if (updateRequestDto.getIndiceSalino() != null) {
            fertilizer.setIndiceSalino(updateRequestDto.getIndiceSalino());
        }
        if (updateRequestDto.getIndiceAcidez() != null) {
            fertilizer.setIndiceAcidez(updateRequestDto.getIndiceAcidez());
        }

        ChelatedFertilizerModel updatedFertilizer = chelatedFertilizerRepository.save(fertilizer);
        return updatedFertilizer.toDto();
    }

    @Override
    @Transactional
    public void deleteChelatedFertilizer(Long chelatedFertilizerId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        ChelatedFertilizerModel fertilizer = findFertilizerByIdOrThrow(chelatedFertilizerId);
        checkOwnership(fertilizer, owner);

        chelatedFertilizerRepository.delete(fertilizer);
    }

    private void checkOwnership(ChelatedFertilizerModel fertilizer, UserModel owner) {
        if (!fertilizer.getUser().getId().equals(owner.getId())) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este recurso.");
        }
    }

    private void checkUserRole(UserModel user) {
        if (user.getCargo() != Cargo.PROPRIETARIO
                && user.getCargo() != Cargo.GERENTE
                && user.getCargo() != Cargo.AGRONOMO_RESIDENTE
                && user.getCargo() != Cargo.AGRONOMO_CONSULTOR
                && user.getCargo() != Cargo.SECRETARIO
                && user.getCargo() != Cargo.SUPERVISOR_DE_AREA) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este recurso.");
        }
    }

    private double getOrDefault(Double value) {
        return value != null ? value : 0.0;
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private ChelatedFertilizerModel findFertilizerByIdOrThrow(Long fertilizerId) {
        return chelatedFertilizerRepository.findById(fertilizerId)
                .orElseThrow(() -> new EntityNotFoundException("Adubo quelatado não encontrado com o ID: " + fertilizerId));
    }
}