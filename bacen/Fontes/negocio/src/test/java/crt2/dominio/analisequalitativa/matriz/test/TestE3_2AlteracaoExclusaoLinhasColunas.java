package crt2.dominio.analisequalitativa.matriz.test;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import br.gov.bcb.comum.util.string.StringUtil;
import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Unidade;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoUnidadeAtividadeEnum;
import br.gov.bcb.sisaps.src.mediator.AtividadeMediator;
import br.gov.bcb.sisaps.src.mediator.UnidadeMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestE3_2AlteracaoExclusaoLinhasColunas extends ConfiguracaoTestesNegocio {

    @Autowired
    private AtividadeMediator atividadeMediator;

    @Autowired
    private UnidadeMediator unidadeMediator;

    private Unidade unidade;
    private Atividade atividade;

    public void salvarAtividade(String pk) {
        atividade = atividadeMediator.loadPK(Integer.valueOf(pk));
        alterarAtividade();
    }

    public void salvarAtividade(String nome, String peso, String tipoAtividade, String pk) {
        atividade = atividadeMediator.loadPK(Integer.valueOf(pk));
        if (StringUtil.isVazioOuNulo(peso)) {
            atividade.setParametroPeso(null);
        }
        if (StringUtil.isVazioOuNulo(nome)) {
            atividade.setNome(null);
        } else {
            atividade.setNome(nome);
        }

        if (StringUtil.isVazioOuNulo(tipoAtividade)) {
            atividade.setTipoAtividade(null);
        } else {
            atividade.setTipoAtividade(buscarTipoAtividade(tipoAtividade));
        }

        alterarAtividade();
    }

    public void salvarAtividadeGrupoIncorreto(String pk) {
        atividade = atividadeMediator.loadPK(Integer.valueOf(pk));
        atividade.setCelulasRiscoControle(null);


        alterarAtividade();
    }

    public void unidadeAssociada(String unidade, String pk) {
        atividade = atividadeMediator.loadPK(Integer.valueOf(pk));
        if (StringUtil.isVazioOuNulo(unidade)) {
            atividade.setUnidade(null);
        } else {
            atividade.setUnidade(unidadeMediator.loadPK(Integer.valueOf(unidade)));
        }

        alterarAtividade();
    }

    private TipoUnidadeAtividadeEnum buscarTipoAtividade(String tipoAtividade) {
        if (TipoUnidadeAtividadeEnum.CORPORATIVO.getDescricao().equals(tipoAtividade)) {
            return TipoUnidadeAtividadeEnum.CORPORATIVO;
        }
        return TipoUnidadeAtividadeEnum.NEGOCIO;
    }

    public void salvarUnidade(String pk) {
        unidade = unidadeMediator.loadPK(Integer.valueOf(pk));
        alterarUnidade();
    }

    public void salvarUnidade(String pesopk, String pk) {
        unidade = unidadeMediator.loadPK(Integer.valueOf(pk));
        if (StringUtil.isVazioOuNulo(pesopk)) {
            unidade.setParametroPeso(null);
        }

        alterarUnidade();
    }

    public void salvarUnidade(String pesopk, String fatorRelevancia, String pk) {
        unidade = unidadeMediator.loadPK(Integer.valueOf(pk));
        if (StringUtil.isVazioOuNulo(pesopk)) {
            unidade.setParametroPeso(null);
        }
        if (StringUtil.isVazioOuNulo(fatorRelevancia)) {
            unidade.setFatorRelevancia(null);
        } else {
            unidade.setFatorRelevancia(new BigDecimal(fatorRelevancia));
        }

        alterarUnidade();
    }

    public void salvarUnidadeNegocio(String nome, String peso, String pk) {
        unidade = unidadeMediator.loadPK(Integer.valueOf(pk));
        if (StringUtil.isVazioOuNulo(nome)) {
            unidade.setNome(null);
        } else {
            unidade.setNome(nome);
        }
        if (StringUtil.isVazioOuNulo(peso)) {
            unidade.setParametroPeso(null);
        }
        //         else {
        //            unidade.setParametroPeso();
        //        }

        alterarUnidade();
    }

    private void alterarAtividade() {
        erro = null;
        try {
            atividadeMediator.alterar(atividade, new ArrayList<CelulaRiscoControle>());
        } catch (NegocioException e) {
            erro = e;
        }

    }

    private void alterarUnidade() {
        erro = null;
        try {
            unidadeMediator.alterarUnidade(unidade, unidade.getTipoUnidade());
        } catch (NegocioException e) {
            erro = e;
        }

    }

    public void excluirAtividade(String pk) {
        atividade = atividadeMediator.loadPK(Integer.valueOf(pk));

        erro = null;
        try {
            atividadeMediator.excluir(atividade, false);
        } catch (NegocioException e) {
            erro = e;
        }
    }

    public void excluirUnidade(String pk) {
        erro = null;
        try {
            unidadeMediator.excluir(Integer.valueOf(pk));
        } catch (NegocioException e) {
            erro = e;
        }
    }

}