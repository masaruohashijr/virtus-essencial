package crt2.dominio.analisequalitativa.delegararc;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.DelegacaoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001DelegarARC extends ConfiguracaoTestesNegocio {

    private static final String VAZIO = "<vazio>";
    private String msg;

    public void delegarARC(int arcPK, String matriculaServidorEquipe) {
        ServidorVO servidorEquipe = null;
        ServidorVO servidorUnidade = null;

        if (!matriculaServidorEquipe.equals(VAZIO)) {
            servidorEquipe = BcPessoaAdapter.get().buscarServidor(matriculaServidorEquipe);
        }

        AvaliacaoRiscoControle arc = AvaliacaoRiscoControleMediator.get().buscar(arcPK);
        UsuarioAplicacao usuario = (UsuarioAplicacao) (UsuarioCorrente.get());
        msg =
                DelegacaoMediator.get().incluir(DelegacaoMediator.get().isARCDelegado(arc, usuario.getMatricula()),
                        arc, servidorEquipe, servidorUnidade);
    }

    public String estado(int aqt) {
        AnaliseQuantitativaAQT analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(aqt);
        return analiseQuantitativaAQT.getEstado().getDescricao();
    }

    public String getMensagem() {
        return msg;
    }

}
