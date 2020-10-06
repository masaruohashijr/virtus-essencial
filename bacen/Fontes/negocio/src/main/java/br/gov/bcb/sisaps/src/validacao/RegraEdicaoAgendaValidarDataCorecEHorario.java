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

import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraEdicaoAgendaValidarDataCorecEHorario {

    private String strDataCorec;
    private String horario;

    public RegraEdicaoAgendaValidarDataCorecEHorario(String strDataCorec, String horario) {
        this.strDataCorec = strDataCorec;
        this.horario = horario;
    }

    public void validar() {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
        SisapsUtil.validarDataInvalida(strDataCorec, "Corec Previsto", Constantes.FORMATO_DATA_COM_BARRAS,
                erros);
        
        SisapsUtil.validarHoraInvalida(horario, "Hor�rio", Constantes.FORMATO_HORA_AGENDA,
                erros);
        
        
        SisapsUtil.lancarNegocioException(erros);
    }
}