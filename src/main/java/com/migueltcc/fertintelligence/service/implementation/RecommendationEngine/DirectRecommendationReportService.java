package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.crop.CropSpacingMode;
import com.migueltcc.fertintelligence.model.fertintelligence.AnnualCropFolderModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationCoverageFormulatedFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationMicronutrientFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationPlantingFormulatedFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFertilizationTableModel;
import com.migueltcc.fertintelligence.repository.AnnualCropFolderRepository;
import com.migueltcc.fertintelligence.repository.CropFertilizationTableRepository;
import com.migueltcc.fertintelligence.repository.CropRepository;
import com.migueltcc.fertintelligence.repository.DirectRecommendationCoverageFormulatedFertilizerLineRepository;
import com.migueltcc.fertintelligence.repository.DirectRecommendationMicronutrientFertilizerLineRepository;
import com.migueltcc.fertintelligence.repository.DirectRecommendationPlantingFormulatedFertilizerLineRepository;
import com.migueltcc.fertintelligence.repository.DirectRecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.TechnicalRecommendationDocumentSupport.LINEAR_CONVERSION_UNAVAILABLE;
import static com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.TechnicalRecommendationDocumentSupport.NOT_CALCULATED;
import static com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.TechnicalRecommendationDocumentSupport.NOT_APPLICABLE;

@Service
@RequiredArgsConstructor
public class DirectRecommendationReportService {

    private static final DirectDoseUnitMetadata INSUFFICIENT_DATA_METADATA =
            new DirectDoseUnitMetadata("INSUFFICIENT_DATA", null, null);

    private final CropSpacingCalculationService cropSpacingCalculationService;
    private final AnnualCropFolderRepository annualCropFolderRepository;
    private final CropRepository cropRepository;
    private final CropFertilizationTableRepository cropFertilizationTableRepository;
    private final DirectRecommendationRepository directRecommendationRepository;
    private final DirectRecommendationMicronutrientFertilizerLineRepository micronutrientFertilizerLineRepository;
    private final DirectRecommendationPlantingFormulatedFertilizerLineRepository plantingFormulatedFertilizerLineRepository;
    private final DirectRecommendationCoverageFormulatedFertilizerLineRepository coverageFormulatedFertilizerLineRepository;

    public String build(RecommendationModel recommendation) {
        DirectRecommendationModel directRecommendation = resolveDirectRecommendation(recommendation).orElse(null);
        return build(
                recommendation,
                resolveCrop(recommendation).orElse(null),
                micronutrientFertilizerLines(directRecommendation),
                plantingFormulatedFertilizerLines(directRecommendation),
                coverageFormulatedFertilizerLines(directRecommendation));
    }

    public DirectDoseUnitMetadata resolveDoseUnitMetadata(RecommendationModel recommendation) {
        if (recommendation == null) {
            return INSUFFICIENT_DATA_METADATA;
        }

        return resolveDoseUnitMetadata(resolveCrop(recommendation).orElse(null));
    }

    public String resolveFertilizationObservations(RecommendationModel recommendation) {
        if (recommendation == null
                || recommendation.getCropFertilizationTableId() == null
                || cropFertilizationTableRepository == null) {
            return "";
        }
        return cropFertilizationTableRepository.findById(recommendation.getCropFertilizationTableId())
                .map(CropFertilizationTableModel::getObservations)
                .map(String::trim)
                .filter(observations -> !observations.isBlank())
                .orElse("");
    }

    private DirectDoseUnitMetadata resolveDoseUnitMetadata(CropModel crop) {
        CropSpacingMode mode = resolveApplicableMode(crop, null);
        return toDirectDoseUnitMetadata(cropSpacingCalculationService.resolveDoseUnitMetadata(mode));
    }

    public String build(RecommendationModel recommendation, CropModel crop) {
        return build(recommendation, crop, List.of(), List.of(), List.of());
    }

