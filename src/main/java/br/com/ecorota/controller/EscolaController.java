package br.com.ecorota.controller;

import br.com.ecorota.dto.EscolaRequest;
import br.com.ecorota.dto.EscolaResponse;
import br.com.ecorota.service.EscolaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/escolas")
@Tag(name = "Escolas", description = "Escolas e creches compradoras pelo PNAE")
public class EscolaController {

    private final EscolaService service;

    public EscolaController(EscolaService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Cadastrar escola")
    public ResponseEntity<EscolaResponse> criar(@RequestBody @Valid EscolaRequest req) {
        EscolaResponse criada = service.criar(req);
        return ResponseEntity.created(URI.create("/api/v1/escolas/" + criada.getId())).body(criada);
    }

    @GetMapping
    @Operation(summary = "Listar escolas")
    public List<EscolaResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar escola por id")
    public EscolaResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar escola")
    public EscolaResponse atualizar(@PathVariable Long id, @RequestBody @Valid EscolaRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir escola (e seus pedidos)")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
