package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.SoilExtracts.TipoExtrato;
import com.migueltcc.fertintelligence.composedAttributes.User.Cargo;
import com.migueltcc.fertintelligence.dto.extract.range.RangeExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extract.range.RangeExtractPostRequestDto;
import com.migueltcc.fertintelligence.dto.extract.range.RangeExtractResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.RangeExtractRepository;
import com.migueltcc.fertintelligence.repository.SoilAnalysisRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.RangeExtractService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RangeExtractServiceImpl implements RangeExtractService {

    @Autowired
    private RangeExtractRepository rangeExtractRepository;

    @Autowired
    private SoilAnalysisRepository soilAnalysisRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public RangeExtractResponseDto createRangeExtract(Long analysisId, RangeExtractCreateRequestDto createRequestDto, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        SoilAnalysisModel analysis = findAnalysisByIdOrThrow(analysisId);
        checkOwnerPermission(analysis.getPlot(), owner);
        ensureAnalysisSupportsRanges(analysis);

        validateDepthRange(createRequestDto.getInitialDepth(), createRequestDto.getFinalDepth());

        RangeExtractModel rangeExtract = RangeExtractModel.builder()
                .analysis(analysis)
                .profundidade_inicial(createRequestDto.getInitialDepth())
                .profundidade_final(createRequestDto.getFinalDepth())
                .build();

        RangeExtractModel savedExtract = rangeExtractRepository.save(rangeExtract);
        return savedExtract.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public RangeExtractResponseDto getRangeExtractById(Long rangeExtractId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        RangeExtractModel rangeExtract = findRangeExtractByIdOrThrow(rangeExtractId);
        checkOwnerPermission(rangeExtract.getAnalysis().getPlot(), owner);
        ensureAnalysisSupportsRanges(rangeExtract.getAnalysis());

        return rangeExtract.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RangeExtractResponseDto> getAllRangeExtractsByAnalysis(Long analysisId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        SoilAnalysisModel analysis = findAnalysisByIdOrThrow(analysisId);
        checkOwnerPermission(analysis.getPlot(), owner);
        ensureAnalysisSupportsRanges(analysis);

        return rangeExtractRepository.findAllByAnalysis(analysis).stream()
                .map(RangeExtractModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RangeExtractResponseDto updateRangeExtract(Long rangeExtractId, RangeExtractPostRequestDto updateRequestDto, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        RangeExtractModel rangeExtract = findRangeExtractByIdOrThrow(rangeExtractId);
        SoilAnalysisModel analysis = rangeExtract.getAnalysis();
        checkOwnerPermission(analysis.getPlot(), owner);
        ensureAnalysisSupportsRanges(analysis);

        Integer updatedInitialDepth = updateRequestDto.getInitialDepth() != null
                ? updateRequestDto.getInitialDepth()
                : rangeExtract.getProfundidade_inicial();
        Integer updatedFinalDepth = updateRequestDto.getFinalDepth() != null
                ? updateRequestDto.getFinalDepth()
                : rangeExtract.getProfundidade_final();

        validateDepthRange(updatedInitialDepth, updatedFinalDepth);

        rangeExtract.setProfundidade_inicial(updatedInitialDepth);
        rangeExtract.setProfundidade_final(updatedFinalDepth);

        RangeExtractModel updatedExtract = rangeExtractRepository.save(rangeExtract);
        return updatedExtract.toDto();
    }

    @Override
    @Transactional
    public void deleteRangeExtract(Long rangeExtractId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        RangeExtractModel rangeExtract = findRangeExtractByIdOrThrow(rangeExtractId);
        checkOwnerPermission(rangeExtract.getAnalysis().getPlot(), owner);
        ensureAnalysisSupportsRanges(rangeExtract.getAnalysis());

        rangeExtractRepository.delete(rangeExtract);
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

    private void ensureAnalysisSupportsRanges(SoilAnalysisModel analysis) {
        if (analysis.getExtractType() != TipoExtrato.INTERVALOS) {
            throw new IllegalArgumentException("A análise de solo selecionada não permite extratos por intervalo.");
        }
    }

    private void checkUserIsProprietario(UserModel user) {
        if (user.getCargo() != Cargo.PROPRIETARIO) {
            throw new AccessDeniedException("Acesso negado. Apenas usuários com o cargo 'PROPRIETARIO' podem gerenciar extratos por intervalo.");
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

    private RangeExtractModel findRangeExtractByIdOrThrow(Long rangeExtractId) {
        return rangeExtractRepository.findById(rangeExtractId)
                .orElseThrow(() -> new EntityNotFoundException("Extrato de intervalo não encontrado com o ID: " + rangeExtractId));
    }

    private void checkOwnerPermission(PlotModel plot, UserModel requestingUser) {
        PropertyModel property = plot.getProperty();
        if (!property.getOwner().getId().equals(requestingUser.getId())) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este recurso.");
        }
    }
}