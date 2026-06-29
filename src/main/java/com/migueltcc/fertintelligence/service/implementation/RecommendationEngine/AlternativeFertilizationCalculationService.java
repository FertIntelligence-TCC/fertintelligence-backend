package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Nutriente;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.DiverseContentRangeModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.MicronutrientDoseModel;
import com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels.BioFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.GreenFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.OrganoMineralFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.OrganicFertilizerModel;
import com.migueltcc.fertintelligence.repository.BioFertilizerRepository;
import com.migueltcc.fertintelligence.repository.DiverseContentRangeRepository;
import com.migueltcc.fertintelligence.repository.GreenFertilizerRepository;
import com.migueltcc.fertintelligence.repository.MicronutrientDoseRepository;
import com.migueltcc.fertintelligence.repository.OrganoMineralFertilizerRepository;
import com.migueltcc.fertintelligence.repository.OrganicFertilizerRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
class AlternativeFertilizationCalculationService {

    private static final double PERCENT_FACTOR = 100d;

    private final OrganicFertilizerRepository organicFertilizerRepository;
    private final OrganoMineralFertilizerRepository organoMineralFertilizerRepository;
    private final GreenFertilizerRepository greenFertilizerRepository;
    private final BioFertilizerRepository bioFertilizerRepository;
    private final MicronutrientDoseRepository micronutrientDoseRepository;
    private final DiverseContentRangeRepository diverseContentRangeRepository;
    private final MicronutrientFertilizerSelectionService micronutrientFertilizerSelectionService;
    private final CropSpacingCalculationService cropSpacingCalculationService;

    AlternativeFertilizationCalculationService(OrganicFertilizerRepository organicFertilizerRepository,
                                               OrganoMineralFertilizerRepository organoMineralFertilizerRepository,
                                               GreenFertilizerRepository greenFertilizerRepository,
                                               BioFertilizerRepository bioFertilizerRepository,
                                               MicronutrientDoseRepository micronutrientDoseRepository,
                                               DiverseContentRangeRepository diverseContentRangeRepository,
                                               MicronutrientFertilizerSelectionService micronutrientFertilizerSelectionService,
                                               CropSpacingCalculationService cropSpacingCalculationService) {
        this.organicFertilizerRepository = organicFertilizerRepository;
        this.organoMineralFertilizerRepository = organoMineralFertilizerRepository;
        this.greenFertilizerRepository = greenFertilizerRepository;
        this.bioFertilizerRepository = bioFertilizerRepository;
        this.micronutrientDoseRepository = micronutrientDoseRepository;
        this.diverseContentRangeRepository = diverseContentRangeRepository;
        this.micronutrientFertilizerSelectionService = micronutrientFertilizerSelectionService;
        this.cropSpacingCalculationService = cropSpacingCalculationService;
    }

    AlternativeFertilizationCalculationResult calculate(
            Double requiredN,
            Double requiredP2O5,
            Double requiredK2O,
            CropModel crop,
            List<RecommendationCalculationService.SoilChemicalDiagnosisItem> chemicalDiagnosis,
            List<RecommendationCalculationService.FoliarDiagnosisItem> foliarDiagnosis,
            SoilFertilityInterpretationCriteriaTableModel soilInterpretationTable,
            UserModel user,
            FertilizerSourceOption sourceOption,
            Boolean useOrganicFertilizer,
            Nutriente organicFertilizerReferenceNutrient,
            Boolean useGreenFertilizer,
            String greenFertilizerSpecies,
            Double greenFertilizerGreenMass,
            Double greenFertilizerMoisturePercentage,
            Double greenFertilizerDryMass,
            List<String> warnings) {
        List<RecommendationCalculationService.AlternativeFertilizationRecommendationRow> rows = new ArrayList<>();
        GreenFertilizerContribution greenContribution = calculateGreenFertilizerContribution(
                user, sourceOption, useGreenFertilizer, greenFertilizerSpecies, greenFertilizerGreenMass,
                greenFertilizerMoisturePercentage, greenFertilizerDryMass, requiredN, requiredP2O5, requiredK2O, warnings);
        addOrganicFertilizationRow(rows, user, sourceOption, useOrganicFertilizer,
                organicFertilizerReferenceNutrient, requiredN, requiredP2O5, requiredK2O, warnings);
        addNpkAlternativeRow(rows, "ORGANOMINERAL", selectBestOrganoMineralSource(user, sourceOption),
                greenContribution.remainingN(), greenContribution.remainingP2O5(), greenContribution.remainingK2O(), warnings);
        rows.add(greenContribution.row());
        addBiofertilizerLimitation(rows, user, sourceOption, warnings);
        MicronutrientRowsResult micronutrientRows = buildMicronutrientRows(
                chemicalDiagnosis, foliarDiagnosis, soilInterpretationTable, crop, user, sourceOption, warnings);
        rows.addAll(micronutrientRows.alternativeRows());
        return new AlternativeFertilizationCalculationResult(
                rows,
                micronutrientRows.directRecommendationRows(),
                greenContribution.remainingN(),
                greenContribution.remainingP2O5(),
                greenContribution.remainingK2O());
    }

