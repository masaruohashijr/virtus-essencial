package crt2.dominio.analisequalitativa.designararclote;

import java.util.ArrayList;
import java.util.List;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Designacao;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CelulaRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.DesignacaoMediator;
import br.gov.bcb.sisaps.src.vo.ARCDesignacaoVO;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001DesignarARCLote extends ConfiguracaoTestesNegocio {

    private static final String VAZIO = "<vazio>";
    private String msg;

    public void designarARCLote(int arcPK1, int arcPK2, int arcPK3, int arcPK4, int arcPK5,
            String matriculaServidorEquipe) {

        ServidorVO servidorEquipe = null;
        UsuarioAplicacao usuario = (UsuarioAplicacao) (UsuarioCorrente.get());

        if (matriculaServidorEquipe != null && !matriculaServidorEquipe.equals(VAZIO)) {
            servidorEquipe = BcPessoaAdapter.get().buscarServidor(matriculaServidorEquipe);
        }


        AvaliacaoRiscoControle arc1 = AvaliacaoRiscoControleMediator.get().buscar(arcPK1);
        AvaliacaoRiscoControle arc2 = AvaliacaoRiscoControleMediator.get().buscar(arcPK2);
        AvaliacaoRiscoControle arc3 = AvaliacaoRiscoControleMediator.get().buscar(arcPK3);
        AvaliacaoRiscoControle arc4 = AvaliacaoRiscoControleMediator.get().buscar(arcPK4);
        AvaliacaoRiscoControle arc5 = AvaliacaoRiscoControleMediator.get().buscar(arcPK5);
        
        List<ARCDesignacaoVO> designacoes = new ArrayList<ARCDesignacaoVO>();
        
        addArc(arc1, designacoes);
        addArc(arc2, designacoes);
        addArc(arc3, designacoes);
        addArc(arc4, designacoes);
        addArc(arc5, designacoes);
        
        try {
            DesignacaoMediator.get().incluir(usuario.getMatricula(), designacoes, servidorEquipe, null, false);
            msg = "ARC(s) designado(s) com sucesso.";
        } catch (Exception e) {
            msg = e.getMessage();
        }
    }


    private void addArc(AvaliacaoRiscoControle arc, List<ARCDesignacaoVO> designacoes) {
        Atividade atividade = null;
        ARCDesignacaoVO designacaoVO = new ARCDesignacaoVO(arc.getPk());
        Designacao designacao = DesignacaoMediator.get().buscarDesignacaoPorARC(arc.getPk());
        CelulaRiscoControle celula = CelulaRiscoControleMediator.get().buscarCelularPorAvaliacao(arc);
        if (celula != null) {
            atividade = celula.getAtividade();
        }
        if (designacao != null) {
            designacaoVO.setPkDesignacao(designacao.getPk());
        }
        if (atividade != null) {
            designacaoVO.setPkAtividade(atividade.getPk());
        }
        designacoes.add(designacaoVO);
    }


    public String getMensagem() {
        return msg;
    }

}
