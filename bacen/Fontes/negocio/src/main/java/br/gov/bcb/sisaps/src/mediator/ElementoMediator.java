package br.gov.bcb.sisaps.src.mediator;

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
import br.gov.bcb.sisaps.src.dao.ElementoDAO;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Elemento;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.ParametroElemento;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoGrupoEnum;
import br.gov.bcb.sisaps.src.validacao.RegraAnaliseARCPermissao;
import br.gov.bcb.sisaps.src.validacao.RegraEdicaoARCInspetorPermissaoAlteracao;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.geral.SisapsValidacaoUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

@Service
@Transactional(readOnly = true)
public class ElementoMediator {

    @Autowired
    private ElementoDAO elementoDAO;

    @Autowired
    private ItemElementoMediator itemElementoMediator;

    @Autowired
    private AvaliacaoRiscoControleMediator avaliacaoRiscoControleMediator;

    public static ElementoMediator get() {
        return SpringUtils.get().getBean(ElementoMediator.class);
    }

    public Elemento buscarPorPk(Integer pk) {
        Elemento elemento = elementoDAO.load(pk);
        elementoDAO.refresh(elemento);
        inicializarDependencias(elemento);
        Hibernate.initialize(elemento.getAvaliacaoRiscoControle());
        return elemento;
    }

    public void criarElementosCelulaRiscoControle(Ciclo ciclo, CelulaRiscoControle celulaRiscoControle) {
        criarAtualizarElementosArc(ciclo, celulaRiscoControle.getParametroGrupoRiscoControle(), 
                celulaRiscoControle.getArcRisco(), TipoGrupoEnum.RISCO);
        criarAtualizarElementosArc(ciclo, celulaRiscoControle.getParametroGrupoRiscoControle(), 
                celulaRiscoControle.getArcControle(), TipoGrupoEnum.CONTROLE);
        elementoDAO.flush();
    }

    public void criarAtualizarElementosArc(Ciclo ciclo, ParametroGrupoRiscoControle parametroGrupoRiscoControle,
            AvaliacaoRiscoControle arc, TipoGrupoEnum tipoGrupo) {
        if (CollectionUtils.isEmpty(arc.getElementos())) {
            criarElementosARC(parametroGrupoRiscoControle, arc, tipoGrupo);
        } else if (arc.getEstado().equals(EstadoARCEnum.PREVISTO) || arc.getEstado().equals(EstadoARCEnum.DESIGNADO)) {
            atualizarEstruturaElementoARC(ciclo, parametroGrupoRiscoControle, arc, tipoGrupo);
        }

    }

    private void atualizarEstruturaElementoARC(Ciclo ciclo, ParametroGrupoRiscoControle parametroGrupoRiscoControle,
            AvaliacaoRiscoControle arc, TipoGrupoEnum tipoGrupoEnum) {
        List<ParametroElemento> parametrosElemento =
                ParametroElementoMediator.get().buscarParametrosElementoPorTipo(
                        parametroGrupoRiscoControle, tipoGrupoEnum);
        List<Elemento> elementosARC = buscarElementosOrdenadosDoArc(arc.getPk());
        excluirElementoExcluidosMetodologia(ciclo, arc, parametrosElemento, elementosARC);
        elementosARC = buscarElementosOrdenadosDoArc(arc.getPk());
        incluirElementosIncluidosMetodologia(arc, parametrosElemento, elementosARC);
        for (Elemento elemento : elementosARC) {
            ItemElementoMediator.get().atualizarEstruturaItensElementoARC(ciclo, arc, elemento);
            elementoDAO.update(elemento);
        }
    }

    private void incluirElementosIncluidosMetodologia(AvaliacaoRiscoControle arc, 
            List<ParametroElemento> parametrosElemento, List<Elemento> elementosARC) {
        List<ParametroElemento> parametrosElementoIncluidosMetodologia = new ArrayList<ParametroElemento>();
        for (ParametroElemento parametroElemento : parametrosElemento) {
            boolean existe = false;
            for (Elemento elemento : elementosARC) {
                if (elemento.getParametroElemento().getPk().equals(parametroElemento.getPk())) {
                    existe = true;
                    break;
                }
            }
            if (!existe) {
                parametrosElementoIncluidosMetodologia.add(parametroElemento);
            }
        }
        for (ParametroElemento parametroElemento : parametrosElementoIncluidosMetodologia) {
            arc.getElementos().add(criarElementoARC(arc, parametroElemento));
        }
        avaliacaoRiscoControleMediator.alterar(arc);
    }

