package br.gov.bcb.sisaps.src.mediator;

import java.io.Serializable;
import java.util.List;

import br.gov.bcb.sisaps.src.vo.ObjetoPersistenteVO;
import br.gov.bcb.sisaps.util.consulta.Consulta;
import br.gov.bcb.sisaps.util.consulta.IPaginado;

public abstract class AbstractMediatorPaginado<T extends ObjetoPersistenteVO, PK extends Serializable, //
Q extends Consulta<T>> implements IPaginado<T, PK, Q> {

    public List<T> consultar(Q consulta) {
        return getDao().consultar(consulta);
    }

    public Long total(Q consulta) {
        return getDao().total(consulta);
    }

    protected abstract IPaginado<T, PK, Q> getDao();
}
