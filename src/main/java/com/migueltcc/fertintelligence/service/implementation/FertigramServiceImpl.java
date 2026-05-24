package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.MenorMaiorTeores;
import com.migueltcc.fertintelligence.dto.fertigram.FertigramNutrientDto;
import com.migueltcc.fertintelligence.dto.fertigram.FertigramResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.FoliarAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableModel;
import com.migueltcc.fertintelligence.repository.CropFoliarAnalysisInterpretationTableLineRepository;
import com.migueltcc.fertintelligence.repository.CropFoliarAnalysisInterpretationTableRepository;
import com.migueltcc.fertintelligence.repository.FoliarAnalysisRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.FertigramService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FertigramServiceImpl implements FertigramService {

    private final FoliarAnalysisRepository foliarAnalysisRepository;
    private final CropFoliarAnalysisInterpretationTableRepository tableRepository;
    private final CropFoliarAnalysisInterpretationTableLineRepository tableLineRepository;
    private final UserRepository userRepository;
    private final PermissionManager permissionManager;

    @Override
    @Transactional(readOnly = true)
    public FertigramResponseDto generate(Long foliarAnalysisId, Long tableId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);
        FoliarAnalysisModel analysis = foliarAnalysisRepository.findById(foliarAnalysisId)
                .orElseThrow(() -> new EntityNotFoundException("Análise foliar não encontrada com o ID: " + foliarAnalysisId));

        permissionManager.assertCanReadPlot(analysis.getCrop().getFolder().getPlot(), requester);

        CropFoliarAnalysisInterpretationTableModel table = tableRepository.findById(tableId)
                .orElseThrow(() -> new EntityNotFoundException("Tabela de interpretação de análise foliar não encontrada com o ID: " + tableId));

        if (!table.isPublicTable() && !table.getCreator().getId().equals(requester.getId())) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar esta tabela.");
        }

        List<CropFoliarAnalysisInterpretationTableLineModel> lines = tableLineRepository.findAllByTableOrderByIdAsc(table);
        if (lines.isEmpty()) {
            return FertigramResponseDto.builder()
                    .foliarAnalysisId(analysis.getId())
                    .tableId(table.getId())
                    .cropName(analysis.getCrop().getName())
                    .warning("Tabela de interpretação sem linhas cadastradas.")
                    .build();
        }

        CropFoliarAnalysisInterpretationTableLineModel line = lines.get(0);

        List<FertigramNutrientDto> macros = new ArrayList<>();
        List<FertigramNutrientDto> micros = new ArrayList<>();

        if (analysis.getMacronutrients() != null) {
            addNutrient(macros, "N", analysis.getMacronutrients().getN_content(), line.getN_content());
            addNutrient(macros, "P", analysis.getMacronutrients().getP_content(), line.getP_content());
            addNutrient(macros, "K", analysis.getMacronutrients().getK_content(), line.getK_content());
            addNutrient(macros, "Ca", analysis.getMacronutrients().getCa_content(), line.getCa_content());
            addNutrient(macros, "Mg", analysis.getMacronutrients().getMg_content(), line.getMg_content());
            addNutrient(macros, "S", analysis.getMacronutrients().getS_content(), line.getS_content());
        }

        if (analysis.getMicronutrients() != null) {
            addNutrient(micros, "B", analysis.getMicronutrients().getB_content(), line.getB_content());
            addNutrient(micros, "Cu", analysis.getMicronutrients().getCu_content(), line.getCu_content());
            addNutrient(micros, "Fe", analysis.getMicronutrients().getFe_content(), line.getFe_content());
            addNutrient(micros, "Mn", analysis.getMicronutrients().getMn_content(), line.getMn_content());
            addNutrient(micros, "Mo", analysis.getMicronutrients().getMo_content(), line.getMo_content());
            addNutrient(micros, "Zn", analysis.getMicronutrients().getZn_content(), line.getZn_content());
        }

        return FertigramResponseDto.builder()
                .foliarAnalysisId(analysis.getId())
                .tableId(table.getId())
                .cropName(analysis.getCrop().getName())
                .macronutrients(macros)
                .micronutrients(micros)
                .build();
    }

    private void addNutrient(List<FertigramNutrientDto> list, String nutrient, Double measured, MenorMaiorTeores range) {
        if (measured == null) return;

        Double min = range != null ? range.getMenor() : null;
        Double max = range != null ? range.getMaior() : null;

        list.add(FertigramNutrientDto.builder()
                .nutrient(nutrient)
                .measuredValue(measured)
                .recommendedMinimum(min)
                .recommendedMaximum(max)
                .unit(range != null ? range.getUnity() : null)
                .interpretation(interpret(measured, min, max))
                .build());
    }

    private String interpret(Double measured, Double min, Double max) {
        if (min == null || max == null) return "SEM_FAIXA";
        if (measured < min) return "ABAIXO";
        if (measured > max) return "ACIMA";
        return "ADEQUADO";
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }
}
