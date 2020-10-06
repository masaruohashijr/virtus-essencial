package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.gestaoApresentacao.painel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.CampoApresentacaoEnum;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;
import br.gov.bcb.sisaps.src.vo.ObjetoPersistenteAuditavelVO;
import br.gov.bcb.sisaps.web.page.PainelSisAps;

public class PainelCampoApresentacao extends PainelSisAps {

	// O campo representado por este painel.
	protected final CampoApresentacaoEnum campo;
	
	private Label labelUltimaAlteracao;
	
	// Construtor
	public PainelCampoApresentacao(String id, ApresentacaoVO apresentacaoVO, CampoApresentacaoEnum campo) {
		super(id);
		
		// Inicializa��es
		this.campo = campo;
		
		// Monta os componentes.
		montarComponentes(apresentacaoVO);
	}

	// Monta os componentes do painel.
	private void montarComponentes(final ApresentacaoVO apresentacaoVO) {
		// Nome do campo.
		add(new Label("idNomeCampo", campo.getDescricao()));
		
		// Label indicando sobre a �ltima altera��o do campo.
        IModel<String> modelUltimaAlteracao = new LoadableDetachableModel<String>() {
            @Override
			public String load() {
            	// Declara��es
            	ObjetoPersistenteAuditavelVO valorVO;
            	
            	// Recupera o valor do campo.
            	valorVO = apresentacaoVO.getValor(campo);
            	
            	// Formata a informa��o de altera��o.
            	return ObjetoPersistenteAuditavelVO.getDadosFormatados(valorVO);
            }
        };

        labelUltimaAlteracao = new Label("idUltimaAlteracao", modelUltimaAlteracao);
        addOrReplace(labelUltimaAlteracao);
	}
	
	// Atualiza label de �ltima atualiza��o.
    protected void atualizarUltimaAtualizacao(AjaxRequestTarget target) {
        target.add(labelUltimaAlteracao);
    }
    
}
