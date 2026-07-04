package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.phosphorusClayPhosphateDose.PhosphorusClayPhosphateDoseCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.phosphorusClayPhosphateDose.PhosphorusClayPhosphateDosePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.phosphorusClayPhosphateDose.PhosphorusClayPhosphateDoseResponseDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.phosphorusClayPhosphateDose.PhosphorusClayPhosphateDoseSectionDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.PhosphorusClayPhosphateDoseModel;
import com.migueltcc.fertintelligence.repository.PhosphorusClayPhosphateDoseRepository;
import com.migueltcc.fertintelligence.repository.SoilFertilityInterpretationCriteriaTableRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.PhosphorusClayPhosphateDoseService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class PhosphorusClayPhosphateDoseServiceImpl implements PhosphorusClayPhosphateDoseService {

    @Autowired
    private PhosphorusClayPhosphateDoseRepository phosphorusClayPhosphateDoseRepository;

    @Autowired
    private SoilFertilityInterpretationCriteriaTableRepository soilFertilityInterpretationCriteriaTableRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public PhosphorusClayPhosphateDoseResponseDto createPhosphorusClayPhosphateDose(
            Long tableId,
            PhosphorusClayPhosphateDoseCreateRequestDto createRequestDto,
            String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);
        checkModifyPermission(table, owner);

        phosphorusClayPhosphateDoseRepository.findByTable(table).ifPresent(existing -> {
            throw new IllegalStateException("Já existe uma tabela de teores de fósforo e argila, e doses de fosfato cadastrada para esta tabela.");
        });

        validateCreateRequest(createRequestDto);

        PhosphorusClayPhosphateDoseModel criterion = PhosphorusClayPhosphateDoseModel.builder()
                .table(table)
                .observations(createRequestDto.getObservations())
                .sources(createRequestDto.getSources())
                .build();
        copyDrylandSection(createRequestDto.getDrylandSection(), criterion, false);
        copyIrrigatedSection(createRequestDto.getIrrigatedSection(), criterion, false);

        return phosphorusClayPhosphateDoseRepository.save(criterion).toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public PhosphorusClayPhosphateDoseResponseDto getPhosphorusClayPhosphateDoseById(Long criterionId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        PhosphorusClayPhosphateDoseModel criterion = findCriterionByIdOrThrow(criterionId);
        checkViewPermission(criterion.getTable(), owner);

        return criterion.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public PhosphorusClayPhosphateDoseResponseDto getPhosphorusClayPhosphateDoseByTable(Long tableId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);
        checkViewPermission(table, owner);

        PhosphorusClayPhosphateDoseModel criterion = phosphorusClayPhosphateDoseRepository.findByTable(table)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tabela de teores de fósforo e argila, e doses de fosfato não encontrada para a tabela: " + tableId));

        return criterion.toDto();
    }

    @Override
    @Transactional
    public PhosphorusClayPhosphateDoseResponseDto updatePhosphorusClayPhosphateDose(
            Long criterionId,
            PhosphorusClayPhosphateDosePostRequestDto updateRequestDto,
            String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        PhosphorusClayPhosphateDoseModel criterion = findCriterionByIdOrThrow(criterionId);
        checkModifyPermission(criterion.getTable(), owner);

        if (updateRequestDto.getDrylandSection() != null) {
            copyDrylandSection(updateRequestDto.getDrylandSection(), criterion, true);
        }
        if (updateRequestDto.getIrrigatedSection() != null) {
            copyIrrigatedSection(updateRequestDto.getIrrigatedSection(), criterion, true);
        }
        if (updateRequestDto.getObservations() != null) {
            criterion.setObservations(updateRequestDto.getObservations());
        }
        if (updateRequestDto.getSources() != null) {
            criterion.setSources(updateRequestDto.getSources());
        }

        return phosphorusClayPhosphateDoseRepository.save(criterion).toDto();
    }

    @Override
    @Transactional
    public void deletePhosphorusClayPhosphateDose(Long criterionId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        PhosphorusClayPhosphateDoseModel criterion = findCriterionByIdOrThrow(criterionId);
        checkModifyPermission(criterion.getTable(), owner);

        phosphorusClayPhosphateDoseRepository.delete(criterion);
    }

    private void validateCreateRequest(PhosphorusClayPhosphateDoseCreateRequestDto createRequestDto) {
        if (createRequestDto == null) {
            throw new IllegalArgumentException("Dados da tabela de teores de fósforo e argila, e doses de fosfato são obrigatórios.");
        }
        validateSection(createRequestDto.getDrylandSection(), "Sistemas de sequeiro");
        validateSection(createRequestDto.getIrrigatedSection(), "Sistemas irrigados");
    }

    private void validateSection(PhosphorusClayPhosphateDoseSectionDto section, String sectionName) {
        if (section == null
                || section.getLowerClayContent() == null
                || section.getLowerClayVeryLowPDose() == null
                || section.getLowerClayLowPDose() == null
                || section.getLowerClayMediumPDose() == null
                || section.getInterval1LowerClayContent() == null
                || section.getInterval1HigherClayContent() == null
                || section.getInterval1VeryLowPDose() == null
                || section.getInterval1LowPDose() == null
                || section.getInterval1MediumPDose() == null
                || section.getInterval2LowerClayContent() == null
                || section.getInterval2HigherClayContent() == null
                || section.getInterval2VeryLowPDose() == null
                || section.getInterval2LowPDose() == null
                || section.getInterval2MediumPDose() == null
                || section.getHigherClayContent() == null
                || section.getHigherClayVeryLowPDose() == null
                || section.getHigherClayLowPDose() == null
                || section.getHigherClayMediumPDose() == null) {
            throw new IllegalArgumentException("Todos os campos numéricos da seção \"" + sectionName + "\" são obrigatórios.");
        }
    }

    private void copyDrylandSection(PhosphorusClayPhosphateDoseSectionDto section, PhosphorusClayPhosphateDoseModel criterion, boolean ignoreNulls) {
        if (!ignoreNulls || section.getLowerClayContent() != null) criterion.setDrylandLowerClayContent(section.getLowerClayContent());
        if (!ignoreNulls || section.getLowerClayVeryLowPDose() != null) criterion.setDrylandLowerClayVeryLowPDose(section.getLowerClayVeryLowPDose());
        if (!ignoreNulls || section.getLowerClayLowPDose() != null) criterion.setDrylandLowerClayLowPDose(section.getLowerClayLowPDose());
        if (!ignoreNulls || section.getLowerClayMediumPDose() != null) criterion.setDrylandLowerClayMediumPDose(section.getLowerClayMediumPDose());
        if (!ignoreNulls || section.getInterval1LowerClayContent() != null) criterion.setDrylandInterval1LowerClayContent(section.getInterval1LowerClayContent());
        if (!ignoreNulls || section.getInterval1HigherClayContent() != null) criterion.setDrylandInterval1HigherClayContent(section.getInterval1HigherClayContent());
        if (!ignoreNulls || section.getInterval1VeryLowPDose() != null) criterion.setDrylandInterval1VeryLowPDose(section.getInterval1VeryLowPDose());
        if (!ignoreNulls || section.getInterval1LowPDose() != null) criterion.setDrylandInterval1LowPDose(section.getInterval1LowPDose());
        if (!ignoreNulls || section.getInterval1MediumPDose() != null) criterion.setDrylandInterval1MediumPDose(section.getInterval1MediumPDose());
        if (!ignoreNulls || section.getInterval2LowerClayContent() != null) criterion.setDrylandInterval2LowerClayContent(section.getInterval2LowerClayContent());
        if (!ignoreNulls || section.getInterval2HigherClayContent() != null) criterion.setDrylandInterval2HigherClayContent(section.getInterval2HigherClayContent());
        if (!ignoreNulls || section.getInterval2VeryLowPDose() != null) criterion.setDrylandInterval2VeryLowPDose(section.getInterval2VeryLowPDose());
        if (!ignoreNulls || section.getInterval2LowPDose() != null) criterion.setDrylandInterval2LowPDose(section.getInterval2LowPDose());
        if (!ignoreNulls || section.getInterval2MediumPDose() != null) criterion.setDrylandInterval2MediumPDose(section.getInterval2MediumPDose());
        if (!ignoreNulls || section.getHigherClayContent() != null) criterion.setDrylandHigherClayContent(section.getHigherClayContent());
        if (!ignoreNulls || section.getHigherClayVeryLowPDose() != null) criterion.setDrylandHigherClayVeryLowPDose(section.getHigherClayVeryLowPDose());
        if (!ignoreNulls || section.getHigherClayLowPDose() != null) criterion.setDrylandHigherClayLowPDose(section.getHigherClayLowPDose());
        if (!ignoreNulls || section.getHigherClayMediumPDose() != null) criterion.setDrylandHigherClayMediumPDose(section.getHigherClayMediumPDose());
    }

    private void copyIrrigatedSection(PhosphorusClayPhosphateDoseSectionDto section, PhosphorusClayPhosphateDoseModel criterion, boolean ignoreNulls) {
        if (!ignoreNulls || section.getLowerClayContent() != null) criterion.setIrrigatedLowerClayContent(section.getLowerClayContent());
        if (!ignoreNulls || section.getLowerClayVeryLowPDose() != null) criterion.setIrrigatedLowerClayVeryLowPDose(section.getLowerClayVeryLowPDose());
        if (!ignoreNulls || section.getLowerClayLowPDose() != null) criterion.setIrrigatedLowerClayLowPDose(section.getLowerClayLowPDose());
        if (!ignoreNulls || section.getLowerClayMediumPDose() != null) criterion.setIrrigatedLowerClayMediumPDose(section.getLowerClayMediumPDose());
        if (!ignoreNulls || section.getInterval1LowerClayContent() != null) criterion.setIrrigatedInterval1LowerClayContent(section.getInterval1LowerClayContent());
        if (!ignoreNulls || section.getInterval1HigherClayContent() != null) criterion.setIrrigatedInterval1HigherClayContent(section.getInterval1HigherClayContent());
        if (!ignoreNulls || section.getInterval1VeryLowPDose() != null) criterion.setIrrigatedInterval1VeryLowPDose(section.getInterval1VeryLowPDose());
        if (!ignoreNulls || section.getInterval1LowPDose() != null) criterion.setIrrigatedInterval1LowPDose(section.getInterval1LowPDose());
        if (!ignoreNulls || section.getInterval1MediumPDose() != null) criterion.setIrrigatedInterval1MediumPDose(section.getInterval1MediumPDose());
        if (!ignoreNulls || section.getInterval2LowerClayContent() != null) criterion.setIrrigatedInterval2LowerClayContent(section.getInterval2LowerClayContent());
        if (!ignoreNulls || section.getInterval2HigherClayContent() != null) criterion.setIrrigatedInterval2HigherClayContent(section.getInterval2HigherClayContent());
        if (!ignoreNulls || section.getInterval2VeryLowPDose() != null) criterion.setIrrigatedInterval2VeryLowPDose(section.getInterval2VeryLowPDose());
        if (!ignoreNulls || section.getInterval2LowPDose() != null) criterion.setIrrigatedInterval2LowPDose(section.getInterval2LowPDose());
        if (!ignoreNulls || section.getInterval2MediumPDose() != null) criterion.setIrrigatedInterval2MediumPDose(section.getInterval2MediumPDose());
        if (!ignoreNulls || section.getHigherClayContent() != null) criterion.setIrrigatedHigherClayContent(section.getHigherClayContent());
        if (!ignoreNulls || section.getHigherClayVeryLowPDose() != null) criterion.setIrrigatedHigherClayVeryLowPDose(section.getHigherClayVeryLowPDose());
        if (!ignoreNulls || section.getHigherClayLowPDose() != null) criterion.setIrrigatedHigherClayLowPDose(section.getHigherClayLowPDose());
        if (!ignoreNulls || section.getHigherClayMediumPDose() != null) criterion.setIrrigatedHigherClayMediumPDose(section.getHigherClayMediumPDose());
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private SoilFertilityInterpretationCriteriaTableModel findTableByIdOrThrow(Long tableId) {
        return soilFertilityInterpretationCriteriaTableRepository.findById(tableId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tabela de critérios de fertilidade do solo não encontrada com o ID: " + tableId));
    }

    private PhosphorusClayPhosphateDoseModel findCriterionByIdOrThrow(Long criterionId) {
        return phosphorusClayPhosphateDoseRepository.findById(criterionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tabela de teores de fósforo e argila, e doses de fosfato não encontrada com o ID: " + criterionId));
    }

    private void checkViewPermission(SoilFertilityInterpretationCriteriaTableModel table, UserModel user) {
        if (isCreator(table, user) || table.isPublicTable() || isDefaultTable(table)) {
            return;
        }
        throw new AccessDeniedException("Acesso negado. O usuário não tem permissão para acessar esta tabela.");
    }

    private void checkModifyPermission(SoilFertilityInterpretationCriteriaTableModel table, UserModel user) {
        if (isDefaultTable(table)) {
            if (isSupremeUser(user)) {
                return;
            }
            throw new AccessDeniedException("Apenas o usuário supremo pode modificar tabelas padrão.");
        }
        if (!isCreator(table, user)) {
            throw new AccessDeniedException("Acesso negado. O usuário não é o criador da tabela.");
        }
    }

    private boolean isCreator(SoilFertilityInterpretationCriteriaTableModel table, UserModel user) {
        return table.getCreator() != null
                && user != null
                && Objects.equals(table.getCreator().getId(), user.getId());
    }

    private boolean isDefaultTable(SoilFertilityInterpretationCriteriaTableModel table) {
        return table.getCreator() != null
                && table.getCreator().getCargo() == Cargo.USUARIO_SUPREMO
                && !table.isPublicTable();
    }

    private boolean isSupremeUser(UserModel user) {
        return user != null && user.getCargo() == Cargo.USUARIO_SUPREMO;
    }
}
