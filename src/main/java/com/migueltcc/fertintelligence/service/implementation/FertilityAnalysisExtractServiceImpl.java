package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.dto.extractAnalysis.fertility.FertilityAnalysisExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.fertility.FertilityAnalysisExtractPostRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.fertility.FertilityAnalysisExtractResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.FertilityAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
import com.migueltcc.fertintelligence.repository.FertilityAnalysisExtractRepository;
import com.migueltcc.fertintelligence.repository.LayerExtractRepository;
import com.migueltcc.fertintelligence.repository.RangeExtractRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.security.PermissionManager;
import com.migueltcc.fertintelligence.service.documentation.FertilityAnalysisExtractService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FertilityAnalysisExtractServiceImpl implements FertilityAnalysisExtractService {

    private final FertilityAnalysisExtractRepository fertilityAnalysisExtractRepository;
    private final RangeExtractRepository rangeExtractRepository;
    private final LayerExtractRepository layerExtractRepository;
    private final UserRepository userRepository;
    private final PermissionManager permissionManager;

    @Override
    @Transactional
    public FertilityAnalysisExtractResponseDto createFertilityAnalysisExtract(
            Long rangeExtractId,
            Long layerExtractId,
            FertilityAnalysisExtractCreateRequestDto createRequestDto,
            String username
    ) {
        UserModel requester = findUserByUsernameOrThrow(username);

        ExtractContext ctx = resolveExtractContext(rangeExtractId, layerExtractId);
        permissionManager.assertCanWrite(ctx.plot(), requester);

        FertilityAnalysisExtractModel analysisExtract = FertilityAnalysisExtractModel.builder()
                .rangeExtract(ctx.rangeExtract())
                .layerExtract(ctx.layerExtract())
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

        return fertilityAnalysisExtractRepository.save(analysisExtract).toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public FertilityAnalysisExtractResponseDto getFertilityAnalysisExtractById(Long fertilityAnalysisExtractId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        FertilityAnalysisExtractModel analysisExtract = findFertilityAnalysisExtractByIdOrThrow(fertilityAnalysisExtractId);
        permissionManager.assertCanRead(resolvePlot(analysisExtract), requester);

        return analysisExtract.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FertilityAnalysisExtractResponseDto> getFertilityAnalysisExtractsByRange(Long rangeExtractId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        RangeExtractModel rangeExtract = findRangeExtractByIdOrThrow(rangeExtractId);
        permissionManager.assertCanRead(rangeExtract.getAnalysis().getPlot(), requester);

        return fertilityAnalysisExtractRepository.findAllByRangeExtract(rangeExtract)
                .stream()
                .map(FertilityAnalysisExtractModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FertilityAnalysisExtractResponseDto> getFertilityAnalysisExtractsByLayer(Long layerExtractId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        LayerExtractModel layerExtract = findLayerExtractByIdOrThrow(layerExtractId);
        permissionManager.assertCanRead(layerExtract.getAnalysis().getPlot(), requester);

        return fertilityAnalysisExtractRepository.findAllByLayerExtract(layerExtract)
                .stream()
                .map(FertilityAnalysisExtractModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FertilityAnalysisExtractResponseDto updateFertilityAnalysisExtract(
            Long fertilityAnalysisExtractId,
            FertilityAnalysisExtractPostRequestDto updateRequestDto,
            String username
    ) {
        UserModel requester = findUserByUsernameOrThrow(username);

        FertilityAnalysisExtractModel analysisExtract = findFertilityAnalysisExtractByIdOrThrow(fertilityAnalysisExtractId);
        permissionManager.assertCanWrite(resolvePlot(analysisExtract), requester);

        updateIfNotNull(updateRequestDto.getPhAgua(), analysisExtract::setPhAgua);
        updateIfNotNull(updateRequestDto.getPhCacl2(), analysisExtract::setPhCacl2);
        updateIfNotNull(updateRequestDto.getCalcio(), analysisExtract::setCalcio);
        updateIfNotNull(updateRequestDto.getMagnesio(), analysisExtract::setMagnesio);
        updateIfNotNull(updateRequestDto.getPotassio(), analysisExtract::setPotassio);
        updateIfNotNull(updateRequestDto.getSodio(), analysisExtract::setSodio);
        updateIfNotNull(updateRequestDto.getAluminio(), analysisExtract::setAluminio);
        updateIfNotNull(updateRequestDto.getAluminioMaisHidrogenio(), analysisExtract::setAluminioMaisHidrogenio);
        updateIfNotNull(updateRequestDto.getSomaBases(), analysisExtract::setSomaBases);
        updateIfNotNull(updateRequestDto.getCtcEfetiva(), analysisExtract::setCtcEfetiva);
        updateIfNotNull(updateRequestDto.getCtcPh7(), analysisExtract::setCtcPh7);
        updateIfNotNull(updateRequestDto.getSaturacaoBasesV(), analysisExtract::setSaturacaoBasesV);
        updateIfNotNull(updateRequestDto.getSaturacaoAluminioM(), analysisExtract::setSaturacaoAluminioM);
        updateIfNotNull(updateRequestDto.getFosforoMehlich1(), analysisExtract::setFosforoMehlich1);
        updateIfNotNull(updateRequestDto.getFosforoResina(), analysisExtract::setFosforoResina);
        updateIfNotNull(updateRequestDto.getEnxofre(), analysisExtract::setEnxofre);
        updateIfNotNull(updateRequestDto.getMateriaOrganica(), analysisExtract::setMateriaOrganica);
        updateIfNotNull(updateRequestDto.getBoro(), analysisExtract::setBoro);
        updateIfNotNull(updateRequestDto.getCobre(), analysisExtract::setCobre);
        updateIfNotNull(updateRequestDto.getFerro(), analysisExtract::setFerro);
        updateIfNotNull(updateRequestDto.getManganes(), analysisExtract::setManganes);
        updateIfNotNull(updateRequestDto.getMolibdenio(), analysisExtract::setMolibdenio);
        updateIfNotNull(updateRequestDto.getZinco(), analysisExtract::setZinco);

        return fertilityAnalysisExtractRepository.save(analysisExtract).toDto();
    }

    @Override
    @Transactional
    public void deleteFertilityAnalysisExtract(Long fertilityAnalysisExtractId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        FertilityAnalysisExtractModel analysisExtract = findFertilityAnalysisExtractByIdOrThrow(fertilityAnalysisExtractId);
        permissionManager.assertCanWrite(resolvePlot(analysisExtract), requester);

        fertilityAnalysisExtractRepository.delete(analysisExtract);
    }

    private void updateIfNotNull(Double value, Consumer<Double> setter) {
        if (value != null) setter.accept(value);
    }

    private ExtractContext resolveExtractContext(Long rangeExtractId, Long layerExtractId) {
        boolean noneProvided = (rangeExtractId == null && layerExtractId == null);
        boolean bothProvided = (rangeExtractId != null && layerExtractId != null);

        if (noneProvided || bothProvided) {
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

    private FertilityAnalysisExtractModel findFertilityAnalysisExtractByIdOrThrow(Long id) {
        return fertilityAnalysisExtractRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Extrato de análise de fertilidade não encontrado com o ID: " + id));
    }

    private record ExtractContext(RangeExtractModel rangeExtract, LayerExtractModel layerExtract, PlotModel plot) {}
}