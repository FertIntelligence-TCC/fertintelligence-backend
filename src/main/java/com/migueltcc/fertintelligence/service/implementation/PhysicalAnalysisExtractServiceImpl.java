package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.dto.extractAnalysis.physical.PhysicalAnalysisExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.physical.PhysicalAnalysisExtractPostRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.physical.PhysicalAnalysisExtractResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.PhysicalAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.LayerExtractRepository;
import com.migueltcc.fertintelligence.repository.PhysicalAnalysisExtractRepository;
import com.migueltcc.fertintelligence.repository.PlotAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.RangeExtractRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.PhysicalAnalysisExtractService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.DoubleConsumer;
import java.util.stream.Collectors;

@Service
public class PhysicalAnalysisExtractServiceImpl implements PhysicalAnalysisExtractService {

    @Autowired
    private PhysicalAnalysisExtractRepository physicalAnalysisExtractRepository;

    @Autowired
    private RangeExtractRepository rangeExtractRepository;

    @Autowired
    private LayerExtractRepository layerExtractRepository;

    @Autowired
    private PlotAccessRequestRepository plotAccessRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public PhysicalAnalysisExtractResponseDto createPhysicalAnalysisExtract(Long rangeExtractId,
                                                                            Long layerExtractId,
                                                                            PhysicalAnalysisExtractCreateRequestDto createRequestDto,
                                                                            String username) {
        UserModel requestingUser = findUserByUsernameOrThrow(username);

        ExtractContext extractContext = resolveExtractContext(rangeExtractId, layerExtractId);
        checkPlotPermission(extractContext.plot(), requestingUser);

        PhysicalAnalysisExtractModel analysisExtract = PhysicalAnalysisExtractModel.builder()
                .rangeExtract(extractContext.rangeExtract())
                .layerExtract(extractContext.layerExtract())
                .teorAreia(valueOrZero(createRequestDto.getTeorAreia()))
                .teorSilte(valueOrZero(createRequestDto.getTeorSilte()))
                .teorArgila(valueOrZero(createRequestDto.getTeorArgila()))
                .densidadeAparente(valueOrZero(createRequestDto.getDensidadeAparente()))
                .densidadeReal(valueOrZero(createRequestDto.getDensidadeReal()))
                .porosidadeTotal(valueOrZero(createRequestDto.getPorosidadeTotal()))
                .microporosidade(valueOrZero(createRequestDto.getMicroporosidade()))
                .umidadeCapacidadeCampo(valueOrZero(createRequestDto.getUmidadeCapacidadeCampo()))
                .umidadePontoMurchaPermanente(valueOrZero(createRequestDto.getUmidadePontoMurchaPermanente()))
                .aguaDisponivel(valueOrZero(createRequestDto.getAguaDisponivel()))
                .resistenciaPenetracao(valueOrZero(createRequestDto.getResistenciaPenetracao()))
                .percAgregados6_0mm(valueOrZero(createRequestDto.getPercAgregados6_0mm()))
                .percAgregados4_1a6_0mm(valueOrZero(createRequestDto.getPercAgregados4_1a6_0mm()))
                .percAgregados2_1a4_0mm(valueOrZero(createRequestDto.getPercAgregados2_1a4_0mm()))
                .percAgregados1_0a2_0mm(valueOrZero(createRequestDto.getPercAgregados1_0a2_0mm()))
                .percAgregadosMenor1_0mm(valueOrZero(createRequestDto.getPercAgregadosMenor1_0mm()))
                .build();

        PhysicalAnalysisExtractModel savedExtract = physicalAnalysisExtractRepository.save(analysisExtract);
        return savedExtract.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public PhysicalAnalysisExtractResponseDto getPhysicalAnalysisExtractById(Long physicalAnalysisExtractId, String username) {
        UserModel requestingUser = findUserByUsernameOrThrow(username);

        PhysicalAnalysisExtractModel analysisExtract = findPhysicalAnalysisExtractByIdOrThrow(physicalAnalysisExtractId);
        checkPlotPermission(resolvePlot(analysisExtract), requestingUser);

        return analysisExtract.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhysicalAnalysisExtractResponseDto> getPhysicalAnalysisExtractsByRange(Long rangeExtractId, String username) {
        UserModel requestingUser = findUserByUsernameOrThrow(username);

        RangeExtractModel rangeExtract = findRangeExtractByIdOrThrow(rangeExtractId);
        checkPlotPermission(rangeExtract.getAnalysis().getPlot(), requestingUser);

        return physicalAnalysisExtractRepository.findAllByRangeExtract(rangeExtract).stream()
                .map(PhysicalAnalysisExtractModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhysicalAnalysisExtractResponseDto> getPhysicalAnalysisExtractsByLayer(Long layerExtractId, String username) {
        UserModel requestingUser = findUserByUsernameOrThrow(username);

        LayerExtractModel layerExtract = findLayerExtractByIdOrThrow(layerExtractId);
        checkPlotPermission(layerExtract.getAnalysis().getPlot(), requestingUser);

        return physicalAnalysisExtractRepository.findAllByLayerExtract(layerExtract).stream()
                .map(PhysicalAnalysisExtractModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PhysicalAnalysisExtractResponseDto updatePhysicalAnalysisExtract(Long physicalAnalysisExtractId,
                                                                            PhysicalAnalysisExtractPostRequestDto updateRequestDto,
                                                                            String username) {
        UserModel requestingUser = findUserByUsernameOrThrow(username);

        PhysicalAnalysisExtractModel analysisExtract = findPhysicalAnalysisExtractByIdOrThrow(physicalAnalysisExtractId);
        checkPlotPermission(resolvePlot(analysisExtract), requestingUser);

        updateField(updateRequestDto.getTeorAreia(), analysisExtract::setTeorAreia);
        updateField(updateRequestDto.getTeorSilte(), analysisExtract::setTeorSilte);
        updateField(updateRequestDto.getTeorArgila(), analysisExtract::setTeorArgila);
        updateField(updateRequestDto.getDensidadeAparente(), analysisExtract::setDensidadeAparente);
        updateField(updateRequestDto.getDensidadeReal(), analysisExtract::setDensidadeReal);
        updateField(updateRequestDto.getPorosidadeTotal(), analysisExtract::setPorosidadeTotal);
        updateField(updateRequestDto.getMicroporosidade(), analysisExtract::setMicroporosidade);
        updateField(updateRequestDto.getUmidadeCapacidadeCampo(), analysisExtract::setUmidadeCapacidadeCampo);
        updateField(updateRequestDto.getUmidadePontoMurchaPermanente(), analysisExtract::setUmidadePontoMurchaPermanente);
        updateField(updateRequestDto.getAguaDisponivel(), analysisExtract::setAguaDisponivel);
        updateField(updateRequestDto.getResistenciaPenetracao(), analysisExtract::setResistenciaPenetracao);
        updateField(updateRequestDto.getPercAgregados6_0mm(), analysisExtract::setPercAgregados6_0mm);
        updateField(updateRequestDto.getPercAgregados4_1a6_0mm(), analysisExtract::setPercAgregados4_1a6_0mm);
        updateField(updateRequestDto.getPercAgregados2_1a4_0mm(), analysisExtract::setPercAgregados2_1a4_0mm);
        updateField(updateRequestDto.getPercAgregados1_0a2_0mm(), analysisExtract::setPercAgregados1_0a2_0mm);
        updateField(updateRequestDto.getPercAgregadosMenor1_0mm(), analysisExtract::setPercAgregadosMenor1_0mm);

        PhysicalAnalysisExtractModel updatedExtract = physicalAnalysisExtractRepository.save(analysisExtract);
        return updatedExtract.toDto();
    }

    @Override
    @Transactional
    public void deletePhysicalAnalysisExtract(Long physicalAnalysisExtractId, String username) {
        UserModel requestingUser = findUserByUsernameOrThrow(username);

        PhysicalAnalysisExtractModel analysisExtract = findPhysicalAnalysisExtractByIdOrThrow(physicalAnalysisExtractId);
        checkPlotPermission(resolvePlot(analysisExtract), requestingUser);

        physicalAnalysisExtractRepository.delete(analysisExtract);
    }

    private double valueOrZero(Double value) {
        return value != null ? value : 0.0;
    }

    private void updateField(Double value, DoubleConsumer setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    private ExtractContext resolveExtractContext(Long rangeExtractId, Long layerExtractId) {
        if ((rangeExtractId == null && layerExtractId == null) ||
                (rangeExtractId != null && layerExtractId != null)) {
            throw new IllegalArgumentException("Informe exatamente um extrato base (intervalo ou camada).");
        }

        if (rangeExtractId != null) {
            RangeExtractModel rangeExtract = findRangeExtractByIdOrThrow(rangeExtractId);
            return new ExtractContext(rangeExtract, null, rangeExtract.getAnalysis().getPlot());
        }

        LayerExtractModel layerExtract = findLayerExtractByIdOrThrow(layerExtractId);
        return new ExtractContext(null, layerExtract, layerExtract.getAnalysis().getPlot());
    }

    private PlotModel resolvePlot(PhysicalAnalysisExtractModel analysisExtract) {
        if (analysisExtract.getRangeExtract() != null) {
            return analysisExtract.getRangeExtract().getAnalysis().getPlot();
        }

        if (analysisExtract.getLayerExtract() != null) {
            return analysisExtract.getLayerExtract().getAnalysis().getPlot();
        }

        throw new IllegalStateException("Extrato de análise física não possui extrato base associado.");
    }

    private void checkPlotPermission(PlotModel plot, UserModel requestingUser) {
        PropertyModel property = plot.getProperty();

        if (property.getOwner().getId().equals(requestingUser.getId())) {
            return;
        }

        if (property.getManager() != null && property.getManager().getId().equals(requestingUser.getId())) {
            return;
        }

        boolean hasApprovedAccess = plotAccessRequestRepository.findByPlotAndRequesterAndStatus(
                plot,
                requestingUser,
                AccessRequestStatus.APPROVED
        ).isPresent();

        if (!hasApprovedAccess) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este recurso.");
        }
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private RangeExtractModel findRangeExtractByIdOrThrow(Long rangeExtractId) {
        return rangeExtractRepository.findById(rangeExtractId)
                .orElseThrow(() -> new EntityNotFoundException("Extrato por intervalo não encontrado com o ID: " + rangeExtractId));
    }

    private LayerExtractModel findLayerExtractByIdOrThrow(Long layerExtractId) {
        return layerExtractRepository.findById(layerExtractId)
                .orElseThrow(() -> new EntityNotFoundException("Extrato por camada não encontrado com o ID: " + layerExtractId));
    }

    private PhysicalAnalysisExtractModel findPhysicalAnalysisExtractByIdOrThrow(Long physicalAnalysisExtractId) {
        return physicalAnalysisExtractRepository.findById(physicalAnalysisExtractId)
                .orElseThrow(() -> new EntityNotFoundException("Extrato de análise física não encontrado com o ID: " + physicalAnalysisExtractId));
    }

    private record ExtractContext(
            RangeExtractModel rangeExtract,
            LayerExtractModel layerExtract,
            PlotModel plot
    ) {}
}
