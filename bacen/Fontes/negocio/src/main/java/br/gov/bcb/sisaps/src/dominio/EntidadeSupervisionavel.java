/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.src.dominio;

import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;
import br.gov.bcb.sisaps.util.objetos.ObjetoVersionadorAuditavelVersionado;

@Entity
@Table(name = "ENS_ENTIDADE_SUPERVISIONAVEL", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = EntidadeSupervisionavel.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "ENS_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "ENS_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(nullable = false, name = "ENS_NU_VERSAO"))})
public class EntidadeSupervisionavel extends ObjetoVersionadorAuditavelVersionado {
    public static final String CAMPO_ID = "ENS_ID";
    public static final int TAMANHO_NOME_LOCALIZACAO = 30;
    public static final int TAMANHO_SEGMENTO = 60;
    public static final int TAMANHO_PORTE = 10;
    public static final int TAMANHO_NOME_ES = 200;
    public static final int TAMANHO_MATRICULA_CNPJ = 8;
    private static final String PREFIXO_C00 = "C00";
    private static final long serialVersionUID = 1L;
    private String nome;
    private String conglomeradoOuCnpj;
    private SimNaoEnum pertenceSrc;
    private ParametroPrioridade prioridade;
    private String localizacao;
    private Date dataInclusao;
    private Metodologia metodologia;
    private String segmento;
    private String porte;
    private Short quantidadeAnosPrevisaoCorec;
    private List<Ciclo> ciclos;

    @Column(name = "ENS_NM", nullable = false, length = TAMANHO_NOME_ES)
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Column(name = "ENS_CD_CNPJ", nullable = false, length = TAMANHO_MATRICULA_CNPJ)
    public String getConglomeradoOuCnpj() {
        return conglomeradoOuCnpj;
    }

    public void setConglomeradoOuCnpj(String conglomeradoOuCnpj) {
        this.conglomeradoOuCnpj = conglomeradoOuCnpj;
    }

    @Column(name = "ENS_IB_PERTENCE_SRC", nullable = false, columnDefinition = "smallint")
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType", parameters = {@Parameter(name = "enumClass", value = SimNaoEnum.CLASS_NAME)})
    public SimNaoEnum getPertenceSrc() {
        return pertenceSrc;
    }

    public void setPertenceSrc(SimNaoEnum pertenceSrc) {
        this.pertenceSrc = pertenceSrc;
    }

    @ManyToOne
    @JoinColumn(name = ParametroPrioridade.CAMPO_ID)
    public ParametroPrioridade getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(ParametroPrioridade prioridade) {
        this.prioridade = prioridade;
    }

    @Column(name = "ENS_DS_LOCALIZACAO", nullable = false, length = TAMANHO_NOME_LOCALIZACAO)
    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    @Column(name = "ENS_DT_INCLUSAO", nullable = false, columnDefinition = "date")
    public Date getDataInclusao() {
        return dataInclusao;
    }

    public void setDataInclusao(Date dataInclusao) {
        this.dataInclusao = dataInclusao;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = Metodologia.CAMPO_ID, nullable = false)
    public Metodologia getMetodologia() {
        return metodologia;
    }

    public void setMetodologia(Metodologia metodologia) {
        this.metodologia = metodologia;
    }

    @Column(name = "ENS_DS_SEGMENTO", nullable = true, length = TAMANHO_SEGMENTO)
    public String getSegmento() {
        return segmento;
    }

    public void setSegmento(String segmento) {
        this.segmento = segmento;
    }

    @Column(name = "ENS_DS_PORTE", nullable = true, length = TAMANHO_PORTE)
    public String getPorte() {
        return porte;
    }

    public void setPorte(String porte) {
        this.porte = porte;
    }

    @Transient
    public String getUnidadeDaLocalizacao() {
        return localizacao.split(SisapsUtil.BARRA)[0];
    }

    @Override
    @ManyToOne
    @JoinColumn(name = VersaoPerfilRisco.CAMPO_ID, nullable = true)
    public VersaoPerfilRisco getVersaoPerfilRisco() {
        return versaoPerfilRisco;
    }

    @Column(name = "ENS_QT_ANOS_PREVISAO_COREC", nullable = true)
    public Short getQuantidadeAnosPrevisaoCorec() {
        return quantidadeAnosPrevisaoCorec;
    }

    public void setQuantidadeAnosPrevisaoCorec(Short quantidadeAnosPrevisaoCorec) {
        this.quantidadeAnosPrevisaoCorec = quantidadeAnosPrevisaoCorec;
    }

    @OneToMany(mappedBy = "entidadeSupervisionavel", fetch = FetchType.LAZY)
    public List<Ciclo> getCiclos() {
        return ciclos;
    }

    public void setCiclos(List<Ciclo> ciclos) {
        this.ciclos = ciclos;
    }

    @Transient
    public String getNomeConglomeradoFormatado() {
        String nome = this.nome == null ? "" : this.nome;
        String cnpj = this.conglomeradoOuCnpj == null ? "" : this.conglomeradoOuCnpj;
        if (cnpj.startsWith(PREFIXO_C00)) {
            cnpj = cnpj.replaceFirst(PREFIXO_C00, "");
        }
        return nome + (this.conglomeradoOuCnpj == null ? "" : " (" + cnpj + ") ");
    }
    
    @Transient
    public String getNomeSupervisor() {
        ServidorVO servidor = CicloMediator.get().buscarChefeAtual(localizacao);
        return servidor == null ? "" : servidor.getNome();
    }

}
