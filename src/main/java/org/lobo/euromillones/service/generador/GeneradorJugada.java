package org.lobo.euromillones.service.generador;

import org.lobo.euromillones.service.generador.estrategia.JugadaEstrategia;
import org.lobo.euromillones.service.generador.model.EstrategiaJugadaVO;
import org.lobo.euromillones.service.generador.model.TipoEstrategia;
import org.lobo.euromillones.service.model.JugadaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The type Generador jugada.
 */
@Component
public class GeneradorJugada {

    private Map<TipoEstrategia, JugadaEstrategia> estrategiaMap;
    @Autowired
    private Set<JugadaEstrategia> estrategiaSet;

    @PostConstruct
    private void init() {
        estrategiaMap = new HashMap<>();
        estrategiaSet.stream().forEach(estrategia -> estrategiaMap.put(estrategia.getTipoEstrategia(), estrategia));
    }

    /**
     * Generar jugadas list.
     *
     * @param estrategiaJugadaVO the estrategia jugada vo
     * @return the list
     */
    public List<JugadaVO> generarJugadas(EstrategiaJugadaVO estrategiaJugadaVO) {
        return estrategiaMap.get(estrategiaJugadaVO.getTipoEstrategia()).generarJugadas(estrategiaJugadaVO);
    }

}
