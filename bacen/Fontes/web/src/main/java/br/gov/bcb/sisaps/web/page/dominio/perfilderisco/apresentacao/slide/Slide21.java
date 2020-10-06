package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.slide;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.PainelDadosMatrizPercentual;

public class Slide21 extends Slide {

    // Construtor
    public Slide21(ApresentacaoVO apresentacaoVO, PerfilRisco perfilRisco) {
        super(SecaoApresentacaoEnum.NOTAS_ARCS, apresentacaoVO.getNomeEs());

        // Adiciona os componentes.
        montarComponentes(apresentacaoVO, perfilRisco);
    }

    // Monta os componentes do painel.
    @Transactional
    private void montarComponentes(ApresentacaoVO apresentacaoVO, PerfilRisco perfilRisco) {

        // Declarações
        PainelDadosMatrizPercentual painel;
        Label label;

        // Inicializações
        painel =
                new PainelDadosMatrizPercentual("matrizVigentePanel", perfilRisco, false,
                        "Matriz de riscos e controles", true, false);

        // Adiciona o painel.
        add(painel);

        // Nota calculada.
        add(new Label("idNotaCalculada", apresentacaoVO.getNotaQualitativa()[0]));

        // Nota refinada.
        label = new Label("idNotaRefinada", apresentacaoVO.getNotaQualitativa()[1]);
        label.add(new AttributeAppender("bgcolor", apresentacaoVO.getNotaQualitativa()[2]));
        add(label);
        final String notaAjustada = apresentacaoVO.getNotaQualitativa()[3];

        addOrReplace(new Label("idNotaAjustada", notaAjustada) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(StringUtils.isNotBlank(notaAjustada));
            }
        });

        addOrReplace(new Label("idJustificativa", apresentacaoVO.getNotaQualitativa()[4]).setEscapeModelStrings(false));

    }

}
