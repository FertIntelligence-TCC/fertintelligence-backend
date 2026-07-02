package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationMicronutrientFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.repository.DirectRecommendationMicronutrientFertilizerLineRepository;
import com.migueltcc.fertintelligence.repository.DirectRecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.TechnicalRecommendationDocumentSupport.NOT_CALCULATED;
import static com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.TechnicalRecommendationDocumentSupport.NOT_INFORMED;

@Service
@RequiredArgsConstructor
public class SummaryRecommendationReportService {

    private final DirectRecommendationRepository directRecommendationRepository;
    private final DirectRecommendationMicronutrientFertilizerLineRepository micronutrientFertilizerLineRepository;

    public String build(RecommendationModel recommendation) {
        String source = recommendation.getTechnicalReport();
        List<DirectRecommendationMicronutrientFertilizerLineModel> micronutrientLines =
                micronutrientFertilizerLines(resolveDirectRecommendation(recommendation).orElse(null));
        String granulometricClassification = extractGranulometricClassification(source);
        StringBuilder report = new StringBuilder();
        TechnicalRecommendationDocumentSupport.appendStyle(report);
        TechnicalRecommendationDocumentSupport.appendInstitutionalHeader(report);
        report.append("LAUDO TÉCNICO DE RECOMENDAÇÃO DE ADUBAÇÃO\n\n");
        TechnicalRecommendationDocumentSupport.appendIdentification(report, recommendation);

        report.append("Diagnóstico da Fertilidade do Solo da Área Avaliada\n\n");
        report.append("- Classificação granulométrica: ").append(TechnicalRecommendationDocumentSupport.safe(granulometricClassification)).append("\n");
        report.append("- Critérios Muito Baixo/Baixo/Médio/Alto ou Bom/Muito Alto: ver tabela técnica calculada abaixo quando disponível.\n");
        report.append(TechnicalRecommendationDocumentSupport.stripHeading(TechnicalRecommendationDocumentSupport.section(source, "3. Diagnóstico químico"))).append("\n\n");

        TechnicalRecommendationDocumentSupport.appendSourceSectionOrMessage(
                report,
                "Condição de sais no solo",
                TechnicalRecommendationDocumentSupport.section(source, "5. Diagnóstico de salinidade/sodicidade"),
                NOT_CALCULATED);

        report.append("Diagnóstico geral\n\n");
        TechnicalRecommendationDocumentSupport.appendBullet(report, "Classificação granulométrica", granulometricClassification);
        report.append("- Acidez/calagem: consolidada na seção de calagem quando calculada.\n");
        report.append("- Salinidade/sodicidade: consolidada na seção de sais quando calculada.\n");
        report.append("- Fertilidade alta/média/baixa: usar as interpretações por atributo do diagnóstico químico; o backend não gerou uma classe global única persistida.\n\n");

        report.append("Recomendação de calagem e gessagem\n\n");
        appendWithoutHeading(report, source, "7. Calagem", NOT_CALCULATED);
        report.append("- Alerta PRNT comercial: corrigir a dose de calcário conforme o PRNT do produto comercial utilizado.\n\n");
        appendWithoutHeading(report, source, "8. Gessagem", NOT_CALCULATED);

        TechnicalRecommendationDocumentSupport.appendSourceSectionOrMessage(
                report,
                "Recomendação de adubação orgânica",
                TechnicalRecommendationDocumentSupport.subsection(source, "Fontes orgânicas, organominerais e micronutrientes"),
                "Não aplicável com os dados disponíveis.");

        appendMicronutrientRecommendation(report, micronutrientLines);

        TechnicalRecommendationDocumentSupport.appendSourceSectionOrMessage(
                report,
                "Recomendações de N, P2O5 e K2O - plantio",
                TechnicalRecommendationDocumentSupport.section(source, "10. Adubação de plantio"),
                NOT_CALCULATED);
        TechnicalRecommendationDocumentSupport.appendSourceSectionOrMessage(
                report,
                "Recomendações de N, P2O5 e K2O - cobertura",
                TechnicalRecommendationDocumentSupport.section(source, "11. Adubação de cobertura"),
                NOT_CALCULATED);
        TechnicalRecommendationDocumentSupport.appendSourceSectionOrMessage(
                report,
                "Adubos simples e formulados",
                TechnicalRecommendationDocumentSupport.section(source, "13. Fertilizantes recomendados"),
                NOT_CALCULATED);

        report.append("Observações\n\n");
        appendWithoutHeading(report, source, "14. Limitações e alertas", "Nenhuma observação adicional foi persistida.");
        report.append("- Conversões g/m linear e g/cova: ").append(NOT_CALCULATED).append("\n");
        report.append("- Dados institucionais não modelados no backend foram mantidos como ").append(NOT_INFORMED).append("\n");
        return report.toString();
    }

