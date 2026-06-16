package com.migueltcc.fertintelligence.model.fertintelligence.fertilizerPhotos;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class FertilizerPhotoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ID_FOTO", nullable = false)
    private String idFoto;

    @Column(name = "ORDEM", nullable = false)
    private Integer ordem;

    protected FertilizerPhotoModel(String idFoto, Integer ordem) {
        this.idFoto = idFoto;
        this.ordem = ordem;
    }
}
