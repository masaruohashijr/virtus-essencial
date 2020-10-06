package br.gov.bcb.sisaps.src.dominio.enumeracoes;

import java.util.HashMap;
import java.util.Map;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoDescricao;
import br.gov.bcb.sisaps.util.Constantes;

/**
 * Enumeração das situações funcionais atribuíveis a um servidor. 
 * Espelho da classe SituacaoFuncional do BcPessoa deixando apenas as situações 
 * tenham anotado "Ativo" no campo "situacaoResumida" e "null" no campo "observacoes"
 */
public enum SituacaoFuncionalEnum implements EnumeracaoComCodigoDescricao<Integer> {

    ATIVO_NORMAL(116, "ATIVO NORMAL", Constantes.ATIVO, Constantes.SERVIDOR, null),
    ATIVO_FERIAS(117, "ATIVO FÉRIAS", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    ATIVO_NORMAL_REQUISITADO_BB(
            118, "ATIVO NORMAL REQUISITADO BB", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    ATIVO_NORMAL_REQUISITADO_DIVERSOS(
            119, "ATIVO NORMAL REQUISITADO DIVERSOS", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    ATIVO_CLT(120, "ATIVO CLT", Constantes.ATIVO, Constantes.SERVIDOR, null),
    ATIVO_ADIDO_A_PEDIDO(121, "ATIVO ADIDO A PEDIDO", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    ATIVO_ADIDO_EX_OFFICIO(122, "ATIVO ADIDO EX-OFFICIO", Constantes.ATIVO, Constantes.SERVIDOR, null),
    ESTAGIO_A_PEDIDO(125, "ESTÁGIO A PEDIDO", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    ESTAGIO_DE_OFICIO(126, "ESTÁGIO DE OFÍCIO", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    EXERCICIO_TEMPORARIO(127, "EXERCÍCIO TEMPORÁRIO", Constantes.ATIVO, Constantes.SERVIDOR, null),
    ATIVO_EXCLUIDO_DE_FOLHA_TEMPORARIAMENTE(
            132, "ATIVO EXCLUÍDO DE FOLHA TEMPORARIAMENTE", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    AFASTADO_ARTIGO_483_CLT(133, "AFASTADO - ARTIGO 483 - CLT", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    ATIVO_LICENCA_MATERNIDADE(142, "ATIVO LICENÇA MATERNIDADE", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    ATIVO_LICENCA_SAUDE(143, "ATIVO LICENÇA SAÚDE", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    ATIVO_LICENCA_ACIDENTE_DE_TRABALHO(
            144, "ATIVO LICENÇA ACIDENTE DE TRABALHO", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    ATIVO_COM_ONUS_REEMBOLSAVEL_REGESP(
            146, "ATIVO C/ÔNUS REEMBOLSAVEL - REGESP", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    ATIVO_MISSAO_EXTERIOR_COM_ONUS(147, "ATIVO MISSÃO NO EXTERIOR COM ÔNUS", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    LICENCA_POR_MOTIVO_DE_DOENCA_NA_FAMILIA(
            159, "LICENÇA POR MOTIVO DE DOENÇA NA FAMÍLIA", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    ATIVO_LICENCA_PREMIO(160, "ATIVO LICENÇA PRÊMIO", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    ATIVO_LIC_SAUDE_NAO_RECONHECIDA_PELO_BANCO(
            169, "ATIVO LIC. SAÚDE NÃO RECONHECIDA P/ BCO", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    ATIVO_DIRETOR_PARCELA_VARIAVEL_25_PC(
            188, "ATIVO DIRETOR - PARCELA VARIÁVEL 25%", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    ATIVO_DIRETOR(189, "ATIVO DIRETOR", Constantes.ATIVO, Constantes.SERVIDOR, null), 
    NAO_FUNCIONARIO_DIRETOR(190, "NÃO FUNCIONÁRIO - DIRETOR", Constantes.ATIVO, Constantes.NAO_SERVIDOR, null),
    NAO_FUNCIONARIO_APOS_DIRETOR(191, "NÃO FUNCIONÁRIO APOS - DIRETOR", Constantes.ATIVO, Constantes.NAO_SERVIDOR, null), 
    CCT_NAO_FUNCIONARIO(251, "CCT - NÃO FUNCIONÁRIO", Constantes.ATIVO, Constantes.NAO_SERVIDOR, null), 
    FUNCIONARIO_APOSENTADO_CCT_TEMPORARIO(
            252, "FUNCIONÁRIO APOSENTADO - CCT TEMPORÁRIO", Constantes.ATIVO, Constantes.NAO_SERVIDOR, null), 
    DIRETOR_FUNCIONARIO_APOSENTADO(253, "DIRETOR FUNCIONÁRIO APOSENTADO", Constantes.ATIVO, Constantes.NAO_SERVIDOR, null), 
    DIRETOR_FUNCIONARIO_APOSENTADO_POR_VELHICE(
            254, "DIRETOR FUNCIONÁRIO APOSENT. P/VELHICE", Constantes.ATIVO, Constantes.NAO_SERVIDOR, null), 
    DIRETOR_FUNCIONARIO_APOSENTADO_ESPECIAL(
            255, "DIRETOR FUNCIONÁRIO APOSENTADO ESPECIAL", Constantes.ATIVO, Constantes.NAO_SERVIDOR, null), 
    DIRETOR_FUNCIONARIO_APOSENT_EX_COMBATE(
            256, "DIRETOR FUNCIONÁRIO APOSENT. EX-COMBATE", Constantes.ATIVO, Constantes.NAO_SERVIDOR, null);
    
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
     * Retorna o código da situação funcional.
     * 
     * @return Código da situação funcional
     */
    public Integer getCodigo() {
        return codigo;
    }

    /**
     * Retorna a descrição da situação funcional.
     * 
     * @return Descrição da situação funcional.
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Retorna a descrição da situação quanto à atividade (ativo / inativo / pensionista / desligado) e regime (RJU ou
     * CLT).
     * 
     * @return Descrição da situação quanto à atividade e regime.
     */
    public String getSituacaoResumida() {
        return situacaoResumida;
    }

    /**
     * Retorna a descrição da situação quanto ao quadro de ativos / inativos.
     * 
     * @return Descrição da situação quanto ao quadro de ativos / inativos.
     */
    public String getQuadro() {
        return quadro;
    }

    /**
     * Retorna as observações adicionais sobre a situação.
     * 
     * @return Observações adicionais sobre a situação.
     */
    public String getObservacoes() {
        return observacoes;
    }
    
    public String getCodigoDescricao() {
        return this.codigo + " " + this.descricao;
    }
}
