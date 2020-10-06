/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arqui cont�m informa��es propriet�rias.
 */
package br.gov.bcb.sisaps.src.validacao;

import java.util.ArrayList;
import java.util.Date;

import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.src.dominio.ObservacaoAgendaCorec;
import br.gov.bcb.sisaps.src.mediator.AgendaCorecMediator;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraAgendaAlteracaoCorec {

    private AgendaCorec agenda;
    private Date dataPrevisaoCorec;

    public RegraAgendaAlteracaoCorec(AgendaCorec agenda, Date dataPrevisaoCorec) {
        this.agenda = agenda;
        this.dataPrevisaoCorec = dataPrevisaoCorec;
    }

    public void validar() {

        //        A data � de preenchimento obrigat�rio e deve ser uma data v�lida maior ou igual que a data corrente. 
        //        Sempre que for alterada a data do 'Corec previsto', pelo menos uma nova observa��o deve ser registrada.

        if (AgendaCorecMediator.get().comiteARealizar(agenda.getCiclo())) {
            ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
            SisapsUtil.validarObrigatoriedade(dataPrevisaoCorec, "Corec previsto", erros);

            if (dataPrevisaoCorec != null && dataPrevisaoCorec.before(DataUtil.getDateTimeAtual().toLocalDate().toDate())) {
                SisapsUtil.adicionarErro(erros, new ErrorMessage(
                        "O campo 'Corec previsto' deve ser maior ou igual que a data corrente."));
            }

            if (dataPrevisaoCorec != null) {
                if (!dataPrevisaoCorec.equals(agenda.getCiclo().getDataPrevisaoCorec())) {

                    boolean possuiObservacao = false;
                    for (ObservacaoAgendaCorec obs : agenda.getObservacoes()) {
                        if (obs.getPk() == null) {
                            possuiObservacao = true;
                            break;
                        }
                    }
                    if (!possuiObservacao) {
                        erros.add(new ErrorMessage("Pelo menos uma nova observa��o deve ser registrada."));
                    }
                }
            }

            SisapsUtil.lancarNegocioException(erros);
        }

    }
}