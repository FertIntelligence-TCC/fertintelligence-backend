package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.diverseContentRange.DiverseContentRangeCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.diverseContentRange.DiverseContentRangePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.diverseContentRange.DiverseContentRangeResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.DiverseContentRangeModel;
import com.migueltcc.fertintelligence.repository.DiverseContentRangeRepository;
import com.migueltcc.fertintelligence.repository.SoilFertilityInterpretationCriteriaTableRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.DiverseContentRangeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
public class DiverseContentRangeServiceImpl implements DiverseContentRangeService {

    @Autowired
    private DiverseContentRangeRepository diverseContentRangeRepository;

    @Autowired
    private SoilFertilityInterpretationCriteriaTableRepository soilFertilityInterpretationCriteriaTableRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public DiverseContentRangeResponseDto createDiverseContentRange(
            Long tableId,
            DiverseContentRangeCreateRequestDto createRequestDto,
            String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);
        checkCreatorPermission(table, owner);

        diverseContentRangeRepository.findByTable(table).ifPresent(existing -> {
            throw new IllegalStateException("Já existe um critério cadastrado para esta tabela.");
        });

        DiverseContentRangeModel criterion = DiverseContentRangeModel.builder()
                .table(table)
                .build();
        BeanUtils.copyProperties(createRequestDto, criterion);

        DiverseContentRangeModel savedCriterion = diverseContentRangeRepository.save(criterion);
        return savedCriterion.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public DiverseContentRangeResponseDto getDiverseContentRangeById(Long criterionId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        DiverseContentRangeModel criterion = findCriterionByIdOrThrow(criterionId);
        checkCreatorPermission(criterion.getTable(), owner);

        return criterion.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public DiverseContentRangeResponseDto getDiverseContentRangeByTable(Long tableId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);
        checkCreatorPermission(table, owner);

        DiverseContentRangeModel criterion = diverseContentRangeRepository.findByTable(table)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Faixas de teores diversos não encontradas para a tabela: " + tableId));

        return criterion.toDto();
    }

    @Override
    @Transactional
    public DiverseContentRangeResponseDto updateDiverseContentRange(
            Long criterionId,
            DiverseContentRangePostRequestDto updateRequestDto,
            String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        DiverseContentRangeModel criterion = findCriterionByIdOrThrow(criterionId);
        checkCreatorPermission(criterion.getTable(), owner);

        copyNonNullProperties(updateRequestDto, criterion);

        DiverseContentRangeModel updatedCriterion = diverseContentRangeRepository.save(criterion);
        return updatedCriterion.toDto();
    }

    @Override
    @Transactional
    public void deleteDiverseContentRange(Long criterionId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        DiverseContentRangeModel criterion = findCriterionByIdOrThrow(criterionId);
        checkCreatorPermission(criterion.getTable(), owner);

        diverseContentRangeRepository.delete(criterion);
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

    private DiverseContentRangeModel findCriterionByIdOrThrow(Long criterionId) {
        return diverseContentRangeRepository.findById(criterionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Faixas de teores diversos não encontradas com o ID: " + criterionId));
    }

    private void checkCreatorPermission(SoilFertilityInterpretationCriteriaTableModel table, UserModel user) {
        if (!table.getCreator().equals(user)) {
            throw new AccessDeniedException("Acesso negado. O usuário não é o criador da tabela.");
        }
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