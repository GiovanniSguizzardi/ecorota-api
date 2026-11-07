package br.com.ecorota.controller;

import br.com.ecorota.dto.PedidoRequest;
import br.com.ecorota.dto.PedidoResponse;
import br.com.ecorota.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
@Tag(name = "Pedidos", description = "Compras das escolas sobre as colheitas (PNAE)")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Criar pedido", description = "Reserva o estoque da colheita e calcula o valor total.")
    public ResponseEntity<PedidoResponse> criar(@RequestBody @Valid PedidoRequest req) {
        PedidoResponse criado = service.criar(req);
        return ResponseEntity.created(URI.create("/api/v1/pedidos/" + criado.getId())).body(criado);
    }

    @GetMapping
    @Operation(summary = "Listar pedidos")
    public List<PedidoResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar pedido por id")
    public PedidoResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar pedido", description = "Status CANCELADO devolve o estoque a colheita.")
    public PedidoResponse atualizar(@PathVariable Long id, @RequestBody @Valid PedidoRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir pedido", description = "Devolve o estoque a colheita.")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
