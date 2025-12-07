package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.topDressingFertilization.TopDressingFertilizationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.topDressingFertilization.TopDressingFertilizationPostRequestDto;
import com.migueltcc.fertintelligence.dto.topDressingFertilization.TopDressingFertilizationResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.AnnualCropFolderModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.TopdressingFertilizationModel;
import com.migueltcc.fertintelligence.repository.CropRepository;
import com.migueltcc.fertintelligence.repository.PlotAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.PropertyAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.TopDressingFertilizationRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.TopDressingFertilizationService;
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
public class TopDressingFertilizationServiceImpl implements TopDressingFertilizationService {

    @Autowired
    private TopDressingFertilizationRepository topDressingFertilizationRepository;

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
    public TopDressingFertilizationResponseDto createTopDressingFertilization(Long cropId,
                                                                              TopDressingFertilizationCreateRequestDto createRequestDto,
                                                                              String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        CropModel crop = findCropByIdOrThrow(cropId);
        checkOwnerPermission(crop.getFolder(), owner);

        topDressingFertilizationRepository.findByCropAndOrder(crop, createRequestDto.getOrder())
                .ifPresent(existing -> {
                    throw new EntityExistsException("Já existe uma adubação de cobertura com a ordem informada nesta cultura: "
                            + createRequestDto.getOrder());
                });

        TopdressingFertilizationModel fertilization = TopdressingFertilizationModel.builder()
                .crop(crop)
                .date(copyDate(createRequestDto.getDate()))
                .order(createRequestDto.getOrder())
                .formulated(createRequestDto.getFormulated())
                .ammonium_sulfate(createRequestDto.getAmmonium_sulfate())
                .urea(createRequestDto.getUrea())
                .potassium_chloride(createRequestDto.getPotassium_chloride())
                .triple_superphosphate(createRequestDto.getTriple_superphosphate())
                .simple_superphosphate(createRequestDto.getSimple_superphosphate())
                .monoammonium_phosphate(createRequestDto.getMonoammonium_phosphate())
                .build();

        TopdressingFertilizationModel savedFertilization = topDressingFertilizationRepository.save(fertilization);
        return savedFertilization.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public TopDressingFertilizationResponseDto getTopDressingFertilizationById(Long fertilizationId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        TopdressingFertilizationModel fertilization = findFertilizationByIdOrThrow(fertilizationId);
        checkOwnerPermission(fertilization.getCrop().getFolder(), owner);

        return fertilization.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopDressingFertilizationResponseDto> getAllTopDressingFertilizationsByCrop(Long cropId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        CropModel crop = findCropByIdOrThrow(cropId);
        checkOwnerPermission(crop.getFolder(), owner);

        return topDressingFertilizationRepository.findAllByCrop(crop).stream()
                .map(TopdressingFertilizationModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TopDressingFertilizationResponseDto updateTopDressingFertilization(Long fertilizationId,
                                                                              TopDressingFertilizationPostRequestDto updateRequestDto,
                                                                              String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        TopdressingFertilizationModel fertilization = findFertilizationByIdOrThrow(fertilizationId);
        CropModel crop = fertilization.getCrop();
        checkOwnerPermission(crop.getFolder(), owner);

        if (updateRequestDto.getOrder() != null && !Objects.equals(updateRequestDto.getOrder(), fertilization.getOrder())) {
            topDressingFertilizationRepository.findByCropAndOrder(crop, updateRequestDto.getOrder())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(fertilizationId)) {
                            throw new EntityExistsException("Já existe uma adubação de cobertura com a ordem informada nesta cultura: "
                                    + updateRequestDto.getOrder());
                        }
                    });
            fertilization.setOrder(updateRequestDto.getOrder());
        }

        if (updateRequestDto.getDate() != null) {
            fertilization.setDate(copyDate(updateRequestDto.getDate()));
        }

        if (updateRequestDto.getFormulated() != null) {
            fertilization.setFormulated(updateRequestDto.getFormulated());
        }

        if (updateRequestDto.getAmmonium_sulfate() != null) {
            fertilization.setAmmonium_sulfate(updateRequestDto.getAmmonium_sulfate());
        }

        if (updateRequestDto.getUrea() != null) {
            fertilization.setUrea(updateRequestDto.getUrea());
        }

        if (updateRequestDto.getPotassium_chloride() != null) {
            fertilization.setPotassium_chloride(updateRequestDto.getPotassium_chloride());
        }

        if (updateRequestDto.getTriple_superphosphate() != null) {
            fertilization.setTriple_superphosphate(updateRequestDto.getTriple_superphosphate());
        }

        if (updateRequestDto.getSimple_superphosphate() != null) {
            fertilization.setSimple_superphosphate(updateRequestDto.getSimple_superphosphate());
        }

        if (updateRequestDto.getMonoammonium_phosphate() != null) {
            fertilization.setMonoammonium_phosphate(updateRequestDto.getMonoammonium_phosphate());
        }

        TopdressingFertilizationModel updatedFertilization = topDressingFertilizationRepository.save(fertilization);
        return updatedFertilization.toDto();
    }

    @Override
    @Transactional
    public void deleteTopDressingFertilization(Long fertilizationId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        TopdressingFertilizationModel fertilization = findFertilizationByIdOrThrow(fertilizationId);
        checkOwnerPermission(fertilization.getCrop().getFolder(), owner);

        topDressingFertilizationRepository.delete(fertilization);
    }

    private void checkUserIsProprietario(UserModel user) {
        if (user.getCargo() != Cargo.PROPRIETARIO
                && user.getCargo() != Cargo.GERENTE
                && user.getCargo() != Cargo.AGRONOMO_RESIDENTE
                && user.getCargo() != Cargo.AGRONOMO_CONSULTOR
                && user.getCargo() != Cargo.SECRETARIO) {
            throw new AccessDeniedException("Acesso negado. Você não tem permissão para gerenciar adubações de cobertura.");
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

    private TopdressingFertilizationModel findFertilizationByIdOrThrow(Long fertilizationId) {
        return topDressingFertilizationRepository.findById(fertilizationId)
                .orElseThrow(() -> new EntityNotFoundException("Adubação de cobertura não encontrada com o ID: " + fertilizationId));
    }

    private void checkOwnerPermission(AnnualCropFolderModel folder, UserModel requestingUser) {
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