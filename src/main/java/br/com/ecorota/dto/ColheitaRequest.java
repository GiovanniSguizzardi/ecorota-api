package br.com.ecorota.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ColheitaRequest {
    @NotBlank @Size(max = 80)
    private String produto;
    @NotNull @Positive
    private Integer quantidade;
    @NotBlank @Size(max = 20)
    private String unidade;
    @NotNull @Positive
    private BigDecimal precoUnitario;
    @NotNull @PastOrPresent
    private LocalDate dataColheita;
    @Positive
    private Integer janelaVendaDias;
    @NotNull
    private Long produtorId;
}