    private String build(RecommendationModel recommendation,
                         CropModel crop,
                         List<DirectRecommendationMicronutrientFertilizerLineModel> micronutrientFertilizerLines,
                         List<DirectRecommendationPlantingFormulatedFertilizerLineModel> plantingFormulatedFertilizerLines,
                         List<DirectRecommendationCoverageFormulatedFertilizerLineModel> coverageFormulatedFertilizerLines) {
        String source = recommendation != null ? recommendation.getTechnicalReport() : null;
        DirectDoseUnitMetadata doseUnitMetadata = resolveEffectiveDoseUnitMetadata(
                resolveDoseUnitMetadata(crop),
                micronutrientFertilizerLines,
                plantingFormulatedFertilizerLines,
                coverageFormulatedFertilizerLines);
        StringBuilder report = new StringBuilder();
        List<String> spacingWarnings = new ArrayList<>();
        TechnicalRecommendationDocumentSupport.appendStyle(report);
        TechnicalRecommendationDocumentSupport.appendInstitutionalHeader(report);
        report.append("LAUDO TÉCNICO DE RECOMENDAÇÃO DE ADUBAÇÃO\n\n");
        TechnicalRecommendationDocumentSupport.appendIdentification(report, recommendation);

        TechnicalRecommendationDocumentSupport.appendSourceSectionOrMessage(
                report,
                "Recomendação de calagem e gessagem",
                TechnicalRecommendationDocumentSupport.section(source, "7. Calagem") + "\n\n" + TechnicalRecommendationDocumentSupport.stripHeading(TechnicalRecommendationDocumentSupport.section(source, "8. Gessagem")),
                NOT_CALCULATED);

        TechnicalRecommendationDocumentSupport.appendSourceSectionOrMessage(
                report,
                "Recomendação orgânica",
                TechnicalRecommendationDocumentSupport.subsection(source, "Fontes orgânicas, organominerais e micronutrientes"),
                NOT_APPLICABLE);

        appendMicronutrientTable(report, source, doseUnitMetadata, micronutrientFertilizerLines);
        appendNpkTable(report, source, crop, doseUnitMetadata, spacingWarnings,
                plantingFormulatedFertilizerLines, coverageFormulatedFertilizerLines);

        report.append("Observações sobre adubação\n\n");
        report.append(resolveFertilizationObservations(recommendation));
        return report.toString();
    }

    private void appendMicronutrientTable(StringBuilder report,
                                          String source,
                                          DirectDoseUnitMetadata doseUnitMetadata,
                                          List<DirectRecommendationMicronutrientFertilizerLineModel> directLines) {
        report.append("Tabela de micronutrientes\n\n");
        if (directLines != null && !directLines.isEmpty()) {
            report.append("| Micronutriente | Adubo sólido | Dose micronutriente | Dose adubo | ")
                    .append(spacingColumnHeader(doseUnitMetadata)).append(" | Observação técnica |\n");
            report.append("|---|---|---:|---:|---:|---|\n");
            for (DirectRecommendationMicronutrientFertilizerLineModel line : directLines) {
                if (line == null) continue;
                report.append("| ").append(TechnicalRecommendationDocumentSupport.safeCell(line.getMicronutrient()))
                        .append(" | ").append(TechnicalRecommendationDocumentSupport.safeCell(line.getFertilizerName()))
                        .append(" | ").append(TechnicalRecommendationDocumentSupport.formatKgHa(line.getMicronutrientDoseKgHa()))
                        .append(" | ").append(TechnicalRecommendationDocumentSupport.formatKgHa(line.getFertilizerDoseKgHa()))
                        .append(" | ").append(applicableLocalizedDose(line.getDoseUnitMode(), line.getGramsPerLinearMeter(), line.getGramsPerPit()))
                        .append(" | ").append(TechnicalRecommendationDocumentSupport.micronutrientTechnicalObservationCell(line.getTechnicalObservation()))
                        .append(" |\n");
            }
            report.append("\n");
            return;
        }

        report.append("| Nutriente/Adubo | kg/ha | ").append(spacingColumnHeader(doseUnitMetadata)).append(" |\n");
        report.append("|---|---:|---:|\n");
        List<List<String>> rows = TechnicalRecommendationDocumentSupport.tableRows(
                TechnicalRecommendationDocumentSupport.subsection(source, "Fontes orgânicas, organominerais e micronutrientes"));
        boolean appended = false;
        for (List<String> row : rows) {
            if (row.size() < 5) continue;
            String sourceName = row.get(2);
            String dose = row.get(3);
            String unit = row.get(4);
            if (TechnicalRecommendationDocumentSupport.looksUnavailable(sourceName)) continue;
            report.append("| ").append(TechnicalRecommendationDocumentSupport.safeCell(sourceName))
                    .append(" | ").append(TechnicalRecommendationDocumentSupport.safeCell(dose + " " + unit))
                    .append(" | ").append(spacingUnavailableCell())
                    .append(" |\n");
            appended = true;
        }
        if (!appended) {
            report.append("| Não calculado | Não calculado | ").append(spacingUnavailableCell()).append(" |\n");
        }
        report.append("\n");
    }

