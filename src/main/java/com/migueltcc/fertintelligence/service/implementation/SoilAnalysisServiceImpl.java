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
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
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
import java.util.stream.Collectors;

@Service
public class SoilAnalysisServiceImpl implements SoilAnalysisService {

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
    public SoilAnalysisResponseDto createSoilAnalysis(SoilAnalysisCreateRequestDto createRequestDto, String username) {
        UserModel requestingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        PlotModel plot = plotRepository.findById(createRequestDto.getPlotId())
                .orElseThrow(() -> new EntityNotFoundException("Talhão não encontrado."));

        validateUserAccess(requestingUser, plot);

        if (!plot.getIdentification().equals(createRequestDto.getPlotIdentification())) {
            throw new IllegalArgumentException("A identificação do talhão informada não corresponde ao talhão selecionado.");
        }

        SoilAnalysisModel soilAnalysis = SoilAnalysisModel.builder()
                .analysisYear(createRequestDto.getAnalysisYear())
                .responsibleLaboratory(createRequestDto.getResponsibleLaboratory())
                .extractType(createRequestDto.getExtractType())
                .plot(plot)
                .build();

        return soilAnalysisRepository.save(soilAnalysis).toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public SoilAnalysisResponseDto getSoilAnalysisById(Long soilAnalysisId, String username) {
        UserModel requestingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        SoilAnalysisModel soilAnalysis = soilAnalysisRepository.findById(soilAnalysisId)
                .orElseThrow(() -> new EntityNotFoundException("Análise de solo não encontrada."));

        validateUserAccess(requestingUser, soilAnalysis.getPlot());

        return soilAnalysis.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SoilAnalysisResponseDto> getAllSoilAnalysesByPlot(Long plotId, String username) {
        UserModel requestingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        PlotModel plot = plotRepository.findById(plotId)
                .orElseThrow(() -> new EntityNotFoundException("Talhão não encontrado."));

        validateUserAccess(requestingUser, plot);

        return soilAnalysisRepository.findAllByPlot(plot).stream()
                .map(SoilAnalysisModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SoilAnalysisResponseDto updateSoilAnalysis(Long soilAnalysisId, SoilAnalysisPostRequestDto updateRequestDto, String username) {
        UserModel requestingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        SoilAnalysisModel soilAnalysis = soilAnalysisRepository.findById(soilAnalysisId)
                .orElseThrow(() -> new EntityNotFoundException("Análise de solo não encontrada."));

        validateUserAccess(requestingUser, soilAnalysis.getPlot());

        if (updateRequestDto.getAnalysisYear() != null) {
            soilAnalysis.setAnalysisYear(updateRequestDto.getAnalysisYear());
        }
        if (updateRequestDto.getResponsibleLaboratory() != null && !updateRequestDto.getResponsibleLaboratory().isBlank()) {
            soilAnalysis.setResponsibleLaboratory(updateRequestDto.getResponsibleLaboratory());
        }
        if (updateRequestDto.getExtractType() != null) {
            soilAnalysis.setExtractType(updateRequestDto.getExtractType());
        }

        return soilAnalysisRepository.save(soilAnalysis).toDto();
    }

    @Override
    @Transactional
    public void deleteSoilAnalysis(Long soilAnalysisId, String username) {
        UserModel requestingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        SoilAnalysisModel soilAnalysis = soilAnalysisRepository.findById(soilAnalysisId)
                .orElseThrow(() -> new EntityNotFoundException("Análise de solo não encontrada."));

        validateUserAccess(requestingUser, soilAnalysis.getPlot());

        // --- DELEÇÃO MANUAL EM CASCATA ---

        // 1. Buscar IDs dos Extratos de Camada vinculados
        TypedQuery<Long> layerQuery = entityManager.createQuery(
                "SELECT l.id FROM LayerExtractModel l WHERE l.analysis.id = :id", Long.class);
        layerQuery.setParameter("id", soilAnalysisId);
        List<Long> layerIds = layerQuery.getResultList();

        // 2. Buscar IDs dos Extratos de Intervalo vinculados
        TypedQuery<Long> rangeQuery = entityManager.createQuery(
                "SELECT r.id FROM RangeExtractModel r WHERE r.analysis.id = :id", Long.class);
        rangeQuery.setParameter("id", soilAnalysisId);
        List<Long> rangeIds = rangeQuery.getResultList();

        // 3. Deletar dados filhos (Physical/Fertility/Saturation) baseados nos IDs encontrados
        if (!layerIds.isEmpty()) {
            deleteChildData(layerIds, "layerExtract");
            // Deletar os extratos de camada
            entityManager.createQuery("DELETE FROM LayerExtractModel l WHERE l.id IN :ids")
                    .setParameter("ids", layerIds).executeUpdate();
        }

        if (!rangeIds.isEmpty()) {
            deleteChildData(rangeIds, "rangeExtract");
            // Deletar os extratos de intervalo
            entityManager.createQuery("DELETE FROM RangeExtractModel r WHERE r.id IN :ids")
                    .setParameter("ids", rangeIds).executeUpdate();
        }

        // 4. Finalmente, deletar a Análise Pai
        soilAnalysisRepository.delete(soilAnalysis);
    }

    // Método auxiliar para deletar das tabelas finais
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

    private void validateUserAccess(UserModel requestingUser, PlotModel plot) {
        if (requestingUser.getCargo() != Cargo.PROPRIETARIO
                && requestingUser.getCargo() != Cargo.GERENTE
                && requestingUser.getCargo() != Cargo.AGRONOMO_RESIDENTE
                && requestingUser.getCargo() != Cargo.AGRONOMO_CONSULTOR
                && requestingUser.getCargo() != Cargo.SECRETARIO) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este recurso.");
        }

        PropertyModel property = plot.getProperty();

        if (property.getOwner().getId().equals(requestingUser.getId())) {
            return;
        }

        if (property.getManager() != null && property.getManager().getId().equals(requestingUser.getId())) {
            return;
        }

        if (requestingUser.getCargo() == Cargo.AGRONOMO_RESIDENTE) {
            boolean hasPropertyApproval = propertyAccessRequestRepository.findByPropertyAndRequesterAndStatus(
                    property,
                    requestingUser,
                    AccessRequestStatus.APPROVED
            ).isPresent();

            if (!hasPropertyApproval) {
                throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este recurso.");
            }
            return;
        }

        boolean hasPlotApproval = plotAccessRequestRepository.findByPlotAndRequesterAndStatus(
                plot,
                requestingUser,
                AccessRequestStatus.APPROVED
        ).isPresent();

        if (!hasPlotApproval) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este recurso.");
        }
    }
}