    private void excluirElementoExcluidosMetodologia(Ciclo ciclo, AvaliacaoRiscoControle arc, 
            List<ParametroElemento> parametrosElemento, List<Elemento> elementosARC) {
        List<Elemento> elementosExcluidosMetodologia = new ArrayList<Elemento>();
        for (Elemento elemento : elementosARC) {
            boolean existe = false;
            for (ParametroElemento parametroElemento : parametrosElemento) {
                if (elemento.getParametroElemento().getPk().equals(parametroElemento.getPk())) {
                    existe = true;
                    break;
                }
            }
            if (!existe) {
                elementosExcluidosMetodologia.add(elemento);
            }
        }
        for (Elemento elemento : elementosExcluidosMetodologia) {
            ItemElementoMediator.get().excluirItensElemento(ciclo, elemento.getItensElemento());
            elemento.getItensElemento().clear();
            elementoDAO.delete(elemento);
            arc.getElementos().remove(elemento);
        }
        avaliacaoRiscoControleMediator.alterar(arc);
        elementoDAO.flush();
    }

    private List<Elemento> criarElementosARC(ParametroGrupoRiscoControle parametroGrupoRiscoControle,
            AvaliacaoRiscoControle arc, TipoGrupoEnum tipoGrupoEnum) {
        List<ParametroElemento> parametrosElemento =
                ParametroElementoMediator.get().buscarParametrosElementoPorTipo(
                        parametroGrupoRiscoControle, tipoGrupoEnum);
        List<Elemento> elementos = new ArrayList<Elemento>();
        for (ParametroElemento parametroElemento : parametrosElemento) {
            elementos.add(criarElementoARC(arc, parametroElemento));
        }
        return elementos;
    }

    private Elemento criarElementoARC(AvaliacaoRiscoControle arc, ParametroElemento parametroElemento) {
        Elemento elemento = new Elemento();
        elemento.setParametroElemento(parametroElemento);
        elemento.setAvaliacaoRiscoControle(arc);
        elementoDAO.save(elemento);
        itemElementoMediator.criarItensElementoARC(arc, parametroElemento, elemento);
        return elemento;
    }

    @Transactional
    public void salvarNovaNotaElementoARC(Ciclo ciclo, AvaliacaoRiscoControle avaliacaoRiscoControle, Elemento elemento) {
        new RegraEdicaoARCInspetorPermissaoAlteracao(ciclo, avaliacaoRiscoControle).validar();
        elementoDAO.update(elemento);
        avaliacaoRiscoControleMediator.alterarEstadoARCParaEmEdicao(avaliacaoRiscoControle);
    }

    @Transactional
    public void salvarNovaNotaElementoARCSupervisor(Ciclo ciclo, Matriz matriz, AvaliacaoRiscoControle arc,
            Elemento elemento, String matriculaUsuario, boolean isNota, boolean isJustificativa) {
        new RegraAnaliseARCPermissao(ciclo, matriz, arc, matriculaUsuario).validar();
        if (SisapsUtil.isTextoCKEditorBrancoOuNulo(elemento.getJustificativaSupervisor())) {
            elemento.setJustificativaSupervisor(null);
        }
        Elemento elementoBD = elementoDAO.load(elemento.getPk());
        if (isNota) {
            elementoBD.setParametroNotaSupervisor(elemento.getParametroNotaSupervisor());
        }
        if (isJustificativa) {
            elementoBD.setJustificativaSupervisor(elemento.getJustificativaSupervisor());
            elementoBD.setOperadorAlteracao(((UsuarioAplicacao) UsuarioCorrente.get()).getLogin());
            elementoBD.setDataAlteracao(DataUtil.getDateTimeAtual());
        }

        elementoDAO.update(elementoBD);
        avaliacaoRiscoControleMediator.alterarEstadoARCParaEmAnalise(arc);
        elementoDAO.flush();
    }

