package com.migueltcc.fertintelligence.composedAttributes.User;

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