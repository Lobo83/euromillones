package org.lobo.euromillones.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.lobo.euromillones.persistence.model.Jugada;
import org.lobo.euromillones.persistence.repository.JugadaRepository;
import org.lobo.euromillones.service.mapper.JugadaMapper;
import org.lobo.euromillones.service.model.FrecuenciaVO;
import org.lobo.euromillones.service.model.JugadaVO;
import org.lobo.euromillones.service.model.SecuenciaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Jugada feeder service.
 */
@Service
@Slf4j
public class JugadaFeederService {

  private static final String CHECK_RESULT_URL_PATTERN =
      "https://www.loteriasyapuestas.es/es/euromillones/resultados/euromillones-resultados-del-%s";
  private static final String GET_RESULT_PATTERN =
      "(<b>([0-9]{2}\\s-\\s[0-9]{2}\\s-\\s[0-9]{2}\\s-\\s[0-9]{2}\\s-\\s[0-9]{2})</b>\\sEstrellas:\\s<b>([0-9]{2}\\s-\\s[0-9]{2})</b>)";

  @Autowired
  private JugadaMapper jugadaMapper;
  @Autowired
  private JugadaRepository jugadaRepository;

  @Autowired
  private EstadisticaService estadisticaService;


  /**
   * Crear jugadas desde origen.
   *
   * @param fechaInicial the fecha inicial
   */
  public void crearJugadasDesdeOrigen(LocalDate fechaInicial) {
    LocalDate fecha = fechaInicial;
    final int incrementoMartesAViernes = 3;
    final int incrementoViernesAMartes = 4;
    LocalDate hoy = LocalDate.now();
    if (fechaInicial.getDayOfWeek().equals(DayOfWeek.TUESDAY)
        || fechaInicial.getDayOfWeek().equals(DayOfWeek.FRIDAY)) {
      jugadaRepository.deleteAll();
      boolean esMartes = fechaInicial.getDayOfWeek().equals(DayOfWeek.TUESDAY);
      while (fecha.isBefore(hoy)) {
        JugadaVO jugadaVO = this.getJugadaPorFecha(fecha);
        if (null != jugadaVO) {
          jugadaVO.setFecha(fecha.plus(1,
              ChronoUnit.DAYS));//por diferencias de timezone (creo) guarda un dia anterior en bbdd
          jugadaRepository.save(jugadaMapper.VoToEntity(jugadaVO));
        }
        if (esMartes) {
          fecha = fecha.plus(incrementoMartesAViernes, ChronoUnit.DAYS);
        } else {
          fecha = fecha.plus(incrementoViernesAMartes, ChronoUnit.DAYS);
        }
        esMartes = !esMartes;
      }
    } else {
      log.error("El día inicial no es ni jueves ni martes");
    }
  }


