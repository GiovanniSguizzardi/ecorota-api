package br.com.ecorota.dto;

import br.com.ecorota.entity.Pedido;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PedidoResponse {
    private Long id;
    private Long escolaId;
    private String escolaNome;
    private Long colheitaId;
    private String colheitaProduto;
    private Integer quantidade;
    private BigDecimal valorTotal;
    private Pedido.Status status;
    private LocalDate dataEntrega;
}
