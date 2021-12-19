package org.lobo.euromillones.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * The type Jugada vo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JugadaVO {
    private Long id;

    private String valor1;

    private String valor2;

    private String valor3;

    private String valor4;

    private String valor5;

    private String estrella1;

    private String estrella2;

    private LocalDate fecha;


}
