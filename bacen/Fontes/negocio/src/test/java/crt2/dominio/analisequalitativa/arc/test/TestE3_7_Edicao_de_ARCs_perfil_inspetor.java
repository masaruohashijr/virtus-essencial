package crt2.dominio.analisequalitativa.arc.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import br.gov.bcb.sisaps.src.dominio.AvaliacaoARC;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.Elemento;
import br.gov.bcb.sisaps.src.dominio.ItemElemento;
import br.gov.bcb.sisaps.src.dominio.TendenciaARC;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfisNotificacaoEnum;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoARCMediator;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.ElementoMediator;
import br.gov.bcb.sisaps.src.mediator.ItemElementoMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroNotaMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroTendenciaMediator;
import br.gov.bcb.sisaps.src.mediator.TendenciaMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestE3_7_Edicao_de_ARCs_perfil_inspetor extends ConfiguracaoTestesNegocio {

    private static final String USUARIO_DELIQ_SISLIQ110 = "deliq.sisliq110";
    @Autowired
    private AvaliacaoRiscoControleMediator avaliacaoRiscoControleMediator;
    @Autowired
    private AvaliacaoARCMediator avaliacaoARCMediator;
    @Autowired
    private ParametroNotaMediator parametroNotaMediator;
    @Autowired
    private CicloMediator cicloMediator;
    @Autowired
    private ElementoMediator elementoMediator;
    @Autowired
    private ItemElementoMediator itemElementoMediator;
    @Autowired
    private TendenciaMediator tendenciaMediator;
    @Autowired
    private ParametroTendenciaMediator parametroTendenciaMediator;

    public void salvarAvaliacaoInspetor(String idARC, String matriculaServidor, String idParamentoNota,
            String justificativa) {
        logar(USUARIO_DELIQ_SISLIQ110, matriculaServidor);
        AvaliacaoRiscoControle arc = avaliacaoRiscoControleMediator.buscarPorPk(Integer.valueOf(idARC));
        AvaliacaoARC avaliacaoARC = criarAvaliacaoARC(idParamentoNota, justificativa, arc);
        salvarAvaliacaoInspetor(null, arc, avaliacaoARC);
    }

    public void salvarAvaliacaoInspetor(String idARC, String idCiclo, String matriculaServidor,
            String idParamentroNota, String justificativa) {
        logar(USUARIO_DELIQ_SISLIQ110, matriculaServidor);
        Ciclo ciclo = cicloMediator.loadPK(Integer.valueOf(idCiclo));
        AvaliacaoRiscoControle arc = avaliacaoRiscoControleMediator.buscarPorPk(Integer.valueOf(idARC));
        AvaliacaoARC avaliacaoARC = criarAvaliacaoARC(idParamentroNota, justificativa, arc);
        salvarAvaliacaoInspetor(ciclo, arc, avaliacaoARC);
    }

    private AvaliacaoARC criarAvaliacaoARC(String idParamentroNota, String justificativa, AvaliacaoRiscoControle arc) {
        AvaliacaoARC avaliacaoARC = new AvaliacaoARC();
        avaliacaoARC.setAvaliacaoRiscoControle(arc);
        avaliacaoARC.setPerfil(PerfisNotificacaoEnum.INSPETOR);
        avaliacaoARC.setParametroNota(parametroNotaMediator.buscarPorPK(Integer.valueOf(idParamentroNota)));
        avaliacaoARC.setJustificativa(justificativa);
        arc.setAvaliacoesArc(new ArrayList<AvaliacaoARC>());
        return avaliacaoARC;
    }

    private void salvarAvaliacaoInspetor(Ciclo ciclo, AvaliacaoRiscoControle arc, AvaliacaoARC avaliacaoARC) {
        erro = null;
        try {
            avaliacaoARCMediator.salvarNovaNotaARC(ciclo, arc, avaliacaoARC);
        } catch (NegocioException e) {
            erro = e;
        }
    }

    public void salvarElementoInspetor(String idARC, String idCiclo, String idElemento, String matriculaServidor,
            String idParametroNota) {
        logar(USUARIO_DELIQ_SISLIQ110, matriculaServidor);
        Ciclo ciclo = cicloMediator.loadPK(Integer.valueOf(idCiclo));
        AvaliacaoRiscoControle arc = avaliacaoRiscoControleMediator.buscarPorPk(Integer.valueOf(idARC));
        Elemento elemento = elementoMediator.buscarPorPk(Integer.valueOf(idElemento));
        if (StringUtils.isNotBlank(idParametroNota)) {
            elemento.setParametroNotaInspetor(parametroNotaMediator.buscarPorPK(Integer.valueOf(idParametroNota)));
        } else {
            elemento.setParametroNotaInspetor(null);
        }
        salvarElemento(ciclo, arc, elemento);
    }

    private void salvarElemento(Ciclo ciclo, AvaliacaoRiscoControle arc, Elemento elemento) {
        erro = null;
        try {
            elementoMediator.salvarNovaNotaElementoARC(ciclo, arc, elemento);
        } catch (NegocioException e) {
            erro = e;
        }
    }

    public void salvarItemElementoInspetor(String idARC, String idCiclo, String idItemElemento,
            String matriculaServidor, String justificativa) {
        logar(USUARIO_DELIQ_SISLIQ110, matriculaServidor);
        Ciclo ciclo = cicloMediator.loadPK(Integer.valueOf(idCiclo));
        AvaliacaoRiscoControle arc = avaliacaoRiscoControleMediator.buscarPorPk(Integer.valueOf(idARC));
        ItemElemento item = itemElementoMediator.buscarPorPk(Integer.valueOf(idItemElemento));
        if (item.getDocumento() != null) {
            item.getDocumento().setJustificativa(justificativa);
        } else {
            Documento documento = new Documento();
            documento.setJustificativa(justificativa);
            item.setDocumento(documento);
        }
        salvarItemElemento(ciclo, arc, item);
    }

    private void salvarItemElemento(Ciclo ciclo, AvaliacaoRiscoControle arc, ItemElemento itemElemento) {
        erro = null;
        try {
            itemElementoMediator.salvarJustificativaItemElementoARC(ciclo, arc, itemElemento, false);
        } catch (NegocioException e) {
            erro = e;
        }
    }

    public List<AvaliacaoRiscoControle> buscaNotaARC(Long idARCNovaNota) {
        AvaliacaoRiscoControle arc = avaliacaoRiscoControleMediator.loadPK(idARCNovaNota.intValue());
        List<AvaliacaoRiscoControle> retorno = new ArrayList<AvaliacaoRiscoControle>();
        retorno.add(arc);
        return retorno;
    }

    public String getBuscaNotaARC(AvaliacaoRiscoControle arc) {
        return arc.getNotaArrastoInspetor();
    }

    public void salvarTendenciaInspetor(String idARC, String idCiclo, String idTendencia, String matriculaServidor,
            String idParamentroTendencia, String justificativa) {
        logar(USUARIO_DELIQ_SISLIQ110, matriculaServidor);
        Ciclo ciclo = cicloMediator.loadPK(Integer.valueOf(idCiclo));
        AvaliacaoRiscoControle arc = avaliacaoRiscoControleMediator.buscarPorPk(Integer.valueOf(idARC));
        TendenciaARC tendenciaARC = null;
        if (StringUtils.isNotBlank(idTendencia)) {
            tendenciaARC = tendenciaMediator.buscarPorPk(Integer.valueOf(idTendencia));
        }
        if (tendenciaARC == null) {
            tendenciaARC = new TendenciaARC();
            tendenciaARC.setAvaliacaoRiscoControle(arc);
            tendenciaARC.setPerfil(PerfisNotificacaoEnum.INSPETOR);
            arc.setTendenciasArc(new ArrayList<TendenciaARC>());
        }
        if (StringUtils.isNotBlank(idParamentroTendencia)) {
            tendenciaARC.setParametroTendencia(parametroTendenciaMediator.load(Integer.valueOf(idParamentroTendencia)));
        } else {
            tendenciaARC.setParametroTendencia(null);
        }
        tendenciaARC.setJustificativa(justificativa);
        salvarTendencia(ciclo, arc, tendenciaARC);
    }

    private void salvarTendencia(Ciclo ciclo, AvaliacaoRiscoControle arc, TendenciaARC tendenciaARC) {
        erro = null;
        try {
            tendenciaMediator.salvarTendenciaARC(ciclo, arc, tendenciaARC, PerfisNotificacaoEnum.INSPETOR);
        } catch (NegocioException e) {
            erro = e;
        }
    }

    public void concluirARC(String idARC, String idCiclo, String matriculaServidor) {
        logar(USUARIO_DELIQ_SISLIQ110, matriculaServidor);
        Ciclo ciclo = cicloMediator.loadPK(Integer.valueOf(idCiclo));
        AvaliacaoRiscoControle arc = avaliacaoRiscoControleMediator.buscarPorPk(Integer.valueOf(idARC));
        concluirARC(ciclo, arc);
    }

    private void concluirARC(Ciclo ciclo, AvaliacaoRiscoControle arc) {
        erro = null;
        try {
            avaliacaoRiscoControleMediator.concluirEdicaoARCInspetor(ciclo, arc.getPk());
        } catch (NegocioException e) {
            erro = e;
        }
    }
}
