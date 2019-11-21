package org.lobo.euromillones.service.generador.estrategia;

import org.lobo.euromillones.service.EstadisticaService;
import org.lobo.euromillones.service.generador.model.EstrategiaJugadaVO;
import org.lobo.euromillones.service.generador.model.TipoEstrategia;
import org.lobo.euromillones.service.model.FrecuenciaVO;
import org.lobo.euromillones.service.model.JugadaVO;
import org.lobo.euromillones.service.model.SecuenciaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JugadaEstrategiaFrecuencias implements JugadaEstrategia {

    @Autowired
    private EstadisticaService estadisticaService;

    @Override
    public List<JugadaVO> generarJugadas(EstrategiaJugadaVO estrategiaJugadaVO) {

        List<JugadaVO> result = new ArrayList<>();
        //Se obtienen las secuencias de longitud X y las estrellas ordenadas de mayor a menor frecuencia
        Integer longitud = estrategiaJugadaVO.getLongitudSecuencia();
        LocalDate fechaInicial = estrategiaJugadaVO.getFechaInicial();
        LocalDate fechaFinal = estrategiaJugadaVO.getFechaFinal();
        Integer frecuenciaMinima = estrategiaJugadaVO.getFrecuenciaMinima();
        Integer numeroJugadas = estrategiaJugadaVO.getNumeroJugadas();
        List<SecuenciaVO> secuenciaVOS =
            estadisticaService.obtenerSecuencias(longitud, fechaInicial, fechaFinal).stream().filter(secuencia ->
                secuencia.getFrecuencia() >= frecuenciaMinima).collect(Collectors.toList());
        List<SecuenciaVO> estrellasVOS =
            estadisticaService.obtenerEstrellas(fechaInicial, fechaFinal).stream().filter(secuencia ->
                secuencia.getFrecuencia() >= frecuenciaMinima).collect(Collectors.toList());
        List<FrecuenciaVO> jugadas =
            estadisticaService.obtenerFrecuencias(fechaInicial, fechaFinal).stream().filter(frecuencia ->
                frecuencia.getFrecuencia() >= frecuenciaMinima).collect(Collectors.toList());
        estrellasVOS.addAll(obtenerEstrellasAdicionales(estrellasVOS, jugadas, frecuenciaMinima));
        if (!secuenciaVOS.isEmpty() && !estrellasVOS.isEmpty() && !jugadas.isEmpty()) {
            //Se cogen las secuencias y estrellas mas frecuentes y se completan con números frecuentes que no estén ya en la secuencia
            Random aleatorio = new Random(System.currentTimeMillis());
            List<List<String>> secuenciasGeneradas = new ArrayList<>();
            for (int i = 0; i < numeroJugadas; i++) {
                int intentos = 5;
                while (intentos > 0) {
                    List<String> secuenciaCandidata =
                        calcularParteNumerica(secuenciaVOS, jugadas, aleatorio);
                    if (!secuenciasGeneradas.contains(secuenciaCandidata)) {
                        secuenciasGeneradas.add(secuenciaCandidata);
                        break;
                    }
                    intentos--;
                }
                List<String> estrellas =
                    new ArrayList<>(estrellasVOS.get(aleatorio.nextInt(estrellasVOS.size())).getNumeros());
                JugadaVO jugadaVO = generateJugadaDesdeComponentes(secuenciasGeneradas.get(
                    secuenciasGeneradas.size() - 1), estrellas);
                if (intentos > 0
                    && !result.contains(jugadaVO)) {//se ha encontrado una secuencia no contenida
                    result.add(jugadaVO);
                }
            }
        }
        return result;
    }

    private List<SecuenciaVO> obtenerEstrellasAdicionales(List<SecuenciaVO> estrellasVOS, List<FrecuenciaVO> jugadas, Integer frecuencia) {
        List<Set<String>> estrellasObtenidas =
            estrellasVOS.stream().map(SecuenciaVO::getNumeros).collect(Collectors.toList());
        List<String> estrellasNuevas = jugadas.stream().filter(frec -> frec.getEstrella()
            && frec.getFrecuencia()
            > frecuencia).map(FrecuenciaVO::getValor).collect(Collectors.toList());
        Map<Set<String>, Integer> mapFrecuencias = new HashMap<>();
        estadisticaService.createSequenceMap(estrellasNuevas, 2, mapFrecuencias);
        mapFrecuencias.keySet().stream().filter(secuenciaEstrella -> !estrellasObtenidas.contains(secuenciaEstrella)).forEach(secuenciaEstrella -> estrellasObtenidas.add(secuenciaEstrella));
        return estrellasObtenidas.stream().map(secuencia -> {
            SecuenciaVO secuenciaVO = new SecuenciaVO();
            secuenciaVO.setNumeros(secuencia);
            return secuenciaVO;
        }).collect(Collectors.toList());
    }

    private JugadaVO generateJugadaDesdeComponentes(List<String> numeros, List<String> estrellas) {
        Collections.sort(numeros);
        Collections.sort(estrellas);
        JugadaVO jugadaVO = new JugadaVO();
        jugadaVO.setValor1(numeros.get(0));
        jugadaVO.setValor2(numeros.get(1));
        jugadaVO.setValor3(numeros.get(2));
        jugadaVO.setValor4(numeros.get(3));
        jugadaVO.setValor5(numeros.get(4));
        jugadaVO.setEstrella1(estrellas.get(0));
        jugadaVO.setEstrella2(estrellas.get(1));
        return jugadaVO;
    }


    private List<String> calcularParteNumerica(List<SecuenciaVO> secuenciaVOS, List<FrecuenciaVO> jugadas, Random aleatorio) {
        int posicionSecuencia = aleatorio.nextInt(secuenciaVOS.size());

        List<String> jugadaNumerica = new ArrayList<>();
        jugadaNumerica.addAll(secuenciaVOS.get(posicionSecuencia).getNumeros());
        while (jugadaNumerica.size() < 5) {
            int posicionJugada = aleatorio.nextInt(jugadas.size());
            String numeroJugada = jugadas.get(posicionJugada).getValor();
            int intentos = 5;
            while (jugadaNumerica.contains(numeroJugada) && intentos > 0) {
                posicionJugada = aleatorio.nextInt(jugadas.size());
                numeroJugada = jugadas.get(posicionJugada).getValor();
                intentos--;
            }
            jugadaNumerica.add(numeroJugada);
        }

        return jugadaNumerica;
    }

    @Override
    public TipoEstrategia getTipoEstrategia() {
        return TipoEstrategia.FRECUENCIA;
    }
}
