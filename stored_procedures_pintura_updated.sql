-- =====================================================
-- PROCEDIMIENTOS ALMACENADOS PARA TABLA 'pintura' (ACTUALIZADO)
-- Base de Datos: IPESAPinturas
-- Motor: MariaDB
-- CAMBIO: capacidad ahora es INT (antes VARCHAR(100))
-- =====================================================

-- =====================================================
-- PROCEDIMIENTO 1: sp_agregar_pintura (ACTUALIZADO)
-- Propósito: Insertar un nuevo registro en la tabla pintura
-- Parámetros de entrada: Todos excepto idPintura (autoincrement)
-- =====================================================
DELIMITER //

DROP PROCEDURE IF EXISTS sp_agregar_pintura//

CREATE PROCEDURE sp_agregar_pintura(
    IN p_claveClasificacion INT UNSIGNED,
    IN p_idProveedor INT UNSIGNED,
    IN p_nombre VARCHAR(100),
    IN p_color VARCHAR(100),
    IN p_capacidad INT,                         -- CAMBIO: INT (antes VARCHAR(100))
    IN p_stock INT,
    IN p_costo DECIMAL(15,2),
    IN p_presentacion VARCHAR(50)
)
BEGIN
    INSERT INTO pintura (
        claveClasificacion,
        idProveedor,
        nombre,
        color,
        capacidad,
        stock,
        costo,
        presentacion
    ) VALUES (
        p_claveClasificacion,
        p_idProveedor,
        p_nombre,
        p_color,
        p_capacidad,
        p_stock,
        p_costo,
        p_presentacion
    );
END//

DELIMITER ;


-- =====================================================
-- PROCEDIMIENTO 2: sp_buscar_pintura (SIN CAMBIOS)
-- Propósito: Buscar un registro por idPintura
-- Parámetros de entrada: p_idPintura
-- Salida: SELECT (Result Set)
-- =====================================================
DELIMITER //

DROP PROCEDURE IF EXISTS sp_buscar_pintura//

CREATE PROCEDURE sp_buscar_pintura(
    IN p_idPintura INT UNSIGNED
)
BEGIN
    SELECT *
    FROM pintura
    WHERE idPintura = p_idPintura;
END//

DELIMITER ;


-- =====================================================
-- PROCEDIMIENTO 3: sp_actualizar_pintura (ACTUALIZADO)
-- Propósito: Actualizar un registro existente en pintura
-- Parámetros de entrada: Todos incluyendo p_idPintura
-- =====================================================
DELIMITER //

DROP PROCEDURE IF EXISTS sp_actualizar_pintura//

CREATE PROCEDURE sp_actualizar_pintura(
    IN p_idPintura INT UNSIGNED,
    IN p_claveClasificacion INT UNSIGNED,
    IN p_idProveedor INT UNSIGNED,
    IN p_nombre VARCHAR(100),
    IN p_color VARCHAR(100),
    IN p_capacidad INT,                         -- CAMBIO: INT (antes VARCHAR(100))
    IN p_stock INT,
    IN p_costo DECIMAL(15,2),
    IN p_presentacion VARCHAR(50)
)
BEGIN
    UPDATE pintura
    SET
        claveClasificacion = p_claveClasificacion,
        idProveedor = p_idProveedor,
        nombre = p_nombre,
        color = p_color,
        capacidad = p_capacidad,
        stock = p_stock,
        costo = p_costo,
        presentacion = p_presentacion
    WHERE idPintura = p_idPintura;
END//

DELIMITER ;


-- =====================================================
-- PROCEDIMIENTO 4: sp_eliminar_pintura (SIN CAMBIOS)
-- Propósito: Eliminar un registro por idPintura
-- Parámetros de entrada: p_idPintura
-- Nota: Confía en BEFORE DELETE trigger para validación
-- =====================================================
DELIMITER //

DROP PROCEDURE IF EXISTS sp_eliminar_pintura//

CREATE PROCEDURE sp_eliminar_pintura(
    IN p_idPintura INT UNSIGNED
)
BEGIN
    DELETE FROM pintura
    WHERE idPintura = p_idPintura;
END//

DELIMITER ;


-- =====================================================
-- EJEMPLOS DE USO (DESPUÉS DEL CAMBIO A INT)
-- =====================================================
--
-- 1. Agregar una pintura (capacidad ahora es INT):
-- CALL sp_agregar_pintura(1, 5, 'Pintura Blanca', 'Blanco', 5, 50, 125.75, 'Lata 5L');
--
-- 2. Buscar una pintura por ID:
-- CALL sp_buscar_pintura(1);
--
-- 3. Actualizar una pintura (capacidad ahora es INT):
-- CALL sp_actualizar_pintura(1, 1, 5, 'Pintura Blanca Premium', 'Blanco', 5, 45, 135.00, 'Lata 5L');
--
-- 4. Eliminar una pintura:
-- CALL sp_eliminar_pintura(1);
--
-- =====================================================
