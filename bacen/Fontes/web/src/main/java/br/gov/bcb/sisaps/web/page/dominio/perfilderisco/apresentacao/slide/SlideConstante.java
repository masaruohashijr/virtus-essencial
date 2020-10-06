package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.slide;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;

// Slide cujo conteúdo é constante.
public class SlideConstante extends Slide {
	
	// Construtor
	public SlideConstante(SecaoApresentacaoEnum secao, boolean doHighlight, ApresentacaoVO apresentacaoVO) {
		super(secao, false, apresentacaoVO.getNomeEs());
		
		// Declarações
		Label label;
		
		// Adiciona o conteúdo.
		label = new Label("idConteudo", secao.getDescricao());
		add(label);
		
		// Verifica se deve aplicar highlight.
		if (doHighlight) {
			label.add(new AttributeAppender("class", Model.of("highlight"), " "));
		}
	}

}
