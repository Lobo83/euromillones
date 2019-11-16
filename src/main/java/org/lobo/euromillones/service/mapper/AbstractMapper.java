package org.lobo.euromillones.service.mapper;

import java.util.List;

/**
 * The interface Abstract mapper.
 *
 * @param <VO> the VO Type
 * @param <E>  the Entity Type
 */
public interface AbstractMapper<VO, E> {

    /**
     * Entity to vo vo.
     *
     * @param e the e
     * @return the vo
     */
    VO entityToVO(E e);

    /**
     * Vo to entity e.
     *
     * @param vo the vo
     * @return the e
     */
    E VoToEntity(VO vo);

    /**
     * Entity to vo list.
     *
     * @param e the e
     * @return the list
     */
    List<VO> entityToVO(List<E> e);

    /**
     * Vo to entity list.
     *
     * @param vo the vo
     * @return the list
     */
    List<E> VoToEntity(List<VO> vo);

}
