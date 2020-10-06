package br.gov.bcb.sisaps.src.vo;

import java.math.BigDecimal;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.ParametroPeso;
import br.gov.bcb.sisaps.src.dominio.ParametroTipoAtividadeNegocio;
import br.gov.bcb.sisaps.src.dominio.Unidade;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoUnidadeAtividadeEnum;
import br.gov.bcb.sisaps.util.consulta.Consulta;

public class ConsultaAtividadeVO extends Consulta<AtividadeVO> {

    private String nome;
    private BigDecimal percentualParticipacao;
    private Matriz matriz;
    private ParametroPeso parametroPeso;
    private ParametroTipoAtividadeNegocio parametroTipoAtividadeNegocio;
    private Unidade unidade;
    private TipoUnidadeAtividadeEnum tipoAtividade;
    private AvaliacaoRiscoControle avaliacaoRiscoControle;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPercentualParticipacao() {
        return percentualParticipacao;
    }

    public void setPercentualParticipacao(BigDecimal percentualParticipacao) {
        this.percentualParticipacao = percentualParticipacao;
    }

    public Matriz getMatriz() {
        return matriz;
    }

    public void setMatriz(Matriz matriz) {
        this.matriz = matriz;
    }

    public ParametroPeso getParametroPeso() {
        return parametroPeso;
    }

    public void setParametroPeso(ParametroPeso parametroPeso) {
        this.parametroPeso = parametroPeso;
    }

    public ParametroTipoAtividadeNegocio getParametroTipoAtividadeNegocio() {
        return parametroTipoAtividadeNegocio;
    }

    public void setParametroTipoAtividadeNegocio(ParametroTipoAtividadeNegocio parametroTipoAtividadeNegocio) {
        this.parametroTipoAtividadeNegocio = parametroTipoAtividadeNegocio;
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public void setUnidade(Unidade unidade) {
        this.unidade = unidade;
    }

    public TipoUnidadeAtividadeEnum getTipoAtividade() {
        return tipoAtividade;
    }

    public void setTipoAtividade(TipoUnidadeAtividadeEnum tipoAtividade) {
        this.tipoAtividade = tipoAtividade;
    }

    public AvaliacaoRiscoControle getAvaliacaoRiscoControle() {
        return avaliacaoRiscoControle;
    }

    public void setAvaliacaoRiscoControle(AvaliacaoRiscoControle avaliacaoRiscoControle) {
        this.avaliacaoRiscoControle = avaliacaoRiscoControle;
    }

}
