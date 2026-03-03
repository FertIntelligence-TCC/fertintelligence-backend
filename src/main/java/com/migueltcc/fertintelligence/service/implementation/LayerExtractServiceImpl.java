package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.soilExtracts.TipoExtrato;
import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.extract.layer.LayerExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extract.layer.LayerExtractPostRequestDto;
import com.migueltcc.fertintelligence.dto.extract.layer.LayerExtractResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.repository.LayerExtractRepository;
import com.migueltcc.fertintelligence.repository.PropertyAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.SoilAnalysisRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.LayerExtractService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LayerExtractServiceImpl implements LayerExtractService {

    @Autowired
    private LayerExtractRepository layerExtractRepository;

    @Autowired
    private SoilAnalysisRepository soilAnalysisRepository;

    @Autowired
    private PropertyAccessRequestRepository propertyAccessRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public LayerExtractResponseDto createLayerExtract(
            Long analysisId,
            LayerExtractCreateRequestDto createRequestDto,
            String username
    ) {
        UserModel requester = findUserByUsernameOrThrow(username);

        SoilAnalysisModel analysis = findAnalysisByIdOrThrow(analysisId);

        checkPermission(analysis.getPlot(), requester, true);
        ensureAnalysisSupportsLayers(analysis);

        validateDepthRange(createRequestDto.getInitialDepth(), createRequestDto.getFinalDepth());

        LayerExtractModel layerExtract = LayerExtractModel.builder()
                .analysis(analysis)
                .profundidade_inicial(createRequestDto.getInitialDepth())
                .profundidade_final(createRequestDto.getFinalDepth())
                .layer(createRequestDto.getLayer())
                .sub_layer(createRequestDto.getSubLayer())
                .build();

        LayerExtractModel savedExtract = layerExtractRepository.save(layerExtract);
        return savedExtract.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public LayerExtractResponseDto getLayerExtractById(Long layerExtractId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        LayerExtractModel layerExtract = findLayerExtractByIdOrThrow(layerExtractId);
        SoilAnalysisModel analysis = layerExtract.getAnalysis();

        checkPermission(analysis.getPlot(), requester, false);
        ensureAnalysisSupportsLayers(analysis);

        return layerExtract.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LayerExtractResponseDto> getAllLayerExtractsByAnalysis(Long analysisId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        SoilAnalysisModel analysis = findAnalysisByIdOrThrow(analysisId);

        checkPermission(analysis.getPlot(), requester, false);
        ensureAnalysisSupportsLayers(analysis);

        return layerExtractRepository.findAllByAnalysis(analysis).stream()
                .map(LayerExtractModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LayerExtractResponseDto updateLayerExtract(
            Long layerExtractId,
            LayerExtractPostRequestDto updateRequestDto,
            String username
    ) {
        UserModel requester = findUserByUsernameOrThrow(username);

        LayerExtractModel layerExtract = findLayerExtractByIdOrThrow(layerExtractId);
        SoilAnalysisModel analysis = layerExtract.getAnalysis();

        checkPermission(analysis.getPlot(), requester, true);
        ensureAnalysisSupportsLayers(analysis);

        Integer updatedInitialDepth = updateRequestDto.getInitialDepth() != null
                ? updateRequestDto.getInitialDepth()
                : layerExtract.getProfundidade_inicial();

        Integer updatedFinalDepth = updateRequestDto.getFinalDepth() != null
                ? updateRequestDto.getFinalDepth()
                : layerExtract.getProfundidade_final();

        validateDepthRange(updatedInitialDepth, updatedFinalDepth);

        layerExtract.setProfundidade_inicial(updatedInitialDepth);
        layerExtract.setProfundidade_final(updatedFinalDepth);

        if (updateRequestDto.getLayer() != null) {
            layerExtract.setLayer(updateRequestDto.getLayer());
        }

        if (updateRequestDto.getSubLayer() != null) {
            layerExtract.setSub_layer(updateRequestDto.getSubLayer());
        }

        LayerExtractModel updatedExtract = layerExtractRepository.save(layerExtract);
        return updatedExtract.toDto();
    }

    @Override
    @Transactional
    public void deleteLayerExtract(Long layerExtractId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        LayerExtractModel layerExtract = findLayerExtractByIdOrThrow(layerExtractId);
        SoilAnalysisModel analysis = layerExtract.getAnalysis();

        checkPermission(analysis.getPlot(), requester, true);
        ensureAnalysisSupportsLayers(analysis);

        layerExtractRepository.delete(layerExtract);
    }

    /* =========================
       Permission rules
       ========================= */

    private void checkPermission(PlotModel plot, UserModel requester, boolean requireEdit) {
        checkUserHasAllowedRole(requester);

        PropertyModel property = plot.getProperty();

        // Dono
        if (property.getOwner().getId().equals(requester.getId())) {
            return;
        }

        // Gerente
        if (property.getManager() != null && property.getManager().getId().equals(requester.getId())) {
            return;
        }

        // Precisa ter aprovação na propriedade para acessar (read/edit)
        boolean hasPropertyApproval = propertyAccessRequestRepository.findByPropertyAndRequesterAndStatus(
                property,
                requester,
                AccessRequestStatus.APPROVED
        ).isPresent();

        if (!hasPropertyApproval) {
            throw new AccessDeniedException("Você não tem permissão para acessar este recurso.");
        }

        // Leitura: aprovação na propriedade basta
        if (!requireEdit) {
            return;
        }

        // Escrita: regra atual (ajuste aqui se quiser abrir para outros cargos)
        if (requester.getCargo() == Cargo.AGRONOMO_RESIDENTE) {
            return;
        }

        throw new AccessDeniedException("Você não tem permissão para modificar este recurso.");
    }

    private void checkUserHasAllowedRole(UserModel requester) {
        if (requester.getCargo() != Cargo.PROPRIETARIO
                && requester.getCargo() != Cargo.GERENTE
                && requester.getCargo() != Cargo.AGRONOMO_RESIDENTE
                && requester.getCargo() != Cargo.AGRONOMO_CONSULTOR
                && requester.getCargo() != Cargo.SUPERVISOR_DE_AREA
                && requester.getCargo() != Cargo.SECRETARIO) {
            throw new AccessDeniedException("Você não tem permissão para acessar este recurso.");
        }
    }

    /* =========================
       Business validations
       ========================= */

    private void validateDepthRange(Integer initialDepth, Integer finalDepth) {
        if (initialDepth == null || finalDepth == null) return;

        if (initialDepth < 0 || finalDepth < 0) {
            throw new IllegalArgumentException("As profundidades devem ser valores não negativos.");
        }

        if (initialDepth >= finalDepth) {
            throw new IllegalArgumentException("A profundidade inicial deve ser menor que a profundidade final.");
        }
    }

    private void ensureAnalysisSupportsLayers(SoilAnalysisModel analysis) {
        if (analysis.getExtractType() != TipoExtrato.CAMADAS) {
            throw new IllegalArgumentException("A análise de solo selecionada não permite extratos por camada.");
        }
    }

    /* =========================
       Finders
       ========================= */

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private SoilAnalysisModel findAnalysisByIdOrThrow(Long analysisId) {
        return soilAnalysisRepository.findById(analysisId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Análise de solo não encontrada com o ID: " + analysisId
                ));
    }

    private LayerExtractModel findLayerExtractByIdOrThrow(Long layerExtractId) {
        return layerExtractRepository.findById(layerExtractId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Extrato de camada não encontrado com o ID: " + layerExtractId
                ));
    }
}