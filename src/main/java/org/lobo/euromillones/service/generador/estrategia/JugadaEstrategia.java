package org.lobo.euromillones.service.generador.estrategia;

import org.lobo.euromillones.service.generador.model.EstrategiaJugadaVO;
import org.lobo.euromillones.service.generador.model.TipoEstrategia;
import org.lobo.euromillones.service.model.JugadaVO;

import java.util.List;

/**
 * The interface Generador jugada tipoEstrategia.
 */
public interface JugadaEstrategia {
    /**
     * Generar jugadas list.
     *
     * @param estrategiaJugadaVO the numero jugadas
     * @return the list
     */
    List<JugadaVO> generarJugadas(EstrategiaJugadaVO estrategiaJugadaVO);

    /**
     * Gets tipo tipoEstrategia.
     *
     * @return the tipo tipoEstrategia
     */
    TipoEstrategia getTipoEstrategia();
}
