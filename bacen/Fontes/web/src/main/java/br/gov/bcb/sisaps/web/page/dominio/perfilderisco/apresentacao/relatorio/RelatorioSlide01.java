/*
 * Sistema: AUD
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */

package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.relatorio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Page;

import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoCicloEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;

/**
 * Relatorio de Pendencias.
 */
public class RelatorioSlide01 extends RelatorioSlideAbstrato {

    /**
     * Título do relatório.
     */
    public static final String TITULO_RELATORIO = "Relatório Slide 01";

    /**
     * Nome do arquivo do relatório enviado ao usuário.
     */
    public static final String NOME_ARQUIVO_RELATORIO = "RelatorioSlide01";

    private static final String NOME_ARQUIVO_JASPER = "/relatorios/apresentacao/slide01.jasper";

    private List<String> dadosRelatorio;
    private Ciclo ciclo;

    public RelatorioSlide01(SecaoApresentacaoEnum secao, ApresentacaoVO apresentacaoVO, int qtdPaginas, int indice,
            Ciclo ciclo) {
        super(secao, apresentacaoVO, true, qtdPaginas, indice);
        this.ciclo = ciclo;

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
        dadosRelatorio = new ArrayList<String>();
        dadosRelatorio.add("teste");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void adicionarParametros(Map<String, Object> parametrosJasper, Page paginaAtual) {
        // Declarações
        EntidadeSupervisionavel es;
        ServidorVO supervisor;
        String situacaoDescricao;

        // Inicializações
        es = ciclo.getEntidadeSupervisionavel();
        Date dataBase = null;
        if (!ciclo.getEstadoCiclo().getEstado().equals(EstadoCicloEnum.EM_ANDAMENTO)) {
            dataBase = ciclo.getDataPrevisaoCorec();
        }
        supervisor = CicloMediator.get().buscarChefeAtual(es.getLocalizacao(), dataBase);
        if (apresentacaoVO.getSituacaoNome() != null) {
            situacaoDescricao = apresentacaoVO.getSituacaoNome();
        } else {
            situacaoDescricao = "";
        }

        parametrosJasper.put("supervisor", "Supervisor: " + supervisor.getNome());
        parametrosJasper.put("dataInicioCiclo",
                "Data do início do ciclo: " + SisapsUtil.getDataFormatadaComBarras(ciclo.getDataInicio()));
        parametrosJasper.put("dataApresentacaoCorec",
                "Data da apresentação/Corec: " + SisapsUtil.getDataFormatadaComBarras(ciclo.getDataPrevisaoCorec()));
        parametrosJasper.put("prioridade", "Prioridade: " + es.getPrioridade().getDescricao());
        parametrosJasper.put("situacao", "Situação: " + situacaoDescricao);

    }

}
