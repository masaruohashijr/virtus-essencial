package crt2.dominio.perfildeacesso.simularlocalizacaoadministrador;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.mediator.ServidorVOMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;

public class TestR003SimularLocalizacaoAdministrador extends TestR001SimularLocalizacaoAdministrador {
    
    private String localizacao4;

    public String alterarLocalizacaoSimulada(String localizacao) {
        try {
            UsuarioAplicacao usuario = (UsuarioAplicacao) (UsuarioCorrente.get());
            return ServidorVOMediator.get().alterarLocalizacaoSimulada(usuario.getServidorVO(), localizacao);
        } catch (NegocioException e) {
            erro = e;
        }
        return erro == null ? "" : erro.getMessage();
    }

    public String cancelarLocalizacaoSimulada() {
        UsuarioAplicacao usuario = (UsuarioAplicacao) (UsuarioCorrente.get());
        return ServidorVOMediator.get().cancelarAlterarLocalizacaoSimulada(usuario.getServidorVO());
    }
    
    
    public String mensagemEsperadaAoSalvar() {
        return alterarLocalizacaoSimulada(localizacao4);
    }

    public String getLocalizacao4() {
        return localizacao4;
    }

    public void setLocalizacao4(String localizacao4) {
        this.localizacao4 = localizacao4;
    }
    
    
    

}
