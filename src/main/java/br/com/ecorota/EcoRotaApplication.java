package br.com.ecorota;

import br.com.ecorota.dto.ColheitaRequest;
import br.com.ecorota.dto.ColheitaResponse;
import br.com.ecorota.dto.PedidoRequest;
import br.com.ecorota.dto.PedidoResponse;
import br.com.ecorota.entity.Colheita;
import br.com.ecorota.entity.Pedido;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.modelmapper.convention.MatchingStrategies;
import io.swagger.v3.oas.annotations.info.Info;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "EcoRota API", version = "v1",
        description = "API central do EcoRota: produtores da agricultura familiar, colheitas, escolas e pedidos do PNAE."))
public class EcoRotaApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcoRotaApplication.class, args);
    }

    /**
     * STRICT evita que "produtorId" seja mapeado para o "id" da entidade.
     * Os relacionamentos sao resolvidos no Service (busca no banco), por isso o mapper os ignora.
     * Na resposta, os dados do relacionamento sao achatados (ex.: produtor.nome -> produtorNome).
     */
    @Bean
    ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        mapper.typeMap(ColheitaRequest.class, Colheita.class)
                .addMappings(m -> m.skip(Colheita::setProdutor));
        mapper.typeMap(PedidoRequest.class, Pedido.class)
                .addMappings(m -> {
                    m.skip(Pedido::setEscola);
                    m.skip(Pedido::setColheita);
                });
        mapper.typeMap(Colheita.class, ColheitaResponse.class)
                .addMappings(m -> {
                    m.map(c -> c.getProdutor().getId(), ColheitaResponse::setProdutorId);
                    m.map(c -> c.getProdutor().getNome(), ColheitaResponse::setProdutorNome);
                });
        mapper.typeMap(Pedido.class, PedidoResponse.class)
                .addMappings(m -> {
                    m.map(p -> p.getEscola().getId(), PedidoResponse::setEscolaId);
                    m.map(p -> p.getEscola().getNome(), PedidoResponse::setEscolaNome);
                    m.map(p -> p.getColheita().getId(), PedidoResponse::setColheitaId);
                    m.map(p -> p.getColheita().getProduto(), PedidoResponse::setColheitaProduto);
                });
        return mapper;
    }

    /** Rota raiz redireciona para o Swagger UI. */
    @Controller
    static class RootController {
        @GetMapping("/")
        String root() {
            return "redirect:/swagger-ui.html";
        }
    }
}
