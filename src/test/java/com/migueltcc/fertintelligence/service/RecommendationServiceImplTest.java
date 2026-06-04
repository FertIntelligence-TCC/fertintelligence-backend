package com.migueltcc.fertintelligence.service;

import com.migueltcc.fertintelligence.composedAttributes.recommendation.RecommendationType;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.recommendation.RecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.PlotRepository;
import com.migueltcc.fertintelligence.repository.PropertyRepository;
import com.migueltcc.fertintelligence.repository.RecommendationRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.implementation.PermissionManager;
import com.migueltcc.fertintelligence.service.implementation.RecommendationCalculationService;
import com.migueltcc.fertintelligence.service.implementation.RecommendationReportService;
import com.migueltcc.fertintelligence.service.implementation.RecommendationNarrativeService;
import com.migueltcc.fertintelligence.service.implementation.RecommendationServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceImplTest {

    @Mock private RecommendationRepository recommendationRepository;
    @Mock private UserRepository userRepository;
    @Mock private PropertyRepository propertyRepository;
    @Mock private PlotRepository plotRepository;
    @Mock private RecommendationCalculationService recommendationCalculationService;
    @Mock private RecommendationReportService recommendationReportService;
    @Mock private RecommendationNarrativeService recommendationNarrativeService;
    @Mock private PermissionManager permissionManager;

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    private PropertyModel property;
    private PlotModel plot;

    @BeforeEach
    void setup() {
        property = PropertyModel.builder()
                .id(10L)
                .nome("Propriedade Teste")
                .build();

        plot = PlotModel.builder()
                .id(20L)
                .identification("Talhão 01")
                .property(property)
                .build();
    }

    private void mockGenerateFlow() {
        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));
        when(plotRepository.findById(20L)).thenReturn(Optional.of(plot));

        RecommendationCalculationService.RecommendationCalculationResult calculationResult =
                mock(RecommendationCalculationService.RecommendationCalculationResult.class);

        when(calculationResult.getCropName()).thenReturn("ALGODAO");
        when(calculationResult.getAnnualCropFolderYear()).thenReturn(2026);

        when(recommendationCalculationService.calculate(any(), any(), any(), any()))
                .thenReturn(calculationResult);

        when(recommendationReportService.buildTechnicalReport(calculationResult))
                .thenReturn("laudo");

        when(recommendationRepository.save(any(RecommendationModel.class)))
                .thenAnswer(inv -> inv.getArgument(0));
    }

    private RecommendationCreateRequestDto dto() {
        RecommendationCreateRequestDto dto = new RecommendationCreateRequestDto();
        dto.setPropertyId(10L);
        dto.setPlotId(20L);
        dto.setRecommendationType(RecommendationType.BOTH);
        dto.setPhysicalAnalysisExtractId(4L);
        dto.setSoilFertilityAnalysisId(2L);
        dto.setSaturationExtractAnalysisExtractId(5L);
        dto.setAnnualCropFolderId(6L);
        dto.setCropId(7L);
        return dto;
    }

    private UserModel user(String username, Cargo cargo) {
        return UserModel.builder()
                .id((long) cargo.ordinal() + 1)
                .username(username)
                .name(username)
                .cargo(cargo)
                .build();
    }

    @Test
    void secretarioConsegueGerarRecomendacao() {
        mockGenerateFlow();

        UserModel secretario = user("sec", Cargo.SECRETARIO);
        when(userRepository.findByUsername("sec")).thenReturn(Optional.of(secretario));

        assertDoesNotThrow(() -> recommendationService.generate(dto(), "sec"));

        verify(permissionManager).assertCanGenerateRecommendation(property, plot, secretario);
        verify(recommendationRepository).save(any(RecommendationModel.class));
    }

    @Test
    void proprietarioConsegueGerarRecomendacao() {
        mockGenerateFlow();

        UserModel proprietario = user("prop", Cargo.PROPRIETARIO);
        when(userRepository.findByUsername("prop")).thenReturn(Optional.of(proprietario));

        assertDoesNotThrow(() -> recommendationService.generate(dto(), "prop"));

        verify(permissionManager).assertCanGenerateRecommendation(property, plot, proprietario);
        verify(recommendationRepository).save(any(RecommendationModel.class));
    }

    @Test
    void gerenteConsegueGerarRecomendacao() {
        mockGenerateFlow();

        UserModel gerente = user("ger", Cargo.GERENTE);
        when(userRepository.findByUsername("ger")).thenReturn(Optional.of(gerente));

        assertDoesNotThrow(() -> recommendationService.generate(dto(), "ger"));

        verify(permissionManager).assertCanGenerateRecommendation(property, plot, gerente);
        verify(recommendationRepository).save(any(RecommendationModel.class));
    }

    @Test
    void supervisorComAcessoConsegueGerarRecomendacao() {
        mockGenerateFlow();

        UserModel supervisor = user("sup", Cargo.SUPERVISOR_DE_AREA);
        when(userRepository.findByUsername("sup")).thenReturn(Optional.of(supervisor));

        assertDoesNotThrow(() -> recommendationService.generate(dto(), "sup"));

        verify(permissionManager).assertCanGenerateRecommendation(property, plot, supervisor);
        verify(recommendationRepository).save(any(RecommendationModel.class));
    }

    @Test
    void agronomosPodemImprimirESecEProprietarioNao() {
        UserModel residente = user("res", Cargo.AGRONOMO_RESIDENTE);
        UserModel consultor = user("con", Cargo.AGRONOMO_CONSULTOR);
        UserModel secretario = user("sec2", Cargo.SECRETARIO);
        UserModel proprietario = user("prop2", Cargo.PROPRIETARIO);

        RecommendationModel rec = RecommendationModel.builder()
                .id(1L)
                .plot(plot)
                .property(property)
                .creator(residente)
                .technicalReport("laudo")
                .recommendationType(RecommendationType.BOTH)
                .build();

        when(recommendationRepository.findById(1L)).thenReturn(Optional.of(rec));

        when(userRepository.findByUsername("res")).thenReturn(Optional.of(residente));
        assertDoesNotThrow(() -> recommendationService.preparePrint(1L, "res"));

        when(userRepository.findByUsername("con")).thenReturn(Optional.of(consultor));
        assertDoesNotThrow(() -> recommendationService.preparePrint(1L, "con"));

        doThrow(new AccessDeniedException("Somente agrônomos residentes e consultores podem imprimir recomendações formais."))
                .when(permissionManager)
                .assertCanPrintRecommendation(secretario);

        when(userRepository.findByUsername("sec2")).thenReturn(Optional.of(secretario));
        assertThrows(AccessDeniedException.class, () -> recommendationService.preparePrint(1L, "sec2"));

        doThrow(new AccessDeniedException("Somente agrônomos residentes e consultores podem imprimir recomendações formais."))
                .when(permissionManager)
                .assertCanPrintRecommendation(proprietario);

        when(userRepository.findByUsername("prop2")).thenReturn(Optional.of(proprietario));
        assertThrows(AccessDeniedException.class, () -> recommendationService.preparePrint(1L, "prop2"));

        verify(permissionManager, times(4)).assertCanReadPlot(eq(plot), any(UserModel.class));
    }

    @Test
    void improveNarrativeMelhoraTextoSemAlterarNumeros() {
        UserModel user = user("res", Cargo.AGRONOMO_RESIDENTE);
        RecommendationModel rec = RecommendationModel.builder()
                .id(1L)
                .plot(plot)
                .property(property)
                .creator(user)
                .technicalReport("Aplicar 120 kg/ha de N e 45.5 kg/ha de K2O")
                .recommendationType(RecommendationType.BOTH)
                .build();

        when(userRepository.findByUsername("res")).thenReturn(Optional.of(user));
        when(recommendationRepository.findById(1L)).thenReturn(Optional.of(rec));
        when(recommendationNarrativeService.improveNarrative(rec.getTechnicalReport()))
                .thenReturn("Aplicar 120 kg/ha de N e 45.5 kg/ha de K2O\n\nTexto revisado para maior clareza. Os cálculos técnicos permanecem inalterados.");
        when(recommendationRepository.save(rec)).thenReturn(rec);

        var result = recommendationService.improveNarrative(1L, "res");

        assertEquals("Aplicar 120 kg/ha de N e 45.5 kg/ha de K2O\n\nTexto revisado para maior clareza. Os cálculos técnicos permanecem inalterados.", result.getTechnicalReport());
        verify(permissionManager).assertCanReadPlot(plot, user);
        verify(recommendationNarrativeService).improveNarrative("Aplicar 120 kg/ha de N e 45.5 kg/ha de K2O");
    }

    @Test
    void usuarioSemAcessoNaoConsegueMelhorarNarrativa() {
        UserModel user = user("sec", Cargo.SECRETARIO);
        RecommendationModel rec = RecommendationModel.builder().id(1L).plot(plot).property(property).creator(user).technicalReport("laudo").build();

        when(userRepository.findByUsername("sec")).thenReturn(Optional.of(user));
        when(recommendationRepository.findById(1L)).thenReturn(Optional.of(rec));
        doThrow(new AccessDeniedException("Sem permissão")).when(permissionManager).assertCanReadPlot(plot, user);

        assertThrows(AccessDeniedException.class, () -> recommendationService.improveNarrative(1L, "sec"));
        verify(recommendationRepository, never()).save(any());
    }

    @Test
    void improveNarrativeComRecommendationInexistenteRetornaErro() {
        UserModel user = user("res", Cargo.AGRONOMO_RESIDENTE);
        when(userRepository.findByUsername("res")).thenReturn(Optional.of(user));
        when(recommendationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> recommendationService.improveNarrative(999L, "res"));
    }
}
