package br.gov.bcb.sisaps.web.page.dominio.ciclo.painel;

import org.apache.wicket.markup.html.basic.Label;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.web.page.PainelSisAps;

@SuppressWarnings("serial")
public class PainelDadosCiclo extends PainelSisAps {

    protected String matriculaSupervisor;
    private Ciclo ciclo;

    public PainelDadosCiclo(String id, Ciclo ciclo, String titulo) {
        super(id);
        this.ciclo = ciclo;
        add(new Label("idTitulo", titulo));
        addComponents();

    }

    private void addComponents() {

        add(new Label("idNumeroPTPE", ciclo == null ? "" : ciclo.getCodigoPTPE()));

        addDatas();

        add(new Label("idNomeEntidadeSupervisionavel", ciclo == null || ciclo.getEntidadeSupervisionavel() == null ? ""
                : ciclo.getEntidadeSupervisionavel().getNome()));

        add(new Label("idMetodologia", ciclo == null || ciclo.getMetodologia() == null ? "" : ciclo.getMetodologia()
                .getTitulo()));

        add(new Label("idEstadoCiclo", ciclo == null || ciclo.getEstadoCiclo() == null
                || ciclo.getEstadoCiclo().getEstado() == null ? "" : ciclo.getEstadoCiclo().getEstado().getDescricao()));

        addPainelOperador();

    }

    private void addDatas() {
        add(new Label("idDataInicioCiclo", ciclo == null ? "" : ciclo.getDataInicioFormatada()));
        add(new Label("idDataPrevistaCorec", ciclo == null ? "" : ciclo.getDataPrevisaoFormatada()));
    }

    private void addPainelOperador() {
        add(new Label("idOperador", ciclo == null || ciclo.getOperador() == null ? "" : ciclo.getOperador()));
        add(new Label("idData", ciclo == null || ciclo.getDataFormatada() == null ? "" : ciclo.getDataFormatada()));
        add(new Label("idHora", ciclo == null || ciclo.getHoraFormatada() == null ? "" : ciclo.getHoraFormatada()));
    }
}
