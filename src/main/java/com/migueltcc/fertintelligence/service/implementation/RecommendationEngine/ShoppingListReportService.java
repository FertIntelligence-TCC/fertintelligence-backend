package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationCoverageFormulatedFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationMicronutrientFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationPlantingFormulatedFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.repository.DirectRecommendationCoverageFormulatedFertilizerLineRepository;
import com.migueltcc.fertintelligence.repository.DirectRecommendationMicronutrientFertilizerLineRepository;
import com.migueltcc.fertintelligence.repository.DirectRecommendationPlantingFormulatedFertilizerLineRepository;
import com.migueltcc.fertintelligence.repository.DirectRecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ShoppingListReportService {

    private static final java.util.regex.Pattern SODICITY_TOTAL = java.util.regex.Pattern.compile(
            "\\|\\s*Dose total estimada de gesso 0[–-]40 cm\\s*\\|\\s*([0-9.,]+)\\s*\\|\\s*kg/ha\\s*\\|",
            java.util.regex.Pattern.CASE_INSENSITIVE);

    private final DirectRecommendationRepository directRecommendationRepository;
    private final DirectRecommendationMicronutrientFertilizerLineRepository micronutrientFertilizerLineRepository;
    private final DirectRecommendationPlantingFormulatedFertilizerLineRepository plantingFormulatedFertilizerLineRepository;
    private final DirectRecommendationCoverageFormulatedFertilizerLineRepository coverageFormulatedFertilizerLineRepository;
    private final DirectRecommendationFertilizerResolver fertilizerResolver;
    private final ShoppingInputCostService shoppingInputCostService;

    public String build(RecommendationModel recommendation) {
        AreaResolution areaResolution = resolveArea(recommendation);
        Double area = areaResolution.area();
        List<TechnicalRecommendationDocumentSupport.ShoppingItem> items =
                TechnicalRecommendationDocumentSupport.collectShoppingItems(
                        recommendation,
                        micronutrientFertilizerLines(recommendation),
                        plantingFormulatedFertilizerLines(recommendation),
                        coverageFormulatedFertilizerLines(recommendation),
                        fertilizerResolver);

        StringBuilder report = new StringBuilder();
        TechnicalRecommendationDocumentSupport.appendStyle(report);
        TechnicalRecommendationDocumentSupport.appendInstitutionalHeader(report);
        report.append("Lista de insumos para a área cultivada (")
                .append(TechnicalRecommendationDocumentSupport.formatArea(area)).append(")\n\n");
        TechnicalRecommendationDocumentSupport.appendIdentification(
                report,
                recommendation,
                area,
                recommendation != null ? recommendation.getCropPlantingDate() : null);
        report.append("- Área usada para totalização: ").append(TechnicalRecommendationDocumentSupport.formatArea(area)).append("\n\n");

        if (items.isEmpty()) {
            report.append("Aviso técnico: lista de compras não gerou itens com dose operacional persistida.\n\n");
        } else {
            appendBlock(report, "BLOCO 1 - Correção da Acidez",
                    itemsInSections(items, TechnicalRecommendationDocumentSupport.SECTION_ACIDITY_CORRECTION), area, recommendation);
            appendBlock(report, "BLOCO 2 - Adubação Corretiva",
                    itemsInSections(items, TechnicalRecommendationDocumentSupport.SECTION_CORRECTIVE_FERTILIZATION), area, recommendation);
            appendBlock(report, "BLOCO 3 - Adubação de plantio e cobertura",
                    itemsInSections(items,
                            TechnicalRecommendationDocumentSupport.SECTION_PLANTING_OPTION_1,
                            TechnicalRecommendationDocumentSupport.SECTION_PLANTING_OPTION_2,
                            TechnicalRecommendationDocumentSupport.SECTION_COVERAGE_OPTION_1,
                            TechnicalRecommendationDocumentSupport.SECTION_COVERAGE_OPTION_2), area, recommendation);
        }
        appendSodicityGypsum(report, recommendation, area);
        appendFoliarAlternatives(report, recommendation, area);

        report.append("Observações\n\n");
        report.append("- A lista consolida insumos e doses do laudo técnico persistido e das linhas calculadas da Recomendação Direta.\n");
        report.append("- ").append(areaResolution.observation()).append("\n");
        if (!hasValidPlantingDate(recommendation)) {
            report.append("- Data de plantio da cultura usada na recomendação indisponível; campo omitível no frontend.\n");
        }
        report.append("- Itens sem dose em kg/ha não são convertidos para compra para evitar conversões não suportadas.\n");
        report.append("- Em blocos com Opção 1 e Opção 2, aceitar uma opção descarta somente a outra opção do mesmo bloco.\n");
        report.append("- Os valores representam apenas a estimativa dos custos dos insumos recomendados, sem incluir transporte, armazenamento ou aplicação.\n");
        report.append("- Custos são totalizados separadamente por opção; alternativas mutuamente exclusivas não são somadas.\n");
        return report.toString();
    }

    private void appendSodicityGypsum(StringBuilder report, RecommendationModel recommendation, Double area) {
        Double dose = sodicityGypsumDose(recommendation != null ? recommendation.getTechnicalReport() : null);
        if (dose == null || dose <= 0d) return;
        ShoppingInputCostService.CostEstimate cost =
                shoppingInputCostService.estimate(recommendation, "Gesso agrícola", dose, area);
        report.append("Grupo: Recuperação de sodicidade/excesso de Na (dose separada; não somada à gessagem convencional nem ao S)\n\n");
        report.append("| Fonte | Finalidade | Dose de aplicação (kg/ha) | Área | Quantidade total teórica | Unidade comercial | Custo estimado (R$/ha) | Custo total estimado |\n");
        report.append("|---|---|---:|---:|---:|---|---:|---:|\n");
        report.append("| Gesso agrícola | Recuperação de sodicidade/excesso de Na | ")
                .append(TechnicalRecommendationDocumentSupport.formatKgHa(dose))
                .append(" | ").append(TechnicalRecommendationDocumentSupport.formatArea(area))
                .append(" | ").append(TechnicalRecommendationDocumentSupport.formatTotal(dose, area))
                .append(" | ").append(formatCommercialPrice(cost))
                .append(" | ").append(formatMoney(cost.estimatedCostPerHa()))
                .append(" | ").append(formatMoney(cost.estimatedTotalCost()))
                .append(" |\n\n");
    }

    static Double sodicityGypsumDose(String report) {
        if (report == null) return null;
        java.util.regex.Matcher matcher = SODICITY_TOTAL.matcher(report);
        if (!matcher.find()) return null;
        try {
            String value = matcher.group(1);
            return Double.valueOf(value.contains(",") ? value.replace(".", "").replace(',', '.') : value);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private void appendFoliarAlternatives(StringBuilder report, RecommendationModel recommendation, Double area) {
        List<FoliarShoppingAlternative> alternatives =
                foliarAlternatives(recommendation != null ? recommendation.getTechnicalReport() : null);
        if (alternatives.isEmpty()) return;
        report.append("BLOCO 4 - Adubação foliar de micronutrientes (alternativas mutuamente exclusivas)\n\n");
        Map<String, List<FoliarShoppingAlternative>> groups = new LinkedHashMap<>();
        for (FoliarShoppingAlternative alternative : alternatives) {
            groups.computeIfAbsent(alternative.micronutrient(), ignored -> new ArrayList<>()).add(alternative);
        }
        for (Map.Entry<String, List<FoliarShoppingAlternative>> group : groups.entrySet()) {
            report.append("Grupo: ").append(group.getKey()).append("\n\n");
            report.append("| Produto | Tipo de fonte | Estado | Dose (kg/ha) | Área | Quantidade total | CUMIC (R$/ha) | Custo total da alternativa |\n");
            report.append("|---|---|---|---:|---:|---:|---:|---:|\n");
            for (FoliarShoppingAlternative alternative : group.getValue()) {
                String state = alternative.state();
                Double total = alternative.costPerHa() != null && area != null ? alternative.costPerHa() * area : null;
                report.append("| ").append(alternative.product())
                        .append(" | ").append(alternative.sourceType())
                        .append(" | ").append(state)
                        .append(" | ").append(formatNumber(alternative.productDoseKgHa()))
                        .append(" | ").append(TechnicalRecommendationDocumentSupport.formatArea(area))
                        .append(" | ").append(TechnicalRecommendationDocumentSupport.formatTotal(alternative.productDoseKgHa(), area))
                        .append(" | ").append(formatMoney(alternative.costPerHa()))
                        .append(" | ").append(formatMoney(total))
                        .append(" |\n");
            }
            report.append("\nAlternativa escolhida: ")
                    .append(group.getValue().stream().filter(value -> "SELECTED".equals(value.state()))
                            .map(FoliarShoppingAlternative::product).findFirst().orElse("Aguardando preços para decisão"))
                    .append(". As alternativas deste grupo não são somadas ao mesmo total.\n\n");
        }
    }

    static List<FoliarShoppingAlternative> foliarAlternatives(String source) {
        if (source == null) return List.of();
        int start = source.indexOf("Adubação foliar de micronutrientes");
        if (start < 0) return List.of();
        String table = source.substring(start);
        List<FoliarShoppingAlternative> alternatives = new ArrayList<>();
        for (List<String> row : TechnicalRecommendationDocumentSupport.tableRows(table)) {
            if (row.size() < 11) continue;
            Double dose = TechnicalRecommendationDocumentSupport.extractKgHa(row.get(5)).orElse(null);
            if (dose == null || dose <= 0d) continue;
            String decision = row.get(10);
            String state = decision.startsWith("SELECTED") ? "SELECTED"
                    : decision.startsWith("NOT_SELECTED") ? "NOT_SELECTED" : "UNDETERMINED";
            alternatives.add(new FoliarShoppingAlternative(row.get(0), row.get(1), row.get(2), dose,
                    parseMoney(row.get(9)), state));
        }
        return alternatives;
    }

    private static Double parseMoney(String value) {
        if (value == null || value.contains("Não informado")) return null;
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("([0-9]+(?:[.,][0-9]+)?)").matcher(value);
        return matcher.find() ? Double.valueOf(matcher.group(1).replace(',', '.')) : null;
    }

    record FoliarShoppingAlternative(String micronutrient, String sourceType, String product,
                                     Double productDoseKgHa, Double costPerHa, String state) {}

    private AreaResolution resolveArea(RecommendationModel recommendation) {
        if (recommendation != null && isPositive(recommendation.getCropUsedAreaInThePlot())) {
            return new AreaResolution(
                    recommendation.getCropUsedAreaInThePlot(),
                    "Área de totalização obtida da área da cultura considerada na recomendação.");
        }
        return new AreaResolution(
                null,
                "Área da cultura usada na recomendação e área do talhão indisponíveis ou inválidas; o total não foi inferido.");
    }

    private boolean isPositive(Double value) {
        return value != null && value > 0;
    }

    private boolean hasValidPlantingDate(RecommendationModel recommendation) {
        if (recommendation == null || recommendation.getCropPlantingDate() == null) {
            return false;
        }
        return recommendation.getCropPlantingDate().getDay() > 0
                && recommendation.getCropPlantingDate().getMonth() > 0
                && recommendation.getCropPlantingDate().getYear() > 0;
    }

    private List<TechnicalRecommendationDocumentSupport.ShoppingItem> itemsInSections(
            List<TechnicalRecommendationDocumentSupport.ShoppingItem> items,
            String... sections) {
        List<TechnicalRecommendationDocumentSupport.ShoppingItem> selected = new ArrayList<>();
        for (TechnicalRecommendationDocumentSupport.ShoppingItem item : items) {
            for (String section : sections) {
                if (section.equals(item.getSection())) {
                    selected.add(item);
                    break;
                }
            }
        }
        return selected;
    }

    private void appendBlock(StringBuilder report,
                             String title,
                             List<TechnicalRecommendationDocumentSupport.ShoppingItem> items,
                             Double area,
                             RecommendationModel recommendation) {
        report.append(title).append("\n\n");
        if (items.isEmpty()) {
            report.append("Aviso técnico: nenhum item com dose operacional foi classificado para este bloco.\n\n");
            return;
        }
        for (Map.Entry<String, List<TechnicalRecommendationDocumentSupport.ShoppingItem>> group : groupedByOption(items).entrySet()) {
            if (!group.getKey().isBlank()) {
                report.append("Grupo: ").append(group.getKey()).append("\n\n");
            }
            appendItemsTable(report, group.getValue(), area, recommendation);
        }
    }

    private Map<String, List<TechnicalRecommendationDocumentSupport.ShoppingItem>> groupedByOption(
            List<TechnicalRecommendationDocumentSupport.ShoppingItem> items) {
        Map<String, List<TechnicalRecommendationDocumentSupport.ShoppingItem>> grouped = new LinkedHashMap<>();
        for (TechnicalRecommendationDocumentSupport.ShoppingItem item : items) {
            String key = optionLabel(item.getOption());
            grouped.computeIfAbsent(key, ignored -> new ArrayList<>()).add(item);
        }
        return grouped;
    }

    private String optionLabel(String option) {
        String key = cell(option);
        return switch (key) {
            case "correcao_acidez_recomendacao_unica" -> "Recomendação única";
            case "adubacao_corretiva_opcao_1_formulados",
                    "plantio_opcao_1_formulados",
                    "cobertura_opcao_1_formulados" -> "Opção 1 - formulados";
            case "adubacao_corretiva_opcao_2_adubos_simples",
                    "plantio_opcao_2_adubos_simples",
                    "cobertura_opcao_2_adubos_simples" -> "Opção 2 - adubos simples";
            default -> key;
        };
    }

    private void appendItemsTable(StringBuilder report,
                                  List<TechnicalRecommendationDocumentSupport.ShoppingItem> items,
                                  Double area,
                                  RecommendationModel recommendation) {
        report.append("| Fonte | Dose de aplicação (DA, kg/ha) | Preço da unidade comercial (PUC) | Custo estimado do insumo (CEI, R$/ha) | Quantidade total teórica (QTT, kg) | Quantidade total comercial (QTC) | Custo total estimado (CTE, R$) |\n");
        report.append("|---|---:|---:|---:|---:|---:|---:|\n");
        double total = 0d;
        int missing = 0;
        int priced = 0;
        for (TechnicalRecommendationDocumentSupport.ShoppingItem item : items) {
            ShoppingInputCostService.CostEstimate cost = shoppingInputCostService.estimate(recommendation, item.getName(), item.getKgHa(), area);
            if (cost.priced() && cost.estimatedTotalCost() != null) {
                total += cost.estimatedTotalCost();
                priced++;
            } else {
                missing++;
            }
            report.append("| ").append(cell(item.getName()))
                    .append(" | ").append(TechnicalRecommendationDocumentSupport.formatKgHa(item.getKgHa()))
                    .append(" | ").append(formatCommercialPrice(cost))
                    .append(" | ").append(formatMoney(cost.estimatedCostPerHa()))
                    .append(" | ").append(TechnicalRecommendationDocumentSupport.formatTotal(item.getKgHa(), area))
                    .append(" | ").append(formatCommercialQuantity(cost))
                    .append(" | ").append(formatMoney(cost.estimatedTotalCost()))
                    .append(" |\n");
        }
        report.append("\nCusto total estimado dos insumos recomendados nesta opção: ")
                .append(priced == 0 ? "Não calculado" : formatMoney(total)).append(".\n\n");
        if (missing > 0) report.append("Estimativa parcial: ").append(missing).append(" insumo(s) não possuem preço comercial cadastrado.\n\n");
    }

    private String formatCommercialPrice(ShoppingInputCostService.CostEstimate cost) {
        if (cost == null || !cost.priced()) return "Não informado";
        String mass = cost.commercialUnitMassKg() == 1000d ? "" : " " + cost.commercialUnitMassKg().intValue() + " kg";
        return formatMoney(cost.commercialUnitPrice().doubleValue()) + " / " + cost.commercialUnitSymbol() + mass;
    }

    private String formatCommercialQuantity(ShoppingInputCostService.CostEstimate cost) {
        if (cost == null || !cost.priced() || cost.theoreticalCommercialQuantity() == null) return "Não calculado";
        return formatNumber(cost.theoreticalCommercialQuantity()) + " " + cost.commercialUnitSymbol();
    }

    private String formatMoney(Double value) { return value == null ? "Não calculado" : "R$ " + formatNumber(value); }
    private String formatNumber(Double value) { return String.format(java.util.Locale.forLanguageTag("pt-BR"), "%.2f", value); }

    private String cell(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.replace("|", "/").replace("\n", " ").trim();
    }

    private List<DirectRecommendationMicronutrientFertilizerLineModel> micronutrientFertilizerLines(
            RecommendationModel recommendation) {
        return directRecommendation(recommendation)
                .map(micronutrientFertilizerLineRepository::findAllByDirectRecommendationOrderByIdAsc)
                .orElseGet(List::of);
    }

    private List<DirectRecommendationPlantingFormulatedFertilizerLineModel> plantingFormulatedFertilizerLines(
            RecommendationModel recommendation) {
        return directRecommendation(recommendation)
                .map(plantingFormulatedFertilizerLineRepository::findAllByDirectRecommendationOrderByDoseKgHaDescIdAsc)
                .orElseGet(List::of);
    }

    private List<DirectRecommendationCoverageFormulatedFertilizerLineModel> coverageFormulatedFertilizerLines(
            RecommendationModel recommendation) {
        return directRecommendation(recommendation)
                .map(coverageFormulatedFertilizerLineRepository::findAllByDirectRecommendationOrderByCoverageOrderAscDoseKgHaDescIdAsc)
                .orElseGet(List::of);
    }

    private java.util.Optional<DirectRecommendationModel> directRecommendation(RecommendationModel recommendation) {
        if (recommendation == null || recommendation.getId() == null) {
            return java.util.Optional.empty();
        }
        return directRecommendationRepository.findByRecommendation(recommendation);
    }

    private record AreaResolution(Double area, String observation) {
    }
}
