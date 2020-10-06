package crt2.dominio.analisequalitativa.arc.test;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.gov.bcb.app.stuff.seguranca.ProvedorInformacoesUsuario;
import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoARC;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Elemento;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.dominio.TendenciaARC;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfisNotificacaoEnum;
import br.gov.bcb.sisaps.src.mediator.AtividadeMediator;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoARCMediator;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.DelegacaoMediator;
import br.gov.bcb.sisaps.src.mediator.DesignacaoMediator;
import br.gov.bcb.sisaps.src.mediator.ElementoMediator;
import br.gov.bcb.sisaps.src.mediator.MatrizCicloMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroNotaMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroTendenciaMediator;
import br.gov.bcb.sisaps.src.mediator.TendenciaMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestE3_32_Analise_ARC extends ConfiguracaoTestesNegocio {

    private static final String USUARIO_DELIQ_SISLIQ110 = "deliq.sisliq110";

    public void salvarNotaElemento(String matriculaServidor, int pkCiclo, int pkArc, int notaElemento, int pkElemento) {

        Ciclo ciclo = CicloMediator.get().loadPK(pkCiclo);

        salvarNotaElemento(matriculaServidor, ciclo.getMatriz().getPk(), pkCiclo, pkArc, notaElemento, pkElemento);

    }

    public void salvarNotaElemento(String matriculaServidor, int pkMatriz, int pkCiclo, int pkArc, int notaElemento,
            int pkElemento) {

        inicializarUsuarioAplicacao(matriculaServidor);

        Matriz matriz = MatrizCicloMediator.get().loadPK(pkMatriz);

        Ciclo ciclo = CicloMediator.get().loadPK(pkCiclo);

        Elemento elemento = ElementoMediator.get().buscarPorPk(pkElemento);

        elemento.setParametroNotaSupervisor(ParametroNotaMediator.get().buscarPorPK(notaElemento));

        salvarElemento(ciclo, matriz, pkArc, elemento, matriculaServidor);

    }

    private void inicializarUsuarioAplicacao(String matriculaServidor) {
        UsuarioAplicacao usuarioAplicacao = new UsuarioAplicacao(new ProvedorInformacoesUsuario() {

            @Override
            public boolean isUserInRole(String role) {
                return "SRAT001".equals(role);
            }

            @Override
            public String getLogin() {
                return "deliq.sisliq101";
            }
        });
        usuarioAplicacao.setServidorVO(BcPessoaAdapter.get().buscarServidor(matriculaServidor));
        usuarioAplicacao.setMatricula(matriculaServidor);
        UsuarioCorrente.set(usuarioAplicacao);
    }

    public void salvarAnaliseElemento(String matriculaServidor, int pkMatriz, int pkCiclo, int pkArc, int pkElemento,
            String justificativaAnalise) {
        Matriz matriz = MatrizCicloMediator.get().loadPK(pkMatriz);

        Ciclo ciclo = CicloMediator.get().loadPK(pkCiclo);

        Elemento elemento = ElementoMediator.get().buscarPorPk(pkElemento);

        elemento.setJustificativaSupervisor(justificativaAnalise);

        salvarElemento(ciclo, matriz, pkArc, elemento, matriculaServidor);

    }

    private void salvarElemento(Ciclo ciclo, Matriz matriz, int pkArc, Elemento elemento, String matricula) {
        erro = null;
        try {
            AvaliacaoRiscoControle arc = AvaliacaoRiscoControleMediator.get().buscar(pkArc);
            ElementoMediator.get().salvarNovaNotaElementoARCSupervisor(ciclo, matriz, arc, elemento, matricula, true, true);
        } catch (NegocioException e) {
            erro = e;
        }
    }

    public void delegarArc(String matriculaLogado, int idArc, String matriculaServidor, String matriculaServidorUnidade) {
        delegarOuDesignar(matriculaLogado, null, null, AvaliacaoRiscoControleMediator.get().loadPK(idArc),
                BcPessoaAdapter.get().buscarServidor(matriculaServidor),
                BcPessoaAdapter.get().buscarServidor(matriculaServidorUnidade), true);
    }

    public void designarArc(String matriculaLogado, int idArc, int atividade,  String matriculaServidor,
            String matriculaServidorUnidade) {

        delegarOuDesignar(matriculaLogado, null, AtividadeMediator.get().loadPK(atividade),
                AvaliacaoRiscoControleMediator.get().loadPK(idArc),
                BcPessoaAdapter.get().buscarServidor(matriculaServidor),
                BcPessoaAdapter.get().buscarServidor(matriculaServidorUnidade), false);
    }

    private void delegarOuDesignar(String matriculaLogadoMatriz, Matriz matriz, Atividade atividade,
            AvaliacaoRiscoControle avaliacaoRiscoControle, ServidorVO servidorEquipe, ServidorVO servidorUnidade,
            boolean isDelegar) {
        inicializarUsuarioAplicacao(matriculaLogadoMatriz);
        erro = null;
        try {
            if (isDelegar) {
                DelegacaoMediator.get().incluir(
                        DelegacaoMediator.get().isARCDelegado(avaliacaoRiscoControle, matriculaLogadoMatriz), 
                        avaliacaoRiscoControle, servidorEquipe, servidorUnidade);
            } else {

                DesignacaoMediator.get().incluir(matriculaLogadoMatriz, atividade,
                        avaliacaoRiscoControle, servidorEquipe, servidorUnidade, true);
            }
        } catch (NegocioException e) {
            erro = e;
        }
    }

    public void salvarTendenciaSupervisor(String idARC, String idCiclo, String idTendencia, String matriculaServidor,
            String idParamentroTendencia, String justificativa) {
        logar(USUARIO_DELIQ_SISLIQ110, matriculaServidor);
        Ciclo ciclo = CicloMediator.get().loadPK(Integer.valueOf(idCiclo));
        AvaliacaoRiscoControle arc = AvaliacaoRiscoControleMediator.get().buscarPorPk(Integer.valueOf(idARC));
        TendenciaARC tendenciaARC = null;
        if (StringUtils.isNotBlank(idTendencia)) {
            tendenciaARC = TendenciaMediator.get().buscarPorPk(Integer.valueOf(idTendencia));
        }
        if (tendenciaARC == null) {
            tendenciaARC = new TendenciaARC();
            tendenciaARC.setAvaliacaoRiscoControle(arc);
            tendenciaARC.setPerfil(PerfisNotificacaoEnum.SUPERVISOR);
        }
        if (StringUtils.isNotBlank(idParamentroTendencia)) {
            tendenciaARC.setParametroTendencia(ParametroTendenciaMediator.get().load(
                    Integer.valueOf(idParamentroTendencia)));
        } else {
            tendenciaARC.setParametroTendencia(null);
        }
        tendenciaARC.setJustificativa(justificativa);
        salvarTendencia(ciclo, arc, tendenciaARC);
    }

    private void salvarTendencia(Ciclo ciclo, AvaliacaoRiscoControle arc, TendenciaARC tendenciaARC) {
        erro = null;
        try {
            TendenciaMediator.get().salvarTendenciaARCEmAnalise(ciclo, arc, tendenciaARC,
                    PerfisNotificacaoEnum.SUPERVISOR);
        } catch (NegocioException e) {
            erro = e;
        }
    }

    public void salvarAvaliacao(String matriculaServidor, int pkCiclo, int pkArc, int notaArc, String justificativa) {
        inicializarUsuarioAplicacao(matriculaServidor);
        Ciclo ciclo = CicloMediator.get().loadPK(pkCiclo);

        AvaliacaoRiscoControle arc = AvaliacaoRiscoControleMediator.get().buscar(pkArc);

        AvaliacaoARC novaAvaliacao = new AvaliacaoARC();
        novaAvaliacao.setAvaliacaoRiscoControle(arc);
        novaAvaliacao.setJustificativa(justificativa);
        novaAvaliacao.setParametroNota(ParametroNotaMediator.get().buscarPorPK(notaArc));
        novaAvaliacao.setPerfil(PerfisNotificacaoEnum.SUPERVISOR);
        salvarAvaliacaoSupervisor(ciclo, pkArc, novaAvaliacao);

    }

    private void salvarAvaliacaoSupervisor(Ciclo ciclo, int idArc, AvaliacaoARC avaliacaoARC) {
        erro = null;
        try {

            AvaliacaoARCMediator.get().salvarAvaliacaoARCSupervisor(ciclo, avaliacaoARC.getAvaliacaoRiscoControle(),
                    avaliacaoARC);
        } catch (NegocioException e) {
            erro = e;
        }
    }

    public void concluir(String matriculaServidor, int idCiclo, int pkArc) {
        Ciclo ciclo = CicloMediator.get().loadPK(idCiclo);
        erro = null;
        try {
            AvaliacaoRiscoControleMediator.get().concluirAnaliseARCSupervisor(ciclo, Integer.valueOf(pkArc));
        } catch (NegocioException e) {
            erro = e;
        }

    }

    @Override
    public void logar(String matricula) {
        inicializarUsuarioAplicacao(matricula);
    }

    public List<ParametroNota> getListaParametrosNotaElementoARC(Integer idElemento) {
        Elemento elemento = ElementoMediator.get().buscarPorPk(idElemento);
        return ParametroNotaMediator.get().buscarNotaARCElementoSupervisor(elemento,
                elemento.getParametroElemento().getMetodologia().getPk());
    }

    public String getNota(ParametroNota parametroNota) {
        return parametroNota.getDescricaoValor();
    }


}
