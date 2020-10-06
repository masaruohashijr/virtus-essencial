package crt2.dominio.perfildeacesso.test;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import br.gov.bcb.sisaps.src.dominio.RegraPerfilAcesso;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SituacaoFuncionalEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SubstitutoEventualEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SubstitutoPrazoCertoEnum;
import br.gov.bcb.sisaps.src.mediator.RegraPerfilAcessoMediator;
import br.gov.bcb.sisaps.src.vo.ConsultaRegraPerfilAcessoVO;
import br.gov.bcb.sisaps.src.vo.RegraPerfilAcessoVO;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestAPSFW0502_Gestao_de_perfil_de_acesso extends ConfiguracaoTestesNegocio {

    @Autowired
    private RegraPerfilAcessoMediator regraPerfilAcessoMediator;

    public List<RegraPerfilAcessoVO> consultarRegras(String loginUsuarioLogado, Long matriculaUsuarioLogado,
            Long perfilAcesso) {
        logar(loginUsuarioLogado, matriculaUsuarioLogado.toString());
        ConsultaRegraPerfilAcessoVO consulta = new ConsultaRegraPerfilAcessoVO();
        consulta.setPerfilAcesso(PerfilAcessoEnum.valueOfCodigo(perfilAcesso.toString()));
        return regraPerfilAcessoMediator.consultar(consulta);
    }

    public void excluirRegra(String loginUsuarioLogado, String matriculaUsuarioLogado, String idRegra) {
        logar(loginUsuarioLogado, matriculaUsuarioLogado.toString());
        RegraPerfilAcesso regra = regraPerfilAcessoMediator.buscarPorPk(Integer.valueOf(idRegra));
        regraPerfilAcessoMediator.excluir(regra);
    }

    public void incluirRegra(String loginUsuarioLogado, Long matriculaUsuarioLogado, String funcao,
            String substitutoEventual, String substitutoPrazoCerto, Long perfilAcesso) {
        logar(loginUsuarioLogado, matriculaUsuarioLogado.toString());
        RegraPerfilAcesso novaRegra = new RegraPerfilAcesso();
        novaRegra.setPerfilAcesso(PerfilAcessoEnum.valueOfCodigo(perfilAcesso.toString()));
        novaRegra.setCodigoFuncao(funcao);
        novaRegra.setSubstitutoEventualFuncao(SubstitutoEventualEnum.valueOfCodigo(substitutoEventual));
        novaRegra.setSubstitutoPrazoCerto(SubstitutoPrazoCertoEnum.valueOfCodigo(substitutoPrazoCerto));
        incluir(novaRegra);
    }

    public void incluirRegra(String loginUsuarioLogado, Long matriculaUsuarioLogado, String perfilAcesso,
            String localizacao, String localizacoesSubordinadas, String funcao, String situacao,
            String substitutoEventual, String substitutoPrazoCerto, String matricula) {
        logar(loginUsuarioLogado, matriculaUsuarioLogado.toString());
        RegraPerfilAcesso novaRegra = new RegraPerfilAcesso();
        novaRegra.setPerfilAcesso(PerfilAcessoEnum.valueOfCodigo(perfilAcesso.toString()));
        novaRegra.setLocalizacao(localizacao);
        novaRegra.setLocalizacoesSubordinadas(SimNaoEnum.getTipo(Boolean.valueOf(localizacoesSubordinadas)));
        novaRegra.setCodigoFuncao(funcao);
        novaRegra.setSituacao(SituacaoFuncionalEnum.obterSituacao(Integer.valueOf(situacao)));
        novaRegra.setSubstitutoEventualFuncao(SubstitutoEventualEnum.valueOfCodigo(substitutoEventual));
        novaRegra.setSubstitutoPrazoCerto(SubstitutoPrazoCertoEnum.valueOfCodigo(substitutoPrazoCerto));
        novaRegra.setMatricula(matricula);
        incluir(novaRegra);
    }

    public void incluirRegraLocalizacao(String loginUsuarioLogado, Long matriculaUsuarioLogado, String localizacao,
            Long perfilAcesso) {
        logar(loginUsuarioLogado, matriculaUsuarioLogado.toString());
        RegraPerfilAcesso novaRegra = new RegraPerfilAcesso();
        novaRegra.setPerfilAcesso(PerfilAcessoEnum.valueOfCodigo(perfilAcesso.toString()));
        novaRegra.setLocalizacao(localizacao);
        incluir(novaRegra);
    }

    public void incluirRegraFuncao(String loginUsuarioLogado, Long matriculaUsuarioLogado, String funcao,
            Long perfilAcesso) {
        logar(loginUsuarioLogado, matriculaUsuarioLogado.toString());
        RegraPerfilAcesso novaRegra = new RegraPerfilAcesso();
        novaRegra.setPerfilAcesso(PerfilAcessoEnum.valueOfCodigo(perfilAcesso.toString()));
        novaRegra.setCodigoFuncao(funcao);
        incluir(novaRegra);
    }

    public void incluirRegraSemValores(String loginUsuarioLogado, Long matriculaUsuarioLogado, Long perfilAcesso) {
        logar(loginUsuarioLogado, matriculaUsuarioLogado.toString());
        RegraPerfilAcesso novaRegra = new RegraPerfilAcesso();
        novaRegra.setPerfilAcesso(PerfilAcessoEnum.valueOfCodigo(perfilAcesso.toString()));
        incluir(novaRegra);
    }

    private void incluir(RegraPerfilAcesso novaRegra) {
        erro = null;
        try {
            regraPerfilAcessoMediator.incluir(novaRegra);
        } catch (NegocioException e) {
            erro = e;
        }
    }

    public String getLocalizacao(RegraPerfilAcessoVO regraPerfilAcessoVO) {
        return regraPerfilAcessoVO.getLocalizacao();
    }

    public String getLocalizacoesSubordinadas(RegraPerfilAcessoVO regraPerfilAcessoVO) {
        return regraPerfilAcessoVO.getDescricaoLocalizacoesSubordinadas();
    }

    public String getFuncao(RegraPerfilAcessoVO regraPerfilAcessoVO) {
        return regraPerfilAcessoVO.getCodigoFuncao();
    }

    public String getSituacao(RegraPerfilAcessoVO regraPerfilAcessoVO) {
        return regraPerfilAcessoVO.getSituacao().getCodigo().toString();
    }

    public String getSubstitutoEventual(RegraPerfilAcessoVO regraPerfilAcessoVO) {
        if (regraPerfilAcessoVO.getSubstitutoEventualFuncao() == null) {
            return "";
        } else {
            return regraPerfilAcessoVO.getSubstitutoEventualFuncao().getDescricao();
        }
    }

    public String getSubstitutoPrazoCerto(RegraPerfilAcessoVO regraPerfilAcessoVO) {
        if (regraPerfilAcessoVO.getSubstitutoPrazoCerto() == null) {
            return "";
        } else {
            return regraPerfilAcessoVO.getSubstitutoPrazoCerto().getDescricao();
        }
    }

    public String getMatricula(RegraPerfilAcessoVO regraPerfilAcessoVO) {
        return regraPerfilAcessoVO.getMatricula();
    }

}
