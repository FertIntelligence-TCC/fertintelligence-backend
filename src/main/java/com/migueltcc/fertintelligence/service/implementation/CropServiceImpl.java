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
import org.springframework.security.access.AccessDeniedException;
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
    private final PlotAccessRequestRepository plotAccessRequestRepository;
    private final PropertyAccessRequestRepository propertyAccessRequestRepository;
    private final UserRepository userRepository;

    private final FoliarAnalysisRepository foliarAnalysisRepository;
    private final LiquidSourceRepository liquidSourceRepository;
    private final SolidSourceRepository solidSourceRepository;
    private final TopDressingFertilizationRepository topDressingFertilizationRepository;

    @Override
    @Transactional
    public CropResponseDto createCrop(Long folderId, CropCreateRequestDto dto, String username) {
        UserModel user = findUserByUsernameOrThrow(username);
        AnnualCropFolderModel folder = findFolderByIdOrThrow(folderId);

        ensureUserHasAllowedRole(user);
        ensurePlotAccess(folder.getPlot(), user, true);

        ensureUniqueCrop(folder, dto.getName(), dto.getVariety(), null);

        CropModel crop = CropModel.builder()
                .folder(folder)
                .cultivationType(dto.getCultivationType())
                .name(dto.getName())
                .variety(dto.getVariety())
                .cycle(dto.getCycle())
                .distanceBetweenLines(dto.getDistanceBetweenLines())
                .plantsPerMeter(dto.getPlantsPerMeter())
                .expectedProductivity(dto.getExpectedProductivity())
                .obtainedProductivity(dto.getObtainedProductivity())
                .usedAreaInThePlot(dto.getUsedAreaInThePlot())
                .plantingDate(copyDate(dto.getPlantingDate()))
                .emergenceDate(copyDate(dto.getEmergenceDate()))
                .buttoningDate(copyDate(dto.getButtoningDate()))
                .floweringDate(copyDate(dto.getFloweringDate()))
                .harvestDate(copyDate(dto.getHarvestDate()))
                .build();

        return cropRepository.save(crop).toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public CropResponseDto getCropById(Long cropId, String username) {
        UserModel user = findUserByUsernameOrThrow(username);
        CropModel crop = findCropByIdOrThrow(cropId);

        ensureUserHasAllowedRole(user);
        ensurePlotAccess(crop.getFolder().getPlot(), user, false);

        return crop.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropResponseDto> getAllCropsByFolder(Long folderId, String username) {
        UserModel user = findUserByUsernameOrThrow(username);
        AnnualCropFolderModel folder = findFolderByIdOrThrow(folderId);

        ensureUserHasAllowedRole(user);
        ensurePlotAccess(folder.getPlot(), user, false);

        return cropRepository.findAllByFolder(folder)
                .stream()
                .map(CropModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CropResponseDto updateCrop(Long cropId, CropPostRequestDto dto, String username) {
        UserModel user = findUserByUsernameOrThrow(username);
        CropModel crop = findCropByIdOrThrow(cropId);

        ensureUserHasAllowedRole(user);
        ensurePlotAccess(crop.getFolder().getPlot(), user, true);

        NomeComum newName = Optional.ofNullable(dto.getName()).orElse(crop.getName());
        String newVariety = Optional.ofNullable(dto.getVariety()).orElse(crop.getVariety());

        if (!newName.equals(crop.getName()) || !newVariety.equals(crop.getVariety())) {
            ensureUniqueCrop(crop.getFolder(), newName, newVariety, cropId);
        }

        if (dto.getCultivationType() != null) crop.setCultivationType(dto.getCultivationType());
        crop.setName(newName);
        crop.setVariety(newVariety);

        if (dto.getCycle() != null) crop.setCycle(dto.getCycle());
        if (dto.getDistanceBetweenLines() != null) crop.setDistanceBetweenLines(dto.getDistanceBetweenLines());
        if (dto.getPlantsPerMeter() != null) crop.setPlantsPerMeter(dto.getPlantsPerMeter());
        if (dto.getExpectedProductivity() != null) crop.setExpectedProductivity(dto.getExpectedProductivity());
        if (dto.getObtainedProductivity() != null) crop.setObtainedProductivity(dto.getObtainedProductivity());
        if (dto.getUsedAreaInThePlot() != null) crop.setUsedAreaInThePlot(dto.getUsedAreaInThePlot());

        if (dto.getPlantingDate() != null) crop.setPlantingDate(copyDate(dto.getPlantingDate()));
        if (dto.getEmergenceDate() != null) crop.setEmergenceDate(copyDate(dto.getEmergenceDate()));
        if (dto.getButtoningDate() != null) crop.setButtoningDate(copyDate(dto.getButtoningDate()));
        if (dto.getFloweringDate() != null) crop.setFloweringDate(copyDate(dto.getFloweringDate()));
        if (dto.getHarvestDate() != null) crop.setHarvestDate(copyDate(dto.getHarvestDate()));

        return cropRepository.save(crop).toDto();
    }

    @Override
    @Transactional
    public void deleteCrop(Long cropId, String username) {
        UserModel user = findUserByUsernameOrThrow(username);
        CropModel crop = findCropByIdOrThrow(cropId);

        ensureUserHasAllowedRole(user);
        ensurePlotAccess(crop.getFolder().getPlot(), user, true);

        // limpeza em cascata (por id)
        foliarAnalysisRepository.deleteAllByCropId(cropId);
        liquidSourceRepository.deleteAllByCropId(cropId);
        solidSourceRepository.deleteAllByCropId(cropId);
        topDressingFertilizationRepository.deleteAllByCropId(cropId);

        cropRepository.delete(crop);
    }

    // -------------------------
    // Helpers
    // -------------------------

    private void ensureUniqueCrop(AnnualCropFolderModel folder, NomeComum name, String variety, Long currentCropId) {
        cropRepository.findByNameAndVarietyAndFolder(name, variety, folder)
                .ifPresent(existing -> {
                    if (currentCropId == null || !existing.getId().equals(currentCropId)) {
                        throw new EntityExistsException(
                                "Já existe uma cultura cadastrada com o mesmo nome e variedade nesta pasta de culturas anuais."
                        );
                    }
                });
    }

    private void ensureUserHasAllowedRole(UserModel user) {
        Cargo c = user.getCargo();
        if (c != Cargo.PROPRIETARIO
                && c != Cargo.GERENTE
                && c != Cargo.AGRONOMO_RESIDENTE
                && c != Cargo.AGRONOMO_CONSULTOR
                && c != Cargo.SUPERVISOR_DE_AREA
                && c != Cargo.SECRETARIO) {
            throw new AccessDeniedException("Você não tem permissão para acessar este recurso.");
        }
    }

    private void ensurePlotAccess(PlotModel plot, UserModel user, boolean requireEditPermission) {
        if (requireEditPermission && user.getCargo() == Cargo.SECRETARIO) {
            throw new AccessDeniedException("Secretários não têm permissão para criar ou editar culturas.");
        }

        PropertyModel property = plot.getProperty();

        // dono/gerente: ok
        if (property.getOwner().getId().equals(user.getId())) return;
        if (property.getManager() != null && property.getManager().getId().equals(user.getId())) return;

        // AGRONOMO_RESIDENTE: permissão por propriedade (aprovada)
        if (user.getCargo() == Cargo.AGRONOMO_RESIDENTE) {
            boolean ok = propertyAccessRequestRepository
                    .findByPropertyAndRequesterAndStatus(property, user, AccessRequestStatus.APPROVED)
                    .isPresent();

            if (!ok) throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este recurso.");
            return;
        }

        // Demais cargos permitidos: permissão por talhão (aprovada)
        boolean ok = plotAccessRequestRepository
                .findByPlotAndRequesterAndStatus(plot, user, AccessRequestStatus.APPROVED)
                .isPresent();

        if (!ok) throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este recurso.");
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private AnnualCropFolderModel findFolderByIdOrThrow(Long folderId) {
        return annualCropFolderRepository.findById(folderId)
                .orElseThrow(() -> new EntityNotFoundException("Pasta de cultura anual não encontrada com o ID: " + folderId));
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