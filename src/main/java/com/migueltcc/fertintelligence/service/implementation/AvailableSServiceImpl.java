package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availableS.AvailableSCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availableS.AvailableSPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availableS.AvailableSResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.AvailableSModel;
import com.migueltcc.fertintelligence.repository.AvailableSRepository;
import com.migueltcc.fertintelligence.repository.SoilFertilityInterpretationCriteriaTableRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.AvailableSService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
public class AvailableSServiceImpl implements AvailableSService {

    @Autowired
    private AvailableSRepository availableSRepository;

    @Autowired
    private SoilFertilityInterpretationCriteriaTableRepository soilFertilityInterpretationCriteriaTableRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public AvailableSResponseDto createAvailableS(Long tableId, AvailableSCreateRequestDto createRequestDto, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        StandardEntityAuthorization.assertSupremeUser(owner);

        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);

        availableSRepository.findByTable(table).ifPresent(existing -> {
            throw new IllegalStateException("Já existe um critério cadastrado para esta tabela.");
        });

        AvailableSModel criterion = AvailableSModel.builder()
                .table(table)
                .build();
        BeanUtils.copyProperties(createRequestDto, criterion);

        AvailableSModel savedCriterion = availableSRepository.save(criterion);
        return savedCriterion.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public AvailableSResponseDto getAvailableSById(Long criterionId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        AvailableSModel criterion = findCriterionByIdOrThrow(criterionId);
        checkCreatorPermission(criterion.getTable(), owner);

        return criterion.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public AvailableSResponseDto getAvailableSByTable(Long tableId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);
        checkCreatorPermission(table, owner);

        AvailableSModel criterion = availableSRepository.findByTable(table)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Critério de enxofre não encontrado para a tabela: " + tableId));

        return criterion.toDto();
    }

    @Override
    @Transactional
    public AvailableSResponseDto updateAvailableS(Long criterionId, AvailableSPostRequestDto updateRequestDto, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        StandardEntityAuthorization.assertSupremeUser(owner);

        AvailableSModel criterion = findCriterionByIdOrThrow(criterionId);

        copyNonNullProperties(updateRequestDto, criterion);

        AvailableSModel updatedCriterion = availableSRepository.save(criterion);
        return updatedCriterion.toDto();
    }

    @Override
    @Transactional
    public void deleteAvailableS(Long criterionId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        StandardEntityAuthorization.assertSupremeUser(owner);

        AvailableSModel criterion = findCriterionByIdOrThrow(criterionId);

        availableSRepository.delete(criterion);
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

    private AvailableSModel findCriterionByIdOrThrow(Long criterionId) {
        return availableSRepository.findById(criterionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Critério de enxofre não encontrado com o ID: " + criterionId));
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
