package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.recommendation.TechnicalTableGroup;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.table.SoilFertilityInterpretationCriteriaTableCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.table.SoilFertilityInterpretationCriteriaTablePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.table.SoilFertilityInterpretationCriteriaTableResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.repository.SoilFertilityInterpretationCriteriaTableRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.SoilFertilityInterpretationCriteriaTableService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SoilFertilityInterpretationCriteriaTableServiceImpl implements SoilFertilityInterpretationCriteriaTableService {

    @Autowired
    private SoilFertilityInterpretationCriteriaTableRepository soilFertilityInterpretationCriteriaTableRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public SoilFertilityInterpretationCriteriaTableResponseDto createSoilFertilityInterpretationCriteriaTable(
            SoilFertilityInterpretationCriteriaTableCreateRequestDto createRequestDto,
            String username) {

        UserModel creator = findUserByUsernameOrThrow(username);

        SoilFertilityInterpretationCriteriaTableModel table = SoilFertilityInterpretationCriteriaTableModel.builder()
                .creator(creator)
                .name(createRequestDto.getName())
                .description(createRequestDto.getDescription())
                .region(createRequestDto.getRegion())
                .observations(createRequestDto.getObservations())
                .sources(createRequestDto.getSources())
                .publicTable(Boolean.TRUE.equals(createRequestDto.getPublic_table()))
                .build();

        SoilFertilityInterpretationCriteriaTableModel savedTable = soilFertilityInterpretationCriteriaTableRepository.save(table);
        return savedTable.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public SoilFertilityInterpretationCriteriaTableResponseDto getSoilFertilityInterpretationCriteriaTableById(
            Long tableId,
            String username) {

        UserModel user = findUserByUsernameOrThrow(username);
        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);
        checkViewPermission(table, user);

        return table.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SoilFertilityInterpretationCriteriaTableResponseDto> getAllSoilFertilityInterpretationCriteriaTablesByCreator(
            String username) {
        return getAllSoilFertilityInterpretationCriteriaTablesByCreator(username, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SoilFertilityInterpretationCriteriaTableResponseDto> getAllSoilFertilityInterpretationCriteriaTablesByCreator(
            String username,
            TechnicalTableGroup group) {

        UserModel creator = findUserByUsernameOrThrow(username);
        List<SoilFertilityInterpretationCriteriaTableModel> tables = group != null
                ? findTablesByGroup(creator, group)
                : isSupremeUser(creator)
                ? soilFertilityInterpretationCriteriaTableRepository.findAllByCreator_Cargo(Cargo.USUARIO_SUPREMO)
                : mergeTables(
                        soilFertilityInterpretationCriteriaTableRepository.findAllByCreator(creator),
                        soilFertilityInterpretationCriteriaTableRepository.findAllByCreator_CargoAndPublicTableTrue(Cargo.USUARIO_SUPREMO),
                        soilFertilityInterpretationCriteriaTableRepository.findAllByPublicTableTrue()
                );

        return tables.stream()
                .map(SoilFertilityInterpretationCriteriaTableModel::toDto)
                .collect(Collectors.toList());
    }

    private List<SoilFertilityInterpretationCriteriaTableModel> findTablesByGroup(UserModel creator, TechnicalTableGroup group) {
        return switch (group) {
            case MINHAS -> soilFertilityInterpretationCriteriaTableRepository.findAllByCreator(creator);
            case PRIVADAS -> soilFertilityInterpretationCriteriaTableRepository.findAllByCreator(creator).stream()
                    .filter(t -> !t.isPublicTable())
                    .filter(t -> t.getCreator() != null && !Cargo.USUARIO_SUPREMO.equals(t.getCreator().getCargo()))
                    .collect(Collectors.toList());
            case PUBLICAS -> soilFertilityInterpretationCriteriaTableRepository.findAllByPublicTableTrueAndCreator_CargoNot(Cargo.USUARIO_SUPREMO);
            case PADRAO -> soilFertilityInterpretationCriteriaTableRepository.findAllByCreator_CargoAndPublicTableTrue(Cargo.USUARIO_SUPREMO);
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<SoilFertilityInterpretationCriteriaTableResponseDto> getAllPublicSoilFertilityInterpretationCriteriaTables() {
        return soilFertilityInterpretationCriteriaTableRepository.findAllByPublicTableTrueAndCreator_CargoNot(Cargo.USUARIO_SUPREMO).stream()
                .map(SoilFertilityInterpretationCriteriaTableModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SoilFertilityInterpretationCriteriaTableResponseDto> getAllDefaultSoilFertilityInterpretationCriteriaTables(
            String username) {
        findUserByUsernameOrThrow(username);
        return soilFertilityInterpretationCriteriaTableRepository.findAllByCreator_CargoAndPublicTableTrue(Cargo.USUARIO_SUPREMO)
                .stream()
                .map(SoilFertilityInterpretationCriteriaTableModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SoilFertilityInterpretationCriteriaTableResponseDto updateSoilFertilityInterpretationCriteriaTable(
            Long tableId,
            SoilFertilityInterpretationCriteriaTablePostRequestDto updateRequestDto,
            String username) {

        UserModel user = findUserByUsernameOrThrow(username);
        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);
        checkModifyPermission(table, user);

        if (updateRequestDto.getName() != null && !updateRequestDto.getName().isEmpty()) {
            table.setName(updateRequestDto.getName());
        }
        if (updateRequestDto.getDescription() != null) {
            table.setDescription(updateRequestDto.getDescription());
        }
        if (updateRequestDto.getRegion() != null) {
            table.setRegion(updateRequestDto.getRegion());
        }
        if (updateRequestDto.getObservations() != null) {
            table.setObservations(updateRequestDto.getObservations());
        }
        if (updateRequestDto.getSources() != null) {
            table.setSources(updateRequestDto.getSources());
        }
        if (updateRequestDto.getPublic_table() != null) {
            table.setPublicTable(updateRequestDto.getPublic_table());
        }

        SoilFertilityInterpretationCriteriaTableModel updatedTable = soilFertilityInterpretationCriteriaTableRepository.save(table);
        return updatedTable.toDto();
    }

    @Override
    @Transactional
    public void deleteSoilFertilityInterpretationCriteriaTable(Long tableId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);
        checkModifyPermission(table, owner);

        soilFertilityInterpretationCriteriaTableRepository.delete(table);
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private SoilFertilityInterpretationCriteriaTableModel findTableByIdOrThrow(Long tableId) {
        return soilFertilityInterpretationCriteriaTableRepository.findById(tableId)
                .orElseThrow(() -> new EntityNotFoundException("Tabela de critérios de interpretação da fertilidade do solo não encontrada com o ID: " + tableId));
    }

    private void checkViewPermission(SoilFertilityInterpretationCriteriaTableModel table, UserModel requestingUser) {
        if (isCreator(table, requestingUser) || table.isPublicTable() || isDefaultTable(table)) {
            return;
        }
        throw new AccessDeniedException("Você não tem permissão para acessar esta tabela.");
    }

    private void checkModifyPermission(SoilFertilityInterpretationCriteriaTableModel table, UserModel requestingUser) {
        if (isDefaultTable(table)) {
            if (isSupremeUser(requestingUser)) {
                return;
            }
            throw new AccessDeniedException("Apenas o usuário supremo pode modificar tabelas padrão.");
        }
        checkCreatorPermission(table, requestingUser);
    }

    private void checkCreatorPermission(SoilFertilityInterpretationCriteriaTableModel table, UserModel requestingUser) {
        if (!Objects.equals(table.getCreator().getId(), requestingUser.getId())) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar esta tabela.");
        }
    }

    private boolean isCreator(SoilFertilityInterpretationCriteriaTableModel table, UserModel requestingUser) {
        return table.getCreator() != null
                && requestingUser != null
                && Objects.equals(table.getCreator().getId(), requestingUser.getId());
    }

    private boolean isDefaultTable(SoilFertilityInterpretationCriteriaTableModel table) {
        return table.getCreator() != null && table.getCreator().getCargo() == Cargo.USUARIO_SUPREMO && table.isPublicTable();
    }

    private boolean isSupremeUser(UserModel user) {
        return user != null && user.getCargo() == Cargo.USUARIO_SUPREMO;
    }

    @SafeVarargs
    private List<SoilFertilityInterpretationCriteriaTableModel> mergeTables(
            List<SoilFertilityInterpretationCriteriaTableModel>... tableLists) {
        Map<Long, SoilFertilityInterpretationCriteriaTableModel> byId = Stream.of(tableLists)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.toMap(SoilFertilityInterpretationCriteriaTableModel::getId, Function.identity(), (first, ignored) -> first, LinkedHashMap::new));
        return List.copyOf(byId.values());
    }
}
