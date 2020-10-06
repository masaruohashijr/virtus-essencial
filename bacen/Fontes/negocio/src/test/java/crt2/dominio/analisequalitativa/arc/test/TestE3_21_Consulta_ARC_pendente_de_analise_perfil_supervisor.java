package crt2.dominio.analisequalitativa.arc.test;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.vo.AvaliacaoRiscoControleVO;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestE3_21_Consulta_ARC_pendente_de_analise_perfil_supervisor extends ConfiguracaoTestesNegocio {

    @Autowired
    private AvaliacaoRiscoControleMediator avaliacaoRiscoControleMediator;

    public void detalharARC(String idARC) {
        Integer pkARC = null;
        if (StringUtils.isNotEmpty(idARC)) {
            pkARC = Integer.valueOf(idARC);
        }
        detalhar(pkARC);
    }

    private void detalhar(Integer pkARC) {
        erro = null;
        try {
            avaliacaoRiscoControleMediator.buscarPorPk(pkARC);
        } catch (NegocioException e) {
            erro = e;
        }
    }

    //    public List<AvaliacaoRiscoControleVO> consultaPainelSupervisor(String loginUsuarioLogado,
    //            Long matriculaUsuarioLogado) {
    //        logar(loginUsuarioLogado, matriculaUsuarioLogado.toString());
    //        return avaliacaoRiscoControleMediator.consultaPainelSupervisor();
    //    }
    //
    //    public List<AvaliacaoRiscoControleVO> consultaPainelSupervisor(String loginUsuarioLogado,
    //            Long matriculaUsuarioLogado, String localizacao) {
    //        logar(loginUsuarioLogado, matriculaUsuarioLogado.toString(), localizacao);
    //        return avaliacaoRiscoControleMediator.consultaPainelSupervisor();
    //    }

    public String getES(AvaliacaoRiscoControleVO arcVO) {
        return arcVO.getMatrizVigente().getCiclo().getEntidadeSupervisionavel().getNome();
    }

    public String getAtividade(AvaliacaoRiscoControleVO arcVO) {
        return arcVO.getAtividade().getNome();
    }

    public String getGrupo(AvaliacaoRiscoControleVO arcVO) {
        return arcVO.getParametroGrupoRiscoControle().getNome();
    }

    public String getRC(AvaliacaoRiscoControleVO arcVO) {
        return arcVO.getTipo().getAbreviacao();
    }

    public String getEstado(AvaliacaoRiscoControleVO arcVO) {
        return arcVO.getEstado().getDescricao();
    }
}
