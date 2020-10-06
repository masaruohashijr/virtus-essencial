package crt2.dominio.analisequalitativa.designararc;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.mediator.AtividadeMediator;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.DesignacaoMediator;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001DesignarARC extends ConfiguracaoTestesNegocio {
    private static final String VAZIO = "<vazio>";
    private String msg;

    public void designarARC(int arcPK, String matriculaServidorEquipe) {
        designarARC(arcPK, matriculaServidorEquipe, null);
    }
    
    public void designarOutroARC(int arcPK, String matriculaServidorOutro) {
        designarARC(arcPK, null, matriculaServidorOutro);
    }
  

    public void designarARC(int arcPK, String matriculaServidorEquipe, String matriculaServidor) {
        ServidorVO servidorEquipe = null;
        ServidorVO servidorUnidade = null;

        if (matriculaServidorEquipe != null && !matriculaServidorEquipe.equals(VAZIO)) {
            servidorEquipe = BcPessoaAdapter.get().buscarServidor(matriculaServidorEquipe);
        }

        if (matriculaServidor != null && !matriculaServidor.equals(VAZIO)) {
            servidorUnidade = BcPessoaAdapter.get().buscarServidor(matriculaServidor);
        }

        AvaliacaoRiscoControle arc = AvaliacaoRiscoControleMediator.get().buscar(arcPK);

        UsuarioAplicacao usuario = (UsuarioAplicacao) (UsuarioCorrente.get());
        msg =
                DesignacaoMediator.get().incluir(usuario.getMatricula(), AtividadeMediator.get().loadPK(6), arc,
                        servidorEquipe, servidorUnidade, false);

    }

    public String getMensagem() {
        return msg;
    }

}
