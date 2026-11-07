package br.com.ecorota.service;

import br.com.ecorota.dto.ColheitaRequest;
import br.com.ecorota.dto.ColheitaResponse;
import br.com.ecorota.entity.Colheita;
import br.com.ecorota.exception.RecursoNaoEncontradoException;
import br.com.ecorota.repository.ColheitaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ColheitaService {

    private final ColheitaRepository repository;
    private final ProdutorService produtorService;
    private final ModelMapper mapper;

    public ColheitaService(ColheitaRepository repository, ProdutorService produtorService, ModelMapper mapper) {
        this.repository = repository;
        this.produtorService = produtorService;
        this.mapper = mapper;
    }

    public List<ColheitaResponse> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public ColheitaResponse buscar(Long id) {
        return toResponse(buscarEntidade(id));
    }

    @Transactional
    public ColheitaResponse criar(ColheitaRequest req) {
        return salvar(new Colheita(), req);
    }

    @Transactional
    public ColheitaResponse atualizar(Long id, ColheitaRequest req) {
        return salvar(buscarEntidade(id), req);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    Colheita buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Colheita", id));
    }

    private ColheitaResponse salvar(Colheita colheita, ColheitaRequest req) {
        mapper.map(req, colheita); // o relacionamento produtor e ignorado pelo mapper (ver EcoRotaApplication#modelMapper)
        colheita.setProdutor(produtorService.buscarEntidade(req.getProdutorId()));
        return toResponse(repository.save(colheita));
    }

    private ColheitaResponse toResponse(Colheita c) {
        return mapper.map(c, ColheitaResponse.class);
    }
}
