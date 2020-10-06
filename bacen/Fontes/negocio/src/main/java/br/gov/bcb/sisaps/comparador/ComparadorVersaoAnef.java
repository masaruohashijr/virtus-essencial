package br.gov.bcb.sisaps.comparador;

import java.io.Serializable;
import java.util.Comparator;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoAQTEnum;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.aqt.AnaliseQuantitativaAQTVO;

public class ComparadorVersaoAnef implements Comparator<AnaliseQuantitativaAQTVO>, Serializable {

    @Override
    public int compare(AnaliseQuantitativaAQTVO o1, AnaliseQuantitativaAQTVO o2) {

        if (!EstadoAQTEnum.CONCLUIDO.equals(o1.getEstado()) && !EstadoAQTEnum.CONCLUIDO.equals(o2.getEstado())) {
            return ordenarEstadoESAcao(o1, o2);
        }

        if (o1.getDataConclusao() != null && o2.getDataConclusao() != null) {
            if (o1.getDataConclusao().compareTo(o2.getDataConclusao()) == 0) {
                return ordenarEstadoESAcao(o1, o2);
            } else {
                return o1.getDataConclusao().compareTo(o2.getDataConclusao()) * -1;
            }
        }

        if (o1.getDataConclusao() != null && o2.getDataConclusao() == null) {
            return 1;
        }

        if (o1.getDataConclusao() == null && o2.getDataConclusao() != null) {
            return -1;
        }

        return 0;

    }

    private int ordenarEstadoESAcao(AnaliseQuantitativaAQTVO o1, AnaliseQuantitativaAQTVO o2) {
        if (o1.getEstado().getDescricao().compareTo(o2.getEstado().getDescricao()) == 0) {
            String nome = o1.getEntidadeSupervisionavel().getNome();
            String nome2 = o2.getEntidadeSupervisionavel().getNome();

            if (nome.compareToIgnoreCase(nome2) == 0) {
                if (o1.getParametroAQT().getOrdem().compareTo(o2.getParametroAQT().getOrdem()) == 0) {
                    if (o1.getAcao().compareToIgnoreCase(o2.getAcao()) == 0) {
                        return 0;
                    } else {
                        return o1.getAcao().compareToIgnoreCase(o2.getAcao());
                    }
                } else {
                    return o1.getParametroAQT().getOrdem().compareTo(o2.getParametroAQT().getOrdem());
                }
            } else {
                return nome.compareToIgnoreCase(nome2);
            }
        } else {
            return o1.getEstado().getDescricao().compareTo(o2.getEstado().getDescricao()) * -1;
        }

    }

}
