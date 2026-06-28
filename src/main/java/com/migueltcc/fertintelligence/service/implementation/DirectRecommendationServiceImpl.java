package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.dto.directRecommendation.DirectRecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.directRecommendation.DirectRecommendationMicronutrientFertilizerLineResponseDto;
import com.migueltcc.fertintelligence.dto.directRecommendation.DirectRecommendationPostRequestDto;
import com.migueltcc.fertintelligence.dto.directRecommendation.DirectRecommendationResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationMicronutrientFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.DirectRecommendationMicronutrientFertilizerLineRepository;
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
    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;
    private final PermissionManager permissionManager;
    private final DirectRecommendationReportService directRecommendationReportService;

    @Override
    @Transactional
    public DirectRecommendationResponseDto create(DirectRecommendationCreateRequestDto dto, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        RecommendationModel recommendation = findRecommendationByIdOrThrow(dto.getRecommendationId());
        permissionManager.assertCanReadPlot(recommendation.getPlot(), user);
        DirectRecommendationModel directRecommendation = createInitial(recommendation, dto.getTechnicalReport());
        return toDto(directRecommendation);
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
        if (recommendation == null || recommendation.getId() == null) {
            throw new IllegalArgumentException("A recomendação precisa estar salva antes da criação da Recomendação Direta.");
        }
        if (technicalReport == null || technicalReport.isBlank()) {
            throw new IllegalArgumentException("O conteúdo da Recomendação Direta não pode ser vazio.");
        }
        DirectRecommendationModel directRecommendation = directRecommendationRepository.findByRecommendation(recommendation)
                .orElseGet(() -> saveNew(recommendation, technicalReport));
        syncMicronutrientFertilizerLines(directRecommendation, micronutrientFertilizerRows);
        return directRecommendation;
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
                .fertilizerName(row.getFertilizerName())
                .micronutrientConcentrationPercent(row.getMicronutrientConcentrationPercent())
                .fertilizerDoseKgHa(row.getFertilizerDoseKgHa())
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
        return toDto(directRecommendation);
    }

    @Override
    @Transactional
    public DirectRecommendationResponseDto getByRecommendation(Long recommendationId, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        RecommendationModel recommendation = findRecommendationByIdOrThrow(recommendationId);
        permissionManager.assertCanReadPlot(recommendation.getPlot(), user);
        return directRecommendationRepository.findByRecommendation(recommendation)
                .map(this::toDto)
                .orElseGet(() -> toDto(createInitial(recommendation, directRecommendationReportService.build(recommendation))));
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

        return toDto(directRecommendationRepository.save(directRecommendation));
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

    private DirectRecommendationResponseDto toDto(DirectRecommendationModel model) {
        RecommendationModel recommendation = model.getRecommendation();
        DirectRecommendationReportService.DirectDoseUnitMetadata doseUnitMetadata =
                directRecommendationReportService.resolveDoseUnitMetadata(recommendation);
        return DirectRecommendationResponseDto.builder()
                .id(model.getId())
                .recommendationId(recommendation != null ? recommendation.getId() : null)
                .documentName(model.getDocumentName() != null ? model.getDocumentName() : DirectRecommendationModel.DOCUMENT_NAME)
                .technicalReport(model.getTechnicalReport())
                .content(model.getTechnicalReport())
                .doseUnitMode(doseUnitMetadata != null ? doseUnitMetadata.doseUnitMode() : "INSUFFICIENT_DATA")
                .doseUnitLabel(doseUnitMetadata != null ? doseUnitMetadata.doseUnitLabel() : null)
                .applicableDoseColumn(doseUnitMetadata != null ? doseUnitMetadata.applicableDoseColumn() : null)
                .micronutrientFertilizerLines(toMicronutrientFertilizerLineDtos(model))
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }

    private List<DirectRecommendationMicronutrientFertilizerLineResponseDto> toMicronutrientFertilizerLineDtos(
            DirectRecommendationModel directRecommendation) {
        List<DirectRecommendationMicronutrientFertilizerLineModel> lines =
                micronutrientFertilizerLineRepository.findAllByDirectRecommendationOrderByIdAsc(directRecommendation);
        if (lines == null) {
            return List.of();
        }
        return lines.stream()
                .map(this::toMicronutrientFertilizerLineDto)
                .toList();
    }

    private DirectRecommendationMicronutrientFertilizerLineResponseDto toMicronutrientFertilizerLineDto(
            DirectRecommendationMicronutrientFertilizerLineModel line) {
        return DirectRecommendationMicronutrientFertilizerLineResponseDto.builder()
                .id(line.getId())
                .micronutrient(line.getMicronutrient())
                .micronutrientDoseKgHa(line.getMicronutrientDoseKgHa())
                .fertilizerId(line.getFertilizerId())
                .fertilizerName(line.getFertilizerName())
                .micronutrientConcentrationPercent(line.getMicronutrientConcentrationPercent())
                .fertilizerDoseKgHa(line.getFertilizerDoseKgHa())
                .doseUnitMode(line.getDoseUnitMode())
                .doseUnitLabel(line.getDoseUnitLabel())
                .gramsPerLinearMeter(line.getGramsPerLinearMeter())
                .gramsPerPit(line.getGramsPerPit())
                .technicalObservation(line.getTechnicalObservation())
                .build();
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
