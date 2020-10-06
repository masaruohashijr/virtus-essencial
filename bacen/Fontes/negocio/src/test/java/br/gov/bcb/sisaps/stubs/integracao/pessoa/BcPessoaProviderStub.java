package br.gov.bcb.sisaps.stubs.integracao.pessoa;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import br.gov.bcb.comum.excecoes.BCConsultaInvalidaException;
import br.gov.bcb.comum.pessoa.negocio.componenteorganizacional.ConsultaChefia;
import br.gov.bcb.comum.pessoa.negocio.componenteorganizacional.ConsultaChefiaSubstitutoEventual;
import br.gov.bcb.comum.pessoa.negocio.componenteorganizacional.ConsultaComponentesOrganizacionais;
import br.gov.bcb.comum.pessoa.negocio.funcaocomissionada.ConsultaExercicioFuncaoComissionada;
import br.gov.bcb.comum.pessoa.negocio.servidor.ConsultaServidores;
import br.gov.bcb.especificacao.spring.listener.stub.Stub;
import br.gov.bcb.sisaps.adaptadores.pessoa.ChefiaVO;
import br.gov.bcb.sisaps.adaptadores.pessoa.ExercicioFuncaoComissionadaVO;
import br.gov.bcb.sisaps.adaptadores.pessoa.FuncaoComissionadaVO;
import br.gov.bcb.sisaps.adaptadores.pessoa.IBcPessoa;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.vo.ComponenteOrganizacionalVO;

@Component(IBcPessoa.NOME)
public class BcPessoaProviderStub implements IBcPessoa, Stub {

    protected static final String DEINF = "DEINF";
    protected static final String DESUP = "DESUP";
    protected static final String DELIQ = "DELIQ";
    private static final String BARRA = "/";

    private final Map<String, ServidorVO> servidoresPorLogin = new HashMap<String, ServidorVO>();
    private final Map<String, ServidorVO> servidoresPorComponenteOrganizacional = new HashMap<String, ServidorVO>();
    private final Map<String, FuncaoComissionadaVO> funcaoComissionadaPorCodigo =
            new HashMap<String, FuncaoComissionadaVO>();
    private final Map<String, ExercicioFuncaoComissionadaVO> exercicioFuncaoComissionadaPorMatricula =
            new HashMap<String, ExercicioFuncaoComissionadaVO>();
    private final List<ComponenteOrganizacionalVO> componentesOrganizacionais =
            new ArrayList<ComponenteOrganizacionalVO>();
    private final Map<String, ChefiaVO> chefiaPorComponenteOrganizacional = new HashMap<String, ChefiaVO>();
    private final Map<String, ChefiaVO> chefiaPorSubstitutoEventual = new HashMap<String, ChefiaVO>();

    public void incluirServidor(ServidorVO servidor) {
        servidoresPorLogin.put(servidor.getLogin(), servidor);
        if (StringUtils.isNotBlank(servidor.getLocalizacao())) {
            servidoresPorComponenteOrganizacional.put(servidor.getLocalizacao(), servidor);
        }
    }

    public void incluirFuncaoComissionada(FuncaoComissionadaVO funcaoComissionadaVO) {
        funcaoComissionadaPorCodigo.put(funcaoComissionadaVO.getCodigo(), funcaoComissionadaVO);
    }

    public void incluirExercicioFuncaoComissionada(ExercicioFuncaoComissionadaVO exercicioFuncaoComissionadaVO) {
        exercicioFuncaoComissionadaPorMatricula.put(exercicioFuncaoComissionadaVO.getMatricula(),
                exercicioFuncaoComissionadaVO);
    }

    public void incluirComponenteOrganizacionalVO(ComponenteOrganizacionalVO componenteOrganizacionalVO) {
        componentesOrganizacionais.add(componenteOrganizacionalVO);
    }

    public void incluirChefiaComponenteOrganizacional(ChefiaVO chefiaVO) {
        chefiaPorComponenteOrganizacional.put(chefiaVO.getRotuloComponenteOrganizacional(), chefiaVO);
    }

    public void incluirChefiaSubstitutoEventual(String matricula, ChefiaVO chefiaVO) {
        chefiaPorSubstitutoEventual.put(matricula, chefiaVO);
    }

    
    @Override
    public void limpar() {
        servidoresPorLogin.clear();
    }

    public void limparTodos() {
        servidoresPorLogin.clear();
        servidoresPorComponenteOrganizacional.clear();
        funcaoComissionadaPorCodigo.clear();
        exercicioFuncaoComissionadaPorMatricula.clear();
        componentesOrganizacionais.clear();
        chefiaPorComponenteOrganizacional.clear();
        chefiaPorSubstitutoEventual.clear();
    }

