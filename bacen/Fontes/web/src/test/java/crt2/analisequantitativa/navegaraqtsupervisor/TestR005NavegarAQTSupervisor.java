package crt2.analisequantitativa.navegaraqtsupervisor;

import org.apache.wicket.model.Model;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TituloTelaAnefEnum;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.UtilNavegabilidadeAQT;
import crt2.ConfiguracaoTestesNegocio;

public class TestR005NavegarAQTSupervisor extends ConfiguracaoTestesNegocio {
    private int anef;
    private AnaliseQuantitativaAQT analiseQuantitativaAQT;

    public String qualATelaDeDestino() {
        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(anef);
        TituloTelaAnefEnum titulo =
                UtilNavegabilidadeAQT.buscarPaginaDeAcordoEstado(new Model<AnaliseQuantitativaAQT>(
                        analiseQuantitativaAQT), PerfilAcessoEnum.SUPERVISOR, true);
        if (titulo == null) {
            return "";
        } else {
            return titulo.getDescricao();
        }
    }

    public String nota() {
        return analiseQuantitativaAQT.getNotaVigenteDescricaoValor();
    }

    public String estado() {
        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(anef);
        return analiseQuantitativaAQT.getEstado().getDescricao();
    }

    public int getAnef() {
        return anef;
    }

    public void setAnef(int anef) {
        this.anef = anef;
    }

    public AnaliseQuantitativaAQT getAnaliseQuantitativaAQT() {
        return analiseQuantitativaAQT;
    }

    public void setAnaliseQuantitativaAQT(AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        this.analiseQuantitativaAQT = analiseQuantitativaAQT;
    }

}
