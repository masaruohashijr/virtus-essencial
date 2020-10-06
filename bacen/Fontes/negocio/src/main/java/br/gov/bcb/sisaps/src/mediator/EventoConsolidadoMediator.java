package br.gov.bcb.sisaps.src.mediator;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dao.EventoConsolidadoDao;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Consolidado;
import br.gov.bcb.sisaps.src.dominio.EntidadeUnicad;
import br.gov.bcb.sisaps.src.dominio.EventoConsolidado;
import br.gov.bcb.sisaps.src.dominio.EventoOpiniaoConclusiva;
import br.gov.bcb.sisaps.src.dominio.EventoPerfilAtuacao;
import br.gov.bcb.sisaps.src.dominio.OpiniaoConclusivaConsolidado;
import br.gov.bcb.sisaps.src.dominio.PerfilAtuacaoConsolidado;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.enumeracoes.TipoEventoConsolidadoEnum;
import br.gov.bcb.sisaps.util.enumeracoes.TipoSubEventoPerfilRiscoSRC;
import br.gov.bcb.sisaps.util.geral.DataUtil;

@Service
@Transactional(readOnly = true)
public class EventoConsolidadoMediator {
    
    @Autowired 
    private EventoConsolidadoDao eventoConsolidadoDao;
    
    @Autowired
    private EntidadeUnicadMediator entidadeUnicadMediator;
    
    @Autowired
    private EventoPerfilDeRiscoSrcMediator eventoPerfilDeRiscoSrcMediator;
    
    @Autowired
    private EntidadeSupervisionavelMediator entidadeSupervisionavelMediator;

    @Autowired
    private EventoPerfilAtuacaoMediator eventoPerfilAtuacaoMediator;

    @Autowired
    private EventoOpiniaoConclusivaMediator eventoOpiniaoConclusivaMediator;

    @Autowired
    private ParametroEmailAPSMediator parametroEmailAPSMediator;

    public static EventoConsolidadoMediator get() {
        return SpringUtils.get().getBean(EventoConsolidadoMediator.class);
    }
    
    public List<EventoConsolidado> buscarEventos() {
        return eventoConsolidadoDao.buscarEventos();
    }

    public void incluirEventoPerfilDeRisco(Ciclo ciclo, TipoSubEventoPerfilRiscoSRC tipoEvento) {
        incluirEventoPerfilDeRisco(ciclo, tipoEvento, null, null, false);
    }
    
    @Transactional
    public void incluirEventoPerfilDeRisco(Ciclo ciclo, TipoSubEventoPerfilRiscoSRC tipoEvento,
            DateTime dateTime, String operador, boolean isBatch) {
        EntidadeUnicad entidadeUnicad = entidadeUnicadMediator
                .buscarEntidadeUnicadPorCnpj(ciclo.getEntidadeSupervisionavel().getConglomeradoOuCnpj());

        EventoConsolidado eventoConsolidadoBase = null;
        if (entidadeSupervisionavelMediator.possuiPermissaoGeracaoEventos(entidadeUnicad)) {
            eventoConsolidadoBase =
                    eventoConsolidadoDao.buscarEventoConsolidadoPerfilRiscoDataAtual(entidadeUnicad.getConsolidado());
            if (eventoConsolidadoBase == null) {
                eventoConsolidadoBase = incluirEventoConsolidado(TipoEventoConsolidadoEnum.PERFIL_DE_RISCO, dateTime,
                        operador, entidadeUnicad.getConsolidado(), isBatch);
                eventoPerfilDeRiscoSrcMediator.incluirEventoESubEventoPerfilRisco(tipoEvento, ciclo,
                        eventoConsolidadoBase);
            } else {
                UsuarioAplicacao usuario = ((UsuarioAplicacao) UsuarioCorrente.get());
                if (isBatch) {
                    eventoConsolidadoBase.setUltimaAtualizacao(dateTime);
                    eventoConsolidadoBase.setDepartamento(operador.substring(0, 5).toUpperCase());
                    eventoConsolidadoBase.setOperadorAtualizacao(operador);
                } else {
                    eventoConsolidadoBase.setDepartamento(usuario.getServidorVO().getLocalizacaoAtual().substring(0, 5));
                    eventoConsolidadoBase.setUltimaAtualizacao(Util.ultimaHora(DataUtil.getDateTimeAtual().toDate()));
                    eventoConsolidadoBase.setOperadorAtualizacao(UsuarioCorrente.get().getLogin());
                }
                eventoConsolidadoBase.setAlterarDataUltimaAtualizacao(false);
                eventoConsolidadoDao.update(eventoConsolidadoBase);
                eventoPerfilDeRiscoSrcMediator.atualizarEventoESubEventoPerfilRisco(tipoEvento, ciclo,
                        eventoConsolidadoBase);
            }
        }
    }

