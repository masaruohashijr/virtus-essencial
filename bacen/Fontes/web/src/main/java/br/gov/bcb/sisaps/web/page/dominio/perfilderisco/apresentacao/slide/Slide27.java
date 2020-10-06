package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.slide;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.src.dominio.PerspectivaES;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;
import br.gov.bcb.sisaps.util.Constantes;

public class Slide27 extends Slide {
    
    private static final String ID_PERSPECTIVA_ANTERIOR = "idPerspectivaAnterior";
    private static final String ID_CONTEUDO0 = "idConteudo0";
    private static final String ID_TITULO0 = "idTitulo0";
    private static final String ID_CONTEUDO1 = "idConteudo1";
    private static final String ID_TITULO1 = "idTitulo1";

    // Construtor
    public Slide27(ApresentacaoVO apresentacaoVO) {
        super(SecaoApresentacaoEnum.PERSPECTIVA_INSTITUICAO, apresentacaoVO.getNomeEs());
        
        // Adiciona os componentes.
        montarComponentes(apresentacaoVO);
    }
    
    // Monta os componentes do painel.
    @Transactional
    private void montarComponentes(ApresentacaoVO apresentacaoVO) {
        // Declarações
        PerspectivaES perspectivaES;
        Component component;
        Label lblTitulo;
        Label lblConteudo;
        
        // Inicializações
        perspectivaES = apresentacaoVO.getDadosCicloVO().getPerspectivaES();
        
        // Perspectiva atual.
        if (perspectivaES != null) {
            String nome = perspectivaES.getParametroPerspectiva().getNome();
            lblTitulo = new Label(ID_TITULO1, perspectivaES.getPk() == null ? 
                    nome + Constantes.COREC : nome);
            lblConteudo = new Label(ID_CONTEUDO1, perspectivaES.getDescricao());
        } else {
            lblTitulo = new Label(ID_TITULO1, "");
            lblConteudo = new Label(ID_CONTEUDO1, "");
        }
        lblConteudo.setEscapeModelStrings(false);
        add(lblTitulo);
        add(lblConteudo);
        
        // Valida dados do ciclo anterior.
        if (apresentacaoVO.getDadosCicloVOAnteriorVO() != null) {
            // Perspectiva anterior.
            perspectivaES = apresentacaoVO.getDadosCicloVOAnteriorVO().getPerspectivaES();
            
            if (perspectivaES != null) {
                String nome = perspectivaES.getParametroPerspectiva().getNome();
                lblTitulo = new Label(ID_TITULO0, perspectivaES.getPk() == null ? 
                        nome + Constantes.COREC : nome);
                lblConteudo = new Label(ID_CONTEUDO0, perspectivaES.getDescricao());
            } else {
                lblTitulo = new Label(ID_TITULO0, "");
                lblConteudo = new Label(ID_CONTEUDO0, "");
            }
            lblConteudo.setEscapeModelStrings(false);
            component = new WebMarkupContainer(ID_PERSPECTIVA_ANTERIOR)
                    .add(lblTitulo)
                    .add(lblConteudo);
        } else {
            component = new Label(ID_PERSPECTIVA_ANTERIOR).setVisible(false);
        }
        add(component);
    }

}
