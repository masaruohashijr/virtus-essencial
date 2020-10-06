package br.gov.bcb.sisaps.web.page.componentes.mascara;

public class MascaraDeMatriculaBehavior extends AbstractMascaraBehavior {

    private static final int TAMANHO = 11;

    public MascaraDeMatriculaBehavior() {
        setMascara(PADRAO_MATRICULA, TAMANHO, true);
    }
}
