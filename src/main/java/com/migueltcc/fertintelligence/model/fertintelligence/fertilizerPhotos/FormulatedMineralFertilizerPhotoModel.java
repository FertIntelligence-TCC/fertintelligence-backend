package com.migueltcc.fertintelligence.model.fertintelligence.fertilizerPhotos;

import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.FormulatedMineralFertilizerModel;
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
@Table(name = "ADUBOS_MINERAIS_FORMULADOS_FOTOS")
public class FormulatedMineralFertilizerPhotoModel extends FertilizerPhotoModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADUBO_ID", nullable = false)
    private FormulatedMineralFertilizerModel fertilizer;

    public FormulatedMineralFertilizerPhotoModel(FormulatedMineralFertilizerModel fertilizer, String idFoto, Integer ordem) {
        super(idFoto, ordem);
        this.fertilizer = fertilizer;
    }
}
