package com.migueltcc.fertintelligence.controller;

import com.migueltcc.fertintelligence.AbstractControllerTest;
import com.migueltcc.fertintelligence.dto.fertigram.FertigramResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FertigramControllerImplTest extends AbstractControllerTest {

    @Test
    @WithMockUser(username = "testuser")
    void generate_ReturnsOk() throws Exception {
        FertigramResponseDto dto = FertigramResponseDto.builder()
                .id(1L)
                .foliarAnalysisId(100L)
                .tableId(5L)
                .cropName("SOJA")
                .warning(null)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(java.util.Optional.of(
                com.migueltcc.fertintelligence.model.fertintelligence.UserModel.builder().id(1L).username("testuser").build()
        ));
        when(foliarAnalysisRepository.findById(100L)).thenReturn(java.util.Optional.of(
                com.migueltcc.fertintelligence.model.fertintelligence.cropModels.FoliarAnalysisModel.builder()
                        .id(100L)
                        .crop(com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel.builder()
                                .id(10L)
                                .name(com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum.SOJA)
                                .folder(com.migueltcc.fertintelligence.model.fertintelligence.AnnualCropFolderModel.builder()
                                        .plot(com.migueltcc.fertintelligence.model.fertintelligence.PlotModel.builder()
                                                .id(20L)
                                                .property(com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel.builder()
                                                        .id(30L)
                                                        .owner(com.migueltcc.fertintelligence.model.fertintelligence.UserModel.builder().id(1L).build())
                                                        .build())
                                                .build())
                                        .build())
                                .build())
                        .build()
        ));
        when(cropFoliarAnalysisInterpretationTableRepository.findById(5L)).thenReturn(java.util.Optional.of(
                com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableModel.builder()
                        .id(5L)
                        .publicTable(true)
                        .creator(com.migueltcc.fertintelligence.model.fertintelligence.UserModel.builder().id(2L).build())
                        .build()
        ));
        when(cropFoliarAnalysisInterpretationTableLineRepository.findAllByTableOrderByIdAsc(org.mockito.ArgumentMatchers.any())).thenReturn(java.util.Collections.emptyList());
        when(fertigramRepository.save(org.mockito.ArgumentMatchers.any())).thenAnswer(inv -> {
            com.migueltcc.fertintelligence.model.fertintelligence.fertigram.FertigramModel m = inv.getArgument(0);
            m.setId(1L);
            return m;
        });

        mockMvc.perform(get("/fertigram/generate")
                        .param("foliarAnalysisId", "100")
                        .param("tableId", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.cropName").value("SOJA"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void generate_ReturnsForbidden_WhenPrivateTableFromOtherUser() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(java.util.Optional.of(
                com.migueltcc.fertintelligence.model.fertintelligence.UserModel.builder().id(1L).username("testuser").build()
        ));
        when(foliarAnalysisRepository.findById(100L)).thenReturn(java.util.Optional.of(
                com.migueltcc.fertintelligence.model.fertintelligence.cropModels.FoliarAnalysisModel.builder()
                        .id(100L)
                        .crop(com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel.builder()
                                .id(10L)
                                .name(com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum.SOJA)
                                .folder(com.migueltcc.fertintelligence.model.fertintelligence.AnnualCropFolderModel.builder()
                                        .plot(com.migueltcc.fertintelligence.model.fertintelligence.PlotModel.builder()
                                                .id(20L)
                                                .property(com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel.builder()
                                                        .id(30L)
                                                        .owner(com.migueltcc.fertintelligence.model.fertintelligence.UserModel.builder().id(1L).build())
                                                        .build())
                                                .build())
                                        .build())
                                .build())
                        .build()
        ));
        when(cropFoliarAnalysisInterpretationTableRepository.findById(5L)).thenReturn(java.util.Optional.of(
                com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableModel.builder()
                        .id(5L)
                        .publicTable(false)
                        .creator(com.migueltcc.fertintelligence.model.fertintelligence.UserModel.builder().id(999L).build())
                        .build()
        ));

        mockMvc.perform(get("/fertigram/generate")
                        .param("foliarAnalysisId", "100")
                        .param("tableId", "5"))
                .andExpect(status().isForbidden());
    }
}
