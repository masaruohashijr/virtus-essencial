package crt2.dominio.analisequantitativa.detalhargestaoanaliseeconomicofinanceira;

import java.util.List;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001DetalharGestaoAnaliseEconomicoFinanceira extends ConfiguracaoTestesNegocio {
    Ciclo ciclo;
    public List<AnaliseQuantitativaAQT> listarAnefNovoQuadro(String loginUsuarioLogado, Integer idCiclo) {
        logar(loginUsuarioLogado);
        ciclo = CicloMediator.get().buscarCicloPorPK(idCiclo);
        return AnaliseQuantitativaAQTMediator.get().listarANEFsNovoQuadro(ciclo);
    }

    public Integer getID(AnaliseQuantitativaAQT aqt) {
        return aqt.getPk();
    }

    public String getDescricao(AnaliseQuantitativaAQT aqt) {
        return aqt.getParametroAQT().getDescricao();
    }

    public String getNota(AnaliseQuantitativaAQT aqt) {
        PerfilRisco perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());
        return AnaliseQuantitativaAQTMediator.get().notaAnef(aqt, ciclo, perfilUsuario(), true, perfilRiscoAtual);
    }

    public String getDestaque(AnaliseQuantitativaAQT aqt) {
        return SimNaoEnum.getTipo(AnaliseQuantitativaAQTMediator.get().corDestaqueAnefRascunho(aqt)).getDescricao();
    }

    public String getPercentual(AnaliseQuantitativaAQT aqt) {
        return aqt.getPesoAQT().getValor().toString();
    }

}
