package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.dto.cropDeficiencyToxicity.*;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropDeficiencyToxicityModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.repository.CropDeficiencyToxicityRepository;
import com.migueltcc.fertintelligence.repository.CropRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.CropDeficiencyToxicityService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CropDeficiencyToxicityServiceImpl implements CropDeficiencyToxicityService {
    private final CropDeficiencyToxicityRepository repository;
    private final CropRepository cropRepository;
    private final UserRepository userRepository;
    private final PermissionManager permissionManager;

    @Override @Transactional
    public CropDeficiencyToxicityResponseDto create(Long cropId, CropDeficiencyToxicityCreateRequestDto dto, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);
        CropModel crop = findCropByIdOrThrow(cropId);
        PermissionContext ctx = resolvePermissionContext(crop);
        permissionManager.assertCanEditAnalyses(ctx.property(), ctx.plot(), requester);

        CropDeficiencyToxicityModel saved = repository.save(CropDeficiencyToxicityModel.builder()
                .crop(crop)
                .nutrientType(dto.getNutrientType())
                .nutrient(dto.getNutrient())
                .healthyPlantImageId(dto.getHealthyPlantImageId())
                .symptomaticPlantImageId(dto.getSymptomaticPlantImageId())
                .observations(dto.getObservations())
                .build());
        return saved.toDto();
    }

    @Override @Transactional(readOnly = true)
    public CropDeficiencyToxicityResponseDto getById(Long id, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);
        CropDeficiencyToxicityModel model = findByIdOrThrow(id);
        PermissionContext ctx = resolvePermissionContext(model.getCrop());
        permissionManager.assertCanReadPlot(ctx.plot(), requester);
        return model.toDto();
    }

    @Override @Transactional(readOnly = true)
    public List<CropDeficiencyToxicityResponseDto> getAllByCrop(Long cropId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);
        CropModel crop = findCropByIdOrThrow(cropId);
        PermissionContext ctx = resolvePermissionContext(crop);
        permissionManager.assertCanReadPlot(ctx.plot(), requester);
        return repository.findAllByCropId(cropId).stream().map(CropDeficiencyToxicityModel::toDto).toList();
    }

    @Override @Transactional
    public CropDeficiencyToxicityResponseDto update(Long id, CropDeficiencyToxicityPostRequestDto dto, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);
        CropDeficiencyToxicityModel model = findByIdOrThrow(id);
        PermissionContext ctx = resolvePermissionContext(model.getCrop());
        permissionManager.assertCanEditAnalyses(ctx.property(), ctx.plot(), requester);

        if (dto.getNutrientType() != null) model.setNutrientType(dto.getNutrientType());
        if (dto.getNutrient() != null) model.setNutrient(dto.getNutrient());
        if (dto.getHealthyPlantImageId() != null) model.setHealthyPlantImageId(dto.getHealthyPlantImageId());
        if (dto.getSymptomaticPlantImageId() != null) model.setSymptomaticPlantImageId(dto.getSymptomaticPlantImageId());
        if (dto.getObservations() != null) model.setObservations(dto.getObservations());

        return repository.save(model).toDto();
    }

    @Override @Transactional
    public void delete(Long id, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);
        CropDeficiencyToxicityModel model = findByIdOrThrow(id);
        PermissionContext ctx = resolvePermissionContext(model.getCrop());
        permissionManager.assertCanEditAnalyses(ctx.property(), ctx.plot(), requester);
        repository.delete(model);
    }

    private CropDeficiencyToxicityModel findByIdOrThrow(Long id){return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Deficiência/toxidez não encontrada com o ID: " + id));}
    private UserModel findUserByUsernameOrThrow(String username){return userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));}
    private CropModel findCropByIdOrThrow(Long cropId){return cropRepository.findById(cropId).orElseThrow(() -> new EntityNotFoundException("Cultura não encontrada com o ID: " + cropId));}
    private PermissionContext resolvePermissionContext(CropModel crop){PlotModel p=crop.getFolder().getPlot(); PropertyModel prop=p.getProperty(); return new PermissionContext(prop,p);}    
    private record PermissionContext(PropertyModel property, PlotModel plot) {}
}
