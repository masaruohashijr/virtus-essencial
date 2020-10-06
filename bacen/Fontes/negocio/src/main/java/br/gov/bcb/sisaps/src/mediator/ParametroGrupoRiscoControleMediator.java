package br.gov.bcb.sisaps.src.mediator;

import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.ParametroGrupoRiscoControleDAO;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;

@Service
@Transactional(readOnly = true)
public class ParametroGrupoRiscoControleMediator {

    @Autowired
    private ParametroGrupoRiscoControleDAO parametroGrupoRiscoControleDAO;

    public static ParametroGrupoRiscoControleMediator get() {
        return SpringUtils.get().getBean(ParametroGrupoRiscoControleMediator.class);
    }

    public ParametroGrupoRiscoControle buscarPorPk(Integer parametroGrupoPk) {
        return parametroGrupoRiscoControleDAO.load(parametroGrupoPk);
    }

    /**
     * Busca todos os parâmetros de grupo de RISCO que estão na matriz passada por parâmetro e
     * também os parâmetros marcadas com síntese obrigatória que NÃO estão na matriz
     * 
     * @param matriz
     * @return
     */
    public List<ParametroGrupoRiscoControle> buscarGruposRiscoDaMatrizESinteseObrigatoria(
            List<Integer> idsGrupoRiscoDaMatriz) {
        return parametroGrupoRiscoControleDAO.buscarGruposRiscoDaMatrizESinteseObrigatoria(idsGrupoRiscoDaMatriz,
                MetodologiaMediator.get().buscarMetodologiaDefault());
    }

    public List<Integer> buscarIdsGruposRiscoDaMatriz(Matriz matriz) {
        if (matriz == null) {
            return null;
        } else {
            return parametroGrupoRiscoControleDAO.buscarIdsGruposRiscoDaMatriz(matriz);
        }
    }

    public void inicializarDependencias(ParametroGrupoRiscoControle parametro) {
        if (parametro != null) {
            Hibernate.initialize(parametro);
            Hibernate.initialize(parametro.getMetodologia());
        }

    }

    /**
     * Buscar o Parametro de Grupo de Risco a partir do seu controle.
     * 
     * @param pkParametroGrupoControle
     * @return
     */
    public ParametroGrupoRiscoControle buscarParametroGrupoRisco(Integer pkParametroGrupoControle) {
        return parametroGrupoRiscoControleDAO.buscarParametroGrupoRisco(pkParametroGrupoControle);
    }

    public boolean existeARCAnalisadoGrupoRisco(ParametroGrupoRiscoControle parametroGrupoRisco, Matriz matriz) {
        return parametroGrupoRiscoControleDAO.existeARCAnalisadoGrupoRisco(parametroGrupoRisco, matriz);
    }

    public List<CelulaRiscoControle> buscarCelulasARCsAnalisadosGrupoRiscoControleEversao(
            ParametroGrupoRiscoControle parametroGrupoRisco, List<VersaoPerfilRisco> versoes) {
        return parametroGrupoRiscoControleDAO.buscarCelulasARCsAnalisadosGrupoRiscoControle(parametroGrupoRisco, null,
                versoes);
    }

    public List<ParametroGrupoRiscoControle> buscarGruposSinteseObrigatorios(Metodologia metodologia) {
        return parametroGrupoRiscoControleDAO.buscarGruposSinteseObrigatorios(metodologia);
    }

    public List<ParametroGrupoRiscoControle> buscarGrupos() {
        return parametroGrupoRiscoControleDAO.buscarGrupos();
    }
    
    public ParametroGrupoRiscoControle buscarParametroRCExterno(Metodologia metodologia) {
        return parametroGrupoRiscoControleDAO.buscarParametroRCExterno(metodologia);
    }
}