    private void appendNpkTable(StringBuilder report,
                                String source,
                                CropModel crop,
                                DirectDoseUnitMetadata doseUnitMetadata,
                                List<String> spacingWarnings,
                                List<DirectRecommendationPlantingFormulatedFertilizerLineModel> plantingFormulatedFertilizerLines,
                                List<DirectRecommendationCoverageFormulatedFertilizerLineModel> coverageFormulatedFertilizerLines) {
        report.append("Tabela de N, P2O5 e K2O\n\n");
        if (hasFormulatedLines(plantingFormulatedFertilizerLines, coverageFormulatedFertilizerLines)) {
            report.append("| Adubação | Formulado | Relação N-P2O5-K2O | kg/ha | ")
                    .append(spacingColumnHeader(doseUnitMetadata)).append(" | Observação técnica |\n");
            report.append("|---|---|---|---:|---:|---|\n");
            appendPlantingFormulatedRows(report, plantingFormulatedFertilizerLines);
            appendAdditionalPlantingRowsForFormulatedTable(report, source, crop, doseUnitMetadata, spacingWarnings);
            appendCoverageFormulatedRows(report, coverageFormulatedFertilizerLines);
            if (coverageFormulatedFertilizerLines == null || coverageFormulatedFertilizerLines.isEmpty()) {
                appendCoverageFallbackRowsForFormulatedTable(report, source, crop, doseUnitMetadata, spacingWarnings);
            }
            report.append("\n");
            return;
        }

        report.append("| Adubação | Adubos simples/formulados | kg/ha | ")
                .append(spacingColumnHeader(doseUnitMetadata)).append(" |\n");
        report.append("|---|---|---:|---:|\n");
        boolean appended = appendFertilizationRows(report, source, "10. Adubação de plantio", crop, doseUnitMetadata, spacingWarnings);
        appended = appendFertilizationRows(report, source, "11. Adubação de cobertura", crop, doseUnitMetadata, spacingWarnings) || appended;
        if (!appended) {
            report.append("| Não calculado | Não calculado | Não calculado | ")
                    .append(spacingUnavailableCell()).append(" |\n");
        }
        report.append("\n");
    }

    private boolean appendFertilizationRows(StringBuilder report,
                                            String source,
                                            String sectionName,
                                            CropModel crop,
                                            DirectDoseUnitMetadata doseUnitMetadata,
                                            List<String> spacingWarnings) {
        boolean appended = false;
        for (List<String> row : TechnicalRecommendationDocumentSupport.tableRows(TechnicalRecommendationDocumentSupport.section(source, sectionName))) {
            if (row.size() < 4) continue;
            String phase = row.get(0);
            String fertilizer = row.get(2);
            String quantity = row.get(3);
            if (TechnicalRecommendationDocumentSupport.looksUnavailable(fertilizer) || TechnicalRecommendationDocumentSupport.looksUnavailable(quantity)) continue;
            String spacingDose = calculateSpacingDose(crop, quantity, doseUnitMetadata, spacingWarnings);
            report.append("| ").append(TechnicalRecommendationDocumentSupport.safeCell(phase))
                    .append(" | ").append(TechnicalRecommendationDocumentSupport.safeCell(fertilizer))
                    .append(" | ").append(TechnicalRecommendationDocumentSupport.safeCell(quantity))
                    .append(" | ").append(spacingDose)
                    .append(" |\n");
            appended = true;
        }
        return appended;
    }

