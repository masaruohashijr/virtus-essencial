package br.gov.bcb.sisaps.src.mediator;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dao.DocumentoDAO;
import br.gov.bcb.sisaps.src.dao.ItemElementoDAO;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.Elemento;
import br.gov.bcb.sisaps.src.dominio.ItemElemento;
import br.gov.bcb.sisaps.src.dominio.ParametroElemento;
import br.gov.bcb.sisaps.src.dominio.ParametroItemElemento;
import br.gov.bcb.sisaps.src.validacao.RegraEdicaoARCInspetorPermissaoAlteracao;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;

@Service
public class ItemElementoMediator {

    @Autowired
    private ItemElementoDAO itemElementoDAO;

    @Autowired
    private DocumentoDAO documentoDAO;

    @Autowired
    private AvaliacaoRiscoControleMediator avaliacaoRiscoControleMediator;

    @Autowired
    private DocumentoMediator documentoMediator;

    @Autowired
    private AnexoItemElementoMediator anexoItemElementoMediator;

    public static ItemElementoMediator get() {
        return SpringUtils.get().getBean(ItemElementoMediator.class);
    }

    @Transactional(readOnly = true)
    public ItemElemento buscarPorPk(Integer pk) {
        ItemElemento itemElemento = itemElementoDAO.load(pk);
        inicializarDependencias(itemElemento);
        Hibernate.initialize(itemElemento.getElemento());
        return itemElemento;
    }

    private void inicializarDependencias(ItemElemento itemElemento) {
        if (itemElemento != null && itemElemento.getDocumento() != null) {
            Hibernate.initialize(itemElemento.getDocumento());
        }
    }

    @Transactional
    public void criarItensElementoARC(AvaliacaoRiscoControle avaliacaoRiscoControle,
            ParametroElemento parametroElemento, Elemento elemento) {
        for (ParametroItemElemento parametroItemElemento : parametroElemento.getItensElemento()) {
            elemento.getItensElemento().add(criarItemElementoARC(elemento, parametroItemElemento));
        }
    }

    private ItemElemento criarItemElementoARC(Elemento elemento, ParametroItemElemento parametroItemElemento) {
        ItemElemento itemElemento = new ItemElemento();
        itemElemento.setParametroItemElemento(parametroItemElemento);
        itemElemento.setElemento(elemento);
        itemElementoDAO.save(itemElemento);
        return itemElemento;
    }
    
    @Transactional
    public void atualizarEstruturaItensElementoARC(Ciclo ciclo, AvaliacaoRiscoControle arc, Elemento elementoARC) {
        List<ParametroItemElemento> parametrosItemElemento = elementoARC.getParametroElemento().getItensElemento();
        List<ItemElemento> itensElemento = buscarItensOrdenadosDoElemento(elementoARC);
        excluirItensExcluidosMetodologia(ciclo, elementoARC, parametrosItemElemento, itensElemento);
        itensElemento = buscarItensOrdenadosDoElemento(elementoARC);
        incluirItensIncluidosMetodologia(elementoARC, parametrosItemElemento, itensElemento);
    }

    private void incluirItensIncluidosMetodologia(Elemento elementoARC, 
            List<ParametroItemElemento> parametrosItemElemento, List<ItemElemento> itensElemento) {
        List<ParametroItemElemento> parametrosItensIncluidosMetodologia = new ArrayList<ParametroItemElemento>();
        for (ParametroItemElemento parametroItem : parametrosItemElemento) {
            boolean existe = false;
            for (ItemElemento item : itensElemento) {
                if (item.getParametroItemElemento().getPk().equals(parametroItem.getPk())) {
                    existe = true;
                    break;
                }
            }
            if (!existe) {
                parametrosItensIncluidosMetodologia.add(parametroItem);
            }
        }
        for (ParametroItemElemento parametroItem : parametrosItensIncluidosMetodologia) {
            elementoARC.getItensElemento().add(criarItemElementoARC(elementoARC, parametroItem));
        }
    }

