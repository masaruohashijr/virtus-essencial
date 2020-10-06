package br.gov.bcb.sisaps.integracao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.joda.time.Hours;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.comum.excecoes.BCConsultaInvalidaException;
import br.gov.bcb.comum.pessoa.negocio.componenteorganizacional.ConsultaChefia;
import br.gov.bcb.sisaps.adaptadores.pessoa.ChefiaVO;
import br.gov.bcb.sisaps.adaptadores.pessoa.IBcPessoa;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.mediator.EntidadeSupervisionavelMediator;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.geral.DataUtil;

public class CacheChefiaPorLocalizacao extends Cache<ChefiaVO> {

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
        Cache.getInstancia(CacheChefiaPorLocalizacao.class).clear();
        Session session = null;
        SessionFactory sessionFactory = null;
        try {
            EntidadeSupervisionavelMediator esMediator = EntidadeSupervisionavelMediator.get();
            sessionFactory = esMediator.obterSessionFactory();
            session = sessionFactory.openSession();
            TransactionSynchronizationManager.bindResource(
                    sessionFactory, new SessionHolder(session));
            carregarDadosCacheInicial(esMediator);
        //CHECKSTYLE:OFF
        } catch (Throwable t) {
            t.getMessage();
        } finally {
        //CHECKSTYLE:ON
            try {
                if (session != null && session.isOpen() && session.isConnected()) {
                    session.clear();
                    session.close();
                    TransactionSynchronizationManager.unbindResource(sessionFactory);
                }
            } catch (HibernateException e) {
                e.getMessage();
            } catch (IllegalStateException ise) {
                ise.getMessage();
            }
        }
    }

    private void carregarDadosCacheInicial(EntidadeSupervisionavelMediator esMediator) throws BCConsultaInvalidaException {
        List<EntidadeSupervisionavel> listaESs = esMediator.buscarVersoeESs();
        for (EntidadeSupervisionavel es : listaESs) {
            String rotulo = es.getLocalizacao(); // Aqui será o rotulo da localização
            String chave = rotulo + Constantes.SUBLINHADO + "null";
            // Busca o servidor sem data base
            ConsultaChefia consultaChefia = criarConsultaChefia(rotulo, null);
            get(chave, consultaChefia);
            
            if (es.getVersaoPerfilRisco() == null) {
                continue;
            }
            List<PerfilRisco> perfis = es.getVersaoPerfilRisco().getPerfisRisco();
            for (PerfilRisco perfil : perfis) {
                chave = rotulo + Constantes.SUBLINHADO + perfil.getDataCriacaoFormatadaSemHora();
                //busca o servidor com data base
                consultaChefia = criarConsultaChefia(rotulo, perfil.getDataCriacao().toDate());
                get(chave, consultaChefia);
            }
        }
    }
    
    private ConsultaChefia criarConsultaChefia(String rotulo, java.util.Date data) {
        ConsultaChefia consulta = new ConsultaChefia();
        consulta.setDataBase(data);
        consulta.setComponenteOrganizacionalRotulo(rotulo);
        return consulta;
    }

    @Override
    public int getTempoExpiracao() {
        return TEMPO_EXPIRACAO;
    }
    
    public ChefiaVO get(String chave, ConsultaChefia consulta) throws BCConsultaInvalidaException {
        ObjetoCacheavel<ChefiaVO> objetoCacheavel = get(chave);
        if (objetoCacheavel == null 
                || Hours.hoursBetween(objetoCacheavel.getDataHoraAtualizacao(), DataUtil.getDateTimeAtual())
                    .getHours() > 23) {
            ChefiaVO chefiaVO = ((IBcPessoa) SpringUtils.get().getBean(IBcPessoa.NOME)).buscarChefia(consulta);
            objetoCacheavel = put(chave, chefiaVO);
        }
        
        return objetoCacheavel == null ? null : objetoCacheavel.getObjeto();
    }

}
