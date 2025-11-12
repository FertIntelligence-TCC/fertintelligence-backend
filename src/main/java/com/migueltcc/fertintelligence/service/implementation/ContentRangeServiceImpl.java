package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Nutriente;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.tables.contentRange.ContentRangeCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.contentRange.ContentRangePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.contentRange.ContentRangeResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.ContentRangeModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CoverageModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFertilizationTableModel;
import com.migueltcc.fertintelligence.repository.ContentRangeRepository;
import com.migueltcc.fertintelligence.repository.CoverageRepository;
import com.migueltcc.fertintelligence.repository.CropFertilizationTableRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.ContentRangeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ContentRangeServiceImpl implements ContentRangeService {

    @Autowired
    private ContentRangeRepository contentRangeRepository;

    @Autowired
    private CropFertilizationTableRepository cropFertilizationTableRepository;

    @Autowired
    private CoverageRepository coverageRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public ContentRangeResponseDto createContentRange(Long tableId, ContentRangeCreateRequestDto createRequestDto, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        CropFertilizationTableModel table = findTableByIdOrThrow(tableId);
        checkCreatorPermission(table, owner);

        List<ContentRangeModel> existingRanges = contentRangeRepository
                .findAllByTableAndNutrientOrderByOrderAsc(table, createRequestDto.getNutrient());

        List<ContentRangeModel> updatedRanges = new ArrayList<>(existingRanges);

        ContentRangeModel newRange = ContentRangeModel.builder()
                .table(table)
                .nutrient(createRequestDto.getNutrient())
                .order(createRequestDto.getOrder())
                .smallest(createRequestDto.getSmallest())
                .largest(createRequestDto.getLargest())
                .application(createRequestDto.getApplication())
                .build();

        updatedRanges.add(newRange);
        validateNutrientRanges(createRequestDto.getNutrient(), updatedRanges);

        ContentRangeModel savedRange = contentRangeRepository.save(newRange);
        return savedRange.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public ContentRangeResponseDto getContentRangeById(Long contentRangeId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        ContentRangeModel range = findContentRangeByIdOrThrow(contentRangeId);
        checkCreatorPermission(range.getTable(), owner);

        return range.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContentRangeResponseDto> getAllContentRangesByTable(Long tableId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        CropFertilizationTableModel table = findTableByIdOrThrow(tableId);
        checkCreatorPermission(table, owner);

        return contentRangeRepository.findAllByTableOrderByNutrientAscOrderAsc(table).stream()
                .map(ContentRangeModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ContentRangeResponseDto updateContentRange(Long contentRangeId, ContentRangePostRequestDto updateRequestDto, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        ContentRangeModel range = findContentRangeByIdOrThrow(contentRangeId);
        CropFertilizationTableModel table = range.getTable();
        checkCreatorPermission(table, owner);

        Nutriente originalNutrient = range.getNutrient();
        Nutriente updatedNutrient = updateRequestDto.getNutrient() != null
                ? updateRequestDto.getNutrient()
                : originalNutrient;

        Integer updatedOrder = updateRequestDto.getOrder() != null
                ? updateRequestDto.getOrder()
                : range.getOrder();

        Double updatedSmallest = updateRequestDto.getSmallest() != null
                ? updateRequestDto.getSmallest()
                : range.getSmallest();

        Double updatedLargest = updateRequestDto.getLargest() != null
                ? updateRequestDto.getLargest()
                : range.getLargest();

        Double updatedApplication = updateRequestDto.getApplication() != null
                ? updateRequestDto.getApplication()
                : range.getApplication();

        ContentRangeModel updatedRange = ContentRangeModel.builder()
                .id(range.getId())
                .table(table)
                .nutrient(updatedNutrient)
                .order(updatedOrder)
                .smallest(updatedSmallest)
                .largest(updatedLargest)
                .application(updatedApplication)
                .build();

        if (originalNutrient == updatedNutrient) {
            List<ContentRangeModel> ranges = contentRangeRepository
                    .findAllByTableAndNutrientOrderByOrderAsc(table, updatedNutrient);

            List<ContentRangeModel> adjustedRanges = ranges.stream()
                    .map(existing -> Objects.equals(existing.getId(), range.getId()) ? updatedRange : existing)
                    .collect(Collectors.toList());

            validateNutrientRanges(updatedNutrient, adjustedRanges);
        } else {
            if (originalNutrient == Nutriente.NITROGENIO) {
                throw new IllegalArgumentException("Não é permitido alterar o nutriente do intervalo de Nitrogênio.");
            }

            List<ContentRangeModel> originalRanges = contentRangeRepository
                    .findAllByTableAndNutrientOrderByOrderAsc(table, originalNutrient);

            List<ContentRangeModel> remainingOriginal = originalRanges.stream()
                    .filter(existing -> !Objects.equals(existing.getId(), range.getId()))
                    .collect(Collectors.toList());

            if (remainingOriginal.isEmpty()) {
                throw new IllegalArgumentException("A tabela deve manter ao menos um intervalo para o nutriente " + originalNutrient + ".");
            }

            validateNutrientRanges(originalNutrient, remainingOriginal);

            List<ContentRangeModel> newRanges = contentRangeRepository
                    .findAllByTableAndNutrientOrderByOrderAsc(table, updatedNutrient);

            List<ContentRangeModel> adjustedNewRanges = new ArrayList<>(newRanges);
            adjustedNewRanges.add(updatedRange);
            validateNutrientRanges(updatedNutrient, adjustedNewRanges);
        }

        range.setNutrient(updatedNutrient);
        range.setOrder(updatedOrder);
        range.setSmallest(updatedSmallest);
        range.setLargest(updatedLargest);
        range.setApplication(updatedApplication);

        ContentRangeModel savedRange = contentRangeRepository.save(range);
        return savedRange.toDto();
    }

    @Override
    @Transactional
    public void deleteContentRange(Long contentRangeId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        ContentRangeModel range = findContentRangeByIdOrThrow(contentRangeId);
        CropFertilizationTableModel table = range.getTable();
        checkCreatorPermission(table, owner);

        if (range.getNutrient() == Nutriente.NITROGENIO) {
            throw new IllegalArgumentException("Não é permitido remover o intervalo do nutriente Nitrogênio.");
        }

        List<ContentRangeModel> ranges = contentRangeRepository
                .findAllByTableAndNutrientOrderByOrderAsc(table, range.getNutrient());

        List<ContentRangeModel> remaining = ranges.stream()
                .filter(existing -> !Objects.equals(existing.getId(), range.getId()))
                .collect(Collectors.toList());

        if (remaining.isEmpty()) {
            throw new IllegalArgumentException("A tabela deve manter ao menos um intervalo para o nutriente " + range.getNutrient() + ".");
        }

        validateNutrientRanges(range.getNutrient(), remaining);

        List<CoverageModel> coverages = coverageRepository.findAllByRangeOrderByOrderAsc(range);
        if (!coverages.isEmpty()) {
            coverageRepository.deleteAll(coverages);
        }

        contentRangeRepository.delete(range);
    }

    private void validateNutrientRanges(Nutriente nutrient, List<ContentRangeModel> ranges) {
        if (nutrient == Nutriente.NITROGENIO) {
            if (ranges.size() != 1) {
                throw new IllegalArgumentException("A tabela deve possuir exatamente um intervalo para Nitrogênio.");
            }

            ContentRangeModel range = ranges.get(0);
            if (range.getOrder() == null || range.getOrder() != 1) {
                throw new IllegalArgumentException("O intervalo de Nitrogênio deve possuir ordem igual a 1.");
            }

            if (range.getSmallest() != null || range.getLargest() != null) {
                throw new IllegalArgumentException("Os teores mínimo e máximo do intervalo de Nitrogênio devem ser nulos.");
            }

            return;
        }

        if (ranges.isEmpty()) {
            throw new IllegalArgumentException("A tabela deve possuir ao menos um intervalo para o nutriente " + nutrient + ".");
        }

        if (ranges.size() > 5) {
            throw new IllegalArgumentException("Cada nutriente deve possuir no máximo cinco intervalos de teor.");
        }

        List<ContentRangeModel> sortedRanges = ranges.stream()
                .sorted(Comparator.comparing(ContentRangeModel::getOrder))
                .collect(Collectors.toList());

        for (int i = 0; i < sortedRanges.size(); i++) {
            ContentRangeModel current = sortedRanges.get(i);
            Integer currentOrder = current.getOrder();

            if (currentOrder == null || currentOrder <= 0) {
                throw new IllegalArgumentException("A ordem dos intervalos deve ser um valor positivo.");
            }

            if (!currentOrder.equals(i + 1)) {
                throw new IllegalArgumentException("Os intervalos devem possuir ordens sequenciais iniciando em 1.");
            }

            boolean isFirst = i == 0;
            boolean isLast = i == sortedRanges.size() - 1;

            if (current.getSmallest() == null && !isFirst) {
                throw new IllegalArgumentException("A partir do segundo intervalo o teor mínimo não pode ser nulo.");
            }

            if (current.getLargest() == null) {
                if (!isLast) {
                    throw new IllegalArgumentException("Somente o último intervalo pode possuir teor máximo nulo.");
                }
            } else {
                if (isLast && sortedRanges.size() > 1) {
                    throw new IllegalArgumentException("O último intervalo deve possuir teor máximo nulo.");
                }
            }

            if (isFirst && current.getSmallest() != null) {
                throw new IllegalArgumentException("O primeiro intervalo deve possuir teor mínimo nulo.");
            }

            if (isFirst && current.getLargest() == null) {
                throw new IllegalArgumentException("O primeiro intervalo deve possuir teor máximo definido.");
            }

            if (current.getSmallest() == null && current.getLargest() == null) {
                throw new IllegalArgumentException("Ao menos um dos teores deve ser informado para o intervalo.");
            }

            if (!isFirst) {
                ContentRangeModel previous = sortedRanges.get(i - 1);
                if (!Objects.equals(previous.getLargest(), current.getSmallest())) {
                    throw new IllegalArgumentException("Os limites dos intervalos devem ser contínuos.");
                }
            }
        }
    }

    private void checkUserIsProprietario(UserModel user) {
        if (user.getCargo() != Cargo.PROPRIETARIO) {
            throw new AccessDeniedException("Acesso negado. Apenas usuários com o cargo 'PROPRIETARIO' podem gerenciar intervalos de teor.");
        }
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private CropFertilizationTableModel findTableByIdOrThrow(Long tableId) {
        return cropFertilizationTableRepository.findById(tableId)
                .orElseThrow(() -> new EntityNotFoundException("Tabela de adubação não encontrada com o ID: " + tableId));
    }

    private ContentRangeModel findContentRangeByIdOrThrow(Long contentRangeId) {
        return contentRangeRepository.findById(contentRangeId)
                .orElseThrow(() -> new EntityNotFoundException("Intervalo de teor não encontrado com o ID: " + contentRangeId));
    }

    private void checkCreatorPermission(CropFertilizationTableModel table, UserModel requestingUser) {
        if (!Objects.equals(table.getCreator().getId(), requestingUser.getId())) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar os intervalos desta tabela.");
        }
    }
}