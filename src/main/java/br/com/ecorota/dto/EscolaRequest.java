package br.com.ecorota.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class EscolaRequest {
    @NotBlank @Size(max = 150)
    private String nome;
    @NotBlank @Pattern(regexp = "[0-9]{8}", message = "deve conter 8 digitos")
    private String codigoInep;
    @NotBlank @Size(max = 80)
    private String municipio;
    @PositiveOrZero
    private Integer numeroAlunos;
    @NotNull @PositiveOrZero
    private BigDecimal orcamentoPnae;
    @Email @Size(max = 120)
    private String email;
}
