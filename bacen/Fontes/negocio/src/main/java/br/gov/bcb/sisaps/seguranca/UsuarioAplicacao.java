/*
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software e confidencial e propriedade do Banco Central do Brasil.
 * Nao e permitida sua distribuicao ou divulgacao do seu conteudo sem
 * expressa autorizacao do Banco Central.
 * Este arquivo contem informacoes proprietarias.
 */
package br.gov.bcb.sisaps.seguranca;

import br.gov.bcb.app.stuff.seguranca.ProvedorInformacoesUsuario;
import br.gov.bcb.app.stuff.seguranca.Usuario;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.integracao.seguranca.Perfis;

/**
 * Representa um usuario do sistema. Adicione outras propriedades caso seja necessário (i.e.: nome,e-mail, etc). <br>
 * Esta classe foi criada apenas para que não seja necessário alterar a configuração da DefaultApplication no caso de
 * sobrescrever algum método da classe {@link Usuario}.
 */
public class UsuarioAplicacao extends Usuario {

    private String matricula;
    private String nome;
    private boolean possuiPerfilAdministrador;
    private ServidorVO servidorVO;
    
    public UsuarioAplicacao(ProvedorInformacoesUsuario provider) {
        super(provider);
        preencherPerfis(provider);
    }

    public ServidorVO getServidorVO() {
        return servidorVO;
    }

    public void setServidorVO(ServidorVO servidorVO) {
        this.servidorVO = servidorVO;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    private void preencherPerfis(ProvedorInformacoesUsuario provider) {
        if (provider.isUserInRole(Perfis.RATING_TRANSACAO_ADMINISTRADOR)) {
            this.possuiPerfilAdministrador = true;
        }
    }
    
    public boolean isPossuiPerfilAdministrador() {
        return possuiPerfilAdministrador;
    }

    public void setPossuiPerfilAdministrador(boolean possuiPerfilAdministrador) {
        this.possuiPerfilAdministrador = possuiPerfilAdministrador;
    }
}