    private void addOrganicFertilizationRow(List<RecommendationCalculationService.AlternativeFertilizationRecommendationRow> rows,
                                            UserModel user,
                                            FertilizerSourceOption sourceOption,
                                            Boolean useOrganicFertilizer,
                                            Nutriente referenceNutrient,
                                            Double requiredN,
                                            Double requiredP2O5,
                                            Double requiredK2O,
                                            List<String> warnings) {
        if (!Boolean.TRUE.equals(useOrganicFertilizer)) {
            rows.add(RecommendationCalculationService.AlternativeFertilizationRecommendationRow.builder()
                    .sourceType("ORGÂNICA")
                    .nutrientOrObjective("Não solicitada")
                    .sourceName("Não selecionada")
                    .dose("Não calculada")
                    .unit("kg/ha de produto")
                    .justification("Uso de adubo orgânico não habilitado no payload da recomendação.")
                    .limitations("Recomendação orgânica quantitativa não solicitada.")
                    .build());
            return;
        }

        if (referenceNutrient == null) {
            String limitation = "Uso de adubo orgânico habilitado, mas o nutriente de referência não foi informado no payload.";
            warnings.add(limitation);
            rows.add(organicLimitationRow("Não informado", "Não selecionada",
                    "Nutriente de referência ausente.", limitation));
            return;
        }

        Double targetNeed = targetNeedByReferenceNutrient(referenceNutrient, requiredN, requiredP2O5, requiredK2O);
        if (nvl(targetNeed) <= 0d) {
            String nutrientLabel = organicReferenceNutrientLabel(referenceNutrient);
            String limitation = "Necessidade de " + nutrientLabel + " ausente ou não positiva; dose orgânica não calculada.";
            warnings.add(limitation);
            rows.add(organicLimitationRow(nutrientLabel, "Não selecionada",
                    "Não há alvo quantitativo positivo para dimensionar o adubo orgânico.", limitation));
            return;
        }

        Optional<OrganicFertilizerModel> selectedSource = selectBestOrganicSource(user, sourceOption, referenceNutrient);
        if (selectedSource.isEmpty()) {
            String nutrientLabel = organicReferenceNutrientLabel(referenceNutrient);
            String limitation = "Não há fonte orgânica acessível com teor positivo de " + nutrientLabel
                    + " para a origem de adubos selecionada.";
            warnings.add(limitation);
            rows.add(organicLimitationRow(nutrientLabel, "Não selecionada",
                    "Fonte não selecionada por ausência de produto cadastrado com composição utilizável.", limitation));
            return;
        }

        OrganicFertilizerModel source = selectedSource.get();
        String nutrientLabel = organicReferenceNutrientLabel(referenceNutrient);
        double concentrationPercent = nvl(organicReferenceConcentration(source, referenceNutrient));
        double mineralizationPercent = nvl(source.getTaxaMineralizacaoPrimeiroAnoPercentual());
        if (mineralizationPercent <= 0d) {
            String limitation = "A fonte orgânica selecionada não possui fator de mineralização do primeiro ano positivo cadastrado.";
            warnings.add(limitation);
            rows.add(organicLimitationRow(nutrientLabel, source.getName(),
                    "Produto com composição disponível, porém sem mineralização válida para cálculo.", limitation));
            return;
        }

        double availableFraction = (concentrationPercent / PERCENT_FACTOR) * (mineralizationPercent / PERCENT_FACTOR);
        if (availableFraction <= 0d) {
            String limitation = "Teor de " + nutrientLabel + " e fator de mineralização resultaram em disponibilidade nula.";
            warnings.add(limitation);
            rows.add(organicLimitationRow(nutrientLabel, source.getName(),
                    "Dados cadastrados não permitem converter necessidade nutricional em dose de produto.", limitation));
            return;
        }

        double productDoseKgHa = round2(targetNeed / availableFraction);
        rows.add(RecommendationCalculationService.AlternativeFertilizationRecommendationRow.builder()
                .sourceType("ORGÂNICA")
                .nutrientOrObjective(nutrientLabel)
                .sourceName(source.getName())
                .dose(String.format(Locale.US, "%.2f", productDoseKgHa))
                .unit("kg/ha de produto")
                .justification(String.format(Locale.US,
                        "Dose calculada por %.2f kg/ha de %s, teor cadastrado de %.2f%% e mineralização do primeiro ano de %.2f%%.",
                        round2(targetNeed), nutrientLabel, round2(concentrationPercent), round2(mineralizationPercent)))
                .limitations("Dose dimensionada apenas pelo nutriente de referência selecionado; a recomendação mineral existente não foi abatida nesta etapa.")
                .build());
    }

    private RecommendationCalculationService.AlternativeFertilizationRecommendationRow organicLimitationRow(
            String nutrientOrObjective,
            String sourceName,
            String justification,
            String limitation) {
        return RecommendationCalculationService.AlternativeFertilizationRecommendationRow.builder()
                .sourceType("ORGÂNICA")
                .nutrientOrObjective(nutrientOrObjective)
                .sourceName(sourceName)
                .dose("Não calculada")
                .unit("kg/ha de produto")
                .justification(justification)
                .limitations(limitation)
                .build();
    }

    private void addNpkAlternativeRow(List<RecommendationCalculationService.AlternativeFertilizationRecommendationRow> rows,
                                      String sourceType,
                                      Optional<NpkAlternativeSource> source,
                                      Double requiredN,
                                      Double requiredP2O5,
                                      Double requiredK2O,
                                      List<String> warnings) {
        if (source.isEmpty()) {
            String limitation = "Não há fonte " + sourceType.toLowerCase(Locale.ROOT)
                    + " acessível com N, P2O5 ou K2O informado para a origem de adubos selecionada.";
            warnings.add(limitation);
            rows.add(RecommendationCalculationService.AlternativeFertilizationRecommendationRow.builder()
                    .sourceType(sourceType)
                    .nutrientOrObjective("NPK")
                    .sourceName("Não selecionada")
                    .dose("Não calculada")
                    .unit("kg/ha")
                    .justification("Fonte não selecionada por ausência de produto cadastrado com composição NPK utilizável.")
                    .limitations(limitation)
                    .build());
            return;
        }

        NpkAlternativeSource selected = source.get();
        Optional<FertilizerDoseCalculation> calculation = calculateByGreatestFactor(
                requiredN, requiredP2O5, requiredK2O, selected.n(), selected.p2o5(), selected.k2o(),
                "concentração NPK declarada no cadastro da fonte " + sourceType.toLowerCase(Locale.ROOT));
        if (calculation.isEmpty()) {
            String limitation = "Há fonte " + sourceType.toLowerCase(Locale.ROOT)
                    + " cadastrada, mas não há necessidade NPK positiva ou concentração válida para calcular dose.";
            warnings.add(limitation);
            rows.add(RecommendationCalculationService.AlternativeFertilizationRecommendationRow.builder()
                    .sourceType(sourceType)
                    .nutrientOrObjective("NPK")
                    .sourceName(selected.name())
                    .dose("Não calculada")
                    .unit("kg/ha")
                    .justification("Produto com composição NPK disponível, porém sem alvo quantitativo compatível.")
                    .limitations(limitation)
                    .build());
            return;
        }

        FertilizerDoseCalculation calc = calculation.get();
        String limitation = sourceType.equals("ORGÂNICA")
                ? "A dose usa teor total cadastrado; o backend não possui coeficiente de mineralização, umidade efetiva, eficiência agronômica ou restrição sanitária para fonte orgânica."
                : "A dose usa teor total cadastrado; o backend não possui coeficiente de eficiência agronômica, liberação gradual ou restrição específica do produto organomineral.";
        warnings.add(limitation);
        rows.add(RecommendationCalculationService.AlternativeFertilizationRecommendationRow.builder()
                .sourceType(sourceType)
                .nutrientOrObjective("NPK - nutriente alvo " + calc.nutrient())
                .sourceName(selected.name())
                .dose(String.format(Locale.US, "%.2f", calc.quantityKgHa()))
                .unit("kg/ha de produto")
                .justification(String.format(Locale.US,
                        "Dose calculada por %.2f kg/ha de %s e concentração cadastrada de %.2f%% na fonte.",
                        calc.targetNeedKgHa(), calc.nutrient(), calc.concentrationPercent()))
                .limitations(limitation)
                .build());
    }

