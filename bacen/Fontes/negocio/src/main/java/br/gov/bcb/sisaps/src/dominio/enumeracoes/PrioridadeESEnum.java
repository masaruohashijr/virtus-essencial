package br.gov.bcb.sisaps.src.dominio.enumeracoes;

import java.util.Arrays;
import java.util.List;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoStringDescricao;

public enum PrioridadeESEnum implements EnumeracaoComCodigoStringDescricao {

    ALTA("1", "Alta"),
    BAIXA("2", "Baixa"),
    MEDIA("3", "Média"),
    MEDIO_ALTA("4", "Médio alta"),
    SIFI("5", "SIFI");

    public static final String CLASS_NAME = "br.gov.bcb.sisaps.src.dominio.enumeracoes.PrioridadeESEnum";
    public static final List<PrioridadeESEnum> LIST = Arrays.asList(ALTA, BAIXA,  MEDIA, MEDIO_ALTA, SIFI);

    private String codigo;
    private String descricao;


    private PrioridadeESEnum(String codigo, String descricao) {
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

    public static EnumeracaoComCodigoStringDescricao valueOfCodigo(String codigo) {
        for (PrioridadeESEnum e : PrioridadeESEnum.values()) {
            if (e.getCodigo().equals(codigo)) {
                return e;
            }
        }
        return null;
    }

    public static PrioridadeESEnum valueOfDescricao(String descricao) {
        for (PrioridadeESEnum e : PrioridadeESEnum.values()) {
            if (e.getDescricao().equals(descricao)) {
                return e;
            }
        }
        return null;
    }

}
