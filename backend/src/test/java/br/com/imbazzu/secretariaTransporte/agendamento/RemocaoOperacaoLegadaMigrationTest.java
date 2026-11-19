package br.com.imbazzu.secretariaTransporte.agendamento;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.DriverManager;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

@Testcontainers(disabledWithoutDocker = true)
class RemocaoOperacaoLegadaMigrationTest {
    @Container
    private static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.0");

    @Test
    void removeTabelasLegadasSemRemoverAgenda() throws Exception {
        Flyway.configure().dataSource(MYSQL.getJdbcUrl(), MYSQL.getUsername(), MYSQL.getPassword())
                .locations("classpath:db/migration").target("20").load().migrate();
        try (var conn = DriverManager.getConnection(MYSQL.getJdbcUrl(), MYSQL.getUsername(), MYSQL.getPassword());
             var sql = conn.createStatement()) {
            sql.executeUpdate("INSERT INTO dias_agendamento (data,aberto_em) VALUES ('2030-01-01',NOW())");
            sql.executeUpdate("INSERT INTO cidades (id,nome,estado,tempo_viagem) VALUES (UNHEX('00000000000000000000000000000011'),'Cidade teste','SP',0)");
            sql.executeUpdate("INSERT INTO destinos (id,nome,cidade_id) VALUES (UNHEX('00000000000000000000000000000012'),'Destino teste',UNHEX('00000000000000000000000000000011'))");
            sql.executeUpdate("INSERT INTO listas (id,nome) VALUES (UNHEX('00000000000000000000000000000013'),'Lista teste')");
            sql.executeUpdate("INSERT INTO tipo_veiculos (id,nome,capacidade) VALUES (UNHEX('00000000000000000000000000000014'),'Tipo teste',4)");
            sql.executeUpdate("INSERT INTO pessoas (id,nome,telefone,especificacao_id) VALUES (UNHEX('00000000000000000000000000000015'),'Pessoa teste','11987654321',UNHEX('00000000000000000000000000000001'))");
            sql.executeUpdate("INSERT INTO veiculos (id,id_tipo_veiculo,data_saida,hora_saida,qtd_passageiro) VALUES "
                    + "(UNHEX('00000000000000000000000000000003'),UNHEX('00000000000000000000000000000014'),'2030-01-01','09:00',1)");
            sql.executeUpdate("INSERT INTO passageiros (id,destino_id,veiculo_id,acompanhantes,hora_chegada,buscar,status) VALUES "
                    + "(UNHEX('00000000000000000000000000000001'),UNHEX('00000000000000000000000000000002'),UNHEX('00000000000000000000000000000003'),0,'10:00',0,'EM_ESPERA')");
            sql.executeUpdate("INSERT INTO agendamentos (id,data,pessoa_id,lista_id,regra_cadastro_id,tipo_veiculo_id,destino_id,"
                    + "tempo_viagem_segundos,tempo_viagem_nanos,tolerancia_segundos,tolerancia_nanos,hora_chegada,buscar_na_residencia,acompanhantes) VALUES "
                    + "(UNHEX('00000000000000000000000000000016'),'2030-01-01',UNHEX('00000000000000000000000000000015'),UNHEX('00000000000000000000000000000013'),UNHEX('00000000000000000000000000000013'),UNHEX('00000000000000000000000000000014'),UNHEX('00000000000000000000000000000012'),0,0,0,0,'10:00',0,1)");
        }

        Flyway.configure().dataSource(MYSQL.getJdbcUrl(), MYSQL.getUsername(), MYSQL.getPassword())
                .locations("classpath:db/migration").load().migrate();
        try (var conn = DriverManager.getConnection(MYSQL.getJdbcUrl(), MYSQL.getUsername(), MYSQL.getPassword());
             var sql = conn.createStatement()) {
            try (var rows = sql.executeQuery("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema=DATABASE() AND table_name IN ('passageiros','veiculos')")) {
                rows.next();
                assertEquals(0, rows.getInt(1));
            }
            try (var rows = sql.executeQuery("SELECT COUNT(*) FROM dias_agendamento WHERE data='2030-01-01'")) {
                rows.next();
                assertEquals(1, rows.getInt(1));
            }
            try (var rows = sql.executeQuery("SELECT acompanhantes FROM agendamentos WHERE id=UNHEX('00000000000000000000000000000016')")) {
                rows.next();
                assertEquals(1, rows.getInt(1));
            }
        }
    }
}
