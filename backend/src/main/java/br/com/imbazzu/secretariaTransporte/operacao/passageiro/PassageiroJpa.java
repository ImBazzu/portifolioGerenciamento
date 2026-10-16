package br.com.imbazzu.secretariaTransporte.operacao.passageiro;

import java.time.LocalTime;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "passageiros")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PassageiroJpa {

@Id
@Column(name = "id", columnDefinition = "BINARY(16)", updatable = false,
nullable = false)
private UUID id;

@Column(name = "destino_id", nullable = false)
@Setter
private UUID destinoId;

@Column (name="veiculo_id", nullable=false)
private UUID veiculoId;


@Column(name = "acompanhantes", columnDefinition = "INT", nullable = false)
private int acompanhantes;

@Column(name = "hora_chegada", columnDefinition = "TIME", nullable = false)
@Setter
private LocalTime horaChegada;

@Column(name = "buscar", columnDefinition = "BIT(1)", nullable = false)
@Setter
private boolean buscar;

@Enumerated(EnumType.STRING)
@Column(name = "status", nullable = false, columnDefinition = "VARCHAR(20)")
@Setter
private StatusPassageiro status;

public PassageiroJpa(UUID destinoId, int acompanhante,
LocalTime horaChegada, boolean buscar) {
if (acompanhante < 0) {
throw new DadosInvalidosException("Acompanhantes não pode ser negativo");
}
this.destinoId = destinoId;
this.acompanhantes = acompanhante;
this.horaChegada = horaChegada;
this.buscar = buscar;
this.status = StatusPassageiro.EM_ESPERA;
}


public int quantidadeTotalPassageiros() {
return getAcompanhantes() + 1;
}

public void setAcompanhantes(int acompanhantes) {
if (acompanhantes < 0) {
throw new DadosInvalidosException("Acompanhantes não pode ser negativo");
}
this.acompanhantes = acompanhantes;
}

}
