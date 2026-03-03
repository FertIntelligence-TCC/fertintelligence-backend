package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.soilAnalysis.SoilAnalysisCreateRequestDto;
import com.migueltcc.fertintelligence.dto.soilAnalysis.SoilAnalysisPostRequestDto;
import com.migueltcc.fertintelligence.dto.soilAnalysis.SoilAnalysisResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.PlotAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.PlotRepository;
import com.migueltcc.fertintelligence.repository.PropertyAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.SoilAnalysisRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.SoilAnalysisService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SoilAnalysisServiceImpl implements SoilAnalysisService {

    private static final Set<Cargo> ALLOWED_ROLES = Set.of(
            Cargo.PROPRIETARIO,
            Cargo.GERENTE,
            Cargo.AGRONOMO_RESIDENTE,
            Cargo.AGRONOMO_CONSULTOR,
            Cargo.SUPERVISOR_DE_AREA,
            Cargo.SECRETARIO
    );

    @Autowired
    private SoilAnalysisRepository soilAnalysisRepository;

    @Autowired
    private PlotRepository plotRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyAccessRequestRepository propertyAccessRequestRepository;

    @Autowired
    private PlotAccessRequestRepository plotAccessRequestRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public SoilAnalysisResponseDto createSoilAnalysis(SoilAnalysisCreateRequestDto dto, String username) {
        UserModel user = findUserByUsernameOrThrow(username);
        PlotModel plot = findPlotByIdOrThrow(dto.getPlotId());

        // CREATE/EDIT: mantém a mesma política que você já vinha usando (ajuste aqui se quiser regra mais rígida)
        assertCanAccessPlot(user, plot, true);

        if (!Objects.equals(plot.getIdentification(), dto.getPlotIdentification())) {
            throw new IllegalArgumentException("A identificação do talhão informada não corresponde ao talhão selecionado.");
        }

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

        // READ: permite por aprovação da PROPRIEDADE também (corrige teu cenário)
        assertCanAccessPlot(user, soilAnalysis.getPlot(), false);

        return soilAnalysis.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SoilAnalysisResponseDto> getAllSoilAnalysesByPlot(Long plotId, String username) {
        UserModel user = findUserByUsernameOrThrow(username);
        PlotModel plot = findPlotByIdOrThrow(plotId);

        // READ: permite por aprovação da PROPRIEDADE também
        assertCanAccessPlot(user, plot, false);

        return soilAnalysisRepository.findAllByPlot(plot).stream()
                .map(SoilAnalysisModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SoilAnalysisResponseDto updateSoilAnalysis(Long soilAnalysisId, SoilAnalysisPostRequestDto dto, String username) {
        UserModel user = findUserByUsernameOrThrow(username);
        SoilAnalysisModel soilAnalysis = findSoilAnalysisByIdOrThrow(soilAnalysisId);

        assertCanAccessPlot(user, soilAnalysis.getPlot(), true);

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

        assertCanAccessPlot(user, soilAnalysis.getPlot(), true);

        // --- DELEÇÃO MANUAL EM CASCATA ---

        // 1) IDs de LayerExtract
        TypedQuery<Long> layerQuery = entityManager.createQuery(
                "SELECT l.id FROM LayerExtractModel l WHERE l.analysis.id = :id", Long.class
        );
        layerQuery.setParameter("id", soilAnalysisId);
        List<Long> layerIds = layerQuery.getResultList();

        // 2) IDs de RangeExtract
        TypedQuery<Long> rangeQuery = entityManager.createQuery(
                "SELECT r.id FROM RangeExtractModel r WHERE r.analysis.id = :id", Long.class
        );
        rangeQuery.setParameter("id", soilAnalysisId);
        List<Long> rangeIds = rangeQuery.getResultList();

        // 3) deletar filhos e extratos
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

        // 4) deletar a análise pai
        soilAnalysisRepository.delete(soilAnalysis);
    }

    private void deleteChildData(List<Long> extractIds, String fieldName) {
        String[] childEntities = {
                "PhysicalAnalysisExtractModel",
                "FertilityAnalysisExtractModel",
                "SaturationExtractAnalysisExtractModel"
        };

        for (String entity : childEntities) {
            String hql = String.format("DELETE FROM %s c WHERE c.%s.id IN :ids", entity, fieldName);
            entityManager.createQuery(hql)
                    .setParameter("ids", extractIds)
                    .executeUpdate();
        }
    }

    /* ======================================================
       Access control (corrigido e padronizado)
    ====================================================== */

    private void assertCanAccessPlot(UserModel user, PlotModel plot, boolean requireEdit) {
        assertAllowedRole(user);

        // regra opcional (igual ao que você fez em Crop): secretário não edita/cria
        if (requireEdit && user.getCargo() == Cargo.SECRETARIO) {
            throw new AccessDeniedException("Secretários não têm permissão para criar, editar ou excluir análises.");
        }

        PropertyModel property = plot.getProperty();

        // Owner
        if (property.getOwner() != null && property.getOwner().getId().equals(user.getId())) {
            return;
        }

        // Manager
        if (property.getManager() != null && property.getManager().getId().equals(user.getId())) {
            return;
        }

        // ✅ CORREÇÃO: se tem aprovação na PROPRIEDADE, pode acessar (ao menos para leitura).
        boolean hasApprovedPropertyAccess = propertyAccessRequestRepository
                .findByPropertyAndRequesterAndStatus(property, user, AccessRequestStatus.APPROVED)
                .isPresent();

        if (hasApprovedPropertyAccess) {
            return;
        }

        // fallback: aprovação específica no TALHÃO
        boolean hasApprovedPlotAccess = plotAccessRequestRepository
                .findByPlotAndRequesterAndStatus(plot, user, AccessRequestStatus.APPROVED)
                .isPresent();

        if (!hasApprovedPlotAccess) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este recurso.");
        }
    }

    private void assertAllowedRole(UserModel user) {
        if (user.getCargo() == null || !ALLOWED_ROLES.contains(user.getCargo())) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este recurso.");
        }
    }

    /* ======================================================
       Finders
    ====================================================== */

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private PlotModel findPlotByIdOrThrow(Long plotId) {
        return plotRepository.findById(plotId)
                .orElseThrow(() -> new EntityNotFoundException("Talhão não encontrado com o ID: " + plotId));
    }

    private SoilAnalysisModel findSoilAnalysisByIdOrThrow(Long id) {
        return soilAnalysisRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Análise de solo não encontrada com o ID: " + id));
    }
}