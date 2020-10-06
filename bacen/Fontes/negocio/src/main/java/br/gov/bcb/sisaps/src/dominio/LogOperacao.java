package br.gov.bcb.sisaps.src.dominio;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;

@Entity
@Table(name = "LOP_LOG_OPERACOES", schema = "SUP")
@AttributeOverrides(value = {
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ID, column = @Column(name = LogOperacao.CAMPO_ID)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_ULTIMA_ATUALIZACAO, column = @Column(name = "LOP_DH_OPERACAO", nullable = false)),
        @AttributeOverride(name = ObjetoPersistenteAuditavel.PROP_OPERADOR_ATUALIZACAO, column = @Column(name = "LOP_CD_RESP_OPERACAO", nullable = false, columnDefinition = "varchar(20)"))})
public class LogOperacao extends ObjetoPersistenteAuditavel<Integer> {
    public static final String CAMPO_ID = "LOP_ID";
    private static final int TAMANHO_NOME_TITULO = 200;
    private static final int TAMANHO_CNPJ = 8;
    private static final int TAMANHO_TRILHA = 400;
    private static final int TAMANHO_CODIGO_TELA_RESPONSAVEL = 20;
    private PerfilRisco perfilRisco;
    private String codigoCnpj;
    private String nome;
    private String trilha;
    private String tituloTela;
    private String codigoTela;

    @Column(name = "LOP_CD_CNPJ", nullable = false, length = TAMANHO_CNPJ)
    public String getCodigoCnpj() {
        return codigoCnpj;
    }

    @Column(name = "LOP_DS_NOME_ES", nullable = false, length = TAMANHO_NOME_TITULO)
    public String getNome() {
        return nome;
    }

    @Column(name = "LOP_DS_TRILHA", nullable = false, length = TAMANHO_TRILHA)
    public String getTrilha() {
        return trilha;
    }

    @Column(name = "LOP_DS_TITULO_TELA", nullable = false, length = TAMANHO_NOME_TITULO)
    public String getTituloTela() {
        return tituloTela;
    }

    @Column(name = "LOP_CD_CODIGO_TELA", nullable = false, length = TAMANHO_CODIGO_TELA_RESPONSAVEL)
    public String getCodigoTela() {
        return codigoTela;
    }


    @ManyToOne
    @JoinColumn(name = PerfilRisco.CAMPO_ID)
    public PerfilRisco getPerfilRisco() {
        return perfilRisco;
    }


    public void setPerfilRisco(PerfilRisco perfilRisco) {
        this.perfilRisco = perfilRisco;
    }

    public void setCodigoCnpj(String codigoCnpj) {
        this.codigoCnpj = codigoCnpj;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTrilha(String trilha) {
        this.trilha = trilha;
    }

    public void setTituloTela(String tituloTela) {
        this.tituloTela = tituloTela;
    }

    public void setCodigoTela(String codigoTela) {
        this.codigoTela = codigoTela;
    }


}
