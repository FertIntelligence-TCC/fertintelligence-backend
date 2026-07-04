package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.kContentAndDose.KContentAndDoseCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.kContentAndDose.KContentAndDosePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.kContentAndDose.KContentAndDoseResponseDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.kContentAndDose.KContentAndDoseSectionDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.KContentAndDoseModel;
import com.migueltcc.fertintelligence.repository.KContentAndDoseRepository;
import com.migueltcc.fertintelligence.repository.SoilFertilityInterpretationCriteriaTableRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.KContentAndDoseService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class KContentAndDoseServiceImpl implements KContentAndDoseService {

    @Autowired
    private KContentAndDoseRepository kContentAndDoseRepository;

    @Autowired
    private SoilFertilityInterpretationCriteriaTableRepository soilFertilityInterpretationCriteriaTableRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public KContentAndDoseResponseDto createKContentAndDose(Long tableId, KContentAndDoseCreateRequestDto createRequestDto, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);
        checkModifyPermission(table, owner);

        kContentAndDoseRepository.findByTable(table).ifPresent(existing -> {
            throw new IllegalStateException("Já existe uma tabela de teores e doses de K cadastrada para esta tabela.");
        });

        validateCreateRequest(createRequestDto);

        KContentAndDoseModel criterion = KContentAndDoseModel.builder()
                .table(table)
                .observations(createRequestDto.getObservations())
                .sources(createRequestDto.getSources())
                .build();
        copyLessThan40Section(createRequestDto.getLessThan40Section(), criterion, false);
        copyGreaterOrEqual40Section(createRequestDto.getGreaterOrEqual40Section(), criterion, false);

        return kContentAndDoseRepository.save(criterion).toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public KContentAndDoseResponseDto getKContentAndDoseById(Long criterionId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        KContentAndDoseModel criterion = findCriterionByIdOrThrow(criterionId);
        checkViewPermission(criterion.getTable(), owner);

        return criterion.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public KContentAndDoseResponseDto getKContentAndDoseByTable(Long tableId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        SoilFertilityInterpretationCriteriaTableModel table = findTableByIdOrThrow(tableId);
        checkViewPermission(table, owner);

        KContentAndDoseModel criterion = kContentAndDoseRepository.findByTable(table)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tabela de teores e doses de K não encontrada para a tabela: " + tableId));

        return criterion.toDto();
    }

    @Override
    @Transactional
    public KContentAndDoseResponseDto updateKContentAndDose(Long criterionId, KContentAndDosePostRequestDto updateRequestDto, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        KContentAndDoseModel criterion = findCriterionByIdOrThrow(criterionId);
        checkModifyPermission(criterion.getTable(), owner);

        if (updateRequestDto.getLessThan40Section() != null) {
            copyLessThan40Section(updateRequestDto.getLessThan40Section(), criterion, true);
        }
        if (updateRequestDto.getGreaterOrEqual40Section() != null) {
            copyGreaterOrEqual40Section(updateRequestDto.getGreaterOrEqual40Section(), criterion, true);
        }
        if (updateRequestDto.getObservations() != null) {
            criterion.setObservations(updateRequestDto.getObservations());
        }
        if (updateRequestDto.getSources() != null) {
            criterion.setSources(updateRequestDto.getSources());
        }

        return kContentAndDoseRepository.save(criterion).toDto();
    }

    @Override
    @Transactional
    public void deleteKContentAndDose(Long criterionId, String username) {
        UserModel owner = findUserByUsernameOrThrow(username);

        KContentAndDoseModel criterion = findCriterionByIdOrThrow(criterionId);
        checkModifyPermission(criterion.getTable(), owner);

        kContentAndDoseRepository.delete(criterion);
    }

    private void validateCreateRequest(KContentAndDoseCreateRequestDto createRequestDto) {
        if (createRequestDto == null) {
            throw new IllegalArgumentException("Dados da tabela de teores e doses de K são obrigatórios.");
        }
        validateSection(createRequestDto.getLessThan40Section(), "CTC a pH 7.0 menor do que 40 mmolc/dm³");
        validateSection(createRequestDto.getGreaterOrEqual40Section(), "CTC a pH 7.0 igual ou maior que 40 mmolc/dm³");
    }

    private void validateSection(KContentAndDoseSectionDto section, String sectionName) {
        if (section == null
                || section.getLowContentLessThan() == null
                || section.getDoseForLowContent() == null
                || section.getMediumLowerContent() == null
                || section.getMediumHigherContent() == null
                || section.getDoseForMediumContent() == null
                || section.getAdequateLowerContent() == null
                || section.getAdequateHigherContent() == null
                || section.getDoseForAdequateContent() == null
                || section.getHighContentGreaterThan() == null
                || section.getDoseForHighContent() == null) {
            throw new IllegalArgumentException("Todos os campos numéricos da seção \"" + sectionName + "\" são obrigatórios.");
        }
    }

    private void copyLessThan40Section(KContentAndDoseSectionDto section, KContentAndDoseModel criterion, boolean ignoreNulls) {
        if (!ignoreNulls || section.getLowContentLessThan() != null) criterion.setLessThan40LowContentLessThan(section.getLowContentLessThan());
        if (!ignoreNulls || section.getDoseForLowContent() != null) criterion.setLessThan40DoseForLowContent(section.getDoseForLowContent());
        if (!ignoreNulls || section.getMediumLowerContent() != null) criterion.setLessThan40MediumLowerContent(section.getMediumLowerContent());
        if (!ignoreNulls || section.getMediumHigherContent() != null) criterion.setLessThan40MediumHigherContent(section.getMediumHigherContent());
        if (!ignoreNulls || section.getDoseForMediumContent() != null) criterion.setLessThan40DoseForMediumContent(section.getDoseForMediumContent());
        if (!ignoreNulls || section.getAdequateLowerContent() != null) criterion.setLessThan40AdequateLowerContent(section.getAdequateLowerContent());
        if (!ignoreNulls || section.getAdequateHigherContent() != null) criterion.setLessThan40AdequateHigherContent(section.getAdequateHigherContent());
        if (!ignoreNulls || section.getDoseForAdequateContent() != null) criterion.setLessThan40DoseForAdequateContent(section.getDoseForAdequateContent());
        if (!ignoreNulls || section.getHighContentGreaterThan() != null) criterion.setLessThan40HighContentGreaterThan(section.getHighContentGreaterThan());
        if (!ignoreNulls || section.getDoseForHighContent() != null) criterion.setLessThan40DoseForHighContent(section.getDoseForHighContent());
    }

    private void copyGreaterOrEqual40Section(KContentAndDoseSectionDto section, KContentAndDoseModel criterion, boolean ignoreNulls) {
        if (!ignoreNulls || section.getLowContentLessThan() != null) criterion.setGreaterOrEqual40LowContentLessThan(section.getLowContentLessThan());
        if (!ignoreNulls || section.getDoseForLowContent() != null) criterion.setGreaterOrEqual40DoseForLowContent(section.getDoseForLowContent());
        if (!ignoreNulls || section.getMediumLowerContent() != null) criterion.setGreaterOrEqual40MediumLowerContent(section.getMediumLowerContent());
        if (!ignoreNulls || section.getMediumHigherContent() != null) criterion.setGreaterOrEqual40MediumHigherContent(section.getMediumHigherContent());
        if (!ignoreNulls || section.getDoseForMediumContent() != null) criterion.setGreaterOrEqual40DoseForMediumContent(section.getDoseForMediumContent());
        if (!ignoreNulls || section.getAdequateLowerContent() != null) criterion.setGreaterOrEqual40AdequateLowerContent(section.getAdequateLowerContent());
        if (!ignoreNulls || section.getAdequateHigherContent() != null) criterion.setGreaterOrEqual40AdequateHigherContent(section.getAdequateHigherContent());
        if (!ignoreNulls || section.getDoseForAdequateContent() != null) criterion.setGreaterOrEqual40DoseForAdequateContent(section.getDoseForAdequateContent());
        if (!ignoreNulls || section.getHighContentGreaterThan() != null) criterion.setGreaterOrEqual40HighContentGreaterThan(section.getHighContentGreaterThan());
        if (!ignoreNulls || section.getDoseForHighContent() != null) criterion.setGreaterOrEqual40DoseForHighContent(section.getDoseForHighContent());
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

    private KContentAndDoseModel findCriterionByIdOrThrow(Long criterionId) {
        return kContentAndDoseRepository.findById(criterionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tabela de teores e doses de K não encontrada com o ID: " + criterionId));
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
