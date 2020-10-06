package br.gov.bcb.sisaps.src.mediator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.dao.EntidadeSupervisionavelDAO;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.validacao.RegraLocalizacaoSimulada;

@Service
@Transactional(readOnly = true)
public class ServidorVOMediator {

    @Autowired
    private EntidadeSupervisionavelDAO entidadeSupervisionavelDAO;

    public static ServidorVOMediator get() {
        return SpringUtils.get().getBean(ServidorVOMediator.class);
    }

    @SuppressWarnings("unchecked")
    public List<ServidorVO> buscarServidoresComEsAtivas() {
        List<ServidorVO> servidores = new ArrayList<ServidorVO>();
        //TODO refatorar para utilizar metodo de consulta em lote ao bcPessoa.
        List<EntidadeSupervisionavel> entidadesSupervisionaveisPertenceSrc =
                entidadeSupervisionavelDAO.buscarPertenceSrc();
        for (EntidadeSupervisionavel entidade : entidadesSupervisionaveisPertenceSrc) {
            ServidorVO servidor = CicloMediator.get().buscarChefeAtual(entidade.getLocalizacao());
            if (servidor != null && !servidores.contains(servidor)) {
                servidores.add(servidor);
            }
        }

        ComparatorChain cc = new ComparatorChain();
        cc.addComparator(new BeanComparator("nome"));
        Collections.sort(servidores, cc);
        return servidores;
    }

    public String alterarLocalizacaoSimulada(ServidorVO servidorLogado, String novaLocalizacaoSimulada) {
        new RegraLocalizacaoSimulada(novaLocalizacaoSimulada, servidorLogado).validar();
        servidorLogado.setLocalizacaoSimulada(novaLocalizacaoSimulada);
        return "Localização simulada alterada com sucesso.";
    }
    
    public String cancelarAlterarLocalizacaoSimulada(ServidorVO servidorLogado) {
        servidorLogado.setLocalizacaoSimulada(null);
        return "Cancelamento da localização simulada efetuado com sucesso.";
    }

}
