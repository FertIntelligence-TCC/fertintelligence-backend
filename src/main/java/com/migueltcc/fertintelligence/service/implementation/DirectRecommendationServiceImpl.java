package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.dto.directRecommendation.DirectRecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.directRecommendation.DirectRecommendationPostRequestDto;
import com.migueltcc.fertintelligence.dto.directRecommendation.DirectRecommendationResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationCoverageFormulatedFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationMicronutrientFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationPlantingFormulatedFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.DirectRecommendationCoverageFormulatedFertilizerLineRepository;
import com.migueltcc.fertintelligence.repository.DirectRecommendationMicronutrientFertilizerLineRepository;
import com.migueltcc.fertintelligence.repository.DirectRecommendationPlantingFormulatedFertilizerLineRepository;
import com.migueltcc.fertintelligence.repository.DirectRecommendationRepository;
import com.migueltcc.fertintelligence.repository.RecommendationRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.DirectRecommendationService;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.DirectRecommendationReportService;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.RecommendationCalculationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectRecommendationServiceImpl implements DirectRecommendationService {

    private final DirectRecommendationRepository directRecommendationRepository;
    private final DirectRecommendationMicronutrientFertilizerLineRepository micronutrientFertilizerLineRepository;
    private final DirectRecommendationPlantingFormulatedFertilizerLineRepository plantingFormulatedFertilizerLineRepository;
    private final DirectRecommendationCoverageFormulatedFertilizerLineRepository coverageFormulatedFertilizerLineRepository;
    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;
    private final PermissionManager permissionManager;
    private final DirectRecommendationReportService directRecommendationReportService;
    private final DirectRecommendationDtoMapper directRecommendationDtoMapper;

    @Override
    @Transactional
    public DirectRecommendationResponseDto create(DirectRecommendationCreateRequestDto dto, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        RecommendationModel recommendation = findRecommendationByIdOrThrow(dto.getRecommendationId());
        permissionManager.assertCanReadPlot(recommendation.getPlot(), user);
        DirectRecommendationModel directRecommendation = createInitial(recommendation, dto.getTechnicalReport());
        return directRecommendationDtoMapper.toDto(directRecommendation);
    }

    @Override
    @Transactional
    public DirectRecommendationModel createInitial(RecommendationModel recommendation, String technicalReport) {
        return createInitial(recommendation, technicalReport, List.of());
    }

    @Override
    @Transactional
    public DirectRecommendationModel createInitial(
            RecommendationModel recommendation,
            String technicalReport,
            List<RecommendationCalculationService.MicronutrientFertilizerRecommendationRow> micronutrientFertilizerRows) {
        return createInitial(recommendation, technicalReport, micronutrientFertilizerRows, List.of(), List.of());
    }

    @Override
    @Transactional
    public DirectRecommendationModel createInitial(
            RecommendationModel recommendation,
            String technicalReport,
            List<RecommendationCalculationService.MicronutrientFertilizerRecommendationRow> micronutrientFertilizerRows,
            List<RecommendationCalculationService.PlantingFormulatedFertilizerRecommendationRow> plantingFormulatedFertilizerRows,
            List<RecommendationCalculationService.CoverageFormulatedFertilizerRecommendationRow> coverageFormulatedFertilizerRows) {
        return createInitial(recommendation, technicalReport, List.of(), micronutrientFertilizerRows,
                plantingFormulatedFertilizerRows, coverageFormulatedFertilizerRows);
    }

    @Override
    @Transactional
    public DirectRecommendationModel createInitial(
            RecommendationModel recommendation,
            String technicalReport,
            List<RecommendationCalculationService.FertilizationRecommendationRow> fertilizationRows,
            List<RecommendationCalculationService.MicronutrientFertilizerRecommendationRow> micronutrientFertilizerRows,
            List<RecommendationCalculationService.PlantingFormulatedFertilizerRecommendationRow> plantingFormulatedFertilizerRows,
            List<RecommendationCalculationService.CoverageFormulatedFertilizerRecommendationRow> coverageFormulatedFertilizerRows) {
        if (recommendation == null || recommendation.getId() == null) {
            throw new IllegalArgumentException("A recomendação precisa estar salva antes da criação da Recomendação Direta.");
        }
        if (technicalReport == null || technicalReport.isBlank()) {
            throw new IllegalArgumentException("O conteúdo da Recomendação Direta não pode ser vazio.");
        }
        DirectRecommendationModel directRecommendation = directRecommendationRepository.findByRecommendation(recommendation)
                .orElseGet(() -> saveNew(recommendation, technicalReport));
        syncMicronutrientFertilizerLines(directRecommendation, micronutrientFertilizerRows);
        syncPlantingFormulatedFertilizerLines(directRecommendation, plantingFormulatedFertilizerRows);
        syncCoverageFormulatedFertilizerLines(directRecommendation, coverageFormulatedFertilizerRows);
        if ((fertilizationRows != null && !fertilizationRows.isEmpty())
                || hasStructuredLines(micronutrientFertilizerRows, plantingFormulatedFertilizerRows, coverageFormulatedFertilizerRows)) {
            directRecommendation.setTechnicalReport(directRecommendationReportService.buildWithFertilizationRows(recommendation, fertilizationRows));
            directRecommendation = directRecommendationRepository.save(directRecommendation);
        }
        return directRecommendation;
    }

    private boolean hasStructuredLines(
            List<RecommendationCalculationService.MicronutrientFertilizerRecommendationRow> micronutrientFertilizerRows,
            List<RecommendationCalculationService.PlantingFormulatedFertilizerRecommendationRow> plantingFormulatedFertilizerRows,
            List<RecommendationCalculationService.CoverageFormulatedFertilizerRecommendationRow> coverageFormulatedFertilizerRows) {
        return (micronutrientFertilizerRows != null && !micronutrientFertilizerRows.isEmpty())
                || (plantingFormulatedFertilizerRows != null && !plantingFormulatedFertilizerRows.isEmpty())
                || (coverageFormulatedFertilizerRows != null && !coverageFormulatedFertilizerRows.isEmpty());
    }

    private DirectRecommendationModel saveNew(RecommendationModel recommendation, String technicalReport) {
        DirectRecommendationModel directRecommendation = DirectRecommendationModel.builder()
                .recommendation(recommendation)
                .documentName(DirectRecommendationModel.DOCUMENT_NAME)
                .technicalReport(technicalReport)
                .build();

        try {
            DirectRecommendationModel saved = directRecommendationRepository.saveAndFlush(directRecommendation);
            recommendation.setDirectRecommendation(saved);
            return saved;
        } catch (DataIntegrityViolationException ex) {
            return directRecommendationRepository.findByRecommendation(recommendation)
                    .orElseThrow(() -> ex);
        }
    }

    private void syncMicronutrientFertilizerLines(
            DirectRecommendationModel directRecommendation,
            List<RecommendationCalculationService.MicronutrientFertilizerRecommendationRow> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        micronutrientFertilizerLineRepository.deleteAllByDirectRecommendation(directRecommendation);
        List<DirectRecommendationMicronutrientFertilizerLineModel> models = rows.stream()
                .map(row -> toMicronutrientFertilizerLineModel(directRecommendation, row))
                .toList();
        micronutrientFertilizerLineRepository.saveAll(models);
    }

    private DirectRecommendationMicronutrientFertilizerLineModel toMicronutrientFertilizerLineModel(
            DirectRecommendationModel directRecommendation,
            RecommendationCalculationService.MicronutrientFertilizerRecommendationRow row) {
        return DirectRecommendationMicronutrientFertilizerLineModel.builder()
                .directRecommendation(directRecommendation)
                .micronutrient(row.getMicronutrient())
                .micronutrientDoseKgHa(row.getMicronutrientDoseKgHa())
                .fertilizerId(row.getFertilizerId())
                .fertilizerDoseKgHa(row.getFertilizerDoseKgHa())
                .doseUnitMode(row.getDoseUnitMode())
                .doseUnitLabel(row.getDoseUnitLabel())
                .gramsPerLinearMeter(row.getGramsPerLinearMeter())
                .gramsPerPit(row.getGramsPerPit())
                .technicalObservation(row.getTechnicalObservation())
                .build();
    }

    private void syncPlantingFormulatedFertilizerLines(
            DirectRecommendationModel directRecommendation,
            List<RecommendationCalculationService.PlantingFormulatedFertilizerRecommendationRow> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        plantingFormulatedFertilizerLineRepository.deleteAllByDirectRecommendation(directRecommendation);
        List<DirectRecommendationPlantingFormulatedFertilizerLineModel> models = rows.stream()
                .map(row -> toPlantingFormulatedFertilizerLineModel(directRecommendation, row))
                .toList();
        plantingFormulatedFertilizerLineRepository.saveAll(models);
    }

    private DirectRecommendationPlantingFormulatedFertilizerLineModel toPlantingFormulatedFertilizerLineModel(
            DirectRecommendationModel directRecommendation,
            RecommendationCalculationService.PlantingFormulatedFertilizerRecommendationRow row) {
        return DirectRecommendationPlantingFormulatedFertilizerLineModel.builder()
                .directRecommendation(directRecommendation)
                .phase(row.getPhase())
                .fertilizerId(row.getFertilizerId())
                .relationUsed(row.getRelationUsed())
                .selectionType(row.getSelectionType())
                .doseKgHa(row.getDoseKgHa())
                .doseUnitMode(row.getDoseUnitMode())
                .doseUnitLabel(row.getDoseUnitLabel())
                .gramsPerLinearMeter(row.getGramsPerLinearMeter())
                .gramsPerPit(row.getGramsPerPit())
                .technicalObservation(row.getTechnicalObservation())
                .build();
    }

    private void syncCoverageFormulatedFertilizerLines(
            DirectRecommendationModel directRecommendation,
            List<RecommendationCalculationService.CoverageFormulatedFertilizerRecommendationRow> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        coverageFormulatedFertilizerLineRepository.deleteAllByDirectRecommendation(directRecommendation);
        List<DirectRecommendationCoverageFormulatedFertilizerLineModel> models = rows.stream()
                .map(row -> toCoverageFormulatedFertilizerLineModel(directRecommendation, row))
                .toList();
        coverageFormulatedFertilizerLineRepository.saveAll(models);
    }

    private DirectRecommendationCoverageFormulatedFertilizerLineModel toCoverageFormulatedFertilizerLineModel(
            DirectRecommendationModel directRecommendation,
            RecommendationCalculationService.CoverageFormulatedFertilizerRecommendationRow row) {
        return DirectRecommendationCoverageFormulatedFertilizerLineModel.builder()
                .directRecommendation(directRecommendation)
                .coverageOrder(row.getCoverageOrder())
                .phase(row.getPhase())
                .fertilizerId(row.getFertilizerId())
                .requiredN(row.getRequiredN())
                .requiredP2O5(row.getRequiredP2O5())
                .requiredK2O(row.getRequiredK2O())
                .relationUsed(row.getRelationUsed())
                .selectionType(row.getSelectionType())
                .doseKgHa(row.getDoseKgHa())
                .doseUnitMode(row.getDoseUnitMode())
                .doseUnitLabel(row.getDoseUnitLabel())
                .gramsPerLinearMeter(row.getGramsPerLinearMeter())
                .gramsPerPit(row.getGramsPerPit())
                .technicalObservation(row.getTechnicalObservation())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DirectRecommendationResponseDto get(Long id, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        DirectRecommendationModel directRecommendation = findDirectRecommendationByIdOrThrow(id);
        permissionManager.assertCanReadPlot(directRecommendation.getRecommendation().getPlot(), user);
        return directRecommendationDtoMapper.toDto(directRecommendation);
    }

    @Override
    @Transactional
    public DirectRecommendationResponseDto getByRecommendation(Long recommendationId, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        RecommendationModel recommendation = findRecommendationByIdOrThrow(recommendationId);
        permissionManager.assertCanReadPlot(recommendation.getPlot(), user);
        return directRecommendationRepository.findByRecommendation(recommendation)
                .map(directRecommendationDtoMapper::toDto)
                .orElseGet(() -> directRecommendationDtoMapper.toDto(
                        createInitial(recommendation, directRecommendationReportService.build(recommendation))));
    }

    @Override
    @Transactional
    public DirectRecommendationResponseDto update(Long id, DirectRecommendationPostRequestDto dto, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        DirectRecommendationModel directRecommendation = findDirectRecommendationByIdOrThrow(id);
        permissionManager.assertCanReadPlot(directRecommendation.getRecommendation().getPlot(), user);

        if (dto.getNewTechnicalReport() != null && !dto.getNewTechnicalReport().isBlank()) {
            directRecommendation.setTechnicalReport(dto.getNewTechnicalReport());
        }

        return directRecommendationDtoMapper.toDto(directRecommendationRepository.save(directRecommendation));
    }

    @Override
    @Transactional
    public void delete(Long id, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        DirectRecommendationModel directRecommendation = findDirectRecommendationByIdOrThrow(id);
        permissionManager.assertCanReadPlot(directRecommendation.getRecommendation().getPlot(), user);
        directRecommendation.getRecommendation().setDirectRecommendation(null);
        directRecommendationRepository.delete(directRecommendation);
    }

    private UserModel findUserByUsernameOrEmailOrThrow(String usernameOrEmail) {
        return userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + usernameOrEmail));
    }

    private RecommendationModel findRecommendationByIdOrThrow(Long id) {
        return recommendationRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new EntityNotFoundException("Recomendação não encontrada com o ID: " + id));
    }

    private DirectRecommendationModel findDirectRecommendationByIdOrThrow(Long id) {
        return directRecommendationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recomendação Direta não encontrada com o ID: " + id));
    }
}
