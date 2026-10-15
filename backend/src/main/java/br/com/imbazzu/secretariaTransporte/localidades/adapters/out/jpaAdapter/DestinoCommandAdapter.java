package br.com.imbazzu.secretariaTransporte.localidades.adapters.out.jpaAdapter;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.infraestrutura.BancoDeDadosException;
import br.com.imbazzu.secretariaTransporte.localidades.adapters.out.mapper.DestinoBancoMapper;
import br.com.imbazzu.secretariaTransporte.localidades.adapters.out.repositorio.DestinoRepository;
import br.com.imbazzu.secretariaTransporte.localidades.core.domain.Destino;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.DestinoCommandPorta;

import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class DestinoCommandAdapter implements DestinoCommandPorta {

    private final DestinoRepository destinoRepository;

    public DestinoCommandAdapter(DestinoRepository destinoRepository) {
        this.destinoRepository = destinoRepository;
    }

    @Override
    public UUID salvar(Destino destino) {
        var destinoJpa = DestinoBancoMapper.paraEntity(destino);
        try {
            var destinoSalvo = destinoRepository.saveAndFlush(destinoJpa);
            return destinoSalvo.getId();
        } catch (Exception e) {
            throw new BancoDeDadosException("Não foi possível salvar o destino no banco de dados", e);
        }
    }

    @Override
    public Optional<Destino> buscarPorId(UUID idDestino) {
        return destinoRepository.findById(idDestino).map(DestinoBancoMapper::paraDomain);
    }

    @Override
    public void deletar(UUID id) {
        destinoRepository.deleteById(id);
    }

}