    private void appendPlantingFormulatedRows(
            StringBuilder report,
            List<DirectRecommendationPlantingFormulatedFertilizerLineModel> lines) {
        if (lines == null) return;
        for (DirectRecommendationPlantingFormulatedFertilizerLineModel line : lines) {
            if (line == null) continue;
            report.append("| ").append(TechnicalRecommendationDocumentSupport.safeCell(line.getPhase()))
                    .append(" | ").append(TechnicalRecommendationDocumentSupport.safeCell(line.getFertilizerName()))
                    .append(" | ").append(TechnicalRecommendationDocumentSupport.safeCell(line.getRelationUsed()))
                    .append(" | ").append(TechnicalRecommendationDocumentSupport.formatKgHa(line.getDoseKgHa()))
                    .append(" | ").append(applicableLocalizedDose(line.getDoseUnitMode(), line.getGramsPerLinearMeter(), line.getGramsPerPit()))
                    .append(" | ").append(TechnicalRecommendationDocumentSupport.safeCell(line.getTechnicalObservation()))
                    .append(" |\n");
        }
    }

    private void appendAdditionalPlantingRowsForFormulatedTable(
            StringBuilder report,
            String source,
            CropModel crop,
            DirectDoseUnitMetadata doseUnitMetadata,
            List<String> spacingWarnings) {
        for (List<String> row : TechnicalRecommendationDocumentSupport.tableRows(
                TechnicalRecommendationDocumentSupport.section(source, "10. Adubação de plantio"))) {
            if (row.size() < 4) continue;
            String phase = row.get(0);
            String fertilizer = row.get(2);
            String quantity = row.get(3);
            if (phase == null || "Plantio".equalsIgnoreCase(phase.trim())) continue;
            if (TechnicalRecommendationDocumentSupport.looksUnavailable(fertilizer)
                    || TechnicalRecommendationDocumentSupport.looksUnavailable(quantity)) continue;
            report.append("| ").append(TechnicalRecommendationDocumentSupport.safeCell(phase))
                    .append(" | ").append(TechnicalRecommendationDocumentSupport.safeCell(fertilizer))
                    .append(" | ").append(NOT_APPLICABLE)
                    .append(" | ").append(TechnicalRecommendationDocumentSupport.safeCell(quantity))
                    .append(" | ").append(calculateSpacingDose(crop, quantity, doseUnitMetadata, spacingWarnings))
                    .append(" | Complemento de nutriente secundário ou adubo simples propagado do laudo técnico. |\n");
        }
    }

    private void appendCoverageFormulatedRows(
            StringBuilder report,
            List<DirectRecommendationCoverageFormulatedFertilizerLineModel> lines) {
        if (lines == null) return;
        for (DirectRecommendationCoverageFormulatedFertilizerLineModel line : lines) {
            if (line == null) continue;
            String phase = coveragePhase(line.getPhase(), line.getCoverageOrder());
            report.append("| ").append(TechnicalRecommendationDocumentSupport.safeCell(phase))
                    .append(" | ").append(TechnicalRecommendationDocumentSupport.safeCell(line.getFertilizerName()))
                    .append(" | ").append(TechnicalRecommendationDocumentSupport.safeCell(line.getRelationUsed()))
                    .append(" | ").append(TechnicalRecommendationDocumentSupport.formatKgHa(line.getDoseKgHa()))
                    .append(" | ").append(applicableLocalizedDose(line.getDoseUnitMode(), line.getGramsPerLinearMeter(), line.getGramsPerPit()))
                    .append(" | ").append(TechnicalRecommendationDocumentSupport.safeCell(line.getTechnicalObservation()))
                    .append(" |\n");
        }
    }

    private boolean appendCoverageFallbackRowsForFormulatedTable(StringBuilder report,
                                                                 String source,
                                                                 CropModel crop,
                                                                 DirectDoseUnitMetadata doseUnitMetadata,
                                                                 List<String> spacingWarnings) {
        boolean appended = false;
        for (List<String> row : TechnicalRecommendationDocumentSupport.tableRows(
                TechnicalRecommendationDocumentSupport.section(source, "11. Adubação de cobertura"))) {
            if (row.size() < 4) continue;
            String phase = row.get(0);
            String fertilizer = row.get(2);
            String quantity = row.get(3);
            if (TechnicalRecommendationDocumentSupport.looksUnavailable(fertilizer)
                    || TechnicalRecommendationDocumentSupport.looksUnavailable(quantity)) {
                continue;
            }
            String spacingDose = calculateSpacingDose(crop, quantity, doseUnitMetadata, spacingWarnings);
            report.append("| ").append(TechnicalRecommendationDocumentSupport.safeCell(phase))
                    .append(" | ").append(TechnicalRecommendationDocumentSupport.safeCell(fertilizer))
                    .append(" | ").append(NOT_APPLICABLE)
                    .append(" | ").append(TechnicalRecommendationDocumentSupport.safeCell(quantity))
                    .append(" | ").append(spacingDose)
                    .append(" | Cobertura propagada do laudo técnico; não houve linha estruturada de formulado NPK para esta cobertura. |\n");
            appended = true;
        }
        return appended;
    }

