package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ItemElementoAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.web.page.componentes.grupo.GrupoExpansivel;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt.PainelAvaliacaoVigenteAnef;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt.TabelaAnexoDocumentoAQT;

public class PainelDocumentoDetalheAnefNovo extends Panel {
    private static final String NAO_AVALIADO_POR = "Não avaliado por ";
    private static final String PREENCHIDO_POR = "Preenchido por ";
    private static final String DOCUMENTO_JUSTIFICATIVA_DETALHE = "documento.justificativaDetalhe";
    private static final String SEM_ALTERACOES = "Sem alterações";
    private final ItemElementoAQT itemElementoANEF;
    private final ItemElementoAQT itemElementoANEFVigente;
    private final AnaliseQuantitativaAQT anef;
    private final boolean exibirColunaInspetor;
    private final boolean exibirColunaVigente;
    private final boolean exibirColunaFinal;
    private final AnaliseQuantitativaAQT anefVigente;
    private WebMarkupContainer exibirNovaJustificativa;

    public PainelDocumentoDetalheAnefNovo(String id, ItemElementoAQT itemElementoANEF,
            ItemElementoAQT itemElementoANEFVigente, AnaliseQuantitativaAQT anef, boolean exibirColunaInspetor,
            boolean exibirColunaVigente) {
        super(id);
        this.itemElementoANEF = itemElementoANEF;
        this.itemElementoANEFVigente = itemElementoANEFVigente;
        this.anef = anef;
        this.exibirColunaInspetor = exibirColunaInspetor;
        this.exibirColunaVigente = exibirColunaVigente;
        this.exibirColunaFinal = AnaliseQuantitativaAQTMediator.get().exibirColunaFinal(anef.getEstado());
        anefVigente = AnaliseQuantitativaAQTMediator.get().obterAnefVigente(anef);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        buildComponentes();
    }

    private void buildComponentes() {
        addDadosRascunho();
        addDadosVigentes();
    }

    private void addDadosRascunho() {
        addExibirNovaJustificativa();
        addDataOperadorItemAtual();
        addTitulo();
        addJustificativa();
        addAnexoDocumento();
    }

    private void addDadosVigentes() {
        addDataOperadorItemVigente();
        addPainelEGrupoExpansivel();
    }

