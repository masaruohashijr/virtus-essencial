package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.TendenciaARC;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfisNotificacaoEnum;
import br.gov.bcb.sisaps.web.page.componentes.LabelLinhas;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.EdicaoArcPage;

public class PainelTendenciaRiscoMercadoDetalheArc extends Panel {

    private static final String PARAMETRO_TENDENCIA_NOME = "parametroTendencia.nome";
    private static final String ID_NOME_DO_GRUPO = "idNomeGrupoRiscoControle";
    private static final String PROP_JUSTIFICATIVA = "justificativa";
    private TendenciaARC tendencia = new TendenciaARC();
    private AvaliacaoRiscoControle avaliacao;
    private final boolean isTendenciaSupervisor;
    private final WebMarkupContainer wmcExibirTendenciaSupervisor = new WebMarkupContainer("exibirTendenciaSupervisor");
    private final WebMarkupContainer wmcExibirTendenciaInspetor = new WebMarkupContainer("exibirTendenciaInspetor");
    private final WebMarkupContainer wmcExibirTendenciaVigente = new WebMarkupContainer("exibirTendenciaVigente");
    private final WebMarkupContainer wmcExibirTendenciaCelula = new WebMarkupContainer("exibirTendenciaCelula");
    private final boolean isTendenciaInspetor;
    private final boolean isTendenciaVigente;

    private final ParametroGrupoRiscoControle parametroGrupoRiscoControle;

    public PainelTendenciaRiscoMercadoDetalheArc(String id, ParametroGrupoRiscoControle parametroGrupoRiscoControle, 
            AvaliacaoRiscoControle avaliacao,
            boolean isTendenciaVigente, boolean isTendenciaInspetor, boolean isTendenciaSupervisor) {
        super(id);
        this.parametroGrupoRiscoControle = parametroGrupoRiscoControle;
        this.avaliacao = avaliacao;
        this.isTendenciaVigente = isTendenciaVigente;
        this.isTendenciaInspetor = isTendenciaInspetor;
        this.isTendenciaSupervisor = isTendenciaSupervisor;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        exibirTendenciaVigente();
        exibirTendenciaInspetor();
        exibirTendenciaSupervisor();
    }

    private void exibirTendenciaVigente() {
        tendencia = avaliacao.getTendenciaARCVigente();
        tendenciaNova(PerfisNotificacaoEnum.SUPERVISOR);
        addNomeOperadorDataHora("idDataVigente", tendencia, wmcExibirTendenciaVigente);
        wmcExibirTendenciaCelula.addOrReplace(
                new Label(ID_NOME_DO_GRUPO, new PropertyModel<String>(parametroGrupoRiscoControle, "nome") {
                    @Override
                    public String getObject() {
                        return parametroGrupoRiscoControle.getNome(avaliacao.getTipo());
                    }
                }));
        addOrReplace(wmcExibirTendenciaCelula);
        wmcExibirTendenciaVigente.addOrReplace(new Label("idTipoTendenciaVigente", new PropertyModel<String>(tendencia,
                PARAMETRO_TENDENCIA_NOME)));
        wmcExibirTendenciaVigente.addOrReplace(new LabelLinhas("idTendenciaVigente", new PropertyModel<String>(
                tendencia, PROP_JUSTIFICATIVA)).setEscapeModelStrings(false));
        wmcExibirTendenciaVigente.setVisible(isTendenciaVigente);
        addOrReplace(wmcExibirTendenciaVigente);
    }

    private void exibirTendenciaInspetor() {
        tendencia = avaliacao.getTendenciaARCInspetor();

        if (getPage() instanceof EdicaoArcPage && avaliacao.getTendenciaARCVigente() != null
                && avaliacao.getTendenciaARCVigente().getParametroTendencia() != null) {
            tendencia = avaliacao.getTendenciaARCVigente();
        }

        tendenciaNova(PerfisNotificacaoEnum.INSPETOR);
        addNomeOperadorDataHora("idDataInspetor", avaliacao.getTendenciaARCInspetor(), wmcExibirTendenciaInspetor);
        wmcExibirTendenciaInspetor.addOrReplace(new Label("idTipoTendenciaInspetor", new PropertyModel<String>(
                tendencia, PARAMETRO_TENDENCIA_NOME)));
        wmcExibirTendenciaInspetor.addOrReplace(new LabelLinhas("idTendenciaInspetor", new PropertyModel<String>(
                tendencia, PROP_JUSTIFICATIVA)).setEscapeModelStrings(false));
        wmcExibirTendenciaInspetor.setVisible(isTendenciaInspetor);
        addOrReplace(wmcExibirTendenciaInspetor);

    }

    private void exibirTendenciaSupervisor() {
        tendencia = avaliacao.getTendenciaARCSupervisor();
        tendenciaNova(PerfisNotificacaoEnum.SUPERVISOR);
        addNomeOperadorDataHora("idDataSupervisor", avaliacao.getTendenciaARCSupervisor(), wmcExibirTendenciaSupervisor);
        wmcExibirTendenciaSupervisor.addOrReplace(new Label("idTipoTendenciaSupervisor", new PropertyModel<String>(
                tendencia, PARAMETRO_TENDENCIA_NOME)));
        wmcExibirTendenciaSupervisor.addOrReplace(new LabelLinhas("idTendenciaSupervisor", new PropertyModel<String>(
                tendencia, PROP_JUSTIFICATIVA)).setEscapeModelStrings(false));
        wmcExibirTendenciaSupervisor.setVisible(isTendenciaSupervisor
                && (tendencia.getParametroTendencia() != null || tendencia.getJustificativa() != null));
        addOrReplace(wmcExibirTendenciaSupervisor);

    }

    private void tendenciaNova(PerfisNotificacaoEnum perfil) {
        if (tendencia == null) {
            tendencia = new TendenciaARC();
            tendencia.setAvaliacaoRiscoControle(avaliacao);
            tendencia.setPerfil(perfil);
        }
    }

    private void addNomeOperadorDataHora(String id, TendenciaARC tendencia, WebMarkupContainer conteiner) {
        String data = "";
        if (tendencia != null && tendencia.getOperadorAtualizacao() != null) {
            data = "Atualizado por " + tendencia.getNomeOperadorDataHora() + ".";

        }
        Label dataAtualizacaoInspetor = new Label(id, data);
        conteiner.addOrReplace(dataAtualizacaoInspetor);

    }

    public void setAvaliacao(AvaliacaoRiscoControle avaliacao) {
        this.avaliacao = avaliacao;
    }

}
