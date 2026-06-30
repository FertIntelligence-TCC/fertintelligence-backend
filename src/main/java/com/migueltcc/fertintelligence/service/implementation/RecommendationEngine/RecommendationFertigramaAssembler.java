package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.dto.recommendation.RecommendationFertigramaDto;
import com.migueltcc.fertintelligence.dto.recommendation.RecommendationFertigramaItemDto;
import com.migueltcc.fertintelligence.dto.recommendation.RecommendationTableSectionDto;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class RecommendationFertigramaAssembler {

    private static final String CHEMICAL_DIAGNOSIS_TITLE = "Diagnóstico químico";
    private static final String FOLIAR_DIAGNOSIS_TITLE = "Diagnóstico foliar";
    private static final Pattern NUMBER_PATTERN = Pattern.compile("[-+]?\\d+(?:[\\.,]\\d+)?");

    private final RecommendationStructuredDataAssembler structuredDataAssembler;

    public List<RecommendationFertigramaDto> generalFertigramas(RecommendationModel recommendation) {
        return chemicalAndFoliarFertigramas(structuredDataAssembler.generalSections(recommendation));
    }

    public List<RecommendationFertigramaDto> generalFertigramas(String report) {
        return chemicalAndFoliarFertigramas(structuredDataAssembler.generalSections(report));
    }

    public List<RecommendationFertigramaDto> summaryFertigramas(RecommendationModel recommendation) {
        return chemicalFertigramas(structuredDataAssembler.generalSections(recommendation));
    }

    private List<RecommendationFertigramaDto> chemicalAndFoliarFertigramas(List<RecommendationTableSectionDto> sections) {
        List<RecommendationFertigramaDto> fertigramas = new ArrayList<>();
        fertigramas.addAll(chemicalFertigramas(sections));
        fertigramas.addAll(foliarFertigramas(sections));
        return fertigramas;
    }

    private List<RecommendationFertigramaDto> chemicalFertigramas(List<RecommendationTableSectionDto> sections) {
        Optional<RecommendationTableSectionDto> section = findSection(sections, CHEMICAL_DIAGNOSIS_TITLE);
        if (section.isEmpty()) {
            return List.of();
        }
        RecommendationTableSectionDto chemical = section.get();
        return List.of(
                fertigram(chemical, "Fertigrama químico - macronutrientes", "soil_chemical_macronutrients",
                        List.of(
                                spec("Fósforo", "P", "fosforo"),
                                spec("Potássio", "K", "potassio"),
                                spec("Cálcio", "Ca", "calcio"),
                                spec("Magnésio", "Mg", "magnesio"),
                                spec("Alumínio", "Al", "aluminio"),
                                spec("Enxofre", "S", "enxofre"))),
                fertigram(chemical, "Fertigrama químico - micronutrientes", "soil_chemical_micronutrients",
                        List.of(
                                spec("Boro", "B", "boro"),
                                spec("Cobre", "Cu", "cobre"),
                                spec("Ferro", "Fe", "ferro"),
                                spec("Manganês", "Mn", "manganes"),
                                spec("Zinco", "Zn", "zinco"))),
                fertigram(chemical, "Fertigrama químico - parâmetros 1", "soil_chemical_parameters_1",
                        List.of(
                                spec("pH", "pH", "ph"),
                                spec("Matéria Orgânica", "MO", "materia organica"),
                                spec("H+Al", "H+Al", "h+al"),
                                spec("Soma de bases", "SB", "soma de bases"))),
                fertigram(chemical, "Fertigrama químico - parâmetros 2", "soil_chemical_parameters_2",
                        List.of(
                                spec("CTC efetiva", "CTCe", "ctc efetiva"),
                                spec("CTC pH 7", "CTC7", "ctc ph 7"),
                                spec("Saturação por bases", "V%", "saturacao por bases"),
                                spec("Saturação por alumínio", "m%", "saturacao por aluminio")))
        );
    }

    private List<RecommendationFertigramaDto> foliarFertigramas(List<RecommendationTableSectionDto> sections) {
        Optional<RecommendationTableSectionDto> section = findSection(sections, FOLIAR_DIAGNOSIS_TITLE);
        if (section.isEmpty()) {
            return List.of();
        }
        RecommendationTableSectionDto foliar = section.get();
        return List.of(
                fertigram(foliar, "Fertigrama foliar - macronutrientes", "foliar_macronutrients",
                        List.of(
                                spec("Nitrogênio", "N", "nitrogenio"),
                                spec("Fósforo", "P", "fosforo"),
                                spec("Potássio", "K", "potassio"),
                                spec("Cálcio", "Ca", "calcio"),
                                spec("Magnésio", "Mg", "magnesio"),
                                spec("Alumínio", "Al", "aluminio"),
                                spec("Enxofre", "S", "enxofre"))),
                fertigram(foliar, "Fertigrama foliar - micronutrientes", "foliar_micronutrients",
                        List.of(
                                spec("Boro", "B", "boro"),
                                spec("Cobre", "Cu", "cobre"),
                                spec("Ferro", "Fe", "ferro"),
                                spec("Manganês", "Mn", "manganes"),
                                spec("Molibdênio", "Mo", "molibdenio"),
                                spec("Zinco", "Zn", "zinco")))
        );
    }

    private RecommendationFertigramaDto fertigram(RecommendationTableSectionDto section,
                                                  String title,
                                                  String groupKey,
                                                  List<FertigramaSpec> specs) {
        Map<String, List<String>> rowsByName = indexRowsByFirstColumn(section);
        List<RecommendationFertigramaItemDto> items = new ArrayList<>();
        for (FertigramaSpec spec : specs) {
            List<String> row = findRow(rowsByName, spec);
            if (row == null) {
                continue;
            }
            items.add(itemFromRow(row, spec));
        }
        long drawableItems = items.stream()
                .filter(item -> item.getNormalizedValue() != null)
                .count();
        return RecommendationFertigramaDto.builder()
                .title(title)
                .groupKey(groupKey)
                .sourceSection(section.getTitle())
                .items(items)
                .technicalWarning(drawableItems < 3
                        ? "Fertigrama retornado com menos de 3 itens normalizáveis a partir da tabela estruturada disponível."
                        : null)
                .build();
    }

    private RecommendationFertigramaItemDto itemFromRow(List<String> row, FertigramaSpec spec) {
        String analyzedCell = cell(row, 1);
        String unit = cell(row, 2);
        String interpretation = cell(row, 3);
        String rangeLabel = cell(row, 4);
        String observation = cell(row, 5);
        Double analyzedValue = parseNumber(analyzedCell);
        RangeBounds range = parseRange(rangeLabel);
        NormalizedRange normalized = normalizeValue(analyzedValue, range);
        String finalObservation = mergeObservation(observation, range.observation(), normalized.observation());

        return RecommendationFertigramaItemDto.builder()
                .label(spec.label())
                .shortLabel(spec.shortLabel())
                .analyzedValue(analyzedValue)
                .unit(blankToNull(unit))
                .adequateMin(range.min())
                .adequateMax(range.max())
                .normalizedValue(normalized.value())
                .normalizedAdequateMin(normalized.adequateMin())
                .normalizedAdequateMax(normalized.adequateMax())
                .interpretation(blankToNull(interpretation))
                .rangeLabel(blankToNull(rangeLabel))
                .observation(blankToNull(finalObservation))
                .build();
    }

    private NormalizedRange normalizeValue(Double analyzedValue, RangeBounds range) {
        if (analyzedValue == null) {
            return new NormalizedRange(null, null, null, "Valor analisado ausente; item não normalizado.");
        }
        if (range == null || (range.min() == null && range.max() == null)) {
            return new NormalizedRange(null, null, null, "Faixa adequada não interpretável a partir do texto da tabela; item não normalizado.");
        }

        Double min = range.min();
        Double max = range.max();
        String observation = null;
        if (min == null) {
            min = inferLowerBound(max);
            observation = "Faixa unilateral superior interpretada com limite inferior técnico apenas para normalização.";
        }
        if (max == null) {
            max = inferUpperBound(min);
            observation = "Faixa unilateral inferior interpretada com limite superior técnico apenas para normalização.";
        }
        if (min == null || max == null || max <= min) {
            return new NormalizedRange(null, null, null, "Faixa numérica inválida para normalização.");
        }

        double span = max - min;
        double normalizedValue;
        if (analyzedValue < min) {
            normalizedValue = 0.40d - ((min - analyzedValue) / span) * 0.40d;
        } else if (analyzedValue > max) {
            normalizedValue = 0.60d + ((analyzedValue - max) / span) * 0.40d;
        } else {
            normalizedValue = 0.40d + ((analyzedValue - min) / span) * 0.20d;
        }

        return new NormalizedRange(round4(clamp(normalizedValue)), 0.40d, 0.60d, observation);
    }

    private RangeBounds parseRange(String text) {
        if (text == null || text.isBlank() || isUnavailable(text)) {
            return new RangeBounds(null, null, null);
        }

        String normalized = normalize(text);
        List<Double> numbers = numbers(text);
        if (numbers.isEmpty()) {
            return new RangeBounds(null, null, "Faixa textual sem número interpretável.");
        }
        if (normalized.contains(" a ") && numbers.size() >= 2) {
            return orderedRange(numbers.get(0), numbers.get(1), null);
        }
        if ((normalized.contains(">=") || normalized.contains("=>")) && !numbers.isEmpty()) {
            return new RangeBounds(numbers.get(0), null, "Faixa textual com limite inferior único.");
        }
        if ((normalized.contains("<=") || normalized.contains("=<")) && !numbers.isEmpty()) {
            return new RangeBounds(null, numbers.get(0), "Faixa textual com limite superior único.");
        }
        if (normalized.contains(">") && !numbers.isEmpty()) {
            return new RangeBounds(numbers.get(0), null, "Faixa textual com limite inferior aberto.");
        }
        if (normalized.contains("<") && !numbers.isEmpty()) {
            return new RangeBounds(null, numbers.get(0), "Faixa textual com limite superior aberto.");
        }
        if (numbers.size() >= 2) {
            return orderedRange(numbers.get(0), numbers.get(1), null);
        }
        return new RangeBounds(null, null, "Faixa textual contém apenas um número sem operador claro.");
    }

    private RangeBounds orderedRange(Double first, Double second, String observation) {
        if (first == null || second == null) {
            return new RangeBounds(first, second, observation);
        }
        return first <= second
                ? new RangeBounds(first, second, observation)
                : new RangeBounds(second, first, observation);
    }

    private Optional<RecommendationTableSectionDto> findSection(List<RecommendationTableSectionDto> sections, String title) {
        if (sections == null) {
            return Optional.empty();
        }
        String normalizedTitle = normalize(title);
        return sections.stream()
                .filter(section -> section != null && normalize(section.getTitle()).equals(normalizedTitle))
                .findFirst();
    }

    private Map<String, List<String>> indexRowsByFirstColumn(RecommendationTableSectionDto section) {
        Map<String, List<String>> rowsByName = new LinkedHashMap<>();
        if (section == null || section.getRows() == null) {
            return rowsByName;
        }
        for (List<String> row : section.getRows()) {
            if (row == null || row.isEmpty()) {
                continue;
            }
            rowsByName.put(normalize(row.get(0)), row);
        }
        return rowsByName;
    }

    private List<String> findRow(Map<String, List<String>> rowsByName, FertigramaSpec spec) {
        for (Map.Entry<String, List<String>> entry : rowsByName.entrySet()) {
            String rowName = entry.getKey();
            for (String token : spec.tokens()) {
                if (rowName.contains(normalize(token))) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    private FertigramaSpec spec(String label, String shortLabel, String... tokens) {
        return new FertigramaSpec(label, shortLabel, List.of(tokens));
    }

    private String cell(List<String> row, int index) {
        return row != null && row.size() > index ? row.get(index) : null;
    }

    private Double parseNumber(String text) {
        if (text == null || text.isBlank() || isUnavailable(text)) {
            return null;
        }
        Matcher matcher = NUMBER_PATTERN.matcher(text);
        if (!matcher.find()) {
            return null;
        }
        try {
            return Double.valueOf(matcher.group().replace(",", "."));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private List<Double> numbers(String text) {
        List<Double> numbers = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return numbers;
        }
        Matcher matcher = NUMBER_PATTERN.matcher(text);
        while (matcher.find()) {
            try {
                numbers.add(Double.valueOf(matcher.group().replace(",", ".")));
            } catch (NumberFormatException ignored) {
                // Ignore isolated values that are not parseable after locale normalization.
            }
        }
        return numbers;
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replace(",", ".")
                .replace("7,0", "7")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private boolean isUnavailable(String value) {
        String normalized = normalize(value);
        return normalized.isBlank()
                || normalized.contains("nao informado")
                || normalized.contains("nao calculado")
                || normalized.contains("nao classificado")
                || normalized.equals("-");
    }

    private Double inferLowerBound(Double max) {
        if (max == null) {
            return null;
        }
        if (max > 0d) {
            return 0d;
        }
        return max - Math.max(Math.abs(max), 1d);
    }

    private Double inferUpperBound(Double min) {
        if (min == null) {
            return null;
        }
        if (min >= 0d) {
            return min + Math.max(Math.abs(min), 1d);
        }
        return 0d;
    }

    private double clamp(double value) {
        return Math.max(0d, Math.min(1d, value));
    }

    private Double round4(Double value) {
        if (value == null) {
            return null;
        }
        return Math.round(value * 10000d) / 10000d;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private String mergeObservation(String... observations) {
        List<String> available = new ArrayList<>();
        if (observations != null) {
            for (String observation : observations) {
                if (observation != null && !observation.isBlank()) {
                    available.add(observation.trim());
                }
            }
        }
        return available.isEmpty() ? null : String.join(" ", available);
    }

    private record FertigramaSpec(String label, String shortLabel, List<String> tokens) {}

    private record RangeBounds(Double min, Double max, String observation) {}

    private record NormalizedRange(Double value, Double adequateMin, Double adequateMax, String observation) {}
}
