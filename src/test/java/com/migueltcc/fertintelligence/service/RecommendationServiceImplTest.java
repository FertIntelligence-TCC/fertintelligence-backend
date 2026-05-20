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
import com.migueltcc.fertintelligence.service.implementation.RecommendationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
    @Mock private PermissionManager permissionManager;

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    private PropertyModel property;
    private PlotModel plot;

    @BeforeEach
    void setup() {
        property = PropertyModel.builder().id(10L).build();
        plot = PlotModel.builder().id(20L).property(property).build();

        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));
        when(plotRepository.findById(20L)).thenReturn(Optional.of(plot));
        when(recommendationCalculationService.calculate(any(), any(), any(), any()))
                .thenReturn(mock(RecommendationCalculationService.RecommendationCalculationResult.class));
        when(recommendationReportService.buildTechnicalReport(any())).thenReturn("laudo");
        when(recommendationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    private RecommendationCreateRequestDto dto() {
        RecommendationCreateRequestDto dto = new RecommendationCreateRequestDto();
        dto.setPropertyId(10L);
        dto.setPlotId(20L);
        dto.setRecommendationType(RecommendationType.BOTH);
        return dto;
    }

    private UserModel user(String username, Cargo cargo) {
        return UserModel.builder().id((long) cargo.ordinal() + 1).username(username).cargo(cargo).build();
    }

    @Test
    void secretarioConsegueGerarRecomendacao() {
        UserModel secretario = user("sec", Cargo.SECRETARIO);
        when(userRepository.findByUsername("sec")).thenReturn(Optional.of(secretario));
        assertDoesNotThrow(() -> recommendationService.generate(dto(), "sec"));
        verify(permissionManager).assertCanGenerateRecommendation(property, plot, secretario);
    }

    @Test
    void proprietarioConsegueGerarRecomendacao() {
        UserModel proprietario = user("prop", Cargo.PROPRIETARIO);
        when(userRepository.findByUsername("prop")).thenReturn(Optional.of(proprietario));
        assertDoesNotThrow(() -> recommendationService.generate(dto(), "prop"));
        verify(permissionManager).assertCanGenerateRecommendation(property, plot, proprietario);
    }

    @Test
    void gerenteConsegueGerarRecomendacao() {
        UserModel gerente = user("ger", Cargo.GERENTE);
        when(userRepository.findByUsername("ger")).thenReturn(Optional.of(gerente));
        assertDoesNotThrow(() -> recommendationService.generate(dto(), "ger"));
        verify(permissionManager).assertCanGenerateRecommendation(property, plot, gerente);
    }

    @Test
    void supervisorComAcessoConsegueGerarRecomendacao() {
        UserModel supervisor = user("sup", Cargo.SUPERVISOR_DE_AREA);
        when(userRepository.findByUsername("sup")).thenReturn(Optional.of(supervisor));
        assertDoesNotThrow(() -> recommendationService.generate(dto(), "sup"));
        verify(permissionManager).assertCanGenerateRecommendation(property, plot, supervisor);
    }

    @Test
    void agronomosPodemImprimirESecEPproprietarioNao() {
        UserModel residente = user("res", Cargo.AGRONOMO_RESIDENTE);
        UserModel consultor = user("con", Cargo.AGRONOMO_CONSULTOR);
        UserModel secretario = user("sec2", Cargo.SECRETARIO);
        UserModel proprietario = user("prop2", Cargo.PROPRIETARIO);

        RecommendationModel rec = RecommendationModel.builder().id(1L).plot(plot).property(property).creator(residente).build();
        when(recommendationRepository.findById(1L)).thenReturn(Optional.of(rec));

        when(userRepository.findByUsername("res")).thenReturn(Optional.of(residente));
        assertDoesNotThrow(() -> recommendationService.preparePrint(1L, "res"));

        when(userRepository.findByUsername("con")).thenReturn(Optional.of(consultor));
        assertDoesNotThrow(() -> recommendationService.preparePrint(1L, "con"));

        doThrow(new AccessDeniedException("Somente agrônomos residentes e consultores podem imprimir recomendações formais."))
                .when(permissionManager).assertCanPrintRecommendation(secretario);
        when(userRepository.findByUsername("sec2")).thenReturn(Optional.of(secretario));
        assertThrows(AccessDeniedException.class, () -> recommendationService.preparePrint(1L, "sec2"));

        doThrow(new AccessDeniedException("Somente agrônomos residentes e consultores podem imprimir recomendações formais."))
                .when(permissionManager).assertCanPrintRecommendation(proprietario);
        when(userRepository.findByUsername("prop2")).thenReturn(Optional.of(proprietario));
        assertThrows(AccessDeniedException.class, () -> recommendationService.preparePrint(1L, "prop2"));
    }
}
