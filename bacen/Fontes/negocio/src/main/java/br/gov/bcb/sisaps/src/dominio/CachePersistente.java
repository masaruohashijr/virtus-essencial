package br.gov.bcb.sisaps.src.dominio;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.SerializationUtils;
import org.joda.time.DateTime;

import br.gov.bcb.app.stuff.hibernate.IObjetoPersistente;
import br.gov.bcb.sisaps.integracao.ObjetoCacheavel;

@Entity
@Table(name = "CHC_CACHE_COMPONENTE", schema = "SUP")
public class CachePersistente<T extends Serializable> extends ObjetoCacheavel<T> implements IObjetoPersistente<Integer> {

    public static final String CAMPO_ID = "CHC_CD_CHAVE";
    
    private Integer pk;
    private T objeto;
    private String chave;
    private byte[] elemento;
    private DateTime dataHora;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHC_ID", nullable = false)
    public Integer getPk() {
        return pk;
    }
    
    public void setPk(Integer pk) {
        this.pk = pk;
    }
    
    @Column(name = "CHC_CD_CHAVE", nullable = false, unique = true)
    public String getChave() {
        return chave;
    }
    
    public void setChave(String chave) {
        this.chave = chave;
    }
    
    @Column(name = "CHC_BI_ELEMENTO")
    @Lob
    public byte[] getElemento() {
        return elemento;
    }    
    
    
    @Column(name = "CHC_DH_ATUALZ", nullable = false)
    public DateTime getDataHora() {
        return dataHora;
    }
    
    public void setElemento(byte[] elemento) {
        this.elemento = elemento;
    }

    public void setDataHora(DateTime dataHora) {
        this.dataHora = dataHora;
    }

    @Transient
    @Override
    public DateTime getDataHoraAtualizacao() {
        return getDataHora();
    }

    @Override
    public void setDataHoraAtualizacao(DateTime dataHora) {
        setDataHora(dataHora);
    }

    @Transient
    @SuppressWarnings("unchecked")
    @Override
    public T getObjeto() {
        if (objeto == null && getElemento() != null) {
            objeto = (T) SerializationUtils.deserialize(getElemento());
        }
        return objeto;
    }

    @Override
    public void setObjeto(T objeto) {
        this.objeto = objeto;
        this.elemento = SerializationUtils.serialize(objeto);
    }

}
