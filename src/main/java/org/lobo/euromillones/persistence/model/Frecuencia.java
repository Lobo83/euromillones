package org.lobo.euromillones.persistence.model;

import lombok.Data;

import javax.persistence.*;


@Entity
@Table(name = "frecuencia")
@Data
public class Frecuencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "valor")
    private String valor;

    @Column(name = "frecuencia")
    private Long frecuencia;

    @Column(name = "frecuencia_relativa")
    private Float frecuenciaRelativa;

    @Column(name = "estrella")
    private Boolean estrella;
}
