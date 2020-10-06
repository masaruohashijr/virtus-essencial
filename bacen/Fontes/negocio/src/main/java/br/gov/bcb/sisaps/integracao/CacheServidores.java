package br.gov.bcb.sisaps.integracao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.joda.time.Hours;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.comum.excecoes.BCConsultaInvalidaException;
import br.gov.bcb.sisaps.adaptadores.pessoa.IBcPessoa;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.dominio.Delegacao;
import br.gov.bcb.sisaps.src.dominio.Designacao;
import br.gov.bcb.sisaps.src.mediator.DelegacaoMediator;
import br.gov.bcb.sisaps.src.mediator.DesignacaoMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.geral.DataUtil;

public class CacheServidores extends Cache<ServidorVO> {

    private static final String NULL = "null";
    private Runnable carregador;

    @Override
    public Runnable getCarregador() {

        if (carregador == null) {
            carregador = new Runnable() {
                @Override
                public void run() {
                    // Precisa iniciar outra thread porque a abertura
                    // da sessão hibernate bloqueia o agendamento da thread raiz
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            rodarThread();
                        }
                    }).start();
                }
            };
        }
        return carregador;
    }
    
    private void rodarThread() {
        Cache.getInstancia(CacheServidores.class).clear();
        
        Session session = null;
        SessionFactory sessionFactory = null;
        try {
            DelegacaoMediator delegacaoMediator = DelegacaoMediator.get();
            sessionFactory = delegacaoMediator.obterSessionFactory();
            session = sessionFactory.openSession();
            TransactionSynchronizationManager.bindResource(
                    sessionFactory, new SessionHolder(session));
            carregarDadosCacheInicial(delegacaoMediator);            
        //CHECKSTYLE:OFF
        } catch (Throwable t) {
            // Morreu a thread. Um minuto de silêncio em sua memória...
        } finally {
            try {
                if (session != null && session.isOpen()
                        && session.isConnected()) {
                    session.clear();
                    session.close();
                    TransactionSynchronizationManager.unbindResource(sessionFactory);
                }
            } catch (HibernateException e) {
                // Paciência
            } catch (IllegalStateException ise) {
                // Falhou na liberação do recurso
            }
        }
        //CHECKSTYLE:ON
    }

    private void carregarDadosCacheInicial(DelegacaoMediator delegacaoMediator) throws BCConsultaInvalidaException {
        List<Delegacao> delegacoes = delegacaoMediator.buscarDelegacoes();
        for (Delegacao delegacao : delegacoes) {
            String matricula = delegacao.getMatriculaServidor();
            String chave = matricula + Constantes.SUBLINHADO + NULL;
            // Busca o servidor sem data base
            get(chave, matricula, null);
        }
        DesignacaoMediator designacaoMediator = DesignacaoMediator.get();
        List<Designacao> designacoes = designacaoMediator.buscarDesignacoes();
        for (Designacao designacao : designacoes) {
            String matricula = designacao.getMatriculaServidor();
            String chave = matricula + Constantes.SUBLINHADO + NULL;
            // Busca o servidor sem data base
            get(chave, matricula, null);
        }
    }
    
    public ServidorVO get(String chave, String matricula, Date data)
            throws BCConsultaInvalidaException {

        ObjetoCacheavel<ServidorVO> objetoCacheavel = get(chave);

        if (objetoCacheavel == null 
                || Hours.hoursBetween(objetoCacheavel.getDataHoraAtualizacao(), DataUtil.getDateTimeAtual())
                    .getHours() > 23) {
            ServidorVO servidorVO = ((IBcPessoa) SpringUtils.get().getBean(IBcPessoa.NOME)).buscarServidor(matricula, data);
            objetoCacheavel = put(chave, servidorVO);
            if (servidorVO != null && StringUtils.isNotBlank(servidorVO.getLogin())) {
                String chaveLogin = Util.obterLoginMinusculo(servidorVO.getLogin()) + Constantes.SUBLINHADO + NULL;
                ObjetoCacheavel<ServidorVO> objetoCacheavelLogin = get(chaveLogin);
                if (objetoCacheavelLogin == null
                        || Hours.hoursBetween(objetoCacheavelLogin.getDataHoraAtualizacao(), DataUtil.getDateTimeAtual())
                                .getHours() > 23) {
                    put(chaveLogin, servidorVO);
                }
            }
        }

        return objetoCacheavel == null ? null : objetoCacheavel.getObjeto();
    }
    
    public ServidorVO get(String chave, String login) throws BCConsultaInvalidaException {

        ObjetoCacheavel<ServidorVO> objetoCacheavel = get(chave);

        if (objetoCacheavel == null 
                || Hours.hoursBetween(objetoCacheavel.getDataHoraAtualizacao(), DataUtil.getDateTimeAtual())
                    .getHours() > 23) {
            ServidorVO servidorVO = ((IBcPessoa) SpringUtils.get().getBean(IBcPessoa.NOME))
                    .buscarServidorPorLogin(login, null);
            objetoCacheavel = put(chave, servidorVO);
            if (servidorVO != null && StringUtils.isNotBlank(servidorVO.getMatricula())) {
                String chaveMatricula =
                        Util.normalizarMatriculaCpf(servidorVO.getMatricula()) + Constantes.SUBLINHADO + NULL;
                ObjetoCacheavel<ServidorVO> objetoCacheavelMatricula = get(chaveMatricula);
                if (objetoCacheavelMatricula == null
                        || Hours.hoursBetween(objetoCacheavelMatricula.getDataHoraAtualizacao(), DataUtil.getDateTimeAtual())
                                .getHours() > 23) {
                    put(chaveMatricula, servidorVO);
                }
            }
        }

        return objetoCacheavel == null ? null : objetoCacheavel.getObjeto();
    }
    
    @Override
    public int getTempoExpiracao() {
        return TEMPO_EXPIRACAO;
    }

}