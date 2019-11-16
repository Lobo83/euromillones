package org.lobo.euromillones.service.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * The type Secuencias vo.
 */
@Data
@NoArgsConstructor
public class SecuenciaVO {
    /**
     * The Numeros.
     */
    private Set<String> numeros;
    /**
     * The Frecuencia.
     */
    private Integer frecuencia;
}
