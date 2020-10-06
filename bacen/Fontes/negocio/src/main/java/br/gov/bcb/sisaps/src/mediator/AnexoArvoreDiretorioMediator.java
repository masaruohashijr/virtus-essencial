package br.gov.bcb.sisaps.src.mediator;

import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import br.gov.bcb.app.stuff.exception.NegocioException;
import br.gov.bcb.app.stuff.hibernate.ObjetoPersistente;
import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.comum.bcjcifs.JCIFSHelper;
import br.gov.bcb.comum.excecoes.BCInfraException;
import br.gov.bcb.sisaps.src.dominio.Apresentacao;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ConclusaoES;
import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.Elemento;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.ItemElemento;
import br.gov.bcb.sisaps.src.dominio.PerfilAtuacaoES;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.QuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ElementoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ItemElementoAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEntidadeAnexoDocumentoEnum;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

@Service
@Scope("singleton")
public class AnexoArvoreDiretorioMediator {

    private static final String ES = "es";
    private static final String QUADROPOSICAOFINANCEIRA = "quadroposicaofinanceira";
    private static final String ITEM = "item";
    private static final String ELEMENTO2 = "elemento";
    private static final String DOCUMENTO = "documento";
    private static final String ARC = "arc";
    private static final String CICLO = "ciclo";
    private static final String APRESENTACAO = "apresentacao";
    private static final String UNDERLINE = "_";
    private static final String BARRA = "/";

    public static AnexoArvoreDiretorioMediator get() {
        return SpringUtils.get().getBean(AnexoArvoreDiretorioMediator.class);
    }

    public SmbFile obterArvoreDiretoriosItemElemento(Ciclo ciclo, EntidadeSupervisionavel es, ItemElemento itemElemento,
            String alias)
            throws SmbException, BCInfraException {
        Elemento elemento = itemElemento.getElemento();
        AvaliacaoRiscoControle arc = elemento.getAvaliacaoRiscoControle();
        
        Ciclo cicloARC = AvaliacaoRiscoControleMediator.get().buscarCicloARC(arc);

        List<String> listaSubDiretorios = new LinkedList<String>();

        montarCaminhoNomeDirEsCicloMatrizArc(listaSubDiretorios, cicloARC == null ? ciclo : cicloARC, arc);
        montarCaminhoNomeDirElementoItemDocumento(listaSubDiretorios, elemento, itemElemento);

        return criarDiretorioSmb(listaSubDiretorios, es, alias);
    }

    public SmbFile obterArvoreDiretoriosAnexoArc(Ciclo ciclo, AvaliacaoRiscoControle arc, String alias,
            EntidadeSupervisionavel es)
            throws SmbException, BCInfraException {
        Ciclo cicloARC = AvaliacaoRiscoControleMediator.get().buscarCicloARC(arc);
        
        List<String> listaSubDiretorios = new LinkedList<String>();

        montarCaminhoNomeDirEsCicloMatrizArc(listaSubDiretorios, cicloARC == null ? ciclo : cicloARC, arc);

        return criarDiretorioSmb(listaSubDiretorios, es, alias);
    }

    public SmbFile obterArvoreDiretoriosAnexoAnef(Ciclo ciclo, EntidadeSupervisionavel es, AnaliseQuantitativaAQT anef, String alias)
            throws SmbException, BCInfraException {
        List<String> listaSubDiretorios = new LinkedList<String>();

        montarCaminhoNomeDirEsCicloAnef(listaSubDiretorios, ciclo, anef);

        return criarDiretorioSmb(listaSubDiretorios, es, alias);
    }

    public SmbFile obterArvoreDiretoriosItemElementoAQT(Ciclo ciclo, EntidadeSupervisionavel es,
            ItemElementoAQT itemElemento, String alias)
            throws SmbException, BCInfraException {
        ElementoAQT elemento = itemElemento.getElemento();
        AnaliseQuantitativaAQT anef = elemento.getAnaliseQuantitativaAQT();

        List<String> listaSubDiretorios = new LinkedList<String>();

        montarCaminhoNomeDirEsCicloAnef(listaSubDiretorios, ciclo, anef);
        montarCaminhoNomeDirElementoItemAQTDocumento(listaSubDiretorios, elemento, itemElemento);

        return criarDiretorioSmb(listaSubDiretorios, es, alias);
    }

    public SmbFile obterArvoreDiretoriosAnexoApresentacao(Apresentacao apresentacao, EntidadeSupervisionavel es,
            String alias) throws SmbException,
            BCInfraException {
        // Declarações
        Ciclo ciclo;

        // Inicializações
        ciclo = apresentacao.getCiclo();

        List<String> listaSubDiretorios = new LinkedList<String>();

        String nomeDirCiclo = novoDiretorio(CICLO, ciclo.getPk());
        String nomeDirApresentacao = novoDiretorio(APRESENTACAO, apresentacao.getPk());

        listaSubDiretorios.add(nomeDirCiclo);
        listaSubDiretorios.add(nomeDirApresentacao);

        return criarDiretorioSmb(listaSubDiretorios, es, alias);
    }

