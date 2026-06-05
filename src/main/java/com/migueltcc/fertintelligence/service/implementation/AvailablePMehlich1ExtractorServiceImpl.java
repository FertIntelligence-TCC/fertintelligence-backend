package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePMehlich1Extractor.AvailablePMehlich1ExtractorCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePMehlich1Extractor.AvailablePMehlich1ExtractorPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePMehlich1Extractor.AvailablePMehlich1ExtractorResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.AvailablePMehlich1ExtractorModel;
import com.migueltcc.fertintelligence.repository.AvailablePMehlich1ExtractorRepository;
import com.migueltcc.fertintelligence.repository.SoilFertilityInterpretationCriteriaTableRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.AvailablePMehlich1ExtractorService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
public class AvailablePMehlich1ExtractorServiceImpl implements AvailablePMehlich1ExtractorService {

    @Autowired
    private AvailablePMehlich1ExtractorRepository availablePMehlich1ExtractorRepository;

    @Autowired
    private SoilFertilityInterpretationCriteriaTableRepository soilFertilityInterpretationCriteriaTableRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public AvailablePMehlich1ExtractorResponseDto createAvailablePMehlich1Extractor(
            Long tableId,
            AvailablePMehlich1ExtractorCreateRequestDto createRequestDto,
            String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        StandardEntityAuthorization.assertSupremeUser(owner);

        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);

        availablePMehlich1ExtractorRepository.findByTable(table).ifPresent(existing -> {
            throw new IllegalStateException("Já existe um critério cadastrado para esta tabela.");
        });

        AvailablePMehlich1ExtractorModel criterion = AvailablePMehlich1ExtractorModel.builder()
                .table(table)
                .build();
        BeanUtils.copyProperties(createRequestDto, criterion);

        AvailablePMehlich1ExtractorModel savedCriterion = availablePMehlich1ExtractorRepository.save(criterion);
        return savedCriterion.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public AvailablePMehlich1ExtractorResponseDto getAvailablePMehlich1ExtractorById(
            Long criterionId,
            String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        AvailablePMehlich1ExtractorModel criterion = findCriterionByIdOrThrow(criterionId);
        checkCreatorPermission(criterion.getTable(), owner);

        return criterion.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public AvailablePMehlich1ExtractorResponseDto getAvailablePMehlich1ExtractorByTable(
            Long tableId,
            String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);
        checkCreatorPermission(table, owner);

        AvailablePMehlich1ExtractorModel criterion = availablePMehlich1ExtractorRepository.findByTable(table)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Critério de fósforo (Mehlich-1) não encontrado para a tabela: " + tableId));

        return criterion.toDto();
    }

    @Override
    @Transactional
    public AvailablePMehlich1ExtractorResponseDto updateAvailablePMehlich1Extractor(
            Long criterionId,
            AvailablePMehlich1ExtractorPostRequestDto updateRequestDto,
            String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        StandardEntityAuthorization.assertSupremeUser(owner);

        AvailablePMehlich1ExtractorModel criterion = findCriterionByIdOrThrow(criterionId);

        copyNonNullProperties(updateRequestDto, criterion);

        AvailablePMehlich1ExtractorModel updatedCriterion = availablePMehlich1ExtractorRepository.save(criterion);
        return updatedCriterion.toDto();
    }

    @Override
    @Transactional
    public void deleteAvailablePMehlich1Extractor(Long criterionId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        StandardEntityAuthorization.assertSupremeUser(owner);

        AvailablePMehlich1ExtractorModel criterion = findCriterionByIdOrThrow(criterionId);

        availablePMehlich1ExtractorRepository.delete(criterion);
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

    private AvailablePMehlich1ExtractorModel findCriterionByIdOrThrow(Long criterionId) {
        return availablePMehlich1ExtractorRepository.findById(criterionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Critério de fósforo (Mehlich-1) não encontrado com o ID: " + criterionId));
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
