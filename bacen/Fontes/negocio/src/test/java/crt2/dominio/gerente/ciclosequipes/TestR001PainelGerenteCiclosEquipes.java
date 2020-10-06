package crt2.dominio.gerente.ciclosequipes;

import java.util.Date;
import java.util.List;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.comum.pessoa.negocio.componenteorganizacional.ConsultaChefia;
import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ChefiaVO;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.vo.ComponenteOrganizacionalVO;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001PainelGerenteCiclosEquipes extends ConfiguracaoTestesNegocio {
    
    public List<ComponenteOrganizacionalVO> buscarEquipe() {
        UsuarioAplicacao usuario = ((UsuarioAplicacao) UsuarioCorrente.get());
        ComponenteOrganizacionalVO vo =
                BcPessoaAdapter.get().buscarComponenteOrganizacionalPorRotulo(
                        usuario.getServidorVO().getLocalizacaoAtual(PerfilAcessoEnum.GERENTE), new Date());
        return vo.getFilhos();
    }
    
    public String getNomeEquipe(ComponenteOrganizacionalVO retorno) {
        return retorno.getSigla();
    }

    public String getNomeSupervisor(ComponenteOrganizacionalVO retorno) {
        ConsultaChefia consulta = new ConsultaChefia();
        consulta.setComponenteOrganizacionalRotulo(retorno.getRotulo());
        consulta.setDataBase(new Date());
        ChefiaVO chefe = BcPessoaAdapter.get().buscarChefia(consulta);
        return  chefe.getChefeTitular().getNome();
    }
    
}
