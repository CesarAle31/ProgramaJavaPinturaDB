-- =====================================================
-- ALTERACIONES A LA TABLA 'pintura'
-- Objetivo: Mejorar estructura, corregir datos y agregar validaciones
-- =====================================================

-- =====================================================
-- PASO 1: CORRECCIÓN DE DATOS INVÁLIDOS
-- Antes de modificar la estructura, corregir valores problemáticos
-- =====================================================

-- Corregir capacidades nulas, vacías o inválidas
UPDATE pintura
SET capacidad = 1
WHERE capacidad IS NULL
   OR capacidad = ''
   OR CAST(capacidad AS DECIMAL(10,2)) <= 0;

-- Verificar los datos corregidos (opcional, para verificación)
-- SELECT idPintura, nombre, capacidad FROM pintura WHERE capacidad <= 0 OR capacidad IS NULL;


-- =====================================================
-- PASO 2: MODIFICAR ESTRUCTURA DE LA TABLA
-- =====================================================

-- 2.1: Cambiar capacidad de VARCHAR(100) a INT NOT NULL
ALTER TABLE pintura
MODIFY capacidad INT NOT NULL;

-- 2.2: Hacer campos obligatorios (NOT NULL)
ALTER TABLE pintura
MODIFY nombre VARCHAR(100) NOT NULL,
MODIFY color VARCHAR(100) NOT NULL,
MODIFY stock INT NOT NULL,
MODIFY costo DECIMAL(15,2) NOT NULL,
MODIFY presentacion VARCHAR(50) NOT NULL;


-- =====================================================
-- PASO 3: AGREGAR CONSTRAINTS DE VALIDACIÓN (CHECK)
-- =====================================================

-- CHECK: capacidad debe ser > 0
ALTER TABLE pintura
ADD CONSTRAINT chk_pintura_capacidad CHECK (capacidad > 0);

-- CHECK: stock debe ser >= 0
ALTER TABLE pintura
ADD CONSTRAINT chk_pintura_stock CHECK (stock >= 0);

-- CHECK: costo debe ser > 0
ALTER TABLE pintura
ADD CONSTRAINT chk_pintura_costo CHECK (costo > 0);

-- CHECK: nombre no debe estar vacío
ALTER TABLE pintura
ADD CONSTRAINT chk_pintura_nombre_not_empty CHECK (TRIM(nombre) != '');

-- CHECK: color no debe estar vacío
ALTER TABLE pintura
ADD CONSTRAINT chk_pintura_color_not_empty CHECK (TRIM(color) != '');

-- CHECK: presentacion no debe estar vacía
ALTER TABLE pintura
ADD CONSTRAINT chk_pintura_presentacion_not_empty CHECK (TRIM(presentacion) != '');


-- =====================================================
-- PASO 4: VERIFICACIÓN DE LA ESTRUCTURA ACTUALIZADA
-- =====================================================

-- Consultar la estructura de la tabla actualizada (descomenta para verificar)
-- DESCRIBE pintura;

-- Consultar las constraints creadas
-- SELECT CONSTRAINT_NAME, TABLE_NAME, CONSTRAINT_TYPE
-- FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
-- WHERE TABLE_NAME = 'pintura' AND TABLE_SCHEMA = 'IPESAPinturas';

-- =====================================================
-- ESTRUCTURA FINAL DE LA TABLA 'pintura'
-- =====================================================
-- idPintura: int(10) unsigned AUTO_INCREMENT PRIMARY KEY
-- claveClasificacion: int(10) unsigned
-- idProveedor: int(10) unsigned
-- nombre: varchar(100) NOT NULL (no vacío)
-- color: varchar(100) NOT NULL (no vacío)
-- capacidad: int NOT NULL (> 0)                    <-- CAMBIO
-- stock: int NOT NULL (>= 0)
-- costo: decimal(15,2) NOT NULL (> 0)
-- presentacion: varchar(50) NOT NULL (no vacío)
--
-- Constraints:
-- ✓ chk_pintura_capacidad: capacidad > 0
-- ✓ chk_pintura_stock: stock >= 0
-- ✓ chk_pintura_costo: costo > 0
-- ✓ chk_pintura_nombre_not_empty: nombre sin espacios vacío
-- ✓ chk_pintura_color_not_empty: color sin espacios vacío
-- ✓ chk_pintura_presentacion_not_empty: presentacion sin espacios vacío

-- =====================================================
-- NOTAS IMPORTANTES
-- =====================================================
-- 1. Los procedimientos almacenados siguen siendo válidos
-- 2. Actualiza ProductoDAO.java:
--    - capacidad ahora es INT, no String
--    - Cambia: setString(capacidad) → setInt(capacidad)
-- 3. Los triggers BEFORE INSERT/UPDATE seguirán validando
-- 4. Las CHECKs de MariaDB validarán a nivel de BD
-- 5. La tabla ahora es más robusta y segura