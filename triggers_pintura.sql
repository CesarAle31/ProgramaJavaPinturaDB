-- =====================================================
-- TRIGGERS PARA VALIDACIÓN DE LA TABLA 'pintura'
-- Base de Datos: IPESAPinturas
-- Motor: MariaDB
-- =====================================================

-- =====================================================
-- TRIGGER 1: VALIDACIÓN ANTES DE INSERT
-- =====================================================
DELIMITER //

DROP TRIGGER IF EXISTS trg_validar_pintura_insert//

CREATE TRIGGER trg_validar_pintura_insert
BEFORE INSERT ON pintura
FOR EACH ROW
BEGIN
    -- Validar que nombre no sea vacío o nulo
    IF TRIM(NEW.nombre) IS NULL OR TRIM(NEW.nombre) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: El nombre de la pintura no puede ser vacío o nulo';
    END IF;

    -- Validar que color no sea vacío o nulo
    IF TRIM(NEW.color) IS NULL OR TRIM(NEW.color) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: El color no puede ser vacío o nulo';
    END IF;

    -- Validar capacidad: debe ser un número válido y mayor a 0
    IF NEW.capacidad IS NULL OR NEW.capacidad = '' OR NOT NEW.capacidad REGEXP '^[0-9]+(\.[0-9]+)?$' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: La capacidad debe ser un número válido mayor a 0';
    ELSEIF CAST(NEW.capacidad AS DECIMAL(15,2)) <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: La capacidad debe ser mayor a 0';
    END IF;

    -- Validar que stock sea mayor o igual a 0
    IF NEW.stock < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: El stock no puede ser negativo';
    END IF;

    -- Validar que costo sea mayor a 0
    IF NEW.costo <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: El costo debe ser mayor a 0';
    END IF;

END//

DELIMITER ;


-- =====================================================
-- TRIGGER 2: VALIDACIÓN ANTES DE UPDATE
-- =====================================================
DELIMITER //

DROP TRIGGER IF EXISTS trg_validar_pintura_update//

CREATE TRIGGER trg_validar_pintura_update
BEFORE UPDATE ON pintura
FOR EACH ROW
BEGIN
    -- Validar que nombre no sea vacío o nulo
    IF TRIM(NEW.nombre) IS NULL OR TRIM(NEW.nombre) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: El nombre de la pintura no puede ser vacío o nulo';
    END IF;

    -- Validar que color no sea vacío o nulo
    IF TRIM(NEW.color) IS NULL OR TRIM(NEW.color) = '' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: El color no puede ser vacío o nulo';
    END IF;

    -- Validar capacidad: debe ser un número válido y mayor a 0
    IF NEW.capacidad IS NULL OR NEW.capacidad = '' OR NOT NEW.capacidad REGEXP '^[0-9]+(\.[0-9]+)?$' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: La capacidad debe ser un número válido mayor a 0';
    ELSEIF CAST(NEW.capacidad AS DECIMAL(15,2)) <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: La capacidad debe ser mayor a 0';
    END IF;

    -- Validar que stock sea mayor o igual a 0
    IF NEW.stock < 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: El stock no puede ser negativo';
    END IF;

    -- Validar que costo sea mayor a 0
    IF NEW.costo <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: El costo debe ser mayor a 0';
    END IF;

END//

DELIMITER ;


-- =====================================================
-- NOTAS IMPORTANTES:
-- =====================================================
-- 1. Los triggers validan ambas operaciones (INSERT y UPDATE)
-- 2. La validación de capacidad usa REGEXP antes del CAST para evitar errores de conversión
-- 3. Todos los errores usan SQLSTATE '45000' (condición general de error)
-- 4. Los mensajes son claros y específicos para cada validación
-- 5. Se valida TRIM en nombre y color para eliminar espacios en blanco
-- 6. Stock acepta 0, costo no (debe ser > 0)
-- =====================================================
