package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.painel;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.ConclusaoES;
import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.GrauPreocupacaoES;
import br.gov.bcb.sisaps.src.dominio.PerfilAtuacaoES;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.PerspectivaES;
import br.gov.bcb.sisaps.src.dominio.SituacaoES;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.GrauPreocupacaoESMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.PerspectivaESMediator;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.grupo.GrupoExpansivelCampoValor;
import br.gov.bcb.sisaps.web.page.dominio.gerenciaes.PainelNotaFinalVigente;
import br.gov.bcb.sisaps.web.page.dominio.gerenciaes.TabelaAnexoPerfilAtuacaoConclusao;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.PerfilDeRiscoPageResumido;

@SuppressWarnings("serial")
public class PainelDetalhesES extends PainelSisAps {
	
    private static final String COREC = " (Corec)";

    @SpringBean
    private PerfilRiscoMediator perfilRiscoMediator;

    private PerfilRisco perfilRisco;

    public PainelDetalhesES(String id, PerfilRisco perfilRisco) {
        super(id);
        setMarkupId(id);
        this.perfilRisco = perfilRisco;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        addComponents(perfilRiscoMediator.getEntidadeSupervisionavelPerfilRisco(perfilRisco));
    }

    private void addComponents(EntidadeSupervisionavel entidadeSupervisionavel) {
        Label labelPrioridade =
                new Label("idPrioridade", entidadeSupervisionavel == null
                        || entidadeSupervisionavel.getPrioridade() == null ? "" : entidadeSupervisionavel
                        .getPrioridade().getDescricao());
        labelPrioridade.setVisibilityAllowed(!(getPaginaAtual() instanceof PerfilDeRiscoPageResumido));
        addOrReplace(labelPrioridade);
        addNotaFinal();
        addOrReplace(new Label("idPorte",
                entidadeSupervisionavel == null || entidadeSupervisionavel.getPorte() == null ? ""
                        : entidadeSupervisionavel.getPorte()));
        addOrReplace(new Label("idSegmento", entidadeSupervisionavel == null
                || entidadeSupervisionavel.getSegmento() == null ? "" : entidadeSupervisionavel.getSegmento()));
        addPerfilAtuacao();
        addConclusao();
        addPerspectiva();
        addSituacao();
    }

    private void addNotaFinal() {
        GrauPreocupacaoES grauPreocupacaoES = GrauPreocupacaoESMediator.get().buscarPorPerfilRisco(perfilRisco.getPk());
        String justificativa = grauPreocupacaoES != null ? grauPreocupacaoES.getJustificativa() : "";
        String descricaoNota =
                GrauPreocupacaoESMediator.get().getNotaFinal(perfilRisco, getPerfilPorPagina(),
                        perfilRisco.getCiclo().getMetodologia());

       
        
        Label labelGrauPreocupacao = new Label("idGrauPreocupacao", descricaoNota.contains(COREC) ? "" : justificativa);

        labelGrauPreocupacao.setEscapeModelStrings(false);
        
        String nomeCampo =
                GrauPreocupacaoESMediator.get().isNotaFinal(grauPreocupacaoES) ? "Nota final" : "Grau de preocupação";
        
        WebMarkupContainer wmcNotaFinalVigente = new WebMarkupContainer("wmcNotaFinalVigente");
        wmcNotaFinalVigente.setMarkupId(wmcNotaFinalVigente.getId());
        PainelNotaFinalVigente  painelNotaFinalVigente =
                new PainelNotaFinalVigente("idPainelNotaFinalVigente", perfilRisco.getCiclo().getPk(), perfilRisco, false);
        painelNotaFinalVigente.setMarkupId(painelNotaFinalVigente.getId());
        wmcNotaFinalVigente.addOrReplace(painelNotaFinalVigente);
        addOrReplace(wmcNotaFinalVigente);
        GrupoExpansivelCampoValor grupo =
                new GrupoExpansivelCampoValor("grupoExpansivelNotaFinal", nomeCampo, descricaoNota, false,
                        StringUtils.isNotBlank(justificativa) ||  GrauPreocupacaoESMediator.get().possuiNotaVigente(descricaoNota), new Component[] { wmcNotaFinalVigente,painelNotaFinalVigente} ) {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setVisibilityAllowed(!(getPaginaAtual() instanceof PerfilDeRiscoPageResumido));
                    }
                };

