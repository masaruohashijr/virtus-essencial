/*
 * Sistema SIGAS..
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.web.page.componentes.botoes;

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.transformer.AbstractTransformerBehavior;
import org.apache.wicket.util.value.IValueMap;
import org.hibernate.StaleObjectStateException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;
import org.springframework.orm.hibernate4.HibernateOptimisticLockingFailureException;

import br.gov.bcb.sisaps.util.validacao.NegocioException;
import br.gov.bcb.sisaps.web.page.componentes.infraestrutura.ExceptionUtils;

@SuppressWarnings("serial")
public abstract class CustomButton extends Button {

    public CustomButton(String id) {
        super(id);
        setMarkupId(id);
        add(new AbstractTransformerBehavior() {

            @Override
            public CharSequence transform(Component component, CharSequence output) {
                return output.toString();
            }

            @Override
            public void onComponentTag(Component component, ComponentTag tag) {
                removeTagAttribute(tag, "onClick");
                removeTagAttribute(tag, "onKeypress");
                super.onComponentTag(component, tag);
            }
        });
    }

    private void removeTagAttribute(ComponentTag tag, String attribute) {
        IValueMap attributes = tag.getAttributes();
        for (Object key : attributes.keySet()) {
            if (((String) key).equalsIgnoreCase(attribute)) {
                attributes.remove(key);
                break;
            }
        }
    }

    @Override
    public void onSubmit() {
        try {
            executeSubmit();
        } catch (InvalidStateException e) {
            ExceptionUtils.tratarInvalidStateException(e, getPage());
        } catch (NegocioException e) {
            ExceptionUtils.tratarNegocioException(e, getPage());
        } catch (HibernateOptimisticLockingFailureException e) {
            ExceptionUtils.tratarErroConcorrencia(e, getPage());
        } catch (StaleObjectStateException e) {
            ExceptionUtils.tratarErroConcorrencia(e, getPage());
        } catch (ConstraintViolationException e) {
            ExceptionUtils.tratarErroConstraint(e, getPage());
        }
    }

    public abstract void executeSubmit();
}
