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
import com.migueltcc.fertintelligence.repository.PlotRepository;
import com.migueltcc.fertintelligence.repository.RangeExtractRepository;
import com.migueltcc.fertintelligence.repository.SaturationExtractAnalysisExtractRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.SaturationExtractAnalysisExtractService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SaturationExtractAnalysisExtractServiceImpl implements SaturationExtractAnalysisExtractService {

    private final SaturationExtractAnalysisExtractRepository saturationRepo;
    private final RangeExtractRepository rangeRepo;
    private final LayerExtractRepository layerRepo;
    private final PlotRepository plotRepo;
    private final UserRepository userRepo;

    // PermissionManager (service.implementation)
    private final PermissionManager permissionManager;

    /* ======================================================
       CREATE (WRITE -> ANALYSES)
    ====================================================== */

    @Override
    @Transactional
    public SaturationExtractAnalysisExtractResponseDto createSaturationExtractAnalysisExtract(
            Long rangeExtractId,
            Long layerExtractId,
            SaturationExtractAnalysisExtractCreateRequestDto dto,
            String username
    ) {
        UserModel requester = findUser(username);

        ExtractContext ctx = resolveExtractContext(rangeExtractId, layerExtractId);

        // ENFORCEMENT: editar análises
        permissionManager.assertCanEditAnalyses(ctx.plot().getProperty(), ctx.plot(), requester);

        SaturationExtractAnalysisExtractModel model = SaturationExtractAnalysisExtractModel.builder()
                .rangeExtract(ctx.rangeExtract())
                .layerExtract(ctx.layerExtract())
                .ph(dto.getPh())
                .ce(dto.getCe())
                .teorCO3(dto.getTeorCO3())
                .teorHCO3(dto.getTeorHCO3())
                .teorNO3(dto.getTeorNO3())
                .teorH2PO4(dto.getTeorH2PO4())
                .teorSO4(dto.getTeorSO4())
                .teorCl(dto.getTeorCl())
                .teorNa(dto.getTeorNa())
                .teorK(dto.getTeorK())
                .teorCa(dto.getTeorCa())
                .teorMg(dto.getTeorMg())
                .residuosSuspensao(dto.getResiduosSuspensao())
                .durezaCaCO3(dto.getDurezaCaCO3())
                .durezaTotalCaCO3(dto.getDurezaTotalCaCO3())
                .ras(dto.getRas())
                .pst(dto.getPst())
                .build();

        return saturationRepo.save(model).toDto();
    }

    /* ======================================================
       READ (READ -> PLOT)
    ====================================================== */

    @Override
    @Transactional(readOnly = true)
    public SaturationExtractAnalysisExtractResponseDto getSaturationExtractAnalysisExtractById(
            Long id,
            String username
    ) {
        UserModel requester = findUser(username);

        SaturationExtractAnalysisExtractModel model = findById(id);
        PlotModel plot = resolvePlot(model);

        // ENFORCEMENT: leitura do talhão
        permissionManager.assertCanReadPlot(plot, requester);

        return model.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaturationExtractAnalysisExtractResponseDto> getSaturationExtractAnalysisExtractsByRange(
            Long rangeExtractId,
            String username
    ) {
        UserModel requester = findUser(username);

        RangeExtractModel range = findRange(rangeExtractId);
        PlotModel plot = range.getAnalysis().getPlot();

        // ENFORCEMENT: leitura do talhão
        permissionManager.assertCanReadPlot(plot, requester);

        return saturationRepo.findAllByRangeExtract(range).stream()
                .map(SaturationExtractAnalysisExtractModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaturationExtractAnalysisExtractResponseDto> getSaturationExtractAnalysisExtractsByLayer(
            Long layerExtractId,
            String username
    ) {
        UserModel requester = findUser(username);

        LayerExtractModel layer = findLayer(layerExtractId);
        PlotModel plot = layer.getAnalysis().getPlot();

        // ENFORCEMENT: leitura do talhão
        permissionManager.assertCanReadPlot(plot, requester);

        return saturationRepo.findAllByLayerExtract(layer).stream()
                .map(SaturationExtractAnalysisExtractModel::toDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<SaturationExtractAnalysisExtractResponseDto> getSaturationExtractAnalysisExtractsByPlot(
            Long plotId,
            String username
    ) {
        UserModel requester = findUser(username);
        PlotModel plot = findPlot(plotId);

        permissionManager.assertCanReadPlot(plot, requester);

        return Stream.concat(
                        saturationRepo.findAllByRangeExtractAnalysisPlot(plot).stream(),
                        saturationRepo.findAllByLayerExtractAnalysisPlot(plot).stream()
                )
                .map(SaturationExtractAnalysisExtractModel::toDto)
                .collect(Collectors.toList());
    }

    /* ======================================================
       UPDATE (WRITE -> ANALYSES)
    ====================================================== */

    @Override
    @Transactional
    public SaturationExtractAnalysisExtractResponseDto updateSaturationExtractAnalysisExtract(
            Long id,
            SaturationExtractAnalysisExtractPostRequestDto dto,
            String username
    ) {
        UserModel requester = findUser(username);

        SaturationExtractAnalysisExtractModel model = findById(id);
        PlotModel plot = resolvePlot(model);

        // ENFORCEMENT: editar análises
        permissionManager.assertCanEditAnalyses(plot.getProperty(), plot, requester);

        applyIfNotNull(dto.getPh(), model::setPh);
        applyIfNotNull(dto.getCe(), model::setCe);
        applyIfNotNull(dto.getTeorCO3(), model::setTeorCO3);
        applyIfNotNull(dto.getTeorHCO3(), model::setTeorHCO3);
        applyIfNotNull(dto.getTeorNO3(), model::setTeorNO3);
        applyIfNotNull(dto.getTeorH2PO4(), model::setTeorH2PO4);
        applyIfNotNull(dto.getTeorSO4(), model::setTeorSO4);
        applyIfNotNull(dto.getTeorCl(), model::setTeorCl);
        applyIfNotNull(dto.getTeorNa(), model::setTeorNa);
        applyIfNotNull(dto.getTeorK(), model::setTeorK);
        applyIfNotNull(dto.getTeorCa(), model::setTeorCa);
        applyIfNotNull(dto.getTeorMg(), model::setTeorMg);
        applyIfNotNull(dto.getResiduosSuspensao(), model::setResiduosSuspensao);
        applyIfNotNull(dto.getDurezaCaCO3(), model::setDurezaCaCO3);
        applyIfNotNull(dto.getDurezaTotalCaCO3(), model::setDurezaTotalCaCO3);
        applyIfNotNull(dto.getRas(), model::setRas);
        applyIfNotNull(dto.getPst(), model::setPst);

        return saturationRepo.save(model).toDto();
    }

    /* ======================================================
       DELETE (WRITE -> ANALYSES)
    ====================================================== */

    @Override
    @Transactional
    public void deleteSaturationExtractAnalysisExtract(Long id, String username) {
        UserModel requester = findUser(username);

        SaturationExtractAnalysisExtractModel model = findById(id);
        PlotModel plot = resolvePlot(model);

        // ENFORCEMENT: editar análises
        permissionManager.assertCanEditAnalyses(plot.getProperty(), plot, requester);

        saturationRepo.delete(model);
    }

    /* ======================================================
       Helpers
    ====================================================== */

    private static <T> void applyIfNotNull(T value, Consumer<T> setter) {
        if (value != null) setter.accept(value);
    }

    private ExtractContext resolveExtractContext(Long rangeExtractId, Long layerExtractId) {
        boolean hasRange = rangeExtractId != null;
        boolean hasLayer = layerExtractId != null;

        if (hasRange == hasLayer) {
            throw new IllegalArgumentException("Informe exatamente um extrato base (intervalo ou camada).");
        }

        if (hasRange) {
            RangeExtractModel range = findRange(rangeExtractId);
            return new ExtractContext(range, null, range.getAnalysis().getPlot());
        }

        LayerExtractModel layer = findLayer(layerExtractId);
        return new ExtractContext(null, layer, layer.getAnalysis().getPlot());
    }

    private PlotModel resolvePlot(SaturationExtractAnalysisExtractModel model) {
        if (model.getRangeExtract() != null) {
            return model.getRangeExtract().getAnalysis().getPlot();
        }
        if (model.getLayerExtract() != null) {
            return model.getLayerExtract().getAnalysis().getPlot();
        }
        throw new IllegalStateException("Extrato de análise de saturação não possui extrato base associado.");
    }

    private UserModel findUser(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private RangeExtractModel findRange(Long id) {
        return rangeRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Extrato por intervalo não encontrado com o ID: " + id));
    }

    private LayerExtractModel findLayer(Long id) {
        return layerRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Extrato por camada não encontrado com o ID: " + id));
    }

    private PlotModel findPlot(Long id) {
        return plotRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Talhão não encontrado com o ID: " + id));
    }

    private SaturationExtractAnalysisExtractModel findById(Long id) {
        return saturationRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Extrato de análise de saturação não encontrado com o ID: " + id
                ));
    }

    private record ExtractContext(RangeExtractModel rangeExtract, LayerExtractModel layerExtract, PlotModel plot) {}
}
