package org.lobo.euromillones.service.mapper;

import org.lobo.euromillones.persistence.model.Jugada;
import org.lobo.euromillones.service.model.JugadaVO;
import org.mapstruct.Mapper;

/**
 * The interface Jugada mapper.
 */
@Mapper(componentModel = "spring")
public interface JugadaMapper extends AbstractMapper<JugadaVO, Jugada> {
}
