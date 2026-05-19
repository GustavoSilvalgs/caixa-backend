package br.caixabackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos_fiado")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PagamentoFiado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id", nullable = false)
    private Venda venda;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "pago_em", nullable = false)
    private LocalDateTime pagoEm;

    @Column
    private String observacao;

    @PrePersist
    public void prePersist() {
        this.pagoEm = LocalDateTime.now();
    }
}