    @Override
    public ServidorVO buscarServidor(String matricula, Date dataBase) throws BCConsultaInvalidaException {
        ServidorVO serv = null;
        List<ServidorVO> servidores = new ArrayList<ServidorVO>(servidoresPorLogin.values());
        for (ServidorVO resultado : servidores) {
            if (matricula.equals(resultado.getMatricula())) {
                return resultado;
            }
        }
        return serv;
    }

    @Override
    public ArrayList<ServidorVO> consultarServidores(ConsultaServidores consulta) throws BCConsultaInvalidaException {
        ArrayList<ServidorVO> lista = new ArrayList<ServidorVO>();
        for (ServidorVO servidor : servidoresPorLogin.values()) {
            for (String rolutoComponenteOrganizacional : consulta.getComponenteOrganizacionalRotulos()) {
                if (servidor.getUnidade().equals(rolutoComponenteOrganizacional)
                        || (servidor.getLocalizacao() != null && servidor.getLocalizacao().equals(
                                rolutoComponenteOrganizacional))) {
                    lista.add(servidor);
                }
            }
        }
        return lista;
    }

    @Override
    public ArrayList<ComponenteOrganizacionalVO> consultarComponentesOrganizacionais(
            ConsultaComponentesOrganizacionais consulta) throws BCConsultaInvalidaException {
        ArrayList<ComponenteOrganizacionalVO> componente = new ArrayList<ComponenteOrganizacionalVO>();

        for (ComponenteOrganizacionalVO filho : componentesOrganizacionais) {
            if (filho.getRotulo().contains(consulta.getRotuloInicio() + "/")) {
                componente.add(filho);
            }
        }

        return componente;

    }

    private List<ComponenteOrganizacionalVO> montarVODeinf() {

        ArrayList<ComponenteOrganizacionalVO> lista = new ArrayList<ComponenteOrganizacionalVO>();

        ComponenteOrganizacionalVO deinf = new ComponenteOrganizacionalVO();
        String sigla = "DIREC";
        deinf.setSigla(sigla);
        deinf.setRotulo(DEINF + BARRA + sigla);
        lista.add(deinf);

        ComponenteOrganizacionalVO gabin = new ComponenteOrganizacionalVO();
        String sigla2 = "GABIN";
        gabin.setSigla(sigla2);
        gabin.setRotulo(DEINF + BARRA + sigla2);
        lista.add(gabin);

        ComponenteOrganizacionalVO gedeg = new ComponenteOrganizacionalVO();
        String sigla3 = "GEDEG";
        gedeg.setSigla(sigla3);
        gedeg.setRotulo(DEINF + BARRA + sigla3);
        lista.add(gedeg);

        return lista;
    }

    private List<ComponenteOrganizacionalVO> montarVODeliq() {

        ArrayList<ComponenteOrganizacionalVO> lista = deliqCompleto();

        ComponenteOrganizacionalVO gabin = new ComponenteOrganizacionalVO();
        String sigla2 = "GLIQ2";
        gabin.setSigla(sigla2);
        gabin.setRotulo(DELIQ + BARRA + sigla2);
        lista.add(gabin);

        ComponenteOrganizacionalVO gedeg = new ComponenteOrganizacionalVO();
        String sigla3 = "GLIQ3";
        gedeg.setSigla(sigla3);
        gedeg.setRotulo(DELIQ + BARRA + sigla3);
        lista.add(gedeg);

        return lista;
    }

    private ArrayList<ComponenteOrganizacionalVO> deliqCompleto() {
        ArrayList<ComponenteOrganizacionalVO> lista = new ArrayList<ComponenteOrganizacionalVO>();
        String siglagl = "GLIQ1";
        String siglagt = "GTBHO";
        String siglacosup = "COSUP-01";

        ArrayList<ComponenteOrganizacionalVO> listagcosup1 = new ArrayList<ComponenteOrganizacionalVO>();
        ComponenteOrganizacionalVO deliqcosup = new ComponenteOrganizacionalVO();

        deliqcosup.setSigla(siglacosup);
        deliqcosup.setRotulo(DELIQ + BARRA + siglagl + BARRA + siglagt + BARRA + siglacosup);
        listagcosup1.add(deliqcosup);

        ArrayList<ComponenteOrganizacionalVO> listagliq = new ArrayList<ComponenteOrganizacionalVO>();
        ComponenteOrganizacionalVO deliqgtbho = new ComponenteOrganizacionalVO();

        deliqgtbho.setSigla(siglagt);
        deliqgtbho.setRotulo(DELIQ + BARRA + siglagl + BARRA + siglagt);
        deliqgtbho.setFilhos(listagcosup1);
        listagliq.add(deliqgtbho);

        ComponenteOrganizacionalVO deliq = new ComponenteOrganizacionalVO();

        deliq.setSigla(siglagl);
        deliq.setRotulo(DELIQ + BARRA + siglagl);
        deliq.setFilhos(listagliq);
        lista.add(deliq);
        return lista;
    }