    private void excluirItensExcluidosMetodologia(Ciclo ciclo, Elemento elementoARC, 
            List<ParametroItemElemento> parametrosItemElemento,
            List<ItemElemento> itensElemento) {
        List<ItemElemento> itensExcluidosMetodologia = new ArrayList<ItemElemento>();
        for (ItemElemento item : itensElemento) {
            boolean existe = false;
            for (ParametroItemElemento parametroItem : parametrosItemElemento) {
                if (item.getParametroItemElemento().getPk().equals(parametroItem.getPk())) {
                    existe = true;
                    break;
                }
            }
            if (!existe) {
                itensExcluidosMetodologia.add(item);
            }
        }
        for (ItemElemento item : itensExcluidosMetodologia) {
            excluirItemElemento(ciclo, item);
            elementoARC.getItensElemento().remove(item);
        }
        itemElementoDAO.flush();
    }

    @Transactional
    public void salvarJustificativaItemElementoARC(Ciclo ciclo, AvaliacaoRiscoControle avaliacaoRiscoControle,
            ItemElemento item, boolean isDuplicacaoARC) {
        if (!isDuplicacaoARC) {
            new RegraEdicaoARCInspetorPermissaoAlteracao(ciclo, avaliacaoRiscoControle).validar();
            UsuarioAplicacao usuario = (UsuarioAplicacao) UsuarioCorrente.get();
            item.setOperadorAlteracao(usuario.getLogin());
            item.setDataAlteracao(DataUtil.getDateTimeAtual());
        }
        if (item.getDocumento() != null
                && SisapsUtil.isTextoCKEditorBrancoOuNulo(item.getDocumento().getJustificativa())) {
            item.getDocumento().setJustificativa(null);
        }

        if (item.getDocumento() == null) {
            item.setDocumento(new Documento());
        }
        documentoDAO.saveOrUpdate(item.getDocumento());
        
       

        itemElementoDAO.update(item);
        if (!isDuplicacaoARC) {
            avaliacaoRiscoControleMediator.alterarEstadoARCParaEmEdicao(avaliacaoRiscoControle);
        }
    }

    public ItemElemento obterItemElementoPorDocumento(Documento documento) {
        return itemElementoDAO.obterItemElementoPorDocumento(documento);
    }

    @Transactional
    public void duplicarItensElementoARCConclusaoAnalise(Ciclo ciclo, Elemento elemento, Elemento novoElemento) {
        for (ItemElemento itemElemento : elemento.getItensElemento()) {
            ItemElemento novoItem = criarNovoItemElemento(novoElemento, itemElemento, false);
            // duplicar documento do item
            Documento novoDocumento =
                    documentoMediator.duplicarDocumentoItemElementoARCConclusaoAnalise(itemElemento, novoItem, false);
            novoItem.setDocumento(novoDocumento);
            itemElementoDAO.save(novoItem);
            // duplicar anexo do documento do item 
            anexoItemElementoMediator.duplicarAnexosItemElementoARCConclusaoAnalise(ciclo, itemElemento, novoItem, false);
        }
    }
    
    @Transactional
    public void duplicarItensElementoARCConclusaoCorec(Ciclo cicloAnterior, Ciclo cicloNovo, 
            Elemento elemento, Elemento novoElemento) {
        List<ItemElemento> itensElementoARCAnterior = buscarItensOrdenadosDoElemento(elemento);
        for (ItemElemento novoItem : buscarItensOrdenadosDoElemento(novoElemento)) {
            ItemElemento itemElementoARCAnterior = 
                    obterItemElementoCorrespondenteARCVigente(itensElementoARCAnterior, novoItem);
            if (itemElementoARCAnterior.getPk() != null) {
                novoItem.setDataAlteracao(itemElementoARCAnterior.getDataAlteracao());
                novoItem.setOperadorAlteracao(itemElementoARCAnterior.getOperadorAlteracao());
                novoItem.setUltimaAtualizacao(itemElementoARCAnterior.getUltimaAtualizacao());
                novoItem.setOperadorAtualizacao(itemElementoARCAnterior.getOperadorAtualizacao());
                novoItem.setAlterarDataUltimaAtualizacao(false);
                Documento novoDocumento =
                        documentoMediator.duplicarDocumentoItemElementoARCConclusaoAnalise(
                                itemElementoARCAnterior, novoItem, true);
                novoItem.setDocumento(novoDocumento);
                itemElementoDAO.saveOrUpdate(novoItem);
                anexoItemElementoMediator.duplicarAnexosItemElementoARCConclusaoCorec(
                        cicloAnterior, cicloNovo, itemElementoARCAnterior, novoItem, true);
            }
        }
    }

