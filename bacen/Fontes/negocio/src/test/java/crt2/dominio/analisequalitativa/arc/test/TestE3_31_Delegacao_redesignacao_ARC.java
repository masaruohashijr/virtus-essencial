package crt2.dominio.analisequalitativa.arc.test;

import org.springframework.beans.factory.annotation.Autowired;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.mediator.AtividadeMediator;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.DelegacaoMediator;
import br.gov.bcb.sisaps.src.mediator.DesignacaoMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestE3_31_Delegacao_redesignacao_ARC extends ConfiguracaoTestesNegocio {

    @Autowired
    private DelegacaoMediator delegacaoMediator;

    @Autowired
    private DesignacaoMediator designacaoMediator;

    @Autowired
    private AvaliacaoRiscoControleMediator avaliacaoRiscoControleMediator;

    public void delegarArc(int idArc, String matriculaServidor, String matriculaServidorUnidade) {
        delegarOuDesignar(null, avaliacaoRiscoControleMediator.loadPK(idArc),
                BcPessoaAdapter.get().buscarServidor(matriculaServidor),
                BcPessoaAdapter.get().buscarServidor(matriculaServidorUnidade), true);
    }

    public void designarArc(int idArc, String matriculaServidor, String matriculaServidorUnidade) {
        delegarOuDesignar(null, avaliacaoRiscoControleMediator.loadPK(idArc),
                BcPessoaAdapter.get().buscarServidor(matriculaServidor),
                BcPessoaAdapter.get().buscarServidor(matriculaServidorUnidade), false);
    }

    private void delegarOuDesignar(Matriz matriz, AvaliacaoRiscoControle avaliacaoRiscoControle,
            ServidorVO servidorEquipe, ServidorVO servidorUnidade, boolean isDelegar) {
        erro = null;
        try {
            if (isDelegar) {
                delegacaoMediator.incluir(
                        DelegacaoMediator.get().isARCDelegado(avaliacaoRiscoControle,
                                ((UsuarioAplicacao) UsuarioCorrente.get()).getMatricula()),
                        avaliacaoRiscoControle, servidorEquipe, servidorUnidade);
            } else {
                designacaoMediator.incluir(((UsuarioAplicacao) UsuarioCorrente.get()).getMatricula(),
                        AtividadeMediator.get().loadPK(1), avaliacaoRiscoControle, servidorEquipe, servidorUnidade,
                        false);
            }
        } catch (NegocioException e) {
            erro = e;
        }
    }
}
