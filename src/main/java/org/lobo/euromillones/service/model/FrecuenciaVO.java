package org.lobo.euromillones.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * The type Frecuencia vo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FrecuenciaVO {
    private Long id;

    private String valor;

    private Long frecuencia;

    private Float frecuenciaRelativa;

    private Boolean estrella;
}
