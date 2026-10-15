package br.com.imbazzu.secretariaTransporte.cadastrais.adapter.out.jpaAdapters;

import br.com.imbazzu.secretariaTransporte.cadastrais.adapter.out.mapper.MotoristaBancoMapper;
import br.com.imbazzu.secretariaTransporte.cadastrais.adapter.out.repositorios.MotoristaRepository;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.Motorista;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.out.MotoristaRepositorioPorta;
import br.com.imbazzu.secretariaTransporte.compartilhados.adapter.outbound.persistence.mapper.PageMapper;
import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class MotoristaJpaAdapter implements MotoristaRepositorioPorta {

    private final MotoristaRepository banco;

    public MotoristaJpaAdapter(MotoristaRepository banco) {
        this.banco = banco;
    }

    @Override
    public UUID salvar(Motorista motorista) {
        var motoristaJpa = MotoristaBancoMapper.paraMotoristaBanco(motorista);
        var motoristaSalvo = banco.save(motoristaJpa);
        return motoristaSalvo.getId();
    }

    @Override
    public boolean verificarExistenciaCpf(String cpf) {
        return banco.existsByCpf(cpf);
    }

    @Override
    public boolean verificarExistenciaCpfIgnorandoId(String cpf, UUID id) {
        return banco.existsByCpfAndIdIgnoreCase(cpf, id);
    }

    @Override
    public Optional<Motorista> buscarPorId(UUID id) {
        return banco.findById(id).map(MotoristaBancoMapper::paraMotorista);
    }

    @Override
    public ResultadoPaginado<Motorista> buscarPorNome(String nome, int pagina, int tamanho) {
        var pageable = PageRequest.of(pagina, tamanho);
        var paginado = banco.buscarPorNome(nome, pageable).map(MotoristaBancoMapper::paraMotorista);
        return PageMapper.toDomain(paginado);
    }

}
