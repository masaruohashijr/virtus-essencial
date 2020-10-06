package br.gov.bcb.sisaps.util.objetos;

import java.io.Serializable;

import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.comum.excecoes.BCNegocioException;
import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;

public class ObjetoPersistenteAuditavelSisAps<PK extends Serializable> extends ObjetoPersistenteAuditavel<PK>
        implements IObjetoPersistenteAuditavelManipulavel {
    private static final String H = "h";
    private boolean isAlterarDataUltimaAtualizacao = true;
    
    @Override
    @Transient
    public void setAlterarDataUltimaAtualizacao(boolean alterarData) {
        isAlterarDataUltimaAtualizacao = alterarData;
    }

    @Override
    @Transient
    public boolean isAlterarDataUltimaAtualizacao() {
        return isAlterarDataUltimaAtualizacao;
    }
    
    
    @Transient
    public String getOperador() {
        StringBuilder odh = new StringBuilder();
        odh.append(StringUtils.trim(this.getOperadorAtualizacao()));
        return odh.toString();
    }

    @Transient
    public String getOperadorDataHora() {
        StringBuilder odh = new StringBuilder(getOperador());
        odh.append(" ");
        odh.append(this.getUltimaAtualizacao().toString(Constantes.FORMATO_DATA_HORA));
        return odh.toString();
    }

    @Transient
    public String getNomeOperadorDataHora() {
        return SisapsUtil.getNomeOperadorDataHora(this);
    }

    @Transient
    public String getNomeOperador() {
        ServidorVO servidorVO = null;
        StringBuilder odh = null;
        if (getOperadorAtualizacao() != null && getUltimaAtualizacao() != null) {
            try {
                servidorVO = BcPessoaAdapter.get().buscarServidorPorLogin(getOperadorAtualizacao());
                odh = new StringBuilder(servidorVO == null ? "" : servidorVO.getNome());
                return odh.toString();
            } catch (BCNegocioException e) {
                return "";
            }
        }
        return null;
    }

    @Transient
    public String getDataHoraFormatada() {
        return this.getUltimaAtualizacao() == null ? "" : this.getUltimaAtualizacao().toString(
                Constantes.FORMATO_DATA_HORA);
    }

    @Transient
    public String getDataHoraFormatada(DateTime date) {
        return date == null ? "" : date.toString(Constantes.FORMATO_DATA_HORA);
    }

    @Transient
    public String getDataFormatada() {
        return this.getUltimaAtualizacao() == null ? "" : this.getUltimaAtualizacao().toString(
                Constantes.FORMATO_DATA_COM_BARRAS);
    }

    @Transient
    public String getHoraFormatada() {
        return this.getUltimaAtualizacao() == null ? "" : this.getUltimaAtualizacao().toString(Constantes.FORMATO_HORA);
    }

    @Transient
    public String getData(DateTime data) {
        return data == null ? "" : data.toString(Constantes.FORMATO_DATA_HORA_SEMSEGUNDOS) + H;
    }


}
