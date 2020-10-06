/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.adaptadores.pessoa;

import java.util.ArrayList;
import java.util.Date;

import br.gov.bcb.comum.excecoes.BCConsultaInvalidaException;
import br.gov.bcb.comum.pessoa.negocio.componenteorganizacional.ConsultaChefia;
import br.gov.bcb.comum.pessoa.negocio.componenteorganizacional.ConsultaChefiaSubstitutoEventual;
import br.gov.bcb.comum.pessoa.negocio.componenteorganizacional.ConsultaComponentesOrganizacionais;
import br.gov.bcb.comum.pessoa.negocio.funcaocomissionada.ConsultaExercicioFuncaoComissionada;
import br.gov.bcb.comum.pessoa.negocio.servidor.ConsultaServidores;
import br.gov.bcb.sisaps.src.vo.ComponenteOrganizacionalVO;

public interface IBcPessoa {
    String NOME = "compBcPessoa";

    ServidorVO buscarServidor(String matricula, Date dataBase) throws BCConsultaInvalidaException;

    ArrayList<ServidorVO> consultarServidores(ConsultaServidores consulta) throws BCConsultaInvalidaException;

    ArrayList<ComponenteOrganizacionalVO> consultarComponentesOrganizacionais(ConsultaComponentesOrganizacionais consulta)
            throws BCConsultaInvalidaException;

    ServidorVO buscarServidorPorLogin(String login, Date dataBase) throws BCConsultaInvalidaException;

    ChefiaVO buscarChefia(ConsultaChefia consulta) throws BCConsultaInvalidaException;

    ChefiaVO buscarChefiaPorSubstitutoEventual(ConsultaChefiaSubstitutoEventual consulta)
            throws BCConsultaInvalidaException;

    ArrayList<ComponenteOrganizacionalVO> consultarUnidadesAtivas() throws BCConsultaInvalidaException;

    ExercicioFuncaoComissionadaVO buscarExercicioFuncaoComissionada(ConsultaExercicioFuncaoComissionada consulta)
            throws BCConsultaInvalidaException;

    ArrayList<FuncaoComissionadaVO> consultarTodasFuncoesComissionadas() throws BCConsultaInvalidaException;

    ComponenteOrganizacionalVO buscarComponenteOrganizacionalPorRotulo(String rotulo, Date dataBase)
            throws BCConsultaInvalidaException;
    

}