    private String coveragePhase(String phase, Integer coverageOrder) {
        if (coverageOrder == null) {
            return phase;
        }
        if (phase == null || phase.isBlank()) {
            return "Cobertura " + coverageOrder;
        }
        String normalized = phase.toLowerCase(Locale.ROOT);
        return normalized.contains(String.valueOf(coverageOrder)) ? phase : phase + " " + coverageOrder;
    }

    private String calculateSpacingDose(CropModel crop,
                                        String quantity,
                                        DirectDoseUnitMetadata doseUnitMetadata,
                                        List<String> spacingWarnings) {
        if (!hasApplicableDoseColumn(doseUnitMetadata)) {
            return spacingUnavailableCell();
        }

        Optional<Double> kgPerHectare = TechnicalRecommendationDocumentSupport.extractKgHa(quantity);
        CropSpacingCalculationService.CropSpacingDoseResult result =
                cropSpacingCalculationService.calculate(crop, kgPerHectare.orElse(null));
        CropSpacingMode applicableMode = resolveApplicableMode(crop, result);

        if (result.technicalWarning() != null && !spacingWarnings.contains(result.technicalWarning())) {
            spacingWarnings.add(result.technicalWarning());
        }

        if (applicableMode == CropSpacingMode.PLANTS_PER_LINEAR_METER) {
            return formatGramDose(result.gramsPerLinearMeter());
        }
        if (applicableMode == CropSpacingMode.PIT) {
            return formatGramDose(result.gramsPerPit());
        }
        return spacingUnavailableCell();
    }

    private CropSpacingMode resolveApplicableMode(CropModel crop, CropSpacingCalculationService.CropSpacingDoseResult result) {
        if (result != null && result.resolvedMode() != null && result.resolvedMode() != CropSpacingMode.UNKNOWN) {
            return result.resolvedMode();
        }
        if (crop == null) {
            return CropSpacingMode.UNKNOWN;
        }
        if (crop.getSpacingMode() != null && crop.getSpacingMode() != CropSpacingMode.UNKNOWN) {
            return crop.getSpacingMode();
        }
        if (crop.getPlantsPerMeter() != null && Double.isFinite(crop.getPlantsPerMeter()) && crop.getPlantsPerMeter() > 0d) {
            return CropSpacingMode.PLANTS_PER_LINEAR_METER;
        }
        return CropSpacingMode.UNKNOWN;
    }

    private String formatGramDose(Double value) {
        return value == null ? LINEAR_CONVERSION_UNAVAILABLE : String.format(Locale.US, "%.2f", value);
    }

    private Optional<CropModel> resolveCrop(RecommendationModel recommendation) {
        if (recommendation == null || recommendation.getPlot() == null || recommendation.getCropYear() == null || recommendation.getCropName() == null) {
            return Optional.empty();
        }
        Optional<AnnualCropFolderModel> folder = annualCropFolderRepository.findByPlotAndCropsYear(
                recommendation.getPlot(), recommendation.getCropYear());
        if (folder.isEmpty()) {
            return Optional.empty();
        }
        Optional<CropModel> cropByName = cropRepository.findTopByFolderAndNameOrderByIdDesc(folder.get(), recommendation.getCropName());
        if (cropByName.isPresent()) {
            return cropByName;
        }
        List<CropModel> crops = cropRepository.findAllByFolder(folder.get());
        return crops.size() == 1 ? Optional.of(crops.get(0)) : Optional.empty();
    }

