package br.gov.bcb.sisaps.src.mediator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dao.DesignacaoDAO;
import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Delegacao;
import br.gov.bcb.sisaps.src.dominio.Designacao;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.EstadoARCEnum;
import br.gov.bcb.sisaps.src.validacao.RegraDesignacaoInclusaoValidacaoCamposTela;
import br.gov.bcb.sisaps.src.vo.ARCDesignacaoVO;
import br.gov.bcb.sisaps.src.vo.ArcNotasVO;
import br.gov.bcb.sisaps.src.vo.ConsultaARCDesignacaoVO;
import br.gov.bcb.sisaps.src.vo.ConsultaDesignacaoVO;
import br.gov.bcb.sisaps.src.vo.DesignacaoVO;

@Service
@Transactional(readOnly = true)
public class DesignacaoMediator extends AbstractMediatorPaginado<DesignacaoVO, Integer, ConsultaDesignacaoVO> {

    @Autowired
    private DesignacaoDAO designacaoDAO;

    @Autowired
    private AvaliacaoRiscoControleMediator avaliacaoRiscoControleMediator;

    @Override
    protected DesignacaoDAO getDao() {
        return designacaoDAO;
    }

    public static DesignacaoMediator get() {
        return SpringUtils.get().getBean(DesignacaoMediator.class);
    }
    
    public List<Designacao> buscarDesignacoes() {
        return designacaoDAO.buscarDesignacoes();
    }

    public Designacao buscarDesignacaoPorPk(Integer pk) {
        return designacaoDAO.load(pk);
    }

    @Transactional
    public String incluir(String matriculaLogado, List<ARCDesignacaoVO> arcsDesignacao, ServidorVO servidorEquipe,
            ServidorVO servidorUnidade, boolean ehAnalise) {
        new RegraDesignacaoInclusaoValidacaoCamposTela(matriculaLogado, arcsDesignacao, servidorEquipe, servidorUnidade)
                .validar();
        String matriculaServidorDesignado = null;
        if (servidorEquipe == null) {
            matriculaServidorDesignado = servidorUnidade.getMatricula();
        } else {
            matriculaServidorDesignado = servidorEquipe.getMatricula();
        }
        for (ARCDesignacaoVO arcDesignacaoVO : arcsDesignacao) {
            Designacao designacao = converterParaEntidade(arcDesignacaoVO, matriculaServidorDesignado);
            incluir(designacao, ehAnalise);
            AvaliacaoRiscoControle arc = designacao.getAvaliacaoRiscoControle();
            DelegacaoMediator.get().excluirDelegacaoARC(arc);
            if (AvaliacaoRiscoControleMediator.get().estadoPrevisto(arc.getEstado())) {
                arc.setEstado(EstadoARCEnum.DESIGNADO);
            } else if (AvaliacaoRiscoControleMediator.get()
                    .estadoPreenchidoAnaliseDelegadaEmAnaliseAnalisado(arc.getEstado())) {
                arc.setEstado(EstadoARCEnum.EM_EDICAO);
            }
            arc.setOperadorPreenchido(null);
            arc.setOperadorAnalise(null);
            arc.setDataAnalise(null);
            arc.setDataPreenchido(null);
            arc.setDesignacao(designacao);
            arc.setAlterarDataUltimaAtualizacao(false);
            avaliacaoRiscoControleMediator.alterar(arc);
        }
        return "ARC designado com sucesso.";
    }

    @Transactional
    public String incluir(String matriculaLogado, Atividade atividade, 
            AvaliacaoRiscoControle avaliacaoRiscoControle,
            ServidorVO servidorEquipe, ServidorVO servidorUnidade, boolean ehAnalise) {
        List<ARCDesignacaoVO> nova = new ArrayList<ARCDesignacaoVO>();
        ARCDesignacaoVO novaVO = new ARCDesignacaoVO(avaliacaoRiscoControle.getPk());
        Designacao designacao = designacaoDAO.buscarDesignacaoPorARC(avaliacaoRiscoControle.getPk());
        if (designacao != null) {
            novaVO.setPkDesignacao(designacao.getPk());
        }
        if (atividade != null) {
            novaVO.setPkAtividade(atividade.getPk());
        }
        nova.add(novaVO);
       return this.incluir(matriculaLogado, nova, servidorEquipe, servidorUnidade, ehAnalise);
    }

