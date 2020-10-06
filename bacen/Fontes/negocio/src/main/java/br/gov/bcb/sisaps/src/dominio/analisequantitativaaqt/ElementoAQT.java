package br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.joda.time.DateTime;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.comum.excecoes.BCNegocioException;
import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.geral.SisapsExcecaoUtil;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

@Entity
@Table(name = "ELA_ELEMENTO_AQT", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = ElementoAQT.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "ELA_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "ELA_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(name = "ELA_NU_VERSAO", nullable = false))})
public class ElementoAQT extends ObjetoPersistenteAuditavelVersionado<Integer> {
    public static final String CAMPO_ID = "ELA_ID";
    private ParametroElementoAQT parametroElemento;
    private AnaliseQuantitativaAQT analiseQuantitativaAQT;
    private ParametroNotaAQT parametroNotaInspetor;
    private ParametroNotaAQT parametroNotaSupervisor;
    private String justificativaSupervisor;
    private List<ItemElementoAQT> itensElemento;
    private DateTime dataAlteracao;
    private String operadorAlteracao;

    @OneToMany(mappedBy = "elemento", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    public List<ItemElementoAQT> getItensElemento() {
        return itensElemento;
    }

    public void setItensElemento(List<ItemElementoAQT> itensElemento) {
        this.itensElemento = itensElemento;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = ParametroElementoAQT.CAMPO_ID, nullable = false)
    public ParametroElementoAQT getParametroElemento() {
        return parametroElemento;
    }

    public void setParametroElemento(ParametroElementoAQT parametroElemento) {
        this.parametroElemento = parametroElemento;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = AnaliseQuantitativaAQT.CAMPO_ID, nullable = false)
    public AnaliseQuantitativaAQT getAnaliseQuantitativaAQT() {
        return analiseQuantitativaAQT;
    }

    public void setAnaliseQuantitativaAQT(AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        this.analiseQuantitativaAQT = analiseQuantitativaAQT;
    }


    @ManyToOne
    @JoinColumn(name = "PNA_ID_INSPETOR")
    public ParametroNotaAQT getParametroNotaInspetor() {
        return parametroNotaInspetor;
    }

    public void setParametroNotaInspetor(ParametroNotaAQT parametroNotaInspetor) {
        this.parametroNotaInspetor = parametroNotaInspetor;
    }

    @ManyToOne
    @JoinColumn(name = "PNA_ID_SUPERVISOR")
    public ParametroNotaAQT getParametroNotaSupervisor() {
        return parametroNotaSupervisor;
    }

    public void setParametroNotaSupervisor(ParametroNotaAQT parametroNotaSupervisor) {
        this.parametroNotaSupervisor = parametroNotaSupervisor;
    }


    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "ELA_AN_JUST_SUPERVISOR")
    public String getJustificativaSupervisor() {
        return justificativaSupervisor;
    }

    public void setJustificativaSupervisor(String justificativaSupervisor) {
        this.justificativaSupervisor = justificativaSupervisor;
    }

    @Column(name = "ELA_DH_ALTERACAO")
    public DateTime getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(DateTime dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    @Column(name = "ELA_CD_OPER_ALTERACAO")
    public String getOperadorAlteracao() {
        return operadorAlteracao;
    }

    public void setOperadorAlteracao(String operadorAlteracao) {
        this.operadorAlteracao = operadorAlteracao;
    }

    @Transient
    public boolean possuiNotasupervisor() {
        return getParametroNotaSupervisor() != null;
    }

    @Transient
    public boolean possuiNotainspetor() {
        return getParametroNotaInspetor() != null;
    }

    @Transient
    public String getNotaSupervisor() {
        if (possuiNotasupervisor()) {
            return getParametroNotaSupervisor().getDescricaoValor();
        } else if (possuiNotainspetor()) {
            return getParametroNotaInspetor().getDescricaoValor();
        } else {
            return "";
        }
    }

    @Transient
    public String getNotaSupervisorDescricao() {
        if (possuiNotasupervisor()) {
            return getParametroNotaSupervisor().getDescricao();
        } else if (possuiNotainspetor()) {
            return getParametroNotaInspetor().getDescricao();
        } else {
            return "";
        }
    }

    @Transient
    public String getJustificativaAtualizada() {
        final String justificativaSupervisorVigente =
                this == null || this.justificativaSupervisor == null ? "" : this.justificativaSupervisor;
        return justificativaSupervisorVigente;
    }
    
    
    @Transient
    public String getNomeOperadorAlteracaoDataHora() {
        ServidorVO servidorVO = null;
        StringBuilder odh = null;
        if (getOperadorAlteracao() != null && getDataAlteracao() != null) {
            try {
                servidorVO = BcPessoaAdapter.get().buscarServidorPorLogin(getOperadorAlteracao());
                odh = new StringBuilder(servidorVO == null ? "" : servidorVO.getNome());
                odh.append(" em ");
                odh.append(this.getDataAlteracao().toString(Constantes.FORMATO_DATA_HORA_SEMSEGUNDOS));
                odh.append("h");
                return odh.toString();
            } catch (BCNegocioException e) {
                SisapsExcecaoUtil.lancarNegocioException(new ErrorMessage(e.getMessage()));
            }
        }
        return null;
    }

}
