package com.migueltcc.fertintelligence.service.implementation;

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
import com.migueltcc.fertintelligence.repository.PlotRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.implementation.PermissionManager;
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
    private final UserRepository userRepository;
    private final CropRepository cropRepository;
    private final PermissionManager permissionManager;

    // evita possíveis ciclos de dependência (folder -> cropService -> folderService)
    private final @Lazy CropService cropService;

    @Override
    @Transactional
    public AnnualCropFolderResponseDto createAnnualCropFolder(
            Long plotId,
            AnnualCropFolderCreateRequestDto createRequestDto,
            String username
    ) {
        // mantém validação de existência do usuário (útil para mensagens de erro consistentes)
        findUserByUsernameOrThrow(username);

        PlotModel plot = findPlotByIdOrThrow(plotId);
        Long propertyId = plot.getProperty().getId();

        // Pasta anual => recurso de CULTURAS => precisa permissão de editar CULTURAS
        permissionManager.assertCanEditCrops(propertyId, plotId, username);

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
        // leitura: por enquanto só garante que o usuário existe
        findUserByUsernameOrThrow(username);

        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        AnnualCropFolderModel folder = findAnnualCropFolderByIdOrThrow(annualCropFolderId);

        PropertyModel property = folder.getPlot().getProperty();

        if (!property.getOwner().getId().equals(user.getId())) {

            // Se você tiver um PermissionManager, a chamada seria algo como:
            // permissionManager.assertCanReadPlot(property, folder.getPlot(), user);

            // Se não tiver um PermissionManager ainda, lance a exceção manualmente:
            throw new AccessDeniedException("Você não tem permissão para acessar recursos desta propriedade.");
        }

        return folder.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnualCropFolderResponseDto> getAllAnnualCropFoldersByPlot(Long plotId, String username) {
        findUserByUsernameOrThrow(username);

        PlotModel plot = findPlotByIdOrThrow(plotId);

        // idem: leitura liberada (enforcement de leitura pode entrar depois)
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
        findUserByUsernameOrThrow(username);

        AnnualCropFolderModel folder = findAnnualCropFolderByIdOrThrow(annualCropFolderId);
        PlotModel plot = folder.getPlot();
        Long propertyId = plot.getProperty().getId();

        // atualizar pasta anual => CULTURAS
        permissionManager.assertCanEditCrops(propertyId, plot.getId(), username);

        Integer newYear = updateRequestDto.getCropsYear();
        if (newYear != null && !newYear.equals(folder.getCropsYear())) {
            annualCropFolderRepository.findByPlotAndCropsYear(plot, newYear)
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
        findUserByUsernameOrThrow(username);

        AnnualCropFolderModel folder = findAnnualCropFolderByIdOrThrow(id);
        PlotModel plot = folder.getPlot();
        Long propertyId = plot.getProperty().getId();

        // deletar pasta anual => CULTURAS
        permissionManager.assertCanEditCrops(propertyId, plot.getId(), username);

        deleteAllCropsFromFolder(folder.getId(), username);

        annualCropFolderRepository.delete(folder);
        annualCropFolderRepository.flush();
    }

    private void deleteAllCropsFromFolder(Long folderId, String username) {
        List<CropModel> crops = cropRepository.findAllByFolderId(folderId);
        for (CropModel crop : crops) {
            // CropService deve aplicar assertCanEditCrops também (Etapa 4)
            cropService.deleteCrop(crop.getId(), username);
        }
        cropRepository.flush();
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