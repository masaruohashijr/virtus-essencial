package br.gov.bcb.sisaps.src.dominio;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.mediator.CargaParticipanteMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;

@Entity
@Table(name = "PAC_PARTICIPANTE_COREC", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = ParticipanteAgendaCorec.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "PAC_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "PAC_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(name = "PAC_NU_VERSAO", nullable = false))})
public class ParticipanteAgendaCorec extends ObjetoPersistenteAuditavelVersionado<Integer> {

    public static final String CAMPO_ID = "PAC_ID";

    private AgendaCorec agenda;
    private String matricula;
    private DateTime assinatura;
    private boolean assinado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = AgendaCorec.CAMPO_ID, nullable = false)
    public AgendaCorec getAgenda() {
        return agenda;
    }

    public void setAgenda(AgendaCorec agenda) {
        this.agenda = agenda;
    }

    @Column(name = "PAC_CD_MATRICULA", nullable = false, columnDefinition = "character(8)")
    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    @Column(name = "PAC_DH_ASSINATURA_ATA")
    public DateTime getAssinatura() {
        return assinatura;
    }

    public void setAssinatura(DateTime assinatura) {
        this.assinatura = assinatura;
    }

    @Transient
    public String getDataAssinaturaFormatada() {
        return assinatura == null ? "" : assinatura.toString(Constantes.FORMATO_DATA_HORA);
    }

    @Transient
    public String getNome() {
        String nome = "";
        if (getMatricula() != null) {
            CargaParticipante cargaParticipante = CargaParticipanteMediator.get().buscarCargaPorMatricula(getMatricula());
            if (cargaParticipante != null && StringUtils.isNotBlank(cargaParticipante.getNome())) {
                nome = cargaParticipante.getNome();
            } else {
                ServidorVO servidorVO = BcPessoaAdapter.get().buscarServidor(getMatricula());
                if (servidorVO != null) {
                    nome = servidorVO.getNome();
                }
            }
            StringBuilder odh = new StringBuilder(nome);
            nome = odh.toString();
        }
        return nome;
    }

    @Transient
    public boolean getAssinado() {
        return assinatura != null;
    }

    public void setAssinado(boolean assinado) {
        this.assinado = assinado;
    }

    @Transient
    public String getDescricaoAssinaturaAta() {
        if (getAssinado()) {
            return "Ata assinada em " + getDataAssinaturaFormatada() + ".";
        } else {
            return "Ata não assinada.";
        }

    }

}
