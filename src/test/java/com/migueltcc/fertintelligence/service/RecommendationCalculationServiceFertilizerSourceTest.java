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
                mock(FertilityAnalysisExtractRepository.class)
        );
        user = UserModel.builder().id(1L).build();
    }

    @Test
    void shouldSelectOnlyPrivateFertilizers() throws Exception {
        when(simpleRepo.findAllByUserOrDefaultCreator(user, Cargo.USUARIO_SUPREMO)).thenReturn(List.of(f(1L, "Privado")));
        List<?> result = callSelectSimple(FertilizerSourceOption.PRIVATE);
        assertEquals(1, result.size());
        verify(simpleRepo, never()).findAllByPublicoTrueOrDefaultCreatorOrderByNameAsc(Cargo.USUARIO_SUPREMO);
    }

    @Test
    void shouldSelectOnlyPublicFertilizers() throws Exception {
        when(simpleRepo.findAllByPublicoTrueOrDefaultCreatorOrderByNameAsc(Cargo.USUARIO_SUPREMO)).thenReturn(List.of(f(2L, "Publico")));
        List<?> result = callSelectSimple(FertilizerSourceOption.PUBLIC);
        assertEquals(1, result.size());
        verify(simpleRepo, never()).findAllByUserOrDefaultCreator(user, Cargo.USUARIO_SUPREMO);
    }

    @Test
    void shouldMergeAndDeduplicateForBoth() throws Exception {
        when(simpleRepo.findAllByUserOrDefaultCreator(user, Cargo.USUARIO_SUPREMO)).thenReturn(List.of(f(1L, "Privado"), f(10L, "Duplicado")));
        when(simpleRepo.findAllByPublicoTrueOrDefaultCreatorOrderByNameAsc(Cargo.USUARIO_SUPREMO)).thenReturn(List.of(f(10L, "Duplicado"), f(2L, "Publico")));
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
