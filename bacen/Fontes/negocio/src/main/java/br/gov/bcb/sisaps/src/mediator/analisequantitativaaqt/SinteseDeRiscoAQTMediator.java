package br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.analisequantitativaaqt.SinteseDeRiscoAQTDAO;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.SinteseDeRiscoAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoAQTEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.mediator.EventoConsolidadoMediator;
import br.gov.bcb.sisaps.src.mediator.GeradorAnexoMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.VersaoPerfilRiscoMediator;
import br.gov.bcb.sisaps.src.util.BufferAnexos;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.aqt.SinteseDeRiscoAQTVO;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.enumeracoes.TipoSubEventoPerfilRiscoSRC;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ConstantesMensagens;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

@Service
@Transactional(readOnly = true)
public class SinteseDeRiscoAQTMediator {

    @Autowired
    private SinteseDeRiscoAQTDAO sinteseDeRiscoAQTDAO;

    @Autowired
    private EventoConsolidadoMediator eventoConsolidadoMediator;

    public static SinteseDeRiscoAQTMediator get() {
        return SpringUtils.get().getBean(SinteseDeRiscoAQTMediator.class);
    }

    public List<SinteseDeRiscoAQTVO> buscarSintesesRiscoRevisao(String localizacao) {
        return new ArrayList<SinteseDeRiscoAQTVO>();
    }

    @Transactional
    public String salvarOuAtualizarSintese(SinteseDeRiscoAQT sintese) {
        validar(sintese);
        sintese.setUltimaAtualizacao(DataUtil.getDateTimeAtual());
        sinteseDeRiscoAQTDAO.saveOrUpdate(sintese);
        return "Síntese salva com sucesso.";
    }

