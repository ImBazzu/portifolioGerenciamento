package br.com.imbazzu.secretariaTransporte.prioridade;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.DriverManager;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

@Testcontainers(disabledWithoutDocker = true)
class PrioridadeMigrationTest {
    @Container
    private static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.0");

    @Test
    void migraReferenciasEConservaDadosHistoricosDaPessoa() throws Exception {
        Flyway.configure().dataSource(MYSQL.getJdbcUrl(), MYSQL.getUsername(), MYSQL.getPassword())
                .locations("classpath:db/migration").target("17").load().migrate();

        try (var conn = DriverManager.getConnection(MYSQL.getJdbcUrl(), MYSQL.getUsername(), MYSQL.getPassword());
             var sql = conn.createStatement()) {
            sql.executeUpdate("INSERT INTO especificacoes (id,nome,prioridade) VALUES (UNHEX('00000000000000000000000000000002'),'Teste','ALTA')");
            sql.executeUpdate("INSERT INTO listas (id,nome) VALUES (UNHEX('00000000000000000000000000000003'),'Lista')");
            sql.executeUpdate("INSERT INTO regras_cadastro (id,lista_id,obrigacao_anexo) VALUES (UNHEX('00000000000000000000000000000004'),UNHEX('00000000000000000000000000000003'),0)");
            sql.executeUpdate("INSERT INTO regra_cadastro_prioridades (regra_cadastro_id,prioridade) VALUES (UNHEX('00000000000000000000000000000004'),'ALTA')");
            sql.executeUpdate("INSERT INTO pessoas (id,nome,telefone,especificacao_id,buscar_na_residencia,quantidade_acompanhantes) VALUES (UNHEX('00000000000000000000000000000005'),'Pessoa','11987654321',UNHEX('00000000000000000000000000000002'),1,2)");
        }

        Flyway.configure().dataSource(MYSQL.getJdbcUrl(), MYSQL.getUsername(), MYSQL.getPassword())
                .locations("classpath:db/migration").target("20").load().migrate();

        try (var conn = DriverManager.getConnection(MYSQL.getJdbcUrl(), MYSQL.getUsername(), MYSQL.getPassword());
             var sql = conn.createStatement()) {
            sql.executeUpdate("UPDATE prioridades SET buscar_na_residencia=1 WHERE nome='ALTA'");
        }

        Flyway.configure().dataSource(MYSQL.getJdbcUrl(), MYSQL.getUsername(), MYSQL.getPassword())
                .locations("classpath:db/migration").load().migrate();

        try (var conn = DriverManager.getConnection(MYSQL.getJdbcUrl(), MYSQL.getUsername(), MYSQL.getPassword());
             var sql = conn.createStatement()) {
            try (var rows = sql.executeQuery("SELECT p.nome,p.quantidade_acompanhantes FROM especificacoes e JOIN prioridades p ON p.id=e.prioridade_id WHERE e.nome='Teste'")) {
                rows.next();
                assertEquals("ALTA", rows.getString(1));
                assertEquals(0, rows.getInt(2));
            }
            try (var rows = sql.executeQuery("SELECT COUNT(*) FROM regra_cadastro_prioridades r JOIN prioridades p ON p.id=r.prioridade_id WHERE p.nome='ALTA'")) {
                rows.next();
                assertEquals(1, rows.getInt(1));
            }
            try (var rows = sql.executeQuery("SELECT buscar_na_residencia,quantidade_acompanhantes,validada FROM pessoas WHERE nome='Pessoa'")) {
                rows.next();
                assertEquals(true, rows.getBoolean(1));
                assertEquals(2, rows.getInt(2));
                assertEquals(false, rows.getBoolean(3));
            }
            try (var rows = sql.executeQuery("SELECT COUNT(*) FROM information_schema.columns WHERE table_schema=DATABASE() AND table_name='prioridades' AND column_name='buscar_na_residencia'")) {
                rows.next();
                assertEquals(0, rows.getInt(1));
            }
        }
    }
}
