package br.com.ecorota.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Oferta de colheita cadastrada pelo produtor (por voz/texto no EcoRota Campo). */
@Entity
@Table(name = "colheitas")
@Getter @Setter
public class Colheita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String produto;

    /** Quantidade ainda disponivel para venda. */
    @Column(nullable = false)
    private Integer quantidade;

    /** Ex.: caixa, maco, kg. */
    @Column(nullable = false, length = 20)
    private String unidade;

    /** Preco por unidade sugerido pelo Motor de Inteligencia. */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    @Column(nullable = false)
    private LocalDate dataColheita;

    /** Janela de venda: dias ate o alimento perder qualidade. */
    private Integer janelaVendaDias;

    @ManyToOne(optional = false)
    @JoinColumn(name = "produtor_id", nullable = false)
    private Produtor produtor;

    @OneToMany(mappedBy = "colheita", cascade = CascadeType.REMOVE)
    private List<Pedido> pedidos = new ArrayList<>();
}
