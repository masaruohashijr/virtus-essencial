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

	// A apresentação.
	private final ApresentacaoVO apresentacaoVO;
	
	// A seção.
	private final SecaoApresentacaoEnum secao;
	
	// Construtor
	public ModelAnexosApresentacao(ApresentacaoVO apresentacaoVO, SecaoApresentacaoEnum secao) {
		// Guarda a apresentação.
		this.apresentacaoVO = apresentacaoVO;
		this.secao = secao;
	}

	@Override
	public List<AnexoApresentacaoVO> getObject() {
		// Declarações
		List<AnexoApresentacaoVO> anexosVOSecao;

		// Inicializações
		anexosVOSecao = new LinkedList<AnexoApresentacaoVO>();

		// Pesquisa a lista de anexos.
		for (AnexoApresentacaoVO anexoVO : apresentacaoVO.getAnexosVO()) {
			// Verifica se não é da seção solicitada.
			if (anexoVO.getSecao() != secao) continue;

			// Adiciona o anexo à lista.
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
		
		// Atualiza a apresentação.
		apresentacaoVO.getAnexosVO().remove(anexoApresentacaoVO);
		
		// Dispara evento de anexo excluído.
		aoExcluirAnexo(target);
	}

	// Evento de anexo excluído.
	public abstract void aoExcluirAnexo(AjaxRequestTarget target);
	
}
