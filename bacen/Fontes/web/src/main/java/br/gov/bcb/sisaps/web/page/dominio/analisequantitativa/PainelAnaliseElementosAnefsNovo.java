package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ElementoAQT;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.grupo.GrupoExpansivel;
import br.gov.bcb.sisaps.web.page.dominio.analisequantitativaaqt.PainelAnaliseVigente;

public class PainelAnaliseElementosAnefsNovo extends PainelSisAps {
    private static final String ANALISADO_POR = "Analisado por ";

    private static final String ID_AVALIACAO_SUPER_VIGENTE = "idAvaliacaoElementoSupervisorVigente";
    
    private final boolean exibirColunaSupervisor;
    private final boolean exibirColunaVigente;
    private final boolean exibirColunaFinal;
    private final ElementoAQT elemento;
    private final ElementoAQT elementoANEFVigente;
    private final AnaliseQuantitativaAQT anef;
    private final AnaliseQuantitativaAQT anefVigente;
    private WebMarkupContainer painelAnalise;
    private WebMarkupContainer painelAnaliseVigenteElemento;



    public PainelAnaliseElementosAnefsNovo(String id, AnaliseQuantitativaAQT aqt, AnaliseQuantitativaAQT aqtVigente,
            ElementoAQT elemento, ElementoAQT elementoANEFVigente, boolean exibirColunaSupervisor,
            boolean exibirColunaVigente) {
        super(id);
        this.anef = aqt;
        this.anefVigente = aqtVigente;
        this.elemento = elemento;
        this.elementoANEFVigente = elementoANEFVigente;
        this.exibirColunaSupervisor = exibirColunaSupervisor;
        this.exibirColunaVigente = exibirColunaVigente;
        this.exibirColunaFinal = AnaliseQuantitativaAQTMediator.get().exibirColunaFinal(anef.getEstado());
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisibilityAllowed(isExibirAnaliseElementoRascunho() || isExibirAnaliseElementoVigente());
        addComponentes();
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
        // Nova an�lise: Exibido sempre que for exibida a coluna 'Supervisor' do grupo 'Resumo do ANEF' 
        // e o estado do ANEF seja diferente de 'Conclu�do' e; houver an�lise do elemento do ANEF salva.
        return exibirColunaSupervisor && !AnaliseQuantitativaAQTMediator.get().estadoConcluido(anef.getEstado())
                && existeAnaliseElementoRascunhoSalva();
    }
    
    private void addTituloAnalise() {
        String titulo = "An�lise do supervisor para o elemento \"" + elemento.getParametroElemento().getDescricao() + "\"";
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
        // Caso o estado do ANEF seja 'Em an�lise', exibir a mensagem '�ltima altera��o por <<Nome do usu�rio que editou a an�lise do elemento>> em <<data hora da edi��o>>'.
        // Sen�o, exibir a mensagem 'Analisado por <<Nome do usu�rio que concluiu an�lise do ANEF>> em <<data hora em que concluiu an�lise do ANEF>>'.
        if (existeAnaliseElementoRascunhoSalva()) {
            if (AnaliseQuantitativaAQTMediator.get().estadoEmAnalise(anef.getEstado())) {
                return "�ltima altera��o por " + Util.nomeOperador(elemento.getOperadorAlteracao()) 
                        + Constantes.EM + anef.getData(elemento.getDataAlteracao()) 
                        + Constantes.PONTO;
            } else {
                return ANALISADO_POR + Util.nomeOperador(anef.getOperadorAnalise()) 
                        + Constantes.EM + anef.getData(anef.getDataAnalise()) + Constantes.PONTO;
            }
        } else {
            return Constantes.VAZIO;
        }
    }
    
    private void addNovaAnalise() {
        Label novaAnalise = new Label("idNovaAvaliacao", new PropertyModel<String>(elemento, "justificativaSupervisor")) {
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
        painelAnaliseVigenteElemento = new WebMarkupContainer(ID_AVALIACAO_SUPER_VIGENTE) {
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
        // 'An�lise vigente/An�lise':
        // Exibido sempre que for exibida a coluna 'Final' ou 'Vigente' do grupo 'Resumo do ANEF' e; 
        // houver an�lise do elemento do ANEF do perfil de risco salva.
        return (exibirColunaFinal || exibirColunaVigente) && existeAnaliseElementoVigenteSalva();
    }
    
    private void addDataOperadorAnaliseVigente() {
        Label dataAtualizacaoElemento = new Label("idDataAvaliacaoVigente", obterUsuarioUltimaAtualizacaoAnaliseVigente());
        painelAnaliseVigenteElemento.addOrReplace(dataAtualizacaoElemento);
    }

    private String obterUsuarioUltimaAtualizacaoAnaliseVigente() {
        // Se tiver sido preenchida:
        // Exibir a mensagem 'Analisado por <<Nome do usu�rio que concluiu an�lise do ANEF>> em <<data hora em que concluiu an�lise do ANEF>>'.
        if (existeAnaliseElementoVigenteSalva()) {
            return ANALISADO_POR + Util.nomeOperador(anefVigente.getOperadorAnalise()) 
                    + Constantes.EM + anef.getData(anefVigente.getDataAnalise()) + Constantes.PONTO;
        } else {
            return Constantes.VAZIO;
        }
    }

    private void addPainelEGrupoExpansivel() {
        PainelAnaliseVigente painelGrupoVigente =
                new PainelAnaliseVigente("idPainelJustificativaAnalise", elemento, elementoANEFVigente, anef);
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
        // Caso o estado do ANEF seja 'Conclu�do', o nome do campo deve ser 'An�lise'. Caso contr�rio, ser� 'An�lise vigente'.
        if (AnaliseQuantitativaAQTMediator.get().estadoConcluido(anef.getEstado())) {
            return "An�lise";
        } else {
            return "An�lise vigente";
        }
    }
    
    private boolean isExpandidoInicialmente() {
        return !(isExibirAnaliseElementoRascunho() && isExibirAnaliseElementoVigente());
    }
    
    private boolean existeAnaliseElementoRascunhoSalva() {
        return elemento != null && StringUtils.isNotBlank(elemento.getJustificativaSupervisor());
    }
    
    private boolean existeAnaliseElementoVigenteSalva() {
        return elementoANEFVigente != null && StringUtils.isNotBlank(elementoANEFVigente.getJustificativaSupervisor());
    }

}