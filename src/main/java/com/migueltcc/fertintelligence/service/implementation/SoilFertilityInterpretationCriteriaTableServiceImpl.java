package com.migueltcc.fertintelligence.service.implementation;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        StandardEntityAuthorization.assertSupremeUser(creator);

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
        StandardEntityAuthorization.assertCanRead(table.getCreator(), table.isPublicTable(), user);

        return table.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SoilFertilityInterpretationCriteriaTableResponseDto> getAllSoilFertilityInterpretationCriteriaTablesByCreator(
            String username) {

        UserModel creator = findUserByUsernameOrThrow(username);
        List<SoilFertilityInterpretationCriteriaTableModel> tables = soilFertilityInterpretationCriteriaTableRepository.findAllByCreator(creator);

        if (StandardEntityAuthorization.isSupremeUser(creator)) {
            return tables.stream()
                    .map(SoilFertilityInterpretationCriteriaTableModel::toDto)
                    .collect(Collectors.toList());
        }

        List<SoilFertilityInterpretationCriteriaTableModel> standardTables =
                soilFertilityInterpretationCriteriaTableRepository.findAllByPublicTableTrue().stream()
                        .filter(table -> StandardEntityAuthorization.isStandardEntity(table.getCreator(), table.isPublicTable()))
                        .toList();

        return Stream.concat(tables.stream(), standardTables.stream())
                .distinct()
                .map(SoilFertilityInterpretationCriteriaTableModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SoilFertilityInterpretationCriteriaTableResponseDto> getAllPublicSoilFertilityInterpretationCriteriaTables() {
        return soilFertilityInterpretationCriteriaTableRepository.findAllByPublicTableTrue().stream()
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
        StandardEntityAuthorization.assertSupremeUser(user);
        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);

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
        StandardEntityAuthorization.assertSupremeUser(owner);

        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);

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

}
