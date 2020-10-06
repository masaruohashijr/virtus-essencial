package crt2.dominio.analisequalitativa.listararcsintese;

import java.util.List;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.EntidadeSupervisionavelMediator;
import br.gov.bcb.sisaps.src.vo.SinteseRiscoRevisaoVO;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001ListarARCRevisaoSupervisor extends ConfiguracaoTestesNegocio{

    public List<SinteseRiscoRevisaoVO> consultaListaSinteses(String loginUsuarioLogado) {
        logar(loginUsuarioLogado);
        UsuarioAplicacao usuario = ((UsuarioAplicacao) UsuarioCorrente.get());
        return EntidadeSupervisionavelMediator.get().buscarSintesesRiscoRevisao(
                usuario.getServidorVO().getLocalizacaoAtual(PerfilAcessoEnum.SUPERVISOR));
    }

    public String getEs(SinteseRiscoRevisaoVO sintese) {
        return sintese.getNomeEntidadeSupervisionavel();
    }

    public Integer getID(SinteseRiscoRevisaoVO sintese) {
        return sintese.getPkArc();
    }

    public String getSintese(SinteseRiscoRevisaoVO sintese) {
        return sintese.getNomeParametroGrupoRiscoControle();
    }

    public String getQtdArcs(SinteseRiscoRevisaoVO sintese) {
        return sintese.getArcsPendentesPublicacao().toString();
    }
 
    
}
