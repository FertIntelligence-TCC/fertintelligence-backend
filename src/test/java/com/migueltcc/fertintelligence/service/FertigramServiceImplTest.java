package com.migueltcc.fertintelligence.service;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.MenorMaiorTeores;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.MacronutrientsContent;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.MicronutrientsContent;
import com.migueltcc.fertintelligence.dto.fertigram.FertigramResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.AnnualCropFolderModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.FoliarAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertigram.FertigramModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertigram.FertigramNutrientModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableModel;
import com.migueltcc.fertintelligence.repository.*;
import com.migueltcc.fertintelligence.service.implementation.FertigramServiceImpl;
import com.migueltcc.fertintelligence.service.implementation.PermissionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FertigramServiceImplTest {
    @Mock FoliarAnalysisRepository foliarRepo;
    @Mock CropFoliarAnalysisInterpretationTableRepository tableRepo;
    @Mock CropFoliarAnalysisInterpretationTableLineRepository lineRepo;
    @Mock UserRepository userRepo;
    @Mock PermissionManager permissionManager;
    @Mock FertigramRepository fertigramRepository;
    @Mock FertigramNutrientRepository fertigramNutrientRepository;
    @InjectMocks FertigramServiceImpl service;

    private UserModel user;
    private FoliarAnalysisModel analysis;
    private CropFoliarAnalysisInterpretationTableModel table;

    @BeforeEach
    void setUp() {
        user = UserModel.builder().id(1L).username("u").build();
        PlotModel plot = PlotModel.builder().id(99L).build();
        AnnualCropFolderModel folder = AnnualCropFolderModel.builder().id(88L).plot(plot).build();
        CropModel crop = CropModel.builder().id(10L).name(NomeComum.SOJA).folder(folder).build();
        analysis = FoliarAnalysisModel.builder()
                .id(100L)
                .crop(crop)
                .macronutrients(new MacronutrientsContent(3.0, 0.2, 2.1, null, 0.4, null))
                .micronutrients(new MicronutrientsContent(20.0, null, 95.0, null, 40.0, 2.0, null))
                .build();
        table = CropFoliarAnalysisInterpretationTableModel.builder().id(5L).creator(user).publicTable(true).build();
    }

    @Test
    void geraFertigramaComMacroEMicroPresentes() {
        CropFoliarAnalysisInterpretationTableLineModel line = CropFoliarAnalysisInterpretationTableLineModel.builder()
                .n_content(new MenorMaiorTeores(3.5, 5.0, "%"))
                .p_content(new MenorMaiorTeores(0.1, 0.3, "%"))
                .k_content(new MenorMaiorTeores(1.8, 2.5, "%"))
                .mg_content(new MenorMaiorTeores(0.3, 0.8, "%"))
                .b_content(new MenorMaiorTeores(15.0, 30.0, "mg/kg"))
                .fe_content(new MenorMaiorTeores(60.0, 120.0, "mg/kg"))
                .mn_content(new MenorMaiorTeores(20.0, 50.0, "mg/kg"))
                .mo_content(new MenorMaiorTeores(1.0, 3.0, "mg/kg"))
                .crop(NomeComum.SOJA)
                .build();

        FertigramModel savedFertigram = FertigramModel.builder().id(1L).foliarAnalysis(analysis).table(table).build();

        when(userRepo.findByUsername("u")).thenReturn(Optional.of(user));
        when(foliarRepo.findById(100L)).thenReturn(Optional.of(analysis));
        when(tableRepo.findById(5L)).thenReturn(Optional.of(table));
        when(lineRepo.findAllByTableOrderByIdAsc(table)).thenReturn(List.of(line));
        when(fertigramRepository.save(any(FertigramModel.class))).thenReturn(savedFertigram);
        when(fertigramNutrientRepository.findAllByFertigramOrderByIdAsc(savedFertigram))
                .thenReturn(List.of(
                        FertigramNutrientModel.builder().id(1L).fertigram(savedFertigram).nutrient("N").groupType(com.migueltcc.fertintelligence.model.fertintelligence.fertigram.FertigramNutrientGroupType.MACRO).measuredValue(3.0).build(),
                        FertigramNutrientModel.builder().id(2L).fertigram(savedFertigram).nutrient("P").groupType(com.migueltcc.fertintelligence.model.fertintelligence.fertigram.FertigramNutrientGroupType.MACRO).measuredValue(0.2).build(),
                        FertigramNutrientModel.builder().id(3L).fertigram(savedFertigram).nutrient("B").groupType(com.migueltcc.fertintelligence.model.fertintelligence.fertigram.FertigramNutrientGroupType.MICRO).measuredValue(20.0).build(),
                        FertigramNutrientModel.builder().id(4L).fertigram(savedFertigram).nutrient("Fe").groupType(com.migueltcc.fertintelligence.model.fertintelligence.fertigram.FertigramNutrientGroupType.MICRO).measuredValue(95.0).build()
                ));

        FertigramResponseDto response = service.generate(100L, 5L, "u");

        assertEquals(2, response.getMacronutrients().size());
        assertEquals(2, response.getMicronutrients().size());
        assertEquals("MACRONUTRIENTE", response.getMacronutrients().get(0).getNutrientGroupType());
        assertEquals("MICRONUTRIENTE", response.getMicronutrients().get(0).getNutrientGroupType());
        verify(fertigramNutrientRepository, atLeastOnce()).save(any(FertigramNutrientModel.class));
    }

    @Test
    void nutrienteNullNaoApareceNoResponse() {
        FertigramModel savedFertigram = FertigramModel.builder().id(1L).foliarAnalysis(analysis).table(table).build();
        when(userRepo.findByUsername("u")).thenReturn(Optional.of(user));
        when(foliarRepo.findById(100L)).thenReturn(Optional.of(analysis));
        when(tableRepo.findById(5L)).thenReturn(Optional.of(table));
        when(lineRepo.findAllByTableOrderByIdAsc(table)).thenReturn(List.of(CropFoliarAnalysisInterpretationTableLineModel.builder().crop(NomeComum.SOJA).build()));
        when(fertigramRepository.save(any(FertigramModel.class))).thenReturn(savedFertigram);
        when(fertigramNutrientRepository.findAllByFertigramOrderByIdAsc(savedFertigram)).thenReturn(List.of());

        FertigramResponseDto response = service.generate(100L, 5L, "u");

        assertTrue(response.getMacronutrients().isEmpty());
        assertTrue(response.getMicronutrients().isEmpty());
    }

    @Test
    void tabelaPrivadaOutroUsuarioContinuaProtegida() {
        CropFoliarAnalysisInterpretationTableModel privateTable = CropFoliarAnalysisInterpretationTableModel.builder()
                .id(6L).creator(UserModel.builder().id(999L).build()).publicTable(false).build();
        when(userRepo.findByUsername("u")).thenReturn(Optional.of(user));
        when(foliarRepo.findById(100L)).thenReturn(Optional.of(analysis));
        when(tableRepo.findById(6L)).thenReturn(Optional.of(privateTable));

        assertThrows(AccessDeniedException.class, () -> service.generate(100L, 6L, "u"));
    }

    @Test
    void semLinhasRetornaListasVaziasComWarning() {
        FertigramModel savedFertigram = FertigramModel.builder().id(1L).foliarAnalysis(analysis).table(table).warning("Tabela de interpretação sem linhas cadastradas.").build();
        when(userRepo.findByUsername("u")).thenReturn(Optional.of(user));
        when(foliarRepo.findById(100L)).thenReturn(Optional.of(analysis));
        when(tableRepo.findById(5L)).thenReturn(Optional.of(table));
        when(lineRepo.findAllByTableOrderByIdAsc(table)).thenReturn(Collections.emptyList());
        when(fertigramRepository.save(any(FertigramModel.class))).thenReturn(savedFertigram);

        FertigramResponseDto response = service.generate(100L, 5L, "u");

        assertTrue(response.getMacronutrients().isEmpty());
        assertTrue(response.getMicronutrients().isEmpty());
        assertNotNull(response.getWarning());
        verify(fertigramNutrientRepository, never()).save(any(FertigramNutrientModel.class));
    }

    @Test
    void foliarAnalysisInexistenteRetorna404Controlado() {
        when(userRepo.findByUsername("u")).thenReturn(Optional.of(user));
        when(foliarRepo.findById(404L)).thenReturn(Optional.empty());

        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> service.generate(404L, 5L, "u"));
    }

    @Test
    void tabelaInexistenteRetorna404Controlado() {
        when(userRepo.findByUsername("u")).thenReturn(Optional.of(user));
        when(foliarRepo.findById(100L)).thenReturn(Optional.of(analysis));
        when(tableRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> service.generate(100L, 999L, "u"));
    }

    @Test
    void dadosNulosDeVinculoDaAnaliseNaoGeramNpe() {
        FoliarAnalysisModel analysisSemVinculo = FoliarAnalysisModel.builder().id(777L).build();
        when(userRepo.findByUsername("u")).thenReturn(Optional.of(user));
        when(foliarRepo.findById(777L)).thenReturn(Optional.of(analysisSemVinculo));

        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> service.generate(777L, 5L, "u"));
    }


    @Test
    void tabelaComLinhasSemCulturaCompativelRetornaListasVaziasComWarning() {
        CropFoliarAnalysisInterpretationTableLineModel lineOutraCultura = CropFoliarAnalysisInterpretationTableLineModel.builder()
                .crop(NomeComum.MILHO)
                .build();
        FertigramModel savedFertigram = FertigramModel.builder().id(1L).foliarAnalysis(analysis).table(table).build();

        when(userRepo.findByUsername("u")).thenReturn(Optional.of(user));
        when(foliarRepo.findById(100L)).thenReturn(Optional.of(analysis));
        when(tableRepo.findById(5L)).thenReturn(Optional.of(table));
        when(lineRepo.findAllByTableOrderByIdAsc(table)).thenReturn(List.of(lineOutraCultura));
        when(fertigramRepository.save(any(FertigramModel.class)))
                .thenReturn(savedFertigram)
                .thenReturn(FertigramModel.builder().id(1L).foliarAnalysis(analysis).table(table).warning("Tabela de interpretação não possui linha compatível com a cultura da análise foliar.").build());

        FertigramResponseDto response = service.generate(100L, 5L, "u");

        assertTrue(response.getMacronutrients().isEmpty());
        assertTrue(response.getMicronutrients().isEmpty());
        assertEquals("Tabela de interpretação não possui linha compatível com a cultura da análise foliar.", response.getWarning());
    }

    @Test
    void nutrienteComFaixaNulaOuUnidadeNulaNaoQuebra() {
        CropFoliarAnalysisInterpretationTableLineModel line = CropFoliarAnalysisInterpretationTableLineModel.builder()
                .crop(NomeComum.SOJA)
                .n_content(null)
                .p_content(new MenorMaiorTeores(0.1, 0.3, null))
                .build();
        FertigramModel savedFertigram = FertigramModel.builder().id(1L).foliarAnalysis(analysis).table(table).build();

        when(userRepo.findByUsername("u")).thenReturn(Optional.of(user));
        when(foliarRepo.findById(100L)).thenReturn(Optional.of(analysis));
        when(tableRepo.findById(5L)).thenReturn(Optional.of(table));
        when(lineRepo.findAllByTableOrderByIdAsc(table)).thenReturn(List.of(line));
        when(fertigramRepository.save(any(FertigramModel.class))).thenReturn(savedFertigram);
        when(fertigramNutrientRepository.findAllByFertigramOrderByIdAsc(savedFertigram)).thenReturn(List.of());

        assertDoesNotThrow(() -> service.generate(100L, 5L, "u"));
    }

}
