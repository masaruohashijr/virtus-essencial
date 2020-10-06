package br.gov.bcb.sisaps.src.mediator;

import java.util.ArrayList;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dao.SituacaoESDAO;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.SituacaoES;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.validacao.RegraSalvarSituacaoES;
import br.gov.bcb.sisaps.util.enumeracoes.NormalidadeEnum;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.enumeracoes.TipoSubEventoPerfilRiscoSRC;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

@Service
@Transactional(readOnly = true)
public class SituacaoESMediator {

    @Autowired
    private SituacaoESDAO situacaoESDAO;

    @Autowired
    private PerfilRiscoMediator perfilRiscoMediator;
    
    @Autowired
    private EventoConsolidadoMediator eventoConsolidadoMediator;

    public static SituacaoESMediator get() {
        return SpringUtils.get().getBean(SituacaoESMediator.class);
    }

    public SituacaoES getUltimaSituacaoES(Ciclo ciclo) {
        return situacaoESDAO.getUltimaSituacaoES(ciclo);
    }

    @Transactional(readOnly = true)
    public SituacaoES getSituacaoESPorPerfil(int perfilPK) {
        SituacaoES ultimoSituacaoES = situacaoESDAO.buscarPorPerfilRisco(perfilPK);
        if (ultimoSituacaoES != null) {
        	ultimoSituacaoES.setAlterarDataUltimaAtualizacao(false);
        }
        inicializar(ultimoSituacaoES);
        return ultimoSituacaoES;
    }

    @Transactional(readOnly = true)
    public SituacaoES getSituacaoESPendencia(Ciclo ciclo) {
        SituacaoES ultimoSituacaoES = situacaoESDAO.buscarPorPendencia(ciclo);
        inicializar(ultimoSituacaoES);
        return ultimoSituacaoES;
    }

    @Transactional(readOnly = true)
    public SituacaoES getSituacaoESSemPerfilRisco(Ciclo ciclo) {
        SituacaoES ultimoSituacaoES = situacaoESDAO.buscarSemPerfil(ciclo);
        inicializar(ultimoSituacaoES);
        return ultimoSituacaoES;
    }

    private void inicializar(SituacaoES ultimoSituacaoES) {
        if (ultimoSituacaoES != null) {
            Hibernate.initialize(ultimoSituacaoES.getParametroSituacao());
            if (ultimoSituacaoES.getSituacaoESAnterior() != null) {
                Hibernate.initialize(ultimoSituacaoES.getSituacaoESAnterior());
                Hibernate.initialize(ultimoSituacaoES.getSituacaoESAnterior().getParametroSituacao());
            }
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public String salvarNovaSituacao(SituacaoES situacaoES) {
        new RegraSalvarSituacaoES(situacaoES).validar();
        if (SisapsUtil.isTextoCKEditorBrancoOuNulo(situacaoES.getDescricao())) {
            situacaoES.setDescricao(null);
        }
        situacaoES.setPendente(SimNaoEnum.NAO);
        situacaoES.setOperadorEncaminhamento(null);
        situacaoES.setDataEncaminhamento(null);
        situacaoESDAO.saveOrUpdate(situacaoES);
        return "Situação salva com sucesso.";
    }

    @Transactional
    public String confirmarNovaSituacao(SituacaoES situacaoES) {
        SituacaoES situacaoESDB = situacaoESDAO.buscarSituacaoESPorPk(situacaoES.getPk());
        validarNovaSituacao(situacaoESDB);
        situacaoESDB.setPendente(null);
        VersaoPerfilRisco versaoPerfilRisco =
                perfilRiscoMediator.gerarNovaVersaoPerfilRisco(situacaoESDB.getCiclo(),
                        situacaoESDB.getSituacaoESAnterior(), TipoObjetoVersionadorEnum.SITUACAO_ES);
        situacaoESDB.setVersaoPerfilRisco(versaoPerfilRisco);
        eventoConsolidadoMediator.incluirEventoPerfilDeRisco(situacaoESDB.getCiclo(),
                TipoSubEventoPerfilRiscoSRC.SITUACAO);
        situacaoESDAO.saveOrUpdate(situacaoESDB);
        return "Atualização da Situação da ES no perfil de risco realizada com sucesso.";
    }
    
    @Transactional
    public String encaminharNovaSituacao(SituacaoES situacaoES) {
        validarNovaSituacao(situacaoES);
        UsuarioAplicacao usuarioAplicacao = (UsuarioAplicacao) UsuarioCorrente.get();
        situacaoES.setPendente(SimNaoEnum.SIM);
        situacaoES.setDataEncaminhamento(DataUtil.getDateTimeAtual());
        situacaoES.setOperadorEncaminhamento(usuarioAplicacao.getLogin());
        situacaoESDAO.saveOrUpdate(situacaoES);
        return "Situação da ES encaminhado(a) para aprovação do gerente com sucesso.";
    }

    private void validarNovaSituacao(SituacaoES situacaoES) {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
        SisapsUtil.validarObrigatoriedade(situacaoES.getParametroSituacao(), "Situação", erros);

        SisapsUtil.adicionarErro(
                erros,
                new ErrorMessage("Campo \"Justificativa da nova situação\" é de preenchimento obrigatório."),
                situacaoES.getParametroSituacao() != null
                        && situacaoES.getParametroSituacao().getNormalidade().equals(NormalidadeEnum.ANORMAL)
                        && situacaoES.getDescricao() == null);
        SisapsUtil.lancarNegocioException(erros);
    }

    public SituacaoES buscarPorPerfilRisco(Integer pkPerfilRisco) {
        return situacaoESDAO.buscarPorPerfilRisco(pkPerfilRisco);
    }

    public SituacaoES buscarSituacaoESRascunho(Integer pkCiclo) {
        return situacaoESDAO.buscarSituacaoESRascunho(pkCiclo);
    }

    @Transactional
    public void criarSituacaoNovoCiclo(PerfilRisco perfilRiscoCicloAtual, Ciclo novoCiclo) {
        SituacaoES situacaoCicloAtual = buscarPorPerfilRisco(perfilRiscoCicloAtual.getPk());
        situacaoESDAO.evict(situacaoCicloAtual);
        SituacaoES novaSituacao = new SituacaoES();
        novaSituacao.setCiclo(novoCiclo);
        novaSituacao.setSituacaoESAnterior(situacaoCicloAtual);
        novaSituacao.setDescricao(situacaoCicloAtual.getDescricao());
        novaSituacao.setParametroSituacao(situacaoCicloAtual.getParametroSituacao());
        novaSituacao.setUltimaAtualizacao(situacaoCicloAtual.getUltimaAtualizacao());
        novaSituacao.setOperadorAtualizacao(situacaoCicloAtual.getOperadorAtualizacao());
        novaSituacao.setOperadorEncaminhamento(situacaoCicloAtual.getOperadorEncaminhamento());
        novaSituacao.setDataEncaminhamento(situacaoCicloAtual.getDataEncaminhamento());
        perfilRiscoMediator.incluirVersaoPerfilRiscoAtual(novoCiclo, novaSituacao,
                TipoObjetoVersionadorEnum.SITUACAO_ES);
        novaSituacao.setAlterarDataUltimaAtualizacao(false);
        situacaoESDAO.save(novaSituacao);
        situacaoESDAO.evict(novaSituacao);
    }
    
    public void evict(SituacaoES situacao) {
        situacaoESDAO.evict(situacao);
    }

}
