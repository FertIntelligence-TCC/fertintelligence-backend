package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.MenorMaiorTeores;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Nutriente;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.UnidadeTeor;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
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
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableModel;
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
    private final KExchangeableContentRepository kExchangeableContentRepository;
    private final AvailablePMehlich1ExtractorRepository availablePMehlich1ExtractorRepository;
    private final AvailablePAnionExchangeResinExtractorRepository availablePAnionExchangeResinExtractorRepository;
    private final AvailableSRepository availableSRepository;
    private final SulfurDoseRepository sulfurDoseRepository;
    private final ExchangeableSodiumRepository exchangeableSodiumRepository;
    private final SalinityInterpretationRepository salinityInterpretationRepository;

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
                                            KExchangeableContentRepository kExchangeableContentRepository,
                                            AvailablePMehlich1ExtractorRepository availablePMehlich1ExtractorRepository,
                                            AvailablePAnionExchangeResinExtractorRepository availablePAnionExchangeResinExtractorRepository,
                                            AvailableSRepository availableSRepository,
                                            SulfurDoseRepository sulfurDoseRepository,
                                            ExchangeableSodiumRepository exchangeableSodiumRepository,
                                            SalinityInterpretationRepository salinityInterpretationRepository,
                                            LimingRequirementCalculator limingRequirementCalculator,
                                            GypsumCalculationService gypsumCalculationService,
                                            SoilTextureClassificationService soilTextureClassificationService,
                                            NutrientFertilizationCalculationService nutrientFertilizationCalculationService) {
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
        this.kExchangeableContentRepository = kExchangeableContentRepository;
        this.availablePMehlich1ExtractorRepository = availablePMehlich1ExtractorRepository;
        this.availablePAnionExchangeResinExtractorRepository = availablePAnionExchangeResinExtractorRepository;
        this.availableSRepository = availableSRepository;
        this.sulfurDoseRepository = sulfurDoseRepository;
        this.exchangeableSodiumRepository = exchangeableSodiumRepository;
        this.salinityInterpretationRepository = salinityInterpretationRepository;
        this.limingRequirementCalculator = limingRequirementCalculator;
        this.gypsumCalculationService = gypsumCalculationService;
        this.soilTextureClassificationService = soilTextureClassificationService;
        this.nutrientFertilizationCalculationService = nutrientFertilizationCalculationService;
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
        PhysicalAnalysisExtractModel physicalAnalysis = findPhysicalAnalysisExtractByIdOrNull(dto.getPhysicalAnalysisExtractId());
        FertilityAnalysisSelection soilFertilitySelection = findSoilFertilitySelectionByIdOrThrow(dto.getSoilFertilityAnalysisId(), plot);
        SoilAnalysisModel soilFertilityAnalysis = soilFertilitySelection.soilAnalysis();
        SaturationExtractAnalysisExtractModel saturationExtractAnalysis = findSaturationExtractAnalysisExtractByIdOrNull(dto.getSaturationExtractAnalysisExtractId());
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
        if (inputs.physicalAnalysis() != null) {
            validateSamePlot(resolvePlot(inputs.physicalAnalysis()), plot, "O extrato de análise física selecionado não pertence ao talhão informado.");
        }
        validateSamePlot(inputs.soilFertilityAnalysis().getPlot(), plot, "A análise de fertilidade selecionada não pertence ao talhão informado.");
        if (inputs.saturationExtractAnalysis() != null) {
            validateSamePlot(resolvePlot(inputs.saturationExtractAnalysis()), plot, "O extrato de análise de saturação selecionado não pertence ao talhão informado.");
        }
        validateSamePlot(inputs.annualCropFolder().getPlot(), plot, "A pasta de cultura anual selecionada não pertence ao talhão informado.");
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
        String physicalSummary = physicalDiagnosis.summary();
        String soilFertilitySummary = "Análise de fertilidade considerada na recomendação.";
        String cropSummary = "Cultura considerada conforme cabeçalho do laudo.";
        List<FoliarDiagnosisItem> foliarDiagnosis = buildFoliarDiagnosis(inputs.foliarAnalysis(), inputs.crop(), inputs.foliarInterpretationTable(), warnings);
        String foliarSummary = buildFoliarSummary(inputs.foliarAnalysis(), foliarDiagnosis, warnings);

        List<String> correctionMessages = buildCorrectionMessages(dto, inputs.fertilityExtract(), Optional.ofNullable(inputs.saturationExtractAnalysis()), warnings);
        LimingRequirementResult limingRequirement = limingRequirementCalculator.calculate(
                dto, inputs.fertilityExtract(), inputs.physicalAnalysis(), inputs.cropFertilizationTable(), warnings);
        GypsumRequirementResult gypsumRequirement = gypsumCalculationService.calculate(
                inputs.fertilityExtract(), inputs.physicalAnalysis(), inputs.cropFertilizationTable(), inputs.soilInterpretationTable(), user, sourceOption, warnings);
        List<SoilChemicalDiagnosisItem> chemicalDiagnosis = buildSoilChemicalDiagnosis(
                inputs.fertilityExtract(), inputs.physicalAnalysis(), inputs.soilInterpretationTable(), warnings);
        List<CorrectiveFertilizationRow> correctiveFertilizationRows = buildCorrectiveFertilizationRows(
                chemicalDiagnosis, inputs.physicalAnalysis(), inputs.cropFertilizationTable(), inputs.soilInterpretationTable(), user, sourceOption, warnings);
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
        return nutrientFertilizationCalculationService.calculate(
                inputs.cropFertilizationTable(), inputs.crop(), inputs.fertilityExtract(), inputs.soilInterpretationTable(),
                user, sourceOption, warnings, chemicalDiagnosis, foliarDiagnosis);
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
                .physicalAnalysisId(inputs.physicalAnalysis() != null ? inputs.physicalAnalysis().getId() : null)
                .soilFertilityAnalysisId(inputs.soilFertilityAnalysis().getId())
                .saturationExtractAnalysisId(inputs.saturationExtractAnalysis() != null ? inputs.saturationExtractAnalysis().getId() : null)
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

    private String addMissing(List<String> warnings,String m){warnings.add(m);return m;}
    private double nvl(Double v){return v==null?0d:v;}
    private double round2(double v){return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP).doubleValue();}
    private Optional<FertilityAnalysisExtractModel> findLatestFertilityExtract(SoilAnalysisModel soil){return fertilityAnalysisExtractRepository.findAll().stream().filter(e->(e.getRangeExtract()!=null&&e.getRangeExtract().getAnalysis()!=null&&Objects.equals(e.getRangeExtract().getAnalysis().getId(),soil.getId()))||(e.getLayerExtract()!=null&&e.getLayerExtract().getAnalysis()!=null&&Objects.equals(e.getLayerExtract().getAnalysis().getId(),soil.getId()))).max(Comparator.comparing(FertilityAnalysisExtractModel::getId));}
    private SoilAnalysisModel resolveSoilAnalysis(FertilityAnalysisExtractModel extract) {if (extract.getRangeExtract() != null && extract.getRangeExtract().getAnalysis() != null) return extract.getRangeExtract().getAnalysis(); if (extract.getLayerExtract() != null && extract.getLayerExtract().getAnalysis() != null) return extract.getLayerExtract().getAnalysis(); throw new IllegalArgumentException("Extrato de análise de fertilidade não possui análise de solo associada.");}
    private Optional<Double> extractPhValue(Optional<FertilityAnalysisExtractModel> e,Optional<SaturationExtractAnalysisExtractModel>s){if(e.isPresent()){if(e.get().getPhAgua()!=null)return Optional.of(e.get().getPhAgua());if(e.get().getPhCacl2()!=null)return Optional.of(e.get().getPhCacl2());} return s.map(SaturationExtractAnalysisExtractModel::getPh);}
    private Optional<Double> extractAluminumValue(Optional<FertilityAnalysisExtractModel> e){return e.map(FertilityAnalysisExtractModel::getAluminio);}
    private List<String> buildCorrectionMessages(RecommendationCreateRequestDto dto, Optional<FertilityAnalysisExtractModel> fertilityExtract, Optional<SaturationExtractAnalysisExtractModel> saturation, List<String> warnings){List<String> m=new ArrayList<>();Optional<Double> ph=extractPhValue(fertilityExtract,saturation);Optional<Double> al=extractAluminumValue(fertilityExtract); if(ph.isPresent()){double v=ph.get(); if(v<5.5)m.add("pH abaixo de 5.5. Indica necessidade provável de correção de acidez, a confirmar com critério de calagem selecionado."); else if(v<=6.5)m.add("pH em faixa intermediária. Correção deve ser avaliada conforme cultura e saturação por bases."); else m.add("pH elevado. Evitar recomendações automáticas de calagem sem validação técnica.");} if(al.isPresent()&&al.get()>0)m.add("Presença de alumínio trocável detectada. Avaliar neutralização conforme critério selecionado."); if(ph.isEmpty()&&al.isEmpty())warnings.add("Não foi possível calcular correção de acidez/salinidade por ausência de parâmetros suficientes.");return m;}

    private boolean isInterpretation(SoilChemicalDiagnosisItem item, String... expected) {
        if (item == null || item.getInterpretation() == null) return false;
        return Arrays.asList(expected).contains(item.getInterpretation());
    }

    private List<CorrectiveFertilizationRow> buildCorrectiveFertilizationRows(List<SoilChemicalDiagnosisItem> chemicalDiagnosis,
                                                                              PhysicalAnalysisExtractModel physicalAnalysis,
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
                "P2O5", physicalAnalysis, cropFertilizationTable, soilInterpretationTable, user, sourceOption, warnings);
        addCorrectiveRowIfRelevant(rows, "Potássio corretivo", findFirstDiagnosis(byAttribute, "potassio"),
                "K2O", physicalAnalysis, cropFertilizationTable, soilInterpretationTable, user, sourceOption, warnings);
        addCorrectiveRowIfRelevant(rows, "Enxofre corretivo", findFirstDiagnosis(byAttribute, "enxofre"),
                "S", physicalAnalysis, cropFertilizationTable, soilInterpretationTable, user, sourceOption, warnings);

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
                        + "; seção usada: " + (less400 ? "Argila < 400 g/dm³" : "Argila > 400 g/dm³")
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

    private String formatDoseRange(Double minimum, Double maximum) {
        if (minimum == null && maximum == null) return "Não calculada";
        if (minimum != null && maximum != null) return formatNumber(minimum) + " a " + formatNumber(maximum);
        return formatNumber(minimum != null ? minimum : maximum);
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

        boolean hasTextureFractions = physicalAnalysis.getTeorAreia() != null
                || physicalAnalysis.getTeorSilte() != null
                || physicalAnalysis.getTeorArgila() != null;
        String summary = textureClassification.classified()
                ? "Análise física considerada com classificação granulométrica " + textureClassification.texturalClass() + "."
                : hasTextureFractions
                ? "Análise física considerada com frações granulométricas; classificação granulométrica não calculada por dados ou unidade insuficientes."
                : "Análise física considerada com atributos físicos disponíveis; frações granulométricas insuficientes para descrever textura.";
        return new PhysicalDiagnosis(summary, diagnosis);
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
            double clayGdm3 = clay;
            AvailablePMehlich1ExtractorModel p = criterion.get();
            RangeCriterion range = clayGdm3 < 150
                    ? new RangeCriterion(p.getP_content_sandy_too_low(), p.getP_content_sandy_low_i(), p.getP_content_sandy_low_f(), p.getP_content_sandy_medium_i(), p.getP_content_sandy_medium_f(), p.getP_content_sandy_hight_i(), p.getP_content_sandy_hight_f(), p.getP_content_sandy_too_hight())
                    : clayGdm3 <= 350
                    ? new RangeCriterion(p.getP_content_sandy_clayey_too_low(), p.getP_content_sandy_clayey_low_i(), p.getP_content_sandy_clayey_low_f(), p.getP_content_sandy_clayey_medium_i(), p.getP_content_sandy_clayey_medium_f(), p.getP_content_sandy_clayey_hight_i(), p.getP_content_sandy_clayey_hight_f(), p.getP_content_sandy_clayey_too_hight())
                    : clayGdm3 <= 600
                    ? new RangeCriterion(p.getP_content_clayey_too_low(), p.getP_content_clayey_low_i(), p.getP_content_clayey_low_f(), p.getP_content_clayey_medium_i(), p.getP_content_clayey_medium_f(), p.getP_content_clayey_hight_i(), p.getP_content_clayey_hight_f(), p.getP_content_clayey_too_hight())
                    : new RangeCriterion(p.getP_content_very_clayey_too_low(), p.getP_content_very_clayey_low_i(), p.getP_content_very_clayey_low_f(), p.getP_content_very_clayey_medium_i(), p.getP_content_very_clayey_medium_f(), p.getP_content_very_clayey_hight_i(), p.getP_content_very_clayey_hight_f(), p.getP_content_very_clayey_too_hight());
            return classifyRange("Fósforo (P) Mehlich-1", fertility.getFosforoMehlich1(), "mg/dm³", range,
                    "Critério de fósforo (P) disponível por Mehlich-1 selecionado pelo teor de argila da análise física.");
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

    private SoilChemicalDiagnosisItem classifyPotassium(FertilityAnalysisExtractModel fertility,
                                                       Optional<KExchangeableContentModel> kRange,
                                                       Optional<DiverseContentRangeModel> diverseRange,
                                                       List<String> warnings) {
        if (fertility.getPotassio() == null) {
            return missingValue("Potássio (K) trocável", "Não há valor de potássio trocável no extrato de fertilidade.");
        }
        if (kRange.isPresent()) {
            KExchangeableContentModel k = kRange.get();
            return classifyRange("Potássio (K) trocável", fertility.getPotassio(), normalizeUnit(k.getUnit(), fertilityUnit(fertility.getUnidadePotassio())),
                    new RangeCriterion(k.getKContentTooLow(), k.getKContentLowI(), k.getKContentLowF(), k.getKContentMediumI(), k.getKContentMediumF(), k.getKContentHighI(), k.getKContentHighF(), k.getKContentTooHigh()),
                    "Potássio (K) trocável classificado em mmolc/dm³ pelo critério específico de K da tabela selecionada.");
        }
        warnings.add("Não foi encontrada linha específica de potássio; foi tentada a faixa diversa de potássio.");
        return classifyDiverseRange("Potássio (K) trocável", fertility.getPotassio(), fertilityUnit(fertility.getUnidadePotassio()), diverseRange,
                r -> new RangeCriterion(r.getPotassium_too_low(), r.getPotassium_low_i(), r.getPotassium_low_f(), r.getPotassium_medium_i(), r.getPotassium_medium_f(), r.getPotassium_hight_i(), r.getPotassium_hight_f(), r.getPotassium_too_hight()),
                "Potássio (K) trocável classificado em mmolc/dm³ pelas faixas diversas da tabela selecionada.");
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
            case g_per_dm3 -> "g/dm³";
            case mg_per_dm3 -> "mg/dm³";
            case cmolc_per_dm3 -> "mmolc/dm³";
            case mmolc_per_dm3 -> "mmolc/dm³";
            case percentage -> "%";
        };
    }

    private String physicalUnit(PhysicalAnalysisUnit unit) {
        PhysicalAnalysisUnit normalized = unit != null ? unit.canonicalForPhysicalExtract() : PhysicalAnalysisUnit.G_PER_DM3;
        return normalizeUnit(normalized.getSymbol(), "g/dm³");
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
    private PhysicalAnalysisExtractModel findPhysicalAnalysisExtractByIdOrNull(Long id) {return id == null ? null : findPhysicalAnalysisExtractByIdOrThrow(id);}
    private PhysicalAnalysisExtractModel findPhysicalAnalysisExtractByIdOrThrow(Long id) {return physicalAnalysisExtractRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Extrato de análise física não encontrado com o ID: " + id));}
    private FertilityAnalysisSelection findSoilFertilitySelectionByIdOrThrow(Long id, PlotModel requestPlot) {Optional<FertilityAnalysisExtractModel> extract = fertilityAnalysisExtractRepository.findById(id); if (extract.isPresent()) {SoilAnalysisModel soil = resolveSoilAnalysis(extract.get()); if (soil.getPlot() != null && requestPlot != null && Objects.equals(soil.getPlot().getId(), requestPlot.getId())) return new FertilityAnalysisSelection(soil, extract);} Optional<SoilAnalysisModel> soil = soilAnalysisRepository.findById(id); if (soil.isPresent()) return new FertilityAnalysisSelection(soil.get(), Optional.empty()); return extract.map(e -> new FertilityAnalysisSelection(resolveSoilAnalysis(e), Optional.of(e))).orElseThrow(() -> new EntityNotFoundException("Análise de fertilidade do solo não encontrada com o ID: " + id));}
    private SaturationExtractAnalysisExtractModel findSaturationExtractAnalysisExtractByIdOrNull(Long id) {return id == null ? null : findSaturationExtractAnalysisExtractByIdOrThrow(id);}
    private SaturationExtractAnalysisExtractModel findSaturationExtractAnalysisExtractByIdOrThrow(Long id) {return saturationExtractAnalysisExtractRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Extrato de análise de saturação não encontrado com o ID: " + id));}
    private AnnualCropFolderModel findAnnualCropFolderByIdOrThrow(Long id) {return annualCropFolderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Pasta de cultura anual não encontrada com o ID: " + id));}
    private CropModel findCropByIdOrThrow(Long id) {return cropRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Cultura não encontrada com o ID: " + id));}
    private CropFertilizationTableModel findCropFertilizationTableBySelectionOrThrow(Long id, TechnicalTableGroup group, UserModel user) {return findCropFertilizationTableBySelection(id, group, user).orElseThrow(() -> new EntityNotFoundException("Tabela de adubação de culturas não encontrada para o grupo " + group + " e ID: " + id));}
    private SoilFertilityInterpretationCriteriaTableModel findSoilFertilityInterpretationTableBySelectionOrThrow(Long id, TechnicalTableGroup group, UserModel user) {return findSoilFertilityInterpretationTableBySelection(id, group, user).orElseThrow(() -> new EntityNotFoundException("Tabela de interpretação da fertilidade do solo não encontrada para o grupo " + group + " e ID: " + id));}
    private CropFoliarAnalysisInterpretationTableModel findCropFoliarAnalysisInterpretationTableBySelectionOrThrow(Long id, TechnicalTableGroup group, UserModel user) {return findCropFoliarAnalysisInterpretationTableBySelection(id, group, user).orElseThrow(() -> new EntityNotFoundException("Tabela de interpretação de análise foliar não encontrada para o grupo " + group + " e ID: " + id));}
    private Optional<CropFertilizationTableModel> findCropFertilizationTableBySelection(Long id, TechnicalTableGroup group, UserModel user) {validateTableSelection(id, group, "tabela de adubação de culturas"); return switch (group) {case MINHAS, PRIVADAS -> cropFertilizationTableRepository.findByIdAndCreatorAndCreator_CargoNot(id, user, Cargo.USUARIO_SUPREMO); case PUBLICAS -> cropFertilizationTableRepository.findByIdAndPublicTableTrueAndCreator_CargoNot(id, Cargo.USUARIO_SUPREMO); case PADRAO -> cropFertilizationTableRepository.findByIdAndCreator_CargoAndPublicTableTrue(id, Cargo.USUARIO_SUPREMO);};}
    private Optional<SoilFertilityInterpretationCriteriaTableModel> findSoilFertilityInterpretationTableBySelection(Long id, TechnicalTableGroup group, UserModel user) {validateTableSelection(id, group, "tabela de interpretação da fertilidade do solo"); return switch (group) {case MINHAS, PRIVADAS -> soilFertilityInterpretationCriteriaTableRepository.findByIdAndCreatorAndCreator_CargoNot(id, user, Cargo.USUARIO_SUPREMO); case PUBLICAS -> soilFertilityInterpretationCriteriaTableRepository.findByIdAndPublicTableTrueAndCreator_CargoNot(id, Cargo.USUARIO_SUPREMO); case PADRAO -> soilFertilityInterpretationCriteriaTableRepository.findByIdAndCreator_Cargo(id, Cargo.USUARIO_SUPREMO);};}
    private Optional<CropFoliarAnalysisInterpretationTableModel> findCropFoliarAnalysisInterpretationTableBySelection(Long id, TechnicalTableGroup group, UserModel user) {validateTableSelection(id, group, "tabela de interpretação de análise foliar"); return switch (group) {case MINHAS, PRIVADAS -> cropFoliarAnalysisInterpretationTableRepository.findByIdAndCreatorAndCreator_CargoNot(id, user, Cargo.USUARIO_SUPREMO); case PUBLICAS -> cropFoliarAnalysisInterpretationTableRepository.findByIdAndPublicTableTrueAndCreator_CargoNot(id, Cargo.USUARIO_SUPREMO); case PADRAO -> cropFoliarAnalysisInterpretationTableRepository.findByIdAndCreator_Cargo(id, Cargo.USUARIO_SUPREMO);};}
    private void validateTableSelection(Long id, TechnicalTableGroup group, String tableName) {if (id == null) throw new IllegalArgumentException("ID da " + tableName + " é obrigatório."); if (group == null) throw new IllegalArgumentException("Grupo da " + tableName + " é obrigatório.");}
    private void validateSamePlot(PlotModel selectedPlot, PlotModel requestPlot, String message) {if (selectedPlot == null || requestPlot == null || !Objects.equals(selectedPlot.getId(), requestPlot.getId())) throw new IllegalArgumentException(message);}
    private PlotModel resolvePlot(PhysicalAnalysisExtractModel model) {if (model.getRangeExtract() != null && model.getRangeExtract().getAnalysis() != null) return model.getRangeExtract().getAnalysis().getPlot(); if (model.getLayerExtract() != null && model.getLayerExtract().getAnalysis() != null) return model.getLayerExtract().getAnalysis().getPlot(); throw new IllegalArgumentException("Extrato de análise física não possui análise de solo associada.");}
    private PlotModel resolvePlot(SaturationExtractAnalysisExtractModel model) {if (model.getRangeExtract() != null && model.getRangeExtract().getAnalysis() != null) return model.getRangeExtract().getAnalysis().getPlot(); if (model.getLayerExtract() != null && model.getLayerExtract().getAnalysis() != null) return model.getLayerExtract().getAnalysis().getPlot(); throw new IllegalArgumentException("Extrato de análise de saturação não possui análise de solo associada.");}
    private Optional<FoliarAnalysisModel> findLatestFoliarAnalysis(CropModel crop) {return foliarAnalysisRepository.findTopByCropOrderByIdDesc(crop);}

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
