package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.slide;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.src.dominio.ConclusaoES;
import br.gov.bcb.sisaps.src.dominio.GrauPreocupacaoES;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.src.mediator.ConclusaoESMediator;
import br.gov.bcb.sisaps.src.mediator.GrauPreocupacaoESMediator;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;
import br.gov.bcb.sisaps.util.Util;

public class Slide25 extends Slide {
    private static final String ID_CONCLUSAO = "idConclusao";
    private static final String ID_GRAU_PREOCUPACAO = "idGrauPreocupacao";
    @SpringBean
    private ConclusaoESMediator conclusaoESMediator;

    // Construtor
    public Slide25(ApresentacaoVO apresentacaoVO, PerfilRisco perfilRisco, PerfilAcessoEnum perfilMenu) {
        super(SecaoApresentacaoEnum.NOTA_FINAL_INSTITUICAO, apresentacaoVO.getNomeEs());

        // Adiciona os componentes.
        montarComponentes(apresentacaoVO, perfilRisco, perfilMenu);
    }

    // Monta os componentes do painel.
    @Transactional
    private void montarComponentes(ApresentacaoVO apresentacaoVO, final PerfilRisco perfilRisco, 
            PerfilAcessoEnum perfilMenu) {
        // Declarações
        final GrauPreocupacaoES grauPreocupacaoES;
        ConclusaoES conclusaoES;

        // Inicializações
        grauPreocupacaoES =
                GrauPreocupacaoESMediator.get().getGrauPreocupacaoESPorPerfil(perfilRisco, false, perfilMenu);

        // Nota quantitativa.
        boolean possuiCorecQuanti = !Util.isNuloOuVazio(apresentacaoVO.getNotaQuantitativa()[3]);
        add(new Label("idNotaQuantitativa0", apresentacaoVO.getNotaQuantitativa()[0])
                .setVisibilityAllowed(!possuiCorecQuanti));
        add(new Label("idNotaQuantitativa1", apresentacaoVO.getNotaQuantitativa()[1])
                .setVisibilityAllowed(!possuiCorecQuanti));
        add(new Label("idNotaQuantitativa2", apresentacaoVO.getNotaQuantitativa()[3])
                .setVisibilityAllowed(possuiCorecQuanti));

        // Nota qualitativa.
        boolean possuiCorec = !Util.isNuloOuVazio(apresentacaoVO.getNotaQualitativa()[3]);
        add(new Label("idNotaQualitativa0", apresentacaoVO.getNotaQualitativa()[0]).setVisibilityAllowed(!possuiCorec));
        add(new Label("idNotaQualitativa1", apresentacaoVO.getNotaQualitativa()[1]).setVisibilityAllowed(!possuiCorec));
        add(new Label("idNotaQualitativa2", apresentacaoVO.getNotaQualitativa()[3]).setVisibilityAllowed(possuiCorec));
        
        // Nota final
        String notaFinal = GrauPreocupacaoESMediator.get().getNotaFinal(perfilRisco, perfilMenu,
                perfilRisco.getCiclo().getMetodologia());
        
        
        String notaAnef = GrauPreocupacaoESMediator.get().getNotaAEF(perfilRisco, perfilMenu);
        String notaMatriz = GrauPreocupacaoESMediator.get().getNotaMatrizFinal(perfilRisco, perfilMenu);
        String notaCalculada =
                GrauPreocupacaoESMediator.get().getNotaFinalCalculada(grauPreocupacaoES, notaAnef, notaMatriz, perfilRisco.getCiclo());
         notaFinal = notaFinal + " (" + notaCalculada+ ")";
        
        add(new Label(ID_GRAU_PREOCUPACAO, notaFinal) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(GrauPreocupacaoESMediator.get().isNotaFinal(grauPreocupacaoES));
            }
        });

        // Conclusão
        conclusaoES = conclusaoESMediator.buscarPorPerfilRisco(perfilRisco.getPk());
        if (conclusaoES != null && conclusaoES.getDocumento() != null) {
            add(new Label(ID_CONCLUSAO, conclusaoES.getDocumento().getJustificativa()).setEscapeModelStrings(false));
        } else {
            add(new Label(ID_CONCLUSAO, ""));
        }
    }

}
