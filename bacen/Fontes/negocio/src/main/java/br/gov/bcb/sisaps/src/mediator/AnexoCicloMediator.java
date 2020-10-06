package br.gov.bcb.sisaps.src.mediator;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.comum.excecoes.BCInfraException;
import br.gov.bcb.sisaps.adaptadores.bcjcifs.IBcJcifs;
import br.gov.bcb.sisaps.src.dao.AnexoCicloDAO;
import br.gov.bcb.sisaps.src.dominio.AnexoCiclo;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

@Service
@Scope("singleton")
public class AnexoCicloMediator {

    private static final String DIRETORIO_ANEXOS_CICLO = "APS.ANEXO.CICLO.DIR";
    @Autowired
    private IBcJcifs bcJcifs;
    @Autowired
    private AnexoCicloDAO anexoCicloDao;

    public static AnexoCicloMediator get() {
        return SpringUtils.get().getBean(AnexoCicloMediator.class);
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings("RR_NOT_CHECKED")
    public byte[] recuperarArquivo(String nomeArquivo, Ciclo ciclo, VersaoPerfilRisco versaoPerfilRisco) {

        try {
            SmbFile arqSubDiretorio = getSmbFileCiclo(nomeArquivo, ciclo, versaoPerfilRisco);
            return bcJcifs.recuperarArquivo(arqSubDiretorio.getCanonicalPath(), nomeArquivo, arqSubDiretorio);
        } catch (NegocioException e) {
            throw new NegocioException(e.getMessage(), e);
        }
    }

    public String caminhoArquivo(String nomeArquivo, Ciclo ciclo, VersaoPerfilRisco versaoPerfilRisco) {
        try {
            SmbFile arqSubDiretorio = getSmbFileCiclo(nomeArquivo, ciclo, versaoPerfilRisco);
            return arqSubDiretorio.getCanonicalPath();
        } catch (NegocioException e) {
            throw new NegocioException(e.getMessage(), e);
        }
    }

    private SmbFile obterArquivoSubDiretorio(String nomeArquivo, Ciclo ciclo, VersaoPerfilRisco versaoPerfilRisco,
            EntidadeSupervisionavel es) {
        SmbFile arqSubDiretorio = null;
        try {
            SmbFile smbFile =
                    AnexoArvoreDiretorioMediator.get().obterArvoreDiretoriosAnexoCiclo(
                            ciclo, es, DIRETORIO_ANEXOS_CICLO);
            arqSubDiretorio = new SmbFile(smbFile, nomeArquivo);
        } catch (BCInfraException e) {
            throw new NegocioException(e.getMessage(), e);
        } catch (MalformedURLException e) {
            throw new NegocioException(e.getMessage(), e);
        } catch (UnknownHostException e) {
            throw new NegocioException(e.getMessage(), e);
        } catch (SmbException e) {
            throw new NegocioException(e.getMessage(), e);
        }
        return arqSubDiretorio;
    }

    @Transactional(readOnly = true)
    public AnexoCiclo buscarAnexoPk(Integer pkAnexo) {
        return anexoCicloDao.load(pkAnexo);
    }

    @Transactional(readOnly = true)
    public AnexoCiclo buscarAnexoPkInicializado(Integer pkAnexo) {
        AnexoCiclo anexo = anexoCicloDao.load(pkAnexo);
        if (anexo.getCiclo() != null) {
            Hibernate.initialize(anexo.getCiclo());
        }
        if (anexo.getVersaoPerfilRisco() != null) {
            Hibernate.initialize(anexo.getVersaoPerfilRisco());
        }
        return anexo;
    }

    @Transactional(readOnly = true)
    public List<AnexoCiclo> buscar(PerfilRisco perfilRisco) {
        List<VersaoPerfilRisco> versoesPerfilRisco = 
                VersaoPerfilRiscoMediator.get().buscarVersoesPerfilRisco(
                        perfilRisco.getPk(), TipoObjetoVersionadorEnum.ANEXO_CICLO);
        if (CollectionUtils.isNotEmpty(versoesPerfilRisco)) {
            return anexoCicloDao.buscarAnexosCicloPerfilRisco(versoesPerfilRisco);
        } else {
            return new ArrayList<AnexoCiclo>();
        }
    }

    public List<AnexoCiclo> listarAnexos() {
        return anexoCicloDao.listarAnexos();
    }

    private SmbFile getSmbFileCiclo(String nomeArquivo, Ciclo ciclo, VersaoPerfilRisco versaoPerfilRisco) {
        List<EntidadeSupervisionavel> listaESs = EntidadeSupervisionavelMediator.get()
                .buscarEssPorCNPJ(ciclo.getEntidadeSupervisionavel().getConglomeradoOuCnpj(), ciclo.getPk() < 0);
        SmbFile arqSubDiretorio = null;
        for (EntidadeSupervisionavel es : listaESs) {
            arqSubDiretorio = obterArquivoSubDiretorio(nomeArquivo, ciclo, versaoPerfilRisco, es);
            try {
                if (arqSubDiretorio.exists()) {
                    break;
                }
            } catch (SmbException e) {
                throw new NegocioException(e.getMessage(), e);
            }
        }
        return arqSubDiretorio;
    }

}
