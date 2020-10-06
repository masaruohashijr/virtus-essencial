package br.gov.bcb.sisaps.web.page.dominio.ciclo.painel;

import org.apache.wicket.Component;

import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.grupo.GrupoExpansivel;
import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesWeb;

public class PainelResponsavelCicloGrupo extends PainelSisAps {

    /**
     * 
     */
    private static final long serialVersionUID = -2935854480934134258L;
    private String matriculaSupervisor;
    private GrupoExpansivel grupo;
    private boolean inicialmenteExpandido;

    public PainelResponsavelCicloGrupo(String id, String matriculaSupervisor, boolean inicialmenteExpandido) {
        super(id);
        this.matriculaSupervisor = matriculaSupervisor;
        this.inicialmenteExpandido = inicialmenteExpandido;
        addComponentes();
    }

    private void addComponentes() {
        addEscopoEObjetivo();
    }

    private void addEscopoEObjetivo() {
        PainelSisAps painel;

        painel = new PainelResponsavelCiclo("idPainelEquipe", matriculaSupervisor);

        painel.setOutputMarkupId(true);
        grupo =
                new GrupoExpansivel("GrupoEspansivelEquipe", "Dados da equipe", inicialmenteExpandido,
                        new Component[] {painel});
        grupo.setCssTitulo(ConstantesWeb.CSS_FUNDO_PADRAO_A_ESCURO3);
        add(painel);
        add(grupo);

    }

    public GrupoExpansivel getGrupoResponsavelCiclo() {
        return grupo;
    }

    public String getMatriculaSupervisor() {
        return matriculaSupervisor;
    }

    public void setMatriculaSupervisor(String matriculaSupervisor) {
        this.matriculaSupervisor = matriculaSupervisor;
    }

    
    
    
}
