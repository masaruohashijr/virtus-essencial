package br.gov.bcb.sisaps.web.page.painelComite;

import java.util.ArrayList;
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
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.EmailCorec;
import br.gov.bcb.sisaps.src.dominio.ParticipanteAgendaCorec;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEmailCorecEnum;
import br.gov.bcb.sisaps.src.mediator.EmailCorecMediator;
import br.gov.bcb.sisaps.src.mediator.ParticipanteAgendaCorecMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.ProviderGenericoList;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponente;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;
import br.gov.bcb.sisaps.web.page.dominio.gestaoAgenda.PainelArquivoParticipante;

public class PainelListagemParticipantesAgenda extends PainelSisAps {

    private static final String WIDTH_20 = "width:20%";
    private static final String NOME = "nome";

    private Tabela<ParticipanteAgendaCorec> tabela;
    private IModel<List<ParticipanteAgendaCorec>> modelConsulta;
    private ProviderGenericoList<ParticipanteAgendaCorec> provider;
    private PainelArquivoParticipante painelArquivo;
    private final Ciclo ciclo;
    private final AgendaCorec agenda;

    public PainelListagemParticipantesAgenda(String id, Ciclo ciclo, AgendaCorec agenda) {
        super(id);
        this.ciclo = ciclo;
        this.agenda = agenda;
        addTabela();
    }

    private void addTabela() {
        Configuracao cfg = obterConfiguracao();
        ProviderGenericoList<ParticipanteAgendaCorec> providerGenericoList = criarProvider();
        List<Coluna<ParticipanteAgendaCorec>> colunas = obterColunas();
        tabela = new Tabela<ParticipanteAgendaCorec>("tabela", cfg, colunas, providerGenericoList, true);
        addOrReplace(tabela);
    }

    private Configuracao obterConfiguracao() {
        Configuracao cfg = new Configuracao("idTitulo", "idDados");
        cfg.setExibirTitulo(false);
        cfg.setTitulo(Model.of("Participantes do comitê"));
        cfg.setExibirPaginador(false);
        cfg.setMensagemVazio(Model.of(Constantes.NENHUM_REGISTRO_ENCONTRADO));
        return cfg;
    }

    private ProviderGenericoList<ParticipanteAgendaCorec> criarProvider() {
        provider = new ProviderGenericoList<ParticipanteAgendaCorec>("", SortOrder.ASCENDING, obterModel());
        return provider;
    }

    private IModel<List<ParticipanteAgendaCorec>> obterModel() {
        modelConsulta = new AbstractReadOnlyModel<List<ParticipanteAgendaCorec>>() {
            @Override
            public List<ParticipanteAgendaCorec> getObject() {
                if (agenda == null) {
                    return new ArrayList<ParticipanteAgendaCorec>();
                }
                return ParticipanteAgendaCorecMediator.get().buscarParticipanteAgendaCorec(agenda.getPk());
            }
        };
        return modelConsulta;
    }

    private List<Coluna<ParticipanteAgendaCorec>> obterColunas() {
        List<Coluna<ParticipanteAgendaCorec>> colunas = new LinkedList<Coluna<ParticipanteAgendaCorec>>();

        colunas.add(new Coluna<ParticipanteAgendaCorec>().setCabecalho("Nome").setPropriedade(NOME).setOrdenar(false)
                .setEstiloCabecalho("width:60%"));

        colunas.add(new Coluna<ParticipanteAgendaCorec>().setCabecalho("Solicitação assinatura")
                .setComponente(new ComponenteLabel()).setOrdenar(false).setEstiloCabecalho(WIDTH_20));

        colunas.add(new Coluna<ParticipanteAgendaCorec>().setCabecalho("Assinatura da ata")
                .setPropriedade("dataAssinaturaFormatada").setOrdenar(false).setEstiloCabecalho(WIDTH_20));

        return colunas;
    }

    private class ComponenteLabel implements IColunaComponente<ParticipanteAgendaCorec> {

        @Override
        public Component obterComponente(Item<ICellPopulator<ParticipanteAgendaCorec>> cellItem, String componentId,
                final IModel<ParticipanteAgendaCorec> rowModel) {

            EmailCorec emailParticipante =
                    EmailCorecMediator.get().buscarEmailParticipante(rowModel.getObject().getAgenda().getPk(),
                            rowModel.getObject().getMatricula(), TipoEmailCorecEnum.SOLICITACAO_ASSINATURA);
            return new Label(componentId, emailParticipante == null ? ""
                    : emailParticipante.getDataAtualizacaoFormatada());

        }
    }

    public Tabela<ParticipanteAgendaCorec> getTabelaParticipantes() {
        return tabela;
    }

    public void setTabelaParticipantes(Tabela<ParticipanteAgendaCorec> tabela) {
        this.tabela = tabela;
    }

    public IModel<List<ParticipanteAgendaCorec>> getModelConsulta() {
        return modelConsulta;
    }

    public void setModelConsulta(IModel<List<ParticipanteAgendaCorec>> modelConsulta) {
        this.modelConsulta = modelConsulta;
    }

    public ProviderGenericoList<ParticipanteAgendaCorec> getProvider() {
        return provider;
    }

    public void setProvider(ProviderGenericoList<ParticipanteAgendaCorec> provider) {
        this.provider = provider;
    }

    public PainelArquivoParticipante getPainelArquivo() {
        return painelArquivo;
    }

    public void setPainelArquivo(PainelArquivoParticipante painelArquivo) {
        this.painelArquivo = painelArquivo;
    }

    public Ciclo getCiclo() {
        return ciclo;
    }

}
