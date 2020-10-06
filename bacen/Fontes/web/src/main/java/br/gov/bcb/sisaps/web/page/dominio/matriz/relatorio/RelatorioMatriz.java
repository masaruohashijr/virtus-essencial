package br.gov.bcb.sisaps.web.page.dominio.matriz.relatorio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControleExterno;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.dominio.NotaMatriz;
import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.ParametroNota;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.mediator.AjusteCorecMediator;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleExternoMediator;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CelulaRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.LinhaMatrizMediator;
import br.gov.bcb.sisaps.src.mediator.MatrizCicloMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.VersaoPerfilRiscoMediator;
import br.gov.bcb.sisaps.src.vo.LinhaMatrizVO;
import br.gov.bcb.sisaps.src.vo.LinhaNotasMatrizVO;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;

public class RelatorioMatriz implements Serializable {

    public static final String NOME_ARQUIVO_RELATORIO = "RelatorioMatriz";

    private final PerfilRisco perfilRisco;
    private List<LinhaMatrizVO> linhaMatrizVO;
    private List<LinhaNotasMatrizVO> notaResiduais;
    private String notaCalculada;
    private String notaCalculadaFinal;
    private Matriz matriz;
    private List<LinhaNotasMatrizVO> mediaResiduais;
    private List<VersaoPerfilRisco> versoesPerfilRiscoARCs;
    private List<CelulaRiscoControle> listaCelula;
    private EntidadeSupervisionavel entidadeSupervisionavel;
    private NotaMatriz notaMatriz;
    private PerfilAcessoEnum perfilMenu;
    private ParametroNota notaAjusteCorec;
    private AvaliacaoRiscoControleExterno arcExterno;


    public RelatorioMatriz(PerfilRisco perfilRisco, PerfilAcessoEnum perfilMenu) {
        this.perfilRisco = perfilRisco;
        this.perfilMenu = perfilMenu;
        obterDados();
    }

    private void obterDados() {
        PerfilRisco perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(perfilRisco.getCiclo().getPk());
        entidadeSupervisionavel = perfilRisco.getCiclo().getEntidadeSupervisionavel();
        List<Atividade> listaAtividade = new LinkedList<Atividade>();
        notaResiduais = new LinkedList<LinhaNotasMatrizVO>();
        mediaResiduais = new LinkedList<LinhaNotasMatrizVO>();
        matriz = PerfilRiscoMediator.get().getMatrizAtualPerfilRisco(perfilRisco);
        versoesPerfilRiscoARCs =
                VersaoPerfilRiscoMediator.get().buscarVersoesPerfilRisco(perfilRisco.getPk(),
                        TipoObjetoVersionadorEnum.CELULA_RISCO_CONTROLE);
        linhaMatrizVO = new ArrayList<LinhaMatrizVO>();
        linhaMatrizVO.addAll(LinhaMatrizMediator.get().consultar(matriz, false, listaAtividade)); // consultar atividades de unidades de negócio
        linhaMatrizVO.addAll(LinhaMatrizMediator.get().montarListaVOAtividadeNegocio(matriz, listaAtividade)); // consultar atividades sem unidades
        linhaMatrizVO.addAll(LinhaMatrizMediator.get().consultar(matriz, true, listaAtividade)); // consultar atividades corporativas
        listaCelula = CelulaRiscoControleMediator.get().buscarParametroDaMatriz(listaAtividade, versoesPerfilRiscoARCs);
        LinhaMatrizMediator.get().montarListaNotasEMediasResiduais(matriz, versoesPerfilRiscoARCs, true,
                getParametroGrupo(), notaResiduais, mediaResiduais, listaCelula, perfilMenu, perfilRisco);

        notaCalculada =
                formatarNota(MatrizCicloMediator.get().notaCalculada(matriz, listaCelula, versoesPerfilRiscoARCs, true,
                        perfilMenu, perfilRisco));

        notaMatriz = PerfilRiscoMediator.get().getNotaMatrizPerfilRisco(perfilRisco);

        notaAjusteCorec = AjusteCorecMediator.get().notaAjustadaCorecMatriz(perfilRisco, perfilRisco.getCiclo(), 
                perfilMenu, perfilRisco.getPk().equals(perfilRiscoAtual.getPk()));

        AvaliacaoRiscoControle arc =
                AvaliacaoRiscoControleMediator.get().buscarArcExternoPorPerfilRisco(perfilRisco.getPk());
        if (arc != null) {
            setArcExterno(AvaliacaoRiscoControleExternoMediator.get().buscarArcExterno(arc.getPk()));
        }

        setNotaCalculadaFinal(MatrizCicloMediator.get().notaCalculadaFinal(matriz, listaCelula, versoesPerfilRiscoARCs,
                true, perfilMenu, perfilRisco, arc));
    }

