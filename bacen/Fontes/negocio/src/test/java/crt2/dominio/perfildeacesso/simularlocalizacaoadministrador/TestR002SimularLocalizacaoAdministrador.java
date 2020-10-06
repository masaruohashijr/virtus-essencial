package crt2.dominio.perfildeacesso.simularlocalizacaoadministrador;

import java.util.Arrays;
import java.util.List;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.ServidorVOMediator;
import br.gov.bcb.sisaps.src.vo.CicloVO;
import br.gov.bcb.sisaps.src.vo.ConsultaCicloVO;
import br.gov.bcb.sisaps.util.consulta.Ordenacao;

public class TestR002SimularLocalizacaoAdministrador extends TestR001SimularLocalizacaoAdministrador {

    public List<CicloVO> consultaPainelInspetor() {
        UsuarioAplicacao usuario = (UsuarioAplicacao) (UsuarioCorrente.get());

        ConsultaCicloVO consulta = new ConsultaCicloVO();
        consulta.setPerfil(PerfilAcessoEnum.INSPETOR);
        consulta.setEstados(Arrays.asList(EstadoCicloEnum.EM_ANDAMENTO, EstadoCicloEnum.COREC));
        consulta.setRotuloLocalizacao(usuario.getServidorVO().getLocalizacaoAtual());
        Ordenacao ordenacao = new Ordenacao();
        ordenacao.setPropriedade("entidadeSupervisionavel.nome");
        ordenacao.setCrescente(true);
        consulta.setOrdenacao(ordenacao);
        return CicloMediator.get().consultarSemValidacao(consulta, false);
    }

    public String alterarLocalizacaoSimulada(String localizacao) {
        UsuarioAplicacao usuario = (UsuarioAplicacao) (UsuarioCorrente.get());
        return ServidorVOMediator.get().alterarLocalizacaoSimulada(usuario.getServidorVO(), localizacao);

    }

    public String cancelarLocalizacaoSimulada() {
        UsuarioAplicacao usuario = (UsuarioAplicacao) (UsuarioCorrente.get());
        return ServidorVOMediator.get().cancelarAlterarLocalizacaoSimulada(usuario.getServidorVO());
    }

    public String getES(CicloVO ciclo) {
        return ciclo.getEntidadeSupervisionavel().getNome();
    }

    public String getDataInicio(CicloVO ciclo) {
        return ciclo.getDataInicioFormatada();
    }

    public String getDataPrevisaoCorec(CicloVO ciclo) {
        return ciclo.getDataPrevisaoFormatada();
    }

    public String getEstado(CicloVO ciclo) {
        return ciclo.getEstadoCiclo().getEstado().getDescricao();
    }

}
