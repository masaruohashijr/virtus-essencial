package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa;

import static br.gov.bcb.app.stuff.util.props.PropertyUtils.getPropertyObject;
import static br.gov.bcb.app.stuff.util.props.PropertyUtils.property;
import static br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.TabelaSemFormatoHelper.buildTabela;
import static br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.TabelaSemFormatoHelper.getFormatoCabecalho;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import br.gov.bcb.sisaps.src.vo.analisequantitativa.IndiceVO;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.QuadroPosicaoFinanceiraVO;
import br.gov.bcb.sisaps.web.page.componentes.mascara.MascaraNumericaBehaviour;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.IColunaComponente;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.TabelaSemFormatoHelper.FormatoColunaBold;

public class BuildTabelaIndices implements Serializable {
    private static final String WIDTH_15 = "width: 15%;";
    private static final String AJUSTADO = "Ajustado";
    private String id;
    private Map<IndiceVO, ColunaFragmentValorAjusteAjustado<IndiceVO>> mapFragmentoAjuste = //
            new HashMap<IndiceVO, ColunaFragmentValorAjusteAjustado<IndiceVO>>();

    public BuildTabelaIndices(String id) {
        this.id = id;
    }

    protected void criarTabela(final WebMarkupContainer container, final QuadroPosicaoFinanceiraVO vo,
            Configuracao configuracao, boolean editable) {
        List<Coluna<IndiceVO>> colunas = new LinkedList<Coluna<IndiceVO>>();
        colunas.add(new Coluna<IndiceVO>().setFormatador(new FormatoColunaBold<IndiceVO>())
                .setEstiloCabecalho("background-color:#fff; color:#000; width: 70%;").setCabecalho("")
                .setPropriedade(property(getPropertyObject(IndiceVO.class).getDescricaoIndice())));

        colunas.add(new Coluna<IndiceVO>().setEstiloCabecalho(getFormatoCabecalho(WIDTH_15)).setCabecalho("Índice")
                .setPropriedadeTela(property(getPropertyObject(IndiceVO.class).getValorIndiceFormatado()))
                .setPropriedade(property(getPropertyObject(IndiceVO.class).getValorIndice())));

        final String propriedadeAjuste = property(getPropertyObject(IndiceVO.class).getValorIndiceAjustado());
        boolean temValorAjustado = false;
        for (IndiceVO indiceVO : vo.getIndices()) {
            if (indiceVO.getValorIndiceAjustado() != null) {
                temValorAjustado = true;
                break;
            }
        }
        Coluna<IndiceVO> colunaAjustado =
                new Coluna<IndiceVO>().setEstiloCabecalho(getFormatoCabecalho(WIDTH_15)).setCabecalho(AJUSTADO)
                        .setPropriedade(propriedadeAjuste)
                        .setPropriedade(property(getPropertyObject(IndiceVO.class).getValorIndiceAjustadoFormatado()));

        if (editable) {
            buildColunaAjustado(container, colunas, propriedadeAjuste, colunaAjustado);
        } else if (temValorAjustado) {
            colunas.add(colunaAjustado);
        }
        container.addOrReplace(buildTabela(id, new LoadableDetachableModel<List<IndiceVO>>() {
            @Override
            protected List<IndiceVO> load() {
                return vo.getIndices() == null ? new ArrayList<IndiceVO>() : vo.getIndices();
            }
        }, configuracao, colunas, property(getPropertyObject(IndiceVO.class).getValorIndice()), SortOrder.DESCENDING));
    }

    private void buildColunaAjustado(final WebMarkupContainer container, List<Coluna<IndiceVO>> colunas,
            final String propriedadeAjuste, Coluna<IndiceVO> colunaAjustado) {
        colunas.add(colunaAjustado.setComponente(new IColunaComponente<IndiceVO>() {
            @Override
            public Component obterComponente(Item<ICellPopulator<IndiceVO>> cellItem, String componentId,
                    final IModel<IndiceVO> rowModel) {
                return criarComponente(container, propriedadeAjuste,
                        componentId, rowModel);
            }
        }));
    }

    private Component criarComponente(final WebMarkupContainer container,
            final String propriedadeAjuste, String componentId, final IModel<IndiceVO> rowModel) {
        ColunaFragmentValorAjusteAjustado<IndiceVO> colunaFragmentValorAjusteAjustado =
                new ColunaFragmentValorAjusteAjustado<IndiceVO>(componentId, "fragmentInput", container,
                        rowModel.getObject(), propriedadeAjuste) {
            @Override
            protected void executorOnUpdateExterno(AjaxRequestTarget target) {
                int temAsterisco = 0;
                for (ColunaFragmentValorAjusteAjustado<?> frag : mapFragmentoAjuste.values()) {
                    if (frag.getIsMostrar()) {
                        temAsterisco++;
                    }
                }
                prepararTargetAjuste(getTextField(), target, temAsterisco > 0);
            }
            @Override
            protected void onAddConfiguracaoTextField(TextField<IndiceVO> textField) {
                if (textField != null) {
                    textField.setMarkupId("input" + getObjeto().getDescricaoIndice().replace(" ", ""));
                    textField.add(new MascaraNumericaBehaviour("##,##"));
                }
            }
        };
        mapFragmentoAjuste.put(rowModel.getObject(), colunaFragmentValorAjusteAjustado);
        return mapFragmentoAjuste.get(rowModel.getObject());
    }
    
    private void prepararTargetAjuste(TextField<IndiceVO> textField, AjaxRequestTarget target,
            boolean isMostrarInformacoesNaoSalvas) {
        GerenciarQuadroPosicaoFinanceira pageAtual = (GerenciarQuadroPosicaoFinanceira) textField.getPage();
        PainelAjusteIndices painelaAjusteIndices = pageAtual.getPainelAjusteIndices();
        if (isMostrarInformacoesNaoSalvas) {
            painelaAjusteIndices.setInformacoesNaoSalvas(Boolean.TRUE);
        } else {
            painelaAjusteIndices.setInformacoesNaoSalvas(Boolean.FALSE);
        }
        target.add(pageAtual.getPainelQuadroPosicaoFinanceira());
        target.add(painelaAjusteIndices.getLabelInfo());
    }
}
