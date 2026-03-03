package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.soilExtracts.TipoExtrato;
import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.extract.range.RangeExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extract.range.RangeExtractPostRequestDto;
import com.migueltcc.fertintelligence.dto.extract.range.RangeExtractResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
import com.migueltcc.fertintelligence.repository.PlotAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.PropertyAccessRequestRepository;
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

    @Autowired
    private PropertyAccessRequestRepository propertyAccessRequestRepository;

    @Autowired
    private PlotAccessRequestRepository plotAccessRequestRepository;

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

        // CREATE = edição
        checkPermission(analysis.getPlot(), requester, true);

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
        UserModel requester = findUserByUsernameOrThrow(username);

        RangeExtractModel rangeExtract = findRangeExtractByIdOrThrow(rangeExtractId);
        ensureAnalysisSupportsRanges(rangeExtract.getAnalysis());

        // READ = não edição
        checkPermission(rangeExtract.getAnalysis().getPlot(), requester, false);

        return rangeExtract.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RangeExtractResponseDto> getAllRangeExtractsByAnalysis(Long analysisId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        SoilAnalysisModel analysis = findAnalysisByIdOrThrow(analysisId);
        ensureAnalysisSupportsRanges(analysis);

        // LIST = não edição
        checkPermission(analysis.getPlot(), requester, false);

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

        // UPDATE = edição
        checkPermission(analysis.getPlot(), requester, true);

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
        UserModel requester = findUserByUsernameOrThrow(username);

        RangeExtractModel rangeExtract = findRangeExtractByIdOrThrow(rangeExtractId);
        ensureAnalysisSupportsRanges(rangeExtract.getAnalysis());

        // DELETE = edição
        checkPermission(rangeExtract.getAnalysis().getPlot(), requester, true);

        rangeExtractRepository.delete(rangeExtract);
    }

    /* ======================================================
       Permission / Validation helpers
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

    private void checkPermission(PlotModel plot, UserModel requestingUser, boolean requireEdit) {
        ensureUserHasAllowedRole(requestingUser);

        // regra simples: secretário só leitura
        if (requireEdit && requestingUser.getCargo() == Cargo.SECRETARIO) {
            throw new AccessDeniedException("Secretários não têm permissão para criar/editar/remover este recurso.");
        }

        PropertyModel property = plot.getProperty();

        // Owner sempre pode
        if (property.getOwner().getId().equals(requestingUser.getId())) return;

        // Manager sempre pode
        if (property.getManager() != null && property.getManager().getId().equals(requestingUser.getId())) return;

        // A partir daqui: não-donos / não-gerentes
        // ✅ Se tem aprovação na PROPRIEDADE, pode visualizar (e, neste refactor, também editar se não for SECRETARIO)
        boolean hasPropertyApproval = propertyAccessRequestRepository
                .findByPropertyAndRequesterAndStatus(property, requestingUser, AccessRequestStatus.APPROVED)
                .isPresent();

        if (hasPropertyApproval) return;

        // (fallback) Se por algum motivo você ainda usa aprovação por TALHÃO, mantém compatibilidade:
        boolean hasPlotApproval = plotAccessRequestRepository
                .findByPlotAndRequesterAndStatus(plot, requestingUser, AccessRequestStatus.APPROVED)
                .isPresent();

        if (!hasPlotApproval) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este recurso.");
        }
    }

    private void ensureUserHasAllowedRole(UserModel requestingUser) {
        Cargo c = requestingUser.getCargo();
        if (c != Cargo.PROPRIETARIO
                && c != Cargo.GERENTE
                && c != Cargo.AGRONOMO_RESIDENTE
                && c != Cargo.AGRONOMO_CONSULTOR
                && c != Cargo.SUPERVISOR_DE_AREA
                && c != Cargo.SECRETARIO) {
            throw new AccessDeniedException("Você não tem permissão para acessar este recurso.");
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
                .orElseThrow(() -> new EntityNotFoundException("Análise de solo não encontrada com o ID: " + analysisId));
    }

    private RangeExtractModel findRangeExtractByIdOrThrow(Long rangeExtractId) {
        return rangeExtractRepository.findById(rangeExtractId)
                .orElseThrow(() -> new EntityNotFoundException("Extrato de intervalo não encontrado com o ID: " + rangeExtractId));
    }
}