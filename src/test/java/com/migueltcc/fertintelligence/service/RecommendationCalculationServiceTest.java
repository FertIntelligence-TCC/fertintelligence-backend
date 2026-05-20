package com.migueltcc.fertintelligence.service;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Nutriente;
import com.migueltcc.fertintelligence.dto.recommendation.RecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.*;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.FoliarAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.SaturationExtractAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.ContentRangeModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CoverageModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFertilizationTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.FormulatedMineralFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.SimpleMineralFertilizerModel;
import com.migueltcc.fertintelligence.repository.*;
import com.migueltcc.fertintelligence.service.implementation.RecommendationCalculationService;
import com.migueltcc.fertintelligence.service.implementation.RecommendationReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationCalculationServiceTest {
    @Mock private PhysicalAnalysisExtractRepository physicalRepo;
    @Mock private SoilAnalysisRepository soilRepo;
    @Mock private SaturationExtractAnalysisExtractRepository saturationRepo;
    @Mock private AnnualCropFolderRepository folderRepo;
    @Mock private CropRepository cropRepo;
    @Mock private FoliarAnalysisRepository foliarRepo;
    @Mock private CropFertilizationTableRepository tableRepo;
    @Mock private ContentRangeRepository rangeRepo;
    @Mock private CoverageRepository coverageRepo;
    @Mock private FormulatedMineralFertilizerRepository formulatedRepo;
    @Mock private SimpleMineralFertilizerRepository simpleRepo;
    @Mock private FertilityAnalysisExtractRepository fertilityExtractRepo;

    @InjectMocks private RecommendationCalculationService service;
    private final RecommendationReportService reportService = new RecommendationReportService();
    private PlotModel plot; private PropertyModel property; private UserModel user;

    @BeforeEach void setup(){
        property=PropertyModel.builder().id(10L).nome("Fazenda").build();
        plot=PlotModel.builder().id(20L).identification("Talhao 1").property(property).build();
        user=UserModel.builder().id(1L).name("U").username("u").build();
        when(folderRepo.findByPlotAndCropsYear(plot,2026)).thenReturn(Optional.empty());
        when(formulatedRepo.findAllByUser(any())).thenReturn(List.of());
        when(formulatedRepo.findAllByPublicoTrueOrderByIdAsc()).thenReturn(List.of());
        when(simpleRepo.findAllByUser(any())).thenReturn(List.of());
        when(simpleRepo.findAllByPublicoTrueOrderByNameAsc()).thenReturn(List.of());
        when(coverageRepo.findAllByRangeOrderByOrderAsc(any())).thenReturn(List.of());
        when(fertilityExtractRepo.findAll()).thenReturn(List.of());
    }

    private RecommendationCreateRequestDto dto(){
        RecommendationCreateRequestDto dto=new RecommendationCreateRequestDto();
        dto.setCropName(NomeComum.ALGODAO); dto.setCropYear(2026); dto.setCropFertilizationTableId(1L); dto.setLimingCriteria("SMP"); return dto;
    }

    @Test void preencheNecessidadesNPKQuandoTabelaExiste(){
        CropFertilizationTableModel table=CropFertilizationTableModel.builder().id(1L).build();
        when(tableRepo.findById(1L)).thenReturn(Optional.of(table));
        when(rangeRepo.findAllByTableAndNutrientOrderByOrderAsc(table,Nutriente.NITROGENIO)).thenReturn(List.of(ContentRangeModel.builder().id(11L).nutrient(Nutriente.NITROGENIO).application(20d).build()));
        when(rangeRepo.findAllByTableAndNutrientOrderByOrderAsc(table,Nutriente.FOSFORO)).thenReturn(List.of(ContentRangeModel.builder().id(12L).nutrient(Nutriente.FOSFORO).application(80d).build()));
        when(rangeRepo.findAllByTableAndNutrientOrderByOrderAsc(table,Nutriente.POTASSIO)).thenReturn(List.of(ContentRangeModel.builder().id(13L).nutrient(Nutriente.POTASSIO).application(70d).build()));
        var result=service.calculate(dto(),user,property,plot);
        assertEquals(20d,result.getRequiredN()); assertEquals(80d,result.getRequiredP2O5()); assertEquals(70d,result.getRequiredK2O());
    }

    @Test void sugereAduboFormuladoQuandoCompativel(){
        CropFertilizationTableModel table=CropFertilizationTableModel.builder().id(1L).build();
        when(tableRepo.findById(1L)).thenReturn(Optional.of(table));
        when(rangeRepo.findAllByTableAndNutrientOrderByOrderAsc(any(),any())).thenReturn(List.of(ContentRangeModel.builder().id(1L).application(60d).build()));
        when(formulatedRepo.findAllByUser(user)).thenReturn(List.of(FormulatedMineralFertilizerModel.builder().id(5L).N(4).P2O5(14).K2O(8).build()));
        var result=service.calculate(dto(),user,property,plot);
        assertTrue(result.getFertilizerSuggestions().stream().anyMatch(s->"FORMULADO".equals(s.getFertilizerType())));
    }

    @Test void usaFallbackDeAduboSimples(){
        CropFertilizationTableModel table=CropFertilizationTableModel.builder().id(1L).build();
        when(tableRepo.findById(1L)).thenReturn(Optional.of(table));
        when(rangeRepo.findAllByTableAndNutrientOrderByOrderAsc(any(),any())).thenReturn(List.of(ContentRangeModel.builder().id(1L).application(60d).build()));
        when(simpleRepo.findAllByUser(user)).thenReturn(List.of(SimpleMineralFertilizerModel.builder().id(8L).name("Ureia").N(45).build()));
        var result=service.calculate(dto(),user,property,plot);
        assertTrue(result.getFertilizerSuggestions().stream().anyMatch(s->"SIMPLES".equals(s.getFertilizerType())));
    }

    @Test void geraWarningQuandoNaoHaTabela(){
        when(tableRepo.findById(1L)).thenReturn(Optional.empty());
        var result=service.calculate(dto(),user,property,plot);
        assertTrue(result.getWarnings().stream().anyMatch(w->w.contains("Tabela de adubação")));
    }

    @Test void semAnaliseFertilidadeUsaPrimeiroIntervaloDePeKComWarning(){
        CropFertilizationTableModel table=CropFertilizationTableModel.builder().id(1L).build();
        when(tableRepo.findById(1L)).thenReturn(Optional.of(table));
        when(rangeRepo.findAllByTableAndNutrientOrderByOrderAsc(table,Nutriente.NITROGENIO)).thenReturn(List.of(ContentRangeModel.builder().id(11L).application(20d).build()));
        when(rangeRepo.findAllByTableAndNutrientOrderByOrderAsc(table,Nutriente.FOSFORO)).thenReturn(List.of(ContentRangeModel.builder().id(12L).application(80d).build()));
        when(rangeRepo.findAllByTableAndNutrientOrderByOrderAsc(table,Nutriente.POTASSIO)).thenReturn(List.of(ContentRangeModel.builder().id(13L).application(70d).build()));
        var result=service.calculate(dto(),user,property,plot);
        assertEquals(12L,result.getPhosphorusRangeId()); assertEquals(13L,result.getPotassiumRangeId());
        assertTrue(result.getWarnings().stream().anyMatch(w->w.contains("Não foi possível classificar teor de fósforo")));
    }

    @Test void geraLinhaCoberturaQuandoExisteCoverage(){
        CropFertilizationTableModel table=CropFertilizationTableModel.builder().id(1L).build();
        ContentRangeModel rangeN=ContentRangeModel.builder().id(11L).nutrient(Nutriente.NITROGENIO).application(20d).build();
        when(tableRepo.findById(1L)).thenReturn(Optional.of(table));
        when(rangeRepo.findAllByTableAndNutrientOrderByOrderAsc(table,Nutriente.NITROGENIO)).thenReturn(List.of(rangeN));
        when(rangeRepo.findAllByTableAndNutrientOrderByOrderAsc(table,Nutriente.FOSFORO)).thenReturn(List.of(ContentRangeModel.builder().id(12L).nutrient(Nutriente.FOSFORO).application(80d).build()));
        when(rangeRepo.findAllByTableAndNutrientOrderByOrderAsc(table,Nutriente.POTASSIO)).thenReturn(List.of(ContentRangeModel.builder().id(13L).nutrient(Nutriente.POTASSIO).application(70d).build()));
        when(coverageRepo.findAllByRangeOrderByOrderAsc(rangeN)).thenReturn(List.of(CoverageModel.builder().id(100L).order(1).range(rangeN).application(30d).build()));
        when(simpleRepo.findAllByUser(user)).thenReturn(List.of(SimpleMineralFertilizerModel.builder().id(8L).name("Ureia").N(45).build()));
        var result=service.calculate(dto(),user,property,plot);
        assertTrue(result.getFertilizationRecommendationRows().stream().anyMatch(r->r.getPhase().contains("Cobertura 1")));
    }

    @Test void reportGeraTabelaMarkdown(){
        var result=RecommendationCalculationService.RecommendationCalculationResult.builder()
                .diagnosticMessages(List.of("d")).correctionMessages(List.of("c")).warnings(List.of("w"))
                .physicalAnalysisSummary("p").soilFertilityAnalysisSummary("s").saturationExtractAnalysisSummary("se").cropSummary("c").foliarAnalysisSummary("f")
                .fertilizationRecommendationRows(List.of(RecommendationCalculationService.FertilizationRecommendationRow.builder().phase("Plantio").nutrients("N: 20").suggestedFertilizer("NPK").fertilizerQuantityKgHa(100d).applicationMode("No sulco").build()))
                .build();
        var report=reportService.buildTechnicalReport(result);
        assertTrue(report.contains("| Fase | Nutrientes Necessários | Adubo Sugerido | Quantidade | Época e Modo de Aplicação |"));
    }

    @Test void mencionaCulturaEAnaliseFoliarQuandoExistem() {
        AnnualCropFolderModel folder = AnnualCropFolderModel.builder().id(7L).plot(plot).cropsYear(2026).build();
        CropModel crop = CropModel.builder().id(8L).folder(folder).name(NomeComum.ALGODAO).build();
        FoliarAnalysisModel foliar = FoliarAnalysisModel.builder().id(31L).crop(crop).build();
        SaturationExtractAnalysisExtractModel saturation = SaturationExtractAnalysisExtractModel.builder().id(5L).build();
        when(folderRepo.findByPlotAndCropsYear(plot, 2026)).thenReturn(Optional.of(folder));
        when(cropRepo.findTopByFolderAndNameOrderByIdDesc(folder, NomeComum.ALGODAO)).thenReturn(Optional.of(crop));
        when(foliarRepo.findTopByCropOrderByIdDesc(crop)).thenReturn(Optional.of(foliar));
        when(saturationRepo.findTopByRangeExtractAnalysisPlotOrderByIdDesc(plot)).thenReturn(Optional.of(saturation));
        when(tableRepo.findById(1L)).thenReturn(Optional.empty());
        var result = service.calculate(dto(), user, property, plot);
        String report = reportService.buildTechnicalReport(result);
        assertTrue(report.contains("Cultura encontrada: ALGODAO"));
        assertTrue(report.contains("Análise foliar encontrada com ID 31"));
    }
}
