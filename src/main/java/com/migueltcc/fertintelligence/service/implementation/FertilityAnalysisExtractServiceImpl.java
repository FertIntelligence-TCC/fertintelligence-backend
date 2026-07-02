package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.dto.extractAnalysis.fertility.FertilityAnalysisExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.fertility.FertilityAnalysisExtractPostRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.fertility.FertilityAnalysisExtractResponseDto;
import com.migueltcc.fertintelligence.composedAttributes.fertilityAnalysis.FertilityAnalysisUnit;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.FertilityAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
import com.migueltcc.fertintelligence.repository.FertilityAnalysisExtractRepository;
import com.migueltcc.fertintelligence.repository.LayerExtractRepository;
import com.migueltcc.fertintelligence.repository.RangeExtractRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.FertilityAnalysisExtractService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FertilityAnalysisExtractServiceImpl implements FertilityAnalysisExtractService {

    private static final FertilityAnalysisUnit DEFAULT_FERTILITY_UNIT = FertilityAnalysisUnit.MMOLC_PER_DM3;

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
        PlotModel plot = ctx.plot();

        // WRITE (ANÁLISES)
        permissionManager.assertCanEditAnalyses(plot.getProperty(), plot, requester);

        FertilityAnalysisExtractModel analysisExtract = FertilityAnalysisExtractModel.builder()
                .rangeExtract(ctx.rangeExtract())
                .layerExtract(ctx.layerExtract())
                .phAgua(createRequestDto.getPhAgua())
                .phCacl2(createRequestDto.getPhCacl2())
                .calcio(createRequestDto.getCalcio())
                .unidadeCalcio(normalizeFertilityUnit(createRequestDto.getUnidadeCalcio()))
                .magnesio(createRequestDto.getMagnesio())
                .unidadeMagnesio(normalizeFertilityUnit(createRequestDto.getUnidadeMagnesio()))
                .potassio(createRequestDto.getPotassio())
                .unidadePotassio(normalizeFertilityUnit(createRequestDto.getUnidadePotassio()))
                .sodio(createRequestDto.getSodio())
                .unidadeSodio(normalizeFertilityUnit(createRequestDto.getUnidadeSodio()))
                .aluminio(createRequestDto.getAluminio())
                .unidadeAluminio(normalizeFertilityUnit(createRequestDto.getUnidadeAluminio()))
                .aluminioMaisHidrogenio(createRequestDto.getAluminioMaisHidrogenio())
                .unidadeAluminioMaisHidrogenio(normalizeFertilityUnit(createRequestDto.getUnidadeAluminioMaisHidrogenio()))
                .unidadeSomaBases(normalizeFertilityUnit(createRequestDto.getUnidadeSomaBases()))
                .unidadeCtcEfetiva(normalizeFertilityUnit(createRequestDto.getUnidadeCtcEfetiva()))
                .unidadeCtcPh7(normalizeFertilityUnit(createRequestDto.getUnidadeCtcPh7()))
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

        recalculateExchangeComplex(analysisExtract);

        return toResponseDto(fertilityAnalysisExtractRepository.save(analysisExtract));
    }

    @Override
    @Transactional(readOnly = true)
    public FertilityAnalysisExtractResponseDto getFertilityAnalysisExtractById(Long fertilityAnalysisExtractId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        FertilityAnalysisExtractModel analysisExtract =
                findFertilityAnalysisExtractByIdOrThrow(fertilityAnalysisExtractId);

        PlotModel plot = resolvePlot(analysisExtract);

        // READ
        permissionManager.assertCanReadPlot(plot, requester);

        return toResponseDto(analysisExtract);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FertilityAnalysisExtractResponseDto> getFertilityAnalysisExtractsByRange(Long rangeExtractId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        RangeExtractModel rangeExtract = findRangeExtractByIdOrThrow(rangeExtractId);
        PlotModel plot = rangeExtract.getAnalysis().getPlot();

        // READ
        permissionManager.assertCanReadPlot(plot, requester);

        return fertilityAnalysisExtractRepository.findAllByRangeExtract(rangeExtract)
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FertilityAnalysisExtractResponseDto> getFertilityAnalysisExtractsByLayer(Long layerExtractId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        LayerExtractModel layerExtract = findLayerExtractByIdOrThrow(layerExtractId);
        PlotModel plot = layerExtract.getAnalysis().getPlot();

        // READ
        permissionManager.assertCanReadPlot(plot, requester);

        return fertilityAnalysisExtractRepository.findAllByLayerExtract(layerExtract)
                .stream()
                .map(this::toResponseDto)
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

        FertilityAnalysisExtractModel analysisExtract =
                findFertilityAnalysisExtractByIdOrThrow(fertilityAnalysisExtractId);

        PlotModel plot = resolvePlot(analysisExtract);

        // WRITE (ANÁLISES)
        permissionManager.assertCanEditAnalyses(plot.getProperty(), plot, requester);

        updateIfNotNull(updateRequestDto.getPhAgua(), analysisExtract::setPhAgua);
        updateIfNotNull(updateRequestDto.getPhCacl2(), analysisExtract::setPhCacl2);
        updateIfNotNull(updateRequestDto.getCalcio(), analysisExtract::setCalcio);
        updateIfNotNull(updateRequestDto.getUnidadeCalcio(), unit -> analysisExtract.setUnidadeCalcio(normalizeFertilityUnit(unit)));
        updateIfNotNull(updateRequestDto.getMagnesio(), analysisExtract::setMagnesio);
        updateIfNotNull(updateRequestDto.getUnidadeMagnesio(), unit -> analysisExtract.setUnidadeMagnesio(normalizeFertilityUnit(unit)));
        updateIfNotNull(updateRequestDto.getPotassio(), analysisExtract::setPotassio);
        updateIfNotNull(updateRequestDto.getUnidadePotassio(), unit -> analysisExtract.setUnidadePotassio(normalizeFertilityUnit(unit)));
        updateIfNotNull(updateRequestDto.getSodio(), analysisExtract::setSodio);
        updateIfNotNull(updateRequestDto.getUnidadeSodio(), unit -> analysisExtract.setUnidadeSodio(normalizeFertilityUnit(unit)));
        updateIfNotNull(updateRequestDto.getAluminio(), analysisExtract::setAluminio);
        updateIfNotNull(updateRequestDto.getUnidadeAluminio(), unit -> analysisExtract.setUnidadeAluminio(normalizeFertilityUnit(unit)));
        updateIfNotNull(updateRequestDto.getAluminioMaisHidrogenio(), analysisExtract::setAluminioMaisHidrogenio);
        updateIfNotNull(updateRequestDto.getUnidadeAluminioMaisHidrogenio(), unit -> analysisExtract.setUnidadeAluminioMaisHidrogenio(normalizeFertilityUnit(unit)));
        updateIfNotNull(updateRequestDto.getUnidadeSomaBases(), unit -> analysisExtract.setUnidadeSomaBases(normalizeFertilityUnit(unit)));
        updateIfNotNull(updateRequestDto.getUnidadeCtcEfetiva(), unit -> analysisExtract.setUnidadeCtcEfetiva(normalizeFertilityUnit(unit)));
        updateIfNotNull(updateRequestDto.getUnidadeCtcPh7(), unit -> analysisExtract.setUnidadeCtcPh7(normalizeFertilityUnit(unit)));
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

        recalculateExchangeComplex(analysisExtract);

        return toResponseDto(fertilityAnalysisExtractRepository.save(analysisExtract));
    }

    @Override
    @Transactional
    public void deleteFertilityAnalysisExtract(Long fertilityAnalysisExtractId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        FertilityAnalysisExtractModel analysisExtract =
                findFertilityAnalysisExtractByIdOrThrow(fertilityAnalysisExtractId);

        PlotModel plot = resolvePlot(analysisExtract);

        // WRITE (ANÁLISES)
        permissionManager.assertCanEditAnalyses(plot.getProperty(), plot, requester);

        fertilityAnalysisExtractRepository.delete(analysisExtract);
        fertilityAnalysisExtractRepository.flush();
    }

    private void updateIfNotNull(Double value, Consumer<Double> setter) {
        if (value != null) setter.accept(value);
    }

    private void updateIfNotNull(FertilityAnalysisUnit value, Consumer<FertilityAnalysisUnit> setter) {
        if (value != null) setter.accept(value);
    }

    private FertilityAnalysisExtractResponseDto toResponseDto(FertilityAnalysisExtractModel analysisExtract) {
        ExchangeComplexCalculatedValues calculatedValues = calculateExchangeComplexValues(analysisExtract);
        FertilityAnalysisExtractResponseDto dto = analysisExtract.toDto();

        dto.setSaturacaoCtcPotassioPercentual(calculatedValues.saturacaoCtcPotassioPercentual());
        dto.setSaturacaoCtcSodioPercentual(calculatedValues.saturacaoCtcSodioPercentual());
        dto.setSaturacaoCtcCalcioPercentual(calculatedValues.saturacaoCtcCalcioPercentual());
        dto.setSaturacaoCtcMagnesioPercentual(calculatedValues.saturacaoCtcMagnesioPercentual());
        dto.setSaturacaoCtcHidrogenioPercentual(calculatedValues.saturacaoCtcHidrogenioPercentual());
        dto.setSaturacaoCtcAluminioPercentual(calculatedValues.saturacaoCtcAluminioPercentual());
        dto.setRelacaoCalcioMagnesio(calculatedValues.relacaoCalcioMagnesio());
        dto.setRelacaoCalcioPotassio(calculatedValues.relacaoCalcioPotassio());
        dto.setRelacaoMagnesioPotassio(calculatedValues.relacaoMagnesioPotassio());
        dto.setRelacaoCalcioMagnesioPotassio(calculatedValues.relacaoCalcioMagnesioPotassio());
        dto.setAvisoTecnicoCalculosFertilidade(calculatedValues.avisoTecnicoCalculosFertilidade());

        return dto;
    }

    private ExchangeComplexCalculatedValues calculateExchangeComplexValues(FertilityAnalysisExtractModel analysisExtract) {
        List<String> warnings = new ArrayList<>();

        boolean canCalculateSaturation = hasPositiveValue(analysisExtract.getCtcPh7())
                && hasValidUnit(analysisExtract.getUnidadeCtcPh7());
        if (!canCalculateSaturation) {
            warnings.add("Saturação da CTC(T) não calculada: CTC pH 7,0/CTC(T) ausente, zerada ou com unidade diferente de mmolc/dm³.");
        }

        Double saturacaoPotassio = calculateCtcSaturation(
                "K", analysisExtract.getPotassio(), analysisExtract.getUnidadePotassio(), analysisExtract.getCtcPh7(), canCalculateSaturation, warnings);
        Double saturacaoSodio = calculateCtcSaturation(
                "Na", analysisExtract.getSodio(), analysisExtract.getUnidadeSodio(), analysisExtract.getCtcPh7(), canCalculateSaturation, warnings);
        Double saturacaoCalcio = calculateCtcSaturation(
                "Ca", analysisExtract.getCalcio(), analysisExtract.getUnidadeCalcio(), analysisExtract.getCtcPh7(), canCalculateSaturation, warnings);
        Double saturacaoMagnesio = calculateCtcSaturation(
                "Mg", analysisExtract.getMagnesio(), analysisExtract.getUnidadeMagnesio(), analysisExtract.getCtcPh7(), canCalculateSaturation, warnings);
        Double saturacaoAluminio = calculateCtcSaturation(
                "Al", analysisExtract.getAluminio(), analysisExtract.getUnidadeAluminio(), analysisExtract.getCtcPh7(), canCalculateSaturation, warnings);
        Double saturacaoHidrogenio = calculateHydrogenCtcSaturation(analysisExtract, canCalculateSaturation, warnings);

        Double relacaoCalcioMagnesio = calculateCationRatio(
                "Ca/Mg", analysisExtract.getCalcio(), analysisExtract.getUnidadeCalcio(),
                analysisExtract.getMagnesio(), analysisExtract.getUnidadeMagnesio(), warnings);
        Double relacaoCalcioPotassio = calculateCationRatio(
                "Ca/K", analysisExtract.getCalcio(), analysisExtract.getUnidadeCalcio(),
                analysisExtract.getPotassio(), analysisExtract.getUnidadePotassio(), warnings);
        Double relacaoMagnesioPotassio = calculateCationRatio(
                "Mg/K", analysisExtract.getMagnesio(), analysisExtract.getUnidadeMagnesio(),
                analysisExtract.getPotassio(), analysisExtract.getUnidadePotassio(), warnings);
        Double relacaoCalcioMagnesioPotassio = calculateSumCationRatio(
                analysisExtract.getCalcio(), analysisExtract.getUnidadeCalcio(),
                analysisExtract.getMagnesio(), analysisExtract.getUnidadeMagnesio(),
                analysisExtract.getPotassio(), analysisExtract.getUnidadePotassio(), warnings);

        return new ExchangeComplexCalculatedValues(
                saturacaoPotassio,
                saturacaoSodio,
                saturacaoCalcio,
                saturacaoMagnesio,
                saturacaoHidrogenio,
                saturacaoAluminio,
                relacaoCalcioMagnesio,
                relacaoCalcioPotassio,
                relacaoMagnesioPotassio,
                relacaoCalcioMagnesioPotassio,
                warnings.isEmpty() ? null : String.join(" ", warnings)
        );
    }

    private Double calculateCtcSaturation(
            String nutrient,
            Double value,
            FertilityAnalysisUnit unit,
            Double ctcPh7,
            boolean canCalculateSaturation,
            List<String> warnings
    ) {
        if (!canCalculateSaturation) {
            return null;
        }
        if (!hasNonNegativeValue(value) || !hasValidUnit(unit)) {
            warnings.add("Saturação da CTC(T) para " + nutrient + " não calculada: valor ausente, negativo ou unidade diferente de mmolc/dm³.");
            return null;
        }
        return percentage(value, ctcPh7);
    }

    private Double calculateHydrogenCtcSaturation(
            FertilityAnalysisExtractModel analysisExtract,
            boolean canCalculateSaturation,
            List<String> warnings
    ) {
        if (!canCalculateSaturation) {
            return null;
        }
        if (!hasNonNegativeValue(analysisExtract.getAluminioMaisHidrogenio())
                || !hasNonNegativeValue(analysisExtract.getAluminio())
                || !hasValidUnit(analysisExtract.getUnidadeAluminioMaisHidrogenio())
                || !hasValidUnit(analysisExtract.getUnidadeAluminio())) {
            warnings.add("Saturação da CTC(T) para H não calculada: H+Al, Al3+ ausente, negativo ou unidade diferente de mmolc/dm³.");
            return null;
        }

        double hydrogen = analysisExtract.getAluminioMaisHidrogenio() - analysisExtract.getAluminio();
        if (hydrogen < 0.0) {
            warnings.add("Saturação da CTC(T) para H não calculada: H+Al menor que Al3+.");
            return null;
        }

        return percentage(hydrogen, analysisExtract.getCtcPh7());
    }

    private Double calculateCationRatio(
            String ratioName,
            Double numerator,
            FertilityAnalysisUnit numeratorUnit,
            Double denominator,
            FertilityAnalysisUnit denominatorUnit,
            List<String> warnings
    ) {
        if (!hasNonNegativeValue(numerator) || !hasValidUnit(numeratorUnit)) {
            warnings.add("Relação " + ratioName + " não calculada: numerador ausente, negativo ou unidade diferente de mmolc/dm³.");
            return null;
        }
        if (!hasPositiveValue(denominator) || !hasValidUnit(denominatorUnit)) {
            warnings.add("Relação " + ratioName + " não calculada: denominador ausente, zerado ou com unidade diferente de mmolc/dm³.");
            return null;
        }
        return roundToTwoDecimalPlaces(numerator / denominator);
    }

    private Double calculateSumCationRatio(
            Double calcio,
            FertilityAnalysisUnit unidadeCalcio,
            Double magnesio,
            FertilityAnalysisUnit unidadeMagnesio,
            Double potassio,
            FertilityAnalysisUnit unidadePotassio,
            List<String> warnings
    ) {
        if (!hasNonNegativeValue(calcio) || !hasValidUnit(unidadeCalcio)
                || !hasNonNegativeValue(magnesio) || !hasValidUnit(unidadeMagnesio)) {
            warnings.add("Relação (Ca+Mg)/K não calculada: Ca, Mg ausente, negativo ou unidade diferente de mmolc/dm³.");
            return null;
        }
        if (!hasPositiveValue(potassio) || !hasValidUnit(unidadePotassio)) {
            warnings.add("Relação (Ca+Mg)/K não calculada: K ausente, zerado ou com unidade diferente de mmolc/dm³.");
            return null;
        }
        return roundToTwoDecimalPlaces((calcio + magnesio) / potassio);
    }

    private boolean hasNonNegativeValue(Double value) {
        return value != null && Double.isFinite(value) && value >= 0.0;
    }

    private boolean hasPositiveValue(Double value) {
        return value != null && Double.isFinite(value) && value > 0.0;
    }

    private boolean hasValidUnit(FertilityAnalysisUnit unit) {
        return unit == FertilityAnalysisUnit.MMOLC_PER_DM3;
    }

    private FertilityAnalysisUnit normalizeFertilityUnit(FertilityAnalysisUnit unit) {
        return unit != null ? unit.canonicalForFertilityExtract() : DEFAULT_FERTILITY_UNIT;
    }

    private void recalculateExchangeComplex(FertilityAnalysisExtractModel analysisExtract) {
        double somaBases = zeroIfNull(analysisExtract.getCalcio())
                + zeroIfNull(analysisExtract.getMagnesio())
                + zeroIfNull(analysisExtract.getPotassio())
                + zeroIfNull(analysisExtract.getSodio());
        double aluminio = zeroIfNull(analysisExtract.getAluminio());
        double aluminioMaisHidrogenio = zeroIfNull(analysisExtract.getAluminioMaisHidrogenio());
        double ctcEfetiva = somaBases + aluminio;
        double ctcPh7 = somaBases + aluminioMaisHidrogenio;

        analysisExtract.setSomaBases(somaBases);
        analysisExtract.setCtcEfetiva(ctcEfetiva);
        analysisExtract.setCtcPh7(ctcPh7);
        analysisExtract.setSaturacaoBasesV(percentage(somaBases, ctcPh7));
        analysisExtract.setSaturacaoAluminioM(percentage(aluminio, ctcEfetiva));
        analysisExtract.setPst(percentage(zeroIfNull(analysisExtract.getSodio()), ctcPh7));
    }

    private Double percentage(double numerator, double denominator) {
        if (denominator == 0.0) {
            return null;
        }
        return roundToOneDecimalPlace(100.0 * numerator / denominator);
    }

    private Double roundToOneDecimalPlace(double value) {
        return BigDecimal.valueOf(value)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private Double roundToTwoDecimalPlaces(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private double zeroIfNull(Double value) {
        return value != null ? value : 0.0;
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

    private record ExchangeComplexCalculatedValues(
            Double saturacaoCtcPotassioPercentual,
            Double saturacaoCtcSodioPercentual,
            Double saturacaoCtcCalcioPercentual,
            Double saturacaoCtcMagnesioPercentual,
            Double saturacaoCtcHidrogenioPercentual,
            Double saturacaoCtcAluminioPercentual,
            Double relacaoCalcioMagnesio,
            Double relacaoCalcioPotassio,
            Double relacaoMagnesioPotassio,
            Double relacaoCalcioMagnesioPotassio,
            String avisoTecnicoCalculosFertilidade
    ) {}
}
