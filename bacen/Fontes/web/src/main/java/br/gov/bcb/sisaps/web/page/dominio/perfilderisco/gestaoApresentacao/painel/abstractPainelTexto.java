package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.gestaoApresentacao.painel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.CampoApresentacaoEnum;
import br.gov.bcb.sisaps.src.mediator.TextoApresentacaoMediator;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;
import br.gov.bcb.sisaps.src.vo.TextoApresentacaoVO;
import br.gov.bcb.sisaps.web.page.componentes.botoes.AjaxSubmitLinkSisAps;
import br.gov.bcb.sisaps.web.page.componentes.textarea.CKEditorTextArea;
import br.gov.bcb.sisaps.web.page.componentes.util.CKEditorUtils;

public abstract class abstractPainelTexto extends PainelCampoApresentacao {
    

    private static final String FECHA_PARAGRAFO = "</p>";

    private static final String ABRE_PARAGRAFO = "<p>";

    // IDs dos componentes.
    private static final String ID_TEXTO = "idTexto", ID_ALERTA_DADOS_NAO_SALVOS = "idAlertaDadosNaoSalvos",
            ID_BOTAO_SALVAR_INFORMACOES = "bttSalvarInformacoes";

    // Texto em edição.
    private String texto;

    public abstractPainelTexto(String id, ApresentacaoVO apresentacaoVO, CampoApresentacaoEnum campo) {
        super(id, apresentacaoVO, campo);
        // Monta os componentes.
        montarComponentes(apresentacaoVO);
    }

    // Monta os componentes do painel.
    private void montarComponentes(final ApresentacaoVO apresentacaoVO) {
        // Declarações
        TextoApresentacaoVO textoApresentacaoVO;

        // Recupera o texto salvo.
        textoApresentacaoVO = buscarTextoApresentacao(apresentacaoVO);

        // Valida o texto.
        if (textoApresentacaoVO != null) {
            // Guarda o texto.
            setTexto(textoApresentacaoVO.getTexto());
        }

        addTextoArea();

        // Alerta
        final Label alertaDadosNaoSalvos = new Label(ID_ALERTA_DADOS_NAO_SALVOS, "Atenção: texto não salvo.");
        alertaDadosNaoSalvos.setMarkupId(ID_ALERTA_DADOS_NAO_SALVOS + campo.getIdTag());
        addOrReplace(alertaDadosNaoSalvos);

        addBotaoSalvar(apresentacaoVO, alertaDadosNaoSalvos);
    }

    protected TextoApresentacaoVO buscarTextoApresentacao(final ApresentacaoVO apresentacaoVO) {
        return (TextoApresentacaoVO) apresentacaoVO.getValor(campo);
    }

    private void addBotaoSalvar(final ApresentacaoVO apresentacaoVO, final Label alertaDadosNaoSalvos) {
        // Botão de salvar.
        AjaxSubmitLinkSisAps botaoSalvar = new AjaxSubmitLinkSisAps(ID_BOTAO_SALVAR_INFORMACOES) {
            @Override
            public void executeSubmit(AjaxRequestTarget target) {
                // Declarações
                TextoApresentacaoVO textoApresentacaoVO;
                String texto;
                String textoNbsp;
                // Recupera o texto.
                texto = getTexto();
                if (texto == null) {
                    texto = "";
                }
                // Limpa textos que possuam apenas espaços em branco.
                textoNbsp = texto.replaceAll("&nbsp;", "");
                if (textoNbsp.contains(ABRE_PARAGRAFO) && textoNbsp.contains(FECHA_PARAGRAFO)) {
                    textoNbsp = textoNbsp.substring(ABRE_PARAGRAFO.length(), textoNbsp.length() - FECHA_PARAGRAFO.length());
                }

                textoNbsp = textoNbsp.trim();
                if (textoNbsp.length() == 0) {
                    texto = "";
                }
                // Salva o texto.
                textoApresentacaoVO = TextoApresentacaoMediator.get().salvar(apresentacaoVO.getPk(), campo, texto);
                // Atualiza o texto da apresentação.
                apresentacaoVO.addOrReplace(textoApresentacaoVO);
                // Exibe a mensagem de sucesso.
                mensagemSucesso();
                // Atualiza dados de última atualização.
                atualizarUltimaAtualizacao(target);
                // Atualiza alerta de dados salvos.
                target.add(alertaDadosNaoSalvos);
            }
        };
        botaoSalvar.setMarkupId(ID_BOTAO_SALVAR_INFORMACOES + campo.getIdTag());
        addOrReplace(botaoSalvar);
    }
    
    private void addTextoArea() {
        // Caixa de texto.
        CKEditorTextArea<String> txtTexto =
                new CKEditorTextArea<String>(ID_TEXTO, new PropertyModel<String>(this, "texto")) {
                    @Override
                    protected String onKeyUpJS() {
                        return CKEditorUtils.jsAtualizarAlerta(ID_ALERTA_DADOS_NAO_SALVOS + campo.getIdTag(), true);
                    }
                };
        txtTexto.setMarkupId(ID_TEXTO + campo.getIdTag());
        addOrReplace(txtTexto);
    }

    private void mensagemSucesso() {
        getPage().success("Informações salvas com sucesso.");
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}