    private GreenFertilizerContribution calculateGreenFertilizerContribution(UserModel user,
                                                                             FertilizerSourceOption sourceOption,
                                                                             Boolean useGreenFertilizer,
                                                                             String species,
                                                                             Double greenMassKgHa,
                                                                             Double moisturePercentage,
                                                                             Double dryMassKgHa,
                                                                             Double requiredN,
                                                                             Double requiredP2O5,
                                                                             Double requiredK2O,
                                                                             List<String> warnings) {
        if (!Boolean.TRUE.equals(useGreenFertilizer)) {
            return greenContribution(
                    requiredN, requiredP2O5, requiredK2O,
                    RecommendationCalculationService.AlternativeFertilizationRecommendationRow.builder()
                            .sourceType("ADUBO VERDE")
                            .nutrientOrObjective("Não solicitado")
                            .sourceName("Não selecionada")
                            .dose("Não calculada")
                            .unit("kg/ha de massa seca")
                            .justification("Uso de adubo verde não habilitado no payload da recomendação.")
                            .limitations("Contribuição nutricional por adubação verde não solicitada.")
                            .build());
        }

        List<GreenFertilizerModel> sources = selectGreenFertilizers(user, sourceOption);
        if (sources.isEmpty()) {
            String limitation = "Não há adubo verde acessível para a origem de adubos selecionada.";
            warnings.add(limitation);
            return greenContribution(requiredN, requiredP2O5, requiredK2O,
                    limitationRow("ADUBO VERDE", "N/P2O5/K2O", limitation));
        }

        Optional<GreenFertilizerModel> selected = selectGreenFertilizerBySpecies(sources, species);
        if (selected.isEmpty()) {
            String limitation = "Uso de adubo verde habilitado, mas a espécie informada não foi encontrada nos adubos verdes acessíveis.";
            warnings.add(limitation);
            return greenContribution(requiredN, requiredP2O5, requiredK2O,
                    RecommendationCalculationService.AlternativeFertilizationRecommendationRow.builder()
                            .sourceType("ADUBO VERDE")
                            .nutrientOrObjective("N/P2O5/K2O")
                            .sourceName(species == null || species.isBlank() ? "Não informada" : species)
                            .dose("Não calculada")
                            .unit("kg/ha de massa seca")
                            .justification("Fonte não selecionada porque a espécie do payload não corresponde a um cadastro acessível.")
                            .limitations(limitation)
                            .build());
        }

        GreenFertilizerModel source = selected.get();
        Optional<Double> dryMass = resolveGreenFertilizerDryMass(source, greenMassKgHa, moisturePercentage, dryMassKgHa);
        if (dryMass.isEmpty() || dryMass.get() <= 0d) {
            String limitation = "Uso de adubo verde habilitado, mas não há massa seca positiva ou dados suficientes para calculá-la.";
            warnings.add(limitation);
            return greenContribution(requiredN, requiredP2O5, requiredK2O,
                    RecommendationCalculationService.AlternativeFertilizationRecommendationRow.builder()
                            .sourceType("ADUBO VERDE")
                            .nutrientOrObjective("N/P2O5/K2O")
                            .sourceName(source.getName())
                            .dose("Não calculada")
                            .unit("kg/ha de massa seca")
                            .justification("A contribuição nutricional depende de massa seca informada, ou massa verde e umidade, ou produtividade esperada cadastrada.")
                            .limitations(limitation)
                            .build());
        }

        double mineralizationPercent = nvl(source.getTaxaMineralizacaoPrimeiroAnoPercentual());
        if (mineralizationPercent <= 0d) {
            String limitation = "Adubo verde selecionado sem mineralização de primeiro ano positiva cadastrada.";
            warnings.add(limitation);
            return greenContribution(requiredN, requiredP2O5, requiredK2O,
                    RecommendationCalculationService.AlternativeFertilizationRecommendationRow.builder()
                            .sourceType("ADUBO VERDE")
                            .nutrientOrObjective("N/P2O5/K2O")
                            .sourceName(source.getName())
                            .dose(formatNumber(round2(dryMass.get())))
                            .unit("kg/ha de massa seca")
                            .justification("Composição encontrada, mas sem fator de mineralização válido para estimar disponibilidade no primeiro ano.")
                            .limitations(limitation)
                            .build());
        }

        double availableFraction = mineralizationPercent / PERCENT_FACTOR;
        double contributionN = round2(dryMass.get() * nvl(source.getN()) / PERCENT_FACTOR * availableFraction);
        double contributionP2O5 = round2(dryMass.get() * nvl(source.getP2O5()) / PERCENT_FACTOR * availableFraction);
        double contributionK2O = round2(dryMass.get() * nvl(source.getK2O()) / PERCENT_FACTOR * availableFraction);
        double abatedN = Math.min(nvl(requiredN), contributionN);
        double abatedP2O5 = Math.min(nvl(requiredP2O5), contributionP2O5);
        double abatedK2O = Math.min(nvl(requiredK2O), contributionK2O);
        double remainingN = round2(Math.max(0d, nvl(requiredN) - abatedN));
        double remainingP2O5 = round2(Math.max(0d, nvl(requiredP2O5) - abatedP2O5));
        double remainingK2O = round2(Math.max(0d, nvl(requiredK2O) - abatedK2O));

        return new GreenFertilizerContribution(
                remainingN,
                remainingP2O5,
                remainingK2O,
                RecommendationCalculationService.AlternativeFertilizationRecommendationRow.builder()
                .sourceType("ADUBO VERDE")
                .nutrientOrObjective("N/P2O5/K2O")
                .sourceName(source.getName())
                .dose(formatNumber(round2(dryMass.get())))
                .unit("kg/ha de massa seca")
                .justification(String.format(Locale.US,
                        "Contribuição disponível no primeiro ano: N %.2f, P2O5 %.2f, K2O %.2f kg/ha; abatimento aplicado na necessidade mineral: N %.2f, P2O5 %.2f, K2O %.2f kg/ha.",
                        contributionN, contributionP2O5, contributionK2O, round2(abatedN), round2(abatedP2O5), round2(abatedK2O)))
                .limitations(String.format(Locale.US,
                        "Cálculo com massa seca %.2f kg/ha, teores cadastrados N %.2f%%, P2O5 %.2f%%, K2O %.2f%% e mineralização do primeiro ano %.2f%%. Excedentes da adubação verde não geram recomendação mineral negativa.",
                        round2(dryMass.get()), round2(nvl(source.getN())), round2(nvl(source.getP2O5())), round2(nvl(source.getK2O())), round2(mineralizationPercent)))
                .build());
    }

