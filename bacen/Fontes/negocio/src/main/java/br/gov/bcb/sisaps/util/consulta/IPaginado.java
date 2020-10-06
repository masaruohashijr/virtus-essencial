package br.gov.bcb.sisaps.util.consulta;

import java.io.Serializable;
import java.util.List;

import br.gov.bcb.sisaps.src.vo.ObjetoPersistenteVO;

public interface IPaginado<T extends ObjetoPersistenteVO, PK extends Serializable, C extends Consulta<T>> {

    Integer QUANTIDADE_PADRAO = 10;

    List<T> consultar(C consulta);

    Long total(C consulta);
}