package br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.app.stuff.hibernate.type.PersistentEnumComCodigo;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfisNotificacaoEnum;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(name = "AVT_AVALIACAO_AQT", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = AvaliacaoAQT.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "AVT_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "AVT_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(name = "AVT_NU_VERSAO", nullable = false))})
public class AvaliacaoAQT extends ObjetoPersistenteAuditavelVersionado<Integer> {
    public static final String CAMPO_ID = "AVT_ID";
    private AnaliseQuantitativaAQT analiseQuantitativaAQT;
    private ParametroNotaAQT parametroNota;
    private PerfisNotificacaoEnum perfil;
    private String justificativa;


    @ManyToOne(optional = false)
    @JoinColumn(name = AnaliseQuantitativaAQT.CAMPO_ID, nullable = false)
    public AnaliseQuantitativaAQT getAnaliseQuantitativaAQT() {
        return analiseQuantitativaAQT;
    }

    public void setAnaliseQuantitativaAQT(AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        this.analiseQuantitativaAQT = analiseQuantitativaAQT;
    }

    @ManyToOne
    @JoinColumn(name = ParametroNotaAQT.CAMPO_ID)
    public ParametroNotaAQT getParametroNota() {
        return parametroNota;
    }

    public void setParametroNota(ParametroNotaAQT parametroNota) {
        this.parametroNota = parametroNota;
    }

    @Column(name = "AVT_CD_PERFIL", nullable = false, columnDefinition = "smallint")
    @Type(type = PersistentEnumComCodigo.CLASS_NAME, parameters = {@Parameter(name = "enumClass", value = PerfisNotificacaoEnum.CLASS_NAME)})
    public PerfisNotificacaoEnum getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfisNotificacaoEnum perfil) {
        this.perfil = perfil;
    }

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "AVT_AN_JUSTIFICATIVA")
    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    @Transient
    public String getValorFormatado() {
        if (parametroNota == null) {
            return "";
        } else {
            return parametroNota.getDescricaoValor();
        }
    }

}
