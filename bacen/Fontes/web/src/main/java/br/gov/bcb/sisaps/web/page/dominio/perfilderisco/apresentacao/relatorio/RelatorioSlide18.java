/*
 * Sistema: AUD
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software � confidencial e propriedade do Banco Central do Brasil.
 * N�o � permitida sua distribui��o ou divulga��o do seu conte�do sem
 * expressa autoriza��o do Banco Central.
 * Este arquivo cont�m informa��es propriet�rias.
 */

package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.relatorio;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoLinhaAtividadeVOEnum;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;
import br.gov.bcb.sisaps.src.vo.LinhaAtividadeVO;
import br.gov.bcb.sisaps.util.validacao.InfraException;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.relatorio.vo.RelatorioSlide18VO;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.relatorio.vo.RelatorioSub18VO;

/**
 * Relatorio de Pendencias.
 */
public class RelatorioSlide18 extends RelatorioSlideAbstrato {

    /**
     * T�tulo do relat�rio.
     */
    public static final String TITULO_RELATORIO = "Relat�rio Slide 18";

    /**
     * Nome do arquivo do relat�rio enviado ao usu�rio.
     */
    public static final String NOME_ARQUIVO_RELATORIO = "RelatorioSlide18";
    private static final String CORPORATIVA2 = "Corporativa";
    private static final String FECHAR = ")";
    private static final String BLOCO_DE_NEGOCIOS = "Bloco de Neg�cios (";

    private static final String BLOCO_CORPORATIVO = "Bloco Corporativo (";

    private static final String NOME_ARQUIVO_JASPER = "/relatorios/apresentacao/slide18.jasper";

    private List<RelatorioSlide18VO> dadosRelatorio;
    private String blocoNegocioAnterior;
    private String blocoCorporativoAnterior;

    public RelatorioSlide18(SecaoApresentacaoEnum secao, ApresentacaoVO apresentacaoVO, int qtdPaginas, int indice) {
        super(secao, apresentacaoVO, true, qtdPaginas, indice);
        converterDados();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<?> getDadosRelatorio() {
        return dadosRelatorio;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNomeArquivoJasper() {
        return NOME_ARQUIVO_JASPER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTituloRelatorio() {
        return TITULO_RELATORIO;
    }

    private void converterDados() {

        dadosRelatorio = new ArrayList<RelatorioSlide18VO>();

        RelatorioSlide18VO relatorio = new RelatorioSlide18VO();

        relatorio.setListaNegocioAtual(inicilizando(getAtividades(apresentacaoVO.getDadosCicloVO().getAtividades(),
                false)));

        relatorio.setListaCorporativoAtual(inicilizando(getAtividades(apresentacaoVO.getDadosCicloVO().getAtividades(),
                true)));

        if (apresentacaoVO.getDadosCicloVOAnteriorVO() != null) {
            relatorio.setListaNegocioAnterior(inicilizando(getAtividades(apresentacaoVO.getDadosCicloVOAnteriorVO()
                    .getAtividades(), false)));

            relatorio.setListaCorporativoAnterior(inicilizando(getAtividades(apresentacaoVO.getDadosCicloVOAnteriorVO()
                    .getAtividades(), true)));
            relatorio.setPossuiAnterior(true);

            blocoCorporativoAnterior =
                    BLOCO_CORPORATIVO + apresentacaoVO.getDadosCicloVOAnteriorVO().getPercentualCorporativo() + FECHAR;
            blocoNegocioAnterior =
                    BLOCO_DE_NEGOCIOS + apresentacaoVO.getDadosCicloVOAnteriorVO().getPercentualNegocio() + FECHAR;
        }

        dadosRelatorio.add(relatorio);

    }

    List<RelatorioSub18VO> inicilizando(List<LinhaAtividadeVO> teste) {
        ArrayList<RelatorioSub18VO> lista = new ArrayList<RelatorioSub18VO>();

        for (LinhaAtividadeVO linha : teste) {
            RelatorioSub18VO sub = new RelatorioSub18VO();
            sub.setFilho(linha.isFilho());
            sub.setNome(linha.getNome());
            sub.setPeso(linha.getParametroPeso().getDescricao());
            lista.add(sub);
        }

        return lista;

    }

    // Extrai as atividades de
    private List<LinhaAtividadeVO> getAtividades(List<LinhaAtividadeVO> atividadesVO, boolean corporativa) {
        // Declara��es
        List<LinhaAtividadeVO> atividadesVOSelecionadas;

        // Inicializa��es
        atividadesVOSelecionadas = new LinkedList<LinhaAtividadeVO>();

        // Analisa as atividades.
        for (LinhaAtividadeVO linhaAtividadeVO : atividadesVO) {
            // Ignora ARCs.
            if (linhaAtividadeVO.getTipo() == TipoLinhaAtividadeVOEnum.ARC) {
                continue;
            }

            // Filtrar as corporativas?
            if (corporativa) {
                // Verifica se � uma atividade corporativa.
                if (linhaAtividadeVO.getNomeParametroTipoAtividade() == CORPORATIVA2) {
                    // Adiciona � lista.
                    atividadesVOSelecionadas.add(linhaAtividadeVO);
                }
            } else {
                // Verifica se � uma atividade de neg�cio.
                if (linhaAtividadeVO.getNomeParametroTipoAtividade() != CORPORATIVA2) {
                    // Adiciona � lista.
                    atividadesVOSelecionadas.add(linhaAtividadeVO);
                }
            }
        }

        return atividadesVOSelecionadas;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void adicionarParametros(Map<String, Object> parametrosJasper, Page paginaAtual) {
        WebApplication wa = (WebApplication) paginaAtual.getApplication();
        JasperReport subRelatorio = null;

        try {
            subRelatorio =
                    (JasperReport) JRLoader.loadObject(wa.getServletContext().getResourceAsStream(
                            "/relatorios/apresentacao/subSlide18.jasper"));
        } catch (JRException e) {
            throw new InfraException("Erro ao carregar o subrelatorio do slide 03.", e);
        }

        parametrosJasper.put("SUB_RELATORIO_TRABALHO", subRelatorio);

        parametrosJasper.put("blocoCorporativoAtual", BLOCO_CORPORATIVO
                + apresentacaoVO.getDadosCicloVO().getPercentualCorporativo() + FECHAR);
        parametrosJasper.put("blocoNegocioAtual", BLOCO_DE_NEGOCIOS
                + apresentacaoVO.getDadosCicloVO().getPercentualNegocio() + FECHAR);

        parametrosJasper.put("blocoNegocioAnterior", blocoNegocioAnterior);

        parametrosJasper.put("blocoCorporativoAnterior", blocoCorporativoAnterior);

    }

}
