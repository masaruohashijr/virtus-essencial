package crt2.analisequantitativa.navegaraqtsupervisor;

import org.apache.wicket.model.Model;

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TituloTelaAnefEnum;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.UtilNavegabilidadeAQT;
import crt2.ConfiguracaoTestesNegocio;

public class TestR004NavegarAQTSupervisor extends ConfiguracaoTestesNegocio {
    private int aqt1;
    private int perfilDeRisco;
    private AnaliseQuantitativaAQT analiseQuantitativaAQT;
    private PerfilRisco perfil;

    public String qualATelaDeDestino() {
        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(aqt1);

        TituloTelaAnefEnum titulo =
                UtilNavegabilidadeAQT.buscarPaginaDeAcordoEstado(new Model<AnaliseQuantitativaAQT>(
                        analiseQuantitativaAQT), PerfilAcessoEnum.SUPERVISOR, true);
        if (titulo == null) {
            return "";
        } else {
            return titulo.getDescricao();
        }
    }

    public String notaDoAnef() {
        return analiseQuantitativaAQT.getNotaVigenteDescricaoValor();
    }

    public String estado() {
        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(aqt1);
        return analiseQuantitativaAQT.getEstado().getDescricao();
    }

    public String existeDelegacaoParaOUsuario() {
        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(aqt1);
        return SimNaoEnum.getTipo(
                AnaliseQuantitativaAQTMediator.get().existeDelegacaoParaOUsuario(analiseQuantitativaAQT))
                .getDescricao();
    }

    public String acao() {
        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(aqt1);
        perfil = PerfilRiscoMediator.get().obterPerfilRiscoPorPk(perfilDeRisco);
        TituloTelaAnefEnum titulo =
                UtilNavegabilidadeAQT.buscarPaginaDeAcordoEstado(new Model<AnaliseQuantitativaAQT>(
                        analiseQuantitativaAQT), perfil, PerfilAcessoEnum.SUPERVISOR, true);
        if (titulo == null) {
            return "";
        } else {
            return titulo.getDescricao();
        }

    }

    public int getAqt1() {
        return aqt1;
    }

    public void setAqt1(int aqt1) {
        this.aqt1 = aqt1;
    }

    public int getPerfilDeRisco() {
        return perfilDeRisco;
    }

    public void setPerfilDeRisco(int perfilDeRisco) {
        this.perfilDeRisco = perfilDeRisco;
    }

    public AnaliseQuantitativaAQT getAnaliseQuantitativaAQT() {
        return analiseQuantitativaAQT;
    }

    public void setAnaliseQuantitativaAQT(AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        this.analiseQuantitativaAQT = analiseQuantitativaAQT;
    }

}
