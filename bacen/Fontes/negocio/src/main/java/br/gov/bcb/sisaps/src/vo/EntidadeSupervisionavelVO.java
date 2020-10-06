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

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Transient;

import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.dominio.ParametroPrioridade;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;

public class EntidadeSupervisionavelVO extends ObjetoPersistenteVO {

    private static final long serialVersionUID = 1L;

    private String nome;
    private String conglomeradoOuCnpj;
    private ParametroPrioridade prioridade;
    private Date dataInclusao;
    private MetodologiaVO metodologia;
    private String localizacao;
    private Integer pkVersaoPerfilRisco;
    private String ciclos;
    private String nomeSupervisorTitular;
    private Date corecPrevisto;
    private Date inicioCiclo;

    public EntidadeSupervisionavelVO() {
        super();
    }

    public EntidadeSupervisionavelVO(Integer pk, String nome, String conglomeradoOuCnpj) {
        this.pk = pk;
        this.nome = nome;
        this.conglomeradoOuCnpj = conglomeradoOuCnpj;
    }

    @Override
    public Integer getPk() {
        return pk;
    }

    @Override
    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getConglomeradoOuCnpj() {
        return conglomeradoOuCnpj;
    }

    public void setConglomeradoOuCnpj(String conglomeradoOuCnpj) {
        this.conglomeradoOuCnpj = conglomeradoOuCnpj;
    }

    public ParametroPrioridade getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(ParametroPrioridade prioridade) {
        this.prioridade = prioridade;
    }

    public Date getDataInclusao() {
        return dataInclusao;
    }

    public void setDataInclusao(Date dataInclusao) {
        this.dataInclusao = dataInclusao;
    }

    public MetodologiaVO getMetodologia() {
        return metodologia;
    }

    public void setMetodologia(MetodologiaVO metodologia) {
        this.metodologia = metodologia;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    @Transient
    public String getUnidadeDaLocalizacao() {
        return localizacao.split(SisapsUtil.BARRA)[0];
    }
    
    public Integer getPkVersaoPerfilRisco() {
        return pkVersaoPerfilRisco;
    }

    public void setPkVersaoPerfilRisco(Integer pkVersaoPerfilRisco) {
        this.pkVersaoPerfilRisco = pkVersaoPerfilRisco;
    }

    @Transient
    public String getNomeSupervisor() {
        return getNomeSupervisor(null);
    }
    
    @Transient
    public String getNomeSupervisor(Date dataBase) {
        ServidorVO servidor = CicloMediator.get().buscarChefeAtual(localizacao, dataBase);
        return servidor == null ? "" : servidor.getNome();
    }

    public String getCiclos() {
        return ciclos;
    }

    public void setCiclos(String ciclos) {
        this.ciclos = ciclos;
    }

    public Short getValorCiclo() {
        if (ciclos == null || ciclos.equals(Constantes.VAZIO)) {
            return null;
        }
        return Short.parseShort(ciclos);
    }

    @Transient
    public String getNomeSupervisorTitular() {
        return nomeSupervisorTitular;
    }

    public void setNomeSupervisorTitular(String nomeSupervisorTitular) {
        this.nomeSupervisorTitular = nomeSupervisorTitular;
    }

    public Date getCorecPrevisto() {
        return corecPrevisto;
    }

    public void setCorecPrevisto(Date corecPrevisto) {
        this.corecPrevisto = corecPrevisto;
    }

    public Date getInicioCiclo() {
        return inicioCiclo;
    }

    public void setInicioCiclo(Date inicioCiclo) {
        this.inicioCiclo = inicioCiclo;
    }
    
    public String getDataInicioFormatada() {
        return this.inicioCiclo == null ? "" : new SimpleDateFormat(Constantes.FORMATO_DATA_COM_BARRAS)
                .format(this.inicioCiclo);
    }

    public String getDataPrevisaoFormatada() {
        return this.corecPrevisto == null ? "" : new SimpleDateFormat(Constantes.FORMATO_DATA_COM_BARRAS)
                .format(this.corecPrevisto);
    }
    
    /*public String getCorecPrevisto() {
        return corecPrevisto;
    }
    
    public void setCorecPrevisto(String corecPrevisto) {
        this.corecPrevisto = corecPrevisto;
    }

    public String getInicioCiclo() {
        return inicioCiclo;
    }

    public void setInicioCiclo(String inicioCiclo) {
        this.inicioCiclo = inicioCiclo;
    }*/

}