    private void appendMicronutrientRecommendation(
            StringBuilder report,
            List<DirectRecommendationMicronutrientFertilizerLineModel> micronutrientLines) {
        report.append("Recomendação de micronutrientes\n\n");
        if (micronutrientLines == null || micronutrientLines.isEmpty()) {
            report.append("- Micronutrientes: ").append(NOT_CALCULATED).append("\n");
            report.append("- Aviso técnico: não há linhas estruturadas de micronutrientes persistidas para esta recomendação. ")
                    .append("Quando houver dados antigos, consulte a tabela de fontes orgânicas/organominerais/micronutrientes acima.\n\n");
            return;
        }

        report.append("| Micronutriente | Dose micronutriente | Adubo sólido | Concentração | Dose adubo | Dose operacional | Observação técnica |\n");
        report.append("|---|---:|---|---:|---:|---:|---|\n");
        for (DirectRecommendationMicronutrientFertilizerLineModel line : micronutrientLines) {
            if (line == null) continue;
            report.append("| ").append(TechnicalRecommendationDocumentSupport.safeCell(line.getMicronutrient()))
                    .append(" | ").append(TechnicalRecommendationDocumentSupport.formatKgHa(line.getMicronutrientDoseKgHa()))
                    .append(" | ").append(TechnicalRecommendationDocumentSupport.safeCell(line.getFertilizerName()))
                    .append(" | ").append(formatPercent(line.getMicronutrientConcentrationPercent()))
                    .append(" | ").append(TechnicalRecommendationDocumentSupport.formatKgHa(line.getFertilizerDoseKgHa()))
                    .append(" | ").append(formatOperationalDose(line))
                    .append(" | ").append(TechnicalRecommendationDocumentSupport.micronutrientTechnicalObservationCell(line.getTechnicalObservation()))
                    .append(" |\n");
        }
        report.append("\n");
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
        if (directRecommendation == null) {
            return List.of();
        }
        if (micronutrientFertilizerLineRepository == null) {
            return List.of();
        }
        List<DirectRecommendationMicronutrientFertilizerLineModel> lines =
                micronutrientFertilizerLineRepository.findAllByDirectRecommendationOrderByIdAsc(directRecommendation);
        return lines != null ? lines : List.of();
    }

    private String formatPercent(Double value) {
        return value == null ? NOT_CALCULATED : String.format(Locale.US, "%.2f%%", value);
    }

    private String formatOperationalDose(DirectRecommendationMicronutrientFertilizerLineModel line) {
        if (line == null) return NOT_CALCULATED;
        Double value = resolveOperationalDoseValue(line);
        if (value == null) return NOT_CALCULATED;
        String label = line.getDoseUnitLabel();
        return String.format(Locale.US, "%.2f%s", value, label == null || label.isBlank() ? "" : " " + label);
    }

    private Double resolveOperationalDoseValue(DirectRecommendationMicronutrientFertilizerLineModel line) {
        String mode = line.getDoseUnitMode();
        if (mode != null) {
            String normalizedMode = normalize(mode);
            if (normalizedMode.contains("pit") || normalizedMode.contains("cova")) {
                return line.getGramsPerPit();
            }
            if (normalizedMode.contains("linear") || normalizedMode.contains("meter") || normalizedMode.contains("metro")) {
                return line.getGramsPerLinearMeter();
            }
        }
        return line.getGramsPerLinearMeter() != null ? line.getGramsPerLinearMeter() : line.getGramsPerPit();
    }

    private String extractGranulometricClassification(String source) {
        String physicalDiagnosis = TechnicalRecommendationDocumentSupport.section(source, "4. Diagnóstico físico");
        for (List<String> row : TechnicalRecommendationDocumentSupport.tableRows(physicalDiagnosis)) {
            if (row.size() < 4 || !normalize(row.get(0)).contains("classificacao granulometrica")) {
                continue;
            }
            String texturalClass = row.get(3);
            String strategy = row.get(2);
            if (TechnicalRecommendationDocumentSupport.looksUnavailable(texturalClass)) {
                return NOT_CALCULATED;
            }
            if (TechnicalRecommendationDocumentSupport.looksUnavailable(strategy)) {
                return texturalClass;
            }
            return texturalClass + " (" + strategy + ")";
        }
        return NOT_CALCULATED;
    }

    private void appendWithoutHeading(StringBuilder report, String source, String heading, String fallback) {
        String section = TechnicalRecommendationDocumentSupport.stripHeading(TechnicalRecommendationDocumentSupport.section(source, heading));
        report.append(section.isBlank() ? fallback : section).append("\n\n");
    }

    private String normalize(String value) {
        if (value == null) return "";
        String noAccent = Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return noAccent.toLowerCase(Locale.ROOT);
    }
}
