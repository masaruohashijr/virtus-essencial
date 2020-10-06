/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacoes proprietarias.
 */

package br.gov.bcb.sisaps.web.app;

import org.apache.wicket.markup.html.panel.Panel;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.web.page.componentes.menu.SisApsMenuItem;

/**
 * Menu do sistema. So e necessario incluir os itens que tenham restricoes de seguranca.
 */
public class AppMenu extends Panel {

    /**
     * Construtor.
     * 
     * @param id - id único do Wicket
     */
    public AppMenu(String id) {
        super(id);
        SisApsMenuItem menu = new SisApsMenuItem("menu");
        add(menu);

        addAdministrador(menu);
        addSupervisor(menu);
        addInspetor(menu);
        addComite(menu);
        addGerente(menu);
        addConsulta(menu);
        addHistorico(menu);
        addAgenda(menu);
    }

    private void addAgenda(SisApsMenuItem menu) {
        menu.addItem("menuAgenda");
    }

    private void addAdministrador(SisApsMenuItem menu) {
        menu.addItem("menuAdministrador", PerfilAcessoEnum.ADMINISTRADOR);
    }

    private void addSupervisor(SisApsMenuItem menu) {
        menu.addItem("menuSupervisor", PerfilAcessoEnum.SUPERVISOR);
    }

    private void addInspetor(SisApsMenuItem menu) {
        menu.addItem("menuInspetor", PerfilAcessoEnum.INSPETOR);
    }

    private void addComite(SisApsMenuItem menu) {
        menu.addItem("menuComite", PerfilAcessoEnum.COMITE);
        menu.addItem("menuProcessamentos", PerfilAcessoEnum.COMITE);
    }

    private void addGerente(SisApsMenuItem menu) {
        menu.addItem("menuGerente", PerfilAcessoEnum.GERENTE);
    }

    private void addConsulta(SisApsMenuItem menu) {
        menu.addItem("menuConsulta", PerfilAcessoEnum.CONSULTA_NAO_BLOQUEADOS, PerfilAcessoEnum.CONSULTA_TUDO,
                PerfilAcessoEnum.CONSULTA_RESUMO_NAO_BLOQUEADOS);
    }

    private void addHistorico(SisApsMenuItem menu) {
        menu.addItem("menuHistorico", PerfilAcessoEnum.ADMINISTRADOR, PerfilAcessoEnum.SUPERVISOR,
                PerfilAcessoEnum.INSPETOR, PerfilAcessoEnum.COMITE, PerfilAcessoEnum.CONSULTA_NAO_BLOQUEADOS,
                PerfilAcessoEnum.CONSULTA_TUDO, PerfilAcessoEnum.CONSULTA_RESUMO_NAO_BLOQUEADOS,
                PerfilAcessoEnum.GERENTE);
    }

}
