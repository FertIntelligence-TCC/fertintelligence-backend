package com.migueltcc.fertintelligence.model.fertintelligence.fertigram;

import jakarta.persistence.JoinColumn;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FertigramNutrientModelMappingTest {

    @Test
    void deveMapearFkDoFertigramaComNomeCorretoDaColuna() throws NoSuchFieldException {
        Field field = FertigramNutrientModel.class.getDeclaredField("fertigram");
        JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);

        assertNotNull(joinColumn);
        assertEquals("ID_FERTIGRAMA", joinColumn.name());
    }
}
