package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.gestaoApresentacao.painel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.CampoApresentacaoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;
import br.gov.bcb.sisaps.web.page.PainelSisAps;

public class PainelSecaoApresentacao extends PainelSisAps {

    private static final String ID_PAINEL_CAMPO = "idPainelCampo";
    private static final String ID_PAINEL_CAMPO_ANEXO = "idPainelCampoAnexo";

    // Construtor
    public PainelSecaoApresentacao(String id, SecaoApresentacaoEnum secao, final ApresentacaoVO apresentacaoVO) {
        super(id);
        setOutputMarkupId(true);

        // Nome da seção.
        add(new Label("idNomeSecao", secao.getDescricao()));

        // Monta os campos.
        add(new ListView<CampoApresentacaoEnum>("idPaineisCampo", secao.getCampos()) {

            // Monta os painéis de campo.
            @Override
            protected void populateItem(ListItem<CampoApresentacaoEnum> item) {
                // Declarações
                CampoApresentacaoEnum campo;

                // Inicializações
                campo = item.getModelObject();

                // Verifica o tipo do campo.

                if (campo.getTipo() == CampoApresentacaoEnum.TC_ANEXO) {
                    // Novo painel de anexo.
                    item.add(new PainelAnexoApresentacao(ID_PAINEL_CAMPO, apresentacaoVO, campo));

                } else if (campo.getTipo() == CampoApresentacaoEnum.TC_TEXTO) {
                    // Novo painel de texto.
                    item.add(new PainelTextoApresentacao(ID_PAINEL_CAMPO, apresentacaoVO, campo));
                } else if (campo.getTipo() == CampoApresentacaoEnum.TC_TEXTO_ANEXO) {
                    // Novo painel de texto.
                    item.add(new PainelTextoAnexoApresentacao(ID_PAINEL_CAMPO, apresentacaoVO, campo));
                }
            }

        });
    }

}
