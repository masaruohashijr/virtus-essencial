package br.gov.bcb.sisaps.web.page.painelComite;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.AgendaCorecMediator;
import br.gov.bcb.sisaps.src.mediator.CargaParticipanteMediator;
import br.gov.bcb.sisaps.src.mediator.ParticipanteAgendaCorecMediator;
import br.gov.bcb.sisaps.src.vo.ParticipanteComiteVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.ProviderGenericoList;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponente;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;

public class PainelParticipantesComite extends PainelSisAps {

    private static final String WIDTH_07 = "width:7%";
    private static final String CENTRALIZADO = "centralizado";
    private static final String WIDTH_93 = "width:93%";
    private static final String PROP_NOME = "nome";
    private static final String NOME = "Nome";
    private final Ciclo ciclo;
    private List<ParticipanteComiteVO> possiveisParticipantes;
    private List<ParticipanteComiteVO> participantesEfetivos;
    private List<ParticipanteComiteVO> participantesEfetivosIncluidos;
    private List<ParticipanteComiteVO> participantesEfetivosExcluidos;
    private Tabela<ParticipanteComiteVO> tabelaPossiveisParticipantes;
    private Tabela<ParticipanteComiteVO> tabelaParticipantesEfetivos;
    private AjaxSubmitLinkSisAps botaoSalvarInformacoes;
    private WebMarkupContainer alerta;
    private AgendaCorec agendaCorec;
    private final List<String> idsAlertas = new ArrayList<String>();
    

    public PainelParticipantesComite(String id, Ciclo ciclo) {
        super(id);
        setOutputMarkupId(true);
        this.ciclo = ciclo;
    }
    
    @Override
    protected void onInitialize() {
        super.onInitialize();
        obterDadosIniciais();
        addTabelaPossiveisParticipantes();
        addTabelaParticipantesEfetivos();
        addBotaoSalvarInformacoes();
    }

    private void obterDadosIniciais() {
        agendaCorec = AgendaCorecMediator.get().buscarAgendaCorecPorCiclo(ciclo.getPk());
        possiveisParticipantes = CargaParticipanteMediator.get().buscarParticipantesPossiveisESSemEfetivos(
                ciclo.getEntidadeSupervisionavel(), agendaCorec);
        if (agendaCorec == null) {
            participantesEfetivos = new ArrayList<ParticipanteComiteVO>();
        } else {
            participantesEfetivos = ParticipanteAgendaCorecMediator.get().buscarParticipantesEfetivos(agendaCorec.getPk());
        }
        participantesEfetivosIncluidos = new ArrayList<ParticipanteComiteVO>();
        participantesEfetivosExcluidos = new ArrayList<ParticipanteComiteVO>();
    }

    private void addTabelaPossiveisParticipantes() {
        Configuracao cfg = obterConfiguracao("PossiveisParticipantes", true);
        List<Coluna<ParticipanteComiteVO>> colunas = obterColunasPossiveisParticipantes();
        tabelaPossiveisParticipantes = new Tabela<ParticipanteComiteVO>("tabelaPossiveisParticipantes", 
                cfg, colunas, criarProviderPossiveisParticipantes(), true);
        addOrReplace(tabelaPossiveisParticipantes);
    }
    
    private void addTabelaParticipantesEfetivos() {
        Configuracao cfg = obterConfiguracao("ParticipantesEfetivos", false);
        List<Coluna<ParticipanteComiteVO>> colunas = obterColunasParticipantesEfetivos();
        tabelaParticipantesEfetivos = new Tabela<ParticipanteComiteVO>("tabelaParticipantesEfetivos", 
                cfg, colunas, criarProviderParticipantesEfetivos(), true);
        addOrReplace(tabelaParticipantesEfetivos);
    }
    
