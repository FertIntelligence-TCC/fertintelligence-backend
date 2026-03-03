package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.dto.extractAnalysis.physical.PhysicalAnalysisExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.physical.PhysicalAnalysisExtractPostRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.physical.PhysicalAnalysisExtractResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.PhysicalAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
import com.migueltcc.fertintelligence.repository.LayerExtractRepository;
import com.migueltcc.fertintelligence.repository.PhysicalAnalysisExtractRepository;
import com.migueltcc.fertintelligence.repository.RangeExtractRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
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
            PhysicalAnalysisExtractCreateRequestDto dto,
            String username
    ) {
        UserModel requester = findUserByUsernameOrThrow(username);

        ExtractContext ctx = resolveExtractContext(rangeExtractId, layerExtractId);
        assertCanEdit(ctx.plot(), requester);

        PhysicalAnalysisExtractModel model = PhysicalAnalysisExtractModel.builder()
                .rangeExtract(ctx.rangeExtract())
                .layerExtract(ctx.layerExtract())
                .teorAreia(zeroIfNull(dto.getTeorAreia()))
                .teorSilte(zeroIfNull(dto.getTeorSilte()))
                .teorArgila(zeroIfNull(dto.getTeorArgila()))
                .densidadeAparente(zeroIfNull(dto.getDensidadeAparente()))
                .densidadeReal(zeroIfNull(dto.getDensidadeReal()))
                .porosidadeTotal(zeroIfNull(dto.getPorosidadeTotal()))
                .microporosidade(zeroIfNull(dto.getMicroporosidade()))
                .umidadeCapacidadeCampo(zeroIfNull(dto.getUmidadeCapacidadeCampo()))
                .umidadePontoMurchaPermanente(zeroIfNull(dto.getUmidadePontoMurchaPermanente()))
                .aguaDisponivel(zeroIfNull(dto.getAguaDisponivel()))
                .resistenciaPenetracao(zeroIfNull(dto.getResistenciaPenetracao()))
                .percAgregados6_0mm(zeroIfNull(dto.getPercAgregados6_0mm()))
                .percAgregados4_1a6_0mm(zeroIfNull(dto.getPercAgregados4_1a6_0mm()))
                .percAgregados2_1a4_0mm(zeroIfNull(dto.getPercAgregados2_1a4_0mm()))
                .percAgregados1_0a2_0mm(zeroIfNull(dto.getPercAgregados1_0a2_0mm()))
                .percAgregadosMenor1_0mm(zeroIfNull(dto.getPercAgregadosMenor1_0mm()))
                .build();

        return physicalAnalysisExtractRepository.save(model).toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public PhysicalAnalysisExtractResponseDto getPhysicalAnalysisExtractById(Long id, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        PhysicalAnalysisExtractModel model = findPhysicalAnalysisExtractByIdOrThrow(id);
        PlotModel plot = resolvePlot(model);

        permissionManager.assertCanReadPlot(plot, requester);

        return model.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhysicalAnalysisExtractResponseDto> getPhysicalAnalysisExtractsByRange(Long rangeExtractId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        RangeExtractModel rangeExtract = findRangeExtractByIdOrThrow(rangeExtractId);
        PlotModel plot = rangeExtract.getAnalysis().getPlot();

        permissionManager.assertCanReadPlot(plot, requester);

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
        PlotModel plot = layerExtract.getAnalysis().getPlot();

        permissionManager.assertCanReadPlot(plot, requester);

        return physicalAnalysisExtractRepository.findAllByLayerExtract(layerExtract)
                .stream()
                .map(PhysicalAnalysisExtractModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PhysicalAnalysisExtractResponseDto updatePhysicalAnalysisExtract(
            Long id,
            PhysicalAnalysisExtractPostRequestDto dto,
            String username
    ) {
        UserModel requester = findUserByUsernameOrThrow(username);

        PhysicalAnalysisExtractModel model = findPhysicalAnalysisExtractByIdOrThrow(id);
        PlotModel plot = resolvePlot(model);

        assertCanEdit(plot, requester);

        applyIfNonNull(dto.getTeorAreia(), model::setTeorAreia);
        applyIfNonNull(dto.getTeorSilte(), model::setTeorSilte);
        applyIfNonNull(dto.getTeorArgila(), model::setTeorArgila);
        applyIfNonNull(dto.getDensidadeAparente(), model::setDensidadeAparente);
        applyIfNonNull(dto.getDensidadeReal(), model::setDensidadeReal);
        applyIfNonNull(dto.getPorosidadeTotal(), model::setPorosidadeTotal);
        applyIfNonNull(dto.getMicroporosidade(), model::setMicroporosidade);
        applyIfNonNull(dto.getUmidadeCapacidadeCampo(), model::setUmidadeCapacidadeCampo);
        applyIfNonNull(dto.getUmidadePontoMurchaPermanente(), model::setUmidadePontoMurchaPermanente);
        applyIfNonNull(dto.getAguaDisponivel(), model::setAguaDisponivel);
        applyIfNonNull(dto.getResistenciaPenetracao(), model::setResistenciaPenetracao);
        applyIfNonNull(dto.getPercAgregados6_0mm(), model::setPercAgregados6_0mm);
        applyIfNonNull(dto.getPercAgregados4_1a6_0mm(), model::setPercAgregados4_1a6_0mm);
        applyIfNonNull(dto.getPercAgregados2_1a4_0mm(), model::setPercAgregados2_1a4_0mm);
        applyIfNonNull(dto.getPercAgregados1_0a2_0mm(), model::setPercAgregados1_0a2_0mm);
        applyIfNonNull(dto.getPercAgregadosMenor1_0mm(), model::setPercAgregadosMenor1_0mm);

        return physicalAnalysisExtractRepository.save(model).toDto();
    }

    @Override
    @Transactional
    public void deletePhysicalAnalysisExtract(Long id, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        PhysicalAnalysisExtractModel model = findPhysicalAnalysisExtractByIdOrThrow(id);
        PlotModel plot = resolvePlot(model);

        assertCanEdit(plot, requester);

        physicalAnalysisExtractRepository.delete(model);
    }

    /* =========================
       Permissão
       ========================= */

    private void assertCanEdit(PlotModel plot, UserModel requester) {
        PropertyModel property = plot != null ? plot.getProperty() : null;
        permissionManager.assertCanEditAnalyses(property, plot, requester);
    }

    /* =========================
       Utils
       ========================= */

    private double zeroIfNull(Double value) {
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

    private PlotModel resolvePlot(PhysicalAnalysisExtractModel model) {
        if (model.getRangeExtract() != null) {
            return model.getRangeExtract().getAnalysis().getPlot();
        }
        if (model.getLayerExtract() != null) {
            return model.getLayerExtract().getAnalysis().getPlot();
        }
        throw new IllegalStateException("Extrato de análise física não possui extrato base associado.");
    }

    /* =========================
       Finders
       ========================= */

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private RangeExtractModel findRangeExtractByIdOrThrow(Long id) {
        return rangeExtractRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Extrato por intervalo não encontrado com o ID: " + id));
    }

    private LayerExtractModel findLayerExtractByIdOrThrow(Long id) {
        return layerExtractRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Extrato por camada não encontrado com o ID: " + id));
    }

    private PhysicalAnalysisExtractModel findPhysicalAnalysisExtractByIdOrThrow(Long id) {
        return physicalAnalysisExtractRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Extrato de análise física não encontrado com o ID: " + id));
    }

    private record ExtractContext(
            RangeExtractModel rangeExtract,
            LayerExtractModel layerExtract,
            PlotModel plot
    ) {}
}