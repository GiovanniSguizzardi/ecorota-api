package br.com.ecorota.dto;

import java.time.Instant;
import java.util.List;

/** Corpo padrao de erro: sem stack trace nem detalhes internos. */
public record ErroResponse(Instant timestamp, int status, String erro, String mensagem, String caminho, List<String> detalhes) {}
