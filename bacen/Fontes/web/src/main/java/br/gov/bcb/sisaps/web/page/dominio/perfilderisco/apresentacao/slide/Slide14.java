package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.slide;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.PesoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.SinteseDeRiscoAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.PesoAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.SinteseDeRiscoAQTMediator;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;

public class Slide14 extends Slide {

    @SpringBean
    private PerfilRiscoMediator perfilRiscoMediator;

    @SpringBean
    private SinteseDeRiscoAQTMediator sinteseDeRiscoAQTMediator;

    @SpringBean
    private PesoAQTMediator pesoAQTMediator;

    // Construtor
    public Slide14(ApresentacaoVO apresentacaoVO, PerfilRisco perfilRisco, PerfilAcessoEnum perfilMenu) {
        super(SecaoApresentacaoEnum.SINTESE_NOTA_QUANTITATIVA, apresentacaoVO.getNomeEs());

        // Adiciona os componentes.
        montarComponentes(apresentacaoVO, perfilRisco, perfilMenu);
    }

    // Monta os componentes do painel.
    @Transactional
    private void montarComponentes(ApresentacaoVO apresentacaoVO, final PerfilRisco perfilRisco,
            final PerfilAcessoEnum perfilMenu) {
        // Declarações
        List<AnaliseQuantitativaAQT> aqtsVigentes;

        // Nota calculada.
        addOrReplace(new Label("idNotaCalculada", apresentacaoVO.getNotaQuantitativa()[0]));

        // Nota refinada.
        addOrReplace(new Label("idNotaRefinada", apresentacaoVO.getNotaQuantitativa()[1]));
        final String notaAjustada = apresentacaoVO.getNotaQuantitativa()[3];
        addOrReplace(new Label("idNotaAjustada", notaAjustada) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(StringUtils.isNotBlank(notaAjustada));
            }
        });

        // Componentes
        aqtsVigentes = perfilRiscoMediator.getAnalisesQuantitativasAQTPerfilRisco(perfilRisco);
        addOrReplace(new ListView<AnaliseQuantitativaAQT>("idListaSintesesAQT", aqtsVigentes) {
            @Override
            protected void populateItem(ListItem<AnaliseQuantitativaAQT> item) {
                addComponenteAQT(item, perfilRisco, perfilMenu);
            }
        });
    }

    // Adiciona um componente AQT.
    private void addComponenteAQT(ListItem<AnaliseQuantitativaAQT> item, PerfilRisco perfilRisco,
            PerfilAcessoEnum perfilMenu) {
        // Declarações
        AnaliseQuantitativaAQT aqt;
        ParametroAQT parametroAQT;
        SinteseDeRiscoAQT sinteseDeRiscoAQT;
        PesoAQT pesoVigente;
        String nomeCampo;
        String pesoCampo;
        String nota;
        String justificativa;

        // Inicializações
        aqt = item.getModelObject();
        parametroAQT = aqt.getParametroAQT();
        sinteseDeRiscoAQT = sinteseDeRiscoAQTMediator.getUltimaSinteseVigente(parametroAQT, perfilRisco.getCiclo());

        // Nome do campo.
        nomeCampo = parametroAQT.getDescricao();

        // Peso do campo.
        pesoVigente = pesoAQTMediator.obterPesoVigente(parametroAQT, aqt.getCiclo());
        pesoCampo = pesoVigente.getValor().toString();

        // Nota do AQT.
        String notaAnef =
                AnaliseQuantitativaAQTMediator.get().notaAnef(aqt, perfilRisco.getCiclo(), perfilMenu, false, 
                        perfilRisco);
        if (!aqt.getNotaSupervisorDescricaoValor().equals(notaAnef)) {
            nota = notaAnef + " (Corec)";
        } else {
            nota = notaAnef;
        }

        // Justificativa.
        if (sinteseDeRiscoAQT == null || sinteseDeRiscoAQT.getJustificativa() == null) {
            justificativa = "";
        } else {
            justificativa = " " + sinteseDeRiscoAQT.getJustificativa();
        }

        // Adiciona os componentes.
        item.add(new Label("idNome", nomeCampo));
        item.add(new Label("idPeso", pesoCampo));
        item.add(new Label("idNota", nota));
        item.add(new Label("idJustificativa", justificativa).setEscapeModelStrings(false));
    }

}
