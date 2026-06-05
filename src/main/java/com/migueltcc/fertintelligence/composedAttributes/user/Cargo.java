package com.migueltcc.fertintelligence.composedAttributes.user;

public enum Cargo {
    USUARIO_SUPREMO,
    PROPRIETARIO,
    GERENTE,
    AGRONOMO_RESIDENTE,
    AGRONOMO_CONSULTOR,
    SUPERVISOR_DE_AREA,
    SECRETARIO;

    public boolean isCadastroComumPermitido() {
        return this != USUARIO_SUPREMO;
    }

    public boolean canManageProperties() {
        return this == USUARIO_SUPREMO || this == PROPRIETARIO;
    }

    public boolean canManageFertilizers() {
        return this == USUARIO_SUPREMO || this == PROPRIETARIO || this == GERENTE;
    }

    public boolean canAccessGeneralResources() {
        return this == USUARIO_SUPREMO
                || this == PROPRIETARIO
                || this == GERENTE
                || this == AGRONOMO_RESIDENTE
                || this == AGRONOMO_CONSULTOR
                || this == SECRETARIO
                || this == SUPERVISOR_DE_AREA;
    }

    public boolean canPrintRecommendations() {
        return this == USUARIO_SUPREMO || this == AGRONOMO_RESIDENTE || this == AGRONOMO_CONSULTOR;
    }
}
