package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.bioFertilizer.BioFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.bioFertilizer.BioFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.bioFertilizer.BioFertilizerResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizerPhotos.BioFertilizerPhotoModel;
import com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels.BioFertilizerModel;
import com.migueltcc.fertintelligence.repository.BioFertilizerPhotoRepository;
import com.migueltcc.fertintelligence.repository.BioFertilizerRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.BioFertilizerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BioFertilizerServiceImpl implements BioFertilizerService {

    private final BioFertilizerRepository repository;
    private final BioFertilizerPhotoRepository photoRepository;
    private final UserRepository userRepository;

    public BioFertilizerServiceImpl(
            BioFertilizerRepository repository,
            BioFertilizerPhotoRepository photoRepository,
            UserRepository userRepository
    ) {
        this.repository = repository;
        this.photoRepository = photoRepository;
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
                .densidadeGml(dto.getDensidadeGml())
                .concentracaoVolumeGl(dto.getConcentracaoVolumeGl())
                .concentracaoMassaGkg(dto.getConcentracaoMassaGkg())
                .proteinasGl(dto.getProteinasGl())
                .aminoacidosGl(dto.getAminoacidosGl())
                .amidosGl(dto.getAmidosGl())
                .acucaresGl(dto.getAcucaresGl())
                .compostosDiversosGl(dto.getCompostosDiversosGl())
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
                .publico(Boolean.TRUE.equals(dto.getPublico()))
                .observation(dto.getObservation())
                .source(dto.getSource())
                .build();
        List<String> idsFotos = copyIdsFotos(dto.getIdsFotos());

        BioFertilizerModel saved = repository.save(fertilizer);
        savePhotos(saved, idsFotos);
        return toDtoWithPhotos(saved, idsFotos);
    }

    @Override
    @Transactional(readOnly = true)
    public BioFertilizerResponseDto getBioFertilizerById(Long id, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        BioFertilizerModel fertilizer = findFertilizerByIdOrThrow(id);
        checkReadAccess(fertilizer, owner);
        return toDtoWithPhotos(fertilizer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BioFertilizerResponseDto> getAllBioFertilizers(String username) {
        findUserByUsernameOrThrow(username);
        return repository.findAllByUserUsernameOrderByNameAsc(username)
                .stream()
                .map(this::toDtoWithPhotos)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BioFertilizerResponseDto> getAllPublicBioFertilizers(String username) {
        findUserByUsernameOrThrow(username);
        return repository.findAllByPublicoTrueAndUser_CargoNotOrderByNameAsc(Cargo.USUARIO_SUPREMO)
                .stream()
                .map(this::toDtoWithPhotos)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BioFertilizerResponseDto> getAllDefaultBioFertilizers(String username) {
        findUserByUsernameOrThrow(username);
        return repository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO)
                .stream()
                .map(this::toDtoWithPhotos)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<BioFertilizerResponseDto> getBioFertilizersByName(String name, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        return repository.findAllByNameContainingIgnoreCaseAndUserOrDefaultCreator(name, owner, Cargo.USUARIO_SUPREMO)
                .stream()
                .map(this::toDtoWithPhotos)
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
        if (dto.getDensidadeGml() != null) fertilizer.setDensidadeGml(dto.getDensidadeGml());
        if (dto.getConcentracaoVolumeGl() != null) fertilizer.setConcentracaoVolumeGl(dto.getConcentracaoVolumeGl());
        if (dto.getConcentracaoMassaGkg() != null) fertilizer.setConcentracaoMassaGkg(dto.getConcentracaoMassaGkg());
        if (dto.getProteinasGl() != null) fertilizer.setProteinasGl(dto.getProteinasGl());
        if (dto.getAminoacidosGl() != null) fertilizer.setAminoacidosGl(dto.getAminoacidosGl());
        if (dto.getAmidosGl() != null) fertilizer.setAmidosGl(dto.getAmidosGl());
        if (dto.getAcucaresGl() != null) fertilizer.setAcucaresGl(dto.getAcucaresGl());
        if (dto.getCompostosDiversosGl() != null) fertilizer.setCompostosDiversosGl(dto.getCompostosDiversosGl());

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

        BioFertilizerModel saved = repository.save(fertilizer);
        if (idsFotos != null) {
            savePhotos(saved, idsFotos);
            return toDtoWithPhotos(saved, idsFotos);
        }
        return toDtoWithPhotos(saved);
    }

    @Override
    @Transactional
    public void deleteBioFertilizer(Long id, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        BioFertilizerModel fertilizer = findFertilizerByIdOrThrow(id);
        checkOwnership(fertilizer, owner);
        photoRepository.deleteAllByFertilizerId(id);
        repository.delete(fertilizer);
    }

    // --- Helpers ---

    private void checkOwnership(BioFertilizerModel fertilizer, UserModel owner) {
        if (!fertilizer.getUser().getId().equals(owner.getId())) {
            throw new AccessDeniedException("Acesso negado.");
        }
    }

    private void checkReadAccess(BioFertilizerModel fertilizer, UserModel owner) {
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

    private void savePhotos(BioFertilizerModel fertilizer, List<String> idsFotos) {
        photoRepository.deleteAllByFertilizerId(fertilizer.getId());
        photoRepository.flush();
        List<BioFertilizerPhotoModel> photos = new ArrayList<>();
        for (int i = 0; i < idsFotos.size(); i++) {
            photos.add(new BioFertilizerPhotoModel(fertilizer, idsFotos.get(i), i));
        }
        photoRepository.saveAll(photos);
    }

    private BioFertilizerResponseDto toDtoWithPhotos(BioFertilizerModel fertilizer) {
        List<String> idsFotos = fertilizer.getId() == null
                ? List.of()
                : photoRepository.findAllByFertilizerIdOrderByOrdemAsc(fertilizer.getId()).stream()
                        .map(BioFertilizerPhotoModel::getIdFoto)
                        .toList();
        return toDtoWithPhotos(fertilizer, idsFotos);
    }

    private BioFertilizerResponseDto toDtoWithPhotos(BioFertilizerModel fertilizer, List<String> idsFotos) {
        BioFertilizerResponseDto dto = fertilizer.toDto();
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

    private BioFertilizerModel findFertilizerByIdOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Adubo não encontrado."));
    }
}