package br.com.ecorota.service;

import br.com.ecorota.dto.EscolaRequest;
import br.com.ecorota.dto.EscolaResponse;
import br.com.ecorota.entity.Escola;
import br.com.ecorota.exception.RecursoNaoEncontradoException;
import br.com.ecorota.repository.EscolaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EscolaService {

    private final EscolaRepository repository;
    private final ModelMapper mapper;

    public EscolaService(EscolaRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<EscolaResponse> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public EscolaResponse buscar(Long id) {
        return toResponse(buscarEntidade(id));
    }

    @Transactional
    public EscolaResponse criar(EscolaRequest req) {
        return toResponse(repository.save(mapper.map(req, Escola.class)));
    }

    @Transactional
    public EscolaResponse atualizar(Long id, EscolaRequest req) {
        Escola escola = buscarEntidade(id);
        mapper.map(req, escola);
        return toResponse(repository.save(escola));
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    Escola buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Escola", id));
    }

    private EscolaResponse toResponse(Escola e) {
        return mapper.map(e, EscolaResponse.class);
    }
}
