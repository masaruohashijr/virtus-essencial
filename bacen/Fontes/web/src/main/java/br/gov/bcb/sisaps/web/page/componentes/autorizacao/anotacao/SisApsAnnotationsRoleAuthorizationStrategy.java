package br.gov.bcb.sisaps.web.page.componentes.autorizacao.anotacao;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.IRoleCheckingStrategy;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AnnotationsRoleAuthorizationStrategy;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.RegraPerfilAcessoMediator;

public class SisApsAnnotationsRoleAuthorizationStrategy extends AnnotationsRoleAuthorizationStrategy {
    
    public SisApsAnnotationsRoleAuthorizationStrategy(IRoleCheckingStrategy roleCheckingStrategy) {
        super(roleCheckingStrategy);
    }

    @Override
    protected boolean isActionAuthorized(Class<?> componentClass, Action action) {
        if (!validar(action, componentClass.getAnnotation(SisApsAuthorizeAction.class))) {
            return false;
        }

        // Check for multiple actions
        final SisApsAuthorizeActions authorizeActionsAnnotation = 
                componentClass.getAnnotation(SisApsAuthorizeActions.class);
        if (authorizeActionsAnnotation != null) {
            for (final SisApsAuthorizeAction authorizeActionAnnotation : authorizeActionsAnnotation.actions()) {
                if (!validar(action, authorizeActionAnnotation)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean validar(final Action action, final SisApsAuthorizeAction authorizeActionAnnotation) {
        if (authorizeActionAnnotation != null && action.getName().equals(authorizeActionAnnotation.action())) {
            List<PerfilAcessoEnum> perfis = new ArrayList<PerfilAcessoEnum>();
            for (String role : authorizeActionAnnotation.roles()) {
                perfis.add(PerfilAcessoEnum.valueOfCodigo(role));
            }
            return SpringUtils.get().getBean(RegraPerfilAcessoMediator.class).isAcessoPermitido(perfis);
        }
        return true;
    }
}
