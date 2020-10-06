package br.gov.bcb.sisaps.src.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.BloqueioESDAO;
import br.gov.bcb.sisaps.src.dao.EventoPerfilDeRiscoSrcDao;
import br.gov.bcb.sisaps.src.dao.OpiniaoConclusivaConsolidadoDao;
import br.gov.bcb.sisaps.src.dao.PerfilAtuacaoConsolidadoDao;
import br.gov.bcb.sisaps.src.dominio.BloqueioES;
import br.gov.bcb.sisaps.src.dominio.EventoOpiniaoConclusiva;
import br.gov.bcb.sisaps.src.dominio.EventoPerfilAtuacao;
import br.gov.bcb.sisaps.src.dominio.EventoPerfilDeRiscoSrc;
import br.gov.bcb.sisaps.src.dominio.OpiniaoConclusivaConsolidado;
import br.gov.bcb.sisaps.src.dominio.PerfilAtuacaoConsolidado;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.geral.DataUtil;

@Service
@Transactional(readOnly = true)
public class BloqueioESMediator {

    @Autowired
    private BloqueioESDAO bloqueioESDAO;
    @Autowired
    private EventoPerfilAtuacaoMediator eventoPerfilAtuacaoMediator;
    @Autowired
    private EventoOpiniaoConclusivaMediator eventoOpiniaoConclusivaMediator;
    @Autowired 
    private PerfilAtuacaoConsolidadoDao perfilAtuacaoConsolidadoDao;
    @Autowired 
    private OpiniaoConclusivaConsolidadoDao opiniaoConclusivaConsolidadoDao;
    
    @Autowired
    private EventoPerfilDeRiscoSrcMediator eventoPerfilDeRiscoMediator;
    
    @Autowired
    private EventoPerfilDeRiscoSrcDao eventoPerfilDeRiscoSrcDao;
    
    
    @Autowired
    private EventoPerfilDeRiscoSrcMediator eventoPerfilDeRiscoSrcMediator;
    
    
    public static BloqueioESMediator get() {
        return SpringUtils.get().getBean(BloqueioESMediator.class);
    }
    
    public boolean isESBloqueada(String cnpj) {
        return bloqueioESDAO.isESBloqueado(cnpj);
    }
    
    @Transactional
    public String bloquearES(String cnpj) {
        bloquearDesbloquear(cnpj, SimNaoEnum.SIM);
        BloqueioES bloqueioES = new BloqueioES();
        bloqueioES.setCnpj(cnpj);
        bloqueioES.setOperadorBloqueio(UsuarioCorrente.get().getLogin());
        bloqueioES.setDataBloqueio(DataUtil.getDateTimeAtual());
        bloqueioESDAO.save(bloqueioES);
        return "ES bloqueada com sucesso.";
    }
    
    @Transactional
    public String desbloquearES(String cnpj) {
        bloquearDesbloquear(cnpj, SimNaoEnum.NAO);
        BloqueioES bloqueioES = bloqueioESDAO.buscarUltimoBloqueioES(cnpj);
        bloqueioES.setOperadorDesbloqueio(UsuarioCorrente.get().getLogin());
        bloqueioES.setDataDesbloqueio(DataUtil.getDateTimeAtual());
        bloqueioESDAO.update(bloqueioES);
        return "ES desbloqueada com sucesso.";
    }
    
    public String getMensagemPerfilBloqueado(String cnpj, PerfilAcessoEnum perfilAcesso) {
        String retorno = "";
        boolean isEsBloqueada = BloqueioESMediator.get().isESBloqueada(cnpj);
        if (isEsBloqueada && (perfilAcesso.equals(PerfilAcessoEnum.CONSULTA_TUDO) 
                || perfilAcesso.equals(PerfilAcessoEnum.SUPERVISOR)
                || perfilAcesso.equals(PerfilAcessoEnum.INSPETOR))) {
            retorno = "O perfil de risco acessado está bloqueado e tem consulta restrita.";
        }
        return retorno;
    }
    
    private void bloquearDesbloquear(String cnpj, SimNaoEnum condicao) {
        List<EventoPerfilAtuacao> listaPerfilAtuacao = eventoPerfilAtuacaoMediator.buscarEventosPerfilAtuacao(cnpj);
        List<EventoOpiniaoConclusiva> listaOpiniaoConclusiva =
                eventoOpiniaoConclusivaMediator.buscarEventosOpiniaoConclusiva(cnpj);
        
        List<EventoPerfilDeRiscoSrc> listaPerfilRisco =
                eventoPerfilDeRiscoSrcMediator.buscarEventosPerfilDeRiscoSrc(cnpj);
        
        
        if (!listaPerfilRisco.isEmpty()) {
            for (EventoPerfilDeRiscoSrc evcPerfilRisco : listaPerfilRisco) {
                evcPerfilRisco.setBloqueado(condicao);
                eventoPerfilDeRiscoSrcDao.update(evcPerfilRisco);
            }
        }

        if (!listaPerfilAtuacao.isEmpty()) {
            for (EventoPerfilAtuacao perfilAtuacao : listaPerfilAtuacao) {
                PerfilAtuacaoConsolidado perfil = perfilAtuacao.getPerfil();
                perfil.setBloqueado(condicao);
                perfilAtuacaoConsolidadoDao.update(perfil);
            }
        }

        if (!listaOpiniaoConclusiva.isEmpty()) {
            for (EventoOpiniaoConclusiva eventoOpiniaoConclusiva : listaOpiniaoConclusiva) {
                OpiniaoConclusivaConsolidado opiniao = eventoOpiniaoConclusiva.getOpiniao();
                opiniao.setBloqueado(condicao);
                opiniaoConclusivaConsolidadoDao.update(opiniao);
            }
        }
    }

}