    public SmbFile obterArvoreDiretoriosAnexoQPF(QuadroPosicaoFinanceira quadro, EntidadeSupervisionavel es,
            String alias, boolean usarPK)
            throws SmbException, BCInfraException {
        // Declarações
        Ciclo ciclo;

        // Inicializações
        ciclo = quadro.getCiclo();

        List<String> listaSubDiretorios = new LinkedList<String>();

        String nomeDirCiclo = novoDiretorio(CICLO, ciclo.getPk());
        listaSubDiretorios.add(nomeDirCiclo);

        String nomeDirApresentacao;
        if (quadro.getVersaoPerfilRisco() != null || usarPK) {
            nomeDirApresentacao = novoDiretorio(QUADROPOSICAOFINANCEIRA, quadro.getPk());
            listaSubDiretorios.add(nomeDirApresentacao);
        }

        return criarDiretorioSmb(listaSubDiretorios, es, alias);
    }

    private void montarCaminhoNomeDirElementoItemAQTDocumento(List<String> listaSubDiretorios, ElementoAQT elemento,
            ItemElementoAQT itemElemento) {
        String nomeDirElemento = novoDiretorio(ELEMENTO2, elemento.getPk());
        String nomeDirItemElemento = novoDiretorio(ITEM, itemElemento.getPk());
        String nomeDirDocumento = novoDiretorio(DOCUMENTO, itemElemento.getDocumento().getPk());

        listaSubDiretorios.add(nomeDirElemento);
        listaSubDiretorios.add(nomeDirItemElemento);
        listaSubDiretorios.add(nomeDirDocumento);
    }

    private void montarCaminhoNomeDirElementoItemDocumento(List<String> listaSubDiretorios, Elemento elemento,
            ItemElemento itemElemento) {
        String nomeDirElemento = novoDiretorio(ELEMENTO2, elemento.getPk());
        String nomeDirItemElemento = novoDiretorio(ITEM, itemElemento.getPk());
        String nomeDirDocumento = novoDiretorio(DOCUMENTO, itemElemento.getDocumento().getPk());

        listaSubDiretorios.add(nomeDirElemento);
        listaSubDiretorios.add(nomeDirItemElemento);
        listaSubDiretorios.add(nomeDirDocumento);
    }

    private void montarCaminhoNomeDirEsCicloMatrizArc(List<String> listaSubDiretorios, Ciclo ciclo,
            AvaliacaoRiscoControle arc) {

        String nomeDirCiclo = novoDiretorio(CICLO, ciclo.getPk());
        String nomeDirArc = novoDiretorio(ARC, arc.getPk());

        listaSubDiretorios.add(nomeDirCiclo);
        listaSubDiretorios.add(nomeDirArc);
    }

    private void montarCaminhoNomeDirEsCicloAnef(List<String> listaSubDiretorios, Ciclo ciclo,
            AnaliseQuantitativaAQT anef) {

        String nomeDirCiclo = novoDiretorio(CICLO, ciclo.getPk());
        String nomeDirArc = novoDiretorio("anef", anef.getPk());
        listaSubDiretorios.add(nomeDirCiclo);
        listaSubDiretorios.add(nomeDirArc);
    }

    private SmbFile criarDiretorioRaiz(Integer esPk, String alias) throws BCInfraException, SmbException {
        JCIFSHelper jcifs = JCIFSHelper.getInstance();
        String nomeDirRaiz = novoDiretorio(ES, esPk);
        SmbFile smbFile = jcifs.getSmbFile(alias, nomeDirRaiz);
        if (!smbFile.exists()) {
            smbFile.mkdir();
        }
        return smbFile;
    }

    private SmbFile criarDiretorioSmb(List<String> listaSubDiretorios, EntidadeSupervisionavel es, String alias) 
            throws SmbException, BCInfraException {
        SmbFile smbFile = criarDiretorioRaiz(es.getPk(), alias);
        for (String subDir : listaSubDiretorios) {
            try {
                smbFile = new SmbFile(smbFile.getCanonicalPath(), subDir);
                if (!smbFile.exists()) {
                    smbFile.mkdir();
                }
            } catch (MalformedURLException e) {
                throw new NegocioException(e.getMessage());
            }
        }
        return smbFile;
    }

    private String novoDiretorio(String nomeEntidade, Integer pk) {
        return nomeEntidade + UNDERLINE + pk.toString() + BARRA;
    }

