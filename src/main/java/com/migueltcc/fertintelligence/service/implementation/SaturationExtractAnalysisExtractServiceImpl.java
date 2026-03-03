package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.dto.extractAnalysis.saturationExtract.SaturationExtractAnalysisExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.saturationExtract.SaturationExtractAnalysisExtractPostRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.saturationExtract.SaturationExtractAnalysisExtractResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.SaturationExtractAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
import com.migueltcc.fertintelligence.repository.LayerExtractRepository;
import com.migueltcc.fertintelligence.repository.RangeExtractRepository;
import com.migueltcc.fertintelligence.repository.SaturationExtractAnalysisExtractRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.security.PermissionManager;
import com.migueltcc.fertintelligence.service.documentation.SaturationExtractAnalysisExtractService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaturationExtractAnalysisExtractServiceImpl implements SaturationExtractAnalysisExtractService {

    private final SaturationExtractAnalysisExtractRepository saturationExtractAnalysisExtractRepository;
    private final RangeExtractRepository rangeExtractRepository;
    private final LayerExtractRepository layerExtractRepository;
    private final UserRepository userRepository;
    private final PermissionManager permissionManager;

    @Override
    @Transactional
    public SaturationExtractAnalysisExtractResponseDto createSaturationExtractAnalysisExtract(
            Long rangeExtractId,
            Long layerExtractId,
            SaturationExtractAnalysisExtractCreateRequestDto createRequestDto,
            String username
    ) {
        UserModel requester = findUserByUsernameOrThrow(username);

        ExtractContext extractContext = resolveExtractContext(rangeExtractId, layerExtractId);
        permissionManager.assertCanWrite(extractContext.plot(), requester);

        SaturationExtractAnalysisExtractModel analysisExtract = SaturationExtractAnalysisExtractModel.builder()
                .rangeExtract(extractContext.rangeExtract())
                .layerExtract(extractContext.layerExtract())
                .ph(createRequestDto.getPh())
                .ce(createRequestDto.getCe())
                .teorCO3(createRequestDto.getTeorCO3())
                .teorHCO3(createRequestDto.getTeorHCO3())
                .teorNO3(createRequestDto.getTeorNO3())
                .teorH2PO4(createRequestDto.getTeorH2PO4())
                .teorSO4(createRequestDto.getTeorSO4())
                .teorNa(createRequestDto.getTeorNa())
                .teorK(createRequestDto.getTeorK())
                .teorCa(createRequestDto.getTeorCa())
                .teorMg(createRequestDto.getTeorMg())
                .residuosSuspensao(createRequestDto.getResiduosSuspensao())
                .durezaCaCO3(createRequestDto.getDurezaCaCO3())
                .durezaTotalCaCO3(createRequestDto.getDurezaTotalCaCO3())
                .ras(createRequestDto.getRas())
                .pst(createRequestDto.getPst())
                .build();

        return saturationExtractAnalysisExtractRepository.save(analysisExtract).toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public SaturationExtractAnalysisExtractResponseDto getSaturationExtractAnalysisExtractById(
            Long saturationExtractAnalysisExtractId,
            String username
    ) {
        UserModel requester = findUserByUsernameOrThrow(username);

        SaturationExtractAnalysisExtractModel analysisExtract =
                findSaturationExtractAnalysisExtractByIdOrThrow(saturationExtractAnalysisExtractId);

        permissionManager.assertCanRead(resolvePlot(analysisExtract), requester);

        return analysisExtract.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaturationExtractAnalysisExtractResponseDto> getSaturationExtractAnalysisExtractsByRange(
            Long rangeExtractId,
            String username
    ) {
        UserModel requester = findUserByUsernameOrThrow(username);

        RangeExtractModel rangeExtract = findRangeExtractByIdOrThrow(rangeExtractId);
        permissionManager.assertCanRead(rangeExtract.getAnalysis().getPlot(), requester);

        return saturationExtractAnalysisExtractRepository.findAllByRangeExtract(rangeExtract)
                .stream()
                .map(SaturationExtractAnalysisExtractModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaturationExtractAnalysisExtractResponseDto> getSaturationExtractAnalysisExtractsByLayer(
            Long layerExtractId,
            String username
    ) {
        UserModel requester = findUserByUsernameOrThrow(username);

        LayerExtractModel layerExtract = findLayerExtractByIdOrThrow(layerExtractId);
        permissionManager.assertCanRead(layerExtract.getAnalysis().getPlot(), requester);

        return saturationExtractAnalysisExtractRepository.findAllByLayerExtract(layerExtract)
                .stream()
                .map(SaturationExtractAnalysisExtractModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SaturationExtractAnalysisExtractResponseDto updateSaturationExtractAnalysisExtract(
            Long saturationExtractAnalysisExtractId,
            SaturationExtractAnalysisExtractPostRequestDto updateRequestDto,
            String username
    ) {
        UserModel requester = findUserByUsernameOrThrow(username);

        SaturationExtractAnalysisExtractModel analysisExtract =
                findSaturationExtractAnalysisExtractByIdOrThrow(saturationExtractAnalysisExtractId);

        permissionManager.assertCanWrite(resolvePlot(analysisExtract), requester);

        updateField(updateRequestDto.getPh(), analysisExtract::setPh);
        updateField(updateRequestDto.getCe(), analysisExtract::setCe);
        updateField(updateRequestDto.getTeorCO3(), analysisExtract::setTeorCO3);
        updateField(updateRequestDto.getTeorHCO3(), analysisExtract::setTeorHCO3);
        updateField(updateRequestDto.getTeorNO3(), analysisExtract::setTeorNO3);
        updateField(updateRequestDto.getTeorH2PO4(), analysisExtract::setTeorH2PO4);
        updateField(updateRequestDto.getTeorSO4(), analysisExtract::setTeorSO4);
        updateField(updateRequestDto.getTeorNa(), analysisExtract::setTeorNa);
        updateField(updateRequestDto.getTeorK(), analysisExtract::setTeorK);
        updateField(updateRequestDto.getTeorCa(), analysisExtract::setTeorCa);
        updateField(updateRequestDto.getTeorMg(), analysisExtract::setTeorMg);
        updateField(updateRequestDto.getResiduosSuspensao(), analysisExtract::setResiduosSuspensao);
        updateField(updateRequestDto.getDurezaCaCO3(), analysisExtract::setDurezaCaCO3);
        updateField(updateRequestDto.getDurezaTotalCaCO3(), analysisExtract::setDurezaTotalCaCO3);
        updateField(updateRequestDto.getRas(), analysisExtract::setRas);
        updateField(updateRequestDto.getPst(), analysisExtract::setPst);

        return saturationExtractAnalysisExtractRepository.save(analysisExtract).toDto();
    }

    @Override
    @Transactional
    public void deleteSaturationExtractAnalysisExtract(Long saturationExtractAnalysisExtractId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        SaturationExtractAnalysisExtractModel analysisExtract =
                findSaturationExtractAnalysisExtractByIdOrThrow(saturationExtractAnalysisExtractId);

        permissionManager.assertCanWrite(resolvePlot(analysisExtract), requester);

        saturationExtractAnalysisExtractRepository.delete(analysisExtract);
    }

    private void updateField(Double value, Consumer<Double> setter) {
        if (value != null) setter.accept(value);
    }

    private ExtractContext resolveExtractContext(Long rangeExtractId, Long layerExtractId) {
        if ((rangeExtractId == null && layerExtractId == null)
                || (rangeExtractId != null && layerExtractId != null)) {
            throw new IllegalArgumentException("Informe exatamente um extrato base (intervalo ou camada).");
        }

        if (rangeExtractId != null) {
            RangeExtractModel rangeExtract = findRangeExtractByIdOrThrow(rangeExtractId);
            return new ExtractContext(rangeExtract, null, rangeExtract.getAnalysis().getPlot());
        }

        LayerExtractModel layerExtract = findLayerExtractByIdOrThrow(layerExtractId);
        return new ExtractContext(null, layerExtract, layerExtract.getAnalysis().getPlot());
    }

    private PlotModel resolvePlot(SaturationExtractAnalysisExtractModel analysisExtract) {
        if (analysisExtract.getRangeExtract() != null)
            return analysisExtract.getRangeExtract().getAnalysis().getPlot();

        if (analysisExtract.getLayerExtract() != null)
            return analysisExtract.getLayerExtract().getAnalysis().getPlot();

        throw new IllegalStateException("Extrato de análise de saturação não possui extrato base associado.");
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private RangeExtractModel findRangeExtractByIdOrThrow(Long rangeExtractId) {
        return rangeExtractRepository.findById(rangeExtractId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Extrato por intervalo não encontrado com o ID: " + rangeExtractId)
                );
    }

    private LayerExtractModel findLayerExtractByIdOrThrow(Long layerExtractId) {
        return layerExtractRepository.findById(layerExtractId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Extrato por camada não encontrado com o ID: " + layerExtractId)
                );
    }

    private SaturationExtractAnalysisExtractModel findSaturationExtractAnalysisExtractByIdOrThrow(Long id) {
        return saturationExtractAnalysisExtractRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Extrato de análise de saturação não encontrado com o ID: " + id)
                );
    }

    private record ExtractContext(RangeExtractModel rangeExtract, LayerExtractModel layerExtract, PlotModel plot) {}
}