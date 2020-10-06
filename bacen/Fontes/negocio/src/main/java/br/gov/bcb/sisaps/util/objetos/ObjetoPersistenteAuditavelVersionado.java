package br.gov.bcb.sisaps.util.objetos;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@SuppressWarnings("serial")
@MappedSuperclass
public class ObjetoPersistenteAuditavelVersionado<PK extends Serializable> extends ObjetoPersistenteAuditavelSisAps<PK>
        implements IObjetoPersistenteAuditavelVersionado<PK> {

    /**
     * Nome da propriedade que guarda a vers�o.
     */
    public static final String PROP_VERSAO = "versao";
    private int versao;

    /**
     * O c�digo de vers�o do objeto.
     * 
     * @return A vers�o corrente.
     */
    @Version
    @Column(nullable = false, name = "CD_VERSAO")
    public int getVersao() {
        return versao;
    }

    /**
     * Ajusta o c�digo de vers�o do objeto.
     * 
     * @param version - o novo c�digo.
     */
    @Override
    public void setVersao(int version) {
        this.versao = version;
    }

}