package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.MenorMaiorTeores;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Nutriente;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.UnidadeTeor;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.RecommendationType;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.TechnicalTableGroup;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.TexturalClassification;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.composedAttributes.fertilityAnalysis.FertilityAnalysisUnit;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.MacronutrientsContent;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.MicronutrientsContent;
import com.migueltcc.fertintelligence.composedAttributes.physicalAnalysis.PhysicalAnalysisUnit;
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
import com.migueltcc.fertintelligence.composedAttributes.fertilityAnalysis.FertilityAnalysisUnit;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFertilizationTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels.BioFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels.ChelatedFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels.MineralFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.AvailablePAnionExchangeResinExtractorModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.AvailablePMehlich1ExtractorModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.AvailableSModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.CorrectiveK2OFertilizationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.CorrectiveP2O5FertilizationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.DiverseContentRangeModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.ExchangeableSodiumModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.MicronutrientDoseModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.SalinityInterpretationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.SulfurDoseModel;
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

    private static final String INCOMPATIBLE_CROP_AND_FERTILIZATION_TABLE_MESSAGE =
            "Cultura Anual e Tabela de Adubação de Culturas incompatíveis!";

    private final LimingRequirementCalculator limingRequirementCalculator;
    private final GypsumCalculationService gypsumCalculationService;
    private final SoilTextureClassificationService soilTextureClassificationService;
    private final NutrientFertilizationCalculationService nutrientFertilizationCalculationService;
    private final FertilizerOpportunityCostService fertilizerOpportunityCostService;

    private final PhysicalAnalysisExtractRepository physicalAnalysisExtractRepository;
    private final SoilAnalysisRepository soilAnalysisRepository;
    private final SaturationExtractAnalysisExtractRepository saturationExtractAnalysisExtractRepository;
    private final AnnualCropFolderRepository annualCropFolderRepository;
    private final CropRepository cropRepository;
    private final FoliarAnalysisRepository foliarAnalysisRepository;
    private final CropFertilizationTableRepository cropFertilizationTableRepository;
    private final FertilityAnalysisExtractRepository fertilityAnalysisExtractRepository;
    private final SoilFertilityInterpretationCriteriaTableRepository soilFertilityInterpretationCriteriaTableRepository;
    private final CropFoliarAnalysisInterpretationTableRepository cropFoliarAnalysisInterpretationTableRepository;
    private final CropFoliarAnalysisInterpretationTableLineRepository cropFoliarAnalysisInterpretationTableLineRepository;
    private final DiverseContentRangeRepository diverseContentRangeRepository;
    private final AvailablePMehlich1ExtractorRepository availablePMehlich1ExtractorRepository;
    private final AvailablePAnionExchangeResinExtractorRepository availablePAnionExchangeResinExtractorRepository;
    private final AvailableSRepository availableSRepository;
    private final SulfurDoseRepository sulfurDoseRepository;
    private final CorrectiveP2O5FertilizationRepository correctiveP2O5FertilizationRepository;
    private final CorrectiveK2OFertilizationRepository correctiveK2OFertilizationRepository;
    private final MicronutrientDoseRepository micronutrientDoseRepository;
    private final ExchangeableSodiumRepository exchangeableSodiumRepository;
    private final SalinityInterpretationRepository salinityInterpretationRepository;
    private final SimpleMineralFertilizerRepository simpleMineralFertilizerRepository;
    private final FormulatedMineralFertilizerRepository formulatedMineralFertilizerRepository;
    private final MicronutrientFertilizerSelectionService micronutrientFertilizerSelectionService;

    public RecommendationCalculationService(PhysicalAnalysisExtractRepository physicalAnalysisExtractRepository,
                                            SoilAnalysisRepository soilAnalysisRepository,
                                            SaturationExtractAnalysisExtractRepository saturationExtractAnalysisExtractRepository,
                                            AnnualCropFolderRepository annualCropFolderRepository,
                                            CropRepository cropRepository,
                                            FoliarAnalysisRepository foliarAnalysisRepository,
                                            CropFertilizationTableRepository cropFertilizationTableRepository,
                                            FertilityAnalysisExtractRepository fertilityAnalysisExtractRepository,
                                            SoilFertilityInterpretationCriteriaTableRepository soilFertilityInterpretationCriteriaTableRepository,
                                            CropFoliarAnalysisInterpretationTableRepository cropFoliarAnalysisInterpretationTableRepository,
                                            CropFoliarAnalysisInterpretationTableLineRepository cropFoliarAnalysisInterpretationTableLineRepository,
                                            DiverseContentRangeRepository diverseContentRangeRepository,
                                            AvailablePMehlich1ExtractorRepository availablePMehlich1ExtractorRepository,
                                            AvailablePAnionExchangeResinExtractorRepository availablePAnionExchangeResinExtractorRepository,
                                            AvailableSRepository availableSRepository,
                                            SulfurDoseRepository sulfurDoseRepository,
                                            CorrectiveP2O5FertilizationRepository correctiveP2O5FertilizationRepository,
                                            CorrectiveK2OFertilizationRepository correctiveK2OFertilizationRepository,
                                            MicronutrientDoseRepository micronutrientDoseRepository,
                                            ExchangeableSodiumRepository exchangeableSodiumRepository,
                                            SalinityInterpretationRepository salinityInterpretationRepository,
                                            SimpleMineralFertilizerRepository simpleMineralFertilizerRepository,
                                            FormulatedMineralFertilizerRepository formulatedMineralFertilizerRepository,
                                            MicronutrientFertilizerSelectionService micronutrientFertilizerSelectionService,
                                            LimingRequirementCalculator limingRequirementCalculator,
                                            GypsumCalculationService gypsumCalculationService,
                                            SoilTextureClassificationService soilTextureClassificationService,
                                            NutrientFertilizationCalculationService nutrientFertilizationCalculationService,
                                            FertilizerOpportunityCostService fertilizerOpportunityCostService) {
        this.physicalAnalysisExtractRepository = physicalAnalysisExtractRepository;
        this.soilAnalysisRepository = soilAnalysisRepository;
        this.saturationExtractAnalysisExtractRepository = saturationExtractAnalysisExtractRepository;
        this.annualCropFolderRepository = annualCropFolderRepository;
        this.cropRepository = cropRepository;
        this.foliarAnalysisRepository = foliarAnalysisRepository;
        this.cropFertilizationTableRepository = cropFertilizationTableRepository;
        this.fertilityAnalysisExtractRepository = fertilityAnalysisExtractRepository;
        this.soilFertilityInterpretationCriteriaTableRepository = soilFertilityInterpretationCriteriaTableRepository;
        this.cropFoliarAnalysisInterpretationTableRepository = cropFoliarAnalysisInterpretationTableRepository;
        this.cropFoliarAnalysisInterpretationTableLineRepository = cropFoliarAnalysisInterpretationTableLineRepository;
        this.diverseContentRangeRepository = diverseContentRangeRepository;
        this.availablePMehlich1ExtractorRepository = availablePMehlich1ExtractorRepository;
        this.availablePAnionExchangeResinExtractorRepository = availablePAnionExchangeResinExtractorRepository;
        this.availableSRepository = availableSRepository;
        this.sulfurDoseRepository = sulfurDoseRepository;
        this.correctiveP2O5FertilizationRepository = correctiveP2O5FertilizationRepository;
        this.correctiveK2OFertilizationRepository = correctiveK2OFertilizationRepository;
        this.micronutrientDoseRepository = micronutrientDoseRepository;
        this.exchangeableSodiumRepository = exchangeableSodiumRepository;
        this.salinityInterpretationRepository = salinityInterpretationRepository;
        this.simpleMineralFertilizerRepository = simpleMineralFertilizerRepository;
        this.formulatedMineralFertilizerRepository = formulatedMineralFertilizerRepository;
        this.micronutrientFertilizerSelectionService = micronutrientFertilizerSelectionService;
        this.limingRequirementCalculator = limingRequirementCalculator;
        this.gypsumCalculationService = gypsumCalculationService;
        this.soilTextureClassificationService = soilTextureClassificationService;
        this.nutrientFertilizationCalculationService = nutrientFertilizationCalculationService;
        this.fertilizerOpportunityCostService = fertilizerOpportunityCostService;
    }

    public RecommendationCalculationResult calculate(RecommendationCreateRequestDto dto, UserModel user, PropertyModel property, PlotModel plot) {
        List<String> diagnostics = new ArrayList<>();
        FertilizerSourceOption sourceOption = dto.getOrigemAdubos() != null ? dto.getOrigemAdubos() : FertilizerSourceOption.BOTH;
        List<String> warnings = new ArrayList<>();

        RecommendationInputs inputs = loadRecommendationInputs(dto, user, plot, warnings);
        validateRecommendationInputs(inputs, plot, warnings);

        DiagnosesContext diagnoses = buildRecommendationDiagnoses(dto, inputs, user, sourceOption, warnings);
        FertilizationRecommendationContext recommendations = shouldRunFertilization(dto)
                ? buildFertilizationRecommendations(dto, inputs, user, sourceOption, warnings, diagnoses.chemicalDiagnosis(),
                diagnoses.foliarDiagnosis(), diagnoses.correctiveFertilizationRows())
                : emptyFertilizationRecommendations();
        FertilizerOpportunityCostService.OpportunityCostResult opportunityCost = shouldRunFertilization(dto)
                ? fertilizerOpportunityCostService.calculate(user, sourceOption, formulatedFertilizerIds(recommendations), warnings)
                : null;

        warnings.add("Valide os parâmetros com engenheiro agrônomo responsável antes de uso operacional.");

        return buildCalculationResult(dto, user, property, plot, diagnostics, warnings, inputs, diagnoses, recommendations, opportunityCost);
    }

    private RecommendationInputs loadRecommendationInputs(RecommendationCreateRequestDto dto, UserModel user, PlotModel plot, List<String> warnings) {
        PhysicalAnalysisSelection physicalSelection = findPhysicalAnalysisSelection(dto.getPhysicalAnalysisId(), dto.getPhysicalAnalysisExtractId(), warnings);
        PhysicalAnalysisExtractModel physicalAnalysis = physicalSelection.primaryExtract().orElse(null);
        FertilityAnalysisSelection soilFertilitySelection = findSoilFertilitySelectionByIdOrThrow(dto.getSoilFertilityAnalysisId(), plot, warnings);
        SoilAnalysisModel soilFertilityAnalysis = soilFertilitySelection.soilAnalysis();
        SaturationAnalysisSelection saturationSelection = findSaturationAnalysisSelection(dto.getSaturationExtractAnalysisId(), dto.getSaturationExtractAnalysisExtractId(), warnings);
        SaturationExtractAnalysisExtractModel saturationExtractAnalysis = saturationSelection.primaryExtract().orElse(null);
        AnnualCropFolderModel annualCropFolder = findAnnualCropFolderByIdOrThrow(dto.getAnnualCropFolderId());
        CropModel crop = resolveCropFromRecommendationContext(dto, annualCropFolder, warnings).orElse(null);
        CropFertilizationTableModel cropFertilizationTable = findCropFertilizationTableBySelectionOrThrow(
                dto.getCropFertilizationTableId(), dto.getCropFertilizationTableGroup(), user);
        SoilFertilityInterpretationCriteriaTableModel soilInterpretationTable = findSoilFertilityInterpretationTableBySelectionOrThrow(
                dto.getSoilFertilityInterpretationCriteriaTableId(), dto.getSoilFertilityInterpretationCriteriaTableGroup(), user);
        CropFoliarAnalysisInterpretationTableModel foliarInterpretationTable = findOptionalCropFoliarAnalysisInterpretationTable(
                dto.getCropFoliarAnalysisInterpretationTableId(), dto.getCropFoliarAnalysisInterpretationTableGroup(), user, warnings).orElse(null);
        Optional<FoliarAnalysisModel> foliarAnalysis = crop != null ? findLatestFoliarAnalysis(crop) : Optional.empty();
        Optional<FertilityAnalysisExtractModel> fertilityExtract = soilFertilitySelection.primaryExtract();

        return new RecommendationInputs(
                physicalSelection.analysis().orElse(null), physicalAnalysis, physicalSelection.extracts(),
                soilFertilityAnalysis, fertilityExtract, soilFertilitySelection.extracts(),
                saturationSelection.analysis().orElse(null), saturationExtractAnalysis, saturationSelection.extracts(),
                annualCropFolder, crop, cropFertilizationTable, soilInterpretationTable,
                foliarInterpretationTable, foliarAnalysis);
    }

    private void validateRecommendationInputs(RecommendationInputs inputs, PlotModel plot, List<String> warnings) {
        if (inputs.physicalAnalysis() != null) {
            validateSamePlot(resolvePlot(inputs.physicalAnalysis()), plot, "O extrato de análise física selecionado não pertence ao talhão informado.");
        }
        validateSamePlot(inputs.soilFertilityAnalysis().getPlot(), plot, "A análise de fertilidade selecionada não pertence ao talhão informado.");
        if (inputs.saturationExtractAnalysis() != null) {
            validateSamePlot(resolvePlot(inputs.saturationExtractAnalysis()), plot, "O extrato de análise de saturação selecionado não pertence ao talhão informado.");
        }
        validateSamePlot(inputs.annualCropFolder().getPlot(), plot, "A pasta de cultura anual selecionada não pertence ao talhão informado.");
        if (inputs.crop() == null) {
            warnings.add("Pasta de cultura anual sem cultura resolvida; validação Cultura x Tabela não foi executada.");
            return;
        }
        if (inputs.crop().getFolder() == null || !Objects.equals(inputs.crop().getFolder().getId(), inputs.annualCropFolder().getId())) {
            throw new IllegalArgumentException("A cultura selecionada não pertence à pasta de cultura anual informada.");
        }
        validateCropCompatibleWithFertilizationTable(inputs.crop(), inputs.cropFertilizationTable());
    }

    private void validateCropCompatibleWithFertilizationTable(CropModel crop, CropFertilizationTableModel cropFertilizationTable) {
        if (crop == null || cropFertilizationTable == null || crop.getName() == null || cropFertilizationTable.getCrop_common_name() == null) {
            throw new IllegalArgumentException("Não foi possível validar compatibilidade entre cultura anual e tabela de adubação por ausência de nome da cultura.");
        }
        if (!Objects.equals(crop.getName(), cropFertilizationTable.getCrop_common_name())) {
            throw new IllegalArgumentException(INCOMPATIBLE_CROP_AND_FERTILIZATION_TABLE_MESSAGE);
        }
    }

    private DiagnosesContext buildRecommendationDiagnoses(RecommendationCreateRequestDto dto,
                                                          RecommendationInputs inputs,
                                                          UserModel user,
                                                          FertilizerSourceOption sourceOption,
                                                          List<String> warnings) {
        TexturalClassification texturalClassification = dto.getTexturalClassification() != null
                ? dto.getTexturalClassification()
                : TexturalClassification.BRASILEIRO;
        PhysicalDiagnosis physicalDiagnosis = buildSoilPhysicalDiagnosis(inputs.physicalAnalysis(), texturalClassification, warnings);
        String physicalSummary = buildPhysicalAnalysisSummary(inputs, physicalDiagnosis.summary());
        String soilFertilitySummary = buildFertilityAnalysisSummary(inputs);
        String cropSummary = inputs.crop() != null
                ? "Cultura considerada conforme cadastro da pasta anual."
                : "Cultura da pasta anual não encontrada; campos dependentes da cultura foram tratados como ausentes.";
        List<FoliarDiagnosisItem> foliarDiagnosis = buildFoliarDiagnosis(inputs.foliarAnalysis(), inputs.crop(), inputs.foliarInterpretationTable(), warnings);
        String foliarSummary = buildFoliarSummary(inputs.foliarAnalysis(), foliarDiagnosis);

        List<String> correctionMessages = buildCorrectionMessages(inputs.fertilityExtract(), Optional.ofNullable(inputs.saturationExtractAnalysis()), warnings);
        LimingRequirementResult limingRequirement = limingRequirementCalculator.calculate(
                dto, inputs.fertilityExtract(), inputs.physicalAnalysis(), inputs.cropFertilizationTable(), warnings);
        List<PhysicalAnalysisExtractModel> gypsumPhysicalExtracts = selectGypsumPhysicalExtracts(inputs.physicalAnalysisExtracts());
        List<FertilityAnalysisExtractModel> gypsumFertilityExtracts = selectGypsumFertilityExtracts(inputs.fertilityAnalysisExtracts());
        GypsumRequirementResult gypsumRequirement = gypsumCalculationService.calculate(
                gypsumFertilityExtracts, gypsumPhysicalExtracts,
                inputs.cropFertilizationTable(), inputs.soilInterpretationTable(), user, sourceOption, warnings);
        List<SoilChemicalDiagnosisItem> chemicalDiagnosis = buildSoilChemicalDiagnosis(
                inputs.fertilityExtract(), inputs.physicalAnalysis(), inputs.soilInterpretationTable(), warnings);
        List<CorrectiveFertilizationRow> correctiveFertilizationRows = shouldRunFertilization(dto)
                ? buildCorrectiveFertilizationRows(
                dto, chemicalDiagnosis, inputs.fertilityExtract(), inputs.physicalAnalysis(), inputs.cropFertilizationTable(),
                inputs.soilInterpretationTable(), user, sourceOption, warnings)
                : List.of();
        SalinityDiagnosis salinityDiagnosis = buildSalinityAndSodicityDiagnosis(
                inputs.saturationExtractAnalysis(), inputs.fertilityExtract(), inputs.soilInterpretationTable(), warnings);
        String saturationSummary = buildSaturationAnalysisSummary(inputs, salinityDiagnosis.summary());

        return new DiagnosesContext(
                physicalDiagnosis, physicalSummary, soilFertilitySummary, cropSummary, foliarDiagnosis, foliarSummary,
                correctionMessages, limingRequirement, gypsumRequirement, chemicalDiagnosis, correctiveFertilizationRows, salinityDiagnosis,
                saturationSummary);
    }

    private FertilizationRecommendationContext buildFertilizationRecommendations(RecommendationCreateRequestDto dto,
                                                                                 RecommendationInputs inputs,
                                                                                 UserModel user,
                                                                                 FertilizerSourceOption sourceOption,
                                                                                 List<String> warnings,
                                                                                 List<SoilChemicalDiagnosisItem> chemicalDiagnosis,
                                                                                 List<FoliarDiagnosisItem> foliarDiagnosis,
                                                                                 List<CorrectiveFertilizationRow> correctiveFertilizationRows) {
        return nutrientFertilizationCalculationService.calculate(
                inputs.cropFertilizationTable(), inputs.crop(), inputs.fertilityExtract(), Optional.ofNullable(inputs.physicalAnalysis()), inputs.soilInterpretationTable(),
                user, sourceOption, dto.getUseOrganicFertilizer(), dto.getOrganicFertilizerReferenceNutrient(),
                dto.getUseOrganoMineralFertilizer(), dto.getUseGreenFertilizer(), dto.getGreenFertilizerSpecies(), dto.getGreenFertilizerGreenMass(),
                dto.getGreenFertilizerMoisturePercentage(), dto.getGreenFertilizerDryMass(),
                dto.getUseBioFertilizer(),
                warnings, chemicalDiagnosis, foliarDiagnosis, correctiveFertilizationRows);
    }

    private boolean shouldRunFertilization(RecommendationCreateRequestDto dto) {
        return dto == null || dto.getRecommendationType() != RecommendationType.ACIDITY_OR_SALINITY_CORRECTION;
    }

    private FertilizationRecommendationContext emptyFertilizationRecommendations() {
        return new FertilizationRecommendationContext(
                List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of(),
                null, null, null, null, null, null, null);
    }

    private RecommendationCalculationResult buildCalculationResult(RecommendationCreateRequestDto dto,
                                                                   UserModel user,
                                                                   PropertyModel property,
                                                                   PlotModel plot,
                                                                   List<String> diagnostics,
                                                                   List<String> warnings,
                                                                   RecommendationInputs inputs,
                                                                   DiagnosesContext diagnoses,
                                                                   FertilizationRecommendationContext recommendations,
                                                                   FertilizerOpportunityCostService.OpportunityCostResult opportunityCost) {
        return RecommendationCalculationResult.builder()
                .requesterName(user != null ? user.getName() : null)
                .requesterUsername(user != null ? user.getUsername() : null)
                .propertyName(property != null ? property.getNome() : null)
                .propertyId(property != null ? property.getId() : null)
                .plotIdentification(plot != null ? plot.getIdentification() : null)
                .plotId(plot != null ? plot.getId() : null)
                .cropName(resolveCropNameForResult(inputs))
                .cropUsedAreaInThePlot(inputs.crop() != null ? inputs.crop().getUsedAreaInThePlot() : null)
                .cropPlantingDate(inputs.crop() != null ? inputs.crop().getPlantingDate() : null)
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
                .micronutrientFertilizerRows(recommendations.micronutrientFertilizerRows())
                .plantingFormulatedFertilizerRows(recommendations.plantingFormulatedFertilizerRows())
                .coverageFormulatedFertilizerRows(recommendations.coverageFormulatedFertilizerRows())
                .opportunityCostNutrientPrices(toNutrientPriceRows(opportunityCost))
                .opportunityCostDecisionRows(toOpportunityCostDecisionRows(opportunityCost))
                .requiredN(recommendations.requiredN()).requiredP2O5(recommendations.requiredP2O5()).requiredK2O(recommendations.requiredK2O()).requiredS(recommendations.requiredS())
                .nitrogenRangeId(recommendations.nRangeId()).phosphorusRangeId(recommendations.pRangeId()).potassiumRangeId(recommendations.kRangeId())
                .physicalAnalysisId(inputs.physicalAnalysisSourceId())
                .soilFertilityAnalysisId(inputs.soilFertilityAnalysis().getId())
                .saturationExtractAnalysisId(inputs.saturationAnalysisSourceId())
                .annualCropFolderId(inputs.annualCropFolder().getId())
                .cropId(inputs.crop() != null ? inputs.crop().getId() : null).foliarAnalysisId(inputs.foliarAnalysis().map(FoliarAnalysisModel::getId).orElse(null))
                .physicalAnalysisSummary(diagnoses.physicalSummary()).soilFertilityAnalysisSummary(diagnoses.soilFertilitySummary()).saturationExtractAnalysisSummary(diagnoses.saturationSummary())
                .annualCropFolderSummary("Pasta de cultura anual considerada na recomendação.")
                .cropSummary(diagnoses.cropSummary()).foliarAnalysisSummary(diagnoses.foliarSummary()).build();
    }

    private List<Long> formulatedFertilizerIds(FertilizationRecommendationContext recommendations) {
        List<Long> ids = new ArrayList<>();
        if (recommendations != null && recommendations.plantingFormulatedFertilizerRows() != null) {
            recommendations.plantingFormulatedFertilizerRows().stream()
                    .map(PlantingFormulatedFertilizerRecommendationRow::getFertilizerId)
                    .filter(Objects::nonNull)
                    .forEach(ids::add);
        }
        if (recommendations != null && recommendations.coverageFormulatedFertilizerRows() != null) {
            recommendations.coverageFormulatedFertilizerRows().stream()
                    .map(CoverageFormulatedFertilizerRecommendationRow::getFertilizerId)
                    .filter(Objects::nonNull)
                    .forEach(ids::add);
        }
        return ids;
    }

    private List<OpportunityCostNutrientPriceRow> toNutrientPriceRows(FertilizerOpportunityCostService.OpportunityCostResult result) {
        if (result == null || result.nutrientPrices() == null || result.nutrientPrices().isEmpty()) {
            return List.of();
        }
        return result.nutrientPrices().values().stream()
                .map(price -> OpportunityCostNutrientPriceRow.builder()
                        .nutrient(price.nutrient().name())
                        .pricePerKg(price.pricePerKg())
                        .sourceName(price.fertilizerName())
                        .sourceType(price.fertilizerType())
                        .commercialWeightKg(price.commercialWeightKg())
                        .commercialPrice(price.commercialPrice())
                        .build())
                .toList();
    }

    private List<OpportunityCostDecisionRow> toOpportunityCostDecisionRows(FertilizerOpportunityCostService.OpportunityCostResult result) {
        if (result == null || result.decisions() == null || result.decisions().isEmpty()) {
            return List.of();
        }
        return result.decisions().stream()
                .map(decision -> OpportunityCostDecisionRow.builder()
                        .fertilizerName(decision.fertilizerName())
                        .category(decision.category())
                        .commercialPriceLabel(decision.commercialPriceLabel())
                        .commercialPrice(decision.commercialPrice())
                        .opportunityPriceLabel(decision.opportunityPriceLabel())
                        .opportunityPrice(decision.opportunityPrice())
                        .commercialWeightKg(decision.commercialWeightKg())
                        .ratio(decision.ratio())
                        .decision(decision.decision())
                        .indeterminate(decision.indeterminate())
                        .justification(decision.justification())
                        .contributionSummary(decision.contributionSummary())
                        .build())
                .toList();
    }

    private String resolveCropNameForResult(RecommendationInputs inputs) {
        if (inputs.crop() != null && inputs.crop().getName() != null) {
            return inputs.crop().getName().name();
        }
        if (inputs.cropFertilizationTable() != null && inputs.cropFertilizationTable().getCrop_common_name() != null) {
            return inputs.cropFertilizationTable().getCrop_common_name().name();
        }
        return null;
    }

    private record RecommendationInputs(
            SoilAnalysisModel physicalAnalysisSource,
            PhysicalAnalysisExtractModel physicalAnalysis,
            List<PhysicalAnalysisExtractModel> physicalAnalysisExtracts,
            SoilAnalysisModel soilFertilityAnalysis,
            Optional<FertilityAnalysisExtractModel> fertilityExtract,
            List<FertilityAnalysisExtractModel> fertilityAnalysisExtracts,
            SoilAnalysisModel saturationAnalysisSource,
            SaturationExtractAnalysisExtractModel saturationExtractAnalysis,
            List<SaturationExtractAnalysisExtractModel> saturationAnalysisExtracts,
            AnnualCropFolderModel annualCropFolder,
            CropModel crop,
            CropFertilizationTableModel cropFertilizationTable,
            SoilFertilityInterpretationCriteriaTableModel soilInterpretationTable,
            CropFoliarAnalysisInterpretationTableModel foliarInterpretationTable,
            Optional<FoliarAnalysisModel> foliarAnalysis) {
        private Long physicalAnalysisSourceId() {
            if (physicalAnalysisSource != null) return physicalAnalysisSource.getId();
            return physicalAnalysis != null ? physicalAnalysis.getId() : null;
        }

        private Long saturationAnalysisSourceId() {
            if (saturationAnalysisSource != null) return saturationAnalysisSource.getId();
            return saturationExtractAnalysis != null ? saturationExtractAnalysis.getId() : null;
        }
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
            SalinityDiagnosis salinityDiagnosis,
            String saturationSummary) {
    }

    private double round2(double v) {
        return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private Optional<FertilityAnalysisExtractModel> findLatestFertilityExtract(SoilAnalysisModel soil) {
        return fertilityAnalysisExtractRepository.findAll().stream()
                .filter(extract -> belongsToSoilAnalysis(extract, soil))
                .max(Comparator.comparing(FertilityAnalysisExtractModel::getId));
    }

    private List<FertilityAnalysisExtractModel> findFertilityExtractsByAnalysis(SoilAnalysisModel soil) {
        if (soil == null) return List.of();
        List<FertilityAnalysisExtractModel> extracts = new ArrayList<>();
        extracts.addAll(fertilityAnalysisExtractRepository.findAllByRangeExtractAnalysis(soil));
        extracts.addAll(fertilityAnalysisExtractRepository.findAllByLayerExtractAnalysis(soil));
        return sortFertilityExtracts(extracts);
    }

    private List<PhysicalAnalysisExtractModel> findPhysicalExtractsByAnalysis(SoilAnalysisModel soil) {
        if (soil == null) return List.of();
        List<PhysicalAnalysisExtractModel> extracts = new ArrayList<>();
        extracts.addAll(physicalAnalysisExtractRepository.findAllByRangeExtractAnalysis(soil));
        extracts.addAll(physicalAnalysisExtractRepository.findAllByLayerExtractAnalysis(soil));
        return sortPhysicalExtracts(extracts);
    }

    private List<SaturationExtractAnalysisExtractModel> findSaturationExtractsByAnalysis(SoilAnalysisModel soil) {
        if (soil == null) return List.of();
        List<SaturationExtractAnalysisExtractModel> extracts = new ArrayList<>();
        extracts.addAll(saturationExtractAnalysisExtractRepository.findAllByRangeExtractAnalysis(soil));
        extracts.addAll(saturationExtractAnalysisExtractRepository.findAllByLayerExtractAnalysis(soil));
        return sortSaturationExtracts(extracts);
    }

    private boolean belongsToSoilAnalysis(FertilityAnalysisExtractModel extract, SoilAnalysisModel soil) {
        if (extract == null || soil == null) return false;
        return (extract.getRangeExtract() != null
                && extract.getRangeExtract().getAnalysis() != null
                && Objects.equals(extract.getRangeExtract().getAnalysis().getId(), soil.getId()))
                || (extract.getLayerExtract() != null
                && extract.getLayerExtract().getAnalysis() != null
                && Objects.equals(extract.getLayerExtract().getAnalysis().getId(), soil.getId()));
    }

    private String buildPhysicalAnalysisSummary(RecommendationInputs inputs, String baseSummary) {
        if (inputs.physicalAnalysisSource() == null && inputs.physicalAnalysis() == null) {
            return baseSummary;
        }
        return buildAnalysisSummary(
                "Análise física",
                inputs.physicalAnalysisSource(),
                inputs.physicalAnalysisExtracts(),
                inputs.physicalAnalysis(),
                baseSummary,
                this::describePhysicalExtract);
    }

    private String buildFertilityAnalysisSummary(RecommendationInputs inputs) {
        return buildAnalysisSummary(
                "Análise de fertilidade",
                inputs.soilFertilityAnalysis(),
                inputs.fertilityAnalysisExtracts(),
                inputs.fertilityExtract().orElse(null),
                "Análise de fertilidade considerada na recomendação.",
                this::describeFertilityExtract);
    }

    private String buildSaturationAnalysisSummary(RecommendationInputs inputs, String baseSummary) {
        if (inputs.saturationAnalysisSource() == null && inputs.saturationExtractAnalysis() == null) {
            return baseSummary;
        }
        return buildAnalysisSummary(
                "Análise de extrato de saturação",
                inputs.saturationAnalysisSource(),
                inputs.saturationAnalysisExtracts(),
                inputs.saturationExtractAnalysis(),
                baseSummary,
                this::describeSaturationExtract);
    }

    private <T> String buildAnalysisSummary(String label,
                                            SoilAnalysisModel analysis,
                                            List<T> extracts,
                                            T primary,
                                            String baseSummary,
                                            Function<T, String> describer) {
        List<T> safeExtracts = extracts != null ? extracts : List.of();
        StringBuilder summary = new StringBuilder();
        if (analysis != null) {
            summary.append(label).append(" completa ID ").append(analysis.getId()).append(" considerada.");
        } else {
            summary.append(baseSummary);
        }
        if (safeExtracts.isEmpty()) {
            summary.append(" Nenhum extrato/camada/intervalo associado foi encontrado.");
            return summary.toString();
        }
        summary.append(" Extratos carregados: ");
        for (int i = 0; i < safeExtracts.size(); i++) {
            if (i > 0) summary.append("; ");
            T extract = safeExtracts.get(i);
            summary.append(describer.apply(extract));
            if (extract == primary) {
                summary.append(" (principal para regras que exigem extrato único)");
            }
        }
        if (primary == null) {
            summary.append(". Nenhum extrato/camada que compreenda 0-20 cm foi encontrado para uso como referência principal.");
        }
        summary.append(". Regra backend: cálculos tabulares usam todos os extratos carregados como contexto do laudo; calagem, diagnóstico principal e rotinas quantitativas que aceitam apenas um extrato usam somente a referência 0-20 cm, quando disponível.");
        return summary.toString();
    }

    private SoilAnalysisModel resolveSoilAnalysis(FertilityAnalysisExtractModel extract) {
        if (extract.getRangeExtract() != null && extract.getRangeExtract().getAnalysis() != null) {
            return extract.getRangeExtract().getAnalysis();
        }
        if (extract.getLayerExtract() != null && extract.getLayerExtract().getAnalysis() != null) {
            return extract.getLayerExtract().getAnalysis();
        }
        throw new IllegalArgumentException("Extrato de análise de fertilidade não possui análise de solo associada.");
    }

    private SoilAnalysisModel resolveSoilAnalysis(PhysicalAnalysisExtractModel extract) {
        if (extract.getRangeExtract() != null && extract.getRangeExtract().getAnalysis() != null) {
            return extract.getRangeExtract().getAnalysis();
        }
        if (extract.getLayerExtract() != null && extract.getLayerExtract().getAnalysis() != null) {
            return extract.getLayerExtract().getAnalysis();
        }
        throw new IllegalArgumentException("Extrato de análise física não possui análise de solo associada.");
    }

    private SoilAnalysisModel resolveSoilAnalysis(SaturationExtractAnalysisExtractModel extract) {
        if (extract.getRangeExtract() != null && extract.getRangeExtract().getAnalysis() != null) {
            return extract.getRangeExtract().getAnalysis();
        }
        if (extract.getLayerExtract() != null && extract.getLayerExtract().getAnalysis() != null) {
            return extract.getLayerExtract().getAnalysis();
        }
        throw new IllegalArgumentException("Extrato de análise de saturação não possui análise de solo associada.");
    }

    private List<PhysicalAnalysisExtractModel> sortPhysicalExtracts(List<PhysicalAnalysisExtractModel> extracts) {
        return extracts.stream().sorted(Comparator.comparing((PhysicalAnalysisExtractModel extract) -> depthStart(extract))
                .thenComparing(extract -> depthEnd(extract))
                .thenComparing(extract -> extract.getId() == null ? Long.MAX_VALUE : extract.getId())).toList();
    }

    private List<FertilityAnalysisExtractModel> sortFertilityExtracts(List<FertilityAnalysisExtractModel> extracts) {
        return extracts.stream().sorted(Comparator.comparing((FertilityAnalysisExtractModel extract) -> depthStart(extract))
                .thenComparing(extract -> depthEnd(extract))
                .thenComparing(extract -> extract.getId() == null ? Long.MAX_VALUE : extract.getId())).toList();
    }

    private List<SaturationExtractAnalysisExtractModel> sortSaturationExtracts(List<SaturationExtractAnalysisExtractModel> extracts) {
        return extracts.stream().sorted(Comparator.comparing((SaturationExtractAnalysisExtractModel extract) -> depthStart(extract))
                .thenComparing(extract -> depthEnd(extract))
                .thenComparing(extract -> extract.getId() == null ? Long.MAX_VALUE : extract.getId())).toList();
    }

    private Optional<PhysicalAnalysisExtractModel> selectPrimaryPhysicalExtract(List<PhysicalAnalysisExtractModel> extracts,
                                                                                Long analysisId,
                                                                                List<String> warnings) {
        Optional<PhysicalAnalysisExtractModel> selected = extracts.stream()
                .filter(this::coversZeroToTwenty)
                .findFirst();
        if (selected.isEmpty() && !extracts.isEmpty()) {
            warnings.add("Análise física ID " + analysisId
                    + " não possui extrato/camada que compreenda 0-20 cm; diagnóstico físico principal e regras de calagem dependentes da análise física foram tratados como indisponíveis.");
        }
        return selected;
    }

    private Optional<FertilityAnalysisExtractModel> selectPrimaryFertilityExtract(List<FertilityAnalysisExtractModel> extracts,
                                                                                  Long analysisId,
                                                                                  List<String> warnings) {
        Optional<FertilityAnalysisExtractModel> selected = extracts.stream()
                .filter(this::coversZeroToTwenty)
                .findFirst();
        if (selected.isEmpty() && !extracts.isEmpty()) {
            warnings.add("Análise de fertilidade ID " + analysisId
                    + " não possui extrato/camada que compreenda 0-20 cm; calagem, diagnóstico químico principal e adubação principal foram tratados como indisponíveis.");
        }
        return selected;
    }

    private Optional<SaturationExtractAnalysisExtractModel> selectPrimarySaturationExtract(List<SaturationExtractAnalysisExtractModel> extracts,
                                                                                           Long analysisId,
                                                                                           List<String> warnings) {
        Optional<SaturationExtractAnalysisExtractModel> selected = extracts.stream()
                .filter(this::coversZeroToTwenty)
                .findFirst();
        if (selected.isEmpty() && !extracts.isEmpty()) {
            warnings.add("Análise de extrato de saturação ID " + analysisId
                    + " não possui extrato/camada que compreenda 0-20 cm; diagnóstico principal de salinidade/sodicidade foi tratado como indisponível.");
        }
        return selected;
    }

    private List<PhysicalAnalysisExtractModel> selectGypsumPhysicalExtracts(List<PhysicalAnalysisExtractModel> extracts) {
        return (extracts != null ? extracts : List.<PhysicalAnalysisExtractModel>of()).stream()
                .filter(this::coversGypsumSubsurfaceLayer)
                .toList();
    }

    private List<FertilityAnalysisExtractModel> selectGypsumFertilityExtracts(List<FertilityAnalysisExtractModel> extracts) {
        return (extracts != null ? extracts : List.<FertilityAnalysisExtractModel>of()).stream()
                .filter(this::coversGypsumSubsurfaceLayer)
                .toList();
    }

    private <T> List<T> joinWithLegacy(List<T> extracts, T legacyExtract) {
        List<T> joined = new ArrayList<>(extracts != null ? extracts : List.of());
        joined.add(legacyExtract);
        return joined;
    }

    private Integer depthStart(PhysicalAnalysisExtractModel extract) {
        if (extract.getRangeExtract() != null) return extract.getRangeExtract().getProfundidade_inicial();
        if (extract.getLayerExtract() != null) return extract.getLayerExtract().getProfundidade_inicial();
        return Integer.MAX_VALUE;
    }

    private Integer depthEnd(PhysicalAnalysisExtractModel extract) {
        if (extract.getRangeExtract() != null) return extract.getRangeExtract().getProfundidade_final();
        if (extract.getLayerExtract() != null) return extract.getLayerExtract().getProfundidade_final();
        return Integer.MAX_VALUE;
    }

    private Integer depthStart(FertilityAnalysisExtractModel extract) {
        if (extract.getRangeExtract() != null) return extract.getRangeExtract().getProfundidade_inicial();
        if (extract.getLayerExtract() != null) return extract.getLayerExtract().getProfundidade_inicial();
        return Integer.MAX_VALUE;
    }

    private Integer depthEnd(FertilityAnalysisExtractModel extract) {
        if (extract.getRangeExtract() != null) return extract.getRangeExtract().getProfundidade_final();
        if (extract.getLayerExtract() != null) return extract.getLayerExtract().getProfundidade_final();
        return Integer.MAX_VALUE;
    }

    private Integer depthStart(SaturationExtractAnalysisExtractModel extract) {
        if (extract.getRangeExtract() != null) return extract.getRangeExtract().getProfundidade_inicial();
        if (extract.getLayerExtract() != null) return extract.getLayerExtract().getProfundidade_inicial();
        return Integer.MAX_VALUE;
    }

    private Integer depthEnd(SaturationExtractAnalysisExtractModel extract) {
        if (extract.getRangeExtract() != null) return extract.getRangeExtract().getProfundidade_final();
        if (extract.getLayerExtract() != null) return extract.getLayerExtract().getProfundidade_final();
        return Integer.MAX_VALUE;
    }

    private boolean coversZeroToTwenty(PhysicalAnalysisExtractModel extract) {
        return coversZeroToTwenty(depthStart(extract), depthEnd(extract));
    }

    private boolean coversZeroToTwenty(FertilityAnalysisExtractModel extract) {
        return coversZeroToTwenty(depthStart(extract), depthEnd(extract));
    }

    private boolean coversZeroToTwenty(SaturationExtractAnalysisExtractModel extract) {
        return coversZeroToTwenty(depthStart(extract), depthEnd(extract));
    }

    private boolean coversGypsumSubsurfaceLayer(PhysicalAnalysisExtractModel extract) {
        return coversGypsumSubsurfaceLayer(depthStart(extract), depthEnd(extract));
    }

    private boolean coversGypsumSubsurfaceLayer(FertilityAnalysisExtractModel extract) {
        return coversGypsumSubsurfaceLayer(depthStart(extract), depthEnd(extract));
    }

    private boolean coversZeroToTwenty(Integer initialDepth, Integer finalDepth) {
        return initialDepth != null && finalDepth != null
                && initialDepth <= 0
                && finalDepth >= 20;
    }

    private boolean coversGypsumSubsurfaceLayer(Integer initialDepth, Integer finalDepth) {
        return initialDepth != null && finalDepth != null
                && ((initialDepth <= 21 && finalDepth >= 40)
                || (initialDepth <= 41 && finalDepth >= 60));
    }

    private Optional<Double> extractPhValue(Optional<FertilityAnalysisExtractModel> fertilityExtract,
                                            Optional<SaturationExtractAnalysisExtractModel> saturation) {
        if (fertilityExtract.isPresent()) {
            FertilityAnalysisExtractModel fertility = fertilityExtract.get();
            if (fertility.getPhAgua() != null) return Optional.of(fertility.getPhAgua());
            if (fertility.getPhCacl2() != null) return Optional.of(fertility.getPhCacl2());
        }
        return saturation.map(SaturationExtractAnalysisExtractModel::getPh);
    }

    private Optional<Double> extractAluminumValue(Optional<FertilityAnalysisExtractModel> fertilityExtract) {
        return fertilityExtract.map(FertilityAnalysisExtractModel::getAluminio);
    }

    private List<String> buildCorrectionMessages(Optional<FertilityAnalysisExtractModel> fertilityExtract,
                                                 Optional<SaturationExtractAnalysisExtractModel> saturation,
                                                 List<String> warnings) {
        List<String> messages = new ArrayList<>();
        Optional<Double> ph = extractPhValue(fertilityExtract, saturation);
        Optional<Double> aluminum = extractAluminumValue(fertilityExtract);

        if (ph.isPresent()) {
            double value = ph.get();
            if (value < 5.5) {
                messages.add("pH abaixo de 5.5. Indica necessidade provável de correção de acidez, a confirmar com critério de calagem selecionado.");
            } else if (value <= 6.5) {
                messages.add("pH em faixa intermediária. Correção deve ser avaliada conforme cultura e saturação por bases.");
            } else {
                messages.add("pH elevado. Evitar recomendações automáticas de calagem sem validação técnica.");
            }
        }
        if (aluminum.isPresent() && aluminum.get() > 0) {
            messages.add("Presença de alumínio trocável detectada. Avaliar neutralização conforme critério selecionado.");
        }
        if (ph.isEmpty() && aluminum.isEmpty()) {
            warnings.add("Não foi possível calcular correção de acidez/salinidade por ausência de parâmetros suficientes.");
        }
        return messages;
    }

    private boolean isInterpretation(SoilChemicalDiagnosisItem item, String... expected) {
        if (item == null || item.getInterpretation() == null) return false;
        return Arrays.asList(expected).contains(item.getInterpretation());
    }

    private List<CorrectiveFertilizationRow> buildCorrectiveFertilizationRows(RecommendationCreateRequestDto dto,
                                                                              List<SoilChemicalDiagnosisItem> chemicalDiagnosis,
                                                                              Optional<FertilityAnalysisExtractModel> fertilityExtract,
                                                                              PhysicalAnalysisExtractModel physicalAnalysis,
                                                                              CropFertilizationTableModel cropFertilizationTable,
                                                                              SoilFertilityInterpretationCriteriaTableModel soilInterpretationTable,
                                                                              UserModel user,
                                                                              FertilizerSourceOption sourceOption,
                                                                              List<String> warnings) {
        List<CorrectiveFertilizationRow> rows = new ArrayList<>();
        if (!Boolean.TRUE.equals(dto.getSoilCorrectiveFertilization())) {
            rows.add(CorrectiveFertilizationRow.builder()
                    .correctedAttribute("Adubação Corretiva do Solo")
                    .need("Não habilitada pelo usuário")
                    .suggestedSource("Não sugerida")
                    .dose(null)
                    .doseUnit("kg/ha")
                    .calculationMemory("Campo adubacaoCorretivaSolo diferente de Sim; rotina corretiva de P, K e micronutrientes não executada.")
                    .technicalWarning("Adubação corretiva do solo não foi calculada porque a opção não foi habilitada na entrada da recomendação.")
                    .build());
            return rows;
        }

        Optional<String> justification = soilCorrectiveJustification(dto);
        if (justification.isEmpty()) {
            rows.add(CorrectiveFertilizationRow.builder()
                    .correctedAttribute("Adubação Corretiva do Solo")
                    .need("Não aplicável")
                    .suggestedSource("Não sugerida")
                    .dose(null)
                    .doseUnit("kg/ha")
                    .calculationMemory("Todas as quatro justificativas de adubação corretiva foram informadas como Não.")
                    .technicalWarning("A Recomendação de Adubação Corretiva não se aplica, pois trata-se de agricultura familiar de baixa/média tecnologia de cultivo.")
                    .build());
            return rows;
        }

        rows.add(CorrectiveFertilizationRow.builder()
                .correctedAttribute("Contexto da Adubação Corretiva do Solo")
                .need(justification.get())
                .suggestedSource("Critério de elegibilidade")
                .dose(null)
                .doseUnit("kg/ha")
                .calculationMemory("Rotina corretiva executada porque adubacaoCorretivaSolo = Sim e ao menos uma justificativa foi Sim.")
                .technicalWarning("Essa adubação corretiva tem efeito residual mínimo de 5 anos. Entretanto, fique atento para culturas que têm altas demandas em boro (Algodão, Amendoim, outras) e em zinco (Milho, Cana-de-açúcar, Algodão, etc). Complementar esses micronutrientes na adubação anual.")
                .build());

        Map<String, SoilChemicalDiagnosisItem> byAttribute = new LinkedHashMap<>();
        if (chemicalDiagnosis != null) {
            for (SoilChemicalDiagnosisItem item : chemicalDiagnosis) {
                if (item != null && item.getAttribute() != null) {
                    byAttribute.put(normalizeText(item.getAttribute()), item);
                }
            }
        }

        FertilityAnalysisExtractModel fertility = fertilityExtract.orElse(null);
        Double p2o5Dose = addCorrectivePhosphorusRows(rows, findFirstDiagnosis(byAttribute, "fosforo"), fertility,
                physicalAnalysis, soilInterpretationTable, user, sourceOption, warnings);
        Double k2oDose = addCorrectivePotassiumRows(rows, findFirstDiagnosis(byAttribute, "potassio"), fertility,
                soilInterpretationTable, user, sourceOption, warnings);
        addCorrectiveFormulatedRows(rows, p2o5Dose, k2oDose, user, sourceOption, warnings);
        addCorrectiveMicronutrientRows(rows, byAttribute, fertility, soilInterpretationTable, user, sourceOption, warnings);

        if (rows.size() == 1) {
            rows.add(CorrectiveFertilizationRow.builder()
                    .correctedAttribute("P/K/micronutrientes corretivos")
                    .need("Não avaliada")
                    .suggestedSource("Não sugerida")
                    .dose(null)
                    .doseUnit("kg/ha")
                    .calculationMemory("Não há dados analíticos e/ou tabelas auxiliares suficientes para calcular P2O5, K2O ou micronutrientes corretivos.")
                    .technicalWarning("Adubação corretiva habilitada, mas sem doses calculáveis com os dados cadastrados.")
                    .build());
        }
        return rows;
    }

    private Optional<String> soilCorrectiveJustification(RecommendationCreateRequestDto dto) {
        if (Boolean.TRUE.equals(dto.getRecentNativeOrPastureConversionArea())) {
            return Optional.of("Área de Incorporação / Conversão recente (1 a 2 anos) à agricultura de uso de mata nativa ou pastagem anterior.");
        }
        if (Boolean.TRUE.equals(dto.getDegradedAreaMoreThanFiveYearsWithoutFertilization())) {
            return Optional.of("Área degradada em recuperação para a agricultura moderna.");
        }
        if (Boolean.TRUE.equals(dto.getErosionRecoveryArea())) {
            return Optional.of("Área em recuperação após cuidados de Manejo de Conservação do Solo.");
        }
        if (Boolean.TRUE.equals(dto.getHighTechnologyHighProductivityArea())) {
            return Optional.of("Área para cultivos de alta tecnologia e altas produtividades.");
        }
        return Optional.empty();
    }

    private Double addCorrectivePhosphorusRows(List<CorrectiveFertilizationRow> rows,
                                               SoilChemicalDiagnosisItem diagnosis,
                                               FertilityAnalysisExtractModel fertility,
                                               PhysicalAnalysisExtractModel physicalAnalysis,
                                               SoilFertilityInterpretationCriteriaTableModel soilInterpretationTable,
                                               UserModel user,
                                               FertilizerSourceOption sourceOption,
                                               List<String> warnings) {
        Double p = fertility != null ? fertility.getFosforoMehlich1() : null;
        Double clay = physicalAnalysis != null ? physicalAnalysis.getTeorArgila() : null;
        if (p == null || clay == null) {
            addNotCalculatedCorrectiveRow(rows, "P2O5 corretivo", "P disponível por Mehlich-1 e teor de argila são obrigatórios para calcular P2O5 corretivo.");
            return null;
        }
        List<CorrectiveP2O5FertilizationModel> table = correctiveP2O5FertilizationRepository
                .findAllByTableOrderByClayContentMinimumAscAvailablePMehlich1MinimumAsc(soilInterpretationTable);
        if (table.isEmpty()) {
            addNotCalculatedCorrectiveRow(rows, "P2O5 corretivo", "Tabela auxiliar de adubação corretiva de P2O5 não encontrada para a tabela de interpretação selecionada.");
            return null;
        }
        CorrectiveP2O5FertilizationModel selectedLine = selectPhosphorusCorrectiveLine(table, clay, p).orElse(null);
        Double dose = selectedLine != null ? selectedLine.getRecommendedP2O5Dose() : null;
        if (dose == null || dose <= 0d) {
            String interpretation = diagnosis != null ? diagnosis.getInterpretation() : null;
            addNotCalculatedCorrectiveRow(rows, "P2O5 corretivo", "Dose de P2O5 não calculada para a faixa " + safeText(interpretation) + " e argila " + formatNumber(clay) + " g/kg.");
            return null;
        }

        addSimpleCorrectiveProductRow(rows, "P2O5 corretivo - Superfosfato Simples", "P2O5", dose,
                selectSimpleByNameOrNutrient(user, sourceOption, "superfosfato simples", "P2O5"), "Superfosfato Simples",
                "100 * dose recomendada P2O5 / teor % P2O5 do adubo. P Mehlich-1 = " + formatNumber(p)
                        + " mg/dm³; argila = " + formatNumber(clay) + " g/kg; linha de adubação corretiva P2O5 ID " + selectedLine.getId() + ".", warnings);
        addSimpleCorrectiveProductRow(rows, "P2O5 corretivo - Superfosfato Triplo", "P2O5", dose,
                selectSimpleByNameOrNutrient(user, sourceOption, "superfosfato triplo", "P2O5"), "Superfosfato Triplo",
                "100 * dose recomendada P2O5 / teor % P2O5 do adubo.", warnings);
        addSimpleCorrectiveProductRow(rows, "P2O5 corretivo - Termofosfato Magnesiano", "P2O5", dose,
                selectSimpleByNameOrNutrient(user, sourceOption, "termofosfato", "P2O5"), "Termofosfato Magnesiano",
                "100 * dose recomendada P2O5 / teor % P2O5 do adubo.", warnings);
        return round2(dose);
    }

    private Double addCorrectivePotassiumRows(List<CorrectiveFertilizationRow> rows,
                                              SoilChemicalDiagnosisItem diagnosis,
                                              FertilityAnalysisExtractModel fertility,
                                              SoilFertilityInterpretationCriteriaTableModel soilInterpretationTable,
                                              UserModel user,
                                              FertilizerSourceOption sourceOption,
                                              List<String> warnings) {
        Double k = fertility != null ? fertility.getPotassio() : null;
        Double ctc = fertility != null ? fertility.getCtcPh7() : null;
        if (k == null || ctc == null) {
            addNotCalculatedCorrectiveRow(rows, "K2O corretivo", "K+ e CTC pH 7,0 são obrigatórios para calcular K2O corretivo.");
            return null;
        }
        List<CorrectiveK2OFertilizationModel> table = correctiveK2OFertilizationRepository
                .findAllByTableOrderByCtcMinimumAscExchangeableKMinimumAsc(soilInterpretationTable);
        if (table.isEmpty()) {
            addNotCalculatedCorrectiveRow(rows, "K2O corretivo", "Tabela auxiliar de adubação corretiva de K2O não encontrada para a tabela de interpretação selecionada.");
            return null;
        }
        CorrectiveK2OFertilizationModel selectedLine = selectPotassiumCorrectiveLine(table, ctc, k).orElse(null);
        Double dose = selectedLine != null ? selectedLine.getRecommendedK2ODose() : null;
        if (dose == null || dose <= 0d) {
            String diagnosisRange = diagnosis != null ? diagnosis.getInterpretation() : null;
            addNotCalculatedCorrectiveRow(rows, "K2O corretivo", "Dose de K2O não calculada para K+ " + formatNumber(k)
                    + " mmolc/dm³, CTC pH 7,0 " + formatNumber(ctc) + " e faixa " + safeText(diagnosisRange) + ".");
            return null;
        }
        addSimpleCorrectiveProductRow(rows, "K2O corretivo - Cloreto de Potássio", "K2O", dose,
                selectSimpleByNameOrNutrient(user, sourceOption, "cloreto de potassio", "K2O"), "Cloreto de Potássio",
                "100 * dose recomendada K2O / teor % K2O do KCl. K+ = " + formatNumber(k)
                        + " mmolc/dm³; CTC pH 7,0 = " + formatNumber(ctc) + "; linha de adubação corretiva K2O ID " + selectedLine.getId() + ".",
                warnings);
        return round2(dose);
    }

    private void addCorrectiveFormulatedRows(List<CorrectiveFertilizationRow> rows,
                                             Double recommendedP2O5,
                                             Double recommendedK2O,
                                             UserModel user,
                                             FertilizerSourceOption sourceOption,
                                             List<String> warnings) {
        if (!positive(recommendedP2O5) || !positive(recommendedK2O)) {
            addNotCalculatedCorrectiveRow(rows, "Formulado 00-P2O5-K2O corretivo", "Formulado não calculado porque P2O5 e K2O corretivos precisam estar ambos calculados.");
            return;
        }
        FormulatedMineralFertilizerModel formulated = selectClosestZeroNFormulated(user, sourceOption, recommendedP2O5, recommendedK2O).orElse(null);
        if (formulated == null) {
            addNotCalculatedCorrectiveRow(rows, "Formulado 00-P2O5-K2O corretivo", "Não há formulado mineral cadastrado sem N e com P2O5/K2O positivos.");
            return;
        }
        double dose = round2(Math.min(100d * recommendedP2O5 / formulated.getP2O5(), 100d * recommendedK2O / formulated.getK2O()));
        double providedP = dose * formulated.getP2O5() / 100d;
        double providedK = dose * formulated.getK2O() / 100d;
        double pBalance = round2(providedP - recommendedP2O5);
        double kBalance = round2(providedK - recommendedK2O);
        rows.add(CorrectiveFertilizationRow.builder()
                .correctedAttribute("Formulado 00-P2O5-K2O corretivo")
                .need("P2O5 " + formatNumber(recommendedP2O5) + " kg/ha; K2O " + formatNumber(recommendedK2O) + " kg/ha")
                .suggestedSource(formatFormulatedName(formulated))
                .dose(dose)
                .doseUnit("kg/ha de produto")
                .calculationMemory("Formulado sem N com relação P2O5:K2O mais próxima da recomendação. Fornece P2O5 "
                        + formatNumber(providedP) + " kg/ha e K2O " + formatNumber(providedK) + " kg/ha.")
                .technicalWarning(balanceWarning(recommendedP2O5, recommendedK2O, pBalance, kBalance))
                .build());
        addComplementRow(rows, "Complemento P2O5 após formulado", "P2O5", recommendedP2O5 - providedP,
                selectSimpleByNameOrNutrient(user, sourceOption, "superfosfato simples", "P2O5"),
                selectSimpleByNameOrNutrient(user, sourceOption, "superfosfato triplo", "P2O5"), warnings);
        addComplementRow(rows, "Complemento K2O após formulado", "K2O", recommendedK2O - providedK,
                selectSimpleByNameOrNutrient(user, sourceOption, "cloreto de potassio", "K2O"),
                null, warnings);
    }

    private void addCorrectiveMicronutrientRows(List<CorrectiveFertilizationRow> rows,
                                                Map<String, SoilChemicalDiagnosisItem> byAttribute,
                                                FertilityAnalysisExtractModel fertility,
                                                SoilFertilityInterpretationCriteriaTableModel soilInterpretationTable,
                                                UserModel user,
                                                FertilizerSourceOption sourceOption,
                                                List<String> warnings) {
        Double ph = fertility != null ? fertility.getPhAgua() : null;
        if (ph != null && ph > 7.0d) {
            rows.add(CorrectiveFertilizationRow.builder()
                    .correctedAttribute("Micronutrientes corretivos")
                    .need("Bloqueado por pH em água > 7,0")
                    .suggestedSource("Não recomendada no solo")
                    .dose(null)
                    .doseUnit("kg/ha")
                    .calculationMemory("pH em água = " + formatNumber(ph) + ".")
                    .technicalWarning("Solos com reação alcalina. Adubação corretiva com micronutrientes não recomendada. Faça adubação foliar com os micronutrientes, de acordo com a necessidade da cultura.")
                    .build());
            return;
        }
        Optional<MicronutrientDoseModel> doses = micronutrientDoseRepository.findByTable(soilInterpretationTable);
        if (doses.isEmpty()) {
            addNotCalculatedCorrectiveRow(rows, "Micronutrientes corretivos", "Tabela de dosagens de micronutrientes não encontrada para a tabela selecionada.");
            return;
        }
        Optional<DiverseContentRangeModel> diverseRange = findDiverseContentRangeByTable(soilInterpretationTable);
        Map<AppliedMicronutrient, Double> recommended = recommendedMicronutrientDoses(doses.get(), byAttribute, diverseRange, warnings);
        Double znDose = recommended.get(AppliedMicronutrient.Zn);
        if (!positive(znDose)) {
            addNotCalculatedCorrectiveRow(rows, "FTE corretivo", "Dose recomendada de Zn ausente ou igual a zero; FTE por nutriente-base Zn não calculado.");
            addMicronutrientSimpleComplements(rows, "Complemento corretivo", recommended, user, sourceOption);
        } else {
            addFteRows(rows, recommended, znDose, user, sourceOption);
        }
    }

    private Optional<CorrectiveP2O5FertilizationModel> selectPhosphorusCorrectiveLine(List<CorrectiveP2O5FertilizationModel> table, Double clay, Double p) {
        if (table == null || clay == null || p == null) return Optional.empty();
        return table.stream()
                .filter(line -> containsValue(line.getClayContentMinimum(), line.getClayContentMaximum(), clay))
                .filter(line -> containsValue(line.getAvailablePMehlich1Minimum(), line.getAvailablePMehlich1Maximum(), p))
                .findFirst();
    }

    private Optional<CorrectiveK2OFertilizationModel> selectPotassiumCorrectiveLine(List<CorrectiveK2OFertilizationModel> table, Double ctc, Double k) {
        if (table == null || ctc == null || k == null) return Optional.empty();
        return table.stream()
                .filter(line -> containsValue(line.getCtcMinimum(), line.getCtcMaximum(), ctc))
                .filter(line -> containsValue(line.getExchangeableKMinimum(), line.getExchangeableKMaximum(), k))
                .findFirst();
    }

    private boolean containsValue(Double minimum, Double maximum, Double value) {
        if (value == null) return false;
        return (minimum == null || value >= minimum) && (maximum == null || value <= maximum);
    }

    private void addSimpleCorrectiveProductRow(List<CorrectiveFertilizationRow> rows,
                                               String correctedAttribute,
                                               String nutrient,
                                               Double recommendedDose,
                                               SimpleMineralFertilizerModel source,
                                               String fallbackSourceName,
                                               String memory,
                                               List<String> warnings) {
        Double concentration = source != null ? concentration(source, nutrient) : null;
        if (!positive(concentration)) {
            String warning = fallbackSourceName + " não encontrado com teor positivo de " + nutrient + " na origem de adubos selecionada.";
            warnings.add(warning);
            rows.add(CorrectiveFertilizationRow.builder()
                    .correctedAttribute(correctedAttribute)
                    .need(formatNumber(recommendedDose) + " kg/ha de " + nutrient)
                    .suggestedSource(fallbackSourceName)
                    .dose(null)
                    .doseUnit("kg/ha de produto")
                    .calculationMemory(memory)
                    .technicalWarning(warning)
                    .build());
            return;
        }
        rows.add(CorrectiveFertilizationRow.builder()
                .correctedAttribute(correctedAttribute)
                .need(formatNumber(recommendedDose) + " kg/ha de " + nutrient)
                .suggestedSource(source.getName())
                .dose(round2(100d * recommendedDose / concentration))
                .doseUnit("kg/ha de produto")
                .calculationMemory(memory + " Teor usado: " + formatNumber(concentration) + "%.")
                .technicalWarning(null)
                .build());
    }

    private void addComplementRow(List<CorrectiveFertilizationRow> rows,
                                  String correctedAttribute,
                                  String nutrient,
                                  Double deficit,
                                  SimpleMineralFertilizerModel preferred,
                                  SimpleMineralFertilizerModel fallback,
                                  List<String> warnings) {
        if (deficit == null || deficit <= 0d) return;
        SimpleMineralFertilizerModel source = positive(concentration(preferred, nutrient)) ? preferred : fallback;
        addSimpleCorrectiveProductRow(rows, correctedAttribute, nutrient, deficit, source,
                nutrient.equals("K2O") ? "Cloreto de Potássio" : "Superfosfato Simples ou Triplo",
                "Complemento calculado para zerar saldo negativo após uso do formulado 00-P2O5-K2O.", warnings);
    }

    private void addFteRows(List<CorrectiveFertilizationRow> rows,
                            Map<AppliedMicronutrient, Double> recommended,
                            Double znDose,
                            UserModel user,
                            FertilizerSourceOption sourceOption) {
        List<SimpleMineralFertilizerModel> simple = selectSimpleFertilizers(user, sourceOption);
        List<SimpleMineralFertilizerModel> ftes = simple.stream()
                .filter(f -> positive(f.getZn()) && normalizeText(f.getName()).contains("fte"))
                .toList();
        selectFteBr12(ftes).ifPresent(fte -> addFteRow(rows, "FTE BR 12 corretivo", fte, recommended, znDose, user, sourceOption));
        ftes.stream()
                .max(Comparator.comparing(SimpleMineralFertilizerModel::getZn))
                .ifPresent(fte -> addFteRow(rows, "FTE mais concentrado em Zn corretivo", fte, recommended, znDose, user, sourceOption));
        if (ftes.isEmpty()) {
            addNotCalculatedCorrectiveRow(rows, "FTE corretivo", "Nenhum FTE com teor positivo de Zn foi encontrado em adubos minerais simples.");
            addMicronutrientSimpleComplements(rows, "Complemento corretivo", recommended, user, sourceOption);
        }
    }

    private Optional<SimpleMineralFertilizerModel> selectFteBr12(List<SimpleMineralFertilizerModel> ftes) {
        if (ftes == null) return Optional.empty();
        return ftes.stream()
                .filter(f -> normalizeText(f.getName()).contains("br 12") || normalizeText(f.getName()).contains("br-12"))
                .findFirst();
    }

    private void addFteRow(List<CorrectiveFertilizationRow> rows,
                           String correctedAttribute,
                           SimpleMineralFertilizerModel fte,
                           Map<AppliedMicronutrient, Double> recommended,
                           Double znDose,
                           UserModel user,
                           FertilizerSourceOption sourceOption) {
        double dose = round2(100d * znDose / fte.getZn());
        rows.add(CorrectiveFertilizationRow.builder()
                .correctedAttribute(correctedAttribute)
                .need("Zn como nutriente-base: " + formatNumber(znDose) + " kg/ha")
                .suggestedSource(fte.getName())
                .dose(dose)
                .doseUnit("kg/ha de produto")
                .calculationMemory("Dose FTE = 100 * dose recomendada Zn / teor % Zn do FTE. Balanço B/Cu/Fe/Mn/Zn: "
                        + micronutrientBalanceSummary(recommended, fte, dose) + ".")
                .technicalWarning(null)
                .build());
        addMicronutrientSimpleComplements(rows, "Complemento após " + fte.getName(),
                micronutrientDeficitsAfterFte(recommended, fte, dose), user, sourceOption);
    }

    private void addMicronutrientSimpleComplements(List<CorrectiveFertilizationRow> rows,
                                                   String prefix,
                                                   Map<AppliedMicronutrient, Double> recommended,
                                                   UserModel user,
                                                   FertilizerSourceOption sourceOption) {
        Map<AppliedMicronutrient, Double> complementDoses = new LinkedHashMap<>();
        addPositiveDose(complementDoses, AppliedMicronutrient.B, recommended.get(AppliedMicronutrient.B));
        addPositiveDose(complementDoses, AppliedMicronutrient.Cu, recommended.get(AppliedMicronutrient.Cu));
        addPositiveDose(complementDoses, AppliedMicronutrient.Fe, recommended.get(AppliedMicronutrient.Fe));
        addPositiveDose(complementDoses, AppliedMicronutrient.Mn, recommended.get(AppliedMicronutrient.Mn));
        List<MicronutrientFertilizerSelectionService.MicronutrientFertilizerSelectionResult> selections =
                micronutrientFertilizerSelectionService.select(user, sourceOption, complementDoses);
        for (MicronutrientFertilizerSelectionService.MicronutrientFertilizerSelectionResult selection : selections) {
            rows.add(CorrectiveFertilizationRow.builder()
                    .correctedAttribute(prefix + " de " + selection.micronutrient().name())
                    .need(formatNumber(selection.micronutrientDoseKgHa()) + " kg/ha de " + selection.micronutrient().name())
                    .suggestedSource(selection.selectedFertilizer() != null ? selection.selectedFertilizer().getName() : "Não selecionada")
                    .dose(selection.fertilizerDoseKgHa())
                    .doseUnit("kg/ha de produto")
                    .calculationMemory("Fonte simples escolhida pelo maior teor cadastrado do micronutriente para zerar saldo negativo.")
                    .technicalWarning(selection.technicalMessage())
                    .build());
        }
    }

    private Map<AppliedMicronutrient, Double> micronutrientDeficitsAfterFte(Map<AppliedMicronutrient, Double> recommended,
                                                                            SimpleMineralFertilizerModel fte,
                                                                            Double fteDoseKgHa) {
        Map<AppliedMicronutrient, Double> deficits = new LinkedHashMap<>();
        for (AppliedMicronutrient micronutrient : List.of(AppliedMicronutrient.B, AppliedMicronutrient.Cu, AppliedMicronutrient.Fe, AppliedMicronutrient.Mn)) {
            double required = nvl(recommended.get(micronutrient));
            double applied = fteDoseKgHa * micronutrientConcentration(fte, micronutrient) / 100d;
            double deficit = round2(required - applied);
            if (deficit > 0d) {
                deficits.put(micronutrient, deficit);
            }
        }
        return deficits;
    }

    private Map<AppliedMicronutrient, Double> recommendedMicronutrientDoses(MicronutrientDoseModel doses,
                                                                            Map<String, SoilChemicalDiagnosisItem> byAttribute,
                                                                            Optional<DiverseContentRangeModel> diverseRange,
                                                                            List<String> warnings) {
        Map<AppliedMicronutrient, Double> result = new LinkedHashMap<>();
        addMicronutrientDose(result, AppliedMicronutrient.B, doses.getBoronLowDose(), doses.getBoronMediumDose(), doses.getBoronHighDose(), byAttribute, "boro", diverseRange, warnings);
        addMicronutrientDose(result, AppliedMicronutrient.Cu, doses.getCopperLowDose(), doses.getCopperMediumDose(), doses.getCopperHighDose(), byAttribute, "cobre", diverseRange, warnings);
        addMicronutrientDose(result, AppliedMicronutrient.Fe, doses.getIronLowDose(), doses.getIronMediumDose(), doses.getIronHighDose(), byAttribute, "ferro", diverseRange, warnings);
        addMicronutrientDose(result, AppliedMicronutrient.Mn, doses.getManganeseLowDose(), doses.getManganeseMediumDose(), doses.getManganeseHighDose(), byAttribute, "manganes", diverseRange, warnings);
        addMicronutrientDose(result, AppliedMicronutrient.Zn, doses.getZincLowDose(), doses.getZincMediumDose(), doses.getZincHighDose(), byAttribute, "zinco", diverseRange, warnings);
        return result;
    }

    private void addMicronutrientDose(Map<AppliedMicronutrient, Double> result,
                                      AppliedMicronutrient micronutrient,
                                      Double low,
                                      Double medium,
                                      Double high,
                                      Map<String, SoilChemicalDiagnosisItem> byAttribute,
                                      String token,
                                      Optional<DiverseContentRangeModel> diverseRange,
                                      List<String> warnings) {
        SoilChemicalDiagnosisItem item = findFirstDiagnosis(byAttribute, token);
        if (item == null || item.getAnalyzedValue() == null) return;
        String range = classifyCorrectiveMicronutrientRange(micronutrient, item.getAnalyzedValue(), diverseRange).orElse(item.getInterpretation());
        if (range == null) {
            warnings.add("Dose corretiva de " + micronutrient.name() + " não calculada por ausência de faixa interpretada.");
            return;
        }
        Double dose = switch (range) {
            case "Baixo", "Muito baixo" -> low;
            case "Médio" -> medium;
            case "Alto", "Muito alto" -> high;
            default -> null;
        };
        if (dose == null) {
            warnings.add("Dose corretiva de " + micronutrient.name() + " não calculada para faixa " + safeText(range) + ".");
            return;
        }
        result.put(micronutrient, round2(dose));
    }

    private Optional<String> classifyCorrectiveMicronutrientRange(AppliedMicronutrient micronutrient,
                                                                  Double value,
                                                                  Optional<DiverseContentRangeModel> diverseRange) {
        if (micronutrient == null || value == null || diverseRange.isEmpty()) return Optional.empty();
        DiverseContentRangeModel r = diverseRange.get();
        ThreeLevelCriterion criterion = switch (micronutrient) {
            case B -> new ThreeLevelCriterion(r.getBoron_low_f(), r.getBoron_medium_i(), r.getBoron_medium_f(), r.getBoron_hight_i());
            case Cu -> new ThreeLevelCriterion(r.getCopper_low_f(), r.getCopper_medium_i(), r.getCopper_medium_f(), r.getCopper_hight_i());
            case Fe -> new ThreeLevelCriterion(r.getIron_low_f(), r.getIron_medium_i(), r.getIron_medium_f(), r.getIron_hight_i());
            case Mn -> new ThreeLevelCriterion(r.getManganese_low_f(), r.getManganese_medium_i(), r.getManganese_medium_f(), r.getManganese_hight_i());
            case Zn -> new ThreeLevelCriterion(r.getZinc_low_f(), r.getZinc_medium_i(), r.getZinc_medium_f(), r.getZinc_hight_i());
            default -> null;
        };
        return Optional.ofNullable(classifyThreeLevelRangeName(value, criterion));
    }

    private void addNotCalculatedCorrectiveRow(List<CorrectiveFertilizationRow> rows, String attribute, String warning) {
        rows.add(CorrectiveFertilizationRow.builder()
                .correctedAttribute(attribute)
                .need("Não calculada")
                .suggestedSource("Não sugerida")
                .dose(null)
                .doseUnit("kg/ha")
                .calculationMemory("Dados insuficientes ou critério não encontrado para cálculo corretivo.")
                .technicalWarning(warning)
                .build());
    }

    private Optional<FormulatedMineralFertilizerModel> selectClosestZeroNFormulated(UserModel user,
                                                                                    FertilizerSourceOption sourceOption,
                                                                                    Double recommendedP2O5,
                                                                                    Double recommendedK2O) {
        double targetRatio = recommendedP2O5 / recommendedK2O;
        return selectFormulatedFertilizers(user, sourceOption).stream()
                .filter(f -> f != null && !positive(f.getN()) && positive(f.getP2O5()) && positive(f.getK2O()))
                .min(Comparator.comparing(f -> Math.abs((f.getP2O5() / f.getK2O()) - targetRatio)));
    }

    private SimpleMineralFertilizerModel selectSimpleByNameOrNutrient(UserModel user,
                                                                      FertilizerSourceOption sourceOption,
                                                                      String nameToken,
                                                                      String nutrient) {
        List<SimpleMineralFertilizerModel> fertilizers = selectSimpleFertilizers(user, sourceOption);
        String normalizedToken = normalizeText(nameToken);
        return fertilizers.stream()
                .filter(f -> normalizeText(f.getName()).contains(normalizedToken) && positive(concentration(f, nutrient)))
                .findFirst()
                .orElseGet(() -> fertilizers.stream()
                        .filter(f -> positive(concentration(f, nutrient)))
                        .max(Comparator.comparing(f -> concentration(f, nutrient)))
                        .orElse(null));
    }

    private List<SimpleMineralFertilizerModel> selectSimpleFertilizers(UserModel user, FertilizerSourceOption sourceOption) {
        FertilizerSourceOption resolvedOption = sourceOption != null ? sourceOption : FertilizerSourceOption.BOTH;
        return switch (resolvedOption) {
            case PRIVATE -> simpleMineralFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user);
            case PUBLIC -> simpleMineralFertilizerRepository.findAllByPublicoTrueAndUser_CargoNotOrderByNameAsc(Cargo.USUARIO_SUPREMO);
            case DEFAULT -> simpleMineralFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO);
            case BOTH, ALL -> dedup(
                    dedup(simpleMineralFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user),
                            simpleMineralFertilizerRepository.findAllByPublicoTrueAndUser_CargoNotOrderByNameAsc(Cargo.USUARIO_SUPREMO),
                            SimpleMineralFertilizerModel::getId),
                    simpleMineralFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO),
                    SimpleMineralFertilizerModel::getId);
        };
    }

    private List<FormulatedMineralFertilizerModel> selectFormulatedFertilizers(UserModel user, FertilizerSourceOption sourceOption) {
        FertilizerSourceOption resolvedOption = sourceOption != null ? sourceOption : FertilizerSourceOption.BOTH;
        return switch (resolvedOption) {
            case PRIVATE -> formulatedMineralFertilizerRepository.findAllByUserAndPublicoFalseOrderByIdAsc(user);
            case PUBLIC -> formulatedMineralFertilizerRepository.findAllByPublicoTrueAndUser_CargoNotOrderByIdAsc(Cargo.USUARIO_SUPREMO);
            case DEFAULT -> formulatedMineralFertilizerRepository.findAllByUser_CargoOrderByIdAsc(Cargo.USUARIO_SUPREMO);
            case BOTH, ALL -> dedup(
                    dedup(formulatedMineralFertilizerRepository.findAllByUserAndPublicoFalseOrderByIdAsc(user),
                            formulatedMineralFertilizerRepository.findAllByPublicoTrueAndUser_CargoNotOrderByIdAsc(Cargo.USUARIO_SUPREMO),
                            FormulatedMineralFertilizerModel::getId),
                    formulatedMineralFertilizerRepository.findAllByUser_CargoOrderByIdAsc(Cargo.USUARIO_SUPREMO),
                    FormulatedMineralFertilizerModel::getId);
        };
    }

    private Double concentration(SimpleMineralFertilizerModel source, String nutrient) {
        if (source == null || nutrient == null) return null;
        return switch (nutrient) {
            case "P2O5" -> source.getP2O5();
            case "K2O" -> source.getK2O();
            case "B" -> source.getB();
            case "Cu" -> source.getCu();
            case "Fe" -> source.getFe();
            case "Mn" -> source.getMn();
            case "Zn" -> source.getZn();
            default -> null;
        };
    }

    private Double micronutrientConcentration(SimpleMineralFertilizerModel source, AppliedMicronutrient micronutrient) {
        if (source == null || micronutrient == null) return 0d;
        return switch (micronutrient) {
            case B -> source.getB();
            case Cu -> source.getCu();
            case Fe -> source.getFe();
            case Mn -> source.getMn();
            case Zn -> source.getZn();
            default -> 0d;
        };
    }

    private String micronutrientBalanceSummary(Map<AppliedMicronutrient, Double> recommended,
                                               SimpleMineralFertilizerModel fte,
                                               Double fteDoseKgHa) {
        List<String> parts = new ArrayList<>();
        for (AppliedMicronutrient micronutrient : List.of(AppliedMicronutrient.B, AppliedMicronutrient.Cu, AppliedMicronutrient.Fe, AppliedMicronutrient.Mn, AppliedMicronutrient.Zn)) {
            double required = nvl(recommended.get(micronutrient));
            double applied = fteDoseKgHa * micronutrientConcentration(fte, micronutrient) / 100d;
            parts.add(micronutrient.name() + " recomendado " + formatNumber(required) + ", aplicado " + formatNumber(applied)
                    + ", saldo " + formatNumber(applied - required) + " kg/ha");
        }
        return String.join("; ", parts);
    }

    private String balanceWarning(Double recommendedP, Double recommendedK, Double balanceP, Double balanceK) {
        boolean pOutside = Math.abs(balanceP) > Math.abs(recommendedP) * 0.10d;
        boolean kOutside = Math.abs(balanceK) > Math.abs(recommendedK) * 0.10d;
        if (pOutside || kOutside) {
            return "Saldo final do formulado ultrapassa +/-10%: P2O5 " + formatNumber(balanceP)
                    + " kg/ha; K2O " + formatNumber(balanceK) + " kg/ha. Complementos foram calculados para saldos negativos; excedentes exigem validação técnica.";
        }
        return "Saldo final do formulado dentro de +/-10%: P2O5 " + formatNumber(balanceP)
                + " kg/ha; K2O " + formatNumber(balanceK) + " kg/ha.";
    }

    private String formatFormulatedName(FormulatedMineralFertilizerModel fertilizer) {
        if (fertilizer == null) return "Formulado 00-P2O5-K2O";
        return String.format(Locale.US, "NPK %.2f-%.2f-%.2f", fertilizer.getN(), fertilizer.getP2O5(), fertilizer.getK2O());
    }

    private void addPositiveDose(Map<AppliedMicronutrient, Double> doses, AppliedMicronutrient micronutrient, Double dose) {
        if (positive(dose)) {
            doses.put(micronutrient, dose);
        }
    }

    private boolean betweenInclusive(Double value, Double lower, Double upper) {
        return value != null && lower != null && upper != null && value >= lower && value <= upper;
    }

    private boolean positive(Double value) {
        return value != null && Double.isFinite(value) && value > 0d;
    }

    private String safeText(String value) {
        return value == null || value.isBlank() ? "não informada" : value;
    }

    private double nvl(Double value) {
        return value == null ? 0d : value;
    }

    private <T> List<T> dedup(List<T> first, List<T> second, Function<T, Long> id) {
        Map<Long, T> byId = new LinkedHashMap<>();
        List<T> withoutId = new ArrayList<>();
        addDedupItems(byId, withoutId, first, id);
        addDedupItems(byId, withoutId, second, id);
        withoutId.addAll(byId.values());
        return withoutId;
    }

    private <T> void addDedupItems(Map<Long, T> byId, List<T> withoutId, List<T> items, Function<T, Long> id) {
        if (items == null) return;
        for (T item : items) {
            Long itemId = id.apply(item);
            if (itemId == null) {
                withoutId.add(item);
            } else {
                byId.putIfAbsent(itemId, item);
            }
        }
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
                                            PhysicalAnalysisExtractModel physicalAnalysis,
                                            CropFertilizationTableModel cropFertilizationTable,
                                            SoilFertilityInterpretationCriteriaTableModel soilInterpretationTable,
                                            UserModel user,
                                            FertilizerSourceOption sourceOption,
                                            List<String> warnings) {
        if (diagnosis == null || diagnosis.getInterpretation() == null) return;

        if ("S".equals(nutrientTarget)) {
            rows.add(buildSulfurCorrectiveRow(correctedAttribute, diagnosis, physicalAnalysis,
                    cropFertilizationTable, soilInterpretationTable, user, sourceOption, warnings));
            return;
        }

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

    private CorrectiveFertilizationRow buildSulfurCorrectiveRow(String correctedAttribute,
                                                                SoilChemicalDiagnosisItem diagnosis,
                                                                PhysicalAnalysisExtractModel physicalAnalysis,
                                                                CropFertilizationTableModel cropFertilizationTable,
                                                                SoilFertilityInterpretationCriteriaTableModel soilInterpretationTable,
                                                                UserModel user,
                                                                FertilizerSourceOption sourceOption,
                                                                List<String> warnings) {
        Double clay = physicalAnalysis != null ? physicalAnalysis.getTeorArgila() : null;
        if (clay == null) {
            String warning = "Dose de S não calculada por ausência de teor de argila na análise física.";
            warnings.add(warning);
            return sulfurNotCalculatedRow(correctedAttribute, diagnosis, warning, cropFertilizationTable, soilInterpretationTable);
        }

        Optional<SulfurDoseModel> criterion = sulfurDoseRepository.findByTable(soilInterpretationTable);
        if (criterion.isEmpty()) {
            String warning = "Dose de S não calculada porque não há tabela auxiliar Doses de S vinculada à tabela de interpretação selecionada.";
            warnings.add(warning);
            return sulfurNotCalculatedRow(correctedAttribute, diagnosis, warning, cropFertilizationTable, soilInterpretationTable);
        }

        SulfurDoseModel doses = criterion.get();
        boolean less400 = clay < 400d;
        Double dose = selectSulfurDose(doses, less400, diagnosis.getInterpretation());
        if (dose == null) {
            String warning = "Dose de S não calculada porque a classe " + diagnosis.getInterpretation()
                    + " não possui dose preenchida na tabela auxiliar Doses de S.";
            warnings.add(warning);
            return sulfurNotCalculatedRow(correctedAttribute, diagnosis, warning, cropFertilizationTable, soilInterpretationTable);
        }

        SimpleMineralFertilizerModel source = selectCorrectiveSource(user, sourceOption, "S");
        String sourceName = source != null ? source.getName() : "Não sugerida automaticamente";
        String sourceDetail = source != null
                ? "Fonte mineral simples compatível encontrada (" + source.getName() + "), mas a dose registrada é de S em kg/ha e não foi convertida para dose comercial do produto."
                : "Fonte comercial não selecionada porque não há adubo mineral simples com teor de S acessível pela origem selecionada.";
        if (source == null) {
            warnings.add(sourceDetail);
        }

        return CorrectiveFertilizationRow.builder()
                .correctedAttribute(correctedAttribute)
                .need("Dose definida pela classe de S disponível: " + diagnosis.getInterpretation())
                .suggestedSource(sourceName)
                .dose(round2(dose))
                .doseUnit("kg/ha de S")
                .calculationMemory("Diagnóstico: " + diagnosis.getAttribute()
                        + " = " + formatNumber(diagnosis.getAnalyzedValue()) + " " + (diagnosis.getUnit() != null ? diagnosis.getUnit() : "")
                        + "; interpretação: " + diagnosis.getInterpretation()
                        + "; argila = " + formatNumber(clay) + " " + physicalUnit(physicalAnalysis.getUnidadeTeorArgila())
                        + "; seção usada: " + (less400 ? "Argila < 400 g/kg" : "Argila > 400 g/kg")
                        + "; Doses de S ID " + doses.getId()
                        + "; tabela de interpretação ID " + (soilInterpretationTable != null ? soilInterpretationTable.getId() : null) + ".")
                .technicalWarning(sourceDetail)
                .build();
    }

    private CorrectiveFertilizationRow sulfurNotCalculatedRow(String correctedAttribute,
                                                              SoilChemicalDiagnosisItem diagnosis,
                                                              String warning,
                                                              CropFertilizationTableModel cropFertilizationTable,
                                                              SoilFertilityInterpretationCriteriaTableModel soilInterpretationTable) {
        return CorrectiveFertilizationRow.builder()
                .correctedAttribute(correctedAttribute)
                .need("Avaliação dependente de Doses de S: " + diagnosis.getInterpretation())
                .suggestedSource("Não sugerida automaticamente")
                .dose(null)
                .doseUnit("kg/ha de S")
                .calculationMemory("Diagnóstico: " + diagnosis.getAttribute()
                        + " = " + formatNumber(diagnosis.getAnalyzedValue()) + " " + (diagnosis.getUnit() != null ? diagnosis.getUnit() : "")
                        + "; interpretação: " + diagnosis.getInterpretation()
                        + ". Tabela de adubação ID " + (cropFertilizationTable != null ? cropFertilizationTable.getId() : null)
                        + " e tabela de interpretação ID " + (soilInterpretationTable != null ? soilInterpretationTable.getId() : null) + ".")
                .technicalWarning(warning)
                .build();
    }

    private Double selectSulfurDose(SulfurDoseModel doses, boolean less400, String interpretation) {
        if (doses == null || interpretation == null) return null;
        return switch (interpretation) {
            case "Muito baixo" -> less400 ? doses.getLess400VeryLowDose() : doses.getGreater400VeryLowDose();
            case "Baixo" -> less400 ? doses.getLess400LowDose() : doses.getGreater400LowDose();
            case "Médio" -> less400 ? doses.getLess400MediumDose() : doses.getGreater400MediumDose();
            case "Alto" -> less400 ? doses.getLess400HighDose() : doses.getGreater400HighDose();
            case "Muito alto" -> less400 ? doses.getLess400VeryHighDose() : doses.getGreater400VeryHighDose();
            default -> null;
        };
    }

    private SimpleMineralFertilizerModel selectCorrectiveSource(UserModel user, FertilizerSourceOption sourceOption, String nutrientTarget) {
        return nutrientFertilizationCalculationService.selectCorrectiveSource(user, sourceOption, nutrientTarget);
    }

    private PhysicalDiagnosis buildSoilPhysicalDiagnosis(PhysicalAnalysisExtractModel physicalAnalysis,
                                                         TexturalClassification texturalClassification,
                                                         List<String> warnings) {
        List<SoilPhysicalDiagnosisItem> diagnosis = new ArrayList<>();
        if (physicalAnalysis == null) {
            String message = "Análise física não disponível para diagnóstico físico do solo.";
            warnings.add(message);
            return new PhysicalDiagnosis(message, diagnosis);
        }

        SoilTextureClassificationService.SoilTextureClassificationResult textureClassification =
                soilTextureClassificationService.classify(texturalClassification, physicalAnalysis);
        appendTextureClassification(diagnosis, textureClassification, warnings);

        addPhysicalItem(diagnosis, "Areia", physicalAnalysis.getTeorAreia(), physicalUnit(physicalAnalysis.getUnidadeTeorAreia()),
                "Teor usado na classificacao granulometrica quando todas as fracoes possuem unidade compativel com a estrategia selecionada.");
        addPhysicalItem(diagnosis, "Silte", physicalAnalysis.getTeorSilte(), physicalUnit(physicalAnalysis.getUnidadeTeorSilte()),
                "Teor usado na classificacao granulometrica quando todas as fracoes possuem unidade compativel com a estrategia selecionada.");
        addPhysicalItem(diagnosis, "Argila", physicalAnalysis.getTeorArgila(), physicalUnit(physicalAnalysis.getUnidadeTeorArgila()),
                "Teor de argila considerado nos criterios quimicos e na classificacao granulometrica, quando aplicavel.");
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

        boolean hasTextureFractions = hasAnyTextureFraction(physicalAnalysis);
        boolean hasCompleteTextureFractions = hasCompleteTextureFractions(physicalAnalysis);
        boolean hasGranulometryInGramsPerKg = hasGranulometryInGramsPerKg(physicalAnalysis);
        String summary = textureClassification.classified()
                ? "Análise física considerada com classificação granulométrica " + textureClassification.texturalClass() + "."
                : hasCompleteTextureFractions && hasGranulometryInGramsPerKg
                ? "Análise física considerada com frações granulométricas em g/kg; classificação granulométrica não calculada pelo critério textural selecionado."
                : hasTextureFractions
                ? "Análise física considerada com frações granulométricas; classificação granulométrica não calculada por frações incompletas ou unidade granulométrica não confirmada em g/kg."
                : "Análise física considerada com atributos físicos disponíveis; frações granulométricas insuficientes para descrever textura.";
        return new PhysicalDiagnosis(summary, diagnosis);
    }

    private boolean hasAnyTextureFraction(PhysicalAnalysisExtractModel physicalAnalysis) {
        return physicalAnalysis.getTeorAreia() != null
                || physicalAnalysis.getTeorSilte() != null
                || physicalAnalysis.getTeorArgila() != null;
    }

    private boolean hasCompleteTextureFractions(PhysicalAnalysisExtractModel physicalAnalysis) {
        return physicalAnalysis.getTeorAreia() != null
                && physicalAnalysis.getTeorSilte() != null
                && physicalAnalysis.getTeorArgila() != null;
    }

    private boolean hasGranulometryInGramsPerKg(PhysicalAnalysisExtractModel physicalAnalysis) {
        return physicalAnalysis.getUnidadeTeorAreia() == PhysicalAnalysisUnit.G_PER_KG
                && physicalAnalysis.getUnidadeTeorSilte() == PhysicalAnalysisUnit.G_PER_KG
                && physicalAnalysis.getUnidadeTeorArgila() == PhysicalAnalysisUnit.G_PER_KG;
    }

    private void appendTextureClassification(List<SoilPhysicalDiagnosisItem> diagnosis,
                                             SoilTextureClassificationService.SoilTextureClassificationResult textureClassification,
                                             List<String> warnings) {
        if (textureClassification == null) return;
        if (!textureClassification.classified()) {
            warnings.addAll(textureClassification.warnings());
            return;
        }
        diagnosis.add(SoilPhysicalDiagnosisItem.builder()
                .attribute("Classificacao granulometrica")
                .unit(textureClassification.strategy() != null ? textureClassification.strategy().getLabel() : null)
                .technicalObservation(textureClassification.texturalClass())
                .build());
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
        addSalinityValue(diagnosis, "Sódio no extrato de saturação", saturation.getTeorNa(), "mg/dm³",
                "Valor apresentado sem classificação isolada; o sistema não possui faixa específica cadastrada para Na do extrato de saturação.");
        addSalinityValue(diagnosis, "RAS", saturation.getRas(), rasUnit(saturation),
                "Relação de adsorção de sódio usada no enquadramento salino/sódico quando há critério completo.");

        FertilityAnalysisExtractModel fertility = fertilityExtract.orElse(null);
        Double pst = fertility != null ? fertility.getPst() : null;
        Double exchangeableNa = fertility != null ? fertility.getSodio() : null;
        Double ctcPh7 = fertility != null ? fertility.getCtcPh7() : null;
        addSalinityValue(diagnosis, "PST", pst, "%",
                "Percentagem de sódio trocável informada no extrato de fertilidade e usada no enquadramento salino/sódico.");

        classifyGlobalSalinity(diagnosis, saturation, pst, table, warnings);
        classifyExchangeableSodium(diagnosis, exchangeableNa, ctcPh7,
                fertilityUnit(fertility != null ? fertility.getUnidadeSodio() : null), table, warnings);

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
        String rasUnit = c.getRasUnit() != null ? c.getRasUnit() : SalinityInterpretationModel.DEFAULT_RAS_UNIT;
        if (le(ce, c.getNormal_soil_highest_ce()) && le(pst, c.getNormal_soil_highest_pst())
                && le(ph, c.getNormal_soil_highest_ph()) && le(ras, c.getNormal_soil_highest_ras())) {
            interpretation = "Solo normal";
            usedCriterion = "CE <= " + formatNumber(c.getNormal_soil_highest_ce())
                    + "; PST <= " + formatNumber(c.getNormal_soil_highest_pst())
                    + "; pH <= " + formatNumber(c.getNormal_soil_highest_ph())
                    + "; RAS <= " + formatNumber(c.getNormal_soil_highest_ras()) + " " + rasUnit;
        } else if (ge(ce, c.getSodic_saline_soil_highest_ce()) && ge(pst, c.getSodic_saline_soil_lowest_pst())
                && ge(ph, c.getSodic_saline_soil_lowest_ph()) && ge(ras, c.getSodic_saline_soil_lowest_ras())) {
            interpretation = "Solo salino-sódico";
            usedCriterion = "CE >= " + formatNumber(c.getSodic_saline_soil_highest_ce())
                    + "; PST >= " + formatNumber(c.getSodic_saline_soil_lowest_pst())
                    + "; pH >= " + formatNumber(c.getSodic_saline_soil_lowest_ph())
                    + "; RAS >= " + formatNumber(c.getSodic_saline_soil_lowest_ras()) + " " + rasUnit;
        } else if (le(ce, c.getSodic_soil_highest_ce()) && ge(pst, c.getSodic_soil_lowest_pst())
                && ge(ph, c.getSodic_soil_lowest_ph()) && ge(ras, c.getSodic_soil_lowest_ras())) {
            interpretation = "Solo sódico";
            usedCriterion = "CE <= " + formatNumber(c.getSodic_soil_highest_ce())
                    + "; PST >= " + formatNumber(c.getSodic_soil_lowest_pst())
                    + "; pH >= " + formatNumber(c.getSodic_soil_lowest_ph())
                    + "; RAS >= " + formatNumber(c.getSodic_soil_lowest_ras()) + " " + rasUnit;
        } else if (ge(ce, c.getSaline_soil_lowest_ce()) && le(pst, c.getSaline_soil_highest_pst())
                && le(ph, c.getSaline_soil_highest_ph()) && le(ras, c.getSaline_soil_highest_ras())) {
            interpretation = "Solo salino";
            usedCriterion = "CE >= " + formatNumber(c.getSaline_soil_lowest_ce())
                    + "; PST <= " + formatNumber(c.getSaline_soil_highest_pst())
                    + "; pH <= " + formatNumber(c.getSaline_soil_highest_ph())
                    + "; RAS <= " + formatNumber(c.getSaline_soil_highest_ras()) + " " + rasUnit;
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
                                            String savedSodiumUnit,
                                            SoilFertilityInterpretationCriteriaTableModel table,
                                            List<String> warnings) {
        if (exchangeableNa == null) {
            diagnosis.add(notClassifiedSalinity("Sódio trocável", null, savedSodiumUnit,
                    "Não há sódio trocável no extrato de fertilidade para classificar pela tabela de sódio trocável."));
            return;
        }
        String sodiumUnit = savedSodiumUnit;
        if (ctcPh7 == null) {
            String observation = "Não há CTC pH 7,0 no extrato de fertilidade para selecionar a faixa de sódio trocável.";
            diagnosis.add(notClassifiedSalinity("Sódio trocável", exchangeableNa, sodiumUnit, observation));
            warnings.add(observation);
            return;
        }
        Optional<ExchangeableSodiumModel> criterion = exchangeableSodiumRepository.findFirstByTableOrderByIdAsc(table);
        if (criterion.isEmpty()) {
            String observation = "Não há critério de sódio trocável cadastrado para a tabela selecionada.";
            diagnosis.add(notClassifiedSalinity("Sódio trocável", exchangeableNa, sodiumUnit, observation));
            warnings.add(observation);
            return;
        }

        SodiumRangeCriterion range = selectSodiumRange(criterion.get(), ctcPh7);
        String criterionSodiumUnit = normalizeUnit(criterion.get().getSodiumUnit(), sodiumUnit);
        SoilSalinityDiagnosisItem item = classifySalinityRange("Sódio trocável", exchangeableNa, criterionSodiumUnit,
                new RangeCriterion(range.veryLowEnd(), range.lowStart(), range.lowEnd(), range.mediumStart(),
                        range.mediumEnd(), range.highStart(), range.highEnd(), range.veryHighStart()),
                "Sódio trocável classificado por faixa de CTC pH 7,0 informada no extrato de fertilidade (" + formatNumber(ctcPh7) + ").");
        diagnosis.add(item);
    }

    private SodiumRangeCriterion selectSodiumRange(ExchangeableSodiumModel c, Double ctcPh7) {
        if (ctcPh7 < 43.0) {
            return new SodiumRangeCriterion(c.getCtcLessThan43VeryLowLessThan(), c.getCtcLessThan43LowMin(), c.getCtcLessThan43LowMax(),
                    c.getCtcLessThan43MediumMin(), c.getCtcLessThan43MediumMax(), c.getCtcLessThan43HighMin(), c.getCtcLessThan43HighMax(), c.getCtcLessThan43VeryHighGreaterThan());
        }
        if (ctcPh7 <= 86.0) {
            return new SodiumRangeCriterion(c.getCtcFrom43To86VeryLowLessThan(), c.getCtcFrom43To86LowMin(), c.getCtcFrom43To86LowMax(),
                    c.getCtcFrom43To86MediumMin(), c.getCtcFrom43To86MediumMax(), c.getCtcFrom43To86HighMin(), c.getCtcFrom43To86HighMax(), c.getCtcFrom43To86VeryHighGreaterThan());
        }
        if (ctcPh7 <= 150.0) {
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

    private String rasUnit(SaturationExtractAnalysisExtractModel saturation) {
        if (saturation == null || saturation.getUnidadeRas() == null) {
            return "(mmolc)**0.5";
        }
        return saturation.getUnidadeRas().getSymbol();
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
        Optional<DiverseContentRangeModel> diverseRange = findDiverseContentRangeByTable(table);

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
        diagnosis.add(classifyPotassium(fertility, diverseRange, warnings));
        diagnosis.add(classifyDiverseRange("Cálcio", fertility.getCalcio(), fertilityUnit(fertility.getUnidadeCalcio()), diverseRange,
                r -> new RangeCriterion(r.getCalcium_too_low(), r.getCalcium_low_i(), r.getCalcium_low_f(), r.getCalcium_medium_i(), r.getCalcium_medium_f(), r.getCalcium_hight_i(), r.getCalcium_hight_f(), r.getCalcium_too_hight()),
                "Cálcio trocável classificado pelas faixas diversas da tabela selecionada."));
        diagnosis.add(classifyDiverseRange("Magnésio", fertility.getMagnesio(), fertilityUnit(fertility.getUnidadeMagnesio()), diverseRange,
                r -> new RangeCriterion(r.getMagnesium_too_low(), r.getMagnesium_low_i(), r.getMagnesium_low_f(), r.getMagnesium_medium_i(), r.getMagnesium_medium_f(), r.getMagnesium_hight_i(), r.getMagnesium_hight_f(), r.getMagnesium_too_hight()),
                "Magnésio trocável classificado pelas faixas diversas da tabela selecionada."));
        diagnosis.add(classifyDiverseRange("Alumínio", fertility.getAluminio(), fertilityUnit(fertility.getUnidadeAluminio()), diverseRange,
                r -> new RangeCriterion(r.getAluminum_too_low(), r.getAluminum_low_i(), r.getAluminum_low_f(), r.getAluminum_medium_i(), r.getAluminum_medium_f(), r.getAluminum_hight_i(), r.getAluminum_hight_f(), r.getAluminum_too_hight()),
                "Alumínio trocável classificado pelas faixas diversas da tabela selecionada."));
        if (fertility.getEnxofre() != null) {
            diagnosis.add(classifySulfur(fertility, physicalAnalysis, table, warnings));
        }
        addDiverseDiagnosisIfPresent(diagnosis, "Matéria orgânica", fertility.getMateriaOrganica(), diverseRange.map(DiverseContentRangeModel::getOrganic_matter_unit).orElse("g/dm³"), diverseRange,
                r -> new RangeCriterion(r.getOrganic_matter_too_low(), r.getOrganic_matter_low_i(), r.getOrganic_matter_low_f(), r.getOrganic_matter_medium_i(), r.getOrganic_matter_medium_f(), r.getOrganic_matter_hight_i(), r.getOrganic_matter_hight_f(), r.getOrganic_matter_too_hight()),
                "Matéria orgânica classificada pelas faixas diversas da tabela selecionada.");
        addDiverseDiagnosisIfPresent(diagnosis, "H+Al", fertility.getAluminioMaisHidrogenio(), fertilityUnit(fertility.getUnidadeAluminioMaisHidrogenio()), diverseRange,
                r -> new RangeCriterion(r.getPotential_acidity_too_low(), r.getPotential_acidity_low_i(), r.getPotential_acidity_low_f(), r.getPotential_acidity_medium_i(), r.getPotential_acidity_medium_f(), r.getPotential_acidity_hight_i(), r.getPotential_acidity_hight_f(), r.getPotential_acidity_too_hight()),
                "Acidez potencial classificada pelas faixas diversas da tabela selecionada.");
        addDiverseDiagnosisIfPresent(diagnosis, "Soma de bases", fertility.getSomaBases(), fertilityUnit(fertility.getUnidadeSomaBases()), diverseRange,
                r -> new RangeCriterion(r.getSum_of_bases_too_low(), r.getSum_of_bases_low_i(), r.getSum_of_bases_low_f(), r.getSum_of_bases_medium_i(), r.getSum_of_bases_medium_f(), r.getSum_of_bases_hight_i(), r.getSum_of_bases_hight_f(), r.getSum_of_bases_too_hight()),
                "Soma de bases classificada pelas faixas diversas da tabela selecionada.");
        addDiverseDiagnosisIfPresent(diagnosis, "CTC efetiva", fertility.getCtcEfetiva(), fertilityUnit(fertility.getUnidadeCtcEfetiva()), diverseRange,
                r -> new RangeCriterion(r.getEffective_cec_too_low(), r.getEffective_cec_low_i(), r.getEffective_cec_low_f(), r.getEffective_cec_medium_i(), r.getEffective_cec_medium_f(), r.getEffective_cec_hight_i(), r.getEffective_cec_hight_f(), r.getEffective_cec_too_hight()),
                "CTC efetiva classificada a partir do valor pronto do extrato de fertilidade.");
        addDiverseDiagnosisIfPresent(diagnosis, "CTC pH 7,0", fertility.getCtcPh7(), fertilityUnit(fertility.getUnidadeCtcPh7()), diverseRange,
                r -> new RangeCriterion(r.getPh7_cec_too_low(), r.getPh7_cec_low_i(), r.getPh7_cec_low_f(), r.getPh7_cec_medium_i(), r.getPh7_cec_medium_f(), r.getPh7_cec_hight_i(), r.getPh7_cec_hight_f(), r.getPh7_cec_too_hight()),
                "CTC pH 7,0 classificada a partir do valor pronto do extrato de fertilidade.");
        addDiverseDiagnosisIfPresent(diagnosis, "Saturação por bases", fertility.getSaturacaoBasesV(), "%", diverseRange,
                r -> new RangeCriterion(r.getBase_saturation_too_low(), r.getBase_saturation_low_i(), r.getBase_saturation_low_f(), r.getBase_saturation_medium_i(), r.getBase_saturation_medium_f(), r.getBase_saturation_hight_i(), r.getBase_saturation_hight_f(), r.getBase_saturation_too_hight()),
                "Saturação por bases classificada a partir do valor pronto do extrato de fertilidade.");
        addDiverseDiagnosisIfPresent(diagnosis, "Saturação por alumínio", fertility.getSaturacaoAluminioM(), "%", diverseRange,
                r -> new RangeCriterion(r.getAluminum_saturation_too_low(), r.getAluminum_saturation_low_i(), r.getAluminum_saturation_low_f(), r.getAluminum_saturation_medium_i(), r.getAluminum_saturation_medium_f(), r.getAluminum_saturation_hight_i(), r.getAluminum_saturation_hight_f(), r.getAluminum_saturation_too_hight()),
                "Saturação por alumínio classificada a partir do valor pronto do extrato de fertilidade.");
        addDiverseMicronutrientDiagnosisIfPresent(diagnosis, "Boro", fertility.getBoro(), "mg/dm³", diverseRange,
                r -> new ThreeLevelCriterion(r.getBoron_low_f(), r.getBoron_medium_i(), r.getBoron_medium_f(), r.getBoron_hight_i()),
                "Boro disponível classificado pelas faixas diversas da tabela selecionada.");
        addDiverseMicronutrientDiagnosisIfPresent(diagnosis, "Cobre", fertility.getCobre(), "mg/dm³", diverseRange,
                r -> new ThreeLevelCriterion(r.getCopper_low_f(), r.getCopper_medium_i(), r.getCopper_medium_f(), r.getCopper_hight_i()),
                "Cobre disponível classificado pelas faixas diversas da tabela selecionada.");
        addDiverseMicronutrientDiagnosisIfPresent(diagnosis, "Ferro", fertility.getFerro(), "mg/dm³", diverseRange,
                r -> new ThreeLevelCriterion(r.getIron_low_f(), r.getIron_medium_i(), r.getIron_medium_f(), r.getIron_hight_i()),
                "Ferro disponível classificado pelas faixas diversas da tabela selecionada.");
        addDiverseMicronutrientDiagnosisIfPresent(diagnosis, "Manganês", fertility.getManganes(), "mg/dm³", diverseRange,
                r -> new ThreeLevelCriterion(r.getManganese_low_f(), r.getManganese_medium_i(), r.getManganese_medium_f(), r.getManganese_hight_i()),
                "Manganês disponível classificado pelas faixas diversas da tabela selecionada.");
        addDiverseMicronutrientDiagnosisIfPresent(diagnosis, "Zinco", fertility.getZinco(), "mg/dm³", diverseRange,
                r -> new ThreeLevelCriterion(r.getZinc_low_f(), r.getZinc_medium_i(), r.getZinc_medium_f(), r.getZinc_hight_i()),
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
                return notClassified("Fósforo (P) Mehlich-1", fertility.getFosforoMehlich1(), "mg/dm³", observation);
            }
            Double clay = physicalAnalysis != null ? physicalAnalysis.getTeorArgila() : null;
            if (clay == null) {
                String observation = "Não há teor de argila na análise física para selecionar a faixa de P Mehlich-1.";
                warnings.add(observation);
                return notClassified("Fósforo (P) Mehlich-1", fertility.getFosforoMehlich1(), "mg/dm³", observation);
            }
            double clayGkg = clay;
            AvailablePMehlich1ExtractorModel p = criterion.get();
            RangeCriterion range = clayGkg < 150
                    ? new RangeCriterion(p.getP_content_sandy_too_low(), p.getP_content_sandy_low_i(), p.getP_content_sandy_low_f(), p.getP_content_sandy_medium_i(), p.getP_content_sandy_medium_f(), p.getP_content_sandy_hight_i(), p.getP_content_sandy_hight_f(), p.getP_content_sandy_too_hight())
                    : clayGkg <= 350
                    ? new RangeCriterion(p.getP_content_sandy_clayey_too_low(), p.getP_content_sandy_clayey_low_i(), p.getP_content_sandy_clayey_low_f(), p.getP_content_sandy_clayey_medium_i(), p.getP_content_sandy_clayey_medium_f(), p.getP_content_sandy_clayey_hight_i(), p.getP_content_sandy_clayey_hight_f(), p.getP_content_sandy_clayey_too_hight())
                    : clayGkg <= 600
                    ? new RangeCriterion(p.getP_content_clayey_too_low(), p.getP_content_clayey_low_i(), p.getP_content_clayey_low_f(), p.getP_content_clayey_medium_i(), p.getP_content_clayey_medium_f(), p.getP_content_clayey_hight_i(), p.getP_content_clayey_hight_f(), p.getP_content_clayey_too_hight())
                    : new RangeCriterion(p.getP_content_very_clayey_too_low(), p.getP_content_very_clayey_low_i(), p.getP_content_very_clayey_low_f(), p.getP_content_very_clayey_medium_i(), p.getP_content_very_clayey_medium_f(), p.getP_content_very_clayey_hight_i(), p.getP_content_very_clayey_hight_f(), p.getP_content_very_clayey_too_hight());
            return classifyRange("Fósforo (P) Mehlich-1", fertility.getFosforoMehlich1(), "mg/dm³", range,
                    "Critério de fósforo (P) disponível por Mehlich-1 selecionado pelo teor de argila em g/kg da análise física.");
        }
        if (fertility.getFosforoResina() != null) {
            Optional<AvailablePAnionExchangeResinExtractorModel> criterion = availablePAnionExchangeResinExtractorRepository.findByTable(table);
            if (criterion.isEmpty()) {
                String observation = "Não há critério de P por resina na tabela selecionada.";
                warnings.add(observation);
                return notClassified("Fósforo (P) resina", fertility.getFosforoResina(), "mg/dm³", observation);
            }
            AvailablePAnionExchangeResinExtractorModel p = criterion.get();
            return classifyRange("Fósforo (P) resina", fertility.getFosforoResina(), p.getUnit(),
                    new RangeCriterion(p.getPContentTooLow(), p.getPContentLowI(), p.getPContentLowF(), p.getPContentMediumI(), p.getPContentMediumF(), p.getPContentHighI(), p.getPContentHighF(), p.getPContentTooHigh()),
                    "Fósforo (P) disponível por resina classificado pelo critério específico da tabela selecionada.");
        }
        return missingValue("Fósforo (P)", "Não há valor de fósforo disponível por Mehlich-1 ou resina no extrato de fertilidade.");
    }

    private Optional<DiverseContentRangeModel> findDiverseContentRangeByTable(SoilFertilityInterpretationCriteriaTableModel table) {
        if (table == null) {
            return Optional.empty();
        }
        if (table.getId() != null) {
            Optional<DiverseContentRangeModel> byTableId = diverseContentRangeRepository.findByTable_Id(table.getId());
            if (byTableId.isPresent()) {
                return byTableId;
            }
        }
        return diverseContentRangeRepository.findByTable(table);
    }

    private SoilChemicalDiagnosisItem classifyPotassium(FertilityAnalysisExtractModel fertility,
                                                         Optional<DiverseContentRangeModel> diverseRange,
                                                         List<String> warnings) {
        if (fertility.getPotassio() == null) {
            return missingValue("Potássio (K) trocável", "Não há valor de potássio trocável no extrato de fertilidade.");
        }
        String unit = fertility.getUnidadePotassio() != null
                ? fertility.getUnidadePotassio().getSymbol()
                : "mmolc/dm³";
        if (fertility.getUnidadePotassio() != null
                && fertility.getUnidadePotassio() != FertilityAnalysisUnit.MMOLC_PER_DM3) {
            String observation = "A unidade do potássio trocável é incompatível com o critério auxiliar em mmolc/dm³.";
            warnings.add(observation);
            return notClassified("Potássio (K) trocável", fertility.getPotassio(), unit, observation);
        }
        if (diverseRange.isEmpty()) {
            String observation = "Não há critério auxiliar de potássio em Teores de Nutrientes Diversos na tabela selecionada.";
            warnings.add(observation);
            return notClassified("Potássio (K) trocável", fertility.getPotassio(), unit, observation);
        }
        DiverseContentRangeModel range = diverseRange.get();
        return classifyThreeLevelRange("Potássio (K) trocável", fertility.getPotassio(), unit,
                new ThreeLevelCriterion(range.getPotassium_low_f(), range.getPotassium_medium_i(),
                        range.getPotassium_medium_f(), range.getPotassium_hight_i()),
                "Potássio trocável classificado pelas faixas de Teores de Nutrientes Diversos.");
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
                return notClassified("Enxofre", fertility.getEnxofre(), "mg/dm³", observation);
        }
        Double clay = physicalAnalysis != null ? physicalAnalysis.getTeorArgila() : null;
        if (clay == null) {
            String observation = "Não há teor de argila na análise física para selecionar a faixa de enxofre.";
            warnings.add(observation);
            return notClassified("Enxofre", fertility.getEnxofre(), "mg/dm³", observation);
        }
        AvailableSModel s = criterion.get();
        RangeCriterion range = clay < 400
                ? new RangeCriterion(s.getSContentLess400TooLow(), s.getSContentLess400LowI(), s.getSContentLess400LowF(), s.getSContentLess400MediumI(), s.getSContentLess400MediumF(), s.getSContentLess400HighI(), s.getSContentLess400HighF(), s.getSContentLess400TooHigh())
                : new RangeCriterion(s.getSContentGreater400TooLow(), s.getSContentGreater400LowI(), s.getSContentGreater400LowF(), s.getSContentGreater400MediumI(), s.getSContentGreater400MediumF(), s.getSContentGreater400HighI(), s.getSContentGreater400HighF(), s.getSContentGreater400TooHigh());
        return classifyRange("Enxofre", fertility.getEnxofre(), "mg/dm³", range,
                "Enxofre classificado pelo critério específico selecionado pelo teor de argila em g/kg da análise física.");
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

    private void addDiverseMicronutrientDiagnosisIfPresent(List<SoilChemicalDiagnosisItem> diagnosis,
                                                           String attribute,
                                                           Double value,
                                                           String unit,
                                                           Optional<DiverseContentRangeModel> range,
                                                           Function<DiverseContentRangeModel, ThreeLevelCriterion> criterionExtractor,
                                                           String observation) {
        if (value == null) return;
        if (range.isEmpty()) {
            diagnosis.add(notClassified(attribute, value, unit, "Critério ausente na tabela selecionada."));
            return;
        }
        diagnosis.add(classifyThreeLevelRange(attribute, value, unit, criterionExtractor.apply(range.get()), observation));
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

    private SoilChemicalDiagnosisItem classifyThreeLevelRange(String attribute, Double value, String unit, ThreeLevelCriterion criterion, String observation) {
        if (value == null) return missingValue(attribute, "Valor ausente no extrato de fertilidade.");
        if (criterion == null || criterion.lowLimit() == null || criterion.mediumStart() == null
                || criterion.mediumEnd() == null || criterion.highLimit() == null) {
            return notClassified(attribute, value, unit, "Critério incompleto na tabela selecionada.");
        }
        String interpretation = classifyThreeLevelRangeName(value, criterion);
        if (interpretation == null) {
            return notClassified(attribute, value, unit,
                    "Valor fora dos intervalos funcionais de micronutrientes cadastrados na tabela selecionada.");
        }
        String usedRange = switch (interpretation) {
            case "Baixo" -> "< " + formatNumber(criterion.lowLimit());
            case "Médio" -> formatNumber(criterion.mediumStart()) + " a " + formatNumber(criterion.mediumEnd());
            case "Alto" -> "> " + formatNumber(criterion.highLimit());
            default -> "não classificado";
        };
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
        if (table == null) {
            warnings.add("Tabela de interpretação foliar não informada; diagnóstico foliar não calculado.");
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
                                      List<FoliarDiagnosisItem> foliarDiagnosis) {
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
            case g_per_dm3 -> "g/dm³";
            case mg_per_dm3 -> "mg/dm³";
            case cmolc_per_dm3 -> "cmolc/dm³";
            case mmolc_per_dm3 -> "mmolc/dm³";
            case percentage -> "%";
        };
    }

    private String physicalUnit(PhysicalAnalysisUnit unit) {
        PhysicalAnalysisUnit normalized = unit != null ? unit.canonicalForPhysicalExtract() : PhysicalAnalysisUnit.G_PER_KG;
        return normalizeUnit(normalized.getSymbol(), "g/kg");
    }

    private String fertilityUnit(FertilityAnalysisUnit unit) {
        FertilityAnalysisUnit normalized = unit != null ? unit.canonicalForFertilityExtract() : FertilityAnalysisUnit.MMOLC_PER_DM3;
        return normalizeUnit(normalized.getSymbol(), "mmolc/dm³");
    }

    private String normalizeUnit(String unit, String fallback) {
        if (unit == null || unit.isBlank()) return fallback;
        return unit.trim()
                .replace("dm3", "dm³")
                .replace("cmolc/dm³", "mmolc/dm³")
                .replace("cmolc/dm3", "mmolc/dm³");
    }

    private String formatInterval(Double start, Double end, Double fallbackEndExclusive) {
        Double effectiveEnd = end != null ? end : fallbackEndExclusive;
        return formatNumber(start) + " a " + formatNumber(effectiveEnd);
    }

    private String formatNumber(Double value) {
        if (value == null) return "não informado";
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }

    private String describePhysicalExtract(PhysicalAnalysisExtractModel extract) {
        return "extrato físico ID " + extract.getId() + " " + describeDepth(extract.getRangeExtract(), extract.getLayerExtract());
    }

    private String describeFertilityExtract(FertilityAnalysisExtractModel extract) {
        return "extrato de fertilidade ID " + extract.getId() + " " + describeDepth(extract.getRangeExtract(), extract.getLayerExtract());
    }

    private String describeSaturationExtract(SaturationExtractAnalysisExtractModel extract) {
        return "extrato de saturação ID " + extract.getId() + " " + describeDepth(extract.getRangeExtract(), extract.getLayerExtract());
    }

    private String describeDepth(com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel range,
                                 com.migueltcc.fertintelligence.model.fertintelligence.extractModels.LayerExtractModel layer) {
        if (range != null) {
            return "(intervalo " + range.getProfundidade_inicial() + "-" + range.getProfundidade_final() + " cm)";
        }
        if (layer != null) {
            return "(camada " + layer.getLayer() + "/" + layer.getSub_layer()
                    + ", " + layer.getProfundidade_inicial() + "-" + layer.getProfundidade_final() + " cm)";
        }
        return "(sem intervalo/camada informado)";
    }

    private String normalizeText(String value) {
        if (value == null) return "";
        return java.text.Normalizer.normalize(value, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }

    private record PhysicalDiagnosis(String summary, List<SoilPhysicalDiagnosisItem> items) {}
    private record SalinityDiagnosis(String summary, List<SoilSalinityDiagnosisItem> items) {}
    private record RangeCriterion(Double tooLowEnd, Double lowStart, Double lowEnd, Double mediumStart,
                                  Double mediumEnd, Double highStart, Double highEnd, Double tooHighStart) {}
    private record ThreeLevelCriterion(Double lowLimit, Double mediumStart, Double mediumEnd, Double highLimit) {}
    private record SodiumRangeCriterion(Double veryLowEnd, Double lowStart, Double lowEnd, Double mediumStart,
                                        Double mediumEnd, Double highStart, Double highEnd, Double veryHighStart) {}
    private PhysicalAnalysisSelection findPhysicalAnalysisSelection(Long analysisId,
                                                                    Long legacyExtractId,
                                                                    List<String> warnings) {
        if (analysisId != null) {
            SoilAnalysisModel analysis = soilAnalysisRepository.findById(analysisId)
                    .orElseThrow(() -> new EntityNotFoundException("Análise física não encontrada com o ID: " + analysisId));
            List<PhysicalAnalysisExtractModel> extracts = findPhysicalExtractsByAnalysis(analysis);
            if (extracts.isEmpty()) {
                warnings.add("Análise física ID " + analysisId + " não possui extratos/camadas/intervalos cadastrados.");
            }
            return new PhysicalAnalysisSelection(Optional.of(analysis), extracts, selectPrimaryPhysicalExtract(extracts, analysisId, warnings));
        }
        if (legacyExtractId == null) {
            return new PhysicalAnalysisSelection(Optional.empty(), List.of(), Optional.empty());
        }
        PhysicalAnalysisExtractModel extract = physicalAnalysisExtractRepository.findById(legacyExtractId)
                .orElseThrow(() -> new EntityNotFoundException("Extrato de análise física não encontrado com o ID: " + legacyExtractId));
        SoilAnalysisModel analysis = resolveSoilAnalysis(extract);
        List<PhysicalAnalysisExtractModel> extracts = findPhysicalExtractsByAnalysis(analysis);
        if (extracts.stream().noneMatch(item -> Objects.equals(item.getId(), extract.getId()))) {
            extracts = sortPhysicalExtracts(joinWithLegacy(extracts, extract));
        }
        warnings.add("Compatibilidade temporária: foi recebido ID de extrato físico legado; a análise completa associada foi carregada no backend.");
        return new PhysicalAnalysisSelection(Optional.ofNullable(analysis), extracts, selectPrimaryPhysicalExtract(extracts, analysis.getId(), warnings));
    }

    private FertilityAnalysisSelection findSoilFertilitySelectionByIdOrThrow(Long id, PlotModel requestPlot, List<String> warnings) {
        Optional<SoilAnalysisModel> soilAnalysis = soilAnalysisRepository.findById(id);
        if (soilAnalysis.isPresent()
                && (requestPlot == null
                || soilAnalysis.get().getPlot() == null
                || Objects.equals(soilAnalysis.get().getPlot().getId(), requestPlot.getId()))) {
            List<FertilityAnalysisExtractModel> extracts = findFertilityExtractsByAnalysis(soilAnalysis.get());
            if (extracts.isEmpty()) {
                warnings.add("Análise de fertilidade ID " + id + " não possui extratos/camadas/intervalos cadastrados.");
            }
            return new FertilityAnalysisSelection(soilAnalysis.get(), extracts, selectPrimaryFertilityExtract(extracts, soilAnalysis.get().getId(), warnings));
        }

        Optional<FertilityAnalysisExtractModel> extract = fertilityAnalysisExtractRepository.findById(id);
        if (extract.isPresent()) {
            SoilAnalysisModel soil = resolveSoilAnalysis(extract.get());
            if (soil.getPlot() != null && requestPlot != null && Objects.equals(soil.getPlot().getId(), requestPlot.getId())) {
                List<FertilityAnalysisExtractModel> extracts = findFertilityExtractsByAnalysis(soil);
                if (extracts.stream().noneMatch(item -> Objects.equals(item.getId(), extract.get().getId()))) {
                    extracts = sortFertilityExtracts(joinWithLegacy(extracts, extract.get()));
                }
                warnings.add("Compatibilidade temporária: o ID de fertilidade recebido corresponde a um extrato legado; a análise completa associada foi carregada no backend.");
                return new FertilityAnalysisSelection(soil, extracts, selectPrimaryFertilityExtract(extracts, soil.getId(), warnings));
            }
        }

        if (soilAnalysis.isPresent()) {
            List<FertilityAnalysisExtractModel> extracts = findFertilityExtractsByAnalysis(soilAnalysis.get());
            if (extracts.isEmpty()) {
                warnings.add("Análise de fertilidade ID " + id + " não possui extratos/camadas/intervalos cadastrados.");
            }
            return new FertilityAnalysisSelection(soilAnalysis.get(), extracts, selectPrimaryFertilityExtract(extracts, soilAnalysis.get().getId(), warnings));
        }

        return extract.map(value -> {
                    SoilAnalysisModel resolvedSoil = resolveSoilAnalysis(value);
                    List<FertilityAnalysisExtractModel> extracts = findFertilityExtractsByAnalysis(resolvedSoil);
                    if (extracts.stream().noneMatch(item -> Objects.equals(item.getId(), value.getId()))) {
                        extracts = sortFertilityExtracts(joinWithLegacy(extracts, value));
                    }
                    warnings.add("Compatibilidade temporária: o ID de fertilidade recebido corresponde a um extrato legado; a análise completa associada foi carregada no backend.");
                    return new FertilityAnalysisSelection(resolvedSoil, extracts, selectPrimaryFertilityExtract(extracts, resolvedSoil.getId(), warnings));
                })
                .orElseThrow(() -> new EntityNotFoundException("Análise de fertilidade do solo não encontrada com o ID: " + id));
    }

    private SaturationAnalysisSelection findSaturationAnalysisSelection(Long analysisId,
                                                                        Long legacyExtractId,
                                                                        List<String> warnings) {
        if (analysisId != null) {
            SoilAnalysisModel analysis = soilAnalysisRepository.findById(analysisId)
                    .orElseThrow(() -> new EntityNotFoundException("Análise de extrato de saturação não encontrada com o ID: " + analysisId));
            List<SaturationExtractAnalysisExtractModel> extracts = findSaturationExtractsByAnalysis(analysis);
            if (extracts.isEmpty()) {
                warnings.add("Análise de extrato de saturação ID " + analysisId + " não possui extratos/camadas/intervalos cadastrados.");
            }
            return new SaturationAnalysisSelection(Optional.of(analysis), extracts, selectPrimarySaturationExtract(extracts, analysisId, warnings));
        }
        if (legacyExtractId == null) {
            return new SaturationAnalysisSelection(Optional.empty(), List.of(), Optional.empty());
        }
        SaturationExtractAnalysisExtractModel extract = saturationExtractAnalysisExtractRepository.findById(legacyExtractId)
                .orElseThrow(() -> new EntityNotFoundException("Extrato de análise de saturação não encontrado com o ID: " + legacyExtractId));
        SoilAnalysisModel analysis = resolveSoilAnalysis(extract);
        List<SaturationExtractAnalysisExtractModel> extracts = findSaturationExtractsByAnalysis(analysis);
        if (extracts.stream().noneMatch(item -> Objects.equals(item.getId(), extract.getId()))) {
            extracts = sortSaturationExtracts(joinWithLegacy(extracts, extract));
        }
        warnings.add("Compatibilidade temporária: foi recebido ID de extrato de saturação legado; a análise completa associada foi carregada no backend.");
        return new SaturationAnalysisSelection(Optional.ofNullable(analysis), extracts, selectPrimarySaturationExtract(extracts, analysis.getId(), warnings));
    }

    private AnnualCropFolderModel findAnnualCropFolderByIdOrThrow(Long id) {
        return annualCropFolderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pasta de cultura anual não encontrada com o ID: " + id));
    }

    private Optional<CropModel> resolveCropFromRecommendationContext(RecommendationCreateRequestDto dto,
                                                                     AnnualCropFolderModel annualCropFolder,
                                                                     List<String> warnings) {
        if (dto.getCropId() != null) {
            Optional<CropModel> selectedCrop = cropRepository.findById(dto.getCropId());
            if (selectedCrop.isPresent()) {
                return selectedCrop;
            }
            warnings.add("Cultura informada não foi encontrada; tentando resolver cultura pela pasta anual.");
        }

        List<CropModel> crops = cropRepository.findAllByFolderId(annualCropFolder.getId());
        Optional<CropModel> resolvedCrop = crops.stream()
                .max(Comparator.comparing(crop -> crop.getId() == null ? 0L : crop.getId()));
        if (resolvedCrop.isEmpty()) {
            warnings.add("Nenhuma cultura cadastrada foi encontrada na pasta anual informada.");
        } else if (dto.getCropId() == null) {
            warnings.add("Cultura resolvida automaticamente a partir da pasta anual informada.");
        }
        return resolvedCrop;
    }

    private CropModel findCropByIdOrThrow(Long id) {
        return cropRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cultura não encontrada com o ID: " + id));
    }

    private CropFertilizationTableModel findCropFertilizationTableBySelectionOrThrow(Long id,
                                                                                     TechnicalTableGroup group,
                                                                                     UserModel user) {
        return findCropFertilizationTableBySelection(id, group, user)
                .orElseThrow(() -> new EntityNotFoundException("Tabela de adubação de culturas não encontrada para o grupo " + group + " e ID: " + id));
    }

    private SoilFertilityInterpretationCriteriaTableModel findSoilFertilityInterpretationTableBySelectionOrThrow(
            Long id,
            TechnicalTableGroup group,
            UserModel user) {
        return findSoilFertilityInterpretationTableBySelection(id, group, user)
                .orElseThrow(() -> new EntityNotFoundException("Tabela de interpretação da fertilidade do solo não encontrada para o grupo " + group + " e ID: " + id));
    }

    private CropFoliarAnalysisInterpretationTableModel findCropFoliarAnalysisInterpretationTableBySelectionOrThrow(
            Long id,
            TechnicalTableGroup group,
            UserModel user) {
        return findCropFoliarAnalysisInterpretationTableBySelection(id, group, user)
                .orElseThrow(() -> new EntityNotFoundException("Tabela de interpretação de análise foliar não encontrada para o grupo " + group + " e ID: " + id));
    }

    private Optional<CropFoliarAnalysisInterpretationTableModel> findOptionalCropFoliarAnalysisInterpretationTable(
            Long id,
            TechnicalTableGroup group,
            UserModel user,
            List<String> warnings) {
        if (id == null && group == null) {
            warnings.add("Tabela de interpretação de análise foliar não informada; rotinas dependentes desse critério foram tratadas como indisponíveis.");
            return Optional.empty();
        }
        return Optional.of(findCropFoliarAnalysisInterpretationTableBySelectionOrThrow(id, group, user));
    }

    private Optional<CropFertilizationTableModel> findCropFertilizationTableBySelection(Long id,
                                                                                       TechnicalTableGroup group,
                                                                                       UserModel user) {
        validateTableSelection(id, group, "tabela de adubação de culturas");
        return switch (group) {
            case MINHAS, PRIVADAS -> cropFertilizationTableRepository.findByIdAndCreatorAndCreator_CargoNot(id, user, Cargo.USUARIO_SUPREMO);
            case PUBLICAS -> cropFertilizationTableRepository.findByIdAndPublicTableTrueAndCreator_CargoNot(id, Cargo.USUARIO_SUPREMO);
            case PADRAO -> cropFertilizationTableRepository.findByIdAndCreator_CargoAndPublicTableTrue(id, Cargo.USUARIO_SUPREMO);
        };
    }

    private Optional<SoilFertilityInterpretationCriteriaTableModel> findSoilFertilityInterpretationTableBySelection(
            Long id,
            TechnicalTableGroup group,
            UserModel user) {
        validateTableSelection(id, group, "tabela de interpretação da fertilidade do solo");
        return switch (group) {
            case MINHAS, PRIVADAS -> soilFertilityInterpretationCriteriaTableRepository.findByIdAndCreatorAndCreator_CargoNot(id, user, Cargo.USUARIO_SUPREMO);
            case PUBLICAS -> soilFertilityInterpretationCriteriaTableRepository.findByIdAndPublicTableTrueAndCreator_CargoNot(id, Cargo.USUARIO_SUPREMO);
            case PADRAO -> soilFertilityInterpretationCriteriaTableRepository.findByIdAndCreator_Cargo(id, Cargo.USUARIO_SUPREMO);
        };
    }

    private Optional<CropFoliarAnalysisInterpretationTableModel> findCropFoliarAnalysisInterpretationTableBySelection(
            Long id,
            TechnicalTableGroup group,
            UserModel user) {
        validateTableSelection(id, group, "tabela de interpretação de análise foliar");
        return switch (group) {
            case MINHAS, PRIVADAS -> cropFoliarAnalysisInterpretationTableRepository.findByIdAndCreatorAndCreator_CargoNot(id, user, Cargo.USUARIO_SUPREMO);
            case PUBLICAS -> cropFoliarAnalysisInterpretationTableRepository.findByIdAndPublicTableTrueAndCreator_CargoNot(id, Cargo.USUARIO_SUPREMO);
            case PADRAO -> cropFoliarAnalysisInterpretationTableRepository.findByIdAndCreator_Cargo(id, Cargo.USUARIO_SUPREMO);
        };
    }

    private void validateTableSelection(Long id, TechnicalTableGroup group, String tableName) {
        if (id == null) {
            throw new IllegalArgumentException("ID da " + tableName + " é obrigatório.");
        }
        if (group == null) {
            throw new IllegalArgumentException("Grupo da " + tableName + " é obrigatório.");
        }
    }

    private void validateSamePlot(PlotModel selectedPlot, PlotModel requestPlot, String message) {
        if (selectedPlot == null || requestPlot == null || !Objects.equals(selectedPlot.getId(), requestPlot.getId())) {
            throw new IllegalArgumentException(message);
        }
    }

    private PlotModel resolvePlot(PhysicalAnalysisExtractModel model) {
        if (model.getRangeExtract() != null && model.getRangeExtract().getAnalysis() != null) {
            return model.getRangeExtract().getAnalysis().getPlot();
        }
        if (model.getLayerExtract() != null && model.getLayerExtract().getAnalysis() != null) {
            return model.getLayerExtract().getAnalysis().getPlot();
        }
        throw new IllegalArgumentException("Extrato de análise física não possui análise de solo associada.");
    }

    private PlotModel resolvePlot(SaturationExtractAnalysisExtractModel model) {
        if (model.getRangeExtract() != null && model.getRangeExtract().getAnalysis() != null) {
            return model.getRangeExtract().getAnalysis().getPlot();
        }
        if (model.getLayerExtract() != null && model.getLayerExtract().getAnalysis() != null) {
            return model.getLayerExtract().getAnalysis().getPlot();
        }
        throw new IllegalArgumentException("Extrato de análise de saturação não possui análise de solo associada.");
    }

    private Optional<FoliarAnalysisModel> findLatestFoliarAnalysis(CropModel crop) {
        return foliarAnalysisRepository.findTopByCropOrderByIdDesc(crop);
    }

    private record PhysicalAnalysisSelection(
            Optional<SoilAnalysisModel> analysis,
            List<PhysicalAnalysisExtractModel> extracts,
            Optional<PhysicalAnalysisExtractModel> primaryExtract
    ) {}

    private record FertilityAnalysisSelection(
            SoilAnalysisModel soilAnalysis,
            List<FertilityAnalysisExtractModel> extracts,
            Optional<FertilityAnalysisExtractModel> primaryExtract
    ) {}

    private record SaturationAnalysisSelection(
            Optional<SoilAnalysisModel> analysis,
            List<SaturationExtractAnalysisExtractModel> extracts,
            Optional<SaturationExtractAnalysisExtractModel> primaryExtract
    ) {}

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
        private Double cropUsedAreaInThePlot;
        private com.migueltcc.fertintelligence.composedAttributes.crop.Date cropPlantingDate;
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
        private Double requiredS;
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
        @Builder.Default
        private List<MicronutrientFertilizerRecommendationRow> micronutrientFertilizerRows = new ArrayList<>();
        @Builder.Default
        private List<PlantingFormulatedFertilizerRecommendationRow> plantingFormulatedFertilizerRows = new ArrayList<>();
        @Builder.Default
        private List<CoverageFormulatedFertilizerRecommendationRow> coverageFormulatedFertilizerRows = new ArrayList<>();
        @Builder.Default
        private List<OpportunityCostNutrientPriceRow> opportunityCostNutrientPrices = new ArrayList<>();
        @Builder.Default
        private List<OpportunityCostDecisionRow> opportunityCostDecisionRows = new ArrayList<>();
    }
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LimingRequirementResult {
        private String selectedCriteria;
        private String criterionJustification;
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
        @Builder.Default
        private Boolean evaluated = true;
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
        private Double sulfurEquivalent;
        private String applicationRecommendation;
        private Boolean lowDoseAlternativeApplicable;
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
    @Builder(toBuilder = true)
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
        private Double providedS;
        private Double balanceN;
        private Double balanceP2O5;
        private Double balanceK2O;
        private Double balanceS;
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
        private Double s;
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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MicronutrientFertilizerRecommendationRow {
        private AppliedMicronutrient micronutrient;
        private Double micronutrientDoseKgHa;
        private Long fertilizerId;
        private String fertilizerName;
        private Double micronutrientConcentrationPercent;
        private Double fertilizerDoseKgHa;
        private String doseUnitMode;
        private String doseUnitLabel;
        private Double gramsPerLinearMeter;
        private Double gramsPerPit;
        private String technicalObservation;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlantingFormulatedFertilizerRecommendationRow {
        private String phase;
        private Long fertilizerId;
        private String fertilizerName;
        private Double nitrogenPercent;
        private Double p2o5Percent;
        private Double k2oPercent;
        private String relationUsed;
        private String selectionType;
        private Double doseKgHa;
        private String limitingNutrient;
        private Double coveragePercent;
        private Double providedN;
        private Double providedP2O5;
        private Double providedK2O;
        private Double balanceN;
        private Double balanceP2O5;
        private Double balanceK2O;
        private Double deficitN;
        private Double deficitP2O5;
        private Double deficitK2O;
        private String doseUnitMode;
        private String doseUnitLabel;
        private Double gramsPerLinearMeter;
        private Double gramsPerPit;
        private String technicalObservation;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CoverageFormulatedFertilizerRecommendationRow {
        private Integer coverageOrder;
        private String phase;
        private Long fertilizerId;
        private String fertilizerName;
        private Double nitrogenPercent;
        private Double p2o5Percent;
        private Double k2oPercent;
        private Double requiredN;
        private Double requiredP2O5;
        private Double requiredK2O;
        private String relationUsed;
        private String selectionType;
        private Double doseKgHa;
        private String limitingNutrient;
        private Double coveragePercent;
        private Double providedN;
        private Double providedP2O5;
        private Double providedK2O;
        private Double providedS;
        private Double balanceN;
        private Double balanceP2O5;
        private Double balanceK2O;
        private Double balanceS;
        private Double deficitN;
        private Double deficitP2O5;
        private Double deficitK2O;
        private Double deficitS;
        private String doseUnitMode;
        private String doseUnitLabel;
        private Double gramsPerLinearMeter;
        private Double gramsPerPit;
        private String technicalObservation;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OpportunityCostNutrientPriceRow {
        private String nutrient;
        private BigDecimal pricePerKg;
        private String sourceName;
        private String sourceType;
        private BigDecimal commercialWeightKg;
        private BigDecimal commercialPrice;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OpportunityCostDecisionRow {
        private String fertilizerName;
        private String category;
        private String commercialPriceLabel;
        private BigDecimal commercialPrice;
        private String opportunityPriceLabel;
        private BigDecimal opportunityPrice;
        private BigDecimal commercialWeightKg;
        private BigDecimal ratio;
        private String decision;
        private boolean indeterminate;
        private String justification;
        private String contributionSummary;
    }
}
