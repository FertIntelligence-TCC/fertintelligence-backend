package com.migueltcc.fertintelligence.model.fertintelligence.fertilizerPhotos;

import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.GreenFertilizerModel;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ADUBOS_VERDES_FOTOS")
public class GreenFertilizerPhotoModel extends FertilizerPhotoModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADUBO_ID", nullable = false)
    private GreenFertilizerModel fertilizer;

    public GreenFertilizerPhotoModel(GreenFertilizerModel fertilizer, String idFoto, Integer ordem) {
        super(idFoto, ordem);
        this.fertilizer = fertilizer;
    }
}
