package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.crop.CropCreateRequestDto;
import com.migueltcc.fertintelligence.dto.crop.CropPostRequestDto;
import com.migueltcc.fertintelligence.dto.crop.CropResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.AnnualCropFolderModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.repository.*;
import com.migueltcc.fertintelligence.service.documentation.CropService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CropServiceImpl implements CropService {

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private AnnualCropFolderRepository annualCropFolderRepository;

    @Autowired
    private PlotAccessRequestRepository plotAccessRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FoliarAnalysisRepository foliarAnalysisRepository;

    @Autowired
    private LiquidSourceRepository liquidSourceRepository;

    @Autowired
    private SolidSourceRepository solidSourceRepository;

    @Autowired
    private TopDressingFertilizationRepository topDressingFertilizationRepository;

    @Override
    @Transactional
    public CropResponseDto createCrop(Long folderId, CropCreateRequestDto createRequestDto, String username) {
        UserModel requestingUser = findUserByUsernameOrThrow(username);

        AnnualCropFolderModel folder = findAnnualCropFolderByIdOrThrow(folderId);
        checkPlotPermission(folder.getPlot(), requestingUser, true);

        cropRepository.findByNameAndVarietyAndFolder(
                        createRequestDto.getName(),
                        createRequestDto.getVariety(),
                        folder)
                .ifPresent(existing -> {
                    throw new EntityExistsException("Já existe uma cultura cadastrada com o mesmo nome e variedade nesta pasta de culturas anuais.");
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

        CropModel savedCrop = cropRepository.save(crop);
        return savedCrop.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public CropResponseDto getCropById(Long cropId, String username) {
        UserModel requestingUser = findUserByUsernameOrThrow(username);

        CropModel crop = findCropByIdOrThrow(cropId);
        checkPlotPermission(crop.getFolder().getPlot(), requestingUser, false);

        return crop.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropResponseDto> getAllCropsByFolder(Long folderId, String username) {
        UserModel requestingUser = findUserByUsernameOrThrow(username);

        AnnualCropFolderModel folder = findAnnualCropFolderByIdOrThrow(folderId);
        checkPlotPermission(folder.getPlot(), requestingUser, false);

        return cropRepository.findAllByFolder(folder).stream()
                .map(CropModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CropResponseDto updateCrop(Long cropId, CropPostRequestDto updateRequestDto, String username) {
        UserModel requestingUser = findUserByUsernameOrThrow(username);

        CropModel crop = findCropByIdOrThrow(cropId);
        checkPlotPermission(crop.getFolder().getPlot(), requestingUser, true);

        NomeComum newName = Optional.ofNullable(updateRequestDto.getName()).orElse(crop.getName());
        String newVariety = Optional.ofNullable(updateRequestDto.getVariety()).orElse(crop.getVariety());

        if (!newName.equals(crop.getName()) || !newVariety.equals(crop.getVariety())) {
            cropRepository.findByNameAndVarietyAndFolder(newName, newVariety, crop.getFolder())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(cropId)) {
                            throw new EntityExistsException("Já existe uma cultura cadastrada com o mesmo nome e variedade nesta pasta de culturas anuais.");
                        }
                    });
        }

        if (updateRequestDto.getCultivationType() != null) {
            crop.setCultivationType(updateRequestDto.getCultivationType());
        }

        crop.setName(newName);
        crop.setVariety(newVariety);

        if (updateRequestDto.getCycle() != null) {
            crop.setCycle(updateRequestDto.getCycle());
        }

        if (updateRequestDto.getDistanceBetweenLines() != null) {
            crop.setDistanceBetweenLines(updateRequestDto.getDistanceBetweenLines());
        }

        if (updateRequestDto.getPlantsPerMeter() != null) {
            crop.setPlantsPerMeter(updateRequestDto.getPlantsPerMeter());
        }

        if (updateRequestDto.getExpectedProductivity() != null) {
            crop.setExpectedProductivity(updateRequestDto.getExpectedProductivity());
        }

        if (updateRequestDto.getObtainedProductivity() != null) {
            crop.setObtainedProductivity(updateRequestDto.getObtainedProductivity());
        }

        if (updateRequestDto.getUsedAreaInThePlot() != null) {
            crop.setUsedAreaInThePlot(updateRequestDto.getUsedAreaInThePlot());
        }

        if (updateRequestDto.getPlantingDate() != null) {
            crop.setPlantingDate(copyDate(updateRequestDto.getPlantingDate()));
        }

        if (updateRequestDto.getEmergenceDate() != null) {
            crop.setEmergenceDate(copyDate(updateRequestDto.getEmergenceDate()));
        }

        if (updateRequestDto.getButtoningDate() != null) {
            crop.setButtoningDate(copyDate(updateRequestDto.getButtoningDate()));
        }

        if (updateRequestDto.getFloweringDate() != null) {
            crop.setFloweringDate(copyDate(updateRequestDto.getFloweringDate()));
        }

        if (updateRequestDto.getHarvestDate() != null) {
            crop.setHarvestDate(copyDate(updateRequestDto.getHarvestDate()));
        }

        CropModel updatedCrop = cropRepository.save(crop);
        return updatedCrop.toDto();
    }

    @Override
    @Transactional
    public void deleteCrop(Long cropId, String username) {
        CropModel crop = cropRepository.findById(cropId)
                .orElseThrow(() -> new EntityNotFoundException("Cultura não encontrada."));

        UserModel requestingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        checkPlotPermission(crop.getFolder().getPlot(), requestingUser, true);

        // Limpeza em cascata
        foliarAnalysisRepository.deleteAllByCropId(crop.getId());
        liquidSourceRepository.deleteAllByCropId(crop.getId());
        solidSourceRepository.deleteAllByCropId(crop.getId());
        topDressingFertilizationRepository.deleteAllByCropId(crop.getId());

        // Deleção da Cultura
        cropRepository.delete(crop);
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private AnnualCropFolderModel findAnnualCropFolderByIdOrThrow(Long folderId) {
        return annualCropFolderRepository.findById(folderId)
                .orElseThrow(() -> new EntityNotFoundException("Pasta de cultura anual não encontrada com o ID: " + folderId));
    }

    private CropModel findCropByIdOrThrow(Long cropId) {
        return cropRepository.findById(cropId)
                .orElseThrow(() -> new EntityNotFoundException("Cultura não encontrada com o ID: " + cropId));
    }

    private void checkPlotPermission(PlotModel plot, UserModel requestingUser, boolean requireEditPermission) {
        PropertyModel property = plot.getProperty();

        if (property.getOwner().getId().equals(requestingUser.getId())) {
            ensureEditAllowed(requestingUser, requireEditPermission);
            return;
        }

        if (property.getManager() != null && property.getManager().getId().equals(requestingUser.getId())) {
            ensureEditAllowed(requestingUser, requireEditPermission);
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

        ensureEditAllowed(requestingUser, requireEditPermission);
    }

    private void ensureEditAllowed(UserModel requestingUser, boolean requireEditPermission) {
        if (requireEditPermission && requestingUser.getCargo() == Cargo.SECRETARIO) {
            throw new AccessDeniedException("Secretários não têm permissão para criar ou editar culturas.");
        }
    }

    private Date copyDate(Date source) {
        if (source == null) {
            return null;
        }
        return new Date(source.getDay(), source.getMonth(), source.getYear());
    }
}