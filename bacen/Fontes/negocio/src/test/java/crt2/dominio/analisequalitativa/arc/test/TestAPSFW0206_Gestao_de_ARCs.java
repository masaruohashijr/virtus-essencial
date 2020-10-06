package crt2.dominio.analisequalitativa.arc.test;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.EntidadeSupervisionavelMediator;
import br.gov.bcb.sisaps.src.mediator.MatrizCicloMediator;
import br.gov.bcb.sisaps.src.validacao.RegraGestaoARC;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestAPSFW0206_Gestao_de_ARCs extends ConfiguracaoTestesNegocio {

    public void salvar(String usuarioLogado, String matriculaServidor, int pkEs, int pkCiclo, int pkMatriz) {
        logar(usuarioLogado, matriculaServidor);
        EntidadeSupervisionavel entSuper =
                EntidadeSupervisionavelMediator.get().buscarEntidadeSupervisionavelPorPK(pkEs);

        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(pkCiclo);
        Matriz matriz = MatrizCicloMediator.get().getUltimaMatrizCiclo(ciclo);
        ciclo.setEntidadeSupervisionavel(entSuper);

        incluir(entSuper, ciclo, matriz, matriculaServidor);
    }

    private void incluir(EntidadeSupervisionavel entSuper, Ciclo ciclo, Matriz matriz, String matriculaServidor) {
        erro = null;
        try {
            new RegraGestaoARC(ciclo, matriz).validarGestao();
        } catch (NegocioException e) {
            erro = e;
        }

    }

}
