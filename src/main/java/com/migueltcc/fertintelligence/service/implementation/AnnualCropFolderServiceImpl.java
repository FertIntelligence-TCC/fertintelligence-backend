package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.annualCropFolder.AnnualCropFolderCreateRequestDto;
import com.migueltcc.fertintelligence.dto.annualCropFolder.AnnualCropFolderPostRequestDto;
import com.migueltcc.fertintelligence.dto.annualCropFolder.AnnualCropFolderResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.AnnualCropFolderModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.repository.AnnualCropFolderRepository;
import com.migueltcc.fertintelligence.repository.CropRepository;
import com.migueltcc.fertintelligence.repository.PlotAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.PlotRepository;
import com.migueltcc.fertintelligence.repository.PropertyAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.AnnualCropFolderService;
import com.migueltcc.fertintelligence.service.documentation.CropService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnualCropFolderServiceImpl implements AnnualCropFolderService {

    private final AnnualCropFolderRepository annualCropFolderRepository;
    private final PlotRepository plotRepository;
    private final PlotAccessRequestRepository plotAccessRequestRepository;
    private final PropertyAccessRequestRepository propertyAccessRequestRepository;
    private final UserRepository userRepository;
    private final CropRepository cropRepository;

    // evita possíveis ciclos de dependência (folder -> cropService -> folderService)
    private final @Lazy CropService cropService;

    @Override
    @Transactional
    public AnnualCropFolderResponseDto createAnnualCropFolder(
            Long plotId,
            AnnualCropFolderCreateRequestDto createRequestDto,
            String username
    ) {
        UserModel requestingUser = findUserByUsernameOrThrow(username);
        PlotModel plot = findPlotByIdOrThrow(plotId);

        authorizePlotAccess(plot, requestingUser);

        annualCropFolderRepository.findByPlotAndCropsYear(plot, createRequestDto.getCropsYear())
                .ifPresent(existing -> {
                    throw new EntityExistsException(
                            "Já existe uma pasta de cultura anual para o ano informado neste talhão: "
                                    + createRequestDto.getCropsYear()
                    );
                });

        AnnualCropFolderModel folder = AnnualCropFolderModel.builder()
                .plot(plot)
                .cropsYear(createRequestDto.getCropsYear())
                .build();

        return annualCropFolderRepository.save(folder).toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public AnnualCropFolderResponseDto getAnnualCropFolderById(Long annualCropFolderId, String username) {
        UserModel requestingUser = findUserByUsernameOrThrow(username);
        AnnualCropFolderModel folder = findAnnualCropFolderByIdOrThrow(annualCropFolderId);

        authorizePlotAccess(folder.getPlot(), requestingUser);

        return folder.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnualCropFolderResponseDto> getAllAnnualCropFoldersByPlot(Long plotId, String username) {
        UserModel requestingUser = findUserByUsernameOrThrow(username);
        PlotModel plot = findPlotByIdOrThrow(plotId);

        authorizePlotAccess(plot, requestingUser);

        return annualCropFolderRepository.findAllByPlot(plot).stream()
                .map(AnnualCropFolderModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AnnualCropFolderResponseDto updateAnnualCropFolder(
            Long annualCropFolderId,
            AnnualCropFolderPostRequestDto updateRequestDto,
            String username
    ) {
        UserModel requestingUser = findUserByUsernameOrThrow(username);
        AnnualCropFolderModel folder = findAnnualCropFolderByIdOrThrow(annualCropFolderId);

        authorizePlotAccess(folder.getPlot(), requestingUser);

        Integer newYear = updateRequestDto.getCropsYear();
        if (newYear != null && !newYear.equals(folder.getCropsYear())) {
            annualCropFolderRepository.findByPlotAndCropsYear(folder.getPlot(), newYear)
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(annualCropFolderId)) {
                            throw new EntityExistsException(
                                    "Já existe uma pasta de cultura anual para o ano informado neste talhão: " + newYear
                            );
                        }
                    });
            folder.setCropsYear(newYear);
        }

        return annualCropFolderRepository.save(folder).toDto();
    }

    @Override
    @Transactional
    public void deleteAnnualCropFolder(Long id, String username) {
        UserModel requestingUser = findUserByUsernameOrThrow(username);
        AnnualCropFolderModel folder = findAnnualCropFolderByIdOrThrow(id);

        authorizePlotAccess(folder.getPlot(), requestingUser);

        deleteAllCropsFromFolder(folder.getId(), username);

        annualCropFolderRepository.delete(folder);
    }

    private void deleteAllCropsFromFolder(Long folderId, String username) {
        List<CropModel> crops = cropRepository.findAllByFolderId(folderId);
        for (CropModel crop : crops) {
            cropService.deleteCrop(crop.getId(), username);
        }
        cropRepository.flush();
    }

    private void authorizePlotAccess(PlotModel plot, UserModel requestingUser) {
        ensureAllowedRole(requestingUser);
        ensureHasPlotAccess(plot, requestingUser);
    }

    private void ensureAllowedRole(UserModel user) {
        Cargo cargo = user.getCargo();
        boolean allowed =
                cargo == Cargo.PROPRIETARIO
                        || cargo == Cargo.GERENTE
                        || cargo == Cargo.AGRONOMO_RESIDENTE
                        || cargo == Cargo.AGRONOMO_CONSULTOR
                        || cargo == Cargo.SUPERVISOR_DE_AREA
                        || cargo == Cargo.SECRETARIO;

        if (!allowed) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar esta pasta de cultura anual.");
        }
    }

    private void ensureHasPlotAccess(PlotModel plot, UserModel requestingUser) {
        PropertyModel property = plot.getProperty();

        if (property.getOwner().getId().equals(requestingUser.getId())) return;

        if (property.getManager() != null && property.getManager().getId().equals(requestingUser.getId())) return;

        if (requestingUser.getCargo() == Cargo.AGRONOMO_RESIDENTE) {
            boolean hasPropertyApproval = propertyAccessRequestRepository
                    .findByPropertyAndRequesterAndStatus(property, requestingUser, AccessRequestStatus.APPROVED)
                    .isPresent();

            if (!hasPropertyApproval) {
                throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este recurso.");
            }
            return;
        }

        boolean hasPlotApproval = plotAccessRequestRepository
                .findByPlotAndRequesterAndStatus(plot, requestingUser, AccessRequestStatus.APPROVED)
                .isPresent();

        if (!hasPlotApproval) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este recurso.");
        }
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private PlotModel findPlotByIdOrThrow(Long plotId) {
        return plotRepository.findById(plotId)
                .orElseThrow(() -> new EntityNotFoundException("Talhão não encontrado com o ID: " + plotId));
    }

    private AnnualCropFolderModel findAnnualCropFolderByIdOrThrow(Long annualCropFolderId) {
        return annualCropFolderRepository.findById(annualCropFolderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Pasta de cultura anual não encontrada com o ID: " + annualCropFolderId
                ));
    }
}