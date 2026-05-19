package br.caixabackend.entity;

import br.caixabackend.enums.StatusEvento;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "eventos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criado_por", nullable = false)
    private Usuario criadoPor;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private LocalDate data;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEvento status = StatusEvento.ABERTO;

    @Column(name = "aberto_em")
    private LocalDateTime abertoEm;

    @Column(name = "fechado_em")
    private LocalDateTime fechadoEm;

    @PrePersist
    public void prePersist() {
        this.abertoEm = LocalDateTime.now();
    }
}