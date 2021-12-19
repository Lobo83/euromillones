package org.lobo.euromillones.controller;

import java.time.Instant;
import org.lobo.euromillones.service.JugadaFeederService;
import org.lobo.euromillones.service.generador.GeneradorJugada;
import org.lobo.euromillones.service.generador.model.EstrategiaJugadaVO;
import org.lobo.euromillones.service.generador.model.TipoEstrategia;
import org.lobo.euromillones.service.model.JugadaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * The type Jugada controller.
 */
@RestController
@RequestMapping("/jugada")
public class JugadaController {

    @Autowired
    private JugadaFeederService jugadaFeederService;

    @Autowired
    private GeneradorJugada generadorJugada;

    /**
     * Obtener jugada jugada vo.
     *
     * @param fecha the fecha
     *
     * @return the jugada vo
     */
    @GetMapping("/")
    public JugadaVO obtenerJugada(@RequestParam(name = "fecha") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha) {
        return jugadaFeederService.getJugadaPorFecha(fecha);
    }

    /**
     * Guardar jugadas desde origen.
     *
     * @param fecha the fecha
     */
    @PostMapping("/")
    public void guardarJugadasDesdeOrigen(@RequestParam(name = "fecha") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha) {
        jugadaFeederService.limpiarJugadas();
        jugadaFeederService.crearJugadasDesdeOrigen(fecha);
    }

    /**
     * Generar jugadas frecuentes list.
     *
     * @param longitud the longitud
     * @param fechaInicial the fecha inicial
     * @param fechaFinal the fecha final
     * @param numeroJugadas the numero jugadas
     * @param frecuenciaMinima the frecuencia minima
     *
     * @return the list
     */
    @GetMapping("/frecuente")
    public List<JugadaVO> generarJugadasFrecuentes(@RequestParam(name = "longitud") Integer longitud, @RequestParam(name = "fechaInicial") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fechaInicial, @RequestParam(name = "fechaFinal", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fechaFinal, @RequestParam(name = "numeroJugadas") Integer numeroJugadas, @RequestParam(name = "frecuenciaMinima") Integer frecuenciaMinima) {
        if (null == fechaFinal) {
            fechaFinal = LocalDate.now();
        }

        EstrategiaJugadaVO estrategiaJugadaVO =
            EstrategiaJugadaVO.builder().tipoEstrategia(TipoEstrategia.FRECUENCIA).fechaFinal(fechaFinal).fechaInicial(fechaInicial).frecuenciaMinima(frecuenciaMinima).numeroJugadas(numeroJugadas).longitudSecuencia(longitud).build();
        return generadorJugada.generarJugadas(estrategiaJugadaVO);
    }

    /**
     * Generar jugadas aleatorias list.
     *
     * @param numeroJugadas the numero jugadas
     *
     * @return the list
     */
    @GetMapping("/aleatoria")
    public List<JugadaVO> generarJugadasAleatorias(@RequestParam(name = "numeroJugadas") Integer numeroJugadas) {


        EstrategiaJugadaVO estrategiaJugadaVO =
            EstrategiaJugadaVO.builder().tipoEstrategia(TipoEstrategia.ALEATORIA).numeroJugadas(numeroJugadas).build();
        return generadorJugada.generarJugadas(estrategiaJugadaVO);
    }

}
