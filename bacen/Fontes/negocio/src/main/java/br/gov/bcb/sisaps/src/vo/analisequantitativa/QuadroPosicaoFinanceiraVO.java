package br.gov.bcb.sisaps.src.vo.analisequantitativa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.ContaQuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.DataBaseES;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.LayoutOutraInformacaoAnaliseQuantitativa;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.OutraInformacaoQuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.QuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.ResultadoQuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoConta;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoInformacaoEnum;
import br.gov.bcb.sisaps.src.mediator.analisequantitativa.ContaQuadroPosicaoFinanceiraMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativa.DataBaseESMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativa.LayoutOutraInformacaoAnaliseQuantitativaMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativa.OutraInformacaoQuadroPosicaoFinanceiraMediator;
import br.gov.bcb.sisaps.src.vo.ObjetoPersistenteVO;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;

public class QuadroPosicaoFinanceiraVO extends ObjetoPersistenteVO implements Serializable {
    public static final String PR_NIVEL_2 = "PR nível 2";
    public static final String CAPITAL_COMPLEMENTAR = "Capital Complementar";
    public static final String CAPITAL_PRINCIPAL = "Capital Principal";
    public static final String PR_NIVEL_1 = "PR nível 1";
    public static final String INDICE_DE_BASILEIA_AMPLO_INCLUI_RBAN = "Basiléia Amplo (inclui RBAN)";
    public static final String INDICE_DE_IMOBILIZACAO = "Imobilização";
    public static final String INDICE_DE_BASILEIA = "Basiléia";
    
    private DataBaseES dataBaseES;
    
    private List<OutraInformacaoVO> patrimoniosNovo = new LinkedList<OutraInformacaoVO>();
    private List<OutraInformacaoVO> indicesNovo = new LinkedList<OutraInformacaoVO>();
    private List<String> nomesResultados = new LinkedList<String>();
    private Map<String, List<OutraInformacaoVO>> resultadosPorNome = new HashMap<String, List<OutraInformacaoVO>>();
    
    private List<PatrimonioVO> patrimonios = new LinkedList<PatrimonioVO>();
    private List<IndiceVO> indices = new LinkedList<IndiceVO>();
    private List<ResultadoQuadroPosicaoFinanceira> resultados = new LinkedList<ResultadoQuadroPosicaoFinanceira>();
    private List<ContaQuadroPosicaoFinanceira> listaContasQuadro = new LinkedList<ContaQuadroPosicaoFinanceira>();
    private boolean ajustadoAtivoDiferentePassivo;
    private List<ContaQuadroPosicaoFinanceira> listaContasExcluidas = new LinkedList<ContaQuadroPosicaoFinanceira>();

    public QuadroPosicaoFinanceiraVO() {
        //Contrutor padrão
    }

    public QuadroPosicaoFinanceiraVO(PerfilRisco perfilRisco) {
        this.dataBaseES = DataBaseESMediator.get().obterDataBaseESRecente(perfilRisco);
    }

    public QuadroPosicaoFinanceiraVO(DataBaseES dataBaseES, QuadroPosicaoFinanceira quadro, PerfilRisco perfilRisco) {
        this.setDataBaseES(dataBaseES);
        if (quadro != null) {
            this.setPk(quadro.getPk());
            preencherPorTipo(quadro, TipoInformacaoEnum.PATRIMONIO);
            preencherPorTipo(quadro, TipoInformacaoEnum.INDICE);
            preencherResultadosNovo(quadro);
            preencherContas(quadro);
            verificarAjustadoAtivoDiferentePassivo();
        }
    }
    
    private void preencherPorTipo(QuadroPosicaoFinanceira quadro, TipoInformacaoEnum tipoInformacaoEnum) {
        List<OutraInformacaoQuadroPosicaoFinanceira> outrasInfo = 
                OutraInformacaoQuadroPosicaoFinanceiraMediator.get().buscarOutraInformacaoQuadroPorTipo(
                        quadro.getPk(), tipoInformacaoEnum);
        for (OutraInformacaoQuadroPosicaoFinanceira outraInfo : outrasInfo) {
            LayoutOutraInformacaoAnaliseQuantitativa layout = 
                    outraInfo.getLayoutOutraInformacaoAnaliseQuantitativa();
            if (outraInfo.getValor() != null || layout.getExibirNulo().booleanValue()) {
                OutraInformacaoVO outraInformacaoVO = new OutraInformacaoVO(outraInfo.getPk(), outraInfo.getPeriodo(),
                        tipoInformacaoEnum, layout.getTipoEdicaoInformacaoEnum(), layout.getExibirNulo(), 
                        layout.getOutraInformacaoAnaliseQuantitativa().getDescricao(), 
                        outraInfo.getValor(), outraInfo.getValorEditado(), 
                        layout.getNumeroCasasInteiras(), layout.getNumeroCasasDecimais());
                switch (tipoInformacaoEnum) {
                    case PATRIMONIO:
                        patrimoniosNovo.add(outraInformacaoVO);
                        break;
                    case INDICE:
                        indicesNovo.add(outraInformacaoVO);
                        break;
                    default:
                        break;
                }
            }
        }
    }
    
