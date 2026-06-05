package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.salinityInterpretation.SalinityInterpretationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.salinityInterpretation.SalinityInterpretationPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.salinityInterpretation.SalinityInterpretationResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.SalinityInterpretationModel;
import com.migueltcc.fertintelligence.repository.SalinityInterpretationRepository;
import com.migueltcc.fertintelligence.repository.SoilFertilityInterpretationCriteriaTableRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.SalinityInterpretationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
public class SalinityInterpretationServiceImpl implements SalinityInterpretationService {

    @Autowired
    private SalinityInterpretationRepository salinityInterpretationRepository;

    @Autowired
    private SoilFertilityInterpretationCriteriaTableRepository soilFertilityInterpretationCriteriaTableRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public SalinityInterpretationResponseDto createSalinityInterpretation(
            Long tableId,
            SalinityInterpretationCreateRequestDto createRequestDto,
            String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        StandardEntityAuthorization.assertSupremeUser(owner);

        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);

        salinityInterpretationRepository.findByTable(table).ifPresent(existing -> {
            throw new IllegalStateException("Já existe um critério cadastrado para esta tabela.");
        });

        SalinityInterpretationModel criterion = SalinityInterpretationModel.builder()
                .table(table)
                .build();
        BeanUtils.copyProperties(createRequestDto, criterion);

        SalinityInterpretationModel savedCriterion = salinityInterpretationRepository.save(criterion);
        return savedCriterion.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public SalinityInterpretationResponseDto getSalinityInterpretationById(Long criterionId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        SalinityInterpretationModel criterion = findCriterionByIdOrThrow(criterionId);
        checkCreatorPermission(criterion.getTable(), owner);

        return criterion.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public SalinityInterpretationResponseDto getSalinityInterpretationByTable(Long tableId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);
        checkCreatorPermission(table, owner);

        SalinityInterpretationModel criterion = salinityInterpretationRepository.findByTable(table)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Critério de salinidade não encontrado para a tabela: " + tableId));

        return criterion.toDto();
    }

    @Override
    @Transactional
    public SalinityInterpretationResponseDto updateSalinityInterpretation(
            Long criterionId,
            SalinityInterpretationPostRequestDto updateRequestDto,
            String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        StandardEntityAuthorization.assertSupremeUser(owner);

        SalinityInterpretationModel criterion = findCriterionByIdOrThrow(criterionId);

        copyNonNullProperties(updateRequestDto, criterion);

        SalinityInterpretationModel updatedCriterion = salinityInterpretationRepository.save(criterion);
        return updatedCriterion.toDto();
    }

    @Override
    @Transactional
    public void deleteSalinityInterpretation(Long criterionId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        StandardEntityAuthorization.assertSupremeUser(owner);

        SalinityInterpretationModel criterion = findCriterionByIdOrThrow(criterionId);

        salinityInterpretationRepository.delete(criterion);
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private SoilFertilityInterpretationCriteriaTableModel findTableByIdOrThrow(Long tableId) {
        return soilFertilityInterpretationCriteriaTableRepository.findById(tableId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tabela de critérios de fertilidade do solo não encontrada com o ID: " + tableId));
    }

    private SalinityInterpretationModel findCriterionByIdOrThrow(Long criterionId) {
        return salinityInterpretationRepository.findById(criterionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Critério de salinidade não encontrado com o ID: " + criterionId));
    }

    private void checkCreatorPermission(SoilFertilityInterpretationCriteriaTableModel table, UserModel user) {
        StandardEntityAuthorization.assertCanRead(table.getCreator(), table.isPublicTable(), user);
    }

    private void copyNonNullProperties(Object source, Object target) {
        String[] nullPropertyNames = getNullPropertyNames(source);
        BeanUtils.copyProperties(source, target, nullPropertyNames);
    }

    private String[] getNullPropertyNames(Object source) {
        BeanWrapper src = new BeanWrapperImpl(source);
        return Arrays.stream(src.getPropertyDescriptors())
                .map(propertyDescriptor -> propertyDescriptor.getName())
                .filter(propertyName -> !"class".equals(propertyName))
                .filter(propertyName -> src.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }
}
