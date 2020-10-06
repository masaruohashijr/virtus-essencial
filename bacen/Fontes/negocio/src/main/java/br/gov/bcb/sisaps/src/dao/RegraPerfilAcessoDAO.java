package br.gov.bcb.sisaps.src.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.gov.bcb.sisaps.dao.GenericDAOParaListagens;
import br.gov.bcb.sisaps.src.dominio.RegraPerfilAcesso;
import br.gov.bcb.sisaps.src.vo.ConsultaRegraPerfilAcessoVO;
import br.gov.bcb.sisaps.src.vo.RegraPerfilAcessoVO;

@Repository
public class RegraPerfilAcessoDAO extends
        GenericDAOParaListagens<RegraPerfilAcesso, Integer, RegraPerfilAcessoVO, ConsultaRegraPerfilAcessoVO> {

    private static final String PROP_MATRICULA = "matricula";
    private static final String PROP_SITUACAO = "situacao";
    private static final String PROP_SUBSTITUTO_EVENTUAL_FUNCAO = "substitutoEventualFuncao";
    private static final String PROP_SUBSTITUTO_PRAZO_CERTO = "substitutoPrazoCerto";
    private static final String PROP_CODIGO_FUNCAO = "codigoFuncao";
    private static final String PROP_LOCALIZACOES_SUBORDINADAS = "localizacoesSubordinadas";
    private static final String PROP_LOCALIZACAO = "localizacao";
    private static final String PROP_PK = "pk";
    private static final String PROP_PERFIL_ACESSO = "perfilAcesso";

    public RegraPerfilAcessoDAO() {
        super(RegraPerfilAcesso.class, RegraPerfilAcessoVO.class);
    }

    @Override
    protected void montarCriterios(Criteria criteria, ConsultaRegraPerfilAcessoVO consulta) {
        if (consulta.getPerfilAcesso() != null) {
            criteria.add(Restrictions.eq(PROP_PERFIL_ACESSO, consulta.getPerfilAcesso()));
        }
        // Colunas retornadas na consulta
        criteria.setProjection(Projections.projectionList().add(Projections.property(PROP_PK), PROP_PK)
                .add(Projections.property(PROP_PERFIL_ACESSO), PROP_PERFIL_ACESSO)
                .add(Projections.property(PROP_LOCALIZACAO), PROP_LOCALIZACAO)
                .add(Projections.property(PROP_LOCALIZACOES_SUBORDINADAS), PROP_LOCALIZACOES_SUBORDINADAS)
                .add(Projections.property(PROP_CODIGO_FUNCAO), PROP_CODIGO_FUNCAO)
                .add(Projections.property(PROP_SUBSTITUTO_EVENTUAL_FUNCAO), PROP_SUBSTITUTO_EVENTUAL_FUNCAO)
                .add(Projections.property(PROP_SUBSTITUTO_PRAZO_CERTO), PROP_SUBSTITUTO_PRAZO_CERTO)
                .add(Projections.property(PROP_SITUACAO), PROP_SITUACAO)
                .add(Projections.property(PROP_MATRICULA), PROP_MATRICULA));
    }

}
