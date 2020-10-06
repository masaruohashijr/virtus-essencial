package br.gov.bcb.sisaps.src.mediator;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dao.PerspectivaESDAO;
import br.gov.bcb.sisaps.src.dominio.AjusteCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.PerspectivaES;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.validacao.RegraSalvarPerspectivaES;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.enumeracoes.TipoSubEventoPerfilRiscoSRC;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

@Service
@Transactional(readOnly = true)
public class PerspectivaESMediator {

    @Autowired
    private PerspectivaESDAO perspectivaESDAO;

    @Autowired
    private PerfilRiscoMediator perfilRiscoMediator;
    
    @Autowired
    private EventoConsolidadoMediator eventoConsolidadoMediator;

    public static PerspectivaESMediator get() {
        return SpringUtils.get().getBean(PerspectivaESMediator.class);
    }

    @Transactional(readOnly = true)
    public PerspectivaES getUltimaPerspectivaES(Ciclo ciclo) {
        return perspectivaESDAO.getUltimaPerspectivaES(ciclo);
    }

    @Transactional(readOnly = true)
    public PerspectivaES getPerspectivaESPorPerfil(int perfilPK) {
        PerspectivaES ultimoPerspectivaES = perspectivaESDAO.buscarPorPerfilRisco(perfilPK);
        if (ultimoPerspectivaES != null) {
        	ultimoPerspectivaES.setAlterarDataUltimaAtualizacao(false);
        }
        inicializar(ultimoPerspectivaES);
        return ultimoPerspectivaES;
    }

    @Transactional(readOnly = true)
    public PerspectivaES getPerspectivaESPendencia(Ciclo ciclo) {
        PerspectivaES ultimoPerspectivaES = perspectivaESDAO.buscarPorPendencia(ciclo);
        inicializar(ultimoPerspectivaES);
        return ultimoPerspectivaES;
    }

    @Transactional(readOnly = true)
    public PerspectivaES getPerspectivaESSemPerfilRisco(Ciclo ciclo) {
        PerspectivaES ultimoPerspectivaES = perspectivaESDAO.buscarSemPerfil(ciclo);
        inicializar(ultimoPerspectivaES);
        return ultimoPerspectivaES;
    }

    public PerspectivaES getPerspectivaRascunhoVigente(PerfilRisco perfil, PerspectivaES perspectivaESVigente) {
        PerspectivaES ultimoPerspectivaES = perspectivaESDAO.buscarSemPerfil(perfil.getCiclo());

        List<PerspectivaES> perspectivas = buscarPerspectivasVigentesPorPerfilRisco(perfil.getCiclo().getPk());

        if (ultimoPerspectivaES == null && perspectivas.size() >= 2) {
            ultimoPerspectivaES = getPerspectivaESPorPerfil(perfil.getPk());
        }
        inicializar(ultimoPerspectivaES);
        return ultimoPerspectivaES;
    }

