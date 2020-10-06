package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.painel;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.DateTime;

import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.mediator.AtividadeCicloMediator;
import br.gov.bcb.sisaps.src.mediator.EntidadeSupervisionavelMediator;
import br.gov.bcb.sisaps.src.mediator.VersaoPerfilRiscoMediator;
import br.gov.bcb.sisaps.src.vo.AtividadeCicloVO;
import br.gov.bcb.sisaps.src.vo.ConsultaAtividadeCicloVO;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.ProviderOrdenadoPaginado;
import br.gov.bcb.sisaps.web.page.componentes.grupo.GrupoExpansivel;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Coluna;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Configuracao;
import br.gov.bcb.sisaps.web.page.componentes.tabela.Tabela;
import br.gov.bcb.sisaps.web.page.componentes.util.ConstantesWeb;

@SuppressWarnings("serial")
public class PainelAtividadesCiclo extends PainelSisAps {
    
    private static final String TITULO = "titulo";

    private static final String STYLE_GRUPO_EXPANSIVEL = 
            "padding: 0px; padding-top: 2px; padding-left: 6px; padding-right: 6px";

    private static final String PROP_CODIGO = "codigo";

    private static final String WIDTH_20 = "width:20%;";
    
    @SpringBean
    private AtividadeCicloMediator atividadeCicloMediator;
    @SpringBean
    private VersaoPerfilRiscoMediator versaoPerfilRiscoMediator;
    
    private PerfilRisco perfilRisco;
    
    private ConsultaAtividadeCicloVO consultaAnoAtual;
    
    private ConsultaAtividadeCicloVO consultaAnoAnterior;

    private GrupoExpansivel grupoExpansivelAnoAnterior;

    private GrupoExpansivel grupoExpansivelAnoAtual;

    public PainelAtividadesCiclo(String id, PerfilRisco perfilRisco) {
        super(id);
        setMarkupId(id);
        this.perfilRisco = perfilRisco;
    }
    
    @Override
    protected void onInitialize() {
        super.onInitialize();
        addComponents();
    }

    private void addComponents() {
        DateTime dateTime = DataUtil.getDateTimeAtual();
        
        addTabelaAnoAnterior(dateTime);
        
        addTabelaAnoAtual(dateTime);
    }

    private void addTabelaAnoAnterior(DateTime dateTime) {
        Tabela<AtividadeCicloVO> tabelaAnoAnterior = addTabela(true);
        grupoExpansivelAnoAnterior = new GrupoExpansivel("grupoExpansivelAtividadesAnoAnterior", 
                String.valueOf(dateTime.getYear() - 1), false, new Component[] {tabelaAnoAnterior}) {
            @Override
            public String getMarkupIdControle() {
                return "grupo_controle_ExpansivelAtividadesAnoAnterior";
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                getDivGrupo().addOrReplace(new Label(TITULO, getTitulo()));
            }
        };
        grupoExpansivelAnoAnterior.setMarkupId(grupoExpansivelAnoAnterior.getId());
        grupoExpansivelAnoAnterior.setCssTitulo(ConstantesWeb.CSS_FUNDO_PADRAO_A_ESCURO3);
        grupoExpansivelAnoAnterior.addStyleGrupo(STYLE_GRUPO_EXPANSIVEL);
        addOrReplace(grupoExpansivelAnoAnterior);
    }
    
    private void addTabelaAnoAtual(DateTime dateTime) {
        Tabela<AtividadeCicloVO> tabelaAnoAtual = addTabela(false);
        grupoExpansivelAnoAtual = new GrupoExpansivel("grupoExpansivelAtividadesAnoAtual", 
                String.valueOf(dateTime.getYear()), false, new Component[] {tabelaAnoAtual}) {
            @Override
            public String getMarkupIdControle() {
                return "grupo_controle_ExpansivelAtividadesAnoAtual";
            }
            
            @Override
            protected void onConfigure() {
                super.onConfigure();
                getDivGrupo().addOrReplace(new Label(TITULO, getTitulo()));
            }
        };
        grupoExpansivelAnoAtual.setCssTitulo(ConstantesWeb.CSS_FUNDO_PADRAO_A_ESCURO3);
        grupoExpansivelAnoAtual.addStyleGrupo(STYLE_GRUPO_EXPANSIVEL);
        addOrReplace(grupoExpansivelAnoAtual);
    }

    private Tabela<AtividadeCicloVO> addTabela(boolean isTabelaAnoAnterior) {
        String idTabela = null;
        if (isTabelaAnoAnterior) {
            idTabela = "tabelaAnoAnterior";
        } else {
            idTabela = "tabelaAnoAtual";
        }
        // configuração
        Configuracao cfg = obterConfiguracao(isTabelaAnoAnterior);
        // colunas da tabela
        List<Coluna<AtividadeCicloVO>> colunas = obterColunas();
        // model da consulta
        IModel<ConsultaAtividadeCicloVO> modelConsulta = obterModelConsulta(isTabelaAnoAnterior);
        // provider
        ProviderOrdenadoPaginado<AtividadeCicloVO, Integer, ConsultaAtividadeCicloVO> provider = 
                obterProvider(modelConsulta);
        // tabela
        Tabela<AtividadeCicloVO> tabela = 
                new Tabela<AtividadeCicloVO>(idTabela, cfg, colunas, provider, true) {
            @Override
            protected void onConfigure() {
                // TODO Auto-generated method stub
                super.onConfigure();
            }
        };
        addOrReplace(tabela);
        return tabela;
    }


