package br.com.ecorota.dto;

import br.com.ecorota.entity.Pedido;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PedidoRequest {
    @NotNull
    private Long escolaId;
    @NotNull
    private Long colheitaId;
    @NotNull @Positive
    private Integer quantidade;
    @NotNull @FutureOrPresent
    private LocalDate dataEntrega;
    /** Opcional; padrao PENDENTE. */
    private Pedido.Status status;
}