    private void inicializar(PerspectivaES ultimoPerspectivaES) {
        if (ultimoPerspectivaES != null) {
            Hibernate.initialize(ultimoPerspectivaES.getParametroPerspectiva());
            if (ultimoPerspectivaES.getPerspectivaESAnterior() != null) {
                Hibernate.initialize(ultimoPerspectivaES.getPerspectivaESAnterior());
                Hibernate.initialize(ultimoPerspectivaES.getPerspectivaESAnterior().getParametroPerspectiva());
            }
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public String salvarNovaPerspectiva(PerspectivaES perspectivaES) {
        new RegraSalvarPerspectivaES(perspectivaES).validar();
        if (SisapsUtil.isTextoCKEditorBrancoOuNulo(perspectivaES.getDescricao())) {
            perspectivaES.setDescricao(null);
        }
        if (perspectivaES.getPerspectivaESAnterior() != null) {
            perspectivaESDAO.evict(perspectivaES.getPerspectivaESAnterior());
        }
        perspectivaES.setPendente(SimNaoEnum.NAO);
        perspectivaES.setOperadorEncaminhamento(null);
        perspectivaES.setDataEncaminhamento(null);
        perspectivaESDAO.saveOrUpdate(perspectivaES);
        return "Perspectiva salva com sucesso.";
    }

    @Transactional
    public String confirmarNovaPerspectiva(PerspectivaES perspectivaES) {
        PerspectivaES perspectivaESDB = perspectivaESDAO.buscarPerspectivaESPorPk(perspectivaES.getPk());
        validarNovaPerspectiva(perspectivaESDB);
        perspectivaESDB.setPendente(null);
        VersaoPerfilRisco versaoPerfilRisco =
                perfilRiscoMediator.gerarNovaVersaoPerfilRisco(perspectivaESDB.getCiclo(),
                        perspectivaESDB.getPerspectivaESAnterior(), TipoObjetoVersionadorEnum.PERSPECTIVA_ES);
        perspectivaESDB.setVersaoPerfilRisco(versaoPerfilRisco);
        eventoConsolidadoMediator.incluirEventoPerfilDeRisco(perspectivaESDB.getCiclo(),
                TipoSubEventoPerfilRiscoSRC.PERSPECTIVA);
        perspectivaESDAO.saveOrUpdate(perspectivaESDB);
        return "Atualização da Perspectiva da ES no perfil de risco realizada com sucesso.";
    }

    @Transactional
    public String encaminharNovaPerspectiva(PerspectivaES perspectivaES) {
        validarNovaPerspectiva(perspectivaES);
        UsuarioAplicacao usuarioAplicacao = (UsuarioAplicacao) UsuarioCorrente.get();
        perspectivaES.setPendente(SimNaoEnum.SIM);
        perspectivaES.setDataEncaminhamento(DataUtil.getDateTimeAtual());
        perspectivaES.setOperadorEncaminhamento(usuarioAplicacao.getLogin());
        perspectivaESDAO.saveOrUpdate(perspectivaES);
        return "Perspectiva da ES encaminhado(a) para aprovação do gerente com sucesso.";
    }

    private void validarNovaPerspectiva(PerspectivaES perspectivaES) {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
        SisapsUtil.validarObrigatoriedade(perspectivaES.getParametroPerspectiva(), "Perspectiva", erros);

        SisapsUtil.adicionarErro(erros, new ErrorMessage(
                "Campo \"Justificativa da nova perspectiva\" é de preenchimento obrigatório."),
                perspectivaES.getParametroPerspectiva() != null && perspectivaES.getDescricao() == null);
        SisapsUtil.lancarNegocioException(erros);

    }

    public PerspectivaES buscarPorPerfilRisco(Integer pkPerfilRisco) {
        return perspectivaESDAO.buscarPorPerfilRisco(pkPerfilRisco);
    }

    public PerspectivaES buscarPerspectivaESRascunho(Integer pkCiclo) {
        return perspectivaESDAO.buscarPerspectivaESRascunho(pkCiclo);
    }

    public List<PerspectivaES> buscarPerspectivasVigentesPorPerfilRisco(Integer pkCiclo) {
        return perspectivaESDAO.buscarPerspectivasVigentesPorPerfilRisco(pkCiclo);
    }

    @Transactional
    public void criarPerspectivaNovoCiclo(PerfilRisco perfilRiscoCicloAtual, Ciclo novoCiclo) {
        PerspectivaES perspectivaCicloAtual = buscarPorPerfilRisco(perfilRiscoCicloAtual.getPk());
        perspectivaESDAO.evict(perspectivaCicloAtual);
        PerspectivaES novaPerspectiva = new PerspectivaES();
        novaPerspectiva.setCiclo(novoCiclo);
        novaPerspectiva.setPerspectivaESAnterior(perspectivaCicloAtual);
        novaPerspectiva.setDescricao(perspectivaCicloAtual.getDescricao());
        novaPerspectiva.setParametroPerspectiva(perspectivaCicloAtual.getParametroPerspectiva());
        novaPerspectiva.setUltimaAtualizacao(perspectivaCicloAtual.getUltimaAtualizacao());
        novaPerspectiva.setOperadorAtualizacao(perspectivaCicloAtual.getOperadorAtualizacao());
        novaPerspectiva.setOperadorEncaminhamento(perspectivaCicloAtual.getOperadorEncaminhamento());
        novaPerspectiva.setDataEncaminhamento(perspectivaCicloAtual.getDataEncaminhamento());
        perfilRiscoMediator.incluirVersaoPerfilRiscoAtual(novoCiclo, novaPerspectiva,
                TipoObjetoVersionadorEnum.PERSPECTIVA_ES);
        novaPerspectiva.setAlterarDataUltimaAtualizacao(false);
        perspectivaESDAO.save(novaPerspectiva);
        perspectivaESDAO.evict(novaPerspectiva);
    }

    @Transactional(readOnly = true)
    public PerspectivaES getUltimaPerspectiva(PerfilRisco perfilRisco, boolean isGestaoDetalhesES,
            PerfilAcessoEnum perfilAcessoEnum) {
        PerspectivaES ultimoPerspectivaES = new PerspectivaES();
        if (isGestaoDetalhesES) {
            ultimoPerspectivaES = notaCorecAnteriorOuNotaPerfilRisco(perfilRisco);
        } else {
            ultimoPerspectivaES = notaCorecAnteriorAtualOuNotaPerfilRisco(perfilRisco, perfilAcessoEnum);
        }
        if (ultimoPerspectivaES != null) {
        	ultimoPerspectivaES.setAlterarDataUltimaAtualizacao(false);
        }
        inicializar(ultimoPerspectivaES);

        if (ultimoPerspectivaES != null) {
            perspectivaESDAO.evict(ultimoPerspectivaES);
        }

        return ultimoPerspectivaES;
    }

    public String getUltimaPerspectivaDescricao(PerfilRisco perfilRisco, boolean isGestaoDetalhesES,
            PerfilAcessoEnum perfilAcessoEnum) {
        PerspectivaES ultimoPerspectivaES = getUltimaPerspectiva(perfilRisco, isGestaoDetalhesES, perfilAcessoEnum);

        String valorPerspectiva = "";

        if (ultimoPerspectivaES != null && ultimoPerspectivaES.getPk() == null) {
        	String descricaoCorec = PerfilAcessoEnum.CONSULTA_RESUMO_NAO_BLOQUEADOS.equals(perfilAcessoEnum) ? "" : " (Corec)";
            valorPerspectiva +=
                    ultimoPerspectivaES.getParametroPerspectiva() == null ? "" : ultimoPerspectivaES
                            .getParametroPerspectiva().getNome() + descricaoCorec;
        } else {
            valorPerspectiva =
                    ultimoPerspectivaES == null || ultimoPerspectivaES.getParametroPerspectiva() == null ? ""
                            : ultimoPerspectivaES.getParametroPerspectiva().getNome();
        }

        return valorPerspectiva;
    }

    private PerspectivaES notaCorecAnteriorAtualOuNotaPerfilRisco(PerfilRisco perfilRisco,
            PerfilAcessoEnum perfilAcessoEnum) {
        if (NotaMatrizMediator.get().cicloAndamentoCorecDiferenteSupervisor(perfilRisco.getCiclo().getMatriz(),
                perfilAcessoEnum)) {
            return notaCorecAnteriorOuNotaPerfilRisco(perfilRisco);
        } else {
            AjusteCorec ajusteCorec = AjusteCorecMediator.get().buscarPorCiclo(perfilRisco.getCiclo().getPk());
            
            PerfilRisco perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(perfilRisco.getCiclo().getPk());
            if (!perfilRiscoAtual.getPk().equals(perfilRisco.getPk()) 
                    || ajusteCorec == null || ajusteCorec.getPerspectiva() == null) {
                return perspectivaESDAO.buscarPorPerfilRisco(perfilRisco.getPk());
            } else {
                return montarPerspectivaCorec(ajusteCorec);
            }
        }
    }

    public PerspectivaES notaCorecAnteriorOuNotaPerfilRisco(PerfilRisco perfilRisco) {
        if (exibirPerspectivaCorec(perfilRisco.getCiclo())) {
            Ciclo cicloAnterior =
                    CicloMediator.get().consultarUltimoCicloPosCorecEncerradoES(
                            perfilRisco.getCiclo().getEntidadeSupervisionavel().getPk());
            AjusteCorec ajusteCorec = AjusteCorecMediator.get().buscarPorCiclo(cicloAnterior.getPk());
            return montarPerspectivaCorec(ajusteCorec);
        } else {
            return perspectivaESDAO.buscarPorPerfilRisco(perfilRisco.getPk());
        }
    }

    private PerspectivaES montarPerspectivaCorec(AjusteCorec ajusteCorec) {
        PerspectivaES ultimoPerspectivaES = new PerspectivaES();
        ultimoPerspectivaES.setParametroPerspectiva(ajusteCorec.getPerspectiva());
        ultimoPerspectivaES.setDataEncaminhamento(ajusteCorec.getUltimaAtualizacao());
        ultimoPerspectivaES.setUltimaAtualizacao(ajusteCorec.getUltimaAtualizacao());
        ultimoPerspectivaES.setOperadorEncaminhamento(ajusteCorec.getOperadorAtualizacao());
        ultimoPerspectivaES.setOperadorAtualizacao(ajusteCorec.getOperadorAtualizacao());
        return ultimoPerspectivaES;
    }

    public boolean exibirPerspectivaCorec(Ciclo ciclo) {
        Ciclo cicloAnterior =
                CicloMediator.get().consultarUltimoCicloPosCorecEncerradoES(ciclo.getEntidadeSupervisionavel().getPk());
        if (cicloAnterior != null) {
            AjusteCorec ajusteCorec = AjusteCorecMediator.get().buscarPorCiclo(cicloAnterior.getPk());

            List<PerspectivaES> perspectivas = buscarPerspectivasVigentesPorPerfilRisco(ciclo.getPk());

            return ajusteCorec != null && ajusteCorec.getPerspectiva() != null && perspectivas.size() == 1;
        }
        return false;
    }

    public void evict(PerspectivaES perspectiva) {
        perspectivaESDAO.evict(perspectiva);
    }

}
