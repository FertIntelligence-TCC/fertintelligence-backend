package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.TechnicalRecommendationDocumentSupport.LINEAR_CONVERSION_UNAVAILABLE;
import static com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.TechnicalRecommendationDocumentSupport.NOT_CALCULATED;
import static com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.TechnicalRecommendationDocumentSupport.NOT_APPLICABLE;

@Service
public class DirectRecommendationReportService {

    public String build(RecommendationModel recommendation) {
        String source = recommendation.getTechnicalReport();
        StringBuilder report = new StringBuilder();
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
        appendNpkTable(report, source);

        report.append("## Observação sobre MAP\n\n");
        report.append(resolveMapObservation(source)).append("\n\n");
        report.append("## Observações finais\n\n");
        report.append(TechnicalRecommendationDocumentSupport.stripHeading(TechnicalRecommendationDocumentSupport.section(source, "14. Limitações e alertas")));
        report.append("\n\n- Conversões g/m linear e g/cova: ").append(LINEAR_CONVERSION_UNAVAILABLE).append("\n");
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

    private void appendNpkTable(StringBuilder report, String source) {
        report.append("## Tabela de N, P2O5 e K2O\n\n");
        report.append("| Adubação | Adubos simples/formulados | kg/ha | g/m linear | g/cova |\n");
        report.append("|---|---|---:|---:|---:|\n");
        boolean appended = appendFertilizationRows(report, source, "10. Adubação de plantio");
        appended = appendFertilizationRows(report, source, "11. Adubação de cobertura") || appended;
        if (!appended) {
            report.append("| Não calculado | Não calculado | Não calculado | ")
                    .append(LINEAR_CONVERSION_UNAVAILABLE).append(" | ")
                    .append(LINEAR_CONVERSION_UNAVAILABLE).append(" |\n");
        }
        report.append("\n");
    }

    private boolean appendFertilizationRows(StringBuilder report, String source, String sectionName) {
        boolean appended = false;
        for (List<String> row : TechnicalRecommendationDocumentSupport.tableRows(TechnicalRecommendationDocumentSupport.section(source, sectionName))) {
            if (row.size() < 4) continue;
            String phase = row.get(0);
            String fertilizer = row.get(2);
            String quantity = row.get(3);
            if (TechnicalRecommendationDocumentSupport.looksUnavailable(fertilizer) || TechnicalRecommendationDocumentSupport.looksUnavailable(quantity)) continue;
            report.append("| ").append(TechnicalRecommendationDocumentSupport.safeCell(phase))
                    .append(" | ").append(TechnicalRecommendationDocumentSupport.safeCell(fertilizer))
                    .append(" | ").append(TechnicalRecommendationDocumentSupport.safeCell(quantity))
                    .append(" | ").append(LINEAR_CONVERSION_UNAVAILABLE)
                    .append(" | ").append(LINEAR_CONVERSION_UNAVAILABLE)
                    .append(" |\n");
            appended = true;
        }
        return appended;
    }

    private String resolveMapObservation(String source) {
        String normalized = source == null ? "" : source.toLowerCase();
        if (normalized.contains("map")) {
            return "MAP aparece nas fontes/fertilizantes recomendados calculados no laudo técnico persistido.";
        }
        return "Não aplicável com os dados disponíveis.";
    }
}
