package br.gov.bcb.sisaps.src.validacao;

import java.util.ArrayList;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoMatrizEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.RegraPerfilAcessoMediator;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ConstantesMensagens;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraGestaoARC {

    private final Ciclo ciclo;
    private final Matriz matriz;

    public RegraGestaoARC(Ciclo ciclo, Matriz matriz) {
        this.ciclo = ciclo;
        this.matriz = matriz;
    }

    public void validarGestao() {
        boolean isSupervisor = isSupervisor();

        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
        SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_GESTAO_ARC_ERROR_001),
                !isSupervisor);
        SisapsUtil.lancarNegocioException(erros);

        SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_GESTAO_ARC_ERROR_002),
                isSupervisor && ciclo.getEstadoCiclo().getEstado().equals(EstadoCicloEnum.EM_ANDAMENTO)
                        && !matriz.getEstadoMatriz().equals(EstadoMatrizEnum.VIGENTE)
                        && matriz.getVersaoPerfilRisco() == null);
        SisapsUtil.lancarNegocioException(erros);

        SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_GESTAO_ARC_ERROR_003),
                isSupervisor && matriz.getEstadoMatriz().equals(EstadoMatrizEnum.VIGENTE)
                        && !ciclo.getEstadoCiclo().getEstado().equals(EstadoCicloEnum.EM_ANDAMENTO));
        SisapsUtil.lancarNegocioException(erros);

    }

    private boolean isSupervisor() {
        return RegraPerfilAcessoMediator.get().isAcessoPermitido(PerfilAcessoEnum.SUPERVISOR);
    }
}
