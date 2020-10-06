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
     * Nome da propriedade que guarda a versão.
     */
    public static final String PROP_VERSAO = "versao";
    private int versao;

    /**
     * O código de versão do objeto.
     * 
     * @return A versão corrente.
     */
    @Version
    @Column(nullable = false, name = "CD_VERSAO")
    public int getVersao() {
        return versao;
    }

    /**
     * Ajusta o código de versão do objeto.
     * 
     * @param version - o novo código.
     */
    @Override
    public void setVersao(int version) {
        this.versao = version;
    }

}