    private void addBotaoSalvarInformacoes() {
        alerta = new WebMarkupContainer("alerta");
        alerta.setOutputMarkupPlaceholderTag(true);
        alerta.setVisible(false);
        idsAlertas.add(alerta.getMarkupId());
        addOrReplace(alerta);
        
        botaoSalvarInformacoes = new AjaxSubmitLinkSisAps("bttSalvarInformacoesParticipantes") {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                salvarParticipantes(target);
            }
        };
        botaoSalvarInformacoes.setEnabled(false);
        addOrReplace(botaoSalvarInformacoes);
    }
    
    private void salvarParticipantes(AjaxRequestTarget target) {
        success(ParticipanteAgendaCorecMediator.get().salvarParticipantesEfetivos(ciclo,
                agendaCorec, participantesEfetivosIncluidos, participantesEfetivosExcluidos));
        botaoSalvarInformacoes.setEnabled(false);
        alerta.setVisible(false);
        obterDadosIniciais();
        ((GestaoCorecPage) getPage()).atualizarTabelasParticipantes(target);
    }
    
    private List<Coluna<ParticipanteComiteVO>> obterColunasPossiveisParticipantes() {
        List<Coluna<ParticipanteComiteVO>> colunas = new LinkedList<Coluna<ParticipanteComiteVO>>();
        colunas.add(new Coluna<ParticipanteComiteVO>().setCabecalho(NOME).setPropriedade(PROP_NOME)
                .setEstiloCabecalho(WIDTH_93).setOrdenar(false));
        colunas.add(new Coluna<ParticipanteComiteVO>().setCabecalho("Selecionar").setEstiloCabecalho(WIDTH_07)
                .setCssCabecalho(CENTRALIZADO)
                .setComponente(new IColunaComponente<ParticipanteComiteVO>() {
                    @Override
                    public Component obterComponente(Item<ICellPopulator<ParticipanteComiteVO>> cellItem,
                            String componentId, IModel<ParticipanteComiteVO> rowModel) {
                        return new AcaoPossiveisParticipantes(componentId, rowModel, 
                                possiveisParticipantes, participantesEfetivos, 
                                participantesEfetivosIncluidos, participantesEfetivosExcluidos);
                    }
                }));
        
        return colunas;
    }
    
    private ProviderGenericoList<ParticipanteComiteVO> criarProviderPossiveisParticipantes() {
        ProviderGenericoList<ParticipanteComiteVO> provider =
                new ProviderGenericoList<ParticipanteComiteVO>(
                        NOME, SortOrder.ASCENDING, obterModelPossiveisParticipantes());
        return provider;
    }

    private IModel<List<ParticipanteComiteVO>> obterModelPossiveisParticipantes() {
        IModel<List<ParticipanteComiteVO>> modelConsulta =
                new AbstractReadOnlyModel<List<ParticipanteComiteVO>>() {
                    @Override
                    public List<ParticipanteComiteVO> getObject() {
                        return possiveisParticipantes;
                    }
                };
        return modelConsulta;
    }
    
    private List<Coluna<ParticipanteComiteVO>> obterColunasParticipantesEfetivos() {
        List<Coluna<ParticipanteComiteVO>> colunas = new LinkedList<Coluna<ParticipanteComiteVO>>();
        colunas.add(new Coluna<ParticipanteComiteVO>().setCabecalho(NOME).setPropriedade(PROP_NOME)
                .setEstiloCabecalho(WIDTH_93).setOrdenar(false));
        colunas.add(new Coluna<ParticipanteComiteVO>().setCabecalho("Remover").setEstiloCabecalho(WIDTH_07)
                .setCssCabecalho(CENTRALIZADO)
                .setComponente(new IColunaComponente<ParticipanteComiteVO>() {
                    @Override
                    public Component obterComponente(Item<ICellPopulator<ParticipanteComiteVO>> cellItem,
                            String componentId, IModel<ParticipanteComiteVO> rowModel) {
                        return new AcaoParticipantesEfetivos(componentId, rowModel,
                                possiveisParticipantes, participantesEfetivos, 
                                participantesEfetivosIncluidos, participantesEfetivosExcluidos);
                    }
                }));
        return colunas;
    }
    
    private ProviderGenericoList<ParticipanteComiteVO> criarProviderParticipantesEfetivos() {
        ProviderGenericoList<ParticipanteComiteVO> provider =
                new ProviderGenericoList<ParticipanteComiteVO>(
                        NOME, SortOrder.ASCENDING, obterModelParticipantesEfetivos());
        return provider;
    }

    private IModel<List<ParticipanteComiteVO>> obterModelParticipantesEfetivos() {
        IModel<List<ParticipanteComiteVO>> modelConsulta =
                new AbstractReadOnlyModel<List<ParticipanteComiteVO>>() {
                    @Override
                    public List<ParticipanteComiteVO> getObject() {
                        return participantesEfetivos;
                    }
                };
        return modelConsulta;
    }
    
    private Configuracao obterConfiguracao(String wicketId, boolean exibirPaginador) {
        Configuracao cfg = new Configuracao("idTitulo" + wicketId, "idDados" + wicketId);
        cfg.setMensagemVazio(Model.of(Constantes.NENHUM_REGISTRO_ENCONTRADO));
        cfg.setExibirPaginador(exibirPaginador);
        cfg.setExibirTitulo(false);
        return cfg;
    }
    
    public void setarBotaoEAlertaVisiveis() {
        botaoSalvarInformacoes.setEnabled(true);
        alerta.setVisible(true);
    }
    
    public void atualizarTabelasParticipantes(AjaxRequestTarget target) {
        target.add(tabelaPossiveisParticipantes);
        target.add(tabelaParticipantesEfetivos);
        target.add(botaoSalvarInformacoes);
        target.add(alerta);
    }

    public AjaxSubmitLinkSisAps getBotaoSalvarInformacoes() {
        return botaoSalvarInformacoes;
    }

    public List<String> getIdsAlertas() {
        return idsAlertas;
    }

    
}
