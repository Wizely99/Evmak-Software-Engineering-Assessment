DELIMITER $$

CREATE TRIGGER enforce_single_admin_update
BEFORE UPDATE ON company
FOR EACH ROW
BEGIN
    -- Only validate if the admin flag is changing to TRUE
    IF NEW.isAdmin = TRUE AND OLD.isAdmin = FALSE THEN
        DECLARE admin_count INT;
        SELECT COUNT(*) INTO admin_count FROM company WHERE isAdmin = TRUE;

        IF admin_count > 0 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Only one company can be marked as admin';
        END IF;
    END IF;
END$$

DELIMITER ;
