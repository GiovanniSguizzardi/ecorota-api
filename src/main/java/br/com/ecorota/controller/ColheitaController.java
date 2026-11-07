package br.com.ecorota.controller;

import br.com.ecorota.dto.ColheitaRequest;
import br.com.ecorota.dto.ColheitaResponse;
import br.com.ecorota.service.ColheitaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/colheitas")
@Tag(name = "Colheitas", description = "Ofertas de colheita cadastradas pelos produtores")
public class ColheitaController {

    private final ColheitaService service;

    public ColheitaController(ColheitaService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Cadastrar colheita")
    public ResponseEntity<ColheitaResponse> criar(@RequestBody @Valid ColheitaRequest req) {
        ColheitaResponse criada = service.criar(req);
        return ResponseEntity.created(URI.create("/api/v1/colheitas/" + criada.getId())).body(criada);
    }

    @GetMapping
    @Operation(summary = "Listar colheitas")
    public List<ColheitaResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar colheita por id")
    public ColheitaResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar colheita")
    public ColheitaResponse atualizar(@PathVariable Long id, @RequestBody @Valid ColheitaRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir colheita (e seus pedidos)")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
