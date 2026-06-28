package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
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
        PropertyModel property = recommendation.getProperty();
        PlotModel plot = recommendation.getPlot();
        UserModel creator = recommendation.getCreator();
        report.append("## Identificação\n\n");
        report.append("- Cliente/Produtor: ").append(safe(property != null && property.getOwner() != null ? property.getOwner().getName() : null)).append("\n");
        report.append("- Propriedade: ").append(safe(property != null ? property.getNome() : null)).append("\n");
        report.append("- Município/UF: ").append(NOT_INFORMED).append("\n");
        report.append("- Talhão: ").append(safe(plot != null ? plot.getIdentification() : null)).append("\n");
        report.append("- Área avaliada: ").append(formatArea(plot != null ? plot.getArea() : null)).append("\n");
        report.append("- Cultura prevista: ").append(safe(recommendation.getCropName())).append("\n");
        report.append("- Safra/Safrinha: ").append(recommendation.getCropYear() == null ? NOT_INFORMED : recommendation.getCropYear()).append("\n");
        report.append("- Data de plantio: ").append(NOT_INFORMED).append("\n");
        report.append("- Responsável técnico: ").append(safe(creator != null ? creator.getName() : null)).append("\n");
        report.append("- Registro profissional: ").append(NOT_INFORMED).append("\n");
        report.append("- Data de emissão: ").append(formatDate(recommendation.getCreatedAt())).append("\n\n");
    }

    static String section(String markdown, String heading) {
        if (markdown == null || markdown.isBlank()) return "";
        String marker = "## " + heading;
        int start = markdown.indexOf(marker);
        if (start < 0) return "";
        int next = markdown.indexOf("\n## ", start + marker.length());
        return markdown.substring(start, next < 0 ? markdown.length() : next).trim();
    }

    static String subsection(String markdown, String heading) {
        if (markdown == null || markdown.isBlank()) return "";
        String marker = "### " + heading;
        int start = markdown.indexOf(marker);
        if (start < 0) return "";
        int nextSection = markdown.indexOf("\n## ", start + marker.length());
        int nextSubsection = markdown.indexOf("\n### ", start + marker.length());
        int end = minPositive(nextSection, nextSubsection);
        return markdown.substring(start, end < 0 ? markdown.length() : end).trim();
    }

    static void appendSourceSectionOrMessage(StringBuilder report, String title, String sourceSection, String missingMessage) {
        report.append("## ").append(title).append("\n\n");
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
        Map<String, ShoppingItem> items = new LinkedHashMap<>();
        String source = recommendation.getTechnicalReport();
        addCorrectiveItem(items, source, "Calcário", section(source, "7. Calagem"),
                "Necessidade de calagem ajustada",
                "Dose efetiva registrada pelo cálculo",
                "Dose corrigida por PRNT");
        addCorrectiveItem(items, source, "Gesso", section(source, "8. Gessagem"), "Dose comercial");
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
                    merge(items, itemName, kgHa.get(), source);
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
            extractKgHa(quantity).ifPresent(kgHa -> merge(items, removeId(fertilizer), kgHa, section));
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
            parseDecimal(dose).ifPresent(kgHa -> merge(items, removeId(sourceName), kgHa, section));
        }
    }

    private static void merge(Map<String, ShoppingItem> items, String name, Double kgHa, String source) {
        String itemName = safe(name);
        ShoppingItem existing = items.get(itemName);
        if (existing == null) {
            items.put(itemName, new ShoppingItem(itemName, kgHa));
            return;
        }
        existing.addKgHa(kgHa);
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

    private static int minPositive(int first, int second) {
        if (first < 0) return second;
        if (second < 0) return first;
        return Math.min(first, second);
    }

    static final class ShoppingItem {
        private final String name;
        private Double kgHa;

        ShoppingItem(String name, Double kgHa) {
            this.name = name;
            this.kgHa = kgHa;
        }

        String getName() {
            return name;
        }

        Double getKgHa() {
            return kgHa;
        }

        void addKgHa(Double value) {
            if (value == null) return;
            this.kgHa = this.kgHa == null ? value : this.kgHa + value;
        }
    }
}
