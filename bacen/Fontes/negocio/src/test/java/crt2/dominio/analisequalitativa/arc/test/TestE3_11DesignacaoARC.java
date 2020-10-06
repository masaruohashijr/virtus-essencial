package crt2.dominio.analisequalitativa.arc.test;

import java.util.ArrayList;
import java.util.List;

import org.specrunner.junit.SRScenarioListeners;
import org.springframework.beans.factory.annotation.Autowired;

import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.mediator.DesignacaoMediator;
import br.gov.bcb.sisaps.src.mediator.MatrizCicloMediator;
import br.gov.bcb.sisaps.src.vo.ARCDesignacaoVO;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;
import crt2.util.LimpadorStubs;

@SRScenarioListeners(LimpadorStubs.class)
public class TestE3_11DesignacaoARC extends ConfiguracaoTestesNegocio {

    @Autowired
    private MatrizCicloMediator matrizCicloMediator;

    @Autowired
    private DesignacaoMediator designacaoMediator;

    public void salvarSemARC(String matriculaServidor, String idARC) {
        salvar(null, null, BcPessoaAdapter.get().buscarServidor(matriculaServidor), null);
    }

    public void salvarSemMatricula(String idARC1, String idARC2, String matriculaServidor, String pkAtividade) {
        List<ARCDesignacaoVO> arcs = new ArrayList<ARCDesignacaoVO>();
        ARCDesignacaoVO arc1 = new ARCDesignacaoVO(Integer.valueOf(idARC1));
        ARCDesignacaoVO arc2 = new ARCDesignacaoVO(Integer.valueOf(idARC2));
        arc1.setPkAtividade(Integer.valueOf(pkAtividade));
        arc2.setPkAtividade(Integer.valueOf(pkAtividade));
        arcs.add(arc1);
        arcs.add(arc2);
        salvar(null, arcs, BcPessoaAdapter.get().buscarServidor(matriculaServidor), null);
    }

    public void salvarComDuasMatriculas(String idARC1, String idARC2, String matriculaServidorEquipe,
            String matriculaServidorUnidade, Integer pkAtividade) {
        List<ARCDesignacaoVO> arcs = new ArrayList<ARCDesignacaoVO>();
        ARCDesignacaoVO arc1 = new ARCDesignacaoVO(Integer.valueOf(idARC1));
        ARCDesignacaoVO arc2 = new ARCDesignacaoVO(Integer.valueOf(idARC2));
        arc1.setPkAtividade(Integer.valueOf(pkAtividade));
        arc2.setPkAtividade(Integer.valueOf(pkAtividade));
        arcs.add(arc1);
        arcs.add(arc2);
        salvar(null, arcs, BcPessoaAdapter.get().buscarServidor(matriculaServidorEquipe), BcPessoaAdapter.get()
                .buscarServidor(matriculaServidorUnidade));
    }

    public void salvar(String idARC1, String idARC2, String idMatriz, String matriculaServidor, String pkAtividade) {
        List<ARCDesignacaoVO> arcs = new ArrayList<ARCDesignacaoVO>();
        ARCDesignacaoVO arc1 = new ARCDesignacaoVO(Integer.valueOf(idARC1));
        ARCDesignacaoVO arc2 = new ARCDesignacaoVO(Integer.valueOf(idARC2));
        arc1.setPkAtividade(Integer.valueOf(pkAtividade));
        arc2.setPkAtividade(Integer.valueOf(pkAtividade));
        arcs.add(arc1);
        arcs.add(arc2);
        salvar(matrizCicloMediator.loadPK(Integer.valueOf(idMatriz)), arcs,
                BcPessoaAdapter.get().buscarServidor(matriculaServidor), null);
    }

    private void salvar(Matriz matriz, List<ARCDesignacaoVO> arcsDesignacao, ServidorVO servidorEquipe,
            ServidorVO servidorUnidade) {
        erro = null;
        try {
            designacaoMediator.incluir("", arcsDesignacao, servidorEquipe, servidorUnidade, false);
        } catch (NegocioException e) {
            erro = e;
        }
    }

}
