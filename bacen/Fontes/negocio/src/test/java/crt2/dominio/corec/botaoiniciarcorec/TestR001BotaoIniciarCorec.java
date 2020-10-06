package crt2.dominio.corec.botaoiniciarcorec;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001BotaoIniciarCorec extends ConfiguracaoTestesNegocio {

    private int ciclo;
    private int perfil;
    private Ciclo cicloInicializado;
    private PerfilRisco perfilRisco;

    public String mostrarSecao() {
        cicloInicializado = CicloMediator.get().buscarCicloPorPK(ciclo);
        perfilRisco = PerfilRiscoMediator.get().obterPerfilRiscoPorPk(perfil);
        return SimNaoEnum
                .getTipo(CicloMediator.get().exibirSecaoCorec(cicloInicializado, perfilRisco, perfilUsuario()))
                .getDescricao();
    }

    public int getCiclo() {
        return ciclo;
    }

    public void setCiclo(int ciclo) {
        this.ciclo = ciclo;
    }

    public int getPerfil() {
        return perfil;
    }

    public void setPerfil(int perfil) {
        this.perfil = perfil;
    }

}
