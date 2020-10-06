package br.gov.bcb.sisaps.integracao;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.SerializationUtils;
import org.hibernate.HibernateException;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.CacheDAO;
import br.gov.bcb.sisaps.src.dominio.CachePersistente;
import br.gov.bcb.sisaps.util.geral.DataUtil;

public abstract class Cache<T extends Serializable> {

    protected static final int TEMPO_EXPIRACAO = 24;

    private static Map<String, AgendadorCache> agendadores = new HashMap<String, AgendadorCache>();
    private static Map<String, Cache<?>> instancias = new HashMap<String, Cache<?>>();
    private String nomeClasse;
    
    
    public Cache() {
        super();
        String chave = this.getClass().getName();
        agendadores.put(chave, new AgendadorCache(getTempoExpiracao()));
        instancias.put(chave, this);
        nomeClasse = this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1) + "_";
    }

    public abstract Runnable getCarregador();

    public abstract int getTempoExpiracao();

    @SuppressWarnings("unchecked")
    public ObjetoCacheavel<T> get(String chave) {
        agendadores.get(this.getClass().getName()).verificarProcesso(getCarregador());
        CacheDAO cacheDAO = SpringUtils.get().getBean(CacheDAO.class);
        obterSessao(cacheDAO);
        String chaveLocal = nomeClasse + chave;
        return (ObjetoCacheavel<T>) cacheDAO.buscarPorChave(chaveLocal);
    }
    
    public CachePersistente<T> put(String chave, T item) {
        String chaveLocal = nomeClasse + chave;
        CachePersistente<T> elemento = new CachePersistente<T>();
        elemento.setChave(chaveLocal);
        elemento.setElemento(item == null ? null : SerializationUtils.serialize(item));
        elemento.setDataHora(DataUtil.getDateTimeAtual());
        
        CacheDAO cacheDAO = SpringUtils.get().getBean(CacheDAO.class);        
        obterSessao(cacheDAO);
        cacheDAO.salvar(elemento);
        return elemento;
    }

    private void obterSessao(CacheDAO cacheDAO) {
        try {
            cacheDAO.getSessionFactory().getCurrentSession();
        } catch (HibernateException he) {
            TransactionSynchronizationManager.bindResource(cacheDAO
                    .getSessionFactory(), new SessionHolder(cacheDAO
                    .getSessionFactory().openSession()));
        }
    }
    
    public boolean contemChave(String chave) {
        CacheDAO cacheDAO = SpringUtils.get().getBean(CacheDAO.class);    
        return cacheDAO.buscarPorChave(chave) != null;
    }

    public void iniciar() {
        agendadores.get(this.getClass().getName()).verificarProcesso(getCarregador());
    }

    public static Cache<?> getInstancia(@SuppressWarnings("rawtypes")
    Class classe) {
        String chave = classe.getName();
        if (instancias.get(chave) == null) {
            try {
                instancias.put(chave, (Cache<?>) classe.newInstance());
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return instancias.get(chave);
    }

    public void clear() {
    }

}

class AgendadorCache {
    private ScheduledFuture<?> processoAgendado;
    private final ScheduledExecutorService agendador = Executors.newScheduledThreadPool(1);

    private int tempoRecarga;

    public AgendadorCache(int tempoRecarga) {
        super();
        this.tempoRecarga = tempoRecarga;
    }

    private void iniciar(final Runnable carregador) {
        processoAgendado = agendador.scheduleAtFixedRate(carregador, 0, tempoRecarga, TimeUnit.HOURS);
    }

    public void verificarProcesso(final Runnable carregador) {

        if (processoAgendado == null || processoAgendado.isCancelled()) {
            iniciar(carregador);
        }

    }

}
