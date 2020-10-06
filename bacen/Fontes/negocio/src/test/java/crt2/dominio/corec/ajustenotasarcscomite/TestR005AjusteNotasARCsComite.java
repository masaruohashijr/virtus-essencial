package crt2.dominio.corec.ajustenotasarcscomite;

import java.util.ArrayList;
import java.util.List;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroNotaMediator;
import br.gov.bcb.sisaps.src.vo.AvaliacaoRiscoControleVO;
import crt2.ConfiguracaoTestesNegocio;

public class TestR005AjusteNotasARCsComite extends ConfiguracaoTestesNegocio {

    public void ajustarNotasARCsCorec(int cicloPk, int idARC7, int notaARC7, int idARC8, int notaARC8, int idARC11,
            int idARC12, int idARC5, int notaARC5, int idARC6, int notaARC6, int idARC14, int notaARC14) {

        Ciclo ciclo = CicloMediator.get().loadPK(cicloPk);

        List<AvaliacaoRiscoControleVO> listArcVO = new ArrayList<AvaliacaoRiscoControleVO>();

        AvaliacaoRiscoControleVO arc7 = new AvaliacaoRiscoControleVO();
        arc7.setPk(idARC7);
        arc7.setAlterouNota(true);
        arc7.setNotaCorec(ParametroNotaMediator.get().buscarPorPK(notaARC7));
        listArcVO.add(arc7);
        AvaliacaoRiscoControleVO arc8 = new AvaliacaoRiscoControleVO();
        arc8.setPk(idARC8);
        arc8.setAlterouNota(true);
        arc8.setNotaCorec(ParametroNotaMediator.get().buscarPorPK(notaARC8));
        listArcVO.add(arc8);
        
        AvaliacaoRiscoControleVO arc11 = new AvaliacaoRiscoControleVO();
        arc11.setPk(idARC11);
        arc11.setAlterouNota(false);
        arc11.setNotaCorec(null);
        listArcVO.add(arc11);
        AvaliacaoRiscoControleVO arc12 = new AvaliacaoRiscoControleVO();
        arc12.setPk(idARC12);
        arc12.setAlterouNota(false);
        arc12.setNotaCorec(null);
        listArcVO.add(arc12);
        
        
        AvaliacaoRiscoControleVO arc5 = new AvaliacaoRiscoControleVO();
        arc5.setPk(idARC5);
        arc5.setAlterouNota(true);
        arc5.setNotaCorec(ParametroNotaMediator.get().buscarPorPK(notaARC5));
        listArcVO.add(arc5);

        AvaliacaoRiscoControleVO arc6 = new AvaliacaoRiscoControleVO();
        arc6.setPk(idARC6);
        arc6.setAlterouNota(true);
        arc6.setNotaCorec(ParametroNotaMediator.get().buscarPorPK(notaARC6));
        listArcVO.add(arc6);
        
        AvaliacaoRiscoControleVO arc14 = new AvaliacaoRiscoControleVO();
        arc14.setPk(idARC14);
        arc14.setAlterouNota(true);
        arc14.setNotaCorec(ParametroNotaMediator.get().buscarPorPK(notaARC14));
        listArcVO.add(arc14);

        AvaliacaoRiscoControleMediator.get().ajustarNotasARCsCorec(listArcVO);
        

    }
}
