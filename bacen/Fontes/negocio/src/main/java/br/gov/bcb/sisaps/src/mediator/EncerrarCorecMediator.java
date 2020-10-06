package br.gov.bcb.sisaps.src.mediator;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.adaptadores.bto.BcBtoAdapter;
import br.gov.bcb.sisaps.src.dao.CicloDAO;
import br.gov.bcb.sisaps.src.dao.ControleBatchEncerrarCorecDAO;
import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.src.dominio.AjusteCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ControleBatchEncerrarCorec;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.EstadoCiclo;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoExecucaoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEmailCorecEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.mediator.analisequantitativa.QuadroPosicaoFinanceiraMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnexoAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AvaliacaoAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.DesignacaoAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ElementoAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.NotaAjustadaAEFMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.PesoAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.SinteseDeRiscoAQTMediator;
import br.gov.bcb.sisaps.src.util.BufferAnexos;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.enumeracoes.TipoSubEventoPerfilRiscoSRC;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;
import br.gov.bcb.utils.logging.BCLogFactory;
import br.gov.bcb.utils.logging.BCLogger;

@Service
@Transactional(readOnly = true)
public class EncerrarCorecMediator {
    
    private static final BCLogger LOG = BCLogFactory.getLogger("EncerrarCorecMediator");
    
    private static final String CONTROLE_DE_ENCERRAMENTO_DO_BATCH_ATUALIZADO_PARA = 
            "Controle de encerramento do batch atualizado para ";

    @Autowired
    private CicloDAO cicloDAO;
    
    @Autowired
    private ControleBatchEncerrarCorecDAO controleBatchEncerrarCorecDAO;
    
    @Autowired
    private EventoConsolidadoMediator eventoConsolidadoMediator;

    public static EncerrarCorecMediator get() {
        return SpringUtils.get().getBean(EncerrarCorecMediator.class);
    }
    
    @Transactional
    public String agendarEncerramentoCorec(Ciclo ciclo) {
        validarEncerramentoCorec(ciclo);
        Ciclo cicloBD = CicloMediator.get().buscarCicloPorPK(ciclo.getPk());
        if (controleBatchEncerrarCorecDAO.existeBatchAgendadoOuEmAndamento(ciclo.getPk())) {
            SisapsUtil.lancarNegocioException(new ErrorMessage(
                    "Já existe um processo de encerramento de Corec agendado ou em andamento."));
        }
        
        if (!cicloBD.getEstadoCiclo().getEstado().equals(EstadoCicloEnum.COREC)) {
            SisapsUtil.lancarNegocioException(new ErrorMessage(
                    "O Corec já foi encerrado e um novo ciclo criado."));
        }
        
        ControleBatchEncerrarCorec controleBatchEncerrarCorec = new ControleBatchEncerrarCorec();
        controleBatchEncerrarCorec.setCiclo(ciclo);
        controleBatchEncerrarCorec.setEstadoExecucao(EstadoExecucaoEnum.AGENDADO);
        controleBatchEncerrarCorecDAO.save(controleBatchEncerrarCorec);
        
        Map<String, String> parametros = new HashMap<String, String>();
        parametros.put("param0", controleBatchEncerrarCorec.getPk().toString());
        parametros.put("param1", UsuarioCorrente.get().getLogin());
        
        BcBtoAdapter.get().agendarRotinaBatch("BatchEncerrarCorec", "sisapsBatch", 
                DataUtil.getDateTimeAtual().toDate(), parametros);
        
        return "O processo de encerramento de Corec e início de novo ciclo foi disparado. " 
                + "Consulte o status da operação no menu 'Processamentos'.";
    }
    
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void processarEncerramentoCorec(Integer idControleBatch) {
        LOG.info("ROTINA DE ENCERRAMENTO DO COREC E CRIAÇÃO DE NOVO CICLO");
        LOG.info("Iniciado em: " + DataUtil.getDateTimeAtual());
        System.out.println("login: " + UsuarioCorrente.get().getLogin());
        ControleBatchEncerrarCorec controleBatch = controleBatchEncerrarCorecDAO.getRecord(idControleBatch);
        controleBatch.setEstadoExecucao(EstadoExecucaoEnum.EM_ANDAMENTO);
        controleBatchEncerrarCorecDAO.update(controleBatch);
        controleBatchEncerrarCorecDAO.flush();
        LOG.info(CONTROLE_DE_ENCERRAMENTO_DO_BATCH_ATUALIZADO_PARA + EstadoExecucaoEnum.EM_ANDAMENTO.getDescricao());
        
        try {
            encerrarCorecCriarNovoCiclo(controleBatch.getCiclo());
            controleBatch.setEstadoExecucao(EstadoExecucaoEnum.CONCLUIDO);
            controleBatchEncerrarCorecDAO.update(controleBatch);
            LOG.info(CONTROLE_DE_ENCERRAMENTO_DO_BATCH_ATUALIZADO_PARA + EstadoExecucaoEnum.CONCLUIDO.getDescricao());
            // CHECKSTYLE:OFF
        } catch (Throwable e) {
        	// CHECKSTYLE:ON
        	controleBatch.setEstadoExecucao(EstadoExecucaoEnum.FALHA);
    		controleBatchEncerrarCorecDAO.update(controleBatch);
    		LOG.info("Ocorreu um erro!");
    		LOG.info(CONTROLE_DE_ENCERRAMENTO_DO_BATCH_ATUALIZADO_PARA + EstadoExecucaoEnum.FALHA.getDescricao());
        	e.printStackTrace();
        } 
    }

