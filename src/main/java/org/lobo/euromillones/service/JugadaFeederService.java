package org.lobo.euromillones.service;

import lombok.extern.slf4j.Slf4j;
import org.lobo.euromillones.persistence.repository.JugadaRepository;
import org.lobo.euromillones.service.mapper.JugadaMapper;
import org.lobo.euromillones.service.model.JugadaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    @Async("taskExecutor")
    public void crearJugadasDesdeOrigen(LocalDate fechaInicial) {
        LocalDate fecha = fechaInicial;
        final int incrementoMartesAViernes = 3;
        final int incrementoViernesAMartes = 4;
        LocalDate hoy = LocalDate.now();
        if (fechaInicial.getDayOfWeek().equals(DayOfWeek.TUESDAY)
            || fechaInicial.getDayOfWeek().equals(DayOfWeek.FRIDAY)) {
            List<Callable<Void>> salvaJugadaThreads = new ArrayList<>();
            jugadaRepository.deleteAll();
            boolean esMartes = fechaInicial.getDayOfWeek().equals(DayOfWeek.TUESDAY);
            while (fecha.isBefore(hoy)) {
                salvaJugadaThreads.add(getSalvarJugadaThread(fecha));
                if (esMartes) {
                    fecha = fecha.plus(incrementoMartesAViernes, ChronoUnit.DAYS);
                } else {
                    fecha = fecha.plus(incrementoViernesAMartes, ChronoUnit.DAYS);
                }
                esMartes = !esMartes;
            }
            ExecutorService executorService = Executors.newFixedThreadPool(4);
            try {
                executorService.invokeAll(salvaJugadaThreads);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            log.error("El día inicial no es ni jueves ni martes");
        }
    }


    /**
     * Gets salvar jugada thread.
     *
     * @param fecha the fecha
     *
     * @return the salvar jugada thread
     */
    public Callable<Void> getSalvarJugadaThread(LocalDate fecha) {
        Callable<Void> callable = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                log.info("Recuperando jugada para la fecha {}", fecha);
                JugadaVO jugadaVO = getJugadaPorFecha(fecha);
                if (null != jugadaVO) {
                    jugadaVO.setFecha(fecha);
                    jugadaRepository.save(jugadaMapper.VoToEntity(jugadaVO));
                }
                return null;
            }

        };

        return callable;
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

        return new JugadaVO(null, numeros[0], numeros[1], numeros[2], numeros[3], numeros[4], estrellas[0], estrellas[1], fecha);

    }

    /**
     * Save jugada.
     *
     * @param jugadaVO the jugada vo
     */
    public void saveJugada(JugadaVO jugadaVO) {
        jugadaRepository.save(jugadaMapper.VoToEntity(jugadaVO));
    }


    /**
     * Limpiar jugadas.
     */
    public void limpiarJugadas() {
        jugadaRepository.deleteAll();
    }



}


