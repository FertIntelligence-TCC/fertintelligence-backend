package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveP2O5Fertilization.CorrectiveP2O5FertilizationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveP2O5Fertilization.CorrectiveP2O5FertilizationPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveP2O5Fertilization.CorrectiveP2O5FertilizationResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.CorrectiveP2O5FertilizationModel;
import com.migueltcc.fertintelligence.repository.CorrectiveP2O5FertilizationRepository;
import com.migueltcc.fertintelligence.repository.SoilFertilityInterpretationCriteriaTableRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.CorrectiveP2O5FertilizationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class CorrectiveP2O5FertilizationServiceImpl implements CorrectiveP2O5FertilizationService {

    @Autowired
    private CorrectiveP2O5FertilizationRepository repository;

    @Autowired
    private SoilFertilityInterpretationCriteriaTableRepository tableRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CorrectiveP2O5FertilizationMapper mapper;

    @Override
    @Transactional
    public CorrectiveP2O5FertilizationResponseDto createCorrectiveP2O5Fertilization(Long tableId, CorrectiveP2O5FertilizationCreateRequestDto createRequestDto, String username) {
        UserModel user = findUserByUsernameOrThrow(username);
        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);
        checkModifyPermission(table, user);
        validateCreateRequest(createRequestDto);

        CorrectiveP2O5FertilizationModel model = CorrectiveP2O5FertilizationModel.builder()
                .table(table)
                .clayContentMinimum(createRequestDto.getClayContentMinimum())
                .clayContentMaximum(createRequestDto.getClayContentMaximum())
                .availablePMehlich1Minimum(createRequestDto.getAvailablePMehlich1Minimum())
                .availablePMehlich1Maximum(createRequestDto.getAvailablePMehlich1Maximum())
                .recommendedP2O5Dose(createRequestDto.getRecommendedP2O5Dose())
                .observations(createRequestDto.getObservations())
                .sources(createRequestDto.getSources())
                .build();
        return mapper.toDto(repository.save(model));
    }

    @Override
    @Transactional(readOnly = true)
    public CorrectiveP2O5FertilizationResponseDto getCorrectiveP2O5FertilizationById(Long criterionId, String username) {
        UserModel user = findUserByUsernameOrThrow(username);
        CorrectiveP2O5FertilizationModel model = findCriterionByIdOrThrow(criterionId);
        checkViewPermission(model.getTable(), user);
        return mapper.toDto(model);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorrectiveP2O5FertilizationResponseDto> getCorrectiveP2O5FertilizationByTable(Long tableId, String username) {
        UserModel user = findUserByUsernameOrThrow(username);
        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);
        checkViewPermission(table, user);
        return repository.findAllByTableOrderByClayContentMinimumAscAvailablePMehlich1MinimumAsc(table).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public CorrectiveP2O5FertilizationResponseDto updateCorrectiveP2O5Fertilization(Long criterionId, CorrectiveP2O5FertilizationPostRequestDto updateRequestDto, String username) {
        UserModel user = findUserByUsernameOrThrow(username);
        CorrectiveP2O5FertilizationModel model = findCriterionByIdOrThrow(criterionId);
        checkModifyPermission(model.getTable(), user);
        if (updateRequestDto.getClayContentMinimum() != null) model.setClayContentMinimum(updateRequestDto.getClayContentMinimum());
        if (updateRequestDto.getClayContentMaximum() != null) model.setClayContentMaximum(updateRequestDto.getClayContentMaximum());
        if (updateRequestDto.getAvailablePMehlich1Minimum() != null) model.setAvailablePMehlich1Minimum(updateRequestDto.getAvailablePMehlich1Minimum());
        if (updateRequestDto.getAvailablePMehlich1Maximum() != null) model.setAvailablePMehlich1Maximum(updateRequestDto.getAvailablePMehlich1Maximum());
        if (updateRequestDto.getRecommendedP2O5Dose() != null) model.setRecommendedP2O5Dose(updateRequestDto.getRecommendedP2O5Dose());
        if (updateRequestDto.getObservations() != null) model.setObservations(updateRequestDto.getObservations());
        if (updateRequestDto.getSources() != null) model.setSources(updateRequestDto.getSources());
        validateModel(model);
        return mapper.toDto(repository.save(model));
    }

    @Override
    @Transactional
    public void deleteCorrectiveP2O5Fertilization(Long criterionId, String username) {
        UserModel user = findUserByUsernameOrThrow(username);
        CorrectiveP2O5FertilizationModel model = findCriterionByIdOrThrow(criterionId);
        checkModifyPermission(model.getTable(), user);
        repository.delete(model);
    }

    private void validateCreateRequest(CorrectiveP2O5FertilizationCreateRequestDto request) {
        if (request == null || request.getRecommendedP2O5Dose() == null) {
            throw new IllegalArgumentException("Dose recomendada de P2O5 é obrigatória.");
        }
        validateRange(request.getClayContentMinimum(), request.getClayContentMaximum(), "argila");
        validateRange(request.getAvailablePMehlich1Minimum(), request.getAvailablePMehlich1Maximum(), "P Mehlich-1");
    }

    private void validateModel(CorrectiveP2O5FertilizationModel model) {
        if (model.getRecommendedP2O5Dose() == null) {
            throw new IllegalArgumentException("Dose recomendada de P2O5 é obrigatória.");
        }
        validateRange(model.getClayContentMinimum(), model.getClayContentMaximum(), "argila");
        validateRange(model.getAvailablePMehlich1Minimum(), model.getAvailablePMehlich1Maximum(), "P Mehlich-1");
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

    private CorrectiveP2O5FertilizationModel findCriterionByIdOrThrow(Long criterionId) {
        return repository.findById(criterionId)
                .orElseThrow(() -> new EntityNotFoundException("Linha de adubação corretiva de P2O5 não encontrada com o ID: " + criterionId));
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
