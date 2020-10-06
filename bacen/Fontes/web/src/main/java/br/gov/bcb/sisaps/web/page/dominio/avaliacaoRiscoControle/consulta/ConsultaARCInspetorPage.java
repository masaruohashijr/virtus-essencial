/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacoes proprietarias.
 */

package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.consulta;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.web.page.DefaultPageMenu;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;
import br.gov.bcb.sisaps.web.page.dominio.aqt.consulta.PainelConsultaAQTInspetor;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.pesquisa.ConsultaCicloPanel;

@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.INSPETOR})
public class ConsultaARCInspetorPage extends DefaultPageMenu {
    private static final String MENU = "Painel do inspetor";
    private final Form<?> form = new Form<Object>("formulario");
    private PerfilRisco perfilRisco = new PerfilRisco();
    @SpringBean
    private PerfilRiscoMediator perfilRiscoMediator;

    public ConsultaARCInspetorPage(Ciclo ciclo) {
        setPerfilRisco(perfilRiscoMediator.obterPerfilRiscoAtual(ciclo.getPk()));
        addComponentes();
    }

    public ConsultaARCInspetorPage(String mensagem) {
        success(mensagem);
        addComponentes();
    }

    public ConsultaARCInspetorPage() {
        addComponentes();
    }

    private void addComponentes() {
        addPainelCicloPanel();
        addPainelArcsDesignados();
        addPainelArcsDelegados();
        addPainelAQTsDesignados();
        addPainelAQTsDelegados();
        add(form);
    }

    private void addPainelCicloPanel() {
        ConsultaCicloPanel painelCicloPanel =
                new ConsultaCicloPanel("consultaCicloPanel", PerfilAcessoEnum.INSPETOR);
        form.addOrReplace(painelCicloPanel);
    }

    private void addPainelArcsDesignados() {
        PainelConsultaARCInspetor painelArcsDesignados =
                new PainelConsultaARCInspetor("idArcsDesignados", "ARCs designados", true);
        form.add(painelArcsDesignados);
    }

    private void addPainelArcsDelegados() {
        PainelConsultaARCInspetor painelArcsDelegados =
                new PainelConsultaARCInspetor("idArcsDelegados", "ARCs delegados para análise", false);
        form.add(painelArcsDelegados);
    }

    private void addPainelAQTsDesignados() {
        PainelConsultaAQTInspetor painelAQTsDesignados =
                new PainelConsultaAQTInspetor("idAQTsDesignados", "ANEFs designados", true);
        form.add(painelAQTsDesignados);
    }

    private void addPainelAQTsDelegados() {
        PainelConsultaAQTInspetor painelAQTsDelegados =
                new PainelConsultaAQTInspetor("idAQTsDelegados", "ANEFs delegados para análise", false);
        form.add(painelAQTsDelegados);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitulo() {
        return MENU;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCodigoTela() {
        return "APSFW0301";
    }

    public PerfilRisco getPerfilRisco() {
        return perfilRisco;
    }

    public void setPerfilRisco(PerfilRisco perfilRisco) {
        this.perfilRisco = perfilRisco;
    }

}
