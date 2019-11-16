package org.lobo.euromillones.controller;


import org.lobo.euromillones.service.EstadisticaService;
import org.lobo.euromillones.service.model.FrecuenciaVO;
import org.lobo.euromillones.service.model.SecuenciaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/estadistica")
public class EstadisticaController {

    @Autowired
    private EstadisticaService estadisticaService;

    @PostMapping(value = "/calcular_frecuencias")
    public void calcularFrecuencias() {
        estadisticaService.crearFrecuencias();
    }

    @GetMapping(value = "/obtener_frecuencias")
    public List<FrecuenciaVO> obtenerFrecuencias(@RequestParam(name = "fechaInicial") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fechaInicial, @RequestParam(name = "fechaFinal", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fechaFinal) {
        if (null == fechaFinal) {
            fechaFinal = LocalDate.now();
        }
        return estadisticaService.obtenerFrecuencias(fechaInicial, fechaFinal);
    }

    @GetMapping(value = "/obtener_secuencias_frecuentes")
    public List<SecuenciaVO> obtenerSecuenciasFrecuentes(@RequestParam(name = "longitud") Integer longitud, @RequestParam(name = "fechaInicial") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fechaInicial, @RequestParam(name = "fechaFinal", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fechaFinal) {
        if (null == fechaFinal) {
            fechaFinal = LocalDate.now();
        }
        return estadisticaService.obtenerSecuencias(longitud, fechaInicial, fechaFinal);
    }

    @GetMapping(value = "/obtener_estrellas_frecuentes")
    public List<SecuenciaVO> obtenerEstrellasFrecuentes(@RequestParam(name = "fechaInicial") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fechaInicial, @RequestParam(name = "fechaFinal", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fechaFinal) {
        if (null == fechaFinal) {
            fechaFinal = LocalDate.now();
        }
        return estadisticaService.obtenerEstrellas(fechaInicial, fechaFinal);
    }

}
