ALTER TABLE pessoas 

    DROP CONSTRAINT uk_pessoas_cpf,
     MODIFY COLUMN cpf VARCHAR(11) NULL;