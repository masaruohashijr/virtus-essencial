package br.gov.bcb.sisaps.src.dominio.enumeracoes;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoStringDescricao;

public enum EstadoMatrizEnum implements EnumeracaoComCodigoStringDescricao {

    ESBOCADA("1", "Esboçada"), VIGENTE("2", "Vigente");

    public static final String CLASS_NAME = "br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoMatrizEnum";

    private String codigo;
    private String descricao;

    private EstadoMatrizEnum(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    @Override
    public String getCodigo() {
        return codigo;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public static EstadoMatrizEnum valueOfCodigo(String codigo) {
        for (EstadoMatrizEnum obj : values()) {
            if (obj.getCodigo().equals(codigo)) {
                return obj;
            }
        }
        return null;
    }

}
