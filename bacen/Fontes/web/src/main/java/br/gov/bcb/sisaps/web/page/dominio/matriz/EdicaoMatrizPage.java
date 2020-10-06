/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacoes proprietarias.
 */

package br.gov.bcb.sisaps.web.page.dominio.matriz;

import org.apache.wicket.authorization.Action;

import br.gov.bcb.comum.util.string.StringUtil;
import br.gov.bcb.sisaps.integracao.seguranca.PerfisAcesso;
import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAuthorizeAction;

@SuppressWarnings("serial")
@SisApsAuthorizeAction(action = Action.RENDER, roles = {PerfisAcesso.SUPERVISOR})
public class EdicaoMatrizPage extends AbstractMatrizPage {

    public EdicaoMatrizPage(Integer pkCiclo, String msgSucesso) {
        this(pkCiclo);
        if (!StringUtil.isVazioOuNulo(msgSucesso)) {
            success(msgSucesso);
        }
    }

    public EdicaoMatrizPage(Integer pkCiclo) {
        super(pkCiclo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitulo() {
        return "Edição de matriz de riscos e controles";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCodigoTela() {
        return "APSFW0205";
    }

}