    private void validar(SinteseDeRiscoAQT sintese) {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
        if (SisapsUtil.isTextoCKEditorBrancoOuNulo(sintese.getJustificativa())) {
            SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_SINTESE_ERRO_001));
        }
        SisapsUtil.lancarNegocioException(erros);
    }

    public SinteseDeRiscoAQT getUltimaSinteseParametroAQTEdicao(ParametroAQT parametroAQT, Ciclo ciclo) {
        return sinteseDeRiscoAQTDAO.getSinteseRascunho(parametroAQT, ciclo);
    }

    public SinteseDeRiscoAQT obterSinteseRiscoPorPk(Integer sintesePK) {
        SinteseDeRiscoAQT sintese = sinteseDeRiscoAQTDAO.load(sintesePK);
        inicializarReferencias(sintese);
        return sintese;
    }

    private void inicializarReferencias(SinteseDeRiscoAQT sintese) {
        if (sintese != null) {
            if (!Hibernate.isInitialized(sintese.getParametroAQT())) {
                Hibernate.initialize(sintese.getParametroAQT());
            }
            if (!Hibernate.isInitialized(sintese.getVersaoPerfilRisco())) {
                Hibernate.initialize(sintese.getVersaoPerfilRisco());
            }
        }
    }

    public SinteseDeRiscoAQT getUltimaSinteseVigente(ParametroAQT parametroAQT, Ciclo ciclo) {
        return sinteseDeRiscoAQTDAO.getSinteseVigente(parametroAQT, ciclo);
    }

    public SinteseDeRiscoAQT getSintesePorVersaoPerfil(ParametroAQT parametroAQT, Ciclo ciclo,
            List<VersaoPerfilRisco> listaVersao) {
        return sinteseDeRiscoAQTDAO.getSintesePorVersaoPerfil(parametroAQT, ciclo, listaVersao);
    }

    public String tituloBotaoConcluirSintese(ParametroAQT parametroAQT, Ciclo ciclo) {
        SinteseDeRiscoAQT vigente = getUltimaSinteseVigente(parametroAQT, ciclo);

        SinteseDeRiscoAQT edicao = getUltimaSinteseParametroAQTEdicao(parametroAQT, ciclo);
        AnaliseQuantitativaAQT analiseRascunho =
                AnaliseQuantitativaAQTMediator.get().buscarAQTRascunhoParametroECiclo(parametroAQT, ciclo);

        if (analiseRascunho != null && EstadoAQTEnum.ANALISADO.equals(analiseRascunho.getEstado())
                && sinteseRascunhoIgualVigente(vigente, edicao)) {
            return "Confirmar síntese vigente sem alteração e publicar ANEF";
        }
        return "Concluir nova síntese e atualizar perfil de risco";
    }

    public boolean sinteseRascunhoIgualVigente(SinteseDeRiscoAQT vigente, SinteseDeRiscoAQT edicao) {
        if (vigente != null && edicao != null && edicao.getJustificativa().equals(vigente.getJustificativa())
        		&& edicao.getUltimaAtualizacao().equals(vigente.getUltimaAtualizacao())
        		&& edicao.getOperadorAtualizacao().equals(vigente.getOperadorAtualizacao())) {
            return true;
        }

        return false;

    }

    public boolean botaoConcluirHabilitado(AnaliseQuantitativaAQT analiseRascunho, SinteseDeRiscoAQT vigente,
            SinteseDeRiscoAQT edicao) {

        if (vigente == null && edicao == null) {
            return false;
        } else if (vigente == null && edicao != null) {
            return true;
        } else if (!sinteseRascunhoIgualVigente(vigente, edicao)) {
            return true;
        } else {
            boolean estadoAnalisadoBotaoVisivel = isEstadoAnalisadoBotaoVisivel(analiseRascunho, vigente, edicao);
            if ((vigente != null || edicao != null) && estadoAnalisadoBotaoVisivel) {
                return true;
            }
        }

        return false;

    }

    private boolean isEstadoAnalisadoBotaoVisivel(AnaliseQuantitativaAQT analiseRascunho, SinteseDeRiscoAQT vigente,
            SinteseDeRiscoAQT edicao) {
        return analiseRascunho != null && EstadoAQTEnum.ANALISADO.equals(analiseRascunho.getEstado())
                && (vigente != null || edicao != null);
    }

    @Transactional
    public String concluirNovaSintesAQT(ParametroAQT parametro, Ciclo ciclo) {
    	
    	System.out.println("<<<<<<<<<<< PARAMETRO >>>>>>>>>> ");
        System.out.println("PARAMETRO ID: " + parametro.getPk());
        System.out.println("PARAMETRO DESCRIÇÃO : " + parametro.getDescricao());
        System.out.println("PARAMETRO METODOLOGIA: " + parametro.getMetodologia().getPk());
        
        
        System.out.println("<<<<<<<<<<< CICLO >>>>>>>>>> ");
        System.out.println("CICLO ID: " + ciclo.getPk());
        System.out.println("CICLO DESCRIÇÃO : " + parametro.getDescricao());
        System.out.println("PARAMETRO METODOLOGIA: " + ciclo.getMetodologia().getPk());
    	
    	
        Util.setIncluirBufferAnexos(true);
        BufferAnexos.resetLocalThreadBufferInclusao();
        SinteseDeRiscoAQT vigente = getUltimaSinteseVigente(parametro, ciclo);

        SinteseDeRiscoAQT edicao = getUltimaSinteseParametroAQTEdicao(parametro, ciclo);

        boolean sinteseDiferenteDaVigente = !sinteseRascunhoIgualVigente(vigente, edicao);
        
        AnaliseQuantitativaAQT aqtRascunho =
                AnaliseQuantitativaAQTMediator.get().buscarAQTRascunhoParametroECiclo(parametro, ciclo);
        aqtRascunho.setAlterarDataUltimaAtualizacao(false);

        PerfilRisco perfilAnterior = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());

        AnaliseQuantitativaAQT aqtVigente =
                AnaliseQuantitativaAQTMediator.get().buscarAQTVigentePorPerfil(parametro, ciclo, perfilAnterior);
        aqtVigente.setAlterarDataUltimaAtualizacao(false);
        PerfilRisco novoPerfilRisco =
                PerfilRiscoMediator.get().gerarVersaoPerfilRiscoPublicacaoSinteseAQT(ciclo, perfilAnterior);

        if (sinteseDiferenteDaVigente) {
            if (vigente != null) {
                novoPerfilRisco.getVersoesPerfilRisco().remove(vigente.getVersaoPerfilRisco());
            }
            VersaoPerfilRisco novaVersaoSintese =
                    VersaoPerfilRiscoMediator.get().criarVersao(edicao, TipoObjetoVersionadorEnum.SINTESE_AQT);
            novoPerfilRisco.getVersoesPerfilRisco().add(novaVersaoSintese);
            sinteseDeRiscoAQTDAO.saveOrUpdate(edicao);
            sinteseDeRiscoAQTDAO.flush();
            duplicarSinteseEdicao(edicao);
        }

        if (EstadoAQTEnum.ANALISADO.equals(aqtRascunho.getEstado())) {
            novoPerfilRisco.getVersoesPerfilRisco().remove(aqtVigente.getVersaoPerfilRisco());
            VersaoPerfilRisco novaVersaoAnef =
                    VersaoPerfilRiscoMediator.get().criarVersao(aqtRascunho, TipoObjetoVersionadorEnum.AQT);
            novoPerfilRisco.getVersoesPerfilRisco().add(novaVersaoAnef);
            AnaliseQuantitativaAQTMediator.get().publicarANEF(ciclo, aqtRascunho, aqtVigente);
        }

        PerfilRiscoMediator.get().saveOrUpdate(novoPerfilRisco);
        GeradorAnexoMediator.get().incluirAnexosBuffer();

        eventoConsolidadoMediator.incluirEventoPerfilDeRisco(ciclo, TipoSubEventoPerfilRiscoSRC.SINTESE_ANEF);
        return "Síntese concluída com sucesso.";
    }

    private void duplicarSinteseEdicao(SinteseDeRiscoAQT edicao) {
        SinteseDeRiscoAQT novaRascunho = new SinteseDeRiscoAQT();
        novaRascunho.setCiclo(edicao.getCiclo());
        novaRascunho.setJustificativa(edicao.getJustificativa());
        novaRascunho.setParametroAQT(edicao.getParametroAQT());
        novaRascunho.setOperadorAtualizacao(edicao.getOperadorAtualizacao());
        novaRascunho.setUltimaAtualizacao(edicao.getUltimaAtualizacao());
        novaRascunho.setAlterarDataUltimaAtualizacao(false);
        sinteseDeRiscoAQTDAO.saveOrUpdate(novaRascunho);

    }
    
    public List<SinteseDeRiscoAQT> buscarPorPerfilRisco(Integer pkPerfilRisco) {
        return sinteseDeRiscoAQTDAO.buscarPorPerfilRisco(pkPerfilRisco);
    }
    
    public List<SinteseDeRiscoAQT> buscarSintesesRascunho(Ciclo ciclo) {
        return sinteseDeRiscoAQTDAO.buscarSintesesRascunho(ciclo);
    }

    public void criarSintesesNovoCiclo(PerfilRisco perfilRiscoCicloAtual, Ciclo novoCiclo) {
        // Criar sínteses vigentes e de rascunho com os mesmos dados das sínteses vigentes do ciclo Corec.
        List<SinteseDeRiscoAQT> sintesesVigentesCicloAtual = buscarPorPerfilRisco(perfilRiscoCicloAtual.getPk());
        for (SinteseDeRiscoAQT sinteseVigenteCicloAtual : sintesesVigentesCicloAtual) {
            criarSinteseNovoCiclo(novoCiclo, sinteseVigenteCicloAtual, true);
        }
        
        List<SinteseDeRiscoAQT> sintesesRascunhosCicloAtual = buscarSintesesRascunho(perfilRiscoCicloAtual.getCiclo());
        for (SinteseDeRiscoAQT sinteseRascunhoCicloAtual : sintesesRascunhosCicloAtual) {
            criarSinteseNovoCiclo(novoCiclo, sinteseRascunhoCicloAtual, false);
        }
    }

    private void criarSinteseNovoCiclo(Ciclo novoCiclo, SinteseDeRiscoAQT sinteseAnterior, boolean isVigente) {
        SinteseDeRiscoAQT novaSinteseVigente = new SinteseDeRiscoAQT();
        novaSinteseVigente.setCiclo(novoCiclo);
        novaSinteseVigente.setParametroAQT(sinteseAnterior.getParametroAQT());
        novaSinteseVigente.setJustificativa(sinteseAnterior.getJustificativa());
        novaSinteseVigente.setOperadorAtualizacao(sinteseAnterior.getOperadorAtualizacao());
        novaSinteseVigente.setUltimaAtualizacao(sinteseAnterior.getUltimaAtualizacao());
        novaSinteseVigente.setAlterarDataUltimaAtualizacao(false);
        if (isVigente) {
            PerfilRiscoMediator.get().incluirVersaoPerfilRiscoAtual(novoCiclo, novaSinteseVigente, 
                    TipoObjetoVersionadorEnum.SINTESE_AQT);
        }
        sinteseDeRiscoAQTDAO.save(novaSinteseVigente);
    }
}
