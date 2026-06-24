package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.exchangeableSodium.ExchangeableSodiumCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.exchangeableSodium.ExchangeableSodiumPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.exchangeableSodium.ExchangeableSodiumResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.ExchangeableSodiumModel;
import com.migueltcc.fertintelligence.repository.ExchangeableSodiumRepository;
import com.migueltcc.fertintelligence.repository.SoilFertilityInterpretationCriteriaTableRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.ExchangeableSodiumService;
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
public class ExchangeableSodiumServiceImpl implements ExchangeableSodiumService {

    @Autowired
    private ExchangeableSodiumRepository exchangeableSodiumRepository;

    @Autowired
    private SoilFertilityInterpretationCriteriaTableRepository soilFertilityInterpretationCriteriaTableRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public ExchangeableSodiumResponseDto createExchangeableSodium(Long tableId, ExchangeableSodiumCreateRequestDto createRequestDto, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);
        checkModifyPermission(table, owner);
        ExchangeableSodiumModel criterion = exchangeableSodiumRepository.findFirstByTableOrderByIdAsc(table)
                .orElseGet(() -> ExchangeableSodiumModel.builder().table(table).build());
        BeanUtils.copyProperties(createRequestDto, criterion);
        criterion.setTable(table);
        return toResponseDto(exchangeableSodiumRepository.save(criterion));
    }

    @Override
    @Transactional(readOnly = true)
    public ExchangeableSodiumResponseDto getExchangeableSodiumById(Long criterionId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        ExchangeableSodiumModel criterion = findCriterionByIdOrThrow(criterionId);
        checkViewPermission(criterion.getTable(), owner);
        return toResponseDto(criterion);
    }

    @Override
    @Transactional(readOnly = true)
    public ExchangeableSodiumResponseDto getExchangeableSodiumByTable(Long tableId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);
        checkViewPermission(table, owner);
        ExchangeableSodiumModel criterion = exchangeableSodiumRepository.findFirstByTableOrderByIdAsc(table)
                .orElseThrow(() -> new EntityNotFoundException("Critério de sódio trocável não encontrado para a tabela: " + tableId));
        return toResponseDto(criterion);
    }

    @Override
    @Transactional
    public ExchangeableSodiumResponseDto updateExchangeableSodium(Long criterionId, ExchangeableSodiumPostRequestDto updateRequestDto, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        ExchangeableSodiumModel criterion = findCriterionByIdOrThrow(criterionId);
        checkModifyPermission(criterion.getTable(), owner);
        copyNonNullProperties(updateRequestDto, criterion);
        return toResponseDto(exchangeableSodiumRepository.save(criterion));
    }

    @Override
    @Transactional
    public void deleteExchangeableSodium(Long criterionId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        ExchangeableSodiumModel criterion = findCriterionByIdOrThrow(criterionId);
        checkModifyPermission(criterion.getTable(), owner);
        exchangeableSodiumRepository.delete(criterion);
    }

    private ExchangeableSodiumResponseDto toResponseDto(ExchangeableSodiumModel model) {
        ExchangeableSodiumResponseDto dto = new ExchangeableSodiumResponseDto();
        dto.setId(model.getId());
        dto.setTableId(model.getTable() != null ? model.getTable().getId() : null);
        dto.setSodiumUnit(model.getSodiumUnit() != null ? model.getSodiumUnit() : ExchangeableSodiumModel.DEFAULT_UNIT);
        dto.setCtcUnit(model.getCtcUnit() != null ? model.getCtcUnit() : ExchangeableSodiumModel.DEFAULT_UNIT);
        dto.setCtcLessThan43RangeLabel("< 43 mmolc/dm³");
        dto.setCtcFrom43To86RangeLabel("43 a 86 mmolc/dm³");
        dto.setCtcFrom87To150RangeLabel("87 a 150 mmolc/dm³");
        dto.setCtcGreaterThan150RangeLabel("> 150 mmolc/dm³");
        dto.setCtcLessThan43VeryLowLessThan(model.getCtcLessThan43VeryLowLessThan());
        dto.setCtcLessThan43LowMin(model.getCtcLessThan43LowMin());
        dto.setCtcLessThan43LowMax(model.getCtcLessThan43LowMax());
        dto.setCtcLessThan43MediumMin(model.getCtcLessThan43MediumMin());
        dto.setCtcLessThan43MediumMax(model.getCtcLessThan43MediumMax());
        dto.setCtcLessThan43HighMin(model.getCtcLessThan43HighMin());
        dto.setCtcLessThan43HighMax(model.getCtcLessThan43HighMax());
        dto.setCtcLessThan43VeryHighGreaterThan(model.getCtcLessThan43VeryHighGreaterThan());
        dto.setCtcFrom43To86VeryLowLessThan(model.getCtcFrom43To86VeryLowLessThan());
        dto.setCtcFrom43To86LowMin(model.getCtcFrom43To86LowMin());
        dto.setCtcFrom43To86LowMax(model.getCtcFrom43To86LowMax());
        dto.setCtcFrom43To86MediumMin(model.getCtcFrom43To86MediumMin());
        dto.setCtcFrom43To86MediumMax(model.getCtcFrom43To86MediumMax());
        dto.setCtcFrom43To86HighMin(model.getCtcFrom43To86HighMin());
        dto.setCtcFrom43To86HighMax(model.getCtcFrom43To86HighMax());
        dto.setCtcFrom43To86VeryHighGreaterThan(model.getCtcFrom43To86VeryHighGreaterThan());
        dto.setCtcFrom87To150VeryLowLessThan(model.getCtcFrom87To150VeryLowLessThan());
        dto.setCtcFrom87To150LowMin(model.getCtcFrom87To150LowMin());
        dto.setCtcFrom87To150LowMax(model.getCtcFrom87To150LowMax());
        dto.setCtcFrom87To150MediumMin(model.getCtcFrom87To150MediumMin());
        dto.setCtcFrom87To150MediumMax(model.getCtcFrom87To150MediumMax());
        dto.setCtcFrom87To150HighMin(model.getCtcFrom87To150HighMin());
        dto.setCtcFrom87To150HighMax(model.getCtcFrom87To150HighMax());
        dto.setCtcFrom87To150VeryHighGreaterThan(model.getCtcFrom87To150VeryHighGreaterThan());
        dto.setCtcGreaterThan15VeryLowLessThan(model.getCtcGreaterThan15VeryLowLessThan());
        dto.setCtcGreaterThan15LowMin(model.getCtcGreaterThan15LowMin());
        dto.setCtcGreaterThan15LowMax(model.getCtcGreaterThan15LowMax());
        dto.setCtcGreaterThan15MediumMin(model.getCtcGreaterThan15MediumMin());
        dto.setCtcGreaterThan15MediumMax(model.getCtcGreaterThan15MediumMax());
        dto.setCtcGreaterThan15HighMin(model.getCtcGreaterThan15HighMin());
        dto.setCtcGreaterThan15HighMax(model.getCtcGreaterThan15HighMax());
        dto.setCtcGreaterThan15VeryHighGreaterThan(model.getCtcGreaterThan15VeryHighGreaterThan());
        return dto;
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private SoilFertilityInterpretationCriteriaTableModel findTableByIdOrThrow(Long tableId) {
        return soilFertilityInterpretationCriteriaTableRepository.findById(tableId).orElseThrow(() -> new EntityNotFoundException("Tabela de critérios de fertilidade do solo não encontrada com o ID: " + tableId));
    }

    private ExchangeableSodiumModel findCriterionByIdOrThrow(Long criterionId) {
        return exchangeableSodiumRepository.findById(criterionId).orElseThrow(() -> new EntityNotFoundException("Critério de sódio trocável não encontrado com o ID: " + criterionId));
    }

    private void checkViewPermission(SoilFertilityInterpretationCriteriaTableModel table, UserModel user) {
        if (isCreator(table, user) || table.isPublicTable() || isDefaultTable(table)) return;
        throw new AccessDeniedException("Acesso negado. O usuário não tem permissão para acessar esta tabela.");
    }

    private void checkModifyPermission(SoilFertilityInterpretationCriteriaTableModel table, UserModel user) {
        if (isDefaultTable(table)) {
            if (isSupremeUser(user)) return;
            throw new AccessDeniedException("Apenas o usuário supremo pode modificar tabelas padrão.");
        }
        if (!isCreator(table, user)) throw new AccessDeniedException("Acesso negado. O usuário não é o criador da tabela.");
    }

    private boolean isCreator(SoilFertilityInterpretationCriteriaTableModel table, UserModel user) {
        return table.getCreator() != null && user != null && Objects.equals(table.getCreator().getId(), user.getId());
    }

    private boolean isDefaultTable(SoilFertilityInterpretationCriteriaTableModel table) {
        return table.getCreator() != null && table.getCreator().getCargo() == Cargo.USUARIO_SUPREMO && !table.isPublicTable();
    }

    private boolean isSupremeUser(UserModel user) { return user != null && user.getCargo() == Cargo.USUARIO_SUPREMO; }

    private void copyNonNullProperties(Object source, Object target) { BeanUtils.copyProperties(source, target, getNullPropertyNames(source)); }

    private String[] getNullPropertyNames(Object source) {
        BeanWrapper src = new BeanWrapperImpl(source);
        return Arrays.stream(src.getPropertyDescriptors()).map(propertyDescriptor -> propertyDescriptor.getName()).filter(propertyName -> !"class".equals(propertyName)).filter(propertyName -> src.getPropertyValue(propertyName) == null).toArray(String[]::new);
    }
}
