package br.gov.bcb.sisaps.src.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.EventoPerfilAtuacaoDao;
import br.gov.bcb.sisaps.src.dominio.EventoPerfilAtuacao;

@Service
@Transactional(readOnly = true)
public class EventoPerfilAtuacaoMediator {

    @Autowired
    EventoPerfilAtuacaoDao eventoPerfilAtuacaoDao;

    public static EventoPerfilAtuacaoMediator get() {
        return SpringUtils.get().getBean(EventoPerfilAtuacaoMediator.class);
    }

    public List<EventoPerfilAtuacao> buscarEventosPerfilAtuacao(String cnpj) {
        return eventoPerfilAtuacaoDao.buscarEventosPerfilAtuacao(cnpj);
    }

    public void salvar(EventoPerfilAtuacao eventoPerfilAtuacao) {
        eventoPerfilAtuacaoDao.saveOrUpdate(eventoPerfilAtuacao);
    }
}
