package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.dto.directRecommendation.DirectRecommendationCoverageFormulatedFertilizerLineResponseDto;
import com.migueltcc.fertintelligence.dto.directRecommendation.DirectRecommendationMicronutrientFertilizerLineResponseDto;
import com.migueltcc.fertintelligence.dto.directRecommendation.DirectRecommendationPlantingFormulatedFertilizerLineResponseDto;
import com.migueltcc.fertintelligence.dto.directRecommendation.DirectRecommendationResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationCoverageFormulatedFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationMicronutrientFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationPlantingFormulatedFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.repository.DirectRecommendationCoverageFormulatedFertilizerLineRepository;
import com.migueltcc.fertintelligence.repository.DirectRecommendationMicronutrientFertilizerLineRepository;
import com.migueltcc.fertintelligence.repository.DirectRecommendationPlantingFormulatedFertilizerLineRepository;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.CropSpacingCalculationService;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.DirectRecommendationReportService;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.RecommendationStructuredDataAssembler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DirectRecommendationDtoMapper {

    private final DirectRecommendationMicronutrientFertilizerLineRepository micronutrientFertilizerLineRepository;
    private final DirectRecommendationPlantingFormulatedFertilizerLineRepository plantingFormulatedFertilizerLineRepository;
    private final DirectRecommendationCoverageFormulatedFertilizerLineRepository coverageFormulatedFertilizerLineRepository;
    private final DirectRecommendationReportService directRecommendationReportService;
    private final CropSpacingCalculationService cropSpacingCalculationService;
    private final RecommendationStructuredDataAssembler structuredDataAssembler;

    public DirectRecommendationResponseDto toDto(DirectRecommendationModel model) {
        if (model == null) {
            return null;
        }
        RecommendationModel recommendation = model.getRecommendation();
        DirectRecommendationReportService.DirectDoseUnitMetadata doseUnitMetadata =
                directRecommendationReportService.resolveDoseUnitMetadata(recommendation);
        return DirectRecommendationResponseDto.builder()
                .id(model.getId())
                .recommendationId(recommendation != null ? recommendation.getId() : null)
                .documentName(model.getDocumentName() != null ? model.getDocumentName() : DirectRecommendationModel.DOCUMENT_NAME)
                .technicalReport(model.getTechnicalReport())
                .content(model.getTechnicalReport())
                .structuredTables(structuredDataAssembler.directSections(recommendation, model.getTechnicalReport()))
                .doseUnitMode(doseUnitMetadata != null ? doseUnitMetadata.doseUnitMode() : "INSUFFICIENT_DATA")
                .doseUnitLabel(doseUnitMetadata != null ? doseUnitMetadata.doseUnitLabel() : null)
                .applicableDoseColumn(doseUnitMetadata != null ? doseUnitMetadata.applicableDoseColumn() : null)
                .fertilizationObservations(directRecommendationReportService.resolveFertilizationObservations(recommendation))
                .technicalObservations(structuredDataAssembler.observations(
                        directRecommendationReportService.resolveFertilizationObservations(recommendation)))
                .micronutrientFertilizerLines(toMicronutrientFertilizerLineDtos(model))
                .plantingFormulatedFertilizerLines(toPlantingFormulatedFertilizerLineDtos(model))
                .coverageFormulatedFertilizerLines(toCoverageFormulatedFertilizerLineDtos(model))
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
        ApplicableDose applicableDose = applicableDose(line.getDoseUnitMode(), line.getDoseUnitLabel(),
                line.getGramsPerLinearMeter(), line.getGramsPerPit());
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
                .applicableDoseValue(applicableDose.value())
                .applicableDoseUnit(applicableDose.unit())
                .applicableDoseColumn(applicableDose.column())
                .technicalObservation(shortTechnicalObservation(line.getTechnicalObservation()))
                .build();
    }

    private List<DirectRecommendationPlantingFormulatedFertilizerLineResponseDto> toPlantingFormulatedFertilizerLineDtos(
            DirectRecommendationModel directRecommendation) {
        List<DirectRecommendationPlantingFormulatedFertilizerLineModel> lines =
                plantingFormulatedFertilizerLineRepository.findAllByDirectRecommendationOrderByDoseKgHaDescIdAsc(directRecommendation);
        if (lines == null) {
            return List.of();
        }
        return lines.stream()
                .map(this::toPlantingFormulatedFertilizerLineDto)
                .toList();
    }

    private DirectRecommendationPlantingFormulatedFertilizerLineResponseDto toPlantingFormulatedFertilizerLineDto(
            DirectRecommendationPlantingFormulatedFertilizerLineModel line) {
        ApplicableDose applicableDose = applicableDose(line.getDoseUnitMode(), line.getDoseUnitLabel(),
                line.getGramsPerLinearMeter(), line.getGramsPerPit());
        return DirectRecommendationPlantingFormulatedFertilizerLineResponseDto.builder()
                .id(line.getId())
                .phase(line.getPhase())
                .fertilizerId(line.getFertilizerId())
                .fertilizerName(line.getFertilizerName())
                .nitrogenPercent(line.getNitrogenPercent())
                .p2o5Percent(line.getP2o5Percent())
                .k2oPercent(line.getK2oPercent())
                .relationUsed(line.getRelationUsed())
                .selectionType(line.getSelectionType())
                .doseKgHa(line.getDoseKgHa())
                .doseUnitMode(line.getDoseUnitMode())
                .doseUnitLabel(line.getDoseUnitLabel())
                .gramsPerLinearMeter(line.getGramsPerLinearMeter())
                .gramsPerPit(line.getGramsPerPit())
                .applicableDoseValue(applicableDose.value())
                .applicableDoseUnit(applicableDose.unit())
                .applicableDoseColumn(applicableDose.column())
                .technicalObservation(shortTechnicalObservation(line.getTechnicalObservation()))
                .build();
    }

    private List<DirectRecommendationCoverageFormulatedFertilizerLineResponseDto> toCoverageFormulatedFertilizerLineDtos(
            DirectRecommendationModel directRecommendation) {
        List<DirectRecommendationCoverageFormulatedFertilizerLineModel> lines =
                coverageFormulatedFertilizerLineRepository.findAllByDirectRecommendationOrderByCoverageOrderAscDoseKgHaDescIdAsc(directRecommendation);
        if (lines == null) {
            return List.of();
        }
        return lines.stream()
                .map(this::toCoverageFormulatedFertilizerLineDto)
                .toList();
    }

    private DirectRecommendationCoverageFormulatedFertilizerLineResponseDto toCoverageFormulatedFertilizerLineDto(
            DirectRecommendationCoverageFormulatedFertilizerLineModel line) {
        ApplicableDose applicableDose = applicableDose(line.getDoseUnitMode(), line.getDoseUnitLabel(),
                line.getGramsPerLinearMeter(), line.getGramsPerPit());
        return DirectRecommendationCoverageFormulatedFertilizerLineResponseDto.builder()
                .id(line.getId())
                .coverageOrder(line.getCoverageOrder())
                .phase(line.getPhase())
                .fertilizerId(line.getFertilizerId())
                .fertilizerName(line.getFertilizerName())
                .nitrogenPercent(line.getNitrogenPercent())
                .p2o5Percent(line.getP2o5Percent())
                .k2oPercent(line.getK2oPercent())
                .requiredN(line.getRequiredN())
                .requiredP2O5(line.getRequiredP2O5())
                .requiredK2O(line.getRequiredK2O())
                .relationUsed(line.getRelationUsed())
                .selectionType(line.getSelectionType())
                .doseKgHa(line.getDoseKgHa())
                .doseUnitMode(line.getDoseUnitMode())
                .doseUnitLabel(line.getDoseUnitLabel())
                .gramsPerLinearMeter(line.getGramsPerLinearMeter())
                .gramsPerPit(line.getGramsPerPit())
                .applicableDoseValue(applicableDose.value())
                .applicableDoseUnit(applicableDose.unit())
                .applicableDoseColumn(applicableDose.column())
                .technicalObservation(shortTechnicalObservation(line.getTechnicalObservation()))
                .build();
    }

    private ApplicableDose applicableDose(String mode, String label, Double gramsPerLinearMeter, Double gramsPerPit) {
        CropSpacingCalculationService.DoseUnitMetadata metadata =
                cropSpacingCalculationService.resolveDoseUnitMetadata(mode, label);
        return new ApplicableDose(
                cropSpacingCalculationService.applicableDoseValue(mode, gramsPerLinearMeter, gramsPerPit),
                metadata.doseUnitLabel(),
                metadata.applicableDoseColumn());
    }

    private String shortTechnicalObservation(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim().replaceAll("\\s+", " ");
        String lower = normalized.toLowerCase();
        int memoryIndex = lower.indexOf("memória de cálculo");
        if (memoryIndex < 0) {
            memoryIndex = lower.indexOf("memoria de calculo");
        }
        if (memoryIndex > 0) {
            normalized = normalized.substring(0, memoryIndex).trim();
        }
        int sentenceEnd = normalized.indexOf(". ");
        if (sentenceEnd > 0 && sentenceEnd < 160) {
            normalized = normalized.substring(0, sentenceEnd + 1);
        }
        if (normalized.isBlank() || normalized.equalsIgnoreCase("Não informado")) {
            return null;
        }
        return normalized.length() <= 220 ? normalized : normalized.substring(0, 217).trim() + "...";
    }

    private record ApplicableDose(Double value, String unit, String column) {
    }
}
