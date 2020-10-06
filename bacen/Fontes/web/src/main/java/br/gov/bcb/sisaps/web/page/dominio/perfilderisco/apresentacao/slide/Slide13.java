package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.slide;

import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativa.PainelAnaliseQuantitativa;

public class Slide13 extends Slide {
	
	// Construtor
	public Slide13(PerfilRisco perfilRisco, ApresentacaoVO apresentacaoVo) {
		super(SecaoApresentacaoEnum.POSICAO_FINANCEIRA_RESULTADOS, apresentacaoVo.getNomeEs());
		
        // Adiciona os componentes.
        montarComponentes(perfilRisco);
	}
	
	// Monta os componentes do painel.
	@Transactional
	private void montarComponentes(PerfilRisco perfilRisco) {
        add(new PainelAnaliseQuantitativa("idAnaliseQuantitativa", perfilRisco, true));
	}

}
