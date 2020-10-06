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

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Transient;

import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.EstadoCiclo;
import br.gov.bcb.sisaps.src.dominio.HistoricoLegado;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.util.Constantes;

public class CicloVO extends ObjetoPersistenteVO {

    private static final long serialVersionUID = 1L;

    private String codigoPTPE;
    private Date dataInicio;
    private Date dataInicioCorec;
    private Date dataPrevisaoCorec;
    private String nomeES;
    private EntidadeSupervisionavelVO entidadeSupervisionavel;
    private EstadoCicloVO estadoCiclo;
    private MetodologiaVO metodologia;
    private MatrizVO matriz;
    private String local;

    public CicloVO() {
        super();
    }
    
    public CicloVO(Integer pk) {
        this.pk = pk;
    }

    public CicloVO(Integer pk, Date dataInicio, Date dataInicioCorec, Date dataPrevisaoCorec,
            EntidadeSupervisionavelVO entidadeSupervisionavel, EstadoCicloVO estadoCicloVO) {
        this.pk = pk;
        this.dataInicio = dataInicio;
        this.dataPrevisaoCorec = dataPrevisaoCorec;
        this.entidadeSupervisionavel = entidadeSupervisionavel;
        this.estadoCiclo = estadoCicloVO;
        this.dataInicioCorec = dataInicioCorec;
    }

    @Override
    public Integer getPk() {
        return pk;
    }

    @Override
    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public String getCodigoPTPE() {
        return codigoPTPE;
    }

    public void setCodigoPTPE(String codigoPTPE) {
        this.codigoPTPE = codigoPTPE;
    }

    public EntidadeSupervisionavelVO getEntidadeSupervisionavel() {
        return entidadeSupervisionavel;
    }

    /**
     * Usado para o AliasToBeanResultTransformer transformar a entidade resultante da consulta em
     * VO
     * 
     * @param entidadeSupervisionavel
     */
    public void setEntidadeSupervisionavel(EntidadeSupervisionavel entidadeSupervisionavel) {
        this.entidadeSupervisionavel =
                new EntidadeSupervisionavelVO(entidadeSupervisionavel.getPk(), entidadeSupervisionavel.getNome(),
                        entidadeSupervisionavel.getConglomeradoOuCnpj());
        this.entidadeSupervisionavel.setLocalizacao(entidadeSupervisionavel.getLocalizacao());
        this.entidadeSupervisionavel.setPrioridade(entidadeSupervisionavel.getPrioridade());
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataPrevisaoCorec() {
        return dataPrevisaoCorec;
    }

    public void setDataPrevisaoCorec(Date dataPrevisaoCorec) {
        this.dataPrevisaoCorec = dataPrevisaoCorec;
    }

    public EstadoCicloVO getEstadoCiclo() {
        return estadoCiclo;
    }

    /**
     * Usado para o AliasToBeanResultTransformer transformar a entidade resultante da consulta em
     * VO
     * 
     * @param estadoCiclo
     */
    public void setEstadoCiclo(EstadoCiclo estadoCiclo) {
        this.estadoCiclo = new EstadoCicloVO(estadoCiclo.getPk(), estadoCiclo.getEstado());
    }

    public MetodologiaVO getMetodologia() {
        return metodologia;
    }

    public void setMetodologia(MetodologiaVO metodologia) {
        this.metodologia = metodologia;
    }

    public MatrizVO getMatriz() {
        return matriz;
    }

    public void setMatriz(MatrizVO matriz) {
        this.matriz = matriz;
    }

    public String getDataInicioFormatada() {
        return this.dataInicio == null ? "" : new SimpleDateFormat(Constantes.FORMATO_DATA_COM_BARRAS)
                .format(this.dataInicio);
    }

    public String getDataPrevisaoFormatada() {
        return this.dataPrevisaoCorec == null ? "" : new SimpleDateFormat(Constantes.FORMATO_DATA_COM_BARRAS)
                .format(this.dataPrevisaoCorec);
    }

    public Date getDataInicioCorec() {
        return dataInicioCorec;
    }

    public void setDataInicioCorec(Date dataInicioCorec) {
        this.dataInicioCorec = dataInicioCorec;
    }
    
    public String getDataInicioCorecFormatada() {
        return this.dataInicioCorec == null ? "" : new SimpleDateFormat(Constantes.FORMATO_DATA_COM_BARRAS)
                .format(this.dataInicioCorec);
    }

    public String getNomeES() {
        return nomeES;
    }

    public void setNomeES(String nomeES) {
        this.nomeES = nomeES;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public boolean isPodeAlterar() {
        return this.pk > 0;
    }
    
    @Transient
    public String getNomeSupervisor() {
        if (this.pk < 0) {
            HistoricoLegado historicoLegado = CicloMediator.get().buscarHistoricoLegadoCiclo(this.pk);
            ServidorVO servidor = BcPessoaAdapter.get().buscarServidor(historicoLegado.getMatriculaSupervisor(), null);
            return servidor == null ? "" : servidor.getNome();
        } else {
            return entidadeSupervisionavel.getNomeSupervisor();
        }
    }
    
    public String getNomeSupervisorCorec() {
        return entidadeSupervisionavel.getNomeSupervisor(dataPrevisaoCorec);
    }

}
