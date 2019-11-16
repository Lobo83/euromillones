package org.lobo.euromillones.persistence.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * The type Jugada.
 */
@Entity
@Table(name = "jugada")
@Data
public class Jugada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "valor_1")
    private String valor1;

    @Column(name = "valor_2")
    private String valor2;

    @Column(name = "valor_3")
    private String valor3;

    @Column(name = "valor_4")
    private String valor4;

    @Column(name = "valor_5")
    private String valor5;

    @Column(name = "estrella_1")
    private String estrella1;

    @Column(name = "estrella_2")
    private String estrella2;

    @Column(name = "fecha")
    private LocalDate fecha;
}
