package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.gestaoApresentacao.painel;

import br.gov.bcb.sisaps.src.dominio.enumeracoes.CampoApresentacaoEnum;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;

public class PainelTextoApresentacao extends abstractPainelTexto {

    // Construtor
    public PainelTextoApresentacao(String id, ApresentacaoVO apresentacaoVO, CampoApresentacaoEnum campo) {
        super(id, apresentacaoVO, campo);
        setOutputMarkupId(true);
    }

}