  /**
   * Gets jugada por fecha.
   *
   * @param fecha the fecha
   *
   * @return the jugada por fecha
   */
  public JugadaVO getJugadaPorFecha(LocalDate fecha) {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-'de'-MMMM-'de'-yyyy");
    String targetURL = String.format(CHECK_RESULT_URL_PATTERN, formatter.format(fecha));
    HttpURLConnection connection = null;
    JugadaVO jugada = null;
    try {
      //Create connection
      URL url = new URL(targetURL);
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      connection.setUseCaches(false);
      connection.setDoOutput(true);
      //Send request
      DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
      wr.close();

      //Get Response
      InputStream is = connection.getInputStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
      StringBuilder respuesta = new StringBuilder(); // or StringBuffer if Java version 5+
      String line;
      while ((line = rd.readLine()) != null) {
        respuesta.append(line);
      }
      rd.close();

      jugada = parseJugada(respuesta.toString(), fecha);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
    return jugada;
  }

  private JugadaVO parseJugada(String jugada, LocalDate fecha) {
    Pattern p = Pattern.compile(GET_RESULT_PATTERN);
    Matcher m = p.matcher(jugada);
    String[] numeros = null;
    String[] estrellas = null;
    if (m.find()) {
      numeros = m.group(2).split(" - ");
      estrellas = m.group(3).split(" - ");
    }

    return new JugadaVO(null, numeros[0], numeros[1], numeros[2], numeros[3], numeros[4],
        estrellas[0], estrellas[1], fecha);

  }

  /**
   * Save jugada.
   *
   * @param jugadaVO the jugada vo
   */
  public void saveJugada(JugadaVO jugadaVO) {
    jugadaRepository.save(jugadaMapper.VoToEntity(jugadaVO));
  }


  public void limpiarJugadas() {
    jugadaRepository.deleteAll();
  }

  public List<JugadaVO> generarJugadas(Integer longitud, LocalDate fechaInicial,
      LocalDate fechaFinal, Integer numeroJugadas, Integer frecuenciaMinima) {
    List<JugadaVO> result=new ArrayList<>();
    //Se obtienen las secuencias de longitud X y las estrellas ordenadas de mayor a menor frecuencia
    List<SecuenciaVO> secuenciaVOS = estadisticaService
        .obtenerSecuencias(longitud, fechaInicial, fechaFinal).stream()
        .filter(secuencia -> secuencia.getFrecuencia() >= frecuenciaMinima).collect(
            Collectors.toList());
    List<SecuenciaVO> estrellasVOS = estadisticaService.obtenerEstrellas(fechaInicial, fechaFinal)
        .stream().filter(secuencia -> secuencia.getFrecuencia() >= frecuenciaMinima).collect(
            Collectors.toList());
    List<FrecuenciaVO> jugadas = estadisticaService.obtenerFrecuencias(fechaInicial, fechaFinal)
        .stream().filter(frecuencia -> frecuencia.getFrecuencia() >= frecuenciaMinima).collect(
            Collectors.toList());
    estrellasVOS.addAll(obtenerEstrellasAdicionales(estrellasVOS,jugadas,frecuenciaMinima));
    if (!secuenciaVOS.isEmpty() && !estrellasVOS.isEmpty() && !jugadas.isEmpty()) {
      //Se cogen las secuencias y estrellas mas frecuentes y se completan con números frecuentes que no estén ya en la secuencia
      Random aleatorio = new Random(System.currentTimeMillis());
      List<List<String>> secuenciasGeneradas = new ArrayList<>();
      for (int i = 0; i < numeroJugadas; i++) {
        int intentos=5;
        while(intentos>0){
          List<String> secuenciaCandidata=calcularParteNumerica(secuenciaVOS, jugadas,aleatorio);
          if(!secuenciasGeneradas.contains(secuenciaCandidata)){
            secuenciasGeneradas.add(secuenciaCandidata);
            break;
          }
          intentos--;
        }
        List<String> estrellas = new ArrayList<>(estrellasVOS.get(aleatorio.nextInt(estrellasVOS.size())).getNumeros());
        JugadaVO jugadaVO=generateJugadaDesdeComponentes(secuenciasGeneradas.get(secuenciasGeneradas.size()-1),estrellas);
        if(intentos>0&&!result.contains(jugadaVO)){//se ha encontrado una secuencia no contenida
          result.add(jugadaVO);
        }
      }
    }
    return result;
  }

  private List<SecuenciaVO> obtenerEstrellasAdicionales(List<SecuenciaVO> estrellasVOS, List<FrecuenciaVO> jugadas, Integer frecuencia) {
    List<Set<String>> estrellasObtenidas=estrellasVOS.stream().map(SecuenciaVO::getNumeros).collect(
        Collectors.toList());
    List<String> estrellasNuevas=jugadas.stream().filter(frec->frec.getEstrella() && frec.getFrecuencia()>frecuencia).map(FrecuenciaVO::getValor).collect(Collectors.toList());
    Map<Set<String>, Integer> mapFrecuencias=new HashMap<>();
    estadisticaService.createSequenceMap(estrellasNuevas,2,mapFrecuencias);
    mapFrecuencias.keySet().stream().filter(secuenciaEstrella->!estrellasObtenidas.contains(secuenciaEstrella)).forEach(secuenciaEstrella->estrellasObtenidas.add(secuenciaEstrella));
    return estrellasObtenidas.stream().map(secuencia->{
      SecuenciaVO secuenciaVO=new SecuenciaVO();
      secuenciaVO.setNumeros(secuencia);
      return secuenciaVO;
    }).collect(Collectors.toList());
  }

  private JugadaVO generateJugadaDesdeComponentes(List<String> numeros, List<String> estrellas){
    Collections.sort(numeros);
    Collections.sort(estrellas);
    JugadaVO jugadaVO=new JugadaVO();
    jugadaVO.setValor1(numeros.get(0));
    jugadaVO.setValor2(numeros.get(1));
    jugadaVO.setValor3(numeros.get(2));
    jugadaVO.setValor4(numeros.get(3));
    jugadaVO.setValor5(numeros.get(4));
    jugadaVO.setEstrella1(estrellas.get(0));
    jugadaVO.setEstrella2(estrellas.get(1));
    return jugadaVO;
  }
  private List<String> calcularParteNumerica(List<SecuenciaVO> secuenciaVOS,
      List<FrecuenciaVO> jugadas,Random aleatorio) {
    int posicionSecuencia = aleatorio.nextInt(secuenciaVOS.size());

    List<String> jugadaNumerica = new ArrayList<>();
    jugadaNumerica.addAll(secuenciaVOS.get(posicionSecuencia).getNumeros());
    while (jugadaNumerica.size() < 5) {
      int posicionJugada = aleatorio.nextInt(jugadas.size());
      String numeroJugada=jugadas.get(posicionJugada).getValor();
      int intentos=5;
      while(jugadaNumerica.contains(numeroJugada)&&intentos>0){
        posicionJugada = aleatorio.nextInt(jugadas.size());
        numeroJugada=jugadas.get(posicionJugada).getValor();
        intentos--;
      }
      jugadaNumerica.add(numeroJugada);
    }

    return jugadaNumerica;
  }
}


