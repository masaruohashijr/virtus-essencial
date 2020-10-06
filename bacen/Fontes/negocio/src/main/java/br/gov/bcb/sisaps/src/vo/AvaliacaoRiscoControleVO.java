package br.gov.bcb.sisaps.src.vo;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoGrupoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoUnidadeAtividadeEnum;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.geral.DataUtilLocalDate;

public class AvaliacaoRiscoControleVO extends ObjetoPersistenteVO {

    private EstadoARCEnum estado;
    private TipoGrupoEnum tipo;
    private Atividade atividade;
    private TipoUnidadeAtividadeEnum tipoAtividade;
    private ParametroGrupoRiscoControleVO parametroGrupoRiscoControle;
    private MatrizVO matrizVigente;
    private DateTime ultimaAtualizacao;
    private Short ordem;
    private ParametroNota notaCorec;
    private ParametroNota notaVigenteSupervisor;
    private BigDecimal valorNotaVigenteSupervisor;
    private boolean alterouNota;
    private String acao;
    private String versao;
    private String codOperadorPreenchido;
    private String codOperadorAnalise;
    private DateTime dataConclusao;
    private String nomeEs;

    public AvaliacaoRiscoControleVO() {

    }

    public AvaliacaoRiscoControleVO(Integer pk, EstadoARCEnum estado, TipoGrupoEnum tipo,
            ParametroGrupoRiscoControle parametroGrupoRiscoControle, Atividade atividade, DateTime dataConclusao) {
        this.pk = pk;
        this.tipo = tipo;
        this.estado = estado;
        setParametroGrupoRiscoControle(parametroGrupoRiscoControle);
        setAtividade(atividade);
        setMatrizVigente(getAtividade().getMatriz());
        this.dataConclusao = dataConclusao;
    }

    public AvaliacaoRiscoControleVO(Integer pk, EstadoARCEnum estado, TipoGrupoEnum tipo,
            ParametroGrupoRiscoControle parametroGrupoRiscoControle, Matriz matriz, Atividade atividade,
            DateTime ultimaAtualizacao) {
        this.pk = pk;
        this.tipo = tipo;
        this.estado = estado;
        setParametroGrupoRiscoControle(parametroGrupoRiscoControle);
        setMatrizVigente(matriz);
        setAtividade(atividade);
        this.ultimaAtualizacao = ultimaAtualizacao;
    } 

    public AvaliacaoRiscoControleVO(Integer pk, EstadoARCEnum estado, TipoGrupoEnum tipo,
            ParametroGrupoRiscoControle parametroGrupoRiscoControle, DateTime ultimaAtualizacao) {
        this.pk = pk;
        this.tipo = tipo;
        this.estado = estado;
        setParametroGrupoRiscoControle(parametroGrupoRiscoControle);
        this.ultimaAtualizacao = ultimaAtualizacao;
    }

    public AvaliacaoRiscoControleVO(Integer pk, EstadoARCEnum estado, TipoGrupoEnum tipo,
            ParametroGrupoRiscoControle parametroGrupoRiscoControle, Matriz matriz, DateTime ultimaAtualizacao) {
        this.pk = pk;
        this.tipo = tipo;
        this.estado = estado;
        setParametroGrupoRiscoControle(parametroGrupoRiscoControle);
        setMatrizVigente(matriz);
        setAtividade(new Atividade());
        this.ultimaAtualizacao = ultimaAtualizacao;
    }

    public AvaliacaoRiscoControleVO(Integer pk, EstadoARCEnum estado, TipoGrupoEnum tipo,
            ParametroGrupoRiscoControle parametroGrupoRiscoControle, Matriz matriz, Atividade atividade,
            DateTime ultimaAtualizacao, Short ordem, TipoUnidadeAtividadeEnum tipoAtividade,
            ParametroNota notaVigenteSupervisor, BigDecimal valorNotaVigenteSupervisor) {
        this.pk = pk;
        this.tipo = tipo;
        this.estado = estado;
        this.ordem = ordem;
        setParametroGrupoRiscoControle(parametroGrupoRiscoControle);
        setMatrizVigente(matriz);
        setAtividade(atividade);
        this.ultimaAtualizacao = ultimaAtualizacao;
        this.tipoAtividade = tipoAtividade;
        this.notaVigenteSupervisor = notaVigenteSupervisor;
        this.setValorNotaVigenteSupervisor(valorNotaVigenteSupervisor);
    }

    public EstadoARCEnum getEstado() {
        return estado;
    }

    public String getEstadoModificado() {
        if (EstadoARCEnum.CONCLUIDO.equals(estado)) {
            return estado.getDescricao() + " em "
                    + DataUtilLocalDate.localDateToString(new LocalDate(ultimaAtualizacao));
        }
        return estado.getDescricao();
    }

    public void setEstado(EstadoARCEnum estado) {
        this.estado = estado;
    }

