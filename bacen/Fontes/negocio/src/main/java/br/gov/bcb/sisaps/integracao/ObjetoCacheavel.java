package br.gov.bcb.sisaps.integracao;

import javax.persistence.Transient;

import org.joda.time.DateTime;

public abstract class ObjetoCacheavel<T>{


	@Transient
	public abstract DateTime getDataHoraAtualizacao();

	public abstract void setDataHoraAtualizacao(DateTime dataHora);

	@Transient
	public abstract T getObjeto();

	public abstract void setObjeto(T objeto);
	
	
}
