package org.lobo.euromillones.service.mapper;

import org.lobo.euromillones.persistence.model.Frecuencia;
import org.lobo.euromillones.service.model.FrecuenciaVO;
import org.mapstruct.Mapper;

/**
 * The interface Jugada mapper.
 */
@Mapper(componentModel = "spring")
public interface FrecuenciaMapper extends AbstractMapper<FrecuenciaVO, Frecuencia> {
}
