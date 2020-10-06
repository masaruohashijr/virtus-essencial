package br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dao.analisequantitativaaqt.DelegacaoAQTDAO;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.DelegacaoAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.validacao.RegraDelegacaoAnefInclusaoValidacaoCampos;

@Service
@Transactional(readOnly = true)
public class DelegacaoAQTMediator {

    @Autowired
    private DelegacaoAQTDAO delegacaoAQTDAO;

    public static DelegacaoAQTMediator get() {
        return SpringUtils.get().getBean(DelegacaoAQTMediator.class);
    }

    @Transactional
    public void excluirDelegacaoARC(AnaliseQuantitativaAQT aqt) {
        if (aqt.getDelegacao() != null) {
            excluir(aqt.getDelegacao());
            aqt.setDelegacao(null);
        }
    }

    public void excluir(DelegacaoAQT delegacao) {
        delegacaoAQTDAO.delete(delegacao);
    }

    @SuppressWarnings("unchecked")
    public List<ServidorVO> buscarListaServidorDelegacao(AnaliseQuantitativaAQT aqt, String localizacao,
            PerfilAcessoEnum perfilMenu) {
        List<ServidorVO> lista = new ArrayList<ServidorVO>();
        lista.addAll(BcPessoaAdapter.get().consultarServidoresAtivos(localizacao));
        removendoServidorDelegado(aqt, lista);
        removendoSupervisor(aqt, lista, perfilMenu);
        List<BeanComparator> sortFields = new ArrayList<BeanComparator>();
        sortFields.add(new BeanComparator("nome"));
        Collections.sort(lista, new ComparatorChain(sortFields));
        return lista;
    }

    private void removendoSupervisor(AnaliseQuantitativaAQT aqt, List<ServidorVO> lista, PerfilAcessoEnum perfilMenu) {
        boolean isSupervisor = PerfilAcessoEnum.SUPERVISOR.equals(perfilMenu);
        if (isSupervisor) {
            UsuarioAplicacao usuarioAplicacao = (UsuarioAplicacao) UsuarioCorrente.get();
            for (ServidorVO servidor : new LinkedList<ServidorVO>(lista)) {
                if (servidor.getMatricula().equals(usuarioAplicacao.getMatricula())) {
                    lista.remove(BcPessoaAdapter.get().buscarServidor(servidor.getMatricula()));
                }
            }
        }
    }

    private void removendoServidorDelegado(AnaliseQuantitativaAQT aqt, List<ServidorVO> lista) {
        ServidorVO servidorDelegado = null;
        if (aqt.getDelegacao() != null) {
            servidorDelegado = BcPessoaAdapter.get().buscarServidor(aqt.getDelegacao().getMatriculaServidor());
            if (servidorDelegado != null) {
                for (ServidorVO servidor : new LinkedList<ServidorVO>(lista)) {
                    if (servidor.getMatricula().equals(servidorDelegado.getMatricula())) {
                        lista.remove(servidorDelegado);
                    }
                }
            }
        }
    }

    @Transactional
    public String incluir(AnaliseQuantitativaAQT analiseQuantitativaAQT, ServidorVO servidorEquipe,
            ServidorVO servidorUnidade) {
        AnaliseQuantitativaAQT anef = AnaliseQuantitativaAQTMediator.get().buscar(analiseQuantitativaAQT.getPk());
        new RegraDelegacaoAnefInclusaoValidacaoCampos(servidorEquipe, servidorUnidade).validar();
        String matriculaServidorDesignado = null;
        if (servidorEquipe == null) {
            matriculaServidorDesignado = servidorUnidade.getMatricula();
        } else {
            matriculaServidorDesignado = servidorEquipe.getMatricula();
        }
        DelegacaoAQT delegacao = anef.getDelegacao() == null ? new DelegacaoAQT() : anef.getDelegacao();
        delegacao.setMatriculaServidor(matriculaServidorDesignado);
        delegacao.setAnaliseQuantitativaAQT(anef);
        anef.setDelegacao(delegacao);
        incluir(delegacao);
        AnaliseQuantitativaAQTMediator.get().alterarEstadoAnefDelegado(anef);
        analiseQuantitativaAQT.setEstado(anef.getEstado());
        return "ANEF delegado com sucesso.";
    }

    @Transactional
    private void incluir(DelegacaoAQT delegacao) {
        if (delegacao.getPk() == null) {
            delegacaoAQTDAO.save(delegacao);
        } else {
            delegacaoAQTDAO.merge(delegacao);
        }
        delegacaoAQTDAO.getSessionFactory().getCurrentSession().flush();
    }

    public String mostraAlertaBotaoDelegar(AnaliseQuantitativaAQT aqt) {
        AnaliseQuantitativaAQTMediator analiseQuantitativaAQTMediator = AnaliseQuantitativaAQTMediator.get();

        if (analiseQuantitativaAQTMediator.estadoAnalisado(aqt.getEstado())) {
            return "Atenção ANEF já analisado.";
        }

        if (AnaliseQuantitativaAQTMediator.get().estadoEmAnalise(aqt.getEstado()) && aqt.getDelegacao() == null) {
            return "Atenção ANEF com análise em andamento.";
        }

        if (analiseQuantitativaAQTMediator.estadoEmAnalise(aqt.getEstado()) && aqt.getDelegacao() != null) {
            return "Atenção ANEF com análise em andamento por delegado.";
        }

        return "";
    }

}
