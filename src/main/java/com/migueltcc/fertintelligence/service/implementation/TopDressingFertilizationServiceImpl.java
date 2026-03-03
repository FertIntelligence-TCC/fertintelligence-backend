package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.dto.topDressingFertilization.TopDressingFertilizationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.topDressingFertilization.TopDressingFertilizationPostRequestDto;
import com.migueltcc.fertintelligence.dto.topDressingFertilization.TopDressingFertilizationResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.TopdressingFertilizationModel;
import com.migueltcc.fertintelligence.repository.CropRepository;
import com.migueltcc.fertintelligence.repository.TopDressingFertilizationRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.security.PermissionManager;
import com.migueltcc.fertintelligence.service.documentation.TopDressingFertilizationService;
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
public class TopDressingFertilizationServiceImpl implements TopDressingFertilizationService {

    private final TopDressingFertilizationRepository topDressingFertilizationRepository;
    private final CropRepository cropRepository;
    private final UserRepository userRepository;
    private final PermissionManager permissionManager;

    @Override
    @Transactional
    public TopDressingFertilizationResponseDto createTopDressingFertilization(
            Long cropId,
            TopDressingFertilizationCreateRequestDto createRequestDto,
            String username
    ) {
        UserModel requester = findUserByUsernameOrThrow(username);

        CropModel crop = findCropByIdOrThrow(cropId);
        permissionManager.assertCanWrite(crop.getFolder().getPlot(), requester);

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

        return topDressingFertilizationRepository.save(fertilization).toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public TopDressingFertilizationResponseDto getTopDressingFertilizationById(Long fertilizationId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        TopdressingFertilizationModel fertilization = findFertilizationByIdOrThrow(fertilizationId);
        permissionManager.assertCanRead(fertilization.getCrop().getFolder().getPlot(), requester);

        return fertilization.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopDressingFertilizationResponseDto> getAllTopDressingFertilizationsByCrop(Long cropId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        CropModel crop = findCropByIdOrThrow(cropId);
        permissionManager.assertCanRead(crop.getFolder().getPlot(), requester);

        return topDressingFertilizationRepository.findAllByCrop(crop).stream()
                .map(TopdressingFertilizationModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TopDressingFertilizationResponseDto updateTopDressingFertilization(
            Long fertilizationId,
            TopDressingFertilizationPostRequestDto updateRequestDto,
            String username
    ) {
        UserModel requester = findUserByUsernameOrThrow(username);

        TopdressingFertilizationModel fertilization = findFertilizationByIdOrThrow(fertilizationId);
        CropModel crop = fertilization.getCrop();

        permissionManager.assertCanWrite(crop.getFolder().getPlot(), requester);

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

        if (updateRequestDto.getDate() != null) fertilization.setDate(copyDate(updateRequestDto.getDate()));
        if (updateRequestDto.getFormulated() != null) fertilization.setFormulated(updateRequestDto.getFormulated());
        if (updateRequestDto.getAmmonium_sulfate() != null) fertilization.setAmmonium_sulfate(updateRequestDto.getAmmonium_sulfate());
        if (updateRequestDto.getUrea() != null) fertilization.setUrea(updateRequestDto.getUrea());
        if (updateRequestDto.getPotassium_chloride() != null) fertilization.setPotassium_chloride(updateRequestDto.getPotassium_chloride());
        if (updateRequestDto.getTriple_superphosphate() != null) fertilization.setTriple_superphosphate(updateRequestDto.getTriple_superphosphate());
        if (updateRequestDto.getSimple_superphosphate() != null) fertilization.setSimple_superphosphate(updateRequestDto.getSimple_superphosphate());
        if (updateRequestDto.getMonoammonium_phosphate() != null) fertilization.setMonoammonium_phosphate(updateRequestDto.getMonoammonium_phosphate());

        return topDressingFertilizationRepository.save(fertilization).toDto();
    }

    @Override
    @Transactional
    public void deleteTopDressingFertilization(Long fertilizationId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        TopdressingFertilizationModel fertilization = findFertilizationByIdOrThrow(fertilizationId);
        permissionManager.assertCanWrite(fertilization.getCrop().getFolder().getPlot(), requester);

        topDressingFertilizationRepository.delete(fertilization);
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

    private Date copyDate(Date source) {
        if (source == null) return null;
        return new Date(source.getDay(), source.getMonth(), source.getYear());
    }
}