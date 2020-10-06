package br.gov.bcb.sisaps.web.page.dominio.gestaoAgenda;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.src.dominio.EmailCorec;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEmailCorecEnum;
import br.gov.bcb.sisaps.src.mediator.AgendaCorecMediator;
import br.gov.bcb.sisaps.src.mediator.CargaParticipanteMediator;
import br.gov.bcb.sisaps.src.mediator.EmailCorecMediator;
import br.gov.bcb.sisaps.src.mediator.ParticipanteAgendaCorecMediator;
import br.gov.bcb.sisaps.src.vo.ParticipanteComiteVO;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.ProviderGenericoList;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponente;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;
import br.gov.bcb.sisaps.web.page.dominio.agenda.AgendaPage;
import br.gov.bcb.utils.logging.BCLogFactory;
import br.gov.bcb.utils.logging.BCLogger;

public class PainelListagemParticipantesAgenda extends PainelSisAps {

    private static final String NOME = "nome";
    private Tabela<ParticipanteComiteVO> tabela;
    private IModel<List<ParticipanteComiteVO>> modelConsulta;
    private ProviderGenericoList<ParticipanteComiteVO> provider;
    private final AgendaCorec agenda;
    private boolean tituloPossivel;
    private static final BCLogger LOG = BCLogFactory.getLogger("PainelListagemParticipantesAgenda");

    public PainelListagemParticipantesAgenda(String id, AgendaCorec agenda) {
        super(id);
        this.agenda = agenda;
    }

    @Override
    protected void onConfigure() {
        String strTitulo = null;
        if (AgendaCorecMediator.get().comiteARealizar(agenda.getCiclo())) {
            strTitulo = "Possíveis participantes";
        } else {
            strTitulo = "Participantes";
        }
        addOrReplace(new Label("titulo", strTitulo));
        addTabela();
    }

    private void addTabela() {
        Configuracao cfg = obterConfiguracao();
        ProviderGenericoList<ParticipanteComiteVO> providerGenericoList = criarProvider();
        List<Coluna<ParticipanteComiteVO>> colunas = obterColunas();
        tabela = new Tabela<ParticipanteComiteVO>("tabela", cfg, colunas, providerGenericoList, true);
        addOrReplace(tabela);
    }

    private Configuracao obterConfiguracao() {
        Configuracao cfg = new Configuracao("idTitulo", "idDados");
        cfg.setExibirTitulo(false);
        cfg.setStyleDados(Model.of("width:100%;"));
        cfg.setExibirPaginador(false);
        cfg.setMensagemVazio(Model.of(""));
        return cfg;
    }

    private ProviderGenericoList<ParticipanteComiteVO> criarProvider() {
        provider = new ProviderGenericoList<ParticipanteComiteVO>(NOME, SortOrder.ASCENDING, obterModel());
        return provider;
    }

    private IModel<List<ParticipanteComiteVO>> obterModel() {
        modelConsulta = new AbstractReadOnlyModel<List<ParticipanteComiteVO>>() {
            @Override
            public List<ParticipanteComiteVO> getObject() {
                if (AgendaCorecMediator.get().comiteARealizar(agenda.getCiclo())) {
                    return CargaParticipanteMediator.get().buscarParticipantesPossiveisES(
                            agenda.getCiclo().getEntidadeSupervisionavel());
                } else {
                    LOG.info("AGENDA: " + agenda.getPk());
                    if (agenda.getCiclo() != null && agenda.getCiclo().getEntidadeSupervisionavel() != null
                            && agenda.getCiclo().getEntidadeSupervisionavel().getConglomeradoOuCnpj() != null) {
                        LOG.info("CNPJ ES: " + agenda.getCiclo().getEntidadeSupervisionavel().getConglomeradoOuCnpj());
                    }
                    return ParticipanteAgendaCorecMediator.get().buscarParticipantesEfetivos(agenda.getPk());
                }
            }
        };
        return modelConsulta;
    }

    private List<Coluna<ParticipanteComiteVO>> obterColunas() {
        List<Coluna<ParticipanteComiteVO>> colunas = new LinkedList<Coluna<ParticipanteComiteVO>>();

        colunas.add(new Coluna<ParticipanteComiteVO>().setCabecalho("Nome").setOrdenar(false).setPropriedade(NOME)
                .setEstiloCabecalho("width:65%"));
        if (AgendaCorecMediator.get().comiteARealizar(agenda.getCiclo()) && !(getPaginaAtual() instanceof AgendaPage)) {
            colunas.add(new Coluna<ParticipanteComiteVO>().setCabecalho("E-mail disponibilidade").setOrdenar(false)
                    .setPropriedade("matricula").setComponente(new ComponenteLabel()).setEstiloCabecalho("width:35%"));
        }

        return colunas;
    }

    private class ComponenteLabel implements IColunaComponente<ParticipanteComiteVO> {

        @Override
        public Component obterComponente(Item<ICellPopulator<ParticipanteComiteVO>> cellItem, String componentId,
                final IModel<ParticipanteComiteVO> rowModel) {

            EmailCorec emailParticipante =
                    EmailCorecMediator.get().buscarEmailParticipante(agenda.getPk(),
                            rowModel.getObject().getMatricula(), TipoEmailCorecEnum.DISPONIBILIDADE);
            return new Label(componentId, emailParticipante == null ? "" : emailParticipante.getDataFormatada());

        }
    }

    public Tabela<ParticipanteComiteVO> getTabelaEntidades() {
        return tabela;
    }

    public void setTabelaEntidades(Tabela<ParticipanteComiteVO> tabelaEntidades) {
        this.tabela = tabelaEntidades;
    }

    public IModel<List<ParticipanteComiteVO>> getModelConsulta() {
        return modelConsulta;
    }

    public void setModelConsulta(IModel<List<ParticipanteComiteVO>> modelConsulta) {
        this.modelConsulta = modelConsulta;
    }

    public ProviderGenericoList<ParticipanteComiteVO> getProvider() {
        return provider;
    }

    public void setProvider(ProviderGenericoList<ParticipanteComiteVO> provider) {
        this.provider = provider;
    }

}
