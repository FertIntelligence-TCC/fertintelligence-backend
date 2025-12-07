package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.foliarFertilization.solid.SolidSourceCreateRequestDto;
import com.migueltcc.fertintelligence.dto.foliarFertilization.solid.SolidSourcePostRequestDto;
import com.migueltcc.fertintelligence.dto.foliarFertilization.solid.SolidSourceResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.AnnualCropFolderModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.foliarFertilizationModels.SolidSourceModel;
import com.migueltcc.fertintelligence.repository.CropRepository;
import com.migueltcc.fertintelligence.repository.PlotAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.PropertyAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.SolidSourceRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.SolidSourceService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SolidSourceServiceImpl implements SolidSourceService {

    @Autowired
    private SolidSourceRepository solidSourceRepository;

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
    public SolidSourceResponseDto createSolidSource(Long cropId, SolidSourceCreateRequestDto createRequestDto, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserHasAllowedRole(owner);

        CropModel crop = findCropByIdOrThrow(cropId);
        checkPermission(crop.getFolder(), owner);

        SolidSourceModel solidSource = SolidSourceModel.builder()
                .crop(crop)
                .date(copyDate(createRequestDto.getDate()))
                .micronutrient(createRequestDto.getMicronutrient())
                .source(createRequestDto.getSource())
                .concentration(createRequestDto.getConcentration())
                .quantity(createRequestDto.getQuantity())
                .build();

        SolidSourceModel savedSource = solidSourceRepository.save(solidSource);
        return savedSource.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public SolidSourceResponseDto getSolidSourceById(Long solidSourceId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserHasAllowedRole(owner);

        SolidSourceModel solidSource = findSolidSourceByIdOrThrow(solidSourceId);
        checkPermission(solidSource.getCrop().getFolder(), owner);

        return solidSource.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolidSourceResponseDto> getAllSolidSourcesByCrop(Long cropId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserHasAllowedRole(owner);

        CropModel crop = findCropByIdOrThrow(cropId);
        checkPermission(crop.getFolder(), owner);

        return solidSourceRepository.findAllByCrop(crop).stream()
                .map(SolidSourceModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SolidSourceResponseDto updateSolidSource(Long solidSourceId, SolidSourcePostRequestDto updateRequestDto, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserHasAllowedRole(owner);

        SolidSourceModel solidSource = findSolidSourceByIdOrThrow(solidSourceId);
        CropModel crop = solidSource.getCrop();
        checkPermission(crop.getFolder(), owner);

        if (updateRequestDto.getDate() != null) {
            solidSource.setDate(copyDate(updateRequestDto.getDate()));
        }
        if (updateRequestDto.getMicronutrient() != null) {
            solidSource.setMicronutrient(updateRequestDto.getMicronutrient());
        }
        if (updateRequestDto.getSource() != null) {
            solidSource.setSource(updateRequestDto.getSource());
        }
        if (updateRequestDto.getConcentration() != null) {
            solidSource.setConcentration(updateRequestDto.getConcentration());
        }
        if (updateRequestDto.getQuantity() != null) {
            solidSource.setQuantity(updateRequestDto.getQuantity());
        }

        SolidSourceModel updatedSource = solidSourceRepository.save(solidSource);
        return updatedSource.toDto();
    }

    @Override
    @Transactional
    public void deleteSolidSource(Long solidSourceId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserHasAllowedRole(owner);

        SolidSourceModel solidSource = findSolidSourceByIdOrThrow(solidSourceId);
        checkPermission(solidSource.getCrop().getFolder(), owner);

        solidSourceRepository.delete(solidSource);
    }

    private void checkUserHasAllowedRole(UserModel user) {
        if (user.getCargo() != Cargo.PROPRIETARIO
                && user.getCargo() != Cargo.GERENTE
                && user.getCargo() != Cargo.AGRONOMO_RESIDENTE
                && user.getCargo() != Cargo.AGRONOMO_CONSULTOR
                && user.getCargo() != Cargo.SECRETARIO) {
            throw new AccessDeniedException("Acesso negado. Você não tem permissão para gerenciar fontes sólidas de adubação foliar.");
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

    private SolidSourceModel findSolidSourceByIdOrThrow(Long solidSourceId) {
        return solidSourceRepository.findById(solidSourceId)
                .orElseThrow(() -> new EntityNotFoundException("Fonte sólida não encontrada com o ID: " + solidSourceId));
    }

    private void checkPermission(AnnualCropFolderModel folder, UserModel requestingUser) {
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