package br.gov.bcb.sisaps.web.page.dominio.matriz;

import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.MatrizCicloMediator;
import br.gov.bcb.sisaps.src.mediator.NotaMatrizMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.web.page.componentes.grupo.ControleExpansivel;

public class PainelResumoMatriz extends Panel {

    private static final String T_RESUMO_MATRIZ = "tResumoMatriz";

    @SpringBean
    private NotaMatrizMediator notaMatrizMediator;

    private final Matriz matriz;

    private final PerfilRisco perfilRisco;

    private boolean isExibirNotaAjustada;

    private boolean isExibirNotaRefinada;

    private final boolean isExibirTitulo;

    private final boolean isExibirStringAAvaliar;
    private String notaCalculada;
    private List<String> mapNota;
    private final WebMarkupContainer wmcExibirNotaAjustada = new WebMarkupContainer("wmcExibirNotaAjustada");
    private final WebMarkupContainer wmcExibirSemAjustada = new WebMarkupContainer("wmcExibirSemAjustada");
    private final WebMarkupContainer wmcExibirNotaRefinada = new WebMarkupContainer("wmcExibirNotaRefinada");

    private final PerfilAcessoEnum perfilAcessoEnum;

    private String titulo;

    private final boolean emAnalise;

    public PainelResumoMatriz(String id, Matriz matriz, PerfilRisco perfilRisco, 
            boolean isExibirTitulo, boolean isExibirNotaAjustada, boolean isExibirNotaRefinada,
            boolean isExibirStringAAvaliar, PerfilAcessoEnum perfilAcessoEnum, String titulo, boolean emAnalise) {
        super(id);
        this.matriz = matriz;
        this.perfilRisco = perfilRisco;
        this.isExibirTitulo = isExibirTitulo;
        this.perfilAcessoEnum = perfilAcessoEnum;
        this.isExibirNotaAjustada = isExibirNotaAjustada;
        this.isExibirNotaRefinada = isExibirNotaRefinada;
        this.isExibirStringAAvaliar = isExibirStringAAvaliar;
        this.titulo = titulo;
        this.emAnalise = emAnalise;
        setMarkupId(id);
        setOutputMarkupId(true);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        addComponentes();

    }

    private void addComponentes() {
        String notaMatriz = "";
        String justificativaNotaAjustadaMatriz = "";
        mapNota = notaMatrizMediator.notaAjustada(matriz, perfilRisco, perfilAcessoEnum);
        if (!mapNota.isEmpty()) {
            notaMatriz = mapNota.get(0);
            justificativaNotaAjustadaMatriz = mapNota.get(1);
            isExibirNotaAjustada = !mapNota.get(0).equals(Constantes.VAZIO);
        }
        WebMarkupContainer tabelaPainel = new WebMarkupContainer(T_RESUMO_MATRIZ);
        tabelaPainel.setMarkupId(getMarkupIdTabelaPainel());
        addOrReplace(tabelaPainel);
        Label lblTitulo = new Label("idTituloResumoMatriz", titulo);
        tabelaPainel.addOrReplace(lblTitulo);
        addContainerNotaAjustada(notaMatriz, justificativaNotaAjustadaMatriz, tabelaPainel);

        WebMarkupContainer linhaAAvaliar = new WebMarkupContainer("LinhaAAvaliar");
        linhaAAvaliar.setVisible(isExibirStringAAvaliar);
        tabelaPainel.addOrReplace(linhaAAvaliar);

        addLabelNotaCalculada(tabelaPainel);
    }

