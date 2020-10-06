package br.gov.bcb.sisaps.web.page.painelComite;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.gov.bcb.sisaps.src.dominio.ControleBatchEncerrarCorec;
import br.gov.bcb.sisaps.src.mediator.ControleBatchEncerrarCorecMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.ProviderGenericoList;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;

public class PainelProcessoEncerramentoCorec extends PainelSisAps {

    private static final String WIDTH_20 = "width:20%";
    private static final String ULTIMA_ATUALIZACAO = "ultimaAtualizacao";
    private Tabela<ControleBatchEncerrarCorec> tabela;

    public PainelProcessoEncerramentoCorec(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        addComponents();
    }

    private void addComponents() {
        addListaCiclo();
    }

    private void addListaCiclo() {
        // configuração
        Configuracao cfg = obterConfiguracao();
        // colunas da tabela
        List<Coluna<ControleBatchEncerrarCorec>> colunas = obterColunas();

        // tabela
        tabela = new Tabela<ControleBatchEncerrarCorec>("tabela", cfg, colunas, criarProvider(), true);
        addOrReplace(tabela);
    }

    private ProviderGenericoList<ControleBatchEncerrarCorec> criarProvider() {
        ProviderGenericoList<ControleBatchEncerrarCorec> provider =
                new ProviderGenericoList<ControleBatchEncerrarCorec>(
                        ULTIMA_ATUALIZACAO, SortOrder.DESCENDING, obterModel());
        return provider;
    }

    private IModel<List<ControleBatchEncerrarCorec>> obterModel() {
        IModel<List<ControleBatchEncerrarCorec>> modelConsulta =
                new AbstractReadOnlyModel<List<ControleBatchEncerrarCorec>>() {
            @Override
            public List<ControleBatchEncerrarCorec> getObject() {
                return ControleBatchEncerrarCorecMediator.get().listarProcessos();
            }
        };
        return modelConsulta;
    }

    private Configuracao obterConfiguracao() {
        Configuracao cfg = new Configuracao("idTitulo", "idDados");
        cfg.setMensagemVazio(Model.of(Constantes.NENHUM_REGISTRO_ENCONTRADO));
        cfg.setExibirPaginador(false);
        cfg.setExibirTitulo(false);
        return cfg;

    }

    private List<Coluna<ControleBatchEncerrarCorec>> obterColunas() {
        List<Coluna<ControleBatchEncerrarCorec>> colunas = new LinkedList<Coluna<ControleBatchEncerrarCorec>>();

        colunas.add(new Coluna<ControleBatchEncerrarCorec>().setCabecalho("ES")
                .setPropriedade("ciclo.entidadeSupervisionavel.nome")
                .setEstiloCabecalho("width:40%").setOrdenar(true));
        colunas.add(new Coluna<ControleBatchEncerrarCorec>().setCabecalho("Corec")
                .setPropriedade("ciclo.dataPrevisaoCorec").setPropriedadeTela("ciclo.dataPrevisaoFormatada")
                .setEstiloCabecalho(WIDTH_20).setOrdenar(true));
        colunas.add(new Coluna<ControleBatchEncerrarCorec>().setCabecalho("Estado")
                .setPropriedade("estadoExecucao.descricao")
                .setEstiloCabecalho(WIDTH_20).setOrdenar(true));
        colunas.add(new Coluna<ControleBatchEncerrarCorec>().setCabecalho("Data de última atualização")
                .setPropriedade(ULTIMA_ATUALIZACAO).setPropriedadeTela("dataHoraFormatada")
                .setEstiloCabecalho(WIDTH_20).setOrdenar(true));

        return colunas;
    }

}
