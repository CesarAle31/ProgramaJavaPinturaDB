DROP TRIGGER IF EXISTS trg_validar_presentacion_update;

DELIMITER //

CREATE TRIGGER trg_validar_presentacion_update
    BEFORE UPDATE ON pintura
    FOR EACH ROW
BEGIN
    IF NEW.presentacion NOT IN ('Litro', 'Galón', 'Cubeta') THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La presentación solo puede ser Litro, Galón o Cubeta';
END IF;

IF NEW.presentacion = 'Litro' AND NEW.capacidad <> 1 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Un Litro debe tener capacidad 1';
END IF;

    IF NEW.presentacion = 'Galón' AND NEW.capacidad <> 4 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Un Galón debe tener capacidad 4';
END IF;

    IF NEW.presentacion = 'Cubeta' AND NEW.capacidad <> 19 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Una Cubeta debe tener capacidad 19';
END IF;
END//

DELIMITER ;