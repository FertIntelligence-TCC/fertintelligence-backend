package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer.SimpleMineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer.SimpleMineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer.SimpleMineralFertilizerResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizerPhotos.SimpleMineralFertilizerPhotoModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.SimpleMineralFertilizerModel;
import com.migueltcc.fertintelligence.repository.SimpleMineralFertilizerPhotoRepository;
import com.migueltcc.fertintelligence.repository.SimpleMineralFertilizerRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.SimpleMineralFertilizerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SimpleMineralFertilizerServiceImpl implements SimpleMineralFertilizerService {

    @Autowired
    private SimpleMineralFertilizerRepository simpleMineralFertilizerRepository;

    @Autowired
    private SimpleMineralFertilizerPhotoRepository photoRepository;

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
                .observation(createRequestDto.getObservation())
                .source(createRequestDto.getSource())
                .build();
        List<String> idsFotos = copyIdsFotos(createRequestDto.getIdsFotos());

        SimpleMineralFertilizerModel saved = simpleMineralFertilizerRepository.save(fertilizer);
        savePhotos(saved, idsFotos);
        return toDtoWithPhotos(saved, idsFotos);
    }

    // CORREÇÃO: Nome do método atualizado para getAllSimpleMineralFertilizers
    @Override
    @Transactional(readOnly = true)
    public List<SimpleMineralFertilizerResponseDto> getAllSimpleMineralFertilizers(String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        return simpleMineralFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(owner)
                .stream()
                .map(this::toDtoWithPhotos)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SimpleMineralFertilizerResponseDto> getAllPublicSimpleMineralFertilizers(String username) {
        findUserByUsernameOrThrow(username);
        return simpleMineralFertilizerRepository.findAllByPublicoTrueOrderByNameAsc()
                .stream()
                .map(this::toDtoWithPhotos)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SimpleMineralFertilizerResponseDto> getAllDefaultSimpleMineralFertilizers(String username) {
        findUserByUsernameOrThrow(username);
        return simpleMineralFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO)
                .stream()
                .map(this::toDtoWithPhotos)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<SimpleMineralFertilizerResponseDto> getSimpleMineralFertilizersByName(String name, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        return simpleMineralFertilizerRepository.findAllByNameContainingIgnoreCaseAndUserOrDefaultCreator(name, owner, Cargo.USUARIO_SUPREMO)
                .stream()
                .map(this::toDtoWithPhotos)
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
        List<String> idsFotos = null;
        if (dto.getIdsFotos() != null) {
            idsFotos = copyIdsFotos(dto.getIdsFotos());
        }
        if (dto.getObservation() != null) fertilizer.setObservation(dto.getObservation());
        if (dto.getSource() != null) fertilizer.setSource(dto.getSource());

        SimpleMineralFertilizerModel updated = simpleMineralFertilizerRepository.save(fertilizer);
        if (idsFotos != null) {
            photoRepository.deleteAllByFertilizerId(updated.getId());
            savePhotos(updated, idsFotos);
            return toDtoWithPhotos(updated, idsFotos);
        }
        return toDtoWithPhotos(updated);
    }

    @Override
    @Transactional
    public void deleteSimpleMineralFertilizer(Long fertilizerId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        SimpleMineralFertilizerModel fertilizer = findFertilizerByIdOrThrow(fertilizerId);

        checkOwnership(fertilizer, owner);

        photoRepository.deleteAllByFertilizerId(fertilizerId);
        simpleMineralFertilizerRepository.delete(fertilizer);
    }

    private void checkOwnership(SimpleMineralFertilizerModel fertilizer, UserModel owner) {
        if (!fertilizer.getUser().getId().equals(owner.getId())) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este recurso.");
        }
    }

    private void checkUserRole(UserModel user) {
        if (user.getCargo() != Cargo.USUARIO_SUPREMO
                && user.getCargo() != Cargo.PROPRIETARIO
                && user.getCargo() != Cargo.GERENTE
                && user.getCargo() != Cargo.AGRONOMO_RESIDENTE
                && user.getCargo() != Cargo.AGRONOMO_CONSULTOR
                && user.getCargo() != Cargo.SECRETARIO
                && user.getCargo() != Cargo.SUPERVISOR_DE_AREA) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este recurso.");
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

    private void savePhotos(SimpleMineralFertilizerModel fertilizer, List<String> idsFotos) {
        List<SimpleMineralFertilizerPhotoModel> photos = new ArrayList<>();
        for (int i = 0; i < idsFotos.size(); i++) {
            photos.add(new SimpleMineralFertilizerPhotoModel(fertilizer, idsFotos.get(i), i));
        }
        photoRepository.saveAll(photos);
    }

    private SimpleMineralFertilizerResponseDto toDtoWithPhotos(SimpleMineralFertilizerModel fertilizer) {
        List<String> idsFotos = fertilizer.getId() == null
                ? List.of()
                : photoRepository.findAllByFertilizerIdOrderByOrdemAsc(fertilizer.getId()).stream()
                        .map(SimpleMineralFertilizerPhotoModel::getIdFoto)
                        .toList();
        return toDtoWithPhotos(fertilizer, idsFotos);
    }

    private SimpleMineralFertilizerResponseDto toDtoWithPhotos(SimpleMineralFertilizerModel fertilizer, List<String> idsFotos) {
        SimpleMineralFertilizerResponseDto dto = fertilizer.toDto();
        dto.setIdsFotos(idsFotos);
        return dto;
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