    private void preencherResultadosNovo(QuadroPosicaoFinanceira quadro) {
        List<LayoutOutraInformacaoAnaliseQuantitativa> layouts = 
                LayoutOutraInformacaoAnaliseQuantitativaMediator.get().obterLayoutsOutraInformacaoQuadro(
                        quadro, TipoInformacaoEnum.RESULTADO);
        for (LayoutOutraInformacaoAnaliseQuantitativa layout : layouts) {
            nomesResultados.add(layout.getOutraInformacaoAnaliseQuantitativa().getDescricao());
            List<OutraInformacaoQuadroPosicaoFinanceira> resultados = 
                    OutraInformacaoQuadroPosicaoFinanceiraMediator.get().buscarPorQuadroELayout(quadro, layout, true);
            List<OutraInformacaoVO> resultadosVO = new ArrayList<OutraInformacaoVO>();
            for (OutraInformacaoQuadroPosicaoFinanceira outraInfo : resultados) {
                resultadosVO.add(new OutraInformacaoVO(outraInfo.getPk(), outraInfo.getPeriodo(),
                    TipoInformacaoEnum.RESULTADO, layout.getTipoEdicaoInformacaoEnum(), layout.getExibirNulo(), 
                    layout.getOutraInformacaoAnaliseQuantitativa().getDescricao(), 
                    outraInfo.getValor(), outraInfo.getValorEditado(),
                    layout.getNumeroCasasInteiras(), layout.getNumeroCasasDecimais()));
            }
            resultadosPorNome.put(layout.getOutraInformacaoAnaliseQuantitativa().getDescricao(), resultadosVO);
        }
    }

    public DataBaseES getDataBaseES() {
        return dataBaseES;
    }

    public void setDataBaseES(DataBaseES dataBaseES) {
        this.dataBaseES = dataBaseES;
    }

    private void preencherContas(QuadroPosicaoFinanceira quadro) {
        this.listaContasQuadro = ContaQuadroPosicaoFinanceiraMediator.get().obterContasNovo(quadro);
    }

    public List<PatrimonioVO> getPatrimonios() {
        return patrimonios;
    }

    public void setPatrimonios(List<PatrimonioVO> patrimonios) {
        this.patrimonios = patrimonios;
    }

    public List<IndiceVO> getIndices() {
        return indices;
    }

    public void setIndices(List<IndiceVO> indices) {
        this.indices = indices;
    }

    public List<ResultadoQuadroPosicaoFinanceira> getResultados() {
        return resultados;
    }

    public void setResultados(List<ResultadoQuadroPosicaoFinanceira> resultados) {
        this.resultados = resultados;
    }

    public List<ContaQuadroPosicaoFinanceira> getListaContasQuadro() {
        return listaContasQuadro;
    }

    public void setListaContasQuadro(List<ContaQuadroPosicaoFinanceira> listaContasQuadro) {
        this.listaContasQuadro = listaContasQuadro;
    }

    public List<ContaQuadroPosicaoFinanceira> getListaContasQuadroPorTipo(TipoConta tipoConta) {
        List<ContaQuadroPosicaoFinanceira> lista = new LinkedList<ContaQuadroPosicaoFinanceira>();
        for (ContaQuadroPosicaoFinanceira conta : getListaContasQuadro()) {
            if (tipoConta
                    .equals(conta.getLayoutContaAnaliseQuantitativa().getContaAnaliseQuantitativa().getTipoConta())) {
                lista.add(conta);
            }
        }
        return lista;
    }

