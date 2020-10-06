package br.gov.bcb.sisaps.src.mediator;

import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.UnidadeDAO;
import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.Unidade;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoUnidadeAtividadeEnum;
import br.gov.bcb.sisaps.src.validacao.RegraCriterioPesquisaSemResultado;
import br.gov.bcb.sisaps.src.validacao.RegraUnidade;
import br.gov.bcb.sisaps.src.vo.ConsultaUnidadeVO;
import br.gov.bcb.sisaps.src.vo.UnidadeVO;

@Service
@Transactional(readOnly = true)
public class UnidadeMediator extends AbstractMediatorPaginado<UnidadeVO, Integer, ConsultaUnidadeVO> {

    private static final String NOME_UNIDADE_CORPORATIVA = "Unidade Corporativa";

    @Autowired
    protected MetodologiaMediator metodologiaMediator;

    @Autowired
    protected AtividadeMediator atividadeMediator;

    @Autowired
    protected CicloMediator cicloMediator;

    @Autowired
    private UnidadeDAO unidadeDao;

    @Override
    protected UnidadeDAO getDao() {
        return unidadeDao;
    }

    public static UnidadeMediator get() {
        return SpringUtils.get().getBean(UnidadeMediator.class);
    }

    @Override
    public List<UnidadeVO> consultar(ConsultaUnidadeVO consulta) {
        List<UnidadeVO> lista = unidadeDao.consultar(consulta);
        new RegraCriterioPesquisaSemResultado().validar(lista);
        return lista;
    }

    public boolean existeUnidadeMesmoNome(Unidade unidade) {
        ConsultaUnidadeVO consulta = new ConsultaUnidadeVO();
        consulta.setNome(unidade.getNome());
        consulta.setMatriz(unidade.getMatriz());
        List<UnidadeVO> lista = unidadeDao.consultar(consulta);
        return !lista.isEmpty();
    }

    @Transactional
    public void incluirUnidadeCorporativa(Matriz matriz, Ciclo ciclo) {
        Unidade unidade = new Unidade();
        unidade.setNome(NOME_UNIDADE_CORPORATIVA);
        unidade.setMatriz(matriz);
        unidade.setTipoUnidade(TipoUnidadeAtividadeEnum.CORPORATIVO);
        unidade.setParametroPeso(metodologiaMediator.buscarMaiorPesoMetodologia(ciclo.getMetodologia()));
        unidadeDao.save(unidade);
        unidadeDao.flush();
    }

    @Transactional
    public void incluirUnidadeNegocio(Unidade unidade) {
        validarInclusao(unidade, false);
        unidade.setTipoUnidade(TipoUnidadeAtividadeEnum.NEGOCIO);
        unidadeDao.saveOrUpdate(unidade);
        unidadeDao.flush();
    }

    @Transactional
    public void alterarUnidade(Unidade unidade, TipoUnidadeAtividadeEnum tipo) {
        validarInclusao(unidade, true);
        unidade.setTipoUnidade(tipo);
        unidadeDao.merge(unidade);
        if (TipoUnidadeAtividadeEnum.CORPORATIVO.equals(unidade.getTipoUnidade())) {
            Ciclo ciclo = unidade.getMatriz().getCiclo();
            cicloMediator.alterar(ciclo);
        }
        unidadeDao.flush();
    }

    public void validarInclusao(Unidade unidade, boolean isAlterar) {
        new RegraUnidade(unidade, this).validar(isAlterar);
    }

    public List<Unidade> buscarUnidadesMatriz(Matriz matriz, TipoUnidadeAtividadeEnum tipo) {
        return unidadeDao.buscarUnidadesMatriz(matriz, tipo);

    }

    public Unidade loadPK(Integer pk) {
        Unidade result = unidadeDao.getRecord(pk);
        inicializarDependencias(result);
        if (TipoUnidadeAtividadeEnum.CORPORATIVO.equals(result.getTipoUnidade())) {
            result.setFatorRelevancia(result.getMatriz().getNumeroFatorRelevanciaUC());
        }
        return result;
    }
    
