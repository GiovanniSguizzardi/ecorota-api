package br.com.ecorota.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProdutorRequest {
    @NotBlank @Size(max = 120)
    private String nome;
    @NotBlank @Pattern(regexp = "[0-9]{11}", message = "deve conter 11 digitos")
    private String cpf;
    @Size(max = 30)
    private String numeroCaf;
    @NotBlank @Size(max = 20)
    private String telefone;
    @NotBlank @Size(max = 80)
    private String municipio;
}
