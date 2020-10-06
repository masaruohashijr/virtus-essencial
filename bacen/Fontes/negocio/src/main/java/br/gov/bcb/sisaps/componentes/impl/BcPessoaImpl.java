/*
 * Sistema SCM
 * 
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.componentes.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import br.gov.bcb.component.infra.factory.ComponentFactory;
import br.gov.bcb.comum.excecoes.BCConsultaInvalidaException;
import br.gov.bcb.comum.pessoa.negocio.BcPessoa;
import br.gov.bcb.comum.pessoa.negocio.componenteorganizacional.Chefia;
import br.gov.bcb.comum.pessoa.negocio.componenteorganizacional.ComponenteOrganizacional;
import br.gov.bcb.comum.pessoa.negocio.componenteorganizacional.ConsultaChefia;
import br.gov.bcb.comum.pessoa.negocio.componenteorganizacional.ConsultaChefiaSubstitutoEventual;
import br.gov.bcb.comum.pessoa.negocio.componenteorganizacional.ConsultaComponentesOrganizacionais;
import br.gov.bcb.comum.pessoa.negocio.componenteorganizacional.TipoComponenteOrganizacionalId;
import br.gov.bcb.comum.pessoa.negocio.funcaocomissionada.ConsultaExercicioFuncaoComissionada;
import br.gov.bcb.comum.pessoa.negocio.funcaocomissionada.ConsultaFuncaoComissionada;
import br.gov.bcb.comum.pessoa.negocio.funcaocomissionada.ExercicioFuncaoComissionada;
import br.gov.bcb.comum.pessoa.negocio.funcaocomissionada.FuncaoComissionada;
import br.gov.bcb.comum.pessoa.negocio.servidor.ConsultaServidores;
import br.gov.bcb.comum.pessoa.negocio.servidor.Servidor;
import br.gov.bcb.sisaps.adaptadores.pessoa.ChefiaVO;
import br.gov.bcb.sisaps.adaptadores.pessoa.ExercicioFuncaoComissionadaVO;
import br.gov.bcb.sisaps.adaptadores.pessoa.FuncaoComissionadaVO;
import br.gov.bcb.sisaps.adaptadores.pessoa.IBcPessoa;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.helper.ComponenteOrganizacionalHelper;
import br.gov.bcb.sisaps.src.vo.ComponenteOrganizacionalVO;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.geral.SisapsUtilSuporte;

@Component(IBcPessoa.NOME)
public class BcPessoaImpl implements IBcPessoa {

    @Override
    public ServidorVO buscarServidor(String matricula, Date dataBase) throws BCConsultaInvalidaException {
        ServidorVO servidorVO = null;

        if (matricula != null) {
            ComponentFactory componentFactory = new ComponentFactory();
            BcPessoa bcPessoa = componentFactory.create(BcPessoa.class);
            System.out.println("##BUGLOCALIZACAO BcPessoaImpl matricula consulta: " + matricula);
            Servidor serv = bcPessoa.buscarServidor(matricula);
            if (serv != null) {
                System.out.println("##BUGLOCALIZACAO BcPessoaImpl serv retorno: " + serv.getMatricula());
                servidorVO = new ServidorVO();
                montarServidorComChefiaFuncao(servidorVO, serv, dataBase, true);
            }
        }
        return servidorVO;
    }

    private void montarServidorComChefiaFuncao(ServidorVO servidorAud, Servidor serv, Date dataBase,
            boolean setarChefia)
            throws BCConsultaInvalidaException {
        montarServidor(servidorAud, serv);
        if (setarChefia) {
            setChefia(servidorAud, dataBase);
        }
    }

    private void montarServidor(ServidorVO servidorVO, Servidor serv) {

        servidorVO.setMatricula(SisapsUtilSuporte.removerFormatacaoMatricula(serv.getMatricula()));
        servidorVO.setLogin(serv.getLogin());
        servidorVO.setNome(serv.getNome());

        System.out.println("##BUGLOCALIZACAO montarServidor: " + serv.getMatricula() + " " + serv.getLogin() + " "
                + serv.getNome());

        montarEmailCorporativo(servidorVO, serv);

        if (serv.getLotacao() != null && serv.getLotacao().getComponenteOrganizacional() != null) {
            servidorVO.setUnidade(serv.getLotacao().getComponenteOrganizacional().getRotulo().substring(0, 5)
                    .toUpperCase());
            servidorVO.setLocalizacao(serv.getLotacao().getComponenteOrganizacional().getRotulo().trim());
            System.out.println(
                    "##BUGLOCALIZACAO getLotacao: " + serv.getLotacao().getComponenteOrganizacional().getRotulo());
        }
        if (serv.getDadosFuncionais() != null && serv.getDadosFuncionais().getSituacao() != null) {
    		servidorVO.setSituacao(String.valueOf(serv.getDadosFuncionais().getSituacao().getCodigo()));
            System.out.println("##BUGLOCALIZACAO getSituacao: " + serv.getDadosFuncionais().getSituacao().getCodigo());
        }
        if (serv.getLotacao() != null && serv.getLotacao().getPostoTrabalho() != null) {
            servidorVO.setFuncao(serv.getLotacao().getPostoTrabalho().getFuncaoComissionadaCodigo());
            System.out.println("##BUGLOCALIZACAO getLotacao: "
                    + serv.getLotacao().getPostoTrabalho().getFuncaoComissionadaCodigo());
        }
    }

	private void montarEmailCorporativo(ServidorVO servidorVO, Servidor serv) {
		if (serv.getEmails() != null && !serv.getEmails().isEmpty()) {
            String email = "";
            for (String itemMail : serv.getEmails()) {
            	
            	//O firewall impede o envio de emails externos, então precisa-se filtrar
            	//o e-mail corporativo. Neste caso, só pode haver um.
            	if (!StringUtils.isBlank(itemMail) && itemMail.trim().endsWith("bcb.gov.br")) {
            		email = itemMail.trim();
            		break;
            	}
            }
            servidorVO.setEmail(email);
        }
	}

    private void setChefia(ServidorVO servidorAud, Date dataBase) throws BCConsultaInvalidaException {
        //Pelo menos um parâmetro deve ser preenchido.
        String matricula = servidorAud.getMatricula();
        if (matricula == null) {
            return;
        }

        Chefia chefia = null;
        Chefia chefiaSubEventual = null;

        ComponentFactory componentFactory = new ComponentFactory();
        BcPessoa bcPessoa = componentFactory.create(BcPessoa.class);

        ConsultaChefia consultaChefe = new ConsultaChefia();
        ConsultaChefia consultaSubEventual = new ConsultaChefia();
        consultaChefe.setServidorMatricula(matricula);
        consultaSubEventual.setComponenteOrganizacionalRotulo(servidorAud.getLocalizacaoAtual());

        if (dataBase != null) {
            consultaChefe.setDataBase(dataBase);
            consultaSubEventual.setDataBase(dataBase);
        }
        try {
            chefia = bcPessoa.buscarChefia(consultaChefe);
        } catch (BCConsultaInvalidaException e) {
            chefia = null;
        }

        if (chefia != null) {
            chefiaSubEventual = getChefia(consultaSubEventual);
            if (chefia.getChefeTitular() != null) {
                servidorAud.setNomeChefe(chefia.getChefeTitular().getNome());
                servidorAud.setMatriculaChefe(
                        SisapsUtilSuporte.removerFormatacaoMatricula(chefia.getChefeTitular().getMatricula()));
                servidorAud.setFuncaoChefe(
                        chefia.getChefeTitular().getLotacao().getPostoTrabalho().getFuncaoComissionadaCodigo());
            }
            if (chefiaSubEventual != null && chefiaSubEventual.getSubstitutoEventual() != null
                    && chefiaSubEventual.getChefeTitular() != null
                    && SisapsUtilSuporte.removerFormatacaoMatricula(chefiaSubEventual.getChefeTitular().getMatricula())
                            .equals(servidorAud.getMatricula())) {
                servidorAud.setNomeSubstituto(chefiaSubEventual.getSubstitutoEventual().getNome());
                servidorAud.setMatriculaSubstituto(SisapsUtilSuporte
                        .removerFormatacaoMatricula(chefiaSubEventual.getSubstitutoEventual().getMatricula()));
            }

        }
    }

    @Override
    public ArrayList<ServidorVO> consultarServidores(ConsultaServidores consulta) throws BCConsultaInvalidaException {
        ComponentFactory componentFactory = new ComponentFactory();
        BcPessoa bcPessoa = componentFactory.create(BcPessoa.class);
        List<Servidor> servidores = bcPessoa.consultarServidores(consulta);
        return converterParaVO(servidores);
    }

    private ArrayList<ServidorVO> converterParaVO(List<Servidor> servidores) {
        ArrayList<ServidorVO> servidoresVO = new ArrayList<ServidorVO>();
        for (Servidor servidor : servidores) {
            ServidorVO servidorVO = new ServidorVO();
            montarServidor(servidorVO, servidor);
            servidoresVO.add(servidorVO);
        }
        return servidoresVO;
    }

    @Override
    public ArrayList<ComponenteOrganizacionalVO> consultarComponentesOrganizacionais(
            ConsultaComponentesOrganizacionais consulta) throws BCConsultaInvalidaException {
        ComponentFactory componentFactory = new ComponentFactory();
        BcPessoa bcPessoa = componentFactory.create(BcPessoa.class);
        return ComponenteOrganizacionalHelper.converterParaVO(bcPessoa.consultarComponentesOrganizacionais(consulta));
    }

    @Override
    public ServidorVO buscarServidorPorLogin(String login, Date dataBase) throws BCConsultaInvalidaException {
        ServidorVO servidorVO = null;
        if (login != null) {
            ComponentFactory componentFactory = new ComponentFactory();
            BcPessoa bcPessoa = componentFactory.create(BcPessoa.class);

            Servidor serv = bcPessoa.buscarServidorPorLogin(login);
            if (serv != null) {
                servidorVO = new ServidorVO();
                montarServidorComChefiaFuncao(servidorVO, serv, dataBase, true);
            }
        }
        return servidorVO;
    }

    @Override
    public ChefiaVO buscarChefia(ConsultaChefia consulta) throws BCConsultaInvalidaException {
        ChefiaVO chefiaVO = null;
        Chefia chefia = getChefia(consulta);
        if (chefia != null) {
            chefiaVO = new ChefiaVO();
            if (chefia.getChefeTitular() != null) {
                ServidorVO chefeTitular = new ServidorVO();
                montarServidorComChefiaFuncao(chefeTitular, chefia.getChefeTitular(), consulta.getDataBase(), false);
                chefiaVO.setChefeTitular(chefeTitular);
            }
            if (chefia.getChefeEmExercicio() != null) {
                ServidorVO chefeExecicio = new ServidorVO();
                montarServidorComChefiaFuncao(chefeExecicio, chefia.getChefeEmExercicio(), consulta.getDataBase(),
                        false);
                chefiaVO.setChefeEmExercicio(chefeExecicio);
            }
            if (chefia.getSubstitutoEventual() != null) {
                ServidorVO chefeSubistituto = new ServidorVO();
                montarServidorComChefiaFuncao(chefeSubistituto, chefia.getSubstitutoEventual(), consulta.getDataBase(),
                        false);
                chefiaVO.setSubstitutoEventual(chefeSubistituto);
            }
        }
        return chefiaVO;
    }

    private Chefia getChefia(ConsultaChefia consulta) throws BCConsultaInvalidaException {
        ComponentFactory componentFactory = new ComponentFactory();
        BcPessoa bcPessoa = componentFactory.create(BcPessoa.class);
        Chefia chefia = bcPessoa.buscarChefia(consulta);
        System.out.println("##APS-SRC getChefia rotulo antes: " + consulta.getComponenteOrganizacionalRotulo());
        if (chefia == null || chefia.getChefeTitular() == null) {
            System.out.println("##APS-SRC getChefia chefia nula");
            String rotulo = "";
            if (consulta.getComponenteOrganizacionalRotulo().contains("-")) {
                System.out.println("##APS-SRC getChefia contains -");
                rotulo = consulta.getComponenteOrganizacionalRotulo().replace("-", "_");
                System.out.println("##APS-SRC getChefia rotulo: " + rotulo);
                consulta.setComponenteOrganizacionalRotulo(rotulo);
                chefia = bcPessoa.buscarChefia(consulta);
            }
        }
        return chefia;
    }

    @Override
    public ChefiaVO buscarChefiaPorSubstitutoEventual(ConsultaChefiaSubstitutoEventual consulta)
            throws BCConsultaInvalidaException {
        ChefiaVO chefiaVO = null;
        Chefia chefia = null;
        ComponentFactory componentFactory = new ComponentFactory();
        BcPessoa bcPessoa = componentFactory.create(BcPessoa.class);
        try {
            chefia = bcPessoa.buscarChefiaPorSubstitutoEventual(consulta);
        } catch (BCConsultaInvalidaException e) {
            if (!"MATRICULA INEXISTENTE OU NAO POSSUI PERFIL PARA SUBSTITUICAO".equals(e.getMessage().trim())) {
                throw new BCConsultaInvalidaException(e);
            }
        }
        if (chefia != null) {
            chefiaVO = new ChefiaVO();
            if (chefia.getChefeTitular() != null) {
                ServidorVO chefeTitular = new ServidorVO();
                montarServidorComChefiaFuncao(chefeTitular, chefia.getChefeTitular(), consulta.getDataBase(), false);
                chefiaVO.setChefeTitular(chefeTitular);
            }
            
            if (chefia.getChefeEmExercicio() != null) {
                ServidorVO chefeExecicio = new ServidorVO();
                montarServidorComChefiaFuncao(chefeExecicio, chefia.getChefeEmExercicio(), consulta.getDataBase(),
                        false);
                chefiaVO.setChefeEmExercicio(chefeExecicio);
            }
            
            if (chefia.getSubstitutoEventual() != null) {
                ServidorVO chefeSubistituto = new ServidorVO();
                montarServidorComChefiaFuncao(chefeSubistituto, chefia.getSubstitutoEventual(), consulta.getDataBase(),
                        false);
                chefiaVO.setSubstitutoEventual(chefeSubistituto);
            }

        }
        return chefiaVO;
    }

    @Override
    public ArrayList<ComponenteOrganizacionalVO> consultarUnidadesAtivas() throws BCConsultaInvalidaException {
        ArrayList<ComponenteOrganizacionalVO> listaComponenteOrganizacional =
                new ArrayList<ComponenteOrganizacionalVO>();
        ConsultaComponentesOrganizacionais consultaUnidades = new ConsultaComponentesOrganizacionais();

        consultaUnidades.setTipoComponenteOrganizacionalId(TipoComponenteOrganizacionalId.UNIDADE);
        selecionarUnidadesAtivas(consultarComponentesOrganizacionais(consultaUnidades), listaComponenteOrganizacional);

        consultaUnidades.setTipoComponenteOrganizacionalId(TipoComponenteOrganizacionalId.NAO_ORGANIZACIONAL);
        selecionarUnidadesAtivas(consultarComponentesOrganizacionais(consultaUnidades), listaComponenteOrganizacional);

        Collections.sort(listaComponenteOrganizacional, new Comparator<ComponenteOrganizacionalVO>() {
            @Override
            public int compare(ComponenteOrganizacionalVO o1, ComponenteOrganizacionalVO o2) {
                return o1.getRotulo().compareTo(o2.getRotulo());
            }
        });

        return listaComponenteOrganizacional;
    }

    private void selecionarUnidadesAtivas(List<ComponenteOrganizacionalVO> listaExistente,
            ArrayList<ComponenteOrganizacionalVO> listaSelecionado) {

        for (ComponenteOrganizacionalVO componente : listaExistente) {
            if (componente.getDataFimVigencia() == null || componente.getDataFimVigencia().after(new Date())) {
                String rotulo = componente.getRotulo().trim();
                if (!unidadeNaoPermitida(rotulo)) {
                    listaSelecionado.add(componente);
                }
            }
        }
    }

    private boolean unidadeNaoPermitida(String rotulo) {
        List<String> unidadeNaoPermitida = Arrays.asList("APOSE", "EXONE", "FALEC", "NAORG");
        boolean retorno = false;
        for (String string : unidadeNaoPermitida) {
            if (rotulo.contains(string)) {
                retorno = true;
                break;
            }
        }
        return retorno;
    }

    @Override
    public ExercicioFuncaoComissionadaVO buscarExercicioFuncaoComissionada(ConsultaExercicioFuncaoComissionada consulta)
            throws BCConsultaInvalidaException {
        ExercicioFuncaoComissionadaVO exercicioFuncaoComissionadaVO = null;
        ComponentFactory componentFactory = new ComponentFactory();
        BcPessoa bcPessoa = componentFactory.create(BcPessoa.class);
        ExercicioFuncaoComissionada exercicioFuncaoComissionada = bcPessoa.buscarExercicioFuncaoComissionada(consulta);
        if (exercicioFuncaoComissionada != null) {
            exercicioFuncaoComissionadaVO = converterParaVO(exercicioFuncaoComissionada);
        }
        return exercicioFuncaoComissionadaVO;
    }

    private ExercicioFuncaoComissionadaVO converterParaVO(ExercicioFuncaoComissionada exercicioFuncaoComissionada) {
        ExercicioFuncaoComissionadaVO exercicioFuncaoComissionadaVO = new ExercicioFuncaoComissionadaVO();
        exercicioFuncaoComissionadaVO.setCodigoFuncaoComissionada(exercicioFuncaoComissionada.getPostoTrabalho()
                .getFuncaoComissionadaCodigo());
        exercicioFuncaoComissionadaVO.setDescricao(exercicioFuncaoComissionada.getPostoTrabalho()
                .getFuncaoComissionada().getDescricao());
        exercicioFuncaoComissionadaVO.setTipoExercicioFuncaoComissionada(exercicioFuncaoComissionada
                .getTipoExercicioFuncaoComissionada().getCodigo());
        if (exercicioFuncaoComissionada.getComponenteOrganizacional() != null) {
            exercicioFuncaoComissionadaVO.setLocalizacaoFuncaoComissionada(exercicioFuncaoComissionada
                    .getComponenteOrganizacional().getRotulo());
        }
        return exercicioFuncaoComissionadaVO;
    }

    @Override
    public ArrayList<FuncaoComissionadaVO> consultarTodasFuncoesComissionadas() throws BCConsultaInvalidaException {
        ComponentFactory componentFactory = new ComponentFactory();
        BcPessoa bcPessoa = componentFactory.create(BcPessoa.class);
        return converterParaVOs(bcPessoa.consultarFuncoesComissionadas(new ConsultaFuncaoComissionada()));
    }

    private ArrayList<FuncaoComissionadaVO> converterParaVOs(List<FuncaoComissionada> funcoesComissionadas) {
        ArrayList<FuncaoComissionadaVO> funcoesVO = new ArrayList<FuncaoComissionadaVO>();
        for (FuncaoComissionada funcao : funcoesComissionadas) {
            FuncaoComissionadaVO funcaoVO = new FuncaoComissionadaVO();
            funcaoVO.setCodigo(funcao.getCodigo());
            funcaoVO.setDescricao(funcao.getDescricao());
            funcoesVO.add(funcaoVO);
        }
        return funcoesVO;
    }

    @Override
    public ComponenteOrganizacionalVO buscarComponenteOrganizacionalPorRotulo(String rotulo, Date dataBase)
            throws BCConsultaInvalidaException {
        ComponentFactory componentFactory = new ComponentFactory();
        BcPessoa bcPessoa = componentFactory.create(BcPessoa.class);
        ComponenteOrganizacional componente = bcPessoa.buscarComponenteOrganizacionalPorRotulo(rotulo, dataBase);

        if (componente == null) {
            return null;
        }

        return montarComponenteOrganizacionalVO(componente);

    }

    private ComponenteOrganizacionalVO montarComponenteOrganizacionalVO(ComponenteOrganizacional componente)
            throws BCConsultaInvalidaException {
        ComponenteOrganizacionalVO componenteVO = new ComponenteOrganizacionalVO();
        componenteVO.setDataFimVigencia(componente.getDataFimVigencia());
        componenteVO.setDataInicioVigencia(componente.getDataInicioVigencia());
        componenteVO.setRotulo(componente.getRotulo());
        componenteVO.setSigla(componente.getSigla());
        componenteVO.setFilhos(new ArrayList<ComponenteOrganizacionalVO>());
        if (!Util.isNuloOuVazio(componente.getComponentes())) {
            for (ComponenteOrganizacional filho : componente.getComponentes()) {
                componenteVO.getFilhos().add(montarComponenteOrganizacionalVO(filho));
            }
        }

        return componenteVO;
    }
    

}
