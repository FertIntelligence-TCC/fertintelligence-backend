package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.dto.annualCropFolder.AnnualCropFolderCreateRequestDto;
import com.migueltcc.fertintelligence.dto.annualCropFolder.AnnualCropFolderPostRequestDto;
import com.migueltcc.fertintelligence.dto.annualCropFolder.AnnualCropFolderResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.AnnualCropFolderModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.AnnualCropFolderRepository;
import com.migueltcc.fertintelligence.repository.PlotAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.PlotRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.AnnualCropFolderService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnnualCropFolderServiceImpl implements AnnualCropFolderService {

    @Autowired
    private AnnualCropFolderRepository annualCropFolderRepository;

    @Autowired
    private PlotRepository plotRepository;

    @Autowired
    private PlotAccessRequestRepository plotAccessRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public AnnualCropFolderResponseDto createAnnualCropFolder(Long plotId,
                                                              AnnualCropFolderCreateRequestDto createRequestDto,
                                                              String username) {
        UserModel requestingUser = findUserByUsernameOrThrow(username);

        PlotModel plot = findPlotByIdOrThrow(plotId);
        checkPlotPermission(plot, requestingUser);

        annualCropFolderRepository.findByPlotAndCropsYear(plot, createRequestDto.getCropsYear())
                .ifPresent(existing -> {
                    throw new EntityExistsException("Já existe uma pasta de cultura anual para o ano informado neste talhão: "
                            + createRequestDto.getCropsYear());
                });

        AnnualCropFolderModel annualCropFolder = AnnualCropFolderModel.builder()
                .plot(plot)
                .cropsYear(createRequestDto.getCropsYear())
                .build();

        AnnualCropFolderModel savedAnnualCropFolder = annualCropFolderRepository.save(annualCropFolder);
        return savedAnnualCropFolder.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public AnnualCropFolderResponseDto getAnnualCropFolderById(Long annualCropFolderId, String username) {
        UserModel requestingUser = findUserByUsernameOrThrow(username);

        AnnualCropFolderModel annualCropFolder = findAnnualCropFolderByIdOrThrow(annualCropFolderId);
        checkPlotPermission(annualCropFolder.getPlot(), requestingUser);

        return annualCropFolder.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnualCropFolderResponseDto> getAllAnnualCropFoldersByPlot(Long plotId, String username) {
        UserModel requestingUser = findUserByUsernameOrThrow(username);

        PlotModel plot = findPlotByIdOrThrow(plotId);
        checkPlotPermission(plot, requestingUser);

        return annualCropFolderRepository.findAllByPlot(plot).stream()
                .map(AnnualCropFolderModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AnnualCropFolderResponseDto updateAnnualCropFolder(Long annualCropFolderId,
                                                              AnnualCropFolderPostRequestDto updateRequestDto,
                                                              String username) {
        UserModel requestingUser = findUserByUsernameOrThrow(username);

        AnnualCropFolderModel annualCropFolder = findAnnualCropFolderByIdOrThrow(annualCropFolderId);
        checkPlotPermission(annualCropFolder.getPlot(), requestingUser);

        if (updateRequestDto.getCropsYear() != null
                && !updateRequestDto.getCropsYear().equals(annualCropFolder.getCropsYear())) {
            annualCropFolderRepository.findByPlotAndCropsYear(annualCropFolder.getPlot(), updateRequestDto.getCropsYear())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(annualCropFolderId)) {
                            throw new EntityExistsException("Já existe uma pasta de cultura anual para o ano informado neste talhão: "
                                    + updateRequestDto.getCropsYear());
                        }
                    });
            annualCropFolder.setCropsYear(updateRequestDto.getCropsYear());
        }

        AnnualCropFolderModel updatedAnnualCropFolder = annualCropFolderRepository.save(annualCropFolder);
        return updatedAnnualCropFolder.toDto();
    }

    @Override
    @Transactional
    public void deleteAnnualCropFolder(Long annualCropFolderId, String username) {
        UserModel requestingUser = findUserByUsernameOrThrow(username);

        AnnualCropFolderModel annualCropFolder = findAnnualCropFolderByIdOrThrow(annualCropFolderId);
        checkPlotPermission(annualCropFolder.getPlot(), requestingUser);

        annualCropFolderRepository.delete(annualCropFolder);
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
                .orElseThrow(() -> new EntityNotFoundException("Pasta de cultura anual não encontrada com o ID: " + annualCropFolderId));
    }

    private void checkPlotPermission(PlotModel plot, UserModel requestingUser) {
        PropertyModel property = plot.getProperty();

        if (property.getOwner().getId().equals(requestingUser.getId())) {
            return;
        }

        if (property.getManager() != null && property.getManager().getId().equals(requestingUser.getId())) {
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
    }
}
