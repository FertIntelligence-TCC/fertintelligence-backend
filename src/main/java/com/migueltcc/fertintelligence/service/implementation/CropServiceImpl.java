package com.migueltcc.fertintelligence.service.implementation;

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

        CropModel crop = CropModel.builder()
                .folder(folder)
                .cultivationType(createRequestDto.getCultivationType())
                .name(createRequestDto.getName())
                .variety(createRequestDto.getVariety())
                .cycle(createRequestDto.getCycle())
                .distanceBetweenLines(createRequestDto.getDistanceBetweenLines())
                .plantsPerMeter(createRequestDto.getPlantsPerMeter())
                .expectedProductivity(createRequestDto.getExpectedProductivity())
                .obtainedProductivity(createRequestDto.getObtainedProductivity())
                .usedAreaInThePlot(createRequestDto.getUsedAreaInThePlot())
                .plantingDate(copyDate(createRequestDto.getPlantingDate()))
                .emergenceDate(copyDate(createRequestDto.getEmergenceDate()))
                .buttoningDate(copyDate(createRequestDto.getButtoningDate()))
                .floweringDate(copyDate(createRequestDto.getFloweringDate()))
                .harvestDate(copyDate(createRequestDto.getHarvestDate()))
                .build();

        return cropRepository.save(crop).toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public CropResponseDto getCropById(Long cropId, String username) {
        // leitura: por enquanto só garante que o usuário existe
        findUserByUsernameOrThrow(username);

        CropModel crop = findCropByIdOrThrow(cropId);

        // Se você quiser enforcement de leitura também, criamos depois um assertCanReadPlot/property.
        return crop.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropResponseDto> getAllCropsByFolder(Long folderId, String username) {
        findUserByUsernameOrThrow(username);

        AnnualCropFolderModel folder = findAnnualCropFolderByIdOrThrow(folderId);

        return cropRepository.findAllByFolder(folder).stream()
                .map(CropModel::toDto)
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

        if (updateRequestDto.getCultivationType() != null) crop.setCultivationType(updateRequestDto.getCultivationType());
        crop.setName(newName);
        crop.setVariety(newVariety);

        if (updateRequestDto.getCycle() != null) crop.setCycle(updateRequestDto.getCycle());
        if (updateRequestDto.getDistanceBetweenLines() != null) crop.setDistanceBetweenLines(updateRequestDto.getDistanceBetweenLines());
        if (updateRequestDto.getPlantsPerMeter() != null) crop.setPlantsPerMeter(updateRequestDto.getPlantsPerMeter());
        if (updateRequestDto.getExpectedProductivity() != null) crop.setExpectedProductivity(updateRequestDto.getExpectedProductivity());
        if (updateRequestDto.getObtainedProductivity() != null) crop.setObtainedProductivity(updateRequestDto.getObtainedProductivity());
        if (updateRequestDto.getUsedAreaInThePlot() != null) crop.setUsedAreaInThePlot(updateRequestDto.getUsedAreaInThePlot());

        if (updateRequestDto.getPlantingDate() != null) crop.setPlantingDate(copyDate(updateRequestDto.getPlantingDate()));
        if (updateRequestDto.getEmergenceDate() != null) crop.setEmergenceDate(copyDate(updateRequestDto.getEmergenceDate()));
        if (updateRequestDto.getButtoningDate() != null) crop.setButtoningDate(copyDate(updateRequestDto.getButtoningDate()));
        if (updateRequestDto.getFloweringDate() != null) crop.setFloweringDate(copyDate(updateRequestDto.getFloweringDate()));
        if (updateRequestDto.getHarvestDate() != null) crop.setHarvestDate(copyDate(updateRequestDto.getHarvestDate()));

        return cropRepository.save(crop).toDto();
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
        topDressingFertilizationRepository.flush();

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
}