package br.gov.bcb.sisaps.integracao;

import java.util.ArrayList;
import java.util.Arrays;

import org.joda.time.Hours;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.comum.excecoes.BCConsultaInvalidaException;
import br.gov.bcb.comum.pessoa.negocio.servidor.ConsultaServidores;
import br.gov.bcb.sisaps.adaptadores.pessoa.IBcPessoa;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.util.geral.DataUtil;

public class CacheServidoresPorUnidade extends Cache<ArrayList<ServidorVO>> {

    public static final String SERVIDORES_POR_UNIDADES_BACEN = "servidoresPorUnidadesBacen";
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
                            runThread();
                        }
                    }).start();
                }
            };
        }
        return carregador;
    }
    
    public void runThread() {
        Cache.getInstancia(CacheServidoresPorUnidade.class).clear();
            String[] unidades = {"DESUP", "DESUC", "DELIQ"};
        for (String unidade : unidades) {
            try {
                ConsultaServidores consulta = new ConsultaServidores();
                consulta.setBuscarServidoresHierarquiaInferior(true);
                consulta.setBuscarLotacoes(true);
                consulta.setComponenteOrganizacionalRotulos(Arrays.asList(new String[] {unidade}));
                get(unidade, consulta);
            } catch (BCConsultaInvalidaException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public ArrayList<ServidorVO> get(String chave, ConsultaServidores consulta) throws BCConsultaInvalidaException {
        ObjetoCacheavel<ArrayList<ServidorVO>> objetoCacheavel = get(chave);
        
        if (objetoCacheavel == null 
                || Hours.hoursBetween(objetoCacheavel.getDataHoraAtualizacao(), DataUtil.getDateTimeAtual())
                    .getHours() > 23) {
            ArrayList<ServidorVO> servidores = ((IBcPessoa) SpringUtils.get().getBean(IBcPessoa.NOME))
                    .consultarServidores(consulta);
            objetoCacheavel = put(chave, servidores);
        }
        
        return objetoCacheavel == null ? null : objetoCacheavel.getObjeto();
    }

    @Override
    public int getTempoExpiracao() {
        return TEMPO_EXPIRACAO;
    }

}