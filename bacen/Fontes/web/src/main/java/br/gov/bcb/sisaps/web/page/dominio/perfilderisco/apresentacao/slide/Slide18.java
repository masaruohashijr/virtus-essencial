package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.slide;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoLinhaAtividadeVOEnum;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;
import br.gov.bcb.sisaps.src.vo.LinhaAtividadeVO;

public class Slide18 extends Slide {

    // Construtor
    public Slide18(ApresentacaoVO apresentacaoVO) {
        super(SecaoApresentacaoEnum.IDENTIFICACAO_UNIDADES_ATIVIDADES, apresentacaoVO.getNomeEs());

        // Adiciona os componentes.
        montarComponentes(apresentacaoVO);
    }

    // List view de atividades.
    private static class ListViewAtividades extends ListView<LinhaAtividadeVO> {

        // Construtor
        public ListViewAtividades(String id, List<LinhaAtividadeVO> lista) {
            super(id, lista);
        }

        // Adiciona as atividades.
        @Override
        protected void populateItem(ListItem<LinhaAtividadeVO> item) {
            // Declarações
            Label label;

            // Nome
            label = new Label("idNome", item.getModelObject().getNome());
            if (item.getModelObject().isFilho()) {
                label.add(new AttributeAppender("class", "nivel_2"));
            } else {
                label.add(new AttributeAppender("class", "nivel_1"));
            }
            item.add(label);

            // Peso
            item.add(new Label("idPeso", item.getModelObject().getParametroPeso().getDescricao()));
        }
    };

    // Monta os componentes do painel.
    @Transactional
    private void montarComponentes(ApresentacaoVO apresentacaoVO) {
        // Declarações
        WebMarkupContainer srcAnterior;

        // SRC atual.
        add(new Label("idNegocio", apresentacaoVO.getDadosCicloVO().getPercentualNegocio()));
        add(new Label("idCorporativa", apresentacaoVO.getDadosCicloVO().getPercentualCorporativo()));

        add(new ListViewAtividades("idListaAtividadesNegocio", getAtividades(apresentacaoVO.getDadosCicloVO()
                .getAtividades(), false)));
        add(new ListViewAtividades("idListaAtividadesCorporativa", getAtividades(apresentacaoVO.getDadosCicloVO()
                .getAtividades(), true)));

        // SRC anterior.
        if (apresentacaoVO.getDadosCicloVOAnteriorVO() != null) {
            srcAnterior = new WebMarkupContainer("idSCRAnterior");
            add(srcAnterior);

            srcAnterior.add(new Label("idNegocio", apresentacaoVO.getDadosCicloVOAnteriorVO().getPercentualNegocio()));
            srcAnterior.add(new Label("idCorporativa", apresentacaoVO.getDadosCicloVOAnteriorVO()
                    .getPercentualCorporativo()));

            srcAnterior.add(new ListViewAtividades("idListaAtividadesNegocio", getAtividades(apresentacaoVO
                    .getDadosCicloVOAnteriorVO().getAtividades(), false)));
            srcAnterior.add(new ListViewAtividades("idListaAtividadesCorporativa", getAtividades(apresentacaoVO
                    .getDadosCicloVO().getAtividades(), true)));

        } else {
            add(new Label("idSCRAnterior").setVisible(false));
        }
    }

    // Extrai as atividades de
    private List<LinhaAtividadeVO> getAtividades(List<LinhaAtividadeVO> atividadesVO, boolean corporativa) {
        // Declarações
        List<LinhaAtividadeVO> atividadesVOSelecionadas;

        // Inicializações
        atividadesVOSelecionadas = new LinkedList<LinhaAtividadeVO>();

        // Analisa as atividades.
        for (LinhaAtividadeVO linhaAtividadeVO : atividadesVO) {
            // Ignora ARCs.
            if (linhaAtividadeVO.getTipo() == TipoLinhaAtividadeVOEnum.ARC) {
                continue;
            }

            // Filtrar as corporativas?
            if (corporativa) {
                // Verifica se é uma atividade corporativa.
                if (linhaAtividadeVO.getNomeParametroTipoAtividade() == "Corporativa") {
                    // Adiciona à lista.
                    atividadesVOSelecionadas.add(linhaAtividadeVO);
                }
            } else {
                // Verifica se é uma atividade de negócio.
                if (linhaAtividadeVO.getNomeParametroTipoAtividade() != "Corporativa") {
                    // Adiciona à lista.
                    atividadesVOSelecionadas.add(linhaAtividadeVO);
                }
            }
        }

        return atividadesVOSelecionadas;
    }
}
