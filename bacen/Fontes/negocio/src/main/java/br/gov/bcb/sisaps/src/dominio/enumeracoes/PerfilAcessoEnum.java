package br.gov.bcb.sisaps.src.dominio.enumeracoes;

import java.util.Arrays;
import java.util.List;

import br.gov.bcb.sisaps.infraestrutura.hibernate.type.EnumeracaoComCodigoStringDescricao;

public enum PerfilAcessoEnum implements EnumeracaoComCodigoStringDescricao {

    ADMINISTRADOR("1", "Administrador"),
    COMITE("2", "Comitê"),
    CONSULTA_NAO_BLOQUEADOS("3", "Consulta somente não bloqueados"),
    CONSULTA_RESUMO_NAO_BLOQUEADOS("4", "Consulta somente resumo de perfil não bloqueado"),
    CONSULTA_TUDO("5", "Consulta tudo"),
    GERENTE("6", "Gerente"),
    INSPETOR("7", "Inspetor"),
    SUPERVISOR("8", "Supervisor");

    public static final String CLASS_NAME = "br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum";
    
    public static final List<PerfilAcessoEnum> LIST = Arrays.asList(ADMINISTRADOR, COMITE, CONSULTA_NAO_BLOQUEADOS,
            CONSULTA_RESUMO_NAO_BLOQUEADOS, CONSULTA_TUDO, GERENTE, INSPETOR, SUPERVISOR);

    private String codigo;
    private String descricao;
    


    private PerfilAcessoEnum(String codigo, String descricao) {
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

    public static PerfilAcessoEnum valueOfCodigo(String codigo) {
        for (PerfilAcessoEnum e : PerfilAcessoEnum.values()) {
            if (e.getCodigo().equals(codigo)) {
                return e;
            }
        }
        return null;
    }

    public static PerfilAcessoEnum valueOfDescricao(String descricao) {
        for (PerfilAcessoEnum e : PerfilAcessoEnum.values()) {
            if (e.getDescricao().equals(descricao)) {
                return e;
            }
        }
        return null;
    }
    
    
    
    

}
