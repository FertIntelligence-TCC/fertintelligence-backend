package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.sulfurDose.SulfurDoseCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.sulfurDose.SulfurDosePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.sulfurDose.SulfurDoseResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.SulfurDoseModel;
import com.migueltcc.fertintelligence.repository.SoilFertilityInterpretationCriteriaTableRepository;
import com.migueltcc.fertintelligence.repository.SulfurDoseRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.SulfurDoseService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Objects;

@Service
public class SulfurDoseServiceImpl implements SulfurDoseService {

    @Autowired
    private SulfurDoseRepository sulfurDoseRepository;

    @Autowired
    private SoilFertilityInterpretationCriteriaTableRepository soilFertilityInterpretationCriteriaTableRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public SulfurDoseResponseDto createSulfurDose(Long tableId, SulfurDoseCreateRequestDto createRequestDto, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);
        checkModifyPermission(table, owner);

        sulfurDoseRepository.findByTable(table).ifPresent(existing -> {
            throw new IllegalStateException("Já existe uma tabela de doses de enxofre cadastrada para esta tabela.");
        });

        SulfurDoseModel criterion = SulfurDoseModel.builder()
                .table(table)
                .build();
        BeanUtils.copyProperties(createRequestDto, criterion);

        SulfurDoseModel savedCriterion = sulfurDoseRepository.save(criterion);
        return savedCriterion.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public SulfurDoseResponseDto getSulfurDoseById(Long criterionId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        SulfurDoseModel criterion = findCriterionByIdOrThrow(criterionId);
        checkViewPermission(criterion.getTable(), owner);

        return criterion.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public SulfurDoseResponseDto getSulfurDoseByTable(Long tableId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);
        checkViewPermission(table, owner);

        SulfurDoseModel criterion = sulfurDoseRepository.findByTable(table)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tabela de doses de enxofre não encontrada para a tabela: " + tableId));

        return criterion.toDto();
    }

    @Override
    @Transactional
    public SulfurDoseResponseDto updateSulfurDose(Long criterionId, SulfurDosePostRequestDto updateRequestDto, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        SulfurDoseModel criterion = findCriterionByIdOrThrow(criterionId);
        checkModifyPermission(criterion.getTable(), owner);

        copyNonNullProperties(updateRequestDto, criterion);

        SulfurDoseModel updatedCriterion = sulfurDoseRepository.save(criterion);
        return updatedCriterion.toDto();
    }

    @Override
    @Transactional
    public void deleteSulfurDose(Long criterionId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        SulfurDoseModel criterion = findCriterionByIdOrThrow(criterionId);
        checkModifyPermission(criterion.getTable(), owner);

        sulfurDoseRepository.delete(criterion);
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

    private SulfurDoseModel findCriterionByIdOrThrow(Long criterionId) {
        return sulfurDoseRepository.findById(criterionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tabela de doses de enxofre não encontrada com o ID: " + criterionId));
    }

    private void checkViewPermission(SoilFertilityInterpretationCriteriaTableModel table, UserModel user) {
        if (isCreator(table, user) || table.isPublicTable() || isDefaultTable(table)) {
            return;
        }
        throw new AccessDeniedException("Acesso negado. O usuário não tem permissão para acessar esta tabela.");
    }

    private void checkModifyPermission(SoilFertilityInterpretationCriteriaTableModel table, UserModel user) {
        if (isDefaultTable(table)) {
            if (isSupremeUser(user)) {
                return;
            }
            throw new AccessDeniedException("Apenas o usuário supremo pode modificar tabelas padrão.");
        }
        if (!isCreator(table, user)) {
            throw new AccessDeniedException("Acesso negado. O usuário não é o criador da tabela.");
        }
    }

    private boolean isCreator(SoilFertilityInterpretationCriteriaTableModel table, UserModel user) {
        return table.getCreator() != null
                && user != null
                && Objects.equals(table.getCreator().getId(), user.getId());
    }

    private boolean isDefaultTable(SoilFertilityInterpretationCriteriaTableModel table) {
        return table.getCreator() != null
                && table.getCreator().getCargo() == Cargo.USUARIO_SUPREMO
                && !table.isPublicTable();
    }

    private boolean isSupremeUser(UserModel user) {
        return user != null && user.getCargo() == Cargo.USUARIO_SUPREMO;
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
