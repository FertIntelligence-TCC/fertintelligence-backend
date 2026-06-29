package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.dto.recommendation.RecommendationFertigramaDto;
import com.migueltcc.fertintelligence.dto.recommendation.RecommendationFertigramaItemDto;
import com.migueltcc.fertintelligence.dto.recommendation.RecommendationTableSectionDto;
import com.migueltcc.fertintelligence.dto.shoppingList.ShoppingListItemResponseDto;
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
public class RecommendationStructuredDataAssembler {

    private static final String SOURCE_TECHNICAL_REPORT = "technical_report";
    private static final String SOURCE_DIRECT_LINES = "direct_recommendation_lines";
    private static final String SOURCE_SHOPPING_ITEMS = "shopping_items";
    private static final String CHEMICAL_DIAGNOSIS_TITLE = "Diagnóstico químico";
    private static final String FOLIAR_DIAGNOSIS_TITLE = "Diagnóstico foliar";
    private static final Pattern NUMBER_PATTERN = Pattern.compile("[-+]?\\d+(?:[\\.,]\\d+)?");

    private final DirectRecommendationRepository directRecommendationRepository;
    private final DirectRecommendationMicronutrientFertilizerLineRepository micronutrientFertilizerLineRepository;
    private final DirectRecommendationPlantingFormulatedFertilizerLineRepository plantingFormulatedFertilizerLineRepository;
    private final DirectRecommendationCoverageFormulatedFertilizerLineRepository coverageFormulatedFertilizerLineRepository;

    public List<RecommendationTableSectionDto> generalSections(RecommendationModel recommendation) {
        String report = report(recommendation);
        List<RecommendationTableSectionDto> sections = new ArrayList<>();
        sections.add(section(report, "2. Dados utilizados", "Dados utilizados"));
        sections.add(section(report, "3. Diagnóstico químico", CHEMICAL_DIAGNOSIS_TITLE));
        sections.add(section(report, "4. Diagnóstico físico", "Diagnóstico físico"));
        sections.add(section(report, "5. Diagnóstico de salinidade/sodicidade", "Diagnóstico de salinidade/sodicidade"));
        sections.add(section(report, "6. Diagnóstico foliar", FOLIAR_DIAGNOSIS_TITLE));
        sections.add(section(report, "9. Adubação corretiva", "Adubação corretiva"));
        return nonEmpty(sections);
    }

    public List<RecommendationTableSectionDto> summarySections(RecommendationModel recommendation) {
        String report = report(recommendation);
        List<RecommendationTableSectionDto> sections = new ArrayList<>();
        sections.add(section(report, "10. Adubação de plantio", "Recomendações de N, P2O5 e K2O - plantio"));
        sections.add(section(report, "11. Adubação de cobertura", "Recomendações de N, P2O5 e K2O - cobertura"));
        sections.add(section(report, "13. Fertilizantes recomendados", "Adubos simples e formulados"));
        sections.add(section(TechnicalRecommendationDocumentSupport.subsection(report, "Fontes orgânicas, organominerais e micronutrientes"),
                null,
                "Fontes orgânicas, organominerais e micronutrientes"));
        sections.add(section(report, "12. Balanço nutricional", "Balanço nutricional"));
        return nonEmpty(sections);
    }

    public List<RecommendationFertigramaDto> generalFertigramas(RecommendationModel recommendation) {
        List<RecommendationTableSectionDto> sections = generalSections(recommendation);
        List<RecommendationFertigramaDto> fertigramas = new ArrayList<>();
        fertigramas.addAll(chemicalFertigramas(sections));
        fertigramas.addAll(foliarFertigramas(sections));
        return fertigramas;
    }

    public List<RecommendationFertigramaDto> summaryFertigramas(RecommendationModel recommendation) {
        return chemicalFertigramas(generalSections(recommendation));
    }

    public List<RecommendationTableSectionDto> directSections(RecommendationModel recommendation, String directReport) {
        List<RecommendationTableSectionDto> sections = new ArrayList<>();
        sections.add(micronutrientLineSection(recommendation));
        sections.add(directLineSection(recommendation));
        return nonEmpty(sections);
    }

