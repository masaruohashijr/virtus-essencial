package br.gov.bcb.sisaps.web.page.dominio.agenda;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.src.dominio.AjusteCorec;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.CargaParticipante;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.GrauPreocupacaoES;
import br.gov.bcb.sisaps.src.dominio.ParametroAtaCorec;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.dominio.ParticipanteAgendaCorec;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.mediator.AgendaCorecMediator;
import br.gov.bcb.sisaps.src.mediator.AjusteCorecMediator;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CargaParticipanteMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.GrauPreocupacaoESMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroAtaCorecMediator;
import br.gov.bcb.sisaps.src.mediator.ParticipanteAgendaCorecMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.VersaoPerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.vo.AvaliacaoRiscoControleVO;

public class GerarAtaCorec {

    public static final String NOME_ARQUIVO_RELATORIO = "RelatorioAtaCorec";
    private static final String FECHAR_TD_ABRIR_TD_STYLE_CENTER = "</td><td style='text-align: center;'>";
    private static final String FECHAR_P = "<p> </p>";
    private static final String ABRIR_TBODY = "<tbody>";
    private static final String INICIARLINHA = "<tr><td style='background-color:#93abc5; width: 25%;'>";
    private static final String TABLE_WIDTH_100 = "<table width='100%' border='1'>";
    private static final String TD_STYLE_TEXT_ALIGN_LEFT = "<td style='text-align: left;'>";
    private static final String FECHA_TBODY_TABLE = "</tbody></table>";
    private static final String FECHA_TD_TR = "</td></tr>";
    private static final String FECHA_TD = "</td>";
    private static final String DATA_COREC = "%datacorec%";

    private final Ciclo ciclo;
    private final EntidadeSupervisionavel entidadeSupervisionavel;
    private final ServidorVO supervisor;
    private final AgendaCorec agenda;
    private final PerfilRisco perfilRiscoAtual;
    private List<AvaliacaoRiscoControleVO> listaArc;

