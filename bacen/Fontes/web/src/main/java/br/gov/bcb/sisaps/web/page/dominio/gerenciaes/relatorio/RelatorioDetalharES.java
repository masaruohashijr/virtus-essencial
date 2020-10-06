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

package br.gov.bcb.sisaps.web.page.dominio.gerenciaes.relatorio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistente;
import br.gov.bcb.sisaps.src.dominio.AnexoDocumento;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ConclusaoES;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.PerfilAtuacaoES;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.PerspectivaES;
import br.gov.bcb.sisaps.src.dominio.SituacaoES;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEntidadeAnexoDocumentoEnum;
import br.gov.bcb.sisaps.src.mediator.AnexoDocumentoMediator;
import br.gov.bcb.sisaps.src.mediator.ConclusaoESMediator;
import br.gov.bcb.sisaps.src.mediator.GrauPreocupacaoESMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilAtuacaoESMediator;
import br.gov.bcb.sisaps.src.mediator.PerspectivaESMediator;
import br.gov.bcb.sisaps.src.mediator.SituacaoESMediator;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.web.page.DefaultPage;
import br.gov.bcb.sisaps.web.page.componentes.util.RelatorioAbstrato;
import br.gov.bcb.sisaps.web.page.dominio.perfilderisco.PerfilDeRiscoPageResumido;
import br.gov.bcb.wicket.pages.AbstractBacenWebPage;

/**
 * Relatorio de Pendencias.
 */
public class RelatorioDetalharES extends RelatorioAbstrato {

    /**
     * Título do relatório.
     */
    public static final String TITULO_RELATORIO = "Relatório detalhes ES";

    /**
     * Nome do arquivo do relatório enviado ao usuário.
     */
    public static final String NOME_ARQUIVO_RELATORIO = "RelatorioImpressaoDetalharES";

    private static final String NOME_ARQUIVO_JASPER = "/relatorios/RelatorioImpressaoDetalharES.jasper";
    private static final String NOME_ARQUIVO_JASPER_RESUMO = "/relatorios/RelatorioImpressaoDetalharESResumo.jasper";
    private static final String TRACO = " - ";
    private static final String NOME_CHAVE_CONCLUSAO = "nomeChaveConclusao";

    private static final String NOME_CHAVE_ATUACAO = "nomeChaveAtuacao";

    private Map<String, List<byte[]>> mapAnexo = new HashMap<String, List<byte[]>>();
    private final Ciclo ciclo;

    private final EntidadeSupervisionavel entidadeSupervisionavel;

    private final PerfilAtuacaoES perfilAtuacaoES;
    private final ConclusaoES conclusaoES;
    private final PerspectivaES perspectivaES;
    private final SituacaoES situacaoES;
    private final PerfilRisco perfilRisco;
    private final String grauPreocupacao;
    private final String justificativaGrauPreocupacao;
    private final String descricaoPerspectiva;
    private final DefaultPage page;

