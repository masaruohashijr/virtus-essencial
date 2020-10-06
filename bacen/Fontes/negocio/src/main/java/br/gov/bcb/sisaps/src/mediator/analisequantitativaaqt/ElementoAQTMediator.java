package br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dao.analisequantitativaaqt.ElementoAQTDAO;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ElementoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroNotaAQT;
import br.gov.bcb.sisaps.src.validacao.RegraAnaliseANEFSupervisorPermissaoAlteracao;
import br.gov.bcb.sisaps.src.validacao.RegraEdicaoANEFInspetorPermissaoAlteracao;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.geral.SisapsValidacaoUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

@Service
@Transactional(readOnly = true)
public class ElementoAQTMediator {
    
    @Autowired
    private ElementoAQTDAO elementoAQTDAO;

    @Autowired
    private ItemElementoAQTMediator itemElementoAQTMediator;


    @Autowired
    private AnaliseQuantitativaAQTMediator analiseQuantitativaAQTMediator;

    public static ElementoAQTMediator get() {
        return SpringUtils.get().getBean(ElementoAQTMediator.class);
    }
    
    @Transactional
    public void duplicarElementosAQTConclusaoAnalise(Ciclo ciclo, AnaliseQuantitativaAQT anef,
            AnaliseQuantitativaAQT novoAnef, boolean isConclusaoCorec) {
        for (ElementoAQT elemento : anef.getElementos()) {
            ElementoAQT novoElemento = new ElementoAQT();
            novoElemento.setParametroElemento(elemento.getParametroElemento());
            novoElemento.setAnaliseQuantitativaAQT(novoAnef);
            novoElemento.setDataAlteracao(elemento.getDataAlteracao());
            novoElemento.setOperadorAlteracao(elemento.getOperadorAlteracao());
            if (isConclusaoCorec) {
                novoElemento.setParametroNotaInspetor(elemento.getParametroNotaInspetor());
                novoElemento.setParametroNotaSupervisor(elemento.getParametroNotaSupervisor());
                novoElemento.setJustificativaSupervisor(elemento.getJustificativaSupervisor());
                novoElemento.setUltimaAtualizacao(elemento.getUltimaAtualizacao());
                novoElemento.setOperadorAtualizacao(elemento.getOperadorAtualizacao());
                novoElemento.setAlterarDataUltimaAtualizacao(false);
            }
            elementoAQTDAO.save(novoElemento);
            // duplicar item elemento
            itemElementoAQTMediator.duplicarItensElementoAQTConclusaoAnalise(
                    ciclo, elemento, novoElemento, isConclusaoCorec);
        }
    }
    
    public List<ElementoAQT> buscarElementosOrdenadosDoAnef(Integer idAQT) {
        return elementoAQTDAO.buscarElementosOrdenadosDoAnef(idAQT);
    }

    public ElementoAQT obterElementoANEFVigente(List<ElementoAQT> elementosANEFVigente, ElementoAQT elemento) {
        ElementoAQT retorno = null;
        if (CollectionUtils.isNotEmpty(elementosANEFVigente)) {
            for (ElementoAQT elementoARCVigente : elementosANEFVigente) {
                if (elemento.getParametroElemento().getDescricao()
                        .equals(elementoARCVigente.getParametroElemento().getDescricao())) {
                    retorno = elementoARCVigente;
                    break;
                }
            }
            elementosANEFVigente.remove(retorno);
        }
        return retorno;
    }

    public ElementoAQT buscarPorPk(Integer pk) {
        ElementoAQT elemento = elementoAQTDAO.load(pk);
        inicializarDependencias(elemento);
        return elemento;
    }

    private void inicializarDependencias(ElementoAQT result) {
        if (result.getParametroNotaInspetor() != null) {
            Hibernate.initialize(result.getParametroNotaInspetor());
        }
        if (result.getItensElemento() != null) {
            Hibernate.initialize(result.getItensElemento());
        }

        if (result.getAnaliseQuantitativaAQT() != null) {
            Hibernate.initialize(result.getAnaliseQuantitativaAQT());
            if (result.getAnaliseQuantitativaAQT().getCiclo() != null) {
                Hibernate.initialize(result.getAnaliseQuantitativaAQT().getCiclo());
            }
            if (result.getAnaliseQuantitativaAQT().getCiclo().getMetodologia() != null) {
                Hibernate.initialize(result.getAnaliseQuantitativaAQT().getCiclo().getMetodologia());
            }
            if (result.getAnaliseQuantitativaAQT().getCiclo().getMetodologia().getParametrosNotaAQT() != null) {
                Hibernate.initialize(result.getAnaliseQuantitativaAQT().getCiclo().getMetodologia()
                        .getParametrosNotaAQT());
            }
        }

    }

    @Transactional
    public void salvarNovaNotaElementoARC(AnaliseQuantitativaAQT aqt, ElementoAQT elemento) {
        new RegraEdicaoANEFInspetorPermissaoAlteracao(aqt).validar();
        UsuarioAplicacao usuarioAplicacao = (UsuarioAplicacao) UsuarioCorrente.get();
        elemento.setOperadorAlteracao(usuarioAplicacao.getLogin());
        elemento.setDataAlteracao(DataUtil.getDateTimeAtual());
        elemento.setParametroElemento(elemento.getParametroElemento());
        elemento.setJustificativaSupervisor(null);
        elementoAQTDAO.update(elemento);
        aqt.setOperadorPreenchido(null);
        aqt.setOperadorAnalise(null);
        aqt.setOperadorConclusao(null);
        analiseQuantitativaAQTMediator.alterarEstadoANEFParaEmEdicao(aqt);
    }