    private String spacingColumnHeader(DirectDoseUnitMetadata doseUnitMetadata) {
        if (doseUnitMetadata != null && doseUnitMetadata.doseUnitLabel() != null && !doseUnitMetadata.doseUnitLabel().isBlank()) {
            return doseUnitMetadata.doseUnitLabel();
        }
        return "Conversão por espaçamento";
    }

    private String spacingObservationLabel(DirectDoseUnitMetadata doseUnitMetadata) {
        if (doseUnitMetadata != null && doseUnitMetadata.doseUnitLabel() != null && !doseUnitMetadata.doseUnitLabel().isBlank()) {
            return "Conversão " + doseUnitMetadata.doseUnitLabel();
        }
        return "Conversão por espaçamento";
    }

    private boolean hasApplicableDoseColumn(DirectDoseUnitMetadata doseUnitMetadata) {
        return cropSpacingCalculationService.hasApplicableDoseColumn(toCropSpacingDoseUnitMetadata(doseUnitMetadata));
    }

    private String applicableLocalizedDose(String doseUnitMode, Double gramsPerLinearMeter, Double gramsPerPit) {
        return formatGramDose(cropSpacingCalculationService.applicableDoseValue(
                doseUnitMode, gramsPerLinearMeter, gramsPerPit));
    }

    private boolean hasFormulatedLines(
            List<DirectRecommendationPlantingFormulatedFertilizerLineModel> plantingFormulatedFertilizerLines,
            List<DirectRecommendationCoverageFormulatedFertilizerLineModel> coverageFormulatedFertilizerLines) {
        return (plantingFormulatedFertilizerLines != null && !plantingFormulatedFertilizerLines.isEmpty())
                || (coverageFormulatedFertilizerLines != null && !coverageFormulatedFertilizerLines.isEmpty());
    }

    private DirectDoseUnitMetadata resolveEffectiveDoseUnitMetadata(
            DirectDoseUnitMetadata cropMetadata,
            List<DirectRecommendationMicronutrientFertilizerLineModel> micronutrientFertilizerLines,
            List<DirectRecommendationPlantingFormulatedFertilizerLineModel> plantingFormulatedFertilizerLines,
            List<DirectRecommendationCoverageFormulatedFertilizerLineModel> coverageFormulatedFertilizerLines) {
        if (hasApplicableDoseColumn(cropMetadata)) {
            return cropMetadata;
        }
        DirectDoseUnitMetadata lineMetadata = firstLineDoseUnitMetadata(
                micronutrientFertilizerLines,
                plantingFormulatedFertilizerLines,
                coverageFormulatedFertilizerLines);
        return hasApplicableDoseColumn(lineMetadata) ? lineMetadata : cropMetadata;
    }

    private DirectDoseUnitMetadata firstLineDoseUnitMetadata(
            List<DirectRecommendationMicronutrientFertilizerLineModel> micronutrientFertilizerLines,
            List<DirectRecommendationPlantingFormulatedFertilizerLineModel> plantingFormulatedFertilizerLines,
            List<DirectRecommendationCoverageFormulatedFertilizerLineModel> coverageFormulatedFertilizerLines) {
        if (micronutrientFertilizerLines != null) {
            for (DirectRecommendationMicronutrientFertilizerLineModel line : micronutrientFertilizerLines) {
                DirectDoseUnitMetadata metadata = lineDoseUnitMetadata(
                        line != null ? line.getDoseUnitMode() : null,
                        line != null ? line.getDoseUnitLabel() : null);
                if (hasApplicableDoseColumn(metadata)) return metadata;
            }
        }
        if (plantingFormulatedFertilizerLines != null) {
            for (DirectRecommendationPlantingFormulatedFertilizerLineModel line : plantingFormulatedFertilizerLines) {
                DirectDoseUnitMetadata metadata = lineDoseUnitMetadata(
                        line != null ? line.getDoseUnitMode() : null,
                        line != null ? line.getDoseUnitLabel() : null);
                if (hasApplicableDoseColumn(metadata)) return metadata;
            }
        }
        if (coverageFormulatedFertilizerLines != null) {
            for (DirectRecommendationCoverageFormulatedFertilizerLineModel line : coverageFormulatedFertilizerLines) {
                DirectDoseUnitMetadata metadata = lineDoseUnitMetadata(
                        line != null ? line.getDoseUnitMode() : null,
                        line != null ? line.getDoseUnitLabel() : null);
                if (hasApplicableDoseColumn(metadata)) return metadata;
            }
        }
        return INSUFFICIENT_DATA_METADATA;
    }

