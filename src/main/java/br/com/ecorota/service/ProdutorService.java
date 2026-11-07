package br.com.ecorota.service;

import br.com.ecorota.dto.ProdutorRequest;
import br.com.ecorota.dto.ProdutorResponse;
import br.com.ecorota.entity.Produtor;
import br.com.ecorota.exception.RecursoNaoEncontradoException;
import br.com.ecorota.repository.ProdutorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProdutorService {

    private final ProdutorRepository repository;
    private final ModelMapper mapper;

    public ProdutorService(ProdutorRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<ProdutorResponse> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public ProdutorResponse buscar(Long id) {
        return toResponse(buscarEntidade(id));
    }

    @Transactional
    public ProdutorResponse criar(ProdutorRequest req) {
        return toResponse(repository.save(mapper.map(req, Produtor.class)));
    }

    @Transactional
    public ProdutorResponse atualizar(Long id, ProdutorRequest req) {
        Produtor produtor = buscarEntidade(id);
        mapper.map(req, produtor);
        return toResponse(repository.save(produtor));
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    Produtor buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Produtor", id));
    }

    private ProdutorResponse toResponse(Produtor p) {
        return mapper.map(p, ProdutorResponse.class);
    }
}
