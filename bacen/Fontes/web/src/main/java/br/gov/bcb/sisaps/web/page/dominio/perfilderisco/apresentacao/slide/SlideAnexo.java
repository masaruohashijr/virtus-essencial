package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.slide;

import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.request.resource.DynamicImageResource;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.src.mediator.AnexoApresentacaoMediator;
import br.gov.bcb.sisaps.src.vo.AnexoApresentacaoVO;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;

// Slide cujo conteúdo é um anexo da apresentação.
public class SlideAnexo extends Slide {
	
	// Construtor
	public SlideAnexo(SecaoApresentacaoEnum secao, final AnexoApresentacaoVO anexoVO, ApresentacaoVO apresentacaoVO) {
		super(secao, apresentacaoVO.getNomeEs());
		
		// Declarações
		DynamicImageResource dynamicImageResource;
		
		// Adiciona o conteúdo.
		dynamicImageResource = new DynamicImageResource() {
			@Override
			protected byte[] getImageData(Attributes attributes) {
				return AnexoApresentacaoMediator.get().recuperarArquivo(anexoVO);
			}
		};
		
		// Adiciona a imagem.
		add(new Image("idImagem", dynamicImageResource));
	}

}
