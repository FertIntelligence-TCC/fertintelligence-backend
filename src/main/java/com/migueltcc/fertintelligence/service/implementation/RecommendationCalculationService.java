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
                                            CropFoliarAnalysisInterpretationTableRepository cropFoliarAnalysisInterpretationTableRepository) {
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
    }

    public RecommendationCalculationResult calculate(RecommendationCreateRequestDto dto, UserModel user, PropertyModel property, PlotModel plot) {
        List<String> diagnostics = new ArrayList<>();
        diagnostics.add("Usuário solicitante: " + user.getName() + " (" + user.getUsername() + ")");
        diagnostics.add("Propriedade selecionada: " + property.getNome() + " (ID " + property.getId() + ")");
        diagnostics.add("Talhão selecionado: " + plot.getIdentification() + " (ID " + plot.getId() + ")");
        diagnostics.add("Extrato de análise física selecionado: ID " + dto.getPhysicalAnalysisExtractId());
        diagnostics.add("Análise de fertilidade selecionada: ID " + dto.getSoilFertilityAnalysisId());
        diagnostics.add("Extrato de saturação selecionado: ID " + dto.getSaturationExtractAnalysisExtractId());
        diagnostics.add("Pasta de cultura anual selecionada: ID " + dto.getAnnualCropFolderId());
        diagnostics.add("Cultura selecionada: ID " + dto.getCropId());
        diagnostics.add("Tabela de adubação de culturas selecionada: grupo " + dto.getCropFertilizationTableGroup() + ", ID " + dto.getCropFertilizationTableId());
        diagnostics.add("Tabela de interpretação da fertilidade do solo selecionada: grupo " + dto.getSoilFertilityInterpretationCriteriaTableGroup() + ", ID " + dto.getSoilFertilityInterpretationCriteriaTableId());
        diagnostics.add("Tabela de interpretação de análise foliar selecionada: grupo " + dto.getCropFoliarAnalysisInterpretationTableGroup() + ", ID " + dto.getCropFoliarAnalysisInterpretationTableId());
        FertilizerSourceOption sourceOption = dto.getOrigemAdubos() != null ? dto.getOrigemAdubos() : FertilizerSourceOption.BOTH;
        diagnostics.add("Origem de adubos selecionada: " + sourceOption);
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
        diagnostics.add("Tabela de fertilidade validada: ID " + soilInterpretationTable.getId());
        diagnostics.add("Tabela foliar validada: ID " + foliarInterpretationTable.getId());

        Optional<FoliarAnalysisModel> foliarAnalysis = findLatestFoliarAnalysis(crop);

        Optional<FertilityAnalysisExtractModel> fertilityExtract = findLatestFertilityExtract(soilFertilityAnalysis);

        String physicalSummary = "Análise física selecionada com ID " + physicalAnalysis.getId() + ".";
        String soilFertilitySummary = "Análise de fertilidade selecionada com ID " + soilFertilityAnalysis.getId() + ".";
        String saturationSummary = "Extrato de saturação selecionado com ID " + saturationExtractAnalysis.getId() + ".";
        String cropSummary = "Cultura selecionada: " + crop.getName() + " (ID " + crop.getId() + ").";
        String foliarSummary = foliarAnalysis.map(m -> "Análise foliar encontrada com ID " + m.getId() + ".").orElseGet(() -> addMissing(warnings, "Nenhuma análise foliar foi encontrada para a cultura selecionada."));

        List<String> correctionMessages = buildCorrectionMessages(dto, fertilityExtract, Optional.of(saturationExtractAnalysis), warnings);
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
                    .applicationMode("No sulco de plantio, conforme recomendação técnica e características da cultura.")
                    .source("Tabela de adubação de culturas ID " + table.getId())
                    .build());

            for (ContentRangeModel selectedRange : List.of(nRange.orElse(null), pRange.orElse(null), kRange.orElse(null))) {
                if (selectedRange != null) recommendationRows.addAll(buildCoverageRows(selectedRange, user, sourceOption, fertilizerSuggestions));
            }
        }

        warnings.add("Versão preliminar: a lógica agronômica detalhada ainda não foi aplicada.");
        warnings.add("Valide os parâmetros com engenheiro agrônomo responsável antes de uso operacional.");
        warnings.add("Sugestões foliares (mineral/quelatado/biofertilizante) ainda não estão implementadas no cálculo desta recomendação.");

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
                .annualCropFolderSummary("Pasta de cultura anual ID " + annualCropFolder.getId() + " selecionada.")
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
    private List<String> buildCorrectionMessages(RecommendationCreateRequestDto dto, Optional<FertilityAnalysisExtractModel> fertilityExtract, Optional<SaturationExtractAnalysisExtractModel> saturation, List<String> warnings){List<String> m=new ArrayList<>();m.add("Critério de calagem selecionado: "+dto.getLimingCriteria());Optional<Double> ph=extractPhValue(fertilityExtract,saturation);Optional<Double> al=extractAluminumValue(fertilityExtract); if(ph.isPresent()){double v=ph.get(); if(v<5.5)m.add("pH abaixo de 5.5. Indica necessidade provável de correção de acidez, a confirmar com critério de calagem selecionado."); else if(v<=6.5)m.add("pH em faixa intermediária. Correção deve ser avaliada conforme cultura e saturação por bases."); else m.add("pH elevado. Evitar recomendações automáticas de calagem sem validação técnica.");} if(al.isPresent()&&al.get()>0)m.add("Presença de alumínio trocável detectada. Avaliar neutralização conforme critério selecionado."); if(ph.isEmpty()&&al.isEmpty())warnings.add("Não foi possível calcular correção de acidez/salinidade por ausência de parâmetros suficientes.");return m;}
    private record FertilizerSelection(String name, Double quantityKgHa, Optional<FertilizerSuggestion> suggestion){}
    private FertilizerSelection selectBestPlantingFertilizer(UserModel user, FertilizerSourceOption sourceOption, Double n, Double p, Double k, List<String>w){var formulated=selectFormulatedFertilizers(user, sourceOption);var bestF=formulated.stream().filter(f->f.getN()>0||f.getP2O5()>0||f.getK2O()>0).max((a,b)->compareScore(a.getN(),a.getP2O5(),a.getK2O(),b.getN(),b.getP2O5(),b.getK2O(),n,p,k,a.getId(),b.getId())); if(bestF.isPresent()){double q=estimate(n,p,k,bestF.get().getN(),bestF.get().getP2O5(),bestF.get().getK2O());w.add("Quantidade de adubo calculada de forma simplificada a partir do nutriente limitante. Ajustes agronômicos serão implementados em incrementos futuros.");var s=FertilizerSuggestion.builder().fertilizerId(bestF.get().getId()).fertilizerType("FORMULADO").fertilizerName("NPK "+(int)bestF.get().getN()+"-"+(int)bestF.get().getP2O5()+"-"+(int)bestF.get().getK2O()).n(bestF.get().getN()).p2o5(bestF.get().getP2O5()).k2o(bestF.get().getK2O()).reason("Maior cobertura dos nutrientes de plantio.").build();return new FertilizerSelection(s.getFertilizerName(),q,Optional.of(s));}
    var simples=selectSimpleFertilizers(user, sourceOption);var bestS=simples.stream().filter(f->f.getN()>0||f.getP2O5()>0||f.getK2O()>0).max((a,b)->compareScore(a.getN(),a.getP2O5(),a.getK2O(),b.getN(),b.getP2O5(),b.getK2O(),n,p,k,a.getId(),b.getId()));if(bestS.isPresent()){double q=estimate(n,p,k,bestS.get().getN(),bestS.get().getP2O5(),bestS.get().getK2O());w.add("Quantidade de adubo calculada de forma simplificada a partir do nutriente limitante. Ajustes agronômicos serão implementados em incrementos futuros.");var s=FertilizerSuggestion.builder().fertilizerId(bestS.get().getId()).fertilizerType("SIMPLES").fertilizerName(bestS.get().getName()).n(bestS.get().getN()).p2o5(bestS.get().getP2O5()).k2o(bestS.get().getK2O()).reason("Fallback por ausência de formulado adequado.").build();return new FertilizerSelection(s.getFertilizerName(),q,Optional.of(s));}
    w.add("Nenhum adubo mineral adequado foi encontrado para a origem de adubos selecionada."); return new FertilizerSelection("Não encontrado",null,Optional.empty());}
    private List<FertilizationRecommendationRow> buildCoverageRows(ContentRangeModel range, UserModel user, FertilizerSourceOption sourceOption, List<FertilizerSuggestion> suggestions){List<FertilizationRecommendationRow> rows=new ArrayList<>();for(CoverageModel c:coverageRepository.findAllByRangeOrderByOrderAsc(range)){var simples=selectSimpleFertilizers(user, sourceOption);SimpleMineralFertilizerModel best=null; if(range.getNutrient()==Nutriente.NITROGENIO) best=simples.stream().max(Comparator.comparing(SimpleMineralFertilizerModel::getN)).orElse(null); else if(range.getNutrient()==Nutriente.POTASSIO) best=simples.stream().max(Comparator.comparing(SimpleMineralFertilizerModel::getK2O)).orElse(null); else best=simples.stream().max(Comparator.comparing(SimpleMineralFertilizerModel::getP2O5)).orElse(null); String fertName="Não encontrado"; Double q=null; if(best!=null){double pct=range.getNutrient()==Nutriente.NITROGENIO?best.getN():range.getNutrient()==Nutriente.POTASSIO?best.getK2O():best.getP2O5(); if(pct>0){q=round2(c.getApplication()/pct*100d);} fertName=best.getName(); suggestions.add(FertilizerSuggestion.builder().fertilizerId(best.getId()).fertilizerType("SIMPLES").fertilizerName(best.getName()).n(best.getN()).p2o5(best.getP2O5()).k2o(best.getK2O()).reason("Cobertura por " + range.getNutrient()).build()); }
rows.add(FertilizationRecommendationRow.builder().phase("Cobertura "+c.getOrder()+" - "+range.getNutrient()).nutrients(range.getNutrient()+": "+String.format("%.2f",nvl(c.getApplication()))+" kg/ha").suggestedFertilizer(fertName).fertilizerQuantityKgHa(q).applicationMode("Aplicar em cobertura, ao lado da linha de cultivo, evitando contato direto com folhas e sementes.").source("Coverage ID "+c.getId()+" vinculado ao ContentRange ID "+range.getId()).build());}
return rows;}
    private int compareScore(double an,double ap,double ak,double bn,double bp,double bk,Double rn,Double rp,Double rk,Long aid,Long bid){int as=(nvl(rn)>0&&an>0?1:0)+(nvl(rp)>0&&ap>0?1:0)+(nvl(rk)>0&&ak>0?1:0);int bs=(nvl(rn)>0&&bn>0?1:0)+(nvl(rp)>0&&bp>0?1:0)+(nvl(rk)>0&&bk>0?1:0); if(as!=bs)return Integer.compare(as,bs); if(nvl(rp)>0&&Double.compare(ap,bp)!=0)return Double.compare(ap,bp); return Long.compare(bid,aid);}
    private double estimate(Double rn,Double rp,Double rk,double n,double p,double k){if(nvl(rp)>0&&p>0)return round2(rp/p*100d);if(nvl(rk)>0&&k>0)return round2(rk/k*100d);if(nvl(rn)>0&&n>0)return round2(rn/n*100d);return 0d;}
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
    private Optional<CropFertilizationTableModel> findCropFertilizationTableBySelection(Long id, TechnicalTableGroup group, UserModel user) {if (group == null) throw new IllegalArgumentException("Grupo da tabela de adubação de culturas é obrigatório."); return switch (group) {case PRIVADAS -> cropFertilizationTableRepository.findByIdAndCreatorAndCreator_CargoNot(id, user, Cargo.USUARIO_SUPREMO); case PUBLICAS -> cropFertilizationTableRepository.findByIdAndPublicTableTrueAndCreator_CargoNot(id, Cargo.USUARIO_SUPREMO); case PADRAO -> cropFertilizationTableRepository.findByIdAndCreator_Cargo(id, Cargo.USUARIO_SUPREMO);};}
    private Optional<SoilFertilityInterpretationCriteriaTableModel> findSoilFertilityInterpretationTableBySelection(Long id, TechnicalTableGroup group, UserModel user) {if (group == null) throw new IllegalArgumentException("Grupo da tabela de interpretação da fertilidade do solo é obrigatório."); return switch (group) {case PRIVADAS -> soilFertilityInterpretationCriteriaTableRepository.findByIdAndCreatorAndCreator_CargoNot(id, user, Cargo.USUARIO_SUPREMO); case PUBLICAS -> soilFertilityInterpretationCriteriaTableRepository.findByIdAndPublicTableTrueAndCreator_CargoNot(id, Cargo.USUARIO_SUPREMO); case PADRAO -> soilFertilityInterpretationCriteriaTableRepository.findByIdAndCreator_Cargo(id, Cargo.USUARIO_SUPREMO);};}
    private Optional<CropFoliarAnalysisInterpretationTableModel> findCropFoliarAnalysisInterpretationTableBySelection(Long id, TechnicalTableGroup group, UserModel user) {if (group == null) throw new IllegalArgumentException("Grupo da tabela de interpretação de análise foliar é obrigatório."); return switch (group) {case PRIVADAS -> cropFoliarAnalysisInterpretationTableRepository.findByIdAndCreatorAndCreator_CargoNot(id, user, Cargo.USUARIO_SUPREMO); case PUBLICAS -> cropFoliarAnalysisInterpretationTableRepository.findByIdAndPublicTableTrueAndCreator_CargoNot(id, Cargo.USUARIO_SUPREMO); case PADRAO -> cropFoliarAnalysisInterpretationTableRepository.findByIdAndCreator_Cargo(id, Cargo.USUARIO_SUPREMO);};}
    private void validateSamePlot(PlotModel selectedPlot, PlotModel requestPlot, String message) {if (selectedPlot == null || requestPlot == null || !Objects.equals(selectedPlot.getId(), requestPlot.getId())) throw new IllegalArgumentException(message);}
    private PlotModel resolvePlot(PhysicalAnalysisExtractModel model) {if (model.getRangeExtract() != null && model.getRangeExtract().getAnalysis() != null) return model.getRangeExtract().getAnalysis().getPlot(); if (model.getLayerExtract() != null && model.getLayerExtract().getAnalysis() != null) return model.getLayerExtract().getAnalysis().getPlot(); throw new IllegalArgumentException("Extrato de análise física não possui análise de solo associada.");}
    private PlotModel resolvePlot(SaturationExtractAnalysisExtractModel model) {if (model.getRangeExtract() != null && model.getRangeExtract().getAnalysis() != null) return model.getRangeExtract().getAnalysis().getPlot(); if (model.getLayerExtract() != null && model.getLayerExtract().getAnalysis() != null) return model.getLayerExtract().getAnalysis().getPlot(); throw new IllegalArgumentException("Extrato de análise de saturação não possui análise de solo associada.");}
    private Optional<FoliarAnalysisModel> findLatestFoliarAnalysis(CropModel crop) {return foliarAnalysisRepository.findTopByCropOrderByIdDesc(crop);}    
    private List<FormulatedMineralFertilizerModel> selectFormulatedFertilizers(UserModel user, FertilizerSourceOption sourceOption){return switch (sourceOption) {case PRIVATE -> formulatedMineralFertilizerRepository.findAllByUserAndPublicoFalseOrderByIdAsc(user); case PUBLIC -> formulatedMineralFertilizerRepository.findAllByPublicoTrueOrderByIdAsc(); case DEFAULT -> formulatedMineralFertilizerRepository.findAllByUser_CargoOrderByIdAsc(Cargo.USUARIO_SUPREMO); case BOTH -> dedup(dedup(formulatedMineralFertilizerRepository.findAllByUserAndPublicoFalseOrderByIdAsc(user), formulatedMineralFertilizerRepository.findAllByPublicoTrueOrderByIdAsc(), FormulatedMineralFertilizerModel::getId), formulatedMineralFertilizerRepository.findAllByUser_CargoOrderByIdAsc(Cargo.USUARIO_SUPREMO), FormulatedMineralFertilizerModel::getId);};}
    private List<SimpleMineralFertilizerModel> selectSimpleFertilizers(UserModel user, FertilizerSourceOption sourceOption){return switch (sourceOption) {case PRIVATE -> simpleMineralFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user); case PUBLIC -> simpleMineralFertilizerRepository.findAllByPublicoTrueOrderByNameAsc(); case DEFAULT -> simpleMineralFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO); case BOTH -> dedup(dedup(simpleMineralFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user), simpleMineralFertilizerRepository.findAllByPublicoTrueOrderByNameAsc(), SimpleMineralFertilizerModel::getId), simpleMineralFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO), SimpleMineralFertilizerModel::getId);};}
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class RecommendationCalculationResult { private String requesterName; private String requesterUsername; private String propertyName; private Long propertyId; private String plotIdentification; private Long plotId; private String cropName; private Integer annualCropFolderYear; private String recommendationType; private String limingCriteria; private LocalDateTime issuedAt; private List<String> warnings; private List<String> diagnosticMessages; private List<String> fertilizationRows; private List<String> correctionMessages; private Long physicalAnalysisId; private Long soilFertilityAnalysisId; private Long saturationExtractAnalysisId; private Long annualCropFolderId; private Long cropId; private Long foliarAnalysisId; private String physicalAnalysisSummary; private String soilFertilityAnalysisSummary; private String saturationExtractAnalysisSummary; private String annualCropFolderSummary; private String cropSummary; private String foliarAnalysisSummary; private Double requiredN; private Double requiredP2O5; private Double requiredK2O; private Long nitrogenRangeId; private Long phosphorusRangeId; private Long potassiumRangeId; private List<FertilizationRecommendationRow> fertilizationRecommendationRows; private List<FertilizerSuggestion> fertilizerSuggestions; }
    @Data @Builder @NoArgsConstructor @AllArgsConstructor public static class FertilizationRecommendationRow { private String phase; private String nutrients; private String suggestedFertilizer; private Double fertilizerQuantityKgHa; private String applicationMode; private String source; }
    @Data @Builder @NoArgsConstructor @AllArgsConstructor public static class FertilizerSuggestion { private Long fertilizerId; private String fertilizerType; private String fertilizerName; private Double n; private Double p2o5; private Double k2o; private String reason; }
}
