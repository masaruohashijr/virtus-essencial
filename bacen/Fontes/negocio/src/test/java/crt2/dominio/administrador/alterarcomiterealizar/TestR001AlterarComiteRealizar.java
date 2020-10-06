package crt2.dominio.administrador.alterarcomiterealizar;

import java.util.ArrayList;
import java.util.List;

import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ObservacaoAgendaCorec;
import br.gov.bcb.sisaps.src.mediator.AgendaCorecMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.ObservacaoAgendaCorecMediator;
import br.gov.bcb.sisaps.util.Util;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001AlterarComiteRealizar extends ConfiguracaoTestesNegocio {
    private List<ObservacaoAgendaCorec> listaObservacao;
    private List<ObservacaoAgendaCorec> listaObservacaoExclusao;
    private String mensagem;

    public String inicializar() {
        listaObservacao = new ArrayList<ObservacaoAgendaCorec>();
        listaObservacaoExclusao = new ArrayList<ObservacaoAgendaCorec>();
        return "";
    }

    public String addObservacao(String idObservacao, String texto) {

        return addObservacao("Não", idObservacao, texto);
    };

    public String addObservacao(String operacao, String idObservacao, String texto) {
        ObservacaoAgendaCorec observacao;
        if (Util.isNuloOuVazio(idObservacao)) {
            observacao = new ObservacaoAgendaCorec();
        } else {
            observacao = ObservacaoAgendaCorecMediator.get().buscarObservacao(Integer.valueOf(idObservacao));
        }
        observacao.setDescricao(texto);

        if ("Sim".equals(operacao)) {
            listaObservacaoExclusao.add(observacao);
        } else {
            listaObservacao.add(observacao);
        }

        return "";
    }

    public void incluirAgenda(Long idCiclo) {
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(idCiclo.intValue());
        AgendaCorec agenda = new AgendaCorec();
        agenda.setCiclo(ciclo);
        atribuirObservacoes(agenda);
        mensagem =
                AgendaCorecMediator.get().salvar(agenda, listaObservacaoExclusao,
                        agenda.getCiclo().getDataPrevisaoCorec());
    }

    public String getMensagem() {
        return mensagem;
    }

    public void salvarAgenda(Long idAgenda) {
        AgendaCorec agenda = AgendaCorecMediator.get().buscarAgendaCorec(idAgenda.intValue());
        atribuirObservacoes(agenda);
        agenda.getObservacoes().removeAll(listaObservacaoExclusao);
        mensagem =
                AgendaCorecMediator.get().salvar(agenda, listaObservacaoExclusao,
                        agenda.getCiclo().getDataPrevisaoCorec());
    }

    private void atribuirObservacoes(AgendaCorec agenda) {
        for (ObservacaoAgendaCorec observacao : listaObservacao) {
            observacao.setAgenda(agenda);
        }

        if (agenda.getObservacoes() == null) {
            agenda.setObservacoes(new ArrayList<ObservacaoAgendaCorec>());
        }

        for (ObservacaoAgendaCorec observacaoAux : listaObservacao) {
            agenda.getObservacoes().add(observacaoAux);
        }
    }

}
