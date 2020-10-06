/*
 * Sistema APS
 * Usuario.java
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacoes proprietarias.
 */
package br.gov.bcb.sisaps.integracao.seguranca;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import br.gov.bcb.app.stuff.seguranca.ProvedorInformacoesUsuario;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfisNotificacaoEnum;

public class Usuario implements Serializable {

    private static final Set<String> PERFIS;

    private static final String SEPARADOR_UNID_LOGIN = ".";
    private static final int COMPRIMENTO_UNIDADE = 5;
    private static final int COMPRIMENTO_DEPENDENCIA = 4;
    private static final int MAX_LOGIN_SIZE = 10;

    static {
        PERFIS = new HashSet<String>();
        PERFIS.add(Perfis.RATING_TRANSACAO_SUPERVISOR);
        PERFIS.add(Perfis.RATING_TRANSACAO_GERENTE_EXECUTIVO);
        PERFIS.add(Perfis.RATING_TRANSACAO_GERENTE_TECNICO);
        PERFIS.add(Perfis.RATING_TRANSACAO_COMITE);
        PERFIS.add(Perfis.RATING_TRANSACAO_ADMINISTRADOR);
        PERFIS.add(Perfis.RATING_TRANSACAO_ANALISTA_ECONOMICO_FINANCEIRO);
        PERFIS.add(Perfis.RATING_TRANSACAO_REVISOR_ECONOMICO_FINANCEIRO);
        PERFIS.add(Perfis.RATING_TRANSACAO_CONSULTOR_METODOLOGIA);
        PERFIS.add(Perfis.RATING_TRANSACAO_GERENTE_REVISOR);
        PERFIS.add(Perfis.RATING_TRANSACAO_REVISOR);
        PERFIS.add(Perfis.RATING_TRANSACAO_GERENTE_CONSULTOR_ESPECIALISTA);
        PERFIS.add(Perfis.RATING_TRANSACAO_CONSULTOR_ESPECIALISTA);
        PERFIS.add(Perfis.RATING_TRANSACAO_INSPETOR);
    }
    private String login;
    private String unidade;
    private String matricula;
    private PerfisNotificacaoEnum perfilAtual;
    private String dependencia;
    private Set<String> perfis = new HashSet<String>();

    public Usuario(ProvedorInformacoesUsuario provider) {
        preencherPerfis(provider);
        preencherUnidadeOperador(provider);
    }

    public Set<String> getPerfis() {
        return perfis;
    }

    public String getLogin() {
        return login;
    }

    public String getUnidade() {
        return unidade;
    }

    public String getDependencia() {
        return dependencia;
    }

    public String getUnidadeLogin() {
        return getUnidade() + SEPARADOR_UNID_LOGIN + getLogin();
    }

    public String getUnidadeDepenciaLogin() {
        return getUnidade() + getDependencia() + SEPARADOR_UNID_LOGIN + getLogin();
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    private void preencherUnidadeOperador(ProvedorInformacoesUsuario provider) {
        login = provider.getLogin();
        if (login != null) {
            login = login.replace("_", SEPARADOR_UNID_LOGIN);
            StringTokenizer tokensUser = new StringTokenizer(login, SEPARADOR_UNID_LOGIN);
            String unidadeDependencia = tokensUser.nextToken();
            unidade = unidadeDependencia.trim().substring(0, COMPRIMENTO_UNIDADE);

            if (unidadeDependencia.trim().length() > COMPRIMENTO_UNIDADE) {
                dependencia = unidadeDependencia.trim().substring(COMPRIMENTO_UNIDADE, //
                        COMPRIMENTO_UNIDADE + COMPRIMENTO_DEPENDENCIA);
            }
            login = tokensUser.nextToken();

            if (login.length() > MAX_LOGIN_SIZE) {
                login = login.substring(0, MAX_LOGIN_SIZE);
            }
        }
    }

    private void preencherPerfis(ProvedorInformacoesUsuario provider) {
        for (String role : PERFIS) {
            if (provider.isUserInRole(role)) {
                perfis.add(role);
            }
        }
    }

    public PerfisNotificacaoEnum getPerfilAtual() {
        return perfilAtual;
    }

    public void setPerfilAtual(PerfisNotificacaoEnum perfilAtual) {
        this.perfilAtual = perfilAtual;
    }

}
