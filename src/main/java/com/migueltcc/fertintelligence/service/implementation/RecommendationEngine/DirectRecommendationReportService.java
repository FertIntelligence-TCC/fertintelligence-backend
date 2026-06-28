package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.crop.CropSpacingMode;
import com.migueltcc.fertintelligence.model.fertintelligence.AnnualCropFolderModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.repository.AnnualCropFolderRepository;
import com.migueltcc.fertintelligence.repository.CropRepository;
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

    private final CropSpacingCalculationService cropSpacingCalculationService;
    private final AnnualCropFolderRepository annualCropFolderRepository;
    private final CropRepository cropRepository;

    public String build(RecommendationModel recommendation) {
        return build(recommendation, resolveCrop(recommendation).orElse(null));
    }

    public String build(RecommendationModel recommendation, CropModel crop) {
        String source = recommendation.getTechnicalReport();
        StringBuilder report = new StringBuilder();
        List<String> spacingWarnings = new ArrayList<>();
        TechnicalRecommendationDocumentSupport.appendStyle(report);
        TechnicalRecommendationDocumentSupport.appendInstitutionalHeader(report);
        report.append("# LAUDO TÉCNICO DE RECOMENDAÇÃO DE ADUBAÇÃO\n\n");
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

        appendMicronutrientTable(report, source);
        appendNpkTable(report, source, crop, spacingWarnings);

        report.append("## Observação sobre MAP\n\n");
        report.append(resolveMapObservation(source)).append("\n\n");
        report.append("## Observações finais\n\n");
        report.append(TechnicalRecommendationDocumentSupport.stripHeading(TechnicalRecommendationDocumentSupport.section(source, "14. Limitações e alertas")));
        report.append("\n\n- Conversões g/m linear e g/cova: ").append(resolveSpacingObservation(spacingWarnings)).append("\n");
        return report.toString();
    }

    private void appendMicronutrientTable(StringBuilder report, String source) {
        report.append("## Tabela de micronutrientes\n\n");
        report.append("| Nutriente/Adubo | kg/ha | g/m linear | g/cova |\n");
        report.append("|---|---:|---:|---:|\n");
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
                    .append(" | ").append(LINEAR_CONVERSION_UNAVAILABLE)
                    .append(" | ").append(LINEAR_CONVERSION_UNAVAILABLE)
                    .append(" |\n");
            appended = true;
        }
        if (!appended) {
            report.append("| Não calculado | Não calculado | ").append(LINEAR_CONVERSION_UNAVAILABLE)
                    .append(" | ").append(LINEAR_CONVERSION_UNAVAILABLE).append(" |\n");
        }
        report.append("\n");
    }

    private void appendNpkTable(StringBuilder report, String source, CropModel crop, List<String> spacingWarnings) {
        report.append("## Tabela de N, P2O5 e K2O\n\n");
        report.append("| Adubação | Adubos simples/formulados | kg/ha | g/m linear | g/cova |\n");
        report.append("|---|---|---:|---:|---:|\n");
        boolean appended = appendFertilizationRows(report, source, "10. Adubação de plantio", crop, spacingWarnings);
        appended = appendFertilizationRows(report, source, "11. Adubação de cobertura", crop, spacingWarnings) || appended;
        if (!appended) {
            report.append("| Não calculado | Não calculado | Não calculado | ")
                    .append(LINEAR_CONVERSION_UNAVAILABLE).append(" | ")
                    .append(LINEAR_CONVERSION_UNAVAILABLE).append(" |\n");
        }
        report.append("\n");
    }

    private boolean appendFertilizationRows(StringBuilder report,
                                            String source,
                                            String sectionName,
                                            CropModel crop,
                                            List<String> spacingWarnings) {
        boolean appended = false;
        for (List<String> row : TechnicalRecommendationDocumentSupport.tableRows(TechnicalRecommendationDocumentSupport.section(source, sectionName))) {
            if (row.size() < 4) continue;
            String phase = row.get(0);
            String fertilizer = row.get(2);
            String quantity = row.get(3);
            if (TechnicalRecommendationDocumentSupport.looksUnavailable(fertilizer) || TechnicalRecommendationDocumentSupport.looksUnavailable(quantity)) continue;
            SpacingDoseColumns spacingColumns = calculateSpacingColumns(crop, quantity, spacingWarnings);
            report.append("| ").append(TechnicalRecommendationDocumentSupport.safeCell(phase))
                    .append(" | ").append(TechnicalRecommendationDocumentSupport.safeCell(fertilizer))
                    .append(" | ").append(TechnicalRecommendationDocumentSupport.safeCell(quantity))
                    .append(" | ").append(spacingColumns.gramsPerLinearMeter())
                    .append(" | ").append(spacingColumns.gramsPerPit())
                    .append(" |\n");
            appended = true;
        }
        return appended;
    }

    private SpacingDoseColumns calculateSpacingColumns(CropModel crop, String quantity, List<String> spacingWarnings) {
        Optional<Double> kgPerHectare = TechnicalRecommendationDocumentSupport.extractKgHa(quantity);
        CropSpacingCalculationService.CropSpacingDoseResult result =
                cropSpacingCalculationService.calculate(crop, kgPerHectare.orElse(null));
        CropSpacingMode applicableMode = resolveApplicableMode(crop, result);

        if (result.technicalWarning() != null && !spacingWarnings.contains(result.technicalWarning())) {
            spacingWarnings.add(result.technicalWarning());
        }

        if (applicableMode == CropSpacingMode.PLANTS_PER_LINEAR_METER) {
            return new SpacingDoseColumns(formatGramDose(result.gramsPerLinearMeter()), NOT_APPLICABLE);
        }
        if (applicableMode == CropSpacingMode.PIT) {
            return new SpacingDoseColumns(NOT_APPLICABLE, formatGramDose(result.gramsPerPit()));
        }
        return new SpacingDoseColumns(LINEAR_CONVERSION_UNAVAILABLE, LINEAR_CONVERSION_UNAVAILABLE);
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

    private String resolveSpacingObservation(List<String> spacingWarnings) {
        if (spacingWarnings == null || spacingWarnings.isEmpty()) {
            return "Calculadas conforme modo de espaçamento da cultura quando houve kg/ha na linha.";
        }
        return String.join(" ", spacingWarnings);
    }

    private record SpacingDoseColumns(String gramsPerLinearMeter, String gramsPerPit) {
    }

    private String resolveMapObservation(String source) {
        String normalized = source == null ? "" : source.toLowerCase();
        if (normalized.contains("map")) {
            return "MAP aparece nas fontes/fertilizantes recomendados calculados no laudo técnico persistido.";
        }
        return "Não aplicável com os dados disponíveis.";
    }
}
