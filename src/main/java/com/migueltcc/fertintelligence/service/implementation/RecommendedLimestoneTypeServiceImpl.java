package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.recommendedLimestoneType.RecommendedLimestoneTypeCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.recommendedLimestoneType.RecommendedLimestoneTypePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.recommendedLimestoneType.RecommendedLimestoneTypeResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.RecommendedLimestoneTypeModel;
import com.migueltcc.fertintelligence.repository.RecommendedLimestoneTypeRepository;
import com.migueltcc.fertintelligence.repository.SoilFertilityInterpretationCriteriaTableRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.RecommendedLimestoneTypeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class RecommendedLimestoneTypeServiceImpl implements RecommendedLimestoneTypeService {

    @Autowired
    private RecommendedLimestoneTypeRepository recommendedLimestoneTypeRepository;

    @Autowired
    private SoilFertilityInterpretationCriteriaTableRepository soilFertilityInterpretationCriteriaTableRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public RecommendedLimestoneTypeResponseDto createRecommendedLimestoneType(Long tableId, RecommendedLimestoneTypeCreateRequestDto createRequestDto, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);
        checkModifyPermission(table, owner);

        recommendedLimestoneTypeRepository.findByTable(table).ifPresent(existing -> {
            throw new IllegalStateException("Já existe uma tabela de tipos de calcário cadastrada para esta tabela.");
        });

        RecommendedLimestoneTypeModel criterion = RecommendedLimestoneTypeModel.builder()
                .table(table)
                .caMgLowRatio(createRequestDto.getCaMgLowRatio())
                .caMgHighRatio(createRequestDto.getCaMgHighRatio())
                .observations(createRequestDto.getObservations())
                .sources(createRequestDto.getSources())
                .build();

        return recommendedLimestoneTypeRepository.save(criterion).toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public RecommendedLimestoneTypeResponseDto getRecommendedLimestoneTypeById(Long criterionId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        RecommendedLimestoneTypeModel criterion = findCriterionByIdOrThrow(criterionId);
        checkViewPermission(criterion.getTable(), owner);
        return criterion.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public RecommendedLimestoneTypeResponseDto getRecommendedLimestoneTypeByTable(Long tableId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);
        checkViewPermission(table, owner);

        RecommendedLimestoneTypeModel criterion = recommendedLimestoneTypeRepository.findByTable(table)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tabela de tipos de calcário não encontrada para a tabela: " + tableId));
        return criterion.toDto();
    }

    @Override
    @Transactional
    public RecommendedLimestoneTypeResponseDto updateRecommendedLimestoneType(Long criterionId, RecommendedLimestoneTypePostRequestDto updateRequestDto, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        RecommendedLimestoneTypeModel criterion = findCriterionByIdOrThrow(criterionId);
        checkModifyPermission(criterion.getTable(), owner);

        if (updateRequestDto.getCaMgLowRatio() != null) criterion.setCaMgLowRatio(updateRequestDto.getCaMgLowRatio());
        if (updateRequestDto.getCaMgHighRatio() != null) criterion.setCaMgHighRatio(updateRequestDto.getCaMgHighRatio());
        if (updateRequestDto.getObservations() != null) criterion.setObservations(updateRequestDto.getObservations());
        if (updateRequestDto.getSources() != null) criterion.setSources(updateRequestDto.getSources());
        return recommendedLimestoneTypeRepository.save(criterion).toDto();
    }

    @Override
    @Transactional
    public void deleteRecommendedLimestoneType(Long criterionId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        RecommendedLimestoneTypeModel criterion = findCriterionByIdOrThrow(criterionId);
        checkModifyPermission(criterion.getTable(), owner);
        recommendedLimestoneTypeRepository.delete(criterion);
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

    private RecommendedLimestoneTypeModel findCriterionByIdOrThrow(Long criterionId) {
        return recommendedLimestoneTypeRepository.findById(criterionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tabela de tipos de calcário não encontrada com o ID: " + criterionId));
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

}
