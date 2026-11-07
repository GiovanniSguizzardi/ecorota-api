package br.com.ecorota.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Pequeno produtor da agricultura familiar. */
@Entity
@Table(name = "produtores")
@Getter @Setter
public class Produtor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    /** Numero do Cadastro Nacional da Agricultura Familiar (exigido pelo PNAE). */
    @Column(length = 30)
    private String numeroCaf;

    @Column(nullable = false, length = 20)
    private String telefone;

    @Column(nullable = false, length = 80)
    private String municipio;

    @Column(nullable = false)
    private LocalDate dataCadastro = LocalDate.now();

    @OneToMany(mappedBy = "produtor", cascade = CascadeType.REMOVE)
    private List<Colheita> colheitas = new ArrayList<>();
}
