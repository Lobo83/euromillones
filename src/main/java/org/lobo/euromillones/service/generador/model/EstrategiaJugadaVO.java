package org.lobo.euromillones.service.generador.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class EstrategiaJugadaVO {
    private Integer numeroJugadas;
    private LocalDate fechaInicial;
    private LocalDate fechaFinal;
    private Integer frecuenciaMinima;
    private Integer longitudSecuencia;
    private TipoEstrategia tipoEstrategia;
}

