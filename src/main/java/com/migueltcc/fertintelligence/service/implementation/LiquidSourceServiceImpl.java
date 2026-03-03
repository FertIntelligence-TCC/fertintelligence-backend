package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.dto.foliarFertilization.liquid.LiquidSourceCreateRequestDto;
import com.migueltcc.fertintelligence.dto.foliarFertilization.liquid.LiquidSourcePostRequestDto;
import com.migueltcc.fertintelligence.dto.foliarFertilization.liquid.LiquidSourceResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.foliarFertilizationModels.LiquidSourceModel;
import com.migueltcc.fertintelligence.repository.CropRepository;
import com.migueltcc.fertintelligence.repository.LiquidSourceRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.LiquidSourceService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LiquidSourceServiceImpl implements LiquidSourceService {

    private final LiquidSourceRepository liquidSourceRepository;
    private final CropRepository cropRepository;
    private final UserRepository userRepository;
    private final PermissionManager permissionManager;

    @Override
    @Transactional
    public LiquidSourceResponseDto createLiquidSource(
            Long cropId,
            LiquidSourceCreateRequestDto createRequestDto,
            String username
    ) {
        UserModel requester = findUserByUsernameOrThrow(username);
        CropModel crop = findCropByIdOrThrow(cropId);

        PermissionContext ctx = resolvePermissionContext(crop);

        // WRITE em LiquidSource = edição de CULTURAS
        permissionManager.assertCanEditCrops(ctx.property(), ctx.plot(), requester);

        LiquidSourceModel liquidSource = LiquidSourceModel.builder()
                .crop(crop)
                .date(copyDate(createRequestDto.getDate()))
                .micronutrient(createRequestDto.getMicronutrient())
                .source(createRequestDto.getSource())
                .concentration(createRequestDto.getConcentration())
                .density(createRequestDto.getDensity())
                .applied_volume(createRequestDto.getApplied_volume())
                .tail_volume(createRequestDto.getTail_volume())
                .build();

        return liquidSourceRepository.save(liquidSource).toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public LiquidSourceResponseDto getLiquidSourceById(Long liquidSourceId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);
        LiquidSourceModel liquidSource = findLiquidSourceByIdOrThrow(liquidSourceId);

        PlotModel plot = liquidSource.getCrop().getFolder().getPlot();

        // READ = visualizar recursos do talhão
        permissionManager.assertCanReadPlot(plot, requester);

        return liquidSource.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LiquidSourceResponseDto> getAllLiquidSourcesByCrop(Long cropId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);
        CropModel crop = findCropByIdOrThrow(cropId);

        PlotModel plot = crop.getFolder().getPlot();

        // LIST = READ
        permissionManager.assertCanReadPlot(plot, requester);

        return liquidSourceRepository.findAllByCrop(crop)
                .stream()
                .map(LiquidSourceModel::toDto)
                .toList();
    }

    @Override
    @Transactional
    public LiquidSourceResponseDto updateLiquidSource(
            Long liquidSourceId,
            LiquidSourcePostRequestDto updateRequestDto,
            String username
    ) {
        UserModel requester = findUserByUsernameOrThrow(username);
        LiquidSourceModel liquidSource = findLiquidSourceByIdOrThrow(liquidSourceId);

        PermissionContext ctx = resolvePermissionContext(liquidSource.getCrop());

        // UPDATE = edição de CULTURAS
        permissionManager.assertCanEditCrops(ctx.property(), ctx.plot(), requester);

        if (updateRequestDto.getDate() != null) liquidSource.setDate(copyDate(updateRequestDto.getDate()));
        if (updateRequestDto.getMicronutrient() != null) liquidSource.setMicronutrient(updateRequestDto.getMicronutrient());
        if (updateRequestDto.getSource() != null) liquidSource.setSource(updateRequestDto.getSource());
        if (updateRequestDto.getConcentration() != null) liquidSource.setConcentration(updateRequestDto.getConcentration());
        if (updateRequestDto.getDensity() != null) liquidSource.setDensity(updateRequestDto.getDensity());
        if (updateRequestDto.getApplied_volume() != null) liquidSource.setApplied_volume(updateRequestDto.getApplied_volume());
        if (updateRequestDto.getTail_volume() != null) liquidSource.setTail_volume(updateRequestDto.getTail_volume());

        return liquidSourceRepository.save(liquidSource).toDto();
    }

    @Override
    @Transactional
    public void deleteLiquidSource(Long liquidSourceId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);
        LiquidSourceModel liquidSource = findLiquidSourceByIdOrThrow(liquidSourceId);

        PermissionContext ctx = resolvePermissionContext(liquidSource.getCrop());

        // DELETE = edição de CULTURAS
        permissionManager.assertCanEditCrops(ctx.property(), ctx.plot(), requester);

        liquidSourceRepository.delete(liquidSource);
        liquidSourceRepository.flush();
    }

    /* =========================
       Permission Context
       ========================= */

    private PermissionContext resolvePermissionContext(CropModel crop) {
        PlotModel plot = crop.getFolder().getPlot();
        PropertyModel property = plot.getProperty();

        if (plot == null || plot.getId() == null || property == null || property.getId() == null) {
            throw new IllegalStateException("Não foi possível resolver plot/property para validação de permissões.");
        }
        return new PermissionContext(property, plot);
    }

    private record PermissionContext(PropertyModel property, PlotModel plot) {}

    /* =========================
       Finders
       ========================= */

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private CropModel findCropByIdOrThrow(Long cropId) {
        return cropRepository.findById(cropId)
                .orElseThrow(() -> new EntityNotFoundException("Cultura não encontrada com o ID: " + cropId));
    }

    private LiquidSourceModel findLiquidSourceByIdOrThrow(Long liquidSourceId) {
        return liquidSourceRepository.findById(liquidSourceId)
                .orElseThrow(() -> new EntityNotFoundException("Fonte líquida não encontrada com o ID: " + liquidSourceId));
    }

    /* =========================
       Helpers
       ========================= */

    private Date copyDate(Date source) {
        if (source == null) return null;
        return new Date(source.getDay(), source.getMonth(), source.getYear());
    }
}