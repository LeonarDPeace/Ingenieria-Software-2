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

-- ====================================================================
-- INDICES COMPUESTOS OPTIMIZADOS (Performance Improvement)
-- ====================================================================

-- Indice 1: Consultas de facturas por cliente y periodo
-- Mejora rendimiento de: findByClienteIdAndPeriodo()
-- Query pattern: SELECT * FROM facturas WHERE id_cliente = ? AND periodo = ?
CREATE INDEX idx_factura_cliente_periodo ON facturas_acueducto(id_cliente, periodo);

-- Indice 2: Proceso batch de marcar facturas vencidas
-- Mejora rendimiento de: findFacturasPendientes() con paginacion
-- Query pattern: SELECT * FROM facturas WHERE estado = 'PENDIENTE' AND fecha_vencimiento < CURRENT_DATE ORDER BY fecha_vencimiento
CREATE INDEX idx_factura_estado_fechavencimiento ON facturas_acueducto(estado, fecha_vencimiento);

-- Indice 3: Consultas historicas de consumo por cliente
-- Mejora rendimiento de: findByClienteId() con ORDER BY periodo DESC
-- Query pattern: SELECT * FROM facturas WHERE id_cliente = ? ORDER BY periodo DESC
CREATE INDEX idx_factura_cliente_periodo_desc ON facturas_acueducto(id_cliente, periodo DESC);

-- Indice 4: Monitoreo de deudas por estado y cliente
-- Mejora rendimiento de: reportes de deuda consolidada filtrados por estado
-- Query pattern: SELECT * FROM facturas WHERE estado IN ('PENDIENTE', 'VENCIDA') AND id_cliente = ?
CREATE INDEX idx_factura_estado_cliente ON facturas_acueducto(estado, id_cliente);

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

-- =============================================================================
-- Tabla: consumo_energia
-- Descripcion: Almacena los consumos de energia electrica leidos por el sistema
-- =============================================================================

DROP TABLE IF EXISTS consumo_energia CASCADE;

CREATE TABLE consumo_energia (
    id              BIGSERIAL PRIMARY KEY,
    cliente_id      VARCHAR(10) NOT NULL,
    periodo         VARCHAR(6) NOT NULL,
    consumo         DECIMAL(10,2) NOT NULL CHECK (consumo >= 0),
    valor_pagar     DECIMAL(12,2) NOT NULL CHECK (valor_pagar >= 0),
    fecha_lectura   DATE NOT NULL,
    estrato         VARCHAR(2) NOT NULL
);

CREATE INDEX idx_consumo_energia_cliente ON consumo_energia(cliente_id);
CREATE INDEX idx_consumo_energia_periodo ON consumo_energia(periodo);

COMMENT ON TABLE consumo_energia IS 'Consumos de energia electrica leidos del sistema';
COMMENT ON COLUMN consumo_energia.cliente_id IS 'Identificador del cliente (10 digitos)';
COMMENT ON COLUMN consumo_energia.periodo IS 'Periodo de consumo YYYYMM';
COMMENT ON COLUMN consumo_energia.consumo IS 'Consumo en kWh';
COMMENT ON COLUMN consumo_energia.valor_pagar IS 'Valor a pagar en pesos colombianos';
COMMENT ON COLUMN consumo_energia.fecha_lectura IS 'Fecha de lectura del medidor';
COMMENT ON COLUMN consumo_energia.estrato IS 'Estrato socioeconomico del cliente';