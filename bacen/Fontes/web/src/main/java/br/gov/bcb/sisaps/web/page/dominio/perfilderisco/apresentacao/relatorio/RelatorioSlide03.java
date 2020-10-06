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
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.mediator.AtividadeCicloMediator;
import br.gov.bcb.sisaps.src.mediator.VersaoPerfilRiscoMediator;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;
import br.gov.bcb.sisaps.src.vo.AtividadeCicloVO;
import br.gov.bcb.sisaps.src.vo.ConsultaAtividadeCicloVO;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.util.validacao.InfraException;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.relatorio.vo.RelatorioSlide03VO;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.relatorio.vo.RelatorioSubAtividadeVO;

/**
 * Relatorio de Pendencias.
 */
public class RelatorioSlide03 extends RelatorioSlideAbstrato {

    /**
     * Título do relatório.
     */
    public static final String TITULO_RELATORIO = "Relatório Slide 20";

    /**
     * Nome do arquivo do relatório enviado ao usuário.
     */
    public static final String NOME_ARQUIVO_RELATORIO = "RelatorioSlide20";

    private static final String NOME_ARQUIVO_JASPER = "/relatorios/apresentacao/slide03.jasper";

    private List<RelatorioSlide03VO> dadosRelatorio;
    private PerfilRisco perfilRisco;
    private Ciclo ciclo;
    private String trabalhoAtual;
    private String trabalhoAnterior;

    public RelatorioSlide03(SecaoApresentacaoEnum secao, ApresentacaoVO apresentacaoVO, int qtdPaginas, int indice,
            PerfilRisco perfilRisco, Ciclo ciclo) {
        super(secao, apresentacaoVO, true, qtdPaginas, indice);
        this.perfilRisco = perfilRisco;
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

        dadosRelatorio = new ArrayList<RelatorioSlide03VO>();

        RelatorioSlide03VO relatorio = new RelatorioSlide03VO();

        // Declarações
        ConsultaAtividadeCicloVO consulta;

        // Consulta as atividades do ciclo.
        consulta = montarConsulta(perfilRisco, ciclo);

        // Adiciona o ano atual.
        trabalhoAtual = consulta.getAno().toString();

        relatorio.setAtual(inicilizando(AtividadeCicloMediator.get().consultar(consulta)));

        consulta.setAno((short) (consulta.getAno() - 1));
        relatorio.setAnterior(inicilizando(AtividadeCicloMediator.get().consultar(consulta)));

        trabalhoAnterior = consulta.getAno().toString();

        dadosRelatorio.add(relatorio);

    }

    List<RelatorioSubAtividadeVO> inicilizando(List<AtividadeCicloVO> atual) {
        ArrayList<RelatorioSubAtividadeVO> lista = new ArrayList<RelatorioSubAtividadeVO>();
        if (atual.isEmpty()) {
            RelatorioSubAtividadeVO atvidade = new RelatorioSubAtividadeVO();
            atvidade.setSituacao("");
            lista.add(atvidade);
        } else {
            for (AtividadeCicloVO ativi : atual) {
                RelatorioSubAtividadeVO atividade = new RelatorioSubAtividadeVO();
                atividade.setSituacao(ativi.getSituacao());
                atividade.setAno(ativi.getAno().toString());
                atividade.setDataBaseFormatada(ativi.getDataBaseFormatada());
                atividade.setDescricao(ativi.getDescricao());
                lista.add(atividade);
            }
        }

        return lista;

    }

    // Monta a consulta das atividades.
    private ConsultaAtividadeCicloVO montarConsulta(PerfilRisco perfilRisco, Ciclo ciclo) {
        // Declarações
        ConsultaAtividadeCicloVO consulta;

        // Inicializações
        consulta = new ConsultaAtividadeCicloVO();

        // Monta a consulta.
        consulta.setCnpjES(ciclo.getEntidadeSupervisionavel().getConglomeradoOuCnpj());
        consulta.setAno((short) DataUtil.getDateTimeAtual().getYear());
        consulta.setVersoesPerfilRisco(VersaoPerfilRiscoMediator.get().buscarVersoesPerfilRisco(perfilRisco.getPk(),
                TipoObjetoVersionadorEnum.ATIVIDADE_CICLO));

        return consulta;
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
                            "/relatorios/apresentacao/subSlide03.jasper"));
        } catch (JRException e) {
            throw new InfraException("Erro ao carregar o subrelatorio do slide 03.", e);
        }

        parametrosJasper.put("SUB_RELATORIO_TRABALHO", subRelatorio);

        parametrosJasper.put("trabalhoAtual", trabalhoAtual);
        parametrosJasper.put("trabalhoAnterior", trabalhoAnterior);

    }

}
