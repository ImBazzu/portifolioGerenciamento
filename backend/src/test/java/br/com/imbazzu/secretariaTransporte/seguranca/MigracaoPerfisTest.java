package br.com.imbazzu.secretariaTransporte.seguranca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.sql.DriverManager;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

@Testcontainers(disabledWithoutDocker = true)
class MigracaoPerfisTest {
    @Container private static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.0");

    @Test
    void migraUsuarioAntigoSemAlterarAdministrador() throws Exception {
        Flyway.configure().dataSource(MYSQL.getJdbcUrl(), MYSQL.getUsername(), MYSQL.getPassword())
                .target("23").load().migrate();
        try (var connection = DriverManager.getConnection(MYSQL.getJdbcUrl(), MYSQL.getUsername(), MYSQL.getPassword());
             var statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO usuarios (id,nome,senha) "
                    + "VALUES (UNHEX('00000000000000000000000000000099'),'usuario antigo','senha')");
            statement.executeUpdate("INSERT INTO usuario_permissoes (usuario_id,permissao) "
                    + "VALUES (UNHEX('00000000000000000000000000000099'),'USUARIO')");
            statement.executeUpdate("INSERT INTO cidades (id,nome,estado,tempo_viagem) VALUES "
                    + "(UNHEX('000000000000000000000000000000a1'),'Cidade antiga','SP',0)");
            statement.executeUpdate("INSERT INTO destinos (id,nome,cidade_id) VALUES "
                    + "(UNHEX('000000000000000000000000000000a2'),'Destino antigo',UNHEX('000000000000000000000000000000a1'))");
            statement.executeUpdate("INSERT INTO listas (id,nome) VALUES (UNHEX('000000000000000000000000000000a3'),'Lista antiga')");
            statement.executeUpdate("INSERT INTO tipo_veiculos (id,nome,capacidade) VALUES (UNHEX('000000000000000000000000000000a4'),'Van antiga',8)");
            statement.executeUpdate("INSERT INTO pessoas (id,nome,telefone,especificacao_id) VALUES "
                    + "(UNHEX('000000000000000000000000000000a5'),'Pessoa antiga','11987654321',UNHEX('00000000000000000000000000000001'))");
            statement.executeUpdate("INSERT INTO dias_agendamento (data,aberto_em) VALUES ('2031-01-01',NOW())");
            statement.executeUpdate("INSERT INTO agendamentos (id,data,pessoa_id,lista_id,regra_cadastro_id,tipo_veiculo_id,destino_id,"
                    + "tempo_viagem_segundos,tempo_viagem_nanos,tolerancia_segundos,tolerancia_nanos,hora_chegada,buscar_na_residencia,acompanhantes) VALUES "
                    + "(UNHEX('000000000000000000000000000000a6'),'2031-01-01',UNHEX('000000000000000000000000000000a5'),"
                    + "UNHEX('000000000000000000000000000000a3'),UNHEX('000000000000000000000000000000a3'),"
                    + "UNHEX('000000000000000000000000000000a4'),UNHEX('000000000000000000000000000000a2'),0,0,0,0,'10:00',0,0)");
        }
        Flyway.configure().dataSource(MYSQL.getJdbcUrl(), MYSQL.getUsername(), MYSQL.getPassword()).load().migrate();
        try (var connection = DriverManager.getConnection(MYSQL.getJdbcUrl(), MYSQL.getUsername(), MYSQL.getPassword());
             var statement = connection.createStatement()) {
            try (var result = statement.executeQuery("SELECT p.permissao,u.versao_token FROM usuarios u "
                    + "JOIN usuario_permissoes p ON p.usuario_id=u.id WHERE u.nome='usuario antigo'")) {
                result.next();
                assertEquals("AGENDAMENTO", result.getString(1));
                assertEquals(0, result.getLong(2));
            }
            try (var result = statement.executeQuery("SELECT p.permissao FROM usuarios u "
                    + "JOIN usuario_permissoes p ON p.usuario_id=u.id WHERE u.nome='admin'")) {
                result.next();
                assertEquals("ADMIN", result.getString(1));
            }
            try (var result = statement.executeQuery("SELECT "
                    + "(SELECT COUNT(*) FROM pessoas WHERE autor_id IS NULL AND nome='Pessoa antiga'),"
                    + "(SELECT COUNT(*) FROM agendamentos WHERE autor_id IS NULL AND id=UNHEX('000000000000000000000000000000a6'))")) {
                result.next();
                assertEquals(1, result.getInt(1));
                assertEquals(1, result.getInt(2));
            }
        }
    }
}
