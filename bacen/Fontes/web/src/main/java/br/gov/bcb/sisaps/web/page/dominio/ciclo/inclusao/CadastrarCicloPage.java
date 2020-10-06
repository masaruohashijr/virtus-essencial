/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacoes proprietarias.
 */

package br.gov.bcb.sisaps.web.page.dominio.ciclo.inclusao;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;
import br.gov.bcb.sisaps.web.page.componentes.botoes.CustomButton;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.PainelEdicaoDadosCiclo;

@SuppressWarnings("serial")
@SisApsAuthorizeAction(action = Action.RENDER, roles = { PerfisAcesso.SUPERVISOR })
public class CadastrarCicloPage extends DefaultPage {

    private Form<Ciclo> form;
    private final Ciclo ciclo = new Ciclo();
    @SpringBean
    private CicloMediator cicloMediator;

    public CadastrarCicloPage() {
        addComponentes();
    }

    private void addComponentes() {
        CompoundPropertyModel<Ciclo> cicloModel = new CompoundPropertyModel<Ciclo>(ciclo);
        form = new Form<Ciclo>("formulario", cicloModel);
        PainelEdicaoDadosCiclo painelEdicao = new PainelEdicaoDadosCiclo(
                "idPainelEdicao", ciclo);
        form.addOrReplace(painelEdicao);
        addButtons();
        add(form);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitulo() {
        return "Inclusão de Ciclo";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCodigoTela() {
        return "APSFW0106";
    }

    private void addButtons() {
        form.add(botaoIncluir());
        form.add(new LinkVoltar());
    }

    private Button botaoIncluir() {
        return new CustomButton("bttIncluir") {

            @Override
            public void executeSubmit() {
                cicloMediator.incluir(ciclo, ciclo.getDataInicioFormatada(), false);
                avancarParaNovaPagina(new ConfirmarCadastrarCicloPage(ciclo));

            }
        };
    }
}
