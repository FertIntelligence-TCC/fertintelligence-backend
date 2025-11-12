package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Nutriente;
import com.migueltcc.fertintelligence.dto.tables.contentRange.ContentRangeResponseDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "INTERVALO_TEOR_NUTRIENTE")
public class ContentRangeModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "ID_TABELA", nullable = false)
    CropFertilizationTableModel table;

    @Column(name = "NUTRIENTE", nullable = false)
    Nutriente nutrient;

    /**
     * Pode ter até 5 intervalos de teores.
     * O primeiro intervalo tem smallest nulo.
     * O último intervalo tem largest nulo.
     * Os outros intervalos tem smallest e largest não nulos.
     * Em 2 intervalos subsequentes, o largest do primeiro é igual ao smallest do segundo.
     */
    @Column(name = "ORDEM_TEOR", nullable = true)
    Integer order;

    @Column(name = "MENOR_TEOR", nullable = true)
    Double smallest; // Ao menos um dos teores deve ser não nulo

    @Column(name = "MAIOR_TEOR", nullable = true)
    Double largest; // Ao menos um dos teores deve ser não nulo

    @Column(name = "APLICACAO_RECOMENDADA_PLANTIO", nullable = true)
    Double application;

    public ContentRangeResponseDto toDto() {
        return ContentRangeResponseDto.builder()
                .id(this.id)
                .tableId(this.table.getId())
                .nutrient(this.nutrient)
                .order(this.order)
                .smallest(this.smallest)
                .largest(this.largest)
                .application(this.application)
                .build();
    }
}