    public GerarAtaCorec(Ciclo ciclo) {
        this.ciclo = ciclo;
        this.perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());
        this.entidadeSupervisionavel = this.ciclo.getEntidadeSupervisionavel();
        this.agenda = AgendaCorecMediator.get().buscarAgendaCorecPorCiclo(ciclo.getPk());
        Date dataBase = null;
        if (!ciclo.getEstadoCiclo().getEstado().equals(EstadoCicloEnum.EM_ANDAMENTO)) {
            dataBase = ciclo.getDataPrevisaoCorec();
        }
        this.supervisor = CicloMediator.get().buscarChefeAtual(entidadeSupervisionavel.getLocalizacao(), dataBase);
        this.listaArc = AvaliacaoRiscoControleMediator.get().consultaArcPerfil(perfilRiscoAtual.getCiclo().getMatriz(),
                VersaoPerfilRiscoMediator.get().buscarVersoesPerfilRisco(perfilRiscoAtual.getPk(),
                        TipoObjetoVersionadorEnum.ARC));
    }

    public String textoFormatado() {
        ParametroAtaCorec parametro = ParametroAtaCorecMediator.get().buscar();
        String corpo = parametro.getCorpo();
        corpo = corpo.replace(DATA_COREC, ciclo.getDataPrevisaoFormatada());
        corpo = corpo.replace("%tabelaciclo%", montarTabelaCiclo());
        corpo = corpo.replace("%tabelaagenda%", montarTabelaAgenda());

        AjusteCorec ajusteCorec = AjusteCorecMediator.get().buscarPorCiclo(ciclo.getPk());
        corpo = corpo.replace("%outrasdeliberacoes%",
                ajusteCorec == null || ajusteCorec.getOutrasDeliberacoes() == null ? ""
                        : ajusteCorec.getOutrasDeliberacoes());

        corpo = corpo.replace("%tabelaparticipantes%", montarTabelaParticipantes());

        corpo = corpo.replace("%tabelaajustes%", montarTabelaAjusteCorec());
        return corpo;
    }

    private String montarTabelaCiclo() {
        StringBuffer sb = new StringBuffer();
        sb.append(TABLE_WIDTH_100);
        sb.append("<thead><tr style='background-color:#003d79;'><th style='text-align: left;' colspan='4'>");
        sb.append(entidadeSupervisionavel == null ? "" : entidadeSupervisionavel.getNomeConglomeradoFormatado());
        sb.append(FECHA_TD_TR);
        sb.append("<tr style='background-color:#4a73a2;'><th style='text-align: left;' colspan='4'>");
        sb.append("Ciclo</th></tr></thead>");
        sb.append("<tbody><tr><td style='background-color:#93abc5; width: 25%; '>Equipe</td><td colspan='3'>");
        sb.append(entidadeSupervisionavel == null ? "" : entidadeSupervisionavel.getLocalizacao());
        sb.append(FECHA_TD_TR);
        sb.append(INICIARLINHA);
        sb.append("Supervisor titular</td><td colspan='3'>");
        sb.append(supervisor == null ? "" : supervisor.getNome());
        sb.append(FECHA_TD_TR);
        sb.append("<tr><td style='background-color:#93abc5; width: 25%; '>Prioridade</td><td colspan='3'>");
        sb.append(entidadeSupervisionavel == null ? "" : entidadeSupervisionavel.getPrioridade().getDescricao());
        sb.append("</label></td></tr>");
        sb.append("<tr><td style='background-color:#93abc5; width: 25%; '>Início</td><td colspan='3'>");
        sb.append(ciclo == null ? "" : ciclo.getDataInicioFormatada());
        sb.append(FECHA_TD_TR);
        sb.append("<tr><td style='background-color:#93abc5; width: 25%; '>Corec</td><td colspan='3'>");
        sb.append(ciclo == null ? "" : ciclo.getDataPrevisaoFormatada());
        sb.append(FECHA_TD_TR);
        sb.append(FECHA_TBODY_TABLE);
        sb.append(FECHAR_P);

        return sb.toString();
    }

    private String montarTabelaAgenda() {
        StringBuffer sb = new StringBuffer();
        sb.append(TABLE_WIDTH_100);
        sb.append("<thead><tr style='background-color:#4a73a2;'><th style='text-align: left;' colspan='4'>"
                + "Dados da reunião</th></tr></thead>");
        sb.append("<tbody><tr><td style='background-color:#93abc5; width: 25%; '>");
        sb.append("Local da reunião</td><td colspan='3'>");
        sb.append(agenda == null ? "" : agenda.getLocal());
        sb.append(FECHA_TD_TR);
        sb.append(INICIARLINHA + "Horário</td><td colspan='3'>");
        sb.append(agenda == null ? "" : agenda.getHoraCorecFormatada());
        sb.append(FECHA_TD_TR);
        sb.append(FECHA_TBODY_TABLE);
        sb.append(FECHAR_P);
        return sb.toString();

    }

    private String montarTabelaParticipantes() {
        StringBuffer sb = new StringBuffer();
        sb.append(TABLE_WIDTH_100);
        sb.append("<thead><tr style='background-color:#003d79;'> <th style='text-align: left;' colspan='4'>");
        sb.append("Participantes do comitê</th></tr>");
        sb.append("<tr style='background-color:#4a73a2;'><th style='text-align: left;'  colspan='3'>Nome</th>");
        sb.append("<th style='text-align: left;  width: 20%;'>Assinatura da ata</th></tr></thead>");
        sb.append(ABRIR_TBODY);
        for (ParticipanteAgendaCorec part : listaParticipantes()) {
            sb.append("<tr>");
            sb.append("<td style='text-align: left;' colspan='3'>");
            CargaParticipante cargaParticipante =
                    CargaParticipanteMediator.get().buscarCargaPorMatricula(part.getMatricula());
            if (cargaParticipante == null) {
                sb.append(part.getNome());
            } else {
                sb.append(cargaParticipante.getNome());
            }
            sb.append(FECHA_TD);
            sb.append(TD_STYLE_TEXT_ALIGN_LEFT);
            sb.append(part.getDataAssinaturaFormatada());
            sb.append(FECHA_TD_TR);
        }
        sb.append(FECHA_TBODY_TABLE);
        sb.append(FECHAR_P);
        return sb.toString();
    }

    private String montarTabelaAjusteCorec() {
        StringBuffer sb = new StringBuffer();
        AjusteCorec ajusteCorec = AjusteCorecMediator.get().buscarPorCiclo(ciclo.getPk());
        if (ajusteCorec == null) {
            ajusteCorec = new AjusteCorec();
        }
        sb.append(TABLE_WIDTH_100);
        sb.append("<thead><tr style='background-color:#003d79;'><th");
        sb.append(" style='text-align: left;' colspan='4'>Avaliações</th></tr>");
        sb.append("<tr style='background-color:#4a73a2;'><th style='text-align: left;' colspan='2'></th>");
        sb.append("<th style='text-align: center;'>Avaliação supervisor</th>");
        sb.append("<th style='text-align: center;'>Avaliação comitê</th></tr></thead>");
        sb.append(ABRIR_TBODY);
        addGrauPreocupacao(sb, ajusteCorec);
        addNotaFinalQualitativa(sb, ajusteCorec);
        addNotaFinalANEF(sb, ajusteCorec);
        sb.append("<tr><td colspan='2'>Perspectiva</td><td style='text-align: center;'>");
        sb.append(PerfilRiscoMediator.get().getPerspectivaESPerfilRisco(perfilRiscoAtual).getParametroPerspectiva()
                .getNome());
        sb.append(FECHAR_TD_ABRIR_TD_STYLE_CENTER);
        sb.append(ajusteCorec.getPerspectiva() == null ? "" : ajusteCorec.getPerspectiva().getDescricao());
        sb.append(FECHA_TD_TR);
        sb.append(FECHA_TBODY_TABLE);
        sb.append(FECHAR_P);
        montarTabelaAjusteArcCorec(sb);
        montarTabelaAjusteAnefCorec(sb);
        return sb.toString();
    }

    private void addGrauPreocupacao(StringBuffer sb, AjusteCorec ajusteCorec) {
        GrauPreocupacaoES grauPreocupacaoESPerfilRisco =
                GrauPreocupacaoESMediator.get().buscarPorPerfilRisco(perfilRiscoAtual.getPk());
        if (GrauPreocupacaoESMediator.get().isNotaFinal(grauPreocupacaoESPerfilRisco)) {
            sb.append("<tr><td colspan='2'>Nota final</td><td style='text-align: center;'>");
        } else {
            sb.append("<tr><td colspan='2'>Grau de preocupação</td><td style='text-align: center;'>");
        }

        String notaFinalSupervisor = GrauPreocupacaoESMediator.get().getNotaFinal(perfilRiscoAtual,
                PerfilAcessoEnum.COMITE, perfilRiscoAtual.getCiclo().getMetodologia(), true);

        sb.append(notaFinalSupervisor);
        sb.append(FECHAR_TD_ABRIR_TD_STYLE_CENTER);
        sb.append(ajusteCorec.getDescricaoNotaFinal());
        sb.append(FECHA_TD_TR);
    }

    private void addNotaFinalQualitativa(StringBuffer sb, AjusteCorec ajusteCorec) {
        ParametroNota notaQualitativa = CicloMediator.get().notaQualitativa(ciclo, PerfilAcessoEnum.COMITE);
        sb.append(
                "<tr><td colspan='2'>Nota final da análise de riscos e controles</td><td style='text-align: center;'>");
        if (ciclo.getMetodologia().getIsMetodologiaNova()) {
            sb.append(notaQualitativa == null ? "" : notaQualitativa.getDescricao());
        } else {
            sb.append(notaQualitativa == null ? "" : notaQualitativa.getDescricaoValor());
        }
        sb.append(FECHAR_TD_ABRIR_TD_STYLE_CENTER);
        if (ciclo.getMetodologia().getIsMetodologiaNova()) {
            sb.append(ajusteCorec.getNotaQualitativa() == null ? "" : ajusteCorec.getNotaQualitativa().getDescricao());
        } else {
            sb.append(ajusteCorec.getNotaQualitativa() == null ? ""
                    : ajusteCorec.getNotaQualitativa().getDescricaoValor());
        }
        sb.append(FECHA_TD_TR);
    }

    private void addNotaFinalANEF(StringBuffer sb, AjusteCorec ajusteCorec) {
        sb.append(
                "<tr><td colspan='2'>Nota final da análise econômico-financeira</td><td style='text-align: center;'>");
        ParametroNotaAQT notaQuantitativa = CicloMediator.get().notaQuantitativa(ciclo, PerfilAcessoEnum.COMITE);
        if (ciclo.getMetodologia().getIsMetodologiaNova()) {
            sb.append(notaQuantitativa.getDescricao());
        } else {
            sb.append(notaQuantitativa.getDescricaoValor());
        }
        sb.append(FECHAR_TD_ABRIR_TD_STYLE_CENTER);
        if (ciclo.getMetodologia().getIsMetodologiaNova()) {
            sb.append(
                    ajusteCorec.getNotaQuantitativa() == null ? "" : ajusteCorec.getNotaQuantitativa().getDescricao());
        } else {
            sb.append(ajusteCorec.getNotaQuantitativa() == null ? ""
                    : ajusteCorec.getNotaQuantitativa().getValorString());
        }
        sb.append(FECHA_TD_TR);
    }

    private void montarTabelaAjusteArcCorec(StringBuffer sb) {
        sb.append(TABLE_WIDTH_100);
        sb.append("<thead><tr style='background-color:#003d79;'>");
        sb.append("<th style='text-align: left;' colspan='8'>Avaliações - ARCs</th></tr>");
        sb.append("<tr style='background-color:#4a73a2;'><th style='text-align: left;' colspan='3'>Atividade</th>");
        sb.append("<th style='text-align: center;' width='15%' colspan='2'>Grupo</th>");
        sb.append("<th style='text-align: center;'  width='5%'>R/C</th>");
        sb.append("<th style='text-align: center;'  width='10%'>Nota final</th>");
        sb.append("<th style='text-align: center;'  width='10%'>Nota comitê</th></tr></thead>");
        sb.append(ABRIR_TBODY);
        for (AvaliacaoRiscoControleVO arcVO : listaArc) {
            sb.append("<tr><td colspan='3'>");
            sb.append(arcVO.getAtividade().getNome() == null ? "" : arcVO.getAtividade().getNome());
            sb.append("</td><td style='text-align: center;' colspan='2'>");
            sb.append(arcVO.getParametroGrupoRiscoControle().getNomeAbreviado());
            sb.append(FECHAR_TD_ABRIR_TD_STYLE_CENTER);
            sb.append(arcVO.getTipo().getAbreviacao());
            sb.append(FECHAR_TD_ABRIR_TD_STYLE_CENTER);
            sb.append(arcVO.getNotaVigenteDescricaoValor());
            sb.append(FECHAR_TD_ABRIR_TD_STYLE_CENTER);
            AvaliacaoRiscoControle arcLoad = AvaliacaoRiscoControleMediator.get().loadPK(arcVO.getPk());
            sb.append(arcLoad.getNotaCorecDescricao());
            sb.append(FECHA_TD_TR);
        }
        sb.append(FECHA_TBODY_TABLE);
        sb.append(FECHAR_P);

    }

    private void montarTabelaAjusteAnefCorec(StringBuffer sb) {
        List<AnaliseQuantitativaAQT> listaAnefs =
                AnaliseQuantitativaAQTMediator.get().buscarAQTsPerfilRisco(VersaoPerfilRiscoMediator.get()
                        .buscarVersoesPerfilRisco(perfilRiscoAtual.getPk(), TipoObjetoVersionadorEnum.AQT));
        sb.append(TABLE_WIDTH_100);
        sb.append("<thead><tr style='background-color:#003d79;'> ");
        sb.append("<th style='text-align: left;' colspan='4'>Avaliações - ANEFs</th></tr>");
        sb.append("<tr style='background-color:#4a73a2;'>");
        sb.append("<th style='text-align: left;' colspan='2'>Componente</th>");
        sb.append("<th style='text-align: center;'>Nota final</th>");
        sb.append("<th style='text-align: center;'>Nota comitê</th></tr></thead>");
        sb.append(ABRIR_TBODY);
        for (AnaliseQuantitativaAQT anef : listaAnefs) {
            sb.append("<tr><td colspan='2'>");
            sb.append(anef.getParametroAQT().getDescricao());
            sb.append(FECHAR_TD_ABRIR_TD_STYLE_CENTER);
            sb.append(anef.getNotaSupervisorDescricaoValor());
            sb.append(FECHAR_TD_ABRIR_TD_STYLE_CENTER);
            sb.append(anef.getNotaCorecAtualDescricao());
            sb.append(FECHA_TD_TR);
        }
        sb.append(FECHA_TBODY_TABLE);
        sb.append(FECHAR_P);

    }

    private List<ParticipanteAgendaCorec> listaParticipantes() {
        if (agenda == null) {
            return new ArrayList<ParticipanteAgendaCorec>();
        }
        return ParticipanteAgendaCorecMediator.get().buscarParticipanteAgendaCorec(agenda.getPk());
    }

    public List<AvaliacaoRiscoControleVO> getListaArcs() {
        return listaArc;
    }

}
