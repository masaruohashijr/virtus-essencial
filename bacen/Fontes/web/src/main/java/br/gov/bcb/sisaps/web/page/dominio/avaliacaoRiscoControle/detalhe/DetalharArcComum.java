/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacoes proprietarias.
 */

package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.UnauthorizedActionException;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.comum.util.string.StringUtil;
import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControleExterno;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.mediator.AtividadeMediator;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleExternoMediator;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CelulaRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.MatrizCicloMediator;
import br.gov.bcb.sisaps.src.mediator.RegraPerfilAcessoMediator;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.consulta.ConsultaHistoricoPage;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.PainelAnexoArc;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.PainelResumoCiclo;

@SuppressWarnings("serial")
@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.SUPERVISOR, PerfisAcesso.INSPETOR,
        PerfisAcesso.GERENTE, PerfisAcesso.CONSULTA_NAO_BLOQUEADOS, PerfisAcesso.CONSULTA_RESUMO_NAO_BLOQUEADOS,
        PerfisAcesso.CONSULTA_TUDO, PerfisAcesso.ADMINISTRADOR})
public class DetalharArcComum extends DefaultPage {
    protected AvaliacaoRiscoControle avaliacao;
    protected Matriz matriz;
    protected Atividade atividade;
    protected ParametroGrupoRiscoControle grupo;
    protected Form<AvaliacaoRiscoControle> form = new Form<AvaliacaoRiscoControle>("formulario");
    protected boolean isPerfilAtual;
    private PainelAcoesArc painelAcoesArc;
    protected int pkAvaliacao;

    @SpringBean
    private AtividadeMediator atividadeMediator;
    @SpringBean
    private MatrizCicloMediator matrizMediator;
    @SpringBean
    private AvaliacaoRiscoControleMediator avaliacaoRiscoControleMediator;

    protected  AvaliacaoRiscoControle avaliacaoNova;
    private boolean isInicializar;

    public DetalharArcComum(AvaliacaoRiscoControle avaliacao, Matriz matriz, Integer pkAtividade, String sucesso,
            boolean isPerfilAtual) {
        this(avaliacao, matriz.getPk(), pkAtividade, true, isPerfilAtual);
        if (!StringUtil.isVazioOuNulo(sucesso)) {
            success(sucesso);
        }
    }

    
    public DetalharArcComum(AvaliacaoRiscoControle avaliacao, Matriz matriz, Integer pkAtividade,
            boolean isPerfilAtual) {
        this(avaliacao, matriz.getPk(), pkAtividade, true, isPerfilAtual);
    }

    public DetalharArcComum(AvaliacaoRiscoControle avaliacaoNova, Integer pkMatriz, Integer pkAtividade,
            boolean isInicializar, boolean isPerfilAtual) {
        this.matriz = matrizMediator.loadPK(pkMatriz);
        this.atividade = pkAtividade == null ? null : atividadeMediator.loadPK(pkAtividade);
        this.avaliacaoNova = avaliacaoNova;
        this.isInicializar = isInicializar;
        this.isPerfilAtual = isPerfilAtual;
    }
    
    public DetalharArcComum(PageParameters parameters) {
    	String p1 = parameters.get("pkArc").toString(null);
        this.pkAvaliacao = Integer.valueOf(p1);
        avaliacaoNova = AvaliacaoRiscoControleMediator.get().loadPK(pkAvaliacao);
        this.avaliacao = this.avaliacaoNova;
        this.isPerfilAtual = true;
        
        CelulaRiscoControle celula = CelulaRiscoControleMediator.get().buscarCelularPorAvaliacao(avaliacaoNova);
        if(celula == null){
            AvaliacaoRiscoControleExterno arcExterno =
                    AvaliacaoRiscoControleExternoMediator.get().buscarArcExterno(avaliacaoNova.getPk());
        	this.atividade = null;
        	this.matriz = matrizMediator.buscar(Integer.valueOf(arcExterno.getCiclo().getMatriz().getPk()));
			this.grupo = arcExterno.getParametroGrupoRiscoControle();
        }else {	
        	this.atividade = atividadeMediator.loadPK(Integer.valueOf(celula.getAtividade().getPk()));
        	this.grupo = celula.getParametroGrupoRiscoControle();
        	this.matriz = matrizMediator.buscar(Integer.valueOf(celula.getAtividade().getMatriz().getPk()));
        }
		
		if (RegraPerfilAcessoMediator.perfilInspetor() || RegraPerfilAcessoMediator.perfilSupervisor()) {
			setPaginaAnterior(new ConsultaHistoricoPage());
		}else {
            throw new UnauthorizedActionException(this, ENABLE);
        }
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (isInicializar && !avaliacaoRiscoControleMediator.estadoPrevistoDesignado(avaliacaoNova.getEstado())) {
            this.avaliacao = avaliacaoRiscoControleMediator.buscar(avaliacaoNova.getPk());
        } else {
            this.avaliacao = this.avaliacaoNova;
        }
        if (atividade == null) {
            this.grupo = AvaliacaoRiscoControleExternoMediator.get().buscarArcExterno(avaliacaoNova.getPk())
                    .getParametroGrupoRiscoControle();
        } else {
            this.grupo = CelulaRiscoControleMediator.get().buscarCelularPorAvaliacao(avaliacaoNova)
                    .getParametroGrupoRiscoControle();
        }
        addOrReplaceComponentes();
    }

    private void addOrReplaceComponentes() {

        form.addOrReplace(new PainelResumoCiclo("idPainelCicloResumo", matriz.getCiclo().getPk()));
        painelAcoesArc = new PainelAcoesArc("idPainelAcoesArc", avaliacao, atividade, matriz, isPerfilAtual);
        form.addOrReplace(painelAcoesArc);
        form.addOrReplace(new PainelAnexoArc("idPainelAnexoArc", matriz.getCiclo(), avaliacao, false)
                .setVisibilityAllowed(!avaliacao.getAnexosArc().isEmpty()));
        form.addOrReplace(new LinkVoltar());
        addOrReplace(form);
    }

    @Override
    public String getTitulo() {
        return "Detalhes do ARC";
    }

    @Override
    public String getCodigoTela() {
        return "APSFW0207";
    }

}
