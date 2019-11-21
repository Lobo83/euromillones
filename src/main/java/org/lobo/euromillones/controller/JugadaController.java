package org.lobo.euromillones.controller;

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

@RestController
@RequestMapping("/jugada")
public class JugadaController {

    @Autowired
    private JugadaFeederService jugadaFeederService;

    @Autowired
    private GeneradorJugada generadorJugada;

    @GetMapping("/obtener")
    public JugadaVO obtenerJugada(@RequestParam(name = "fecha") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha) {
        return jugadaFeederService.getJugadaPorFecha(fecha);
    }

    @PostMapping("/crear")
    public void guardarJugadasDesdeOrigen(@RequestParam(name = "fecha") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha) {
        jugadaFeederService.limpiarJugadas();
        jugadaFeederService.crearJugadasDesdeOrigen(fecha);
    }

    @GetMapping("/generar")
    public List<JugadaVO> generarJugadas(@RequestParam(name = "longitud") Integer longitud, @RequestParam(name = "fechaInicial") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fechaInicial, @RequestParam(name = "fechaFinal", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fechaFinal, @RequestParam(name = "numero") Integer numeroJugadas, @RequestParam(name = "frecuenciaMinima") Integer frecuenciaMinima) {
        if (null == fechaFinal) {
            fechaFinal = LocalDate.now();
        }

        EstrategiaJugadaVO estrategiaJugadaVO =
            EstrategiaJugadaVO.builder().tipoEstrategia(TipoEstrategia.FRECUENCIA).fechaFinal(fechaFinal).fechaInicial(fechaInicial).frecuenciaMinima(frecuenciaMinima).numeroJugadas(numeroJugadas).longitudSecuencia(longitud).build();
        return generadorJugada.generarJugadas(estrategiaJugadaVO);
    }
}
