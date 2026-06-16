package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organoMineralFertilizer.OrganoMineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organoMineralFertilizer.OrganoMineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organoMineralFertilizer.OrganoMineralFertilizerResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizerPhotos.OrganoMineralFertilizerPhotoModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.OrganoMineralFertilizerModel;
import com.migueltcc.fertintelligence.repository.OrganoMineralFertilizerPhotoRepository;
import com.migueltcc.fertintelligence.repository.OrganoMineralFertilizerRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.OrganoMineralFertilizerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrganoMineralFertilizerServiceImpl implements OrganoMineralFertilizerService {

    @Autowired
    private OrganoMineralFertilizerRepository repository;

    private final OrganoMineralFertilizerPhotoRepository photoRepository;

    @Autowired
    private UserRepository userRepository;

    // Injeção via Construtor (Previne NPEs na inicialização)
    public OrganoMineralFertilizerServiceImpl(
            OrganoMineralFertilizerRepository repository,
            OrganoMineralFertilizerPhotoRepository photoRepository,
            UserRepository userRepository
    ) {
        this.repository = repository;
        this.photoRepository = photoRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public OrganoMineralFertilizerResponseDto createOrganoMineralFertilizer(
            OrganoMineralFertilizerCreateRequestDto dto,
            String username
    ) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        OrganoMineralFertilizerModel fertilizer = OrganoMineralFertilizerModel.builder()
                .user(owner)
                .name(dto.getName())
                // Nutrientes Essenciais
                .C(getOrDefault(dto.getC()))
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
                .publico(Boolean.TRUE.equals(dto.getPublico()))
                .observation(dto.getObservation())
                .source(dto.getSource())
                .build();
        List<String> idsFotos = copyIdsFotos(dto.getIdsFotos());

        OrganoMineralFertilizerModel saved = repository.save(fertilizer);
        savePhotos(saved, idsFotos);
        return toDtoWithPhotos(saved, idsFotos);
    }

    @Override
    @Transactional(readOnly = true)
    public OrganoMineralFertilizerResponseDto getOrganoMineralFertilizerById(Long id, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        OrganoMineralFertilizerModel fertilizer = findFertilizerByIdOrThrow(id);
        checkReadAccess(fertilizer, owner);
        return toDtoWithPhotos(fertilizer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganoMineralFertilizerResponseDto> getAllOrganoMineralFertilizers(String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        return repository.findAllByUserOrderByNameAsc(owner)
                .stream()
                .map(this::toDtoWithPhotos)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganoMineralFertilizerResponseDto> getAllPublicOrganoMineralFertilizers(String username) {
        findUserByUsernameOrThrow(username);
        return repository.findAllByPublicoTrueOrderByNameAsc()
                .stream()
                .map(this::toDtoWithPhotos)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganoMineralFertilizerResponseDto> getAllDefaultOrganoMineralFertilizers(String username) {
        findUserByUsernameOrThrow(username);
        return repository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO)
                .stream()
                .map(this::toDtoWithPhotos)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<OrganoMineralFertilizerResponseDto> getOrganoMineralFertilizersByName(String name, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        return repository.findAllByNameContainingIgnoreCaseAndUserOrDefaultCreator(name, owner, Cargo.USUARIO_SUPREMO)
                .stream()
                .map(this::toDtoWithPhotos)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrganoMineralFertilizerResponseDto updateOrganoMineralFertilizer(
            Long id,
            OrganoMineralFertilizerPostRequestDto dto,
            String username
    ) {
        UserModel owner = findUserByUsernameOrThrow(username);
        OrganoMineralFertilizerModel fertilizer = findFertilizerByIdOrThrow(id);
        checkOwnership(fertilizer, owner);

        if (dto.getName() != null) fertilizer.setName(dto.getName());
        if (dto.getC() != null) fertilizer.setC(dto.getC());
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

        OrganoMineralFertilizerModel saved = repository.save(fertilizer);
        if (idsFotos != null) {
            photoRepository.deleteAllByFertilizerId(saved.getId());
            savePhotos(saved, idsFotos);
            return toDtoWithPhotos(saved, idsFotos);
        }
        return toDtoWithPhotos(saved);
    }

    @Override
    @Transactional
    public void deleteOrganoMineralFertilizer(Long id, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        OrganoMineralFertilizerModel fertilizer = findFertilizerByIdOrThrow(id);
        checkOwnership(fertilizer, owner);
        photoRepository.deleteAllByFertilizerId(id);
        repository.delete(fertilizer);
    }

    private void checkOwnership(OrganoMineralFertilizerModel fertilizer, UserModel owner) {
        if (!fertilizer.getUser().getId().equals(owner.getId())) {
            throw new AccessDeniedException("Acesso negado.");
        }
    }

    private void checkReadAccess(OrganoMineralFertilizerModel fertilizer, UserModel owner) {
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

    private void savePhotos(OrganoMineralFertilizerModel fertilizer, List<String> idsFotos) {
        List<OrganoMineralFertilizerPhotoModel> photos = new ArrayList<>();
        for (int i = 0; i < idsFotos.size(); i++) {
            photos.add(new OrganoMineralFertilizerPhotoModel(fertilizer, idsFotos.get(i), i));
        }
        photoRepository.saveAll(photos);
    }

    private OrganoMineralFertilizerResponseDto toDtoWithPhotos(OrganoMineralFertilizerModel fertilizer) {
        List<String> idsFotos = fertilizer.getId() == null
                ? List.of()
                : photoRepository.findAllByFertilizerIdOrderByOrdemAsc(fertilizer.getId()).stream()
                        .map(OrganoMineralFertilizerPhotoModel::getIdFoto)
                        .toList();
        return toDtoWithPhotos(fertilizer, idsFotos);
    }

    private OrganoMineralFertilizerResponseDto toDtoWithPhotos(
            OrganoMineralFertilizerModel fertilizer,
            List<String> idsFotos
    ) {
        OrganoMineralFertilizerResponseDto dto = fertilizer.toDto();
        dto.setIdsFotos(idsFotos);
        return dto;
    }

    private double getOrDefault(Double value) {
        return value != null ? value : 0.0;
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));
    }

    private OrganoMineralFertilizerModel findFertilizerByIdOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Adubo não encontrado."));
    }
}
