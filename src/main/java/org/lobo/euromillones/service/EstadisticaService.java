package org.lobo.euromillones.service;

import org.lobo.euromillones.persistence.model.Frecuencia;
import org.lobo.euromillones.persistence.model.Jugada;
import org.lobo.euromillones.persistence.repository.FrecuenciaRepository;
import org.lobo.euromillones.persistence.repository.JugadaRepository;
import org.lobo.euromillones.service.mapper.FrecuenciaMapper;
import org.lobo.euromillones.service.model.FrecuenciaVO;
import org.lobo.euromillones.service.model.SecuenciaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EstadisticaService {
    @Autowired
    private FrecuenciaRepository frecuenciaRepository;

    @Autowired
    private JugadaRepository jugadaRepository;

    @Autowired
    private FrecuenciaMapper frecuenciaMapper;

    @Transactional
    @Async
    public void crearFrecuencias() {
        Iterable<Jugada> jugadas = jugadaRepository.findAll();
        List<Frecuencia> frecuencias = calcularFrecuenciasDeJugadas(jugadas);
        frecuenciaRepository.saveAll(frecuencias);
    }

    public List<FrecuenciaVO> obtenerFrecuencias(LocalDate fechaInicial, LocalDate fechaFinal) {
        Iterable<Jugada> jugadas =
            jugadaRepository.findByFechaGreaterThanEqualAndFechaLessThanEqual(fechaInicial, fechaFinal);
        List<Frecuencia> frecuencias = calcularFrecuenciasDeJugadas(jugadas);
        Collections.sort(frecuencias, (o1, o2) -> {
            int result = -1;
            //orden primero valores no estrella y luego estrella
            // si son iguales se recupera la mayor frecuencia primero
            if (o1.getEstrella().equals(o2.getEstrella())) {
                result = -1 * o1.getFrecuencia().compareTo(o2.getFrecuencia());
            } else if (o1.getEstrella()) {
                result = 1;
            }
            return result;
        });
        return frecuenciaMapper.entityToVO(frecuencias);

    }

    private List<Frecuencia> calcularFrecuenciasDeJugadas(Iterable<Jugada> jugadas) {
        Map<String, Long> numeroFrecuencia = new HashMap<>();

        Map<String, Long> numeroFrecuenciaEstrella = new HashMap<>();

        jugadas.forEach(jugada -> {
            addValorAMapa(numeroFrecuencia, jugada.getValor1());
            addValorAMapa(numeroFrecuencia, jugada.getValor2());
            addValorAMapa(numeroFrecuencia, jugada.getValor3());
            addValorAMapa(numeroFrecuencia, jugada.getValor4());
            addValorAMapa(numeroFrecuencia, jugada.getValor5());
            addValorAMapa(numeroFrecuenciaEstrella, jugada.getEstrella1());
            addValorAMapa(numeroFrecuenciaEstrella, jugada.getEstrella2());
        });
        Integer numeroValores = numeroFrecuencia.keySet().size();
        List<Frecuencia> frecuencias = new ArrayList<>();

        numeroFrecuencia.entrySet().stream().forEach(entry -> {
            Frecuencia frecuencia = new Frecuencia();
            frecuencia.setValor(entry.getKey());
            frecuencia.setFrecuencia(entry.getValue());
            frecuencia.setFrecuenciaRelativa(
                Float.valueOf(entry.getValue()) / Float.valueOf(numeroValores));
            frecuencia.setEstrella(false);
            frecuencias.add(frecuencia);
        });
        Integer numeroValoresEstrella = numeroFrecuenciaEstrella.keySet().size();
        numeroFrecuenciaEstrella.entrySet().stream().forEach(entry -> {
            Frecuencia frecuencia = new Frecuencia();
            frecuencia.setValor(entry.getKey());
            frecuencia.setFrecuencia(entry.getValue());
            frecuencia.setFrecuenciaRelativa(
                Float.valueOf(entry.getValue()) / Float.valueOf(numeroValoresEstrella));
            frecuencia.setEstrella(true);
            frecuencias.add(frecuencia);
        });
        return frecuencias;
    }

    private void addValorAMapa(Map<String, Long> map, String valor) {
        if (!map.containsKey(valor)) {
            map.put(valor, 1L);
        } else {
            Long ocurrencias = map.get(valor);
            map.put(valor, ++ocurrencias);
        }
    }

    public List<SecuenciaVO> obtenerSecuencias(Integer longitud, LocalDate fechaInicial, LocalDate fechaFinal) {
        Iterable<Jugada> jugadas =
            jugadaRepository.findByFechaGreaterThanEqualAndFechaLessThanEqual(fechaInicial, fechaFinal);
        Map<Set<String>, Integer> map = new HashMap<>();
        jugadas.forEach(jugada -> createSequenceMap(jugadaToListString(jugada), longitud, map));
        List<SecuenciaVO> secuenciaVOS = map.entrySet().stream().map(entry -> {
            SecuenciaVO secuenciaVO = new SecuenciaVO();
            secuenciaVO.setNumeros(entry.getKey());
            secuenciaVO.setFrecuencia(entry.getValue());
            return secuenciaVO;
        }).collect(Collectors.toList());
        Collections.sort(secuenciaVOS, (o1, o2) -> {
            return -1 * o1.getFrecuencia().compareTo(o2.getFrecuencia());
        });
        return secuenciaVOS;
    }

    public void createSequenceMap(List<String> numeros, Integer tamSequencia, Map<Set<String>, Integer> map) {
        for (int i = 0; i < numeros.size() - 1; i++) {
            Set<String> secuenciaBase = new LinkedHashSet<>();
            secuenciaBase.add(numeros.get(i));
            int j = i + 1;
            for (; (j < i + tamSequencia - 1) && (j < numeros.size()); j++) {
                secuenciaBase.add(numeros.get(j));
            }

            for (int k = j; k < numeros.size(); k++) {
                Set<String> secuencia = new LinkedHashSet<>();
                secuencia.addAll(secuenciaBase);
                secuencia.add(numeros.get(k));

                if (!map.containsKey(secuencia)) {
                    map.put(secuencia, 0);
                }
                map.put(secuencia, map.get(secuencia) + 1);
            }
        }
    }

    private List<String> jugadaToListString(Jugada jugada) {
        List<String> numeros = new ArrayList<>();
        numeros.add(jugada.getValor1());
        numeros.add(jugada.getValor2());
        numeros.add(jugada.getValor3());
        numeros.add(jugada.getValor4());
        numeros.add(jugada.getValor5());
        return numeros;
    }

    public List<SecuenciaVO> obtenerEstrellas(LocalDate fechaInicial, LocalDate fechaFinal) {
        Iterable<Jugada> jugadas =
            jugadaRepository.findByFechaGreaterThanEqualAndFechaLessThanEqual(fechaInicial, fechaFinal);
        Map<Set<String>, Integer> map = new HashMap<>();
        jugadas.forEach(jugada -> createSequenceMap(estrellaToListString(jugada), 2, map));
        List<SecuenciaVO> secuenciaVOS = map.entrySet().stream().map(entry -> {
            SecuenciaVO secuenciaVO = new SecuenciaVO();
            secuenciaVO.setNumeros(entry.getKey());
            secuenciaVO.setFrecuencia(entry.getValue());
            return secuenciaVO;
        }).collect(Collectors.toList());
        Collections.sort(secuenciaVOS, (o1, o2) -> {
            return -1 * o1.getFrecuencia().compareTo(o2.getFrecuencia());
        });
        return secuenciaVOS;
    }

    private List<String> estrellaToListString(Jugada jugada) {
        List<String> estrellas = new ArrayList<>();
        estrellas.add(jugada.getEstrella1());
        estrellas.add(jugada.getEstrella2());

        return estrellas;
    }


}
