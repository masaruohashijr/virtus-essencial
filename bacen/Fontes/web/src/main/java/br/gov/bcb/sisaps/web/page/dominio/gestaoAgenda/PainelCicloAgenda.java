package br.gov.bcb.sisaps.web.page.dominio.gestaoAgenda;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.EstadoCiclo;
import br.gov.bcb.sisaps.src.dominio.HistoricoLegado;
import br.gov.bcb.sisaps.src.mediator.AgendaCorecMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.calendar.CalendarTextField;
import br.gov.bcb.sisaps.web.page.componentes.infraestrutura.ExceptionUtils;

@SuppressWarnings("serial")
public class PainelCicloAgenda extends PainelSisAps {
    private static final String PATTERN = "dd/MM/yyyy";
    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern(PATTERN);
    private ServidorVO supervisor;
    private final Ciclo ciclo;
    @SpringBean
    private CicloMediator cicloMediator;
    private final boolean isAlteracao;
    private Date dataCorec;
    private IModel<String> modelDataCorec = new Model<String>();

    public PainelCicloAgenda(String id, AgendaCorec agenda, boolean isAlteracao) {
        super(id);
        this.ciclo = agenda.getCiclo();
        this.isAlteracao = isAlteracao;
        this.dataCorec = ciclo.getDataPrevisaoCorec();
        this.modelDataCorec.setObject(getDataPrevisaoFormatada());
    }

    public String getDataPrevisaoFormatada() {
        return this.dataCorec == null ? "" : new SimpleDateFormat(Constantes.FORMATO_DATA_COM_BARRAS)
                .format(this.dataCorec);
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
        estado = this.ciclo.getEstadoCiclo();
        entidadeSupervisionavel = this.ciclo.getEntidadeSupervisionavel();
        setSupervisorGerenteDataBaseCicloMigrado(entidadeSupervisionavel.getLocalizacao(), null);
        addComponents(entidadeSupervisionavel, estado);
        addDataCorecPrevista();
    }

    private void addDataCorecPrevista() {
        CalendarTextField<String> txtDataPrevCorec = new CalendarTextField<String>("idimputDataCorec", modelDataCorec);
        txtDataPrevCorec.setOutputMarkupId(true);
        boolean comiteARealizar = AgendaCorecMediator.get().comiteARealizar(ciclo);
        txtDataPrevCorec.setVisible(isAlteracao && comiteARealizar);
        txtDataPrevCorec.setParentId("idCalendario");
        txtDataPrevCorec.setOutputMarkupId(true);
        txtDataPrevCorec.setMarkupId(txtDataPrevCorec.getId());
        txtDataPrevCorec.setVisible(isAlteracao && comiteARealizar);
        addOrReplace(txtDataPrevCorec);

        Label labelDataCorec = new Label("idLabelDataPrevistaCorec", getDataPrevisaoFormatada());
        labelDataCorec.setVisible(!isAlteracao || !comiteARealizar);
        addOrReplace(labelDataCorec);
    }

    private void setSupervisorGerenteDataBaseCicloMigrado(String localizacao, Date dataBase) {
        if (SisapsUtil.isCicloMigrado(ciclo)) {
            HistoricoLegado historicoLegado = cicloMediator.buscarHistoricoLegadoCiclo(ciclo.getPk());
            this.supervisor = BcPessoaAdapter.get().buscarServidor(historicoLegado.getMatriculaSupervisor(), dataBase);
        } else {
            this.supervisor = cicloMediator.buscarChefeAtual(localizacao, dataBase);
        }
    }

    private void addComponents(EntidadeSupervisionavel entidadeSupervisionavel, EstadoCiclo estado) {
        addOrReplace(new Label("idNomeConglomerado", entidadeSupervisionavel == null ? ""
                : entidadeSupervisionavel.getNomeConglomeradoFormatado()));

        addOrReplace(new Label("idEquipe", entidadeSupervisionavel == null ? ""
                : entidadeSupervisionavel.getLocalizacao()));

        addOrReplace(new Label("idPrioridade", entidadeSupervisionavel == null ? ""
                : entidadeSupervisionavel.getPrioridade() == null ? "" : entidadeSupervisionavel.getPrioridade()
                        .getDescricao()));

        addOrReplace(new Label("idDataInicioCiclo", ciclo == null ? "" : ciclo.getDataInicioFormatada()));
        addOrReplace(new Label("idCorec", "Corec"));
        Label labelDataCorec = new Label("idDataPrevistaCorec", ciclo == null ? "" : ciclo.getDataPrevisaoFormatada());
        labelDataCorec.setOutputMarkupId(true);
        labelDataCorec.setVisible(!isAlteracao);
        addOrReplace(labelDataCorec);

        addDadosSupervisor();
    }

    private void addDadosSupervisor() {
        addOrReplace(new Label("idNomeSupervisor", supervisor == null ? "" : supervisor.getNome()));
    }

    public void atualizarPainel(AjaxRequestTarget target) {
        target.add(PainelCicloAgenda.this);
    }

    public Date getDataCorec() {
        return dataCorec;
    }

    public void setDataCorec(Date dataCorec) {
        this.dataCorec = dataCorec;
    }

    public IModel<String> getModelDataCorec() {
        return modelDataCorec;
    }

    public void setModelDataCorec(IModel<String> modelDataCorec) {
        this.modelDataCorec = modelDataCorec;
    }

    public void atualizarDataCorec(String object) {
        try {
            setDataCorec(Util.isNuloOuVazio(object) ? null : FORMATTER.parseLocalDate(object).toDate());
        } catch (IllegalArgumentException e) {
            ExceptionUtils.tratarIllegalArgumentExceptio(e, getPage(), "Corec Previsto");
        }
    }

}
