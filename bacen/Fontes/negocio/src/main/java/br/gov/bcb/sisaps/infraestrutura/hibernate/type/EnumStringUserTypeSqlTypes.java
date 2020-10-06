package br.gov.bcb.sisaps.infraestrutura.hibernate.type;

public abstract class EnumStringUserTypeSqlTypes<E extends Enum<E>> extends EnumStringUserTypeVerificacao<E> {

    private static final int[] SQL_TYPES = {SQL_TYPE};

    @Override
    public int[] sqlTypes() {
        return SQL_TYPES.clone();
    }

}