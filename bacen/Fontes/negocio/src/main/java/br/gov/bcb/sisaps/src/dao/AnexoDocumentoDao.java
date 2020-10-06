package br.gov.bcb.sisaps.src.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.dao.GenericDAOParaListagens;
import br.gov.bcb.sisaps.src.dominio.AnexoDocumento;
import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.mediator.DocumentoMediator;
import br.gov.bcb.sisaps.src.vo.AnexoDocumentoVo;
import br.gov.bcb.sisaps.src.vo.ConsultaAnexo;

@Transactional
@Repository
public class AnexoDocumentoDao extends
        GenericDAOParaListagens<AnexoDocumento, Integer, AnexoDocumentoVo, ConsultaAnexo<AnexoDocumentoVo>> {

    public AnexoDocumentoDao() {
        super(AnexoDocumento.class, AnexoDocumentoVo.class);
    }

    @Override
    protected void montarCriterios(Criteria criteria, ConsultaAnexo<AnexoDocumentoVo> consulta) {
        //TODO não precisa implementar
    }

    public List<AnexoDocumento> buscarAnexos(Documento documento) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(AnexoDocumento.PROP_DOCUMENTO, documento));
        return cast(criteria.list());
    }
    
    public AnexoDocumento buscarAnexoDocumentoMesmoNome(Documento documento, String link) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq(AnexoDocumento.PROP_DOCUMENTO, documento));
        criteria.add(Restrictions.eq("link", link));
        return (AnexoDocumento) criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<AnexoDocumentoVo> listarAnexosConclusao() {
        SQLQuery query =
                getCurrentSession().createSQLQuery(
                        "SELECT distinct ane.AND_ID, ane.AND_DS_LINK, doc.DOC_ID, esc.ESC_ID "
                                + "FROM sup.AND_ANEXO_DOCUMENTO ane "
                                + "inner join sup.DOC_DOCUMENTO_SRC doc on doc.DOC_ID = ane.DOC_ID "
                                + "inner join sup.ESC_ES_CONCLUSAO esc on esc.DOC_ID_CONCLUSAO = doc.DOC_ID ");
        return montarDadosAnexo(query.list());
    }

    @SuppressWarnings("unchecked")
    public List<AnexoDocumentoVo> listarAnexosPerfilAtuacao() {
        SQLQuery query =
                getCurrentSession()
                        .createSQLQuery(
                                "SELECT distinct ane.AND_ID, ane.AND_DS_LINK, doc.DOC_ID, esp.ESP_ID "
                                        + "FROM sup.AND_ANEXO_DOCUMENTO ane "
                                        + "inner join sup.DOC_DOCUMENTO_SRC doc on doc.DOC_ID = ane.DOC_ID "
                                        + "inner join sup.ESP_ES_PERFIL_ATUACAO esp on esp.DOC_ID_PERFIL_ATUACAO = doc.DOC_ID ");
        return montarDadosAnexo(query.list());
    }

    @SuppressWarnings("unchecked")
    public List<AnexoDocumentoVo> listarAnexosItemAQT() {
        SQLQuery query =
                getCurrentSession().createSQLQuery(
                        "SELECT distinct ane.AND_ID, ane.AND_DS_LINK, doc.DOC_ID, ita.ITA_ID "
                                + "FROM sup.AND_ANEXO_DOCUMENTO ane "
                                + "inner join sup.DOC_DOCUMENTO_SRC doc on doc.DOC_ID = ane.DOC_ID "
                                + "inner join sup.ITA_ITEM_ELEMENTO_AQT ita on ita.DOC_ID = doc.DOC_ID ");
        return montarDadosAnexo(query.list());
    }

    @SuppressWarnings("unchecked")
    public List<AnexoDocumentoVo> listarAnexosItemARC() {
        SQLQuery query =
                getCurrentSession().createSQLQuery(
                        "SELECT distinct ane.AND_ID, ane.AND_DS_LINK, doc.DOC_ID, ite.ITE_ID "
                                + "FROM sup.AND_ANEXO_DOCUMENTO ane "
                                + "inner join sup.DOC_DOCUMENTO_SRC doc on doc.DOC_ID = ane.DOC_ID "
                                + "inner join sup.ITE_ITEM_ELEMENTO ite on ite.DOC_ID = doc.DOC_ID ");
        return montarDadosAnexo(query.list());
    }

    private List<AnexoDocumentoVo> montarDadosAnexo(List<Object> objects) {
        List<AnexoDocumentoVo> anexos = new ArrayList<AnexoDocumentoVo>();
        if (objects != null && !objects.isEmpty()) {
            for (Object object : objects) {
                AnexoDocumentoVo vo = new AnexoDocumentoVo();
                Documento documento = DocumentoMediator.get().buscarPorPk((Integer) ((Object[]) object)[2]);
                vo.setPk((Integer) ((Object[]) object)[0]);
                vo.setLink((String) ((Object[]) object)[1]);
                vo.setDocumento(documento);
                vo.setSequencial((Integer) ((Object[]) object)[3]);
                vo.setDescricao("Anexo");
                anexos.add(vo);
            }
        }
        return anexos;
    }

}