    private ItemElemento criarNovoItemElemento(Elemento novoElemento, ItemElemento itemElemento, 
            boolean isCopiarUsuarioAnterior) {
        ItemElemento novoItem = new ItemElemento();
        novoItem.setElemento(novoElemento);
        novoItem.setParametroItemElemento(itemElemento.getParametroItemElemento());
        novoItem.setDataAlteracao(itemElemento.getDataAlteracao());
        novoItem.setOperadorAlteracao(itemElemento.getOperadorAlteracao());
        if (isCopiarUsuarioAnterior) {
            novoItem.setUltimaAtualizacao(itemElemento.getUltimaAtualizacao());
            novoItem.setOperadorAtualizacao(itemElemento.getOperadorAtualizacao());
            novoItem.setAlterarDataUltimaAtualizacao(false);
        }
        return novoItem;
    }

    public ItemElemento obterItemElementoCorrespondenteARCVigente(List<ItemElemento> itensElementoARCVigente,
            ItemElemento itemElemento) {
        ItemElemento retorno = null;
        if (itensElementoARCVigente != null) {
            for (ItemElemento itemElementoARCVigente : itensElementoARCVigente) {
                if (itemElemento.getParametroItemElemento().getNome()
                        .equals(itemElementoARCVigente.getParametroItemElemento().getNome())) {
                    retorno = itemElementoARCVigente;
                    break;
                }
            }
            itensElementoARCVigente.remove(retorno);
        }
        if (retorno == null) {
            return new ItemElemento();
        }
        return retorno;
    }

    @Transactional(readOnly = true)
    public List<ItemElemento> buscarItensOrdenadosDoElemento(Elemento elemento) {
        return itemElementoDAO.buscarItensOrdenadosDoElemento(elemento);

    }

    @Transactional
    public void copiarDadosItemElementoARCAnterior(Ciclo ciclo, Elemento elemento, Elemento elementoARCAnterior) {
        List<ItemElemento> itensElementoARCAnterior = elementoARCAnterior.getItensElemento();
        for (ItemElemento item : elemento.getItensElemento()) {
            ItemElemento itemARCAnterior = obterItemElementoCorrespondenteARCVigente(itensElementoARCAnterior, item);
            if (itemARCAnterior.getPk() == null) {
                excluirItemElemento(ciclo, item);
            } else {
            if (itemARCAnterior.getDocumento() == null) {
                item.setDocumento(null);
            } else {
                DocumentoMediator.get().copiarDadosDocumentoARCAnterior(ciclo, item, itemARCAnterior);
            }
            item.setUltimaAtualizacao(itemARCAnterior.getUltimaAtualizacao());
            item.setOperadorAtualizacao(itemARCAnterior.getOperadorAtualizacao());
            item.setDataAlteracao(itemARCAnterior.getDataAlteracao());
            item.setOperadorAlteracao(itemARCAnterior.getOperadorAlteracao());
            item.setAlterarDataUltimaAtualizacao(false);
            itemElementoDAO.saveOrUpdate(item);
        }
    }
    }
    
    @Transactional
    public void excluirItensElemento(Ciclo ciclo, List<ItemElemento> itens) {
        for (ItemElemento item : itens) {
            excluirItemElemento(ciclo, item);
        }
    }

    private void excluirItemElemento(Ciclo ciclo, ItemElemento item) {
        DocumentoMediator.get().excluirDocumento(ciclo, item);
        itemElementoDAO.delete(item);
        itemElementoDAO.flush();
    }

    @Transactional
    public void alterar(ItemElemento item) {
        itemElementoDAO.update(item);
    }

}