    private Optional<GreenFertilizerModel> selectGreenFertilizerBySpecies(List<GreenFertilizerModel> sources, String species) {
        String normalizedSpecies = normalizeText(species);
        if (normalizedSpecies.isBlank()) {
            return Optional.empty();
        }
        Optional<GreenFertilizerModel> exact = sources.stream()
                .filter(source -> normalizeText(source.getName()).equals(normalizedSpecies))
                .findFirst();
        if (exact.isPresent()) {
            return exact;
        }
        return sources.stream()
                .filter(source -> {
                    String sourceName = normalizeText(source.getName());
                    return !sourceName.isBlank()
                            && (sourceName.contains(normalizedSpecies) || normalizedSpecies.contains(sourceName));
                })
                .findFirst();
    }

    private Optional<Double> resolveGreenFertilizerDryMass(GreenFertilizerModel source,
                                                           Double greenMassKgHa,
                                                           Double moisturePercentage,
                                                           Double dryMassKgHa) {
        if (dryMassKgHa != null) {
            return Optional.of(round2(dryMassKgHa));
        }
        if (greenMassKgHa != null && moisturePercentage != null) {
            return Optional.of(round2(greenMassKgHa * (PERCENT_FACTOR - moisturePercentage) / PERCENT_FACTOR));
        }
        if (source != null && source.getProdutividadeEsperadaKgHa() != null) {
            return Optional.of(round2(source.getProdutividadeEsperadaKgHa()));
        }
        return Optional.empty();
    }

    private GreenFertilizerContribution greenContribution(
            Double requiredN,
            Double requiredP2O5,
            Double requiredK2O,
            RecommendationCalculationService.AlternativeFertilizationRecommendationRow row) {
        return new GreenFertilizerContribution(nvl(requiredN), nvl(requiredP2O5), nvl(requiredK2O), row);
    }

    private void addBiofertilizerLimitation(List<RecommendationCalculationService.AlternativeFertilizationRecommendationRow> rows,
                                            UserModel user,
                                            FertilizerSourceOption sourceOption,
                                            List<String> warnings) {
        List<BioFertilizerModel> sources = selectBioFertilizers(user, sourceOption);
        if (sources.isEmpty()) {
            String limitation = "Não há biofertilizante acessível para a origem de adubos selecionada.";
            warnings.add(limitation);
            rows.add(limitationRow("BIOFERTILIZANTE", "Nutrição foliar/estímulo fisiológico", limitation));
            return;
        }
        BioFertilizerModel source = sources.stream()
                .max(Comparator.comparing((BioFertilizerModel f) -> nvl(f.getN()) + nvl(f.getP2O5()) + nvl(f.getK2O()) + nvl(f.getB()) + nvl(f.getZn()))
                        .thenComparing(f -> f.getId() == null ? 0L : f.getId()))
                .orElse(null);
        String limitation = "Biofertilizante possui composição cadastrada, mas o backend não possui dose recomendada por cultura, concentração de calda, via de aplicação ou eficiência para calcular recomendação operacional.";
        warnings.add(limitation);
        rows.add(RecommendationCalculationService.AlternativeFertilizationRecommendationRow.builder()
                .sourceType("BIOFERTILIZANTE")
                .nutrientOrObjective("Nutrição foliar/estímulo fisiológico")
                .sourceName(source != null ? source.getName() : "Não selecionada")
                .dose("Não calculada")
                .unit("L/ha ou kg/ha")
                .justification("Fonte apresentada como opção cadastrada, sem recomendação quantitativa automática.")
                .limitations(limitation)
                .build());
    }

    private RecommendationCalculationService.AlternativeFertilizationRecommendationRow limitationRow(String sourceType, String objective, String limitation) {
        return RecommendationCalculationService.AlternativeFertilizationRecommendationRow.builder()
                .sourceType(sourceType)
                .nutrientOrObjective(objective)
                .sourceName("Não selecionada")
                .dose("Não calculada")
                .unit("Não modelada")
                .justification("Recomendação não gerada por ausência de dados suportados.")
                .limitations(limitation)
                .build();
    }

