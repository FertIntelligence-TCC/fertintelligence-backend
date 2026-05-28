package com.migueltcc.fertintelligence.config;

import com.migueltcc.fertintelligence.composedAttributes.plot.AreaIrrigada;
import com.migueltcc.fertintelligence.composedAttributes.plot.ClasseSolo;
import com.migueltcc.fertintelligence.composedAttributes.plot.TexturaSolo;
import com.migueltcc.fertintelligence.composedAttributes.property.LatitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.property.LongitudeDirection;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.repository.PlotRepository;
import com.migueltcc.fertintelligence.repository.PropertyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(6)
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
@ConditionalOnProperty(
        prefix = "app.seed",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class PlotDataSeeder implements CommandLineRunner {

    private final PropertyRepository propertyRepository;
    private final PlotRepository plotRepository;

    private static final String[] PLOT_NAMES = {
            "Talhão A1",
            "Talhão A2",
            "Talhão B1",
            "Talhão B2",
            "Setor Norte",
            "Setor Sul",
            "Área Experimental",
            "Área Irrigada 01",
            "Área Sequeiro 02",
            "Bloco Central",
            "Bloco Oeste",
            "Bloco Leste"
    };

    private static final String[] OBSERVATIONS = {
            "Solo com histórico de cultivo de milho.",
            "Área utilizada para rotação soja/milho.",
            "Região com necessidade frequente de correção de potássio.",
            "Área irrigada por pivô central.",
            "Talhão com compactação moderada.",
            "Área experimental para manejo de fósforo.",
            "Área com histórico de adubação orgânica.",
            "Talhão indicado para testes de recomendação."
    };

    private static final TexturaSolo[] SOIL_TEXTURES = {
            TexturaSolo.AREIA,
            TexturaSolo.FRANCO_ARENOSO,
            TexturaSolo.FRANCA,
            TexturaSolo.FRANCO_ARGILOSA,
            TexturaSolo.ARGILA
    };

    private static final ClasseSolo[] SOIL_CLASSES = {
            ClasseSolo.LATOSSOLO,
            ClasseSolo.ARGISSOLO,
            ClasseSolo.NEOSSOLO,
            ClasseSolo.CAMBISSOLO,
            ClasseSolo.VERTISSOLO
    };

    @Override
    @Transactional
    public void run(String... args) {
        List<PropertyModel> properties = propertyRepository.findAll();

        if (properties.isEmpty()) {
            log.warn("⚠️ Nenhuma propriedade encontrada. PlotDataSeeder ignorado.");
            return;
        }

        for (int propertyIndex = 0; propertyIndex < properties.size(); propertyIndex++) {
            PropertyModel property = properties.get(propertyIndex);
            int amount = 4 + (propertyIndex % 5);

            for (int plotIndex = 0; plotIndex < amount; plotIndex++) {
                String baseName = PLOT_NAMES[plotIndex % PLOT_NAMES.length];
                String identification = baseName + " - P" + property.getId();

                double rawArea = 5.0 + (propertyIndex * 3.5) + (plotIndex * 7.25);
                double limitedArea = Math.max(2.0, Math.min(150.0, rawArea));
                double area = Math.round(limitedArea * 100.0) / 100.0;

                TexturaSolo texture = SOIL_TEXTURES[(propertyIndex + plotIndex) % SOIL_TEXTURES.length];
                ClasseSolo soilClass = SOIL_CLASSES[(propertyIndex + plotIndex) % SOIL_CLASSES.length];
                AreaIrrigada irrigated = (plotIndex % 2 == 0) ? AreaIrrigada.SIM : AreaIrrigada.NAO;
                String observation = OBSERVATIONS[(propertyIndex + plotIndex) % OBSERVATIONS.length];

                createIfNotExists(
                        property,
                        identification,
                        area,
                        texture,
                        soilClass,
                        irrigated,
                        observation,
                        propertyIndex,
                        plotIndex
                );
            }
        }
    }

    private void createIfNotExists(
            PropertyModel property,
            String identification,
            double area,
            TexturaSolo texture,
            ClasseSolo soilClass,
            AreaIrrigada irrigated,
            String observation,
            int propertyIndex,
            int plotIndex
    ) {
        if (property == null) {
            return;
        }

        boolean exists = plotRepository.findByIdentificationAndProperty(identification, property).isPresent();
        if (exists) {
            log.info("↩️ Talhão já existe: propriedade={} talhão={}", property.getNome(), identification);
            return;
        }

        int yearBase = 2020;
        int cropIncorporationYear = yearBase + ((propertyIndex + plotIndex) % 6);
        double declivity = Math.round((2.5 + propertyIndex + (plotIndex * 0.75)) * 100.0) / 100.0;
        double monthlyPluviosity = Math.round((90.0 + propertyIndex * 4.0 + plotIndex * 6.5) * 100.0) / 100.0;
        double annualPluviosity = Math.round((monthlyPluviosity * 12.0) * 100.0) / 100.0;
        double latitude = Math.round((-15.0 - (propertyIndex * 0.2) - (plotIndex * 0.05)) * 10000.0) / 10000.0;
        double longitude = Math.round((-47.0 - (propertyIndex * 0.25) - (plotIndex * 0.06)) * 10000.0) / 10000.0;
        double altitude = Math.round((700.0 + (propertyIndex * 15.0) + (plotIndex * 8.0)) * 100.0) / 100.0;

        PlotModel plot = PlotModel.builder()
                .property(property)
                .identification(identification)
                .area(area)
                .soilTexture(texture)
                .soilClass(soilClass)
                .cropIncorporationYear(cropIncorporationYear)
                .irrigatedArea(irrigated)
                .declivity(declivity)
                .monthlyPluviosity(monthlyPluviosity)
                .annualPluviosity(annualPluviosity)
                .latitude(latitude)
                .latitudeDirection(LatitudeDirection.SUL)
                .longitude(longitude)
                .longitudeDirection(LongitudeDirection.OESTE)
                .altitude(altitude)
                .build();

        plotRepository.save(plot);
        log.info("✅ Talhão criado: propriedade={} talhão={} ({})", property.getNome(), identification, observation);
    }
}
