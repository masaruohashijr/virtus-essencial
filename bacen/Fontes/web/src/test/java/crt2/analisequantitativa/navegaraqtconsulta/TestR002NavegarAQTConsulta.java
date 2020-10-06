package crt2.analisequantitativa.navegaraqtconsulta;

import org.apache.wicket.model.Model;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TituloTelaAnefEnum;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.UtilNavegabilidadeAQT;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002NavegarAQTConsulta extends ConfiguracaoTestesNegocio {

    private int aqt;
    private int perfil;
    private AnaliseQuantitativaAQT analiseQuantitativaAQT;

    public String qualATelaDeDestino() {
        return verificarTelaDestino();
    }

    public String notaDoAnef() {
        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(aqt);
        return analiseQuantitativaAQT.getNotaVigenteDescricaoValor();
    }

    public String estado() {
        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(aqt);
        return analiseQuantitativaAQT.getEstado().getDescricao();
    }

    private String verificarTelaDestino() {
        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(aqt);
        TituloTelaAnefEnum titulo =
                UtilNavegabilidadeAQT.buscarPaginaDeAcordoEstado(new Model<AnaliseQuantitativaAQT>(
                        analiseQuantitativaAQT), PerfilAcessoEnum.CONSULTA_TUDO, true);
        if (titulo == null) {
            return "";
        } else {
            return titulo.getDescricao();
        }
    }

    public int getAqt() {
        return aqt;
    }

    public void setAqt(int aqt) {
        this.aqt = aqt;
    }

    public int getPerfil() {
        return perfil;
    }

    public void setPerfil(int perfil) {
        this.perfil = perfil;
    }

}
