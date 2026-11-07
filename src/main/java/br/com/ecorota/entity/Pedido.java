package br.com.ecorota.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Compra de uma escola sobre uma colheita (chamada publica PNAE). */
@Entity
@Table(name = "pedidos")
@Getter @Setter
public class Pedido {

    public enum Status { PENDENTE, CONFIRMADO, ENTREGUE, CANCELADO }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "escola_id", nullable = false)
    private Escola escola;

    @ManyToOne(optional = false)
    @JoinColumn(name = "colheita_id", nullable = false)
    private Colheita colheita;

    @Column(nullable = false)
    private Integer quantidade;

    /** Calculado no Service: quantidade x preco unitario da colheita. */
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private Status status = Status.PENDENTE;

    @Column(nullable = false)
    private LocalDate dataEntrega;
}