    public TipoGrupoEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoGrupoEnum tipo) {
        this.tipo = tipo;
    }

    public Atividade getAtividade() {
        return atividade;
    }

    public void setAtividade(Atividade atividade) {
        AtividadeVO atividadeVO = new AtividadeVO();
        atividadeVO.setPk(atividade.getPk());
        atividadeVO.setNome(atividade.getNome());
        if (atividade.getMatriz() != null) {
            atividadeVO.setMatriz(atividade.getMatriz());
        }
        this.atividade = atividade;
    }

    public ParametroGrupoRiscoControleVO getParametroGrupoRiscoControle() {
        return parametroGrupoRiscoControle;
    }

    /**
     * Usado para o AliasToBeanResultTransformer transformar a entidade resultante da consulta em
     * VO
     * 
     * @param parametroGrupoRiscoControle
     */
    public void setParametroGrupoRiscoControle(ParametroGrupoRiscoControle parametroGrupoRiscoControle) {
        ParametroGrupoRiscoControleVO parametroVO =
                new ParametroGrupoRiscoControleVO(parametroGrupoRiscoControle.getNome(tipo),
                        parametroGrupoRiscoControle.getNomeAbreviado(), parametroGrupoRiscoControle.getDescricao(),
                        parametroGrupoRiscoControle.getEnderecoManual(), parametroGrupoRiscoControle.getOrdem());
        this.parametroGrupoRiscoControle = parametroVO;
    }

    public void setParametroGrupoRiscoControleVO(ParametroGrupoRiscoControleVO parametroGrupoRiscoControle) {
        this.parametroGrupoRiscoControle = parametroGrupoRiscoControle;
    }

    public MatrizVO getMatrizVigente() {
        return matrizVigente;
    }

    public void setMatrizVigente(Matriz matrizVigente) {
        this.matrizVigente =
                new MatrizVO(matrizVigente.getPk(), matrizVigente.getEstadoMatriz(), matrizVigente.getCiclo());
    }

    public DateTime getUltimaAtualizacao() {
        return ultimaAtualizacao;
    }

    public void setUltimaAtualizacao(DateTime ultimaAtualizacao) {
        this.ultimaAtualizacao = ultimaAtualizacao;
    }

    public Short getOrdem() {
        return ordem;
    }

    public void setOrdem(Short ordem) {
        this.ordem = ordem;
    }

    public ParametroNota getNotaCorec() {
        return notaCorec;
    }

    public void setNotaCorec(ParametroNota notaCorec) {
        this.notaCorec = notaCorec;
    }

    public boolean isAlterouNota() {
        return alterouNota;
    }

    public void setAlterouNota(boolean alterouNota) {
        this.alterouNota = alterouNota;
    }

    public TipoUnidadeAtividadeEnum getTipoAtividade() {
        return tipoAtividade;
    }

    public void setTipoAtividade(TipoUnidadeAtividadeEnum tipoAtividade) {
        this.tipoAtividade = tipoAtividade;
    }
    
    public ParametroNota getNotaVigenteSupervisor() {
        return notaVigenteSupervisor;
    }

    public void setNotaVigenteSupervisor(ParametroNota notaVigenteSupervisor) {
        this.notaVigenteSupervisor = notaVigenteSupervisor;
    }

    public BigDecimal getValorNotaVigenteSupervisor() {
        return valorNotaVigenteSupervisor;
    }

    public void setValorNotaVigenteSupervisor(BigDecimal valorNotaVigenteSupervisor) {
        this.valorNotaVigenteSupervisor = valorNotaVigenteSupervisor;
    }

    public String getAcao() {
        return acao;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }

    public String getVersao() {
        return versao;
    }

    public void setVersao(String versao) {
        this.versao = versao;
    }


    public String getCodOperadorPreenchido() {
        return codOperadorPreenchido;
    }

    public void setCodOperadorPreenchido(String codOperadorPreenchido) {
        this.codOperadorPreenchido = codOperadorPreenchido;
    }

    public String getCodOperadorAnalise() {
        return codOperadorAnalise;
    }

    public void setCodOperadorAnalise(String codOperadorAnalise) {
        this.codOperadorAnalise = codOperadorAnalise;
    }

    public DateTime getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(DateTime dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public String getNomeEs() {
        return nomeEs;
    }

    public void setNomeEs(String nomeEs) {
        this.nomeEs = nomeEs;
    }

    public String getNotaVigenteDescricaoValor() {
        ParametroNota parametroNotaVigente = getNotaVigenteSupervisor();
        BigDecimal valorNotaVigente = getValorNotaVigenteSupervisor();
        if (parametroNotaVigente != null) {
            return parametroNotaVigente.getDescricaoValor();
        } else if (valorNotaVigente != null) {
            return valorNotaVigente.compareTo(BigDecimal.ZERO) == 1 ? valorNotaVigente.toString().replace('.', ',') : "N/A";
        }
        return Constantes.ASTERISCO_A;
    }

}
