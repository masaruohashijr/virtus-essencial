package br.gov.bcb.sisaps.web.page.dominio.analisequantitativa;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.app.stuff.util.props.PropertyUtils;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.ContaQuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.DataBaseES;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoConta;
import br.gov.bcb.sisaps.src.mediator.analisequantitativa.QuadroPosicaoFinanceiraMediator;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.QuadroPosicaoFinanceiraVO;

public class PainelBaseAtivoPassivo extends Panel {
    private static final String DATA_BASE_ATIVO = "dataBaseAtivo";
    protected QuadroPosicaoFinanceiraVO vo;
    protected HiddenField<String> hiddenFieldScroll;
    protected Boolean informacoesNaoSalvas = Boolean.FALSE;
    protected LabelInformacoesNaoSalvas labelInformacoes;
    protected PropertyModel<String> modelDataBaseES;
    private boolean editable;
    private TipoConta tipoConta;
    private BuildTabelaConta tabelaConta;
    private List<ContaQuadroPosicaoFinanceira> contas;

    public PainelBaseAtivoPassivo(String id, QuadroPosicaoFinanceiraVO vo, TipoConta tipoConta, boolean editable) {
        super(id);
        setMarkupId(id);
        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);
        this.vo = vo;
        this.tipoConta = tipoConta;
        this.editable = editable;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        contas = new LinkedList<ContaQuadroPosicaoFinanceira>();
        if (editable) {
            contas.addAll(vo.getListaContasQuadroPorTipo(tipoConta));
        } else {
            contas.addAll(vo.getListaContasSelecionadasQuadroPorTipo(tipoConta));
        }
        tabelaConta = new BuildTabelaConta();
        tabelaConta.criarTabela(this, editable, contas, vo);
        if (editable) {
            BotaoSalvarInformacoes botaoSalvarInformacoes = new BotaoSalvarInformacoes() {
                @Override
                protected void executarSubmissao(AjaxRequestTarget target) {
                    QuadroPosicaoFinanceiraMediator.get().salvarContas(vo, tipoConta);
                    GerenciarQuadroPosicaoFinanceira gerenciarQuadroPosicaoFinanceira =
                            (GerenciarQuadroPosicaoFinanceira) getPage();
                    setInformacoesNaoSalvas(Boolean.FALSE);
                    gerenciarQuadroPosicaoFinanceira.lancarInfoAjax(" A seleção de contas de "
                            + tipoConta.getDescricao().toLowerCase() + " foi salva com sucesso.",
                            PainelBaseAtivoPassivo.this, target);
                }

                @Override
                public String getMarkupId() {
                    return getId() + tipoConta.getDescricao();
                }
            };
            botaoSalvarInformacoes.setMarkupId(botaoSalvarInformacoes.getId() + tipoConta.getDescricao());
            addOrReplace(botaoSalvarInformacoes);
        }
        setVisibilityAllowed(CollectionUtils.isNotEmpty(contas));
    }

   

    public HiddenField<String> getHiddenFieldScroll() {
        return hiddenFieldScroll;
    }

    public Boolean isInformacoesNaoSalvas() {
        return informacoesNaoSalvas;
    }

    public void setInformacoesNaoSalvas(Boolean informacoesNaoSalvas) {
        this.informacoesNaoSalvas = informacoesNaoSalvas;
    }

    public LabelInformacoesNaoSalvas getLabelInformacoes() {
        return labelInformacoes;
    }

    protected void buildDataBase() {
        PropertyModel<DataBaseES> model =
                new PropertyModel<DataBaseES>(vo, PropertyUtils.property(PropertyUtils.getPropertyObject(
                        QuadroPosicaoFinanceiraVO.class).getDataBaseES()));
        DataBaseES dataBaseES = new DataBaseES();
        if (model.getObject() != null) {
            dataBaseES = model.getObject();
        }
        modelDataBaseES = new PropertyModel<String>(dataBaseES, "dataBaseFormatada");
        addOrReplace(new Label(DATA_BASE_ATIVO, modelDataBaseES)).setMarkupId(DATA_BASE_ATIVO + getId());
    }

    public BuildTabelaConta getTabelaConta() {
        return tabelaConta;
    }

    public List<ContaQuadroPosicaoFinanceira> getContas() {
        return contas;
    }
}
