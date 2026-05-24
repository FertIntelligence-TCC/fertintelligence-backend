package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.MenorMaiorTeores;
import com.migueltcc.fertintelligence.dto.fertigram.FertigramNutrientDto;
import com.migueltcc.fertintelligence.dto.fertigram.FertigramResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.FoliarAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertigram.FertigramModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertigram.FertigramNutrientGroupType;
import com.migueltcc.fertintelligence.model.fertintelligence.fertigram.FertigramNutrientModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableModel;
import com.migueltcc.fertintelligence.repository.*;
import com.migueltcc.fertintelligence.service.documentation.FertigramService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FertigramServiceImpl implements FertigramService {

    private final FoliarAnalysisRepository foliarAnalysisRepository;
    private final CropFoliarAnalysisInterpretationTableRepository tableRepository;
    private final CropFoliarAnalysisInterpretationTableLineRepository tableLineRepository;
    private final UserRepository userRepository;
    private final FertigramRepository fertigramRepository;
    private final FertigramNutrientRepository fertigramNutrientRepository;
    private final PermissionManager permissionManager;

    @Override
    @Transactional
    public FertigramResponseDto generate(Long foliarAnalysisId, Long tableId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);
        FoliarAnalysisModel analysis = foliarAnalysisRepository.findById(foliarAnalysisId)
                .orElseThrow(() -> new EntityNotFoundException("Análise foliar não encontrada com o ID: " + foliarAnalysisId));

        if (analysis.getCrop() == null || analysis.getCrop().getFolder() == null || analysis.getCrop().getFolder().getPlot() == null) {
            throw new EntityNotFoundException("A análise foliar informada não possui vínculo válido com a área (plot).");
        }

        permissionManager.assertCanReadPlot(analysis.getCrop().getFolder().getPlot(), requester);

        CropFoliarAnalysisInterpretationTableModel table = tableRepository.findById(tableId)
                .orElseThrow(() -> new EntityNotFoundException("Tabela de interpretação de análise foliar não encontrada com o ID: " + tableId));

        if (!table.isPublicTable() && (table.getCreator() == null || !table.getCreator().getId().equals(requester.getId()))) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar esta tabela.");
        }

        FertigramModel fertigram = fertigramRepository.save(FertigramModel.builder()
                .foliarAnalysis(analysis)
                .table(table)
                .build());

        List<CropFoliarAnalysisInterpretationTableLineModel> lines = tableLineRepository.findAllByTableOrderByIdAsc(table);
        if (lines.isEmpty()) {
            fertigram.setWarning("Tabela de interpretação sem linhas cadastradas.");
            fertigram = fertigramRepository.save(fertigram);
            return buildResponse(fertigram, List.of());
        }

        CropFoliarAnalysisInterpretationTableLineModel line = resolveLineForAnalysis(lines, analysis)
                .orElse(lines.get(0));

        if (analysis.getMacronutrients() != null) {
            saveNutrient(fertigram, "N", FertigramNutrientGroupType.MACRO, analysis.getMacronutrients().getN_content(), line.getN_content());
            saveNutrient(fertigram, "P", FertigramNutrientGroupType.MACRO, analysis.getMacronutrients().getP_content(), line.getP_content());
            saveNutrient(fertigram, "K", FertigramNutrientGroupType.MACRO, analysis.getMacronutrients().getK_content(), line.getK_content());
            saveNutrient(fertigram, "Ca", FertigramNutrientGroupType.MACRO, analysis.getMacronutrients().getCa_content(), line.getCa_content());
            saveNutrient(fertigram, "Mg", FertigramNutrientGroupType.MACRO, analysis.getMacronutrients().getMg_content(), line.getMg_content());
            saveNutrient(fertigram, "S", FertigramNutrientGroupType.MACRO, analysis.getMacronutrients().getS_content(), line.getS_content());
        }

        if (analysis.getMicronutrients() != null) {
            saveNutrient(fertigram, "B", FertigramNutrientGroupType.MICRO, analysis.getMicronutrients().getB_content(), line.getB_content());
            saveNutrient(fertigram, "Cu", FertigramNutrientGroupType.MICRO, analysis.getMicronutrients().getCu_content(), line.getCu_content());
            saveNutrient(fertigram, "Fe", FertigramNutrientGroupType.MICRO, analysis.getMicronutrients().getFe_content(), line.getFe_content());
            saveNutrient(fertigram, "Mn", FertigramNutrientGroupType.MICRO, analysis.getMicronutrients().getMn_content(), line.getMn_content());
            saveNutrient(fertigram, "Mo", FertigramNutrientGroupType.MICRO, analysis.getMicronutrients().getMo_content(), line.getMo_content());
            saveNutrient(fertigram, "Zn", FertigramNutrientGroupType.MICRO, analysis.getMicronutrients().getZn_content(), line.getZn_content());
        }

        List<FertigramNutrientModel> nutrients = fertigramNutrientRepository.findAllByFertigramOrderByIdAsc(fertigram);
        return buildResponse(fertigram, nutrients);
    }

    private FertigramResponseDto buildResponse(FertigramModel fertigram, List<FertigramNutrientModel> nutrients) {
        FertigramResponseDto response = FertigramResponseDto.builder()
                .id(fertigram.getId())
                .foliarAnalysisId(fertigram.getFoliarAnalysis() != null ? fertigram.getFoliarAnalysis().getId() : null)
                .tableId(fertigram.getTable() != null ? fertigram.getTable().getId() : null)
                .cropName(
                        fertigram.getFoliarAnalysis() != null
                                && fertigram.getFoliarAnalysis().getCrop() != null
                                && fertigram.getFoliarAnalysis().getCrop().getName() != null
                                ? fertigram.getFoliarAnalysis().getCrop().getName().name()
                                : null
                )
                .warning(fertigram.getWarning())
                .build();

        for (FertigramNutrientModel nutrient : nutrients) {
            FertigramNutrientDto dto = nutrient.toDto();
            if (nutrient.getGroupType() == FertigramNutrientGroupType.MACRO) {
                response.getMacronutrients().add(dto);
            } else if (nutrient.getGroupType() == FertigramNutrientGroupType.MICRO) {
                response.getMicronutrients().add(dto);
            }
        }

        return response;
    }

    private Optional<CropFoliarAnalysisInterpretationTableLineModel> resolveLineForAnalysis(
            List<CropFoliarAnalysisInterpretationTableLineModel> lines,
            FoliarAnalysisModel analysis
    ) {
        if (analysis.getCrop() == null || analysis.getCrop().getName() == null) {
            return Optional.empty();
        }

        return lines.stream()
                .filter(line -> line.getCrop() != null && line.getCrop().equals(analysis.getCrop().getName()))
                .findFirst();
    }

    private void saveNutrient(FertigramModel fertigram, String nutrient, FertigramNutrientGroupType group, Double measured, MenorMaiorTeores range) {
        if (measured == null) return;
        Double min = range != null ? range.getMenor() : null;
        Double max = range != null ? range.getMaior() : null;

        fertigramNutrientRepository.save(FertigramNutrientModel.builder()
                .fertigram(fertigram)
                .nutrient(nutrient)
                .groupType(group)
                .measuredValue(measured)
                .recommendedMinimum(min)
                .recommendedMaximum(max)
                .unit(range != null && range.getUnity() != null ? range.getUnity().name() : null)
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
