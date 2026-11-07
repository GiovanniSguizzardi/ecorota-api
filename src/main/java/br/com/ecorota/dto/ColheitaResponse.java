package br.com.ecorota.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ColheitaResponse {
    private Long id;
    private String produto;
    private Integer quantidade;
    private String unidade;
    private BigDecimal precoUnitario;
    private LocalDate dataColheita;
    private Integer janelaVendaDias;
    private Long produtorId;
    private String produtorNome;
}
