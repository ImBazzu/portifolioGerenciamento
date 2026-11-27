package br.com.imbazzu.secretariaTransporte.lista;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

@Testcontainers(disabledWithoutDocker = true)
class MigracaoListasEspeciaisTest {
    @Container
    private static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.0");

    @Test
    void preservaListasERegrasExistentesAoMigrar() {
        var fonte = new DriverManagerDataSource(MYSQL.getJdbcUrl(), MYSQL.getUsername(), MYSQL.getPassword());
        Flyway.configure().dataSource(fonte).target("30").load().migrate();
        var jdbc = new JdbcTemplate(fonte);
        var alta = UUID.randomUUID().toString();
        var emergencia = UUID.randomUUID().toString();
        var regra = UUID.randomUUID().toString();
        jdbc.update("INSERT INTO listas (id,nome,descricao) VALUES (UUID_TO_BIN(?),?,?)",
                alta, "Alta", "Preservar alta");
        jdbc.update("INSERT INTO listas (id,nome,descricao) VALUES (UUID_TO_BIN(?),?,?)",
                emergencia, " EMERGENCIA ", "Preservar emergência");
        jdbc.update("INSERT INTO regras_cadastro (id,lista_id,obrigacao_anexo,descricao) VALUES (UUID_TO_BIN(?),UUID_TO_BIN(?),0,?)",
                regra, emergencia, "Regra existente");

        Flyway.configure().dataSource(fonte).load().migrate();

        assertEquals(alta, jdbc.queryForObject(
                "SELECT BIN_TO_UUID(id) FROM listas WHERE finalidade_especial='ALTA'", String.class));
        assertEquals(emergencia, jdbc.queryForObject(
                "SELECT BIN_TO_UUID(id) FROM listas WHERE finalidade_especial='EMERGENCIA'", String.class));
        assertEquals(2, jdbc.queryForObject("SELECT COUNT(*) FROM listas", Integer.class));
        assertEquals(1, jdbc.queryForObject(
                "SELECT COUNT(*) FROM regras_cadastro WHERE lista_id=UUID_TO_BIN(?) AND descricao='Regra existente'",
                Integer.class, emergencia));
    }
}