    @Transactional
    public void salvarNovaNotaElementoAQTSupervisor(AnaliseQuantitativaAQT aqt, ElementoAQT elemento, boolean isNota , boolean isJustificativa) {
        new RegraAnaliseANEFSupervisorPermissaoAlteracao(aqt).validar();
        UsuarioAplicacao usuarioAplicacao = (UsuarioAplicacao) UsuarioCorrente.get();
        if (SisapsUtil.isTextoCKEditorBrancoOuNulo(elemento.getJustificativaSupervisor())) {
            elemento.setJustificativaSupervisor(null);
        }
        ElementoAQT elementoBD = buscarPorPk(elemento.getPk());
        if (isNota) {
        elementoBD.setParametroNotaSupervisor(elemento.getParametroNotaSupervisor());
        }
        if (isJustificativa) {
            elementoBD.setJustificativaSupervisor(elemento.getJustificativaSupervisor());
            elementoBD.setOperadorAlteracao(usuarioAplicacao.getLogin());
            elementoBD.setDataAlteracao(DataUtil.getDateTimeAtual());
        }
       
        elementoAQTDAO.update(elementoBD);
        elementoAQTDAO.flush();
        aqt.setOperadorAnalise(null);
        aqt.setOperadorConclusao(null);
        analiseQuantitativaAQTMediator.alterarEstadoAnefParaEmAnalise(aqt);
    }

    public List<ParametroNotaAQT> carregarNotasSupervisor(final ElementoAQT elemento) {
        List<ParametroNotaAQT> listaChoices =
                elemento.getAnaliseQuantitativaAQT().getCiclo().getMetodologia().getParametrosNotaAQT();

        return SisapsUtil.removerParametroNotaInspetorANEF(elemento.getParametroNotaInspetor().getDescricaoValor(),
                listaChoices);
    }

    public boolean verificarComJustificava(List<ElementoAQT> elementos) {
        if (elementos != null) {
            for (ElementoAQT elemento : elementos) {
                if (elemento.getParametroNotaSupervisor() != null
                        && elemento.getParametroNotaSupervisor().getValor().compareTo(BigDecimal.ZERO) <= 0
                        && StringUtils.isNotBlank(SisapsUtil.extrairTextoCKEditorSemEspacosEmBranco(elemento
                                .getJustificativaSupervisor()))) {
                    return true;
                }
            }
        }
        return false;
    }

    public void verificarElementosSemJustificava(AnaliseQuantitativaAQT aqt, ArrayList<ErrorMessage> erros) {

        if (CollectionUtils.isNotEmpty(aqt.getElementos())) {
            for (ElementoAQT elemento : aqt.getElementos()) {
                if (elemento.getParametroNotaSupervisor() != null
                        && elemento.getParametroNotaSupervisor().getValor().compareTo(BigDecimal.ZERO) == 1
                        && StringUtils.isBlank(SisapsUtil.extrairTextoCKEditorSemEspacosEmBranco(elemento
                                .getJustificativaSupervisor()))) {
                    SisapsUtil.adicionarErro(erros, SisapsValidacaoUtil.validarObrigatoriedadeAnalise(elemento
                            .getParametroElemento().getDescricao()));
                }
                if (elemento.getParametroNotaInspetor() != null
                        && elemento.getParametroNotaSupervisor() != null
                        && elemento.getParametroNotaSupervisor().getValor()
                                .equals(elemento.getParametroNotaInspetor().getValor())) {
                    erros.add(new ErrorMessage("Para o elemento '" + elemento.getParametroElemento().getDescricao()
                            + "', a 'Nota supervisor' deve ser diferente da 'Nota inspetor'."));
                }
            }
        }
    }
    
    @Transactional
    public void atualizarDadosNovaMetodologia(List<ElementoAQT> elementos, Metodologia metodologia) {
        for (ElementoAQT elemento : elementos) {
            ParametroNotaAQT notaInspetorAntiga = elemento.getParametroNotaInspetor();
            ParametroNotaAQT notaSupervisorAntiga = elemento.getParametroNotaSupervisor();
            if (notaInspetorAntiga != null) {
                ParametroNotaAQT novaNotaInspetor =
                        ParametroNotaAQTMediator.get().buscarPorNota(metodologia,
                                notaInspetorAntiga.getValor());
                elemento.setParametroNotaInspetor(novaNotaInspetor);
            }
            if (notaSupervisorAntiga != null) {
                ParametroNotaAQT novaNotaSupervisor =
                        ParametroNotaAQTMediator.get().buscarPorNota(metodologia,
                                notaSupervisorAntiga.getValor());
                elemento.setParametroNotaSupervisor(novaNotaSupervisor);
            }
            elemento.setAlterarDataUltimaAtualizacao(false);
            elementoAQTDAO.update(elemento);
        }
    }

}
