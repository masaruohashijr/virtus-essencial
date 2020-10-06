package br.gov.bcb.sisaps.src.dominio;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.sisaps.util.objetos.ObjetoPersistenteAuditavelVersionado;


@Entity
@Table(name = "AEX_ARC_EXTERNO", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = AvaliacaoRiscoControleExterno.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "AEX_DH_ATUALZ", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "AEX_CD_OPER_ATUALZ", nullable = false, columnDefinition = "varchar(20)")),
        @AttributeOverride(name = ObjetoPersistenteAuditavelVersionado.PROP_VERSAO, column = @Column(nullable = false, name = "AEX_NU_VERSAO"))})
public class AvaliacaoRiscoControleExterno extends ObjetoPersistenteAuditavelVersionado<Integer> {

    public static final String CAMPO_ID = "AEX_ID";
    
    private ParametroGrupoRiscoControle parametroGrupoRiscoControle;
    private AvaliacaoRiscoControle avaliacaoRiscoControle;
    private Ciclo ciclo;
    
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = ParametroGrupoRiscoControle.CAMPO_ID, nullable = false)
    public ParametroGrupoRiscoControle getParametroGrupoRiscoControle() {
        return parametroGrupoRiscoControle;
    }

    public void setParametroGrupoRiscoControle(ParametroGrupoRiscoControle parametroGrupoRiscoControle) {
        this.parametroGrupoRiscoControle = parametroGrupoRiscoControle;
    }
    
    @OneToOne
    @JoinColumn(name = AvaliacaoRiscoControle.CAMPO_ID, nullable = false)
    public AvaliacaoRiscoControle getAvaliacaoRiscoControle() {
        return avaliacaoRiscoControle;
    }

    public void setAvaliacaoRiscoControle(AvaliacaoRiscoControle avaliacaoRiscoControle) {
        this.avaliacaoRiscoControle = avaliacaoRiscoControle;
    }
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Ciclo.CAMPO_ID, nullable = false)
    public Ciclo getCiclo() {
        return ciclo;
    }

    public void setCiclo(Ciclo ciclo) {
        this.ciclo = ciclo;
    }
    
}
