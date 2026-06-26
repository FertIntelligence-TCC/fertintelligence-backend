package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import org.springframework.stereotype.Service;

import static com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.TechnicalRecommendationDocumentSupport.NOT_CALCULATED;
import static com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.TechnicalRecommendationDocumentSupport.NOT_INFORMED;

@Service
public class SummaryRecommendationReportService {

    public String build(RecommendationModel recommendation) {
        String source = recommendation.getTechnicalReport();
        StringBuilder report = new StringBuilder();
        TechnicalRecommendationDocumentSupport.appendStyle(report);
        TechnicalRecommendationDocumentSupport.appendInstitutionalHeader(report);
        report.append("# LAUDO TÉCNICO DE RECOMENDAÇÃO DE ADUBAÇÃO\n\n");
        TechnicalRecommendationDocumentSupport.appendIdentification(report, recommendation);

        report.append("## Diagnóstico da Fertilidade do Solo da Área Avaliada\n\n");
        report.append("- Classificação granulométrica: ").append(TechnicalRecommendationDocumentSupport.safe(recommendation.getPlot() != null ? recommendation.getPlot().getSoilTexture() : null)).append("\n");
        report.append("- Critérios Muito Baixo/Baixo/Médio/Alto ou Bom/Muito Alto: ver tabela técnica calculada abaixo quando disponível.\n");
        report.append("- Fertigrama/gráfico: ").append(NOT_CALCULATED).append("\n\n");
        report.append(TechnicalRecommendationDocumentSupport.stripHeading(TechnicalRecommendationDocumentSupport.section(source, "3. Diagnóstico químico"))).append("\n\n");

        TechnicalRecommendationDocumentSupport.appendSourceSectionOrMessage(
                report,
                "Condição de sais no solo",
                TechnicalRecommendationDocumentSupport.section(source, "5. Diagnóstico de salinidade/sodicidade"),
                NOT_CALCULATED);

        report.append("## Diagnóstico geral\n\n");
        TechnicalRecommendationDocumentSupport.appendBullet(report, "Textura", recommendation.getPlot() != null ? String.valueOf(recommendation.getPlot().getSoilTexture()) : null);
        report.append("- Acidez/calagem: consolidada na seção de calagem quando calculada.\n");
        report.append("- Salinidade/sodicidade: consolidada na seção de sais quando calculada.\n");
        report.append("- Fertilidade alta/média/baixa: usar as interpretações por atributo do diagnóstico químico; o backend não gerou uma classe global única persistida.\n\n");

        report.append("## Recomendação de calagem e gessagem\n\n");
        appendWithoutHeading(report, source, "7. Calagem", NOT_CALCULATED);
        report.append("- Alerta PRNT comercial: corrigir a dose de calcário conforme o PRNT do produto comercial utilizado.\n\n");
        appendWithoutHeading(report, source, "8. Gessagem", NOT_CALCULATED);

        TechnicalRecommendationDocumentSupport.appendSourceSectionOrMessage(
                report,
                "Recomendação de adubação orgânica",
                TechnicalRecommendationDocumentSupport.subsection(source, "Fontes orgânicas, organominerais e micronutrientes"),
                "Não aplicável com os dados disponíveis.");

        report.append("## Recomendação de micronutrientes\n\n");
        report.append("- Boro: ").append(NOT_CALCULATED).append("\n");
        report.append("- Cobre: ").append(NOT_CALCULATED).append("\n");
        report.append("- Ferro: ").append(NOT_CALCULATED).append("\n");
        report.append("- Manganês: ").append(NOT_CALCULATED).append("\n");
        report.append("- Zinco: ").append(NOT_CALCULATED).append("\n");
        report.append("- Adubos associados: somente os listados na tabela de fontes orgânicas/organominerais/micronutrientes acima, quando calculados.\n\n");

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

        report.append("## Observações\n\n");
        appendWithoutHeading(report, source, "14. Limitações e alertas", "Nenhuma observação adicional foi persistida.");
        report.append("- Conversões g/m linear e g/cova: ").append(NOT_CALCULATED).append("\n");
        report.append("- Dados institucionais não modelados no backend foram mantidos como ").append(NOT_INFORMED).append("\n");
        return report.toString();
    }

    private void appendWithoutHeading(StringBuilder report, String source, String heading, String fallback) {
        String section = TechnicalRecommendationDocumentSupport.stripHeading(TechnicalRecommendationDocumentSupport.section(source, heading));
        report.append(section.isBlank() ? fallback : section).append("\n\n");
    }
}
