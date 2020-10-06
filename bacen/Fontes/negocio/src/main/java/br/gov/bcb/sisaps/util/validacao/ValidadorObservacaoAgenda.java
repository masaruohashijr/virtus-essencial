package br.gov.bcb.sisaps.util.validacao;

import br.gov.bcb.sisaps.src.dominio.ObservacaoAgendaCorec;

public class ValidadorObservacaoAgenda extends Validador {

 

    public Validador camposObrigatorios(ObservacaoAgendaCorec observacao) {
        naoNulo(observacao.getDescricao());
        return this;
    }


    public Validador naoNulo(Object obj) {
        if (obj == null) {
            adicionar("O campo texto � de preenchimento obrigat�rio para a inclus�o da observa��o.");
        }
        return this;
    }
}
