package crt2;

import java.util.LinkedList;
import java.util.List;

import nu.xom.Node;
import nu.xom.Nodes;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunnerSpringScenario;
import org.specrunner.util.aligner.core.DefaultAlignmentException;
import org.specrunner.util.string.core.StringNormalizerDefault;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.especificacao.spring.listener.StubCleanerListener;
import br.gov.bcb.sisaps.adaptadores.pessoa.Email;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.stubs.BcMailStub;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.junit.listener.GerarEsquemaListener;
import crt2.junit.listener.PluginObjectListener;
import crt2.junit.listener.SpringUtilExtendedSetterListener;
import crt2.junit.listener.UsuarioCorrenteSetterListener;

@RunWith(SRRunnerSpringScenario.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-teste.xml"})
@TestExecutionListeners(inheritListeners = false, listeners = {DependencyInjectionTestExecutionListener.class,
        SpringUtilExtendedSetterListener.class, UsuarioCorrenteSetterListener.class, StubCleanerListener.class,
        GerarEsquemaListener.class, PluginObjectListener.class})
@SuppressWarnings({"PMD.AbstractClassWithoutAnyMethod", "PMD.AbstractClassWithoutAbstractMethod",
        "PMD.AtLeastOneConstructor", "PMD.AbstractNaming"})
public abstract class ConfiguracaoTestesNegocio extends AbstractTestesNegocio {

    protected NegocioException erro;

    public boolean verificar(Node node) {
        Assert.assertNotNull("Um objeto de erro é esperado.", erro);
        List<String> esperadas = new LinkedList<String>();
        Nodes ns = node.query("child::li");
        for (int i = 0; i < ns.size(); i++) {
            esperadas.add(new StringNormalizerDefault().normalize(ns.get(i).getValue()));
        }
        List<String> recebidas = new LinkedList<String>();
        for (ErrorMessage e : erro.getMensagens()) {
            String tmp = new StringNormalizerDefault().normalize(e.toString());
            // para corrigir bug do Negocio exception que adiciona duas vezes o valor na lista
            // quando usamos o construtor que recebe uma String.
            if (!recebidas.contains(tmp)) {
                recebidas.add(tmp);
            }
        }
        if (!esperadas.toString().equals(recebidas.toString())) {
            throw new RuntimeException(
                    new DefaultAlignmentException(esperadas.toString(), recebidas.toString()).asString());
        }
        return true;
    }

    public PerfilAcessoEnum perfilUsuario() {
        String login = ((UsuarioAplicacao) UsuarioCorrente.get()).getLogin();
        if ("deliq.sisliq101".equals(login) || "deliq.sisliq114".equals(login)) {
            return PerfilAcessoEnum.SUPERVISOR;
        }

        if ("deliq.sisliq113".equals(login) || "deliq.sisliq112".equals(login)) {
            return PerfilAcessoEnum.INSPETOR;
        }

        if ("deliq.sisliq123".equals(login)) {
            return PerfilAcessoEnum.CONSULTA_TUDO;
        }

        if ("deliq.sisliq108".equals(login)) {
            return PerfilAcessoEnum.CONSULTA_TUDO;
        }

        return PerfilAcessoEnum.INSPETOR;
    }
    
    public String formatarMensagem(String mensagem) {
        return mensagem.replace("[", "").replace("]", "");
    }
    
    
    public List<Email> getEmailsEnviados() {
        List<Email> listaEMail = BcMailStub.getListaEMail();
        BcMailStub.limpaListaEMails();
        return listaEMail;
    }
    
    public String getRemetente(Email resultado) {
        return resultado.getRemetente();
    }

    public String getDestinatario(Email resultado) {
        return resultado.getDestinatario();
    }

    public String getTitulo(Email resultado) {
        return resultado.getAssunto();
    }

    public String getCorpo(Email resultado) {
        return resultado.getCorpo();
    }
}