    private void addLabelNotaCalculada(WebMarkupContainer tabelaPainel) {
        Label labelNotaCalculada =
                new Label("colunaNotaCalculada", getNotaCalculada() == null ? "" : getNotaCalculada().replace('.', ','));
        tabelaPainel.addOrReplace(labelNotaCalculada);

        WebMarkupContainer colunaNotaRefinada = new WebMarkupContainer("colunaNotaRefinada") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(isExibirNotaRefinada);
            }
        };

        wmcExibirNotaRefinada.addOrReplace(colunaNotaRefinada);

        Label labelNotaRefinada =
                new Label("colunaNotaRefinadaValor", getNotaCalculada() == null ? "" : buscarNotaRefinada()){
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(isExibirNotaRefinada);
            }
        };

        wmcExibirNotaRefinada.addOrReplace(labelNotaRefinada);
        tabelaPainel.addOrReplace(wmcExibirNotaRefinada);
    }

    private String buscarNotaRefinada() {
        return MatrizCicloMediator.get().notaRefinadaFinal(matriz.getCiclo().getMetodologia(), getNotaCalculada());
    }

    private void addContainerNotaAjustada(
            final String notaMatriz, final String justificativaNotaAjustadaMatriz,
            WebMarkupContainer tabelaPainel) {
        WebMarkupContainer linhaJustificativaNotaAjustada =
                new WebMarkupContainer("idLinhaJustificativaNotaAjustadaMatriz");

        linhaJustificativaNotaAjustada.addOrReplace(new Label("idJustificativaNotaAjustadaMatriz",
                justificativaNotaAjustadaMatriz).setEscapeModelStrings(false));

        linhaJustificativaNotaAjustada.setVisible(justificativaNotaAjustadaMatriz != null);

        tabelaPainel.addOrReplace(linhaJustificativaNotaAjustada);

        ControleExpansivel controle = new ControleExpansivel("controle", false, linhaJustificativaNotaAjustada) {
            @Override
            public String getMarkupIdLink() {
                return "idExpandirJustificativaNotaAjustadaMatriz_" + this.getId();
            }
        };
        controle.setOutputMarkupPlaceholderTag(true);
        controle.setVisible(justificativaNotaAjustadaMatriz != null
                && !justificativaNotaAjustadaMatriz.equals(Constantes.COREC));
        controle.setMarkupId(controle.getId() + "_" + this.getId());
        controle.setOutputMarkupId(true);

        WebMarkupContainer colunaControle = new WebMarkupContainer("colunaControle") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(isExibirNotaAjustada);
            }
        };
        colunaControle.addOrReplace(controle);
        wmcExibirNotaAjustada.addOrReplace(colunaControle);


        WebMarkupContainer colunaNotaAjustada = new WebMarkupContainer("colunaNotaAjustada") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(isExibirNotaAjustada);
            }
        };

        wmcExibirNotaAjustada.addOrReplace(colunaNotaAjustada);

        WebMarkupContainer colunaNotaAjustadaValor = new WebMarkupContainer("colunaNotaAjustadaValor") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(isExibirNotaAjustada);
            }
        };

        String nota = notaMatriz;
        if (justificativaNotaAjustadaMatriz != null && justificativaNotaAjustadaMatriz.equals(Constantes.COREC)) {
            nota += justificativaNotaAjustadaMatriz;
        }

        colunaNotaAjustadaValor.addOrReplace(new Label("notaAjustadaMatriz", nota));
        wmcExibirNotaAjustada.addOrReplace(colunaNotaAjustadaValor);
        tabelaPainel.add(wmcExibirNotaAjustada);

        wmcExibirSemAjustada.setVisible(!isExibirNotaAjustada);

        tabelaPainel.add(wmcExibirSemAjustada);

    }

    public String getMarkupIdTabelaPainel() {
        return T_RESUMO_MATRIZ;
    }

    public String getNotaCalculada() {
        return notaCalculada;
    }

    public PerfilRisco getPerfilRisco() {
        return perfilRisco;
    }


    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public boolean isExibirNotaRefinada() {
        return isExibirNotaRefinada;
    }

    public void setExibirNotaRefinada(boolean isExibirNotaRefinada) {
        this.isExibirNotaRefinada = isExibirNotaRefinada;
    }

}