    private String formatarNota(String nota) {
        if (nota == null || nota.isEmpty()) {
            nota = Constantes.ASTERISCO_A;
        }
        nota = nota.replace('.', ',');

        return nota;
    }

    public List<ParametroGrupoRiscoControle> getParametroGrupo() {

        List<ParametroGrupoRiscoControle> listaParametro = new ArrayList<ParametroGrupoRiscoControle>();
        for (CelulaRiscoControle celula : listaCelula) {
            if (!listaParametro.contains(celula.getParametroGrupoRiscoControle())) {
                listaParametro.add(celula.getParametroGrupoRiscoControle());
            }
        }
        return listaParametro;

    }

    public List<CelulaRiscoControle> getCelulasPorGrupo(ParametroGrupoRiscoControle grupo) {
        return CelulaRiscoControleMediator.get().getCelulasPorGrupo(listaCelula, grupo);
    }

    public CelulaRiscoControle getCelulaPelaAtividadeEGrupo(ParametroGrupoRiscoControle grupo, Integer pkAtividade) {

        for (CelulaRiscoControle celula : listaCelula) {
            if (celula.getParametroGrupoRiscoControle().equals(grupo)
                    && celula.getAtividade().getPk().equals(pkAtividade)) {
                return celula;
            }
        }

        return null;

    }

    public String getNotaAjustada() {
        Metodologia metodologia = this.getPerfilRisco().getCiclo().getMetodologia();
        if (this.getNotaAjusteCorec() != null) {
            return metodologia.getIsCalculoMedia() == null || metodologia.getIsCalculoMedia().equals(SimNaoEnum.NAO) ? this
                    .getNotaAjusteCorec().getDescricaoValor() : this.getNotaAjusteCorec().getDescricao();
        } else if (this.getNotaMatriz() != null) {
            NotaMatriz notaMatriz = this.getNotaMatriz();
            return notaMatriz.getNotaFinalMatriz() == null ? "" : metodologia.getIsCalculoMedia() == null
                    || metodologia.getIsCalculoMedia().equals(SimNaoEnum.NAO) ? notaMatriz.getNotaFinalMatriz()
                    .getDescricaoValor() : notaMatriz.getNotaFinalMatriz().getDescricao();
        }
        return "";
    }

    public boolean possuiNotaAjustadaCorec() {
        return this.getNotaAjusteCorec() != null;
    }

    public boolean possuiNotaAjustada() {
        return possuiNotaAjustadaCorec()
                || (this.getNotaMatriz() != null && this.getNotaMatriz().getNotaFinalMatriz() != null);
    }

    public PerfilRisco getPerfilRisco() {
        return perfilRisco;
    }

    public List<LinhaMatrizVO> getLinhaMatrizVO() {
        return linhaMatrizVO;
    }

    public List<LinhaNotasMatrizVO> getNotaResiduais() {
        return notaResiduais;
    }

    public String getNotaCalculada() {
        return notaCalculada;
    }

    public Matriz getMatriz() {
        return matriz;
    }

    public List<LinhaNotasMatrizVO> getMediaResiduais() {
        return mediaResiduais;
    }

    public List<VersaoPerfilRisco> getVersoesPerfilRiscoARCs() {
        return versoesPerfilRiscoARCs;
    }

    public EntidadeSupervisionavel getEntidadeSupervisionavel() {
        return entidadeSupervisionavel;
    }

    public NotaMatriz getNotaMatriz() {
        return notaMatriz;
    }

    public ParametroNota getNotaAjusteCorec() {
        return notaAjusteCorec;
    }

    public void setNotaAjusteCorec(ParametroNota notaAjusteCorec) {
        this.notaAjusteCorec = notaAjusteCorec;
    }

    public PerfilAcessoEnum getPerfilMenu() {
        return perfilMenu;
    }

    public void setPerfilMenu(PerfilAcessoEnum perfilMenu) {
        this.perfilMenu = perfilMenu;
    }

    public AvaliacaoRiscoControleExterno getArcExterno() {
        return arcExterno;
    }

    public void setArcExterno(AvaliacaoRiscoControleExterno arcExterno) {
        this.arcExterno = arcExterno;
    }

    public String getNotaCalculadaFinal() {
        return notaCalculadaFinal;
    }

    public void setNotaCalculadaFinal(String notaCalculadaFinal) {
        this.notaCalculadaFinal = notaCalculadaFinal;
    }

}
