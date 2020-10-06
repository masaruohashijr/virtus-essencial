package crt2.analisequantitativa.navegaraqtinspetor;

import org.apache.wicket.model.Model;

import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TituloTelaAnefEnum;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.UtilNavegabilidadeAQT;
import crt2.ConfiguracaoTestesWeb;

public class TestR001NavegarAQTInspetor extends ConfiguracaoTestesWeb {

    private int anef;
    private String perfilVigente;
    private AnaliseQuantitativaAQT analiseQuantitativaAQT;

    public String qualATelaDeDestino() {
        return verificarTelaDestino();
    }

    private String verificarTelaDestino() {
        boolean atual = "Sim".trim().equalsIgnoreCase(perfilVigente) ? true : false;

        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(anef);
        TituloTelaAnefEnum titulo =
                UtilNavegabilidadeAQT.buscarPaginaDeAcordoEstado(new Model<AnaliseQuantitativaAQT>(
                        analiseQuantitativaAQT), PerfilAcessoEnum.INSPETOR, atual);
        if (titulo == null) {
            return "";
        } else {
            return titulo.getDescricao();
        }
    }

    public int getAnef() {
        return anef;
    }

    public void setAnef(int anef) {
        this.anef = anef;
    }

    public String estado() {
        AnaliseQuantitativaAQTMediator.get().limpaSessao();
        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(anef);
        
        return analiseQuantitativaAQT.getEstado().getDescricao();
    }

    public String nota() {
        analiseQuantitativaAQT = AnaliseQuantitativaAQTMediator.get().buscar(anef);
        return analiseQuantitativaAQT.getNotaVigenteDescricaoValor();
    }

    public AnaliseQuantitativaAQT getAnaliseQuantitativaAQT() {
        return analiseQuantitativaAQT;
    }

    public void setAnaliseQuantitativaAQT(AnaliseQuantitativaAQT analiseQuantitativaAQT) {
        this.analiseQuantitativaAQT = analiseQuantitativaAQT;
    }

    public int getAqt1() {
        return anef;
    }

    public void setAqt1(int aqt1) {
        this.anef = aqt1;
    }

    public String getPerfilVigente() {
        return perfilVigente;
    }

    public void setPerfilVigente(String perfilVigente) {
        this.perfilVigente = perfilVigente;
    }

}
