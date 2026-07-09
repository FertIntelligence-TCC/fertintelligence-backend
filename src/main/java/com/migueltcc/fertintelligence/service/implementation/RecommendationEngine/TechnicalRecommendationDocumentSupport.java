package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationCoverageFormulatedFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationMicronutrientFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationPlantingFormulatedFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class TechnicalRecommendationDocumentSupport {
    static final String NOT_INFORMED = "Não informado.";
    static final String NOT_CALCULATED = "Não calculado por falta de dados.";
    static final String NOT_APPLICABLE = "Não aplicável com os dados disponíveis.";
    static final String LINEAR_CONVERSION_UNAVAILABLE = "Não calculado por falta de dados.";
    static final String STYLE_METADATA = "<!-- formato: markdown; fonte: Aptos; tamanho: 10 -->";
    static final String DEFAULT_MICRONUTRIENT_TECHNICAL_OBSERVATION = "Misturar com os demais adubos minerais no plantio.";
    private static final String LINEAR_METER_MODE = "LINEAR_METER";
    private static final String PIT_MODE = "PIT";
    private static final String LINEAR_METER_LABEL = "g/m linear";
    private static final String PIT_LABEL = "g/cova";
    private static final String FLAG_REQUIRED = "obrigatorio";
    private static final String FLAG_OPTIONAL = "opcional";
    private static final String FLAG_ALTERNATIVE = "alternativa";
    private static final String FLAG_COMPLEMENT = "complemento";
    private static final String OPTION_ACIDITY_CORRECTION = "correcao_acidez_recomendacao_unica";
    private static final String OPTION_CORRECTIVE_FORMULATED = "adubacao_corretiva_opcao_1_formulados";
    private static final String OPTION_CORRECTIVE_SIMPLE_SOURCES = "adubacao_corretiva_opcao_2_adubos_simples";
    private static final String OPTION_PLANTING_FORMULATED = "plantio_opcao_1_formulados";
    private static final String OPTION_PLANTING_SIMPLE_SOURCES = "plantio_opcao_2_adubos_simples";
    private static final String OPTION_COVERAGE_FORMULATED = "cobertura_opcao_1_formulados";
    private static final String OPTION_COVERAGE_SIMPLE_SOURCES = "cobertura_opcao_2_adubos_simples";
    static final String SECTION_ACIDITY_CORRECTION = "bloco_1_correcao_acidez";
    static final String SECTION_CORRECTIVE_FERTILIZATION = "bloco_2_adubacao_corretiva";
    static final String SECTION_PLANTING_OPTION_1 = "bloco_3_plantio_opcao_1";
    static final String SECTION_PLANTING_OPTION_2 = "bloco_3_plantio_opcao_2";
    static final String SECTION_COVERAGE_OPTION_1 = "bloco_4_cobertura_opcao_1";
    static final String SECTION_COVERAGE_OPTION_2 = "bloco_4_cobertura_opcao_2";

    private static final DateTimeFormatter BR_DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Pattern QUANTITY_KG_HA = Pattern.compile("(-?\\d+(?:[\\.,]\\d+)?)\\s*kg\\s*/\\s*ha", Pattern.CASE_INSENSITIVE);
    private static final List<String> TOP_LEVEL_HEADINGS = List.of(
            "Laudo Técnico de Recomendação Agrícola",
            "1. Identificação",
            "2. Dados utilizados",
            "3. Diagnóstico químico",
            "4. Diagnóstico físico",
            "5. Diagnóstico de salinidade/sodicidade",
            "6. Diagnóstico foliar",
            "7. Calagem",
            "8. Gessagem",
            "9. Adubação corretiva",
            "10. Adubação de plantio",
            "11. Adubação de cobertura",
            "12. Balanço nutricional",
            "13. Fertilizantes recomendados",
            "14. Limitações e alertas",
            "15. Memória de cálculo",
            "16. Encerramento"
    );
    private static final List<String> SUBSECTION_HEADINGS = List.of(
            "Entradas de calagem",
            "Entradas de gessagem"
    );

    private TechnicalRecommendationDocumentSupport() {
    }

    static void appendStyle(StringBuilder report) {
        report.append(STYLE_METADATA).append("\n\n");
    }

    static void appendInstitutionalHeader(StringBuilder report) {
        report.append("**FertIntelligence**\n\n");
        report.append("- Endereço: ").append(NOT_INFORMED).append("\n");
        report.append("- Telefone/WhatsApp: ").append(NOT_INFORMED).append("\n");
        report.append("- E-mail: ").append(NOT_INFORMED).append("\n");
        report.append("- CEO: ").append(NOT_INFORMED).append("\n\n");
    }

    static void appendIdentification(StringBuilder report, RecommendationModel recommendation) {
        PlotModel plot = recommendation != null ? recommendation.getPlot() : null;
        appendIdentification(report, recommendation, plot != null ? plot.getArea() : null, null);
    }

    static void appendIdentification(StringBuilder report,
                                     RecommendationModel recommendation,
                                     Double evaluatedArea,
                                     Date plantingDate) {
        PropertyModel property = recommendation != null ? recommendation.getProperty() : null;
        PlotModel plot = recommendation != null ? recommendation.getPlot() : null;
        UserModel creator = recommendation != null ? recommendation.getCreator() : null;
        report.append("Identificação\n\n");
        report.append("- Cliente/Produtor: ").append(safe(property != null && property.getOwner() != null ? property.getOwner().getName() : null)).append("\n");
        report.append("- Propriedade: ").append(safe(property != null ? property.getNome() : null)).append("\n");
        report.append("- Município/UF: ").append(NOT_INFORMED).append("\n");
        report.append("- Talhão: ").append(safe(plot != null ? plot.getIdentification() : null)).append("\n");
        report.append("- Área avaliada: ").append(formatArea(evaluatedArea)).append("\n");
        report.append("- Cultura prevista: ").append(safe(recommendation != null ? recommendation.getCropName() : null)).append("\n");
        report.append("- Safra/Safrinha: ").append(recommendation == null || recommendation.getCropYear() == null ? NOT_INFORMED : recommendation.getCropYear()).append("\n");
        report.append("- Data de plantio: ").append(formatCropDate(plantingDate)).append("\n");
        report.append("- Responsável técnico: ").append(safe(creator != null ? creator.getName() : null)).append("\n");
        report.append("- Registro profissional: ").append(NOT_INFORMED).append("\n");
        report.append("- Data de emissão: ").append(formatDate(recommendation != null ? recommendation.getCreatedAt() : null)).append("\n\n");
    }

    static String section(String markdown, String heading) {
        return headingBlock(markdown, heading, false);
    }

    static String subsection(String markdown, String heading) {
        return headingBlock(markdown, heading, true);
    }

    static void appendSourceSectionOrMessage(StringBuilder report, String title, String sourceSection, String missingMessage) {
        report.append(title).append("\n\n");
        String content = cleanSectionForFinalReport(stripHeading(sourceSection));
        report.append(content.isBlank() ? missingMessage : content).append("\n\n");
    }

    static void appendBullet(StringBuilder report, String label, String value) {
        report.append("- ").append(label).append(": ").append(safe(value)).append("\n");
    }

    static List<List<String>> tableRows(String section) {
        List<List<String>> rows = new ArrayList<>();
        if (section == null || section.isBlank()) return rows;
        String[] lines = section.split("\\R");
        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.startsWith("|") || isSeparatorRow(trimmed)) continue;
            List<String> cells = new ArrayList<>();
            String body = trimmed.substring(1, trimmed.endsWith("|") ? trimmed.length() - 1 : trimmed.length());
            for (String cell : body.split("\\|", -1)) {
                cells.add(cell.trim());
            }
            rows.add(cells);
        }
        if (!rows.isEmpty()) rows.remove(0);
        return rows;
    }

    static List<ShoppingItem> collectShoppingItems(RecommendationModel recommendation) {
        return collectShoppingItems(recommendation, List.of(), List.of(), List.of(), null);
    }

    static List<ShoppingItem> collectShoppingItems(
            RecommendationModel recommendation,
            List<DirectRecommendationMicronutrientFertilizerLineModel> micronutrientFertilizerLines,
            List<DirectRecommendationPlantingFormulatedFertilizerLineModel> plantingFormulatedFertilizerLines,
            List<DirectRecommendationCoverageFormulatedFertilizerLineModel> coverageFormulatedFertilizerLines,
            DirectRecommendationFertilizerResolver fertilizerResolver) {
        Map<String, ShoppingItem> items = new LinkedHashMap<>();
        String source = recommendation.getTechnicalReport();
        addCorrectiveItem(items, source, "Calcário", section(source, "7. Calagem"), SECTION_ACIDITY_CORRECTION, OPTION_ACIDITY_CORRECTION, FLAG_REQUIRED,
                "Necessidade de calagem ajustada",
                "Dose efetiva registrada pelo cálculo",
                "Dose corrigida por PRNT");
        String gypsumSection = section(source, "8. Gessagem");
        addPositiveCorrectiveItem(items, "Gesso agrícola", gypsumSection, SECTION_ACIDITY_CORRECTION, OPTION_ACIDITY_CORRECTION, FLAG_OPTIONAL,
                "Dose comercial", "Dose de gesso");
        addGypsumSulfurAlternativeItems(items, gypsumSection);
        addSoilCorrectiveFertilizationItems(items, section(source, "9. Adubação corretiva"));
        addDirectMicronutrientItems(items, micronutrientFertilizerLines, fertilizerResolver);
        addDirectPlantingFormulatedItems(items, plantingFormulatedFertilizerLines, fertilizerResolver);
        addDirectCoverageFormulatedItems(items, coverageFormulatedFertilizerLines, fertilizerResolver);
        addAlternativeItems(items, subsection(source, "Fontes orgânicas, organominerais e micronutrientes"));
        addFertilizationItems(items, section(source, "10. Adubação de plantio"));
        addFertilizationItems(items, section(source, "11. Adubação de cobertura"));
        annotateOpportunityCostDecisions(items, source);
        return new ArrayList<>(items.values());
    }

    static Optional<Double> extractKgHa(String value) {
        if (value == null) return Optional.empty();
        Matcher matcher = QUANTITY_KG_HA.matcher(value);
        if (!matcher.find()) return Optional.empty();
        return parseDecimal(matcher.group(1));
    }

    static String formatKgHa(Double value) {
        return value == null ? NOT_CALCULATED : String.format(Locale.US, "%.2f kg/ha", value);
    }

    static String formatTotal(Double kgHa, Double areaHa) {
        if (kgHa == null || areaHa == null || areaHa <= 0) return NOT_CALCULATED;
        return String.format(Locale.US, "%.2f kg", kgHa * areaHa);
    }

    static String safe(Object value) {
        if (value == null) return NOT_INFORMED;
        String asText = String.valueOf(value).trim();
        return asText.isBlank() ? NOT_INFORMED : asText;
    }

    static String safeCell(Object value) {
        return safe(value).replace("|", "/").replace("\n", " ");
    }

    static String micronutrientTechnicalObservationCell(String value) {
        String observation = missingMicronutrientTechnicalObservation(value)
                ? DEFAULT_MICRONUTRIENT_TECHNICAL_OBSERVATION
                : value.trim();
        return safeCell(observation);
    }

    static String formatDate(LocalDateTime date) {
        return (date == null ? LocalDateTime.now() : date).format(BR_DATE_TIME);
    }

    static String formatArea(Double area) {
        return area == null ? NOT_INFORMED : String.format(Locale.US, "%.2f ha", area);
    }

    static String formatCropDate(Date date) {
        if (date == null || date.getDay() <= 0 || date.getMonth() <= 0 || date.getYear() <= 0) {
            return NOT_INFORMED;
        }
        return String.format(Locale.US, "%02d/%02d/%04d", date.getDay(), date.getMonth(), date.getYear());
    }

    static String stripHeading(String section) {
        if (section == null || section.isBlank()) return "";
        String[] lines = section.split("\\R", 2);
        return lines.length == 1 ? "" : lines[1].trim();
    }

    static boolean looksUnavailable(String value) {
        if (value == null || value.isBlank()) return true;
        String normalized = normalize(value);
        return normalized.contains("nao calculad")
                || normalized.contains("nao selecionad")
                || normalized.contains("nao informad")
                || normalized.contains("nao avaliad");
    }

    static boolean hasPositiveKgHa(Double value) {
        return value != null && Double.isFinite(value) && value > 0d;
    }

    static boolean hasPositiveKgHa(String value) {
        return extractKgHa(value)
                .filter(TechnicalRecommendationDocumentSupport::hasPositiveKgHa)
                .isPresent();
    }

    static String cleanSectionForFinalReport(String content) {
        if (content == null || content.isBlank()) {
            return "";
        }
        StringBuilder cleaned = new StringBuilder();
        for (String line : content.split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.isBlank()) {
                appendCleanedLine(cleaned, "");
                continue;
            }
            if (isDiscardableUnavailableLine(trimmed) || isZeroDoseLine(trimmed)) {
                continue;
            }
            appendCleanedLine(cleaned, line);
        }
        return collapseBlankLines(cleaned.toString().trim());
    }

    private static boolean missingMicronutrientTechnicalObservation(String value) {
        if (value == null || value.isBlank()) return true;
        String normalized = normalize(value).replace(".", "").trim();
        return "nao informado".equals(normalized);
    }

    private static void addCorrectiveItem(Map<String, ShoppingItem> items,
                                          String source,
                                          String itemName,
                                          String section,
                                          String sectionKey,
                                          String option,
                                          String flag,
                                          String... labels) {
        if (section.isBlank()) return;
        for (String label : labels) {
            for (String line : section.split("\\R")) {
                if (!normalize(line).contains(normalize(label))) continue;
                Optional<Double> kgHa = extractKgHa(line);
                if (kgHa.isPresent()) {
                    merge(items, itemName, kgHa.get(), null, sectionKey, option, flag, null, null);
                    return;
                }
            }
        }
    }

    private static void addPositiveCorrectiveItem(Map<String, ShoppingItem> items,
                                                  String itemName,
                                                  String section,
                                                  String sectionKey,
                                                  String option,
                                                  String flag,
                                                  String... labels) {
        if (section == null || section.isBlank()) return;
        for (String label : labels) {
            for (String line : section.split("\\R")) {
                if (!normalize(line).contains(normalize(label))) continue;
                Optional<Double> kgHa = extractKgHa(line);
                if (kgHa.isPresent() && kgHa.get() > 0d) {
                    merge(items, itemName, kgHa.get(), null, sectionKey, option, flag, null, null);
                    return;
                }
            }
        }
    }

    private static void addGypsumSulfurAlternativeItems(Map<String, ShoppingItem> items, String section) {
        if (section == null || section.isBlank()) return;
        addGypsumAlternativeItem(items, section, "Sulfato de amônio 22% S", "Alternativa de S para gessagem em dose baixa",
                "Alternativa com sulfato de amônio 22% S");
        addGypsumAlternativeItem(items, section, "Superfosfato simples 11% S", "Alternativa de S para gessagem em dose baixa",
                "Alternativa com superfosfato simples 11% S");
    }

    private static void addGypsumAlternativeItem(Map<String, ShoppingItem> items,
                                                 String section,
                                                 String itemName,
                                                 String typeGroup,
                                                 String label) {
        for (String line : section.split("\\R")) {
            if (!normalize(line).contains(normalize(label))) continue;
            extractKgHa(line).ifPresent(kgHa -> merge(items, itemName, kgHa, typeGroup, SECTION_PLANTING_OPTION_1,
                    OPTION_PLANTING_FORMULATED, FLAG_COMPLEMENT, "Plantio - suprimento de S", null));
            return;
        }
    }

    private static void addFertilizationItems(Map<String, ShoppingItem> items, String section) {
        for (List<String> row : tableRows(section)) {
            if (row.size() < 4) continue;
            String phase = row.get(0);
            String nutrients = row.get(1);
            String fertilizer = row.get(2);
            String quantity = row.get(3);
            String application = row.size() > 4 ? row.get(4) : null;
            if (looksUnavailable(fertilizer) || looksUnavailable(quantity)) continue;
            String itemName = removeId(fertilizer);
            String sectionKey = normalize(phase).contains("cobertura") ? SECTION_COVERAGE_OPTION_2 : SECTION_PLANTING_OPTION_2;
            String option = normalize(phase).contains("cobertura") ? OPTION_COVERAGE_SIMPLE_SOURCES : OPTION_PLANTING_SIMPLE_SOURCES;
            if (hasOperationalItemInSamePhase(items, itemName, phase)) continue;
            extractKgHa(quantity)
                    .filter(TechnicalRecommendationDocumentSupport::hasPositiveKgHa)
                    .ifPresent(kgHa -> merge(items, itemName, kgHa, shoppingNutrientGroup(nutrients, application), sectionKey, option, FLAG_ALTERNATIVE, phase, null));
        }
    }

    private static void addSoilCorrectiveFertilizationItems(Map<String, ShoppingItem> items, String section) {
        for (List<String> row : tableRows(section)) {
            if (row.size() < 4) continue;
            String attribute = row.get(0);
            String sourceName = row.get(2);
            String dose = row.get(3);
            if (looksUnavailable(sourceName) || looksUnavailable(dose)) continue;
            if (isAutomaticFteComplement(attribute)) continue;
            String itemName = removeId(sourceName);
            extractKgHa(dose).ifPresent(kgHa -> {
                if (kgHa > 0d) {
                    String typeGroup = correctiveTypeGroup(attribute, itemName);
                    merge(items, itemName, kgHa, typeGroup, SECTION_CORRECTIVE_FERTILIZATION,
                            correctiveOption(typeGroup, attribute, itemName), correctiveItemFlag(attribute), attribute, null);
                }
            });
        }
    }

    private static void addAlternativeItems(Map<String, ShoppingItem> items, String section) {
        for (List<String> row : tableRows(section)) {
            if (row.size() < 5) continue;
            String sourceName = row.get(2);
            String dose = row.get(3);
            String unit = row.get(4);
            if (looksUnavailable(sourceName) || looksUnavailable(dose) || unit == null) continue;
            if (!normalize(unit).contains("kg/ha")) continue;
            String itemName = removeId(sourceName);
            if (hasOperationalItemInSamePhase(items, itemName, "Plantio")) continue;
            String typeGroup = normalize(row.get(0)).contains("micronutriente")
                    ? "Complementação de micronutrientes"
                    : row.get(0);
            parseDecimal(dose)
                    .filter(TechnicalRecommendationDocumentSupport::hasPositiveKgHa)
                    .ifPresent(kgHa -> merge(items, itemName, kgHa, typeGroup, SECTION_PLANTING_OPTION_1, OPTION_PLANTING_FORMULATED,
                            FLAG_COMPLEMENT, "Plantio", null));
        }
    }

    private static void addDirectMicronutrientItems(
            Map<String, ShoppingItem> items,
            List<DirectRecommendationMicronutrientFertilizerLineModel> lines,
            DirectRecommendationFertilizerResolver fertilizerResolver) {
        if (lines == null) return;
        for (DirectRecommendationMicronutrientFertilizerLineModel line : lines) {
            DirectRecommendationFertilizerResolver.SimpleMineralFertilizerData fertilizer =
                    fertilizerResolver == null ? DirectRecommendationFertilizerResolver.SimpleMineralFertilizerData.unresolved()
                            : fertilizerResolver.simple(line != null ? line.getFertilizerId() : null, line != null ? line.getMicronutrient() : null);
            if (line == null || looksUnavailable(fertilizer.name()) || !hasPositiveKgHa(line.getFertilizerDoseKgHa())) continue;
            merge(
                    items,
                    removeId(fertilizer.name()),
                    line.getFertilizerDoseKgHa(),
                    "Micronutriente" + (line.getMicronutrient() == null ? "" : " - " + line.getMicronutrient()),
                    SECTION_PLANTING_OPTION_1,
                    OPTION_PLANTING_FORMULATED,
                    FLAG_COMPLEMENT,
                    "Plantio",
                    localizedDose(line.getDoseUnitMode(), line.getDoseUnitLabel(), line.getGramsPerLinearMeter(), line.getGramsPerPit()));
        }
    }

    private static void addDirectPlantingFormulatedItems(
            Map<String, ShoppingItem> items,
            List<DirectRecommendationPlantingFormulatedFertilizerLineModel> lines,
            DirectRecommendationFertilizerResolver fertilizerResolver) {
        if (lines == null) return;
        for (DirectRecommendationPlantingFormulatedFertilizerLineModel line : lines) {
            DirectRecommendationFertilizerResolver.FormulatedMineralFertilizerData fertilizer =
                    fertilizerResolver == null ? DirectRecommendationFertilizerResolver.FormulatedMineralFertilizerData.unresolved()
                            : fertilizerResolver.formulated(line != null ? line.getFertilizerId() : null);
            if (line == null || looksUnavailable(fertilizer.name()) || !hasPositiveKgHa(line.getDoseKgHa())) continue;
            merge(
                    items,
                    removeId(fertilizer.name()),
                    line.getDoseKgHa(),
                    formattedFormulatedGroup(fertilizer.nitrogenPercent(), fertilizer.p2o5Percent(), fertilizer.k2oPercent()),
                    SECTION_PLANTING_OPTION_1,
                    OPTION_PLANTING_FORMULATED,
                    FLAG_ALTERNATIVE,
                    line.getPhase(),
                    localizedDose(line.getDoseUnitMode(), line.getDoseUnitLabel(), line.getGramsPerLinearMeter(), line.getGramsPerPit()));
        }
    }

    private static void addDirectCoverageFormulatedItems(
            Map<String, ShoppingItem> items,
            List<DirectRecommendationCoverageFormulatedFertilizerLineModel> lines,
            DirectRecommendationFertilizerResolver fertilizerResolver) {
        if (lines == null) return;
        for (DirectRecommendationCoverageFormulatedFertilizerLineModel line : lines) {
            DirectRecommendationFertilizerResolver.FormulatedMineralFertilizerData fertilizer =
                    fertilizerResolver == null ? DirectRecommendationFertilizerResolver.FormulatedMineralFertilizerData.unresolved()
                            : fertilizerResolver.formulated(line != null ? line.getFertilizerId() : null);
            if (line == null || looksUnavailable(fertilizer.name()) || !hasPositiveKgHa(line.getDoseKgHa())) continue;
            String phase = coveragePhase(line.getPhase(), line.getCoverageOrder());
            merge(
                    items,
                    removeId(fertilizer.name()),
                    line.getDoseKgHa(),
                    formattedFormulatedGroup(fertilizer.nitrogenPercent(), fertilizer.p2o5Percent(), fertilizer.k2oPercent()),
                    SECTION_COVERAGE_OPTION_1,
                    OPTION_COVERAGE_FORMULATED,
                    FLAG_ALTERNATIVE,
                    phase,
                    localizedDose(line.getDoseUnitMode(), line.getDoseUnitLabel(), line.getGramsPerLinearMeter(), line.getGramsPerPit()));
        }
    }

    private static String formattedFormulatedGroup(Double nitrogen, Double p2o5, Double k2o) {
        String formula = formatPercentForFormula(nitrogen) + "-" + formatPercentForFormula(p2o5) + "-" + formatPercentForFormula(k2o);
        return "Formulado NPK " + formula;
    }

    private static String correctiveTypeGroup(String attribute, String itemName) {
        String normalizedAttribute = normalize(attribute);
        String normalizedName = normalize(itemName);
        if (normalizedName.contains("superfosfato simples")) return "Superfosfato Simples";
        if (normalizedName.contains("cloreto de potassio")) return "Cloreto de Potássio";
        if (normalizedName.contains("fte br 12") || normalizedName.contains("fte br-12")) return "FTE BR-12";
        if (normalizedName.contains("fte br 24") || normalizedName.contains("fte br-24")) return "FTE BR-24";
        if (normalizedName.contains("fte")) return "FTE";
        if (normalizedAttribute.contains("formulado") || normalizedName.contains("npk")) return "Formulados";
        if (normalizedAttribute.contains("complemento corretivo")) return "Micronutriente simples em dose cheia";
        return "Adubação corretiva do solo";
    }

    private static String correctiveOption(String typeGroup, String attribute, String itemName) {
        String normalizedGroup = normalize(typeGroup);
        String normalizedAttribute = normalize(attribute);
        String normalizedName = normalize(itemName);
        if (normalizedGroup.contains("formulado")
                || normalizedGroup.contains("fte")
                || normalizedAttribute.contains("formulado")
                || normalizedName.contains("npk")
                || normalizedName.contains("fte")) {
            return OPTION_CORRECTIVE_FORMULATED;
        }
        return OPTION_CORRECTIVE_SIMPLE_SOURCES;
    }

    private static String correctiveItemFlag(String attribute) {
        return normalize(attribute).contains("complemento corretivo") ? FLAG_ALTERNATIVE : FLAG_OPTIONAL;
    }

    private static boolean isAutomaticFteComplement(String attribute) {
        String normalized = normalize(attribute);
        return normalized.contains("complemento apos") || normalized.contains("complemento após");
    }

    private static String shoppingNutrientGroup(String nutrients, String application) {
        List<String> parts = new ArrayList<>();
        if (nutrients != null && !nutrients.isBlank()) {
            parts.add("Nutrientes: " + nutrients);
        }
        String balance = extractBalance(application);
        if (balance != null) {
            parts.add("Saldos: " + balance);
        }
        return parts.isEmpty() ? null : String.join("; ", parts);
    }

    private static String extractBalance(String value) {
        if (value == null || value.isBlank()) return null;
        String normalized = normalize(value);
        int index = normalized.indexOf("saldo:");
        if (index < 0) return null;
        String original = value.substring(index + "saldo:".length()).trim();
        int end = original.indexOf(';');
        return end >= 0 ? original.substring(0, end).trim() : original;
    }

    private static String formatPercentForFormula(Double value) {
        if (value == null) return "?";
        if (Math.rint(value) == value) {
            return String.format(Locale.US, "%.0f", value);
        }
        return String.format(Locale.US, "%.2f", value);
    }

    private static String localizedDose(String mode, String label, Double gramsPerLinearMeter, Double gramsPerPit) {
        if (LINEAR_METER_MODE.equals(mode)) {
            return gramsPerLinearMeter == null ? null : String.format(Locale.US, "%.2f %s", gramsPerLinearMeter, defaultLabel(label, LINEAR_METER_LABEL));
        }
        if (PIT_MODE.equals(mode)) {
            return gramsPerPit == null ? null : String.format(Locale.US, "%.2f %s", gramsPerPit, defaultLabel(label, PIT_LABEL));
        }
        if (label != null && !label.isBlank()) {
            String normalizedLabel = normalize(label);
            if (normalizedLabel.contains("cova")) {
                return gramsPerPit == null ? null : String.format(Locale.US, "%.2f %s", gramsPerPit, label);
            }
            if (normalizedLabel.contains("m linear")) {
                return gramsPerLinearMeter == null ? null : String.format(Locale.US, "%.2f %s", gramsPerLinearMeter, label);
            }
        }
        return null;
    }

    private static String coveragePhase(String phase, Integer coverageOrder) {
        if (coverageOrder == null) {
            return phase;
        }
        if (phase == null || phase.isBlank()) {
            return "Cobertura " + coverageOrder;
        }
        String normalized = normalize(phase);
        return normalized.contains(String.valueOf(coverageOrder)) ? phase : phase + " " + coverageOrder;
    }

    private static String defaultLabel(String label, String defaultLabel) {
        return label == null || label.isBlank() ? defaultLabel : label;
    }

    private static boolean hasOperationalItemInSamePhase(Map<String, ShoppingItem> items, String name, String phase) {
        String itemName = cleanNullable(name);
        String normalizedPhase = normalize(phase);
        if (items.isEmpty() || itemName == null || normalizedPhase.isBlank()) {
            return false;
        }
        String normalizedName = normalize(itemName);
        for (ShoppingItem item : items.values()) {
            if (item == null) continue;
            if (!isOperationalFertilizationSection(item.getSection())) continue;
            if (normalizedName.equals(normalize(item.getName()))
                    && normalizedPhase.equals(normalize(item.getPhase()))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isOperationalFertilizationSection(String section) {
        String normalized = normalize(section);
        return normalize(SECTION_PLANTING_OPTION_1).equals(normalized)
                || normalize(SECTION_PLANTING_OPTION_2).equals(normalized)
                || normalize(SECTION_COVERAGE_OPTION_1).equals(normalized)
                || normalize(SECTION_COVERAGE_OPTION_2).equals(normalized);
    }

    private static void merge(Map<String, ShoppingItem> items,
                              String name,
                              Double kgHa,
                              String typeGroup,
                              String section,
                              String option,
                              String itemFlag,
                              String phase,
                              String localizedDose) {
        String itemName = cleanNullable(name);
        if (itemName == null) {
            return;
        }
        String key = normalize(itemName) + "|" + normalize(section) + "|" + normalize(option) + "|" + normalize(itemFlag) + "|" + normalize(phase);
        ShoppingItem existing = items.get(key);
        if (existing == null) {
            items.put(key, new ShoppingItem(itemName, kgHa, typeGroup, section, option, itemFlag, phase, localizedDose));
            return;
        }
        existing.addKgHa(kgHa);
        existing.addTypeGroup(typeGroup);
        existing.addPhase(phase);
        existing.addLocalizedDose(localizedDose);
    }

    private static void annotateOpportunityCostDecisions(Map<String, ShoppingItem> items, String source) {
        if (items.isEmpty() || source == null || source.isBlank()) return;
        String opportunitySection = section(source, "13.2. Comparativo de custo de oportunidade");
        if (opportunitySection.isBlank()) return;
        for (List<String> row : tableRows(opportunitySection)) {
            if (row.size() < 8 || normalize(row.get(0)).contains("categoria")) continue;
            String category = row.get(0);
            String fertilizer = row.get(1);
            String decision = row.get(6);
            String ratio = row.get(5);
            if (!isLowerOpportunityCostDecision(category, decision)) continue;
            String marker = "Escolha guiada por menor custo de oportunidade"
                    + (ratio == null || looksUnavailable(ratio) ? "" : " (razão PC/PO " + ratio + ")")
                    + ": " + decision + ".";
            for (ShoppingItem item : items.values()) {
                if (matchesOpportunityDecision(item, fertilizer)) {
                    item.addOpportunityCostDecision(marker);
                }
            }
        }
    }

    private static boolean isLowerOpportunityCostDecision(String category, String decision) {
        if (decision == null || decision.isBlank()) return false;
        String normalizedDecision = normalize(decision);
        if (normalizedDecision.contains("indeterminada") || normalizedDecision.contains("adubos simples")) return false;
        String normalizedCategory = normalize(category);
        return normalizedDecision.contains("composto")
                || normalizedDecision.contains("formulado")
                || normalizedDecision.contains("fte")
                || normalizedCategory.contains("composto")
                || normalizedCategory.contains("formulado")
                || normalizedCategory.contains("fte");
    }

    private static boolean matchesOpportunityDecision(ShoppingItem item, String fertilizer) {
        if (item == null || fertilizer == null || fertilizer.isBlank()) return false;
        String normalizedFertilizer = normalize(fertilizer);
        String normalizedName = normalize(item.getName());
        String normalizedGroup = normalize(item.getTypeGroup());
        if (normalizedName.contains(normalizedFertilizer) || normalizedFertilizer.contains(normalizedName)) {
            return true;
        }
        String fertilizerFormula = formulaKey(normalizedFertilizer);
        if (fertilizerFormula == null) {
            return false;
        }
        return normalizedName.contains(fertilizerFormula) || normalizedGroup.contains(fertilizerFormula);
    }

    private static String formulaKey(String value) {
        if (value == null || value.isBlank()) return null;
        Matcher matcher = Pattern.compile("(\\d+(?:[\\.,]\\d+)?)\\D+(\\d+(?:[\\.,]\\d+)?)\\D+(\\d+(?:[\\.,]\\d+)?)").matcher(value);
        if (!matcher.find()) return null;
        return formulaNumber(matcher.group(1)) + "-" + formulaNumber(matcher.group(2)) + "-" + formulaNumber(matcher.group(3));
    }

    private static String formulaNumber(String value) {
        try {
            double parsed = Double.parseDouble(value.replace(",", "."));
            if (Math.rint(parsed) == parsed) {
                return String.format(Locale.US, "%.0f", parsed);
            }
            return String.format(Locale.US, "%.2f", parsed);
        } catch (NumberFormatException ex) {
            return value;
        }
    }

    private static Optional<Double> parseDecimal(String value) {
        if (value == null || value.isBlank()) return Optional.empty();
        try {
            return Optional.of(Double.parseDouble(value.replace(",", ".")));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    private static String removeId(String value) {
        return value == null ? null : value.replaceAll("\\s*\\(ID\\s+\\d+\\)", "").trim();
    }

    private static String cleanNullable(String value) {
        if (value == null || value.isBlank() || looksUnavailable(value)) {
            return null;
        }
        return value.trim();
    }

    private static String normalize(String value) {
        if (value == null) return "";
        String noAccent = Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return noAccent.toLowerCase(Locale.ROOT);
    }

    private static boolean isDiscardableUnavailableLine(String line) {
        if (line == null || line.isBlank()) return false;
        String normalized = normalize(line);
        boolean unavailable = normalized.contains("nao calculad")
                || normalized.contains("nao se aplica")
                || normalized.contains("nao aplicavel")
                || normalized.contains("nao informado");
        if (!unavailable) {
            return false;
        }
        boolean conclusion = normalized.contains("aviso tecnico")
                || normalized.contains("justificativa")
                || normalized.contains("bloqueado")
                || normalized.contains("nao recomendad");
        return !conclusion;
    }

    private static boolean isZeroDoseLine(String line) {
        if (line == null || line.isBlank()) return false;
        Matcher matcher = QUANTITY_KG_HA.matcher(line);
        while (matcher.find()) {
            Optional<Double> parsed = parseDecimal(matcher.group(1));
            if (parsed.isPresent() && parsed.get() == 0d) {
                return true;
            }
        }
        return false;
    }

    private static void appendCleanedLine(StringBuilder target, String line) {
        if (target.length() > 0) {
            target.append("\n");
        }
        target.append(line);
    }

    private static String collapseBlankLines(String value) {
        return value.replaceAll("(\\R\\s*){3,}", "\n\n");
    }

    private static boolean isSeparatorRow(String line) {
        return line.replace("|", "").replace(":", "").replace("-", "").trim().isBlank();
    }

    private static String headingBlock(String text, String heading, boolean includeSubsections) {
        if (text == null || text.isBlank()) return "";
        String[] lines = text.split("\\R", -1);
        int startLine = -1;
        for (int i = 0; i < lines.length; i++) {
            if (matchesHeading(lines[i], heading)) {
                startLine = i;
                break;
            }
        }
        if (startLine < 0) return "";

        int endLine = lines.length;
        for (int i = startLine + 1; i < lines.length; i++) {
            if (isBoundaryHeading(lines[i], includeSubsections)) {
                endLine = i;
                break;
            }
        }

        return joinLines(lines, startLine, endLine).trim();
    }

    private static boolean matchesHeading(String line, String heading) {
        return plainHeading(line).equals(heading);
    }

    private static boolean isBoundaryHeading(String line, boolean includeSubsections) {
        String heading = plainHeading(line);
        if (TOP_LEVEL_HEADINGS.contains(heading)) return true;
        return includeSubsections && SUBSECTION_HEADINGS.contains(heading);
    }

    private static String plainHeading(String line) {
        if (line == null) return "";
        return line.trim().replaceFirst("^#{1,3}\\s+", "");
    }

    private static String joinLines(String[] lines, int startLine, int endLine) {
        StringBuilder block = new StringBuilder();
        for (int i = startLine; i < endLine; i++) {
            if (i > startLine) block.append("\n");
            block.append(lines[i]);
        }
        return block.toString();
    }

    static final class ShoppingItem {
        private final String name;
        private Double kgHa;
        private String typeGroup;
        private String section;
        private String option;
        private String itemFlag;
        private String phase;
        private String localizedDose;
        private String opportunityCostDecision;

        ShoppingItem(String name,
                     Double kgHa,
                     String typeGroup,
                     String section,
                     String option,
                     String itemFlag,
                     String phase,
                     String localizedDose) {
            this.name = name;
            this.kgHa = kgHa;
            this.typeGroup = typeGroup;
            this.section = section;
            this.option = option;
            this.itemFlag = itemFlag;
            this.phase = phase;
            this.localizedDose = localizedDose;
        }

        String getName() {
            return name;
        }

        Double getKgHa() {
            return kgHa;
        }

        String getTypeGroup() {
            return typeGroup == null || typeGroup.isBlank() ? null : typeGroup;
        }

        String getSection() {
            return section == null || section.isBlank() ? null : section;
        }

        String getOption() {
            return option == null || option.isBlank() ? null : option;
        }

        String getItemFlag() {
            return itemFlag == null || itemFlag.isBlank() ? null : itemFlag;
        }

        String getPhase() {
            return phase == null || phase.isBlank() ? null : phase;
        }

        String getLocalizedDose() {
            return localizedDose == null || localizedDose.isBlank() ? null : localizedDose;
        }

        String getOpportunityCostDecision() {
            return opportunityCostDecision == null || opportunityCostDecision.isBlank() ? null : opportunityCostDecision;
        }

        void addKgHa(Double value) {
            if (value == null) return;
            this.kgHa = this.kgHa == null ? value : this.kgHa + value;
        }

        void addTypeGroup(String value) {
            this.typeGroup = appendDistinct(this.typeGroup, value);
        }

        void addPhase(String value) {
            this.phase = appendDistinct(this.phase, value);
        }

        void addLocalizedDose(String value) {
            this.localizedDose = appendDistinct(this.localizedDose, value);
        }

        void addOpportunityCostDecision(String value) {
            this.opportunityCostDecision = appendDistinct(this.opportunityCostDecision, value);
        }

        private String appendDistinct(String current, String value) {
            if (value == null || value.isBlank()) return current;
            if (current == null || current.isBlank()) return value.trim();
            for (String part : current.split(";")) {
                if (normalize(part.trim()).equals(normalize(value.trim()))) {
                    return current;
                }
            }
            return current + "; " + value.trim();
        }
    }
}
