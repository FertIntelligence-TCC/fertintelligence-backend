package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

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
        PropertyModel property = recommendation != null ? recommendation.getProperty() : null;
        PlotModel plot = recommendation != null ? recommendation.getPlot() : null;
        UserModel creator = recommendation != null ? recommendation.getCreator() : null;
        report.append("Identificação\n\n");
        report.append("- Cliente/Produtor: ").append(safe(property != null && property.getOwner() != null ? property.getOwner().getName() : null)).append("\n");
        report.append("- Propriedade: ").append(safe(property != null ? property.getNome() : null)).append("\n");
        report.append("- Município/UF: ").append(NOT_INFORMED).append("\n");
        report.append("- Talhão: ").append(safe(plot != null ? plot.getIdentification() : null)).append("\n");
        report.append("- Área avaliada: ").append(formatArea(plot != null ? plot.getArea() : null)).append("\n");
        report.append("- Cultura prevista: ").append(safe(recommendation != null ? recommendation.getCropName() : null)).append("\n");
        report.append("- Safra/Safrinha: ").append(recommendation == null || recommendation.getCropYear() == null ? NOT_INFORMED : recommendation.getCropYear()).append("\n");
        report.append("- Data de plantio: ").append(NOT_INFORMED).append("\n");
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
        String content = stripHeading(sourceSection);
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
        return collectShoppingItems(recommendation, List.of(), List.of(), List.of());
    }

    static List<ShoppingItem> collectShoppingItems(
            RecommendationModel recommendation,
            List<DirectRecommendationMicronutrientFertilizerLineModel> micronutrientFertilizerLines,
            List<DirectRecommendationPlantingFormulatedFertilizerLineModel> plantingFormulatedFertilizerLines,
            List<DirectRecommendationCoverageFormulatedFertilizerLineModel> coverageFormulatedFertilizerLines) {
        Map<String, ShoppingItem> items = new LinkedHashMap<>();
        String source = recommendation.getTechnicalReport();
        addCorrectiveItem(items, source, "Calcário", section(source, "7. Calagem"),
                "Necessidade de calagem ajustada",
                "Dose efetiva registrada pelo cálculo",
                "Dose corrigida por PRNT");
        addCorrectiveItem(items, source, "Gesso", section(source, "8. Gessagem"), "Dose comercial");
        addDirectMicronutrientItems(items, micronutrientFertilizerLines);
        addDirectPlantingFormulatedItems(items, plantingFormulatedFertilizerLines);
        addDirectCoverageFormulatedItems(items, coverageFormulatedFertilizerLines);
        addAlternativeItems(items, subsection(source, "Fontes orgânicas, organominerais e micronutrientes"));
        addFertilizationItems(items, section(source, "10. Adubação de plantio"));
        addFertilizationItems(items, section(source, "11. Adubação de cobertura"));
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

    static String formatDate(LocalDateTime date) {
        return (date == null ? LocalDateTime.now() : date).format(BR_DATE_TIME);
    }

    static String formatArea(Double area) {
        return area == null ? NOT_INFORMED : String.format(Locale.US, "%.2f ha", area);
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

    private static void addCorrectiveItem(Map<String, ShoppingItem> items, String source, String itemName, String section, String... labels) {
        if (section.isBlank()) return;
        for (String label : labels) {
            for (String line : section.split("\\R")) {
                if (!normalize(line).contains(normalize(label))) continue;
                Optional<Double> kgHa = extractKgHa(line);
                if (kgHa.isPresent()) {
                    merge(items, itemName, kgHa.get(), null, null, null);
                    return;
                }
            }
        }
    }

    private static void addFertilizationItems(Map<String, ShoppingItem> items, String section) {
        for (List<String> row : tableRows(section)) {
            if (row.size() < 4) continue;
            String fertilizer = row.get(2);
            String quantity = row.get(3);
            if (looksUnavailable(fertilizer) || looksUnavailable(quantity)) continue;
            String itemName = removeId(fertilizer);
            if (items.containsKey(safe(itemName))) continue;
            extractKgHa(quantity).ifPresent(kgHa -> merge(items, itemName, kgHa, null, null, null));
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
            if (items.containsKey(safe(itemName))) continue;
            parseDecimal(dose).ifPresent(kgHa -> merge(items, itemName, kgHa, null, null, null));
        }
    }

    private static void addDirectMicronutrientItems(
            Map<String, ShoppingItem> items,
            List<DirectRecommendationMicronutrientFertilizerLineModel> lines) {
        if (lines == null) return;
        for (DirectRecommendationMicronutrientFertilizerLineModel line : lines) {
            if (line == null || looksUnavailable(line.getFertilizerName()) || line.getFertilizerDoseKgHa() == null) continue;
            merge(
                    items,
                    removeId(line.getFertilizerName()),
                    line.getFertilizerDoseKgHa(),
                    "Micronutriente" + (line.getMicronutrient() == null ? "" : " - " + line.getMicronutrient()),
                    null,
                    localizedDose(line.getDoseUnitLabel(), line.getGramsPerLinearMeter(), line.getGramsPerPit()));
        }
    }

    private static void addDirectPlantingFormulatedItems(
            Map<String, ShoppingItem> items,
            List<DirectRecommendationPlantingFormulatedFertilizerLineModel> lines) {
        if (lines == null) return;
        for (DirectRecommendationPlantingFormulatedFertilizerLineModel line : lines) {
            if (line == null || looksUnavailable(line.getFertilizerName()) || line.getDoseKgHa() == null) continue;
            merge(
                    items,
                    removeId(line.getFertilizerName()),
                    line.getDoseKgHa(),
                    formattedFormulatedGroup(line.getNitrogenPercent(), line.getP2o5Percent(), line.getK2oPercent()),
                    line.getPhase(),
                    localizedDose(line.getDoseUnitLabel(), line.getGramsPerLinearMeter(), line.getGramsPerPit()));
        }
    }

    private static void addDirectCoverageFormulatedItems(
            Map<String, ShoppingItem> items,
            List<DirectRecommendationCoverageFormulatedFertilizerLineModel> lines) {
        if (lines == null) return;
        for (DirectRecommendationCoverageFormulatedFertilizerLineModel line : lines) {
            if (line == null || looksUnavailable(line.getFertilizerName()) || line.getDoseKgHa() == null) continue;
            String phase = line.getPhase();
            if (line.getCoverageOrder() != null) {
                phase = safePhase(phase) + " " + line.getCoverageOrder();
            }
            merge(
                    items,
                    removeId(line.getFertilizerName()),
                    line.getDoseKgHa(),
                    formattedFormulatedGroup(line.getNitrogenPercent(), line.getP2o5Percent(), line.getK2oPercent()),
                    phase,
                    localizedDose(line.getDoseUnitLabel(), line.getGramsPerLinearMeter(), line.getGramsPerPit()));
        }
    }

    private static String formattedFormulatedGroup(Double nitrogen, Double p2o5, Double k2o) {
        String formula = formatPercentForFormula(nitrogen) + "-" + formatPercentForFormula(p2o5) + "-" + formatPercentForFormula(k2o);
        return "Formulado NPK " + formula;
    }

    private static String formatPercentForFormula(Double value) {
        if (value == null) return "?";
        if (Math.rint(value) == value) {
            return String.format(Locale.US, "%.0f", value);
        }
        return String.format(Locale.US, "%.2f", value);
    }

    private static String localizedDose(String label, Double gramsPerLinearMeter, Double gramsPerPit) {
        if (label == null || label.isBlank()) return null;
        String normalizedLabel = normalize(label);
        if (normalizedLabel.contains("cova")) {
            return gramsPerPit == null ? null : String.format(Locale.US, "%.2f %s", gramsPerPit, label);
        }
        if (normalizedLabel.contains("m linear")) {
            return gramsPerLinearMeter == null ? null : String.format(Locale.US, "%.2f %s", gramsPerLinearMeter, label);
        }
        return null;
    }

    private static String safePhase(String value) {
        return value == null || value.isBlank() ? "Cobertura" : value.trim();
    }

    private static void merge(Map<String, ShoppingItem> items, String name, Double kgHa, String typeGroup, String phase, String localizedDose) {
        String itemName = safe(name);
        ShoppingItem existing = items.get(itemName);
        if (existing == null) {
            items.put(itemName, new ShoppingItem(itemName, kgHa, typeGroup, phase, localizedDose));
            return;
        }
        existing.addKgHa(kgHa);
        existing.addTypeGroup(typeGroup);
        existing.addPhase(phase);
        existing.addLocalizedDose(localizedDose);
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

    private static String normalize(String value) {
        if (value == null) return "";
        String noAccent = Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return noAccent.toLowerCase(Locale.ROOT);
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
        private String phase;
        private String localizedDose;

        ShoppingItem(String name, Double kgHa, String typeGroup, String phase, String localizedDose) {
            this.name = name;
            this.kgHa = kgHa;
            this.typeGroup = typeGroup;
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
            return typeGroup == null || typeGroup.isBlank() ? NOT_APPLICABLE : typeGroup;
        }

        String getPhase() {
            return phase == null || phase.isBlank() ? NOT_APPLICABLE : phase;
        }

        String getLocalizedDose() {
            return localizedDose == null || localizedDose.isBlank() ? NOT_APPLICABLE : localizedDose;
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
