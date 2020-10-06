package br.gov.bcb.sisaps.batch.main;

import br.gov.bcb.app.stuff.seguranca.ProvedorInformacoesUsuario;
import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.sisaps.batch.AbstractBatchApplication;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.mediator.EncerrarCorecMediator;
import br.gov.bcb.utils.logging.BCLogFactory;
import br.gov.bcb.utils.logging.BCLogger;

public class BatchEncerrarCorecCiclo extends AbstractBatchApplication {
    private static final BCLogger LOG = BCLogFactory.getLogger("BatchEncerrarCorecCiclo");

    
    private Integer idControleBatch;
    private String login;

    public static void main(String[] args) {
        new BatchEncerrarCorecCiclo().init(args);
    }
    
    public void init(String[] args) {
        processarArgumentos(args);
        configurarContexto();
        configurarUsuario(args);
        executar();
    }
    
    private void processarArgumentos(String[] args) {
        System.out.println("processarArgumentos");
        System.out.println(args.length);
        for (int i = 0; i < args.length; i++) {
            System.out.println("arg" + i + " " + args[i]);
        }
        idControleBatch = new Integer(args[0]);
        login = args[1];
    }

    protected void configurarUsuario(final String[] args) {
        UsuarioAplicacao usuario = new UsuarioAplicacao(new ProvedorInformacoesUsuario() {

            @Override
            public String getLogin() {
                return login;
            }

            @Override
            public boolean isUserInRole(String role) {
                return true;
            }
            
        });
        UsuarioCorrente.set(usuario);
    }

    @Override
    protected void executar() {
        LOG.info("ROTINA DE AUTOMATICA DE ENCERRAMENTO DE COREC DO CICLO ");
        try {
            EncerrarCorecMediator encerrarCorecMediator = 
                    (EncerrarCorecMediator) getSpringContext().getBean("encerrarCorecMediator");
            encerrarCorecMediator.processarEncerramentoCorec(idControleBatch);
        } catch (Exception e) {
            LOG.info(e.toString());
        } finally {
            //retirar para rodar teste local
            System.exit(0);
        }
    }
}