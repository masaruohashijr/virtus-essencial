package br.gov.bcb.sisaps.web.page.dominio.gerenciaes;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.WebMarkupContainer;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.GrauPreocupacaoES;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.AjusteCorecMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.GrauPreocupacaoESMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.LabelLinhas;
import br.gov.bcb.sisaps.web.page.componentes.grupo.ControleExpansivel;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;

public class PainelNotaFinalVigente extends PainelSisAps {
	
    private static final String ATUALIZADO_POR = "Atualizado por ";
    private static final String PERCENTUAL_ANEF = "30%";
    private static final String PERCENTUAL_MATRIZ = "70%";
    private static final String PERCENTUAL_50 = "50%";
    private static final String PONTO = ".";
    private GrauPreocupacaoES grauPreocupacaoESVigente;
    private final PerfilRisco perfilRisco;
    private final Ciclo ciclo;
    private final Boolean mostrarCabecalho;

    public PainelNotaFinalVigente(String id, Integer cicloId, PerfilRisco perfilRisco) {
        super(id);
        this.perfilRisco = perfilRisco;
        this.mostrarCabecalho = true;
        this.ciclo = CicloMediator.get().buscarCicloPorPK(cicloId);
    }
    
    public PainelNotaFinalVigente(String id, Integer cicloId, PerfilRisco perfilRisco,Boolean mostrarCabecalho) {
        super(id);
        this.perfilRisco = perfilRisco;
        this.mostrarCabecalho = mostrarCabecalho;
        this.ciclo = CicloMediator.get().buscarCicloPorPK(cicloId);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        grauPreocupacaoESVigente = GrauPreocupacaoESMediator.get().buscarPorPerfilRisco(perfilRisco.getPk());
        addGrauPreocupacaoVigente();
        setVisibilityAllowed(grauPreocupacaoESVigente == null
                || grauPreocupacaoESVigente.getParametroGrauPreocupacao() == null);
    }

    @SuppressWarnings("serial")
    private void addGrauPreocupacaoVigente() {

		WebMarkupContainer wmcMostrarCabecalho = new WebMarkupContainer(
				"wmcMostrarCabecalho")  {
            @Override
            public boolean isVisible() {
                return mostrarCabecalho;
            }
        };
		wmcMostrarCabecalho.setMarkupId(wmcMostrarCabecalho.getId());

		addOrReplace(wmcMostrarCabecalho);
        String pesoAnef = "";
        String pesoMatriz = "";
        String notaFinalAjustada = "";
        String justificativaNotaFinalAjustada = "";
        String dataAtualizacao = "";
        String notaAnef = "";
        String notaMatriz = "";
        String notaFinal = "";
        String notaRefinada = "";

        notaAnef = GrauPreocupacaoESMediator.get().getNotaAEF(perfilRisco, getPerfilPorPagina());
        notaMatriz = GrauPreocupacaoESMediator.get().getNotaMatrizFinal(perfilRisco, getPerfilPorPagina());
        notaFinal =
                GrauPreocupacaoESMediator.get().getNotaFinalCalculada(grauPreocupacaoESVigente, notaAnef, notaMatriz, perfilRisco.getCiclo());
        notaRefinada = GrauPreocupacaoESMediator.get().getNotaFinalRefinada(ciclo.getMetodologia(), notaFinal);
        String ajusteCorec = AjusteCorecMediator.get().notaAjustadaCorecES(perfilRisco, perfilRisco.getCiclo(), getPerfilPorPagina());
        
        if (ajusteCorec != null) {
            notaFinalAjustada = ajusteCorec + " (Corec)";
        }

        if (grauPreocupacaoESVigente != null) {
            // Peso Anef
            pesoAnef = grauPreocupacaoESVigente.getPercentualAnef();

            // Peso Matriz
            pesoMatriz = grauPreocupacaoESVigente.getPercentualMatriz();

            // Nota final
            dataAtualizacao =
                    ATUALIZADO_POR + grauPreocupacaoESVigente.getNomeOperadorAtualizacaoDataHora() + PONTO;
            
            if (StringUtils.isBlank(notaFinalAjustada)) {
                notaFinalAjustada = grauPreocupacaoESVigente.getDescricaoNotaFinal();
                justificativaNotaFinalAjustada = grauPreocupacaoESVigente.getJustificativa();
            }
        } else if (GrauPreocupacaoESMediator.get().cicloMaisTresAnos(ciclo)) {
            pesoAnef = PERCENTUAL_50;
            pesoMatriz = PERCENTUAL_50;
        } else {
            pesoAnef = PERCENTUAL_ANEF;
            pesoMatriz = PERCENTUAL_MATRIZ;
        }
        
        WebMarkupContainer linhaNotaAjustada = new WebMarkupContainer("linhaNotaAjustada");
        linhaNotaAjustada.setMarkupId(linhaNotaAjustada.getId());
        LabelLinhas labelNotaAjustada = new LabelLinhas("idNotaFinalAjustada", notaFinalAjustada);
        LabelLinhas labelJustificativaNotaAjustada = new LabelLinhas("idJustificativaNotaFinalAjustada", justificativaNotaFinalAjustada);
        linhaNotaAjustada.addOrReplace(labelNotaAjustada);
        linhaNotaAjustada.addOrReplace(labelJustificativaNotaAjustada.setEscapeModelStrings(false));
        linhaNotaAjustada.setVisibilityAllowed(StringUtils.isNotBlank(notaFinalAjustada));

        addOrReplace(new LabelLinhas("idPesoAnef", pesoAnef));
        addOrReplace(new LabelLinhas("idPesoMatriz", pesoMatriz));
        addOrReplace(new LabelLinhas("idNotaMatriz", notaMatriz));
        addOrReplace(new LabelLinhas("idNotaAnef", notaAnef));
        addOrReplace(new LabelLinhas("idNotaFinalCalculada", notaFinal));
        addOrReplace(new LabelLinhas("idNotaFinalRefinada", notaRefinada));

        
        WebMarkupContainer wmcMostrarAtualizacao = new WebMarkupContainer(
				"wmcMostrarAtualizacao")  {
            @Override
            public boolean isVisible() {
                return mostrarCabecalho;
            }
        };
        wmcMostrarAtualizacao.setMarkupId(wmcMostrarAtualizacao.getId());
        wmcMostrarAtualizacao.addOrReplace(new LabelLinhas("idDataAtualizacao", dataAtualizacao));
        addOrReplace(wmcMostrarAtualizacao);
        
        addOrReplace(linhaNotaAjustada);
        
    }

}
