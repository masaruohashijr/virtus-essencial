package br.gov.bcb.sisaps.src.mediator;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.comum.pessoa.negocio.componenteorganizacional.ConsultaChefiaSubstitutoEventual;
import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ChefiaVO;
import br.gov.bcb.sisaps.adaptadores.pessoa.ExercicioFuncaoComissionadaVO;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dao.RegraPerfilAcessoDAO;
import br.gov.bcb.sisaps.src.dominio.RegraPerfilAcesso;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SubstitutoEventualEnum;
import br.gov.bcb.sisaps.src.validacao.RegraPerfilAcessoValidacaoCampos;
import br.gov.bcb.sisaps.src.vo.ConsultaRegraPerfilAcessoVO;
import br.gov.bcb.sisaps.src.vo.RegraPerfilAcessoVO;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;

@Service
public class RegraPerfilAcessoMediator extends
        AbstractMediatorPaginado<RegraPerfilAcessoVO, Integer, ConsultaRegraPerfilAcessoVO> {

    private static final ThreadLocal<ConcurrentHashMap<PerfilAcessoEnum, List<RegraPerfilAcessoVO>>> CACHEREGRAACESSO =
            new ThreadLocal<ConcurrentHashMap<PerfilAcessoEnum, List<RegraPerfilAcessoVO>>>() {
                @Override
                protected java.util.concurrent.ConcurrentHashMap<PerfilAcessoEnum, java.util.List<RegraPerfilAcessoVO>> initialValue() {
                    return new ConcurrentHashMap<PerfilAcessoEnum, List<RegraPerfilAcessoVO>>();
                };
            };

    @Autowired
    private RegraPerfilAcessoDAO regraPerfilAcessoDAO;

    @Override
    protected RegraPerfilAcessoDAO getDao() {
        return regraPerfilAcessoDAO;
    }

    public RegraPerfilAcesso buscarPorPk(Integer pk) {
        return regraPerfilAcessoDAO.load(pk);
    }

    public static RegraPerfilAcessoMediator get() {
        return SpringUtils.get().getBean(RegraPerfilAcessoMediator.class);
    }

    public boolean isAcessoPermitido(List<PerfilAcessoEnum> perfis) {
        for (PerfilAcessoEnum perfil : perfis) {
            if (isAcessoPermitido(perfil)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAcessoPermitido(PerfilAcessoEnum perfil) {
        UsuarioAplicacao usuario = ((UsuarioAplicacao) UsuarioCorrente.get());
        if (perfil.equals(PerfilAcessoEnum.ADMINISTRADOR) && usuario.isPossuiPerfilAdministrador()) {
            return true;
        }

        ConsultaRegraPerfilAcessoVO consulta = preencherConsultaRegraPerfilAcessoVo(perfil);
        if (CACHEREGRAACESSO.get().get(perfil) == null) {
            CACHEREGRAACESSO.get().put(perfil, consultar(consulta));
        }

        List<RegraPerfilAcessoVO> regras = CACHEREGRAACESSO.get().get(perfil);
        if (CollectionUtils.isNotEmpty(regras)) {
            for (RegraPerfilAcessoVO regra : regras) {
                if (validarRegrasAcesso(usuario, regra)) {
                    return true;
                } else if (usuario.getServidorVO().getLocalizacaoSubstituicaoPorPerfil().containsKey(perfil)) {
                	setLocalizacaoSubstituicao(usuario.getServidorVO(), regra.getPerfilAcesso(), null);
                }
            }
        }
        return false;
    }

    private boolean validarRegrasAcesso(UsuarioAplicacao usuario, RegraPerfilAcessoVO regra) {
        return verificarRegraFuncao(usuario.getServidorVO(), regra)
                && verificarRegraLocalizacao(usuario.getServidorVO(), regra)
                && verificarRegraSituacao(usuario.getServidorVO(), regra)
                && verificarRegraMatricula(usuario.getServidorVO(), regra);
    }

    private ConsultaRegraPerfilAcessoVO preencherConsultaRegraPerfilAcessoVo(PerfilAcessoEnum perfil) {
        ConsultaRegraPerfilAcessoVO consulta = new ConsultaRegraPerfilAcessoVO();
        consulta.setPaginada(false);
        consulta.setPerfilAcesso(perfil);
        return consulta;
    }

    private boolean verificarRegraLocalizacao(ServidorVO servidorVO, RegraPerfilAcessoVO regra) {
        if (regra.getLocalizacao() != null && regra.getLocalizacoesSubordinadas() != null) {
            String local = null;
            if (servidorVO.getLocalizacaoSubstituicao() == null) {
                local = servidorVO.getLocalizacao();
            } else {
                local = servidorVO.getLocalizacaoSubstituicao();
            }
            if (local == null) {
                return false;
            }

            if (regra.getLocalizacoesSubordinadas().equals(SimNaoEnum.SIM)) {
                return local.trim().startsWith(regra.getLocalizacao().trim());
            } else {
                return local.trim().equals(regra.getLocalizacao().trim());
            }
        }
        return true;
    }
    
    private void setLocalizacaoSubstituicao(
    		ServidorVO servidorVO, PerfilAcessoEnum perfilAcesso, String localizacao) {
    	servidorVO.setLocalizacaoSubstituicao(localizacao);
        servidorVO.getLocalizacaoSubstituicaoPorPerfil().put(perfilAcesso, localizacao);
    }

    private boolean verificarRegraFuncao(ServidorVO servidorVO,
            RegraPerfilAcessoVO regra) {
        if (regra.getCodigoFuncao() != null && regra.getSubstitutoEventualFuncao() != null
                && regra.getSubstitutoPrazoCerto() != null) {
            ExercicioFuncaoComissionadaVO funcao = BcPessoaAdapter.get()
                    .buscarExercicioFuncaoComissionada(servidorVO.getMatricula());
            if (funcao == null) {
                try {
                    ConsultaChefiaSubstitutoEventual consulta = new ConsultaChefiaSubstitutoEventual();
                    consulta.setMatriculaSubstitutoEventual(servidorVO.getMatricula());
                    ChefiaVO chefia = BcPessoaAdapter.get().buscarChefiaPorSubstitutoEventual(consulta);
                    if (chefia != null 
                            && chefia.getChefeTitular().getFuncao().equals(regra.getCodigoFuncao())
                            && regra.getSubstitutoEventualFuncao().equals(SubstitutoEventualEnum.SEMPRE)) {
                        ServidorVO servidorVOChefia = 
                                BcPessoaAdapter.get().buscarServidor(chefia.getChefeTitular().getMatricula());
                        setLocalizacaoSubstituicao(servidorVO, regra.getPerfilAcesso(), servidorVOChefia.getLocalizacao());
                        return true;
                    } else {
                        return false;
                    }
                } catch (Exception e) {
                    String mensagem = e.getMessage() == null ? "" : e.getMessage();
                    
                    if ("MATRICULA INEXISTENTE OU NAO POSSUI PERFIL PARA SUBSTITUICAO".equals(mensagem.trim())) {
                        return false;
                    } else {
                        
                        // O método buscarChefiaPorSubstitutoEventual() é
                        // completamente instável e não confiável,
                        // então a regra alternativa a seguir serve como
                        // contingência para os casos em que ele falhar.
                        System.out.println(">>> Erro no buscarChefiaPorSubstitutoEventual() para o usuário: "
                                        + servidorVO.getMatricula());
                        System.out.println(e.getMessage());
                    }
                }
                return verificarUsuarioLogadoEhSubstitutoEventualEmNaoExercicio(servidorVO, regra);
            } else if (verificarRegraSubstituto(regra, funcao)) {
            	// Se a função da regra é a mesma que a função em exercício
            	setLocalizacaoSubstituicao(
            			servidorVO, regra.getPerfilAcesso(), funcao.getLocalizacaoFuncaoComissionada());
                return true;
			} else if (regra.getCodigoFuncao().equals(servidorVO.getFuncao())) {
				// Se a função da regra é a mesma que a função original do usuário.
				servidorVO.getLocalizacaoSubstituicaoPorPerfil().put(regra.getPerfilAcesso(), servidorVO.getLocalizacao());
			} else {
            	return false;
            }
        }
        return true;
    }
    
    private boolean verificarUsuarioLogadoEhSubstitutoEventualEmNaoExercicio(ServidorVO servidorVO,
            RegraPerfilAcessoVO regra) {
        return regra.getSubstitutoEventualFuncao().equals(SubstitutoEventualEnum.SEMPRE)
                && servidorVO.getMatricula().equals(servidorVO.getMatriculaSubstituto())
                && regra.getCodigoFuncao().equals(servidorVO.getFuncaoChefe());
    }

    private boolean verificarRegraSubstituto(RegraPerfilAcessoVO regra, ExercicioFuncaoComissionadaVO funcao) {
        if (funcao.getCodigoFuncaoComissionada().equals(regra.getCodigoFuncao())) {
            if ("E".equals(funcao.getTipoExercicioFuncaoComissionada())) {
                return true;
            } else if ("S".equals(funcao.getTipoExercicioFuncaoComissionada())) {
                return validarRegraFuncaoSubstitutoEventual(regra);
            } else if ("P".equals(funcao.getTipoExercicioFuncaoComissionada())) {
                return validarRegraFuncaoSubstitutoPrazoCerto(regra);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean validarRegraFuncaoSubstitutoEventual(RegraPerfilAcessoVO regra) {
        boolean isValido = false;
        switch (regra.getSubstitutoEventualFuncao()) {
            case SEMPRE:
            case QUANDO_EM_EXERCICIO:
                isValido = true;
                break;
            case NUNCA:
            default:
                isValido = false;
                break;
        }
        return isValido;
    }

    //
    private boolean validarRegraFuncaoSubstitutoPrazoCerto(RegraPerfilAcessoVO regra) {
        boolean isValido = false;
        switch (regra.getSubstitutoPrazoCerto()) {
            case QUANDO_EM_EXERCICIO:
                isValido = true;
                break;
            case NUNCA:
            default:
                isValido = false;
                break;
        }
        return isValido;
    }

    private boolean verificarRegraSituacao(ServidorVO servidorVO, RegraPerfilAcessoVO regra) {
        if (regra.getSituacao() != null) {
            if (servidorVO.getSituacao() == null) {
                return false;
            }

            return regra.getSituacao().getCodigo().equals(Integer.valueOf(servidorVO.getSituacao()));
        }
        return true;
    }

    private boolean verificarRegraMatricula(ServidorVO servidorVO, RegraPerfilAcessoVO regra) {
        if (regra.getMatricula() != null) {
            return regra.getMatricula().equals(servidorVO.getMatricula());
        }
        return true;
    }

    @Transactional
    public void incluir(RegraPerfilAcesso regraPerfilAcesso) {
        new RegraPerfilAcessoValidacaoCampos(regraPerfilAcesso).validar();
        regraPerfilAcessoDAO.save(regraPerfilAcesso);
    }

    public void excluir(RegraPerfilAcesso regraPerfilAcesso) {
        regraPerfilAcessoDAO.delete(regraPerfilAcesso);
    }

    public static void resetLocalThreadRegras() {
        CACHEREGRAACESSO.remove();
    }

    public static boolean perfilSupervisor() {
        return RegraPerfilAcessoMediator.get().isAcessoPermitido(PerfilAcessoEnum.SUPERVISOR);
    }

    public static boolean perfilInspetor() {
        return RegraPerfilAcessoMediator.get().isAcessoPermitido(PerfilAcessoEnum.INSPETOR);
    }
    
    public static boolean perfilGerente() {
        return RegraPerfilAcessoMediator.get().isAcessoPermitido(PerfilAcessoEnum.GERENTE);
    }

    public static boolean perfilConsulta() {
        return RegraPerfilAcessoMediator.get().isAcessoPermitido(PerfilAcessoEnum.CONSULTA_NAO_BLOQUEADOS)
                || RegraPerfilAcessoMediator.get().isAcessoPermitido(PerfilAcessoEnum.CONSULTA_RESUMO_NAO_BLOQUEADOS)
                || RegraPerfilAcessoMediator.get().isAcessoPermitido(PerfilAcessoEnum.CONSULTA_TUDO);
    }
}