    public Unidade getPK(Integer pk) {
        Unidade result = unidadeDao.getRecord(pk);
        if (TipoUnidadeAtividadeEnum.CORPORATIVO.equals(result.getTipoUnidade())) {
            Hibernate.initialize(result.getMatriz());
            result.setFatorRelevancia(result.getMatriz().getNumeroFatorRelevanciaUC());
        }
        return result;
    }

    public void inicializarDependencias(Unidade result) {
        if (result.getAtividades() != null) {
            Hibernate.initialize(result.getAtividades());
        }
        Hibernate.initialize(result.getMatriz());
        Hibernate.initialize(result.getMatriz().getCiclo());

    }

    @Transactional
    public void excluir(Integer pk) {
        Unidade unidade = loadPK(pk);
        new RegraUnidade(unidade, this).validarExclusao();
        MatrizCicloMediator.get().alterarVersaoMatriz(unidade.getMatriz());
        atividadeMediator.excluir(unidade.getAtividades());
        unidade.setAtividades(null);
        unidadeDao.flush();
        unidadeDao.delete(unidade);
        unidadeDao.flush();
    }

    public void flush() {
        unidadeDao.flush();
    }

    @Transactional
    public void excluirListaUnidade(List<Unidade> unidades) {
        if (unidades != null && !unidades.isEmpty()) {
            for (Unidade unidade2 : unidades) {
                atividadeMediator.excluir(unidade2.getAtividades());
                unidade2.setAtividades(null);
                unidadeDao.flush();
                unidadeDao.delete(unidade2);
            }
        }
    }

    @Transactional
    public void criarVersaoUnidades(Matriz matrizBase, Matriz matrizNova, boolean isEncerrarCorec) {
        for (Unidade unidade : unidadeDao.buscarUnidadesMatriz(matrizBase)) {
            Unidade unidadeNova = new Unidade();
            unidadeNova.setNome(unidade.getNome());
            unidadeNova.setMatriz(matrizNova);
            unidadeNova.setParametroPeso(unidade.getParametroPeso());
            unidadeNova.setTipoUnidade(unidade.getTipoUnidade());
            if (isEncerrarCorec) {
                unidadeNova.setFatorRelevancia(unidade.getFatorRelevancia());
                unidadeNova.setUltimaAtualizacao(unidade.getUltimaAtualizacao());
                unidadeNova.setOperadorAtualizacao(unidade.getOperadorAtualizacao());
                unidadeNova.setAlterarDataUltimaAtualizacao(false);
            }
            unidadeDao.save(unidadeNova);
        }
    }

    public Unidade getUnidadeVersionada(Unidade unidade, Matriz matrizNova) {
        ConsultaUnidadeVO consulta = new ConsultaUnidadeVO();
        consulta.setMatriz(matrizNova);
        consulta.setNome(unidade.getNome());
        consulta.setTipo(unidade.getTipoUnidade());
        return loadPK(unidadeDao.consultar(consulta).get(0).getPk());
    }

    public Long somarPesoTipoUnidade(Matriz matriz, TipoUnidadeAtividadeEnum tipoUnidade) {
        return unidadeDao.somarPesoTipoUnidade(matriz, tipoUnidade);
    }

    public boolean existeUnidadeEAtividadeDeNegocioComMaiorPeso(List<Unidade> unidades,
            List<Atividade> atividadesMatriz, Integer idMaiorPeso) {
        for (Unidade unidade : unidades) {
            if (unidade.getParametroPeso().getPk().equals(idMaiorPeso)) {
                return true;
            }
        }

        for (Atividade atividade : atividadesMatriz) {
            if (atividade.getParametroPeso().getPk().equals(idMaiorPeso)) {
                return true;
            }
        }

        return false;
    }

    public boolean podeExcluirUnidade(Unidade unidade) {
        boolean podeExcluir = true;
        List<Atividade> atividadesUnidade = AtividadeMediator.get().buscarAtividadesUnidade(unidade);
        if (atividadesUnidade != null && !atividadesUnidade.isEmpty()) {
            for (Atividade atividade : atividadesUnidade) {
                if (!CelulaRiscoControleMediator.get().buscarCelularPorAtividade(atividade).isEmpty()) {
                    podeExcluir = false;
                }
            }
        }
        return podeExcluir;
    }

}