        addOrReplace(grupo);
        
    }

    private void addPerfilAtuacao() {
        final PerfilAtuacaoES perfilAtuacaoES = PerfilRiscoMediator.get().getPerfilAtuacaoESPerfilRisco(perfilRisco);
        WebMarkupContainer linhaPerfilAtuacao = new WebMarkupContainer("linhaPerfilAtuacao");
        linhaPerfilAtuacao.setMarkupId(linhaPerfilAtuacao.getId());
        addOrReplace(linhaPerfilAtuacao);
        Documento documento = null;
        if (perfilAtuacaoES != null) {
            documento = perfilAtuacaoES.getDocumento();
        }
        Label justificativaPerfilAtuacao =
                new Label("idJustificativaPerfilAtuacao",
                        documento == null || documento.getJustificativa() == null ? "" : perfilAtuacaoES.getDocumento()
                                .getJustificativa());
        justificativaPerfilAtuacao.setEscapeModelStrings(false);
        linhaPerfilAtuacao.addOrReplace(justificativaPerfilAtuacao);

        TabelaAnexoPerfilAtuacaoConclusao tabelaAnexoPerfilAtuacao =
                new TabelaAnexoPerfilAtuacaoConclusao("idTabelaAnexoDocumentoPerfilAtuacao", perfilAtuacaoES,
                        documento, perfilRisco.getCiclo(), false, true, null) {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setVisibilityAllowed(perfilAtuacaoES != null);
                    }
                };
        linhaPerfilAtuacao.addOrReplace(tabelaAnexoPerfilAtuacao);

        GrupoExpansivelCampoValor grupo =
                new GrupoExpansivelCampoValor("grupoExpansivelPerfilAtuacao", "Perfil de atuação", null, false,
                        linhaPerfilAtuacao);
        addOrReplace(grupo);
    }

    private void addConclusao() {
        final ConclusaoES conclusaoES = PerfilRiscoMediator.get().getConclusaoESPerfilRisco(perfilRisco);
        WebMarkupContainer linhaConclusao = new WebMarkupContainer("linhaConclusao");
        linhaConclusao.setMarkupId(linhaConclusao.getId());
        addOrReplace(linhaConclusao);

        Documento documento = null;
        if (conclusaoES != null) {
            documento = conclusaoES.getDocumento();
        }
        Label justificativaConclusao =
                new Label("idJustificativaConclusao", documento == null || documento.getJustificativa() == null ? ""
                        : conclusaoES.getDocumento().getJustificativa());
        justificativaConclusao.setEscapeModelStrings(false);
        linhaConclusao.addOrReplace(justificativaConclusao);

        TabelaAnexoPerfilAtuacaoConclusao tabelaAnexoConclusao =
                new TabelaAnexoPerfilAtuacaoConclusao("idTabelaAnexoDocumentoConclusao", conclusaoES, documento,
                        perfilRisco.getCiclo(), false, false, null) {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setVisibilityAllowed(conclusaoES != null);
                    }
                };
        linhaConclusao.addOrReplace(tabelaAnexoConclusao);

        GrupoExpansivelCampoValor grupo =
                new GrupoExpansivelCampoValor("grupoExpansivelConclusao", "Conclusão", null, false, linhaConclusao);
        addOrReplace(grupo);
    }

    private void addPerspectiva() {
        final PerspectivaES perspectivaES =
                PerspectivaESMediator.get().getUltimaPerspectiva(perfilRisco, false,
                        getPaginaAtual().getPerfilPorPagina());

        WebMarkupContainer linhaPerspectiva = new WebMarkupContainer("linhaPerspectiva");
        linhaPerspectiva.setMarkupId(linhaPerspectiva.getId());
        addOrReplace(linhaPerspectiva);

        Label justificativaPerspectiva =
                new Label("idJustificativaPerspectiva",
                        perspectivaES == null || perspectivaES.getDescricao() == null ? ""
                                : perspectivaES.getDescricao());
        justificativaPerspectiva.setEscapeModelStrings(false);
        linhaPerspectiva.addOrReplace(justificativaPerspectiva);

        String valorPerspectiva = "";

        if (perspectivaES != null && perspectivaES.getPk() == null) {
        	String descricaoCorec = PerfilAcessoEnum.CONSULTA_RESUMO_NAO_BLOQUEADOS.equals(getPaginaAtual().getPerfilPorPagina()) ? "" : COREC;
            valorPerspectiva +=
                    perspectivaES.getParametroPerspectiva() == null ? "" : perspectivaES.getParametroPerspectiva()
                            .getNome() + descricaoCorec;
        } else {
            valorPerspectiva =
                    perspectivaES == null || perspectivaES.getParametroPerspectiva() == null ? "" : perspectivaES
                            .getParametroPerspectiva().getNome();
        }

        GrupoExpansivelCampoValor grupo =
                new GrupoExpansivelCampoValor("grupoExpansivelPerspectiva", "Perspectiva", valorPerspectiva, false,
                        linhaPerspectiva) {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        getControle().setVisibilityAllowed(!(perspectivaES != null && perspectivaES.getPk() == null));
                    }
                };

        addOrReplace(grupo);
    }

    private void addSituacao() {
        SituacaoES situacaoES = PerfilRiscoMediator.get().getSituacaoESPerfilRisco(perfilRisco);
        WebMarkupContainer linhaSituacao = new WebMarkupContainer("linhaSituacao");
        linhaSituacao.setMarkupId(linhaSituacao.getId());
        addOrReplace(linhaSituacao);
        Label justificativaSituacao =
                new Label("idJustificativaSituacao", situacaoES == null || situacaoES.getDescricao() == null ? ""
                        : situacaoES.getDescricao());
        justificativaSituacao.setEscapeModelStrings(false);
        linhaSituacao.addOrReplace(justificativaSituacao);
        String valorSituacao =
                situacaoES == null || situacaoES.getParametroSituacao() == null ? "" : situacaoES
                        .getParametroSituacao().getNome();
        GrupoExpansivelCampoValor grupo =
                new GrupoExpansivelCampoValor("grupoExpansivelSituacao", "Situação", valorSituacao, false,
                        linhaSituacao);
        addOrReplace(grupo);
    }

    public void setPerfilRisco(PerfilRisco perfilRisco) {
        this.perfilRisco = perfilRisco;
    }
}
