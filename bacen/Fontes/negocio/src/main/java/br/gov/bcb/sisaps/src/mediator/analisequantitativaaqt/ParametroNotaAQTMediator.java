package br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.analisequantitativaaqt.ParametroNotaAQTDAO;
import br.gov.bcb.sisaps.src.dominio.AjusteCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ElementoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.MetodologiaMediator;

@Service
@Transactional(readOnly = true)
public class ParametroNotaAQTMediator {

    @Autowired
    private ParametroNotaAQTDAO parametroNotaAQTDAO;

    public static ParametroNotaAQTMediator get() {
        return SpringUtils.get().getBean(ParametroNotaAQTMediator.class);
    }
    
    public ParametroNotaAQT buscarPorPK(Integer pk) {
        return parametroNotaAQTDAO.buscarPorPK(pk);
    }


    public ParametroNotaAQT buscarPorMetodologiaENota(Metodologia metodologia, BigDecimal nota) {
        return parametroNotaAQTDAO.buscarPorMetodologiaENota(metodologia, nota);
    }

    public ParametroNotaAQT buscarCorPorMetodologia(Metodologia metodologia, BigDecimal nota) {
        return parametroNotaAQTDAO.buscarCorPorMetodologiaENota(metodologia, nota);
    }

    public ParametroNotaAQT buscarPorNota(Metodologia metodologia, BigDecimal nota) {
        return parametroNotaAQTDAO.buscarPorNota(metodologia, nota);
    }

    public List<ParametroNotaAQT> buscarParametrosNotaANEFCorec(AnaliseQuantitativaAQT anef, Integer pkMetodologia) {

        Metodologia metodologia = MetodologiaMediator.get().loadPK(pkMetodologia);
        List<ParametroNotaAQT> listaNotasParametros = metodologia.getNotasAnefSemNA();
        BigDecimal notaAnef = null;
        if (StringUtils.isNotBlank(anef.getNotaVigenteDescricaoValor())) {
            notaAnef = new BigDecimal(anef.getNotaVigenteDescricaoValor().replace(",", "."));
        }
        if (anef.getNotaCorecAtual() == null || (anef.getNotaCorecAtual().getValor().compareTo(notaAnef) != 0)) {
            List<ParametroNotaAQT> notas = new ArrayList<ParametroNotaAQT>();
            notas.addAll(listaNotasParametros);
            for (ParametroNotaAQT nota : notas) {
                if (notaAnef.compareTo(nota.getValor()) == 0) {
                    listaNotasParametros.remove(nota);
                }
            }
        }
        return listaNotasParametros;
    }

    public List<ParametroNotaAQT> buscarNotaANEFAjusteSupervisor(AnaliseQuantitativaAQT anef, Integer pkMetodologia) {
        Metodologia metodologia = MetodologiaMediator.get().loadPK(pkMetodologia);
        return metodologia.getNotasAnefSemNA();
    }

    public List<ParametroNotaAQT> buscarNotaANEFAjusteInspetor(AnaliseQuantitativaAQT anef, Integer pkMetodologia) {
        Metodologia metodologia = MetodologiaMediator.get().loadPK(pkMetodologia);
        return metodologia.getNotasAnefSemNA();
    }

    public List<ParametroNotaAQT> buscarNotaANEFElementoSupervisor(ElementoAQT elemento, Integer pkMetodologia) {

        Metodologia metodologia = MetodologiaMediator.get().loadPK(pkMetodologia);
        List<ParametroNotaAQT> listaNotasParametros = metodologia.getNotasAnefSemNA();

        if (elemento.getParametroNotaSupervisor() == null
                || (!elemento.getParametroNotaInspetor().equals(elemento.getParametroNotaSupervisor()))) {
            listaNotasParametros.remove(elemento.getParametroNotaInspetor());
        }
        return listaNotasParametros;
    }
    
    
    public List<ParametroNotaAQT> buscarParametrosNotaQuantitativa(Ciclo ciclo, Integer pkMetodologia,
            AjusteCorec ajusteCorec) {
        Metodologia metodologia = MetodologiaMediator.get().loadPK(pkMetodologia);
        List<ParametroNotaAQT> listaNotasParametros = metodologia.getNotasAnefAjuste();

        ParametroNotaAQT notaAEF = CicloMediator.get().notaQuantitativa(ciclo, PerfilAcessoEnum.SUPERVISOR);
        if (ajusteCorec == null || (ajusteCorec != null && ajusteCorec.getNotaQuantitativa() == null)
                || !notaAEF.equals(ajusteCorec.getNotaQuantitativa())) {
            listaNotasParametros.remove(notaAEF);
        }
        return listaNotasParametros;
    }

    public void salvarNotaAQT(ParametroNotaAQT nota) {
        parametroNotaAQTDAO.saveOrUpdate(nota);
    }
    
    public ParametroNotaAQT buscarPorDescricao(Metodologia metodologia, String descricao) {
        return parametroNotaAQTDAO.buscarPorDescricao(metodologia, descricao);
    }

}
