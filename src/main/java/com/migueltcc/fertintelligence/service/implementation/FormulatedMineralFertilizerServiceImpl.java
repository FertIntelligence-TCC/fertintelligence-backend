package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.fertilizers.Formulate;
import com.migueltcc.fertintelligence.composedAttributes.fertilizers.NPKrelation;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer.FormulatedMineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer.FormulatedMineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer.FormulatedMineralFertilizerResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizerPhotos.FormulatedMineralFertilizerPhotoModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.FormulatedMineralFertilizerModel;
import com.migueltcc.fertintelligence.repository.FormulatedMineralFertilizerPhotoRepository;
import com.migueltcc.fertintelligence.repository.FormulatedMineralFertilizerRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.FormulatedMineralFertilizerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FormulatedMineralFertilizerServiceImpl implements FormulatedMineralFertilizerService {

    private static final int MINIMUM_NPK_SUM = 24;
    private static final String MINIMUM_NPK_SUM_MESSAGE =
            "A soma dos valores de NPK deve ser de pelo menos 24.";

    private final FormulatedMineralFertilizerRepository formulatedMineralFertilizerRepository;
    private final FormulatedMineralFertilizerPhotoRepository photoRepository;
    private final UserRepository userRepository;

    public FormulatedMineralFertilizerServiceImpl(
            FormulatedMineralFertilizerRepository formulatedMineralFertilizerRepository,
            FormulatedMineralFertilizerPhotoRepository photoRepository,
            UserRepository userRepository) {
        this.formulatedMineralFertilizerRepository = formulatedMineralFertilizerRepository;
        this.photoRepository = photoRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public FormulatedMineralFertilizerResponseDto createFormulatedMineralFertilizer(
            FormulatedMineralFertilizerCreateRequestDto dto,
            String username
    ) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        // Mapear Objetos Embutidos
        Formulate formulate = new Formulate();
        if (dto.getFormulate() != null) {
            formulate.setN(getOrDefault(dto.getFormulate().getN()));
            formulate.setP(getOrDefault(dto.getFormulate().getP()));
            formulate.setK(getOrDefault(dto.getFormulate().getK()));
        }
        validateFormulateNpkSum(formulate);
        validatePrimaryNpkSumWhenProvided(dto.getN(), dto.getP2o5(), dto.getK2o());

        NPKrelation relation = FormulatedMineralFertilizerModel.calculateRelation(formulate);

        FormulatedMineralFertilizerModel fertilizer = FormulatedMineralFertilizerModel.builder()
                .user(owner)
                .formulate(formulate)
                .relation(relation)
                .indicatedFormulaNumber(dto.getIndicatedFormulaNumber())
                .N(getOrDefault(dto.getN()))
                .P2O5(getOrDefault(dto.getP2o5()))
                .K2O(getOrDefault(dto.getK2o()))
                .Ca(getOrDefault(dto.getCa())).Mg(getOrDefault(dto.getMg())).S(getOrDefault(dto.getS()))
                .B(getOrDefault(dto.getB())).Cu(getOrDefault(dto.getCu())).Fe(getOrDefault(dto.getFe()))
                .Mn(getOrDefault(dto.getMn())).Mo(getOrDefault(dto.getMo())).Zn(getOrDefault(dto.getZn()))
                .publico(Boolean.TRUE.equals(dto.getPublico()))
                .observation(dto.getObservation())
                .source(dto.getSource())
                .build();
        List<String> idsFotos = copyIdsFotos(dto.getIdsFotos());

        FormulatedMineralFertilizerModel saved = formulatedMineralFertilizerRepository.save(fertilizer);
        savePhotos(saved, idsFotos);
        return toDtoWithPhotos(saved, idsFotos);
    }

    @Override
    @Transactional(readOnly = true)
    public FormulatedMineralFertilizerResponseDto getFormulatedMineralFertilizerById(Long id, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        FormulatedMineralFertilizerModel fertilizer = findFertilizerByIdOrThrow(id);
        checkReadAccess(fertilizer, owner);
        return toDtoWithPhotos(fertilizer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormulatedMineralFertilizerResponseDto> getAllFormulatedMineralFertilizers(String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        return formulatedMineralFertilizerRepository.findAllByUserOrderByFormulaAsc(owner)
                .stream()
                .map(this::toDtoWithPhotos)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormulatedMineralFertilizerResponseDto> getAllPublicFormulatedMineralFertilizers(String username) {
        findUserByUsernameOrThrow(username);
        return formulatedMineralFertilizerRepository.findAllByPublicoTrueOrderByIdAsc()
                .stream()
                .map(this::toDtoWithPhotos)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormulatedMineralFertilizerResponseDto> getAllDefaultFormulatedMineralFertilizers(String username) {
        findUserByUsernameOrThrow(username);
        return formulatedMineralFertilizerRepository.findAllByUser_CargoOrderByIdAsc(Cargo.USUARIO_SUPREMO)
                .stream()
                .map(this::toDtoWithPhotos)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public FormulatedMineralFertilizerResponseDto updateFormulatedMineralFertilizer(
            Long id,
            FormulatedMineralFertilizerPostRequestDto dto,
            String username
    ) {
        UserModel owner = findUserByUsernameOrThrow(username);
        FormulatedMineralFertilizerModel fertilizer = findFertilizerByIdOrThrow(id);
        checkOwnership(fertilizer, owner);

        if (dto.getFormulate() != null) {
            if (fertilizer.getFormulate() == null) fertilizer.setFormulate(new Formulate());
            fertilizer.getFormulate().setN(getOrDefault(dto.getFormulate().getN()));
            fertilizer.getFormulate().setP(getOrDefault(dto.getFormulate().getP()));
            fertilizer.getFormulate().setK(getOrDefault(dto.getFormulate().getK()));
            fertilizer.setRelation(FormulatedMineralFertilizerModel.calculateRelation(fertilizer.getFormulate()));
        }

        if (dto.getFormulate() == null && dto.getRelation() != null) {
            if (fertilizer.getRelation() == null) fertilizer.setRelation(new NPKrelation());
            fertilizer.getRelation().setN(getOrDefault(dto.getRelation().getN()));
            fertilizer.getRelation().setP(getOrDefault(dto.getRelation().getP()));
            fertilizer.getRelation().setK(getOrDefault(dto.getRelation().getK()));
        }

        if (dto.getIndicatedFormulaNumber() != null) fertilizer.setIndicatedFormulaNumber(dto.getIndicatedFormulaNumber());

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
        if (dto.getNovoPublico() != null) fertilizer.setPublico(dto.getNovoPublico());
        List<String> idsFotos = null;
        if (dto.getIdsFotos() != null) {
            idsFotos = copyIdsFotos(dto.getIdsFotos());
        }
        if (dto.getObservation() != null) fertilizer.setObservation(dto.getObservation());
        if (dto.getSource() != null) fertilizer.setSource(dto.getSource());

        validateFormulateNpkSum(fertilizer.getFormulate());
        validatePrimaryNpkSum(fertilizer.getN(), fertilizer.getP2O5(), fertilizer.getK2O());

        FormulatedMineralFertilizerModel updated = formulatedMineralFertilizerRepository.save(fertilizer);
        if (idsFotos != null) {
            photoRepository.deleteAllByFertilizerId(updated.getId());
            savePhotos(updated, idsFotos);
            return toDtoWithPhotos(updated, idsFotos);
        }
        return toDtoWithPhotos(updated);
    }

    @Override
    @Transactional
    public void deleteFormulatedMineralFertilizer(Long id, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        FormulatedMineralFertilizerModel fertilizer = findFertilizerByIdOrThrow(id);
        checkOwnership(fertilizer, owner);
        photoRepository.deleteAllByFertilizerId(id);
        formulatedMineralFertilizerRepository.delete(fertilizer);
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

    private void savePhotos(FormulatedMineralFertilizerModel fertilizer, List<String> idsFotos) {
        List<FormulatedMineralFertilizerPhotoModel> photos = new ArrayList<>();
        for (int i = 0; i < idsFotos.size(); i++) {
            photos.add(new FormulatedMineralFertilizerPhotoModel(fertilizer, idsFotos.get(i), i));
        }
        photoRepository.saveAll(photos);
    }

    private FormulatedMineralFertilizerResponseDto toDtoWithPhotos(FormulatedMineralFertilizerModel fertilizer) {
        List<String> idsFotos = fertilizer.getId() == null
                ? List.of()
                : photoRepository.findAllByFertilizerIdOrderByOrdemAsc(fertilizer.getId()).stream()
                        .map(FormulatedMineralFertilizerPhotoModel::getIdFoto)
                        .toList();
        return toDtoWithPhotos(fertilizer, idsFotos);
    }

    private FormulatedMineralFertilizerResponseDto toDtoWithPhotos(
            FormulatedMineralFertilizerModel fertilizer,
            List<String> idsFotos
    ) {
        FormulatedMineralFertilizerResponseDto dto = fertilizer.toDto();
        dto.setIdsFotos(idsFotos);
        return dto;
    }

    private double getOrDefault(Double value) {
        return value != null ? value : 0.0;
    }

    private int getOrDefault(Integer value) {
        return value != null ? value : 0;
    }

    private void validateFormulateNpkSum(Formulate formulate) {
        int sum = formulate != null
                ? formulate.getN() + formulate.getP() + formulate.getK()
                : 0;
        if (sum < MINIMUM_NPK_SUM) {
            throw new IllegalArgumentException(MINIMUM_NPK_SUM_MESSAGE);
        }
    }

    private void validatePrimaryNpkSumWhenProvided(Double n, Double p2o5, Double k2o) {
        if (n != null || p2o5 != null || k2o != null) {
            validatePrimaryNpkSum(getOrDefault(n), getOrDefault(p2o5), getOrDefault(k2o));
        }
    }

    private void validatePrimaryNpkSum(double n, double p2o5, double k2o) {
        if (n + p2o5 + k2o < MINIMUM_NPK_SUM) {
            throw new IllegalArgumentException(MINIMUM_NPK_SUM_MESSAGE);
        }
    }

    private void checkOwnership(FormulatedMineralFertilizerModel fertilizer, UserModel owner) {
        if (!fertilizer.getUser().getId().equals(owner.getId())) {
            throw new AccessDeniedException("Acesso negado.");
        }
    }

    private void checkReadAccess(FormulatedMineralFertilizerModel fertilizer, UserModel owner) {
        if (!fertilizer.getUser().getId().equals(owner.getId())
                && !Boolean.TRUE.equals(fertilizer.getPublico())
                && fertilizer.getUser().getCargo() != Cargo.USUARIO_SUPREMO) {
            throw new AccessDeniedException("Acesso negado.");
        }
    }

    private void checkUserRole(UserModel user) {
        if (user.getCargo() != Cargo.USUARIO_SUPREMO && user.getCargo() != Cargo.PROPRIETARIO && user.getCargo() != Cargo.GERENTE) {
            throw new AccessDeniedException("Permissão insuficiente para criar adubos.");
        }
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private FormulatedMineralFertilizerModel findFertilizerByIdOrThrow(Long id) {
        return formulatedMineralFertilizerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Adubo não encontrado com ID: " + id));
    }
}
