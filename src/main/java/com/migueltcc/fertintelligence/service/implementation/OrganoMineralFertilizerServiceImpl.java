package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organoMineralFertilizer.OrganoMineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organoMineralFertilizer.OrganoMineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organoMineralFertilizer.OrganoMineralFertilizerResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.OrganoMineralFertilizerModel;
import com.migueltcc.fertintelligence.repository.OrganoMineralFertilizerRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.OrganoMineralFertilizerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrganoMineralFertilizerServiceImpl implements OrganoMineralFertilizerService {

    @Autowired
    private OrganoMineralFertilizerRepository organoMineralFertilizerRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public OrganoMineralFertilizerResponseDto createOrganoMineralFertilizer(OrganoMineralFertilizerCreateRequestDto createRequestDto,
                                                                            String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        OrganoMineralFertilizerModel fertilizer = OrganoMineralFertilizerModel.builder()
                .user(owner)
                .name(createRequestDto.getName())
                .C(getOrDefault(createRequestDto.getC()))
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

        OrganoMineralFertilizerModel savedFertilizer = organoMineralFertilizerRepository.save(fertilizer);
        return savedFertilizer.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public OrganoMineralFertilizerResponseDto getOrganoMineralFertilizerById(Long organoMineralFertilizerId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        OrganoMineralFertilizerModel fertilizer = findFertilizerByIdOrThrow(organoMineralFertilizerId);
        checkOwnership(fertilizer, owner);

        return fertilizer.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganoMineralFertilizerResponseDto> getOrganoMineralFertilizersByUser(String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        return organoMineralFertilizerRepository.findAllByUser(owner).stream()
                .map(OrganoMineralFertilizerModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganoMineralFertilizerResponseDto> getOrganoMineralFertilizersByName(String name, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        return organoMineralFertilizerRepository.findAllByNameContainingIgnoreCaseAndUser(name, owner)
                .stream()
                .map(OrganoMineralFertilizerModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrganoMineralFertilizerResponseDto updateOrganoMineralFertilizer(Long organoMineralFertilizerId,
                                                                            OrganoMineralFertilizerPostRequestDto updateRequestDto,
                                                                            String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        OrganoMineralFertilizerModel fertilizer = findFertilizerByIdOrThrow(organoMineralFertilizerId);
        checkOwnership(fertilizer, owner);

        if (updateRequestDto.getName() != null) {
            fertilizer.setName(updateRequestDto.getName());
        }
        if (updateRequestDto.getC() != null) {
            fertilizer.setC(updateRequestDto.getC());
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

        OrganoMineralFertilizerModel updatedFertilizer = organoMineralFertilizerRepository.save(fertilizer);
        return updatedFertilizer.toDto();
    }

    @Override
    @Transactional
    public void deleteOrganoMineralFertilizer(Long organoMineralFertilizerId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        OrganoMineralFertilizerModel fertilizer = findFertilizerByIdOrThrow(organoMineralFertilizerId);
        checkOwnership(fertilizer, owner);

        organoMineralFertilizerRepository.delete(fertilizer);
    }

    private void checkOwnership(OrganoMineralFertilizerModel fertilizer, UserModel owner) {
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

    private OrganoMineralFertilizerModel findFertilizerByIdOrThrow(Long fertilizerId) {
        return organoMineralFertilizerRepository.findById(fertilizerId)
                .orElseThrow(() -> new EntityNotFoundException("Adubo organo-mineral não encontrado com o ID: " + fertilizerId));
    }
}