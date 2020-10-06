package crt2.dominio.analisequalitativa.designararclote;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoGrupoEnum;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.DesignacaoMediator;
import br.gov.bcb.sisaps.src.vo.ARCDesignacaoVO;
import br.gov.bcb.sisaps.src.vo.ConsultaARCDesignacaoVO;

public class TestR002DesignarARCLote extends TestR001DesignarARCLote {

    public List<ARCDesignacaoVO> consultaArcs(Integer ciclo, String filtro, boolean isAtividade) {
        Ciclo cicloBase = CicloMediator.get().buscarCicloPorPK(ciclo);
        ConsultaARCDesignacaoVO consulta = new ConsultaARCDesignacaoVO();
        consulta.setMatriz(cicloBase.getMatriz());
        if (isAtividade && !filtro.equals("Todos")) {
            consulta.setNomeAtividade(filtro);
        } else if (!filtro.equals("Todos")) {
            consulta.setTipoGrupo(TipoGrupoEnum.valueOfDescricao(filtro));
        }
        
        List<ARCDesignacaoVO> lista = DesignacaoMediator.get().buscarListaArcs(cicloBase, consulta);
        
        Collections.sort(lista, new Comparator<ARCDesignacaoVO>() {
            @Override
            public int compare(ARCDesignacaoVO a1, ARCDesignacaoVO a2) {
                if (a1.getNomeAtividade() != null && a2.getNomeAtividade() != null) {
                    return a1.getNomeAtividade().compareTo(a2.getNomeAtividade());
                } else {
                    return -1;
                }
            }
        });
        return lista;
    }

    public String getID(ARCDesignacaoVO arc) {
        return arc.getPk().toString();
    }

    public String getAtividade(ARCDesignacaoVO arc) {
        return arc.getNomeAtividade() == null ? "" : arc.getNomeAtividade();
    }

    public String getGrupo(ARCDesignacaoVO arc) {
        return arc.getNomeGrupoRiscoControle();
    }

    public String getRiscoControle(ARCDesignacaoVO arc) {
        return arc.getTipoGrupoRiscoControle().getAbreviacao();
    }

    public String getEstado(ARCDesignacaoVO arc) {
        return arc.getEstado().getDescricao();
    }
}
