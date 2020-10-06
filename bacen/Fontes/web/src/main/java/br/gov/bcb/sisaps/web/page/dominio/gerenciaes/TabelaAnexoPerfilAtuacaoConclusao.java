package br.gov.bcb.sisaps.web.page.dominio.gerenciaes;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistente;
import br.gov.bcb.sisaps.src.dominio.AnexoDocumento;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ConclusaoES;
import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.PerfilAtuacaoES;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEntidadeAnexoDocumentoEnum;
import br.gov.bcb.sisaps.src.mediator.AnexoDocumentoMediator;
import br.gov.bcb.sisaps.src.mediator.ConclusaoESMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilAtuacaoESMediator;
import br.gov.bcb.sisaps.src.vo.AnexoDocumentoVo;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import br.gov.bcb.sisaps.web.page.dominio.avaliacaoRiscoControle.edicao.TabelaAnexoComum;

public class TabelaAnexoPerfilAtuacaoConclusao extends TabelaAnexoComum<AnexoDocumentoVo> {

    @SpringBean
    private PerfilAtuacaoESMediator perfilAtuacaoESMediator;
    @SpringBean
    private ConclusaoESMediator conclusaoESMediator;

    private PerfilAtuacaoES perfilAtuacaoES;
    private ConclusaoES conclusaoES;
    private Ciclo ciclo;
    private final boolean isPerfilAtuacao;
    private final PainelPerfilAtuacaoConclusao painelPerfilAtuacaoConclusao;

    public TabelaAnexoPerfilAtuacaoConclusao(String id, ObjetoPersistente<Integer> entidadeAnexoDocumento,
            final Documento documento, Ciclo ciclo, final boolean isEdicao, boolean isPerfilAtuacao,
            PainelPerfilAtuacaoConclusao painelPerfilAtuacaoConclusao) {
        super(id, new AbstractReadOnlyModel<List<AnexoDocumentoVo>>() {
            @Override
            public List<AnexoDocumentoVo> getObject() {
                List<AnexoDocumento> anexosItem = AnexoDocumentoMediator.get().buscar(documento);
                return AnexoDocumentoVo.converterParaListaVo(anexosItem);
            }
        }, isEdicao, null, isPerfilAtuacao ? "perfilAtuacaoAnexoDocumento" : "conclusaoAnexoDocumento", true);
        this.ciclo = ciclo;
        this.isPerfilAtuacao = isPerfilAtuacao;
        this.painelPerfilAtuacaoConclusao = painelPerfilAtuacaoConclusao;
        if (entidadeAnexoDocumento != null) {
            if (isPerfilAtuacao) {
                this.perfilAtuacaoES = (PerfilAtuacaoES) entidadeAnexoDocumento;
            } else {
                this.conclusaoES = (ConclusaoES) entidadeAnexoDocumento;
            }
        }
    }

    @Override
    protected void excluirAnexo(AjaxRequestTarget target, IModel<AnexoDocumentoVo> model) {
        if (isPerfilAtuacao) {
            perfilAtuacaoESMediator.excluirAnexo(model.getObject(), perfilAtuacaoES, ciclo, true);
        } else {
            conclusaoESMediator.excluirAnexo(model.getObject(), conclusaoES, ciclo, true);
        }
        target.add(painelPerfilAtuacaoConclusao);
    }

    @Override
    protected void onDownLoad(AnexoDocumentoVo vo) {
        String link = vo.getLink();
        try {
            byte[] arquivoBytes;
            if (isPerfilAtuacao) {

                arquivoBytes =
                        AnexoDocumentoMediator.get().recuperarArquivo(
                                link,
                                vo.getPk() != null && perfilAtuacaoES.getPk() == null ? perfilAtuacaoES
                                        .getPerfilAtuacaoESAnterior() : perfilAtuacaoES,
                                TipoEntidadeAnexoDocumentoEnum.PERFIL_ATUACAO, ciclo);

            } else {
                arquivoBytes =
                        AnexoDocumentoMediator.get().recuperarArquivo(
                                link,
                                vo.getPk() != null && conclusaoES.getPk() == null ? conclusaoES
                                        .getConclusaoESAnterior() : conclusaoES,
                                TipoEntidadeAnexoDocumentoEnum.CONCLUSAO, ciclo);
            }
            executarDowload(link, arquivoBytes);
        } catch (NegocioException e) {
            error("Arquivo '" + link + "' não foi encontrado.");
        }
    }

}
