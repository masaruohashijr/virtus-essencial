package br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.analisequantitativaaqt.AvaliacaoAQTDAO;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AvaliacaoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfisNotificacaoEnum;
import br.gov.bcb.sisaps.src.validacao.RegraAnaliseANEFSupervisorPermissaoAlteracao;
import br.gov.bcb.sisaps.src.validacao.RegraEdicaoANEFInspetorPermissaoAlteracao;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;

@Service
@Transactional(readOnly = true)
public class AvaliacaoAQTMediator {

    private static final String NOVA_NOTA_DO_ANEF_AJUSTADA_SALVA_COM_SUCESSO =
            "Nova nota do ANEF (ajustada) salva com sucesso.";
    @Autowired
    private AvaliacaoAQTDAO avaliacaoAQTDAO;

    public static AvaliacaoAQTMediator get() {
        return SpringUtils.get().getBean(AvaliacaoAQTMediator.class);
    }

    public AvaliacaoAQT loadPK(Integer pk) {
        return avaliacaoAQTDAO.buscarAvaliacaoPorPK(pk);
    }

    public AvaliacaoAQT buscarNotaAvaliacaoANEF(AnaliseQuantitativaAQT aqt, PerfisNotificacaoEnum perfil) {
        return avaliacaoAQTDAO.buscarNotaAvaliacaoANEF(aqt, perfil);
    }

    @Transactional
    public String salvarAterarAvalicaoInspetor(AnaliseQuantitativaAQT aqt, AvaliacaoAQT avaliacaoInspetor) {
        new RegraEdicaoANEFInspetorPermissaoAlteracao(aqt).validar();
        
        if (SisapsUtil.isTextoCKEditorBrancoOuNulo(avaliacaoInspetor.getJustificativa())) {
            avaliacaoInspetor.setJustificativa(null);
        }

        AnaliseQuantitativaAQTMediator.get().alterarEstadoANEFParaEmEdicao(aqt);
        incluirExcluirAvaliacaoAQT(avaliacaoInspetor, aqt, PerfisNotificacaoEnum.INSPETOR);

        return NOVA_NOTA_DO_ANEF_AJUSTADA_SALVA_COM_SUCESSO;
    }

    @Transactional
    public String salvarAterarAvalicaoSupervisor(AnaliseQuantitativaAQT aqt, AvaliacaoAQT avaliacaoSupervisor) {
        new RegraAnaliseANEFSupervisorPermissaoAlteracao(aqt).validar();
        if (SisapsUtil.isTextoCKEditorBrancoOuNulo(avaliacaoSupervisor.getJustificativa())) {
            avaliacaoSupervisor.setJustificativa(null);
        }

        incluirExcluirAvaliacaoAQT(avaliacaoSupervisor, aqt, PerfisNotificacaoEnum.SUPERVISOR);
        AnaliseQuantitativaAQTMediator.get().alterarEstadoAnefParaEmAnalise(aqt);

        return NOVA_NOTA_DO_ANEF_AJUSTADA_SALVA_COM_SUCESSO;
    }

    private void incluirExcluirAvaliacaoAQT(AvaliacaoAQT avaliacao, AnaliseQuantitativaAQT aqt,
            PerfisNotificacaoEnum perfil) {

        if (avaliacao.getPk() != null && avaliacao.getJustificativa() == null && avaliacao.getParametroNota() == null) {
            AvaliacaoAQT ava = loadPK(avaliacao.getPk());
            if (ava != null) {
                aqt.getAvaliacoesAnef().remove(ava);
                avaliacaoAQTDAO.delete(ava);
            }
        } else if (avaliacao.getJustificativa() != null || avaliacao.getParametroNota() != null) {
            AvaliacaoAQT ava = loadPK(avaliacao.getPk());
            if (ava == null) {
                ava = avaliacao;
                ava.setAnaliseQuantitativaAQT(aqt);
                ava.setPerfil(perfil);
                avaliacaoAQTDAO.save(ava);
            } else {
                ava.setJustificativa(avaliacao.getJustificativa());
                ava.setParametroNota(avaliacao.getParametroNota());
                avaliacaoAQTDAO.update(ava);
            }
            aqt.getAvaliacoesAnef().add(ava);
        }
    }

    @Transactional
    public void duplicarAvaliacoesNovoCiclo(Ciclo novoCiclo, AnaliseQuantitativaAQT anefAnterior,
            AnaliseQuantitativaAQT novoAnef) {
        for (AvaliacaoAQT avaliacaoAQT : anefAnterior.getAvaliacoesAnef()) {
            AvaliacaoAQT novaAvaliacao = new AvaliacaoAQT();
            novaAvaliacao.setAnaliseQuantitativaAQT(novoAnef);
            novaAvaliacao.setJustificativa(avaliacaoAQT.getJustificativa());
            novaAvaliacao.setParametroNota(avaliacaoAQT.getParametroNota());
            novaAvaliacao.setPerfil(avaliacaoAQT.getPerfil());
            novaAvaliacao.setOperadorAtualizacao(avaliacaoAQT.getOperadorAtualizacao());
            novaAvaliacao.setUltimaAtualizacao(avaliacaoAQT.getUltimaAtualizacao());
            novaAvaliacao.setAlterarDataUltimaAtualizacao(false);
            avaliacaoAQTDAO.save(novaAvaliacao);
        }
    }
    
    @Transactional
    public void atualizarDadosNovaMetodologia(List<AvaliacaoAQT> avaliacoes, Metodologia metodologia) {
        for (AvaliacaoAQT avaliacao : avaliacoes) {
            ParametroNotaAQT notaAntiga = avaliacao.getParametroNota();
            if (notaAntiga != null) {
                ParametroNotaAQT novaNota =
                        ParametroNotaAQTMediator.get().buscarPorNota(metodologia,
                                notaAntiga.getValor());
                avaliacao.setParametroNota(novaNota);
                avaliacao.setAlterarDataUltimaAtualizacao(false);
                avaliacaoAQTDAO.update(avaliacao);
            }
        }
    }
    
    @Transactional
    public void incluir(AvaliacaoAQT avaliacao) {
        avaliacaoAQTDAO.save(avaliacao);
    }

}
