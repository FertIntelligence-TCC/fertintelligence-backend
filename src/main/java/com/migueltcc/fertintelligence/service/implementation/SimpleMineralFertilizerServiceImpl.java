package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer.SimpleMineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer.SimpleMineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer.SimpleMineralFertilizerResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.SimpleMineralFertilizerModel;
import com.migueltcc.fertintelligence.repository.SimpleMineralFertilizerRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.SimpleMineralFertilizerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SimpleMineralFertilizerServiceImpl implements SimpleMineralFertilizerService {

    @Autowired
    private SimpleMineralFertilizerRepository simpleMineralFertilizerRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public SimpleMineralFertilizerResponseDto createSimpleMineralFertilizer(SimpleMineralFertilizerCreateRequestDto createRequestDto,
                                                                            String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        SimpleMineralFertilizerModel fertilizer = SimpleMineralFertilizerModel.builder()
                .user(owner)
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

        SimpleMineralFertilizerModel savedFertilizer = simpleMineralFertilizerRepository.save(fertilizer);
        return savedFertilizer.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public SimpleMineralFertilizerResponseDto getSimpleMineralFertilizerById(Long simpleMineralFertilizerId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        SimpleMineralFertilizerModel fertilizer = findFertilizerByIdOrThrow(simpleMineralFertilizerId);
        checkOwnership(fertilizer, owner);

        return fertilizer.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SimpleMineralFertilizerResponseDto> getSimpleMineralFertilizersByUser(String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        return simpleMineralFertilizerRepository.findAllByUser(owner).stream()
                .map(SimpleMineralFertilizerModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SimpleMineralFertilizerResponseDto updateSimpleMineralFertilizer(Long simpleMineralFertilizerId,
                                                                            SimpleMineralFertilizerPostRequestDto updateRequestDto,
                                                                            String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        SimpleMineralFertilizerModel fertilizer = findFertilizerByIdOrThrow(simpleMineralFertilizerId);
        checkOwnership(fertilizer, owner);

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

        SimpleMineralFertilizerModel updatedFertilizer = simpleMineralFertilizerRepository.save(fertilizer);
        return updatedFertilizer.toDto();
    }

    @Override
    @Transactional
    public void deleteSimpleMineralFertilizer(Long simpleMineralFertilizerId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        SimpleMineralFertilizerModel fertilizer = findFertilizerByIdOrThrow(simpleMineralFertilizerId);
        checkOwnership(fertilizer, owner);

        simpleMineralFertilizerRepository.delete(fertilizer);
    }

    private void checkOwnership(SimpleMineralFertilizerModel fertilizer, UserModel owner) {
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

    private SimpleMineralFertilizerModel findFertilizerByIdOrThrow(Long fertilizerId) {
        return simpleMineralFertilizerRepository.findById(fertilizerId)
                .orElseThrow(() -> new EntityNotFoundException("Adubo mineral simples não encontrado com o ID: " + fertilizerId));
    }
}