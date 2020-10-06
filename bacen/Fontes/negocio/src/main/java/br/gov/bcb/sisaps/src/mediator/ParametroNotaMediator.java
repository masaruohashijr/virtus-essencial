package br.gov.bcb.sisaps.src.mediator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.ParametroNotaDAO;
import br.gov.bcb.sisaps.src.dominio.AjusteCorec;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Elemento;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.util.Constantes;

@Service
@Transactional(readOnly = true)
public class ParametroNotaMediator {

    @Autowired
    private ParametroNotaDAO parametroNotaDAO;

    public static ParametroNotaMediator get() {
        return SpringUtils.get().getBean(ParametroNotaMediator.class);
    }

    @Transactional(readOnly = true)
    public ParametroNota buscarPorPK(Integer pk) {
        return parametroNotaDAO.buscarPorPK(pk);
    }
    
    @Transactional(readOnly = true)
    public ParametroNota buscarParametrosMetodologiaEDescricao(Integer pkMetodologia , String descricao) {
        return parametroNotaDAO.buscarParametrosMetodologiaEDescricao(pkMetodologia, descricao);
    }

    @Transactional(readOnly = true)
    public String getNotaDescricaoValor(ParametroNota parametroNota) {
        ParametroNota notavigente = buscarPorPK(parametroNota.getPk());
        if (notavigente != null) {
            return notavigente.getDescricaoValor();
        }
        return Constantes.N_A;
    }

    @Transactional(readOnly = true)
    public ParametroNota buscarPorMetodologiaENota(Metodologia metodologia, BigDecimal nota, boolean isNotaElemento) {
        return parametroNotaDAO.buscarPorMetodologiaENota(metodologia, nota, isNotaElemento);
    }
    
    @Transactional(readOnly = true)
    public ParametroNota buscarNota(Metodologia metodologia, BigDecimal nota) {
        return parametroNotaDAO.buscarNota(metodologia, nota);
    }

    @Transactional(readOnly = true)
    public List<ParametroNota> buscarParametrosNota(Integer pkMetodologia) {
        return parametroNotaDAO.buscarParametrosNota(pkMetodologia);
    }
    
    @Transactional(readOnly = true)
    public ParametroNota buscarPorDescricao(Metodologia metodologia, String descricao) {
        return parametroNotaDAO.buscarPorDescricao(metodologia, descricao);
    }

    @Transactional(readOnly = true)
    public List<ParametroNota> buscarParametrosNotaARCCorec(AvaliacaoRiscoControle avaliacaoRiscoControle,
            Integer pkMetodologia) {
        List<ParametroNota> listaNotasParametros = parametroNotaDAO.buscarParametrosNotaCorec(pkMetodologia, true);
        List<ParametroNota> notas = new ArrayList<ParametroNota>();
        notas.addAll(listaNotasParametros);
        BigDecimal notaVigente = avaliacaoRiscoControle.getNotaVigenteValor();
        if (avaliacaoRiscoControle.getNotaCorec() == null
                || (avaliacaoRiscoControle.getNotaCorec().getValor().compareTo(notaVigente) != 0)) {
            for (ParametroNota nota : notas) {
                if (nota.getValor().compareTo(notaVigente) == 0) {
                    listaNotasParametros.remove(nota);
                }
            }
        }
        return listaNotasParametros;
    }

    @Transactional(readOnly = true)
    public List<ParametroNota> buscarParametrosNotaQualitativa(Ciclo ciclo, Integer pkMetodologia,
            AjusteCorec ajusteCorec) {
        List<ParametroNota> listaNotasParametros = parametroNotaDAO.buscarParametrosNotaCorec(pkMetodologia, false);

        ParametroNota notaMatriz = CicloMediator.get().notaQualitativa(ciclo, PerfilAcessoEnum.SUPERVISOR);
        if (ajusteCorec == null || (ajusteCorec != null && ajusteCorec.getNotaQualitativa() == null)
                || !notaMatriz.equals(ajusteCorec.getNotaQualitativa())) {
            listaNotasParametros.remove(notaMatriz);
        }
        return listaNotasParametros;
    }
    
    @Transactional(readOnly = true)
    public List<ParametroNota> buscarParametrosNotaFinal(Ciclo ciclo, Integer pkMetodologia,
            AjusteCorec ajusteCorec) {
        List<ParametroNota> listaNotasParametros = parametroNotaDAO.buscarParametrosNotaCorec(pkMetodologia, false);
        
        /*String notaFinalStr = GrauPreocupacaoESMediator.get().getNotaFinal(perfilRisco, PerfilAcessoEnum.COMITE,
                perfilRisco.getCiclo().getMetodologia());
        
        BigDecimal notaFinal = new BigDecimal(notaFinalStr);

        if (ajusteCorec == null || (ajusteCorec != null && ajusteCorec.getNotaQualitativa() == null)
                || notaFinal.compareTo(ajusteCorec.getNotaFinal().getValor()) != 0) {
            listaNotasParametros.remove(notaMatriz);
        }*/
        return listaNotasParametros;
    }

    @Transactional(readOnly = true)
    public List<ParametroNota> buscarNotaARCElementoSupervisor(Elemento elemento, Integer pkMetodologia) {

        List<ParametroNota> listaNotasParametros = parametroNotaDAO.buscarParametrosNotaCorec(pkMetodologia, true);
        if (elemento.getParametroNotaSupervisor() == null
                || (!elemento.getParametroNotaInspetor().equals(elemento.getParametroNotaSupervisor()))) {
            listaNotasParametros.remove(elemento.getParametroNotaInspetor());
        }
        return listaNotasParametros;
    }

}
