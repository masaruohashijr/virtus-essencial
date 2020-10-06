package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.painel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.SinteseDeRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoParametroGrupoRiscoControleEnum;
import br.gov.bcb.sisaps.src.mediator.CelulaRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroGrupoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.SinteseDeRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.VersaoPerfilRiscoMediator;
import br.gov.bcb.sisaps.src.vo.CelulaRiscoControleVO;
import br.gov.bcb.sisaps.src.vo.LinhaNotasMatrizVO;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.web.page.PainelSisAps;
import br.gov.bcb.sisaps.web.page.componentes.grupo.GrupoExpansivelCampoValor;

@SuppressWarnings("serial")
public class PainelSinteseDeRisco extends PainelSisAps {

    private static final String LINHA = "linha";

    private static final String GRUPO_EXPANSIVEL = "grupoExpansivel";
    
    private String titulo; 

    @SpringBean
    private PerfilRiscoMediator perfilRiscoMediator;
    @SpringBean
    private VersaoPerfilRiscoMediator versaoPerfilRiscoMediator;
    @SpringBean
    private ParametroGrupoRiscoControleMediator parametroGrupoRiscoControleMediator;
    @SpringBean
    private SinteseDeRiscoMediator sinteseDeRiscoMediator;

    private PerfilRisco perfilRisco;
    private Matriz matriz;
    private List<ParametroGrupoRiscoControle> gruposRiscoDaMatrizESinteseObrigatoria =
            new ArrayList<ParametroGrupoRiscoControle>();
    private List<VersaoPerfilRisco> versoesSintesesMatriz;

    public PainelSinteseDeRisco(String id, PerfilRisco perfilRisco) {
        super(id);
        setMarkupId(id);
        this.perfilRisco = perfilRisco;

    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        this.matriz = perfilRiscoMediator.getMatrizAtualPerfilRisco(perfilRisco);
        this.versoesSintesesMatriz = null;
        if (matriz != null) {
            this.matriz.setCiclo(CicloMediator.get().buscarCicloPorPK(perfilRisco.getCiclo().getPk()));
            List<Integer> idsParametrosGrupoRiscoMatriz =
                    parametroGrupoRiscoControleMediator.buscarIdsGruposRiscoDaMatriz(matriz);

            gruposRiscoDaMatrizESinteseObrigatoria =
                    parametroGrupoRiscoControleMediator
                            .buscarGruposRiscoDaMatrizESinteseObrigatoria(idsParametrosGrupoRiscoMatriz);
            
            if (this.matriz.getPercentualGovernancaCorporativoInt() > 0) {
                for (ParametroGrupoRiscoControle prcGrupoRiscoControle 
                        : this.matriz.getCiclo().getMetodologia().getParametrosGrupoRiscoControle()) {
                    if (prcGrupoRiscoControle.getTipoGrupo() == TipoParametroGrupoRiscoControleEnum.EXTERNO
                            && !gruposRiscoDaMatrizESinteseObrigatoria.contains(prcGrupoRiscoControle)) {
                        gruposRiscoDaMatrizESinteseObrigatoria.add(prcGrupoRiscoControle);
                        break;
                    }
                } 
            }
            versoesSintesesMatriz =
                    versaoPerfilRiscoMediator.buscarVersoesPerfilRisco(perfilRisco.getPk(),
                            TipoObjetoVersionadorEnum.SINTESE_RISCO);

        }
        setVisibilityAllowed(matriz != null);
        addComponents();
    }

    private void addComponents() {
        
        this.setTitulo(this.matriz != null 
                && this.matriz.getPercentualGovernancaCorporativoInt() > 0 ? 
                        "Síntese de riscos e governança corporativa" : "Síntese de riscos");
        Label tituloSintese = new Label("idTituloSintese", this.titulo);
        addOrReplace(tituloSintese);
        
        addOrReplace(new ListView<ParametroGrupoRiscoControle>("listaSintesesDeRisco",
                gruposRiscoDaMatrizESinteseObrigatoria) {
            @Override
            protected void populateItem(ListItem<ParametroGrupoRiscoControle> item) {
                addGrupoSinteseDeRisco(item);
            }
        });
    }

    private void addGrupoSinteseDeRisco(ListItem<ParametroGrupoRiscoControle> item) {
        ParametroGrupoRiscoControle grupoRisco = item.getModelObject();
        SinteseDeRisco sinteseDeRisco = null;
        if (CollectionUtils.isNotEmpty(versoesSintesesMatriz)) {
            sinteseDeRisco =
                    sinteseDeRiscoMediator.getUltimaSinteseParametroGrupoRisco(grupoRisco, perfilRisco.getCiclo(),
                            versoesSintesesMatriz);
        }
        WebMarkupContainer linhaSituacao = new WebMarkupContainer(LINHA);
        linhaSituacao.setMarkupId(linhaSituacao.getId());
        addOrReplace(linhaSituacao);
        Label justificativaSituacao =
                new Label("idDescricaoSintese",
                        sinteseDeRisco == null || sinteseDeRisco.getDescricaoSintese() == null ? ""
                                : sinteseDeRisco.getDescricaoSintese());
        justificativaSituacao.setEscapeModelStrings(false);
        linhaSituacao.addOrReplace(justificativaSituacao);
        
        String valorSituacao = null;
        if (sinteseDeRisco == null || sinteseDeRisco.getRisco() == null) {
            
            List<ParametroGrupoRiscoControle> listaParamentro = new ArrayList<ParametroGrupoRiscoControle>();
            listaParamentro.add(grupoRisco);

            List<VersaoPerfilRisco> versaoPerfilRiscosCelula =
                    VersaoPerfilRiscoMediator.get().buscarVersoesPerfilRisco(perfilRisco.getPk(),
                            TipoObjetoVersionadorEnum.CELULA_RISCO_CONTROLE);
            List<CelulaRiscoControleVO> celulas =
                    CelulaRiscoControleMediator.get().buscarCelulaPorVersaoPerfilVO(versaoPerfilRiscosCelula);

            valorSituacao =
                    sinteseDeRiscoMediator.obterDescricaoRisco(celulas, matriz, versaoPerfilRiscosCelula, true,
                            listaParamentro, new LinkedList<LinhaNotasMatrizVO>(),
                            new LinkedList<LinhaNotasMatrizVO>(), getPaginaAtual().getPerfilPorPagina(), perfilRisco);
        } else {
            valorSituacao = sinteseDeRisco.getRisco().getDescricao();
        }

        String nomeDoCampo = grupoRisco.getNomeAbreviado();
        GrupoExpansivelCampoValor grupo =
                new GrupoExpansivelCampoValor(GRUPO_EXPANSIVEL, nomeDoCampo, valorSituacao, false, linhaSituacao) {
                    @Override
                    public String getMarkupIdControle() {
                        return "grupo_controle_" + SisapsUtil.criarMarkupId(getMarkupId());
                    }
                };
        grupo.setMarkupId(GRUPO_EXPANSIVEL + grupoRisco.getNomeRisco());
        linhaSituacao.setMarkupId(LINHA + SisapsUtil.criarMarkupId(grupoRisco.getNomeRisco()));
        item.addOrReplace(linhaSituacao);
        item.addOrReplace(grupo);
    }

    public void setPerfilRisco(PerfilRisco perfilRisco) {
        this.perfilRisco = perfilRisco;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

}
