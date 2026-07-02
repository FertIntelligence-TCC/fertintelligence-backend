package com.migueltcc.fertintelligence.model.fertintelligence;

import com.migueltcc.fertintelligence.dto.annualCropFolder.AnnualCropFolderResponseDto;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "PASTAS_CULTURAS_ANUAIS")
@EqualsAndHashCode
public class AnnualCropFolderModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TALHAO", nullable = false)
    PlotModel plot;

    @Column(name = "ANO_CULURAS", nullable = false)
    Integer cropsYear;

    public AnnualCropFolderResponseDto toDto() {
        return AnnualCropFolderResponseDto.builder()
                .id(this.id)
                .cropsYear(this.cropsYear)
                .plotId(this.plot.getId())
                .plotIdentification(this.plot.getIdentification())
                .build();
    }

}