    public List<ShoppingListItemResponseDto> shoppingItems(RecommendationModel recommendation) {
        Double area = resolveArea(recommendation);
        return TechnicalRecommendationDocumentSupport.collectShoppingItems(
                        recommendation,
                        micronutrientFertilizerLines(recommendation),
                        plantingFormulatedFertilizerLines(recommendation),
                        coverageFormulatedFertilizerLines(recommendation))
                .stream()
                .map(item -> ShoppingListItemResponseDto.builder()
                        .inputName(item.getName())
                        .typeGroup(item.getTypeGroup())
                        .phase(item.getPhase())
                        .quantityKgHa(item.getKgHa())
                        .quantityPerHectare(TechnicalRecommendationDocumentSupport.formatKgHa(item.getKgHa()))
                        .localizedUnit(item.getLocalizedDose())
                        .totalForArea(TechnicalRecommendationDocumentSupport.formatTotal(item.getKgHa(), area))
                        .build())
                .toList();
    }

    public List<String> observations(String text) {
        List<String> observations = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return observations;
        }
        for (String line : text.split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.startsWith("- ")) {
                observations.add(trimmed.substring(2).trim());
            }
        }
        return observations;
    }

    private RecommendationTableSectionDto directLineSection(RecommendationModel recommendation) {
        List<DirectRecommendationPlantingFormulatedFertilizerLineModel> planting = plantingFormulatedFertilizerLines(recommendation);
        List<DirectRecommendationCoverageFormulatedFertilizerLineModel> coverage = coverageFormulatedFertilizerLines(recommendation);
        if (planting.isEmpty() && coverage.isEmpty()) {
            return null;
        }

        List<List<String>> rows = new ArrayList<>();
        for (DirectRecommendationPlantingFormulatedFertilizerLineModel line : planting) {
            rows.add(List.of(
                    value(line.getPhase()),
                    value(line.getFertilizerName()),
                    value(line.getRelationUsed()),
                    TechnicalRecommendationDocumentSupport.formatKgHa(line.getDoseKgHa()),
                    value(line.getTechnicalObservation())));
        }
        for (DirectRecommendationCoverageFormulatedFertilizerLineModel line : coverage) {
            rows.add(List.of(
                    value(line.getPhase()),
                    value(line.getFertilizerName()),
                    value(line.getRelationUsed()),
                    TechnicalRecommendationDocumentSupport.formatKgHa(line.getDoseKgHa()),
                    value(line.getTechnicalObservation())));
        }

        return RecommendationTableSectionDto.builder()
                .title("Recomendação Direta - formulados")
                .source(SOURCE_DIRECT_LINES)
                .columns(List.of("Fase", "Formulado", "Relação N-P2O5-K2O", "Dose", "Observação técnica"))
                .rows(rows)
                .technicalObservations(extractLastColumn(rows))
                .build();
    }

    private RecommendationTableSectionDto micronutrientLineSection(RecommendationModel recommendation) {
        List<DirectRecommendationMicronutrientFertilizerLineModel> micronutrients = micronutrientFertilizerLines(recommendation);
        if (micronutrients.isEmpty()) {
            return null;
        }

        List<List<String>> rows = new ArrayList<>();
        for (DirectRecommendationMicronutrientFertilizerLineModel line : micronutrients) {
            rows.add(List.of(
                    value(line.getMicronutrient()),
                    value(line.getFertilizerName()),
                    TechnicalRecommendationDocumentSupport.formatKgHa(line.getMicronutrientDoseKgHa()),
                    TechnicalRecommendationDocumentSupport.formatKgHa(line.getFertilizerDoseKgHa()),
                    value(line.getTechnicalObservation())));
        }

        return RecommendationTableSectionDto.builder()
                .title("Recomendação Direta - micronutrientes")
                .source(SOURCE_DIRECT_LINES)
                .columns(List.of("Micronutriente", "Adubo", "Dose micronutriente", "Dose adubo", "Observação técnica"))
                .rows(rows)
                .technicalObservations(extractLastColumn(rows))
                .build();
    }

    private RecommendationTableSectionDto section(String report, String heading, String title) {
        String section = heading == null ? report : TechnicalRecommendationDocumentSupport.section(report, heading);
        List<List<String>> tableRows = TechnicalRecommendationDocumentSupport.tableRows(section);
        if (tableRows.isEmpty()) {
            return null;
        }
        return RecommendationTableSectionDto.builder()
                .title(title)
                .source(SOURCE_TECHNICAL_REPORT)
                .columns(tableHeader(section))
                .rows(tableRows)
                .technicalObservations(extractTechnicalObservations(tableRows))
                .build();
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

    private List<String> tableHeader(String section) {
        if (section == null || section.isBlank()) {
            return List.of();
        }
        for (String line : section.split("\\R")) {
            String trimmed = line.trim();
            if (!trimmed.startsWith("|") || isSeparatorRow(trimmed)) {
                continue;
            }
            return splitRow(trimmed);
        }
        return List.of();
    }

    private List<String> splitRow(String line) {
        String body = line.substring(1, line.endsWith("|") ? line.length() - 1 : line.length());
        List<String> cells = new ArrayList<>();
        for (String cell : body.split("\\|", -1)) {
            cells.add(cell.trim());
        }
        return cells;
    }

    private List<String> extractTechnicalObservations(List<List<String>> rows) {
        List<String> observations = new ArrayList<>();
        for (List<String> row : rows) {
            for (String cell : row) {
                if (cell != null && cell.toLowerCase().contains("observ")) {
                    observations.add(cell);
                    break;
                }
            }
        }
        return observations;
    }

    private List<String> extractLastColumn(List<List<String>> rows) {
        List<String> observations = new ArrayList<>();
        for (List<String> row : rows) {
            if (!row.isEmpty()) {
                observations.add(row.get(row.size() - 1));
            }
        }
        return observations;
    }

    private List<RecommendationTableSectionDto> nonEmpty(List<RecommendationTableSectionDto> sections) {
        return sections.stream()
                .filter(section -> section != null && section.getRows() != null && !section.getRows().isEmpty())
                .toList();
    }

    private String report(RecommendationModel recommendation) {
        return recommendation != null ? recommendation.getTechnicalReport() : null;
    }

    private String value(Object value) {
        return TechnicalRecommendationDocumentSupport.safe(value);
    }

    private boolean isSeparatorRow(String line) {
        return line.replace("|", "").replace(":", "").replace("-", "").trim().isBlank();
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

    private Double resolveArea(RecommendationModel recommendation) {
        if (recommendation != null && recommendation.getCropUsedAreaInThePlot() != null
                && recommendation.getCropUsedAreaInThePlot() > 0) {
            return recommendation.getCropUsedAreaInThePlot();
        }
        if (recommendation != null && recommendation.getPlot() != null
                && recommendation.getPlot().getArea() != null && recommendation.getPlot().getArea() > 0) {
            return recommendation.getPlot().getArea();
        }
        return null;
    }

    private Optional<DirectRecommendationModel> directRecommendation(RecommendationModel recommendation) {
        if (recommendation == null) {
            return Optional.empty();
        }
        if (recommendation.getDirectRecommendation() != null) {
            return Optional.of(recommendation.getDirectRecommendation());
        }
        if (recommendation.getId() == null) {
            return Optional.empty();
        }
        return directRecommendationRepository.findByRecommendation(recommendation);
    }

    private List<DirectRecommendationMicronutrientFertilizerLineModel> micronutrientFertilizerLines(RecommendationModel recommendation) {
        return directRecommendation(recommendation)
                .map(micronutrientFertilizerLineRepository::findAllByDirectRecommendationOrderByIdAsc)
                .orElseGet(List::of);
    }

    private List<DirectRecommendationPlantingFormulatedFertilizerLineModel> plantingFormulatedFertilizerLines(RecommendationModel recommendation) {
        return directRecommendation(recommendation)
                .map(plantingFormulatedFertilizerLineRepository::findAllByDirectRecommendationOrderByDoseKgHaDescIdAsc)
                .orElseGet(List::of);
    }

    private List<DirectRecommendationCoverageFormulatedFertilizerLineModel> coverageFormulatedFertilizerLines(RecommendationModel recommendation) {
        return directRecommendation(recommendation)
                .map(coverageFormulatedFertilizerLineRepository::findAllByDirectRecommendationOrderByCoverageOrderAscDoseKgHaDescIdAsc)
                .orElseGet(List::of);
    }

    private record FertigramaSpec(String label, String shortLabel, List<String> tokens) {}

    private record RangeBounds(Double min, Double max, String observation) {}

    private record NormalizedRange(Double value, Double adequateMin, Double adequateMax, String observation) {}
}
