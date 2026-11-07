package br.com.ecorota.service;

import br.com.ecorota.dto.PedidoRequest;
import br.com.ecorota.dto.PedidoResponse;
import br.com.ecorota.entity.Colheita;
import br.com.ecorota.entity.Pedido;
import br.com.ecorota.exception.RecursoNaoEncontradoException;
import br.com.ecorota.exception.RegraNegocioException;
import br.com.ecorota.repository.PedidoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Regras de negocio do pedido PNAE:
 * - a quantidade pedida nao pode exceder o disponivel na colheita;
 * - o pedido (exceto CANCELADO) reserva o estoque da colheita;
 * - valorTotal = quantidade x preco unitario da colheita.
 */
@Service
public class PedidoService {

    private final PedidoRepository repository;
    private final EscolaService escolaService;
    private final ColheitaService colheitaService;
    private final ModelMapper mapper;

    public PedidoService(PedidoRepository repository, EscolaService escolaService,
                         ColheitaService colheitaService, ModelMapper mapper) {
        this.repository = repository;
        this.escolaService = escolaService;
        this.colheitaService = colheitaService;
        this.mapper = mapper;
    }

    public List<PedidoResponse> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public PedidoResponse buscar(Long id) {
        return toResponse(buscarEntidade(id));
    }

    @Transactional
    public PedidoResponse criar(PedidoRequest req) {
        return salvar(new Pedido(), req);
    }

    @Transactional
    public PedidoResponse atualizar(Long id, PedidoRequest req) {
        Pedido pedido = buscarEntidade(id);
        devolverEstoque(pedido);
        return salvar(pedido, req);
    }

    @Transactional
    public void excluir(Long id) {
        Pedido pedido = buscarEntidade(id);
        devolverEstoque(pedido);
        repository.delete(pedido);
    }

    private PedidoResponse salvar(Pedido pedido, PedidoRequest req) {
        Colheita colheita = colheitaService.buscarEntidade(req.getColheitaId());
        Pedido.Status status = req.getStatus() != null ? req.getStatus() : Pedido.Status.PENDENTE;

        if (status != Pedido.Status.CANCELADO) {
            if (req.getQuantidade() > colheita.getQuantidade()) {
                throw new RegraNegocioException("Quantidade indisponivel: a colheita possui apenas "
                        + colheita.getQuantidade() + " " + colheita.getUnidade());
            }
            colheita.setQuantidade(colheita.getQuantidade() - req.getQuantidade());
        }

        mapper.map(req, pedido); // relacionamentos sao ignorados pelo mapper (ver EcoRotaApplication#modelMapper)
        pedido.setStatus(status);
        pedido.setEscola(escolaService.buscarEntidade(req.getEscolaId()));
        pedido.setColheita(colheita);
        pedido.setValorTotal(colheita.getPrecoUnitario().multiply(BigDecimal.valueOf(req.getQuantidade())));
        return toResponse(repository.save(pedido));
    }

    private void devolverEstoque(Pedido pedido) {
        if (pedido.getStatus() != Pedido.Status.CANCELADO) {
            Colheita c = pedido.getColheita();
            c.setQuantidade(c.getQuantidade() + pedido.getQuantidade());
        }
    }

    private Pedido buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Pedido", id));
    }

    private PedidoResponse toResponse(Pedido p) {
        return mapper.map(p, PedidoResponse.class);
    }
}