    private MicronutrientRowsResult buildMicronutrientRows(
            List<RecommendationCalculationService.SoilChemicalDiagnosisItem> chemicalDiagnosis,
            List<RecommendationCalculationService.FoliarDiagnosisItem> foliarDiagnosis,
            SoilFertilityInterpretationCriteriaTableModel soilInterpretationTable,
            CropModel crop,
            UserModel user,
            FertilizerSourceOption sourceOption,
            List<String> warnings) {
        List<RecommendationCalculationService.AlternativeFertilizationRecommendationRow> rows = new ArrayList<>();
        List<RecommendationCalculationService.MicronutrientFertilizerRecommendationRow> directRows = new ArrayList<>();
        Map<AppliedMicronutrient, RecommendationCalculationService.SoilChemicalDiagnosisItem> soilMicronutrients = soilMicronutrientDiagnosis(chemicalDiagnosis);
        Optional<MicronutrientDoseModel> micronutrientDoses = soilInterpretationTable != null
                ? micronutrientDoseRepository.findByTable(soilInterpretationTable)
                : Optional.empty();
        Optional<DiverseContentRangeModel> micronutrientRanges = soilInterpretationTable != null
                ? diverseContentRangeRepository.findByTable(soilInterpretationTable)
                : Optional.empty();

        if (micronutrientDoses.isPresent()) {
            MicronutrientDoseModel doses = micronutrientDoses.get();
            Map<AppliedMicronutrient, Double> recommendedDoses = new LinkedHashMap<>();
            Map<AppliedMicronutrient, String> doseJustifications = new LinkedHashMap<>();
            for (Map.Entry<AppliedMicronutrient, RecommendationCalculationService.SoilChemicalDiagnosisItem> entry : soilMicronutrients.entrySet()) {
                RecommendationCalculationService.SoilChemicalDiagnosisItem item = entry.getValue();
                String functionalRange = classifyMicronutrientDoseRange(entry.getKey(), item.getAnalyzedValue(), micronutrientRanges);
                Double dose = selectMicronutrientDose(doses, entry.getKey(), item.getAnalyzedValue(), item.getInterpretation(), micronutrientRanges);
                if (dose == null) {
                    warnings.add("Dose de micronutriente não calculada para " + entry.getKey().name()
                            + " por ausência de dose cadastrada para a faixa " + item.getInterpretation() + ".");
                    continue;
                }
                recommendedDoses.put(entry.getKey(), dose);
                doseJustifications.put(entry.getKey(), "Dose selecionada pela faixa "
                        + (functionalRange != null ? functionalRange : item.getInterpretation())
                        + " do teor de " + item.getAttribute() + " na tabela de fertilidade do solo.");
            }
            List<MicronutrientFertilizerSelectionService.MicronutrientFertilizerSelectionResult> selections =
                    micronutrientFertilizerSelectionService.select(user, sourceOption, recommendedDoses);
            for (MicronutrientFertilizerSelectionService.MicronutrientFertilizerSelectionResult selection : selections) {
                if (selection.calculated()) {
                    directRows.add(toDirectRecommendationMicronutrientRow(selection, crop, null));
                    rows.add(RecommendationCalculationService.AlternativeFertilizationRecommendationRow.builder()
                            .sourceType("MICRONUTRIENTE")
                            .nutrientOrObjective(selection.micronutrient().name())
                            .sourceName(selection.selectedFertilizer().getName())
                            .dose(formatNumber(selection.fertilizerDoseKgHa()))
                            .unit("kg/ha de produto")
                            .justification(doseJustifications.getOrDefault(selection.micronutrient(), "Dose elementar selecionada pela tabela de fertilidade do solo.")
                                    + " Produto escolhido por maior teor cadastrado de " + selection.micronutrient().name()
                                    + " (" + formatNumber(selection.selectedConcentrationPercent()) + "%).")
                            .limitations("Dose calculada por 100 * kg/ha do micronutriente recomendado / teor percentual do micronutriente no adubo mineral simples.")
                            .build());
                    continue;
                }
                warnings.add(selection.technicalMessage());
                directRows.add(toDirectRecommendationMicronutrientRow(selection, crop, selection.technicalMessage()));
                rows.add(RecommendationCalculationService.AlternativeFertilizationRecommendationRow.builder()
                        .sourceType("MICRONUTRIENTE")
                        .nutrientOrObjective(selection.micronutrient().name())
                        .sourceName("Não selecionada")
                        .dose("Não calculada")
                        .unit("kg/ha de produto")
                        .justification(doseJustifications.getOrDefault(selection.micronutrient(), "Dose elementar selecionada pela tabela de fertilidade do solo."))
                        .limitations(selection.technicalMessage())
                        .build());
            }
        } else if (!soilMicronutrients.isEmpty()) {
            warnings.add("Tabela de doses de micronutrientes não encontrada para a tabela de interpretação da fertilidade do solo selecionada.");
        }

        if (!rows.isEmpty()) {
            return new MicronutrientRowsResult(rows, directRows);
        }

        Map<AppliedMicronutrient, String> deficiencyEvidence = micronutrientDeficiencyEvidence(chemicalDiagnosis, foliarDiagnosis);
        String limitation = "Doses de micronutrientes por tabela de adubação de cultura foram descontinuadas; recomendação quantitativa de micronutrientes não está modelada para os dados disponíveis.";
        warnings.add(limitation);
        String objective = deficiencyEvidence.isEmpty()
                ? "B/Cu/Fe/Mn/Mo/Zn"
                : deficiencyEvidence.keySet().stream().map(Enum::name).collect(Collectors.joining("/"));
        String justification = deficiencyEvidence.isEmpty()
                ? "Não houve deficiência de micronutriente classificada pelos diagnósticos químico ou foliar disponíveis."
                : "Há evidência diagnóstica para micronutrientes, mas faltam teores classificados e doses cadastradas para recomendação quantitativa.";
        rows.add(RecommendationCalculationService.AlternativeFertilizationRecommendationRow.builder()
                .sourceType("MICRONUTRIENTE")
                .nutrientOrObjective(objective)
                .sourceName("Não selecionada")
                .dose("Não calculada")
                .unit("Não modelada")
                .justification(justification)
                .limitations(limitation)
                .build());
        return new MicronutrientRowsResult(rows, directRows);
    }

