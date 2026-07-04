package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveK2OFertilization.CorrectiveK2OFertilizationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveK2OFertilization.CorrectiveK2OFertilizationPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveK2OFertilization.CorrectiveK2OFertilizationResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.CorrectiveK2OFertilizationModel;
import com.migueltcc.fertintelligence.repository.CorrectiveK2OFertilizationRepository;
import com.migueltcc.fertintelligence.repository.SoilFertilityInterpretationCriteriaTableRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.CorrectiveK2OFertilizationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class CorrectiveK2OFertilizationServiceImpl implements CorrectiveK2OFertilizationService {

    @Autowired
    private CorrectiveK2OFertilizationRepository repository;

    @Autowired
    private SoilFertilityInterpretationCriteriaTableRepository tableRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CorrectiveK2OFertilizationMapper mapper;

    @Override
    @Transactional
    public CorrectiveK2OFertilizationResponseDto createCorrectiveK2OFertilization(Long tableId, CorrectiveK2OFertilizationCreateRequestDto createRequestDto, String username) {
        UserModel user = findUserByUsernameOrThrow(username);
        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);
        checkModifyPermission(table, user);
        validateCreateRequest(createRequestDto);

        CorrectiveK2OFertilizationModel model = CorrectiveK2OFertilizationModel.builder()
                .table(table)
                .ctcMinimum(createRequestDto.getCtcMinimum())
                .ctcMaximum(createRequestDto.getCtcMaximum())
                .exchangeableKMinimum(createRequestDto.getExchangeableKMinimum())
                .exchangeableKMaximum(createRequestDto.getExchangeableKMaximum())
                .recommendedK2ODose(createRequestDto.getRecommendedK2ODose())
                .observations(createRequestDto.getObservations())
                .sources(createRequestDto.getSources())
                .build();
        return mapper.toDto(repository.save(model));
    }

    @Override
    @Transactional(readOnly = true)
    public CorrectiveK2OFertilizationResponseDto getCorrectiveK2OFertilizationById(Long criterionId, String username) {
        UserModel user = findUserByUsernameOrThrow(username);
        CorrectiveK2OFertilizationModel model = findCriterionByIdOrThrow(criterionId);
        checkViewPermission(model.getTable(), user);
        return mapper.toDto(model);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorrectiveK2OFertilizationResponseDto> getCorrectiveK2OFertilizationByTable(Long tableId, String username) {
        UserModel user = findUserByUsernameOrThrow(username);
        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);
        checkViewPermission(table, user);
        return repository.findAllByTableOrderByCtcMinimumAscExchangeableKMinimumAsc(table).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public CorrectiveK2OFertilizationResponseDto updateCorrectiveK2OFertilization(Long criterionId, CorrectiveK2OFertilizationPostRequestDto updateRequestDto, String username) {
        UserModel user = findUserByUsernameOrThrow(username);
        CorrectiveK2OFertilizationModel model = findCriterionByIdOrThrow(criterionId);
        checkModifyPermission(model.getTable(), user);
        if (updateRequestDto.getCtcMinimum() != null) model.setCtcMinimum(updateRequestDto.getCtcMinimum());
        if (updateRequestDto.getCtcMaximum() != null) model.setCtcMaximum(updateRequestDto.getCtcMaximum());
        if (updateRequestDto.getExchangeableKMinimum() != null) model.setExchangeableKMinimum(updateRequestDto.getExchangeableKMinimum());
        if (updateRequestDto.getExchangeableKMaximum() != null) model.setExchangeableKMaximum(updateRequestDto.getExchangeableKMaximum());
        if (updateRequestDto.getRecommendedK2ODose() != null) model.setRecommendedK2ODose(updateRequestDto.getRecommendedK2ODose());
        if (updateRequestDto.getObservations() != null) model.setObservations(updateRequestDto.getObservations());
        if (updateRequestDto.getSources() != null) model.setSources(updateRequestDto.getSources());
        validateModel(model);
        return mapper.toDto(repository.save(model));
    }

    @Override
    @Transactional
    public void deleteCorrectiveK2OFertilization(Long criterionId, String username) {
        UserModel user = findUserByUsernameOrThrow(username);
        CorrectiveK2OFertilizationModel model = findCriterionByIdOrThrow(criterionId);
        checkModifyPermission(model.getTable(), user);
        repository.delete(model);
    }

    private void validateCreateRequest(CorrectiveK2OFertilizationCreateRequestDto request) {
        if (request == null || request.getRecommendedK2ODose() == null) {
            throw new IllegalArgumentException("Dose recomendada de K2O é obrigatória.");
        }
        validateRange(request.getCtcMinimum(), request.getCtcMaximum(), "CTC");
        validateRange(request.getExchangeableKMinimum(), request.getExchangeableKMaximum(), "K+");
    }

    private void validateModel(CorrectiveK2OFertilizationModel model) {
        if (model.getRecommendedK2ODose() == null) {
            throw new IllegalArgumentException("Dose recomendada de K2O é obrigatória.");
        }
        validateRange(model.getCtcMinimum(), model.getCtcMaximum(), "CTC");
        validateRange(model.getExchangeableKMinimum(), model.getExchangeableKMaximum(), "K+");
    }

    private void validateRange(Double minimum, Double maximum, String field) {
        if (minimum != null && maximum != null && minimum > maximum) {
            throw new IllegalArgumentException("Faixa inválida para " + field + ": mínimo maior que máximo.");
        }
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private SoilFertilityInterpretationCriteriaTableModel findTableByIdOrThrow(Long tableId) {
        return tableRepository.findById(tableId)
                .orElseThrow(() -> new EntityNotFoundException("Tabela de critérios de fertilidade do solo não encontrada com o ID: " + tableId));
    }

    private CorrectiveK2OFertilizationModel findCriterionByIdOrThrow(Long criterionId) {
        return repository.findById(criterionId)
                .orElseThrow(() -> new EntityNotFoundException("Linha de adubação corretiva de K2O não encontrada com o ID: " + criterionId));
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
        if (!isCreator(table, user)) {
            throw new AccessDeniedException("Acesso negado. O usuário não é o criador da tabela.");
        }
    }

    private boolean isCreator(SoilFertilityInterpretationCriteriaTableModel table, UserModel user) {
        return table.getCreator() != null && user != null && Objects.equals(table.getCreator().getId(), user.getId());
    }

    private boolean isDefaultTable(SoilFertilityInterpretationCriteriaTableModel table) {
        return table.getCreator() != null && table.getCreator().getCargo() == Cargo.USUARIO_SUPREMO && !table.isPublicTable();
    }

    private boolean isSupremeUser(UserModel user) {
        return user != null && user.getCargo() == Cargo.USUARIO_SUPREMO;
    }
}
