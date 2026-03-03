package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.dto.soilAnalysis.SoilAnalysisCreateRequestDto;
import com.migueltcc.fertintelligence.dto.soilAnalysis.SoilAnalysisPostRequestDto;
import com.migueltcc.fertintelligence.dto.soilAnalysis.SoilAnalysisResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.PlotRepository;
import com.migueltcc.fertintelligence.repository.SoilAnalysisRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.security.PermissionManager;
import com.migueltcc.fertintelligence.service.documentation.SoilAnalysisService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SoilAnalysisServiceImpl implements SoilAnalysisService {

    private static final List<String> CHILD_ENTITIES = List.of(
            "PhysicalAnalysisExtractModel",
            "FertilityAnalysisExtractModel",
            "SaturationExtractAnalysisExtractModel"
    );

    private final SoilAnalysisRepository soilAnalysisRepository;
    private final PlotRepository plotRepository;
    private final UserRepository userRepository;
    private final PermissionManager permissionManager;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public SoilAnalysisResponseDto createSoilAnalysis(SoilAnalysisCreateRequestDto dto, String username) {
        UserModel user = findUserByUsernameOrThrow(username);

        PlotModel plot = findPlotByIdOrThrow(dto.getPlotId());
        permissionManager.assertCanWrite(plot, user);

        validatePlotIdentification(plot, dto.getPlotIdentification());

        SoilAnalysisModel soilAnalysis = SoilAnalysisModel.builder()
                .analysisYear(dto.getAnalysisYear())
                .responsibleLaboratory(dto.getResponsibleLaboratory())
                .extractType(dto.getExtractType())
                .plot(plot)
                .build();

        return soilAnalysisRepository.save(soilAnalysis).toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public SoilAnalysisResponseDto getSoilAnalysisById(Long soilAnalysisId, String username) {
        UserModel user = findUserByUsernameOrThrow(username);

        SoilAnalysisModel soilAnalysis = findSoilAnalysisByIdOrThrow(soilAnalysisId);
        permissionManager.assertCanRead(soilAnalysis.getPlot(), user);

        return soilAnalysis.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SoilAnalysisResponseDto> getAllSoilAnalysesByPlot(Long plotId, String username) {
        UserModel user = findUserByUsernameOrThrow(username);

        PlotModel plot = findPlotByIdOrThrow(plotId);
        permissionManager.assertCanRead(plot, user);

        return soilAnalysisRepository.findAllByPlot(plot)
                .stream()
                .map(SoilAnalysisModel::toDto)
                .toList();
    }

    @Override
    @Transactional
    public SoilAnalysisResponseDto updateSoilAnalysis(Long soilAnalysisId, SoilAnalysisPostRequestDto dto, String username) {
        UserModel user = findUserByUsernameOrThrow(username);

        SoilAnalysisModel soilAnalysis = findSoilAnalysisByIdOrThrow(soilAnalysisId);
        permissionManager.assertCanWrite(soilAnalysis.getPlot(), user);

        if (dto.getAnalysisYear() != null) {
            soilAnalysis.setAnalysisYear(dto.getAnalysisYear());
        }
        if (dto.getResponsibleLaboratory() != null && !dto.getResponsibleLaboratory().isBlank()) {
            soilAnalysis.setResponsibleLaboratory(dto.getResponsibleLaboratory());
        }
        if (dto.getExtractType() != null) {
            soilAnalysis.setExtractType(dto.getExtractType());
        }

        return soilAnalysisRepository.save(soilAnalysis).toDto();
    }

    @Override
    @Transactional
    public void deleteSoilAnalysis(Long soilAnalysisId, String username) {
        UserModel user = findUserByUsernameOrThrow(username);

        SoilAnalysisModel soilAnalysis = findSoilAnalysisByIdOrThrow(soilAnalysisId);
        permissionManager.assertCanWrite(soilAnalysis.getPlot(), user);

        // --- DELEÇÃO MANUAL EM CASCATA (mantida) ---
        List<Long> layerIds = entityManager.createQuery(
                        "SELECT l.id FROM LayerExtractModel l WHERE l.analysis.id = :id", Long.class)
                .setParameter("id", soilAnalysisId)
                .getResultList();

        List<Long> rangeIds = entityManager.createQuery(
                        "SELECT r.id FROM RangeExtractModel r WHERE r.analysis.id = :id", Long.class)
                .setParameter("id", soilAnalysisId)
                .getResultList();

        if (!layerIds.isEmpty()) {
            deleteChildData(layerIds, "layerExtract");
            entityManager.createQuery("DELETE FROM LayerExtractModel l WHERE l.id IN :ids")
                    .setParameter("ids", layerIds)
                    .executeUpdate();
        }

        if (!rangeIds.isEmpty()) {
            deleteChildData(rangeIds, "rangeExtract");
            entityManager.createQuery("DELETE FROM RangeExtractModel r WHERE r.id IN :ids")
                    .setParameter("ids", rangeIds)
                    .executeUpdate();
        }

        soilAnalysisRepository.delete(soilAnalysis);
    }

    private void deleteChildData(List<Long> extractIds, String fieldName) {
        for (String entity : CHILD_ENTITIES) {
            String hql = "DELETE FROM " + entity + " c WHERE c." + fieldName + ".id IN :ids";
            entityManager.createQuery(hql)
                    .setParameter("ids", extractIds)
                    .executeUpdate();
        }
    }

    private void validatePlotIdentification(PlotModel plot, String providedIdentification) {
        if (providedIdentification == null || !plot.getIdentification().equals(providedIdentification)) {
            throw new IllegalArgumentException("A identificação do talhão informada não corresponde ao talhão selecionado.");
        }
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private PlotModel findPlotByIdOrThrow(Long plotId) {
        return plotRepository.findById(plotId)
                .orElseThrow(() -> new EntityNotFoundException("Talhão não encontrado com o ID: " + plotId));
    }

    private SoilAnalysisModel findSoilAnalysisByIdOrThrow(Long soilAnalysisId) {
        return soilAnalysisRepository.findById(soilAnalysisId)
                .orElseThrow(() -> new EntityNotFoundException("Análise de solo não encontrada com o ID: " + soilAnalysisId));
    }
}