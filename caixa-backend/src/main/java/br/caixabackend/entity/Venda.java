package br.caixabackend.entity;

import br.caixabackend.enums.StatusVenda;
import br.caixabackend.enums.TipoVenda;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vendas")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operador_id", nullable = false)
    private Usuario operador;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoVenda tipo = TipoVenda.NORMAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusVenda status = StatusVenda.CONCLUIDA;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "cliente_fiado")
    private String clienteFiado;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemVenda> itens = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.criadoEm = LocalDateTime.now();
    }
}