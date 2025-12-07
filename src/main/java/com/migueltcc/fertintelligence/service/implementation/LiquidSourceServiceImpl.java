package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.foliarFertilization.liquid.LiquidSourceCreateRequestDto;
import com.migueltcc.fertintelligence.dto.foliarFertilization.liquid.LiquidSourcePostRequestDto;
import com.migueltcc.fertintelligence.dto.foliarFertilization.liquid.LiquidSourceResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.AnnualCropFolderModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.foliarFertilizationModels.LiquidSourceModel;
import com.migueltcc.fertintelligence.repository.CropRepository;
import com.migueltcc.fertintelligence.repository.LiquidSourceRepository;
import com.migueltcc.fertintelligence.repository.PlotAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.PropertyAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.LiquidSourceService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LiquidSourceServiceImpl implements LiquidSourceService {

    @Autowired
    private LiquidSourceRepository liquidSourceRepository;

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyAccessRequestRepository propertyAccessRequestRepository;

    @Autowired
    private PlotAccessRequestRepository plotAccessRequestRepository;

    @Override
    @Transactional
    public LiquidSourceResponseDto createLiquidSource(Long cropId, LiquidSourceCreateRequestDto createRequestDto, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserHasAllowedRole(owner, true);

        CropModel crop = findCropByIdOrThrow(cropId);
        checkPermission(crop.getFolder(), owner, true);

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

        LiquidSourceModel savedSource = liquidSourceRepository.save(liquidSource);
        return savedSource.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public LiquidSourceResponseDto getLiquidSourceById(Long liquidSourceId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserHasAllowedRole(owner, false);

        LiquidSourceModel liquidSource = findLiquidSourceByIdOrThrow(liquidSourceId);
        checkPermission(liquidSource.getCrop().getFolder(), owner, false);

        return liquidSource.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LiquidSourceResponseDto> getAllLiquidSourcesByCrop(Long cropId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserHasAllowedRole(owner, false);

        CropModel crop = findCropByIdOrThrow(cropId);
        checkPermission(crop.getFolder(), owner, false);

        return liquidSourceRepository.findAllByCrop(crop).stream()
                .map(LiquidSourceModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LiquidSourceResponseDto updateLiquidSource(Long liquidSourceId, LiquidSourcePostRequestDto updateRequestDto, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserHasAllowedRole(owner, true);

        LiquidSourceModel liquidSource = findLiquidSourceByIdOrThrow(liquidSourceId);
        CropModel crop = liquidSource.getCrop();
        checkPermission(crop.getFolder(), owner, true);

        if (updateRequestDto.getDate() != null) {
            liquidSource.setDate(copyDate(updateRequestDto.getDate()));
        }
        if (updateRequestDto.getMicronutrient() != null) {
            liquidSource.setMicronutrient(updateRequestDto.getMicronutrient());
        }
        if (updateRequestDto.getSource() != null) {
            liquidSource.setSource(updateRequestDto.getSource());
        }
        if (updateRequestDto.getConcentration() != null) {
            liquidSource.setConcentration(updateRequestDto.getConcentration());
        }
        if (updateRequestDto.getDensity() != null) {
            liquidSource.setDensity(updateRequestDto.getDensity());
        }
        if (updateRequestDto.getApplied_volume() != null) {
            liquidSource.setApplied_volume(updateRequestDto.getApplied_volume());
        }
        if (updateRequestDto.getTail_volume() != null) {
            liquidSource.setTail_volume(updateRequestDto.getTail_volume());
        }

        LiquidSourceModel updatedSource = liquidSourceRepository.save(liquidSource);
        return updatedSource.toDto();
    }

    @Override
    @Transactional
    public void deleteLiquidSource(Long liquidSourceId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserHasAllowedRole(owner, true);

        LiquidSourceModel liquidSource = findLiquidSourceByIdOrThrow(liquidSourceId);
        checkPermission(liquidSource.getCrop().getFolder(), owner, true);

        liquidSourceRepository.delete(liquidSource);
    }

    private void checkUserHasAllowedRole(UserModel user, boolean requireEditPermission) {
        if (user.getCargo() != Cargo.PROPRIETARIO
                && user.getCargo() != Cargo.GERENTE
                && user.getCargo() != Cargo.AGRONOMO_RESIDENTE
                && user.getCargo() != Cargo.AGRONOMO_CONSULTOR
                && user.getCargo() != Cargo.SECRETARIO) {
            throw new AccessDeniedException("Acesso negado. Você não tem permissão para gerenciar fontes líquidas de adubação foliar.");
        }

        if (requireEditPermission && user.getCargo() == Cargo.SECRETARIO) {
            throw new AccessDeniedException("Secretários não têm permissão para criar ou editar culturas ou suas aplicações de adubação foliar.");
        }
    }

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

    private void checkPermission(AnnualCropFolderModel folder, UserModel requestingUser, boolean requireEditPermission) {
        checkUserHasAllowedRole(requestingUser, requireEditPermission);

        PlotModel plot = folder.getPlot();
        PropertyModel property = plot.getProperty();

        if (property.getOwner().getId().equals(requestingUser.getId())) {
            return;
        }

        if (property.getManager() != null && property.getManager().getId().equals(requestingUser.getId())) {
            return;
        }

        if (requestingUser.getCargo() == Cargo.AGRONOMO_RESIDENTE) {
            boolean hasPropertyApproval = propertyAccessRequestRepository.findByPropertyAndRequesterAndStatus(
                    property,
                    requestingUser,
                    AccessRequestStatus.APPROVED
            ).isPresent();

            if (!hasPropertyApproval) {
                throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este recurso.");
            }
            return;
        }

        boolean hasPlotApproval = plotAccessRequestRepository.findByPlotAndRequesterAndStatus(
                plot,
                requestingUser,
                AccessRequestStatus.APPROVED
        ).isPresent();

        if (!hasPlotApproval) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este recurso.");
        }
    }

    private Date copyDate(Date source) {
        if (source == null) {
            return null;
        }
        return new Date(source.getDay(), source.getMonth(), source.getYear());
    }
}