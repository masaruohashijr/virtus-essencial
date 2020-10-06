package crt2.dominio.analisequalitativa.editarmatriz.excluirgruposatividadesunidades;

import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.Unidade;
import br.gov.bcb.sisaps.src.mediator.AtividadeMediator;
import br.gov.bcb.sisaps.src.mediator.UnidadeMediator;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import crt2.ConfiguracaoTestesNegocio;

public class TestR003ExcluirGrupoAtividadeUnidade extends ConfiguracaoTestesNegocio {
    private Integer unidade;

    private Integer atividade;

    public String excluirAtividadeVisivel() {
        Atividade ativ = AtividadeMediator.get().loadPK(atividade);
        return SimNaoEnum.getTipo(AtividadeMediator.get().podeExcluirAtividade(ativ)).getDescricao();
    }

    public String excluirUnidadeVisivel() {
        Unidade unid = UnidadeMediator.get().loadPK(unidade);
        return SimNaoEnum.getTipo(UnidadeMediator.get().podeExcluirUnidade(unid)).getDescricao();
    }

    public Integer getUnidade() {
        return unidade;
    }

    public void setUnidade(Integer unidade) {
        this.unidade = unidade;
    }

    public Integer getAtividade() {
        return atividade;
    }

    public void setAtividade(Integer atividade) {
        this.atividade = atividade;
    }

}
