package org.lobo.euromillones.persistence.repository;

import org.lobo.euromillones.persistence.model.Jugada;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * The interface Jugada repository.
 */
public interface JugadaRepository extends CrudRepository<Jugada, Long> {



    List<Jugada> findByFecha(LocalDate fecha);

    List<Jugada> findByFechaGreaterThanEqual(LocalDate fecha);

    List<Jugada> findByFechaGreaterThanEqualAndFechaLessThanEqual(LocalDate fechaInicial, LocalDate fechaFinal);
}
