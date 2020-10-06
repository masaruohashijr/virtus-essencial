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

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.ParametroPeso;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoLinhaAtividadeVOEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoUnidadeAtividadeEnum;
import br.gov.bcb.sisaps.util.Constantes;

public class LinhaAtividadeVO extends ObjetoPersistenteVO {

    private static final String FILHO = "&nbsp;&nbsp;&nbsp;";
    private static final long serialVersionUID = 1L;
    private Integer sequencial;
    private String nome;
    private TipoLinhaAtividadeVOEnum tipo;
    private TipoUnidadeAtividadeEnum atividade;
    private boolean filho;
    private Integer pkMatriz;
    private String nomeParametroTipoAtividade;
    private ParametroPeso parametroPeso;
    private List<LinhaAtividadeVO> filhos;
    private String corCelula;
    private Integer pkAtividade;

    public LinhaAtividadeVO() {
        super();
    }

    public LinhaAtividadeVO(Integer pk) {
        this.pk = pk;
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

    public TipoLinhaAtividadeVOEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoLinhaAtividadeVOEnum tipo) {
        this.tipo = tipo;
    }

    public TipoUnidadeAtividadeEnum getAtividade() {
        return atividade;
    }

    public void setAtividade(TipoUnidadeAtividadeEnum atividade) {
        this.atividade = atividade;
    }

    public String nomeFormatado() {
        StringBuffer nomeFormatado = new StringBuffer();
        nomeFormatado.append(FILHO);
        if (filho) {
            nomeFormatado.append(FILHO);
            nomeFormatado.append(FILHO);
            if (TipoLinhaAtividadeVOEnum.ARC.equals(tipo)) {
                nomeFormatado.append(FILHO);
                nomeFormatado.append(FILHO);
            }
        }
        nomeFormatado.append(tipo.getDescricao());
        nomeFormatado.append(atividade.getSigla());
        nomeFormatado.append(Constantes.SEPARADOR_HIFEN);
        nomeFormatado.append(nome);
        return nomeFormatado.toString();

    }

    public String nomeFormatadoVigente() {
        StringBuffer nomeFormatado = new StringBuffer();
        nomeFormatado.append(FILHO);
        if (filho) {
            nomeFormatado.append(FILHO);
            nomeFormatado.append(FILHO);
            if (TipoLinhaAtividadeVOEnum.ARC.equals(tipo)) {
                nomeFormatado.append(FILHO);
                nomeFormatado.append(FILHO);
            }
        }
        nomeFormatado.append(nome);
        return nomeFormatado.toString();

    }

    public String grupoRisco() {
        return "";
    }

    public boolean isFilho() {
        return filho;
    }

    public void setFilho(boolean filho) {
        this.filho = filho;
    }

    public Integer getSequencial() {
        return sequencial;
    }

    public void setSequencial(Integer sequencial) {
        this.sequencial = sequencial;
    }

    public Integer getPkMatriz() {
        return pkMatriz;
    }

    public void setPkMatriz(Integer pkMatriz) {
        this.pkMatriz = pkMatriz;
    }

    public String getNomeParametroTipoAtividade() {
        return nomeParametroTipoAtividade;
    }

    public void setNomeParametroTipoAtividade(String nomeParametroTipoAtividade) {
        this.nomeParametroTipoAtividade = nomeParametroTipoAtividade;
    }

    public ParametroPeso getParametroPeso() {
        return parametroPeso;
    }

    public void setParametroPeso(ParametroPeso parametroPeso) {
        this.parametroPeso = parametroPeso;
    }

    public List<LinhaAtividadeVO> getFilhos() {
        return filhos;
    }

    public void setFilhos(List<LinhaAtividadeVO> filhos) {
        this.filhos = filhos;
    }

    public String getCorCelula() {
        return corCelula;
    }

    public void setCorCelula(String corCelula) {
        this.corCelula = corCelula;
    }

    public Integer getPkAtividade() {
        return pkAtividade;
    }

    public void setPkAtividade(Integer pkAtividade) {
        this.pkAtividade = pkAtividade;
    }
    
    

}
