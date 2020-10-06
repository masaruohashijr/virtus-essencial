package br.gov.bcb.sisaps.src.mediator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.src.dao.CicloDAO;
import br.gov.bcb.sisaps.src.dao.EntidadeSupervisionavelDAO;
import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Consolidado;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavelETL;
import br.gov.bcb.sisaps.src.dominio.EntidadeUnicad;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.ObservacaoAgendaCorec;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.helper.ComponenteOrganizacionalHelper;
import br.gov.bcb.sisaps.src.vo.CicloVO;
import br.gov.bcb.sisaps.src.vo.ComponenteOrganizacionalVO;
import br.gov.bcb.sisaps.src.vo.ConsultaEntidadeSupervisionavelVO;
import br.gov.bcb.sisaps.src.vo.EntidadeSupervisionavelVO;
import br.gov.bcb.sisaps.src.vo.SinteseRiscoRevisaoVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.enumeracoes.TipoSubEventoPerfilRiscoSRC;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

@Service
@Transactional(readOnly = true)
public class EntidadeSupervisionavelMediator extends
        AbstractMediatorPaginado<EntidadeSupervisionavelVO, Integer, ConsultaEntidadeSupervisionavelVO> {

    @Autowired
    private EntidadeSupervisionavelDAO entidadeSupervisionavelDAO;

    @Autowired
    private CicloDAO cicloDAO;

    @Autowired
    private EventoConsolidadoMediator eventoConsolidadoMediator;

    public static EntidadeSupervisionavelMediator get() {
        return SpringUtils.get().getBean(EntidadeSupervisionavelMediator.class);
    }

    public EntidadeSupervisionavel loadPK(Integer pk) {
        EntidadeSupervisionavel entSuper = entidadeSupervisionavelDAO.load(pk);
        if (entSuper.getMetodologia() != null) {
            Hibernate.initialize(entSuper.getMetodologia());
        }
        return entSuper;
    }

    @Override
    public List<EntidadeSupervisionavelVO> consultar(ConsultaEntidadeSupervisionavelVO consulta) {
        return entidadeSupervisionavelDAO.consultar(consulta);
    }

    @SuppressWarnings("unchecked")
    public List<EntidadeSupervisionavelVO> consultarEntidadesPerfilConsulta(ConsultaEntidadeSupervisionavelVO consulta) {
        List<EntidadeSupervisionavelVO> retorno = new ArrayList<EntidadeSupervisionavelVO>();
        List<EntidadeSupervisionavelVO> entidadesSemCiclo =
                entidadeSupervisionavelDAO.consultarEntidadesSemCicloPainelConsulta(consulta);
        List<EntidadeSupervisionavelVO> entidadesComCicloEmAndamentoCorec =
                entidadeSupervisionavelDAO.consultarEntidadesComCicloEmAndamentoCorecPainelConsulta(consulta);
        retorno.addAll(entidadesSemCiclo);
        retorno.addAll(entidadesComCicloEmAndamentoCorec);

        ComparatorChain cc = new ComparatorChain();
        cc.addComparator(new BeanComparator("nome"));
        Collections.sort(retorno, cc);

        
        if (consulta.isAdministrador()) {
            for (EntidadeSupervisionavelVO ent : retorno) {
                String anos = EntidadeSupervisionavelMediator.get().retornarQuatidadeAnosCiclo(ent.getPk());
                ent.setCiclos(anos);
                Ciclo ciclo = CicloMediator.get().consultarUltimoCicloEmAndamentoCorecES(ent.getPk());
                if (ciclo != null) {
                    ent.setCorecPrevisto(ciclo.getDataPrevisaoCorec());
                    ent.setInicioCiclo(ciclo.getDataInicio());
                }
            }
        }

        return retorno;
    }

    public EntidadeSupervisionavel buscarEntidadeSupervisionavelPorPK(Integer id) {
        return entidadeSupervisionavelDAO.buscarEntidadeSupervisionavelPorPK(id);
    }

    public List<EntidadeSupervisionavel> buscarPertenceSrc() {
        return entidadeSupervisionavelDAO.buscarPertenceSrc();
    }

    public List<ServidorVO> buscarEquipeES(String localizacao) {
        List<ServidorVO> equipeES = new ArrayList<ServidorVO>();

        ServidorVO supervisor =
                BcPessoaAdapter.get().buscarServidor(CicloMediator.get().buscarChefeAtual(localizacao).getMatricula());
        equipeES.add(supervisor);
        ServidorVO chefe = BcPessoaAdapter.get().buscarServidor(supervisor.getMatriculaChefe());
        equipeES.add(chefe);
        ServidorVO substituto = BcPessoaAdapter.get().buscarServidor(supervisor.getMatriculaSubstituto());
        equipeES.add(substituto);

        return equipeES;
    }

    public boolean validarServidorEquipeEs(String matriculaServidor, String localizacaoES) {
        ServidorVO servidor = BcPessoaAdapter.get().buscarServidor(matriculaServidor);
        if (servidor != null && servidor.getLocalizacao().trim().equals(localizacaoES.trim())) {
            return true;
        }
        return false;
    }

    public List<EntidadeSupervisionavelVO> buscarEntidadeSemCiclo() {
        return entidadeSupervisionavelDAO.buscarEntidadeSemCiclo();
    }

    @Transactional
    public void alterar(EntidadeSupervisionavel entidadeSupervisionavel) {
        entidadeSupervisionavelDAO.update(entidadeSupervisionavel);
    }

    public EntidadeSupervisionavel buscarPorPerfilRisco(Integer pkPerfilRisco) {
        return entidadeSupervisionavelDAO.buscarPorPerfilRisco(pkPerfilRisco);
    }

    @Override
    protected EntidadeSupervisionavelDAO getDao() {
        return entidadeSupervisionavelDAO;
    }

    public List<ComponenteOrganizacionalVO> montarUnidadesValidasEAtivas() {
        return BcPessoaAdapter.get().consultarUnidadesAtivas();
    }

    public List<EntidadeSupervisionavel> buscarVersoeESs() {
        return entidadeSupervisionavelDAO.buscarVersoeESs();
    }

    public SessionFactory obterSessionFactory() {
        return entidadeSupervisionavelDAO.getSessionFactory();
    }

    public List<SinteseRiscoRevisaoVO> buscarSintesesRiscoRevisao(String localizacao) {
        return entidadeSupervisionavelDAO.buscarSintesesRiscoRevisao(localizacao);
    }

    public List<ComponenteOrganizacionalVO> consultarUnidadesESsAtivas() {
        List<String> localizacoes = entidadeSupervisionavelDAO.consultarLocalizacoesESsAtivas();
        return ComponenteOrganizacionalHelper.montarComponenteOrganizacionalVO(localizacoes);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public EntidadeSupervisionavel incluirNovaEntidadeSupervisionavel(Metodologia metodologiaDefault,
            EntidadeSupervisionavelETL entidadeSupervisionavelETL) {
        EntidadeSupervisionavel novaEntidadeSupervisionavel = new EntidadeSupervisionavel();
        novaEntidadeSupervisionavel.setConglomeradoOuCnpj(entidadeSupervisionavelETL.getCnpj());
        novaEntidadeSupervisionavel.setNome(entidadeSupervisionavelETL.getNome());
        novaEntidadeSupervisionavel.setPorte(entidadeSupervisionavelETL.getPorte());
        novaEntidadeSupervisionavel.setSegmento(entidadeSupervisionavelETL.getSegmento());
        novaEntidadeSupervisionavel.setLocalizacao(entidadeSupervisionavelETL.getLocalizacao());
        novaEntidadeSupervisionavel.setDataInclusao(DataUtil.getDateTimeAtual().toDate());
        novaEntidadeSupervisionavel.setMetodologia(metodologiaDefault);
        novaEntidadeSupervisionavel.setPrioridade(entidadeSupervisionavelETL.getPrioridade());
        novaEntidadeSupervisionavel.setPertenceSrc(entidadeSupervisionavelETL.getPertenceSrc());
        entidadeSupervisionavelDAO.save(novaEntidadeSupervisionavel);
        return novaEntidadeSupervisionavel;
    }

    public String retornarQuatidadeAnosCiclo(int pkEntidade) {
        EntidadeSupervisionavel enti =
                EntidadeSupervisionavelMediator.get().buscarEntidadeSupervisionavelPorPK(pkEntidade);
        return enti.getQuantidadeAnosPrevisaoCorec() == null ? "" : enti.getQuantidadeAnosPrevisaoCorec().toString();
    }

    public String possuiBloqueioES(Ciclo ciclo) {
        PerfilAcessoEnum perfilAcesso = obterPerfilAcessoValido();
        if (BloqueioESMediator.get().isESBloqueada(ciclo.getEntidadeSupervisionavel().getConglomeradoOuCnpj())
                && (perfilAcesso.equals(PerfilAcessoEnum.CONSULTA_NAO_BLOQUEADOS) || perfilAcesso
                        .equals(PerfilAcessoEnum.CONSULTA_RESUMO_NAO_BLOQUEADOS))) {
            PerfilRisco perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());
            LogOperacaoMediator.get().gerarLogDetalhamento(ciclo.getEntidadeSupervisionavel(), perfilRiscoAtual,
                    montarTrilhaLog());
            return "Para informações sobre este Perfil de Risco, consulte o Supervisor responsável pela ES.";
        }
        return "";
    }

    private PerfilAcessoEnum obterPerfilAcessoValido() {
        PerfilAcessoEnum retorno = null;
        if (RegraPerfilAcessoMediator.get().isAcessoPermitido(PerfilAcessoEnum.CONSULTA_TUDO)) {
            retorno = PerfilAcessoEnum.CONSULTA_TUDO;
        } else if (RegraPerfilAcessoMediator.get().isAcessoPermitido(PerfilAcessoEnum.CONSULTA_NAO_BLOQUEADOS)) {
            retorno = PerfilAcessoEnum.CONSULTA_NAO_BLOQUEADOS;
        } else {
            retorno = PerfilAcessoEnum.CONSULTA_RESUMO_NAO_BLOQUEADOS;
        }
        return retorno;
    }

    public String[] montarTrilhaLog() {
        String[] dados = new String[3];
        dados[0] = "Início-Painel de consulta-Perfil de risco";
        dados[1] = "APSFW0202";
        dados[2] = "Perfil de risco (ACESSO NEGADO)";
        return dados;
    }

    public boolean consultaEsSemCiclo(Integer pkEnt) {
        Ciclo ciclo = CicloMediator.get().consultarUltimoCicloEmAndamentoCorecES(pkEnt);
        return ciclo == null ? false : true;
    }

    public void validarDataCorecPrevisto(EntidadeSupervisionavel ent, ConsultaEntidadeSupervisionavelVO consulta) {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
        Ciclo ciclo = CicloMediator.get().consultarUltimoCicloEmAndamentoCorecES(ent.getPk());
        if (ciclo != null) {
            SisapsUtil.validarDataInvalida(consulta.getDataCorec(), "Corec previsto",
                    Constantes.FORMATO_DATA_COM_BARRAS, erros);

            if (consulta.getDataCorec() != null) {
                if (erros.isEmpty()) {
                    LocalDate date =
                            DateTimeFormat.forPattern("dd/MM/yyyy").parseLocalDate(consulta.getDataCorec().toString());
                    LocalDate dataAtual = DataUtil.getDateTimeAtual().toLocalDate();
                    if (date.isBefore(dataAtual) && !date.isEqual(dataAtual)) {
                        SisapsUtil.adicionarErro(erros, new ErrorMessage(
                                "O campo 'Corec previsto' deve ser maior ou igual à data corrente."));
                    }
                    consulta.setDataPrevistaCorec(date);
                }
            }
        }
        SisapsUtil.lancarNegocioException(erros);
    }

    @Transactional
    public String salvarAlteracaoGestaoES(EntidadeSupervisionavel entidade) {
        Ciclo ciclo = CicloMediator.get().consultarUltimoCicloEmAndamentoCorecES(entidade.getPk());
        EntidadeSupervisionavel entidadeDB = buscarEntidadeSupervisionavelPorPK(entidade.getPk());
        alterarPrevisaoCorec(entidade, entidadeDB, ciclo);
        if (ciclo != null && isPrioridadeAlterada(entidade, entidadeDB)) {
            EntidadeSupervisionavel novaEntidade = duplicarEntidade(entidade);
            VersaoPerfilRisco versaoPerfilRisco = 
                    PerfilRiscoMediator.get().gerarNovaVersaoPerfilRisco(
                            ciclo, entidadeDB, TipoObjetoVersionadorEnum.ENTIDADE_SUPERVISIONAVEL);
            novaEntidade.setVersaoPerfilRisco(versaoPerfilRisco);
            entidadeSupervisionavelDAO.save(novaEntidade);
            ciclo.setEntidadeSupervisionavel(novaEntidade);
            cicloDAO.update(ciclo);
            cicloDAO.flush();
            eventoConsolidadoMediator.incluirEventoPerfilDeRisco(ciclo, TipoSubEventoPerfilRiscoSRC.PRIORIDADE_ES);
            return "ES e perfil de risco atualizados com sucesso.";
        } else {
            entidadeSupervisionavelDAO.merge(entidade);
            return "ES alterada com sucesso.";
        }
    }

    private void alterarPrevisaoCorec(EntidadeSupervisionavel entidade, EntidadeSupervisionavel entidadeDB, Ciclo ciclo) {
        if (ciclo != null && isQtdAnosPrevisaoCorecAlterada(entidade, entidadeDB)) {
            DateTime dataPrevisaoCorec = new DateTime(ciclo.getDataInicio().getTime());
            dataPrevisaoCorec = dataPrevisaoCorec.plusYears(entidade.getQuantidadeAnosPrevisaoCorec());
            ciclo.setDataPrevisaoCorec(dataPrevisaoCorec.toDate());
            cicloDAO.update(ciclo);
            
            AgendaCorec agenda = AgendaCorecMediator.get().buscarAgendaCorecPorCiclo(ciclo.getPk());
            if (agenda == null) {
                agenda = new AgendaCorec();
                agenda.setCiclo(ciclo);
            } else {
                AgendaCorecMediator.get().removerEmailDisponibilidadeParticipantes(agenda);
            }
            agenda.setDataEnvioApresentacao(null);
            agenda.setDataEnvioDisponibilidade(null);
            
            ObservacaoAgendaCorec observacao = new ObservacaoAgendaCorec();
            observacao.setAgenda(agenda);
            observacao.setDescricao("Alteração de ciclo pelo administrador.");

            agenda.addObservacao(observacao);
            AgendaCorecMediator.get().saveOrUpdate(agenda);
        }
    }
    
    public boolean exibirNomeBotaoSalvar(EntidadeSupervisionavel entidade) {
        Ciclo ciclo = CicloMediator.get().consultarUltimoCicloEmAndamentoCorecES(entidade.getPk());
        EntidadeSupervisionavel entidadeDB = entidadeSupervisionavelDAO.load(entidade.getPk());
        return ciclo == null || !isPrioridadeAlterada(entidade, entidadeDB);
    }

    private boolean isQtdAnosPrevisaoCorecAlterada(EntidadeSupervisionavel entidade, EntidadeSupervisionavel entidadeDB) {
        return isQtdAnosPrevisaoCorecNulasDiferentes(entidade, entidadeDB)
                || isQtdAnosPrevisaoCorecNaoNulasDiferentes(entidade, entidadeDB);
    }

    private boolean isPrioridadeAlterada(EntidadeSupervisionavel entidade, EntidadeSupervisionavel entidadeDB) {
        return isPrioridadesNulasDiferentes(entidade, entidadeDB) 
                || isPrioridadesNaoNulasDiferentes(entidade, entidadeDB);
    }
    
    private boolean isQtdAnosPrevisaoCorecNulasDiferentes(EntidadeSupervisionavel entidade,
            EntidadeSupervisionavel entidadeDB) {
        return (entidadeDB.getQuantidadeAnosPrevisaoCorec() == null 
                && entidade.getQuantidadeAnosPrevisaoCorec() != null) 
                || (entidadeDB.getQuantidadeAnosPrevisaoCorec() != null 
                && entidade.getQuantidadeAnosPrevisaoCorec() == null);
    }

    private boolean isQtdAnosPrevisaoCorecNaoNulasDiferentes(EntidadeSupervisionavel entidade,
            EntidadeSupervisionavel entidadeDB) {
        return !entidadeDB.getQuantidadeAnosPrevisaoCorec().equals(entidade.getQuantidadeAnosPrevisaoCorec());
    }
    
    private boolean isPrioridadesNulasDiferentes(EntidadeSupervisionavel entidade, EntidadeSupervisionavel entidadeDB) {
        return (entidadeDB.getPrioridade() == null && entidade.getPrioridade() != null) 
                || (entidadeDB.getPrioridade() != null && entidade.getPrioridade() == null);
    }

    private boolean isPrioridadesNaoNulasDiferentes(EntidadeSupervisionavel entidade, EntidadeSupervisionavel entidadeDB) {
        return !entidadeDB.getPrioridade().getPk().equals(entidade.getPrioridade().getPk());
    }

    private EntidadeSupervisionavel duplicarEntidade(EntidadeSupervisionavel ent) {
        EntidadeSupervisionavel entidade = new EntidadeSupervisionavel();
        entidade.setNome(ent.getNome());
        entidade.setPorte(ent.getPorte());
        entidade.setSegmento(ent.getSegmento());
        entidade.setConglomeradoOuCnpj(ent.getConglomeradoOuCnpj());
        entidade.setPertenceSrc(ent.getPertenceSrc());
        entidade.setPrioridade(ent.getPrioridade());
        entidade.setLocalizacao(ent.getLocalizacao());
        entidade.setQuantidadeAnosPrevisaoCorec(ent.getQuantidadeAnosPrevisaoCorec());
        entidade.setDataInclusao(ent.getDataInclusao());
        entidade.setMetodologia(ent.getMetodologia());
        return entidade;
    }
    
    public void evict(EntidadeSupervisionavel entidade) {
        entidadeSupervisionavelDAO.evict(entidade);
    }

    public SimNaoEnum pertenceAoSRC(Consolidado consolidado, List<String> cnpjEsPerfilRisco) {
        SimNaoEnum retorno = SimNaoEnum.NAO;

        List<String> listaCnpjPorPerfilRisco = new ArrayList<String>();

        if (consolidado != null && consolidado.getCnpjEsDefault() != null && !consolidado.getCnpjEsDefault().isEmpty()) {
            String cnpjFormatado = limparString(consolidado.getCnpjEsDefault());
            EntidadeSupervisionavelVO esDefault =
                    entidadeSupervisionavelDAO
                            .buscarEntidadeSRCPorCnpj(Integer.valueOf(cnpjFormatado));
            if (esDefault == null) {
                retorno = essConsolidado(consolidado, retorno, listaCnpjPorPerfilRisco);
            } else {
                CicloVO ciclo = cicloDAO.buscar(esDefault.getPk());
                if (ciclo == null) {
                    retorno = essConsolidado(consolidado, retorno, listaCnpjPorPerfilRisco);
                } else {
                    retorno = SimNaoEnum.SIM;
                    listaCnpjPorPerfilRisco.add(esDefault.getConglomeradoOuCnpj());
                }
            }
        }
        if (retorno.booleanValue()) {
            cnpjEsPerfilRisco.addAll(listaCnpjPorPerfilRisco);
        }
        return retorno;
    }

    public SimNaoEnum essConsolidado(Consolidado consolidado, SimNaoEnum retorno,
            List<String> listaCnpjPorPerfilRisco) {
        if (consolidado != null && consolidado.getEntidadesUnicad() != null) {
            for (EntidadeUnicad es : consolidado.getEntidadesUnicad()) {
                String cnpjFormatado = limparString(es.getCnpjConglomerado());
                EntidadeSupervisionavelVO esConsolidado =
                        entidadeSupervisionavelDAO.buscarEntidadeSRCPorCnpj(Integer.valueOf(cnpjFormatado));
                if (esConsolidado != null) {
                    CicloVO ciclo2 = cicloDAO.buscar(esConsolidado.getPk());
                    if (ciclo2 == null) {
                        retorno = SimNaoEnum.NAO;
                    } else {
                        listaCnpjPorPerfilRisco.add(es.getCnpjConglomerado());
                    }
                }
            }
        }
        if (!listaCnpjPorPerfilRisco.isEmpty()) {
            if (listaCnpjPorPerfilRisco.size() > 1) {
                retorno = SimNaoEnum.NAO;
            } else {
                retorno = SimNaoEnum.SIM;
            }
        }
        return retorno;
    }

    
    private String limparString(String cnpjEs) {
        String cnpjFormatado = cnpjEs;
        cnpjFormatado = cnpjFormatado.replace(".", "");
        cnpjFormatado = cnpjFormatado.replace(",", "");
        cnpjFormatado = cnpjFormatado.replace("'", "");
        cnpjFormatado = cnpjFormatado.replace(";", "");
        cnpjFormatado = cnpjFormatado.replace("C", "");
        cnpjFormatado = cnpjFormatado.trim();
        return cnpjFormatado;
    }

    public boolean possuiPermissaoGeracaoEventos(EntidadeUnicad entidadeUnicad) {
        return (entidadeUnicad != null && entidadeUnicad.getConsolidado() != null)
                && (entidadeUnicad.getCnpjConglomerado().equals(entidadeUnicad.getConsolidado().getCnpjEsDefault())
                        || essConsolidado(entidadeUnicad.getConsolidado(), SimNaoEnum.NAO, new ArrayList<String>())
                        .booleanValue());
    }
    
    public EntidadeSupervisionavelVO buscarEntidadeSRCPorCnpj(Integer codigo) {
        return entidadeSupervisionavelDAO.buscarEntidadeSRCPorCnpj(codigo);
    }
    
    public List<EntidadeSupervisionavel> buscarEssPorCNPJ(String cnpj, boolean isCicloMigrado) {
        return entidadeSupervisionavelDAO.buscarEssPorCNPJ(cnpj, isCicloMigrado);
    }
}
