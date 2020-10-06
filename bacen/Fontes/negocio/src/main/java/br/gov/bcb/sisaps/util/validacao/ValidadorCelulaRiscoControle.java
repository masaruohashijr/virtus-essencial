package br.gov.bcb.sisaps.util.validacao;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;

public class ValidadorCelulaRiscoControle extends Validador {

    private static final String ERRO_GRUPO_DUPLICADO = "Grupo já incluído.";

    public Validador validadorComum(CelulaRiscoControle celula, List<CelulaRiscoControle> listaCelulasRiscoControle) {
        camposObrigatorios(celula);
        if (getMensagens().isEmpty()) {
            grupoRiscoControleDuplicado(celula, listaCelulasRiscoControle);
        }
        return this;
    }

    public Validador camposObrigatorios(CelulaRiscoControle celula) {
        naoNulo(celula.getParametroGrupoRiscoControle(), CelulaRiscoControle.DESCRICAO_GRUPO_RISCO_CONTROLE);
        naoNulo(celula.getParametroPeso(), CelulaRiscoControle.DESCRICAO_PESO);
        return this;
    }

    public Validador grupoRiscoControleDuplicado(CelulaRiscoControle celula,
            List<CelulaRiscoControle> listaCelulasRiscoControle) {
        for (CelulaRiscoControle arcIncluido : listaCelulasRiscoControle) {
            if (arcIncluido.getParametroGrupoRiscoControle().getPk()
                    .equals(celula.getParametroGrupoRiscoControle().getPk())) {
                adicionar(ERRO_GRUPO_DUPLICADO);
            }
        }
        return this;
    }

    public Validador naoNulo(Object obj, String campo) {
        if (obj == null) {
            adicionar(String.format(CAMPO_OBRIGATORIO, campo));
        }
        return this;
    }
}
