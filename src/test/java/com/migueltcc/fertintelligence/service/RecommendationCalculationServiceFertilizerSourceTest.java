package com.migueltcc.fertintelligence.service;

import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.SimpleMineralFertilizerModel;
import com.migueltcc.fertintelligence.repository.*;
import com.migueltcc.fertintelligence.service.implementation.RecommendationCalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RecommendationCalculationServiceFertilizerSourceTest {
    private RecommendationCalculationService service;
    private SimpleMineralFertilizerRepository simpleRepo;
    private UserModel user;

    @BeforeEach
    void setUp() {
        simpleRepo = mock(SimpleMineralFertilizerRepository.class);
        service = new RecommendationCalculationService(
                mock(PhysicalAnalysisExtractRepository.class),
                mock(SoilAnalysisRepository.class),
                mock(SaturationExtractAnalysisExtractRepository.class),
                mock(AnnualCropFolderRepository.class),
                mock(CropRepository.class),
                mock(FoliarAnalysisRepository.class),
                mock(CropFertilizationTableRepository.class),
                mock(ContentRangeRepository.class),
                mock(CoverageRepository.class),
                mock(FormulatedMineralFertilizerRepository.class),
                simpleRepo,
                mock(FertilityAnalysisExtractRepository.class),
                mock(SoilFertilityInterpretationCriteriaTableRepository.class),
                mock(CropFoliarAnalysisInterpretationTableRepository.class),
                mock(CropFoliarAnalysisInterpretationTableLineRepository.class),
                mock(DiverseContentRangeRepository.class),
                mock(KExchangeableContentRepository.class),
                mock(AvailablePMehlich1ExtractorRepository.class),
                mock(AvailablePAnionExchangeResinExtractorRepository.class),
                mock(AvailableSRepository.class),
                mock(ExchangeableSodiumRepository.class),
                mock(SalinityInterpretationRepository.class),
                mock(OrganicFertilizerRepository.class),
                mock(OrganoMineralFertilizerRepository.class),
                mock(GreenFertilizerRepository.class),
                mock(BioFertilizerRepository.class),
                mock(MineralFertilizerRepository.class),
                mock(ChelatedFertilizerRepository.class),
                mock(CropFertilizationMicronutrientDoseRepository.class)
        );
        user = UserModel.builder().id(1L).build();
    }

    @Test
    void shouldSelectOnlyPrivateFertilizers() throws Exception {
        when(simpleRepo.findAllByUserAndPublicoFalseOrderByNameAsc(user)).thenReturn(List.of(f(1L, "Privado")));
        List<?> result = callSelectSimple(FertilizerSourceOption.PRIVATE);
        assertEquals(1, result.size());
        verify(simpleRepo, never()).findAllByPublicoTrueOrderByNameAsc();
        verify(simpleRepo, never()).findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO);
    }

    @Test
    void shouldSelectOnlyPublicFertilizers() throws Exception {
        when(simpleRepo.findAllByPublicoTrueOrderByNameAsc()).thenReturn(List.of(f(2L, "Publico")));
        List<?> result = callSelectSimple(FertilizerSourceOption.PUBLIC);
        assertEquals(1, result.size());
        verify(simpleRepo, never()).findAllByUserAndPublicoFalseOrderByNameAsc(user);
        verify(simpleRepo, never()).findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO);
    }

    @Test
    void shouldSelectOnlyDefaultFertilizers() throws Exception {
        when(simpleRepo.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO)).thenReturn(List.of(f(3L, "Padrao")));
        List<?> result = callSelectSimple(FertilizerSourceOption.DEFAULT);
        assertEquals(1, result.size());
        verify(simpleRepo, never()).findAllByUserAndPublicoFalseOrderByNameAsc(user);
        verify(simpleRepo, never()).findAllByPublicoTrueOrderByNameAsc();
    }

    @Test
    void shouldMergeAndDeduplicateForBoth() throws Exception {
        when(simpleRepo.findAllByUserAndPublicoFalseOrderByNameAsc(user)).thenReturn(List.of(f(1L, "Privado"), f(10L, "Duplicado")));
        when(simpleRepo.findAllByPublicoTrueOrderByNameAsc()).thenReturn(List.of(f(10L, "Duplicado"), f(2L, "Publico")));
        when(simpleRepo.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO)).thenReturn(List.of(f(2L, "Publico"), f(3L, "Padrao")));
        List<?> result = callSelectSimple(FertilizerSourceOption.BOTH);
        assertEquals(3, result.size());
    }

    @SuppressWarnings("unchecked")
    private List<SimpleMineralFertilizerModel> callSelectSimple(FertilizerSourceOption option) throws Exception {
        Method m = RecommendationCalculationService.class.getDeclaredMethod("selectSimpleFertilizers", UserModel.class, FertilizerSourceOption.class);
        m.setAccessible(true);
        return (List<SimpleMineralFertilizerModel>) m.invoke(service, user, option);
    }

    private static SimpleMineralFertilizerModel f(Long id, String name) {
        return SimpleMineralFertilizerModel.builder().id(id).name(name).build();
    }
}
