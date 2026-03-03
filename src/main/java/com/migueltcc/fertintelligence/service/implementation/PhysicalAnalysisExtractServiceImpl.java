package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
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
import com.migueltcc.fertintelligence.repository.PlotAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.PropertyAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.RangeExtractRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.PhysicalAnalysisExtractService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.function.DoubleConsumer;
import java.util.stream.Collectors;

@Service
public class PhysicalAnalysisExtractServiceImpl implements PhysicalAnalysisExtractService {

    private static final EnumSet<Cargo> ALLOWED_ROLES = EnumSet.of(
            Cargo.PROPRIETARIO,
            Cargo.GERENTE,
            Cargo.AGRONOMO_RESIDENTE,
            Cargo.AGRONOMO_CONSULTOR,
            Cargo.SUPERVISOR_DE_AREA,
            Cargo.SECRETARIO
    );

    private final PhysicalAnalysisExtractRepository physicalAnalysisExtractRepository;
    private final RangeExtractRepository rangeExtractRepository;
    private final LayerExtractRepository layerExtractRepository;
    private final PlotAccessRequestRepository plotAccessRequestRepository;
    private final PropertyAccessRequestRepository propertyAccessRequestRepository;
    private final UserRepository userRepository;

    public PhysicalAnalysisExtractServiceImpl(
            PhysicalAnalysisExtractRepository physicalAnalysisExtractRepository,
            RangeExtractRepository rangeExtractRepository,
            LayerExtractRepository layerExtractRepository,
            PlotAccessRequestRepository plotAccessRequestRepository,
            PropertyAccessRequestRepository propertyAccessRequestRepository,
            UserRepository userRepository
    ) {
        this.physicalAnalysisExtractRepository = physicalAnalysisExtractRepository;
        this.rangeExtractRepository = rangeExtractRepository;
        this.layerExtractRepository = layerExtractRepository;
        this.plotAccessRequestRepository = plotAccessRequestRepository;
        this.propertyAccessRequestRepository = propertyAccessRequestRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public PhysicalAnalysisExtractResponseDto createPhysicalAnalysisExtract(
            Long rangeExtractId,
            Long layerExtractId,
            PhysicalAnalysisExtractCreateRequestDto createRequestDto,
            String username
    ) {
        UserModel requester = findUserByUsernameOrThrow(username);
        assertAllowedRole(requester);

        ExtractContext ctx = resolveExtractContext(rangeExtractId, layerExtractId);
        assertPlotPermission(ctx.plot(), requester);

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
        assertAllowedRole(requester);

        PhysicalAnalysisExtractModel analysisExtract = findPhysicalAnalysisExtractByIdOrThrow(physicalAnalysisExtractId);
        assertPlotPermission(resolvePlot(analysisExtract), requester);

        return analysisExtract.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhysicalAnalysisExtractResponseDto> getPhysicalAnalysisExtractsByRange(Long rangeExtractId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);
        assertAllowedRole(requester);

        RangeExtractModel rangeExtract = findRangeExtractByIdOrThrow(rangeExtractId);
        assertPlotPermission(rangeExtract.getAnalysis().getPlot(), requester);

        return physicalAnalysisExtractRepository.findAllByRangeExtract(rangeExtract)
                .stream()
                .map(PhysicalAnalysisExtractModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhysicalAnalysisExtractResponseDto> getPhysicalAnalysisExtractsByLayer(Long layerExtractId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);
        assertAllowedRole(requester);

        LayerExtractModel layerExtract = findLayerExtractByIdOrThrow(layerExtractId);
        assertPlotPermission(layerExtract.getAnalysis().getPlot(), requester);

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
        assertAllowedRole(requester);

        PhysicalAnalysisExtractModel analysisExtract = findPhysicalAnalysisExtractByIdOrThrow(physicalAnalysisExtractId);
        assertPlotPermission(resolvePlot(analysisExtract), requester);

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
        assertAllowedRole(requester);

        PhysicalAnalysisExtractModel analysisExtract = findPhysicalAnalysisExtractByIdOrThrow(physicalAnalysisExtractId);
        assertPlotPermission(resolvePlot(analysisExtract), requester);

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

    private void assertPlotPermission(PlotModel plot, UserModel requester) {
        assertAllowedRole(requester);

        PropertyModel property = plot.getProperty();

        if (property.getOwner().getId().equals(requester.getId())) return;
        if (property.getManager() != null && property.getManager().getId().equals(requester.getId())) return;

        if (requester.getCargo() == Cargo.AGRONOMO_RESIDENTE) {
            boolean hasPropertyApproval = propertyAccessRequestRepository
                    .findByPropertyAndRequesterAndStatus(property, requester, AccessRequestStatus.APPROVED)
                    .isPresent();

            if (hasPropertyApproval) return;

            // se não tem acesso por propriedade, cai para a regra padrão do talhão
        }

        boolean hasPlotApproval = plotAccessRequestRepository
                .findByPlotAndRequesterAndStatus(plot, requester, AccessRequestStatus.APPROVED)
                .isPresent();

        if (!hasPlotApproval) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este recurso.");
        }
    }

    private void assertAllowedRole(UserModel requester) {
        if (!ALLOWED_ROLES.contains(requester.getCargo())) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este recurso.");
        }
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