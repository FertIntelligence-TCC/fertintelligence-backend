package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.User.Cargo;
import com.migueltcc.fertintelligence.composedAttributes.SoilExtracts.TipoExtrato;
import com.migueltcc.fertintelligence.dto.extract.layer.LayerExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extract.layer.LayerExtractPostRequestDto;
import com.migueltcc.fertintelligence.dto.extract.layer.LayerExtractResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.LayerExtractRepository;
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
    private UserRepository userRepository;

    @Override
    @Transactional
    public LayerExtractResponseDto createLayerExtract(Long analysisId, LayerExtractCreateRequestDto createRequestDto, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        SoilAnalysisModel analysis = findAnalysisByIdOrThrow(analysisId);
        checkOwnerPermission(analysis.getPlot(), owner);
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
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        LayerExtractModel layerExtract = findLayerExtractByIdOrThrow(layerExtractId);
        checkOwnerPermission(layerExtract.getAnalysis().getPlot(), owner);
        ensureAnalysisSupportsLayers(layerExtract.getAnalysis());

        return layerExtract.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LayerExtractResponseDto> getAllLayerExtractsByAnalysis(Long analysisId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        SoilAnalysisModel analysis = findAnalysisByIdOrThrow(analysisId);
        checkOwnerPermission(analysis.getPlot(), owner);
        ensureAnalysisSupportsLayers(analysis);

        return layerExtractRepository.findAllByAnalysis(analysis).stream()
                .map(LayerExtractModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LayerExtractResponseDto updateLayerExtract(Long layerExtractId, LayerExtractPostRequestDto updateRequestDto, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        LayerExtractModel layerExtract = findLayerExtractByIdOrThrow(layerExtractId);
        SoilAnalysisModel analysis = layerExtract.getAnalysis();
        checkOwnerPermission(analysis.getPlot(), owner);
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
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        LayerExtractModel layerExtract = findLayerExtractByIdOrThrow(layerExtractId);
        checkOwnerPermission(layerExtract.getAnalysis().getPlot(), owner);
        ensureAnalysisSupportsLayers(layerExtract.getAnalysis());

        layerExtractRepository.delete(layerExtract);
    }

    private void validateDepthRange(Integer initialDepth, Integer finalDepth) {
        if (initialDepth == null || finalDepth == null) {
            return;
        }

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

    private void checkUserIsProprietario(UserModel user) {
        if (user.getCargo() != Cargo.PROPRIETARIO) {
            throw new AccessDeniedException("Acesso negado. Apenas usuários com o cargo 'PROPRIETARIO' podem gerenciar extratos por camada.");
        }
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private SoilAnalysisModel findAnalysisByIdOrThrow(Long analysisId) {
        return soilAnalysisRepository.findById(analysisId)
                .orElseThrow(() -> new EntityNotFoundException("Análise de solo não encontrada com o ID: " + analysisId));
    }

    private LayerExtractModel findLayerExtractByIdOrThrow(Long layerExtractId) {
        return layerExtractRepository.findById(layerExtractId)
                .orElseThrow(() -> new EntityNotFoundException("Extrato de camada não encontrado com o ID: " + layerExtractId));
    }

    private void checkOwnerPermission(PlotModel plot, UserModel requestingUser) {
        PropertyModel property = plot.getProperty();
        if (!property.getOwner().getId().equals(requestingUser.getId())) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este recurso.");
        }
    }
}