    private Configuracao obterConfiguracao(boolean isTabelaAnoAnterior) {
        String idDados = null;
        if (isTabelaAnoAnterior) {
            idDados = "idDadosAnoAnterior";
        } else {
            idDados = "idDadosAnoAtual";
        }
        Configuracao cfg = new Configuracao("idTitulo", idDados);
        cfg.setMensagemVazio(Model.of("Nenhum registro que satisfaça os critérios de pesquisa foi encontrado."));
        cfg.setExibirPaginador(false);
        cfg.setExibirTitulo(false);
        return cfg;
    }

    private List<Coluna<AtividadeCicloVO>> obterColunas() {
        List<Coluna<AtividadeCicloVO>> colunas = new LinkedList<Coluna<AtividadeCicloVO>>();

        colunas.add(new Coluna<AtividadeCicloVO>().setCabecalho("Ação").setPropriedade(PROP_CODIGO)
                .setOrdenar(true).setEstiloCabecalho(WIDTH_20));

        colunas.add(new Coluna<AtividadeCicloVO>().setCabecalho("Data-base").setPropriedade("dataBase")
                .setPropriedadeTela("dataBaseFormatada").setOrdenar(true).setEstiloCabecalho(WIDTH_20));

        colunas.add(new Coluna<AtividadeCicloVO>().setCabecalho("Descrição").setPropriedade("descricao")
                .setOrdenar(true).setEstiloCabecalho(WIDTH_20));

        colunas.add(new Coluna<AtividadeCicloVO>().setCabecalho("Situação").setPropriedade("situacao")
                .setOrdenar(true).setEstiloCabecalho(WIDTH_20));
        return colunas;
    }

    private IModel<ConsultaAtividadeCicloVO> obterModelConsulta(final boolean isTabelaAnoAnterior) {
        final ConsultaAtividadeCicloVO consulta = new ConsultaAtividadeCicloVO();
        EntidadeSupervisionavel entidade = EntidadeSupervisionavelMediator.get().buscarEntidadeSupervisionavelPorPK(
                perfilRisco.getCiclo().getEntidadeSupervisionavel().getPk());
        consulta.setCnpjES(entidade.getConglomeradoOuCnpj());
        if (isTabelaAnoAnterior) {
            consulta.setAno((short) DataUtil.getDateTimeAtual().minusYears(1).getYear());
        } else {
            consulta.setAno((short) DataUtil.getDateTimeAtual().getYear());
        }
        consulta.setVersoesPerfilRisco(versaoPerfilRiscoMediator.buscarVersoesPerfilRisco(
                perfilRisco.getPk(), TipoObjetoVersionadorEnum.ATIVIDADE_CICLO));
        IModel<ConsultaAtividadeCicloVO> modelConsulta = new AbstractReadOnlyModel<ConsultaAtividadeCicloVO>() {
            @Override
            public ConsultaAtividadeCicloVO getObject() {
                if (isTabelaAnoAnterior) {
                    return consultaAnoAnterior;
                } else {
                    return consultaAnoAtual;
                }
            }
        };
        if (isTabelaAnoAnterior) {
            consultaAnoAnterior = consulta;
        } else {
            consultaAnoAtual = consulta;
        }
        return modelConsulta;
    }

    private ProviderOrdenadoPaginado<AtividadeCicloVO, Integer, ConsultaAtividadeCicloVO> obterProvider(
            IModel<ConsultaAtividadeCicloVO> modelConsulta) {
        ProviderOrdenadoPaginado<AtividadeCicloVO, Integer, ConsultaAtividadeCicloVO> provider =
                new ProviderOrdenadoPaginado<AtividadeCicloVO, Integer, ConsultaAtividadeCicloVO>(PROP_CODIGO,
                        SortOrder.ASCENDING, atividadeCicloMediator, modelConsulta);
        return provider;
    }
    
    public void setPerfilRiscoEModelConsulta(PerfilRisco perfilRisco, boolean isPerfilRiscoAtual) {
        this.perfilRisco = perfilRisco;
        setDadosConsultaAnoAnterior(isPerfilRiscoAtual);
        setDadosConsultaAnoAtual(isPerfilRiscoAtual);
    }

    private void setDadosConsultaAnoAnterior(boolean isPerfilRiscoAtual) {
        Short ano = null;
        if (isPerfilRiscoAtual) {
            ano = (short) DataUtil.getDateTimeAtual().minusYears(1).getYear();
        } else {
            ano = (short) (perfilRisco.getDataCriacao().getYear() - 1);
        }
        consultaAnoAnterior.setVersoesPerfilRisco(
                versaoPerfilRiscoMediator.buscarVersoesPerfilRisco(
                        perfilRisco.getPk(), TipoObjetoVersionadorEnum.ATIVIDADE_CICLO));
        consultaAnoAnterior.setAno(ano);
        grupoExpansivelAnoAnterior.setTitulo(Integer.toString(ano));
    }

    private void setDadosConsultaAnoAtual(boolean isPerfilRiscoAtual) {
        Short ano = null;
        if (isPerfilRiscoAtual) {
            ano = (short) DataUtil.getDateTimeAtual().getYear();
        } else {
            ano = (short) (perfilRisco.getDataCriacao().getYear());
        }
        consultaAnoAtual.setVersoesPerfilRisco(
                versaoPerfilRiscoMediator.buscarVersoesPerfilRisco(
                        perfilRisco.getPk(), TipoObjetoVersionadorEnum.ATIVIDADE_CICLO));
        consultaAnoAtual.setAno(ano);
        grupoExpansivelAnoAtual.setTitulo(Integer.toString(ano));
    }
    
}
