package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.dto.shoppingList.ShoppingListCreateRequestDto;
import com.migueltcc.fertintelligence.dto.shoppingList.ShoppingListPostRequestDto;
import com.migueltcc.fertintelligence.dto.shoppingList.ShoppingListResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.ShoppingListModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.RecommendationRepository;
import com.migueltcc.fertintelligence.repository.ShoppingListRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.ShoppingListService;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.RecommendationStructuredDataAssembler;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.ShoppingListReportService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShoppingListServiceImpl implements ShoppingListService {

    private final ShoppingListRepository shoppingListRepository;
    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;
    private final PermissionManager permissionManager;
    private final ShoppingListReportService shoppingListReportService;
    private final RecommendationStructuredDataAssembler structuredDataAssembler;

    @Override
    @Transactional
    public ShoppingListResponseDto create(ShoppingListCreateRequestDto dto, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        RecommendationModel recommendation = findRecommendationByIdOrThrow(dto.getRecommendationId());
        permissionManager.assertCanReadPlot(recommendation.getPlot(), user);
        ShoppingListModel shoppingList = createInitial(recommendation, dto.getTechnicalReport());
        return toDto(shoppingList);
    }

    @Override
    @Transactional
    public ShoppingListModel createInitial(RecommendationModel recommendation, String technicalReport) {
        if (recommendation == null || recommendation.getId() == null) {
            throw new IllegalArgumentException("A recomendação precisa estar salva antes da criação da Lista de Compras.");
        }
        if (technicalReport == null || technicalReport.isBlank()) {
            throw new IllegalArgumentException("O conteúdo da Lista de Compras não pode ser vazio.");
        }
        return shoppingListRepository.findByRecommendation(recommendation)
                .orElseGet(() -> saveNew(recommendation, technicalReport));
    }

    private ShoppingListModel saveNew(RecommendationModel recommendation, String technicalReport) {
        ShoppingListModel shoppingList = ShoppingListModel.builder()
                .recommendation(recommendation)
                .documentName(ShoppingListModel.DOCUMENT_NAME)
                .technicalReport(technicalReport)
                .build();

        try {
            ShoppingListModel saved = shoppingListRepository.saveAndFlush(shoppingList);
            recommendation.setShoppingList(saved);
            return saved;
        } catch (DataIntegrityViolationException ex) {
            return shoppingListRepository.findByRecommendation(recommendation)
                    .orElseThrow(() -> ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ShoppingListResponseDto get(Long id, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        ShoppingListModel shoppingList = findShoppingListByIdOrThrow(id);
        permissionManager.assertCanReadPlot(shoppingList.getRecommendation().getPlot(), user);
        return toDto(shoppingList);
    }

    @Override
    @Transactional
    public ShoppingListResponseDto getByRecommendation(Long recommendationId, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        RecommendationModel recommendation = findRecommendationByIdOrThrow(recommendationId);
        permissionManager.assertCanReadPlot(recommendation.getPlot(), user);
        return shoppingListRepository.findByRecommendation(recommendation)
                .map(this::toDto)
                .orElseGet(() -> toDto(createInitial(recommendation, shoppingListReportService.build(recommendation))));
    }

    @Override
    @Transactional
    public ShoppingListResponseDto update(Long id, ShoppingListPostRequestDto dto, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        ShoppingListModel shoppingList = findShoppingListByIdOrThrow(id);
        permissionManager.assertCanReadPlot(shoppingList.getRecommendation().getPlot(), user);

        if (dto.getNewTechnicalReport() != null && !dto.getNewTechnicalReport().isBlank()) {
            shoppingList.setTechnicalReport(dto.getNewTechnicalReport());
        }

        return toDto(shoppingListRepository.save(shoppingList));
    }

    @Override
    @Transactional
    public void delete(Long id, String username) {
        UserModel user = findUserByUsernameOrEmailOrThrow(username);
        ShoppingListModel shoppingList = findShoppingListByIdOrThrow(id);
        permissionManager.assertCanReadPlot(shoppingList.getRecommendation().getPlot(), user);
        shoppingList.getRecommendation().setShoppingList(null);
        shoppingListRepository.delete(shoppingList);
    }

    private ShoppingListResponseDto toDto(ShoppingListModel model) {
        return ShoppingListResponseDto.builder()
                .id(model.getId())
                .recommendationId(model.getRecommendation().getId())
                .documentName(model.getDocumentName() != null ? model.getDocumentName() : ShoppingListModel.DOCUMENT_NAME)
                .technicalReport(model.getTechnicalReport())
                .content(model.getTechnicalReport())
                .items(structuredDataAssembler.shoppingItems(model.getRecommendation()))
                .technicalObservations(structuredDataAssembler.observations(model.getTechnicalReport()))
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }

    private UserModel findUserByUsernameOrEmailOrThrow(String usernameOrEmail) {
        return userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + usernameOrEmail));
    }

    private RecommendationModel findRecommendationByIdOrThrow(Long id) {
        return recommendationRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new EntityNotFoundException("Recomendação não encontrada com o ID: " + id));
    }

    private ShoppingListModel findShoppingListByIdOrThrow(Long id) {
        return shoppingListRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lista de Compras não encontrada com o ID: " + id));
    }
}