    @Transactional
    public String encerrarCorecCriarNovoCiclo(Ciclo cicloAtual) {
        System.out.println("###### encerrarCorecCriarNovoCiclo(Ciclo cicloAtual)");
        long tempo = System.currentTimeMillis();
        Util.setIncluirBufferAnexos(true);
        BufferAnexos.resetLocalThreadBufferInclusao();
        BufferAnexos.resetLocalThreadBufferExclusao();
        
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(cicloAtual.getPk());
        
        PerfilRisco perfilRiscoCicloAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());
        alterarEstadoCiclo(ciclo, perfilRiscoCicloAtual);
        
        Ciclo novoCiclo = new Ciclo();
        
        PerfilRisco perfilRiscoNovoCiclo = new PerfilRisco();
        perfilRiscoNovoCiclo.setCiclo(novoCiclo);
        perfilRiscoNovoCiclo.setDataCriacao(DataUtil.getDateTimeAtual());
        // Para os dados do ciclo:
        LOG.info("Setando dados do novo ciclo.");
        setDadosNovoCiclo(ciclo, novoCiclo, perfilRiscoNovoCiclo, perfilRiscoCicloAtual);
        
        // Para os detalhes da ES:
        LOG.info("Incluindo detalhes da ES do novo ciclo.");
        criarDetalhesESNovoCiclo(novoCiclo, perfilRiscoCicloAtual);
        
        // Para as atividades do ciclo:
        LOG.info("Incluindo as atividades do novo ciclo.");
        criarAtividadesNovoCiclo(novoCiclo, perfilRiscoCicloAtual);
        
        // Para o quadro da posição financeira:
        LOG.info("Incluindo o quadro da posição financeira do novo ciclo.");
        criarQuadroPosicaoFinanceiraNovoCiclo(novoCiclo, perfilRiscoCicloAtual);
        
        // Para a análise econômico-financeira:
        LOG.info("Incluindo a análise econômico-financeira do novo ciclo.");
        criarAEFNovoCiclo(novoCiclo, perfilRiscoCicloAtual);
        
        // Para a matriz de riscos e controles:
        LOG.info("Incluindo os dados da matriz do novo ciclo.");
        criarMatrizNovoCiclo(novoCiclo, perfilRiscoCicloAtual);
        
        // Geração de evento perfil de risco
        eventoConsolidadoMediator.incluirEventoPerfilDeRisco(novoCiclo, TipoSubEventoPerfilRiscoSRC.VERSAO_COREC,
                novoCiclo.getUltimaAtualizacao(), novoCiclo.getOperador(), true);

        System.out.println("Tempo de execução até as operações com arquivos: " + (System.currentTimeMillis() - tempo));
        Util.setIncluirBufferAnexos(false);
        GeradorAnexoMediator.get().excluirAnexosBuffer();
        GeradorAnexoMediator.get().incluirAnexosBuffer();
        
        // Enviar email aos participantes para assinar a ata:
        AgendaCorec agenda = AgendaCorecMediator.get().buscarAgendaCorecPorCiclo(ciclo.getPk());
        EmailCorecMediator.get().enviarEmailParticipantesCorec(agenda, TipoEmailCorecEnum.SOLICITACAO_ASSINATURA);

