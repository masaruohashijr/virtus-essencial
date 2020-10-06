package br.gov.bcb.sisaps.src.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.dao.GenericDAOParaListagens;
import br.gov.bcb.sisaps.src.dominio.AnexoApresentacao;
import br.gov.bcb.sisaps.src.dominio.Apresentacao;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.CampoApresentacaoEnum;
import br.gov.bcb.sisaps.src.vo.AnexoApresentacaoVO;
import br.gov.bcb.sisaps.src.vo.ConsultaAnexo;

@Transactional
@Repository
public class AnexoApresentacaoDao
		extends
		GenericDAOParaListagens<AnexoApresentacao, Integer, AnexoApresentacaoVO, ConsultaAnexo<AnexoApresentacaoVO>> {

	public AnexoApresentacaoDao() {
		super(AnexoApresentacao.class, AnexoApresentacaoVO.class);
	}

	@Override
	protected void montarCriterios(Criteria criteria,
			ConsultaAnexo<AnexoApresentacaoVO> consulta) {
		// Não precisa implementar.
	}

	public List<AnexoApresentacao> buscarAnexos(Apresentacao apresentacao) {
		Criteria criteria = getCurrentSession().createCriteria(
				getPersistentClass());
		criteria.add(Restrictions.eq(AnexoApresentacao.PROP_APRESENTACAO, apresentacao));
		return cast(criteria.list());
	}

	public AnexoApresentacao buscarAnexoDocumentoMesmoNome(
			Apresentacao apresentacao, String link, CampoApresentacaoEnum campo) {
		Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
		criteria.add(Restrictions.eq(AnexoApresentacao.PROP_APRESENTACAO, apresentacao));
		criteria.add(Restrictions.eq("link", link));
		criteria.add(Restrictions.eq("secao", campo.getSecao()));
		return (AnexoApresentacao) criteria.uniqueResult();
	}

	public List<AnexoApresentacao> buscarAnexoDocumentoMesmoNome(
			Apresentacao apresentacao, String link) {
		Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
		criteria.add(Restrictions.eq(AnexoApresentacao.PROP_APRESENTACAO, apresentacao));
		criteria.add(Restrictions.eq("link", link));
		return cast(criteria.list());
	}
	
    public List<AnexoApresentacao> listarAnexos() {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        return cast(criteria.list());
    }

}