    public RelatorioDetalharES(Ciclo ciclo, PerfilRisco perfilRisco, PerfilAcessoEnum perfilMenu, DefaultPage page) {
        this.entidadeSupervisionavel = ciclo.getEntidadeSupervisionavel();
        this.ciclo = ciclo;
        this.perfilRisco = perfilRisco;
        this.page = page;
        this.grauPreocupacao = GrauPreocupacaoESMediator.get().getNotaFinal(perfilRisco, perfilMenu, perfilRisco.getCiclo().getMetodologia());
        this.justificativaGrauPreocupacao = GrauPreocupacaoESMediator.get().justificativaGrauPreocupacao(perfilRisco, perfilMenu);
        this.perfilAtuacaoES = PerfilAtuacaoESMediator.get().buscarPorPerfilRisco(perfilRisco.getPk());
        this.conclusaoES = ConclusaoESMediator.get().buscarPorPerfilRisco(perfilRisco.getPk());
        this.perspectivaES = PerspectivaESMediator.get().getUltimaPerspectiva(perfilRisco, false, perfilMenu);
        this.situacaoES = SituacaoESMediator.get().buscarPorPerfilRisco(perfilRisco.getPk());
        this.descricaoPerspectiva =
                PerspectivaESMediator.get().getUltimaPerspectivaDescricao(perfilRisco, false, perfilMenu);
        montarAnexoPerfiAtuacao();
        montarAnexoConclusao();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<?> getDadosRelatorio() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNomeArquivoJasper() {
        if (page instanceof PerfilDeRiscoPageResumido) {
            return NOME_ARQUIVO_JASPER_RESUMO;
        }
        return NOME_ARQUIVO_JASPER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTituloRelatorio() {
        return TITULO_RELATORIO;
    }

    private void montarAnexoPerfiAtuacao() {
        if (perfilAtuacaoES != null && perfilAtuacaoES.getDocumento() != null
                && !Util.isNuloOuVazio(perfilAtuacaoES.getDocumento().getAnexosItemElemento())) {
            addMapAnexo(NOME_CHAVE_ATUACAO, TipoEntidadeAnexoDocumentoEnum.PERFIL_ATUACAO, perfilAtuacaoES,
                    perfilAtuacaoES.getDocumento().getAnexosItemElemento());
        }
    }

    private void montarAnexoConclusao() {
        if (conclusaoES != null && conclusaoES.getDocumento() != null
                && !Util.isNuloOuVazio(conclusaoES.getDocumento().getAnexosItemElemento())) {
            addMapAnexo(NOME_CHAVE_CONCLUSAO, TipoEntidadeAnexoDocumentoEnum.CONCLUSAO, conclusaoES, conclusaoES
                    .getDocumento().getAnexosItemElemento());
        }
    }

    private void addMapAnexo(String chave, TipoEntidadeAnexoDocumentoEnum tipo, ObjetoPersistente<Integer> objeto,
            List<AnexoDocumento> anexos) {
        List<byte[]> conclusao = new ArrayList<byte[]>();
        for (AnexoDocumento anexo : anexos) {
            conclusao.add(AnexoDocumentoMediator.get().recuperarArquivo(anexo.getLink(), objeto, tipo, ciclo));
        }
        mapAnexo.put(chave, conclusao);
    }

    private String montarRodape() {
        StringBuffer rodape = new StringBuffer();
        rodape.append("Versão ");
        rodape.append(perfilRisco.getDataCriacaoFormatadaSemHora());
        rodape.append(TRACO);
        rodape.append("Ciclo ");
        rodape.append(ciclo.getDataInicioFormatada());
        rodape.append(" ~ ");
        rodape.append(ciclo.getDataPrevisaoFormatada());
        return rodape.toString();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void adicionarParametros(Map<String, Object> parametrosJasper, AbstractBacenWebPage paginaAtual) {
        

        parametrosJasper.put("rodape", montarRodape());
        parametrosJasper.put("nomeConglomerado", entidadeSupervisionavel.getNomeConglomeradoFormatado());
        parametrosJasper.put("notaPrioridade", entidadeSupervisionavel.getPrioridade().getDescricao());
        parametrosJasper.put("nomeSecao", GrauPreocupacaoESMediator.get().isNotaFinal(
        		GrauPreocupacaoESMediator.get().buscarPorPerfilRisco(perfilRisco.getPk())) ? "2. Nota final" : "2. Grau de preocupação");
        parametrosJasper.put("notaGrauPreocupacao", grauPreocupacao);
        parametrosJasper.put("justificativaGrauPreocupacao", justificativaGrauPreocupacao);
        parametrosJasper.put("notaPorte", entidadeSupervisionavel.getPorte());
        parametrosJasper.put("notaSegmento", entidadeSupervisionavel.getSegmento());
        //Dados perfil atuacao
        addDadosPerfilAtuacao(parametrosJasper);
        //Dados Conclusao
        addDadosConclusao(parametrosJasper);
        //Dados persperctiva
        parametrosJasper.put("notaPerspectiva", descricaoPerspectiva);
        parametrosJasper.put("justificativaPerspectiva",
                perspectivaES == null ? "" : normalizaTexto(perspectivaES.getDescricao()));
        //Dados Situacao
        parametrosJasper.put("notaSituacao", situacaoES == null ? "" : situacaoES.getParametroSituacao() == null ? ""
                : situacaoES.getParametroSituacao().getNome());
        parametrosJasper.put("justificativaSituacao",
                situacaoES == null ? "" : normalizaTexto(situacaoES.getDescricao()));

    }

    private void addDadosConclusao(Map<String, Object> parametrosJasper) {
        parametrosJasper.put("justificativaConclusao",
                normalizaTexto(conclusaoES == null ? "" : conclusaoES.getDocumento() == null ? "" : conclusaoES
                        .getDocumento().getJustificativaDetalhe()));
        parametrosJasper.put(NOME_CHAVE_CONCLUSAO, NOME_CHAVE_CONCLUSAO);

        parametrosJasper.put(
                "possuiAnexoConclusao",
                conclusaoES == null ? false : conclusaoES.getDocumento() == null ? false : !Util
                        .isNuloOuVazio(conclusaoES.getDocumento().getAnexosItemElemento()));

    }

    private void addDadosPerfilAtuacao(Map<String, Object> parametrosJasper) {
        parametrosJasper.put("justificativaPerfilAtuacao", normalizaTexto(perfilAtuacaoES == null ? ""
                : perfilAtuacaoES.getDocumento() == null ? "" : perfilAtuacaoES.getDocumento()
                        .getJustificativaDetalhe()));
        parametrosJasper.put(NOME_CHAVE_ATUACAO, NOME_CHAVE_ATUACAO);

        parametrosJasper.put(
                "possuiAnexoAtuacao",
                perfilAtuacaoES == null ? false : perfilAtuacaoES.getDocumento() == null ? false : !Util
                        .isNuloOuVazio(perfilAtuacaoES.getDocumento().getAnexosItemElemento()));
    }

    public Map<String, List<byte[]>> getMapAnexo() {
        return mapAnexo;
    }

    public void setMapAnexo(Map<String, List<byte[]>> mapAnexo) {
        this.mapAnexo = mapAnexo;
    }

}
