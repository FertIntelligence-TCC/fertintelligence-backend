package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.foliarAnalysis.FoliarAnalysisCreateRequestDto;
import com.migueltcc.fertintelligence.dto.foliarAnalysis.FoliarAnalysisPostRequestDto;
import com.migueltcc.fertintelligence.dto.foliarAnalysis.FoliarAnalysisResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.AnnualCropFolderModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.foliarAnalysisModels.FoliarAnalysisModel;
import com.migueltcc.fertintelligence.repository.CropRepository;
import com.migueltcc.fertintelligence.repository.FoliarAnalysisRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.FoliarAnalysisService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FoliarAnalysisServiceImpl implements FoliarAnalysisService {

    @Autowired
    private FoliarAnalysisRepository foliarAnalysisRepository;

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public FoliarAnalysisResponseDto createFoliarAnalysis(Long cropId, FoliarAnalysisCreateRequestDto createRequestDto, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        CropModel crop = findCropByIdOrThrow(cropId);
        checkOwnerPermission(crop.getFolder(), owner);

        foliarAnalysisRepository.findByCropAndCollectDate(crop, createRequestDto.getCollectDate())
                .ifPresent(existing -> {
                    throw new EntityExistsException("Já existe uma análise foliar para a data informada nesta cultura: "
                            + formatDate(createRequestDto.getCollectDate()));
                });

        FoliarAnalysisModel foliarAnalysis = FoliarAnalysisModel.builder()
                .crop(crop)
                .collectDate(copyDate(createRequestDto.getCollectDate()))
                .laboratory(createRequestDto.getLaboratory())
                .build();

        FoliarAnalysisModel savedAnalysis = foliarAnalysisRepository.save(foliarAnalysis);
        return savedAnalysis.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public FoliarAnalysisResponseDto getFoliarAnalysisById(Long foliarAnalysisId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        FoliarAnalysisModel foliarAnalysis = findFoliarAnalysisByIdOrThrow(foliarAnalysisId);
        checkOwnerPermission(foliarAnalysis.getCrop().getFolder(), owner);

        return foliarAnalysis.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoliarAnalysisResponseDto> getAllFoliarAnalysesByCrop(Long cropId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        CropModel crop = findCropByIdOrThrow(cropId);
        checkOwnerPermission(crop.getFolder(), owner);

        return foliarAnalysisRepository.findAllByCrop(crop).stream()
                .map(FoliarAnalysisModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FoliarAnalysisResponseDto updateFoliarAnalysis(Long foliarAnalysisId, FoliarAnalysisPostRequestDto updateRequestDto, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        FoliarAnalysisModel foliarAnalysis = findFoliarAnalysisByIdOrThrow(foliarAnalysisId);
        CropModel crop = foliarAnalysis.getCrop();
        checkOwnerPermission(crop.getFolder(), owner);

        if (updateRequestDto.getCollectDate() != null
                && !Objects.equals(updateRequestDto.getCollectDate(), foliarAnalysis.getCollectDate())) {
            foliarAnalysisRepository.findByCropAndCollectDate(crop, updateRequestDto.getCollectDate())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(foliarAnalysisId)) {
                            throw new EntityExistsException("Já existe uma análise foliar para a data informada nesta cultura: "
                                    + formatDate(updateRequestDto.getCollectDate()));
                        }
                    });
            foliarAnalysis.setCollectDate(copyDate(updateRequestDto.getCollectDate()));
        }

        if (updateRequestDto.getLaboratory() != null) {
            foliarAnalysis.setLaboratory(updateRequestDto.getLaboratory());
        }

        FoliarAnalysisModel updatedAnalysis = foliarAnalysisRepository.save(foliarAnalysis);
        return updatedAnalysis.toDto();
    }

    @Override
    @Transactional
    public void deleteFoliarAnalysis(Long foliarAnalysisId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        FoliarAnalysisModel foliarAnalysis = findFoliarAnalysisByIdOrThrow(foliarAnalysisId);
        checkOwnerPermission(foliarAnalysis.getCrop().getFolder(), owner);

        foliarAnalysisRepository.delete(foliarAnalysis);
    }

    private void checkUserIsProprietario(UserModel user) {
        if (user.getCargo() != Cargo.PROPRIETARIO) {
            throw new AccessDeniedException("Acesso negado. Apenas usuários com o cargo 'PROPRIETARIO' podem gerenciar análises foliares.");
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

    private FoliarAnalysisModel findFoliarAnalysisByIdOrThrow(Long foliarAnalysisId) {
        return foliarAnalysisRepository.findById(foliarAnalysisId)
                .orElseThrow(() -> new EntityNotFoundException("Análise foliar não encontrada com o ID: " + foliarAnalysisId));
    }

    private void checkOwnerPermission(AnnualCropFolderModel folder, UserModel requestingUser) {
        PlotModel plot = folder.getPlot();
        PropertyModel property = plot.getProperty();
        if (!property.getOwner().getId().equals(requestingUser.getId())) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar este recurso.");
        }
    }

    private Date copyDate(Date source) {
        if (source == null) {
            return null;
        }
        return new Date(source.getDay(), source.getMonth(), source.getYear());
    }

    private String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return String.format("%02d/%02d/%04d", date.getDay(), date.getMonth(), date.getYear());
    }
}