    private RecommendationCalculationService.MicronutrientFertilizerRecommendationRow toDirectRecommendationMicronutrientRow(
            MicronutrientFertilizerSelectionService.MicronutrientFertilizerSelectionResult selection,
            CropModel crop,
            String technicalObservation) {
        CropSpacingCalculationService.CropSpacingDoseResult spacingDose =
                cropSpacingCalculationService.calculate(crop, selection.fertilizerDoseKgHa());
        CropSpacingCalculationService.DoseUnitMetadata doseUnitMetadata =
                cropSpacingCalculationService.resolveDoseUnitMetadata(spacingDose);
        String observation = technicalObservation;
        if (observation == null) {
            observation = spacingDose.technicalWarning();
        } else if (spacingDose.technicalWarning() != null) {
            observation = observation + " " + spacingDose.technicalWarning();
        }

        return RecommendationCalculationService.MicronutrientFertilizerRecommendationRow.builder()
                .micronutrient(selection.micronutrient())
                .micronutrientDoseKgHa(selection.micronutrientDoseKgHa())
                .fertilizerId(selection.selectedFertilizer() != null ? selection.selectedFertilizer().getId() : null)
                .fertilizerName(selection.selectedFertilizer() != null ? selection.selectedFertilizer().getName() : null)
                .micronutrientConcentrationPercent(selection.selectedConcentrationPercent())
                .fertilizerDoseKgHa(selection.fertilizerDoseKgHa())
                .doseUnitMode(doseUnitMetadata.doseUnitMode())
                .doseUnitLabel(doseUnitMetadata.doseUnitLabel())
                .gramsPerLinearMeter(spacingDose.gramsPerLinearMeter())
                .gramsPerPit(spacingDose.gramsPerPit())
                .technicalObservation(observation)
                .build();
    }

    private Map<AppliedMicronutrient, RecommendationCalculationService.SoilChemicalDiagnosisItem> soilMicronutrientDiagnosis(
            List<RecommendationCalculationService.SoilChemicalDiagnosisItem> chemicalDiagnosis) {
        Map<AppliedMicronutrient, RecommendationCalculationService.SoilChemicalDiagnosisItem> diagnosis = new LinkedHashMap<>();
        if (chemicalDiagnosis == null) return diagnosis;
        for (RecommendationCalculationService.SoilChemicalDiagnosisItem item : chemicalDiagnosis) {
            if (item == null || item.getAnalyzedValue() == null || item.getInterpretation() == null) continue;
            if (!isInterpretation(item, "Baixo", "Médio", "Alto")) continue;
            AppliedMicronutrient micronutrient = micronutrientFromText(item.getAttribute());
            if (micronutrient == AppliedMicronutrient.B
                    || micronutrient == AppliedMicronutrient.Cu
                    || micronutrient == AppliedMicronutrient.Fe
                    || micronutrient == AppliedMicronutrient.Mn
                    || micronutrient == AppliedMicronutrient.Zn) {
                diagnosis.putIfAbsent(micronutrient, item);
            }
        }
        return diagnosis;
    }

    private Double selectMicronutrientDose(MicronutrientDoseModel doses,
                                           AppliedMicronutrient micronutrient,
                                           Double analyzedValue,
                                           String interpretation,
                                           Optional<DiverseContentRangeModel> diverseRange) {
        if (doses == null || micronutrient == null) return null;
        String range = classifyMicronutrientDoseRange(micronutrient, analyzedValue, diverseRange);
        if (range == null) {
            range = interpretation;
        }
        if (range == null) return null;
        return switch (micronutrient) {
            case B -> selectThreeLevelDose(range, doses.getBoronLowDose(), doses.getBoronMediumDose(), doses.getBoronHighDose());
            case Cu -> selectThreeLevelDose(range, doses.getCopperLowDose(), doses.getCopperMediumDose(), doses.getCopperHighDose());
            case Fe -> selectThreeLevelDose(range, doses.getIronLowDose(), doses.getIronMediumDose(), doses.getIronHighDose());
            case Mn -> selectThreeLevelDose(range, doses.getManganeseLowDose(), doses.getManganeseMediumDose(), doses.getManganeseHighDose());
            case Zn -> selectThreeLevelDose(range, doses.getZincLowDose(), doses.getZincMediumDose(), doses.getZincHighDose());
            default -> null;
        };
    }

    private String classifyMicronutrientDoseRange(AppliedMicronutrient micronutrient,
                                                  Double analyzedValue,
                                                  Optional<DiverseContentRangeModel> diverseRange) {
        if (micronutrient == null || analyzedValue == null || diverseRange == null || diverseRange.isEmpty()) return null;
        return classifyThreeLevelRangeName(analyzedValue, micronutrientCriterion(diverseRange.get(), micronutrient));
    }

    private ThreeLevelCriterion micronutrientCriterion(DiverseContentRangeModel range, AppliedMicronutrient micronutrient) {
        if (range == null || micronutrient == null) return null;
        return switch (micronutrient) {
            case B -> new ThreeLevelCriterion(range.getBoron_low_f(), range.getBoron_medium_i(), range.getBoron_medium_f(), range.getBoron_hight_i());
            case Cu -> new ThreeLevelCriterion(range.getCopper_low_f(), range.getCopper_medium_i(), range.getCopper_medium_f(), range.getCopper_hight_i());
            case Fe -> new ThreeLevelCriterion(range.getIron_low_f(), range.getIron_medium_i(), range.getIron_medium_f(), range.getIron_hight_i());
            case Mn -> new ThreeLevelCriterion(range.getManganese_low_f(), range.getManganese_medium_i(), range.getManganese_medium_f(), range.getManganese_hight_i());
            case Zn -> new ThreeLevelCriterion(range.getZinc_low_f(), range.getZinc_medium_i(), range.getZinc_medium_f(), range.getZinc_hight_i());
            default -> null;
        };
    }

