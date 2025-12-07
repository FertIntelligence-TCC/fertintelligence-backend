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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FormulatedMineralFertilizerServiceImpl implements FormulatedMineralFertilizerService {

    @Autowired
    private FormulatedMineralFertilizerRepository formulatedMineralFertilizerRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public FormulatedMineralFertilizerResponseDto createFormulatedMineralFertilizer(FormulatedMineralFertilizerCreateRequestDto createRequestDto,
                                                                                    String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        FormulatedMineralFertilizerModel fertilizer = FormulatedMineralFertilizerModel.builder()
                .user(owner)
                .N(createRequestDto.getN())
                .P2O5(createRequestDto.getP2o5())
                .K2O(createRequestDto.getK2o())
                .Ca(createRequestDto.getCa())
                .Mg(createRequestDto.getMg())
                .S(createRequestDto.getS())
                .B(createRequestDto.getB())
                .Cu(createRequestDto.getCu())
                .Fe(createRequestDto.getFe())
                .Mn(createRequestDto.getMn())
                .Mo(createRequestDto.getMo())
                .Zn(createRequestDto.getZn())
                .indicatedFormulaNumber(createRequestDto.getIndicatedFormulaNumber())
                .build();

        applyFormulateAndRelationFromNutrients(fertilizer);

        FormulatedMineralFertilizerModel savedFertilizer = formulatedMineralFertilizerRepository.save(fertilizer);
        return savedFertilizer.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public FormulatedMineralFertilizerResponseDto getFormulatedMineralFertilizerById(Long formulatedMineralFertilizerId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        FormulatedMineralFertilizerModel fertilizer = findFertilizerByIdOrThrow(formulatedMineralFertilizerId);
        checkOwnership(fertilizer, owner);

        return fertilizer.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormulatedMineralFertilizerResponseDto> getFormulatedMineralFertilizersByUser(String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        return formulatedMineralFertilizerRepository.findAllByUser(owner).stream()
                .map(FormulatedMineralFertilizerModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FormulatedMineralFertilizerResponseDto updateFormulatedMineralFertilizer(Long formulatedMineralFertilizerId,
                                                                                    FormulatedMineralFertilizerPostRequestDto updateRequestDto,
                                                                                    String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        FormulatedMineralFertilizerModel fertilizer = findFertilizerByIdOrThrow(formulatedMineralFertilizerId);
        checkOwnership(fertilizer, owner);

        if (updateRequestDto.getN() != null) {
            fertilizer.setN(updateRequestDto.getN());
        }
        if (updateRequestDto.getP2o5() != null) {
            fertilizer.setP2O5(updateRequestDto.getP2o5());
        }
        if (updateRequestDto.getK2o() != null) {
            fertilizer.setK2O(updateRequestDto.getK2o());
        }
        if (updateRequestDto.getCa() != null) {
            fertilizer.setCa(updateRequestDto.getCa());
        }
        if (updateRequestDto.getMg() != null) {
            fertilizer.setMg(updateRequestDto.getMg());
        }
        if (updateRequestDto.getS() != null) {
            fertilizer.setS(updateRequestDto.getS());
        }
        if (updateRequestDto.getB() != null) {
            fertilizer.setB(updateRequestDto.getB());
        }
        if (updateRequestDto.getCu() != null) {
            fertilizer.setCu(updateRequestDto.getCu());
        }
        if (updateRequestDto.getFe() != null) {
            fertilizer.setFe(updateRequestDto.getFe());
        }
        if (updateRequestDto.getMn() != null) {
            fertilizer.setMn(updateRequestDto.getMn());
        }
        if (updateRequestDto.getMo() != null) {
            fertilizer.setMo(updateRequestDto.getMo());
        }
        if (updateRequestDto.getZn() != null) {
            fertilizer.setZn(updateRequestDto.getZn());
        }
        if (updateRequestDto.getIndicatedFormulaNumber() != null) {
            fertilizer.setIndicatedFormulaNumber(updateRequestDto.getIndicatedFormulaNumber());
        }

        applyFormulateAndRelationFromNutrients(fertilizer);

        FormulatedMineralFertilizerModel updatedFertilizer = formulatedMineralFertilizerRepository.save(fertilizer);
        return updatedFertilizer.toDto();
    }

    @Override
    @Transactional
    public void deleteFormulatedMineralFertilizer(Long formulatedMineralFertilizerId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        FormulatedMineralFertilizerModel fertilizer = findFertilizerByIdOrThrow(formulatedMineralFertilizerId);
        checkOwnership(fertilizer, owner);

        formulatedMineralFertilizerRepository.delete(fertilizer);
    }

    private void applyFormulateAndRelationFromNutrients(FormulatedMineralFertilizerModel fertilizer) {
        double nValue = fertilizer.getN();
        double pValue = fertilizer.getP2O5();
        double kValue = fertilizer.getK2O();

        fertilizer.setFormulate(new Formulate((int) Math.round(nValue), (int) Math.round(pValue), (int) Math.round(kValue)));

        double divisor = determineRelationDivisor(nValue, pValue, kValue);
        fertilizer.setRelation(new NPKrelation(
                safeDivide(nValue, divisor),
                safeDivide(pValue, divisor),
                safeDivide(kValue, divisor)
        ));
    }

    private double determineRelationDivisor(double n, double p, double k) {
        if (n > 0) {
            return n;
        }
        if (p > 0) {
            return p;
        }
        if (k > 0) {
            return k;
        }
        return 1.0;
    }

    private double safeDivide(double value, double divisor) {
        if (divisor == 0) {
            return 0.0;
        }
        return value / divisor;
    }

    private void checkOwnership(FormulatedMineralFertilizerModel fertilizer, UserModel owner) {
        if (!fertilizer.getUser().getId().equals(owner.getId())) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este recurso.");
        }
    }

    private void checkUserRole(UserModel user) {
        if (user.getCargo() != Cargo.PROPRIETARIO
                && user.getCargo() != Cargo.GERENTE
                && user.getCargo() != Cargo.AGRONOMO_RESIDENTE
                && user.getCargo() != Cargo.AGRONOMO_CONSULTOR
                && user.getCargo() != Cargo.SECRETARIO
                && user.getCargo() != Cargo.SUPERVISOR_DE_AREA) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este recurso.");
        }
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private FormulatedMineralFertilizerModel findFertilizerByIdOrThrow(Long fertilizerId) {
        return formulatedMineralFertilizerRepository.findById(fertilizerId)
                .orElseThrow(() -> new EntityNotFoundException("Adubo mineral formulado não encontrado com o ID: " + fertilizerId));
    }
}