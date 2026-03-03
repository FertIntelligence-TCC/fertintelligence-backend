package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.BeneficialElementsContent;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.MacronutrientsContent;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.MicronutrientsContent;
import com.migueltcc.fertintelligence.dto.foliarAnalysis.BeneficialElementsContentDto;
import com.migueltcc.fertintelligence.dto.foliarAnalysis.FoliarAnalysisCreateRequestDto;
import com.migueltcc.fertintelligence.dto.foliarAnalysis.FoliarAnalysisPostRequestDto;
import com.migueltcc.fertintelligence.dto.foliarAnalysis.FoliarAnalysisResponseDto;
import com.migueltcc.fertintelligence.dto.foliarAnalysis.MacronutrientsContentDto;
import com.migueltcc.fertintelligence.dto.foliarAnalysis.MicronutrientsContentDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.FoliarAnalysisModel;
import com.migueltcc.fertintelligence.repository.CropRepository;
import com.migueltcc.fertintelligence.repository.FoliarAnalysisRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.security.PermissionManager;
import com.migueltcc.fertintelligence.service.documentation.FoliarAnalysisService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoliarAnalysisServiceImpl implements FoliarAnalysisService {

    private final FoliarAnalysisRepository foliarAnalysisRepository;
    private final CropRepository cropRepository;
    private final UserRepository userRepository;
    private final PermissionManager permissionManager;

    @Override
    @Transactional
    public FoliarAnalysisResponseDto createFoliarAnalysis(Long cropId, FoliarAnalysisCreateRequestDto createRequestDto, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        CropModel crop = findCropByIdOrThrow(cropId);
        permissionManager.assertCanWrite(crop.getFolder().getPlot(), requester);

        foliarAnalysisRepository.findByCropAndCollectDate(crop, createRequestDto.getCollectDate())
                .ifPresent(existing -> {
                    throw new EntityExistsException("Já existe uma análise foliar para a data informada nesta cultura: "
                            + formatDate(createRequestDto.getCollectDate()));
                });

        FoliarAnalysisModel foliarAnalysis = FoliarAnalysisModel.builder()
                .crop(crop)
                .collectDate(copyDate(createRequestDto.getCollectDate()))
                .laboratory(createRequestDto.getLaboratory())
                .micronutrients(copyMicronutrientsContent(createRequestDto.getMicronutrients()))
                .macronutrients(copyMacronutrientsContent(createRequestDto.getMacronutrients()))
                .elements(copyBeneficialElementsContent(createRequestDto.getElements()))
                .build();

        return foliarAnalysisRepository.save(foliarAnalysis).toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public FoliarAnalysisResponseDto getFoliarAnalysisById(Long foliarAnalysisId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        FoliarAnalysisModel foliarAnalysis = findFoliarAnalysisByIdOrThrow(foliarAnalysisId);
        permissionManager.assertCanRead(foliarAnalysis.getCrop().getFolder().getPlot(), requester);

        return foliarAnalysis.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoliarAnalysisResponseDto> getAllFoliarAnalysesByCrop(Long cropId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        CropModel crop = findCropByIdOrThrow(cropId);
        permissionManager.assertCanRead(crop.getFolder().getPlot(), requester);

        return foliarAnalysisRepository.findAllByCrop(crop).stream()
                .map(FoliarAnalysisModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FoliarAnalysisResponseDto updateFoliarAnalysis(Long foliarAnalysisId, FoliarAnalysisPostRequestDto updateRequestDto, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        FoliarAnalysisModel foliarAnalysis = findFoliarAnalysisByIdOrThrow(foliarAnalysisId);
        CropModel crop = foliarAnalysis.getCrop();

        permissionManager.assertCanWrite(crop.getFolder().getPlot(), requester);

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
        if (updateRequestDto.getMicronutrients() != null) {
            foliarAnalysis.setMicronutrients(copyMicronutrientsContent(updateRequestDto.getMicronutrients()));
        }
        if (updateRequestDto.getMacronutrients() != null) {
            foliarAnalysis.setMacronutrients(copyMacronutrientsContent(updateRequestDto.getMacronutrients()));
        }
        if (updateRequestDto.getElements() != null) {
            foliarAnalysis.setElements(copyBeneficialElementsContent(updateRequestDto.getElements()));
        }

        return foliarAnalysisRepository.save(foliarAnalysis).toDto();
    }

    @Override
    @Transactional
    public void deleteFoliarAnalysis(Long foliarAnalysisId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        FoliarAnalysisModel foliarAnalysis = findFoliarAnalysisByIdOrThrow(foliarAnalysisId);
        permissionManager.assertCanWrite(foliarAnalysis.getCrop().getFolder().getPlot(), requester);

        foliarAnalysisRepository.delete(foliarAnalysis);
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

    private Date copyDate(Date source) {
        if (source == null) return null;
        return new Date(source.getDay(), source.getMonth(), source.getYear());
    }

    private String formatDate(Date date) {
        if (date == null) return "";
        return String.format("%02d/%02d/%04d", date.getDay(), date.getMonth(), date.getYear());
    }

    private MicronutrientsContent copyMicronutrientsContent(MicronutrientsContentDto dto) {
        if (dto == null) return null;
        return new MicronutrientsContent(
                dto.getB_content(),
                dto.getCu_content(),
                dto.getFe_content(),
                dto.getNi_content(),
                dto.getMn_content(),
                dto.getMo_content(),
                dto.getZn_content()
        );
    }

    private MacronutrientsContent copyMacronutrientsContent(MacronutrientsContentDto dto) {
        if (dto == null) return null;
        return new MacronutrientsContent(
                dto.getN_content(),
                dto.getP_content(),
                dto.getK_content(),
                dto.getCa_content(),
                dto.getMg_content(),
                dto.getS_content()
        );
    }

    private BeneficialElementsContent copyBeneficialElementsContent(BeneficialElementsContentDto dto) {
        if (dto == null) return null;
        return new BeneficialElementsContent(
                dto.getNa_content(),
                dto.getSi_content(),
                dto.getV_content(),
                dto.getCo_content(),
                dto.getSe_content()
        );
    }
}