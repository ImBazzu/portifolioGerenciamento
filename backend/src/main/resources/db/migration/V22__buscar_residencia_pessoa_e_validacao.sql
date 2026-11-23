-- Preserve the existing pickup behavior while moving its source from priority to person.
UPDATE pessoas pessoa
JOIN especificacoes especificacao ON especificacao.id = pessoa.especificacao_id
JOIN prioridades prioridade ON prioridade.id = especificacao.prioridade_id
SET pessoa.buscar_na_residencia = prioridade.buscar_na_residencia;

ALTER TABLE pessoas
    ADD COLUMN validada BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE prioridades
    DROP COLUMN buscar_na_residencia;
