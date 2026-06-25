package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeCientifico;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.CriterioCalagem;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.SpacingType;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.TechnicalTableGroup;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.dto.tables.cropFertilization.CropFertilizationTableCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFertilization.CropFertilizationTablePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFertilization.CropFertilizationTableResponseDto;
import com.migueltcc.fertintelligence.dto.tables.cropFertilization.CropFertilizationTableResolveLimingCriterionRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFertilization.CropFertilizationTableResolveLimingCriterionResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.PhysicalAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.FertilityAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.ContentRangeModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CoverageModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFertilizationTableModel;
import com.migueltcc.fertintelligence.repository.ContentRangeRepository;
import com.migueltcc.fertintelligence.repository.CoverageRepository;
import com.migueltcc.fertintelligence.repository.CropFertilizationTableRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.repository.PropertyRepository;
import com.migueltcc.fertintelligence.repository.PlotRepository;
import com.migueltcc.fertintelligence.repository.PhysicalAnalysisExtractRepository;
import com.migueltcc.fertintelligence.repository.FertilityAnalysisExtractRepository;
import com.migueltcc.fertintelligence.repository.PropertyAccessRequestRepository;
import com.migueltcc.fertintelligence.service.documentation.CropFertilizationTableService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CropFertilizationTableServiceImpl implements CropFertilizationTableService {

    private final CropFertilizationTableRepository cropFertilizationTableRepository;
    private final UserRepository userRepository;

    // Novos repositórios para gerenciar a exclusão dos filhos
    private final ContentRangeRepository contentRangeRepository;
    private final CoverageRepository coverageRepository;
    private final PropertyRepository propertyRepository;
    private final PlotRepository plotRepository;
    private final PhysicalAnalysisExtractRepository physicalAnalysisExtractRepository;
    private final FertilityAnalysisExtractRepository fertilityAnalysisExtractRepository;
    private final PermissionManager permissionManager;
    private final PropertyAccessRequestRepository propertyAccessRequestRepository;

    public CropFertilizationTableServiceImpl(
            CropFertilizationTableRepository cropFertilizationTableRepository,
            UserRepository userRepository,
            ContentRangeRepository contentRangeRepository,
            CoverageRepository coverageRepository,
            PropertyRepository propertyRepository,
            PlotRepository plotRepository,
            PhysicalAnalysisExtractRepository physicalAnalysisExtractRepository,
            FertilityAnalysisExtractRepository fertilityAnalysisExtractRepository,
            PermissionManager permissionManager,
            PropertyAccessRequestRepository propertyAccessRequestRepository) {
        this.cropFertilizationTableRepository = cropFertilizationTableRepository;
        this.userRepository = userRepository;
        this.contentRangeRepository = contentRangeRepository;
        this.coverageRepository = coverageRepository;
        this.propertyRepository = propertyRepository;
        this.plotRepository = plotRepository;
        this.physicalAnalysisExtractRepository = physicalAnalysisExtractRepository;
        this.fertilityAnalysisExtractRepository = fertilityAnalysisExtractRepository;
        this.permissionManager = permissionManager;
        this.propertyAccessRequestRepository = propertyAccessRequestRepository;
    }

    @Override
    @Transactional
    public CropFertilizationTableResponseDto createCropFertilizationTable(
            CropFertilizationTableCreateRequestDto createRequestDto,
            String username
    ) {
        UserModel owner = findUserByUsernameOrThrow(username);

        validateCropNames(createRequestDto.getCrop_common_name(), createRequestDto.getCrop_scientific_nome());

        validateRegionalSpacing(createRequestDto.getUsed_spacing());

        Selection selection = resolveSelection(createRequestDto.getPropertyId(), createRequestDto.getPlotId(), createRequestDto.getPhysicalAnalysisId(), createRequestDto.getFertilityAnalysisId(), owner);

        CropFertilizationTableModel table = CropFertilizationTableModel.builder()
                .creator(owner)
                .region(createRequestDto.getRegion())
                .crop_common_name(createRequestDto.getCrop_common_name())
                .crop_scientific_nome(createRequestDto.getCrop_scientific_nome())
                .cultivares(createRequestDto.getCultivares())
                .suggested_spacing(createRequestDto.getSuggested_spacing())
                .initial_value(createRequestDto.getInitial_value())
                .final_value(createRequestDto.getFinal_value())
                .used_spacing(createRequestDto.getUsed_spacing())
                .used_spacing_value(createRequestDto.getUsed_spacing_value())
                .used_spacing_maximum_value(resolveRegionalSpacingMaximum(createRequestDto.getUsed_spacing_value(), createRequestDto.getUsed_spacing_maximum_value()))
                .regional_productivity(createRequestDto.getRegional_productivity())
                .expected_productivity(createRequestDto.getExpected_productivity())
                .criteria(resolveCriteria(selection.physicalAnalysis(), selection.fertilityAnalysis()))
                .property(selection.property())
                .plot(selection.plot())
                .physicalAnalysis(selection.physicalAnalysis())
                .fertilityAnalysis(selection.fertilityAnalysis())
                .observations(createRequestDto.getObservations())
                .sources(createRequestDto.getSources())
                .publicTable(Boolean.TRUE.equals(createRequestDto.getPublic_table()))
                .build();

        CropFertilizationTableModel saved = cropFertilizationTableRepository.save(table);
        return buildResponse(saved, owner);
    }

    @Override
    @Transactional(readOnly = true)
    public CropFertilizationTableResponseDto getCropFertilizationTableById(Long tableId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);
        CropFertilizationTableModel table = findTableByIdOrThrow(tableId);
        assertCanView(table, requester);
        return buildResponse(table, requester);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropFertilizationTableResponseDto> getAllCropFertilizationTables(String username) {
        return getAllCropFertilizationTables(username, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropFertilizationTableResponseDto> getAllCropFertilizationTables(String username, TechnicalTableGroup group) {
        UserModel owner = findUserByUsernameOrThrow(username);

        if (group != null) {
            return findTablesByGroup(owner, group).stream()
                    .map(t -> buildResponse(t, owner))
                    .toList();
        }

        if (isSupremeUser(owner)) {
            return cropFertilizationTableRepository.findAllByCreator_CargoAndPublicTableTrue(Cargo.USUARIO_SUPREMO)
                    .stream()
                    .map(t -> buildResponse(t, owner))
                    .toList();
        }

        return mergeTables(
                cropFertilizationTableRepository.findAllByCreator(owner),
                cropFertilizationTableRepository.findAllByCreator_CargoAndPublicTableTrue(Cargo.USUARIO_SUPREMO),
                cropFertilizationTableRepository.findAllByPublicTableTrue()
        )
                .stream()
                .map(t -> buildResponse(t, owner))
                .toList();
    }

    private List<CropFertilizationTableModel> findTablesByGroup(UserModel owner, TechnicalTableGroup group) {
        return switch (group) {
            case MINHAS -> cropFertilizationTableRepository.findAllByCreator(owner);
            case PRIVADAS -> {
                // Retorna tabelas do usuário logado que não são públicas (privadas)
                List<CropFertilizationTableModel> allByOwner = cropFertilizationTableRepository.findAllByCreator(owner);
                yield allByOwner.stream()
                        .filter(t -> !t.isPublicTable())
                        .collect(Collectors.toList());
            }
            case PUBLICAS -> cropFertilizationTableRepository.findAllByPublicTableTrueAndCreator_CargoNot(Cargo.USUARIO_SUPREMO);
            case PADRAO -> cropFertilizationTableRepository.findAllByCreator_CargoAndPublicTableTrue(Cargo.USUARIO_SUPREMO);
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropFertilizationTableResponseDto> getAllPublicCropFertilizationTables(String username) {
        UserModel requester = findUserByUsernameOrThrow(username);
        return cropFertilizationTableRepository.findAllByPublicTableTrueAndCreator_CargoNot(Cargo.USUARIO_SUPREMO)
                .stream()
                .map(t -> buildResponse(t, requester))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropFertilizationTableResponseDto> getAllDefaultCropFertilizationTables(String username) {
        UserModel requester = findUserByUsernameOrThrow(username);
        return cropFertilizationTableRepository.findAllByCreator_CargoAndPublicTableTrue(Cargo.USUARIO_SUPREMO)
                .stream()
                .map(t -> buildResponse(t, requester))
                .toList();
    }

    @Override
    @Transactional
    public CropFertilizationTableResponseDto updateCropFertilizationTable(
            Long tableId,
            CropFertilizationTablePostRequestDto updateRequestDto,
            String username
    ) {
        UserModel requester = findUserByUsernameOrThrow(username);
        CropFertilizationTableModel table = findTableByIdOrThrow(tableId);
        assertCanModify(table, requester);

        if (updateRequestDto.getCrop_common_name() != null || updateRequestDto.getCrop_scientific_nome() != null) {
            NomeComum common = updateRequestDto.getCrop_common_name() != null
                    ? updateRequestDto.getCrop_common_name()
                    : table.getCrop_common_name();

            NomeCientifico scientific = updateRequestDto.getCrop_scientific_nome() != null
                    ? updateRequestDto.getCrop_scientific_nome()
                    : table.getCrop_scientific_nome();

            validateCropNames(common, scientific);
        }

        validateRegionalSpacing(updateRequestDto.getUsed_spacing());

        updateTableFields(table, updateRequestDto);
        if (updateRequestDto.getPropertyId() != null || updateRequestDto.getPlotId() != null
                || updateRequestDto.getPhysicalAnalysisId() != null || updateRequestDto.getFertilityAnalysisId() != null) {
            Selection selection = resolveSelection(
                    updateRequestDto.getPropertyId() != null ? updateRequestDto.getPropertyId() : idOf(table.getProperty()),
                    updateRequestDto.getPlotId() != null ? updateRequestDto.getPlotId() : idOf(table.getPlot()),
                    updateRequestDto.getPhysicalAnalysisId() != null ? updateRequestDto.getPhysicalAnalysisId() : idOf(table.getPhysicalAnalysis()),
                    updateRequestDto.getFertilityAnalysisId() != null ? updateRequestDto.getFertilityAnalysisId() : idOf(table.getFertilityAnalysis()),
                    requester);
            table.setProperty(selection.property());
            table.setPlot(selection.plot());
            table.setPhysicalAnalysis(selection.physicalAnalysis());
            table.setFertilityAnalysis(selection.fertilityAnalysis());
        }
        table.setCriteria(resolveCriteria(table.getPhysicalAnalysis(), table.getFertilityAnalysis()));

        CropFertilizationTableModel saved = cropFertilizationTableRepository.save(table);
        return buildResponse(saved, requester);
    }

    @Override
    @Transactional(readOnly = true)
    public CropFertilizationTableResolveLimingCriterionResponseDto resolvePublicLimingCriterion(
            CropFertilizationTableResolveLimingCriterionRequestDto requestDto,
            String username
    ) {
        UserModel requester = findUserByUsernameOrThrow(username);
        CropFertilizationTableModel table = findTableByIdOrThrow(requestDto.getCropFertilizationTableId());
        if (!table.isPublicTable() && !isDefaultTable(table)) {
            throw new AccessDeniedException("Tabela pública/padrão não encontrada ou indisponível para visualização.");
        }
        assertCanView(table, requester);
        Selection selection = resolveSelection(
                requestDto.getPropertyId(),
                requestDto.getPlotId(),
                requestDto.getPhysicalAnalysisId(),
                requestDto.getFertilityAnalysisId(),
                requester);
        return CropFertilizationTableResolveLimingCriterionResponseDto.builder()
                .indicatedLimingCriterion(resolveIndicatedLimingCriterion(selection.physicalAnalysis(), selection.fertilityAnalysis()))
                .build();
    }

    @Override
    @Transactional
    public void deleteCropFertilizationTable(Long tableId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);
        CropFertilizationTableModel table = findTableByIdOrThrow(tableId);
        assertCanModify(table, requester);

        // --- INÍCIO DA CORREÇÃO: Deleção Manual em Cascata ---

        // 1. Busca todos os intervalos desta tabela
        List<ContentRangeModel> ranges = contentRangeRepository.findAllByTableOrderByNutrientAscOrderAsc(table);

        // 2. Para cada intervalo, busca e deleta suas coberturas
        for (ContentRangeModel range : ranges) {
            List<CoverageModel> coverages = coverageRepository.findAllByRangeOrderByOrderAsc(range);
            coverageRepository.deleteAll(coverages);
        }

        // 3. Deleta os intervalos
        contentRangeRepository.deleteAll(ranges);

        // 4. Finalmente, deleta a tabela pai
        cropFertilizationTableRepository.delete(table);

        // --- FIM DA CORREÇÃO ---
    }

    // --- Métodos Privados ---

    private void updateTableFields(CropFertilizationTableModel table, CropFertilizationTablePostRequestDto dto) {
        if (dto.getRegion() != null) table.setRegion(dto.getRegion());
        if (dto.getCrop_common_name() != null) table.setCrop_common_name(dto.getCrop_common_name());
        if (dto.getCrop_scientific_nome() != null) table.setCrop_scientific_nome(dto.getCrop_scientific_nome());
        if (dto.getCultivares() != null) table.setCultivares(dto.getCultivares());
        if (dto.getSuggested_spacing() != null) table.setSuggested_spacing(dto.getSuggested_spacing());
        if (dto.getInitial_value() != null) table.setInitial_value(dto.getInitial_value());
        if (dto.getFinal_value() != null) table.setFinal_value(dto.getFinal_value());
        if (dto.getUsed_spacing() != null) table.setUsed_spacing(dto.getUsed_spacing());
        if (dto.getUsed_spacing_value() != null) table.setUsed_spacing_value(dto.getUsed_spacing_value());
        if (dto.getUsed_spacing_maximum_value() != null) table.setUsed_spacing_maximum_value(dto.getUsed_spacing_maximum_value());
        if (dto.getRegional_productivity() != null) table.setRegional_productivity(dto.getRegional_productivity());
        if (dto.getExpected_productivity() != null) table.setExpected_productivity(dto.getExpected_productivity());
        // Critério de calagem é calculado pelo servidor a partir das análises selecionadas.
        if (dto.getObservations() != null) table.setObservations(dto.getObservations());
        if (dto.getSources() != null) table.setSources(dto.getSources());
        if (dto.getPublic_table() != null) table.setPublicTable(dto.getPublic_table());
    }

    private void validateRegionalSpacing(SpacingType regionalSpacingType) {
        if (regionalSpacingType == null) {
            return;
        }
        if (regionalSpacingType == SpacingType.BETWEEN_LINES_IN_METERS) {
            throw new IllegalArgumentException("Espaçamento usado na região não aceita Entre Linhas (m). Use Entre Plantas/Covas (m) ou Plantas por metro linear (m).");
        }
    }

    private Double resolveRegionalSpacingMaximum(Double minimumValue, Double maximumValue) {
        return maximumValue != null ? maximumValue : minimumValue;
    }

    private CropFertilizationTableResponseDto buildResponse(CropFertilizationTableModel table, UserModel requester) {
        CropFertilizationTableResponseDto dto = table.toDto();
        boolean canViewLinkedAnalyses = canViewOriginalLinkedAnalyses(table, requester);
        dto.setCanViewLinkedAnalyses(canViewLinkedAnalyses);
        if (canViewLinkedAnalyses) {
            dto.setIndicatedLimingCriterion(resolveIndicatedLimingCriterion(table.getPhysicalAnalysis(), table.getFertilityAnalysis()));
        } else {
            hideLinkedAnalysisData(dto);
        }
        return dto;
    }


    private boolean canViewOriginalLinkedAnalyses(CropFertilizationTableModel table, UserModel requester) {
        if (isCreator(table, requester)) return true;
        if (table.getProperty() == null && table.getPlot() == null && table.getPhysicalAnalysis() == null && table.getFertilityAnalysis() == null) return true;
        if (table.getPlot() == null) return false;
        if (table.getProperty() != null && !table.getPlot().getProperty().getId().equals(table.getProperty().getId())) return false;
        return permissionManager.canReadPlot(table.getPlot(), requester);
    }

    private void hideLinkedAnalysisData(CropFertilizationTableResponseDto dto) {
        dto.setPropertyId(null);
        dto.setPropertyName(null);
        dto.setPlotId(null);
        dto.setPlotIdentification(null);
        dto.setPhysicalAnalysisId(null);
        dto.setPhysicalAnalysisIdentification(null);
        dto.setFertilityAnalysisId(null);
        dto.setFertilityAnalysisIdentification(null);
        dto.setIndicatedLimingCriterion(null);
    }

    private Selection resolveSelection(Long propertyId, Long plotId, Long physicalAnalysisId, Long fertilityAnalysisId, UserModel user) {
        if ((physicalAnalysisId != null || fertilityAnalysisId != null) && plotId == null) {
            throw new IllegalArgumentException("Informe o talhão para vincular análises física ou de fertilidade à tabela de adubação.");
        }

        PropertyModel property = propertyId == null ? null : propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Propriedade não encontrada com ID: " + propertyId));
        PlotModel plot = plotId == null ? null : plotRepository.findById(plotId)
                .orElseThrow(() -> new EntityNotFoundException("Talhão não encontrado com ID: " + plotId));

        if (plot != null) {
            if (property != null && !plot.getProperty().getId().equals(property.getId())) {
                throw new IllegalArgumentException("Talhão informado não pertence à propriedade selecionada.");
            }
            property = plot.getProperty();
            permissionManager.assertCanReadPlot(plot, user);
        } else if (property != null) {
            assertCanReadProperty(property, user);
        }

        PhysicalAnalysisExtractModel physical = physicalAnalysisId == null ? null : physicalAnalysisExtractRepository.findById(physicalAnalysisId)
                .orElseThrow(() -> new EntityNotFoundException("Extrato de análise física não encontrado com ID: " + physicalAnalysisId));
        FertilityAnalysisExtractModel fertility = fertilityAnalysisId == null ? null : fertilityAnalysisExtractRepository.findById(fertilityAnalysisId)
                .orElseThrow(() -> new EntityNotFoundException("Extrato de análise de fertilidade não encontrado com ID: " + fertilityAnalysisId));

        if (physical != null && !plot.getId().equals(resolvePlot(physical).getId())) {
            throw new IllegalArgumentException("Análise física informada não pertence ao talhão selecionado.");
        }
        if (fertility != null && !plot.getId().equals(resolvePlot(fertility).getId())) {
            throw new IllegalArgumentException("Análise de fertilidade informada não pertence ao talhão selecionado.");
        }
        return new Selection(property, plot, physical, fertility);
    }

    public String resolveIndicatedLimingCriterion(PhysicalAnalysisExtractModel physical, FertilityAnalysisExtractModel fertility) {
        if (fertility == null) return "Não é possível definir um critério de calagem";
        if (physical == null) return "SATURAÇÃO POR BASES TROCÁVEIS";
        double factor = limingFactor(physical.getTeorArgila());
        double i = factor * zeroIfNull(fertility.getAluminio());
        double ii = factor * (2.0 - (zeroIfNull(fertility.getCalcio()) + zeroIfNull(fertility.getMagnesio())));
        return i >= ii ? "Neutralização do Al trocável" : "Elevação dos teores de Ca + Mg";
    }

    private CriterioCalagem resolveCriteria(PhysicalAnalysisExtractModel physical, FertilityAnalysisExtractModel fertility) {
        String text = resolveIndicatedLimingCriterion(physical, fertility);
        if ("SATURAÇÃO POR BASES TROCÁVEIS".equals(text)) return CriterioCalagem.SATURACAO_POR_BASES_TROCAVEIS;
        if ("Elevação dos teores de Ca + Mg".equals(text)) return CriterioCalagem.ELEVACAO_DO_TEOR_DE_CALCIO_MAIS_MAGNESIO;
        return CriterioCalagem.NEUTRALIZACAO_POR_ALUMINIO_TROCAVEL;
    }

    private double limingFactor(Double clayContent) {
        double clay = zeroIfNull(clayContent);
        if (clay < 150.0) return 1.5;
        if (clay <= 350.0) return 2.0;
        return 2.5;
    }

    private double zeroIfNull(Double value) { return value != null ? value : 0.0; }

    private void assertCanReadProperty(PropertyModel property, UserModel user) {
        if (property.getOwner() != null && property.getOwner().getId().equals(user.getId())) return;
        if (property.getManager() != null && property.getManager().getId().equals(user.getId())) return;
        if (propertyAccessRequestRepository.findByPropertyAndRequesterAndStatus(property, user, AccessRequestStatus.APPROVED).isPresent()) return;
        throw new AccessDeniedException("Você não tem permissão para acessar esta propriedade.");
    }

    private PlotModel resolvePlot(PhysicalAnalysisExtractModel analysis) {
        if (analysis.getRangeExtract() != null && analysis.getRangeExtract().getAnalysis() != null) return analysis.getRangeExtract().getAnalysis().getPlot();
        if (analysis.getLayerExtract() != null && analysis.getLayerExtract().getAnalysis() != null) return analysis.getLayerExtract().getAnalysis().getPlot();
        throw new IllegalArgumentException("Análise física não possui talhão associado.");
    }

    private PlotModel resolvePlot(FertilityAnalysisExtractModel analysis) {
        if (analysis.getRangeExtract() != null && analysis.getRangeExtract().getAnalysis() != null) return analysis.getRangeExtract().getAnalysis().getPlot();
        if (analysis.getLayerExtract() != null && analysis.getLayerExtract().getAnalysis() != null) return analysis.getLayerExtract().getAnalysis().getPlot();
        throw new IllegalArgumentException("Análise de fertilidade não possui talhão associado.");
    }

    private Long idOf(Object entity) {
        if (entity instanceof PropertyModel p) return p.getId();
        if (entity instanceof PlotModel p) return p.getId();
        if (entity instanceof PhysicalAnalysisExtractModel p) return p.getId();
        if (entity instanceof FertilityAnalysisExtractModel f) return f.getId();
        return null;
    }

    private record Selection(PropertyModel property, PlotModel plot, PhysicalAnalysisExtractModel physicalAnalysis, FertilityAnalysisExtractModel fertilityAnalysis) {}

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private CropFertilizationTableModel findTableByIdOrThrow(Long tableId) {
        return cropFertilizationTableRepository.findById(tableId)
                .orElseThrow(() -> new EntityNotFoundException("Tabela de adubação não encontrada com ID: " + tableId));
    }

    private void assertCanView(CropFertilizationTableModel table, UserModel requester) {
        if (isCreator(table, requester) || table.isPublicTable() || isDefaultTable(table)) {
            return;
        }
        throw new AccessDeniedException("Acesso negado: Você não tem permissão para acessar esta tabela.");
    }

    private void assertCanModify(CropFertilizationTableModel table, UserModel requester) {
        if (isDefaultTable(table)) {
            if (isSupremeUser(requester)) {
                return;
            }
            throw new AccessDeniedException("Acesso negado: Apenas o usuário supremo pode modificar tabelas padrão.");
        }
        assertIsCreator(table, requester);
    }

    private void assertIsCreator(CropFertilizationTableModel table, UserModel requester) {
        if (table.getCreator() == null || requester == null || table.getCreator().getId() == null) {
            throw new AccessDeniedException("Acesso negado: Propriedades de criador inválidas.");
        }
        if (!table.getCreator().getId().equals(requester.getId())) {
            throw new AccessDeniedException("Acesso negado: Você não tem permissão para modificar esta tabela.");
        }
    }

    private boolean isCreator(CropFertilizationTableModel table, UserModel requester) {
        return table.getCreator() != null
                && requester != null
                && table.getCreator().getId() != null
                && table.getCreator().getId().equals(requester.getId());
    }

    private boolean isDefaultTable(CropFertilizationTableModel table) {
        return table.getCreator() != null && table.getCreator().getCargo() == Cargo.USUARIO_SUPREMO && table.isPublicTable();
    }

    private boolean isSupremeUser(UserModel user) {
        return user != null && user.getCargo() == Cargo.USUARIO_SUPREMO;
    }

    @SafeVarargs
    private List<CropFertilizationTableModel> mergeTables(List<CropFertilizationTableModel>... tableLists) {
        Map<Long, CropFertilizationTableModel> byId = Stream.of(tableLists)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.toMap(CropFertilizationTableModel::getId, Function.identity(), (first, ignored) -> first, LinkedHashMap::new));
        return List.copyOf(byId.values());
    }

    private void validateCropNames(NomeComum commonName, NomeCientifico scientificName) {
        if (commonName == null || scientificName == null) {
            throw new IllegalArgumentException("Nome comum e nome científico são obrigatórios.");
        }

        NomeCientifico expectedScientificName;
        switch (commonName) {
            case ALGODAO -> expectedScientificName = NomeCientifico.Gossypium_hirsutum;
            case AMENDOIM -> expectedScientificName = NomeCientifico.Arachis_hypogaea;
            case CANA_DE_ACUCAR -> expectedScientificName = NomeCientifico.Saccharum_officinarum;
            case FEIJAO_CAUPI -> expectedScientificName = NomeCientifico.Vigna_unguiculata;
            case FEIJAO_COMUM -> expectedScientificName = NomeCientifico.Phaseolus_vulgaris;
            case GERGELIM -> expectedScientificName = NomeCientifico.Sesamum_indicum;
            case MAMONA -> expectedScientificName = NomeCientifico.Ricinus_communis;
            case MILHO -> expectedScientificName = NomeCientifico.Zea_mays;
            case SISAL -> expectedScientificName = NomeCientifico.Agave_sisalana;
            case SOJA -> expectedScientificName = NomeCientifico.Glycine_max;
            default -> throw new IllegalArgumentException("Nome comum da cultura inválido ou não suportado: " + commonName);
        }

        if (scientificName != expectedScientificName) {
            throw new IllegalArgumentException(
                    String.format("Inconsistência: O nome científico '%s' não corresponde à cultura '%s'. Esperado: '%s'.",
                            scientificName, commonName, expectedScientificName)
            );
        }
    }
}