    public List<ContaQuadroPosicaoFinanceira> getListaContasSelecionadasQuadroPorTipo(TipoConta tipoConta) {
        List<ContaQuadroPosicaoFinanceira> contasFiltradas =
                new LinkedList<ContaQuadroPosicaoFinanceira>(getListaContasQuadroPorTipo(tipoConta));
        for (ContaQuadroPosicaoFinanceira conta : new LinkedList<ContaQuadroPosicaoFinanceira>(contasFiltradas)) {
            if (SimNaoEnum.NAO.equals(conta.getLayoutContaAnaliseQuantitativa().getObrigatorio())
                    && conta.getAjustado() == null
                    && (conta.getExibir() == null || SimNaoEnum.NAO.equals(conta.getExibir()))) {
                    contasFiltradas.remove(conta);
                }
            if (conta.isLinhaEmBranco()) {
                contasFiltradas.remove(conta);
            }
        }
        return contasFiltradas;
    }

    public boolean isAjustadoAtivoDiferentePassivo() {
        return ajustadoAtivoDiferentePassivo;
    }

    public void setAjustadoAtivoDiferentePassivo(boolean ajustadoAtivoDiferentePassivo) {
        this.ajustadoAtivoDiferentePassivo = ajustadoAtivoDiferentePassivo;
    }

    public void verificarAjustadoAtivoDiferentePassivo() {
        Integer ajustadoPassivo = 0;
        Integer ajustadoAtivo = 0;
        for (ContaQuadroPosicaoFinanceira contaPassiva : getListaContasSelecionadasQuadroPorTipo(TipoConta.PASSIVO)) {
            if (contaPassiva.isContaRaiz()) {
                ajustadoPassivo =
                        contaPassiva.getAjustado() == null ? contaPassiva.getValor() : contaPassiva.getAjustado();
                break;
            }
        }

        for (ContaQuadroPosicaoFinanceira contaAtiva : getListaContasSelecionadasQuadroPorTipo(TipoConta.ATIVO)) {
            if (contaAtiva.isContaRaiz()) {
                ajustadoAtivo = contaAtiva.getAjustado() == null ? contaAtiva.getValor() : contaAtiva.getAjustado();
                break;
            }
        }

        if (ajustadoPassivo != null && ajustadoAtivo != null) {
            ajustadoAtivoDiferentePassivo = !ajustadoPassivo.equals(ajustadoAtivo);
        }
    }

    public List<ContaQuadroPosicaoFinanceira> getListaContasExcluidas() {
        return listaContasExcluidas;
    }

    public void setListaContasExcluidas(List<ContaQuadroPosicaoFinanceira> listaContasExcluidas) {
        this.listaContasExcluidas = listaContasExcluidas;
    }

    public List<OutraInformacaoVO> getPatrimoniosNovo() {
        return patrimoniosNovo;
    }

    public void setPatrimoniosNovo(List<OutraInformacaoVO> patrimoniosNovo) {
        this.patrimoniosNovo = patrimoniosNovo;
    }

    public List<OutraInformacaoVO> getIndicesNovo() {
        return indicesNovo;
    }

    public void setIndicesNovo(List<OutraInformacaoVO> indicesNovo) {
        this.indicesNovo = indicesNovo;
    }

    public List<String> getNomesResultados() {
        return nomesResultados;
    }

    public void setNomesResultados(List<String> nomesResultados) {
        this.nomesResultados = nomesResultados;
    }

    public Map<String, List<OutraInformacaoVO>> getResultadosPorNome() {
        return resultadosPorNome;
    }

    public void setResultadosPorNome(Map<String, List<OutraInformacaoVO>> resultadosPorNome) {
        this.resultadosPorNome = resultadosPorNome;
    }
    
    public boolean existeValorEditado(TipoInformacaoEnum tipoInformacaoEnum, String nomeResultado) {
        boolean temValorEditado = false;
        List<OutraInformacaoVO> informacoesQuadro = new ArrayList<OutraInformacaoVO>();
        switch (tipoInformacaoEnum) {
            case PATRIMONIO:
                informacoesQuadro = patrimoniosNovo;
                break;
            case INDICE:
                informacoesQuadro = indicesNovo;
                break;
            case RESULTADO:
                informacoesQuadro = getResultadosPorNome().get(nomeResultado);
                break;
            default:
                break;
        }
        
        for (OutraInformacaoVO info : informacoesQuadro) {
            if (info.getValorEditado() != null) {
                temValorEditado = true;
                break;
            }
        }
        return temValorEditado;
    }

}