    @Transactional
    public Designacao buscarDesignacaoPorARC(int pkArc) {
        return designacaoDAO.buscarDesignacaoPorARC(pkArc);
    }

    @Transactional
    public void excluir(AvaliacaoRiscoControle avaliacao, Designacao designacao) {
        //chamar validação
        designacaoDAO.delete(designacao);
        designacaoDAO.getSessionFactory().getCurrentSession().flush();
        avaliacao.setEstado(EstadoARCEnum.PREVISTO);
        avaliacaoRiscoControleMediator.alterar(avaliacao);
    }

    @Transactional
    public void excluir(Designacao designacao) {
        designacaoDAO.delete(designacao);
    }

    private Designacao converterParaEntidade(ARCDesignacaoVO arcDesignacaoVO, String matriculaServidorDesignado) {
        Designacao designacao = new Designacao();
        if (arcDesignacaoVO.getPkDesignacao() != null) {
            designacao = designacaoDAO.getRecord(arcDesignacaoVO.getPkDesignacao());
        }
        designacao.setAvaliacaoRiscoControle(avaliacaoRiscoControleMediator.buscarPorPk(arcDesignacaoVO.getPk()));
        designacao.setMatriculaServidor(matriculaServidorDesignado);
        return designacao;
    }

    private void incluir(Designacao designacao, boolean ehAnalise) {
        if (designacao.getPk() == null) {
            designacaoDAO.save(designacao);
        } else {
            designacaoDAO.merge(designacao);
        }
        designacaoDAO.getSessionFactory().getCurrentSession().flush();
    }

    public boolean isARCDesignado(AvaliacaoRiscoControle avaliacaoRiscoControle, String matriculaServidor) {
        return designacaoDAO.isARCDesignado(avaliacaoRiscoControle, matriculaServidor);
    }

    @Transactional
    public void duplicarDesignacaoARCConclusaoAnalise(Ciclo ciclo, AvaliacaoRiscoControle arc,
            AvaliacaoRiscoControle novoARC, boolean isCopiarUsuarioAnterior) {
        if (EstadoARCEnum.DESIGNADO.equals(novoARC.getEstado())) {
            Designacao nova = new Designacao();
            nova.setAvaliacaoRiscoControle(novoARC);
            nova.setMatriculaServidor(arc.getDesignacao().getMatriculaServidor());
            if (isCopiarUsuarioAnterior) {
                nova.setAlterarDataUltimaAtualizacao(false);
                nova.setUltimaAtualizacao(arc.getDesignacao().getUltimaAtualizacao());
                nova.setOperadorAtualizacao(arc.getDesignacao().getOperadorAtualizacao());
            }
            incluir(nova, false);
            novoARC.setDesignacao(nova);
        }

    }

    public List<ARCDesignacaoVO> buscarListaArcs(Ciclo ciclo, ConsultaARCDesignacaoVO consulta) {
        consulta.setListaEstados(EstadoARCEnum.listaEstadosPrevistoDesignado());
        return obterListaARCDesignacaoVO(ciclo, consulta);
    }

    public List<ARCDesignacaoVO> buscarListaARCsAnalise(Ciclo ciclo, ConsultaARCDesignacaoVO consulta) {
        consulta.setListaEstados(EstadoARCEnum.listaEstadosPreenchidoDelegadoAnalise());
        return obterListaARCDesignacaoVO(ciclo, consulta);
    }