    private void inicializarDependencias(Elemento result) {
        if (result.getParametroNotaInspetor() != null) {
            Hibernate.initialize(result.getParametroNotaInspetor());
        }
    }

    @Transactional
    public void duplicarElementosARCConclusaoAnalise(Ciclo ciclo, AvaliacaoRiscoControle arc,
            AvaliacaoRiscoControle novoARC) {
        for (Elemento elemento : arc.getElementos()) {
            Elemento novoElemento = criarNovoElemento(novoARC, elemento, false);
            // duplicar item elemento
            itemElementoMediator.duplicarItensElementoARCConclusaoAnalise(ciclo, elemento, novoElemento);
        }
    }

    @Transactional
    public void duplicarElementosARCConclusaoCorec(Ciclo cicloAnterior, Ciclo cicloNovo,
            ParametroGrupoRiscoControle parametroGrupoRiscoControle, AvaliacaoRiscoControle arc, 
            AvaliacaoRiscoControle novoARC) {

        List<Elemento> novosElementos = 
                criarElementosARC(parametroGrupoRiscoControle, novoARC, arc.getTipo());
        elementoDAO.flush();
        List<Elemento> elementosARCAnterior = new ArrayList<Elemento>();
        elementosARCAnterior.addAll(buscarElementosOrdenadosDoArc(arc.getPk()));

        for (Elemento novoElemento : novosElementos) {
            Elemento elementoARCAnterior = obterElementoCorrespondenteARCVigente(elementosARCAnterior, novoElemento);
            if (elementoARCAnterior != null) {
                novoElemento.setDataAlteracao(elementoARCAnterior.getDataAlteracao());
                novoElemento.setOperadorAlteracao(elementoARCAnterior.getOperadorAlteracao());
                novoElemento.setUltimaAtualizacao(elementoARCAnterior.getUltimaAtualizacao());
                novoElemento.setOperadorAtualizacao(elementoARCAnterior.getOperadorAtualizacao());
                novoElemento.setAlterarDataUltimaAtualizacao(false);
                elementoDAO.saveOrUpdate(novoElemento);
                elementoDAO.flush();
                itemElementoMediator.duplicarItensElementoARCConclusaoCorec(cicloAnterior, cicloNovo,
                        elementoARCAnterior, novoElemento);
            }
        }
    }

    private Elemento criarNovoElemento(AvaliacaoRiscoControle novoARC, Elemento elemento,
            boolean isCopiarUsuarioAnterior) {
        Elemento novoElemento = new Elemento();
        novoElemento.setParametroElemento(elemento.getParametroElemento());
        novoElemento.setAvaliacaoRiscoControle(novoARC);
        novoElemento.setDataAlteracao(elemento.getDataAlteracao());
        novoElemento.setOperadorAlteracao(elemento.getOperadorAlteracao());
        if (isCopiarUsuarioAnterior) {
            novoElemento.setUltimaAtualizacao(elemento.getUltimaAtualizacao());
            novoElemento.setOperadorAtualizacao(elemento.getOperadorAtualizacao());
            novoElemento.setAlterarDataUltimaAtualizacao(false);
        }
        elementoDAO.save(novoElemento);
        return novoElemento;
    }

    public Elemento obterElementoCorrespondenteARCVigente(List<Elemento> elementosARCVigente, Elemento elemento) {
        Elemento retorno = null;
        if (CollectionUtils.isNotEmpty(elementosARCVigente)) {
            for (Elemento elementoARCVigente : elementosARCVigente) {
                if (elemento.getParametroElemento().getNome()
                        .equals(elementoARCVigente.getParametroElemento().getNome())) {
                    retorno = elementoARCVigente;
                    break;
                }
            }
            elementosARCVigente.remove(retorno);
        }
        return retorno;
    }

