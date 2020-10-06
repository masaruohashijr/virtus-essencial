package br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.dao.analisequantitativaaqt.DesignacaoAQTDAO;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.DesignacaoAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoAQTEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.validacao.RegraDesignacaoAQT;

@Service
@Transactional(readOnly = true)
public class DesignacaoAQTMediator {
    private static final String ATENCAO_INSPETOR_DESIGNADO =
            "Atenção ANEF pendente de conclusão pelo inspetor designado.";
    @Autowired
    private DesignacaoAQTDAO designacaoAQTDAO;

    public static DesignacaoAQTMediator get() {
        return SpringUtils.get().getBean(DesignacaoAQTMediator.class);
    }

    public boolean podeDesignar(AnaliseQuantitativaAQT aqt) {
        boolean validacaoCiclo = estadoCicloEmAndamento(aqt) && aqt.getVersaoPerfilRisco() == null;
        return CicloMediator.get().isSupervisor(aqt.getCiclo().getEntidadeSupervisionavel().getLocalizacao())
                && !AnaliseQuantitativaAQTMediator.get().estadoConcluido(aqt.getEstado()) && validacaoCiclo;

    }

    @SuppressWarnings("unchecked")
    public List<ServidorVO> buscarListaServidorDesignacao(AnaliseQuantitativaAQT aqt, String localizacao) {
        List<ServidorVO> lista = new ArrayList<ServidorVO>();
        lista.addAll(BcPessoaAdapter.get().consultarServidoresAtivos(localizacao));
        if (aqt.getDesignacao() != null
                && (AnaliseQuantitativaAQTMediator.get().estadoEmEdicao(aqt.getEstado()) || AnaliseQuantitativaAQTMediator
                        .get().estadoDesignado(aqt.getEstado()))) {
            lista.remove(BcPessoaAdapter.get().buscarServidor(aqt.getDesignacao().getMatriculaServidor()));
        }

        List<BeanComparator> sortFields = new ArrayList<BeanComparator>();
        sortFields.add(new BeanComparator("nome"));
        Collections.sort(lista, new ComparatorChain(sortFields));
        return lista;
    }

    public String mostraAlertaBotaoDesignar(AnaliseQuantitativaAQT aqt) {
        if (AnaliseQuantitativaAQTMediator.get().estadoEmEdicao(aqt.getEstado())) {
            return ATENCAO_INSPETOR_DESIGNADO;
        }

        if (AnaliseQuantitativaAQTMediator.get().estadoPreenchido(aqt.getEstado())
                || AnaliseQuantitativaAQTMediator.get().estadoAnaliseDelegada(aqt.getEstado())) {
            return "Atenção ANEF já preenchido por inspetor.";
        }

        if (AnaliseQuantitativaAQTMediator.get().estadoAnalisado(aqt.getEstado())) {
            return "Atenção ANEF já analisado.";
        }

        if (AnaliseQuantitativaAQTMediator.get().estadoEmAnalise(aqt.getEstado())) {
            if (aqt.getDelegacao() == null) {
                return "Atenção ANEF já preenchido por inspetor e com análise em andamento.";
            } else {
                return "Atenção ANEF já preenchido por inspetor e com análise em andamento por delegado.";
            }

        }

        return "";
    }

    @Transactional
    public String incluir(AnaliseQuantitativaAQT anef, ServidorVO servidorEquipe, ServidorVO servidorUnidade) {
        AnaliseQuantitativaAQT anefBD = AnaliseQuantitativaAQTMediator.get().buscar(anef.getPk());
        new RegraDesignacaoAQT(servidorEquipe, servidorUnidade).validar();
        String matriculaServidorDesignado = null;
        if (servidorEquipe == null) {
            matriculaServidorDesignado = servidorUnidade.getMatricula();
        } else {
            matriculaServidorDesignado = servidorEquipe.getMatricula();
        }

        if (anefBD.getDelegacao() != null) {
            DelegacaoAQTMediator.get().excluir(anefBD.getDelegacao());
        }

        DesignacaoAQT designacao;

        if (anefBD.getDesignacao() == null) {
            designacao = new DesignacaoAQT();
            designacao.setAnaliseQuantitativaAQT(anefBD);
        } else {
            designacao = anefBD.getDesignacao();
        }
        designacao.setMatriculaServidor(matriculaServidorDesignado);

        save(designacao);
        anefBD.setDesignacao(designacao);
        AnaliseQuantitativaAQTMediator.get().alterarEstadoAnefDesignar(anefBD);
        anef.setEstado(anefBD.getEstado());
        return "ANEF designado com sucesso.";
    }

    @Transactional
    private void save(DesignacaoAQT designacao) {
        if (designacao.getPk() == null) {
            designacaoAQTDAO.save(designacao);
        } else {
            designacaoAQTDAO.update(designacao);
        }
        designacaoAQTDAO.getSessionFactory().getCurrentSession().flush();
    }

    public boolean podeExcluirDesignar(AnaliseQuantitativaAQT aqt) {

        return CicloMediator.get().isSupervisor(aqt.getCiclo().getEntidadeSupervisionavel().getLocalizacao())
                && estadoExcluirDesignacao(aqt) && aqt.possuiDesignacao() && estadoCicloEmAndamento(aqt);

    }

    private boolean estadoExcluirDesignacao(AnaliseQuantitativaAQT aqt) {
        return (AnaliseQuantitativaAQTMediator.get().estadoEmEdicao(aqt.getEstado()) || AnaliseQuantitativaAQTMediator
                .get().estadoDesignado(aqt.getEstado()));
    }

    private boolean estadoCicloEmAndamento(AnaliseQuantitativaAQT aqt) {
        return EstadoCicloEnum.EM_ANDAMENTO.equals(aqt.getCiclo().getEstadoCiclo().getEstado());
    }

    public String mostraAlertaBotaoExcluirDesignar(AnaliseQuantitativaAQT aqt) {
        if (AnaliseQuantitativaAQTMediator.get().estadoEmEdicao(aqt.getEstado())) {
            return ATENCAO_INSPETOR_DESIGNADO;
        }

        return "";
    }

    @Transactional
    public void excluirDesignacao(AnaliseQuantitativaAQT anef) {
        designacaoAQTDAO.delete(anef.getDesignacao());
        designacaoAQTDAO.getSessionFactory().getCurrentSession().flush();
        AnaliseQuantitativaAQTMediator.get().alterarEstadoAnefExcluirDesignar(anef);
    }

    @Transactional
    public void duplicarDesignacaoAQTConclusao(AnaliseQuantitativaAQT anef, AnaliseQuantitativaAQT novoAnef,
            boolean isCopiarUsuarioAnterior) {
        if (EstadoAQTEnum.DESIGNADO.equals(novoAnef.getEstado()) && anef.getDesignacao() != null) {
            DesignacaoAQT nova = new DesignacaoAQT();
            nova.setAnaliseQuantitativaAQT(novoAnef);
            nova.setMatriculaServidor(anef.getDesignacao().getMatriculaServidor());
            if (isCopiarUsuarioAnterior) {
                nova.setUltimaAtualizacao(anef.getDesignacao().getUltimaAtualizacao());
                nova.setOperadorAtualizacao(anef.getDesignacao().getOperadorAtualizacao());
                nova.setAlterarDataUltimaAtualizacao(false);
            }
            designacaoAQTDAO.save(nova);
            designacaoAQTDAO.getSessionFactory().getCurrentSession().flush();
            novoAnef.setDesignacao(nova);
        }

    }

}
