package br.gov.bcb.sisaps.src.dominio.enumeracoes;

import java.util.HashMap;
import java.util.Map;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoDescricao;
import br.gov.bcb.sisaps.util.Constantes;

/**
 * Enumera��o das situa��es funcionais atribu�veis a um servidor. 
 * Espelho da classe SituacaoFuncional do BcPessoa deixando apenas as situa��es 
 * tenham anotado "Ativo" no campo "situacaoResumida" e "null" no campo "observacoes"
 */
public enum SituacaoFuncionalEnum implements EnumeracaoComCodigoDescricao<Integer> {

    ATIVO_NORMAL(116, "ATIVO NORMAL", Constantes.ATIVO, Constantes.SERVIDOR, null),
    ATIVO_FERIAS(117, "ATIVO F�RIAS", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    ATIVO_NORMAL_REQUISITADO_BB(
            118, "ATIVO NORMAL REQUISITADO BB", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    ATIVO_NORMAL_REQUISITADO_DIVERSOS(
            119, "ATIVO NORMAL REQUISITADO DIVERSOS", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    ATIVO_CLT(120, "ATIVO CLT", Constantes.ATIVO, Constantes.SERVIDOR, null),
    ATIVO_ADIDO_A_PEDIDO(121, "ATIVO ADIDO A PEDIDO", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    ATIVO_ADIDO_EX_OFFICIO(122, "ATIVO ADIDO EX-OFFICIO", Constantes.ATIVO, Constantes.SERVIDOR, null),
    ESTAGIO_A_PEDIDO(125, "EST�GIO A PEDIDO", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    ESTAGIO_DE_OFICIO(126, "EST�GIO DE OF�CIO", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    EXERCICIO_TEMPORARIO(127, "EXERC�CIO TEMPOR�RIO", Constantes.ATIVO, Constantes.SERVIDOR, null),
    ATIVO_EXCLUIDO_DE_FOLHA_TEMPORARIAMENTE(
            132, "ATIVO EXCLU�DO DE FOLHA TEMPORARIAMENTE", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    AFASTADO_ARTIGO_483_CLT(133, "AFASTADO - ARTIGO 483 - CLT", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    ATIVO_LICENCA_MATERNIDADE(142, "ATIVO LICEN�A MATERNIDADE", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    ATIVO_LICENCA_SAUDE(143, "ATIVO LICEN�A SA�DE", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    ATIVO_LICENCA_ACIDENTE_DE_TRABALHO(
            144, "ATIVO LICEN�A ACIDENTE DE TRABALHO", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    ATIVO_COM_ONUS_REEMBOLSAVEL_REGESP(
            146, "ATIVO C/�NUS REEMBOLSAVEL - REGESP", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    ATIVO_MISSAO_EXTERIOR_COM_ONUS(147, "ATIVO MISS�O NO EXTERIOR COM �NUS", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    LICENCA_POR_MOTIVO_DE_DOENCA_NA_FAMILIA(
            159, "LICEN�A POR MOTIVO DE DOEN�A NA FAM�LIA", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    ATIVO_LICENCA_PREMIO(160, "ATIVO LICEN�A PR�MIO", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    ATIVO_LIC_SAUDE_NAO_RECONHECIDA_PELO_BANCO(
            169, "ATIVO LIC. SA�DE N�O RECONHECIDA P/ BCO", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    ATIVO_DIRETOR_PARCELA_VARIAVEL_25_PC(
            188, "ATIVO DIRETOR - PARCELA VARI�VEL 25%", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    ATIVO_DIRETOR(189, "ATIVO DIRETOR", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    NAO_FUNCIONARIO_DIRETOR(190, "N�O FUNCION�RIO - DIRETOR", Constantes.ATIVO, Constantes.NAO_SERVIDOR, null),
    NAO_FUNCIONARIO_APOS_DIRETOR(191, "N�O FUNCION�RIO APOS - DIRETOR", Constantes.ATIVO, Constantes.NAO_SERVIDOR, null), 
    CCT_NAO_FUNCIONARIO(251, "CCT - N�O FUNCION�RIO", Constantes.ATIVO, Constantes.NAO_SERVIDOR, null), 
    FUNCIONARIO_APOSENTADO_CCT_TEMPORARIO(
            252, "FUNCION�RIO APOSENTADO - CCT TEMPOR�RIO", Constantes.ATIVO, Constantes.NAO_SERVIDOR, null), 
    DIRETOR_FUNCIONARIO_APOSENTADO(253, "DIRETOR FUNCION�RIO APOSENTADO", Constantes.ATIVO, Constantes.NAO_SERVIDOR, null), 
    DIRETOR_FUNCIONARIO_APOSENTADO_POR_VELHICE(
            254, "DIRETOR FUNCION�RIO APOSENT. P/VELHICE", Constantes.ATIVO, Constantes.NAO_SERVIDOR, null), 
    DIRETOR_FUNCIONARIO_APOSENTADO_ESPECIAL(
            255, "DIRETOR FUNCION�RIO APOSENTADO ESPECIAL", Constantes.ATIVO, Constantes.NAO_SERVIDOR, null), 
    DIRETOR_FUNCIONARIO_APOSENT_EX_COMBATE(
            256, "DIRETOR FUNCION�RIO APOSENT. EX-COMBATE", Constantes.ATIVO, Constantes.NAO_SERVIDOR, null);
    
    public static final String CLASS_NAME = "br.gov.bcb.sisaps.src.dominio.enumeracoes.SituacaoFuncionalEnum";

    private static final Map<Integer, SituacaoFuncionalEnum> SITUACOES_MAP = new HashMap<Integer, SituacaoFuncionalEnum>();

    private Integer codigo;
    private String descricao;
    private String situacaoResumida;
    private String quadro;
    private String observacoes;

    private SituacaoFuncionalEnum(int codigo, String descricao,
            String situacaoResumida, String quadro, String observacoes) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.situacaoResumida = situacaoResumida;
        this.quadro = quadro;
        this.observacoes = observacoes;
    }

    static {
        for (SituacaoFuncionalEnum situacao : SituacaoFuncionalEnum.values()) {
            SITUACOES_MAP.put(situacao.getCodigo(), situacao);
        }
    }

    public static SituacaoFuncionalEnum obterSituacao(int codigo) {
        return SITUACOES_MAP.get(codigo);
    }

    /**
     * Retorna o c�digo da situa��o funcional.
     * 
     * @return C�digo da situa��o funcional
     */
    public Integer getCodigo() {
        return codigo;
    }

    /**
     * Retorna a descri��o da situa��o funcional.
     * 
     * @return Descri��o da situa��o funcional.
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Retorna a descri��o da situa��o quanto � atividade (ativo / inativo / pensionista / desligado) e regime (RJU ou
     * CLT).
     * 
     * @return Descri��o da situa��o quanto � atividade e regime.
     */
    public String getSituacaoResumida() {
        return situacaoResumida;
    }

    /**
     * Retorna a descri��o da situa��o quanto ao quadro de ativos / inativos.
     * 
     * @return Descri��o da situa��o quanto ao quadro de ativos / inativos.
     */
    public String getQuadro() {
        return quadro;
    }

    /**
     * Retorna as observa��es adicionais sobre a situa��o.
     * 
     * @return Observa��es adicionais sobre a situa��o.
     */
    public String getObservacoes() {
        return observacoes;
    }
    
    public String getCodigoDescricao() {
        return this.codigo + " " + this.descricao;
    }
}
