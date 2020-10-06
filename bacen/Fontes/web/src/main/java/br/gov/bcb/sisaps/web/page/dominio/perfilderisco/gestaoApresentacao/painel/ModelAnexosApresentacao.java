package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.gestaoApresentacao.painel;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.AbstractReadOnlyModel;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.src.mediator.AnexoApresentacaoMediator;
import br.gov.bcb.sisaps.src.vo.AnexoApresentacaoVO;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;

public abstract class ModelAnexosApresentacao extends AbstractReadOnlyModel<List<AnexoApresentacaoVO>> {

	// A apresenta��o.
	private final ApresentacaoVO apresentacaoVO;
	
	// A se��o.
	private final SecaoApresentacaoEnum secao;
	
	// Construtor
	public ModelAnexosApresentacao(ApresentacaoVO apresentacaoVO, SecaoApresentacaoEnum secao) {
		// Guarda a apresenta��o.
		this.apresentacaoVO = apresentacaoVO;
		this.secao = secao;
	}

	@Override
	public List<AnexoApresentacaoVO> getObject() {
		// Declara��es
		List<AnexoApresentacaoVO> anexosVOSecao;

		// Inicializa��es
		anexosVOSecao = new LinkedList<AnexoApresentacaoVO>();

		// Pesquisa a lista de anexos.
		for (AnexoApresentacaoVO anexoVO : apresentacaoVO.getAnexosVO()) {
			// Verifica se n�o � da se��o solicitada.
			if (anexoVO.getSecao() != secao) continue;

			// Adiciona o anexo � lista.
			anexosVOSecao.add(anexoVO);
		}
		
		Collections.sort(anexosVOSecao, new Comparator<AnexoApresentacaoVO>() {
            @Override
            public int compare(AnexoApresentacaoVO a1, AnexoApresentacaoVO a2) {
                return a1.getLink().compareToIgnoreCase(a2.getLink());
            }
        });

		return anexosVOSecao;
	}
	
	// Exclui um anexo do model.
	public void excluirAnexo(AjaxRequestTarget target, AnexoApresentacaoVO anexoApresentacaoVO) {
		// Exclui o anexo.
		AnexoApresentacaoMediator.get().excluirAnexo(anexoApresentacaoVO);
		
		// Atualiza a apresenta��o.
		apresentacaoVO.getAnexosVO().remove(anexoApresentacaoVO);
		
		// Dispara evento de anexo exclu�do.
		aoExcluirAnexo(target);
	}

	// Evento de anexo exclu�do.
	public abstract void aoExcluirAnexo(AjaxRequestTarget target);
	
}
