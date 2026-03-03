package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.dto.foliarFertilization.solid.SolidSourceCreateRequestDto;
import com.migueltcc.fertintelligence.dto.foliarFertilization.solid.SolidSourcePostRequestDto;
import com.migueltcc.fertintelligence.dto.foliarFertilization.solid.SolidSourceResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.foliarFertilizationModels.SolidSourceModel;
import com.migueltcc.fertintelligence.repository.CropRepository;
import com.migueltcc.fertintelligence.repository.SolidSourceRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.security.PermissionManager;
import com.migueltcc.fertintelligence.service.documentation.SolidSourceService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SolidSourceServiceImpl implements SolidSourceService {

    private final SolidSourceRepository solidSourceRepository;
    private final CropRepository cropRepository;
    private final UserRepository userRepository;
    private final PermissionManager permissionManager;

    @Override
    @Transactional
    public SolidSourceResponseDto createSolidSource(Long cropId, SolidSourceCreateRequestDto createRequestDto, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        CropModel crop = findCropByIdOrThrow(cropId);
        permissionManager.assertCanWrite(crop.getFolder().getPlot(), requester);

        SolidSourceModel solidSource = SolidSourceModel.builder()
                .crop(crop)
                .date(copyDate(createRequestDto.getDate()))
                .micronutrient(createRequestDto.getMicronutrient())
                .source(createRequestDto.getSource())
                .concentration(createRequestDto.getConcentration())
                .quantity(createRequestDto.getQuantity())
                .build();

        return solidSourceRepository.save(solidSource).toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public SolidSourceResponseDto getSolidSourceById(Long solidSourceId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        SolidSourceModel solidSource = findSolidSourceByIdOrThrow(solidSourceId);
        permissionManager.assertCanRead(solidSource.getCrop().getFolder().getPlot(), requester);

        return solidSource.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolidSourceResponseDto> getAllSolidSourcesByCrop(Long cropId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        CropModel crop = findCropByIdOrThrow(cropId);
        permissionManager.assertCanRead(crop.getFolder().getPlot(), requester);

        return solidSourceRepository.findAllByCrop(crop).stream()
                .map(SolidSourceModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SolidSourceResponseDto updateSolidSource(Long solidSourceId, SolidSourcePostRequestDto updateRequestDto, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        SolidSourceModel solidSource = findSolidSourceByIdOrThrow(solidSourceId);
        permissionManager.assertCanWrite(solidSource.getCrop().getFolder().getPlot(), requester);

        if (updateRequestDto.getDate() != null) solidSource.setDate(copyDate(updateRequestDto.getDate()));
        if (updateRequestDto.getMicronutrient() != null) solidSource.setMicronutrient(updateRequestDto.getMicronutrient());
        if (updateRequestDto.getSource() != null) solidSource.setSource(updateRequestDto.getSource());
        if (updateRequestDto.getConcentration() != null) solidSource.setConcentration(updateRequestDto.getConcentration());
        if (updateRequestDto.getQuantity() != null) solidSource.setQuantity(updateRequestDto.getQuantity());

        return solidSourceRepository.save(solidSource).toDto();
    }

    @Override
    @Transactional
    public void deleteSolidSource(Long solidSourceId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        SolidSourceModel solidSource = findSolidSourceByIdOrThrow(solidSourceId);
        permissionManager.assertCanWrite(solidSource.getCrop().getFolder().getPlot(), requester);

        solidSourceRepository.delete(solidSource);
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

    private Date copyDate(Date source) {
        if (source == null) return null;
        return new Date(source.getDay(), source.getMonth(), source.getYear());
    }
}