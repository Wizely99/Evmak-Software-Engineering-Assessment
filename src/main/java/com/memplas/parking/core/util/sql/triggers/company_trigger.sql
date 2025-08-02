-- V1__create_company_trigger.sql

CREATE OR REPLACE FUNCTION check_single_admin()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.isAdmin = true THEN
        IF EXISTS (SELECT 1 FROM company WHERE isAdmin = true) THEN
            RAISE EXCEPTION 'Only one company can be marked as admin';
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER enforce_single_admin
BEFORE INSERT OR UPDATE ON company
FOR EACH ROW
EXECUTE FUNCTION check_single_admin();
