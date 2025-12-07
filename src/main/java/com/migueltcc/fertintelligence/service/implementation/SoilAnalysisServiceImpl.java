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
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
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
    private PlotAccessRequestRepository plotAccessRequestRepository;

    @Autowired
    private PropertyAccessRequestRepository propertyAccessRequestRepository;

    @Override
    @Transactional
    public SoilAnalysisResponseDto createSoilAnalysis(SoilAnalysisCreateRequestDto createRequestDto, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        PlotModel plot = findPlotByIdOrThrow(createRequestDto.getPlotId());
        checkPermission(plot, requester);

        if (!plot.getIdentification().equals(createRequestDto.getPlotIdentification())) {
            throw new IllegalArgumentException("A identificação do talhão informada não corresponde ao talhão selecionado.");
        }

        soilAnalysisRepository.findByPlotAndAnalysisYear(plot, createRequestDto.getAnalysisYear())
                .ifPresent(existing -> {
                    throw new EntityExistsException("Já existe uma análise de solo para o ano informado neste talhão: "
                            + createRequestDto.getAnalysisYear());
                });

        SoilAnalysisModel soilAnalysis = SoilAnalysisModel.builder()
                .plot(plot)
                .analysisYear(createRequestDto.getAnalysisYear())
                .responsibleLaboratory(createRequestDto.getResponsibleLaboratory())
                .extractType(createRequestDto.getExtractType())
                .build();

        SoilAnalysisModel savedAnalysis = soilAnalysisRepository.save(soilAnalysis);
        return savedAnalysis.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public SoilAnalysisResponseDto getSoilAnalysisById(Long soilAnalysisId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        SoilAnalysisModel soilAnalysis = findSoilAnalysisByIdOrThrow(soilAnalysisId);
        checkPermission(soilAnalysis.getPlot(), requester);

        return soilAnalysis.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SoilAnalysisResponseDto> getAllSoilAnalysesByPlot(Long plotId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        PlotModel plot = findPlotByIdOrThrow(plotId);
        checkPermission(plot, requester);

        return soilAnalysisRepository.findAllByPlot(plot).stream()
                .map(SoilAnalysisModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SoilAnalysisResponseDto updateSoilAnalysis(Long soilAnalysisId, SoilAnalysisPostRequestDto updateRequestDto, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        SoilAnalysisModel soilAnalysis = findSoilAnalysisByIdOrThrow(soilAnalysisId);
        checkPermission(soilAnalysis.getPlot(), requester);

        if (updateRequestDto.getAnalysisYear() != null
                && !updateRequestDto.getAnalysisYear().equals(soilAnalysis.getAnalysisYear())) {
            soilAnalysisRepository.findByPlotAndAnalysisYear(soilAnalysis.getPlot(), updateRequestDto.getAnalysisYear())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(soilAnalysisId)) {
                            throw new EntityExistsException("Já existe uma análise de solo para o ano informado neste talhão: "
                                    + updateRequestDto.getAnalysisYear());
                        }
                    });
            soilAnalysis.setAnalysisYear(updateRequestDto.getAnalysisYear());
        }

        if (updateRequestDto.getResponsibleLaboratory() != null) {
            soilAnalysis.setResponsibleLaboratory(updateRequestDto.getResponsibleLaboratory());
        }

        if (updateRequestDto.getExtractType() != null) {
            soilAnalysis.setExtractType(updateRequestDto.getExtractType());
        }

        SoilAnalysisModel updatedAnalysis = soilAnalysisRepository.save(soilAnalysis);
        return updatedAnalysis.toDto();
    }

    @Override
    @Transactional
    public void deleteSoilAnalysis(Long soilAnalysisId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        SoilAnalysisModel soilAnalysis = findSoilAnalysisByIdOrThrow(soilAnalysisId);
        checkPermission(soilAnalysis.getPlot(), requester);

        soilAnalysisRepository.delete(soilAnalysis);
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

    private void checkPermission(PlotModel plot, UserModel requestingUser) {
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