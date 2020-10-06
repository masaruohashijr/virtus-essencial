package br.gov.bcb.sisaps.src.mediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.comum.excecoes.BCConsultaInvalidaException;
import br.gov.bcb.sisaps.adaptadores.bcmail.BcMailAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.Email;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dao.EntidadeUnicadDao;
import br.gov.bcb.sisaps.src.dao.EventoConsolidadoDao;
import br.gov.bcb.sisaps.src.dao.ParametroEmailAPSDAO;
import br.gov.bcb.sisaps.src.dominio.EntidadeUnicad;
import br.gov.bcb.sisaps.src.dominio.EventoConsolidado;
import br.gov.bcb.sisaps.src.dominio.ParametroEmailAPS;
import br.gov.bcb.sisaps.util.AmbienteEmail;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.UtilFormatarEmail;
import br.gov.bcb.sisaps.util.enumeracoes.TipoSubEventoPerfilRiscoSRC;

@Service
@Transactional(readOnly = true)
public class ParametroEmailAPSMediator {

    private static final String FECHAR_TAG = "'>";
    private static final String HOMOLOGACAO = "Homologacao";
    private static final String DESENVOLVIMENTO = "Desenvolvimento";
    private static final String DETALHES = "/detalhes'>";
    private static final String ENDERECO = "{ENDERECO}";
    @Autowired
    ParametroEmailAPSDAO parametroEmailAPSDAO;
    @Autowired
    EventoConsolidadoDao eventoConsolidadoDao;
    @Autowired
    EntidadeUnicadDao entidadeUnicadDao;
    @Autowired
    EntidadeSupervisionavelMediator entidadeSupervisionavelMediator;

    public static ParametroEmailAPSMediator get() {
        return SpringUtils.get().getBean(ParametroEmailAPSMediator.class);
    }

    public void enviarEmailAps(TipoSubEventoPerfilRiscoSRC tipo, Integer idObjeto, EventoConsolidado eventoConsolidado) {
        UsuarioAplicacao usuario = ((UsuarioAplicacao) UsuarioCorrente.get());
        ServidorVO servidorVO;

        try {
            servidorVO = BcPessoaAdapter.get().buscarServidorPorLogin(usuario.getLogin());
        } catch (BCConsultaInvalidaException e) {
            throw new RuntimeException(e);
        }

        if (eventoConsolidado != null) {
            List<String> destinatarios = new ArrayList<String>();
            String data = "";
            String localizacaoEvento = "";
            String localizacaoConsolidado = "";
            String nomeEs = "";
            String cnpjEs = "";
            String cnpjEsLink = "";
            String localizacao = "";
            data = Util.ultimaHora(eventoConsolidado.getUltimaAtualizacao().toDate(), true)
                    .toString(Constantes.FORMATO_DATA_HORA_SEMSEGUNDOS, new Locale("pt-br"));
            localizacaoEvento = eventoConsolidado.getDepartamento();
            if (eventoConsolidado.getConsolidado() != null) {
                localizacaoConsolidado = eventoConsolidado.getConsolidado().getSupervisao().substring(0, 5);
                if (eventoConsolidado.getConsolidado() != null) {
                    localizacaoConsolidado = eventoConsolidado.getConsolidado().getSupervisao().substring(0, 5);
                    if (eventoConsolidado.getConsolidado().getCnpjEsDefault() != null) {
                        EntidadeUnicad entidadeUnicad = EntidadeUnicadMediator.get().buscarEntidadeUnicadPorCnpj(
                                eventoConsolidado.getConsolidado().getCnpjEsDefault());
                        if (entidadeUnicad != null) {
                            String nomeAbreviado = entidadeUnicad.getNomeAbreviado();
                            nomeEs = nomeAbreviado == null ? entidadeUnicad.getNome() : nomeAbreviado;
                            cnpjEs = eventoConsolidado.getConsolidado().getCnpjEsDefault();
                            localizacao = entidadeUnicad.getConsolidado().getSupervisao();
                            List<String> cnpjsPerfilRisco = new ArrayList<String>();
                            entidadeSupervisionavelMediator.pertenceAoSRC(eventoConsolidado.getConsolidado(),
                                    cnpjsPerfilRisco);
                            if (!cnpjsPerfilRisco.isEmpty()) {
                                cnpjEsLink = cnpjsPerfilRisco.get(0);
                            }
                        }
                    }
                }
            }

            //Parametro por localização consolidado
            ParametroEmailAPS parametroEmailAPSCon =
                    parametroEmailAPSDAO.buscarEmailParticipante(localizacaoConsolidado);
            addDestinatario(destinatarios, parametroEmailAPSCon);

            //Parametro por localização evento
            ParametroEmailAPS parametroEmailAPSEvento = parametroEmailAPSDAO.buscarEmailParticipante(localizacaoEvento);
            addDestinatario(destinatarios, parametroEmailAPSEvento);

            //Parametro por GRUPOS_TODOS_EVENTOS
            ParametroEmailAPS parametroEmailAPSGrupos =
                    parametroEmailAPSDAO.buscarEmailParticipante(Constantes.PARAMETRO_EMAIL_TODOS_EVENTOS);
            addDestinatario(destinatarios, parametroEmailAPSGrupos);

            //Parametro por PERFIL_RISCO
            ParametroEmailAPS parametroEmailAPSPerfilRisco =
                    parametroEmailAPSDAO.buscarEmailParticipante(Constantes.PARAMETRO_EMAIL_PERFIL_RISCO);
            addDestinatario(destinatarios, parametroEmailAPSPerfilRisco);

            if (TipoSubEventoPerfilRiscoSRC.CONCLUSAO.equals(tipo)
                    || TipoSubEventoPerfilRiscoSRC.PERFIL_ATUACAO.equals(tipo)) {
                //Parametro por OPINIAO_CONCLUSIVA
                ParametroEmailAPS parametroEmailAPSOpiniaoConclusiva =
                        parametroEmailAPSDAO.buscarEmailParticipante(Constantes.PARAMETRO_EMAIL_AUX_OPINIAO);
                addDestinatario(destinatarios, parametroEmailAPSOpiniaoConclusiva);
            }

            //REMETENTE  
            ParametroEmailAPS parametroEmailAPSRemet =
                    parametroEmailAPSDAO.buscarEmailParticipante(Constantes.PARAMETRO_EMAIL_REMETENTE);
            String remetente = "";
            if (parametroEmailAPSRemet != null) {
                remetente = parametroEmailAPSRemet.getValor();
            }

            if (!destinatarios.isEmpty()) {
                String titulo = "";
                String corpo = "";
                if (TipoSubEventoPerfilRiscoSRC.PERFIL_ATUACAO.equals(tipo)) {
                    titulo = "APS-Painéis - Publicação de perfil de atuação -";
                    corpo = "Foi publicado novo perfil de atuação no APS - Painéis em ";
                } else if (TipoSubEventoPerfilRiscoSRC.CONCLUSAO.equals(tipo)) {
                    titulo = "APS-Painéis - Publicação de opinião conclusiva -";
                    corpo = "Foi publicada nova opinião conclusiva no APS - Painéis em ";
                }
                String tituloEmail = titulo + nomeEs;
                String corpoEmail =
                        montarTituloCorpoEmail(nomeEs, cnpjEs, cnpjEsLink, servidorVO, data, tipo, idObjeto,
                                localizacao, corpo);
                enviarEmail(destinatarios, corpoEmail, tituloEmail, remetente);
            }
        }
    }

