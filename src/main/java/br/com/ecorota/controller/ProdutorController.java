package br.com.ecorota.controller;

import br.com.ecorota.dto.ProdutorRequest;
import br.com.ecorota.dto.ProdutorResponse;
import br.com.ecorota.service.ProdutorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/produtores")
@Tag(name = "Produtores", description = "Produtores da agricultura familiar")
public class ProdutorController {

    private final ProdutorService service;

    public ProdutorController(ProdutorService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Cadastrar produtor")
    public ResponseEntity<ProdutorResponse> criar(@RequestBody @Valid ProdutorRequest req) {
        ProdutorResponse criado = service.criar(req);
        return ResponseEntity.created(URI.create("/api/v1/produtores/" + criado.getId())).body(criado);
    }

    @GetMapping
    @Operation(summary = "Listar produtores")
    public List<ProdutorResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar produtor por id")
    public ProdutorResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar produtor")
    public ProdutorResponse atualizar(@PathVariable Long id, @RequestBody @Valid ProdutorRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir produtor (e suas colheitas)")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
