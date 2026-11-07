package br.com.ecorota;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDate;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Fluxo completo: produtor -> colheita -> escola -> pedido, mais erros 400/404. */
@SpringBootTest
@AutoConfigureMockMvc
class EcoRotaApiTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @Test
    void fluxoPnaeCompleto() throws Exception {
        long produtor = enviar("/api/v1/produtores", """
                {"nome":"Ana Souza","cpf":"12345678901","telefone":"11999990000","municipio":"Mogi das Cruzes"}""",
                status().isCreated()).get("id").asLong();

        JsonNode colheita = enviar("/api/v1/colheitas", """
                {"produto":"Tomate","quantidade":20,"unidade":"caixa","precoUnitario":45.50,
                 "dataColheita":"%s","janelaVendaDias":5,"produtorId":%d}""".formatted(LocalDate.now(), produtor),
                status().isCreated());
        long colheitaId = colheita.get("id").asLong();
        assert colheita.get("produtorNome").asText().equals("Ana Souza");

        long escola = enviar("/api/v1/escolas", """
                {"nome":"EMEF Parelheiros","codigoInep":"35123456","municipio":"Sao Paulo","orcamentoPnae":100000}""",
                status().isCreated()).get("id").asLong();

        String pedido = """
                {"escolaId":%d,"colheitaId":%d,"quantidade":%d,"dataEntrega":"%s"}""";
        JsonNode criado = enviar("/api/v1/pedidos", pedido.formatted(escola, colheitaId, 8, LocalDate.now().plusDays(2)),
                status().isCreated());
        assert criado.get("valorTotal").decimalValue().compareTo(new java.math.BigDecimal("364.00")) == 0;
        assert criado.get("status").asText().equals("PENDENTE");

        // estoque reservado: 20 - 8 = 12; pedir 13 deve falhar com 400
        mvc.perform(get("/api/v1/colheitas/" + colheitaId)).andExpect(jsonPath("$.quantidade").value(12));
        enviar("/api/v1/pedidos", pedido.formatted(escola, colheitaId, 13, LocalDate.now()), status().isBadRequest());

        // PUT altera quantidade e recalcula estoque; DELETE devolve o estoque
        long pedidoId = criado.get("id").asLong();
        mvc.perform(put("/api/v1/pedidos/" + pedidoId).contentType(MediaType.APPLICATION_JSON)
                        .content(pedido.formatted(escola, colheitaId, 10, LocalDate.now())))
                .andExpect(status().isOk()).andExpect(jsonPath("$.quantidade").value(10));
        mvc.perform(get("/api/v1/colheitas/" + colheitaId)).andExpect(jsonPath("$.quantidade").value(10));
        mvc.perform(delete("/api/v1/pedidos/" + pedidoId)).andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/colheitas/" + colheitaId)).andExpect(jsonPath("$.quantidade").value(20));

        // PUT de colheita nao pode alterar o id do produtor relacionado
        mvc.perform(put("/api/v1/colheitas/" + colheitaId).contentType(MediaType.APPLICATION_JSON).content("""
                        {"produto":"Tomate italiano","quantidade":20,"unidade":"caixa","precoUnitario":50,
                         "dataColheita":"%s","produtorId":%d}""".formatted(LocalDate.now(), produtor)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.produtorId").value(produtor));

        // CPF duplicado -> 409
        enviar("/api/v1/produtores", """
                {"nome":"Outro","cpf":"12345678901","telefone":"11","municipio":"X"}""", status().isConflict());
    }

    @Test
    void errosPadronizados() throws Exception {
        enviar("/api/v1/produtores", "{\"nome\":\"\"}", status().isBadRequest());
        mvc.perform(get("/api/v1/escolas/9999")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Escola com id 9999 nao encontrado(a)"));
        mvc.perform(get("/api/v1/pedidos/abc")).andExpect(status().isBadRequest())
                .andExpect(content().string(not(containsString("Exception"))));
        mvc.perform(get("/")).andExpect(status().is3xxRedirection());
    }

    private JsonNode enviar(String url, String body, ResultMatcher esperado) throws Exception {
        String resp = mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(esperado).andReturn().getResponse().getContentAsString();
        return json.readTree(resp);
    }
}
