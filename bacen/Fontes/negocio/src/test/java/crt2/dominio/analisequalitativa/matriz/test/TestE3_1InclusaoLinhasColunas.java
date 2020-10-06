package crt2.dominio.analisequalitativa.matriz.test;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.ParametroFatorRelevanciaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.ParametroPeso;
import br.gov.bcb.sisaps.src.dominio.ParametroTipoAtividadeNegocio;
import br.gov.bcb.sisaps.src.dominio.Unidade;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoGrupoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoUnidadeAtividadeEnum;
import br.gov.bcb.sisaps.src.mediator.AtividadeMediator;
import br.gov.bcb.sisaps.src.mediator.MatrizCicloMediator;
import br.gov.bcb.sisaps.src.mediator.MetodologiaMediator;
import br.gov.bcb.sisaps.src.mediator.UnidadeMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestE3_1InclusaoLinhasColunas extends ConfiguracaoTestesNegocio {

    private static final String NULL = "null";

    @Autowired
    private AtividadeMediator atividadeMediator;

    @Autowired
    private MetodologiaMediator metodologiaMediator;

    @Autowired
    private MatrizCicloMediator matrizCicloMediator;

    @Autowired
    private UnidadeMediator unidadeMediator;

    public void salvarUnidade(int pkMatriz, boolean isCorporativa) {

        Unidade unidade = new Unidade();

        unidade.setNome("Unidade Negócio");
        unidade.setParametroPeso(obterPeso(0));

        if (isCorporativa) {
            unidade.setTipoUnidade(TipoUnidadeAtividadeEnum.CORPORATIVO);
        } else {
            unidade.setTipoUnidade(TipoUnidadeAtividadeEnum.NEGOCIO);
        }

        unidade.setMatriz(getMatriz(pkMatriz));

        salvarUnidade(unidade);
    }

    private void salvarUnidade(Unidade unidade) {
        erro = null;
        try {
            unidadeMediator.incluirUnidadeNegocio(unidade);
        } catch (NegocioException e) {
            erro = e;
        }
    }

    public void salvarUnidadeNegocioSuccess(String nome) {
        Unidade unidade = new Unidade();
        unidade.setNome(nome);
        unidade.setTipoUnidade(TipoUnidadeAtividadeEnum.NEGOCIO);
        unidade.setMatriz(getMatriz(1));
        unidade.setParametroPeso(obterPeso(0));
        salvarUnidade(unidade);
    }

    public void salvarAtividadeNegocioSuccess(String pkUnidade, String nome, boolean isCorp) {
        Atividade atividade = new Atividade();
        atividade.setMatriz(getMatriz(1));
        ParametroPeso parametroPeso = obterPeso(0);
        ParametroFatorRelevanciaRiscoControle pfr = obterFatorRelevancia(0);
        ParametroGrupoRiscoControle pgrc = obterGrupoRiscoControle(0);

        atividade.setParametroPeso(parametroPeso);
        if (!NULL.equals(nome)) {
            atividade.setNome(nome);
        }
        if (isCorp) {
            atividade.setTipoAtividade(TipoUnidadeAtividadeEnum.CORPORATIVO);
        } else {
            setAtividadeNegocio(atividade);
        }

        CelulaRiscoControle celula = new CelulaRiscoControle();

        if (!NULL.equals(pkUnidade)) {
            Unidade unidade = new Unidade();
            unidade.setPk(Integer.parseInt(pkUnidade));
            atividade.setUnidade(unidade);
        }

        montarArc(celula, parametroPeso, pfr, pgrc);
        celula.setParametroGrupoRiscoControle(pgrc);
        celula.setParametroPeso(parametroPeso);
        celula.setAtividade(atividade);
        List<CelulaRiscoControle> listaCelulas = new ArrayList<CelulaRiscoControle>();
        listaCelulas.add(celula);

        atividade.setCelulasRiscoControle(listaCelulas);
        salvarAtividade(atividade);
    }

    public void salvarAtividadeGrupoIncorreto(boolean isCorp) {
        Atividade atividade = new Atividade();
        atividade.setMatriz(getMatriz(1));
        ParametroPeso parametroPeso = obterPeso(0);
        atividade.setParametroPeso(parametroPeso);
        atividade.setNome("Atividade 3");
        Unidade unidade = new Unidade();
        unidade.setPk(1);
        atividade.setUnidade(unidade);
        if (isCorp) {
            atividade.setTipoAtividade(TipoUnidadeAtividadeEnum.CORPORATIVO);
        } else {
            setAtividadeNegocio(atividade);
        }
        
        CelulaRiscoControle celula = new CelulaRiscoControle();
        celula.setParametroGrupoRiscoControle(obterGrupoRiscoControle(0));
        List<CelulaRiscoControle> listaCelulas = new ArrayList<CelulaRiscoControle>();
        listaCelulas.add(celula);

        atividade.setCelulasRiscoControle(listaCelulas);

        salvarAtividade(atividade);
    }

    public void salvarAtividadeNegocio() {
        Atividade atividade = new Atividade();
        atividade.setTipoAtividade(TipoUnidadeAtividadeEnum.NEGOCIO);
        atividade.setMatriz(getMatriz(1));
        salvarAtividade(atividade);
    }

    public void salvarAtividade(int pkMatriz, boolean isCoorporativa, String pkUnidade, String nomeAtividade) {

        Atividade atividade = new Atividade();
        atividade.setMatriz(getMatriz(pkMatriz));

        ParametroPeso parametroPeso = obterPeso(0);
        ParametroFatorRelevanciaRiscoControle pfr = obterFatorRelevancia(0);
        ParametroGrupoRiscoControle pgrc = obterGrupoRiscoControle(0);

        atividade.setParametroPeso(parametroPeso);
        atividade.setNome(nomeAtividade);

        if (!NULL.equals(pkUnidade)) {
            Unidade unidade = new Unidade();
            unidade.setPk(Integer.parseInt(pkUnidade));
            atividade.setUnidade(unidade);
        }

        if (isCoorporativa) {
            atividade.setTipoAtividade(TipoUnidadeAtividadeEnum.CORPORATIVO);
        } else {
            setAtividadeNegocio(atividade);
        }

        CelulaRiscoControle celula = new CelulaRiscoControle();
        montarArc(celula, parametroPeso, pfr, pgrc);
        celula.setAtividade(atividade);
        celula.setParametroGrupoRiscoControle(pgrc);
        celula.setParametroPeso(parametroPeso);
        List<CelulaRiscoControle> listaCelulas = new ArrayList<CelulaRiscoControle>();
        listaCelulas.add(celula);

        atividade.setCelulasRiscoControle(listaCelulas);
        salvarAtividade(atividade);
    }

    private ParametroGrupoRiscoControle obterGrupoRiscoControle(int index) {
        return getMetodologia().getParametrosGrupoRiscoControle().get(index);
    }

    private ParametroFatorRelevanciaRiscoControle obterFatorRelevancia(int index) {
        return getMetodologia().getParametrosFatorRelevancia().get(index);
    }

    private void setAtividadeNegocio(Atividade atividade) {
        ParametroTipoAtividadeNegocio parametroTipoAtividadeNegocio = new ParametroTipoAtividadeNegocio();
        parametroTipoAtividadeNegocio.setPk(1);
        parametroTipoAtividadeNegocio.setNome("Crédito Varejo");
        atividade.setParametroTipoAtividadeNegocio(parametroTipoAtividadeNegocio);
        atividade.setTipoAtividade(TipoUnidadeAtividadeEnum.NEGOCIO);
    }

    private Matriz getMatriz(int pk) {
        return matrizCicloMediator.loadPK(pk);
    }

    private Metodologia getMetodologia() {
        return metodologiaMediator.loadPK(2);
    }

    private ParametroPeso obterPeso(int index) {
        ParametroPeso parametroPeso = getMetodologia().getParametrosPeso().get(index);
        return parametroPeso;
    }

    private void montarArc(CelulaRiscoControle celula, ParametroPeso parametroPeso,
            ParametroFatorRelevanciaRiscoControle pfr, ParametroGrupoRiscoControle pgrc) {
        AvaliacaoRiscoControle arcRisco = new AvaliacaoRiscoControle();
        arcRisco.setTipo(TipoGrupoEnum.RISCO);
        arcRisco.setEstado(EstadoARCEnum.PREVISTO);

        celula.setArcRisco(arcRisco);
        AvaliacaoRiscoControle arcControle = new AvaliacaoRiscoControle();
        arcControle.setTipo(TipoGrupoEnum.CONTROLE);
        arcControle.setEstado(EstadoARCEnum.PREVISTO);
        celula.setArcControle(arcControle);
    }

    private void salvarAtividade(Atividade atividade) {
        erro = null;
        try {
            atividadeMediator.incluir(atividade);
        } catch (NegocioException e) {
            erro = e;
        }

    }

    public void salvar() {
        Atividade atividade = new Atividade();
        atividade.setMatriz(getMatriz(1));
        salvarAtividade(atividade);
    }
}