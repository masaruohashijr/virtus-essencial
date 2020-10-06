package br.gov.bcb.sisaps.web.page.dominio.gestaoAgenda;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.gov.bcb.sisaps.src.dominio.AnexoParticipanteComite;
import br.gov.bcb.sisaps.src.mediator.AnexoParticipanteComiteMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.ProviderGenericoList;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;

public class PainelListagemAnexoParticipantes extends PainelSisAps {

    private static final String WIDTH_50 = "width:50%";
    private Tabela<AnexoParticipanteComite> tabela;

    public PainelListagemAnexoParticipantes(String id) {
        super(id);
        addTabela();
    }

    private void addTabela() {
        Configuracao cfg = obterConfiguracao();
        ProviderGenericoList<AnexoParticipanteComite> providerGenericoList = criarProvider();
        List<Coluna<AnexoParticipanteComite>> colunas = obterColunas();
        tabela = new Tabela<AnexoParticipanteComite>("tabela", cfg, colunas, providerGenericoList, true);
        addOrReplace(tabela);
    }

    private Configuracao obterConfiguracao() {
        Configuracao cfg = new Configuracao("idTitulo", "idDados");
        cfg.setExibirTitulo(true);
        cfg.setTitulo(Model.of("Histórico de arquivos processados (5 últimos arquivos)"));
        cfg.setCssTitulo(Model.of("tabela fundoPadraoAEscuro3"));
        cfg.setExibirPaginador(false);
        cfg.setMensagemVazio(Model.of(Constantes.NENHUM_REGISTRO_ENCONTRADO));
        return cfg;
    }

    private ProviderGenericoList<AnexoParticipanteComite> criarProvider() {
        ProviderGenericoList<AnexoParticipanteComite> provider =
                new ProviderGenericoList<AnexoParticipanteComite>("", SortOrder.ASCENDING, obterModel());
        return provider;
    }

    private IModel<List<AnexoParticipanteComite>> obterModel() {
        IModel<List<AnexoParticipanteComite>> modelConsulta =
                new AbstractReadOnlyModel<List<AnexoParticipanteComite>>() {
                    @Override
                    public List<AnexoParticipanteComite> getObject() {
                        return AnexoParticipanteComiteMediator.get().listarAnexos();
                    }
                };
        return modelConsulta;
    }

    private List<Coluna<AnexoParticipanteComite>> obterColunas() {
        List<Coluna<AnexoParticipanteComite>> colunas = new LinkedList<Coluna<AnexoParticipanteComite>>();

        colunas.add(new Coluna<AnexoParticipanteComite>().setCabecalho("Arquivo").setOrdenar(false)
                .setPropriedade("nome").setEstiloCabecalho(WIDTH_50));

        colunas.add(new Coluna<AnexoParticipanteComite>().setCabecalho("Data upload").setOrdenar(false)
                .setPropriedade("dataUploadFormatada").setEstiloCabecalho(WIDTH_50));

        return colunas;
    }

    public Tabela<AnexoParticipanteComite> getTabela() {
        return tabela;
    }

    public void setTabela(Tabela<AnexoParticipanteComite> tabela) {
        this.tabela = tabela;
    }

}
