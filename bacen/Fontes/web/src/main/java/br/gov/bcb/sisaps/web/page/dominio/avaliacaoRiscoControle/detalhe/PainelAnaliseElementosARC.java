package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.detalhe;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Elemento;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.grupo.GrupoExpansivel;

public class PainelAnaliseElementosARC extends PainelSisAps {

    private static final String ANALISADO_POR = "Analisado por ";
    private final AvaliacaoRiscoControle arc;
    private final Elemento elemento;
    private final Elemento elementoARCVigente;
    private final boolean exibirColunaSupervisor;
    private final boolean exibirColunaVigente;
    private WebMarkupContainer painelAnalise;
    private WebMarkupContainer painelAnaliseVigenteElemento;
    private final boolean exibirColunaFinal;

    public PainelAnaliseElementosARC(String id, AvaliacaoRiscoControle arc, Elemento elemento, 
            Elemento elementoARCVigente, boolean exibirColunaSupervisor, boolean exibirColunaVigente) {
        super(id);
        this.arc = arc;
        this.elemento = elemento;
        this.elementoARCVigente = elementoARCVigente;
        this.exibirColunaSupervisor = exibirColunaSupervisor;
        this.exibirColunaVigente = exibirColunaVigente;
        this.exibirColunaFinal = AvaliacaoRiscoControleMediator.get().exibirColunaFinal(arc.getEstado());
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        addComponentes();
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisibilityAllowed(isExibirAnaliseElementoRascunho() || isExibirAnaliseElementoVigente());
    }

    private void addComponentes() {
        addDadosRascunho();
        addDadosVigente();
    }

    private void addDadosRascunho() {
        addAnaliseElementoSupervisor();
        addTituloAnalise();
        addDataOperadorAnalise();
        addNovaAnalise();
    }

    private void addDadosVigente() {
        addAnaliseVigenteElementoSupervisor();
        addDataOperadorAnaliseVigente();
        addPainelEGrupoExpansivel();
    }

    private void addAnaliseElementoSupervisor() {
        painelAnalise = new WebMarkupContainer("tDadosAnaliseElementoSupervisor");
        painelAnalise.setMarkupId(painelAnalise.getId() + elemento.getPk());
        addOrReplace(painelAnalise);
    }

    private boolean isExibirAnaliseElementoRascunho() {
        // Nova análise: Exibido sempre que for exibida a coluna 'Supervisor' do grupo 'Resumo do ANEF' 
        // e o estado do ANEF seja diferente de 'Concluído' e; houver análise do elemento do ANEF salva.
        return exibirColunaSupervisor && !AvaliacaoRiscoControleMediator.get().estadoConcluido(arc.getEstado())
                && existeAnaliseElementoRascunhoSalva();
    }

    private void addTituloAnalise() {
        String titulo =
                "Análise do supervisor para o elemento \"" + elemento.getParametroElemento().getNome() + "\"";
        Label nome = new Label("idTituloElementoSupervisor", titulo);
        nome.setMarkupId(nome.getId() + elemento.getPk());
        painelAnalise.addOrReplace(nome);
    }

    private void addDataOperadorAnalise() {
        Label dataAnaliseElemento = new Label("idDataAvaliacao", obterUsuarioUltimaAtualizacao());
        painelAnalise.addOrReplace(dataAnaliseElemento);
    }

    private String obterUsuarioUltimaAtualizacao() {
        // Se tiver sido preenchida:
        // Caso o estado do ANEF seja 'Em análise', exibir a mensagem 'Última alteração por <<Nome do usuário que editou a análise do elemento>> em <<data hora da edição>>'.
        // Senão, exibir a mensagem 'Analisado por <<Nome do usuário que concluiu análise do ANEF>> em <<data hora em que concluiu análise do ANEF>>'.
        if (existeAnaliseElementoRascunhoSalva()) {
            if (AvaliacaoRiscoControleMediator.get().estadoEmAnalise(arc.getEstado())) {
                return "Última alteração por " + Util.nomeOperador(elemento.getOperadorAlteracao()) + Constantes.EM
                        + arc.getData(elemento.getDataAlteracao()) + Constantes.PONTO;
            } else {
                return ANALISADO_POR + Util.nomeOperador(arc.getOperadorAnalise()) + Constantes.EM
                        + arc.getData(arc.getDataAnalise()) + Constantes.PONTO;
            }
        } else {
            return Constantes.VAZIO;
        }
    }

