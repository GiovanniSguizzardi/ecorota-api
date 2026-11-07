package br.com.ecorota.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class EscolaResponse {
    private Long id;
    private String nome;
    private String codigoInep;
    private String municipio;
    private Integer numeroAlunos;
    private BigDecimal orcamentoPnae;
    private String email;
}
