package com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels;

import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "BIO_FERTILIZANTES")
@EqualsAndHashCode
public class BioFertilizerModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_CRIADOR", nullable = false)
    private UserModel user;

}
