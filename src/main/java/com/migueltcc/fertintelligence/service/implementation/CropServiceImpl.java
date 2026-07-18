package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.crop.CropSpacingMode;
import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.dto.crop.CropCreateRequestDto;
import com.migueltcc.fertintelligence.dto.crop.CropPostRequestDto;
import com.migueltcc.fertintelligence.dto.crop.CropResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.AnnualCropFolderModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.repository.*;
import com.migueltcc.fertintelligence.service.documentation.CropService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CropServiceImpl implements CropService {

    private final CropRepository cropRepository;
    private final AnnualCropFolderRepository annualCropFolderRepository;
    private final UserRepository userRepository;

    private final FoliarAnalysisRepository foliarAnalysisRepository;
    private final LiquidSourceRepository liquidSourceRepository;
    private final SolidSourceRepository solidSourceRepository;
    private final TopDressingFertilizationRepository topDressingFertilizationRepository;
    private final CropDeficiencyToxicityRepository cropDeficiencyToxicityRepository;

    private final PermissionManager permissionManager;

    @Override
    @Transactional
    public CropResponseDto createCrop(Long folderId, CropCreateRequestDto createRequestDto, String username) {
        // valida usuário (mensagem consistente)
        findUserByUsernameOrThrow(username);

        AnnualCropFolderModel folder = findAnnualCropFolderByIdOrThrow(folderId);
        PlotModel plot = folder.getPlot();

        Long plotId = plot.getId();
        Long propertyId = plot.getProperty().getId();

        // CULTURAS => precisa permissão de editar culturas
        permissionManager.assertCanEditCrops(propertyId, plotId, username);

        cropRepository.findByNameAndVarietyAndFolder(createRequestDto.getName(), createRequestDto.getVariety(), folder)
                .ifPresent(existing -> {
                    throw new EntityExistsException(
                            "Já existe uma cultura cadastrada com o mesmo nome e variedade nesta pasta de culturas anuais."
                    );
                });

        CropPhenologyDateValidator.validate(
                createRequestDto.getPlantingDate(), createRequestDto.getEmergenceDate(),
                createRequestDto.getButtoningDate(), createRequestDto.getFloweringDate(),
                createRequestDto.getHarvestDate());

        validateUsedAreaLimit(
                folder,
                null,
                createRequestDto.getUsedAreaInThePlot(),
                createRequestDto.getPlantingDate(),
                createRequestDto.getHarvestDate()
        );

        CropModel crop = CropModel.builder()
                .folder(folder)
                .cultivationType(createRequestDto.getCultivationType())
                .name(createRequestDto.getName())
                .variety(createRequestDto.getVariety())
                .cycle(createRequestDto.getCycle())
                .expectedProductivity(createRequestDto.getExpectedProductivity())
                .obtainedProductivity(createRequestDto.getObtainedProductivity())
                .usedAreaInThePlot(createRequestDto.getUsedAreaInThePlot())
                .plantingDate(copyDate(createRequestDto.getPlantingDate()))
                .emergenceDate(copyDate(createRequestDto.getEmergenceDate()))
                .buttoningDate(copyDate(createRequestDto.getButtoningDate()))
                .floweringDate(copyDate(createRequestDto.getFloweringDate()))
                .harvestDate(copyDate(createRequestDto.getHarvestDate()))
                .idFoto(createRequestDto.getIdFoto())
                .build();

        applySpacingFields(
                crop,
                createRequestDto.getDistanceBetweenLines(),
                createRequestDto.getPlantsPerMeter(),
                createRequestDto.getSpacingMode(),
                createRequestDto.getDistanceBetweenPits(),
                createRequestDto.getPlantsPerPit()
        );
        validateSpacing(crop);

        return toResponseDto(cropRepository.save(crop));
    }

    @Override
    @Transactional(readOnly = true)
    public CropResponseDto getCropById(Long cropId, String username) {
        // leitura: por enquanto só garante que o usuário existe
        findUserByUsernameOrThrow(username);

        CropModel crop = findCropByIdOrThrow(cropId);

        // Se você quiser enforcement de leitura também, criamos depois um assertCanReadPlot/property.
        return toResponseDto(crop);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropResponseDto> getAllCropsByFolder(Long folderId, String username) {
        findUserByUsernameOrThrow(username);

        AnnualCropFolderModel folder = findAnnualCropFolderByIdOrThrow(folderId);

        return cropRepository.findAllByFolder(folder).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CropResponseDto updateCrop(Long cropId, CropPostRequestDto updateRequestDto, String username) {
        findUserByUsernameOrThrow(username);

        CropModel crop = findCropByIdOrThrow(cropId);
        PlotModel plot = crop.getFolder().getPlot();

        Long plotId = plot.getId();
        Long propertyId = plot.getProperty().getId();

        // CULTURAS => precisa permissão de editar culturas
        permissionManager.assertCanEditCrops(propertyId, plotId, username);

        NomeComum newName = Optional.ofNullable(updateRequestDto.getName()).orElse(crop.getName());
        String newVariety = Optional.ofNullable(updateRequestDto.getVariety()).orElse(crop.getVariety());

        if (!newName.equals(crop.getName()) || !newVariety.equals(crop.getVariety())) {
            cropRepository.findByNameAndVarietyAndFolder(newName, newVariety, crop.getFolder())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(cropId)) {
                            throw new EntityExistsException(
                                    "Já existe uma cultura cadastrada com o mesmo nome e variedade nesta pasta de culturas anuais."
                            );
                        }
                    });
        }

        Double newUsedArea = Optional.ofNullable(updateRequestDto.getUsedAreaInThePlot()).orElse(crop.getUsedAreaInThePlot());
        Date newPlantingDate = Optional.ofNullable(updateRequestDto.getPlantingDate()).orElse(crop.getPlantingDate());
        Date newEmergenceDate = Optional.ofNullable(updateRequestDto.getEmergenceDate()).orElse(crop.getEmergenceDate());
        Date newButtoningDate = Optional.ofNullable(updateRequestDto.getButtoningDate()).orElse(crop.getButtoningDate());
        Date newFloweringDate = Optional.ofNullable(updateRequestDto.getFloweringDate()).orElse(crop.getFloweringDate());
        Date newHarvestDate = Optional.ofNullable(updateRequestDto.getHarvestDate()).orElse(crop.getHarvestDate());

        CropPhenologyDateValidator.validate(
                newPlantingDate, newEmergenceDate, newButtoningDate, newFloweringDate, newHarvestDate);

        validateUsedAreaLimit(crop.getFolder(), cropId, newUsedArea, newPlantingDate, newHarvestDate);

        if (updateRequestDto.getCultivationType() != null) crop.setCultivationType(updateRequestDto.getCultivationType());
        crop.setName(newName);
        crop.setVariety(newVariety);

        if (updateRequestDto.getCycle() != null) crop.setCycle(updateRequestDto.getCycle());
        applySpacingFields(
                crop,
                updateRequestDto.getDistanceBetweenLines(),
                updateRequestDto.getPlantsPerMeter(),
                updateRequestDto.getSpacingMode(),
                updateRequestDto.getDistanceBetweenPits(),
                updateRequestDto.getPlantsPerPit()
        );
        validateSpacing(crop);

        if (updateRequestDto.getExpectedProductivity() != null) crop.setExpectedProductivity(updateRequestDto.getExpectedProductivity());
        if (updateRequestDto.getObtainedProductivity() != null) crop.setObtainedProductivity(updateRequestDto.getObtainedProductivity());
        if (updateRequestDto.getUsedAreaInThePlot() != null) crop.setUsedAreaInThePlot(updateRequestDto.getUsedAreaInThePlot());

        if (updateRequestDto.getPlantingDate() != null) crop.setPlantingDate(copyDate(updateRequestDto.getPlantingDate()));
        if (updateRequestDto.getEmergenceDate() != null) crop.setEmergenceDate(copyDate(updateRequestDto.getEmergenceDate()));
        if (updateRequestDto.getButtoningDate() != null) crop.setButtoningDate(copyDate(updateRequestDto.getButtoningDate()));
        if (updateRequestDto.getFloweringDate() != null) crop.setFloweringDate(copyDate(updateRequestDto.getFloweringDate()));
        if (updateRequestDto.getHarvestDate() != null) crop.setHarvestDate(copyDate(updateRequestDto.getHarvestDate()));
        if (updateRequestDto.getIdFoto() != null) crop.setIdFoto(updateRequestDto.getIdFoto());

        return toResponseDto(cropRepository.save(crop));
    }

    @Override
    @Transactional
    public void deleteCrop(Long cropId, String username) {
        findUserByUsernameOrThrow(username);

        CropModel crop = findCropByIdOrThrow(cropId);
        PlotModel plot = crop.getFolder().getPlot();

        Long plotId = plot.getId();
        Long propertyId = plot.getProperty().getId();

        // CULTURAS => precisa permissão de editar culturas
        permissionManager.assertCanEditCrops(propertyId, plotId, username);

        foliarAnalysisRepository.deleteAllByCropId(crop.getId());
        liquidSourceRepository.deleteAllByCropId(crop.getId());
        solidSourceRepository.deleteAllByCropId(crop.getId());
        solidSourceRepository.flush();

        topDressingFertilizationRepository.deleteAllByCropId(crop.getId());
        cropDeficiencyToxicityRepository.deleteAllByCropId(crop.getId());
        topDressingFertilizationRepository.flush();
        cropDeficiencyToxicityRepository.flush();

        liquidSourceRepository.flush();
        foliarAnalysisRepository.flush();

        cropRepository.delete(crop);
        cropRepository.flush();
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private AnnualCropFolderModel findAnnualCropFolderByIdOrThrow(Long folderId) {
        return annualCropFolderRepository.findById(folderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Pasta de cultura anual não encontrada com o ID: " + folderId
                ));
    }

    private CropModel findCropByIdOrThrow(Long cropId) {
        return cropRepository.findById(cropId)
                .orElseThrow(() -> new EntityNotFoundException("Cultura não encontrada com o ID: " + cropId));
    }

    private Date copyDate(Date source) {
        if (source == null) return null;
        return new Date(source.getDay(), source.getMonth(), source.getYear());
    }

    private CropResponseDto toResponseDto(CropModel crop) {
        CropResponseDto dto = crop.toDto();
        dto.setSpacingMode(resolveSpacingMode(crop.getSpacingMode(), crop.getPlantsPerMeter()));
        return dto;
    }

    private void applySpacingFields(
            CropModel crop,
            Double distanceBetweenLines,
            Double plantsPerMeter,
            CropSpacingMode spacingMode,
            Double distanceBetweenPits,
            Double plantsPerPit
    ) {
        if (distanceBetweenLines != null) crop.setDistanceBetweenLines(distanceBetweenLines);
        if (plantsPerMeter != null) crop.setPlantsPerMeter(plantsPerMeter);
        if (spacingMode != null) crop.setSpacingMode(spacingMode);
        if (distanceBetweenPits != null) crop.setDistanceBetweenPits(distanceBetweenPits);
        if (plantsPerPit != null) crop.setPlantsPerPit(plantsPerPit);

        crop.setSpacingMode(resolveSpacingMode(crop.getSpacingMode(), crop.getPlantsPerMeter()));
    }

    private CropSpacingMode resolveSpacingMode(CropSpacingMode spacingMode, Double plantsPerMeter) {
        if (spacingMode != null && spacingMode != CropSpacingMode.UNKNOWN) {
            return spacingMode;
        }
        if (isPositive(plantsPerMeter)) {
            return CropSpacingMode.PLANTS_PER_LINEAR_METER;
        }
        return spacingMode;
    }

    private void validateSpacing(CropModel crop) {
        if (!isPositive(crop.getDistanceBetweenLines())) {
            throw new IllegalArgumentException("A distância entre linhas deve ser maior que zero.");
        }

        CropSpacingMode spacingMode = resolveSpacingMode(crop.getSpacingMode(), crop.getPlantsPerMeter());
        if (spacingMode == CropSpacingMode.PLANTS_PER_LINEAR_METER) {
            if (!isPositive(crop.getPlantsPerMeter())) {
                throw new IllegalArgumentException("O modo Nº de Plantas/m linear exige número de plantas por metro linear maior que zero.");
            }
            return;
        }

        if (spacingMode == CropSpacingMode.PIT) {
            if (!isPositive(crop.getDistanceBetweenPits())) {
                throw new IllegalArgumentException("O modo Distância entre covas (m) exige distância entre covas maior que zero.");
            }
            if (!isPositive(crop.getPlantsPerPit())) {
                throw new IllegalArgumentException("O modo Distância entre covas (m) exige número de plantas por cova maior que zero.");
            }
        }
    }

    private boolean isPositive(Double value) {
        return value != null && value > 0;
    }

    private void validateUsedAreaLimit(
            AnnualCropFolderModel folder,
            Long ignoredCropId,
            Double usedAreaInThePlot,
            Date plantingDate,
            Date harvestDate
    ) {
        double plotArea = folder.getPlot().getArea();
        YearMonth newStart = toYearMonth(plantingDate);
        YearMonth newEnd = toYearMonth(harvestDate);

        if (newStart.isAfter(newEnd)) {
            throw new IllegalArgumentException("A data de plantio não pode ser posterior à data de colheita.");
        }

        List<CropModel> existingCrops = annualCropFolderRepository.findAllByPlot(folder.getPlot()).stream()
                .flatMap(plotFolder -> cropRepository.findAllByFolder(plotFolder).stream())
                .toList();
        for (YearMonth currentMonth = newStart;
             !currentMonth.isAfter(newEnd);
             currentMonth = currentMonth.plusMonths(1)) {
            double occupiedArea = usedAreaInThePlot;

            for (CropModel existingCrop : existingCrops) {
                if (ignoredCropId != null && ignoredCropId.equals(existingCrop.getId())) {
                    continue;
                }

                YearMonth existingStart = toYearMonth(existingCrop.getPlantingDate());
                YearMonth existingEnd = toYearMonth(existingCrop.getHarvestDate());

                if (monthWithinRange(currentMonth, existingStart, existingEnd)) {
                    occupiedArea += existingCrop.getUsedAreaInThePlot();
                }
            }

            if (occupiedArea > plotArea) {
                throw new IllegalArgumentException(
                        "A soma das áreas ocupadas por culturas com meses sobrepostos não pode exceder a área do talhão."
                );
            }
        }
    }

    private YearMonth toYearMonth(Date date) {
        return YearMonth.of(date.getYear(), date.getMonth());
    }

    private boolean monthWithinRange(YearMonth month, YearMonth start, YearMonth end) {
        return !month.isBefore(start) && !month.isAfter(end);
    }
}
