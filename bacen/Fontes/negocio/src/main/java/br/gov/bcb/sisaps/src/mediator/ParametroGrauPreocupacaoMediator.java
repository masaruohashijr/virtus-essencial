package br.gov.bcb.sisaps.src.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.ParametroGrauPreocupacaoDAO;
import br.gov.bcb.sisaps.src.dominio.AjusteCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.GrauPreocupacaoES;
import br.gov.bcb.sisaps.src.dominio.ParametroGrauPreocupacao;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.util.Constantes;

@Service
@Transactional(readOnly = true)
public class ParametroGrauPreocupacaoMediator {
    @Autowired
    private ParametroGrauPreocupacaoDAO parametroGrauPreocupacaoDAO;

    public static ParametroGrauPreocupacaoMediator get() {
        return SpringUtils.get().getBean(ParametroGrauPreocupacaoMediator.class);
    }

    public ParametroGrauPreocupacao buscarPorPK(Integer pk) {
        return parametroGrauPreocupacaoDAO.buscarPorPK(pk);
    }

    public String getNotaDescricaoValor(ParametroGrauPreocupacao parametroGrauPreocupacao) {
        ParametroGrauPreocupacao notavigente = buscarPorPK(parametroGrauPreocupacao.getPk());

        if (notavigente != null) {
            return notavigente.getDescricaoValor();
        }

        return Constantes.N_A;
    }

    public ParametroGrauPreocupacao buscarPorMetodologiaEDescricao(Integer pkMetodologia, String descricao) {
        return parametroGrauPreocupacaoDAO.buscarPorMetodologiaEDescricao(pkMetodologia, descricao);
    }

    public ParametroGrauPreocupacao buscarPorMetodologiaENota(Integer pkMetodologia, Short nota) {
        return parametroGrauPreocupacaoDAO.buscarPorMetodologiaENota(pkMetodologia, nota);
    }

    public List<ParametroGrauPreocupacao> buscarParametrosGrauPreocupacao(Integer pkMetodologia) {
        return parametroGrauPreocupacaoDAO.buscarParametrosGrauPreocupacao(pkMetodologia);
    }

    public List<ParametroGrauPreocupacao> buscarParametrosNotaGrauPreocupacao(Ciclo ciclo, Integer pkMetodologia,
            AjusteCorec ajusteCorec) {
        List<ParametroGrauPreocupacao> listaGrauPreocupacao =
                parametroGrauPreocupacaoDAO.buscarParametrosNotaCorec(pkMetodologia);

        GrauPreocupacaoES ultimoGrauPreocupacaoES = GrauPreocupacaoESMediator.get().buscarPorPerfilRisco(
                PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk()).getPk());
        ParametroGrauPreocupacao grauPreocupacao =
                ultimoGrauPreocupacaoES == null || ultimoGrauPreocupacaoES.getParametroGrauPreocupacao() == null ? null
                        : ultimoGrauPreocupacaoES.getParametroGrauPreocupacao();

        if (ajusteCorec == null || (ajusteCorec != null && ajusteCorec.getGrauPreocupacao() == null)
                || !grauPreocupacao.equals(ajusteCorec.getGrauPreocupacao())) {
            listaGrauPreocupacao.remove(grauPreocupacao);
        }

        return listaGrauPreocupacao;
    }
    
    /**
     * Método que verifica se o parametro é um "Grau de preocupação" ou uma "Nota final".
     * Caso o parâmetro tiver data de atualização maior que a data limite na 
     * Constantes.DATA_LIMITE_GRAU_PREOCUPACAO, o parâmetro é considerado uma "Nota final", 
     * senão, um "Grau de preocupação".
     * 
     * @return
     */
    public boolean isNotaFinal(ParametroGrauPreocupacao parametroGrauPreocupacao, ParametroNota parametroNota) {
        return (parametroNota == null && parametroGrauPreocupacao == null)
                || parametroNota != null
                || (parametroGrauPreocupacao != null && parametroGrauPreocupacao.getUltimaAtualizacao().toDate()
                        .after(Constantes.DATA_LIMITE_GRAU_PREOCUPACAO));
    }

}
