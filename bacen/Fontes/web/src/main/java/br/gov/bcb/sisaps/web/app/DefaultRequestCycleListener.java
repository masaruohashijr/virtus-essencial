/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.web.app;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Session;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate4.HibernateOptimisticLockingFailureException;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistenteAuditavel;
import br.gov.bcb.app.stuff.seguranca.ProvedorInformacoesUsuario;
import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.mediator.RegraPerfilAcessoMediator;
import br.gov.bcb.sisaps.web.page.HomePage;
import br.gov.bcb.wicket.stuff.ProvedorInformacoesUsuarioWicket;
import br.gov.bcb.wicket.stuff.protocol.http.BcWebSession;

public class DefaultRequestCycleListener extends AbstractRequestCycleListener {

    private static final Log LOG = LogFactory.getLog(DefaultRequestCycleListener.class);

    private ProvedorInformacoesUsuario userInfoProvider = new ProvedorInformacoesUsuarioWicket();

    public ProvedorInformacoesUsuario getUserInfoProvider() {
        return userInfoProvider;
    }

    protected void setUserInfoProvider(ProvedorInformacoesUsuario userInfoProvider) {
        this.userInfoProvider = userInfoProvider;
    }

    protected UsuarioAplicacao newUsuario(ProvedorInformacoesUsuario provedorInformacoes) {
        //aqui sao feitas chamadas ao BcPessoal, BcAft etc para dados mais detalhados dos usuarios.
        //caso tenha estendido Usuario, instancie e retorne a nova instancia aqui.
        UsuarioAplicacao usuario = new UsuarioAplicacao(provedorInformacoes);
        ServidorVO servidorVO =
                SpringUtils.get().getBean(BcPessoaAdapter.class).buscarServidorPorLoginSemExcecao(usuario.getLogin());
        //Exemplo de uso do adapter para obter detalhes do usuario
        usuario.setNome(servidorVO.getNome());
        usuario.setMatricula(servidorVO.getMatricula());
        usuario.setServidorVO(servidorVO);

        return usuario;
    }

    /**
     * Indica se a segurança está habilitada no Web.xml.
     */
    public boolean isSegurancaHabilitada() {
        Request request = RequestCycle.get().getRequest();
        HttpServletRequest servletRequest = (HttpServletRequest) request.getContainerRequest();
        return servletRequest.getUserPrincipal() != null;
    }

    @Override
    public void onBeginRequest(RequestCycle requestCycle) {
        UsuarioAplicacao usuarioCorrente;
        
        RegraPerfilAcessoMediator.resetLocalThreadRegras();
        
        @SuppressWarnings("unchecked")
        BcWebSession<UsuarioAplicacao> bcWebSession = (BcWebSession<UsuarioAplicacao>) BcWebSession.get();
        usuarioCorrente = bcWebSession.getUsuarioCorrente();
        if (usuarioCorrente == null) {
            usuarioCorrente = newUsuario(userInfoProvider);
            bcWebSession.setUsuarioCorrente(usuarioCorrente);
        }
        UsuarioCorrente.set(usuarioCorrente);
    }

    @Override
    public IRequestHandler onException(RequestCycle cycle, Exception ex) {
        if (ex instanceof RuntimeException) {
            if (isExcecaoConcorrecia((RuntimeException) ex)) {
                Object usuario = identificarUsuario(cast((RuntimeException) ex));
                Session.get()
                        .error(String.format("O registro já está sendo atualizado pelo usuário %s.", usuario));
                return new RenderPageRequestHandler(new PageProvider(HomePage.class)); //p.getClass()
            } else {
                LOG.error(ex.getMessage(), ex);
            }
        }
        return super.onException(cycle, ex);
    }
    
    private boolean isExcecaoConcorrecia(RuntimeException e) {
        return e instanceof HibernateOptimisticLockingFailureException
                || (e.getCause() != null 
                && (e.getCause() instanceof HibernateOptimisticLockingFailureException 
                        || (e.getCause() instanceof InvocationTargetException 
                                && ((InvocationTargetException) e.getCause()).getTargetException() 
                                instanceof HibernateOptimisticLockingFailureException)));
    }

    private HibernateOptimisticLockingFailureException cast(RuntimeException e) {
        if (isExcecaoConcorrecia(e)) {
            return (HibernateOptimisticLockingFailureException) 
                    ((InvocationTargetException) e.getCause()).getTargetException();
        }
        return (HibernateOptimisticLockingFailureException) e.getCause();
    }

    private String identificarUsuario(HibernateOptimisticLockingFailureException e) {
        Object objeto = getObjetoAtualizado(e);
        if (objeto instanceof ObjetoPersistenteAuditavel<?>) {
            return ((ObjetoPersistenteAuditavel<?>) objeto).getOperadorAtualizacao();
        }
        return "[usuário não identificado]";
    }

    private Object getObjetoAtualizado(HibernateOptimisticLockingFailureException e) {
        try {
            Class<?> classe = Class.forName(e.getPersistentClassName());
            return getCurrentSession().get(classe, (Serializable) e.getIdentifier());
        } catch (ClassNotFoundException e1) {
            return null;
        }
    }

    private org.hibernate.Session getCurrentSession() {
        return SpringUtils.get().getBean(SessionFactory.class).getCurrentSession();
    }

    @Override
    public void onExceptionRequestHandlerResolved(RequestCycle cycle, IRequestHandler handler, Exception exception) {
        super.onExceptionRequestHandlerResolved(cycle, handler, exception);
    }

}
