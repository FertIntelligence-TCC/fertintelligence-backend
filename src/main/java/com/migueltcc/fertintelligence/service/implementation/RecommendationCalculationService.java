package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.CriterioCalagem;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.MenorMaiorTeores;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Nutriente;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.UnidadeTeor;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.TechnicalTableGroup;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.MacronutrientsContent;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.MicronutrientsContent;
import com.migueltcc.fertintelligence.dto.recommendation.RecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.*;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.FoliarAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.FertilityAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.PhysicalAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.SaturationExtractAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.ContentRangeModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CoverageModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFertilizationMicronutrientDoseModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFertilizationTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels.BioFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels.ChelatedFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels.MineralFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.AvailablePAnionExchangeResinExtractorModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.AvailablePMehlich1ExtractorModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.AvailableSModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.DiverseContentRangeModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.ExchangeableSodiumModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.KExchangeableContentModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.SalinityInterpretationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.FormulatedMineralFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.GreenFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.OrganoMineralFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.OrganicFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.SimpleMineralFertilizerModel;
import com.migueltcc.fertintelligence.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

@Service
public class RecommendationCalculationService {

    private final PhysicalAnalysisExtractRepository physicalAnalysisExtractRepository;
    private final SoilAnalysisRepository soilAnalysisRepository;
    private final SaturationExtractAnalysisExtractRepository saturationExtractAnalysisExtractRepository;
    private final AnnualCropFolderRepository annualCropFolderRepository;
    private final CropRepository cropRepository;
    private final FoliarAnalysisRepository foliarAnalysisRepository;
    private final CropFertilizationTableRepository cropFertilizationTableRepository;
    private final ContentRangeRepository contentRangeRepository;
    private final CoverageRepository coverageRepository;
    private final FormulatedMineralFertilizerRepository formulatedMineralFertilizerRepository;
    private final SimpleMineralFertilizerRepository simpleMineralFertilizerRepository;
    private final FertilityAnalysisExtractRepository fertilityAnalysisExtractRepository;
    private final SoilFertilityInterpretationCriteriaTableRepository soilFertilityInterpretationCriteriaTableRepository;
    private final CropFoliarAnalysisInterpretationTableRepository cropFoliarAnalysisInterpretationTableRepository;
    private final CropFoliarAnalysisInterpretationTableLineRepository cropFoliarAnalysisInterpretationTableLineRepository;
    private final DiverseContentRangeRepository diverseContentRangeRepository;
    private final KExchangeableContentRepository kExchangeableContentRepository;
    private final AvailablePMehlich1ExtractorRepository availablePMehlich1ExtractorRepository;
    private final AvailablePAnionExchangeResinExtractorRepository availablePAnionExchangeResinExtractorRepository;
    private final AvailableSRepository availableSRepository;
    private final ExchangeableSodiumRepository exchangeableSodiumRepository;
    private final SalinityInterpretationRepository salinityInterpretationRepository;
    private final OrganicFertilizerRepository organicFertilizerRepository;
    private final OrganoMineralFertilizerRepository organoMineralFertilizerRepository;
    private final GreenFertilizerRepository greenFertilizerRepository;
    private final BioFertilizerRepository bioFertilizerRepository;
    private final MineralFertilizerRepository mineralFertilizerRepository;
    private final ChelatedFertilizerRepository chelatedFertilizerRepository;
    private final CropFertilizationMicronutrientDoseRepository micronutrientDoseRepository;

    public RecommendationCalculationService(PhysicalAnalysisExtractRepository physicalAnalysisExtractRepository,
                                            SoilAnalysisRepository soilAnalysisRepository,
                                            SaturationExtractAnalysisExtractRepository saturationExtractAnalysisExtractRepository,
                                            AnnualCropFolderRepository annualCropFolderRepository,
                                            CropRepository cropRepository,
                                            FoliarAnalysisRepository foliarAnalysisRepository,
                                            CropFertilizationTableRepository cropFertilizationTableRepository,
                                            ContentRangeRepository contentRangeRepository,
                                            CoverageRepository coverageRepository,
                                            FormulatedMineralFertilizerRepository formulatedMineralFertilizerRepository,
                                            SimpleMineralFertilizerRepository simpleMineralFertilizerRepository,
                                            FertilityAnalysisExtractRepository fertilityAnalysisExtractRepository,
                                            SoilFertilityInterpretationCriteriaTableRepository soilFertilityInterpretationCriteriaTableRepository,
                                            CropFoliarAnalysisInterpretationTableRepository cropFoliarAnalysisInterpretationTableRepository,
                                            CropFoliarAnalysisInterpretationTableLineRepository cropFoliarAnalysisInterpretationTableLineRepository,
                                            DiverseContentRangeRepository diverseContentRangeRepository,
                                            KExchangeableContentRepository kExchangeableContentRepository,
                                            AvailablePMehlich1ExtractorRepository availablePMehlich1ExtractorRepository,
                                            AvailablePAnionExchangeResinExtractorRepository availablePAnionExchangeResinExtractorRepository,
                                            AvailableSRepository availableSRepository,
                                            ExchangeableSodiumRepository exchangeableSodiumRepository,
                                            SalinityInterpretationRepository salinityInterpretationRepository,
                                            OrganicFertilizerRepository organicFertilizerRepository,
                                            OrganoMineralFertilizerRepository organoMineralFertilizerRepository,
                                            GreenFertilizerRepository greenFertilizerRepository,
                                            BioFertilizerRepository bioFertilizerRepository,
                                            MineralFertilizerRepository mineralFertilizerRepository,
                                            ChelatedFertilizerRepository chelatedFertilizerRepository,
                                            CropFertilizationMicronutrientDoseRepository micronutrientDoseRepository) {
        this.physicalAnalysisExtractRepository = physicalAnalysisExtractRepository;
        this.soilAnalysisRepository = soilAnalysisRepository;
        this.saturationExtractAnalysisExtractRepository = saturationExtractAnalysisExtractRepository;
        this.annualCropFolderRepository = annualCropFolderRepository;
        this.cropRepository = cropRepository;
        this.foliarAnalysisRepository = foliarAnalysisRepository;
        this.cropFertilizationTableRepository = cropFertilizationTableRepository;
        this.contentRangeRepository = contentRangeRepository;
        this.coverageRepository = coverageRepository;
        this.formulatedMineralFertilizerRepository = formulatedMineralFertilizerRepository;
        this.simpleMineralFertilizerRepository = simpleMineralFertilizerRepository;
        this.fertilityAnalysisExtractRepository = fertilityAnalysisExtractRepository;
        this.soilFertilityInterpretationCriteriaTableRepository = soilFertilityInterpretationCriteriaTableRepository;
        this.cropFoliarAnalysisInterpretationTableRepository = cropFoliarAnalysisInterpretationTableRepository;
        this.cropFoliarAnalysisInterpretationTableLineRepository = cropFoliarAnalysisInterpretationTableLineRepository;
        this.diverseContentRangeRepository = diverseContentRangeRepository;
        this.kExchangeableContentRepository = kExchangeableContentRepository;
        this.availablePMehlich1ExtractorRepository = availablePMehlich1ExtractorRepository;
        this.availablePAnionExchangeResinExtractorRepository = availablePAnionExchangeResinExtractorRepository;
        this.availableSRepository = availableSRepository;
        this.exchangeableSodiumRepository = exchangeableSodiumRepository;
        this.salinityInterpretationRepository = salinityInterpretationRepository;
        this.organicFertilizerRepository = organicFertilizerRepository;
        this.organoMineralFertilizerRepository = organoMineralFertilizerRepository;
        this.greenFertilizerRepository = greenFertilizerRepository;
        this.bioFertilizerRepository = bioFertilizerRepository;
        this.mineralFertilizerRepository = mineralFertilizerRepository;
        this.chelatedFertilizerRepository = chelatedFertilizerRepository;
        this.micronutrientDoseRepository = micronutrientDoseRepository;
    }

    public RecommendationCalculationResult calculate(RecommendationCreateRequestDto dto, UserModel user, PropertyModel property, PlotModel plot) {
        List<String> diagnostics = new ArrayList<>();
        FertilizerSourceOption sourceOption = dto.getOrigemAdubos() != null ? dto.getOrigemAdubos() : FertilizerSourceOption.BOTH;
        List<String> warnings = new ArrayList<>();

        RecommendationInputs inputs = loadRecommendationInputs(dto, user, plot);
        validateRecommendationInputs(inputs, plot);

        DiagnosesContext diagnoses = buildRecommendationDiagnoses(dto, inputs, user, sourceOption, warnings);
        FertilizationRecommendationContext recommendations = buildFertilizationRecommendations(
                inputs, user, sourceOption, warnings, diagnoses.chemicalDiagnosis(), diagnoses.foliarDiagnosis());

        warnings.add("Valide os parâmetros com engenheiro agrônomo responsável antes de uso operacional.");

        return buildCalculationResult(dto, user, property, plot, diagnostics, warnings, inputs, diagnoses, recommendations);
    }

    private RecommendationInputs loadRecommendationInputs(RecommendationCreateRequestDto dto, UserModel user, PlotModel plot) {
        PhysicalAnalysisExtractModel physicalAnalysis = findPhysicalAnalysisExtractByIdOrThrow(dto.getPhysicalAnalysisExtractId());
        FertilityAnalysisSelection soilFertilitySelection = findSoilFertilitySelectionByIdOrThrow(dto.getSoilFertilityAnalysisId(), plot);
        SoilAnalysisModel soilFertilityAnalysis = soilFertilitySelection.soilAnalysis();
        SaturationExtractAnalysisExtractModel saturationExtractAnalysis = findSaturationExtractAnalysisExtractByIdOrThrow(dto.getSaturationExtractAnalysisExtractId());
        AnnualCropFolderModel annualCropFolder = findAnnualCropFolderByIdOrThrow(dto.getAnnualCropFolderId());
        CropModel crop = findCropByIdOrThrow(dto.getCropId());
        CropFertilizationTableModel cropFertilizationTable = findCropFertilizationTableBySelectionOrThrow(
                dto.getCropFertilizationTableId(), dto.getCropFertilizationTableGroup(), user);
        SoilFertilityInterpretationCriteriaTableModel soilInterpretationTable = findSoilFertilityInterpretationTableBySelectionOrThrow(
                dto.getSoilFertilityInterpretationCriteriaTableId(), dto.getSoilFertilityInterpretationCriteriaTableGroup(), user);
        CropFoliarAnalysisInterpretationTableModel foliarInterpretationTable = findCropFoliarAnalysisInterpretationTableBySelectionOrThrow(
                dto.getCropFoliarAnalysisInterpretationTableId(), dto.getCropFoliarAnalysisInterpretationTableGroup(), user);
        Optional<FoliarAnalysisModel> foliarAnalysis = findLatestFoliarAnalysis(crop);
        Optional<FertilityAnalysisExtractModel> fertilityExtract = soilFertilitySelection.selectedExtract()
                .or(() -> findLatestFertilityExtract(soilFertilityAnalysis));

        return new RecommendationInputs(
                physicalAnalysis, soilFertilityAnalysis, saturationExtractAnalysis, annualCropFolder, crop,
                cropFertilizationTable, soilInterpretationTable, foliarInterpretationTable, foliarAnalysis, fertilityExtract);
    }

    private void validateRecommendationInputs(RecommendationInputs inputs, PlotModel plot) {
        validateSamePlot(resolvePlot(inputs.physicalAnalysis()), plot, "O extrato de análise física selecionado não pertence ao talhão informado.");
        validateSamePlot(inputs.soilFertilityAnalysis().getPlot(), plot, "A análise de fertilidade selecionada não pertence ao talhão informado.");
        validateSamePlot(resolvePlot(inputs.saturationExtractAnalysis()), plot, "O extrato de análise de saturação selecionado não pertence ao talhão informado.");
        validateSamePlot(inputs.annualCropFolder().getPlot(), plot, "A pasta de cultura anual selecionada não pertence ao talhão informado.");
        if (inputs.crop().getFolder() == null || !Objects.equals(inputs.crop().getFolder().getId(), inputs.annualCropFolder().getId())) {
            throw new IllegalArgumentException("A cultura selecionada não pertence à pasta de cultura anual informada.");
        }
    }

    private DiagnosesContext buildRecommendationDiagnoses(RecommendationCreateRequestDto dto,
                                                          RecommendationInputs inputs,
                                                          UserModel user,
                                                          FertilizerSourceOption sourceOption,
                                                          List<String> warnings) {
        PhysicalDiagnosis physicalDiagnosis = buildSoilPhysicalDiagnosis(inputs.physicalAnalysis(), warnings);
        String physicalSummary = physicalDiagnosis.summary();
        String soilFertilitySummary = "Análise de fertilidade considerada na recomendação.";
        String cropSummary = "Cultura considerada conforme cabeçalho do laudo.";
        List<FoliarDiagnosisItem> foliarDiagnosis = buildFoliarDiagnosis(inputs.foliarAnalysis(), inputs.crop(), inputs.foliarInterpretationTable(), warnings);
        String foliarSummary = buildFoliarSummary(inputs.foliarAnalysis(), foliarDiagnosis, warnings);

        List<String> correctionMessages = buildCorrectionMessages(dto, inputs.fertilityExtract(), Optional.of(inputs.saturationExtractAnalysis()), warnings);
        LimingRequirementResult limingRequirement = calculateLimingRequirement(dto, inputs.fertilityExtract(), inputs.physicalAnalysis(), inputs.cropFertilizationTable(), warnings);
        GypsumRequirementResult gypsumRequirement = calculateGypsumRequirement(
                inputs.fertilityExtract(), inputs.physicalAnalysis(), inputs.cropFertilizationTable(), inputs.soilInterpretationTable(), user, sourceOption, warnings);
        List<SoilChemicalDiagnosisItem> chemicalDiagnosis = buildSoilChemicalDiagnosis(
                inputs.fertilityExtract(), inputs.physicalAnalysis(), inputs.soilInterpretationTable(), warnings);
        List<CorrectiveFertilizationRow> correctiveFertilizationRows = buildCorrectiveFertilizationRows(
                chemicalDiagnosis, inputs.cropFertilizationTable(), inputs.soilInterpretationTable(), user, sourceOption, warnings);
        SalinityDiagnosis salinityDiagnosis = buildSalinityAndSodicityDiagnosis(
                inputs.saturationExtractAnalysis(), inputs.fertilityExtract(), inputs.soilInterpretationTable(), warnings);

        return new DiagnosesContext(
                physicalDiagnosis, physicalSummary, soilFertilitySummary, cropSummary, foliarDiagnosis, foliarSummary,
                correctionMessages, limingRequirement, gypsumRequirement, chemicalDiagnosis, correctiveFertilizationRows, salinityDiagnosis);
    }

    private FertilizationRecommendationContext buildFertilizationRecommendations(RecommendationInputs inputs,
                                                                                 UserModel user,
                                                                                 FertilizerSourceOption sourceOption,
                                                                                 List<String> warnings,
                                                                                 List<SoilChemicalDiagnosisItem> chemicalDiagnosis,
                                                                                 List<FoliarDiagnosisItem> foliarDiagnosis) {
        List<FertilizationRecommendationRow> recommendationRows = new ArrayList<>();
        List<FertilizerSuggestion> fertilizerSuggestions = new ArrayList<>();
        List<NutrientBalanceRow> nutrientBalanceRows = List.of();

        CropFertilizationTableModel table = inputs.cropFertilizationTable();
        Optional<ContentRangeModel> nRange = selectNitrogenRange(table);
        Optional<ContentRangeModel> pRange = selectNutrientRange(table, Nutriente.FOSFORO, extractPhosphorusValue(inputs.fertilityExtract()), warnings, "fósforo");
        Optional<ContentRangeModel> kRange = selectNutrientRange(table, Nutriente.POTASSIO, extractPotassiumValue(inputs.fertilityExtract()), warnings, "potássio");

        Double requiredN = nRange.map(ContentRangeModel::getApplication).orElse(null);
        Double requiredP2O5 = pRange.map(ContentRangeModel::getApplication).orElse(null);
        Double requiredK2O = kRange.map(ContentRangeModel::getApplication).orElse(null);
        Long nRangeId = nRange.map(ContentRangeModel::getId).orElse(null);
        Long pRangeId = pRange.map(ContentRangeModel::getId).orElse(null);
        Long kRangeId = kRange.map(ContentRangeModel::getId).orElse(null);

        if (nRange.isEmpty()) warnings.add("Não foi encontrado intervalo para NITROGENIO na tabela selecionada.");
        if (pRange.isEmpty()) warnings.add("Não foi encontrado intervalo para FOSFORO na tabela selecionada.");
        if (kRange.isEmpty()) warnings.add("Não foi encontrado intervalo para POTASSIO na tabela selecionada.");

        FertilizerSelection planting = selectBestPlantingFertilizer(user, sourceOption, requiredN, requiredP2O5, requiredK2O, warnings);
        planting.suggestion().ifPresent(fertilizerSuggestions::add);
        NutrientBalanceAccumulator nutrientBalance = new NutrientBalanceAccumulator(requiredN, requiredP2O5, requiredK2O);
        nutrientBalance.addPlanting(planting.providedN(), planting.providedP2O5(), planting.providedK2O());

        recommendationRows.add(FertilizationRecommendationRow.builder()
                .phase("Plantio")
                .nutrients(String.format("N: %.2f kg/ha, P2O5: %.2f kg/ha, K2O: %.2f kg/ha", nvl(requiredN), nvl(requiredP2O5), nvl(requiredK2O)))
                .suggestedFertilizer(planting.name())
                .fertilizerQuantityKgHa(planting.quantityKgHa())
                .providedN(planting.providedN())
                .providedP2O5(planting.providedP2O5())
                .providedK2O(planting.providedK2O())
                .balanceN(planting.balanceN())
                .balanceP2O5(planting.balanceP2O5())
                .balanceK2O(planting.balanceK2O())
                .limitingNutrient(planting.limitingNutrient())
                .targetNeedKgHa(planting.targetNeedKgHa())
                .productConcentrationPercent(planting.productConcentrationPercent())
                .calculationMemory(planting.calculationMemory())
                .warning(planting.warning())
                .applicationMode("Aplicação no plantio, conforme recomendação técnica.")
                .source("Tabela de adubação de culturas ID " + table.getId())
                .build());

        for (ContentRangeModel selectedRange : List.of(nRange.orElse(null), pRange.orElse(null), kRange.orElse(null))) {
            if (selectedRange != null) recommendationRows.addAll(buildCoverageRows(selectedRange, user, sourceOption, fertilizerSuggestions, nutrientBalance, warnings));
        }
        recommendationRows.add(FertilizationRecommendationRow.builder()
                .phase("Balanço global NPK")
                .nutrients("Consolidado após plantio e coberturas recomendadas")
                .suggestedFertilizer("Não se aplica")
                .applicationMode("Memória de cálculo consolidada em kg/ha.")
                .providedN(nutrientBalance.providedTotalN())
                .providedP2O5(nutrientBalance.providedTotalP2O5())
                .providedK2O(nutrientBalance.providedTotalK2O())
                .balanceN(nutrientBalance.balanceN())
                .balanceP2O5(nutrientBalance.balanceP2O5())
                .balanceK2O(nutrientBalance.balanceK2O())
                .source("Balanço global calculado pelo backend")
                .build());
        nutrientBalanceRows = nutrientBalance.toRows();
        if (nutrientBalanceRows.stream().anyMatch(balance -> balance.getRecommendedCoverageKgHa() > 0d)) {
            warnings.add("Como CoverageModel não possui marcação de parcelamento técnico, coberturas NPK foram calculadas apenas para déficit remanescente após o plantio.");
        }

        List<AlternativeFertilizationRecommendationRow> alternativeFertilizationRows =
                buildOrganicOrganoMineralAndMicronutrientRows(
                        requiredN, requiredP2O5, requiredK2O, chemicalDiagnosis, foliarDiagnosis,
                        table, user, sourceOption, warnings);

        return new FertilizationRecommendationContext(
                recommendationRows, fertilizerSuggestions, nutrientBalanceRows, alternativeFertilizationRows,
                requiredN, requiredP2O5, requiredK2O, nRangeId, pRangeId, kRangeId);
    }