    private void addExibirNovaJustificativa() {
        exibirNovaJustificativa = new WebMarkupContainer("exibirNovaJustificativa") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(isExibirNovaJustificativa());
            }
        };
        addOrReplace(exibirNovaJustificativa);
    }

    private boolean isExibirNovaJustificativa() {
        // Nova avaliação: Exibido sempre que for exibida a coluna 'Inspetor' do grupo 'Resumo do ANEF' e o estado do ANEF seja diferente de 'Concluído'.
        return exibirColunaInspetor && !AnaliseQuantitativaAQTMediator.get().estadoConcluido(anef.getEstado());
    }

    private void addDataOperadorItemAtual() {
        Label dataItemElemento = new Label("idDataItemElemento", obterUsuarioUltimaAtualizacao());
        exibirNovaJustificativa.addOrReplace(dataItemElemento);
    }

    private String obterUsuarioUltimaAtualizacao() {
        if (itemElementoANEF.getDocumento() == null) { // Se não tiver sido preenchida:
            // Caso o ANEF tenha o registro de quem concluiu a edição do ANEF sem preencher os dados, 
            // exibir a mensagem 'Não avaliado por <<Nome do usuário que concluiu edição do ANEF>> em <<data hora em que concluiu>>'.
            if (anef.getDataPreenchido() != null) {
                return NAO_AVALIADO_POR + Util.nomeOperador(anef.getOperadorPreenchido()) + Constantes.EM
                        + anef.getData(anef.getDataPreenchido()) + Constantes.PONTO;
            } else if (isExibirItemVigente() && itemElementoANEFVigente.getDocumento() == null) {
                // Senão, caso seja exibido a 'Avaliação vigente' e ela não esteja preenchida, exibir a informação 'Sem alterações'.
                return SEM_ALTERACOES;
            }
        } else { // Se tiver sido preenchida:
            // Caso seja igual a 'Avaliação vigente' (texto, usuário e data/hora de alteração), 
            // o sistema deverá exibir a a mensagem 'Sem alterações' na 'Nova avaliação'.
            if (isAvaliacoesRascunhoEVigenteIguais()) {
                return SEM_ALTERACOES;
            } else if (AnaliseQuantitativaAQTMediator.get().estadoEmEdicao(anef.getEstado())) {
                // Senão, caso o estado do ANEF seja 'Em edição', exibir a mensagem 
                // 'Última alteração por <<Nome do usuário que editou avaliação do elemento>> em <<data hora da edição>>'.
                return "Última alteração por " + itemElementoANEF.getDocumento().getNomeOperadorDataHora()
                        + Constantes.PONTO;
            } else if (anef.getDataPreenchido() != null) {
                // Senão, exibir a mensagem 'Preenchido por <<Nome do usuário que concluiu edição do ANEF>> em <<data hora em que concluiu edição do ANEF>>'.
                return PREENCHIDO_POR + Util.nomeOperador(anef.getOperadorPreenchido()) + Constantes.EM
                        + anef.getData(anef.getDataPreenchido()) + Constantes.PONTO;
            }
        }
        return Constantes.VAZIO;
    }

    private boolean isAvaliacoesRascunhoEVigenteIguais() {
        // Caso seja igual a 'Avaliação vigente' (texto, usuário e data/hora de alteração)
        if (itemElementoANEFVigente == null) {
            return false;
        } else {
            String justificativaRascunho =
                    itemElementoANEF.getDocumento().getJustificativa() == null ? "" : itemElementoANEF.getDocumento()
                            .getJustificativa();

            String justificativaVigente =
                    itemElementoANEFVigente.getDocumento() == null ? "" : itemElementoANEFVigente.getDocumento()
                            .getJustificativa() == null ? "" : itemElementoANEFVigente.getDocumento()
                            .getJustificativa();

            return justificativaRascunho.equals(justificativaVigente)
                    && itemElementoANEF.getDataAlteracao() != null
                    && anef.getData(itemElementoANEF.getDataAlteracao()).equals(
                            anef.getData(itemElementoANEFVigente.getDataAlteracao()))
                    && itemElementoANEF.getOperadorAlteracao().equals(itemElementoANEFVigente.getOperadorAlteracao());
        }
    }

    private void addTitulo() {
        Label tituloNovaJustificativa = new Label("tituloNovaJustificativa", obterTituloJustificativa());
        exibirNovaJustificativa.addOrReplace(tituloNovaJustificativa);
    }

    private String obterTituloJustificativa() {
        return "Nova avaliação";
    }

    private void addJustificativa() {
        Label justificativa =
                new Label("idNovaJustificativa", new PropertyModel<String>(itemElementoANEF,
                        DOCUMENTO_JUSTIFICATIVA_DETALHE));
        justificativa.setEscapeModelStrings(false);
        exibirNovaJustificativa.addOrReplace(justificativa);
    }

    private void addAnexoDocumento() {
        TabelaAnexoDocumentoAQT tabelaAnexoDocumentoAQT =
                new TabelaAnexoDocumentoAQT("idTabelaAnexoDocumento", itemElementoANEF, anef.getCiclo(),
                        false) {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setVisibilityAllowed(isExibirTabelaAnexos());
                    }
                };
        exibirNovaJustificativa.addOrReplace(tabelaAnexoDocumentoAQT);
    }

    private boolean isExibirTabelaAnexos() {
        return itemElementoANEF.getDocumento() != null
                && CollectionUtils.isNotEmpty(itemElementoANEF.getDocumento().getAnexosItemElemento());
    }

    private void addDataOperadorItemVigente() {
        Label dataItemElemento = new Label("idDataItem", obterUsuarioUltimaAtualizacaoItemVigente()) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(isExibirItemVigente());
            }
        };
        addOrReplace(dataItemElemento);
    }

    private boolean isExibirItemVigente() {
        // Exibido sempre que for exibida a coluna 'Final' ou 'Vigente' do grupo 'Resumo do ANEF'. e; 
        // houver avaliação do item do elemento no ANEF do perfil de risco salva ou registro de quem
        // concluiu a edição do ANEF sem avaliação do item.
        boolean exibeColunaVigenteOuFinal = exibirColunaVigente || exibirColunaFinal;
        boolean existeAvaliacaoVigenteOuConclusaoEdicao =
                itemElementoANEFVigente.getDocumento() != null
                        || (anefVigente != null && anefVigente.getDataPreenchido() != null);
        return exibeColunaVigenteOuFinal && existeAvaliacaoVigenteOuConclusaoEdicao;
    }

    private String obterUsuarioUltimaAtualizacaoItemVigente() {
        // Se tiver sido preenchida:
        //    Exibir a mensagem 'Preenchido por <<Nome do usuário que concluiu edição do ANEF>> em <<data hora em que concluiu edição do ANEF>>'.
        if (isItemVigentePreenchido()) {
            return PREENCHIDO_POR + Util.nomeOperador(anefVigente.getOperadorPreenchido()) + Constantes.EM
                    + anefVigente.getData(anefVigente.getDataPreenchido()) + Constantes.PONTO;
        } else {
            // Se não tiver sido preenchida:
            //    Caso o ANEF tenha o registro de quem concluiu a edição do ANEF sem preencher os dados, 
            //    exibir a mensagem 'Não avaliado por <<Nome do usuário que concluiu edição do ANEF>> em <<data hora em que concluiu edição do ANEF>>'.
            if (anefVigente != null && anefVigente.getDataPreenchido() != null) {
                return NAO_AVALIADO_POR + Util.nomeOperador(anefVigente.getOperadorPreenchido()) + Constantes.EM
                        + anefVigente.getData(anefVigente.getDataPreenchido()) + Constantes.PONTO;
            }
        }

        return Constantes.VAZIO;
    }

    private boolean isItemVigentePreenchido() {
        return itemElementoANEFVigente != null && itemElementoANEFVigente.getDocumento() != null;
    }

    private PainelAvaliacaoVigenteAnef addPainelAvaliacaoVigente() {
        PropertyModel<String> modelJustificativaVigente =
                new PropertyModel<String>(itemElementoANEFVigente, DOCUMENTO_JUSTIFICATIVA_DETALHE);
        Documento documento = new Documento();
        if (itemElementoANEFVigente.getDocumento() != null) {
            documento = itemElementoANEFVigente.getDocumento();
        }
        PainelAvaliacaoVigenteAnef painelAvaliacaoVigente =
                new PainelAvaliacaoVigenteAnef("idPainelJustificativaVigente", anef, modelJustificativaVigente,
                        itemElementoANEFVigente, documento, "");
        painelAvaliacaoVigente.setMarkupId(painelAvaliacaoVigente.getId() + itemElementoANEF.getPk());
        addOrReplace(painelAvaliacaoVigente);
        return painelAvaliacaoVigente;
    }

    private void addPainelEGrupoExpansivel() {
        PainelAvaliacaoVigenteAnef painelAvaliacaoVigente = addPainelAvaliacaoVigente();

        GrupoExpansivel grupoExpansivelAvaliacaoVigente =
                new GrupoExpansivel("grupoExpansivelAvaliacao", getTituloAvaliacaoVigente(), isExpandidoInicialmente(),
                        painelAvaliacaoVigente) {
                    @Override
                    public String getMarkupIdControle() {
                        return "idGrupo_" + itemElementoANEF.getParametroItemElemento().getNome();
                    }
                    @Override
                    public String getMarkupIdTitulo() {
                        return "bttExpandir";
                    }
                    @Override
                    public boolean isControleVisivel() {
                        return isGrupoExpansivelVisivel();
                    }
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setVisible(isExibirItemVigente());
                        add(new AttributeModifier("style", new Model<String>(definirStyle())));
                    }
                };

        addOrReplace(grupoExpansivelAvaliacaoVigente);
    }

    private String definirStyle() {
        String css = "";
        if (!isGrupoExpansivelVisivel()) {
            css = "border-left: 15px solid white";
        }
        return css;
    }

    private String getTituloAvaliacaoVigente() {
        // Caso o estado do ANEF seja 'Concluído', o nome do campo deve ser 'Avaliação'. Caso contrário, será 'Avaliação vigente'.
        if (AnaliseQuantitativaAQTMediator.get().estadoConcluido(anef.getEstado())) {
            return "Avaliação";
        } else {
            return "Avaliação vigente";
        }
    }

    private boolean isGrupoExpansivelVisivel() {
        return isItemVigentePreenchido() && !isExpandidoInicialmente();
    }

    private boolean isExpandidoInicialmente() {
        // Obs.: Caso sejam exibidos a 'Nova avaliação' e a 'Avaliação vigente', a avaliação vigente deve ser listada como um grupo expansível minimizado.
        return !(isExibirNovaJustificativa() && isExibirItemVigente());
    }

}
