/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.src.vo;

import java.util.List;

import org.joda.time.LocalDate;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.util.consulta.Consulta;

public class ConsultaCicloVO extends Consulta<CicloVO> {

    private List<EstadoCicloEnum> estados;
    private EntidadeSupervisionavelVO entidadeSupervisionavel;
    private LocalDate dataInicio;
    private LocalDate dataPrevisaoCorec;
    private PerfilAcessoEnum perfil;
    private String rotuloLocalizacao;
    private boolean buscarHierarquiaInferior;
    private LocalDate dataCorecInicio;
    private LocalDate dataCorecFim;
    private boolean isRealizar;
    private int indice;
    private String matriculaSupervisor;
    

    public ConsultaCicloVO() {
        this.entidadeSupervisionavel = new EntidadeSupervisionavelVO();
    }

    public List<EstadoCicloEnum> getEstados() {
        return estados;
    }

    public void setEstados(List<EstadoCicloEnum> estados) {
        this.estados = estados;
    }

    public EntidadeSupervisionavelVO getEntidadeSupervisionavel() {
        return entidadeSupervisionavel;
    }

    public void setEntidadeSupervisionavel(EntidadeSupervisionavelVO entidadeSupervisionavel) {
        this.entidadeSupervisionavel = entidadeSupervisionavel;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataPrevisaoCorec() {
        return dataPrevisaoCorec;
    }

    public void setDataPrevisaoCorec(LocalDate dataPrevisaoCorec) {
        this.dataPrevisaoCorec = dataPrevisaoCorec;
    }

    public PerfilAcessoEnum getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilAcessoEnum perfil) {
        this.perfil = perfil;
    }

    public String getRotuloLocalizacao() {
        return rotuloLocalizacao;
    }

    public void setRotuloLocalizacao(String rotuloLocalizacao) {
        this.rotuloLocalizacao = rotuloLocalizacao;
    }

    public boolean isBuscarHierarquiaInferior() {
        return buscarHierarquiaInferior;
    }

    public void setBuscarHierarquiaInferior(boolean isBuscarHierarquiaInferior) {
        this.buscarHierarquiaInferior = isBuscarHierarquiaInferior;
    }

    public LocalDate getDataCorecInicio() {
        return dataCorecInicio;
    }

    public void setDataCorecInicio(LocalDate dataCorecInicio) {
        this.dataCorecInicio = dataCorecInicio;
    }

    public LocalDate getDataCorecFim() {
        return dataCorecFim;
    }

    public void setDataCorecFim(LocalDate dataCorecFim) {
        this.dataCorecFim = dataCorecFim;
    }

    public boolean isRealizar() {
        return isRealizar;
    }

    public void setRealizar(boolean isRealizar) {
        this.isRealizar = isRealizar;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public String getMatriculaSupervisor() {
        return matriculaSupervisor;
    }

    public void setMatriculaSupervisor(String matriculaSupervisor) {
        this.matriculaSupervisor = matriculaSupervisor;
    }
    
    


}