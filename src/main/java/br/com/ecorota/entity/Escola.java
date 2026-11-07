package br.com.ecorota.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Escola/creche publica que compra pela cota do PNAE. */
@Entity
@Table(name = "escolas")
@Getter @Setter
public class Escola {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    /** Codigo INEP da escola. */
    @Column(nullable = false, unique = true, length = 8)
    private String codigoInep;

    @Column(nullable = false, length = 80)
    private String municipio;

    private Integer numeroAlunos;

    /** Verba federal do PNAE no periodo; 45% deve ir para a agricultura familiar. */
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal orcamentoPnae;

    @Column(length = 120)
    private String email;

    @OneToMany(mappedBy = "escola", cascade = CascadeType.REMOVE)
    private List<Pedido> pedidos = new ArrayList<>();
}
