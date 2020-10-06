package br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt;

import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dao.DocumentoDAO;
import br.gov.bcb.sisaps.src.dao.analisequantitativaaqt.ItemElementoAQTDAO;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ElementoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ItemElementoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroElementoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroItemElementoAQT;
import br.gov.bcb.sisaps.src.mediator.DocumentoMediator;
import br.gov.bcb.sisaps.src.validacao.RegraEdicaoANEFInspetorPermissaoAlteracao;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;

@Service
@Transactional(readOnly = true)
public class ItemElementoAQTMediator {

    @Autowired
    private ItemElementoAQTDAO itemElementoAQTDAO;
    @Autowired
    private DocumentoDAO documentoDAO;

    public static ItemElementoAQTMediator get() {
        return SpringUtils.get().getBean(ItemElementoAQTMediator.class);
    }

    public void criarItensElementoAQT(AnaliseQuantitativaAQT analiseQuantitativaAQT,
            ParametroElementoAQT parametroElementoAQT, ElementoAQT elemento,
            List<ParametroItemElementoAQT> listaParItensElementosAQT, int cont) {

        for (int i = 0; i < 2; i++) {
            ItemElementoAQT itemElementoAQT = new ItemElementoAQT();
            itemElementoAQT.setParametroItemElemento(adicionarItem(listaParItensElementosAQT, cont));
            itemElementoAQT.setElemento(elemento);
            itemElementoAQTDAO.save(itemElementoAQT);
            itemElementoAQTDAO.flush();
        }
    }

    //CHECKSTYLE:OFF
    private ParametroItemElementoAQT adicionarItem(List<ParametroItemElementoAQT> listaParItensElementosAQT, int cont) {
        ParametroItemElementoAQT paramentoItem = null;
        if (listaParItensElementosAQT.contains(listaParItensElementosAQT.get(cont))) {
            paramentoItem = listaParItensElementosAQT.get(cont);
            listaParItensElementosAQT.remove(cont);
        }
        cont++;
        return paramentoItem;

    }

    //CHECKSTYLE:ON

    @Transactional
    public void duplicarItensElementoAQTConclusaoAnalise(Ciclo ciclo, ElementoAQT elemento, ElementoAQT novoElemento,
            boolean isConclusaoCorec) {
        for (ItemElementoAQT itemElemento : elemento.getItensElemento()) {
            ItemElementoAQT novoItem = new ItemElementoAQT();
            novoItem.setElemento(novoElemento);
            novoItem.setParametroItemElemento(itemElemento.getParametroItemElemento());
            novoItem.setDataAlteracao(itemElemento.getDataAlteracao());
            novoItem.setOperadorAlteracao(itemElemento.getOperadorAlteracao());
            if (isConclusaoCorec) {
                novoItem.setUltimaAtualizacao(itemElemento.getUltimaAtualizacao());
                novoItem.setOperadorAtualizacao(itemElemento.getOperadorAtualizacao());
                novoItem.setAlterarDataUltimaAtualizacao(false);
            }
            // duplicar documento do item
            Documento novoDocumento =
                    DocumentoMediator.get().duplicarDocumentoItemElementoAQTConclusaoAnalise(ciclo, itemElemento,
                            novoItem, isConclusaoCorec);
            novoItem.setDocumento(novoDocumento);
            itemElementoAQTDAO.save(novoItem);
            // duplicar anexo do documento do item 
            AnexoItemElementoAQTMediator.get().duplicarAnexosItemElementoARCConclusaoAnalise(ciclo, itemElemento,
                    novoItem, isConclusaoCorec);
        }
    }

    @Transactional
    public String salvarJustificativaItemElementoAQT(AnaliseQuantitativaAQT anef, ItemElementoAQT item,
            boolean isDuplicacaoARC) {
        if (!isDuplicacaoARC) {
            new RegraEdicaoANEFInspetorPermissaoAlteracao(anef).validar();
            anef.setOperadorPreenchido(null);
            anef.setOperadorAnalise(null);
            anef.setOperadorConclusao(null);
        }

        if (item.getDocumento() != null
                && SisapsUtil.isTextoCKEditorBrancoOuNulo(item.getDocumento().getJustificativa())) {
            item.getDocumento().setJustificativa(null);
        }
        if (null != item.getDocumento()) {
            if (null != item.getDocumento().getPk()) {
                documentoDAO.update(item.getDocumento());
            } else {
                documentoDAO.saveOrUpdate(item.getDocumento());
            }
            documentoDAO.getSessionFactory().getCurrentSession().flush();
        }
        UsuarioAplicacao usuarioAplicacao = (UsuarioAplicacao) UsuarioCorrente.get();


        if (!isDuplicacaoARC) {
            AnaliseQuantitativaAQTMediator.get().alterarEstadoANEFParaEmEdicao(anef);
            item.setOperadorAlteracao(usuarioAplicacao.getLogin());
            item.setDataAlteracao(DataUtil.getDateTimeAtual());
        }
        
        item.getElemento().getAnaliseQuantitativaAQT();
        itemElementoAQTDAO.update(item);
        itemElementoAQTDAO.flush();

        return "Justificativa para '" + item.getParametroItemElemento().getNome() + "' salva com sucesso.";
    }

    public List<ItemElementoAQT> buscarItensOrdenadosDoElemento(ElementoAQT elemento) {
        return itemElementoAQTDAO.buscarItensOrdenadosDoElemento(elemento);

    }

    public ItemElementoAQT obterItemElementoPorDocumento(Documento documento) {
        return itemElementoAQTDAO.obterItemElementoPorDocumento(documento);
    }

    public ItemElementoAQT obterItemElementoCorrespondenteANEFVigente(List<ItemElementoAQT> itensElementoARCVigente,
            ItemElementoAQT itemElemento) {
        ItemElementoAQT retorno = null;
        if (itensElementoARCVigente != null) {
            for (ItemElementoAQT itemElementoARCVigente : itensElementoARCVigente) {
                if (itemElemento.getParametroItemElemento().getNome()
                        .equals(itemElementoARCVigente.getParametroItemElemento().getNome())) {
                    retorno = itemElementoARCVigente;
                    break;
                }
            }
            itensElementoARCVigente.remove(retorno);
        }
        if (retorno == null) {
            return new ItemElementoAQT();
        }
        return retorno;
    }

    public ItemElementoAQT buscarPorPk(Integer pk) {
        ItemElementoAQT itemElemento = itemElementoAQTDAO.load(pk);
        inicializarDependencias(itemElemento);
        return itemElemento;
    }

    private void inicializarDependencias(ItemElementoAQT itemElemento) {
        if (itemElemento != null && itemElemento.getDocumento() != null) {
            Hibernate.initialize(itemElemento.getDocumento());
        }
        if (itemElemento.getElemento() != null) {
            Hibernate.initialize(itemElemento.getElemento());
        }
    }

}
