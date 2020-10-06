package br.gov.bcb.sisaps.web.page.componentes.autorizacao;

import org.apache.wicket.authroles.authorization.strategies.role.IRoleCheckingStrategy;
import org.apache.wicket.authroles.authorization.strategies.role.RoleAuthorizationStrategy;

import br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao.SisApsAnnotationsRoleAuthorizationStrategy;

public class SisApsRoleAuthorizationStrategy extends RoleAuthorizationStrategy {

    public SisApsRoleAuthorizationStrategy(final IRoleCheckingStrategy roleCheckingStrategy) {
        super(roleCheckingStrategy);
        add(new SisApsAnnotationsRoleAuthorizationStrategy(roleCheckingStrategy));
    }

}
