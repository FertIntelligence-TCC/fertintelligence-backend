package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.extractAnalysis.fertility.FertilityAnalysisExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.fertility.FertilityAnalysisExtractPostRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.fertility.FertilityAnalysisExtractResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.FertilityAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.FertilityAnalysisExtractRepository;
import com.migueltcc.fertintelligence.repository.LayerExtractRepository;
import com.migueltcc.fertintelligence.repository.PlotAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.PropertyAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.RangeExtractRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.FertilityAnalysisExtractService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class FertilityAnalysisExtractServiceImpl implements FertilityAnalysisExtractService {

    @Autowired
    private FertilityAnalysisExtractRepository fertilityAnalysisExtractRepository;

    @Autowired
    private RangeExtractRepository rangeExtractRepository;

    @Autowired
    private LayerExtractRepository layerExtractRepository;

    @Autowired
    private PlotAccessRequestRepository plotAccessRequestRepository;

    @Autowired
    private PropertyAccessRequestRepository propertyAccessRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public FertilityAnalysisExtractResponseDto createFertilityAnalysisExtract(Long rangeExtractId,
                                                                              Long layerExtractId,
                                                                              FertilityAnalysisExtractCreateRequestDto createRequestDto,
                                                                              String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        ExtractContext extractContext = resolveExtractContext(rangeExtractId, layerExtractId);
        checkOwnerPermission(extractContext.plot(), owner);

        FertilityAnalysisExtractModel analysisExtract = FertilityAnalysisExtractModel.builder()
                .rangeExtract(extractContext.rangeExtract())
                .layerExtract(extractContext.layerExtract())
                .phAgua(createRequestDto.getPhAgua())
                .phCacl2(createRequestDto.getPhCacl2())
                .calcio(createRequestDto.getCalcio())
                .magnesio(createRequestDto.getMagnesio())
                .potassio(createRequestDto.getPotassio())
                .sodio(createRequestDto.getSodio())
                .aluminio(createRequestDto.getAluminio())
                .aluminioMaisHidrogenio(createRequestDto.getAluminioMaisHidrogenio())
                .somaBases(createRequestDto.getSomaBases())
                .ctcEfetiva(createRequestDto.getCtcEfetiva())
                .ctcPh7(createRequestDto.getCtcPh7())
                .saturacaoBasesV(createRequestDto.getSaturacaoBasesV())
                .saturacaoAluminioM(createRequestDto.getSaturacaoAluminioM())
                .fosforoMehlich1(createRequestDto.getFosforoMehlich1())
                .fosforoResina(createRequestDto.getFosforoResina())
                .enxofre(createRequestDto.getEnxofre())
                .materiaOrganica(createRequestDto.getMateriaOrganica())
                .boro(createRequestDto.getBoro())
                .cobre(createRequestDto.getCobre())
                .ferro(createRequestDto.getFerro())
                .manganes(createRequestDto.getManganes())
                .molibdenio(createRequestDto.getMolibdenio())
                .zinco(createRequestDto.getZinco())
                .build();

        FertilityAnalysisExtractModel savedExtract = fertilityAnalysisExtractRepository.save(analysisExtract);
        return savedExtract.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public FertilityAnalysisExtractResponseDto getFertilityAnalysisExtractById(Long fertilityAnalysisExtractId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        FertilityAnalysisExtractModel analysisExtract = findFertilityAnalysisExtractByIdOrThrow(fertilityAnalysisExtractId);
        checkOwnerPermission(resolvePlot(analysisExtract), owner);

        return analysisExtract.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FertilityAnalysisExtractResponseDto> getFertilityAnalysisExtractsByRange(Long rangeExtractId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        RangeExtractModel rangeExtract = findRangeExtractByIdOrThrow(rangeExtractId);
        checkOwnerPermission(rangeExtract.getAnalysis().getPlot(), owner);

        return fertilityAnalysisExtractRepository.findAllByRangeExtract(rangeExtract).stream()
                .map(FertilityAnalysisExtractModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FertilityAnalysisExtractResponseDto> getFertilityAnalysisExtractsByLayer(Long layerExtractId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        LayerExtractModel layerExtract = findLayerExtractByIdOrThrow(layerExtractId);
        checkOwnerPermission(layerExtract.getAnalysis().getPlot(), owner);

        return fertilityAnalysisExtractRepository.findAllByLayerExtract(layerExtract).stream()
                .map(FertilityAnalysisExtractModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FertilityAnalysisExtractResponseDto updateFertilityAnalysisExtract(Long fertilityAnalysisExtractId,
                                                                              FertilityAnalysisExtractPostRequestDto updateRequestDto,
                                                                              String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        FertilityAnalysisExtractModel analysisExtract = findFertilityAnalysisExtractByIdOrThrow(fertilityAnalysisExtractId);
        checkOwnerPermission(resolvePlot(analysisExtract), owner);

        updateField(updateRequestDto.getPhAgua(), analysisExtract::setPhAgua);
        updateField(updateRequestDto.getPhCacl2(), analysisExtract::setPhCacl2);
        updateField(updateRequestDto.getCalcio(), analysisExtract::setCalcio);
        updateField(updateRequestDto.getMagnesio(), analysisExtract::setMagnesio);
        updateField(updateRequestDto.getPotassio(), analysisExtract::setPotassio);
        updateField(updateRequestDto.getSodio(), analysisExtract::setSodio);
        updateField(updateRequestDto.getAluminio(), analysisExtract::setAluminio);
        updateField(updateRequestDto.getAluminioMaisHidrogenio(), analysisExtract::setAluminioMaisHidrogenio);
        updateField(updateRequestDto.getSomaBases(), analysisExtract::setSomaBases);
        updateField(updateRequestDto.getCtcEfetiva(), analysisExtract::setCtcEfetiva);
        updateField(updateRequestDto.getCtcPh7(), analysisExtract::setCtcPh7);
        updateField(updateRequestDto.getSaturacaoBasesV(), analysisExtract::setSaturacaoBasesV);
        updateField(updateRequestDto.getSaturacaoAluminioM(), analysisExtract::setSaturacaoAluminioM);
        updateField(updateRequestDto.getFosforoMehlich1(), analysisExtract::setFosforoMehlich1);
        updateField(updateRequestDto.getFosforoResina(), analysisExtract::setFosforoResina);
        updateField(updateRequestDto.getEnxofre(), analysisExtract::setEnxofre);
        updateField(updateRequestDto.getMateriaOrganica(), analysisExtract::setMateriaOrganica);
        updateField(updateRequestDto.getBoro(), analysisExtract::setBoro);
        updateField(updateRequestDto.getCobre(), analysisExtract::setCobre);
        updateField(updateRequestDto.getFerro(), analysisExtract::setFerro);
        updateField(updateRequestDto.getManganes(), analysisExtract::setManganes);
        updateField(updateRequestDto.getMolibdenio(), analysisExtract::setMolibdenio);
        updateField(updateRequestDto.getZinco(), analysisExtract::setZinco);

        FertilityAnalysisExtractModel updatedExtract = fertilityAnalysisExtractRepository.save(analysisExtract);
        return updatedExtract.toDto();
    }

    @Override
    @Transactional
    public void deleteFertilityAnalysisExtract(Long fertilityAnalysisExtractId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserRole(owner);

        FertilityAnalysisExtractModel analysisExtract = findFertilityAnalysisExtractByIdOrThrow(fertilityAnalysisExtractId);
        checkOwnerPermission(resolvePlot(analysisExtract), owner);

        fertilityAnalysisExtractRepository.delete(analysisExtract);
    }

    private void updateField(Double value, Consumer<Double> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    private ExtractContext resolveExtractContext(Long rangeExtractId, Long layerExtractId) {
        if ((rangeExtractId == null && layerExtractId == null) || (rangeExtractId != null && layerExtractId != null)) {
            throw new IllegalArgumentException("Informe exatamente um extrato base (intervalo ou camada).");
        }

        if (rangeExtractId != null) {
            RangeExtractModel rangeExtract = findRangeExtractByIdOrThrow(rangeExtractId);
            return new ExtractContext(rangeExtract, null, rangeExtract.getAnalysis().getPlot());
        }

        LayerExtractModel layerExtract = findLayerExtractByIdOrThrow(layerExtractId);
        return new ExtractContext(null, layerExtract, layerExtract.getAnalysis().getPlot());
    }

    private PlotModel resolvePlot(FertilityAnalysisExtractModel analysisExtract) {
        if (analysisExtract.getRangeExtract() != null) {
            return analysisExtract.getRangeExtract().getAnalysis().getPlot();
        }

        if (analysisExtract.getLayerExtract() != null) {
            return analysisExtract.getLayerExtract().getAnalysis().getPlot();
        }

        throw new IllegalStateException("Extrato de análise de fertilidade não possui extrato base associado.");
    }

    private void checkUserRole(UserModel user) {
        if (user.getCargo() != Cargo.PROPRIETARIO
                && user.getCargo() != Cargo.GERENTE
                && user.getCargo() != Cargo.AGRONOMO_RESIDENTE
                && user.getCargo() != Cargo.AGRONOMO_CONSULTOR
                && user.getCargo() != Cargo.SECRETARIO) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este recurso.");
        }
    }

    private void checkOwnerPermission(PlotModel plot, UserModel requestingUser) {
        PropertyModel property = plot.getProperty();

        if (property.getOwner().getId().equals(requestingUser.getId())) {
            return;
        }

        if (property.getManager() != null && property.getManager().getId().equals(requestingUser.getId())) {
            return;
        }

        if (requestingUser.getCargo() == Cargo.AGRONOMO_RESIDENTE) {
            boolean hasApprovedPropertyAccess = propertyAccessRequestRepository.findByPropertyAndRequesterAndStatus(
                    property,
                    requestingUser,
                    AccessRequestStatus.APPROVED
            ).isPresent();

            if (!hasApprovedPropertyAccess) {
                throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este recurso.");
            }
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

    private FertilityAnalysisExtractModel findFertilityAnalysisExtractByIdOrThrow(Long fertilityAnalysisExtractId) {
        return fertilityAnalysisExtractRepository.findById(fertilityAnalysisExtractId)
                .orElseThrow(() -> new EntityNotFoundException("Extrato de análise de fertilidade não encontrado com o ID: " + fertilityAnalysisExtractId));
    }

    private record ExtractContext(RangeExtractModel rangeExtract, LayerExtractModel layerExtract, PlotModel plot) {
    }
}