    @Transactional(isolation=Isolation.READ_COMMITTED)
    public void incluirEventoPerfilAtuacao(EntidadeUnicad entidadeUnicad, PerfilAtuacaoConsolidado perfilPublicado,
            boolean isBatch) {
        EventoConsolidado eventoConsolidado =
                incluirEventoConsolidado(TipoEventoConsolidadoEnum.PERFIL_ATUACAO, perfilPublicado.getDataPublicacao(),
                        perfilPublicado.getOperadorPublicacao(), entidadeUnicad.getConsolidado(), isBatch);
        EventoPerfilAtuacao eventoPerfilAtuacao = new EventoPerfilAtuacao();
        eventoPerfilAtuacao.setEventoConsolidado(eventoConsolidado);
        eventoPerfilAtuacao.setPerfil(perfilPublicado);
        eventoPerfilAtuacao.setCnpjCodigoOrigemSrc(entidadeUnicad.getCnpjConglomerado());
        eventoPerfilAtuacaoMediator.salvar(eventoPerfilAtuacao);
        if (!isBatch) {
            parametroEmailAPSMediator.enviarEmailAps(TipoSubEventoPerfilRiscoSRC.PERFIL_ATUACAO,
                    perfilPublicado.getPk(), eventoConsolidado);
        }
    }

    public void incluirEventoOpiniao(EntidadeUnicad entidadeUnicad, OpiniaoConclusivaConsolidado opiniaoPublicada,
            boolean isBatch) {
        EventoConsolidado eventoConsolidado = incluirEventoConsolidado(TipoEventoConsolidadoEnum.OPINIAO_CONCLUSIVA,
                opiniaoPublicada.getDataPublicacao(), opiniaoPublicada.getOperadorPublicacao(),
                entidadeUnicad.getConsolidado(), isBatch);
        EventoOpiniaoConclusiva eventoOpiniaoConclusiva = new EventoOpiniaoConclusiva();
        eventoOpiniaoConclusiva.setEventoConsolidado(eventoConsolidado);
        eventoOpiniaoConclusiva.setOpiniao(opiniaoPublicada);
        eventoOpiniaoConclusiva.setCnpjCodigoOrigemSrc(entidadeUnicad.getCnpjConglomerado());
        eventoOpiniaoConclusivaMediator.salvar(eventoOpiniaoConclusiva);
        if (!isBatch) {
            parametroEmailAPSMediator.enviarEmailAps(TipoSubEventoPerfilRiscoSRC.CONCLUSAO, opiniaoPublicada.getPk(),
                    eventoConsolidado);
        }
    }

    private EventoConsolidado incluirEventoConsolidado(TipoEventoConsolidadoEnum tipoEvento, DateTime dateTime,
            String operador, Consolidado consolidado, boolean isBatch) {
        EventoConsolidado eventoConsolidado = new EventoConsolidado();
        eventoConsolidado.setConsolidado(consolidado);
        eventoConsolidado.setTipo(tipoEvento);
        if (isBatch) {
            eventoConsolidado.setUltimaAtualizacao(dateTime);
            eventoConsolidado.setDepartamento(operador.substring(0, 5).toUpperCase());
            eventoConsolidado.setOperadorAtualizacao(operador);
        } else {
            UsuarioAplicacao usuario = ((UsuarioAplicacao) UsuarioCorrente.get());
            eventoConsolidado.setUltimaAtualizacao(Util.ultimaHora(DataUtil.getDateTimeAtual().toDate()));
            eventoConsolidado.setDepartamento(usuario.getServidorVO().getLocalizacaoAtual().substring(0, 5));
            eventoConsolidado.setOperadorAtualizacao(UsuarioCorrente.get().getLogin());
        }
        eventoConsolidado.setAlterarDataUltimaAtualizacao(false);
        eventoConsolidadoDao.saveOrUpdate(eventoConsolidado);
        return eventoConsolidado;
    }

}