    private void addDestinatario(List<String> destinatarios, ParametroEmailAPS parametroEmailAPS) {
        if (parametroEmailAPS != null && !destinatarios.contains(parametroEmailAPS.getValor())) {
            destinatarios.add(parametroEmailAPS.getValor().toString());
        }
    }

    private void enviarEmail(List<String> destinatarios, String corpoEmail, String assunto, String remetente) {
        Email email = new Email();
        email.setDestinatarios(destinatarios);
        email.setAssunto(UtilFormatarEmail.removerMarcadoresEmail(assunto));
        email.setCorpo(corpoEmail);
        email.setRemetente(remetente);
        BcMailAdapter.get().enviarEmail(email);
    }

    private String montarTituloCorpoEmail(String nomeEs, String cnpjEs, String cnpjEsLink, ServidorVO servidorVO,
            String data, TipoSubEventoPerfilRiscoSRC tipo, Integer idObjeto, String localizacao, String corpo) {
        String tituloCorpoEmail = corpo;
        tituloCorpoEmail += montarLink(tipo, idObjeto, cnpjEsLink);
        tituloCorpoEmail += data;
        tituloCorpoEmail += "</a>";
        tituloCorpoEmail += " por " + servidorVO.getNome();
        tituloCorpoEmail += ".<br/> Equipe responsável pela informação: " + localizacao;
        tituloCorpoEmail += "<br/> Entidade Supervisionada: " + cnpjEs + " " + nomeEs;
        return tituloCorpoEmail;
    }
    
    private String montarLink(TipoSubEventoPerfilRiscoSRC tipo, Integer idObjeto, String cnpjEsLink) {
        String link = "";
        switch (tipo) {
            case PERFIL_ATUACAO:
                link = montarLinkPerfilAtuacao(idObjeto);
                break;
            case CONCLUSAO:
                link = montarLinkOpiniaoConclusiva(idObjeto);
                break;
            default:
                link = montarLinkPerfilRisco(cnpjEsLink);
                break;
        }
        return link;
    }

    private String montarLinkPerfilRisco(String idObjeto) {
        String ambiente = setAmbiente();
        String link = "<a href='{ENDERECO}/aps-src/perfilDeRisco?cnpj=" + idObjeto + FECHAR_TAG;
        link = link.replace(ENDERECO, ambiente);
        return link;
    }

    private String montarLinkOpiniaoConclusiva(Integer idObjeto) {
        String ambiente = setAmbiente();
        String link = "<a href='{ENDERECO}/aps/#/opinioes-conclusivas/" + idObjeto + DETALHES;
        link = link.replace(ENDERECO, ambiente);
        return link;
    }

    private String montarLinkPerfilAtuacao(Integer idObjeto) {
        String ambiente = setAmbiente();
        String link = "<a href='{ENDERECO}/aps/#/eventoConsolidado/perfilAtuacao?codigo=" + idObjeto + FECHAR_TAG;
        link = link.replace(ENDERECO, ambiente);
        return link;
    }

    private String setAmbiente() {

        String ambiente = "https://";
        if (AmbienteEmail.getAmbiente() == null) {
            ambiente += "localhost:8080";
        } else {
            if (AmbienteEmail.isProducao()) {
                ambiente += "was-p";
            } else if (AmbienteEmail.getAmbiente().equals(DESENVOLVIMENTO)) {
                ambiente += "was-t";
            } else if (AmbienteEmail.getAmbiente().equals(HOMOLOGACAO)) {
                ambiente += "was-h";
            }
        }

        return ambiente;
    }

    public ParametroEmailAPS buscarEmailParticipante(String parametro) {
        return parametroEmailAPSDAO.buscarEmailParticipante(parametro);
    }

}
