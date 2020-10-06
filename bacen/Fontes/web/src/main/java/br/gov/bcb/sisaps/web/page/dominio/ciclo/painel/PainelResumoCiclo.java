package br.gov.bcb.sisaps.web.page.dominio.ciclo.painel;

import java.util.Date;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.EstadoCiclo;
import br.gov.bcb.sisaps.src.dominio.HistoricoLegado;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.EstadoCicloMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.web.page.PainelSisAps;

@SuppressWarnings("serial")
public class PainelResumoCiclo extends PainelSisAps {

    private ServidorVO supervisor;
    private ServidorVO gerente;
    private PerfilRisco perfilRisco;
    private boolean estadoCiclo;
    private Ciclo ciclo;
    private Integer dataBaseCicloMigrado;
    @SpringBean
    private CicloMediator cicloMediator;
    @SpringBean
    private PerfilRiscoMediator perfilRiscoMediator;
    @SpringBean
    private EstadoCicloMediator estadoCicloMediator;

    public PainelResumoCiclo(String id, Integer ciclo) {
        super(id);
        this.ciclo = (cicloMediator.buscarCicloPorPK(ciclo));
        addTitulo();
    }

    public PainelResumoCiclo(String id, Integer ciclo, PerfilRisco perfil, boolean estadoCiclo) {
        this(id, ciclo, perfil);
        this.estadoCiclo = estadoCiclo;
        addTitulo();
    }

    public PainelResumoCiclo(String id, Integer ciclo, PerfilRisco perfil) {
        this(id, ciclo);
        this.perfilRisco = perfil;
    }

    private void addTitulo() {
        String titulo = "Estado";
        if (estadoCiclo) {
            titulo = "Situação";
        }
        Label lblTitulo = new Label("idTituloEstado", titulo);
        addOrReplace(lblTitulo);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        EntidadeSupervisionavel entidadeSupervisionavel = null;
        EstadoCiclo estado = null;
        if (perfilRisco == null) {
            estado = this.ciclo.getEstadoCiclo();
            entidadeSupervisionavel = this.ciclo.getEntidadeSupervisionavel();
            setSupervisorGerenteDataBaseCicloMigrado(entidadeSupervisionavel.getLocalizacao(), null);
        } else {
            System.out.println("##BUGLOCALIZACAO perfilRisco: " + perfilRisco.getPk());
            estado = perfilRiscoMediator.getEstadoCicloPerfilRisco(perfilRisco);
            if (estado != null) {
                System.out.println("##BUGLOCALIZACAO estado: " + estado.getPk());
            }
            entidadeSupervisionavel = perfilRiscoMediator.getEntidadeSupervisionavelPerfilRisco(perfilRisco);
            if (entidadeSupervisionavel != null) {
                System.out.println("##BUGLOCALIZACAO entidadeSupervisionavel pk: " + entidadeSupervisionavel.getPk());
                System.out
                        .println("##BUGLOCALIZACAO entidadeSupervisionavel nome: " + entidadeSupervisionavel.getNome());
                System.out.println(
                        "##BUGLOCALIZACAO entidadeSupervisionavel local: " + entidadeSupervisionavel.getLocalizacao());
                System.out.println(
                        "##BUGLOCALIZACAO entidadeSupervisionavel nome superv: "
                                + entidadeSupervisionavel.getNomeSupervisor());
                setSupervisorGerenteDataBaseCicloMigrado(entidadeSupervisionavel.getLocalizacao(), perfilRisco
                        .getDataCriacao().toDate());
            }
        }
        addComponents(entidadeSupervisionavel, estado);
    }

    private void setSupervisorGerenteDataBaseCicloMigrado(String localizacao, Date dataBase) {
        if (SisapsUtil.isCicloMigrado(ciclo)) {
            HistoricoLegado historicoLegado = cicloMediator.buscarHistoricoLegadoCiclo(ciclo.getPk());
            this.supervisor = BcPessoaAdapter.get().buscarServidor(historicoLegado.getMatriculaSupervisor(), dataBase);
            this.gerente = BcPessoaAdapter.get().buscarServidor(historicoLegado.getMatriculaGerente(), dataBase);
            this.dataBaseCicloMigrado = historicoLegado.getDataBase();
        } else {
            this.supervisor = cicloMediator.buscarChefeAtual(localizacao, dataBase);
            this.gerente = null;
            this.dataBaseCicloMigrado = null;
        }
    }

    private void addComponents(EntidadeSupervisionavel entidadeSupervisionavel, EstadoCiclo estado) {
        addOrReplace(new Label("idNomeConglomerado", entidadeSupervisionavel == null ? ""
                : entidadeSupervisionavel.getNomeConglomeradoFormatado()));
        addOrReplace(new Label("idEstadoCiclo", estado == null ? "" : (estado.getEstado() == null ? "" : estado
                .getEstado().getDescricao())));
        addOrReplace(new Label("idDataInicioCiclo", ciclo == null ? "" : ciclo.getDataInicioFormatada()) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(!SisapsUtil.isCicloMigrado(ciclo));
            }
        });
        addOrReplace(new Label("idCorec", estadoCicloMediator.obterLabelCorec(estado)));
        addOrReplace(new Label("idDataPrevistaCorec", ciclo == null ? "" : ciclo.getDataPrevisaoFormatada()));

        addOrReplace(new Label("idDataBaseCicloMigrado", dataBaseCicloMigrado == null ? ""
                : DataUtil.formatoMesAno(String.valueOf(dataBaseCicloMigrado))) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(SisapsUtil.isCicloMigrado(ciclo));
            }
        });
        addDadosSupervisorGerente();
    }

    private void addDadosSupervisorGerente() {
        String labelSupervisor = "Supervisor titular";
        String labelGerente = "Gerente titular";
        String nomeGerente = supervisor == null ? "" : supervisor.getNomeChefe();
        if (SisapsUtil.isCicloMigrado(ciclo)) {
            labelSupervisor = "Supervisor";
            labelGerente = "Gerente";
            nomeGerente = gerente == null ? "" : gerente.getNome();
        }

        addOrReplace(new Label("idSupervisor", labelSupervisor));
        addOrReplace(new Label("idNomeSupervisor", supervisor == null ? "" : supervisor.getNome()));

        addOrReplace(new Label("idGerente", labelGerente));
        addOrReplace(new Label("idNomeGerente", nomeGerente == null ? "" : nomeGerente));
    }

    public void setPerfilRisco(PerfilRisco perfilRisco) {
        this.perfilRisco = perfilRisco;
        this.ciclo = perfilRisco.getCiclo();
    }

    public void atualizarPainel(AjaxRequestTarget target) {
        target.add(PainelResumoCiclo.this);
    }
}