    private Double selectThreeLevelDose(String interpretation, Double lowDose, Double mediumDose, Double highDose) {
        return switch (interpretation) {
            case "Baixo" -> lowDose;
            case "Médio" -> mediumDose;
            case "Alto" -> highDose;
            default -> null;
        };
    }

    private Map<AppliedMicronutrient, String> micronutrientDeficiencyEvidence(
            List<RecommendationCalculationService.SoilChemicalDiagnosisItem> chemicalDiagnosis,
            List<RecommendationCalculationService.FoliarDiagnosisItem> foliarDiagnosis) {
        Map<AppliedMicronutrient, String> evidence = new LinkedHashMap<>();
        if (chemicalDiagnosis != null) {
            for (RecommendationCalculationService.SoilChemicalDiagnosisItem item : chemicalDiagnosis) {
                if (item == null || !isInterpretation(item, "Muito baixo", "Baixo")) continue;
                AppliedMicronutrient micronutrient = micronutrientFromText(item.getAttribute());
                if (micronutrient != null) {
                    evidence.putIfAbsent(micronutrient, "diagnóstico químico do solo (" + item.getAttribute() + " " + item.getInterpretation() + ")");
                }
            }
        }
        if (foliarDiagnosis != null) {
            for (RecommendationCalculationService.FoliarDiagnosisItem item : foliarDiagnosis) {
                if (item == null || !"Deficiência".equals(item.getInterpretation())) continue;
                AppliedMicronutrient micronutrient = micronutrientFromText(item.getNutrient());
                if (micronutrient != null) {
                    evidence.putIfAbsent(micronutrient, "diagnóstico foliar (" + item.getNutrient() + " em deficiência)");
                }
            }
        }
        return evidence;
    }

    private Optional<OrganicFertilizerModel> selectBestOrganicSource(UserModel user, FertilizerSourceOption sourceOption, Nutriente referenceNutrient) {
        return selectOrganicFertilizers(user, sourceOption).stream()
                .filter(f -> nvl(organicReferenceConcentration(f, referenceNutrient)) > 0d)
                .max(Comparator.comparing((OrganicFertilizerModel f) -> organicReferenceEffectiveConcentration(f, referenceNutrient))
                        .thenComparing(f -> f.getId() == null ? 0L : f.getId()));
    }

    private Optional<NpkAlternativeSource> selectBestOrganoMineralSource(UserModel user, FertilizerSourceOption sourceOption) {
        return selectOrganoMineralFertilizers(user, sourceOption).stream()
                .filter(f -> nvl(f.getN()) > 0d || nvl(f.getP2O5()) > 0d || nvl(f.getK2O()) > 0d)
                .max(Comparator.comparing((OrganoMineralFertilizerModel f) -> nvl(f.getN()) + nvl(f.getP2O5()) + nvl(f.getK2O()))
                        .thenComparing(f -> f.getId() == null ? 0L : f.getId()))
                .map(f -> new NpkAlternativeSource(f.getName(), nvl(f.getN()), nvl(f.getP2O5()), nvl(f.getK2O())));
    }