    private DirectDoseUnitMetadata lineDoseUnitMetadata(String mode, String label) {
        return toDirectDoseUnitMetadata(cropSpacingCalculationService.resolveDoseUnitMetadata(mode, label));
    }

    private Optional<DirectRecommendationModel> resolveDirectRecommendation(RecommendationModel recommendation) {
        if (recommendation == null) {
            return Optional.empty();
        }
        if (recommendation.getDirectRecommendation() != null) {
            return Optional.of(recommendation.getDirectRecommendation());
        }
        if (recommendation.getId() == null || directRecommendationRepository == null) {
            return Optional.empty();
        }
        return directRecommendationRepository.findByRecommendation(recommendation);
    }

    private List<DirectRecommendationMicronutrientFertilizerLineModel> micronutrientFertilizerLines(
            DirectRecommendationModel directRecommendation) {
        if (directRecommendation == null || micronutrientFertilizerLineRepository == null) {
            return List.of();
        }
        List<DirectRecommendationMicronutrientFertilizerLineModel> lines =
                micronutrientFertilizerLineRepository.findAllByDirectRecommendationOrderByIdAsc(directRecommendation);
        return lines != null ? lines : List.of();
    }

    private List<DirectRecommendationPlantingFormulatedFertilizerLineModel> plantingFormulatedFertilizerLines(
            DirectRecommendationModel directRecommendation) {
        if (directRecommendation == null || plantingFormulatedFertilizerLineRepository == null) {
            return List.of();
        }
        List<DirectRecommendationPlantingFormulatedFertilizerLineModel> lines =
                plantingFormulatedFertilizerLineRepository.findAllByDirectRecommendationOrderByDoseKgHaDescIdAsc(directRecommendation);
        return lines != null ? lines : List.of();
    }

    private List<DirectRecommendationCoverageFormulatedFertilizerLineModel> coverageFormulatedFertilizerLines(
            DirectRecommendationModel directRecommendation) {
        if (directRecommendation == null || coverageFormulatedFertilizerLineRepository == null) {
            return List.of();
        }
        List<DirectRecommendationCoverageFormulatedFertilizerLineModel> lines =
                coverageFormulatedFertilizerLineRepository.findAllByDirectRecommendationOrderByCoverageOrderAscDoseKgHaDescIdAsc(directRecommendation);
        return lines != null ? lines : List.of();
    }

    private String spacingUnavailableCell() {
        return LINEAR_CONVERSION_UNAVAILABLE;
    }

    private DirectDoseUnitMetadata toDirectDoseUnitMetadata(CropSpacingCalculationService.DoseUnitMetadata metadata) {
        if (metadata == null) {
            return INSUFFICIENT_DATA_METADATA;
        }
        return new DirectDoseUnitMetadata(metadata.doseUnitMode(), metadata.doseUnitLabel(), metadata.applicableDoseColumn());
    }

    private CropSpacingCalculationService.DoseUnitMetadata toCropSpacingDoseUnitMetadata(DirectDoseUnitMetadata metadata) {
        if (metadata == null) {
            return null;
        }
        return new CropSpacingCalculationService.DoseUnitMetadata(
                metadata.doseUnitMode(), metadata.doseUnitLabel(), metadata.applicableDoseColumn());
    }

    private String resolveSpacingObservation(DirectDoseUnitMetadata doseUnitMetadata, List<String> spacingWarnings) {
        if (!hasApplicableDoseColumn(doseUnitMetadata)) {
            return "Não foi possível determinar uma unidade aplicável com segurança; dados de cultura ou espaçamento insuficientes.";
        }
        if (spacingWarnings == null || spacingWarnings.isEmpty()) {
            return "Calculadas conforme modo de espaçamento da cultura quando houve kg/ha na linha.";
        }
        return String.join(" ", spacingWarnings);
    }

    public record DirectDoseUnitMetadata(String doseUnitMode, String doseUnitLabel, String applicableDoseColumn) {
    }
}
