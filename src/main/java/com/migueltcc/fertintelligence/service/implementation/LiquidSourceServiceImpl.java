package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
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

        @Override
        @Transactional
        public LiquidSourceResponseDto createLiquidSource(Long cropId, LiquidSourceCreateRequestDto createRequestDto, String username) {
            UserModel owner = findUserByUsernameOrThrow(username);
            checkUserIsProprietario(owner);

            CropModel crop = findCropByIdOrThrow(cropId);
            checkOwnerPermission(crop.getFolder(), owner);

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
            checkUserIsProprietario(owner);

            LiquidSourceModel liquidSource = findLiquidSourceByIdOrThrow(liquidSourceId);
            checkOwnerPermission(liquidSource.getCrop().getFolder(), owner);

            return liquidSource.toDto();
        }

        @Override
        @Transactional(readOnly = true)
        public List<LiquidSourceResponseDto> getAllLiquidSourcesByCrop(Long cropId, String username) {
            UserModel owner = findUserByUsernameOrThrow(username);
            checkUserIsProprietario(owner);

            CropModel crop = findCropByIdOrThrow(cropId);
            checkOwnerPermission(crop.getFolder(), owner);

            return liquidSourceRepository.findAllByCrop(crop).stream()
                    .map(LiquidSourceModel::toDto)
                    .collect(Collectors.toList());
        }

        @Override
        @Transactional
        public LiquidSourceResponseDto updateLiquidSource(Long liquidSourceId, LiquidSourcePostRequestDto updateRequestDto, String username) {
            UserModel owner = findUserByUsernameOrThrow(username);
            checkUserIsProprietario(owner);

            LiquidSourceModel liquidSource = findLiquidSourceByIdOrThrow(liquidSourceId);
            CropModel crop = liquidSource.getCrop();
            checkOwnerPermission(crop.getFolder(), owner);

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
            checkUserIsProprietario(owner);

            LiquidSourceModel liquidSource = findLiquidSourceByIdOrThrow(liquidSourceId);
            checkOwnerPermission(liquidSource.getCrop().getFolder(), owner);

            liquidSourceRepository.delete(liquidSource);
        }

        private void checkUserIsProprietario(UserModel user) {
            if (user.getCargo() != Cargo.PROPRIETARIO) {
                throw new AccessDeniedException("Acesso negado. Apenas usuários com o cargo 'PROPRIETARIO' podem gerenciar fontes líquidas de adubação foliar.");
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
    }