package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.dto.generalRecommendation.GeneralRecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.generalRecommendation.GeneralRecommendationPostRequestDto;
import com.migueltcc.fertintelligence.dto.generalRecommendation.GeneralRecommendationResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.GeneralRecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.GeneralRecommendationRepository;
import com.migueltcc.fertintelligence.repository.RecommendationRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.GeneralRecommendationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GeneralRecommendationServiceImpl implements GeneralRecommendationService {

    private final GeneralRecommendationRepository generalRecommendationRepository;
    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;
    private final PermissionManager permissionManager;

    @Override
    @Transactional
    public GeneralRecommendationResponseDto create(GeneralRecommendationCreateRequestDto dto, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        RecommendationModel recommendation = findRecommendationByIdForUpdateOrThrow(dto.getRecommendationId());
        permissionManager.assertCanReadPlot(recommendation.getPlot(), user);
        GeneralRecommendationModel generalRecommendation = createInitial(recommendation, dto.getTechnicalReport());
        return toDto(generalRecommendation);
    }

    @Override
    @Transactional
    public GeneralRecommendationModel createInitial(RecommendationModel recommendation, String technicalReport) {
        if (recommendation == null || recommendation.getId() == null) {
            throw new IllegalArgumentException("A recomendação precisa estar salva antes da criação da Recomendação Geral.");
        }
        if (technicalReport == null || technicalReport.isBlank()) {
            throw new IllegalArgumentException("O conteúdo da Recomendação Geral não pode ser vazio.");
        }
        return generalRecommendationRepository.findByRecommendation(recommendation)
                .orElseGet(() -> saveNew(recommendation, technicalReport));
    }

    private GeneralRecommendationModel saveNew(RecommendationModel recommendation, String technicalReport) {
        GeneralRecommendationModel generalRecommendation = GeneralRecommendationModel.builder()
                .recommendation(recommendation)
                .documentName(GeneralRecommendationModel.DOCUMENT_NAME)
                .technicalReport(technicalReport)
                .build();

        try {
            GeneralRecommendationModel saved = generalRecommendationRepository.saveAndFlush(generalRecommendation);
            recommendation.setGeneralRecommendation(saved);
            return saved;
        } catch (DataIntegrityViolationException ex) {
            return generalRecommendationRepository.findByRecommendation(recommendation)
                    .orElseThrow(() -> ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public GeneralRecommendationResponseDto get(Long id, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        GeneralRecommendationModel generalRecommendation = findGeneralRecommendationByIdOrThrow(id);
        permissionManager.assertCanReadPlot(generalRecommendation.getRecommendation().getPlot(), user);
        return toDto(generalRecommendation);
    }

    @Override
    @Transactional(readOnly = true)
    public GeneralRecommendationResponseDto getByRecommendation(Long recommendationId, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        RecommendationModel recommendation = findRecommendationByIdOrThrow(recommendationId);
        permissionManager.assertCanReadPlot(recommendation.getPlot(), user);
        return generalRecommendationRepository.findByRecommendation(recommendation)
                .map(this::toDto)
                .orElseGet(() -> legacyFallbackDto(recommendation));
    }

    @Override
    @Transactional
    public GeneralRecommendationResponseDto update(Long id, GeneralRecommendationPostRequestDto dto, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        GeneralRecommendationModel generalRecommendation = findGeneralRecommendationByIdOrThrow(id);
        permissionManager.assertCanReadPlot(generalRecommendation.getRecommendation().getPlot(), user);

        if (dto.getNewTechnicalReport() != null && !dto.getNewTechnicalReport().isBlank()) {
            generalRecommendation.setTechnicalReport(dto.getNewTechnicalReport());
            generalRecommendation.getRecommendation().setTechnicalReport(dto.getNewTechnicalReport());
        }

        return toDto(generalRecommendationRepository.save(generalRecommendation));
    }

    @Override
    @Transactional
    public void delete(Long id, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        GeneralRecommendationModel generalRecommendation = findGeneralRecommendationByIdOrThrow(id);
        permissionManager.assertCanReadPlot(generalRecommendation.getRecommendation().getPlot(), user);
        generalRecommendation.getRecommendation().setGeneralRecommendation(null);
        generalRecommendationRepository.delete(generalRecommendation);
    }

    private GeneralRecommendationResponseDto toDto(GeneralRecommendationModel model) {
        return GeneralRecommendationResponseDto.builder()
                .id(model.getId())
                .recommendationId(model.getRecommendation().getId())
                .documentName(model.getDocumentName() != null ? model.getDocumentName() : GeneralRecommendationModel.DOCUMENT_NAME)
                .technicalReport(model.getTechnicalReport())
                .content(model.getTechnicalReport())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }

    private GeneralRecommendationResponseDto legacyFallbackDto(RecommendationModel recommendation) {
        return GeneralRecommendationResponseDto.builder()
                .id(null)
                .recommendationId(recommendation.getId())
                .documentName(GeneralRecommendationModel.DOCUMENT_NAME)
                .technicalReport(recommendation.getTechnicalReport())
                .content(recommendation.getTechnicalReport())
                .generated(recommendation.getTechnicalReport() != null && !recommendation.getTechnicalReport().isBlank())
                .createdAt(recommendation.getCreatedAt())
                .updatedAt(recommendation.getUpdatedAt())
                .build();
    }

    private UserModel findUserByUsernameOrEmailOrThrow(String usernameOrEmail) {
        return userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + usernameOrEmail));
    }

    private RecommendationModel findRecommendationByIdOrThrow(Long id) {
        return recommendationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recomendação não encontrada com o ID: " + id));
    }

    private RecommendationModel findRecommendationByIdForUpdateOrThrow(Long id) {
        return recommendationRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new EntityNotFoundException("Recomendação não encontrada com o ID: " + id));
    }

    private GeneralRecommendationModel findGeneralRecommendationByIdOrThrow(Long id) {
        return generalRecommendationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recomendação Geral não encontrada com o ID: " + id));
    }
}
