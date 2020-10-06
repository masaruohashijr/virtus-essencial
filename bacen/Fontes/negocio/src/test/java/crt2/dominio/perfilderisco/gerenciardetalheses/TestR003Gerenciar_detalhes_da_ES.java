package crt2.dominio.perfilderisco.gerenciardetalheses;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TestR003Gerenciar_detalhes_da_ES extends TestR001Gerenciar_detalhes_da_ES {
    @Override
    public String mensagem() {
        return super.mensagem();
    }
    
    
    @Override
    public String botaoConfirmarHabilitado() {
        return super.botaoConfirmarHabilitado();
    }
}
