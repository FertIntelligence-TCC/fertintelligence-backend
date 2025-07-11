package com.migueltcc.fertintelligence.composedAtributes;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataNasc {
    private String dia;
    private String mes;
    private String ano;
}
