package br.gov.bcb.sisaps.comparador;

import java.io.Serializable;
import java.util.Comparator;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.vo.AvaliacaoRiscoControleVO;

public class ComparadorVersaoARC implements Comparator<AvaliacaoRiscoControleVO>, Serializable {

    @Override
    public int compare(AvaliacaoRiscoControleVO o1, AvaliacaoRiscoControleVO o2) {
        if (!EstadoARCEnum.CONCLUIDO.equals(o1.getEstado()) && !EstadoARCEnum.CONCLUIDO.equals(o2.getEstado())) {
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

    private int ordenarEstadoESAcao(AvaliacaoRiscoControleVO o1, AvaliacaoRiscoControleVO o2) {
        String nomeESO1 = o1.getAtividade().getMatriz().getCiclo().getEntidadeSupervisionavel().getNome();
        String nomeESO2 = o2.getAtividade().getMatriz().getCiclo().getEntidadeSupervisionavel().getNome();

        if (o1.getEstado().getDescricao().compareTo(o2.getEstado().getDescricao()) == 0) {
            if (nomeESO1.compareToIgnoreCase(nomeESO2) == 0) {
                if (	o1.getAtividade() != null && o2.getAtividade() != null &&
                		o1.getAtividade().getNome() != null && o2.getAtividade().getNome() != null
                        && o1.getAtividade().getParametroTipoAtividadeNegocio() != null
                        && o2.getAtividade().getParametroTipoAtividadeNegocio() != null
                        && o1.getAtividade().getParametroTipoAtividadeNegocio().getNome()
                                .compareToIgnoreCase(o2.getAtividade().getParametroTipoAtividadeNegocio().getNome()) == 0) {
                    if (o1.getParametroGrupoRiscoControle().getOrdem()
                            .compareTo(o2.getParametroGrupoRiscoControle().getOrdem()) == 0) {
                        if (o1.getTipo().compareTo(o2.getTipo()) == 0) {
                            if (o1.getAcao().compareToIgnoreCase(o2.getAcao()) == 0) {
                                return 0;
                            } else {
                                return o1.getAcao().compareTo(o2.getAcao());
                            }
                        } else {
                            return o1.getTipo().compareTo(o2.getTipo()) * -1;
                        }
                    } else {
                        return o1.getParametroGrupoRiscoControle().getOrdem()
                                .compareTo(o1.getParametroGrupoRiscoControle().getOrdem());
                    }

                } else {
                    if (o1.getAtividade().getParametroTipoAtividadeNegocio() != null
                            && o2.getAtividade().getParametroTipoAtividadeNegocio() != null) {
                        return o1.getAtividade().getParametroTipoAtividadeNegocio().getNome()
                                .compareToIgnoreCase(o2.getAtividade().getParametroTipoAtividadeNegocio().getNome());
                    }
                    return 0;
                }
            } else {
                return nomeESO1.compareToIgnoreCase(nomeESO2);
            }
        } else {
            return o1.getEstado().getDescricao().compareTo(o2.getEstado().getDescricao()) * -1;
        }

    }

}
