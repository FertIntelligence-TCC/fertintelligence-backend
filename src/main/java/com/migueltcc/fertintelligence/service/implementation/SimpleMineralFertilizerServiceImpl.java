package com.migueltcc.fertintelligence.service.implementation;

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
    public SimpleMineralFertilizerResponseDto createSimpleMineralFertilizer(
            SimpleMineralFertilizerCreateRequestDto createRequestDto,
            String username
    ) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        SimpleMineralFertilizerModel fertilizer = SimpleMineralFertilizerModel.builder()
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
                .publico(Boolean.TRUE.equals(createRequestDto.getPublico()))
                .build();

        SimpleMineralFertilizerModel saved = simpleMineralFertilizerRepository.save(fertilizer);
        return saved.toDto();
    }

    // CORREÇÃO: Nome do método atualizado para getAllSimpleMineralFertilizers
    @Override
    @Transactional(readOnly = true)
    public List<SimpleMineralFertilizerResponseDto> getAllSimpleMineralFertilizers(String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        return simpleMineralFertilizerRepository.findAllByUser(owner)
                .stream()
                .map(SimpleMineralFertilizerModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SimpleMineralFertilizerResponseDto> getAllPublicSimpleMineralFertilizers(String username) {
        findUserByUsernameOrThrow(username);
        return simpleMineralFertilizerRepository.findAllByPublicoTrueOrderByNameAsc()
                .stream()
                .map(SimpleMineralFertilizerModel::toDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<SimpleMineralFertilizerResponseDto> getSimpleMineralFertilizersByName(String name, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        return simpleMineralFertilizerRepository.findAllByNameContainingIgnoreCaseAndUser(name, owner)
                .stream()
                .map(SimpleMineralFertilizerModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SimpleMineralFertilizerResponseDto updateSimpleMineralFertilizer(
            Long fertilizerId,
            SimpleMineralFertilizerPostRequestDto dto,
            String username
    ) {
        UserModel owner = findUserByUsernameOrThrow(username);
        SimpleMineralFertilizerModel fertilizer = findFertilizerByIdOrThrow(fertilizerId);

        checkOwnership(fertilizer, owner);

        if (dto.getName() != null) fertilizer.setName(dto.getName());
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
        if (dto.getIndiceSalino() != null) fertilizer.setIndiceSalino(dto.getIndiceSalino());
        if (dto.getIndiceAcidez() != null) fertilizer.setIndiceAcidez(dto.getIndiceAcidez());
        if (dto.getNovoPublico() != null) fertilizer.setPublico(dto.getNovoPublico());

        SimpleMineralFertilizerModel updated = simpleMineralFertilizerRepository.save(fertilizer);
        return updated.toDto();
    }

    @Override
    @Transactional
    public void deleteSimpleMineralFertilizer(Long fertilizerId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        SimpleMineralFertilizerModel fertilizer = findFertilizerByIdOrThrow(fertilizerId);

        checkOwnership(fertilizer, owner);

        simpleMineralFertilizerRepository.delete(fertilizer);
    }

    private void checkOwnership(SimpleMineralFertilizerModel fertilizer, UserModel owner) {
        if (!fertilizer.getUser().getId().equals(owner.getId())) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este recurso.");
        }
    }

    private void checkUserRole(UserModel user) {
        if (!user.getCargo().canAccessGeneralResources()) {
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