    public SmbFile obterArvoreDiretorios(Ciclo ciclo, EntidadeSupervisionavel es, 
            ObjetoPersistente<Integer> entidadeAnexoDocumento,
            TipoEntidadeAnexoDocumentoEnum tipoEntidadeAnexoDocumento, String alias) throws SmbException,
            BCInfraException {
        switch (tipoEntidadeAnexoDocumento) {
            case ITEM_ELEMENTO:
                if (entidadeAnexoDocumento instanceof ItemElemento) {
                    return obterArvoreDiretoriosItemElemento(ciclo, es, (ItemElemento) entidadeAnexoDocumento, alias);
                }
                return null;
            case ITEM_ELEMENTO_AQT:
                if (entidadeAnexoDocumento instanceof ItemElementoAQT) {
                    return obterArvoreDiretoriosItemElementoAQT(ciclo, es, (ItemElementoAQT) entidadeAnexoDocumento,
                            alias);
                }
                return null;
            case PERFIL_ATUACAO:
                if (entidadeAnexoDocumento instanceof PerfilAtuacaoES) {
                    return obterArvoreDiretoriosPerfilAtuacao(ciclo, es, (PerfilAtuacaoES) entidadeAnexoDocumento, alias);
                }
                return null;
            case CONCLUSAO:
                if (entidadeAnexoDocumento instanceof ConclusaoES) {
                    return obterArvoreDiretoriosConclusao(ciclo, es, (ConclusaoES) entidadeAnexoDocumento, alias);
                }
                return null;
            default:
                return null;
        }
    }

    private SmbFile obterArvoreDiretoriosConclusao(Ciclo ciclo, EntidadeSupervisionavel es, 
            ConclusaoES conclusaoES, String alias)
            throws SmbException, BCInfraException {
        List<String> listaSubDiretorios = new LinkedList<String>();

        criarNomeDirEsCicloConclusao(listaSubDiretorios, ciclo, conclusaoES);
        criarNomeDirDocumento(listaSubDiretorios, conclusaoES.getDocumento());

        return criarDiretorioSmb(listaSubDiretorios, es, alias);
    }

    private void criarNomeDirEsCicloConclusao(List<String> listaSubDiretorios, Ciclo ciclo, ConclusaoES conclusaoES) {
        String nomeDirCiclo = novoDiretorio(CICLO, ciclo.getPk());
        String nomeDirConclusao = novoDiretorio("conclusao", conclusaoES.getPk());
        listaSubDiretorios.add(nomeDirCiclo);
        listaSubDiretorios.add(nomeDirConclusao);
    }

    private SmbFile obterArvoreDiretoriosPerfilAtuacao(Ciclo ciclo, EntidadeSupervisionavel es, 
            PerfilAtuacaoES perfilAtuacao, String alias)
            throws SmbException, BCInfraException {
        List<String> listaSubDiretorios = new LinkedList<String>();

        criarNomeDirEsCicloPerfilAtuacao(listaSubDiretorios, ciclo, perfilAtuacao);
        criarNomeDirDocumento(listaSubDiretorios, perfilAtuacao.getDocumento());

        return criarDiretorioSmb(listaSubDiretorios, es, alias);
    }

    private void criarNomeDirEsCicloPerfilAtuacao(List<String> listaSubDiretorios, Ciclo ciclo,
            PerfilAtuacaoES perfilAtuacao) {
        String nomeDirCiclo = novoDiretorio(CICLO, ciclo.getPk());
        String nomeDirPerfilAtuacao = novoDiretorio("perfilAtuacao", perfilAtuacao.getPk());
        listaSubDiretorios.add(nomeDirCiclo);
        listaSubDiretorios.add(nomeDirPerfilAtuacao);
    }

    private void criarNomeDirDocumento(List<String> listaSubDiretorios, Documento documento) {
        String nomeDirDocumento = novoDiretorio(DOCUMENTO, documento.getPk());
        listaSubDiretorios.add(nomeDirDocumento);
    }

    public SmbFile obterArvoreDiretoriosAnexoCiclo(Ciclo ciclo, EntidadeSupervisionavel es, String diretorioAnexosCiclo)
            throws SmbException,
            BCInfraException {
        List<String> listaSubDiretorios = new LinkedList<String>();

        criarNomeDirEsCicloPerfilRisco(listaSubDiretorios, ciclo);

        return criarDiretorioSmb(listaSubDiretorios, es, diretorioAnexosCiclo);
    }

    public SmbFile obterArvoreDiretoriosAnexoPosCorec(Ciclo ciclo, EntidadeSupervisionavel es,
            String diretorioAnexosCiclo) throws SmbException,
            BCInfraException {
        List<String> listaSubDiretorios = new LinkedList<String>();

        criarNomeDirEsCicloPerfilRisco(listaSubDiretorios, ciclo);

        return criarDiretorioSmb(listaSubDiretorios, es, diretorioAnexosCiclo);
    }

    private void criarNomeDirEsCicloPerfilRisco(List<String> listaSubDiretorios, Ciclo ciclo) {
        String nomeDirCiclo = novoDiretorio(CICLO, ciclo.getPk());
        listaSubDiretorios.add(nomeDirCiclo);
    }
}
