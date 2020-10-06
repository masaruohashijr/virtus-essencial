package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.ItemElemento;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.web.page.componentes.grupo.GrupoExpansivel;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.TabelaAnexoDocumento;

public class PainelDocumentoDetalheARC extends Panel {

    private static final String NAO_AVALIADO_POR = "Não avaliado por ";
    private static final String PREENCHIDO_POR = "Preenchido por ";
    private static final String DOCUMENTO_JUSTIFICATIVA_DETALHE = "documento.justificativaDetalhe";
    private static final String SEM_ALTERACOES = "Sem alterações";
    private final Ciclo ciclo;
    private final ItemElemento itemElementoARC;
    private final ItemElemento itemElementoARCVigente;
    private final AvaliacaoRiscoControle arc;
    private final AvaliacaoRiscoControle arcVigente;
    private final boolean exibirColunaInspetor;
    private final boolean exibirColunaVigente;
    private final boolean exibirColunaFinal;
    private WebMarkupContainer exibirNovaJustificativa;

    public PainelDocumentoDetalheARC(String id, Ciclo ciclo, ItemElemento itemElementoARC, 
            ItemElemento itemElementoARCVigente, AvaliacaoRiscoControle arc, boolean exibirColunaInspetor, 
            boolean exibirColunaVigente) {
        super(id);
        this.ciclo = ciclo;
        this.itemElementoARC = itemElementoARC;
        this.arc = arc;
        this.exibirColunaInspetor = exibirColunaInspetor;
        this.exibirColunaVigente = exibirColunaVigente;
        this.exibirColunaFinal = AvaliacaoRiscoControleMediator.get().exibirColunaFinal(arc.getEstado());
        if (AvaliacaoRiscoControleMediator.get().estadoConcluido(arc.getEstado())) {
            this.itemElementoARCVigente = itemElementoARC;
            this.arcVigente = arc;
        } else {
            this.itemElementoARCVigente = itemElementoARCVigente;
            this.arcVigente = arc.getAvaliacaoRiscoControleVigente();
        }
    }
    
    @Override
    protected void onInitialize() {
        super.onInitialize();
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
        // Nova avaliação: Exibido sempre que for exibida a coluna 'Inspetor' do grupo 'Resumo do ARC' e o estado do ARC seja diferente de 'Concluído'.
        return exibirColunaInspetor && !AvaliacaoRiscoControleMediator.get().estadoConcluido(arc.getEstado());
    }

    private void addDataOperadorItemAtual() {
        Label dataItemElemento = new Label("idDataItemElemento", obterUsuarioUltimaAtualizacao());
        exibirNovaJustificativa.addOrReplace(dataItemElemento);
    }

    private String obterUsuarioUltimaAtualizacao() {
        if (itemElementoARC.getDocumento() == null) { // Se não tiver sido preenchida:
            // Caso o ARC tenha o registro de quem concluiu a edição do ARC sem preencher os dados, 
            // exibir a mensagem 'Não avaliado por <<Nome do usuário que concluiu edição do ARC>> em <<data hora em que concluiu>>'.
            if (arc.getDataPreenchido() != null) {
                return NAO_AVALIADO_POR + Util.nomeOperador(arc.getOperadorPreenchido()) + Constantes.EM
                        + arc.getData(arc.getDataPreenchido()) + Constantes.PONTO;
            } else if (isExibirItemVigente() && itemElementoARCVigente.getDocumento() == null) {
                // Senão, caso seja exibido a 'Avaliação vigente' e ela não esteja preenchida, exibir a informação 'Sem alterações'.
                return SEM_ALTERACOES;
            }
        } else { // Se tiver sido preenchida:
            // Caso seja igual a 'Avaliação vigente' (texto, usuário e data/hora de alteração), 
            // o sistema deverá exibir a a mensagem 'Sem alterações' na 'Nova avaliação'.
            if (isAvaliacoesRascunhoEVigenteIguais()) {
                return SEM_ALTERACOES;
            } else if (AvaliacaoRiscoControleMediator.get().estadoEmEdicao(arc.getEstado())) {
                // Senão, caso o estado do ARC seja 'Em edição', exibir a mensagem 
                // 'Última alteração por <<Nome do usuário que editou avaliação do elemento>> em <<data hora da edição>>'.
                return "Última alteração por " + itemElementoARC.getDocumento().getNomeOperadorDataHora()
                        + Constantes.PONTO;
            } else if (arc.getDataPreenchido() != null) {
                // Senão, exibir a mensagem 'Preenchido por <<Nome do usuário que concluiu edição do ARC>> em <<data hora em que concluiu edição do ARC>>'.
                return PREENCHIDO_POR + Util.nomeOperador(arc.getOperadorPreenchido()) + Constantes.EM
                        + arc.getData(arc.getDataPreenchido()) + Constantes.PONTO;
            }
        }
        return Constantes.VAZIO;
    }

