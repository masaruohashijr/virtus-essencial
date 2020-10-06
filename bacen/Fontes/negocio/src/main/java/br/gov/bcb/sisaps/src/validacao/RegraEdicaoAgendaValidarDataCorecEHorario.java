/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arqui contém informações proprietárias.
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
        
        SisapsUtil.validarHoraInvalida(horario, "Horário", Constantes.FORMATO_HORA_AGENDA,
                erros);
        
        
        SisapsUtil.lancarNegocioException(erros);
    }
}