        return "Corec encerrado e novo ciclo criado com sucesso.";
    }

    private void alterarEstadoCiclo(Ciclo ciclo, PerfilRisco perfilRiscoCicloAtual) {
        EstadoCiclo estadoCiclo = EstadoCicloMediator.get().buscarPorPerfilRisco(perfilRiscoCicloAtual.getPk());
        estadoCiclo.setEstado(EstadoCicloEnum.POS_COREC);
        EstadoCicloMediator.get().alterar(estadoCiclo);
        ciclo.setUltimaAtualizacao(DataUtil.getDateTimeAtual());
        cicloDAO.update(ciclo);
    }

    private void validarEncerramentoCorec(Ciclo cicloAtual) {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
        if (!AnexoPosCorecMediator.get().isPossivelAnexarAta(cicloAtual)
                && !ParticipanteAgendaCorecMediator.get().existeParticipanteAgendaCorec(cicloAtual.getPk())) {
            SisapsUtil.adicionarErro(erros, new ErrorMessage(
                    "É necessário selecionar pelo menos um participante efetivo do comitê."));
        }
        SisapsUtil.lancarNegocioException(erros);
    }

    private void setDadosNovoCiclo(Ciclo cicloAtual, Ciclo novoCiclo, 
            PerfilRisco perfilRiscoNovoCiclo, PerfilRisco perfilRiscoCicloAtual) {
        novoCiclo.setEntidadeSupervisionavel(cicloAtual.getEntidadeSupervisionavel());
        perfilRiscoNovoCiclo.addVersaoPerfilRisco(
                VersaoPerfilRiscoMediator.get().buscarVersaoPerfilRisco(
                        perfilRiscoCicloAtual.getPk(), TipoObjetoVersionadorEnum.ENTIDADE_SUPERVISIONAVEL));
        novoCiclo.setMetodologia(cicloAtual.getMetodologia());
        // Novo estado 'Em andamento'.
        EstadoCicloMediator.get().incluir(novoCiclo);
        perfilRiscoNovoCiclo.addVersaoPerfilRisco(novoCiclo.getEstadoCiclo().getVersaoPerfilRisco());
        // Data de início do ciclo será a data do Corec anterior mais 1 dia (dia seguinte ao Corec).
        novoCiclo.setDataInicio(obterDataInicioNovoCiclo(cicloAtual.getDataPrevisaoCorec()));
        // Data prevista para o Corec será a data de início do ciclo mais o período previsto de anos cadastrado na ES do ciclo Corec.
        novoCiclo.setDataPrevisaoCorec(obterDataPrevisaoCorecNovoCiclo(
                novoCiclo.getDataInicio(), cicloAtual.getEntidadeSupervisionavel()));
        
        cicloDAO.save(novoCiclo);
        PerfilRiscoMediator.get().saveOrUpdate(perfilRiscoNovoCiclo);
    }

    private void criarDetalhesESNovoCiclo(Ciclo novoCiclo, PerfilRisco perfilRiscoCicloAtual) {
        // Criar um registro com os seguintes dados do perfil de risco vigente do ciclo Corec e colocar no perfil vigente do novo ciclo:
        // Grau de preocupação.
        GrauPreocupacaoESMediator.get().criarGrauPreocupacaoNovoCiclo(perfilRiscoCicloAtual, novoCiclo);
        
        // Perfil de atuação e seus anexos.
        PerfilAtuacaoESMediator.get().criarPerfilAtuacaoNovoCiclo(perfilRiscoCicloAtual, novoCiclo);
        
        // Conclusão e seus anexos.
        ConclusaoESMediator.get().criarConclusaoNovoCiclo(perfilRiscoCicloAtual, novoCiclo);
        
        // Perspectiva.
        PerspectivaESMediator.get().criarPerspectivaNovoCiclo(perfilRiscoCicloAtual, novoCiclo);
        
        // Situação.
        SituacaoESMediator.get().criarSituacaoNovoCiclo(perfilRiscoCicloAtual, novoCiclo);
    }
    
    private void criarMatrizNovoCiclo(Ciclo novoCiclo, PerfilRisco perfilRiscoCicloAtual) {
        // Criar um registro com os seguintes dados do perfil de risco vigente do ciclo Corec e colocar no perfil vigente do novo ciclo:
        // A matriz vigente, suas unidades e atividades.
        // As células de risco e controle.
        Matriz matrizNovoCiclo = MatrizCicloMediator.get().criarMatrizNovoCiclo(perfilRiscoCicloAtual, novoCiclo);
        novoCiclo.setMatriz(matrizNovoCiclo);
        cicloDAO.update(novoCiclo);
        
        // Caso o comitê não tenha feito ajuste de nota na matriz, a nota ajustada da matriz pelo supervisor, assim como sua justificativa, se existir.
        AjusteCorec ajusteCorecCicloAtual = 
                AjusteCorecMediator.get().buscarPorCiclo(perfilRiscoCicloAtual.getCiclo().getPk());
        if (ajusteCorecCicloAtual == null || ajusteCorecCicloAtual.getNotaQualitativa() == null) {
            NotaMatrizMediator.get().criarNotaMatrizNovoCiclo(perfilRiscoCicloAtual, novoCiclo);
        }
        
        // As sínteses de risco da matriz vigente.
        SinteseDeRiscoMediator.get().criarSintesesNovoCiclo(perfilRiscoCicloAtual, novoCiclo);
        
        if (matrizNovoCiclo.getPercentualGovernancaCorporativoInt() > 0) {
            AvaliacaoRiscoControleExternoMediator.get().criarArcExternoNovoCiclo(perfilRiscoCicloAtual, novoCiclo);
        }
    }
    
    private void criarAEFNovoCiclo(Ciclo novoCiclo, PerfilRisco perfilRiscoCicloAtual) {
        // Para os percentuais dos ANEFs:
        PesoAQTMediator.get().criarPesosNovoCiclo(perfilRiscoCicloAtual, novoCiclo);
        
        // Para as sínteses dos ANEFs:
        SinteseDeRiscoAQTMediator.get().criarSintesesNovoCiclo(perfilRiscoCicloAtual, novoCiclo);
        
        // Para os ANEFs:
        criarANEFsNovoCiclo(perfilRiscoCicloAtual, novoCiclo);
        
        // Caso o comitê não tenha feito ajuste de nota da análise econômico-financeira: 
        AjusteCorec ajusteCorecCicloAtual = 
                AjusteCorecMediator.get().buscarPorCiclo(perfilRiscoCicloAtual.getCiclo().getPk());
        if (ajusteCorecCicloAtual == null || ajusteCorecCicloAtual.getNotaQuantitativa() == null) {
            NotaAjustadaAEFMediator.get().criarNotaAjustadaAEFNovoCiclo(perfilRiscoCicloAtual, novoCiclo);
        }
    }
    
    private void criarQuadroPosicaoFinanceiraNovoCiclo(Ciclo novoCiclo, PerfilRisco perfilRiscoCicloAtual) {
        QuadroPosicaoFinanceiraMediator.get().criarQuadroPosicaoFinanceiraNovoCiclo(perfilRiscoCicloAtual, novoCiclo);
    }
    
    private void criarAtividadesNovoCiclo(Ciclo novoCiclo, PerfilRisco perfilRiscoCicloAtual) {
        // Para cada atividade do ciclo no perfil de risco vigente do ciclo Corec, 
        // criar um novo registro com os mesmos dados e incluir no perfil de risco vigente do novo ciclo.
        AtividadeCicloMediator.get().criarAtividadesNovoCiclo(perfilRiscoCicloAtual, novoCiclo);
    }
    
    private Date obterDataInicioNovoCiclo(Date dataPrevisaoCorecCicloAnterior) {
        LocalDate data = new LocalDate(dataPrevisaoCorecCicloAnterior);
        data = data.plusDays(1);
        return data.toDate();
    }
    
    private Date obterDataPrevisaoCorecNovoCiclo(Date inicioNovoCiclo, EntidadeSupervisionavel entidadeSupervisionavel) {
        LocalDate data = new LocalDate(inicioNovoCiclo);
        data = data.plusYears(entidadeSupervisionavel.getQuantidadeAnosPrevisaoCorec());
        return data.toDate();
    }
    
    private void criarANEFsNovoCiclo(PerfilRisco perfilRiscoCicloAtual, Ciclo novoCiclo) {
        // Criar ANEFs vigentes com os mesmos dados dos ANEFs vigentes do ciclo Corec.
        PerfilRisco perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(novoCiclo.getPk());
        List<AnaliseQuantitativaAQT> anefsVigentesCicloAtual = AnaliseQuantitativaAQTMediator.get().buscarAQTsPerfilRisco(
                VersaoPerfilRiscoMediator.get().buscarVersoesPerfilRisco(
                perfilRiscoCicloAtual.getPk(), TipoObjetoVersionadorEnum.AQT));
        for (AnaliseQuantitativaAQT anefVigenteCicloAtual : anefsVigentesCicloAtual) {
            AnaliseQuantitativaAQT novoAnefVigente = criarANEFNovoCiclo(
                    perfilRiscoAtual, novoCiclo, anefVigenteCicloAtual, true);
            VersaoPerfilRiscoMediator.get().criarVersao(novoAnefVigente, TipoObjetoVersionadorEnum.AQT);
            perfilRiscoAtual.addVersaoPerfilRisco(novoAnefVigente.getVersaoPerfilRisco());
            
        }
        PerfilRiscoMediator.get().saveOrUpdate(perfilRiscoAtual);
        
        // Criar ANEFs de rascunho com os mesmos dados dos ANEFs de rascunho do ciclo Corec.
        List<AnaliseQuantitativaAQT> anefsRascunhosCicloAtual = 
                AnaliseQuantitativaAQTMediator.get().buscarANEFsRascunho(perfilRiscoCicloAtual.getCiclo());
        for (AnaliseQuantitativaAQT anefRascunhoCicloAtual : anefsRascunhosCicloAtual) {
            criarANEFNovoCiclo(perfilRiscoAtual, novoCiclo, anefRascunhoCicloAtual, false);
        }
    }

    private AnaliseQuantitativaAQT criarANEFNovoCiclo(PerfilRisco perfilRiscoAtual, Ciclo novoCiclo, 
            AnaliseQuantitativaAQT anefVigenteCicloAtual, boolean isANEFVigente) {
        AnaliseQuantitativaAQT novoAnefVigente = new AnaliseQuantitativaAQT();
        novoAnefVigente.setEstado(anefVigenteCicloAtual.getEstado());
        novoAnefVigente.setNotaSupervisor(anefVigenteCicloAtual.getNotaSupervisor());
        novoAnefVigente.setValorNota(anefVigenteCicloAtual.getValorNota());
        novoAnefVigente.setParametroAQT(anefVigenteCicloAtual.getParametroAQT());
        novoAnefVigente.setCiclo(novoCiclo);
        novoAnefVigente.setDataPreenchido(anefVigenteCicloAtual.getDataPreenchido());
        novoAnefVigente.setOperadorPreenchido(anefVigenteCicloAtual.getOperadorPreenchido());
        novoAnefVigente.setDataAnalise(anefVigenteCicloAtual.getDataAnalise());
        novoAnefVigente.setOperadorAnalise(anefVigenteCicloAtual.getOperadorAnalise());
        novoAnefVigente.setDataConclusao(anefVigenteCicloAtual.getDataConclusao());
        novoAnefVigente.setOperadorConclusao(anefVigenteCicloAtual.getOperadorConclusao());
        novoAnefVigente.setUltimaAtualizacao(anefVigenteCicloAtual.getUltimaAtualizacao());
        novoAnefVigente.setOperadorAtualizacao(anefVigenteCicloAtual.getOperadorAtualizacao());
        novoAnefVigente.setAlterarDataUltimaAtualizacao(false);
        if (isANEFVigente) {
            // Caso exista nota de ajuste do COREC no ANEF vigente do ciclo Corec, no ANEF vigente 
            // do novo ciclo o ajuste do Corec deve ser movido para 'ajuste de Corec anterior'.
            novoAnefVigente.setNotaCorecAnterior(anefVigenteCicloAtual.getNotaCorecAtual());
        }
        AnaliseQuantitativaAQTMediator.get().saveOrUpdate(novoAnefVigente);
        // Por alguma razão escrota, é necessário salvar o perfil de risco nesse local.
        PerfilRiscoMediator.get().saveOrUpdate(perfilRiscoAtual);
        // Duplicar Designação
        DesignacaoAQTMediator.get().duplicarDesignacaoAQTConclusao(anefVigenteCicloAtual, novoAnefVigente, true);
        // Duplicar Anexos ANEF
        AnexoAQTMediator.get().duplicarAnexosANEFConclusaoAnalise(novoCiclo, anefVigenteCicloAtual, novoAnefVigente, true);
        // Duplicar Elementos ANEF
        ElementoAQTMediator.get().duplicarElementosAQTConclusaoAnalise(
                novoCiclo, anefVigenteCicloAtual, novoAnefVigente, true);
        // Dupliar Avaliações ANEF
        AvaliacaoAQTMediator.get().duplicarAvaliacoesNovoCiclo(novoCiclo, anefVigenteCicloAtual, novoAnefVigente);
      
        
        return novoAnefVigente;
        
    }

}
