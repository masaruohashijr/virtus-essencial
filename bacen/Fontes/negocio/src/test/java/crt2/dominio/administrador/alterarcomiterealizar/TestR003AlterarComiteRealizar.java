package crt2.dominio.administrador.alterarcomiterealizar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ObservacaoAgendaCorec;
import br.gov.bcb.sisaps.src.mediator.AgendaCorecMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.ObservacaoAgendaCorecMediator;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestR003AlterarComiteRealizar extends ConfiguracaoTestesNegocio {
    private static final String DD_MM_YYYY = "dd/MM/yyyy";
    private String mensagem;
    private List<ObservacaoAgendaCorec> listaObservacao;
    private List<ObservacaoAgendaCorec> listaObservacaoExclusao;

    public String inicializar() {
        listaObservacao = new ArrayList<ObservacaoAgendaCorec>();
        listaObservacaoExclusao = new ArrayList<ObservacaoAgendaCorec>();
        return "";
    }

    public String addObservacao(String operacao, String idObservacao, String texto) {
        ObservacaoAgendaCorec observacao;
        if (Util.isNuloOuVazio(idObservacao)) {
            observacao = new ObservacaoAgendaCorec();
        } else {
            observacao = ObservacaoAgendaCorecMediator.get().buscarObservacao(Integer.valueOf(idObservacao));
        }
        observacao.setDescricao(texto);

        if ("Excluir".equals(operacao)) {
            listaObservacaoExclusao.add(observacao);
        } else {
            listaObservacao.add(observacao);
        }

        return "";
    }

    public String addObservacao(String idObservacao, String texto) {
        ObservacaoAgendaCorec observacao;
        if (Util.isNuloOuVazio(idObservacao)) {
            observacao = new ObservacaoAgendaCorec();
        } else {
            observacao = ObservacaoAgendaCorecMediator.get().buscarObservacao(Integer.valueOf(idObservacao));
        }
        observacao.setDescricao(texto);

        listaObservacao.add(observacao);

        return "";
    }

    public void incluirAgenda(Long idCiclo, String corecPrevisto) {
        erro = null;
        try {
            Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(idCiclo.intValue());
            AgendaCorec agenda = new AgendaCorec();

            SimpleDateFormat sdf = new SimpleDateFormat(DD_MM_YYYY);
            Date date = null;
            try {
                date = sdf.parse(corecPrevisto.replace('"', ' ').trim());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            agenda.setCiclo(ciclo);
            atribuirObservacoes(agenda);
            mensagem = AgendaCorecMediator.get().salvar(agenda, listaObservacaoExclusao, date);
        } catch (NegocioException e) {
            erro = e;
        }
        if (erro != null) {
            mensagem = erro.getMessage().replace("[", "").replace("]", "");
        }
    }

    public String getMensagem() {
        return mensagem;
    }

    public void salvarAgenda(Long idAgenda, String corecPrevisto) {
        erro = null;
        try {
            AgendaCorec agenda = AgendaCorecMediator.get().buscarAgendaCorec(idAgenda.intValue());

            SimpleDateFormat sdf = new SimpleDateFormat(DD_MM_YYYY);
            Date date = null;
            try {
                date = sdf.parse(corecPrevisto.replace('"', ' ').trim());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            atribuirObservacoes(agenda);
            agenda.getObservacoes().removeAll(listaObservacaoExclusao);
            mensagem = AgendaCorecMediator.get().salvar(agenda, listaObservacaoExclusao, date);
        } catch (NegocioException e) {
            erro = e;
        }
        if (erro != null) {
            mensagem = erro.getMessage().replace("[", "").replace("]", "");
        }
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
