package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.fertilizers.Formulate;
import com.migueltcc.fertintelligence.composedAttributes.fertilizers.NPKrelation;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer.FormulatedMineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer.FormulatedMineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer.FormulatedMineralFertilizerResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.FormulatedMineralFertilizerModel;
import com.migueltcc.fertintelligence.repository.FormulatedMineralFertilizerRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.FormulatedMineralFertilizerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FormulatedMineralFertilizerServiceImpl implements FormulatedMineralFertilizerService {

    private final FormulatedMineralFertilizerRepository formulatedMineralFertilizerRepository;
    private final UserRepository userRepository;

    public FormulatedMineralFertilizerServiceImpl(
            FormulatedMineralFertilizerRepository formulatedMineralFertilizerRepository,
            UserRepository userRepository) {
        this.formulatedMineralFertilizerRepository = formulatedMineralFertilizerRepository;
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
            formulate.setN((int) getOrDefault(Double.valueOf(dto.getFormulate().getN())));
            formulate.setP((int) getOrDefault(Double.valueOf(dto.getFormulate().getP())));
            formulate.setK((int) getOrDefault(Double.valueOf(dto.getFormulate().getK())));
        }

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
                .build();

        FormulatedMineralFertilizerModel saved = formulatedMineralFertilizerRepository.save(fertilizer);
        return saved.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public FormulatedMineralFertilizerResponseDto getFormulatedMineralFertilizerById(Long id, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        FormulatedMineralFertilizerModel fertilizer = findFertilizerByIdOrThrow(id);
        checkReadAccess(fertilizer, owner);
        return fertilizer.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormulatedMineralFertilizerResponseDto> getAllFormulatedMineralFertilizers(String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        return formulatedMineralFertilizerRepository.findAllByUserOrDefaultCreator(owner, Cargo.USUARIO_SUPREMO)
                .stream()
                .map(FormulatedMineralFertilizerModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormulatedMineralFertilizerResponseDto> getAllPublicFormulatedMineralFertilizers(String username) {
        findUserByUsernameOrThrow(username);
        return formulatedMineralFertilizerRepository.findAllByPublicoTrueOrDefaultCreatorOrderByIdAsc(Cargo.USUARIO_SUPREMO)
                .stream()
                .map(FormulatedMineralFertilizerModel::toDto)
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
            fertilizer.getFormulate().setN((int) getOrDefault(Double.valueOf(dto.getFormulate().getN())));
            fertilizer.getFormulate().setP((int) getOrDefault(Double.valueOf(dto.getFormulate().getP())));
            fertilizer.getFormulate().setK((int) getOrDefault(Double.valueOf(dto.getFormulate().getK())));
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

        FormulatedMineralFertilizerModel updated = formulatedMineralFertilizerRepository.save(fertilizer);
        return updated.toDto();
    }

    @Override
    @Transactional
    public void deleteFormulatedMineralFertilizer(Long id, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        FormulatedMineralFertilizerModel fertilizer = findFertilizerByIdOrThrow(id);
        checkOwnership(fertilizer, owner);
        formulatedMineralFertilizerRepository.delete(fertilizer);
    }

    private double getOrDefault(Double value) {
        return value != null ? value : 0.0;
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