    private List<ComponenteOrganizacionalVO> montarVODesup() {

        ArrayList<ComponenteOrganizacionalVO> lista = new ArrayList<ComponenteOrganizacionalVO>();

        ComponenteOrganizacionalVO deinf = new ComponenteOrganizacionalVO();
        String sigla = "TESTE1";
        deinf.setSigla(sigla);
        deinf.setRotulo(DESUP + BARRA + sigla);
        lista.add(deinf);

        ComponenteOrganizacionalVO gabin = new ComponenteOrganizacionalVO();
        String sigla2 = "TESTE2";
        gabin.setSigla(sigla2);
        gabin.setRotulo(DESUP + BARRA + sigla2);
        lista.add(gabin);

        ComponenteOrganizacionalVO gedeg = new ComponenteOrganizacionalVO();
        String sigla3 = "TESTE3";
        gedeg.setSigla(sigla3);
        gedeg.setRotulo(DESUP + BARRA + sigla3);
        lista.add(gedeg);

        return lista;
    }

    @Override
    public ServidorVO buscarServidorPorLogin(String login, Date dataBase) throws BCConsultaInvalidaException {
        validarLogin(login);
        ServidorVO serv = servidoresPorLogin.get(login);
        return serv;
    }

    @Override
    public ChefiaVO buscarChefia(ConsultaChefia consulta) throws BCConsultaInvalidaException {
        return chefiaPorComponenteOrganizacional.get(consulta.getComponenteOrganizacionalRotulo());
    }

    @Override
    public ChefiaVO buscarChefiaPorSubstitutoEventual(ConsultaChefiaSubstitutoEventual consulta)
            throws BCConsultaInvalidaException {
        return chefiaPorSubstitutoEventual.get(consulta.getMatriculaSubstitutoEventual());
    }

    private void validarLogin(String login) throws BCConsultaInvalidaException {
        // assumindo no mock que isso vai acontecer sempre para poder testar mensagem do ADAPTER.
        //        if (login != null && !"a.a".equalsIgnoreCase(login)) {
        //            Validador.validarFormatoLogin(login);
        if (login != null && login.length() < 5 && !"a.a".equalsIgnoreCase(login)) {
            throw new BCConsultaInvalidaException("Formato de login inválido");
        }
    }

    @Override
    public ArrayList<ComponenteOrganizacionalVO> consultarUnidadesAtivas() throws BCConsultaInvalidaException {
        ArrayList<ComponenteOrganizacionalVO> lista = new ArrayList<ComponenteOrganizacionalVO>();

        ComponenteOrganizacionalVO deinf = new ComponenteOrganizacionalVO();
        deinf.setSigla(DEINF);
        deinf.setRotulo(DEINF);
        deinf.setFilhos(montarVODeinf());
        lista.add(deinf);

        ComponenteOrganizacionalVO deliq = new ComponenteOrganizacionalVO();
        deliq.setSigla(DELIQ);
        deliq.setRotulo(DELIQ);
        deliq.setFilhos(montarVODeliq());
        lista.add(deliq);

        ComponenteOrganizacionalVO desup = new ComponenteOrganizacionalVO();
        desup.setSigla(DESUP);
        desup.setRotulo(DESUP);
        desup.setFilhos(montarVODesup());
        lista.add(desup);

        return lista;
    }

    @Override
    public ExercicioFuncaoComissionadaVO buscarExercicioFuncaoComissionada(ConsultaExercicioFuncaoComissionada consulta)
            throws BCConsultaInvalidaException {
        ExercicioFuncaoComissionadaVO exercicioFuncaoComissionadaVO =
                exercicioFuncaoComissionadaPorMatricula.get(consulta.getMatricula());
        return exercicioFuncaoComissionadaVO;
    }

    @Override
    public ArrayList<FuncaoComissionadaVO> consultarTodasFuncoesComissionadas() throws BCConsultaInvalidaException {
        ArrayList<FuncaoComissionadaVO> funcoes = new ArrayList<FuncaoComissionadaVO>();
        funcoes.addAll(funcaoComissionadaPorCodigo.values());
        return funcoes;
    }

    @Override
    public ComponenteOrganizacionalVO buscarComponenteOrganizacionalPorRotulo(String rotulo, Date dataBase)
            throws BCConsultaInvalidaException {
        for (ComponenteOrganizacionalVO componente : componentesOrganizacionais) {
            if (rotulo.equals(componente.getRotulo())) {
                componente.setFilhos(new ArrayList<ComponenteOrganizacionalVO>());
                for (ComponenteOrganizacionalVO filho : componentesOrganizacionais) {
                    if (filho.getRotulo().contains(componente.getRotulo() + "/")) {
                        componente.getFilhos().add(filho);
                    }
                }

                return componente;
            }
        }
        return null;
    }

}