    private boolean isAvaliacoesRascunhoEVigenteIguais() {
        // Caso seja igual a 'Avaliação vigente' (texto, usuário e data/hora de alteração)
        if (itemElementoARCVigente == null) {
            return false;
        } else {
            String justificativaRascunho =
                    itemElementoARC.getDocumento().getJustificativa() == null ? "" : itemElementoARC.getDocumento()
                            .getJustificativa();

            String justificativaVigente =
                    itemElementoARCVigente.getDocumento() == null ? "" : itemElementoARCVigente.getDocumento()
                            .getJustificativa() == null ? "" : itemElementoARCVigente.getDocumento()
                            .getJustificativa();

            return justificativaRascunho.equals(justificativaVigente)
                    && itemElementoARC.getDataAlteracao() != null
                    && arc.getData(itemElementoARC.getDataAlteracao()).equals(
                            arc.getData(itemElementoARCVigente.getDataAlteracao()))
                    && itemElementoARC.getOperadorAlteracao().equals(itemElementoARCVigente.getOperadorAlteracao());
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
                new Label("idNovaJustificativa", new PropertyModel<String>(itemElementoARC,
                        DOCUMENTO_JUSTIFICATIVA_DETALHE));
        justificativa.setEscapeModelStrings(false);
        exibirNovaJustificativa.addOrReplace(justificativa);
    }

    private void addAnexoDocumento() {
        TabelaAnexoDocumento tabelaAnexoDocumentoAQT =
                new TabelaAnexoDocumento("idTabelaAnexoDocumento", itemElementoARC, ciclo, false) {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setVisibilityAllowed(isExibirTabelaAnexos());
                    }
                };
        exibirNovaJustificativa.addOrReplace(tabelaAnexoDocumentoAQT);
    }

    private boolean isExibirTabelaAnexos() {
        return itemElementoARC.getDocumento() != null
                && CollectionUtils.isNotEmpty(itemElementoARC.getDocumento().getAnexosItemElemento());
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
        // Exibido sempre que for exibida a coluna 'Final' ou 'Vigente' do grupo 'Resumo do ARC'. e; 
        // houver avaliação do item do elemento no ARC do perfil de risco salva ou registro de quem
        // concluiu a edição do ARC sem avaliação do item.
        boolean exibeColunaVigenteOuFinal = exibirColunaVigente || exibirColunaFinal;
        boolean existeAvaliacaoVigenteOuConclusaoEdicao =
                itemElementoARCVigente.getDocumento() != null
                        || (arcVigente != null && arcVigente.getDataPreenchido() != null);
        return exibeColunaVigenteOuFinal && existeAvaliacaoVigenteOuConclusaoEdicao;
    }

    private String obterUsuarioUltimaAtualizacaoItemVigente() {
        // Se tiver sido preenchida:
        //    Exibir a mensagem 'Preenchido por <<Nome do usuário que concluiu edição do ARC>> em <<data hora em que concluiu edição do ARC>>'.
        if (isItemVigentePreenchido()) {
            return PREENCHIDO_POR + Util.nomeOperador(arcVigente.getOperadorPreenchido()) + Constantes.EM
                    + arcVigente.getData(arcVigente.getDataPreenchido()) + Constantes.PONTO;
        } else {
            // Se não tiver sido preenchida:
            //    Caso o ARC tenha o registro de quem concluiu a edição do ARC sem preencher os dados, 
            //    exibir a mensagem 'Não avaliado por <<Nome do usuário que concluiu edição do ARC>> em <<data hora em que concluiu edição do ARC>>'.
            if (arcVigente != null && arcVigente.getDataPreenchido() != null) {
                return NAO_AVALIADO_POR + Util.nomeOperador(arcVigente.getOperadorPreenchido()) + Constantes.EM
                        + arcVigente.getData(arcVigente.getDataPreenchido()) + Constantes.PONTO;
            }
        }

        return Constantes.VAZIO;
    }

    private boolean isItemVigentePreenchido() {
        return itemElementoARCVigente != null && itemElementoARCVigente.getDocumento() != null;
    }

    private PainelAvaliacaoVigenteARC addPainelAvaliacaoVigente() {
        PropertyModel<String> modelJustificativaVigente =
                new PropertyModel<String>(itemElementoARCVigente, DOCUMENTO_JUSTIFICATIVA_DETALHE);
        Documento documento = new Documento();
        if (itemElementoARCVigente.getDocumento() != null) {
            documento = itemElementoARCVigente.getDocumento();
        }
        PainelAvaliacaoVigenteARC painelAvaliacaoVigente =
                new PainelAvaliacaoVigenteARC("idPainelJustificativaVigente", ciclo, arc, modelJustificativaVigente,
                        itemElementoARCVigente, documento, "");
        painelAvaliacaoVigente.setMarkupId(painelAvaliacaoVigente.getId() + itemElementoARC.getPk());
        addOrReplace(painelAvaliacaoVigente);
        return painelAvaliacaoVigente;
    }

    private void addPainelEGrupoExpansivel() {
        PainelAvaliacaoVigenteARC painelAvaliacaoVigente = addPainelAvaliacaoVigente();

        GrupoExpansivel grupoExpansivelAvaliacaoVigente =
                new GrupoExpansivel("grupoExpansivelAvaliacao", getTituloAvaliacaoVigente(), isExpandidoInicialmente(),
                        painelAvaliacaoVigente) {
                    @Override
                    public String getMarkupIdControle() {
                        return "idGrupo_" + itemElementoARC.getParametroItemElemento().getNome();
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
        // Caso o estado do ARC seja 'Concluído', o nome do campo deve ser 'Avaliação'. Caso contrário, será 'Avaliação vigente'.
        if (AvaliacaoRiscoControleMediator.get().estadoConcluido(arc.getEstado())) {
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
