package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.slide;

import java.util.Date;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;

public class Slide01 extends Slide {

    @SpringBean
    private CicloMediator cicloMediator;
    
	// Construtor
	public Slide01(Ciclo ciclo, ApresentacaoVO apresentacaoVO) {
		super(SecaoApresentacaoEnum.SRC, apresentacaoVO.getNomeEs());
		
        // Adiciona os componentes.
        montarComponentes(ciclo, apresentacaoVO);
	}

	// Monta os componentes do painel.
	private void montarComponentes(Ciclo ciclo, ApresentacaoVO apresentacaoVO) {
		// Declarações
		EntidadeSupervisionavel es;
		ServidorVO supervisor;
    	String situacaoDescricao;
		
    	// Inicializações
		es = ciclo.getEntidadeSupervisionavel();
		Date dataBase = null;
		if (!ciclo.getEstadoCiclo().getEstado().equals(EstadoCicloEnum.EM_ANDAMENTO)) {
		    dataBase = ciclo.getDataPrevisaoCorec();
		}
		supervisor = cicloMediator.buscarChefeAtual(es.getLocalizacao(), dataBase);
		if (apresentacaoVO.getSituacaoNome() != null) {
			situacaoDescricao = apresentacaoVO.getSituacaoNome();
		} else {
			situacaoDescricao = "";
		}
		
		// Adiciona os componentes ao painel.
		add(new Label("idNomeConglomeradoSlide01", es.getNomeConglomeradoFormatado()));
        add(new Label("idNomeSupervisor", supervisor.getNome()));
        add(new Label("idDataInicioCiclo",
        		SisapsUtil.getDataFormatadaComBarras(ciclo.getDataInicio())));
        add(new Label("idDataCorec",
        		SisapsUtil.getDataFormatadaComBarras(ciclo.getDataPrevisaoCorec())));
        add(new Label("idPrioridade", es.getPrioridade().getDescricao()));
        add(new Label("idSituacao", situacaoDescricao));
	}
    
}
