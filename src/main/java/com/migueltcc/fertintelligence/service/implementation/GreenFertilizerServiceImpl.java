package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.greenFertilizer.GreenFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.greenFertilizer.GreenFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.greenFertilizer.GreenFertilizerResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizerPhotos.GreenFertilizerPhotoModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.GreenFertilizerModel;
import com.migueltcc.fertintelligence.repository.GreenFertilizerPhotoRepository;
import com.migueltcc.fertintelligence.repository.GreenFertilizerRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.GreenFertilizerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GreenFertilizerServiceImpl implements GreenFertilizerService {

    private final GreenFertilizerRepository greenFertilizerRepository;
    private final GreenFertilizerPhotoRepository photoRepository;
    private final UserRepository userRepository;

    public GreenFertilizerServiceImpl(
            GreenFertilizerRepository greenFertilizerRepository,
            GreenFertilizerPhotoRepository photoRepository,
            UserRepository userRepository
    ) {
        this.greenFertilizerRepository = greenFertilizerRepository;
        this.photoRepository = photoRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public GreenFertilizerResponseDto createGreenFertilizer(
            GreenFertilizerCreateRequestDto dto,
            String username
    ) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        GreenFertilizerModel fertilizer = GreenFertilizerModel.builder()
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
                .publico(Boolean.TRUE.equals(dto.getPublico()))
                .observation(dto.getObservation())
                .source(dto.getSource())
                .dataTomadaPreco(dto.getDataTomadaPreco())
                .precoSaco5Kg(dto.getPrecoSaco5Kg())
                .precoSaco25Kg(dto.getPrecoSaco25Kg())
                .precoSaco50Kg(dto.getPrecoSaco50Kg())
                .precoSaco1000Kg(dto.getPrecoSaco1000Kg())
                .produtividadeEsperadaKgHa(dto.getProdutividadeEsperadaKgHa())
                .taxaMineralizacaoPrimeiroAnoPercentual(dto.getTaxaMineralizacaoPrimeiroAnoPercentual())
                .taxaMineralizacaoSegundoAnoPercentual(dto.getTaxaMineralizacaoSegundoAnoPercentual())
                .taxaMineralizacaoTerceiroAnoPercentual(dto.getTaxaMineralizacaoTerceiroAnoPercentual())
                .build();
        List<String> idsFotos = copyIdsFotos(dto.getIdsFotos());

        GreenFertilizerModel saved = greenFertilizerRepository.save(fertilizer);
        savePhotos(saved, idsFotos);
        return toDtoWithPhotos(saved, idsFotos);
    }

    @Override
    @Transactional(readOnly = true)
    public GreenFertilizerResponseDto getGreenFertilizerById(Long id, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        GreenFertilizerModel fertilizer = findFertilizerByIdOrThrow(id);
        checkReadAccess(fertilizer, owner);
        return toDtoWithPhotos(fertilizer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GreenFertilizerResponseDto> getAllGreenFertilizers(String username) {
        findUserByUsernameOrThrow(username);
        return greenFertilizerRepository.findAllByUserUsernameOrderByNameAsc(username)
                .stream()
                .map(this::toDtoWithPhotos)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GreenFertilizerResponseDto> getAllPublicGreenFertilizers(String username) {
        findUserByUsernameOrThrow(username);
        return greenFertilizerRepository.findAllByPublicoTrueAndUser_CargoNotOrderByNameAsc(Cargo.USUARIO_SUPREMO)
                .stream()
                .map(this::toDtoWithPhotos)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GreenFertilizerResponseDto> getAllDefaultGreenFertilizers(String username) {
        findUserByUsernameOrThrow(username);
        return greenFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO)
                .stream()
                .map(this::toDtoWithPhotos)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<GreenFertilizerResponseDto> getGreenFertilizersByName(String name, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        return greenFertilizerRepository.findAllByNameContainingIgnoreCaseAndUserOrDefaultCreator(name, owner, Cargo.USUARIO_SUPREMO)
                .stream()
                .map(this::toDtoWithPhotos)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GreenFertilizerResponseDto updateGreenFertilizer(
            Long id,
            GreenFertilizerPostRequestDto dto,
            String username
    ) {
        UserModel owner = findUserByUsernameOrThrow(username);
        GreenFertilizerModel fertilizer = findFertilizerByIdOrThrow(id);
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
        if (dto.getProdutividadeEsperadaKgHa() != null) fertilizer.setProdutividadeEsperadaKgHa(dto.getProdutividadeEsperadaKgHa());
        if (dto.getTaxaMineralizacaoPrimeiroAnoPercentual() != null) fertilizer.setTaxaMineralizacaoPrimeiroAnoPercentual(dto.getTaxaMineralizacaoPrimeiroAnoPercentual());
        if (dto.getTaxaMineralizacaoSegundoAnoPercentual() != null) fertilizer.setTaxaMineralizacaoSegundoAnoPercentual(dto.getTaxaMineralizacaoSegundoAnoPercentual());
        if (dto.getTaxaMineralizacaoTerceiroAnoPercentual() != null) fertilizer.setTaxaMineralizacaoTerceiroAnoPercentual(dto.getTaxaMineralizacaoTerceiroAnoPercentual());

        if (dto.getNovoPublico() != null) fertilizer.setPublico(dto.getNovoPublico());
        List<String> idsFotos = null;
        if (dto.getIdsFotos() != null) {
            idsFotos = copyIdsFotos(dto.getIdsFotos());
        }
        if (dto.getObservation() != null) fertilizer.setObservation(dto.getObservation());
        if (dto.getSource() != null) fertilizer.setSource(dto.getSource());
        if (dto.getDataTomadaPreco() != null) fertilizer.setDataTomadaPreco(dto.getDataTomadaPreco());
        if (dto.getPrecoSaco5Kg() != null) fertilizer.setPrecoSaco5Kg(dto.getPrecoSaco5Kg());
        if (dto.getPrecoSaco25Kg() != null) fertilizer.setPrecoSaco25Kg(dto.getPrecoSaco25Kg());
        if (dto.getPrecoSaco50Kg() != null) fertilizer.setPrecoSaco50Kg(dto.getPrecoSaco50Kg());
        if (dto.getPrecoSaco1000Kg() != null) fertilizer.setPrecoSaco1000Kg(dto.getPrecoSaco1000Kg());

        GreenFertilizerModel saved = greenFertilizerRepository.save(fertilizer);
        if (idsFotos != null) {
            savePhotos(saved, idsFotos);
            return toDtoWithPhotos(saved, idsFotos);
        }
        return toDtoWithPhotos(saved);
    }

    @Override
    @Transactional
    public void deleteGreenFertilizer(Long id, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        GreenFertilizerModel fertilizer = findFertilizerByIdOrThrow(id);
        checkOwnership(fertilizer, owner);
        photoRepository.deleteAllByFertilizerId(id);
        greenFertilizerRepository.delete(fertilizer);
    }

    // --- Helpers ---

    private void checkOwnership(GreenFertilizerModel fertilizer, UserModel owner) {
        if (!fertilizer.getUser().getId().equals(owner.getId())) {
            throw new AccessDeniedException("Acesso negado.");
        }
    }

    private void checkReadAccess(GreenFertilizerModel fertilizer, UserModel owner) {
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

    private void savePhotos(GreenFertilizerModel fertilizer, List<String> idsFotos) {
        photoRepository.deleteAllByFertilizerId(fertilizer.getId());
        photoRepository.flush();
        List<GreenFertilizerPhotoModel> photos = new ArrayList<>();
        for (int i = 0; i < idsFotos.size(); i++) {
            photos.add(new GreenFertilizerPhotoModel(fertilizer, idsFotos.get(i), i));
        }
        photoRepository.saveAll(photos);
    }

    private GreenFertilizerResponseDto toDtoWithPhotos(GreenFertilizerModel fertilizer) {
        List<String> idsFotos = fertilizer.getId() == null
                ? List.of()
                : photoRepository.findAllByFertilizerIdOrderByOrdemAsc(fertilizer.getId()).stream()
                        .map(GreenFertilizerPhotoModel::getIdFoto)
                        .toList();
        return toDtoWithPhotos(fertilizer, idsFotos);
    }

    private GreenFertilizerResponseDto toDtoWithPhotos(GreenFertilizerModel fertilizer, List<String> idsFotos) {
        GreenFertilizerResponseDto dto = fertilizer.toDto();
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

    private GreenFertilizerModel findFertilizerByIdOrThrow(Long id) {
        return greenFertilizerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Adubo verde não encontrado com o ID: " + id));
    }
}
