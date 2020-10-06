/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.src.dominio.enumeracoes;

import java.util.Arrays;
import java.util.List;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoStringDescricao;

public enum TipoGrupoEnum implements EnumeracaoComCodigoStringDescricao {

    RISCO("1", "Risco", "R"), CONTROLE("2", "Controle", "C"), EXTERNO("3", "Externo", "E");

    public static final String CLASS_NAME = "br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoGrupoEnum";
    
    public static final List<TipoGrupoEnum> TIPO_GRUPO_MATRIZ = Arrays.asList(RISCO, CONTROLE);
    
    private String codigo;
    private String descricao;
    private String abreviacao;

    private TipoGrupoEnum(String codigo, String descricao, String abreviacao) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.abreviacao = abreviacao;
    }

    @Override
    public String getCodigo() {
        return codigo;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
    
    public String getAbreviacao() {
        return this.equals(EXTERNO) ? "" : abreviacao;
    }
    
    public static List<TipoGrupoEnum> listaTipoGrupo() {
        return Arrays.asList(TipoGrupoEnum.CONTROLE, TipoGrupoEnum.RISCO);
    }

    public static TipoGrupoEnum valueOfCodigo(String codigo) {
        for (TipoGrupoEnum e : values()) {
            if (e.getCodigo().equals(codigo)) {
                return e;
            }
        }
        return null;
    }

    public static TipoGrupoEnum valueOfDescricao(String descricao) {
        for (TipoGrupoEnum e : values()) {
            if (e.getDescricao().equals(descricao)) {
                return e;
            }
        }
        return null;
    }

}
