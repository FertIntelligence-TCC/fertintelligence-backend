package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.composedAttributes.fertilizers.NaturezaFisica;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.mineralFertilizer.MineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.mineralFertilizer.MineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.mineralFertilizer.MineralFertilizerResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizerPhotos.MineralFertilizerPhotoModel;
import com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels.MineralFertilizerModel;
import com.migueltcc.fertintelligence.repository.MineralFertilizerPhotoRepository;
import com.migueltcc.fertintelligence.repository.MineralFertilizerRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.MineralFertilizerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MineralFertilizerServiceImpl implements MineralFertilizerService {

    private static final String NON_NEGATIVE_VALUE_MESSAGE = "O valor não pode ser negativo";

    private final MineralFertilizerRepository repository;
    private final MineralFertilizerPhotoRepository photoRepository;
    private final UserRepository userRepository;

    public MineralFertilizerServiceImpl(
            MineralFertilizerRepository repository,
            MineralFertilizerPhotoRepository photoRepository,
            UserRepository userRepository
    ) {
        this.repository = repository;
        this.photoRepository = photoRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public MineralFertilizerResponseDto createMineralFertilizer(
            MineralFertilizerCreateRequestDto dto,
            String username
    ) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);
        NaturezaFisica naturezaFisica = resolveNaturezaFisica(dto.getNaturezaFisica());
        validateLiquidFields(naturezaFisica, dto.getDensidadeGml(), dto.getConcentracaoVolumeGl(), dto.getConcentracaoMassaGkg());

        MineralFertilizerModel fertilizer = MineralFertilizerModel.builder()
                .user(owner)
                .name(dto.getName())
                .naturezaFisica(naturezaFisica)
                .densidadeGml(liquidValue(naturezaFisica, dto.getDensidadeGml()))
                .concentracaoVolumeGl(liquidValue(naturezaFisica, dto.getConcentracaoVolumeGl()))
                .concentracaoMassaGkg(liquidValue(naturezaFisica, dto.getConcentracaoMassaGkg()))
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

        MineralFertilizerModel saved = repository.save(fertilizer);
        savePhotos(saved, idsFotos);
        return toDtoWithPhotos(saved, idsFotos);
    }

    @Override
    @Transactional(readOnly = true)
    public MineralFertilizerResponseDto getMineralFertilizerById(Long id, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        MineralFertilizerModel fertilizer = findFertilizerByIdOrThrow(id);
        checkReadAccess(fertilizer, owner);
        return toDtoWithPhotos(fertilizer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MineralFertilizerResponseDto> getAllMineralFertilizers(String username) {
        findUserByUsernameOrThrow(username);
        return repository.findAllByUserUsernameOrderByNameAsc(username)
                .stream()
                .map(this::toDtoWithPhotos)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MineralFertilizerResponseDto> getAllPublicMineralFertilizers(String username) {
        findUserByUsernameOrThrow(username);
        return repository.findAllByPublicoTrueOrderByNameAsc()
                .stream()
                .map(this::toDtoWithPhotos)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MineralFertilizerResponseDto> getAllDefaultMineralFertilizers(String username) {
        findUserByUsernameOrThrow(username);
        return repository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO)
                .stream()
                .map(this::toDtoWithPhotos)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<MineralFertilizerResponseDto> getMineralFertilizersByName(String name, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        return repository.findAllByNameContainingIgnoreCaseAndUserOrDefaultCreator(name, owner, Cargo.USUARIO_SUPREMO)
                .stream()
                .map(this::toDtoWithPhotos)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MineralFertilizerResponseDto updateMineralFertilizer(
            Long id,
            MineralFertilizerPostRequestDto dto,
            String username
    ) {
        UserModel owner = findUserByUsernameOrThrow(username);
        MineralFertilizerModel fertilizer = findFertilizerByIdOrThrow(id);
        checkOwnership(fertilizer, owner);

        if (dto.getName() != null) fertilizer.setName(dto.getName());
        if (dto.getNaturezaFisica() != null) fertilizer.setNaturezaFisica(dto.getNaturezaFisica());
        NaturezaFisica naturezaFisica = resolveNaturezaFisica(fertilizer.getNaturezaFisica());

        validateLiquidFields(naturezaFisica, dto.getDensidadeGml(), dto.getConcentracaoVolumeGl(), dto.getConcentracaoMassaGkg());
        if (dto.getDensidadeGml() != null) fertilizer.setDensidadeGml(dto.getDensidadeGml());
        if (dto.getConcentracaoVolumeGl() != null) fertilizer.setConcentracaoVolumeGl(dto.getConcentracaoVolumeGl());
        if (dto.getConcentracaoMassaGkg() != null) fertilizer.setConcentracaoMassaGkg(dto.getConcentracaoMassaGkg());
        clearLiquidFieldsWhenSolid(fertilizer);

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

        MineralFertilizerModel saved = repository.save(fertilizer);
        if (idsFotos != null) {
            savePhotos(saved, idsFotos);
            return toDtoWithPhotos(saved, idsFotos);
        }
        return toDtoWithPhotos(saved);
    }

    @Override
    @Transactional
    public void deleteMineralFertilizer(Long id, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        MineralFertilizerModel fertilizer = findFertilizerByIdOrThrow(id);
        checkOwnership(fertilizer, owner);
        photoRepository.deleteAllByFertilizerId(id);
        repository.delete(fertilizer);
    }

    // --- Helpers ---

    private void checkOwnership(MineralFertilizerModel fertilizer, UserModel owner) {
        if (!fertilizer.getUser().getId().equals(owner.getId())) {
            throw new AccessDeniedException("Acesso negado.");
        }
    }

    private void checkReadAccess(MineralFertilizerModel fertilizer, UserModel owner) {
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

    private void savePhotos(MineralFertilizerModel fertilizer, List<String> idsFotos) {
        photoRepository.deleteAllByFertilizerId(fertilizer.getId());
        photoRepository.flush();
        List<MineralFertilizerPhotoModel> photos = new ArrayList<>();
        for (int i = 0; i < idsFotos.size(); i++) {
            photos.add(new MineralFertilizerPhotoModel(fertilizer, idsFotos.get(i), i));
        }
        photoRepository.saveAll(photos);
    }

    private MineralFertilizerResponseDto toDtoWithPhotos(MineralFertilizerModel fertilizer) {
        List<String> idsFotos = fertilizer.getId() == null
                ? List.of()
                : photoRepository.findAllByFertilizerIdOrderByOrdemAsc(fertilizer.getId()).stream()
                        .map(MineralFertilizerPhotoModel::getIdFoto)
                        .toList();
        return toDtoWithPhotos(fertilizer, idsFotos);
    }

    private MineralFertilizerResponseDto toDtoWithPhotos(MineralFertilizerModel fertilizer, List<String> idsFotos) {
        MineralFertilizerResponseDto dto = fertilizer.toDto();
        dto.setIdsFotos(idsFotos);
        return dto;
    }

    private double getOrDefault(Double value) {
        return value != null ? value : 0.0;
    }

    private NaturezaFisica resolveNaturezaFisica(NaturezaFisica naturezaFisica) {
        return naturezaFisica != null ? naturezaFisica : NaturezaFisica.SOLIDO;
    }

    private Double liquidValue(NaturezaFisica naturezaFisica, Double value) {
        return resolveNaturezaFisica(naturezaFisica) == NaturezaFisica.LIQUIDO ? value : null;
    }

    private void validateLiquidFields(NaturezaFisica naturezaFisica, Double... values) {
        if (resolveNaturezaFisica(naturezaFisica) != NaturezaFisica.LIQUIDO) {
            return;
        }

        for (Double value : values) {
            if (value != null && value < 0.0) {
                throw new IllegalArgumentException(NON_NEGATIVE_VALUE_MESSAGE);
            }
        }
    }

    private void clearLiquidFieldsWhenSolid(MineralFertilizerModel fertilizer) {
        if (fertilizer.getNaturezaFisica() == NaturezaFisica.SOLIDO) {
            fertilizer.setDensidadeGml(null);
            fertilizer.setConcentracaoVolumeGl(null);
            fertilizer.setConcentracaoMassaGkg(null);
        }
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));
    }

    private MineralFertilizerModel findFertilizerByIdOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Adubo não encontrado."));
    }
}