    private List<OrganicFertilizerModel> selectOrganicFertilizers(UserModel user, FertilizerSourceOption sourceOption) {
        return switch (sourceOption) {
            case PRIVATE -> organicFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user);
            case PUBLIC -> organicFertilizerRepository.findAllByPublicoTrueAndUser_CargoNotOrderByNameAsc(Cargo.USUARIO_SUPREMO);
            case DEFAULT -> organicFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO);
            case BOTH, ALL -> dedup(dedup(organicFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user), organicFertilizerRepository.findAllByPublicoTrueAndUser_CargoNotOrderByNameAsc(Cargo.USUARIO_SUPREMO), OrganicFertilizerModel::getId), organicFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO), OrganicFertilizerModel::getId);
        };
    }

    private List<OrganoMineralFertilizerModel> selectOrganoMineralFertilizers(UserModel user, FertilizerSourceOption sourceOption) {
        return switch (sourceOption) {
            case PRIVATE -> organoMineralFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user);
            case PUBLIC -> organoMineralFertilizerRepository.findAllByPublicoTrueAndUser_CargoNotOrderByNameAsc(Cargo.USUARIO_SUPREMO);
            case DEFAULT -> organoMineralFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO);
            case BOTH, ALL -> dedup(dedup(organoMineralFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user), organoMineralFertilizerRepository.findAllByPublicoTrueAndUser_CargoNotOrderByNameAsc(Cargo.USUARIO_SUPREMO), OrganoMineralFertilizerModel::getId), organoMineralFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO), OrganoMineralFertilizerModel::getId);
        };
    }

    private List<GreenFertilizerModel> selectGreenFertilizers(UserModel user, FertilizerSourceOption sourceOption) {
        return switch (sourceOption) {
            case PRIVATE -> greenFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user);
            case PUBLIC -> greenFertilizerRepository.findAllByPublicoTrueAndUser_CargoNotOrderByNameAsc(Cargo.USUARIO_SUPREMO);
            case DEFAULT -> greenFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO);
            case BOTH, ALL -> dedup(dedup(greenFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user), greenFertilizerRepository.findAllByPublicoTrueAndUser_CargoNotOrderByNameAsc(Cargo.USUARIO_SUPREMO), GreenFertilizerModel::getId), greenFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO), GreenFertilizerModel::getId);
        };
    }

    private List<BioFertilizerModel> selectBioFertilizers(UserModel user, FertilizerSourceOption sourceOption) {
        return switch (sourceOption) {
            case PRIVATE -> bioFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user);
            case PUBLIC -> bioFertilizerRepository.findAllByPublicoTrueAndUser_CargoNotOrderByNameAsc(Cargo.USUARIO_SUPREMO);
            case DEFAULT -> bioFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO);
            case BOTH, ALL -> dedup(dedup(bioFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user), bioFertilizerRepository.findAllByPublicoTrueAndUser_CargoNotOrderByNameAsc(Cargo.USUARIO_SUPREMO), BioFertilizerModel::getId), bioFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO), BioFertilizerModel::getId);
        };
    }

    private Optional<FertilizerDoseCalculation> calculateByGreatestFactor(Double rn, Double rp, Double rk, double n, double p, double k, String method) {
        List<FertilizerDoseCalculation> calculations = new ArrayList<>();
        addDoseCandidate(calculations, "N", rn, n, method);
        addDoseCandidate(calculations, "P2O5", rp, p, method);
        addDoseCandidate(calculations, "K2O", rk, k, method);
        return calculations.stream().max(Comparator.comparing(FertilizerDoseCalculation::quantityKgHa));
    }

    private Double targetNeedByReferenceNutrient(Nutriente nutrient, Double requiredN, Double requiredP2O5, Double requiredK2O) {
        if (nutrient == null) return null;
        return switch (nutrient) {
            case NITROGENIO -> requiredN;
            case FOSFORO -> requiredP2O5;
            case POTASSIO -> requiredK2O;
        };
    }

    private Double organicReferenceConcentration(OrganicFertilizerModel fertilizer, Nutriente nutrient) {
        if (fertilizer == null || nutrient == null) return null;
        return switch (nutrient) {
            case NITROGENIO -> fertilizer.getN();
            case FOSFORO -> fertilizer.getP2O5();
            case POTASSIO -> fertilizer.getK2O();
        };
    }

    private double organicReferenceEffectiveConcentration(OrganicFertilizerModel fertilizer, Nutriente nutrient) {
        return nvl(organicReferenceConcentration(fertilizer, nutrient))
                * nvl(fertilizer != null ? fertilizer.getTaxaMineralizacaoPrimeiroAnoPercentual() : null);
    }

    private String organicReferenceNutrientLabel(Nutriente nutrient) {
        if (nutrient == null) return "Não informado";
        return switch (nutrient) {
            case NITROGENIO -> "N";
            case FOSFORO -> "P2O5";
            case POTASSIO -> "K2O";
        };
    }

    private void addDoseCandidate(List<FertilizerDoseCalculation> calculations, String nutrient, Double required, double concentration, String method) {
        if (nvl(required) <= 0d || concentration <= 0d) return;
        calculations.add(new FertilizerDoseCalculation(nutrient, round2(required), round2(concentration), round2(required / concentration * 100d), method));
    }

    private boolean isInterpretation(RecommendationCalculationService.SoilChemicalDiagnosisItem item, String... expected) {
        if (item == null || item.getInterpretation() == null) return false;
        return List.of(expected).contains(item.getInterpretation());
    }

    private String classifyThreeLevelRangeName(Double value, ThreeLevelCriterion criterion) {
        if (value == null || criterion == null || criterion.lowLimit() == null || criterion.mediumStart() == null
                || criterion.mediumEnd() == null || criterion.highLimit() == null) {
            return null;
        }
        if (value < criterion.lowLimit()) return "Baixo";
        if (value >= criterion.mediumStart() && value <= criterion.mediumEnd()) return "Médio";
        if (value > criterion.highLimit()) return "Alto";
        return null;
    }

    private AppliedMicronutrient micronutrientFromText(String text) {
        String normalized = normalizeText(text);
        if (normalized.contains("boro") || normalized.contains("(b)")) return AppliedMicronutrient.B;
        if (normalized.contains("cobre") || normalized.contains("cu")) return AppliedMicronutrient.Cu;
        if (normalized.contains("ferro") || normalized.contains("fe")) return AppliedMicronutrient.Fe;
        if (normalized.contains("manganes") || normalized.contains("mn")) return AppliedMicronutrient.Mn;
        if (normalized.contains("molibdenio") || normalized.contains("mo")) return AppliedMicronutrient.Mo;
        if (normalized.contains("zinco") || normalized.contains("zn")) return AppliedMicronutrient.Zn;
        return null;
    }

    private String normalizeText(String value) {
        if (value == null) return "";
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }

    private String formatNumber(Double value) {
        if (value == null) return "não informado";
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }

    private double nvl(Double v) {
        return v == null ? 0d : v;
    }

    private double round2(double v) {
        return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private <T> List<T> dedup(List<T> a, List<T> b, Function<T, Long> id) {
        Map<Long, T> m = new LinkedHashMap<>();
        a.forEach(x -> m.putIfAbsent(id.apply(x), x));
        b.forEach(x -> m.putIfAbsent(id.apply(x), x));
        return new ArrayList<>(m.values());
    }

    private record NpkAlternativeSource(String name, double n, double p2o5, double k2o) {
    }

    private record FertilizerDoseCalculation(String nutrient, double targetNeedKgHa, double concentrationPercent, double quantityKgHa, String method) {
    }

    private record ThreeLevelCriterion(Double lowLimit, Double mediumStart, Double mediumEnd, Double highLimit) {
    }

    record AlternativeFertilizationCalculationResult(
            List<RecommendationCalculationService.AlternativeFertilizationRecommendationRow> alternativeRows,
            List<RecommendationCalculationService.MicronutrientFertilizerRecommendationRow> directRecommendationRows,
            Double remainingRequiredN,
            Double remainingRequiredP2O5,
            Double remainingRequiredK2O) {
    }

    private record MicronutrientRowsResult(
            List<RecommendationCalculationService.AlternativeFertilizationRecommendationRow> alternativeRows,
            List<RecommendationCalculationService.MicronutrientFertilizerRecommendationRow> directRecommendationRows) {
    }

    private record GreenFertilizerContribution(
            Double remainingN,
            Double remainingP2O5,
            Double remainingK2O,
            RecommendationCalculationService.AlternativeFertilizationRecommendationRow row) {
    }
}
