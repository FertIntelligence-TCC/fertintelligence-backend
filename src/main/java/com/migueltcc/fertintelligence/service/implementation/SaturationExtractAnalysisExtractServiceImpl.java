package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.extractAnalysis.saturationExtract.SaturationExtractAnalysisExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.saturationExtract.SaturationExtractAnalysisExtractPostRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.saturationExtract.SaturationExtractAnalysisExtractResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.SaturationExtractAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.LayerExtractRepository;
import com.migueltcc.fertintelligence.repository.PlotAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.RangeExtractRepository;
import com.migueltcc.fertintelligence.repository.SaturationExtractAnalysisExtractRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.SaturationExtractAnalysisExtractService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class SaturationExtractAnalysisExtractServiceImpl implements SaturationExtractAnalysisExtractService {

    @Autowired
    private SaturationExtractAnalysisExtractRepository saturationExtractAnalysisExtractRepository;

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
    public SaturationExtractAnalysisExtractResponseDto createSaturationExtractAnalysisExtract(
            Long rangeExtractId,
            Long layerExtractId,
            SaturationExtractAnalysisExtractCreateRequestDto createRequestDto,
            String username
    ) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        ExtractContext extractContext = resolveExtractContext(rangeExtractId, layerExtractId);
        checkOwnerPermission(extractContext.plot(), owner);

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

        SaturationExtractAnalysisExtractModel savedExtract = saturationExtractAnalysisExtractRepository.save(analysisExtract);
        return savedExtract.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public SaturationExtractAnalysisExtractResponseDto getSaturationExtractAnalysisExtractById(
            Long saturationExtractAnalysisExtractId,
            String username
    ) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        SaturationExtractAnalysisExtractModel analysisExtract =
                findSaturationExtractAnalysisExtractByIdOrThrow(saturationExtractAnalysisExtractId);

        checkOwnerPermission(resolvePlot(analysisExtract), owner);

        return analysisExtract.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaturationExtractAnalysisExtractResponseDto> getSaturationExtractAnalysisExtractsByRange(
            Long rangeExtractId,
            String username
    ) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        RangeExtractModel rangeExtract = findRangeExtractByIdOrThrow(rangeExtractId);
        checkOwnerPermission(rangeExtract.getAnalysis().getPlot(), owner);

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
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        LayerExtractModel layerExtract = findLayerExtractByIdOrThrow(layerExtractId);
        checkOwnerPermission(layerExtract.getAnalysis().getPlot(), owner);

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
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        SaturationExtractAnalysisExtractModel analysisExtract =
                findSaturationExtractAnalysisExtractByIdOrThrow(saturationExtractAnalysisExtractId);

        checkOwnerPermission(resolvePlot(analysisExtract), owner);

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

        SaturationExtractAnalysisExtractModel updatedExtract =
                saturationExtractAnalysisExtractRepository.save(analysisExtract);

        return updatedExtract.toDto();
    }

    @Override
    @Transactional
    public void deleteSaturationExtractAnalysisExtract(Long saturationExtractAnalysisExtractId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        SaturationExtractAnalysisExtractModel analysisExtract =
                findSaturationExtractAnalysisExtractByIdOrThrow(saturationExtractAnalysisExtractId);

        checkOwnerPermission(resolvePlot(analysisExtract), owner);

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

    private void checkUserIsProprietario(UserModel user) {
        if (user.getCargo() != Cargo.PROPRIETARIO &&
                user.getCargo() != Cargo.GERENTE &&
                user.getCargo() != Cargo.AGRONOMO_RESIDENTE &&
                user.getCargo() != Cargo.AGRONOMO_CONSULTOR &&
                user.getCargo() != Cargo.SECRETARIO) {
            throw new AccessDeniedException(
                    "Acesso negado. Apenas proprietários, gerentes, agrônomos consultores ou secretários podem gerenciar extratos de análises."
            );
        }
    }

    private void checkOwnerPermission(PlotModel plot, UserModel requestingUser) {
        PropertyModel property = plot.getProperty();

        if (property.getOwner().getId().equals(requestingUser.getId())) {
            return;
        }

        if (property.getManager() != null &&
                property.getManager().getId().equals(requestingUser.getId())) {
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

    private SaturationExtractAnalysisExtractModel findSaturationExtractAnalysisExtractByIdOrThrow(
            Long id
    ) {
        return saturationExtractAnalysisExtractRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Extrato de análise de saturação não encontrado com o ID: " + id)
                );
    }

    private record ExtractContext(RangeExtractModel rangeExtract, LayerExtractModel layerExtract, PlotModel plot) {}
}