    private void addNovaAnalise() {
        Label novaAnalise =
                new Label("idNovaAvaliacao", new PropertyModel<String>(elemento, "justificativaSupervisor")) {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        setVisible(isExibirAnaliseElementoRascunho());
                    }
                };
        novaAnalise.setEscapeModelStrings(false);
        painelAnalise.add(novaAnalise);
    }

    private void addAnaliseVigenteElementoSupervisor() {
        painelAnaliseVigenteElemento = new WebMarkupContainer("idAvaliacaoElementoSupervisorVigente") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisibilityAllowed(isExibirAnaliseElementoVigente());
            }
        };
        painelAnaliseVigenteElemento.setMarkupId(painelAnaliseVigenteElemento.getId() + elemento.getPk());
        addOrReplace(painelAnaliseVigenteElemento);
    }

    private boolean isExibirAnaliseElementoVigente() {
        // 'Análise vigente/Análise':
        // Exibido sempre que for exibida a coluna 'Final' ou 'Vigente' do grupo 'Resumo do ANEF' e; 
        // houver análise do elemento do ANEF do perfil de risco salva.
        return (exibirColunaFinal || exibirColunaVigente) && existeAnaliseElementoVigenteSalva();
    }

    private void addDataOperadorAnaliseVigente() {
        Label dataAtualizacaoElemento =
                new Label("idDataAvaliacaoVigente", obterUsuarioUltimaAtualizacaoAnaliseVigente());
        painelAnaliseVigenteElemento.addOrReplace(dataAtualizacaoElemento);
    }

    private String obterUsuarioUltimaAtualizacaoAnaliseVigente() {
        // Se tiver sido preenchida:
        // Exibir a mensagem 'Analisado por <<Nome do usuário que concluiu análise do ANEF>> em <<data hora em que concluiu análise do ANEF>>'.
        if (existeAnaliseElementoVigenteSalva()) {
            return ANALISADO_POR + Util.nomeOperador(elementoARCVigente.getOperadorAlteracao()) + Constantes.EM
                    + arc.getData(elementoARCVigente.getDataAlteracao()) + Constantes.PONTO;
        } else {
            return Constantes.VAZIO;
        }
    }

    private void addPainelEGrupoExpansivel() {
        PainelAnaliseVigenteARC painelGrupoVigente =
                new PainelAnaliseVigenteARC("idPainelJustificativaAnalise", elemento, elementoARCVigente, arc);
        painelAnaliseVigenteElemento.addOrReplace(painelGrupoVigente);
        GrupoExpansivel grupoExpansivelAnaliseVigente =
                new GrupoExpansivel("idGrupoExpansivelAnaliseVigente", getTituloAnaliseVigente(),
                        isExpandidoInicialmente(), new Component[] {painelGrupoVigente}) {
                    @Override
                    public String getMarkupIdControle() {
                        return "idGrupoAnaliseVigente_" + elemento.getParametroElemento().getDescricao();
                    }
                    @Override
                    public String getMarkupIdTitulo() {
                        return "bttExpandirGrupoAnaliseVigente";
                    }
                    @Override
                    public boolean isControleVisivel() {
                        return !isExpandidoInicialmente();

                    }
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();
                        add(new AttributeModifier("style", new Model<String>(definirStyle())));
                    }
                };
        painelAnaliseVigenteElemento.addOrReplace(grupoExpansivelAnaliseVigente);

    }

    private String definirStyle() {
        String css = "";
        if (isExpandidoInicialmente()) {
            css = "border-left: 16px solid white";
        }
        return css;
    }

    private String getTituloAnaliseVigente() {
        // Caso o estado do ANEF seja 'Concluído', o nome do campo deve ser 'Análise'. Caso contrário, será 'Análise vigente'.
        if (AvaliacaoRiscoControleMediator.get().estadoConcluido(arc.getEstado())) {
            return "Análise";
        } else {
            return "Análise vigente";
        }
    }

    private boolean isExpandidoInicialmente() {
        return !(isExibirAnaliseElementoRascunho() && isExibirAnaliseElementoVigente());
    }

    private boolean existeAnaliseElementoRascunhoSalva() {
        return elemento != null && StringUtils.isNotBlank(elemento.getJustificativaSupervisor());
    }

    private boolean existeAnaliseElementoVigenteSalva() {
        return elementoARCVigente != null && StringUtils.isNotBlank(elementoARCVigente.getJustificativaSupervisor());
    }

}