    private RecommendationCalculationResult buildCalculationResult(RecommendationCreateRequestDto dto,
                                                                   UserModel user,
                                                                   PropertyModel property,
                                                                   PlotModel plot,
                                                                   List<String> diagnostics,
                                                                   List<String> warnings,
                                                                   RecommendationInputs inputs,
                                                                   DiagnosesContext diagnoses,
                                                                   FertilizationRecommendationContext recommendations) {
        return RecommendationCalculationResult.builder()
                .requesterName(user != null ? user.getName() : null)
                .requesterUsername(user != null ? user.getUsername() : null)
                .propertyName(property != null ? property.getNome() : null)
                .propertyId(property != null ? property.getId() : null)
                .plotIdentification(plot != null ? plot.getIdentification() : null)
                .plotId(plot != null ? plot.getId() : null)
                .cropName(inputs.crop().getName() != null ? inputs.crop().getName().name() : null)
                .annualCropFolderYear(inputs.annualCropFolder().getCropsYear())
                .recommendationType(dto.getRecommendationType() != null ? dto.getRecommendationType().name() : null)
                .limingCriteria(diagnoses.limingRequirement() != null ? diagnoses.limingRequirement().getSelectedCriteria() : null)
                .issuedAt(LocalDateTime.now())
                .warnings(warnings).diagnosticMessages(diagnostics).correctionMessages(diagnoses.correctionMessages())
                .limingRequirement(diagnoses.limingRequirement())
                .gypsumRequirement(diagnoses.gypsumRequirement())
                .soilChemicalDiagnosis(diagnoses.chemicalDiagnosis())
                .correctiveFertilizationRows(diagnoses.correctiveFertilizationRows())
                .soilPhysicalDiagnosis(diagnoses.physicalDiagnosis().items())
                .soilSalinityDiagnosis(diagnoses.salinityDiagnosis().items())
                .foliarDiagnosis(diagnoses.foliarDiagnosis())
                .fertilizationRows(List.of("Recomendação estruturada em linhas de plantio e cobertura."))
                .fertilizationRecommendationRows(recommendations.recommendationRows()).fertilizerSuggestions(recommendations.fertilizerSuggestions())
                .nutrientBalanceRows(recommendations.nutrientBalanceRows())
                .alternativeFertilizationRows(recommendations.alternativeFertilizationRows())
                .requiredN(recommendations.requiredN()).requiredP2O5(recommendations.requiredP2O5()).requiredK2O(recommendations.requiredK2O())
                .nitrogenRangeId(recommendations.nRangeId()).phosphorusRangeId(recommendations.pRangeId()).potassiumRangeId(recommendations.kRangeId())
                .physicalAnalysisId(inputs.physicalAnalysis().getId())
                .soilFertilityAnalysisId(inputs.soilFertilityAnalysis().getId())
                .saturationExtractAnalysisId(inputs.saturationExtractAnalysis().getId())
                .annualCropFolderId(inputs.annualCropFolder().getId())
                .cropId(inputs.crop().getId()).foliarAnalysisId(inputs.foliarAnalysis().map(FoliarAnalysisModel::getId).orElse(null))
                .physicalAnalysisSummary(diagnoses.physicalSummary()).soilFertilityAnalysisSummary(diagnoses.soilFertilitySummary()).saturationExtractAnalysisSummary(diagnoses.salinityDiagnosis().summary())
                .annualCropFolderSummary("Pasta de cultura anual considerada na recomendação.")
                .cropSummary(diagnoses.cropSummary()).foliarAnalysisSummary(diagnoses.foliarSummary()).build();
    }

    private record RecommendationInputs(
            PhysicalAnalysisExtractModel physicalAnalysis,
            SoilAnalysisModel soilFertilityAnalysis,
            SaturationExtractAnalysisExtractModel saturationExtractAnalysis,
            AnnualCropFolderModel annualCropFolder,
            CropModel crop,
            CropFertilizationTableModel cropFertilizationTable,
            SoilFertilityInterpretationCriteriaTableModel soilInterpretationTable,
            CropFoliarAnalysisInterpretationTableModel foliarInterpretationTable,
            Optional<FoliarAnalysisModel> foliarAnalysis,
            Optional<FertilityAnalysisExtractModel> fertilityExtract) {
    }

    private record DiagnosesContext(
            PhysicalDiagnosis physicalDiagnosis,
            String physicalSummary,
            String soilFertilitySummary,
            String cropSummary,
            List<FoliarDiagnosisItem> foliarDiagnosis,
            String foliarSummary,
            List<String> correctionMessages,
            LimingRequirementResult limingRequirement,
            GypsumRequirementResult gypsumRequirement,
            List<SoilChemicalDiagnosisItem> chemicalDiagnosis,
            List<CorrectiveFertilizationRow> correctiveFertilizationRows,
            SalinityDiagnosis salinityDiagnosis) {
    }

    private record FertilizationRecommendationContext(
            List<FertilizationRecommendationRow> recommendationRows,
            List<FertilizerSuggestion> fertilizerSuggestions,
            List<NutrientBalanceRow> nutrientBalanceRows,
            List<AlternativeFertilizationRecommendationRow> alternativeFertilizationRows,
            Double requiredN,
            Double requiredP2O5,
            Double requiredK2O,
            Long nRangeId,
            Long pRangeId,
            Long kRangeId) {
    }
    private String addMissing(List<String> warnings,String m){warnings.add(m);return m;}
    private double nvl(Double v){return v==null?0d:v;}
    private Optional<ContentRangeModel> selectNitrogenRange(CropFertilizationTableModel t){var l=contentRangeRepository.findAllByTableAndNutrientOrderByOrderAsc(t,Nutriente.NITROGENIO);return l.stream().findFirst();}
    private Optional<ContentRangeModel> selectNutrientRange(CropFertilizationTableModel t,Nutriente n,Optional<Double> value,List<String>w,String label){var ranges=contentRangeRepository.findAllByTableAndNutrientOrderByOrderAsc(t,n);if(ranges.isEmpty())return Optional.empty();if(value.isEmpty()){w.add("Não foi possível classificar teor de "+label+"; primeiro intervalo da tabela foi utilizado.");return Optional.of(ranges.get(0));} double v=value.get(); return ranges.stream().filter(r->(r.getSmallest()==null||v>=r.getSmallest())&&(r.getLargest()==null||v<r.getLargest())).findFirst().or(()->Optional.of(ranges.get(0)));}
    private Optional<FertilityAnalysisExtractModel> findLatestFertilityExtract(SoilAnalysisModel soil){return fertilityAnalysisExtractRepository.findAll().stream().filter(e->(e.getRangeExtract()!=null&&e.getRangeExtract().getAnalysis()!=null&&Objects.equals(e.getRangeExtract().getAnalysis().getId(),soil.getId()))||(e.getLayerExtract()!=null&&e.getLayerExtract().getAnalysis()!=null&&Objects.equals(e.getLayerExtract().getAnalysis().getId(),soil.getId()))).max(Comparator.comparing(FertilityAnalysisExtractModel::getId));}
    private SoilAnalysisModel resolveSoilAnalysis(FertilityAnalysisExtractModel extract) {if (extract.getRangeExtract() != null && extract.getRangeExtract().getAnalysis() != null) return extract.getRangeExtract().getAnalysis(); if (extract.getLayerExtract() != null && extract.getLayerExtract().getAnalysis() != null) return extract.getLayerExtract().getAnalysis(); throw new IllegalArgumentException("Extrato de análise de fertilidade não possui análise de solo associada.");}
    private Optional<Double> extractPhosphorusValue(Optional<FertilityAnalysisExtractModel> e){return e.map(x->x.getFosforoMehlich1()!=null?x.getFosforoMehlich1():x.getFosforoResina());}
    private Optional<Double> extractPotassiumValue(Optional<FertilityAnalysisExtractModel> e){return e.map(FertilityAnalysisExtractModel::getPotassio);}
    private Optional<Double> extractPhValue(Optional<FertilityAnalysisExtractModel> e,Optional<SaturationExtractAnalysisExtractModel>s){if(e.isPresent()){if(e.get().getPhAgua()!=null)return Optional.of(e.get().getPhAgua());if(e.get().getPhCacl2()!=null)return Optional.of(e.get().getPhCacl2());} return s.map(SaturationExtractAnalysisExtractModel::getPh);}
    private Optional<Double> extractAluminumValue(Optional<FertilityAnalysisExtractModel> e){return e.map(FertilityAnalysisExtractModel::getAluminio);}
    private List<String> buildCorrectionMessages(RecommendationCreateRequestDto dto, Optional<FertilityAnalysisExtractModel> fertilityExtract, Optional<SaturationExtractAnalysisExtractModel> saturation, List<String> warnings){List<String> m=new ArrayList<>();Optional<Double> ph=extractPhValue(fertilityExtract,saturation);Optional<Double> al=extractAluminumValue(fertilityExtract); if(ph.isPresent()){double v=ph.get(); if(v<5.5)m.add("pH abaixo de 5.5. Indica necessidade provável de correção de acidez, a confirmar com critério de calagem selecionado."); else if(v<=6.5)m.add("pH em faixa intermediária. Correção deve ser avaliada conforme cultura e saturação por bases."); else m.add("pH elevado. Evitar recomendações automáticas de calagem sem validação técnica.");} if(al.isPresent()&&al.get()>0)m.add("Presença de alumínio trocável detectada. Avaliar neutralização conforme critério selecionado."); if(ph.isEmpty()&&al.isEmpty())warnings.add("Não foi possível calcular correção de acidez/salinidade por ausência de parâmetros suficientes.");return m;}

    private LimingRequirementResult calculateLimingRequirement(RecommendationCreateRequestDto dto,
                                                               Optional<FertilityAnalysisExtractModel> fertilityExtract,
                                                               PhysicalAnalysisExtractModel physicalAnalysis,
                                                               CropFertilizationTableModel cropFertilizationTable,
                                                               List<String> warnings) {
        CriterioCalagem selectedCriteria = dto.getLimingCriteria() != null
                ? dto.getLimingCriteria()
                : cropFertilizationTable.getCriteria();
        String formula = "NC teórica (t/ha) = T * (V2 - V1) / 100; NC corrigida (t/ha) = NC teórica * 100 / PRNT";
        Map<String, Double> inputValues = new LinkedHashMap<>();
        List<String> limingWarnings = new ArrayList<>();

        if (selectedCriteria == null) {
            limingWarnings.add("Critério de calagem não informado na recomendação nem na tabela de adubação selecionada.");
            warnings.addAll(limingWarnings);
            return LimingRequirementResult.builder()
                    .selectedCriteria(null)
                    .formula(formula)
                    .inputValues(inputValues)
                    .unit("t/ha")
                    .warnings(limingWarnings)
                    .build();
        }

        if (selectedCriteria == CriterioCalagem.NEUTRALIZACAO_POR_ALUMINIO_TROCAVEL) {
            return calculateLimingByExchangeableAluminum(selectedCriteria, fertilityExtract, physicalAnalysis, warnings, inputValues, limingWarnings);
        }

        if (selectedCriteria != CriterioCalagem.SATURACAO_POR_BASES_TROCAVEIS) {
            limingWarnings.add("Cálculo de calagem pelo critério " + selectedCriteria.name()
                    + " não possui fórmula completa suportada pelos modelos atuais; somente V% e neutralização de Al trocável foram avaliados.");
            warnings.addAll(limingWarnings);
            return LimingRequirementResult.builder()
                    .selectedCriteria(selectedCriteria.name())
                    .formula("Não calculada: critério sem parametrização completa no backend.")
                    .inputValues(inputValues)
                    .unit("t/ha")
                    .warnings(limingWarnings)
                    .build();
        }

        FertilityAnalysisExtractModel fertility = fertilityExtract.orElse(null);
        Double currentBaseSaturation = fertility != null ? fertility.getSaturacaoBasesV() : null;
        Double ctcPh7 = fertility != null ? fertility.getCtcPh7() : null;
        Double targetBaseSaturation = null;
        Double prnt = null;
        inputValues.put("V atual (%)", currentBaseSaturation);
        inputValues.put("V desejado (%)", targetBaseSaturation);
        inputValues.put("CTC pH 7,0 - T (mmolc/dm³)", ctcPh7);
        inputValues.put("PRNT (%)", prnt);

        if (currentBaseSaturation == null) limingWarnings.add("V atual ausente no extrato de fertilidade (saturacaoBasesV).");
        if (ctcPh7 == null) limingWarnings.add("CTC pH 7,0 ausente no extrato de fertilidade (ctcPh7).");
        if (targetBaseSaturation == null) limingWarnings.add("V desejado não está modelado nos DTOs, entidades ou tabelas técnicas inspecionadas.");
        if (prnt == null) limingWarnings.add("PRNT do calcário não está modelado nos DTOs, entidades ou tabelas técnicas inspecionadas.");

        Double theoreticalRequirement = null;
        if (currentBaseSaturation != null && ctcPh7 != null && targetBaseSaturation != null) {
            theoreticalRequirement = Math.max(0d, round2(ctcPh7 * (targetBaseSaturation - currentBaseSaturation) / 100d));
        }

        PrntAdjustment prntAdjustment = applyPrntAdjustment(theoreticalRequirement, prnt, limingWarnings);
        if (theoreticalRequirement != null && prntAdjustment.correctedRequirement() == null) {
            limingWarnings.add("Dose teórica calculada, mas dose corrigida por PRNT não foi calculada porque o PRNT não está disponível.");
        }
        warnings.addAll(limingWarnings);
        return LimingRequirementResult.builder()
                .selectedCriteria(selectedCriteria.name())
                .formula(formula)
                .inputValues(inputValues)
                .theoreticalRequirement(theoreticalRequirement)
                .prnt(prnt)
                .correctedRequirement(prntAdjustment.correctedRequirement())
                .calculatedRequirement(prntAdjustment.effectiveRequirement(theoreticalRequirement))
                .limestoneSource("Não informada: fonte de calcário/corretivo não está modelada no backend atual.")
                .unit("t/ha")
                .warnings(limingWarnings)
                .build();
    }

    private LimingRequirementResult calculateLimingByExchangeableAluminum(CriterioCalagem selectedCriteria,
                                                                          Optional<FertilityAnalysisExtractModel> fertilityExtract,
                                                                          PhysicalAnalysisExtractModel physicalAnalysis,
                                                                          List<String> warnings,
                                                                          Map<String, Double> inputValues,
                                                                          List<String> limingWarnings) {
        String formula = "NC teórica (t/ha) = fator de calagem por argila * Al trocável; NC corrigida (t/ha) = NC teórica * 100 / PRNT";
        FertilityAnalysisExtractModel fertility = fertilityExtract.orElse(null);
        Double exchangeableAluminum = fertility != null ? fertility.getAluminio() : null;
        Double clayContent = physicalAnalysis != null ? physicalAnalysis.getTeorArgila() : null;
        Double factor = clayContent != null ? limingFactorByClayContent(clayContent) : null;
        Double prnt = null;

        inputValues.put("Al trocável (mmolc/dm³)", exchangeableAluminum);
        inputValues.put("Argila (g/dm3)", clayContent);
        inputValues.put("Fator de calagem por argila", factor);
        inputValues.put("PRNT (%)", prnt);

        if (exchangeableAluminum == null) {
            limingWarnings.add("Al trocável ausente no extrato de fertilidade (aluminio).");
        }
        if (clayContent == null) {
            limingWarnings.add("Teor de argila ausente no extrato de análise física (teorArgila), necessário para selecionar o fator de calagem.");
        }

        Double theoreticalRequirement = null;
        if (exchangeableAluminum != null && factor != null) {
            theoreticalRequirement = Math.max(0d, round2(factor * exchangeableAluminum));
        }

        if (prnt == null) {
            limingWarnings.add("PRNT do calcário não está modelado nos DTOs, entidades ou tabelas técnicas inspecionadas.");
        }
        PrntAdjustment prntAdjustment = applyPrntAdjustment(theoreticalRequirement, prnt, limingWarnings);
        if (theoreticalRequirement != null && prntAdjustment.correctedRequirement() == null) {
            limingWarnings.add("Dose teórica calculada, mas dose corrigida por PRNT não foi calculada porque o PRNT não está disponível.");
        }
        warnings.addAll(limingWarnings);
        return LimingRequirementResult.builder()
                .selectedCriteria(selectedCriteria.name())
                .formula(formula)
                .inputValues(inputValues)
                .theoreticalRequirement(theoreticalRequirement)
                .prnt(prnt)
                .correctedRequirement(prntAdjustment.correctedRequirement())
                .calculatedRequirement(prntAdjustment.effectiveRequirement(theoreticalRequirement))
                .limestoneSource("Não informada: fonte de calcário/corretivo não está modelada no backend atual.")
                .unit("t/ha")
                .warnings(limingWarnings)
                .build();
    }

    private PrntAdjustment applyPrntAdjustment(Double theoreticalRequirement, Double prnt, List<String> limingWarnings) {
        if (theoreticalRequirement == null) return new PrntAdjustment(null);
        if (prnt == null) return new PrntAdjustment(null);
        if (prnt <= 0d) {
            limingWarnings.add("PRNT informado inválido; a correção exige PRNT maior que zero.");
            return new PrntAdjustment(null);
        }
        return new PrntAdjustment(Math.max(0d, round2(theoreticalRequirement * 100d / prnt)));
    }

