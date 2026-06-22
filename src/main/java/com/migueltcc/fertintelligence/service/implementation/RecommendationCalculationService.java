package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Nutriente;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.TechnicalTableGroup;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.recommendation.RecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.*;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.FoliarAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.FertilityAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.PhysicalAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.SaturationExtractAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.ContentRangeModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CoverageModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFertilizationTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.AvailablePAnionExchangeResinExtractorModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.AvailablePMehlich1ExtractorModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.AvailableSModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.DiverseContentRangeModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.KExchangeableContentModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.FormulatedMineralFertilizerModel;
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
    private final DiverseContentRangeRepository diverseContentRangeRepository;
    private final KExchangeableContentRepository kExchangeableContentRepository;
    private final AvailablePMehlich1ExtractorRepository availablePMehlich1ExtractorRepository;
    private final AvailablePAnionExchangeResinExtractorRepository availablePAnionExchangeResinExtractorRepository;
    private final AvailableSRepository availableSRepository;

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
                                            DiverseContentRangeRepository diverseContentRangeRepository,
                                            KExchangeableContentRepository kExchangeableContentRepository,
                                            AvailablePMehlich1ExtractorRepository availablePMehlich1ExtractorRepository,
                                            AvailablePAnionExchangeResinExtractorRepository availablePAnionExchangeResinExtractorRepository,
                                            AvailableSRepository availableSRepository) {
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
        this.diverseContentRangeRepository = diverseContentRangeRepository;
        this.kExchangeableContentRepository = kExchangeableContentRepository;
        this.availablePMehlich1ExtractorRepository = availablePMehlich1ExtractorRepository;
        this.availablePAnionExchangeResinExtractorRepository = availablePAnionExchangeResinExtractorRepository;
        this.availableSRepository = availableSRepository;
    }

    public RecommendationCalculationResult calculate(RecommendationCreateRequestDto dto, UserModel user, PropertyModel property, PlotModel plot) {
        List<String> diagnostics = new ArrayList<>();
        FertilizerSourceOption sourceOption = dto.getOrigemAdubos() != null ? dto.getOrigemAdubos() : FertilizerSourceOption.BOTH;
        List<String> warnings = new ArrayList<>();

        PhysicalAnalysisExtractModel physicalAnalysis = findPhysicalAnalysisExtractByIdOrThrow(dto.getPhysicalAnalysisExtractId());
        SoilAnalysisModel soilFertilityAnalysis = findSoilFertilityAnalysisByIdOrThrow(dto.getSoilFertilityAnalysisId());
        SaturationExtractAnalysisExtractModel saturationExtractAnalysis = findSaturationExtractAnalysisExtractByIdOrThrow(dto.getSaturationExtractAnalysisExtractId());
        AnnualCropFolderModel annualCropFolder = findAnnualCropFolderByIdOrThrow(dto.getAnnualCropFolderId());
        CropModel crop = findCropByIdOrThrow(dto.getCropId());
        CropFertilizationTableModel cropFertilizationTable = findCropFertilizationTableBySelectionOrThrow(
                dto.getCropFertilizationTableId(), dto.getCropFertilizationTableGroup(), user);
        SoilFertilityInterpretationCriteriaTableModel soilInterpretationTable = findSoilFertilityInterpretationTableBySelectionOrThrow(
                dto.getSoilFertilityInterpretationCriteriaTableId(), dto.getSoilFertilityInterpretationCriteriaTableGroup(), user);
        CropFoliarAnalysisInterpretationTableModel foliarInterpretationTable = findCropFoliarAnalysisInterpretationTableBySelectionOrThrow(
                dto.getCropFoliarAnalysisInterpretationTableId(), dto.getCropFoliarAnalysisInterpretationTableGroup(), user);

        validateSamePlot(resolvePlot(physicalAnalysis), plot, "O extrato de análise física selecionado não pertence ao talhão informado.");
        validateSamePlot(soilFertilityAnalysis.getPlot(), plot, "A análise de fertilidade selecionada não pertence ao talhão informado.");
        validateSamePlot(resolvePlot(saturationExtractAnalysis), plot, "O extrato de análise de saturação selecionado não pertence ao talhão informado.");
        validateSamePlot(annualCropFolder.getPlot(), plot, "A pasta de cultura anual selecionada não pertence ao talhão informado.");
        if (crop.getFolder() == null || !Objects.equals(crop.getFolder().getId(), annualCropFolder.getId())) {
            throw new IllegalArgumentException("A cultura selecionada não pertence à pasta de cultura anual informada.");
        }

        Optional<FoliarAnalysisModel> foliarAnalysis = findLatestFoliarAnalysis(crop);

        Optional<FertilityAnalysisExtractModel> fertilityExtract = findLatestFertilityExtract(soilFertilityAnalysis);

        PhysicalDiagnosis physicalDiagnosis = buildSoilPhysicalDiagnosis(physicalAnalysis, warnings);
        String physicalSummary = physicalDiagnosis.summary();
        String soilFertilitySummary = "Análise de fertilidade considerada na recomendação.";
        String saturationSummary = "Extrato de saturação considerado na recomendação.";
        String cropSummary = "Cultura considerada conforme cabeçalho do laudo.";
        String foliarSummary = foliarAnalysis.map(m -> "Análise foliar considerada quando aplicável.").orElseGet(() -> addMissing(warnings, "Nenhuma análise foliar foi encontrada para a cultura selecionada."));

        List<String> correctionMessages = buildCorrectionMessages(dto, fertilityExtract, Optional.of(saturationExtractAnalysis), warnings);
        List<SoilChemicalDiagnosisItem> chemicalDiagnosis = buildSoilChemicalDiagnosis(
                fertilityExtract, physicalAnalysis, soilInterpretationTable, warnings);
        List<FertilizationRecommendationRow> recommendationRows = new ArrayList<>();
        List<FertilizerSuggestion> fertilizerSuggestions = new ArrayList<>();

        Double requiredN = null, requiredP2O5 = null, requiredK2O = null;
        Long nRangeId = null, pRangeId = null, kRangeId = null;

        {
            CropFertilizationTableModel table = cropFertilizationTable;
            Optional<ContentRangeModel> nRange = selectNitrogenRange(table);
            Optional<ContentRangeModel> pRange = selectNutrientRange(table, Nutriente.FOSFORO, extractPhosphorusValue(fertilityExtract), warnings, "fósforo");
            Optional<ContentRangeModel> kRange = selectNutrientRange(table, Nutriente.POTASSIO, extractPotassiumValue(fertilityExtract), warnings, "potássio");

            requiredN = nRange.map(ContentRangeModel::getApplication).orElse(null);
            requiredP2O5 = pRange.map(ContentRangeModel::getApplication).orElse(null);
            requiredK2O = kRange.map(ContentRangeModel::getApplication).orElse(null);
            nRangeId = nRange.map(ContentRangeModel::getId).orElse(null);
            pRangeId = pRange.map(ContentRangeModel::getId).orElse(null);
            kRangeId = kRange.map(ContentRangeModel::getId).orElse(null);

            if (nRange.isEmpty()) warnings.add("Não foi encontrado intervalo para NITROGENIO na tabela selecionada.");
            if (pRange.isEmpty()) warnings.add("Não foi encontrado intervalo para FOSFORO na tabela selecionada.");
            if (kRange.isEmpty()) warnings.add("Não foi encontrado intervalo para POTASSIO na tabela selecionada.");

            FertilizerSelection planting = selectBestPlantingFertilizer(user, sourceOption, requiredN, requiredP2O5, requiredK2O, warnings);
            planting.suggestion().ifPresent(fertilizerSuggestions::add);

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
                    .warning(planting.warning())
                    .applicationMode("Aplicação no plantio, conforme recomendação técnica.")
                    .source("Tabela de adubação de culturas ID " + table.getId())
                    .build());

            for (ContentRangeModel selectedRange : List.of(nRange.orElse(null), pRange.orElse(null), kRange.orElse(null))) {
                if (selectedRange != null) recommendationRows.addAll(buildCoverageRows(selectedRange, user, sourceOption, fertilizerSuggestions));
            }
        }

        
        warnings.add("Valide os parâmetros com engenheiro agrônomo responsável antes de uso operacional.");

        return RecommendationCalculationResult.builder()
                .requesterName(user != null ? user.getName() : null)
                .requesterUsername(user != null ? user.getUsername() : null)
                .propertyName(property != null ? property.getNome() : null)
                .propertyId(property != null ? property.getId() : null)
                .plotIdentification(plot != null ? plot.getIdentification() : null)
                .plotId(plot != null ? plot.getId() : null)
                .cropName(crop.getName() != null ? crop.getName().name() : null)
                .annualCropFolderYear(annualCropFolder.getCropsYear())
                .recommendationType(dto.getRecommendationType() != null ? dto.getRecommendationType().name() : null)
                .limingCriteria(dto.getLimingCriteria() != null ? dto.getLimingCriteria().name() : null)
                .issuedAt(LocalDateTime.now())
                .warnings(warnings).diagnosticMessages(diagnostics).correctionMessages(correctionMessages)
                .soilChemicalDiagnosis(chemicalDiagnosis)
                .soilPhysicalDiagnosis(physicalDiagnosis.items())
                .fertilizationRows(List.of("Recomendação estruturada em linhas de plantio e cobertura."))
                .fertilizationRecommendationRows(recommendationRows).fertilizerSuggestions(fertilizerSuggestions)
                .requiredN(requiredN).requiredP2O5(requiredP2O5).requiredK2O(requiredK2O)
                .nitrogenRangeId(nRangeId).phosphorusRangeId(pRangeId).potassiumRangeId(kRangeId)
                .physicalAnalysisId(physicalAnalysis.getId())
                .soilFertilityAnalysisId(soilFertilityAnalysis.getId())
                .saturationExtractAnalysisId(saturationExtractAnalysis.getId())
                .annualCropFolderId(annualCropFolder.getId())
                .cropId(crop.getId()).foliarAnalysisId(foliarAnalysis.map(FoliarAnalysisModel::getId).orElse(null))
                .physicalAnalysisSummary(physicalSummary).soilFertilityAnalysisSummary(soilFertilitySummary).saturationExtractAnalysisSummary(saturationSummary)
                .annualCropFolderSummary("Pasta de cultura anual considerada na recomendação.")
                .cropSummary(cropSummary).foliarAnalysisSummary(foliarSummary).build();
    }
    private String addMissing(List<String> warnings,String m){warnings.add(m);return m;}
    private double nvl(Double v){return v==null?0d:v;}
    private Optional<ContentRangeModel> selectNitrogenRange(CropFertilizationTableModel t){var l=contentRangeRepository.findAllByTableAndNutrientOrderByOrderAsc(t,Nutriente.NITROGENIO);return l.stream().findFirst();}
    private Optional<ContentRangeModel> selectNutrientRange(CropFertilizationTableModel t,Nutriente n,Optional<Double> value,List<String>w,String label){var ranges=contentRangeRepository.findAllByTableAndNutrientOrderByOrderAsc(t,n);if(ranges.isEmpty())return Optional.empty();if(value.isEmpty()){w.add("Não foi possível classificar teor de "+label+"; primeiro intervalo da tabela foi utilizado.");return Optional.of(ranges.get(0));} double v=value.get(); return ranges.stream().filter(r->(r.getSmallest()==null||v>=r.getSmallest())&&(r.getLargest()==null||v<r.getLargest())).findFirst().or(()->Optional.of(ranges.get(0)));}
    private Optional<FertilityAnalysisExtractModel> findLatestFertilityExtract(SoilAnalysisModel soil){return fertilityAnalysisExtractRepository.findAll().stream().filter(e->(e.getRangeExtract()!=null&&e.getRangeExtract().getAnalysis()!=null&&Objects.equals(e.getRangeExtract().getAnalysis().getId(),soil.getId()))||(e.getLayerExtract()!=null&&e.getLayerExtract().getAnalysis()!=null&&Objects.equals(e.getLayerExtract().getAnalysis().getId(),soil.getId()))).max(Comparator.comparing(FertilityAnalysisExtractModel::getId));}
    private Optional<Double> extractPhosphorusValue(Optional<FertilityAnalysisExtractModel> e){return e.map(x->x.getFosforoMehlich1()!=null?x.getFosforoMehlich1():x.getFosforoResina());}
    private Optional<Double> extractPotassiumValue(Optional<FertilityAnalysisExtractModel> e){return e.map(FertilityAnalysisExtractModel::getPotassio);}
    private Optional<Double> extractPhValue(Optional<FertilityAnalysisExtractModel> e,Optional<SaturationExtractAnalysisExtractModel>s){if(e.isPresent()){if(e.get().getPhAgua()!=null)return Optional.of(e.get().getPhAgua());if(e.get().getPhCacl2()!=null)return Optional.of(e.get().getPhCacl2());} return s.map(SaturationExtractAnalysisExtractModel::getPh);}
    private Optional<Double> extractAluminumValue(Optional<FertilityAnalysisExtractModel> e){return e.map(FertilityAnalysisExtractModel::getAluminio);}
    private List<String> buildCorrectionMessages(RecommendationCreateRequestDto dto, Optional<FertilityAnalysisExtractModel> fertilityExtract, Optional<SaturationExtractAnalysisExtractModel> saturation, List<String> warnings){List<String> m=new ArrayList<>();Optional<Double> ph=extractPhValue(fertilityExtract,saturation);Optional<Double> al=extractAluminumValue(fertilityExtract); if(ph.isPresent()){double v=ph.get(); if(v<5.5)m.add("pH abaixo de 5.5. Indica necessidade provável de correção de acidez, a confirmar com critério de calagem selecionado."); else if(v<=6.5)m.add("pH em faixa intermediária. Correção deve ser avaliada conforme cultura e saturação por bases."); else m.add("pH elevado. Evitar recomendações automáticas de calagem sem validação técnica.");} if(al.isPresent()&&al.get()>0)m.add("Presença de alumínio trocável detectada. Avaliar neutralização conforme critério selecionado."); if(ph.isEmpty()&&al.isEmpty())warnings.add("Não foi possível calcular correção de acidez/salinidade por ausência de parâmetros suficientes.");return m;}
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
        diagnosis.add(classifyDiverseRange("Cálcio", fertility.getCalcio(), "mmolc/dm3", diverseRange,
                r -> new RangeCriterion(r.getCalcium_too_low(), r.getCalcium_low_i(), r.getCalcium_low_f(), r.getCalcium_medium_i(), r.getCalcium_medium_f(), r.getCalcium_hight_i(), r.getCalcium_hight_f(), r.getCalcium_too_hight()),
                "Cálcio trocável classificado pelas faixas diversas da tabela selecionada."));
        diagnosis.add(classifyDiverseRange("Magnésio", fertility.getMagnesio(), "mmolc/dm3", diverseRange,
                r -> new RangeCriterion(r.getMagnesium_too_low(), r.getMagnesium_low_i(), r.getMagnesium_low_f(), r.getMagnesium_medium_i(), r.getMagnesium_medium_f(), r.getMagnesium_hight_i(), r.getMagnesium_hight_f(), r.getMagnesium_too_hight()),
                "Magnésio trocável classificado pelas faixas diversas da tabela selecionada."));
        diagnosis.add(classifyDiverseRange("Alumínio", fertility.getAluminio(), "mmolc/dm3", diverseRange,
                r -> new RangeCriterion(r.getAluminum_too_low(), r.getAluminum_low_i(), r.getAluminum_low_f(), r.getAluminum_medium_i(), r.getAluminum_medium_f(), r.getAluminum_hight_i(), r.getAluminum_hight_f(), r.getAluminum_too_hight()),
                "Alumínio trocável classificado pelas faixas diversas da tabela selecionada."));
        if (fertility.getEnxofre() != null) {
            diagnosis.add(classifySulfur(fertility, physicalAnalysis, table, warnings));
        }
        addDiverseDiagnosisIfPresent(diagnosis, "Matéria orgânica", fertility.getMateriaOrganica(), diverseRange.map(DiverseContentRangeModel::getOrganic_matter_unit).orElse("g/dm3"), diverseRange,
                r -> new RangeCriterion(r.getOrganic_matter_too_low(), r.getOrganic_matter_low_i(), r.getOrganic_matter_low_f(), r.getOrganic_matter_medium_i(), r.getOrganic_matter_medium_f(), r.getOrganic_matter_hight_i(), r.getOrganic_matter_hight_f(), r.getOrganic_matter_too_hight()),
                "Matéria orgânica classificada pelas faixas diversas da tabela selecionada.");
        addDiverseDiagnosisIfPresent(diagnosis, "H+Al", fertility.getAluminioMaisHidrogenio(), "mmolc/dm3", diverseRange,
                r -> new RangeCriterion(r.getPotential_acidity_too_low(), r.getPotential_acidity_low_i(), r.getPotential_acidity_low_f(), r.getPotential_acidity_medium_i(), r.getPotential_acidity_medium_f(), r.getPotential_acidity_hight_i(), r.getPotential_acidity_hight_f(), r.getPotential_acidity_too_hight()),
                "Acidez potencial classificada pelas faixas diversas da tabela selecionada.");
        addDiverseDiagnosisIfPresent(diagnosis, "Soma de bases", fertility.getSomaBases(), "mmolc/dm3", diverseRange,
                r -> new RangeCriterion(r.getSum_of_bases_too_low(), r.getSum_of_bases_low_i(), r.getSum_of_bases_low_f(), r.getSum_of_bases_medium_i(), r.getSum_of_bases_medium_f(), r.getSum_of_bases_hight_i(), r.getSum_of_bases_hight_f(), r.getSum_of_bases_too_hight()),
                "Soma de bases classificada pelas faixas diversas da tabela selecionada.");
        addDiverseDiagnosisIfPresent(diagnosis, "CTC efetiva", fertility.getCtcEfetiva(), "mmolc/dm3", diverseRange,
                r -> new RangeCriterion(r.getEffective_cec_too_low(), r.getEffective_cec_low_i(), r.getEffective_cec_low_f(), r.getEffective_cec_medium_i(), r.getEffective_cec_medium_f(), r.getEffective_cec_hight_i(), r.getEffective_cec_hight_f(), r.getEffective_cec_too_hight()),
                "CTC efetiva classificada a partir do valor pronto do extrato de fertilidade.");
        addDiverseDiagnosisIfPresent(diagnosis, "CTC pH 7,0", fertility.getCtcPh7(), "mmolc/dm3", diverseRange,
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
            return classifyRange("Potássio", fertility.getPotassio(), "mmolc/dm3",
                    new RangeCriterion(k.getKContentTooLow(), k.getKContentLowI(), k.getKContentLowF(), k.getKContentMediumI(), k.getKContentMediumF(), k.getKContentHighI(), k.getKContentHighF(), k.getKContentTooHigh()),
                    "Potássio classificado pelo critério específico de K da tabela selecionada.");
        }
        warnings.add("Não foi encontrada linha específica de potássio; foi tentada a faixa diversa de potássio.");
        return classifyDiverseRange("Potássio", fertility.getPotassio(), "mmolc/dm3", diverseRange,
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

    private String formatInterval(Double start, Double end, Double fallbackEndExclusive) {
        Double effectiveEnd = end != null ? end : fallbackEndExclusive;
        return formatNumber(start) + " a " + formatNumber(effectiveEnd);
    }

    private String formatNumber(Double value) {
        if (value == null) return "não informado";
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }

    private record PhysicalDiagnosis(String summary, List<SoilPhysicalDiagnosisItem> items) {}
    private record RangeCriterion(Double tooLowEnd, Double lowStart, Double lowEnd, Double mediumStart,
                                  Double mediumEnd, Double highStart, Double highEnd, Double tooHighStart) {}
    private record FertilizerSelection(String name, Double quantityKgHa, Double providedN, Double providedP2O5, Double providedK2O, Double balanceN, Double balanceP2O5, Double balanceK2O, String warning, Optional<FertilizerSuggestion> suggestion){}
    private FertilizerSelection selectBestPlantingFertilizer(UserModel user, FertilizerSourceOption sourceOption, Double n, Double p, Double k, List<String>w){var formulated=selectFormulatedFertilizers(user, sourceOption);var bestF=formulated.stream().filter(f->f.getN()>0||f.getP2O5()>0||f.getK2O()>0).max((a,b)->compareScore(a.getN(),a.getP2O5(),a.getK2O(),b.getN(),b.getP2O5(),b.getK2O(),n,p,k,a.getId(),b.getId())); if(bestF.isPresent()){var f=bestF.get();double q=estimate(n,p,k,f.getN(),f.getP2O5(),f.getK2O());w.add("Quantidade de adubo estimada a partir do maior fator necessário entre N, P2O5 e K2O para o fertilizante selecionado.");var s=FertilizerSuggestion.builder().fertilizerId(f.getId()).fertilizerType("FORMULADO").fertilizerName("NPK "+(int)f.getN()+"-"+(int)f.getP2O5()+"-"+(int)f.getK2O()).n(f.getN()).p2o5(f.getP2O5()).k2o(f.getK2O()).reason("Maior cobertura dos nutrientes de plantio.").build();return buildSelection(s.getFertilizerName(),q,n,p,k,f.getN(),f.getP2O5(),f.getK2O(),w,Optional.of(s));}
    var simples=selectSimpleFertilizers(user, sourceOption);var bestS=simples.stream().filter(f->f.getN()>0||f.getP2O5()>0||f.getK2O()>0).max((a,b)->compareScore(a.getN(),a.getP2O5(),a.getK2O(),b.getN(),b.getP2O5(),b.getK2O(),n,p,k,a.getId(),b.getId()));if(bestS.isPresent()){var f=bestS.get();double q=estimate(n,p,k,f.getN(),f.getP2O5(),f.getK2O());w.add("Quantidade de adubo estimada a partir do maior fator necessário entre N, P2O5 e K2O para o fertilizante selecionado.");var s=FertilizerSuggestion.builder().fertilizerId(f.getId()).fertilizerType("SIMPLES").fertilizerName(f.getName()).n(f.getN()).p2o5(f.getP2O5()).k2o(f.getK2O()).reason("Fallback por ausência de formulado adequado.").build();return buildSelection(s.getFertilizerName(),q,n,p,k,f.getN(),f.getP2O5(),f.getK2O(),w,Optional.of(s));}
    w.add("Nenhum adubo mineral adequado foi encontrado para a origem de adubos selecionada."); return new FertilizerSelection("Não encontrado",null,null,null,null,null,null,null,null,Optional.empty());}
    private FertilizerSelection buildSelection(String name, Double q, Double rn, Double rp, Double rk, double fn, double fp, double fk, List<String> warnings, Optional<FertilizerSuggestion> suggestion){double pn=q==null?0d:round2(q*fn/100d), pp=q==null?0d:round2(q*fp/100d), pk=q==null?0d:round2(q*fk/100d);double bn=round2(pn-nvl(rn)), bp=round2(pp-nvl(rp)), bk=round2(pk-nvl(rk));String warning=null;if(bn<0||bp<0||bk<0){warning=String.format("Fertilizante selecionado não atende todos os nutrientes no plantio. Déficits: N %.2f kg/ha, P2O5 %.2f kg/ha, K2O %.2f kg/ha.", Math.max(0d,-bn), Math.max(0d,-bp), Math.max(0d,-bk));warnings.add(warning);}return new FertilizerSelection(name,q,pn,pp,pk,bn,bp,bk,warning,suggestion);}
    private List<FertilizationRecommendationRow> buildCoverageRows(ContentRangeModel range, UserModel user, FertilizerSourceOption sourceOption, List<FertilizerSuggestion> suggestions){List<FertilizationRecommendationRow> rows=new ArrayList<>();for(CoverageModel c:coverageRepository.findAllByRangeOrderByOrderAsc(range)){if(nvl(c.getApplication())<=0d) continue;var simples=selectSimpleFertilizers(user, sourceOption);SimpleMineralFertilizerModel best=null; if(range.getNutrient()==Nutriente.NITROGENIO) best=simples.stream().max(Comparator.comparing(SimpleMineralFertilizerModel::getN)).orElse(null); else if(range.getNutrient()==Nutriente.POTASSIO) best=simples.stream().max(Comparator.comparing(SimpleMineralFertilizerModel::getK2O)).orElse(null); else best=simples.stream().max(Comparator.comparing(SimpleMineralFertilizerModel::getP2O5)).orElse(null); String fertName="Não encontrado"; Double q=null; if(best!=null){double pct=range.getNutrient()==Nutriente.NITROGENIO?best.getN():range.getNutrient()==Nutriente.POTASSIO?best.getK2O():best.getP2O5(); if(pct>0){q=round2(c.getApplication()/pct*100d);} fertName=best.getName(); suggestions.add(FertilizerSuggestion.builder().fertilizerId(best.getId()).fertilizerType("SIMPLES").fertilizerName(best.getName()).n(best.getN()).p2o5(best.getP2O5()).k2o(best.getK2O()).reason("Cobertura por " + range.getNutrient()).build()); }
rows.add(FertilizationRecommendationRow.builder().phase("Cobertura "+c.getOrder()+" - "+range.getNutrient()).nutrients(range.getNutrient()+": "+String.format("%.2f",nvl(c.getApplication()))+" kg/ha").suggestedFertilizer(fertName).fertilizerQuantityKgHa(q).applicationMode("Aplicação em cobertura, conforme fase da cultura e recomendação técnica.").source("Tabela de adubação da cultura").build());}
return rows;}
    private int compareScore(double an,double ap,double ak,double bn,double bp,double bk,Double rn,Double rp,Double rk,Long aid,Long bid){int as=(nvl(rn)>0&&an>0?1:0)+(nvl(rp)>0&&ap>0?1:0)+(nvl(rk)>0&&ak>0?1:0);int bs=(nvl(rn)>0&&bn>0?1:0)+(nvl(rp)>0&&bp>0?1:0)+(nvl(rk)>0&&bk>0?1:0); if(as!=bs)return Integer.compare(as,bs); if(nvl(rp)>0&&Double.compare(ap,bp)!=0)return Double.compare(ap,bp); return Long.compare(bid,aid);}
    private double estimate(Double rn,Double rp,Double rk,double n,double p,double k){double q=0d;if(nvl(rn)>0&&n>0)q=Math.max(q,rn/n*100d);if(nvl(rp)>0&&p>0)q=Math.max(q,rp/p*100d);if(nvl(rk)>0&&k>0)q=Math.max(q,rk/k*100d);return round2(q);}
    private double round2(double v){return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP).doubleValue();}
    private <T> List<T> dedup(List<T> a,List<T> b, Function<T,Long> id){Map<Long,T> m=new LinkedHashMap<>();a.forEach(x->m.putIfAbsent(id.apply(x),x));b.forEach(x->m.putIfAbsent(id.apply(x),x));return new ArrayList<>(m.values());}

    private PhysicalAnalysisExtractModel findPhysicalAnalysisExtractByIdOrThrow(Long id) {return physicalAnalysisExtractRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Extrato de análise física não encontrado com o ID: " + id));}
    private SoilAnalysisModel findSoilFertilityAnalysisByIdOrThrow(Long id) {return soilAnalysisRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Análise de fertilidade do solo não encontrada com o ID: " + id));}
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
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class RecommendationCalculationResult { private String requesterName; private String requesterUsername; private String propertyName; private Long propertyId; private String plotIdentification; private Long plotId; private String cropName; private Integer annualCropFolderYear; private String recommendationType; private String limingCriteria; private LocalDateTime issuedAt; private List<String> warnings; private List<String> diagnosticMessages; private List<String> fertilizationRows; private List<String> correctionMessages; private List<SoilChemicalDiagnosisItem> soilChemicalDiagnosis; private List<SoilPhysicalDiagnosisItem> soilPhysicalDiagnosis; private Long physicalAnalysisId; private Long soilFertilityAnalysisId; private Long saturationExtractAnalysisId; private Long annualCropFolderId; private Long cropId; private Long foliarAnalysisId; private String physicalAnalysisSummary; private String soilFertilityAnalysisSummary; private String saturationExtractAnalysisSummary; private String annualCropFolderSummary; private String cropSummary; private String foliarAnalysisSummary; private Double requiredN; private Double requiredP2O5; private Double requiredK2O; private Long nitrogenRangeId; private Long phosphorusRangeId; private Long potassiumRangeId; private List<FertilizationRecommendationRow> fertilizationRecommendationRows; private List<FertilizerSuggestion> fertilizerSuggestions; }
    @Data @Builder @NoArgsConstructor @AllArgsConstructor public static class SoilChemicalDiagnosisItem { private String attribute; private Double analyzedValue; private String unit; private String interpretation; private String usedCriterion; private String technicalObservation; }
    @Data @Builder @NoArgsConstructor @AllArgsConstructor public static class SoilPhysicalDiagnosisItem { private String attribute; private Double analyzedValue; private String unit; private String technicalObservation; }
    @Data @Builder @NoArgsConstructor @AllArgsConstructor public static class FertilizationRecommendationRow { private String phase; private String nutrients; private String suggestedFertilizer; private Double fertilizerQuantityKgHa; private String applicationMode; private String source; private Double providedN; private Double providedP2O5; private Double providedK2O; private Double balanceN; private Double balanceP2O5; private Double balanceK2O; private String warning; }
    @Data @Builder @NoArgsConstructor @AllArgsConstructor public static class FertilizerSuggestion { private Long fertilizerId; private String fertilizerType; private String fertilizerName; private Double n; private Double p2o5; private Double k2o; private String reason; }
}
