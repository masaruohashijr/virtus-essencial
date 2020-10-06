package br.gov.bcb.sisaps.src.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.dao.GenericDAOLocal;
import br.gov.bcb.sisaps.src.dominio.AnexoApresentacao;
import br.gov.bcb.sisaps.src.dominio.Apresentacao;
import br.gov.bcb.sisaps.src.dominio.TextoApresentacao;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.CampoApresentacaoEnum;

@Transactional
@Repository
public class TextoApresentacaoDao extends GenericDAOLocal<TextoApresentacao, Integer> {

	// Construtor
	public TextoApresentacaoDao() {
		super(TextoApresentacao.class);
	}

	// Retorna um texto.
	public TextoApresentacao buscarTexto(Apresentacao apresentacao, CampoApresentacaoEnum campo) {
		Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
		criteria.add(Restrictions.eq(AnexoApresentacao.PROP_APRESENTACAO, apresentacao));
		criteria.add(Restrictions.eq("secao", campo.getSecao()));
		criteria.add(Restrictions.eq("campo", campo));
		return (TextoApresentacao) criteria.uniqueResult();
	}
}
