USE pinturadb;

DROP TRIGGER IF EXISTS trg_prevenir_eliminar_pintura;

DELIMITER //

CREATE TRIGGER trg_prevenir_eliminar_pintura
    BEFORE DELETE ON pintura
    FOR EACH ROW
BEGIN
    -- Verificar si el ID de la pintura que se intenta borrar ya existe en la tabla ticket
    IF EXISTS (
        SELECT 1
        FROM ticket
        WHERE idPintura = OLD.idPintura
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'No se puede eliminar la pintura porque ya tiene ventas registradas';
END IF;
END//

DELIMITER ;