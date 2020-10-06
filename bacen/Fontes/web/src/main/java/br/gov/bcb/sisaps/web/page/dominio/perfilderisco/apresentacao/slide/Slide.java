package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.slide;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;

// Slide padr�o da apresenta��o.
public abstract class Slide extends Panel {

    // A se��o do slide.
    private final SecaoApresentacaoEnum secao;

    private int indicePagina;
    private int totalPaginas;
    private String nomeEs;

    // Construtor
    public Slide(SecaoApresentacaoEnum secao, String nomeEs) {
        this(secao, true, nomeEs);
    }

    // Construtor
    public Slide(SecaoApresentacaoEnum secao, boolean comTitulo, String nomeEs) {
        super("idPainelSlide");

        // Inicializa��es
        this.secao = secao;
        this.nomeEs = nomeEs;
        montarComponentes(comTitulo);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        addDadosGerais(nomeEs);
    }

    // Monta os componentes do painel.
    private void montarComponentes(boolean comTitulo) {
        // Declara��es
        HiddenField<String> hidIndiceTitulo;

        // Adiciona o valor para o �ndice.
        hidIndiceTitulo = new HiddenField<String>("idHiddenIndice", Model.of(getTitulo()));
        add(hidIndiceTitulo);

        // Verifica se coloca t�tulo no slide.
        if (comTitulo) {
            hidIndiceTitulo.add(new AttributeAppender("class", Model.of("titulo"), " "));
        }
    }

    // Retorna o t�tulo do slide.
    protected String getTitulo() {
        return secao.getDescricao();
    }

    // Adiciona dados gerais da apresenta��o.
    private void addDadosGerais(String nomeEs) {
        // Logo do BCB.
        addOrReplace(new Image("idImagemLogo", "img/logo_bc.png").setOutputMarkupId(true));

        addOrReplace(new Label("spanTituloImpre", getTitulo()).setOutputMarkupId(true));
        // Nome do conglomerado.
        addOrReplace(new Label("idNomeConglomeradoFoot", nomeEs).setOutputMarkupId(true));

        //        addOrReplace(new Label("spanPaginaImpre", "SRC " + getIndicePagina() + "/" + getTotalPaginas())
        //        .setOutputMarkupId(true));
        // TODO VERSAO TEMPORARIA
        addOrReplace(new Label("spanPaginaImpre", "SRC TOTAL " + getTotalPaginas()).setOutputMarkupId(true));

    }

    public int getIndicePagina() {
        return indicePagina;
    }

    public void setIndicePagina(int indicePagina) {
        this.indicePagina = indicePagina;
    }

    public int getTotalPaginas() {
        return totalPaginas;
    }

    public void setTotalPaginas(int totalPaginas) {
        this.totalPaginas = totalPaginas;
    }
}
