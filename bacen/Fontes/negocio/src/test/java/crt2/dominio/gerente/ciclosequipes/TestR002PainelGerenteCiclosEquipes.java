package crt2.dominio.gerente.ciclosequipes;

import java.util.Arrays;
import java.util.List;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.vo.CicloVO;
import br.gov.bcb.sisaps.src.vo.ConsultaCicloVO;
import br.gov.bcb.sisaps.util.consulta.Ordenacao;
import crt2.ConfiguracaoTestesNegocio;

public class TestR002PainelGerenteCiclosEquipes extends ConfiguracaoTestesNegocio {
    
    public List<CicloVO> buscarEquipe(String equipe) {
        ConsultaCicloVO consulta = new ConsultaCicloVO();
        consulta.setPerfil(PerfilAcessoEnum.GERENTE);
        consulta.setEstados(Arrays.asList(EstadoCicloEnum.EM_ANDAMENTO, EstadoCicloEnum.COREC));
        consulta.setRotuloLocalizacao(equipe);
        Ordenacao ordenacao = new Ordenacao();
        ordenacao.setPropriedade("entidadeSupervisionavel.nome");
        ordenacao.setCrescente(true);
        consulta.setOrdenacao(ordenacao);
        return CicloMediator.get().consultarSemValidacao(consulta, false);
    }
    
    public String getNomeEquipe(CicloVO retorno) {
        return retorno.getEntidadeSupervisionavel().getNome();
    }

    public String getDataInicio(CicloVO retorno) {
        return  retorno.getDataInicioFormatada();
    }
    
    public String getDataCorec(CicloVO retorno) {
        return  retorno.getDataPrevisaoFormatada();
    }
    
    public String getEstado(CicloVO retorno) {
        return  retorno.getEstadoCiclo().getEstado().getDescricao();
    }
    
    
    
    
}
