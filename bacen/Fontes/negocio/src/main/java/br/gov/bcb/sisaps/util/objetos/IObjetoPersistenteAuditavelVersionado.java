package br.gov.bcb.sisaps.util.objetos;

import java.io.Serializable;

import br.gov.bcb.app.stuff.hibernate.IObjetoPersistenteAuditavel;

public interface IObjetoPersistenteAuditavelVersionado<PK extends Serializable> extends IObjetoPersistenteAuditavel<PK> {

    void setVersao(int version);
}