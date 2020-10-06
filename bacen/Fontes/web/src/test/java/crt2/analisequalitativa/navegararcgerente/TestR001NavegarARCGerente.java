package crt2.analisequalitativa.navegararcgerente;

import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TituloTelaARCEnum;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.vo.AvaliacaoRiscoControleVO;
import br.gov.bcb.sisaps.src.vo.ConsultaAvaliacaoRiscoControleVO;
import br.gov.bcb.sisaps.web.page.dominio.ciclo.painel.UtilNavegabilidadeARC;
import crt2.ConfiguracaoTestesWeb;

public class TestR001NavegarARCGerente extends ConfiguracaoTestesWeb {

    private int arc1;
    private int perfilDeRisco;
    private AvaliacaoRiscoControle avaliacaoRiscoControle;
    private AvaliacaoRiscoControleVO avaliacaoRiscoControleVO;
    private List<AvaliacaoRiscoControleVO> listaARCs;
    private PerfilRisco perfil;

    public String qualATelaDeDestino() {
        carregarARCs();
        IModel<AvaliacaoRiscoControleVO> rowModel = new Model<AvaliacaoRiscoControleVO>(avaliacaoRiscoControleVO);
        TituloTelaARCEnum titulo =
                UtilNavegabilidadeARC.avancarParaNovaPagina(rowModel, PerfilAcessoEnum.GERENTE, true);
        if (titulo == null) {
            return "";
        } else {
            return titulo.getDescricao();
        }
    }

    private void carregarARCs() {
        ConsultaAvaliacaoRiscoControleVO consulta = new ConsultaAvaliacaoRiscoControleVO();
        consulta.setTeste(true);
        listaARCs =
                AvaliacaoRiscoControleMediator.get().consultaHistoricoARC(consulta);
        for (AvaliacaoRiscoControleVO arcVO : listaARCs) {
            if (arcVO.getPk().equals(arc1)) {
                avaliacaoRiscoControleVO = arcVO;
                break;
            }
        }
    }

    public String notaDoArc() {
        return avaliacaoRiscoControle.getNotaVigenteDescricaoValor();
    }

    public String estado() {
        avaliacaoRiscoControle = AvaliacaoRiscoControleMediator.get().buscar(arc1);
        return avaliacaoRiscoControle.getEstado().getDescricao();
    }

    public String acao() {
        carregarARCs();
        IModel<AvaliacaoRiscoControleVO> rowModel = new Model<AvaliacaoRiscoControleVO>(avaliacaoRiscoControleVO);
        TituloTelaARCEnum titulo =
                UtilNavegabilidadeARC.avancarParaNovaPagina(rowModel, PerfilAcessoEnum.GERENTE, false);
        if (titulo == null) {
            return "";
        } else {
            return titulo.getDescricao();
        }

    }

    public int getArc1() {
        return arc1;
    }

    public void setArc1(int arc1) {
        this.arc1 = arc1;
    }

    public AvaliacaoRiscoControle getAvaliacaoRiscoControle() {
        return avaliacaoRiscoControle;
    }

    public void setAvaliacaoRiscoControle(AvaliacaoRiscoControle avaliacaoRiscoControle) {
        this.avaliacaoRiscoControle = avaliacaoRiscoControle;
    }

    public PerfilRisco getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilRisco perfil) {
        this.perfil = perfil;
    }

    public int getPerfilDeRisco() {
        return perfilDeRisco;
    }

    public void setPerfilDeRisco(int perfilDeRisco) {
        this.perfilDeRisco = perfilDeRisco;
    }



}
