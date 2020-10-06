package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.slide;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;
import br.gov.bcb.sisaps.util.enumeracoes.NormalidadeEnum;

public class Slide11 extends Slide {

	// Construtor
	public Slide11(List<Slide> slides, ApresentacaoVO apresentacaoVO) {
		super(SecaoApresentacaoEnum.SITUACAO, apresentacaoVO.getNomeEs());
		
        // Adiciona os componentes.
        montarComponentes(slides, apresentacaoVO);
	}
	
	@Override
	protected String getTitulo() {
		return SecaoApresentacaoEnum.SITUACAO.getDescricao();
	}
	
	// Monta os componentes do painel.
	@Transactional
	private void montarComponentes(List<Slide> slides, ApresentacaoVO apresentacaoVO) {
		// Valida a situação.
		if (apresentacaoVO.getSituacaoNormalidade() == null 
				|| apresentacaoVO.getSituacaoNormalidade() == NormalidadeEnum.NORMAL) return;
				
		// Adiciona os componentes ao painel.
		add(new Label("idNome", apresentacaoVO.getSituacaoNome()));
        add(new Label("idJustificativa", apresentacaoVO.getSituacaoJustificativa())
        		.setEscapeModelStrings(false));
        
        // Adiciona o slide à apresentação.
        slides.add(this);
	}

}
