package br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.comum.excecoes.BCInfraException;
import br.gov.bcb.sisaps.adaptadores.bcjcifs.IBcJcifs;
import br.gov.bcb.sisaps.src.dao.analisequantitativaaqt.AnexoAQTDAO;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnexoAQT;
import br.gov.bcb.sisaps.src.mediator.AnexoArvoreDiretorioMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.EntidadeSupervisionavelMediator;
import br.gov.bcb.sisaps.src.mediator.GeradorAnexoMediator;
import br.gov.bcb.sisaps.src.util.BufferAnexos;
import br.gov.bcb.sisaps.src.validacao.RegraEdicaoANEFInspetorPermissaoAlteracao;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.aqt.AnexoAQTVO;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ConstantesMensagens;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

@Service
@Transactional(readOnly = true)
public class AnexoAQTMediator {
    
    private static final String DIRETORIO_ANEXOS_ANEF = "APS.ANEXO.ANEF.DIR";
    private static final String MSG_ERRO_EXCLUIR_ARQUIVO_DE_ANEXO = "Erro ao tentar excluir arquivo de anexo: ";

    @Autowired
    private IBcJcifs bcJcifs;
    @Autowired
    private AnexoAQTDAO anexoAqtDao;
    
    public static AnexoAQTMediator get() {
        return SpringUtils.get().getBean(AnexoAQTMediator.class);
    }

    
    private AnexoAQT criarAnexoAQT(AnaliseQuantitativaAQT anef, String link, InputStream inputStream, 
            AnexoAQT anexoAqt, boolean isCopiarUsuarioAnterior)
            throws NegocioException {
        AnexoAQT anexo = null;
        if (anef.getPk() != null) {
            anexo = anexoAqtDao.buscarAnexoArcMesmoNome(anef, link);
        }
        if (anexo == null) {
            anexo = new AnexoAQT();
            anexo.setLink(link);
            anexo.setAnaliseQuantitativaAQT(anef);
            anexo.setInputStream(inputStream);
            if (isCopiarUsuarioAnterior) {
                anexo.setUltimaAtualizacao(anexoAqt.getUltimaAtualizacao());
                anexo.setOperadorAtualizacao(anexoAqt.getOperadorAtualizacao());
                anexo.setAlterarDataUltimaAtualizacao(false);
            }
        } else {
            SisapsUtil.lancarNegocioException(new ErrorMessage(ConstantesMensagens.MSG_APS_ANEXO_ERROR_001));
        }
        return anexo;
    }
    
    @edu.umd.cs.findbugs.annotations.SuppressWarnings("RR_NOT_CHECKED")
    public byte[] recuperarArquivo(String nomeArquivo, Ciclo ciclo, AnaliseQuantitativaAQT anef) {
        try {
            SmbFile arqSubDiretorio = getSmbFileAnef(nomeArquivo, ciclo, anef);
            return bcJcifs.recuperarArquivo(arqSubDiretorio.getCanonicalPath(), nomeArquivo, arqSubDiretorio);
        } catch (NegocioException e) {
            throw new NegocioException(e.getMessage(), e);
        }
    }

    public String caminhoArquivo(String nomeArquivo, Ciclo ciclo, AnaliseQuantitativaAQT anef) {
        try {
            SmbFile arqSubDiretorio = getSmbFileAnef(nomeArquivo, ciclo, anef);
            return arqSubDiretorio.getCanonicalPath();
        } catch (NegocioException e) {
            throw new NegocioException(e.getMessage(), e);
        }
    }

