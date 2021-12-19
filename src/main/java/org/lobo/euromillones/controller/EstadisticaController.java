package org.lobo.euromillones.controller;


import org.lobo.euromillones.service.EstadisticaService;
import org.lobo.euromillones.service.model.FrecuenciaVO;
import org.lobo.euromillones.service.model.SecuenciaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * The type Estadistica controller.
 */
@RestController
@RequestMapping("/estadisticas")
public class EstadisticaController {

    @Autowired
    private EstadisticaService estadisticaService;

    /**
     * Calcular frecuencias.
     */
    @PostMapping(value = "/")
    public void calcularFrecuencias() {
        estadisticaService.crearFrecuencias();
    }

    /**
     * Obtener frecuencias list.
     *
     * @param fechaInicial the fecha inicial
     * @param fechaFinal the fecha final
     *
     * @return the list
     */
    @GetMapping(value = "/frecuencias")
    public List<FrecuenciaVO> obtenerFrecuencias(@RequestParam(name = "fechaInicial") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fechaInicial, @RequestParam(name = "fechaFinal", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fechaFinal) {
        if (null == fechaFinal) {
            fechaFinal = LocalDate.now();
        }
        return estadisticaService.obtenerFrecuencias(fechaInicial, fechaFinal);
    }

    /**
     * Obtener secuencias frecuentes list.
     *
     * @param longitud the longitud
     * @param fechaInicial the fecha inicial
     * @param fechaFinal the fecha final
     *
     * @return the list
     */
    @GetMapping(value = "/secuencias")
    public List<SecuenciaVO> obtenerSecuenciasFrecuentes(@RequestParam(name = "longitud") Integer longitud, @RequestParam(name = "fechaInicial") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fechaInicial, @RequestParam(name = "fechaFinal", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fechaFinal) {
        if (null == fechaFinal) {
            fechaFinal = LocalDate.now();
        }
        return estadisticaService.obtenerSecuencias(longitud, fechaInicial, fechaFinal);
    }

    /**
     * Obtener estrellas frecuentes list.
     *
     * @param fechaInicial the fecha inicial
     * @param fechaFinal the fecha final
     *
     * @return the list
     */
    @GetMapping(value = "/estrellas_frecuentes")
    public List<SecuenciaVO> obtenerEstrellasFrecuentes(@RequestParam(name = "fechaInicial") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fechaInicial, @RequestParam(name = "fechaFinal", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fechaFinal) {
        if (null == fechaFinal) {
            fechaFinal = LocalDate.now();
        }
        return estadisticaService.obtenerEstrellas(fechaInicial, fechaFinal);
    }

}