    private record PrntAdjustment(Double correctedRequirement) {
        Double effectiveRequirement(Double theoreticalRequirement) {
            return correctedRequirement != null ? correctedRequirement : theoreticalRequirement;
        }
    }

    private double limingFactorByClayContent(Double clayContent) {
        double clay = nvl(clayContent);
        if (clay < 150.0) return 1.5;
        if (clay <= 350.0) return 2.0;
        return 2.5;
    }

    private GypsumRequirementResult calculateGypsumRequirement(Optional<FertilityAnalysisExtractModel> fertilityExtract,
                                                               PhysicalAnalysisExtractModel physicalAnalysis,
                                                               CropFertilizationTableModel cropFertilizationTable,
                                                               SoilFertilityInterpretationCriteriaTableModel soilInterpretationTable,
                                                               UserModel user,
                                                               FertilizerSourceOption sourceOption,
                                                               List<String> warnings) {
        String criterion = "Faixas diversas da tabela de interpretação para cálcio, alumínio e saturação por alumínio; dose parametrizada em SUGESTAO_GESSAGEM da tabela de adubação.";
        Map<String, Double> inputValues = new LinkedHashMap<>();
        List<String> gypsumWarnings = new ArrayList<>();
        FertilityAnalysisExtractModel fertility = fertilityExtract.orElse(null);

        if (fertility == null) {
            gypsumWarnings.add("Nenhum extrato de fertilidade foi encontrado para avaliar necessidade de gessagem.");
            warnings.addAll(gypsumWarnings);
            return GypsumRequirementResult.builder()
                    .needed(null)
                    .criterion(criterion)
                    .inputValues(inputValues)
                    .unit("t/ha")
                    .justification("Gessagem não avaliada por ausência de extrato de fertilidade.")
                    .warnings(gypsumWarnings)
                    .build();
        }

        inputValues.put("Cálcio (mmolc/dm³)", fertility.getCalcio());
        inputValues.put("Alumínio (mmolc/dm³)", fertility.getAluminio());
        inputValues.put("Saturação por alumínio (%)", fertility.getSaturacaoAluminioM());
        inputValues.put("CTC efetiva (mmolc/dm³)", fertility.getCtcEfetiva());
        inputValues.put("CTC pH 7,0 (mmolc/dm³)", fertility.getCtcPh7());
        inputValues.put("Argila (g/dm3)", physicalAnalysis != null ? physicalAnalysis.getTeorArgila() : null);
        inputValues.put("Enxofre (mg/dm3)", fertility.getEnxofre());
        inputValues.put("Profundidade inicial do extrato de fertilidade (cm)", extractInitialDepth(fertility));
        inputValues.put("Profundidade final do extrato de fertilidade (cm)", extractFinalDepth(fertility));
        inputValues.put("Sugestão de gessagem da tabela (t/ha)", cropFertilizationTable != null ? cropFertilizationTable.getGessing() : null);

        if (extractInitialDepth(fertility) == null || extractFinalDepth(fertility) == null) {
            gypsumWarnings.add("Profundidade do extrato de fertilidade não disponível; o backend não inferiu camada para gessagem.");
        }

        Optional<DiverseContentRangeModel> diverseRange = diverseContentRangeRepository.findByTable(soilInterpretationTable);
        if (diverseRange.isEmpty()) {
            gypsumWarnings.add("Não há faixas diversas cadastradas para avaliar cálcio, alumínio e saturação por alumínio na gessagem.");
            warnings.addAll(gypsumWarnings);
            return GypsumRequirementResult.builder()
                    .needed(null)
                    .criterion(criterion)
                    .inputValues(inputValues)
                    .unit("t/ha")
                    .justification("Gessagem não calculada por ausência de critério técnico cadastrado para os indicadores disponíveis.")
                    .warnings(gypsumWarnings)
                    .build();
        }

        SoilChemicalDiagnosisItem calcium = classifyDiverseRange("Cálcio", fertility.getCalcio(), "mmolc/dm³", diverseRange,
                r -> new RangeCriterion(r.getCalcium_too_low(), r.getCalcium_low_i(), r.getCalcium_low_f(), r.getCalcium_medium_i(), r.getCalcium_medium_f(), r.getCalcium_hight_i(), r.getCalcium_hight_f(), r.getCalcium_too_hight()),
                "Cálcio usado como indicador para necessidade de gessagem.");
        SoilChemicalDiagnosisItem aluminum = classifyDiverseRange("Alumínio", fertility.getAluminio(), "mmolc/dm³", diverseRange,
                r -> new RangeCriterion(r.getAluminum_too_low(), r.getAluminum_low_i(), r.getAluminum_low_f(), r.getAluminum_medium_i(), r.getAluminum_medium_f(), r.getAluminum_hight_i(), r.getAluminum_hight_f(), r.getAluminum_too_hight()),
                "Alumínio usado como indicador para necessidade de gessagem.");
        SoilChemicalDiagnosisItem aluminumSaturation = classifyDiverseRange("Saturação por alumínio", fertility.getSaturacaoAluminioM(), "%", diverseRange,
                r -> new RangeCriterion(r.getAluminum_saturation_too_low(), r.getAluminum_saturation_low_i(), r.getAluminum_saturation_low_f(), r.getAluminum_saturation_medium_i(), r.getAluminum_saturation_medium_f(), r.getAluminum_saturation_hight_i(), r.getAluminum_saturation_hight_f(), r.getAluminum_saturation_too_hight()),
                "Saturação por alumínio usada como indicador para necessidade de gessagem.");

        boolean calciumIndicatesNeed = isInterpretation(calcium, "Muito baixo", "Baixo");
        boolean aluminumIndicatesNeed = isInterpretation(aluminum, "Alto", "Muito alto");
        boolean aluminumSaturationIndicatesNeed = isInterpretation(aluminumSaturation, "Alto", "Muito alto");
        boolean hasClassifiedIndicator = calcium.getInterpretation() != null
                || aluminum.getInterpretation() != null
                || aluminumSaturation.getInterpretation() != null;

        if (!hasClassifiedIndicator) {
            gypsumWarnings.add("Dados ou critérios insuficientes para classificar cálcio, alumínio ou saturação por alumínio.");
            warnings.addAll(gypsumWarnings);
            return GypsumRequirementResult.builder()
                    .needed(null)
                    .criterion(criterion)
                    .inputValues(inputValues)
                    .unit("t/ha")
                    .justification("Gessagem não calculada porque nenhum indicador pôde ser classificado com os dados e critérios cadastrados.")
                    .warnings(gypsumWarnings)
                    .build();
        }

        boolean needed = calciumIndicatesNeed || aluminumIndicatesNeed || aluminumSaturationIndicatesNeed;
        Double tableDose = cropFertilizationTable != null ? cropFertilizationTable.getGessing() : null;
        Double dose = needed && tableDose != null ? Math.max(0d, round2(tableDose)) : needed ? null : 0d;
        if (needed && tableDose == null) {
            gypsumWarnings.add("A necessidade foi indicada pelos critérios disponíveis, mas a tabela de adubação não possui sugestão de gessagem preenchida.");
        }
        if (needed) {
            gypsumWarnings.add("A dose usa o campo SUGESTAO_GESSAGEM da tabela de adubação; não há fórmula quantitativa de gessagem por argila/profundidade modelada no backend atual.");
        }

        String justification = needed
                ? "Gessagem indicada por pelo menos um indicador crítico: Ca=" + safeInterpretation(calcium)
                + ", Al=" + safeInterpretation(aluminum)
                + ", m%=" + safeInterpretation(aluminumSaturation) + "."
                : "Gessagem não indicada pelos indicadores classificados: Ca=" + safeInterpretation(calcium)
                + ", Al=" + safeInterpretation(aluminum)
                + ", m%=" + safeInterpretation(aluminumSaturation) + ".";

        GypsumSourceSelection sourceSelection = selectGypsumSource(user, sourceOption, dose, gypsumWarnings);

        warnings.addAll(gypsumWarnings);
        return GypsumRequirementResult.builder()
                .needed(needed)
                .criterion(criterion)
                .inputValues(inputValues)
                .calculatedRequirement(dose)
                .unit("t/ha")
                .sourceName(sourceSelection.sourceName())
                .sourceType(sourceSelection.sourceType())
                .commercialDose(sourceSelection.commercialDose())
                .commercialDoseUnit(sourceSelection.commercialDoseUnit())
                .sourceJustification(sourceSelection.justification())
                .sourceLimitations(sourceSelection.limitations())
                .justification(justification)
                .warnings(gypsumWarnings)
                .build();
    }

    private GypsumSourceSelection selectGypsumSource(UserModel user,
                                                     FertilizerSourceOption sourceOption,
                                                     Double calculatedDose,
                                                     List<String> gypsumWarnings) {
        if (calculatedDose == null) {
            String limitation = "Fonte comercial de gesso agrícola não selecionada porque a dose de gessagem não foi calculada.";
            gypsumWarnings.add(limitation);
            return new GypsumSourceSelection(null, null, null, null,
                    "Seleção de fonte não realizada por ausência de dose calculada.", limitation);
        }
        if (calculatedDose <= 0d) {
            return new GypsumSourceSelection(null, null, 0d, "t/ha",
                    "Gessagem não indicada; não há fonte comercial a aplicar.", "Sem limitação adicional para fonte comercial.");
        }

        Optional<SimpleMineralFertilizerModel> gypsumSource = selectSimpleFertilizers(user, sourceOption).stream()
                .filter(this::isGypsumProduct)
                .max(Comparator.comparing((SimpleMineralFertilizerModel f) -> nvl(f.getCa()) + nvl(f.getS()))
                        .thenComparing(f -> f.getId() == null ? 0L : f.getId()));

        if (gypsumSource.isEmpty()) {
            String limitation = "Não há produto de gesso agrícola cadastrado nos adubos minerais simples acessíveis pela origem selecionada; a dose calculada foi mantida sem fonte comercial.";
            gypsumWarnings.add(limitation);
            return new GypsumSourceSelection(null, null, calculatedDose, "t/ha",
                    "Dose mantida como gesso agrícola calculado pela tabela, sem produto comercial selecionado.", limitation);
        }

        SimpleMineralFertilizerModel source = gypsumSource.get();
        String justification = String.format(Locale.US,
                "Produto cadastrado como adubo mineral simples com nome compatível com gesso agrícola e composição Ca %.2f%% / S %.2f%%.",
                nvl(source.getCa()), nvl(source.getS()));
        String limitation = "O modelo do produto não possui teor de pureza de gesso agrícola; portanto a dose comercial foi mantida igual à dose de gesso calculada.";
        gypsumWarnings.add(limitation);
        return new GypsumSourceSelection(source.getName(), "SIMPLES", calculatedDose, "t/ha", justification, limitation);
    }

    private boolean isGypsumProduct(SimpleMineralFertilizerModel fertilizer) {
        if (fertilizer == null || fertilizer.getName() == null) return false;
        String name = normalizeText(fertilizer.getName());
        boolean nameMatches = name.contains("gesso") || name.contains("gypsum") || name.contains("sulfato de calcio");
        return nameMatches && (nvl(fertilizer.getCa()) > 0d || nvl(fertilizer.getS()) > 0d);
    }