    private SmbFile obterArquivoSubDiretorio(String nomeArquivo, Ciclo ciclo, AnaliseQuantitativaAQT anef,
            EntidadeSupervisionavel es) {
        SmbFile arqSubDiretorio = null;
        try {
            SmbFile smbFile =
                    AnexoArvoreDiretorioMediator.get().obterArvoreDiretoriosAnexoAnef(ciclo, es, anef,
                            DIRETORIO_ANEXOS_ANEF);
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
    
    @Transactional
    private void salvar(AnexoAQT anexo) {
        anexoAqtDao.save(anexo);
    }
    
    @Transactional
    public String anexarArquivo(Ciclo ciclo,  AnaliseQuantitativaAQT anef, String nomeArquivo, InputStream inputStream,
            boolean isDuplicacaoAQT, AnexoAQT anexoAqt, boolean isCopiarUsuarioAnterior) throws NegocioException {
        if (!isDuplicacaoAQT) {
            new RegraEdicaoANEFInspetorPermissaoAlteracao(anef).validar();
            Util.setIncluirBufferAnexos(true);
            BufferAnexos.resetLocalThreadBufferInclusao();
        }
        AnexoAQT anexo = criarAnexoAQT(anef, nomeArquivo, inputStream, anexoAqt, isCopiarUsuarioAnterior);
        try {
            if (inputStream != null) {
                SmbFile smbFile =
                        AnexoArvoreDiretorioMediator.get().obterArvoreDiretoriosAnexoAnef(ciclo,
                                ciclo.getEntidadeSupervisionavel(), anef,
                                DIRETORIO_ANEXOS_ANEF);
                GeradorAnexoMediator.get().gerarArquivoAnexoUpload(anexo, nomeArquivo, DIRETORIO_ANEXOS_ANEF,
                        smbFile.getCanonicalPath());
            }
        } catch (BCInfraException e) {
            throw new NegocioException(e.getMessage());
        } catch (SmbException e) {
            throw new NegocioException(e.getMessage());
            //CHECKSTYLE:OFF Exception deve ser tratada
        } catch (Exception e) {
            //CHECKSTYLE:ON
            throw new NegocioException(e.getMessage()); //NOPMD
        }
        salvar(anexo);
        if (!isDuplicacaoAQT) {
            AnaliseQuantitativaAQTMediator.get().alterarEstadoANEFParaEmEdicao(anef);
            GeradorAnexoMediator.get().incluirAnexosBuffer();
        }
        return "Arquivo anexado com sucesso.";
    }
    
    public AnexoAQT buscarAnexoPk(Integer pkAnexo) {
        return anexoAqtDao.load(pkAnexo);
    }
    
    @Transactional
    public void duplicarAnexosANEFConclusaoAnalise(Ciclo ciclo, AnaliseQuantitativaAQT anef,
            AnaliseQuantitativaAQT anefNovo, boolean isEncerrarCorec) {
        for (AnexoAQT anexoAqt : anef.getAnexosAqt()) {
            ByteArrayInputStream inputStream = null;
            if (!SisapsUtil.isExecucaoTeste()) {
                byte[] arquivoAnexo = recuperarArquivo(anexoAqt.getLink(), anef.getCiclo(), anef);
                inputStream = new ByteArrayInputStream(arquivoAnexo);
            }
            anexarArquivo(ciclo, anefNovo, anexoAqt.getLink(), inputStream, true, anexoAqt, isEncerrarCorec);
        }
    }

    @Transactional
    public String excluirAnexo(AnexoAQTVO anexoAqtVo, Ciclo ciclo, AnaliseQuantitativaAQT aqt) {
        new RegraEdicaoANEFInspetorPermissaoAlteracao(aqt).validar();
        AnaliseQuantitativaAQTMediator.get().alterarEstadoANEFParaEmEdicao(aqt);
        AnexoAQT anexoARC = buscarAnexoPk(anexoAqtVo.getPk());
        anexoAqtDao.evict(anexoARC);
        try {
        	anexoAqtDao.delete(anexoARC);
	        if (!SisapsUtil.isExecucaoTeste()) {
	            SmbFile arqSubDiretorio = getSmbFileAnef(anexoARC.getLink(), ciclo, aqt);
	            bcJcifs.apagarArquivo(DIRETORIO_ANEXOS_ANEF, anexoARC.getLink(),
	                    MSG_ERRO_EXCLUIR_ARQUIVO_DE_ANEXO + anexoARC.getLink(), arqSubDiretorio);
	        }
	       
		    } catch (Exception e) {
				// TODO: handle exception
			}
        return "Arquivo excluído com sucesso.";
    }

    public List<AnexoAQT> buscar(AnaliseQuantitativaAQT arc) {
        return anexoAqtDao.buscarAnexosAqt(arc);
    }

    public boolean possuiAnexos(AnaliseQuantitativaAQT aqt) {
        return CollectionUtils.isNotEmpty(buscar(aqt));
    }

    public AnexoAQT buscarPorNome(AnaliseQuantitativaAQT anef, String link) {
        return anexoAqtDao.buscarAnexoArcMesmoNome(anef, link);
    }
    
    public AnexoAQT buscarUltimoAnexoANEF(AnaliseQuantitativaAQT anef) {
        return anexoAqtDao.buscarUltimoAnexoANEF(anef);
    }

    @Transactional(readOnly = true)
    public List<AnexoAQT> listarAnexos() {
        return anexoAqtDao.listarAnexos();
    }

    private SmbFile getSmbFileAnef(String nomeArquivo, Ciclo ciclo, AnaliseQuantitativaAQT anef) {
        List<EntidadeSupervisionavel> listaESs = EntidadeSupervisionavelMediator.get()
                .buscarEssPorCNPJ(ciclo.getEntidadeSupervisionavel().getConglomeradoOuCnpj(), ciclo.getPk() < 0);
        List<Ciclo> listaCiclos = CicloMediator.get().consultarCiclosEntidadeSupervisionavel(
                ciclo.getEntidadeSupervisionavel().getConglomeradoOuCnpj(), true);
        SmbFile arqSubDiretorio = null;
        boolean encontrouAnexo = false;
        try {
            // Caso o anexo ainda não esteja no ciclo atual
            for (EntidadeSupervisionavel esAnterior : listaESs) {
                for (Ciclo cicloAnterior : listaCiclos) {
                    arqSubDiretorio = obterArquivoSubDiretorio(nomeArquivo, cicloAnterior, anef, esAnterior);
                    if (arqSubDiretorio.exists()) {
                        encontrouAnexo = true;
                        break;
                    }
                }
                if (encontrouAnexo) {
                    break;
                }
            }
        } catch (SmbException e) {
            throw new NegocioException(e.getMessage(), e);
        }

        return arqSubDiretorio;
    }


}
