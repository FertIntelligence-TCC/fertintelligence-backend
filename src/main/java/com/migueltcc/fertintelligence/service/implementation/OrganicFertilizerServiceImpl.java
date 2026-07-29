package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organicFertilizer.OrganicFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organicFertilizer.OrganicFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organicFertilizer.OrganicFertilizerResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizerPhotos.OrganicFertilizerPhotoModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.OrganicFertilizerModel;
import com.migueltcc.fertintelligence.repository.OrganicFertilizerPhotoRepository;
import com.migueltcc.fertintelligence.repository.OrganicFertilizerRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.OrganicFertilizerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrganicFertilizerServiceImpl implements OrganicFertilizerService {

    private final OrganicFertilizerRepository organicFertilizerRepository;
    private final OrganicFertilizerPhotoRepository photoRepository;
    private final UserRepository userRepository;

    public OrganicFertilizerServiceImpl(
            OrganicFertilizerRepository organicFertilizerRepository,
            OrganicFertilizerPhotoRepository photoRepository,
            UserRepository userRepository
    ) {
        this.organicFertilizerRepository = organicFertilizerRepository;
        this.photoRepository = photoRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public OrganicFertilizerResponseDto createOrganicFertilizer(
            OrganicFertilizerCreateRequestDto dto,
            String username
    ) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        OrganicFertilizerModel fertilizer = OrganicFertilizerModel.builder()
                .user(owner)
                .name(dto.getName())
                // Nutrientes Essenciais
                .C(OrganicFertilizerModel.calcularTeorCarbonoOrganicoPercentual(dto.getTeorMateriaOrganicaPercentual()))
                .N(dto.getN())
                .P2O5(dto.getP2o5())
                .K2O(dto.getK2o())
                .Ca(getOrDefault(dto.getCa()))
                .Mg(getOrDefault(dto.getMg()))
                .S(getOrDefault(dto.getS()))
                .B(getOrDefault(dto.getB()))
                .Cu(getOrDefault(dto.getCu()))
                .Fe(getOrDefault(dto.getFe()))
                .Mn(getOrDefault(dto.getMn()))
                .Mo(getOrDefault(dto.getMo()))
                .Zn(getOrDefault(dto.getZn()))
                .teorUmidade(dto.getTeorUmidade())
                .teorMateriaOrganicaPercentual(dto.getTeorMateriaOrganicaPercentual())
                .taxaMineralizacaoPrimeiroAnoPercentual(dto.getTaxaMineralizacaoPrimeiroAnoPercentual())
                .taxaMineralizacaoSegundoAnoPercentual(dto.getTaxaMineralizacaoSegundoAnoPercentual())
                .taxaMineralizacaoTerceiroAnoPercentual(dto.getTaxaMineralizacaoTerceiroAnoPercentual())
                .taxaMineralizacaoQuartoAnoPercentual(dto.getTaxaMineralizacaoQuartoAnoPercentual())
                .arsenioMgKg(dto.getArsenioMgKg())
                .cadmioMgKg(dto.getCadmioMgKg())
                .cromioMgKg(dto.getCromioMgKg())
                .chumboMgKg(dto.getChumboMgKg())
                .mercurioMgKg(dto.getMercurioMgKg())
                .niquelMgKg(dto.getNiquelMgKg())
                .selenioMgKg(dto.getSelenioMgKg())
                .publico(Boolean.TRUE.equals(dto.getPublico()))
                .observation(dto.getObservation())
                .source(dto.getSource())
                .dataTomadaPreco(dto.getDataTomadaPreco())
                .precoSaco5Kg(dto.getPrecoSaco5Kg())
                .precoSaco25Kg(dto.getPrecoSaco25Kg())
                .precoSaco50Kg(dto.getPrecoSaco50Kg())
                .precoSaco1000Kg(dto.getPrecoSaco1000Kg())
                .valorFreteTonelada(dto.getValorFreteTonelada())
                .build();
        List<String> idsFotos = copyIdsFotos(dto.getIdsFotos());

        OrganicFertilizerModel saved = organicFertilizerRepository.save(fertilizer);
        savePhotos(saved, idsFotos);
        return toDtoWithPhotos(saved, idsFotos);
    }

    @Override
    @Transactional(readOnly = true)
    public OrganicFertilizerResponseDto getOrganicFertilizerById(Long id, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        OrganicFertilizerModel fertilizer = findFertilizerByIdOrThrow(id);
        checkReadAccess(fertilizer, owner);
        return toDtoWithPhotos(fertilizer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganicFertilizerResponseDto> getAllOrganicFertilizers(String username) {
        findUserByUsernameOrThrow(username);
        return organicFertilizerRepository.findAllByUserUsernameOrderByNameAsc(username)
                .stream()
                .map(this::toDtoWithPhotos)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganicFertilizerResponseDto> getAllPublicOrganicFertilizers(String username) {
        findUserByUsernameOrThrow(username);
        return organicFertilizerRepository.findAllByPublicoTrueAndUser_CargoNotOrderByNameAsc(Cargo.USUARIO_SUPREMO)
                .stream()
                .map(this::toDtoWithPhotos)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganicFertilizerResponseDto> getAllDefaultOrganicFertilizers(String username) {
        findUserByUsernameOrThrow(username);
        return organicFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO)
                .stream()
                .map(this::toDtoWithPhotos)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<OrganicFertilizerResponseDto> getOrganicFertilizersByName(String name, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        return organicFertilizerRepository.findAllByNameContainingIgnoreCaseAndUserOrDefaultCreator(name, owner, Cargo.USUARIO_SUPREMO)
                .stream()
                .map(this::toDtoWithPhotos)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrganicFertilizerResponseDto updateOrganicFertilizer(
            Long id,
            OrganicFertilizerPostRequestDto dto,
            String username
    ) {
        UserModel owner = findUserByUsernameOrThrow(username);
        OrganicFertilizerModel fertilizer = findFertilizerByIdOrThrow(id);
        checkOwnership(fertilizer, owner);

        if (dto.getName() != null) fertilizer.setName(dto.getName());

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

        if (dto.getTeorUmidade() != null) fertilizer.setTeorUmidade(dto.getTeorUmidade());
        if (dto.getTeorMateriaOrganicaPercentual() != null) {
            fertilizer.setTeorMateriaOrganicaPercentual(dto.getTeorMateriaOrganicaPercentual());
            fertilizer.setC(OrganicFertilizerModel.calcularTeorCarbonoOrganicoPercentual(dto.getTeorMateriaOrganicaPercentual()));
        }
        if (dto.getTaxaMineralizacaoPrimeiroAnoPercentual() != null) {
            fertilizer.setTaxaMineralizacaoPrimeiroAnoPercentual(dto.getTaxaMineralizacaoPrimeiroAnoPercentual());
        }
        if (dto.getTaxaMineralizacaoSegundoAnoPercentual() != null) {
            fertilizer.setTaxaMineralizacaoSegundoAnoPercentual(dto.getTaxaMineralizacaoSegundoAnoPercentual());
        }
        if (dto.getTaxaMineralizacaoTerceiroAnoPercentual() != null) {
            fertilizer.setTaxaMineralizacaoTerceiroAnoPercentual(dto.getTaxaMineralizacaoTerceiroAnoPercentual());
        }
        if (dto.getTaxaMineralizacaoQuartoAnoPercentual() != null) {
            fertilizer.setTaxaMineralizacaoQuartoAnoPercentual(dto.getTaxaMineralizacaoQuartoAnoPercentual());
        }
        if (dto.getArsenioMgKg() != null) fertilizer.setArsenioMgKg(dto.getArsenioMgKg());
        if (dto.getCadmioMgKg() != null) fertilizer.setCadmioMgKg(dto.getCadmioMgKg());
        if (dto.getCromioMgKg() != null) fertilizer.setCromioMgKg(dto.getCromioMgKg());
        if (dto.getChumboMgKg() != null) fertilizer.setChumboMgKg(dto.getChumboMgKg());
        if (dto.getMercurioMgKg() != null) fertilizer.setMercurioMgKg(dto.getMercurioMgKg());
        if (dto.getNiquelMgKg() != null) fertilizer.setNiquelMgKg(dto.getNiquelMgKg());
        if (dto.getSelenioMgKg() != null) fertilizer.setSelenioMgKg(dto.getSelenioMgKg());
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
        if (dto.getValorFreteTonelada() != null) fertilizer.setValorFreteTonelada(dto.getValorFreteTonelada());

        OrganicFertilizerModel saved = organicFertilizerRepository.save(fertilizer);
        if (idsFotos != null) {
            savePhotos(saved, idsFotos);
            return toDtoWithPhotos(saved, idsFotos);
        }
        return toDtoWithPhotos(saved);
    }

    @Override
    @Transactional
    public void deleteOrganicFertilizer(Long id, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        OrganicFertilizerModel fertilizer = findFertilizerByIdOrThrow(id);
        checkOwnership(fertilizer, owner);
        photoRepository.deleteAllByFertilizerId(id);
        organicFertilizerRepository.delete(fertilizer);
    }

    // --- Helpers ---

    private void checkOwnership(OrganicFertilizerModel fertilizer, UserModel owner) {
        if (!fertilizer.getUser().getId().equals(owner.getId())) {
            throw new AccessDeniedException("Acesso negado.");
        }
    }

    private void checkReadAccess(OrganicFertilizerModel fertilizer, UserModel owner) {
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

    private void savePhotos(OrganicFertilizerModel fertilizer, List<String> idsFotos) {
        photoRepository.deleteAllByFertilizerId(fertilizer.getId());
        photoRepository.flush();
        List<OrganicFertilizerPhotoModel> photos = new ArrayList<>();
        for (int i = 0; i < idsFotos.size(); i++) {
            photos.add(new OrganicFertilizerPhotoModel(fertilizer, idsFotos.get(i), i));
        }
        photoRepository.saveAll(photos);
    }

    private OrganicFertilizerResponseDto toDtoWithPhotos(OrganicFertilizerModel fertilizer) {
        List<String> idsFotos = fertilizer.getId() == null
                ? List.of()
                : photoRepository.findAllByFertilizerIdOrderByOrdemAsc(fertilizer.getId()).stream()
                        .map(OrganicFertilizerPhotoModel::getIdFoto)
                        .toList();
        return toDtoWithPhotos(fertilizer, idsFotos);
    }

    private OrganicFertilizerResponseDto toDtoWithPhotos(OrganicFertilizerModel fertilizer, List<String> idsFotos) {
        OrganicFertilizerResponseDto dto = fertilizer.toDto();
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

    private OrganicFertilizerModel findFertilizerByIdOrThrow(Long id) {
        return organicFertilizerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Adubo orgânico não encontrado com o ID: " + id));
    }
}
