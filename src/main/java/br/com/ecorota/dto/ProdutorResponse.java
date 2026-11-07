package br.com.ecorota.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ProdutorResponse {
    private Long id;
    private String nome;
    private String cpf;
    private String numeroCaf;
    private String telefone;
    private String municipio;
    private LocalDate dataCadastro;
}
