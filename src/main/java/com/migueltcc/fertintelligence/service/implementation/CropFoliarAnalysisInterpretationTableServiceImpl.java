package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.recommendation.TechnicalTableGroup;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.table.CropFoliarAnalysisInterpretationTableCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.table.CropFoliarAnalysisInterpretationTablePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.table.CropFoliarAnalysisInterpretationTableResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableModel;
import com.migueltcc.fertintelligence.repository.CropFoliarAnalysisInterpretationTableLineRepository;
import com.migueltcc.fertintelligence.repository.CropFoliarAnalysisInterpretationTableRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.CropFoliarAnalysisInterpretationTableService;
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
public class CropFoliarAnalysisInterpretationTableServiceImpl
        implements CropFoliarAnalysisInterpretationTableService {

    @Autowired
    private CropFoliarAnalysisInterpretationTableRepository tableRepository;

    @Autowired
    private CropFoliarAnalysisInterpretationTableLineRepository tableLineRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public CropFoliarAnalysisInterpretationTableResponseDto createCropFoliarAnalysisInterpretationTable(
            CropFoliarAnalysisInterpretationTableCreateRequestDto createRequestDto,
            String username) {

        UserModel owner = findUserByUsernameOrThrow(username);

        CropFoliarAnalysisInterpretationTableModel table = CropFoliarAnalysisInterpretationTableModel.builder()
                .creator(owner)
                .region(createRequestDto.getRegion())
                .name(createRequestDto.getName())
                .observations(createRequestDto.getObservations())
                .sources(createRequestDto.getSources())
                .publicTable(Boolean.TRUE.equals(createRequestDto.getPublicTable()))
                .build();

        CropFoliarAnalysisInterpretationTableModel savedTable = tableRepository.save(table);
        return savedTable.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public CropFoliarAnalysisInterpretationTableResponseDto getCropFoliarAnalysisInterpretationTableById(
            Long tableId,
            String username) {

        UserModel owner = findUserByUsernameOrThrow(username);

        CropFoliarAnalysisInterpretationTableModel table = findTableByIdOrThrow(tableId);
        checkViewPermission(table, owner);

        return table.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropFoliarAnalysisInterpretationTableResponseDto>
    getAllCropFoliarAnalysisInterpretationTablesByCreator(String username) {
        return getAllCropFoliarAnalysisInterpretationTablesByCreator(username, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropFoliarAnalysisInterpretationTableResponseDto>
    getAllCropFoliarAnalysisInterpretationTablesByCreator(String username, TechnicalTableGroup group) {

        UserModel owner = findUserByUsernameOrThrow(username);

        List<CropFoliarAnalysisInterpretationTableModel> tables = group != null
                ? findTablesByGroup(owner, group)
                : isSupremeUser(owner)
                ? tableRepository.findAllByCreator_Cargo(Cargo.USUARIO_SUPREMO)
                : mergeTables(
                        tableRepository.findAllByCreator(owner),
                        tableRepository.findAllByCreator_Cargo(Cargo.USUARIO_SUPREMO),
                        tableRepository.findAllByPublicTableTrue()
                );

        return tables.stream()
                .map(CropFoliarAnalysisInterpretationTableModel::toDto)
                .collect(Collectors.toList());
    }

    private List<CropFoliarAnalysisInterpretationTableModel> findTablesByGroup(UserModel owner, TechnicalTableGroup group) {
        return switch (group) {
            case PRIVADAS -> tableRepository.findAllByCreator(owner).stream()
                    .filter(t -> !t.isPublicTable())
                    .filter(t -> t.getCreator() != null
                            && !Cargo.USUARIO_SUPREMO.equals(t.getCreator().getCargo()))
                    .toList();
            case PUBLICAS -> tableRepository.findAllByPublicTableTrueAndCreator_CargoNot(Cargo.USUARIO_SUPREMO);
            case PADRAO -> tableRepository.findAllByCreator_Cargo(Cargo.USUARIO_SUPREMO);
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropFoliarAnalysisInterpretationTableResponseDto> getAllPublicCropFoliarAnalysisInterpretationTables() {
        return tableRepository.findAllByPublicTableTrueAndCreator_CargoNot(Cargo.USUARIO_SUPREMO).stream()
                .map(CropFoliarAnalysisInterpretationTableModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropFoliarAnalysisInterpretationTableResponseDto> getAllDefaultCropFoliarAnalysisInterpretationTables(
            String username) {
        findUserByUsernameOrThrow(username);
        return tableRepository.findAllByCreator_Cargo(Cargo.USUARIO_SUPREMO)
                .stream()
                .map(CropFoliarAnalysisInterpretationTableModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CropFoliarAnalysisInterpretationTableResponseDto updateCropFoliarAnalysisInterpretationTable(
            Long tableId,
            CropFoliarAnalysisInterpretationTablePostRequestDto updateRequestDto,
            String username) {

        UserModel owner = findUserByUsernameOrThrow(username);

        CropFoliarAnalysisInterpretationTableModel table = findTableByIdOrThrow(tableId);
        checkModifyPermission(table, owner);

        if (updateRequestDto.getRegion() != null) {
            table.setRegion(updateRequestDto.getRegion());
        }
        if (updateRequestDto.getName() != null && !updateRequestDto.getName().isEmpty()) {
            table.setName(updateRequestDto.getName());
        }
        if (updateRequestDto.getObservations() != null) {
            table.setObservations(updateRequestDto.getObservations());
        }
        if (updateRequestDto.getSources() != null) {
            table.setSources(updateRequestDto.getSources());
        }
        if (updateRequestDto.getPublicTable() != null) {
            table.setPublicTable(updateRequestDto.getPublicTable());
        }

        CropFoliarAnalysisInterpretationTableModel updatedTable = tableRepository.save(table);
        return updatedTable.toDto();
    }

    @Override
    @Transactional
    public void deleteCropFoliarAnalysisInterpretationTable(Long tableId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        CropFoliarAnalysisInterpretationTableModel table = findTableByIdOrThrow(tableId);
        checkModifyPermission(table, owner);

        tableLineRepository.deleteAllByTable(table);
        tableRepository.delete(table);
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private CropFoliarAnalysisInterpretationTableModel findTableByIdOrThrow(Long tableId) {
        return tableRepository.findById(tableId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tabela de interpretação de análise foliar não encontrada com o ID: " + tableId));
    }

    private void checkViewPermission(CropFoliarAnalysisInterpretationTableModel table, UserModel requestingUser) {
        if (isCreator(table, requestingUser) || table.isPublicTable() || isDefaultTable(table)) {
            return;
        }
        throw new AccessDeniedException("Você não tem permissão para acessar esta tabela.");
    }

    private void checkModifyPermission(CropFoliarAnalysisInterpretationTableModel table, UserModel requestingUser) {
        if (isDefaultTable(table)) {
            if (isSupremeUser(requestingUser)) {
                return;
            }
            throw new AccessDeniedException("Apenas o usuário supremo pode modificar tabelas padrão.");
        }
        checkCreatorPermission(table, requestingUser);
    }

    private void checkCreatorPermission(CropFoliarAnalysisInterpretationTableModel table, UserModel requestingUser) {
        if (!Objects.equals(table.getCreator().getId(), requestingUser.getId())) {
            throw new AccessDeniedException("Você não tem permissão para acessar ou modificar esta tabela.");
        }
    }

    private boolean isCreator(CropFoliarAnalysisInterpretationTableModel table, UserModel requestingUser) {
        return table.getCreator() != null
                && requestingUser != null
                && Objects.equals(table.getCreator().getId(), requestingUser.getId());
    }

    private boolean isDefaultTable(CropFoliarAnalysisInterpretationTableModel table) {
        return table.getCreator() != null && table.getCreator().getCargo() == Cargo.USUARIO_SUPREMO;
    }

    private boolean isSupremeUser(UserModel user) {
        return user != null && user.getCargo() == Cargo.USUARIO_SUPREMO;
    }

    @SafeVarargs
    private List<CropFoliarAnalysisInterpretationTableModel> mergeTables(
            List<CropFoliarAnalysisInterpretationTableModel>... tableLists) {
        Map<Long, CropFoliarAnalysisInterpretationTableModel> byId = Stream.of(tableLists)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.toMap(CropFoliarAnalysisInterpretationTableModel::getId, Function.identity(), (first, ignored) -> first, LinkedHashMap::new));
        return List.copyOf(byId.values());
    }
}
