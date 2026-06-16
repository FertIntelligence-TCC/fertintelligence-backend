package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePAnionExchangeResinExtractor.AvailablePAnionExchangeResinExtractorCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePAnionExchangeResinExtractor.AvailablePAnionExchangeResinExtractorPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePAnionExchangeResinExtractor.AvailablePAnionExchangeResinExtractorResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.AvailablePAnionExchangeResinExtractorModel;
import com.migueltcc.fertintelligence.repository.AvailablePAnionExchangeResinExtractorRepository;
import com.migueltcc.fertintelligence.repository.SoilFertilityInterpretationCriteriaTableRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.AvailablePAnionExchangeResinExtractorService;
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
public class AvailablePAnionExchangeResinExtractorServiceImpl
        implements AvailablePAnionExchangeResinExtractorService {

    @Autowired
    private AvailablePAnionExchangeResinExtractorRepository availablePAnionExchangeResinExtractorRepository;

    @Autowired
    private SoilFertilityInterpretationCriteriaTableRepository soilFertilityInterpretationCriteriaTableRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public AvailablePAnionExchangeResinExtractorResponseDto createAvailablePAnionExchangeResinExtractor(
            Long tableId,
            AvailablePAnionExchangeResinExtractorCreateRequestDto createRequestDto,
            String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);
        checkCreatorPermission(table, owner);

        availablePAnionExchangeResinExtractorRepository.findByTable(table).ifPresent(existing -> {
            throw new IllegalStateException("Já existe um critério cadastrado para esta tabela.");
        });

        AvailablePAnionExchangeResinExtractorModel criterion = AvailablePAnionExchangeResinExtractorModel.builder()
                .table(table)
                .build();
        copyNonNullProperties(createRequestDto, criterion);

        AvailablePAnionExchangeResinExtractorModel savedCriterion =
                availablePAnionExchangeResinExtractorRepository.save(criterion);
        return savedCriterion.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public AvailablePAnionExchangeResinExtractorResponseDto getAvailablePAnionExchangeResinExtractorById(
            Long criterionId,
            String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        AvailablePAnionExchangeResinExtractorModel criterion = findCriterionByIdOrThrow(criterionId);
        checkCreatorPermission(criterion.getTable(), owner);

        return criterion.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public AvailablePAnionExchangeResinExtractorResponseDto getAvailablePAnionExchangeResinExtractorByTable(
            Long tableId,
            String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);
        checkCreatorPermission(table, owner);

        AvailablePAnionExchangeResinExtractorModel criterion =
                availablePAnionExchangeResinExtractorRepository.findByTable(table)
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Critério de fósforo (resina) não encontrado para a tabela: " + tableId));

        return criterion.toDto();
    }

    @Override
    @Transactional
    public AvailablePAnionExchangeResinExtractorResponseDto updateAvailablePAnionExchangeResinExtractor(
            Long criterionId,
            AvailablePAnionExchangeResinExtractorPostRequestDto updateRequestDto,
            String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        AvailablePAnionExchangeResinExtractorModel criterion = findCriterionByIdOrThrow(criterionId);
        checkCreatorPermission(criterion.getTable(), owner);

        copyNonNullProperties(updateRequestDto, criterion);

        AvailablePAnionExchangeResinExtractorModel updatedCriterion =
                availablePAnionExchangeResinExtractorRepository.save(criterion);
        return updatedCriterion.toDto();
    }

    @Override
    @Transactional
    public void deleteAvailablePAnionExchangeResinExtractor(Long criterionId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        AvailablePAnionExchangeResinExtractorModel criterion = findCriterionByIdOrThrow(criterionId);
        checkCreatorPermission(criterion.getTable(), owner);

        availablePAnionExchangeResinExtractorRepository.delete(criterion);
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

    private AvailablePAnionExchangeResinExtractorModel findCriterionByIdOrThrow(Long criterionId) {
        return availablePAnionExchangeResinExtractorRepository.findById(criterionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Critério de fósforo (resina) não encontrado com o ID: " + criterionId));
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
