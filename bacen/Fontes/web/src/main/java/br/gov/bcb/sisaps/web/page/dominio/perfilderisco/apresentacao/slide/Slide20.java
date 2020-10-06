
package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.slide;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;
import br.gov.bcb.sisaps.src.vo.RiscoVO;

public class Slide20 extends Slide {
    
    private WebMarkupContainer grupo;

    private WebMarkupContainer grupoExterno;
    // Construtor
    public Slide20(ApresentacaoVO apresentacaoVO) {
        super(SecaoApresentacaoEnum.ANALISE_QUALITATIVA_POR_RISCOS, apresentacaoVO.getNomeEs());
        
        // Adiciona os componentes.
        montarComponentes(apresentacaoVO);
    }
    
    // Monta os componentes do painel.
    @Transactional
    private void montarComponentes(ApresentacaoVO apresentacaoVO) {
        add(new ListView<RiscoVO>("idListaGrupos", apresentacaoVO.getRiscosVO()) {
            // Adiciona os grupos.
            @Override
            protected void populateItem(ListItem<RiscoVO> item) {
                addComponentesItem(item);
            }
        });
    }
    
    private void addComponentesItem(ListItem<RiscoVO> item) {
        // Declarações
        RiscoVO riscoVO;
        
        // Inicializações
        riscoVO = item.getModelObject();
        
        // Nome do risco.
        item.add(new Label("idNome", riscoVO.getNome()));
        
        // Notas
        grupo = new WebMarkupContainer("idGrupo");
        grupo.addOrReplace(new Label("idNotaRisco", riscoVO.getNotaRisco()));
        grupo.addOrReplace(new Label("idNotaControle", riscoVO.getNotaControle()).setOutputMarkupId(true));
        grupo.addOrReplace(new Label("idNotaResidual", riscoVO.getNotaResidual()).setOutputMarkupId(true));
        // Conceito residual.
        grupo.addOrReplace(new Label("idConceitoResidual", riscoVO.getConceitoResidual()));
        grupo.setVisible(!riscoVO.isArcExterno() && !riscoVO.isEObrigatorio());
        grupo.setOutputMarkupId(true);
        grupo.setMarkupId(grupo.getId() + riscoVO.getNome());
        item.add(grupo);
        grupoExterno = new WebMarkupContainer("idGrupoExterno");
        grupoExterno.addOrReplace(new Label("idNotaExterno", riscoVO.getNotaRisco()).setOutputMarkupId(true));
        grupoExterno.setOutputMarkupId(true);
        grupoExterno.setVisible(riscoVO.isArcExterno());
        item.add(grupoExterno);
        
        WebMarkupContainer grupoObrigatorio = new WebMarkupContainer("idGrupoObrigatorio");
        grupoObrigatorio.addOrReplace(new Label("idNotaRiscoObrigatorio", riscoVO.getNotaRisco()));
        grupoObrigatorio.setVisible(!riscoVO.isArcExterno() && riscoVO.isEObrigatorio());
        item.add(grupoObrigatorio);

        // Síntese do risco.
        item.add(new Label("idSintese", riscoVO.getSintese()).setEscapeModelStrings(false));
    }

}
