package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.bioFertilizer.BioFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.bioFertilizer.BioFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.bioFertilizer.BioFertilizerResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels.BioFertilizerModel;
import com.migueltcc.fertintelligence.repository.BioFertilizerRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.BioFertilizerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BioFertilizerServiceImpl implements BioFertilizerService {

    private final BioFertilizerRepository repository;
    private final UserRepository userRepository;

    public BioFertilizerServiceImpl(BioFertilizerRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public BioFertilizerResponseDto createBioFertilizer(
            BioFertilizerCreateRequestDto dto,
            String username
    ) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        BioFertilizerModel fertilizer = BioFertilizerModel.builder()
                .user(owner)
                .name(dto.getName())
                // Macros
                .N(getOrDefault(dto.getN()))
                .P2O5(getOrDefault(dto.getP2o5()))
                .K2O(getOrDefault(dto.getK2o()))
                // Secundários
                .Ca(getOrDefault(dto.getCa()))
                .Mg(getOrDefault(dto.getMg()))
                .S(getOrDefault(dto.getS()))
                // Micros
                .B(getOrDefault(dto.getB()))
                .Cu(getOrDefault(dto.getCu()))
                .Fe(getOrDefault(dto.getFe()))
                .Mn(getOrDefault(dto.getMn()))
                .Mo(getOrDefault(dto.getMo()))
                .Zn(getOrDefault(dto.getZn()))
                // Índices
                .indiceSalino(getOrDefault(dto.getIndiceSalino()))
                .indiceAcidez(getOrDefault(dto.getIndiceAcidez()))
                .build();

        return repository.save(fertilizer).toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public BioFertilizerResponseDto getBioFertilizerById(Long id, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        BioFertilizerModel fertilizer = findFertilizerByIdOrThrow(id);
        checkOwnership(fertilizer, owner);
        return fertilizer.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BioFertilizerResponseDto> getAllBioFertilizers(String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        return repository.findAllByUser(owner)
                .stream()
                .map(BioFertilizerModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BioFertilizerResponseDto> getBioFertilizersByName(String name, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        return repository.findAllByNameContainingIgnoreCaseAndUser(name, owner)
                .stream()
                .map(BioFertilizerModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BioFertilizerResponseDto updateBioFertilizer(
            Long id,
            BioFertilizerPostRequestDto dto,
            String username
    ) {
        UserModel owner = findUserByUsernameOrThrow(username);
        BioFertilizerModel fertilizer = findFertilizerByIdOrThrow(id);
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

        return repository.save(fertilizer).toDto();
    }

    @Override
    @Transactional
    public void deleteBioFertilizer(Long id, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        BioFertilizerModel fertilizer = findFertilizerByIdOrThrow(id);
        checkOwnership(fertilizer, owner);
        repository.delete(fertilizer);
    }

    // --- Helpers ---

    private void checkOwnership(BioFertilizerModel fertilizer, UserModel owner) {
        if (!fertilizer.getUser().getId().equals(owner.getId())) {
            throw new AccessDeniedException("Acesso negado.");
        }
    }

    private void checkUserRole(UserModel user) {
        if (user.getCargo() != Cargo.PROPRIETARIO && user.getCargo() != Cargo.GERENTE) {
            throw new AccessDeniedException("Permissão insuficiente.");
        }
    }

    private double getOrDefault(Double value) {
        return value != null ? value : 0.0;
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));
    }

    private BioFertilizerModel findFertilizerByIdOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Adubo não encontrado."));
    }
}