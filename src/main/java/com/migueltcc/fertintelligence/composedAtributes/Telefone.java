package com.migueltcc.fertintelligence.composedAtributes;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Telefone {
    String pais;
    String ddd;
    String numero;
}