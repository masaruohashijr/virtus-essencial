package br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao;

import java.io.IOException;
import java.io.InputStream;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.hibernate.StaleObjectStateException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;
import org.springframework.orm.hibernate4.HibernateOptimisticLockingFailureException;

import br.gov.bcb.sisaps.src.validacao.IRegraAnexosValidacao;
import br.gov.bcb.sisaps.src.validacao.RegraAnexosValidacaoPDFA4;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import br.gov.bcb.sisaps.web.page.componentes.behavior.AjaxButtonIndicator;
import br.gov.bcb.sisaps.web.page.componentes.infraestrutura.ExceptionUtils;

public abstract class AjaxBotaoAnexarArquivo extends AjaxButtonIndicator {

    private final FileUploadField fileUploadFieldArquivo;
    private final boolean visible;
    private final IRegraAnexosValidacao regraAnexosValidacao;

    // Construtor
    public AjaxBotaoAnexarArquivo(String id, FileUploadField fileUploadFieldArquivo, boolean visible) {
        this(id, fileUploadFieldArquivo, visible, new RegraAnexosValidacaoPDFA4());
    }
    
    // Construtor
    public AjaxBotaoAnexarArquivo(String id, FileUploadField fileUploadFieldArquivo, boolean visible,
            IRegraAnexosValidacao regraAnexosValidacao) {
        super(id);
        setOutputMarkupPlaceholderTag(true);
        this.fileUploadFieldArquivo = fileUploadFieldArquivo;
        this.visible = visible;
        this.regraAnexosValidacao = regraAnexosValidacao;
    }
    
    @Override
    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
        getPage().getFeedbackMessages().clear();
        try {

            if (fileUploadFieldArquivo.getFileUpload() == null) {
                throw new NegocioException("Nenhum arquivo foi selecionado.");
            } else {
                String clientFileName = fileUploadFieldArquivo.getFileUpload().getClientFileName();
                InputStream inputStream = null;
                InputStream inputStreamValidacao = null;
                try {
                    inputStream = fileUploadFieldArquivo.getFileUpload().getInputStream();
                    inputStreamValidacao = fileUploadFieldArquivo.getFileUpload().getInputStream();
                } catch (IOException e) {
                    throw new NegocioException(e.getMessage());
                }
                if (regraAnexosValidacao != null) {
                    regraAnexosValidacao.validar(inputStreamValidacao, clientFileName);
                }
                executarSubmit(target, clientFileName, inputStream);
            }
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
        target.add(getPage().get("feedback"));
        Component scriptErro = getPage().get("scriptErro");
        if (scriptErro != null) {
            target.add(scriptErro);
        }

    }

    public abstract void executarSubmit(AjaxRequestTarget target, String clientFileName, InputStream inputStream);

    @Override
    public boolean isVisible() {
        return visible;
    }
}