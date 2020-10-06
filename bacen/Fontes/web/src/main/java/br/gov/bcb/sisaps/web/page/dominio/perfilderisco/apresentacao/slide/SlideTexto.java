package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.slide;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;
import br.gov.bcb.sisaps.src.vo.TextoApresentacaoVO;

// Slide cujo conte�do � um anexo da apresenta��o.
public class SlideTexto extends Slide {
	
	// Construtor
	public SlideTexto(SecaoApresentacaoEnum secao, List<TextoApresentacaoVO> textosVO, ApresentacaoVO apresentacaoVO) {
		super(secao, apresentacaoVO.getNomeEs());
		
		// Monta o conteudo do slide.
		add(new ListView<TextoApresentacaoVO>("idTextos", textosVO) {
			// Monta os textos
			@Override
			protected void populateItem(ListItem<TextoApresentacaoVO> item) {
				// Declara��es
				Label label;
				
				// Adiciona o nome do campo.
				label = new Label("idNomeCampo", item.getModelObject().getCampo().getDescricao());
				item.add(label);
				
				// Adiciona o conte�do.
				label = new Label("idConteudoCampo", item.getModelObject().getTexto());
				label.setEscapeModelStrings(false);
				item.add(label);
			}
		});
	}

}
