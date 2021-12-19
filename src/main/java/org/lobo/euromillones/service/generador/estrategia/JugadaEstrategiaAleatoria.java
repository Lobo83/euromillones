package org.lobo.euromillones.service.generador.estrategia;

import org.lobo.euromillones.service.EstadisticaService;
import org.lobo.euromillones.service.generador.model.EstrategiaJugadaVO;
import org.lobo.euromillones.service.generador.model.TipoEstrategia;
import org.lobo.euromillones.service.model.JugadaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Jugada estrategia aleatoria.
 */
@Component
public class JugadaEstrategiaAleatoria implements JugadaEstrategia {

    @Autowired
    private EstadisticaService estadisticaService;


    @Override
    public List<JugadaVO> generarJugadas(EstrategiaJugadaVO estrategiaJugadaVO) {
        List<JugadaVO> result = new ArrayList<>();
        Integer numeroJugadas = estrategiaJugadaVO.getNumeroJugadas();
        while ((numeroJugadas--) > 0) {
            result.add(obtenerJugada());
        }
        return result;
    }

    private JugadaVO obtenerJugada() {
        HttpURLConnection connection = null;
        JugadaVO jugada = null;
        try {
            //Create connection
            URL url =
                new URL("https://www.euro-millions.com/includes/ajax?Mode=GenerateRandomNumber&LotteryID=1");
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

            jugada = parseJugada(respuesta.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return jugada;
    }

    private JugadaVO parseJugada(String s) {
        String[] jugadaArray = s.split(",");
        List<String> parteNumerica =
            Arrays.asList(jugadaArray).subList(0, 5).stream().map(numero -> String.format("%02d", Integer.valueOf(numero))).collect(Collectors.toList());
        List<String> parteEstrella =
            Arrays.asList(jugadaArray).subList(5, 7).stream().map(numero -> String.format("%02d", Integer.valueOf(numero))).collect(Collectors.toList());
        Collections.sort(parteNumerica);
        Collections.sort(parteEstrella);
        return new JugadaVO(null, parteNumerica.get(0), parteNumerica.get(1), parteNumerica.get(2), parteNumerica.get(3), parteNumerica.get(4), parteEstrella.get(0), parteEstrella.get(1), null);


    }

    @Override
    public TipoEstrategia getTipoEstrategia() {
        return TipoEstrategia.ALEATORIA;
    }
}