    private String normalizeText(String value) {
        if (value == null) return "";
        return java.text.Normalizer.normalize(value, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }

    private Double extractInitialDepth(FertilityAnalysisExtractModel fertility) {
        if (fertility == null) return null;
        if (fertility.getRangeExtract() != null) return doubleFromInteger(fertility.getRangeExtract().getProfundidade_inicial());
        if (fertility.getLayerExtract() != null) return doubleFromInteger(fertility.getLayerExtract().getProfundidade_inicial());
        return null;
    }

    private Double extractFinalDepth(FertilityAnalysisExtractModel fertility) {
        if (fertility == null) return null;
        if (fertility.getRangeExtract() != null) return doubleFromInteger(fertility.getRangeExtract().getProfundidade_final());
        if (fertility.getLayerExtract() != null) return doubleFromInteger(fertility.getLayerExtract().getProfundidade_final());
        return null;
    }

    private Double doubleFromInteger(Integer value) {
        return value == null ? null : value.doubleValue();
    }

    private boolean isInterpretation(SoilChemicalDiagnosisItem item, String... expected) {
        if (item == null || item.getInterpretation() == null) return false;
        return Arrays.asList(expected).contains(item.getInterpretation());
    }

    private String safeInterpretation(SoilChemicalDiagnosisItem item) {
        return item == null || item.getInterpretation() == null ? "não classificado" : item.getInterpretation();
    }

    private List<CorrectiveFertilizationRow> buildCorrectiveFertilizationRows(List<SoilChemicalDiagnosisItem> chemicalDiagnosis,
                                                                              CropFertilizationTableModel cropFertilizationTable,
                                                                              SoilFertilityInterpretationCriteriaTableModel soilInterpretationTable,
                                                                              UserModel user,
                                                                              FertilizerSourceOption sourceOption,
                                                                              List<String> warnings) {
        List<CorrectiveFertilizationRow> rows = new ArrayList<>();
        Map<String, SoilChemicalDiagnosisItem> byAttribute = new LinkedHashMap<>();
        if (chemicalDiagnosis != null) {
            for (SoilChemicalDiagnosisItem item : chemicalDiagnosis) {
                if (item != null && item.getAttribute() != null) {
                    byAttribute.put(normalizeText(item.getAttribute()), item);
                }
            }
        }

        addCorrectiveRowIfRelevant(rows, "Fósforo corretivo", findFirstDiagnosis(byAttribute, "fosforo"),
                "P2O5", cropFertilizationTable, soilInterpretationTable, user, sourceOption, warnings);
        addCorrectiveRowIfRelevant(rows, "Potássio corretivo", findFirstDiagnosis(byAttribute, "potassio"),
                "K2O", cropFertilizationTable, soilInterpretationTable, user, sourceOption, warnings);
        addCorrectiveRowIfRelevant(rows, "Enxofre corretivo", findFirstDiagnosis(byAttribute, "enxofre"),
                "S", cropFertilizationTable, soilInterpretationTable, user, sourceOption, warnings);

        if (rows.isEmpty()) {
            rows.add(CorrectiveFertilizationRow.builder()
                    .correctedAttribute("P/K/S corretivos")
                    .need("Não avaliada")
                    .suggestedSource("Não sugerida")
                    .dose(null)
                    .doseUnit("kg/ha")
                    .calculationMemory("Não há diagnóstico classificável de fósforo, potássio ou enxofre com os dados e critérios disponíveis.")
                    .technicalWarning("Adubação corretiva não foi calculada por ausência de diagnóstico classificável e/ou critério quantitativo corretivo separado.")
                    .build());
        }
        return rows;
    }

    private SoilChemicalDiagnosisItem findFirstDiagnosis(Map<String, SoilChemicalDiagnosisItem> byAttribute, String token) {
        return byAttribute.entrySet().stream()
                .filter(entry -> entry.getKey().contains(token))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    private void addCorrectiveRowIfRelevant(List<CorrectiveFertilizationRow> rows,
                                            String correctedAttribute,
                                            SoilChemicalDiagnosisItem diagnosis,
                                            String nutrientTarget,
                                            CropFertilizationTableModel cropFertilizationTable,
                                            SoilFertilityInterpretationCriteriaTableModel soilInterpretationTable,
                                            UserModel user,
                                            FertilizerSourceOption sourceOption,
                                            List<String> warnings) {
        if (diagnosis == null || diagnosis.getInterpretation() == null) return;

        boolean deficiency = isInterpretation(diagnosis, "Muito baixo", "Baixo");
        SimpleMineralFertilizerModel source = deficiency ? selectCorrectiveSource(user, sourceOption, nutrientTarget) : null;
        String need = deficiency
                ? "Indicada tecnicamente para avaliação: " + diagnosis.getInterpretation()
                : "Não indicada automaticamente: " + diagnosis.getInterpretation();
        String warning = "Não há dose corretiva separada modelada para " + correctedAttribute
                + "; a tabela de adubação possui doses de plantio/cobertura, mas não curva ou coeficiente corretivo independente.";
        warnings.add(warning);

        String sourceName = source != null && deficiency ? source.getName() : "Não sugerida automaticamente";
        String sourceDetail = source != null && deficiency
                ? "Fonte mineral simples compatível encontrada (" + source.getName() + "), sem dose calculada por ausência de critério quantitativo corretivo."
                : "Fonte não selecionada porque a dose corretiva não foi calculada.";

        rows.add(CorrectiveFertilizationRow.builder()
                .correctedAttribute(correctedAttribute)
                .need(need)
                .suggestedSource(sourceName)
                .dose(null)
                .doseUnit("kg/ha")
                .calculationMemory("Diagnóstico: " + diagnosis.getAttribute()
                        + " = " + formatNumber(diagnosis.getAnalyzedValue()) + " " + (diagnosis.getUnit() != null ? diagnosis.getUnit() : "")
                        + "; interpretação: " + diagnosis.getInterpretation()
                        + "; critério usado: " + (diagnosis.getUsedCriterion() != null ? diagnosis.getUsedCriterion() : "não informado")
                        + ". Tabela de adubação ID " + (cropFertilizationTable != null ? cropFertilizationTable.getId() : null)
                        + " e tabela de interpretação ID " + (soilInterpretationTable != null ? soilInterpretationTable.getId() : null)
                        + " não possuem dose corretiva independente para " + nutrientTarget + ".")
                .technicalWarning(sourceDetail + " " + warning)
                .build());
    }

    private SimpleMineralFertilizerModel selectCorrectiveSource(UserModel user, FertilizerSourceOption sourceOption, String nutrientTarget) {
        List<SimpleMineralFertilizerModel> fertilizers = selectSimpleFertilizers(user, sourceOption);
        if (fertilizers == null) return null;
        return fertilizers.stream()
                .filter(f -> correctiveSourcePercentage(f, nutrientTarget) > 0d)
                .max(Comparator.comparing((SimpleMineralFertilizerModel f) -> correctiveSourcePercentage(f, nutrientTarget))
                        .thenComparing(f -> f.getId() == null ? 0L : f.getId()))
                .orElse(null);
    }

    private double correctiveSourcePercentage(SimpleMineralFertilizerModel fertilizer, String nutrientTarget) {
        if (fertilizer == null || nutrientTarget == null) return 0d;
        return switch (nutrientTarget) {
            case "P2O5" -> nvl(fertilizer.getP2O5());
            case "K2O" -> nvl(fertilizer.getK2O());
            case "S" -> nvl(fertilizer.getS());
            default -> 0d;
        };
    }

    private List<AlternativeFertilizationRecommendationRow> buildOrganicOrganoMineralAndMicronutrientRows(
            Double requiredN,
            Double requiredP2O5,
            Double requiredK2O,
            List<SoilChemicalDiagnosisItem> chemicalDiagnosis,
            List<FoliarDiagnosisItem> foliarDiagnosis,
            CropFertilizationTableModel cropFertilizationTable,
            UserModel user,
            FertilizerSourceOption sourceOption,
            List<String> warnings) {
        List<AlternativeFertilizationRecommendationRow> rows = new ArrayList<>();
        addNpkAlternativeRow(rows, "ORGÂNICA", selectBestOrganicSource(user, sourceOption),
                requiredN, requiredP2O5, requiredK2O, warnings);
        addNpkAlternativeRow(rows, "ORGANOMINERAL", selectBestOrganoMineralSource(user, sourceOption),
                requiredN, requiredP2O5, requiredK2O, warnings);
        addGreenFertilizerLimitation(rows, user, sourceOption, warnings);
        addBiofertilizerLimitation(rows, user, sourceOption, warnings);
        rows.addAll(buildMicronutrientRows(chemicalDiagnosis, foliarDiagnosis, cropFertilizationTable, user, sourceOption, warnings));
        return rows;
    }

    private void addNpkAlternativeRow(List<AlternativeFertilizationRecommendationRow> rows,
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
            rows.add(AlternativeFertilizationRecommendationRow.builder()
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
            rows.add(AlternativeFertilizationRecommendationRow.builder()
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
        rows.add(AlternativeFertilizationRecommendationRow.builder()
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

    private void addGreenFertilizerLimitation(List<AlternativeFertilizationRecommendationRow> rows,
                                              UserModel user,
                                              FertilizerSourceOption sourceOption,
                                              List<String> warnings) {
        List<GreenFertilizerModel> sources = selectGreenFertilizers(user, sourceOption);
        if (sources.isEmpty()) {
            String limitation = "Não há adubo verde acessível para a origem de adubos selecionada.";
            warnings.add(limitation);
            rows.add(limitationRow("ADUBO VERDE", "Matéria orgânica/NPK", limitation));
            return;
        }
        GreenFertilizerModel source = sources.stream()
                .max(Comparator.comparing((GreenFertilizerModel f) -> nvl(f.getC()) + nvl(f.getN()) + nvl(f.getP2O5()) + nvl(f.getK2O()))
                        .thenComparing(f -> f.getId() == null ? 0L : f.getId()))
                .orElse(null);
        String limitation = "Adubo verde possui composição cadastrada, mas o backend não possui biomassa produzida/incorporada, matéria seca, época de manejo ou eficiência de liberação para converter em dose.";
        warnings.add(limitation);
        rows.add(AlternativeFertilizationRecommendationRow.builder()
                .sourceType("ADUBO VERDE")
                .nutrientOrObjective("Matéria orgânica/NPK")
                .sourceName(source != null ? source.getName() : "Não selecionada")
                .dose("Não calculada")
                .unit("kg/ha")
                .justification("Fonte apresentada como opção cadastrada, sem recomendação quantitativa automática.")
                .limitations(limitation)
                .build());
    }

    private void addBiofertilizerLimitation(List<AlternativeFertilizationRecommendationRow> rows,
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
        rows.add(AlternativeFertilizationRecommendationRow.builder()
                .sourceType("BIOFERTILIZANTE")
                .nutrientOrObjective("Nutrição foliar/estímulo fisiológico")
                .sourceName(source != null ? source.getName() : "Não selecionada")
                .dose("Não calculada")
                .unit("L/ha ou kg/ha")
                .justification("Fonte apresentada como opção cadastrada, sem recomendação quantitativa automática.")
                .limitations(limitation)
                .build());
    }

    private AlternativeFertilizationRecommendationRow limitationRow(String sourceType, String objective, String limitation) {
        return AlternativeFertilizationRecommendationRow.builder()
                .sourceType(sourceType)
                .nutrientOrObjective(objective)
                .sourceName("Não selecionada")
                .dose("Não calculada")
                .unit("Não modelada")
                .justification("Recomendação não gerada por ausência de dados suportados.")
                .limitations(limitation)
                .build();
    }

    private List<AlternativeFertilizationRecommendationRow> buildMicronutrientRows(
            List<SoilChemicalDiagnosisItem> chemicalDiagnosis,
            List<FoliarDiagnosisItem> foliarDiagnosis,
            CropFertilizationTableModel cropFertilizationTable,
            UserModel user,
            FertilizerSourceOption sourceOption,
            List<String> warnings) {
        List<AlternativeFertilizationRecommendationRow> rows = new ArrayList<>();
        List<CropFertilizationMicronutrientDoseModel> doses = cropFertilizationTable == null
                ? List.of()
                : micronutrientDoseRepository.findAllByTableOrderByMicronutrientAsc(cropFertilizationTable);
        if (doses.isEmpty()) {
            String limitation = "Tabela de adubação da cultura não possui doses relacionais de micronutrientes cadastradas.";
            warnings.add(limitation);
            rows.add(limitationRow("MICRONUTRIENTE", "Correção de deficiência", limitation));
            return rows;
        }

        Map<AppliedMicronutrient, String> deficiencyEvidence = micronutrientDeficiencyEvidence(chemicalDiagnosis, foliarDiagnosis);
        boolean recommendedAny = false;
        for (CropFertilizationMicronutrientDoseModel dose : doses) {
            AppliedMicronutrient micronutrient = dose.getMicronutrient();
            String evidence = deficiencyEvidence.get(micronutrient);
            if (evidence == null) continue;
            recommendedAny = true;
            MicronutrientSourceSelection source = selectMicronutrientSource(user, sourceOption, micronutrient);
            String limitation = "A unidade da dose de micronutriente não está modelada na tabela; o laudo preserva a faixa cadastrada e não converte para dose comercial do produto.";
            if (source.sourceName() == null) {
                limitation += " Não foi encontrada fonte mineral/quelatada acessível com teor cadastrado para " + micronutrient + ".";
            }
            warnings.add(limitation);
            rows.add(AlternativeFertilizationRecommendationRow.builder()
                    .sourceType(source.sourceType() != null ? source.sourceType() : "MICRONUTRIENTE")
                    .nutrientOrObjective(micronutrient.name())
                    .sourceName(source.sourceName() != null ? source.sourceName() : "Não selecionada")
                    .dose(formatDoseRange(dose.getMinimumDose(), dose.getMaximumDose()))
                    .unit("unidade cadastrada na tabela")
                    .justification("Recomendação acionada por " + evidence + "; faixa vinda da tabela de adubação da cultura.")
                    .limitations(limitation)
                    .build());
        }

        if (!recommendedAny) {
            rows.add(AlternativeFertilizationRecommendationRow.builder()
                    .sourceType("MICRONUTRIENTE")
                    .nutrientOrObjective("B/Cu/Fe/Mn/Mo/Zn")
                    .sourceName("Não selecionada")
                    .dose("Não calculada")
                    .unit("unidade cadastrada na tabela")
                    .justification("Não houve deficiência de micronutriente classificada pelos diagnósticos químico ou foliar disponíveis.")
                    .limitations("Doses cadastradas só são usadas quando há deficiência diagnosticada; não foi gerada recomendação preventiva genérica.")
                    .build());
        }
        return rows;
    }

    private Map<AppliedMicronutrient, String> micronutrientDeficiencyEvidence(List<SoilChemicalDiagnosisItem> chemicalDiagnosis,
                                                                              List<FoliarDiagnosisItem> foliarDiagnosis) {
        Map<AppliedMicronutrient, String> evidence = new LinkedHashMap<>();
        if (chemicalDiagnosis != null) {
            for (SoilChemicalDiagnosisItem item : chemicalDiagnosis) {
                if (item == null || !isInterpretation(item, "Muito baixo", "Baixo")) continue;
                AppliedMicronutrient micronutrient = micronutrientFromText(item.getAttribute());
                if (micronutrient != null) {
                    evidence.putIfAbsent(micronutrient, "diagnóstico químico do solo (" + item.getAttribute() + " " + item.getInterpretation() + ")");
                }
            }
        }
        if (foliarDiagnosis != null) {
            for (FoliarDiagnosisItem item : foliarDiagnosis) {
                if (item == null || !"Deficiência".equals(item.getInterpretation())) continue;
                AppliedMicronutrient micronutrient = micronutrientFromText(item.getNutrient());
                if (micronutrient != null) {
                    evidence.putIfAbsent(micronutrient, "diagnóstico foliar (" + item.getNutrient() + " em deficiência)");
                }
            }
        }
        return evidence;
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

    private String formatDoseRange(Double minimum, Double maximum) {
        if (minimum == null && maximum == null) return "Não calculada";
        if (minimum != null && maximum != null) return formatNumber(minimum) + " a " + formatNumber(maximum);
        return formatNumber(minimum != null ? minimum : maximum);
    }

    private PhysicalDiagnosis buildSoilPhysicalDiagnosis(PhysicalAnalysisExtractModel physicalAnalysis, List<String> warnings) {
        List<SoilPhysicalDiagnosisItem> diagnosis = new ArrayList<>();
        if (physicalAnalysis == null) {
            String message = "Análise física não disponível para diagnóstico físico do solo.";
            warnings.add(message);
            return new PhysicalDiagnosis(message, diagnosis);
        }

        addPhysicalItem(diagnosis, "Areia", physicalAnalysis.getTeorAreia(), "g/dm3",
                "Teor usado apenas como descrição física; o sistema não possui critério textural modelado para classificar a textura.");
        addPhysicalItem(diagnosis, "Silte", physicalAnalysis.getTeorSilte(), "g/dm3",
                "Teor usado apenas como descrição física; o sistema não possui critério textural modelado para classificar a textura.");
        addPhysicalItem(diagnosis, "Argila", physicalAnalysis.getTeorArgila(), "g/dm3",
                "Teor de argila considerado nos critérios químicos que dependem da análise física, quando aplicável.");
        addPhysicalItem(diagnosis, "Densidade aparente", physicalAnalysis.getDensidadeAparente(), "g/cm3",
                "Valor físico relacionado à compactação e ao crescimento radicular; sem faixa crítica cadastrada nesta etapa.");
        addPhysicalItem(diagnosis, "Densidade real", physicalAnalysis.getDensidadeReal(), "g/cm3",
                "Valor usado no próprio extrato para cálculo de porosidade total, quando informado.");
        addPhysicalItem(diagnosis, "Porosidade total", physicalAnalysis.getPorosidadeTotal(), "%",
                "Indicador físico associado à aeração e armazenamento de água; sem classificação automática por ausência de critério modelado.");
        addPhysicalItem(diagnosis, "Microporosidade", physicalAnalysis.getMicroporosidade(), "%",
                "Indicador físico associado à retenção de água; sem classificação automática por ausência de critério modelado.");
        addPhysicalItem(diagnosis, "Água disponível", physicalAnalysis.getAguaDisponivel(), "%",
                "Diferença entre capacidade de campo e ponto de murcha permanente registrada no extrato.");
        addPhysicalItem(diagnosis, "Resistência à penetração", physicalAnalysis.getResistenciaPenetracao(), "MPa",
                "Indicador de impedimento mecânico potencial; interpretar com avaliação de campo e umidade no momento da medição.");
        addPhysicalItem(diagnosis, "Diâmetro médio dos agregados", physicalAnalysis.getDmAgregados(), "mm",
                "Indicador de estrutura do solo calculado a partir das classes de agregados informadas.");

        if (diagnosis.isEmpty()) {
            String message = "Análise física selecionada, mas sem valores físicos preenchidos para diagnosticar.";
            warnings.add(message);
            return new PhysicalDiagnosis(message, diagnosis);
        }

        boolean hasTextureFractions = physicalAnalysis.getTeorAreia() != null
                || physicalAnalysis.getTeorSilte() != null
                || physicalAnalysis.getTeorArgila() != null;
        String summary = hasTextureFractions
                ? "Análise física considerada com frações granulométricas e atributos físicos disponíveis; classe textural não informada no modelo e não inferida sem critério cadastrado."
                : "Análise física considerada com atributos físicos disponíveis; frações granulométricas insuficientes para descrever textura.";
        return new PhysicalDiagnosis(summary, diagnosis);
    }

    private void addPhysicalItem(List<SoilPhysicalDiagnosisItem> diagnosis, String attribute, Double value, String unit, String observation) {
        if (value == null) return;
        diagnosis.add(SoilPhysicalDiagnosisItem.builder()
                .attribute(attribute)
                .analyzedValue(value)
                .unit(unit)
                .technicalObservation(observation)
                .build());
    }

    private SalinityDiagnosis buildSalinityAndSodicityDiagnosis(SaturationExtractAnalysisExtractModel saturation,
                                                                Optional<FertilityAnalysisExtractModel> fertilityExtract,
                                                                SoilFertilityInterpretationCriteriaTableModel table,
                                                                List<String> warnings) {
        List<SoilSalinityDiagnosisItem> diagnosis = new ArrayList<>();
        if (saturation == null) {
            String message = "Extrato de saturação não disponível para diagnóstico de salinidade e sodicidade.";
            warnings.add(message);
            return new SalinityDiagnosis(message, diagnosis);
        }

        addSalinityValue(diagnosis, "Condutividade elétrica do extrato", saturation.getCe(), "dS/m",
                "Valor do extrato de saturação usado para enquadramento salino quando há critério completo.");
        addSalinityValue(diagnosis, "pH do extrato de saturação", saturation.getPh(), null,
                "Valor do extrato de saturação usado no enquadramento de salinidade/sodicidade quando há critério completo.");
        addSalinityValue(diagnosis, "Sódio no extrato de saturação", saturation.getTeorNa(), "mg/dm3",
                "Valor apresentado sem classificação isolada; o sistema não possui faixa específica cadastrada para Na do extrato de saturação.");
        addSalinityValue(diagnosis, "RAS", saturation.getRas(), "mmolc/mmolc^0.5",
                "Relação de adsorção de sódio usada no enquadramento salino/sódico quando há critério completo.");

        FertilityAnalysisExtractModel fertility = fertilityExtract.orElse(null);
        Double pst = fertility != null ? fertility.getPst() : null;
        Double exchangeableNa = fertility != null ? fertility.getSodio() : null;
        Double ctcPh7 = fertility != null ? fertility.getCtcPh7() : null;
        addSalinityValue(diagnosis, "PST", pst, "%",
                "Percentagem de sódio trocável informada no extrato de fertilidade e usada no enquadramento salino/sódico.");

        classifyGlobalSalinity(diagnosis, saturation, pst, table, warnings);
        classifyExchangeableSodium(diagnosis, exchangeableNa, ctcPh7, table, warnings);

        if (diagnosis.isEmpty()) {
            String message = "Extrato de saturação selecionado, mas sem CE, pH, Na ou RAS preenchidos para diagnóstico.";
            warnings.add(message);
            return new SalinityDiagnosis(message, diagnosis);
        }

        String summary = "Extrato de saturação considerado com diagnóstico estruturado de salinidade e sodicidade; correção salina/sódica e cálculo de gesso não foram calculados nesta etapa.";
        return new SalinityDiagnosis(summary, diagnosis);
    }

    private void classifyGlobalSalinity(List<SoilSalinityDiagnosisItem> diagnosis,
                                        SaturationExtractAnalysisExtractModel saturation,
                                        Double pst,
                                        SoilFertilityInterpretationCriteriaTableModel table,
                                        List<String> warnings) {
        Double ce = saturation.getCe();
        Double ph = saturation.getPh();
        Double ras = saturation.getRas();
        if (ce == null || ph == null || ras == null || pst == null) {
            Map<String, Double> values = new LinkedHashMap<>();
            values.put("CE", ce);
            values.put("pH do extrato", ph);
            values.put("RAS", ras);
            values.put("PST", pst);
            String missing = missingParameters(values);
            String observation = "Classificação global não calculada por dados insuficientes: " + missing + ".";
            diagnosis.add(notClassifiedSalinity("Classificação salina/sódica", null, null, observation));
            warnings.add(observation);
            return;
        }
        Optional<SalinityInterpretationModel> criterion = salinityInterpretationRepository.findByTable(table);
        if (criterion.isEmpty()) {
            String observation = "Não há critério de interpretação de salinidade cadastrado para a tabela selecionada.";
            diagnosis.add(notClassifiedSalinity("Classificação salina/sódica", null, null, observation));
            warnings.add(observation);
            return;
        }

        SalinityInterpretationModel c = criterion.get();
        String interpretation = "Não enquadrado";
        String usedCriterion = "Tabela de salinidade ID " + c.getId();
        if (le(ce, c.getNormal_soil_highest_ce()) && le(pst, c.getNormal_soil_highest_pst())
                && le(ph, c.getNormal_soil_highest_ph()) && le(ras, c.getNormal_soil_highest_ras())) {
            interpretation = "Solo normal";
            usedCriterion = "CE <= " + formatNumber(c.getNormal_soil_highest_ce())
                    + "; PST <= " + formatNumber(c.getNormal_soil_highest_pst())
                    + "; pH <= " + formatNumber(c.getNormal_soil_highest_ph())
                    + "; RAS <= " + formatNumber(c.getNormal_soil_highest_ras());
        } else if (ge(ce, c.getSodic_saline_soil_highest_ce()) && ge(pst, c.getSodic_saline_soil_lowest_pst())
                && ge(ph, c.getSodic_saline_soil_lowest_ph()) && ge(ras, c.getSodic_saline_soil_lowest_ras())) {
            interpretation = "Solo salino-sódico";
            usedCriterion = "CE >= " + formatNumber(c.getSodic_saline_soil_highest_ce())
                    + "; PST >= " + formatNumber(c.getSodic_saline_soil_lowest_pst())
                    + "; pH >= " + formatNumber(c.getSodic_saline_soil_lowest_ph())
                    + "; RAS >= " + formatNumber(c.getSodic_saline_soil_lowest_ras());
        } else if (le(ce, c.getSodic_soil_highest_ce()) && ge(pst, c.getSodic_soil_lowest_pst())
                && ge(ph, c.getSodic_soil_lowest_ph()) && ge(ras, c.getSodic_soil_lowest_ras())) {
            interpretation = "Solo sódico";
            usedCriterion = "CE <= " + formatNumber(c.getSodic_soil_highest_ce())
                    + "; PST >= " + formatNumber(c.getSodic_soil_lowest_pst())
                    + "; pH >= " + formatNumber(c.getSodic_soil_lowest_ph())
                    + "; RAS >= " + formatNumber(c.getSodic_soil_lowest_ras());
        } else if (ge(ce, c.getSaline_soil_lowest_ce()) && le(pst, c.getSaline_soil_highest_pst())
                && le(ph, c.getSaline_soil_highest_ph()) && le(ras, c.getSaline_soil_highest_ras())) {
            interpretation = "Solo salino";
            usedCriterion = "CE >= " + formatNumber(c.getSaline_soil_lowest_ce())
                    + "; PST <= " + formatNumber(c.getSaline_soil_highest_pst())
                    + "; pH <= " + formatNumber(c.getSaline_soil_highest_ph())
                    + "; RAS <= " + formatNumber(c.getSaline_soil_highest_ras());
        }

        diagnosis.add(SoilSalinityDiagnosisItem.builder()
                .attribute("Classificação salina/sódica")
                .interpretation(interpretation)
                .usedCriterion(usedCriterion)
                .technicalObservation("Enquadramento calculado somente com critérios cadastrados; não gera recomendação de correção nesta etapa.")
                .build());
    }

    private void classifyExchangeableSodium(List<SoilSalinityDiagnosisItem> diagnosis,
                                            Double exchangeableNa,
                                            Double ctcPh7,
                                            SoilFertilityInterpretationCriteriaTableModel table,
                                            List<String> warnings) {
        if (exchangeableNa == null) {
            diagnosis.add(notClassifiedSalinity("Sódio trocável", null, "mmolc/dm³",
                    "Não há sódio trocável no extrato de fertilidade para classificar pela tabela de sódio trocável."));
            return;
        }
        if (ctcPh7 == null) {
            String observation = "Não há CTC pH 7,0 no extrato de fertilidade para selecionar a faixa de sódio trocável.";
            diagnosis.add(notClassifiedSalinity("Sódio trocável", exchangeableNa, "mmolc/dm³", observation));
            warnings.add(observation);
            return;
        }
        Optional<ExchangeableSodiumModel> criterion = exchangeableSodiumRepository.findFirstByTableOrderByIdAsc(table);
        if (criterion.isEmpty()) {
            String observation = "Não há critério de sódio trocável cadastrado para a tabela selecionada.";
            diagnosis.add(notClassifiedSalinity("Sódio trocável", exchangeableNa, "mmolc/dm³", observation));
            warnings.add(observation);
            return;
        }

        SodiumRangeCriterion range = selectSodiumRange(criterion.get(), ctcPh7);
        SoilSalinityDiagnosisItem item = classifySalinityRange("Sódio trocável", exchangeableNa, "mmolc/dm³",
                new RangeCriterion(range.veryLowEnd(), range.lowStart(), range.lowEnd(), range.mediumStart(),
                        range.mediumEnd(), range.highStart(), range.highEnd(), range.veryHighStart()),
                "Sódio trocável classificado por faixa de CTC pH 7,0 informada no extrato de fertilidade (" + formatNumber(ctcPh7) + ").");
        diagnosis.add(item);
    }

    private SodiumRangeCriterion selectSodiumRange(ExchangeableSodiumModel c, Double ctcPh7) {
        if (ctcPh7 < 4.3) {
            return new SodiumRangeCriterion(c.getCtcLessThan43VeryLowLessThan(), c.getCtcLessThan43LowMin(), c.getCtcLessThan43LowMax(),
                    c.getCtcLessThan43MediumMin(), c.getCtcLessThan43MediumMax(), c.getCtcLessThan43HighMin(), c.getCtcLessThan43HighMax(), c.getCtcLessThan43VeryHighGreaterThan());
        }
        if (ctcPh7 <= 8.6) {
            return new SodiumRangeCriterion(c.getCtcFrom43To86VeryLowLessThan(), c.getCtcFrom43To86LowMin(), c.getCtcFrom43To86LowMax(),
                    c.getCtcFrom43To86MediumMin(), c.getCtcFrom43To86MediumMax(), c.getCtcFrom43To86HighMin(), c.getCtcFrom43To86HighMax(), c.getCtcFrom43To86VeryHighGreaterThan());
        }
        if (ctcPh7 <= 15.0) {
            return new SodiumRangeCriterion(c.getCtcFrom87To150VeryLowLessThan(), c.getCtcFrom87To150LowMin(), c.getCtcFrom87To150LowMax(),
                    c.getCtcFrom87To150MediumMin(), c.getCtcFrom87To150MediumMax(), c.getCtcFrom87To150HighMin(), c.getCtcFrom87To150HighMax(), c.getCtcFrom87To150VeryHighGreaterThan());
        }
        return new SodiumRangeCriterion(c.getCtcGreaterThan15VeryLowLessThan(), c.getCtcGreaterThan15LowMin(), c.getCtcGreaterThan15LowMax(),
                c.getCtcGreaterThan15MediumMin(), c.getCtcGreaterThan15MediumMax(), c.getCtcGreaterThan15HighMin(), c.getCtcGreaterThan15HighMax(), c.getCtcGreaterThan15VeryHighGreaterThan());
    }

    private void addSalinityValue(List<SoilSalinityDiagnosisItem> diagnosis, String attribute, Double value, String unit, String observation) {
        if (value == null) return;
        diagnosis.add(SoilSalinityDiagnosisItem.builder()
                .attribute(attribute)
                .analyzedValue(value)
                .unit(unit)
                .technicalObservation(observation)
                .build());
    }

    private SoilSalinityDiagnosisItem classifySalinityRange(String attribute, Double value, String unit, RangeCriterion criterion, String observation) {
        SoilChemicalDiagnosisItem classified = classifyRange(attribute, value, unit, criterion, observation);
        return SoilSalinityDiagnosisItem.builder()
                .attribute(classified.getAttribute())
                .analyzedValue(classified.getAnalyzedValue())
                .unit(classified.getUnit())
                .interpretation(classified.getInterpretation())
                .usedCriterion(classified.getUsedCriterion())
                .technicalObservation(classified.getTechnicalObservation())
                .build();
    }

    private SoilSalinityDiagnosisItem notClassifiedSalinity(String attribute, Double value, String unit, String observation) {
        return SoilSalinityDiagnosisItem.builder()
                .attribute(attribute)
                .analyzedValue(value)
                .unit(unit)
                .technicalObservation(observation)
                .build();
    }

    private String missingParameters(Map<String, Double> values) {
        return values.entrySet().stream()
                .filter(entry -> entry.getValue() == null)
                .map(Map.Entry::getKey)
                .reduce((a, b) -> a + ", " + b)
                .orElse("nenhum");
    }

    private boolean le(Double value, Double limit) {
        return value != null && limit != null && value <= limit;
    }

    private boolean ge(Double value, Double limit) {
        return value != null && limit != null && value >= limit;
    }

    private List<SoilChemicalDiagnosisItem> buildSoilChemicalDiagnosis(Optional<FertilityAnalysisExtractModel> fertilityExtract,
                                                                       PhysicalAnalysisExtractModel physicalAnalysis,
                                                                       SoilFertilityInterpretationCriteriaTableModel table,
                                                                       List<String> warnings) {
        List<SoilChemicalDiagnosisItem> diagnosis = new ArrayList<>();
        if (fertilityExtract.isEmpty()) {
            String observation = "Nenhum extrato de fertilidade foi encontrado para a análise de solo selecionada.";
            warnings.add(observation);
            diagnosis.add(SoilChemicalDiagnosisItem.builder()
                    .attribute("Fertilidade do solo")
                    .technicalObservation(observation)
                    .build());
            return diagnosis;
        }

        FertilityAnalysisExtractModel fertility = fertilityExtract.get();
        Optional<DiverseContentRangeModel> diverseRange = diverseContentRangeRepository.findByTable(table);
        Optional<KExchangeableContentModel> kRange = kExchangeableContentRepository.findByTable(table);

        if (diverseRange.isEmpty()) {
            warnings.add("Não foi encontrada linha de faixas diversas na tabela de interpretação da fertilidade do solo selecionada.");
        }

        if (fertility.getPhAgua() != null) {
            diagnosis.add(classifyDiverseRange("pH em água", fertility.getPhAgua(), null, diverseRange,
                    r -> new RangeCriterion(r.getPh_too_low(), r.getPh_low_i(), r.getPh_low_f(), r.getPh_medium_i(), r.getPh_medium_f(), r.getPh_hight_i(), r.getPh_hight_f(), r.getPh_too_hight()),
                    "pH em água classificado pelas faixas diversas da tabela selecionada."));
        } else if (fertility.getPhCacl2() != null) {
            diagnosis.add(classifyDiverseRange("pH CaCl2", fertility.getPhCacl2(), null, diverseRange,
                    r -> new RangeCriterion(r.getPh_cacl2_too_low(), r.getPh_cacl2_low_i(), r.getPh_cacl2_low_f(), r.getPh_cacl2_medium_i(), r.getPh_cacl2_medium_f(), r.getPh_cacl2_hight_i(), r.getPh_cacl2_hight_f(), r.getPh_cacl2_too_hight()),
                    "pH em CaCl2 classificado pelas faixas diversas da tabela selecionada."));
        } else {
            diagnosis.add(missingValue("pH", "Não há pH em água nem pH CaCl2 no extrato de fertilidade."));
        }

        diagnosis.add(classifyPhosphorus(fertility, physicalAnalysis, table, warnings));
        diagnosis.add(classifyPotassium(fertility, kRange, diverseRange, warnings));
        diagnosis.add(classifyDiverseRange("Cálcio", fertility.getCalcio(), "mmolc/dm³", diverseRange,
                r -> new RangeCriterion(r.getCalcium_too_low(), r.getCalcium_low_i(), r.getCalcium_low_f(), r.getCalcium_medium_i(), r.getCalcium_medium_f(), r.getCalcium_hight_i(), r.getCalcium_hight_f(), r.getCalcium_too_hight()),
                "Cálcio trocável classificado pelas faixas diversas da tabela selecionada."));
        diagnosis.add(classifyDiverseRange("Magnésio", fertility.getMagnesio(), "mmolc/dm³", diverseRange,
                r -> new RangeCriterion(r.getMagnesium_too_low(), r.getMagnesium_low_i(), r.getMagnesium_low_f(), r.getMagnesium_medium_i(), r.getMagnesium_medium_f(), r.getMagnesium_hight_i(), r.getMagnesium_hight_f(), r.getMagnesium_too_hight()),
                "Magnésio trocável classificado pelas faixas diversas da tabela selecionada."));
        diagnosis.add(classifyDiverseRange("Alumínio", fertility.getAluminio(), "mmolc/dm³", diverseRange,
                r -> new RangeCriterion(r.getAluminum_too_low(), r.getAluminum_low_i(), r.getAluminum_low_f(), r.getAluminum_medium_i(), r.getAluminum_medium_f(), r.getAluminum_hight_i(), r.getAluminum_hight_f(), r.getAluminum_too_hight()),
                "Alumínio trocável classificado pelas faixas diversas da tabela selecionada."));
        if (fertility.getEnxofre() != null) {
            diagnosis.add(classifySulfur(fertility, physicalAnalysis, table, warnings));
        }
        addDiverseDiagnosisIfPresent(diagnosis, "Matéria orgânica", fertility.getMateriaOrganica(), diverseRange.map(DiverseContentRangeModel::getOrganic_matter_unit).orElse("g/dm3"), diverseRange,
                r -> new RangeCriterion(r.getOrganic_matter_too_low(), r.getOrganic_matter_low_i(), r.getOrganic_matter_low_f(), r.getOrganic_matter_medium_i(), r.getOrganic_matter_medium_f(), r.getOrganic_matter_hight_i(), r.getOrganic_matter_hight_f(), r.getOrganic_matter_too_hight()),
                "Matéria orgânica classificada pelas faixas diversas da tabela selecionada.");
        addDiverseDiagnosisIfPresent(diagnosis, "H+Al", fertility.getAluminioMaisHidrogenio(), "mmolc/dm³", diverseRange,
                r -> new RangeCriterion(r.getPotential_acidity_too_low(), r.getPotential_acidity_low_i(), r.getPotential_acidity_low_f(), r.getPotential_acidity_medium_i(), r.getPotential_acidity_medium_f(), r.getPotential_acidity_hight_i(), r.getPotential_acidity_hight_f(), r.getPotential_acidity_too_hight()),
                "Acidez potencial classificada pelas faixas diversas da tabela selecionada.");
        addDiverseDiagnosisIfPresent(diagnosis, "Soma de bases", fertility.getSomaBases(), "mmolc/dm³", diverseRange,
                r -> new RangeCriterion(r.getSum_of_bases_too_low(), r.getSum_of_bases_low_i(), r.getSum_of_bases_low_f(), r.getSum_of_bases_medium_i(), r.getSum_of_bases_medium_f(), r.getSum_of_bases_hight_i(), r.getSum_of_bases_hight_f(), r.getSum_of_bases_too_hight()),
                "Soma de bases classificada pelas faixas diversas da tabela selecionada.");
        addDiverseDiagnosisIfPresent(diagnosis, "CTC efetiva", fertility.getCtcEfetiva(), "mmolc/dm³", diverseRange,
                r -> new RangeCriterion(r.getEffective_cec_too_low(), r.getEffective_cec_low_i(), r.getEffective_cec_low_f(), r.getEffective_cec_medium_i(), r.getEffective_cec_medium_f(), r.getEffective_cec_hight_i(), r.getEffective_cec_hight_f(), r.getEffective_cec_too_hight()),
                "CTC efetiva classificada a partir do valor pronto do extrato de fertilidade.");
        addDiverseDiagnosisIfPresent(diagnosis, "CTC pH 7,0", fertility.getCtcPh7(), "mmolc/dm³", diverseRange,
                r -> new RangeCriterion(r.getPh7_cec_too_low(), r.getPh7_cec_low_i(), r.getPh7_cec_low_f(), r.getPh7_cec_medium_i(), r.getPh7_cec_medium_f(), r.getPh7_cec_hight_i(), r.getPh7_cec_hight_f(), r.getPh7_cec_too_hight()),
                "CTC pH 7,0 classificada a partir do valor pronto do extrato de fertilidade.");
        addDiverseDiagnosisIfPresent(diagnosis, "Saturação por bases", fertility.getSaturacaoBasesV(), "%", diverseRange,
                r -> new RangeCriterion(r.getBase_saturation_too_low(), r.getBase_saturation_low_i(), r.getBase_saturation_low_f(), r.getBase_saturation_medium_i(), r.getBase_saturation_medium_f(), r.getBase_saturation_hight_i(), r.getBase_saturation_hight_f(), r.getBase_saturation_too_hight()),
                "Saturação por bases classificada a partir do valor pronto do extrato de fertilidade.");
        addDiverseDiagnosisIfPresent(diagnosis, "Saturação por alumínio", fertility.getSaturacaoAluminioM(), "%", diverseRange,
                r -> new RangeCriterion(r.getAluminum_saturation_too_low(), r.getAluminum_saturation_low_i(), r.getAluminum_saturation_low_f(), r.getAluminum_saturation_medium_i(), r.getAluminum_saturation_medium_f(), r.getAluminum_saturation_hight_i(), r.getAluminum_saturation_hight_f(), r.getAluminum_saturation_too_hight()),
                "Saturação por alumínio classificada a partir do valor pronto do extrato de fertilidade.");
        addDiverseDiagnosisIfPresent(diagnosis, "Boro", fertility.getBoro(), "mg/dm3", diverseRange,
                r -> new RangeCriterion(r.getBoron_too_low(), r.getBoron_low_i(), r.getBoron_low_f(), r.getBoron_medium_i(), r.getBoron_medium_f(), r.getBoron_hight_i(), r.getBoron_hight_f(), r.getBoron_too_hight()),
                "Boro disponível classificado pelas faixas diversas da tabela selecionada.");
        addDiverseDiagnosisIfPresent(diagnosis, "Cobre", fertility.getCobre(), "mg/dm3", diverseRange,
                r -> new RangeCriterion(r.getCopper_too_low(), r.getCopper_low_i(), r.getCopper_low_f(), r.getCopper_medium_i(), r.getCopper_medium_f(), r.getCopper_hight_i(), r.getCopper_hight_f(), r.getCopper_too_hight()),
                "Cobre disponível classificado pelas faixas diversas da tabela selecionada.");
        addDiverseDiagnosisIfPresent(diagnosis, "Ferro", fertility.getFerro(), "mg/dm3", diverseRange,
                r -> new RangeCriterion(r.getIron_too_low(), r.getIron_low_i(), r.getIron_low_f(), r.getIron_medium_i(), r.getIron_medium_f(), r.getIron_hight_i(), r.getIron_hight_f(), r.getIron_too_hight()),
                "Ferro disponível classificado pelas faixas diversas da tabela selecionada.");
        addDiverseDiagnosisIfPresent(diagnosis, "Manganês", fertility.getManganes(), "mg/dm3", diverseRange,
                r -> new RangeCriterion(r.getManganese_too_low(), r.getManganese_low_i(), r.getManganese_low_f(), r.getManganese_medium_i(), r.getManganese_medium_f(), r.getManganese_hight_i(), r.getManganese_hight_f(), r.getManganese_too_hight()),
                "Manganês disponível classificado pelas faixas diversas da tabela selecionada.");
        addDiverseDiagnosisIfPresent(diagnosis, "Zinco", fertility.getZinco(), "mg/dm3", diverseRange,
                r -> new RangeCriterion(r.getZinc_too_low(), r.getZinc_low_i(), r.getZinc_low_f(), r.getZinc_medium_i(), r.getZinc_medium_f(), r.getZinc_hight_i(), r.getZinc_hight_f(), r.getZinc_too_hight()),
                "Zinco disponível classificado pelas faixas diversas da tabela selecionada.");
        return diagnosis;
    }

    private SoilChemicalDiagnosisItem classifyPhosphorus(FertilityAnalysisExtractModel fertility,
                                                        PhysicalAnalysisExtractModel physicalAnalysis,
                                                        SoilFertilityInterpretationCriteriaTableModel table,
                                                        List<String> warnings) {
        if (fertility.getFosforoMehlich1() != null) {
            Optional<AvailablePMehlich1ExtractorModel> criterion = availablePMehlich1ExtractorRepository.findByTable(table);
            if (criterion.isEmpty()) {
                String observation = "Não há critério de P Mehlich-1 na tabela selecionada.";
                warnings.add(observation);
                return notClassified("Fósforo Mehlich-1", fertility.getFosforoMehlich1(), "mg/dm3", observation);
            }
            Double clay = physicalAnalysis != null ? physicalAnalysis.getTeorArgila() : null;
            if (clay == null) {
                String observation = "Não há teor de argila na análise física para selecionar a faixa de P Mehlich-1.";
                warnings.add(observation);
                return notClassified("Fósforo Mehlich-1", fertility.getFosforoMehlich1(), "mg/dm3", observation);
            }
            double clayDagKg = clay > 100 ? clay / 10.0 : clay;
            AvailablePMehlich1ExtractorModel p = criterion.get();
            RangeCriterion range = clayDagKg < 15
                    ? new RangeCriterion(p.getP_content_sandy_too_low(), p.getP_content_sandy_low_i(), p.getP_content_sandy_low_f(), p.getP_content_sandy_medium_i(), p.getP_content_sandy_medium_f(), p.getP_content_sandy_hight_i(), p.getP_content_sandy_hight_f(), p.getP_content_sandy_too_hight())
                    : clayDagKg <= 35
                    ? new RangeCriterion(p.getP_content_sandy_clayey_too_low(), p.getP_content_sandy_clayey_low_i(), p.getP_content_sandy_clayey_low_f(), p.getP_content_sandy_clayey_medium_i(), p.getP_content_sandy_clayey_medium_f(), p.getP_content_sandy_clayey_hight_i(), p.getP_content_sandy_clayey_hight_f(), p.getP_content_sandy_clayey_too_hight())
                    : clayDagKg <= 60
                    ? new RangeCriterion(p.getP_content_clayey_too_low(), p.getP_content_clayey_low_i(), p.getP_content_clayey_low_f(), p.getP_content_clayey_medium_i(), p.getP_content_clayey_medium_f(), p.getP_content_clayey_hight_i(), p.getP_content_clayey_hight_f(), p.getP_content_clayey_too_hight())
                    : new RangeCriterion(p.getP_content_very_clayey_too_low(), p.getP_content_very_clayey_low_i(), p.getP_content_very_clayey_low_f(), p.getP_content_very_clayey_medium_i(), p.getP_content_very_clayey_medium_f(), p.getP_content_very_clayey_hight_i(), p.getP_content_very_clayey_hight_f(), p.getP_content_very_clayey_too_hight());
            return classifyRange("Fósforo Mehlich-1", fertility.getFosforoMehlich1(), "mg/dm3", range,
                    "Critério de P Mehlich-1 selecionado pelo teor de argila da análise física.");
        }
        if (fertility.getFosforoResina() != null) {
            Optional<AvailablePAnionExchangeResinExtractorModel> criterion = availablePAnionExchangeResinExtractorRepository.findByTable(table);
            if (criterion.isEmpty()) {
                String observation = "Não há critério de P por resina na tabela selecionada.";
                warnings.add(observation);
                return notClassified("Fósforo resina", fertility.getFosforoResina(), "mg/dm3", observation);
            }
            AvailablePAnionExchangeResinExtractorModel p = criterion.get();
            return classifyRange("Fósforo resina", fertility.getFosforoResina(), p.getUnit(),
                    new RangeCriterion(p.getPContentTooLow(), p.getPContentLowI(), p.getPContentLowF(), p.getPContentMediumI(), p.getPContentMediumF(), p.getPContentHighI(), p.getPContentHighF(), p.getPContentTooHigh()),
                    "Fósforo por resina classificado pelo critério específico da tabela selecionada.");
        }
        return missingValue("Fósforo", "Não há valor de fósforo Mehlich-1 ou resina no extrato de fertilidade.");
    }

    private SoilChemicalDiagnosisItem classifyPotassium(FertilityAnalysisExtractModel fertility,
                                                       Optional<KExchangeableContentModel> kRange,
                                                       Optional<DiverseContentRangeModel> diverseRange,
                                                       List<String> warnings) {
        if (fertility.getPotassio() == null) {
            return missingValue("Potássio", "Não há valor de potássio no extrato de fertilidade.");
        }
        if (kRange.isPresent()) {
            KExchangeableContentModel k = kRange.get();
            return classifyRange("Potássio", fertility.getPotassio(), "mmolc/dm³",
                    new RangeCriterion(k.getKContentTooLow(), k.getKContentLowI(), k.getKContentLowF(), k.getKContentMediumI(), k.getKContentMediumF(), k.getKContentHighI(), k.getKContentHighF(), k.getKContentTooHigh()),
                    "Potássio classificado pelo critério específico de K da tabela selecionada.");
        }
        warnings.add("Não foi encontrada linha específica de potássio; foi tentada a faixa diversa de potássio.");
        return classifyDiverseRange("Potássio", fertility.getPotassio(), "mmolc/dm³", diverseRange,
                r -> new RangeCriterion(r.getPotassium_too_low(), r.getPotassium_low_i(), r.getPotassium_low_f(), r.getPotassium_medium_i(), r.getPotassium_medium_f(), r.getPotassium_hight_i(), r.getPotassium_hight_f(), r.getPotassium_too_hight()),
                "Potássio classificado pelas faixas diversas da tabela selecionada.");
    }

    private SoilChemicalDiagnosisItem classifySulfur(FertilityAnalysisExtractModel fertility,
                                                    PhysicalAnalysisExtractModel physicalAnalysis,
                                                    SoilFertilityInterpretationCriteriaTableModel table,
                                                    List<String> warnings) {
        if (fertility.getEnxofre() == null) {
            return missingValue("Enxofre", "Não há valor de enxofre no extrato de fertilidade.");
        }
        Optional<AvailableSModel> criterion = availableSRepository.findByTable(table);
        if (criterion.isEmpty()) {
            String observation = "Não há critério de enxofre na tabela selecionada.";
            warnings.add(observation);
            return notClassified("Enxofre", fertility.getEnxofre(), "mg/dm3", observation);
        }
        Double clay = physicalAnalysis != null ? physicalAnalysis.getTeorArgila() : null;
        if (clay == null) {
            String observation = "Não há teor de argila na análise física para selecionar a faixa de enxofre.";
            warnings.add(observation);
            return notClassified("Enxofre", fertility.getEnxofre(), "mg/dm3", observation);
        }
        AvailableSModel s = criterion.get();
        RangeCriterion range = clay < 400
                ? new RangeCriterion(s.getSContentLess400TooLow(), s.getSContentLess400LowI(), s.getSContentLess400LowF(), s.getSContentLess400MediumI(), s.getSContentLess400MediumF(), s.getSContentLess400HighI(), s.getSContentLess400HighF(), s.getSContentLess400TooHigh())
                : new RangeCriterion(s.getSContentGreater400TooLow(), s.getSContentGreater400LowI(), s.getSContentGreater400LowF(), s.getSContentGreater400MediumI(), s.getSContentGreater400MediumF(), s.getSContentGreater400HighI(), s.getSContentGreater400HighF(), s.getSContentGreater400TooHigh());
        return classifyRange("Enxofre", fertility.getEnxofre(), "mg/dm3", range,
                "Enxofre classificado pelo critério específico selecionado pelo teor de argila da análise física.");
    }

    private SoilChemicalDiagnosisItem classifyDiverseRange(String attribute,
                                                          Double value,
                                                          String unit,
                                                          Optional<DiverseContentRangeModel> range,
                                                          Function<DiverseContentRangeModel, RangeCriterion> criterionExtractor,
                                                          String observation) {
        if (value == null) return missingValue(attribute, "Valor ausente no extrato de fertilidade.");
        if (range.isEmpty()) return notClassified(attribute, value, unit, "Critério ausente na tabela selecionada.");
        return classifyRange(attribute, value, unit, criterionExtractor.apply(range.get()), observation);
    }

    private void addDiverseDiagnosisIfPresent(List<SoilChemicalDiagnosisItem> diagnosis,
                                             String attribute,
                                             Double value,
                                             String unit,
                                             Optional<DiverseContentRangeModel> range,
                                             Function<DiverseContentRangeModel, RangeCriterion> criterionExtractor,
                                             String observation) {
        if (value == null) return;
        diagnosis.add(classifyDiverseRange(attribute, value, unit, range, criterionExtractor, observation));
    }

    private SoilChemicalDiagnosisItem classifyRange(String attribute, Double value, String unit, RangeCriterion criterion, String observation) {
        if (value == null) return missingValue(attribute, "Valor ausente no extrato de fertilidade.");
        if (criterion == null || criterion.lowStart() == null || criterion.mediumStart() == null
                || criterion.highStart() == null || criterion.tooHighStart() == null) {
            return notClassified(attribute, value, unit, "Critério incompleto na tabela selecionada.");
        }
        String interpretation;
        String usedRange;
        if (value < criterion.lowStart()) {
            interpretation = "Muito baixo";
            usedRange = "< " + formatNumber(criterion.lowStart());
        } else if (value < criterion.mediumStart()) {
            interpretation = "Baixo";
            usedRange = formatInterval(criterion.lowStart(), criterion.lowEnd(), criterion.mediumStart());
        } else if (value < criterion.highStart()) {
            interpretation = "Médio";
            usedRange = formatInterval(criterion.mediumStart(), criterion.mediumEnd(), criterion.highStart());
        } else if (value < criterion.tooHighStart()) {
            interpretation = "Alto";
            usedRange = formatInterval(criterion.highStart(), criterion.highEnd(), criterion.tooHighStart());
        } else {
            interpretation = "Muito alto";
            usedRange = ">= " + formatNumber(criterion.tooHighStart());
        }
        return SoilChemicalDiagnosisItem.builder()
                .attribute(attribute)
                .analyzedValue(value)
                .unit(unit)
                .interpretation(interpretation)
                .usedCriterion(usedRange)
                .technicalObservation(observation)
                .build();
    }

    private SoilChemicalDiagnosisItem missingValue(String attribute, String observation) {
        return SoilChemicalDiagnosisItem.builder()
                .attribute(attribute)
                .technicalObservation(observation)
                .build();
    }

    private SoilChemicalDiagnosisItem notClassified(String attribute, Double value, String unit, String observation) {
        return SoilChemicalDiagnosisItem.builder()
                .attribute(attribute)
                .analyzedValue(value)
                .unit(unit)
                .technicalObservation(observation)
                .build();
    }

    private List<FoliarDiagnosisItem> buildFoliarDiagnosis(Optional<FoliarAnalysisModel> foliarAnalysis,
                                                           CropModel crop,
                                                           CropFoliarAnalysisInterpretationTableModel table,
                                                           List<String> warnings) {
        if (foliarAnalysis.isEmpty()) {
            warnings.add("Análise foliar não informada para a cultura selecionada; diagnóstico foliar não calculado.");
            return List.of();
        }
        if (crop == null || crop.getName() == null) {
            warnings.add("Cultura sem nome comum cadastrado; não foi possível buscar linha de interpretação foliar.");
            return List.of();
        }
        Optional<CropFoliarAnalysisInterpretationTableLineModel> line =
                cropFoliarAnalysisInterpretationTableLineRepository.findByTableAndCrop(table, crop.getName());
        if (line.isEmpty()) {
            warnings.add("Tabela de interpretação foliar selecionada não possui linha compatível com a cultura " + crop.getName() + ".");
            return List.of();
        }

        FoliarAnalysisModel analysis = foliarAnalysis.get();
        CropFoliarAnalysisInterpretationTableLineModel criteria = line.get();
        List<FoliarDiagnosisItem> items = new ArrayList<>();
        MacronutrientsContent macro = analysis.getMacronutrients();
        MicronutrientsContent micro = analysis.getMicronutrients();

        addFoliarItem(items, "Nitrogênio (N)", macro != null ? macro.getN_content() : null, criteria.getN_content());
        addFoliarItem(items, "Fósforo (P)", macro != null ? macro.getP_content() : null, criteria.getP_content());
        addFoliarItem(items, "Potássio (K)", macro != null ? macro.getK_content() : null, criteria.getK_content());
        addFoliarItem(items, "Cálcio (Ca)", macro != null ? macro.getCa_content() : null, criteria.getCa_content());
        addFoliarItem(items, "Magnésio (Mg)", macro != null ? macro.getMg_content() : null, criteria.getMg_content());
        addFoliarItem(items, "Enxofre (S)", macro != null ? macro.getS_content() : null, criteria.getS_content());
        addFoliarItem(items, "Boro (B)", micro != null ? micro.getB_content() : null, criteria.getB_content());
        addFoliarItem(items, "Cobre (Cu)", micro != null ? micro.getCu_content() : null, criteria.getCu_content());
        addFoliarItem(items, "Ferro (Fe)", micro != null ? micro.getFe_content() : null, criteria.getFe_content());
        addFoliarItem(items, "Manganês (Mn)", micro != null ? micro.getMn_content() : null, criteria.getMn_content());
        addFoliarItem(items, "Molibdênio (Mo)", micro != null ? micro.getMo_content() : null, criteria.getMo_content());
        addFoliarItem(items, "Zinco (Zn)", micro != null ? micro.getZn_content() : null, criteria.getZn_content());

        if (items.stream().noneMatch(item -> item.getAnalyzedValue() != null && item.getInterpretation() != null)) {
            warnings.add("Análise foliar encontrada, mas não há valores e critérios suficientes para classificar nutrientes foliares.");
        }
        return items;
    }

    private void addFoliarItem(List<FoliarDiagnosisItem> items, String nutrient, Double value, MenorMaiorTeores range) {
        if (value == null) {
            items.add(FoliarDiagnosisItem.builder()
                    .nutrient(nutrient)
                    .technicalObservation("Valor foliar não informado na análise selecionada.")
                    .build());
            return;
        }
        if (range == null || range.getMenor() == null || range.getMaior() == null) {
            items.add(FoliarDiagnosisItem.builder()
                    .nutrient(nutrient)
                    .analyzedValue(value)
                    .unit(resolveFoliarUnit(range))
                    .technicalObservation("Critério foliar ausente ou incompleto na tabela selecionada.")
                    .build());
            return;
        }

        String interpretation;
        String observation;
        if (value < range.getMenor()) {
            interpretation = "Deficiência";
            observation = "Valor abaixo do menor teor adequado cadastrado para a cultura.";
        } else if (value > range.getMaior()) {
            interpretation = "Alto";
            observation = "Valor acima do maior teor adequado cadastrado para a cultura; avaliar risco de excesso/toxidez conforme acompanhamento agronômico.";
        } else {
            interpretation = "Adequado";
            observation = "Valor dentro da faixa adequada cadastrada para a cultura.";
        }

        items.add(FoliarDiagnosisItem.builder()
                .nutrient(nutrient)
                .analyzedValue(value)
                .unit(resolveFoliarUnit(range))
                .interpretation(interpretation)
                .usedCriterion(formatNumber(range.getMenor()) + " a " + formatNumber(range.getMaior()))
                .technicalObservation(observation)
                .build());
    }

    private String buildFoliarSummary(Optional<FoliarAnalysisModel> foliarAnalysis,
                                      List<FoliarDiagnosisItem> foliarDiagnosis,
                                      List<String> warnings) {
        if (foliarAnalysis.isEmpty()) return "Análise foliar não informada.";
        long classified = foliarDiagnosis.stream()
                .filter(item -> item.getAnalyzedValue() != null && item.getInterpretation() != null)
                .count();
        if (classified == 0) return "Análise foliar encontrada, mas sem diagnóstico classificável por dados/critério insuficientes.";
        return "Análise foliar encontrada com ID " + foliarAnalysis.get().getId() + "; " + classified + " nutriente(s) classificado(s).";
    }

    private String resolveFoliarUnit(MenorMaiorTeores range) {
        UnidadeTeor unity = range != null ? range.getUnity() : null;
        if (unity == null) return "Não informado";
        return switch (unity) {
            case g_per_kg -> "g/kg";
            case mg_per_kg -> "mg/kg";
            case dag_per_kg -> "dag/kg";
            case g_per_dm3 -> "g/dm3";
            case mg_per_dm3 -> "mg/dm3";
            case cmolc_per_dm3 -> "cmolc/dm3";
            case mmolc_per_dm3 -> "mmolc/dm³";
            case percentage -> "%";
        };
    }

    private String formatInterval(Double start, Double end, Double fallbackEndExclusive) {
        Double effectiveEnd = end != null ? end : fallbackEndExclusive;
        return formatNumber(start) + " a " + formatNumber(effectiveEnd);
    }

    private String formatNumber(Double value) {
        if (value == null) return "não informado";
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }

    private record PhysicalDiagnosis(String summary, List<SoilPhysicalDiagnosisItem> items) {}
    private record SalinityDiagnosis(String summary, List<SoilSalinityDiagnosisItem> items) {}
    private record GypsumSourceSelection(String sourceName, String sourceType, Double commercialDose, String commercialDoseUnit, String justification, String limitations) {}
    private record RangeCriterion(Double tooLowEnd, Double lowStart, Double lowEnd, Double mediumStart,
                                  Double mediumEnd, Double highStart, Double highEnd, Double tooHighStart) {}
    private record SodiumRangeCriterion(Double veryLowEnd, Double lowStart, Double lowEnd, Double mediumStart,
                                        Double mediumEnd, Double highStart, Double highEnd, Double veryHighStart) {}
    private class NutrientBalanceAccumulator {
        private final double requiredN;
        private final double requiredP2O5;
        private final double requiredK2O;
        private double plantingN;
        private double plantingP2O5;
        private double plantingK2O;
        private double coverageRecommendedN;
        private double coverageRecommendedP2O5;
        private double coverageRecommendedK2O;
        private double coverageProvidedN;
        private double coverageProvidedP2O5;
        private double coverageProvidedK2O;

        NutrientBalanceAccumulator(Double requiredN, Double requiredP2O5, Double requiredK2O) {
            this.requiredN = nvl(requiredN);
            this.requiredP2O5 = nvl(requiredP2O5);
            this.requiredK2O = nvl(requiredK2O);
        }

        void addPlanting(Double providedN, Double providedP2O5, Double providedK2O) {
            plantingN = nvl(providedN);
            plantingP2O5 = nvl(providedP2O5);
            plantingK2O = nvl(providedK2O);
        }

        void addCoverage(Nutriente nutrient, double recommendedApplication, double providedN, double providedP2O5, double providedK2O) {
            if (nutrient == Nutriente.NITROGENIO) coverageRecommendedN = round2(coverageRecommendedN + recommendedApplication);
            else if (nutrient == Nutriente.POTASSIO) coverageRecommendedK2O = round2(coverageRecommendedK2O + recommendedApplication);
            else coverageRecommendedP2O5 = round2(coverageRecommendedP2O5 + recommendedApplication);
            coverageProvidedN = round2(coverageProvidedN + providedN);
            coverageProvidedP2O5 = round2(coverageProvidedP2O5 + providedP2O5);
            coverageProvidedK2O = round2(coverageProvidedK2O + providedK2O);
        }

        double remainingDeficit(Nutriente nutrient) {
            if (nutrient == Nutriente.NITROGENIO) return Math.max(0d, round2(requiredN - providedTotalN()));
            if (nutrient == Nutriente.POTASSIO) return Math.max(0d, round2(requiredK2O - providedTotalK2O()));
            return Math.max(0d, round2(requiredP2O5 - providedTotalP2O5()));
        }

        double providedTotalN() { return round2(plantingN + coverageProvidedN); }
        double providedTotalP2O5() { return round2(plantingP2O5 + coverageProvidedP2O5); }
        double providedTotalK2O() { return round2(plantingK2O + coverageProvidedK2O); }
        double balanceN() { return round2(providedTotalN() - requiredN); }
        double balanceP2O5() { return round2(providedTotalP2O5() - requiredP2O5); }
        double balanceK2O() { return round2(providedTotalK2O() - requiredK2O); }

        List<NutrientBalanceRow> toRows() {
            return List.of(
                    row("N", requiredN, plantingN, coverageRecommendedN, coverageProvidedN, providedTotalN(), balanceN()),
                    row("P2O5", requiredP2O5, plantingP2O5, coverageRecommendedP2O5, coverageProvidedP2O5, providedTotalP2O5(), balanceP2O5()),
                    row("K2O", requiredK2O, plantingK2O, coverageRecommendedK2O, coverageProvidedK2O, providedTotalK2O(), balanceK2O())
            );
        }

        private NutrientBalanceRow row(String nutrient, double required, double planting, double coverageRecommended, double coverageProvided, double totalProvided, double balance) {
            return NutrientBalanceRow.builder()
                    .nutrient(nutrient)
                    .requiredTotalKgHa(round2(required))
                    .providedByPlantingKgHa(round2(planting))
                    .recommendedCoverageKgHa(round2(coverageRecommended))
                    .providedByCoverageKgHa(round2(coverageProvided))
                    .providedTotalKgHa(round2(totalProvided))
                    .finalBalanceKgHa(round2(balance))
                    .status(balance < 0d ? "Déficit" : balance > 0d ? "Excedente" : "Atendido")
                    .build();
        }
    }
    private record FertilizerSelection(String name, Double quantityKgHa, Double providedN, Double providedP2O5, Double providedK2O, Double balanceN, Double balanceP2O5, Double balanceK2O, String limitingNutrient, Double targetNeedKgHa, Double productConcentrationPercent, String calculationMemory, String warning, Optional<FertilizerSuggestion> suggestion){}
    private record FertilizerDoseCalculation(String nutrient, double targetNeedKgHa, double concentrationPercent, double quantityKgHa, String method) {}

    private FertilizerSelection selectBestPlantingFertilizer(UserModel user, FertilizerSourceOption sourceOption, Double n, Double p, Double k, List<String>w){var formulated=selectFormulatedFertilizers(user, sourceOption);var bestF=formulated.stream().filter(f->f.getN()>0||f.getP2O5()>0||f.getK2O()>0).max((a,b)->compareScore(a.getN(),a.getP2O5(),a.getK2O(),b.getN(),b.getP2O5(),b.getK2O(),n,p,k,a.getId(),b.getId())); if(bestF.isPresent()){var f=bestF.get();Optional<FertilizerDoseCalculation> calc=calculateByGreatestFactor(n,p,k,f.getN(),f.getP2O5(),f.getK2O(),"maior fator necessário entre N, P2O5 e K2O");var s=FertilizerSuggestion.builder().fertilizerId(f.getId()).fertilizerType("FORMULADO").fertilizerName("NPK "+(int)f.getN()+"-"+(int)f.getP2O5()+"-"+(int)f.getK2O()).n(f.getN()).p2o5(f.getP2O5()).k2o(f.getK2O()).reason("Maior cobertura dos nutrientes de plantio.").build();if(calc.isEmpty()){String warning="Fertilizante formulado selecionado, mas sem nutriente alvo com necessidade e concentração válidas para calcular dose comercial.";w.add(warning);return buildSelection(s.getFertilizerName(),(Double)null,n,p,k,f.getN(),f.getP2O5(),f.getK2O(),null,w,warning,Optional.of(s));}w.add("Quantidade de adubo formulado estimada a partir do maior fator necessário entre N, P2O5 e K2O; excedentes ficam explícitos no saldo.");return buildSelection(s.getFertilizerName(),calc.get(),n,p,k,f.getN(),f.getP2O5(),f.getK2O(),w,null,Optional.of(s));}
    var simples=selectSimpleFertilizers(user, sourceOption);var bestS=simples.stream().filter(f->f.getN()>0||f.getP2O5()>0||f.getK2O()>0).max((a,b)->compareScore(a.getN(),a.getP2O5(),a.getK2O(),b.getN(),b.getP2O5(),b.getK2O(),n,p,k,a.getId(),b.getId()));if(bestS.isPresent()){var f=bestS.get();Optional<FertilizerDoseCalculation> calc=calculateByGreatestFactor(n,p,k,f.getN(),f.getP2O5(),f.getK2O(),"concentração do nutriente alvo em fertilizante simples");var s=FertilizerSuggestion.builder().fertilizerId(f.getId()).fertilizerType("SIMPLES").fertilizerName(f.getName()).n(f.getN()).p2o5(f.getP2O5()).k2o(f.getK2O()).reason("Fallback por ausência de formulado adequado; dose calculada pelo nutriente alvo identificado.").build();if(calc.isEmpty()){String warning="Fertilizante simples selecionado, mas sem nutriente alvo com necessidade e concentração válidas para calcular dose comercial.";w.add(warning);return buildSelection(s.getFertilizerName(),(Double)null,n,p,k,f.getN(),f.getP2O5(),f.getK2O(),null,w,warning,Optional.of(s));}w.add("Quantidade de adubo simples calculada pela concentração do nutriente alvo identificado.");return buildSelection(s.getFertilizerName(),calc.get(),n,p,k,f.getN(),f.getP2O5(),f.getK2O(),w,null,Optional.of(s));}
    w.add("Nenhum adubo mineral adequado foi encontrado para a origem de adubos selecionada."); return new FertilizerSelection("Não encontrado",null,null,null,null,null,null,null,null,null,null,null,null,Optional.empty());}

    private FertilizerSelection buildSelection(String name, FertilizerDoseCalculation calc, Double rn, Double rp, Double rk, double fn, double fp, double fk, List<String> warnings, String warning, Optional<FertilizerSuggestion> suggestion){Double q=calc!=null?calc.quantityKgHa():null;return buildSelection(name,q,rn,rp,rk,fn,fp,fk,calc,warnings,warning,suggestion);}
    private FertilizerSelection buildSelection(String name, Double q, Double rn, Double rp, Double rk, double fn, double fp, double fk, FertilizerDoseCalculation calc, List<String> warnings, String warning, Optional<FertilizerSuggestion> suggestion){double pn=q==null?0d:round2(q*fn/100d), pp=q==null?0d:round2(q*fp/100d), pk=q==null?0d:round2(q*fk/100d);double bn=round2(pn-nvl(rn)), bp=round2(pp-nvl(rp)), bk=round2(pk-nvl(rk));String effectiveWarning=warning;if(effectiveWarning==null&&(bn<0||bp<0||bk<0)){effectiveWarning=String.format("Fertilizante selecionado não atende todos os nutrientes no plantio. Déficits: N %.2f kg/ha, P2O5 %.2f kg/ha, K2O %.2f kg/ha.", Math.max(0d,-bn), Math.max(0d,-bp), Math.max(0d,-bk));warnings.add(effectiveWarning);}String memory=calc==null?null:buildCalculationMemory(calc,pn,pp,pk,bn,bp,bk);return new FertilizerSelection(name,q,pn,pp,pk,bn,bp,bk,calc!=null?calc.nutrient():null,calc!=null?calc.targetNeedKgHa():null,calc!=null?calc.concentrationPercent():null,memory,effectiveWarning,suggestion);}
    private Optional<FertilizerDoseCalculation> calculateByGreatestFactor(Double rn, Double rp, Double rk, double n, double p, double k, String method) {
        List<FertilizerDoseCalculation> calculations = new ArrayList<>();
        addDoseCandidate(calculations, "N", rn, n, method);
        addDoseCandidate(calculations, "P2O5", rp, p, method);
        addDoseCandidate(calculations, "K2O", rk, k, method);
        return calculations.stream().max(Comparator.comparing(FertilizerDoseCalculation::quantityKgHa));
    }
    private void addDoseCandidate(List<FertilizerDoseCalculation> calculations, String nutrient, Double required, double concentration, String method) {
        if (nvl(required) <= 0d || concentration <= 0d) return;
        calculations.add(new FertilizerDoseCalculation(nutrient, round2(required), round2(concentration), round2(required / concentration * 100d), method));
    }
    private String buildCalculationMemory(FertilizerDoseCalculation calc, double providedN, double providedP2O5, double providedK2O, double balanceN, double balanceP2O5, double balanceK2O) {
        return String.format(Locale.US,
                "Nutriente limitante/alvo: %s; necessidade alvo: %.2f kg/ha; concentração do produto: %.2f%%; dose calculada: %.2f kg/ha (%s); fornecido: N %.2f, P2O5 %.2f, K2O %.2f kg/ha; déficit/excedente: N %+.2f, P2O5 %+.2f, K2O %+.2f kg/ha.",
                calc.nutrient(), calc.targetNeedKgHa(), calc.concentrationPercent(), calc.quantityKgHa(), calc.method(), providedN, providedP2O5, providedK2O, balanceN, balanceP2O5, balanceK2O);
    }
    private List<FertilizationRecommendationRow> buildCoverageRows(ContentRangeModel range, UserModel user, FertilizerSourceOption sourceOption, List<FertilizerSuggestion> suggestions, NutrientBalanceAccumulator balance, List<String> warnings) {
        List<FertilizationRecommendationRow> rows = new ArrayList<>();
        Nutriente nutrient = range.getNutrient();
        List<CoverageModel> coverages = coverageRepository.findAllByRangeOrderByOrderAsc(range);
        if (coverages.stream().anyMatch(c -> nvl(c.getApplication()) > 0d) && balance.remainingDeficit(nutrient) <= 0d) {
            warnings.add("Cobertura de " + nutrient + " cadastrada na tabela não foi recomendada porque o plantio já atende ou excede a necessidade calculada. O modelo de cobertura não possui marcação de parcelamento técnico.");
            return rows;
        }

        for (CoverageModel c : coverages) {
            double registeredApplication = nvl(c.getApplication());
            if (registeredApplication <= 0d) continue;

            double remainingDeficit = balance.remainingDeficit(nutrient);
            if (remainingDeficit <= 0d) {
                warnings.add("Cobertura " + c.getOrder() + " de " + nutrient + " não recomendada porque o déficit remanescente já foi atendido por aplicações anteriores.");
                continue;
            }

            double targetApplication = round2(Math.min(registeredApplication, remainingDeficit));
            var simples = selectSimpleFertilizers(user, sourceOption);
            SimpleMineralFertilizerModel best = selectCoverageFertilizer(simples, nutrient);
            String fertName = "Não encontrado";
            Double q = null;
            String limitingNutrient = null;
            Double targetNeed = null;
            Double concentration = null;
            String calculationMemory = null;
            String warning = null;
            double providedN = 0d;
            double providedP2O5 = 0d;
            double providedK2O = 0d;

            if (best != null) {
                double pct = coveragePercentage(best, nutrient);
                if (pct > 0d) {
                    q = round2(targetApplication / pct * 100d);
                    limitingNutrient = nutrientLabel(nutrient);
                    targetNeed = targetApplication;
                    concentration = round2(pct);
                    providedN = round2(q * nvl(best.getN()) / 100d);
                    providedP2O5 = round2(q * nvl(best.getP2O5()) / 100d);
                    providedK2O = round2(q * nvl(best.getK2O()) / 100d);
                    balance.addCoverage(nutrient, targetApplication, providedN, providedP2O5, providedK2O);
                    calculationMemory = buildCalculationMemory(new FertilizerDoseCalculation(limitingNutrient, targetNeed, concentration, q, "concentração do nutriente alvo em fertilizante simples"), providedN, providedP2O5, providedK2O, balance.balanceN(), balance.balanceP2O5(), balance.balanceK2O());
                } else {
                    warning = "Adubo mineral simples encontrado para cobertura de " + nutrient + ", mas sem concentração válida do nutriente alvo; dose não calculada.";
                    warnings.add(warning);
                }
                fertName = best.getName();
                suggestions.add(FertilizerSuggestion.builder()
                        .fertilizerId(best.getId())
                        .fertilizerType("SIMPLES")
                        .fertilizerName(best.getName())
                        .n(best.getN())
                        .p2o5(best.getP2O5())
                        .k2o(best.getK2O())
                        .reason("Cobertura por " + nutrient + " limitada ao saldo global remanescente.")
                        .build());
            } else {
                warnings.add("Não foi encontrado adubo mineral simples para cobertura de " + nutrient + ".");
            }

            rows.add(FertilizationRecommendationRow.builder()
                    .phase("Cobertura " + c.getOrder() + " - " + nutrient)
                    .nutrients(nutrient + ": " + String.format(Locale.US, "%.2f", targetApplication) + " kg/ha")
                    .suggestedFertilizer(fertName)
                    .fertilizerQuantityKgHa(q)
                    .applicationMode("Aplicação em cobertura limitada ao déficit remanescente do balanço global NPK.")
                    .source("Tabela de adubação da cultura; aplicação cadastrada " + String.format(Locale.US, "%.2f", registeredApplication) + " kg/ha")
                    .providedN(providedN)
                    .providedP2O5(providedP2O5)
                    .providedK2O(providedK2O)
                    .balanceN(balance.balanceN())
                    .balanceP2O5(balance.balanceP2O5())
                    .balanceK2O(balance.balanceK2O())
                    .limitingNutrient(limitingNutrient)
                    .targetNeedKgHa(targetNeed)
                    .productConcentrationPercent(concentration)
                    .calculationMemory(calculationMemory)
                    .warning(warning)
                    .build());
        }
        return rows;
    }

    private SimpleMineralFertilizerModel selectCoverageFertilizer(List<SimpleMineralFertilizerModel> fertilizers, Nutriente nutrient) {
        if (nutrient == Nutriente.NITROGENIO) return fertilizers.stream().filter(f -> nvl(f.getN()) > 0d).max(Comparator.comparing(f -> nvl(f.getN()))).orElse(null);
        if (nutrient == Nutriente.POTASSIO) return fertilizers.stream().filter(f -> nvl(f.getK2O()) > 0d).max(Comparator.comparing(f -> nvl(f.getK2O()))).orElse(null);
        return fertilizers.stream().filter(f -> nvl(f.getP2O5()) > 0d).max(Comparator.comparing(f -> nvl(f.getP2O5()))).orElse(null);
    }

    private double coveragePercentage(SimpleMineralFertilizerModel fertilizer, Nutriente nutrient) {
        if (fertilizer == null) return 0d;
        if (nutrient == Nutriente.NITROGENIO) return nvl(fertilizer.getN());
        if (nutrient == Nutriente.POTASSIO) return nvl(fertilizer.getK2O());
        return nvl(fertilizer.getP2O5());
    }
    private String nutrientLabel(Nutriente nutrient) {
        if (nutrient == Nutriente.NITROGENIO) return "N";
        if (nutrient == Nutriente.POTASSIO) return "K2O";
        return "P2O5";
    }
    private int compareScore(double an,double ap,double ak,double bn,double bp,double bk,Double rn,Double rp,Double rk,Long aid,Long bid){int as=(nvl(rn)>0&&an>0?1:0)+(nvl(rp)>0&&ap>0?1:0)+(nvl(rk)>0&&ak>0?1:0);int bs=(nvl(rn)>0&&bn>0?1:0)+(nvl(rp)>0&&bp>0?1:0)+(nvl(rk)>0&&bk>0?1:0); if(as!=bs)return Integer.compare(as,bs); if(nvl(rp)>0&&Double.compare(ap,bp)!=0)return Double.compare(ap,bp); return Long.compare(bid,aid);}
    private double round2(double v){return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP).doubleValue();}
    private <T> List<T> dedup(List<T> a,List<T> b, Function<T,Long> id){Map<Long,T> m=new LinkedHashMap<>();a.forEach(x->m.putIfAbsent(id.apply(x),x));b.forEach(x->m.putIfAbsent(id.apply(x),x));return new ArrayList<>(m.values());}

    private PhysicalAnalysisExtractModel findPhysicalAnalysisExtractByIdOrThrow(Long id) {return physicalAnalysisExtractRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Extrato de análise física não encontrado com o ID: " + id));}
    private FertilityAnalysisSelection findSoilFertilitySelectionByIdOrThrow(Long id, PlotModel requestPlot) {Optional<FertilityAnalysisExtractModel> extract = fertilityAnalysisExtractRepository.findById(id); if (extract.isPresent()) {SoilAnalysisModel soil = resolveSoilAnalysis(extract.get()); if (soil.getPlot() != null && requestPlot != null && Objects.equals(soil.getPlot().getId(), requestPlot.getId())) return new FertilityAnalysisSelection(soil, extract);} Optional<SoilAnalysisModel> soil = soilAnalysisRepository.findById(id); if (soil.isPresent()) return new FertilityAnalysisSelection(soil.get(), Optional.empty()); return extract.map(e -> new FertilityAnalysisSelection(resolveSoilAnalysis(e), Optional.of(e))).orElseThrow(() -> new EntityNotFoundException("Análise de fertilidade do solo não encontrada com o ID: " + id));}
    private SaturationExtractAnalysisExtractModel findSaturationExtractAnalysisExtractByIdOrThrow(Long id) {return saturationExtractAnalysisExtractRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Extrato de análise de saturação não encontrado com o ID: " + id));}
    private AnnualCropFolderModel findAnnualCropFolderByIdOrThrow(Long id) {return annualCropFolderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Pasta de cultura anual não encontrada com o ID: " + id));}
    private CropModel findCropByIdOrThrow(Long id) {return cropRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Cultura não encontrada com o ID: " + id));}
    private CropFertilizationTableModel findCropFertilizationTableBySelectionOrThrow(Long id, TechnicalTableGroup group, UserModel user) {return findCropFertilizationTableBySelection(id, group, user).orElseThrow(() -> new EntityNotFoundException("Tabela de adubação de culturas não encontrada para o grupo " + group + " e ID: " + id));}
    private SoilFertilityInterpretationCriteriaTableModel findSoilFertilityInterpretationTableBySelectionOrThrow(Long id, TechnicalTableGroup group, UserModel user) {return findSoilFertilityInterpretationTableBySelection(id, group, user).orElseThrow(() -> new EntityNotFoundException("Tabela de interpretação da fertilidade do solo não encontrada para o grupo " + group + " e ID: " + id));}
    private CropFoliarAnalysisInterpretationTableModel findCropFoliarAnalysisInterpretationTableBySelectionOrThrow(Long id, TechnicalTableGroup group, UserModel user) {return findCropFoliarAnalysisInterpretationTableBySelection(id, group, user).orElseThrow(() -> new EntityNotFoundException("Tabela de interpretação de análise foliar não encontrada para o grupo " + group + " e ID: " + id));}
    private Optional<CropFertilizationTableModel> findCropFertilizationTableBySelection(Long id, TechnicalTableGroup group, UserModel user) {validateTableSelection(id, group, "tabela de adubação de culturas"); return switch (group) {case MINHAS, PRIVADAS -> cropFertilizationTableRepository.findByIdAndCreatorAndCreator_CargoNot(id, user, Cargo.USUARIO_SUPREMO); case PUBLICAS -> cropFertilizationTableRepository.findByIdAndPublicTableTrue(id); case PADRAO -> cropFertilizationTableRepository.findByIdAndCreator_CargoAndPublicTableTrue(id, Cargo.USUARIO_SUPREMO);};}
    private Optional<SoilFertilityInterpretationCriteriaTableModel> findSoilFertilityInterpretationTableBySelection(Long id, TechnicalTableGroup group, UserModel user) {validateTableSelection(id, group, "tabela de interpretação da fertilidade do solo"); return switch (group) {case MINHAS, PRIVADAS -> soilFertilityInterpretationCriteriaTableRepository.findByIdAndCreatorAndCreator_CargoNot(id, user, Cargo.USUARIO_SUPREMO); case PUBLICAS -> soilFertilityInterpretationCriteriaTableRepository.findByIdAndPublicTableTrueAndCreator_CargoNot(id, Cargo.USUARIO_SUPREMO); case PADRAO -> soilFertilityInterpretationCriteriaTableRepository.findByIdAndCreator_Cargo(id, Cargo.USUARIO_SUPREMO);};}
    private Optional<CropFoliarAnalysisInterpretationTableModel> findCropFoliarAnalysisInterpretationTableBySelection(Long id, TechnicalTableGroup group, UserModel user) {validateTableSelection(id, group, "tabela de interpretação de análise foliar"); return switch (group) {case MINHAS, PRIVADAS -> cropFoliarAnalysisInterpretationTableRepository.findByIdAndCreatorAndCreator_CargoNot(id, user, Cargo.USUARIO_SUPREMO); case PUBLICAS -> cropFoliarAnalysisInterpretationTableRepository.findByIdAndPublicTableTrueAndCreator_CargoNot(id, Cargo.USUARIO_SUPREMO); case PADRAO -> cropFoliarAnalysisInterpretationTableRepository.findByIdAndCreator_Cargo(id, Cargo.USUARIO_SUPREMO);};}
    private void validateTableSelection(Long id, TechnicalTableGroup group, String tableName) {if (id == null) throw new IllegalArgumentException("ID da " + tableName + " é obrigatório."); if (group == null) throw new IllegalArgumentException("Grupo da " + tableName + " é obrigatório.");}
    private void validateSamePlot(PlotModel selectedPlot, PlotModel requestPlot, String message) {if (selectedPlot == null || requestPlot == null || !Objects.equals(selectedPlot.getId(), requestPlot.getId())) throw new IllegalArgumentException(message);}
    private PlotModel resolvePlot(PhysicalAnalysisExtractModel model) {if (model.getRangeExtract() != null && model.getRangeExtract().getAnalysis() != null) return model.getRangeExtract().getAnalysis().getPlot(); if (model.getLayerExtract() != null && model.getLayerExtract().getAnalysis() != null) return model.getLayerExtract().getAnalysis().getPlot(); throw new IllegalArgumentException("Extrato de análise física não possui análise de solo associada.");}
    private PlotModel resolvePlot(SaturationExtractAnalysisExtractModel model) {if (model.getRangeExtract() != null && model.getRangeExtract().getAnalysis() != null) return model.getRangeExtract().getAnalysis().getPlot(); if (model.getLayerExtract() != null && model.getLayerExtract().getAnalysis() != null) return model.getLayerExtract().getAnalysis().getPlot(); throw new IllegalArgumentException("Extrato de análise de saturação não possui análise de solo associada.");}
    private Optional<FoliarAnalysisModel> findLatestFoliarAnalysis(CropModel crop) {return foliarAnalysisRepository.findTopByCropOrderByIdDesc(crop);}    
    private List<FormulatedMineralFertilizerModel> selectFormulatedFertilizers(UserModel user, FertilizerSourceOption sourceOption){return switch (sourceOption) {case PRIVATE -> formulatedMineralFertilizerRepository.findAllByUserAndPublicoFalseOrderByIdAsc(user); case PUBLIC -> formulatedMineralFertilizerRepository.findAllByPublicoTrueOrderByIdAsc(); case DEFAULT -> formulatedMineralFertilizerRepository.findAllByUser_CargoOrderByIdAsc(Cargo.USUARIO_SUPREMO); case BOTH -> dedup(dedup(formulatedMineralFertilizerRepository.findAllByUserAndPublicoFalseOrderByIdAsc(user), formulatedMineralFertilizerRepository.findAllByPublicoTrueOrderByIdAsc(), FormulatedMineralFertilizerModel::getId), formulatedMineralFertilizerRepository.findAllByUser_CargoOrderByIdAsc(Cargo.USUARIO_SUPREMO), FormulatedMineralFertilizerModel::getId);};}
    private List<SimpleMineralFertilizerModel> selectSimpleFertilizers(UserModel user, FertilizerSourceOption sourceOption){return switch (sourceOption) {case PRIVATE -> simpleMineralFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user); case PUBLIC -> simpleMineralFertilizerRepository.findAllByPublicoTrueOrderByNameAsc(); case DEFAULT -> simpleMineralFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO); case BOTH -> dedup(dedup(simpleMineralFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user), simpleMineralFertilizerRepository.findAllByPublicoTrueOrderByNameAsc(), SimpleMineralFertilizerModel::getId), simpleMineralFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO), SimpleMineralFertilizerModel::getId);};}
    private List<OrganicFertilizerModel> selectOrganicFertilizers(UserModel user, FertilizerSourceOption sourceOption){return switch (sourceOption) {case PRIVATE -> organicFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user); case PUBLIC -> organicFertilizerRepository.findAllByPublicoTrueOrderByNameAsc(); case DEFAULT -> organicFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO); case BOTH -> dedup(dedup(organicFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user), organicFertilizerRepository.findAllByPublicoTrueOrderByNameAsc(), OrganicFertilizerModel::getId), organicFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO), OrganicFertilizerModel::getId);};}
    private List<OrganoMineralFertilizerModel> selectOrganoMineralFertilizers(UserModel user, FertilizerSourceOption sourceOption){return switch (sourceOption) {case PRIVATE -> organoMineralFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user); case PUBLIC -> organoMineralFertilizerRepository.findAllByPublicoTrueOrderByNameAsc(); case DEFAULT -> organoMineralFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO); case BOTH -> dedup(dedup(organoMineralFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user), organoMineralFertilizerRepository.findAllByPublicoTrueOrderByNameAsc(), OrganoMineralFertilizerModel::getId), organoMineralFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO), OrganoMineralFertilizerModel::getId);};}
    private List<GreenFertilizerModel> selectGreenFertilizers(UserModel user, FertilizerSourceOption sourceOption){return switch (sourceOption) {case PRIVATE -> greenFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user); case PUBLIC -> greenFertilizerRepository.findAllByPublicoTrueOrderByNameAsc(); case DEFAULT -> greenFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO); case BOTH -> dedup(dedup(greenFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user), greenFertilizerRepository.findAllByPublicoTrueOrderByNameAsc(), GreenFertilizerModel::getId), greenFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO), GreenFertilizerModel::getId);};}
    private List<BioFertilizerModel> selectBioFertilizers(UserModel user, FertilizerSourceOption sourceOption){return switch (sourceOption) {case PRIVATE -> bioFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user); case PUBLIC -> bioFertilizerRepository.findAllByPublicoTrueOrderByNameAsc(); case DEFAULT -> bioFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO); case BOTH -> dedup(dedup(bioFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user), bioFertilizerRepository.findAllByPublicoTrueOrderByNameAsc(), BioFertilizerModel::getId), bioFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO), BioFertilizerModel::getId);};}
    private List<MineralFertilizerModel> selectFoliarMineralFertilizers(UserModel user, FertilizerSourceOption sourceOption){return switch (sourceOption) {case PRIVATE -> mineralFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user); case PUBLIC -> mineralFertilizerRepository.findAllByPublicoTrueOrderByNameAsc(); case DEFAULT -> mineralFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO); case BOTH -> dedup(dedup(mineralFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user), mineralFertilizerRepository.findAllByPublicoTrueOrderByNameAsc(), MineralFertilizerModel::getId), mineralFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO), MineralFertilizerModel::getId);};}
    private List<ChelatedFertilizerModel> selectChelatedFertilizers(UserModel user, FertilizerSourceOption sourceOption){return switch (sourceOption) {case PRIVATE -> chelatedFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user); case PUBLIC -> chelatedFertilizerRepository.findAllByPublicoTrueOrderByNameAsc(); case DEFAULT -> chelatedFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO); case BOTH -> dedup(dedup(chelatedFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user), chelatedFertilizerRepository.findAllByPublicoTrueOrderByNameAsc(), ChelatedFertilizerModel::getId), chelatedFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO), ChelatedFertilizerModel::getId);};}

    private Optional<NpkAlternativeSource> selectBestOrganicSource(UserModel user, FertilizerSourceOption sourceOption) {
        return selectOrganicFertilizers(user, sourceOption).stream()
                .filter(f -> nvl(f.getN()) > 0d || nvl(f.getP2O5()) > 0d || nvl(f.getK2O()) > 0d)
                .max(Comparator.comparing((OrganicFertilizerModel f) -> nvl(f.getN()) + nvl(f.getP2O5()) + nvl(f.getK2O()))
                        .thenComparing(f -> f.getId() == null ? 0L : f.getId()))
                .map(f -> new NpkAlternativeSource(f.getName(), nvl(f.getN()), nvl(f.getP2O5()), nvl(f.getK2O())));
    }

    private Optional<NpkAlternativeSource> selectBestOrganoMineralSource(UserModel user, FertilizerSourceOption sourceOption) {
        return selectOrganoMineralFertilizers(user, sourceOption).stream()
                .filter(f -> nvl(f.getN()) > 0d || nvl(f.getP2O5()) > 0d || nvl(f.getK2O()) > 0d)
                .max(Comparator.comparing((OrganoMineralFertilizerModel f) -> nvl(f.getN()) + nvl(f.getP2O5()) + nvl(f.getK2O()))
                        .thenComparing(f -> f.getId() == null ? 0L : f.getId()))
                .map(f -> new NpkAlternativeSource(f.getName(), nvl(f.getN()), nvl(f.getP2O5()), nvl(f.getK2O())));
    }

    private MicronutrientSourceSelection selectMicronutrientSource(UserModel user, FertilizerSourceOption sourceOption, AppliedMicronutrient micronutrient) {
        Optional<MineralFertilizerModel> mineral = selectFoliarMineralFertilizers(user, sourceOption).stream()
                .filter(f -> micronutrientPercentage(f, micronutrient) > 0d)
                .max(Comparator.comparing((MineralFertilizerModel f) -> micronutrientPercentage(f, micronutrient))
                        .thenComparing(f -> f.getId() == null ? 0L : f.getId()));
        if (mineral.isPresent()) {
            return new MicronutrientSourceSelection(mineral.get().getName(), "MINERAL COM MICRONUTRIENTE");
        }
        Optional<ChelatedFertilizerModel> chelated = selectChelatedFertilizers(user, sourceOption).stream()
                .filter(f -> micronutrientPercentage(f, micronutrient) > 0d)
                .max(Comparator.comparing((ChelatedFertilizerModel f) -> micronutrientPercentage(f, micronutrient))
                        .thenComparing(f -> f.getId() == null ? 0L : f.getId()));
        return chelated.map(f -> new MicronutrientSourceSelection(f.getName(), "QUELATADO COM MICRONUTRIENTE"))
                .orElseGet(() -> new MicronutrientSourceSelection(null, null));
    }

    private double micronutrientPercentage(MineralFertilizerModel fertilizer, AppliedMicronutrient micronutrient) {
        if (fertilizer == null || micronutrient == null) return 0d;
        return switch (micronutrient) {
            case B -> nvl(fertilizer.getB());
            case Cu -> nvl(fertilizer.getCu());
            case Fe -> nvl(fertilizer.getFe());
            case Mn -> nvl(fertilizer.getMn());
            case Mo -> nvl(fertilizer.getMo());
            case Zn -> nvl(fertilizer.getZn());
            case Ni -> 0d;
        };
    }

    private double micronutrientPercentage(ChelatedFertilizerModel fertilizer, AppliedMicronutrient micronutrient) {
        if (fertilizer == null || micronutrient == null) return 0d;
        return switch (micronutrient) {
            case B -> nvl(fertilizer.getB());
            case Cu -> nvl(fertilizer.getCu());
            case Fe -> nvl(fertilizer.getFe());
            case Mn -> nvl(fertilizer.getMn());
            case Mo -> nvl(fertilizer.getMo());
            case Zn -> nvl(fertilizer.getZn());
            case Ni -> 0d;
        };
    }

    private record NpkAlternativeSource(
            String name,
            double n,
            double p2o5,
            double k2o
    ) {
    }

    private record MicronutrientSourceSelection(
            String sourceName,
            String sourceType
    ) {
    }

    private record FertilityAnalysisSelection(
            SoilAnalysisModel soilAnalysis,
            Optional<FertilityAnalysisExtractModel> selectedExtract
    ) {
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendationCalculationResult {
        // Identification
        private String requesterName;
        private String requesterUsername;
        private String propertyName;
        private Long propertyId;
        private String plotIdentification;
        private Long plotId;
        private String cropName;
        private Integer annualCropFolderYear;
        private String recommendationType;
        private String limingCriteria;
        private LocalDateTime issuedAt;

        // General messages
        @Builder.Default
        private List<String> warnings = new ArrayList<>();
        @Builder.Default
        private List<String> diagnosticMessages = new ArrayList<>();
        @Builder.Default
        private List<String> fertilizationRows = new ArrayList<>();
        @Builder.Default
        private List<String> correctionMessages = new ArrayList<>();

        // Correctives
        private LimingRequirementResult limingRequirement;
        private GypsumRequirementResult gypsumRequirement;

        // Diagnosis
        @Builder.Default
        private List<SoilChemicalDiagnosisItem> soilChemicalDiagnosis = new ArrayList<>();
        @Builder.Default
        private List<SoilPhysicalDiagnosisItem> soilPhysicalDiagnosis = new ArrayList<>();
        @Builder.Default
        private List<SoilSalinityDiagnosisItem> soilSalinityDiagnosis = new ArrayList<>();
        @Builder.Default
        private List<FoliarDiagnosisItem> foliarDiagnosis = new ArrayList<>();

        // Source analysis references
        private Long physicalAnalysisId;
        private Long soilFertilityAnalysisId;
        private Long saturationExtractAnalysisId;
        private Long annualCropFolderId;
        private Long cropId;
        private Long foliarAnalysisId;

        // Source analysis summaries
        private String physicalAnalysisSummary;
        private String soilFertilityAnalysisSummary;
        private String saturationExtractAnalysisSummary;
        private String annualCropFolderSummary;
        private String cropSummary;
        private String foliarAnalysisSummary;

        // NPK requirements
        private Double requiredN;
        private Double requiredP2O5;
        private Double requiredK2O;
        private Long nitrogenRangeId;
        private Long phosphorusRangeId;
        private Long potassiumRangeId;

        // Fertilization results
        @Builder.Default
        private List<CorrectiveFertilizationRow> correctiveFertilizationRows = new ArrayList<>();
        @Builder.Default
        private List<FertilizationRecommendationRow> fertilizationRecommendationRows = new ArrayList<>();
        @Builder.Default
        private List<FertilizerSuggestion> fertilizerSuggestions = new ArrayList<>();
        @Builder.Default
        private List<NutrientBalanceRow> nutrientBalanceRows = new ArrayList<>();
        @Builder.Default
        private List<AlternativeFertilizationRecommendationRow> alternativeFertilizationRows = new ArrayList<>();
    }
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LimingRequirementResult {
        private String selectedCriteria;
        private String formula;
        private Map<String, Double> inputValues;
        private Double theoreticalRequirement;
        private Double prnt;
        private Double correctedRequirement;
        private String limestoneSource;
        private Double calculatedRequirement;
        private String unit;
        private List<String> warnings;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GypsumRequirementResult {
        private Boolean needed;
        private String criterion;
        private Map<String, Double> inputValues;
        private Double calculatedRequirement;
        private String unit;
        private String sourceName;
        private String sourceType;
        private Double commercialDose;
        private String commercialDoseUnit;
        private String sourceJustification;
        private String sourceLimitations;
        private String justification;
        private List<String> warnings;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SoilChemicalDiagnosisItem {
        private String attribute;
        private Double analyzedValue;
        private String unit;
        private String interpretation;
        private String usedCriterion;
        private String technicalObservation;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CorrectiveFertilizationRow {
        private String correctedAttribute;
        private String need;
        private String suggestedSource;
        private Double dose;
        private String doseUnit;
        private String calculationMemory;
        private String technicalWarning;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SoilPhysicalDiagnosisItem {
        private String attribute;
        private Double analyzedValue;
        private String unit;
        private String technicalObservation;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SoilSalinityDiagnosisItem {
        private String attribute;
        private Double analyzedValue;
        private String unit;
        private String interpretation;
        private String usedCriterion;
        private String technicalObservation;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FoliarDiagnosisItem {
        private String nutrient;
        private Double analyzedValue;
        private String unit;
        private String interpretation;
        private String usedCriterion;
        private String technicalObservation;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FertilizationRecommendationRow {
        private String phase;
        private String nutrients;
        private String suggestedFertilizer;
        private Double fertilizerQuantityKgHa;
        private String applicationMode;
        private String source;
        private Double providedN;
        private Double providedP2O5;
        private Double providedK2O;
        private Double balanceN;
        private Double balanceP2O5;
        private Double balanceK2O;
        private String limitingNutrient;
        private Double targetNeedKgHa;
        private Double productConcentrationPercent;
        private String calculationMemory;
        private String warning;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FertilizerSuggestion {
        private Long fertilizerId;
        private String fertilizerType;
        private String fertilizerName;
        private Double n;
        private Double p2o5;
        private Double k2o;
        private String reason;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NutrientBalanceRow {
        private String nutrient;
        private Double requiredTotalKgHa;
        private Double providedByPlantingKgHa;
        private Double recommendedCoverageKgHa;
        private Double providedByCoverageKgHa;
        private Double providedTotalKgHa;
        private Double finalBalanceKgHa;
        private String status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlternativeFertilizationRecommendationRow {
        private String sourceType;
        private String nutrientOrObjective;
        private String sourceName;
        private String dose;
        private String unit;
        private String justification;
        private String limitations;
    }
}
