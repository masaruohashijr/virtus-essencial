package br.gov.bcb.sisaps.src.dominio;

import java.util.ArrayList;
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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
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
@Table(name = "ELE_ELEMENTO_ARC", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = Elemento.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "ELE_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "ELE_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(name = "ELE_NU_VERSAO", nullable = false))})
public class Elemento extends ObjetoPersistenteAuditavelVersionado<Integer> {
    public static final String CAMPO_ID = "ELE_ID";
    private ParametroElemento parametroElemento;
    private AvaliacaoRiscoControle avaliacaoRiscoControle;
    private ParametroNota parametroNotaInspetor;
    private ParametroNota parametroNotaSupervisor;
    private String justificativaSupervisor;
    private List<ItemElemento> itensElemento = new ArrayList<ItemElemento>();
    private DateTime dataAlteracao;
    private String operadorAlteracao;

    @OneToMany(mappedBy = "elemento", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    public List<ItemElemento> getItensElemento() {
        return itensElemento;
    }

    public void setItensElemento(List<ItemElemento> itensElemento) {
        this.itensElemento = itensElemento;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = ParametroElemento.CAMPO_ID, nullable = false)
    public ParametroElemento getParametroElemento() {
        return parametroElemento;
    }

    public void setParametroElemento(ParametroElemento parametroElemento) {
        this.parametroElemento = parametroElemento;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = AvaliacaoRiscoControle.CAMPO_ID, nullable = false)
    public AvaliacaoRiscoControle getAvaliacaoRiscoControle() {
        return avaliacaoRiscoControle;
    }

    public void setAvaliacaoRiscoControle(AvaliacaoRiscoControle avaliacaoRiscoControle) {
        this.avaliacaoRiscoControle = avaliacaoRiscoControle;
    }

    @ManyToOne
    @JoinColumn(name = "PNO_ID_INSPETOR")
    public ParametroNota getParametroNotaInspetor() {
        return parametroNotaInspetor;
    }

    public void setParametroNotaInspetor(ParametroNota parametroNotaInspetor) {
        this.parametroNotaInspetor = parametroNotaInspetor;
    }

    @ManyToOne
    @JoinColumn(name = "PNO_ID_SUPERVISOR")
    public ParametroNota getParametroNotaSupervisor() {
        return parametroNotaSupervisor;
    }

    public void setParametroNotaSupervisor(ParametroNota parametroNotaSupervisor) {
        this.parametroNotaSupervisor = parametroNotaSupervisor;
    }

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "ELE_AN_JUST_SUPERVISOR")
    public String getJustificativaSupervisor() {
        return justificativaSupervisor;
    }

    public void setJustificativaSupervisor(String justificativaSupervisor) {
        this.justificativaSupervisor = justificativaSupervisor;
    }

    @Column(name = "ELE_DH_ALTERACAO")
    public DateTime getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(DateTime dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    @Column(name = "ELE_CD_OPER_ALTERACAO")
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
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getPk()).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Elemento) {
            Elemento elemento = (Elemento) obj;
            return new EqualsBuilder().append(getPk(), elemento.getPk()).isEquals();
        }
        return false;
    }

}