    public List<ARCDesignacaoVO> buscarListaARCsAtualizacao(Ciclo ciclo, ConsultaARCDesignacaoVO consulta) {
        consulta.setListaEstados(EstadoARCEnum.listaEstadosPrevistoDesignadoEdicao());
        return obterListaARCDesignacaoVO(ciclo, consulta);
    }

    private List<ARCDesignacaoVO> obterListaARCDesignacaoVO(Ciclo ciclo, ConsultaARCDesignacaoVO consulta) {
        consulta.setMatriz(ciclo.getMatriz());
        return avaliacaoRiscoControleMediator.consultaARCsDesignacaoVO(consulta);
    }

    public String preencherResponsavel(ARCDesignacaoVO arcDesignacaoVO, Ciclo ciclo) {
        AvaliacaoRiscoControleMediator avaliacaoMediator = AvaliacaoRiscoControleMediator.get();

        String responsavel = "";
        ArcNotasVO avaliacaoRiscoControle = avaliacaoRiscoControleMediator.consultarNotasArc(arcDesignacaoVO.getPk());
        String matricula = "";
        UsuarioAplicacao usuario = ((UsuarioAplicacao) UsuarioCorrente.get());
        if (EstadoARCEnum.listaEstadosPrevistoPreenchidoAnalisado().contains(
                arcDesignacaoVO.getEstado())) {
            responsavel = "";
        } else if (avaliacaoMediator.estadoDesignado(arcDesignacaoVO.getEstado())
                || avaliacaoMediator.estadoEmEdicao(arcDesignacaoVO.getEstado())) {
            Designacao designacao =
                    DesignacaoMediator.get().buscarDesignacaoPorPk(avaliacaoRiscoControle.getDesignacaoPk());
            matricula = designacao.getMatriculaServidor();
            responsavel = buscarResponsavel(matricula);
        } else if (avaliacaoMediator.responsavelEstadoAnaliseDelegadaEmAnalise(avaliacaoRiscoControle, usuario)) {
            Delegacao delegacao = DelegacaoMediator.get().buscarDelegacaoPorPk(avaliacaoRiscoControle.getDelegacaoPk());
            matricula = delegacao.getMatriculaServidor();
            responsavel = buscarResponsavel(matricula);
        } else {
            if (avaliacaoMediator.estadoEmAnaliseSupervisor(avaliacaoRiscoControle, arcDesignacaoVO.getEstado())) {
                matricula =
                        CicloMediator.get().buscarChefeAtual(ciclo.getEntidadeSupervisionavel().getLocalizacao())
                                .getMatricula();
                responsavel = buscarResponsavel(matricula);
            }
        }
        return responsavel;
    }

    private String buscarResponsavel(String matricula) {
        ServidorVO servidorVO = BcPessoaAdapter.get().buscarServidor(matricula);
        return servidorVO.getNome();
    }

    @Transactional
    public void copiarDesignacaoARCAnterior(AvaliacaoRiscoControle arcPerfilRiscoVigente) {
        AvaliacaoRiscoControle arcAnterior = arcPerfilRiscoVigente.getAvaliacaoRiscoControleVigente();
        Designacao designacao = new Designacao();
        if (arcPerfilRiscoVigente.getDesignacao() != null) {
            designacao = arcPerfilRiscoVigente.getDesignacao();
        }
        designacao.setAvaliacaoRiscoControle(arcPerfilRiscoVigente);
        designacao.setMatriculaServidor(arcAnterior.getDesignacao().getMatriculaServidor());
        designacao.setUltimaAtualizacao(arcAnterior.getDesignacao().getUltimaAtualizacao());
        designacao.setOperadorAtualizacao(arcAnterior.getDesignacao().getOperadorAtualizacao());
        designacao.setAlterarDataUltimaAtualizacao(false);
        designacaoDAO.saveOrUpdate(designacao);
        arcPerfilRiscoVigente.setDesignacao(designacao);
    }

}

