package br.gov.bcb.sisaps.src.vo;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoAQTEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoGrupoEnum;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.aqt.AnaliseQuantitativaAQTVO;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.aqt.ParametroAQTVO;
import br.gov.bcb.sisaps.util.consulta.Consulta;

public class ConsultaAnaliseQuantitativaAQTVO extends Consulta<AnaliseQuantitativaAQTVO> {

    private ParametroAQTVO parametroVO;
    private String nomeComponente;
    private TipoGrupoEnum tipoGrupo;
    private EstadoAQTEnum estadoANEF;
    private String nomeES;
    private boolean isAcao;
   
    public ParametroAQTVO getParametroVO() {
        return parametroVO;
    }

    public void setParametroVO(ParametroAQTVO parametroVO) {
        this.parametroVO = parametroVO;
    }


    public String getNomeComponente() {
        return nomeComponente;
    }

    public void setNomeComponente(String nomeComponente) {
        this.nomeComponente = nomeComponente;
    }

    public TipoGrupoEnum getTipoGrupo() {
        return tipoGrupo;
    }

    public void setTipoGrupo(TipoGrupoEnum tipoGrupo) {
        this.tipoGrupo = tipoGrupo;
    }

    public EstadoAQTEnum getEstadoANEF() {
        return estadoANEF;
    }

    public void setEstadoANEF(EstadoAQTEnum estadoANEF) {
        this.estadoANEF = estadoANEF;
    }

    public String getNomeES() {
        return nomeES;
    }

    public void setNomeES(String nomeES) {
        this.nomeES = nomeES;
    }

    public boolean isAcao() {
        return isAcao;
    }

    public void setAcao(boolean isAcao) {
        this.isAcao = isAcao;
    }

}

