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
@Builder(toBuilder = true)
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

    @Column(name = "ORDEM_TEOR", nullable = true)
    Integer order;

    @Column(name = "MENOR_TEOR", nullable = true)
    Double smallest;

    @Column(name = "MAIOR_TEOR", nullable = true)
    Double largest;

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
