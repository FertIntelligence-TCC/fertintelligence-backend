package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.soilExtracts.TipoExtrato;
import com.migueltcc.fertintelligence.dto.extract.range.RangeExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extract.range.RangeExtractPostRequestDto;
import com.migueltcc.fertintelligence.dto.extract.range.RangeExtractResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
import com.migueltcc.fertintelligence.repository.RangeExtractRepository;
import com.migueltcc.fertintelligence.repository.SoilAnalysisRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.RangeExtractService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RangeExtractServiceImpl implements RangeExtractService {

    private final RangeExtractRepository rangeExtractRepository;
    private final SoilAnalysisRepository soilAnalysisRepository;
    private final UserRepository userRepository;
    private final PermissionManager permissionManager;

    @Override
    @Transactional
    public RangeExtractResponseDto createRangeExtract(
            Long analysisId,
            RangeExtractCreateRequestDto createRequestDto,
            String username
    ) {
        UserModel requester = findUserByUsernameOrThrow(username);
        SoilAnalysisModel analysis = findAnalysisByIdOrThrow(analysisId);

        ensureAnalysisSupportsRanges(analysis);
        validateDepthRange(createRequestDto.getInitialDepth(), createRequestDto.getFinalDepth());

        // CREATE = editar análises
        assertCanEditAnalyses(analysis, requester);

        RangeExtractModel rangeExtract = RangeExtractModel.builder()
                .analysis(analysis)
                .profundidade_inicial(createRequestDto.getInitialDepth())
                .profundidade_final(createRequestDto.getFinalDepth())
                .build();

        return rangeExtractRepository.save(rangeExtract).toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public RangeExtractResponseDto getRangeExtractById(Long rangeExtractId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);
        RangeExtractModel rangeExtract = findRangeExtractByIdOrThrow(rangeExtractId);

        SoilAnalysisModel analysis = rangeExtract.getAnalysis();
        ensureAnalysisSupportsRanges(analysis);

        // READ = pode visualizar o talhão (entrada aprovada)
        permissionManager.assertCanReadPlot(analysis.getPlot(), requester);

        return rangeExtract.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RangeExtractResponseDto> getAllRangeExtractsByAnalysis(Long analysisId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);
        SoilAnalysisModel analysis = findAnalysisByIdOrThrow(analysisId);

        ensureAnalysisSupportsRanges(analysis);

        permissionManager.assertCanReadPlot(analysis.getPlot(), requester);

        return rangeExtractRepository.findAllByAnalysis(analysis).stream()
                .map(RangeExtractModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RangeExtractResponseDto updateRangeExtract(
            Long rangeExtractId,
            RangeExtractPostRequestDto updateRequestDto,
            String username
    ) {
        UserModel requester = findUserByUsernameOrThrow(username);
        RangeExtractModel rangeExtract = findRangeExtractByIdOrThrow(rangeExtractId);

        SoilAnalysisModel analysis = rangeExtract.getAnalysis();
        ensureAnalysisSupportsRanges(analysis);

        // UPDATE = editar análises
        assertCanEditAnalyses(analysis, requester);

        Integer updatedInitialDepth = updateRequestDto.getInitialDepth() != null
                ? updateRequestDto.getInitialDepth()
                : rangeExtract.getProfundidade_inicial();

        Integer updatedFinalDepth = updateRequestDto.getFinalDepth() != null
                ? updateRequestDto.getFinalDepth()
                : rangeExtract.getProfundidade_final();

        validateDepthRange(updatedInitialDepth, updatedFinalDepth);

        rangeExtract.setProfundidade_inicial(updatedInitialDepth);
        rangeExtract.setProfundidade_final(updatedFinalDepth);

        return rangeExtractRepository.save(rangeExtract).toDto();
    }

    @Override
    @Transactional
    public void deleteRangeExtract(Long rangeExtractId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);
        RangeExtractModel rangeExtract = findRangeExtractByIdOrThrow(rangeExtractId);

        SoilAnalysisModel analysis = rangeExtract.getAnalysis();
        ensureAnalysisSupportsRanges(analysis);

        // DELETE = editar análises
        assertCanEditAnalyses(analysis, requester);

        rangeExtractRepository.delete(rangeExtract);
    }

    /* ======================================================
       Permission helpers
    ====================================================== */

    private void assertCanEditAnalyses(SoilAnalysisModel analysis, UserModel requester) {
        PlotModel plot = analysis.getPlot();
        PropertyModel property = plot.getProperty();
        permissionManager.assertCanEditAnalyses(property, plot, requester);
    }

    /* ======================================================
       Business validations
    ====================================================== */

    private void validateDepthRange(Integer initialDepth, Integer finalDepth) {
        if (initialDepth == null || finalDepth == null) return;

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

    /* ======================================================
       Finders
    ====================================================== */

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

    private RangeExtractModel findRangeExtractByIdOrThrow(Long rangeExtractId) {
        return rangeExtractRepository.findById(rangeExtractId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Extrato de intervalo não encontrado com o ID: " + rangeExtractId
                ));
    }
}