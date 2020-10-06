package br.gov.bcb.sisaps.src.dominio;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SituacaoFuncionalEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SubstitutoEventualEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SubstitutoPrazoCertoEnum;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(name = "RPA_REGRA_PERFIL_ACESSO", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = RegraPerfilAcesso.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "RPA_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "RPA_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(nullable = false, name = "RPA_NU_VERSAO"))})
public class RegraPerfilAcesso extends ObjetoPersistenteAuditavelVersionado<Integer> {

    public static final String CAMPO_ID = "RPA_ID";

    private static final String ENUM_CLASS = "enumClass";

    private static final int TAMANHO_MATRICULA = 8;

    private static final int TAMANHO_LOCALIZACAO = 30;

    private static final String DEFINICAO_SMALLINT = "smallint";

    private PerfilAcessoEnum perfilAcesso;
    private String localizacao;
    private SimNaoEnum localizacoesSubordinadas;
    private String codigoFuncao;
    private SubstitutoEventualEnum substitutoEventualFuncao;
    private SubstitutoPrazoCertoEnum substitutoPrazoCerto;
    private SituacaoFuncionalEnum situacao;
    private String matricula;

    @Column(name = "RPA_CD_PERFIL", nullable = false, columnDefinition = DEFINICAO_SMALLINT)
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumStringUserType", parameters = {@Parameter(name = ENUM_CLASS, value = PerfilAcessoEnum.CLASS_NAME)})
    public PerfilAcessoEnum getPerfilAcesso() {
        return perfilAcesso;
    }

    public void setPerfilAcesso(PerfilAcessoEnum perfilAcesso) {
        this.perfilAcesso = perfilAcesso;
    }

    @Column(name = "RPA_DS_LOCALIZACAO", length = TAMANHO_LOCALIZACAO)
    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    @Column(name = "RPA_IB_SUB_LOCALIZACAO", columnDefinition = DEFINICAO_SMALLINT)
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType", parameters = {@Parameter(name = ENUM_CLASS, value = SimNaoEnum.CLASS_NAME)})
    public SimNaoEnum getLocalizacoesSubordinadas() {
        return localizacoesSubordinadas;
    }

    public void setLocalizacoesSubordinadas(SimNaoEnum localizacoesSubordinadas) {
        this.localizacoesSubordinadas = localizacoesSubordinadas;
    }

    @Column(name = "RPA_CD_FUNCAO", columnDefinition = "character(10)")
    public String getCodigoFuncao() {
        return codigoFuncao;
    }

    public void setCodigoFuncao(String codigoFuncao) {
        this.codigoFuncao = codigoFuncao;
    }

    @Column(name = "RPA_CD_SUBST_EVENTUAL_FUNCAO", columnDefinition = DEFINICAO_SMALLINT)
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumStringUserType", parameters = {@Parameter(name = ENUM_CLASS, value = SubstitutoEventualEnum.CLASS_NAME)})
    public SubstitutoEventualEnum getSubstitutoEventualFuncao() {
        return substitutoEventualFuncao;
    }

    public void setSubstitutoEventualFuncao(SubstitutoEventualEnum substitutoEventualFuncao) {
        this.substitutoEventualFuncao = substitutoEventualFuncao;
    }

    @Column(name = "RPA_CD_SUBST_PRAZO_CERTO", columnDefinition = DEFINICAO_SMALLINT)
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumStringUserType", parameters = {@Parameter(name = ENUM_CLASS, value = SubstitutoPrazoCertoEnum.CLASS_NAME)})
    public SubstitutoPrazoCertoEnum getSubstitutoPrazoCerto() {
        return substitutoPrazoCerto;
    }

    public void setSubstitutoPrazoCerto(SubstitutoPrazoCertoEnum substitutoPrazoCerto) {
        this.substitutoPrazoCerto = substitutoPrazoCerto;
    }

    @Column(name = "RPA_CD_SITUACAO", columnDefinition = DEFINICAO_SMALLINT)
    @Type(type = "br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumIntegerUserType", parameters = {@Parameter(name = ENUM_CLASS, value = SituacaoFuncionalEnum.CLASS_NAME)})
    public SituacaoFuncionalEnum getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoFuncionalEnum situacao) {
        this.situacao = situacao;
    }

    @Column(name = "RPA_CD_MATRICULA", length = TAMANHO_MATRICULA, columnDefinition = "character(8)")
    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

}
