package br.com.imbazzu.secretariaTransporte.lista;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void limparBancoDeTeste() {
        Flyway.configure().dataSource(MYSQL.getJdbcUrl(), MYSQL.getUsername(), MYSQL.getPassword())
                .cleanDisabled(false).load().clean();
    }

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
        assertEquals(3, jdbc.queryForObject("SELECT COUNT(*) FROM listas", Integer.class));
        assertEquals(1, jdbc.queryForObject(
                "SELECT COUNT(*) FROM listas WHERE nome='Encaixe' AND finalidade_especial='ENCAIXE'",
                Integer.class));
        assertEquals(1, jdbc.queryForObject(
                "SELECT COUNT(*) FROM regras_cadastro r JOIN listas l ON r.lista_id=l.id "
                        + "WHERE l.finalidade_especial='ENCAIXE'", Integer.class));
        assertEquals(1, jdbc.queryForObject(
                "SELECT COUNT(*) FROM regras_cadastro WHERE lista_id=UUID_TO_BIN(?) AND descricao='Regra existente'",
                Integer.class, emergencia));
    }

    @Test
    void aproveitaListaEncaixeExistenteSemAlterarSuasRegras() {
        var fonte = new DriverManagerDataSource(MYSQL.getJdbcUrl(), MYSQL.getUsername(), MYSQL.getPassword());
        Flyway.configure().dataSource(fonte).target("31").load().migrate();
        var jdbc = new JdbcTemplate(fonte);
        var encaixe = UUID.randomUUID().toString();
        var regra = UUID.randomUUID().toString();
        jdbc.update("INSERT INTO listas (id,nome,descricao) VALUES (UUID_TO_BIN(?),?,?)",
                encaixe, " Encaixe ", "Lista existente");
        jdbc.update("INSERT INTO regras_cadastro (id,lista_id,obrigacao_anexo,descricao) VALUES (UUID_TO_BIN(?),UUID_TO_BIN(?),0,?)",
                regra, encaixe, "Regra existente");

        Flyway.configure().dataSource(fonte).load().migrate();

        assertEquals(encaixe, jdbc.queryForObject(
                "SELECT BIN_TO_UUID(id) FROM listas WHERE finalidade_especial='ENCAIXE'", String.class));
        assertEquals(1, jdbc.queryForObject(
                "SELECT COUNT(*) FROM regras_cadastro WHERE lista_id=UUID_TO_BIN(?) AND descricao='Regra existente'",
                Integer.class, encaixe));
        assertEquals(1, jdbc.queryForObject(
                "SELECT COUNT(*) FROM regras_cadastro WHERE lista_id=UUID_TO_BIN(?)", Integer.class, encaixe));
    }
}
