-- =============================================================================
-- SERVICIUDAD CALI - Schema Database
-- Tabla: facturas_acueducto
-- Descripcion: Almacena las facturas del servicio de acueducto consolidadas
-- =============================================================================

DROP TABLE IF EXISTS facturas_acueducto CASCADE;

CREATE TABLE facturas_acueducto (
    id                  BIGSERIAL PRIMARY KEY,
    id_cliente          VARCHAR(10) NOT NULL,
    periodo             VARCHAR(6) NOT NULL,
    consumo_m3          INTEGER NOT NULL CHECK (consumo_m3 >= 0),
    valor_pagar         DECIMAL(12,2) NOT NULL CHECK (valor_pagar >= 0),
    estado              VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    fecha_vencimiento   DATE,
    fecha_creacion      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT uk_cliente_periodo UNIQUE (id_cliente, periodo),
    CONSTRAINT chk_estado CHECK (estado IN ('PENDIENTE', 'PAGADA', 'VENCIDA', 'ANULADA'))
);

CREATE INDEX idx_facturas_id_cliente ON facturas_acueducto(id_cliente);
CREATE INDEX idx_facturas_periodo ON facturas_acueducto(periodo);
CREATE INDEX idx_facturas_estado ON facturas_acueducto(estado);
CREATE INDEX idx_facturas_fecha_vencimiento ON facturas_acueducto(fecha_vencimiento);

COMMENT ON TABLE facturas_acueducto IS 'Facturas consolidadas del servicio de acueducto';
COMMENT ON COLUMN facturas_acueducto.id IS 'Identificador unico de la factura';
COMMENT ON COLUMN facturas_acueducto.id_cliente IS 'Identificador del cliente (10 digitos)';
COMMENT ON COLUMN facturas_acueducto.periodo IS 'Periodo de facturacion YYYYMM';
COMMENT ON COLUMN facturas_acueducto.consumo_m3 IS 'Consumo en metros cubicos';
COMMENT ON COLUMN facturas_acueducto.valor_pagar IS 'Valor a pagar en pesos colombianos';
COMMENT ON COLUMN facturas_acueducto.estado IS 'Estado de la factura: PENDIENTE, PAGADA, VENCIDA, ANULADA';
COMMENT ON COLUMN facturas_acueducto.fecha_vencimiento IS 'Fecha limite de pago';
COMMENT ON COLUMN facturas_acueducto.fecha_creacion IS 'Fecha de creacion del registro';
COMMENT ON COLUMN facturas_acueducto.fecha_actualizacion IS 'Fecha de ultima actualizacion';