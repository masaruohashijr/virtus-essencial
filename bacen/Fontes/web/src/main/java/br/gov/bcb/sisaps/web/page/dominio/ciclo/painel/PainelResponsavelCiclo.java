package br.gov.bcb.sisaps.web.page.dominio.ciclo.painel;

import org.apache.wicket.markup.html.basic.Label;

import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.web.page.PainelSisAps;

@SuppressWarnings("serial")
public class PainelResponsavelCiclo extends PainelSisAps {

    protected String matriculaSupervisor;
    private ServidorVO supervidor;

    public PainelResponsavelCiclo(String id, String matriculaSupervisor) {
        super(id);
        this.matriculaSupervisor = matriculaSupervisor;
        this.supervidor =
                matriculaSupervisor == null ? null : BcPessoaAdapter.get().buscarServidor(matriculaSupervisor);
        addComponents();
    }

    private void addComponents() {

        add(new Label("unidadeGerente", supervidor == null ? "" : supervidor.getUnidade()));
        add(new Label("unidadeSupervisor", supervidor == null ? "" : supervidor.getUnidade()));
        add(new Label("unidadeSubSupervisor", supervidor == null ? "" : supervidor.getUnidade()));

        add(new Label("nomeGerente", supervidor == null ? "" : supervidor.getNomeChefe()));
        add(new Label("nomeSupervisor", supervidor == null ? "" : supervidor.getNome()));
        add(new Label("nomeSubSupervisor", supervidor == null ? "" : supervidor.getNomeSubstituto()));

    }
}
