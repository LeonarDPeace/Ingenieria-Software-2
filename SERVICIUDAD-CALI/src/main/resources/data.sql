-- =============================================================================
-- SERVICIUDAD CALI - Data SQL
-- Datos iniciales: facturas_acueducto
-- Descripcion: 25 registros de ejemplo para desarrollo y testing
-- Incluye 5 clientes destacados para pruebas completas con datos variados
-- =============================================================================

-- ========================================
-- CLIENTES DE PRUEBA PRINCIPALES (5)
-- Datos completos y variados para testing
-- ========================================
INSERT INTO facturas_acueducto (id_cliente, periodo, consumo_m3, valor_pagar, estado, fecha_vencimiento, fecha_creacion, fecha_actualizacion) VALUES
-- Cliente 1: Juan Pérez García - Consumo bajo, pendiente, próximo a vencer
('1001234567', '202510', 15, 75000.00, 'PENDIENTE', '2025-11-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Cliente 2: María López Castro - Consumo bajo, pagada, buen historial
('1002345678', '202510', 22, 110000.00, 'PAGADA', '2025-10-28', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Cliente 3: Carlos Rodríguez M. - Consumo alto, vencida, mora
('1004567890', '202510', 30, 150000.00, 'VENCIDA', '2025-10-20', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Cliente 4: Ana Martínez Silva - Consumo medio-alto, pendiente
('1006789012', '202510', 25, 125000.00, 'PENDIENTE', '2025-11-18', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Cliente 5: Roberto Gómez Díaz - Consumo medio, vencida, mora crítica
('1000123456', '202510', 35, 175000.00, 'VENCIDA', '2025-10-18', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- ========================================
-- CLIENTES ADICIONALES (15)
-- Datos variados para testing general
-- ========================================
('1003456789', '202510', 18, 90000.00, 'PENDIENTE', '2025-11-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('1005678901', '202510', 12, 60000.00, 'PAGADA', '2025-11-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('1007890123', '202510', 20, 100000.00, 'PAGADA', '2025-11-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('1008901234', '202510', 28, 140000.00, 'PENDIENTE', '2025-11-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('1009012345', '202510', 16, 80000.00, 'PENDIENTE', '2025-11-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('1011234568', '202510', 14, 70000.00, 'PAGADA', '2025-11-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('1012345679', '202510', 21, 105000.00, 'PENDIENTE', '2025-11-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('1013456780', '202510', 19, 95000.00, 'PAGADA', '2025-11-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('1014567891', '202510', 26, 130000.00, 'PENDIENTE', '2025-11-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('1015678902', '202510', 17, 85000.00, 'PENDIENTE', '2025-11-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('1016789013', '202510', 23, 115000.00, 'PAGADA', '2025-11-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('1017890124', '202510', 13, 65000.00, 'PENDIENTE', '2025-11-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('1018901235', '202510', 29, 145000.00, 'VENCIDA', '2025-11-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('1019012346', '202510', 24, 120000.00, 'PAGADA', '2025-11-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('1010123457', '202510', 27, 135000.00, 'PENDIENTE', '2025-11-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
