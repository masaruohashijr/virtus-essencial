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
package br.gov.bcb.sisaps.src.vo;

import javax.persistence.Transient;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SituacaoFuncionalEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SubstitutoEventualEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SubstitutoPrazoCertoEnum;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;

public class RegraPerfilAcessoVO extends ObjetoPersistenteVO {

    private static final long serialVersionUID = 1L;

    private PerfilAcessoEnum perfilAcesso;
    private String localizacao;
    private SimNaoEnum localizacoesSubordinadas;
    private String codigoFuncao;
    private SubstitutoEventualEnum substitutoEventualFuncao;
    private SubstitutoPrazoCertoEnum substitutoPrazoCerto;
    private SituacaoFuncionalEnum situacao;
    private String matricula;

    public PerfilAcessoEnum getPerfilAcesso() {
        return perfilAcesso;
    }

    public void setPerfilAcesso(PerfilAcessoEnum perfilAcesso) {
        this.perfilAcesso = perfilAcesso;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public SimNaoEnum getLocalizacoesSubordinadas() {
        return localizacoesSubordinadas;
    }

    public void setLocalizacoesSubordinadas(SimNaoEnum localizacoesSubordinadas) {
        this.localizacoesSubordinadas = localizacoesSubordinadas;
    }

    public String getCodigoFuncao() {
        return codigoFuncao;
    }

    public void setCodigoFuncao(String codigoFuncao) {
        this.codigoFuncao = codigoFuncao;
    }

    public SubstitutoEventualEnum getSubstitutoEventualFuncao() {
        return substitutoEventualFuncao;
    }

    public void setSubstitutoEventualFuncao(SubstitutoEventualEnum substitutoEventualFuncao) {
        this.substitutoEventualFuncao = substitutoEventualFuncao;
    }

    public SubstitutoPrazoCertoEnum getSubstitutoPrazoCerto() {
        return substitutoPrazoCerto;
    }

    public void setSubstitutoPrazoCerto(SubstitutoPrazoCertoEnum substitutoPrazoCerto) {
        this.substitutoPrazoCerto = substitutoPrazoCerto;
    }

    public SituacaoFuncionalEnum getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoFuncionalEnum situacao) {
        this.situacao = situacao;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    @Transient
    public String getMatriculaFormatada() {
        return Util.formatarMatricula(matricula);
    }

    @Transient
    public String getDescricaoLocalizacoesSubordinadas() {
        if (localizacoesSubordinadas == null) {
            return "";
        } else {
            if (SimNaoEnum.SIM.equals(localizacoesSubordinadas)) {
                return "Incluir";
            } else {
                return "Não incluir";
            }
        }
    }

}
