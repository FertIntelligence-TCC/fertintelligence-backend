ALTER TABLE usuarios
DROP CONSTRAINT IF EXISTS usuarios_cargo_check;

ALTER TABLE usuarios
ADD CONSTRAINT usuarios_cargo_check
CHECK (
    cargo IN (
        'PROPRIETARIO',
        'GERENTE',
        'AGRONOMO_CONSULTOR',
        'AGRONOMO_RESIDENTE',
        'SUPERVISOR_AREA',
        'SECRETARIO',
        'USUARIO_SUPREMO'
    )
);
