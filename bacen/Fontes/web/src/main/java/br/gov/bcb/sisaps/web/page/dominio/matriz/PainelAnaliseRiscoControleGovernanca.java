package br.gov.bcb.sisaps.web.page.dominio.matriz;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TituloTelaARCEnum;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleExternoMediator;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.vo.ArcNotasVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.PainelDadosMatrizPercentual;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.UtilNavegabilidadeARC;

@SuppressWarnings("serial")
public class PainelAnaliseRiscoControleGovernanca extends PainelSisAps {

    private static final String NOTA = "nota";
    private boolean perfilAtual = true;
    private final ArcNotasVO arcExterno;
    private final PerfilAcessoEnum perfilPorMenu;
    private final PainelDadosMatrizPercentual painelDadosMatriz;
    private String titulo;

    public PainelAnaliseRiscoControleGovernanca(String id, PerfilAcessoEnum perfilPorMenu, 
            PainelDadosMatrizPercentual painelDadosMatriz, String titulo) {
        super(id);
        this.painelDadosMatriz = painelDadosMatriz;
        this.perfilPorMenu = perfilPorMenu;
        this.arcExterno = buscarArcExterno();
        this.titulo = titulo;
        setMarkupId(id);
        setOutputMarkupId(true);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
    }

    @Override
    protected void onConfigure() {
        addComponentes();
    }

    private ArcNotasVO buscarArcExterno() {
        if (painelDadosMatriz.getPerfilRisco() == null) {
            return AvaliacaoRiscoControleExternoMediator.get()
                    .buscarUltimoArcExternoVO(painelDadosMatriz.getMatriz().getCiclo().getPk());
        } else {
            return AvaliacaoRiscoControleMediator.get()
                    .consultarNotasArcExterno(painelDadosMatriz.getPerfilRisco().getPk());
        }
    }

    private void addComponentes() {
        
        addOrReplace(new Label("tituloPainel", titulo));
        Label lPercentualPeso = new Label("colunaPercentualAE", 
                painelDadosMatriz.getMatriz() == null ? 
                        "" : painelDadosMatriz.getMatriz().getPercentualGovernancaCorporativa());
        add(lPercentualPeso);
        
        
        WebMarkupContainer dados = new WebMarkupContainer("dados");
        
        Link<String> link = new AjaxFallbackLink<String>("lnkNotaGovernancaCorporativa") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                TituloTelaARCEnum titulo =
                        UtilNavegabilidadeARC.novaPaginaDeAcordoStatus(
                                painelDadosMatriz.getMatriz(), arcExterno, 
                                painelDadosMatriz.isEmAnalise(), 
                                perfilAtual, perfilPorMenu);
                
                instanciarPaginaDestinoARC(arcExterno, painelDadosMatriz.getMatriz(), null, titulo, perfilAtual);
            }
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(painelDadosMatriz.mostrarLinkMatriz(arcExterno));
            }
        };
        
        String nota = arcExterno == null ? Constantes.ESPACO_EM_BRANCO : painelDadosMatriz.buscarNota(arcExterno);
        link.add(new Label(NOTA, nota));
        boolean mostrarLink = true;
        link.setVisible(mostrarLink);
        
        dados.add(link);
        dados.add(painelDadosMatriz.labelValorNotaString(NOTA, arcExterno, nota));
        if (painelDadosMatriz.montarBordaEmAnalise(arcExterno)) {
            dados.add(new AttributeAppender("style", painelDadosMatriz.obterBorda(), Constantes.ESPACO_EM_BRANCO));
        }
        dados.add(new AttributeAppender("bgcolor", painelDadosMatriz.obterCorNota(nota), Constantes.ESPACO_EM_BRANCO));
        add(dados);
    }
    
}
