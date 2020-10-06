package br.gov.bcb.sisaps.src.mediator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.TextoApresentacaoDao;
import br.gov.bcb.sisaps.src.dominio.Apresentacao;
import br.gov.bcb.sisaps.src.dominio.TextoApresentacao;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.CampoApresentacaoEnum;
import br.gov.bcb.sisaps.src.vo.TextoApresentacaoVO;

@Service
@Scope("singleton")
public class TextoApresentacaoMediator {

    @Autowired
    private TextoApresentacaoDao textoApresentacaoDao;

    // Retorna a instância do mediator.
    public static TextoApresentacaoMediator get() {
        return SpringUtils.get().getBean(TextoApresentacaoMediator.class);
    }

    @Transactional
    public TextoApresentacaoVO salvar(Integer apresentacaoPk, CampoApresentacaoEnum campo, String texto) {
        // Declarações
        Apresentacao apresentacao;
        TextoApresentacao textoApresentacao;

        // Recupera a apresentação.
        apresentacao = ApresentacaoMediator.get().buscarPk(apresentacaoPk);

        // Verifica se já existe um texto para o campo.
        textoApresentacao = textoApresentacaoDao.buscarTexto(apresentacao, campo);

        // Existe?
        if (textoApresentacao == null) {
            // Novo texto.
            textoApresentacao = new TextoApresentacao();
            textoApresentacao.setApresentacao(apresentacao);
            textoApresentacao.setSecao(campo.getSecao());
            textoApresentacao.setCampo(campo);
        }

        // Atribui o texto ao campo.
        textoApresentacao.setTexto(texto);

        // Salva/atualiza o texto.
        textoApresentacaoDao.saveOrUpdate(textoApresentacao);

        // Reconsulta o objeto para atualizá-lo.
        textoApresentacao = textoApresentacaoDao.buscarTexto(apresentacao, campo);

        return textoApresentacao.toVO();
    }

}
