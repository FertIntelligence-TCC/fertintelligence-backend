package com.migueltcc.fertintelligence.service;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.dto.recommendation.RecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.AnnualCropFolderModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.FoliarAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.PhysicalAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.SaturationExtractAnalysisExtractModel;
import com.migueltcc.fertintelligence.repository.*;
import com.migueltcc.fertintelligence.service.implementation.RecommendationCalculationService;
import com.migueltcc.fertintelligence.service.implementation.RecommendationReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationCalculationServiceTest {

    @Mock private PhysicalAnalysisExtractRepository physicalRepo;
    @Mock private SoilAnalysisRepository soilRepo;
    @Mock private SaturationExtractAnalysisExtractRepository saturationRepo;
    @Mock private AnnualCropFolderRepository folderRepo;
    @Mock private CropRepository cropRepo;
    @Mock private FoliarAnalysisRepository foliarRepo;

    @InjectMocks
    private RecommendationCalculationService service;

    private final RecommendationReportService reportService = new RecommendationReportService();
    private PlotModel plot;

    @BeforeEach
    void setup() {
        plot = PlotModel.builder().id(20L).identification("Talhao 1").property(PropertyModel.builder().id(10L).nome("Fazenda").build()).build();
    }

    private RecommendationCreateRequestDto dto() {
        RecommendationCreateRequestDto dto = new RecommendationCreateRequestDto();
        dto.setCropName(NomeComum.ALGODAO);
        dto.setCropYear(2026);
        return dto;
    }

    @Test
    void geraComWarningsQuandoNaoHaAnalises() {
        when(folderRepo.findByPlotAndCropsYear(plot, 2026)).thenReturn(Optional.empty());

        var result = service.calculate(dto(), UserModel.builder().name("U").username("u").build(), plot.getProperty(), plot);
        String report = reportService.buildTechnicalReport(result);

        assertTrue(report.contains("Nenhuma análise física"));
        assertTrue(report.contains("Nenhuma análise de fertilidade do solo"));
        assertTrue(report.contains("Nenhuma pasta de culturas anuais"));
    }

    @Test
    void mencionaAnaliseFertilidadeRecente() {
        SoilAnalysisModel soil = SoilAnalysisModel.builder().id(22L).plot(plot).build();
        when(soilRepo.findTopByPlotOrderByIdDesc(plot)).thenReturn(Optional.of(soil));
        when(folderRepo.findByPlotAndCropsYear(plot, 2026)).thenReturn(Optional.empty());

        var result = service.calculate(dto(), UserModel.builder().name("U").username("u").build(), plot.getProperty(), plot);
        assertEquals(22L, result.getSoilFertilityAnalysisId());
        assertTrue(reportService.buildTechnicalReport(result).contains("ID 22"));
    }

    @Test
    void usaMaisRecentePorMaiorId() {
        PhysicalAnalysisExtractModel latestPhysical = PhysicalAnalysisExtractModel.builder().id(10L).build();
        when(physicalRepo.findTopByRangeExtractAnalysisPlotOrderByIdDesc(plot)).thenReturn(Optional.of(latestPhysical));
        when(folderRepo.findByPlotAndCropsYear(plot, 2026)).thenReturn(Optional.empty());

        var result = service.calculate(dto(), UserModel.builder().name("U").username("u").build(), plot.getProperty(), plot);
        assertEquals(10L, result.getPhysicalAnalysisId());
    }

    @Test
    void warningQuandoNaoExistePastaDaSafra() {
        when(folderRepo.findByPlotAndCropsYear(plot, 2026)).thenReturn(Optional.empty());

        var result = service.calculate(dto(), UserModel.builder().name("U").username("u").build(), plot.getProperty(), plot);
        assertTrue(result.getWarnings().stream().anyMatch(msg -> msg.contains("pasta de culturas anuais")));
    }

    @Test
    void mencionaCulturaEAnaliseFoliarQuandoExistem() {
        AnnualCropFolderModel folder = AnnualCropFolderModel.builder().id(7L).plot(plot).cropsYear(2026).build();
        CropModel crop = CropModel.builder().id(8L).folder(folder).name(NomeComum.ALGODAO).build();
        FoliarAnalysisModel foliar = FoliarAnalysisModel.builder().id(31L).crop(crop).build();
        SaturationExtractAnalysisExtractModel saturation = SaturationExtractAnalysisExtractModel.builder().id(5L).build();

        when(folderRepo.findByPlotAndCropsYear(plot, 2026)).thenReturn(Optional.of(folder));
        when(cropRepo.findTopByFolderAndNameOrderByIdDesc(folder, NomeComum.ALGODAO)).thenReturn(Optional.of(crop));
        when(foliarRepo.findTopByCropOrderByIdDesc(crop)).thenReturn(Optional.of(foliar));
        when(saturationRepo.findTopByRangeExtractAnalysisPlotOrderByIdDesc(plot)).thenReturn(Optional.of(saturation));

        var result = service.calculate(dto(), UserModel.builder().name("U").username("u").build(), plot.getProperty(), plot);
        String report = reportService.buildTechnicalReport(result);

        assertTrue(report.contains("Cultura encontrada: ALGODAO"));
        assertTrue(report.contains("Análise foliar encontrada com ID 31"));
    }
}
