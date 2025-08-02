DELIMITER $$

CREATE TRIGGER enforce_single_admin_insert
BEFORE INSERT ON company
FOR EACH ROW
BEGIN
    IF NEW.isAdmin = TRUE THEN
        DECLARE admin_count INT;
        SELECT COUNT(*) INTO admin_count FROM company WHERE isAdmin = TRUE;

        IF admin_count > 0 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Only one company can be marked as admin';
        END IF;
    END IF;
END$$

DELIMITER ;
