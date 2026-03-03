package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.dto.extractAnalysis.physical.PhysicalAnalysisExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.physical.PhysicalAnalysisExtractPostRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.physical.PhysicalAnalysisExtractResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.PhysicalAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
import com.migueltcc.fertintelligence.repository.LayerExtractRepository;
import com.migueltcc.fertintelligence.repository.PhysicalAnalysisExtractRepository;
import com.migueltcc.fertintelligence.repository.RangeExtractRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.security.PermissionManager;
import com.migueltcc.fertintelligence.service.documentation.PhysicalAnalysisExtractService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.DoubleConsumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhysicalAnalysisExtractServiceImpl implements PhysicalAnalysisExtractService {

    private final PhysicalAnalysisExtractRepository physicalAnalysisExtractRepository;
    private final RangeExtractRepository rangeExtractRepository;
    private final LayerExtractRepository layerExtractRepository;
    private final UserRepository userRepository;
    private final PermissionManager permissionManager;

    @Override
    @Transactional
    public PhysicalAnalysisExtractResponseDto createPhysicalAnalysisExtract(
            Long rangeExtractId,
            Long layerExtractId,
            PhysicalAnalysisExtractCreateRequestDto createRequestDto,
            String username
    ) {
        UserModel requester = findUserByUsernameOrThrow(username);

        ExtractContext ctx = resolveExtractContext(rangeExtractId, layerExtractId);
        permissionManager.assertCanWrite(ctx.plot(), requester);

        PhysicalAnalysisExtractModel analysisExtract = PhysicalAnalysisExtractModel.builder()
                .rangeExtract(ctx.rangeExtract())
                .layerExtract(ctx.layerExtract())
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

        return physicalAnalysisExtractRepository.save(analysisExtract).toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public PhysicalAnalysisExtractResponseDto getPhysicalAnalysisExtractById(Long physicalAnalysisExtractId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        PhysicalAnalysisExtractModel analysisExtract = findPhysicalAnalysisExtractByIdOrThrow(physicalAnalysisExtractId);
        permissionManager.assertCanRead(resolvePlot(analysisExtract), requester);

        return analysisExtract.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhysicalAnalysisExtractResponseDto> getPhysicalAnalysisExtractsByRange(Long rangeExtractId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        RangeExtractModel rangeExtract = findRangeExtractByIdOrThrow(rangeExtractId);
        permissionManager.assertCanRead(rangeExtract.getAnalysis().getPlot(), requester);

        return physicalAnalysisExtractRepository.findAllByRangeExtract(rangeExtract)
                .stream()
                .map(PhysicalAnalysisExtractModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhysicalAnalysisExtractResponseDto> getPhysicalAnalysisExtractsByLayer(Long layerExtractId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        LayerExtractModel layerExtract = findLayerExtractByIdOrThrow(layerExtractId);
        permissionManager.assertCanRead(layerExtract.getAnalysis().getPlot(), requester);

        return physicalAnalysisExtractRepository.findAllByLayerExtract(layerExtract)
                .stream()
                .map(PhysicalAnalysisExtractModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PhysicalAnalysisExtractResponseDto updatePhysicalAnalysisExtract(
            Long physicalAnalysisExtractId,
            PhysicalAnalysisExtractPostRequestDto updateRequestDto,
            String username
    ) {
        UserModel requester = findUserByUsernameOrThrow(username);

        PhysicalAnalysisExtractModel analysisExtract = findPhysicalAnalysisExtractByIdOrThrow(physicalAnalysisExtractId);
        permissionManager.assertCanWrite(resolvePlot(analysisExtract), requester);

        applyIfNonNull(updateRequestDto.getTeorAreia(), analysisExtract::setTeorAreia);
        applyIfNonNull(updateRequestDto.getTeorSilte(), analysisExtract::setTeorSilte);
        applyIfNonNull(updateRequestDto.getTeorArgila(), analysisExtract::setTeorArgila);
        applyIfNonNull(updateRequestDto.getDensidadeAparente(), analysisExtract::setDensidadeAparente);
        applyIfNonNull(updateRequestDto.getDensidadeReal(), analysisExtract::setDensidadeReal);
        applyIfNonNull(updateRequestDto.getPorosidadeTotal(), analysisExtract::setPorosidadeTotal);
        applyIfNonNull(updateRequestDto.getMicroporosidade(), analysisExtract::setMicroporosidade);
        applyIfNonNull(updateRequestDto.getUmidadeCapacidadeCampo(), analysisExtract::setUmidadeCapacidadeCampo);
        applyIfNonNull(updateRequestDto.getUmidadePontoMurchaPermanente(), analysisExtract::setUmidadePontoMurchaPermanente);
        applyIfNonNull(updateRequestDto.getAguaDisponivel(), analysisExtract::setAguaDisponivel);
        applyIfNonNull(updateRequestDto.getResistenciaPenetracao(), analysisExtract::setResistenciaPenetracao);
        applyIfNonNull(updateRequestDto.getPercAgregados6_0mm(), analysisExtract::setPercAgregados6_0mm);
        applyIfNonNull(updateRequestDto.getPercAgregados4_1a6_0mm(), analysisExtract::setPercAgregados4_1a6_0mm);
        applyIfNonNull(updateRequestDto.getPercAgregados2_1a4_0mm(), analysisExtract::setPercAgregados2_1a4_0mm);
        applyIfNonNull(updateRequestDto.getPercAgregados1_0a2_0mm(), analysisExtract::setPercAgregados1_0a2_0mm);
        applyIfNonNull(updateRequestDto.getPercAgregadosMenor1_0mm(), analysisExtract::setPercAgregadosMenor1_0mm);

        return physicalAnalysisExtractRepository.save(analysisExtract).toDto();
    }

    @Override
    @Transactional
    public void deletePhysicalAnalysisExtract(Long physicalAnalysisExtractId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        PhysicalAnalysisExtractModel analysisExtract = findPhysicalAnalysisExtractByIdOrThrow(physicalAnalysisExtractId);
        permissionManager.assertCanWrite(resolvePlot(analysisExtract), requester);

        physicalAnalysisExtractRepository.delete(analysisExtract);
    }

    private double valueOrZero(Double value) {
        return value != null ? value : 0.0;
    }

    private void applyIfNonNull(Double value, DoubleConsumer setter) {
        if (value != null) setter.accept(value);
    }

    private ExtractContext resolveExtractContext(Long rangeExtractId, Long layerExtractId) {
        boolean hasRange = rangeExtractId != null;
        boolean hasLayer = layerExtractId != null;

        if (hasRange == hasLayer) {
            throw new IllegalArgumentException("Informe exatamente um extrato base (intervalo ou camada).");
        }

        if (hasRange) {
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

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private RangeExtractModel findRangeExtractByIdOrThrow(Long rangeExtractId) {
        return rangeExtractRepository.findById(rangeExtractId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Extrato por intervalo não encontrado com o ID: " + rangeExtractId
                ));
    }

    private LayerExtractModel findLayerExtractByIdOrThrow(Long layerExtractId) {
        return layerExtractRepository.findById(layerExtractId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Extrato por camada não encontrado com o ID: " + layerExtractId
                ));
    }

    private PhysicalAnalysisExtractModel findPhysicalAnalysisExtractByIdOrThrow(Long id) {
        return physicalAnalysisExtractRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Extrato de análise física não encontrado com o ID: " + id
                ));
    }

    private record ExtractContext(
            RangeExtractModel rangeExtract,
            LayerExtractModel layerExtract,
            PlotModel plot
    ) {}
}