    public void verificarElementosSemJustificava(List<Elemento> elementos, ArrayList<ErrorMessage> erros) {
        if (CollectionUtils.isNotEmpty(elementos)) {
            for (Elemento elemento : elementos) {
                if (elemento.getParametroNotaSupervisor() != null
                        && elemento.getParametroNotaSupervisor().getValor().compareTo(BigDecimal.ZERO) == 1
                        && StringUtils.isBlank(SisapsUtil.extrairTextoCKEditorSemEspacosEmBranco(elemento
                                .getJustificativaSupervisor()))) {
                    SisapsUtil.adicionarErro(erros, SisapsValidacaoUtil.validarObrigatoriedadeAnalise(elemento
                            .getParametroElemento().getNome()));
                }
                if (elemento.getParametroNotaInspetor() != null
                        && elemento.getParametroNotaSupervisor() != null
                        && elemento.getParametroNotaSupervisor().getValor()
                                .equals(elemento.getParametroNotaInspetor().getValor())) {
                    erros.add(new ErrorMessage("Para o elemento '" + elemento.getParametroElemento().getNome()
                            + "', a 'Nota supervisor' deve ser diferente da 'Nota inspetor'."));
                }
            }
        }
    }

    public List<Elemento> buscarElementosOrdenadosDoArc(Integer idAvaliacaoRiscoControle) {
        return elementoDAO.buscarElementosOrdenadosDoArc(idAvaliacaoRiscoControle);
    }

    @Transactional
    public void copiarDadosElementoARCAnterior(Ciclo ciclo, AvaliacaoRiscoControle arcPerfilRiscoVigente) {
        AvaliacaoRiscoControle arcAnterior = arcPerfilRiscoVigente.getAvaliacaoRiscoControleVigente();
        List<Elemento> elementosArcAnterior = arcAnterior.getElementos();
        for (Elemento elemento : arcPerfilRiscoVigente.getElementos()) {
            Elemento elementoARCAnterior = obterElementoCorrespondenteARCVigente(elementosArcAnterior, elemento);
            if (elementoARCAnterior == null) {
                ItemElementoMediator.get().excluirItensElemento(ciclo, elemento.getItensElemento());
            } else {
                elemento.setParametroNotaInspetor(elementoARCAnterior.getParametroNotaInspetor());
                elemento.setParametroNotaSupervisor(elementoARCAnterior.getParametroNotaSupervisor());
                elemento.setJustificativaSupervisor(elementoARCAnterior.getJustificativaSupervisor());
                elemento.setUltimaAtualizacao(elementoARCAnterior.getUltimaAtualizacao());
                elemento.setOperadorAtualizacao(elementoARCAnterior.getOperadorAtualizacao());
                elemento.setDataAlteracao(elementoARCAnterior.getDataAlteracao());
                elemento.setOperadorAlteracao(elementoARCAnterior.getOperadorAlteracao());
                elemento.setAlterarDataUltimaAtualizacao(false);
                elementoDAO.saveOrUpdate(elemento);
                ItemElementoMediator.get().copiarDadosItemElementoARCAnterior(ciclo, elemento, elementoARCAnterior);
            }
        }
    }

    public String getJustificativaAtualizada(Elemento elemento) {
        final String justificativaSupervisorVigente =
                elemento == null || elemento.getJustificativaSupervisor() == null ? "" : elemento
                        .getJustificativaSupervisor();
        return justificativaSupervisorVigente;
    }

    @Transactional
    public void alterar(Elemento elemento) {
        elementoDAO.update(elemento);
    }
    
    @Transactional
    public void atualizarDadosNovaMetodologia(List<Elemento> elementos, Metodologia metodologia) {
        for (Elemento elemento : elementos) {
            ParametroNota notaInspetorAntiga = elemento.getParametroNotaInspetor();
            ParametroNota notaSupervisorAntiga = elemento.getParametroNotaSupervisor();
            if (notaInspetorAntiga != null) {
                ParametroNota novaNotaInspetor =
                        ParametroNotaMediator.get().buscarNota(metodologia,
                                notaInspetorAntiga.getValor());
                elemento.setParametroNotaInspetor(novaNotaInspetor);
            }
            if (notaSupervisorAntiga != null) {
                ParametroNota novaNotaSupervisor =
                        ParametroNotaMediator.get().buscarNota(metodologia,
                                notaSupervisorAntiga.getValor());
                elemento.setParametroNotaSupervisor(novaNotaSupervisor);
            }
            elemento.setAlterarDataUltimaAtualizacao(false);
            elementoDAO.update(elemento);
        }
    }

}
