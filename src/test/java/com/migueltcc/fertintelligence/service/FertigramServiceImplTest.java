package com.migueltcc.fertintelligence.service;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.MenorMaiorTeores;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.MacronutrientsContent;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.MicronutrientsContent;
import com.migueltcc.fertintelligence.dto.fertigram.FertigramResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.FoliarAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableModel;
import com.migueltcc.fertintelligence.repository.CropFoliarAnalysisInterpretationTableLineRepository;
import com.migueltcc.fertintelligence.repository.CropFoliarAnalysisInterpretationTableRepository;
import com.migueltcc.fertintelligence.repository.FoliarAnalysisRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.implementation.FertigramServiceImpl;
import com.migueltcc.fertintelligence.service.implementation.PermissionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
    @InjectMocks FertigramServiceImpl service;

    private UserModel user;
    private FoliarAnalysisModel analysis;
    private CropFoliarAnalysisInterpretationTableModel table;

    @BeforeEach
    void setUp() {
        user = UserModel.builder().id(1L).username("u").build();
        CropModel crop = CropModel.builder().id(10L).name("Soja").build();
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
                .build();

        when(userRepo.findByUsername("u")).thenReturn(Optional.of(user));
        when(foliarRepo.findById(100L)).thenReturn(Optional.of(analysis));
        when(tableRepo.findById(5L)).thenReturn(Optional.of(table));
        when(lineRepo.findAllByTableOrderByIdAsc(table)).thenReturn(List.of(line));

        FertigramResponseDto response = service.generate(100L, 5L, "u");

        assertEquals(4, response.getMacronutrients().size());
        assertEquals(4, response.getMicronutrients().size());
    }

    @Test
    void nutrienteNullNaoApareceNoResponse() {
        when(userRepo.findByUsername("u")).thenReturn(Optional.of(user));
        when(foliarRepo.findById(100L)).thenReturn(Optional.of(analysis));
        when(tableRepo.findById(5L)).thenReturn(Optional.of(table));
        when(lineRepo.findAllByTableOrderByIdAsc(table)).thenReturn(List.of(CropFoliarAnalysisInterpretationTableLineModel.builder().build()));

        FertigramResponseDto response = service.generate(100L, 5L, "u");

        assertTrue(response.getMacronutrients().stream().noneMatch(n -> "Ca".equals(n.getNutrient())));
        assertTrue(response.getMicronutrients().stream().noneMatch(n -> "Cu".equals(n.getNutrient())));
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
        when(userRepo.findByUsername("u")).thenReturn(Optional.of(user));
        when(foliarRepo.findById(100L)).thenReturn(Optional.of(analysis));
        when(tableRepo.findById(5L)).thenReturn(Optional.of(table));
        when(lineRepo.findAllByTableOrderByIdAsc(table)).thenReturn(Collections.emptyList());

        FertigramResponseDto response = service.generate(100L, 5L, "u");

        assertTrue(response.getMacronutrients().isEmpty());
        assertTrue(response.getMicronutrients().isEmpty());
        assertNotNull(response.getWarning());
    }
}
