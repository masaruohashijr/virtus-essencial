package br.gov.bcb.sisaps.src.mediator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.comum.excecoes.BCInfraException;
import br.gov.bcb.sisaps.adaptadores.bcjcifs.IBcJcifs;
import br.gov.bcb.sisaps.src.dao.AnexoArcDao;
import br.gov.bcb.sisaps.src.dominio.AnexoARC;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.util.AnexoBuffer;
import br.gov.bcb.sisaps.src.util.BufferAnexos;
import br.gov.bcb.sisaps.src.vo.AnexoArcVo;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ConstantesMensagens;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

@Service
@Scope("singleton")
public class AnexoArcMediator {

    private static final String MSG_ERRO_EXCLUIR_ARQUIVO_DE_ANEXO = "Erro ao tentar excluir arquivo de anexo: ";
    private static final String DIRETORIO_ANEXOS_ARC = "APS.ANEXO.ARC.DIR";
    @Autowired
    private IBcJcifs bcJcifs;
    @Autowired
    private AnexoArcDao anexoArcDao;
    @Autowired
    private AvaliacaoRiscoControleMediator avaliacaoRiscoControleMediator;

    public static AnexoArcMediator get() {
        return SpringUtils.get().getBean(AnexoArcMediator.class);
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings("RR_NOT_CHECKED")
    public byte[] recuperarArquivo(String nomeArquivo, Ciclo ciclo, AvaliacaoRiscoControle arc) {
        try {
            SmbFile arqSubDiretorio = getSmbFileArc(nomeArquivo, ciclo, arc);
            return bcJcifs.recuperarArquivo(arqSubDiretorio.getCanonicalPath(), nomeArquivo, arqSubDiretorio);
        } catch (NegocioException e) {
            throw new NegocioException(e.getMessage(), e);
        }
    }

    private SmbFile getSmbFileArc(String nomeArquivo, Ciclo ciclo, AvaliacaoRiscoControle arc) {
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
                    arqSubDiretorio = obterArquivoSubDiretorio(nomeArquivo, cicloAnterior, arc, esAnterior);
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


    public String caminhoArquivo(String nomeArquivo, Ciclo ciclo, AvaliacaoRiscoControle arc) {
        try {
            SmbFile arqSubDiretorio = getSmbFileArc(nomeArquivo, ciclo, arc);
            return arqSubDiretorio.getCanonicalPath();
        } catch (NegocioException e) {
            throw new NegocioException(e.getMessage(), e);
        }
    }

    private SmbFile obterArquivoSubDiretorio(String nomeArquivo, Ciclo ciclo, AvaliacaoRiscoControle arc,
            EntidadeSupervisionavel es) {
        SmbFile arqSubDiretorio = null;
        try {
            SmbFile smbFile =
                    AnexoArvoreDiretorioMediator.get().obterArvoreDiretoriosAnexoArc(ciclo, arc, DIRETORIO_ANEXOS_ARC,
                            es);
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
    public void excluirAnexo(AnexoArcVo anexoArcVo, Ciclo ciclo, AvaliacaoRiscoControle arc) {
        AnexoARC anexoARC = buscarAnexoPk(anexoArcVo.getPk());
        excluirAnexo(anexoARC, ciclo, arc);
    }
    
    @Transactional
    public void excluirAnexo(AnexoARC anexoARC, Ciclo ciclo, AvaliacaoRiscoControle arc) {
        anexoArcDao.evict(anexoARC);
        try {
	        anexoArcDao.delete(anexoARC);
	        if (!SisapsUtil.isExecucaoTeste()) {
	            SmbFile arqSubDiretorio = getSmbFileArc(anexoARC.getLink(), ciclo, arc);
	            if (Util.isIncluirBufferAnexos()) {
	                AnexoBuffer anexoBuffer = new AnexoBuffer();
	                anexoBuffer.setAlias(DIRETORIO_ANEXOS_ARC);
	                anexoBuffer.setNome(anexoARC.getLink());
	                anexoBuffer.setArquivoSmbFile(arqSubDiretorio);
	                BufferAnexos.getBufferExclusaoAnexos().add(anexoBuffer);
	            } else {
	                bcJcifs.apagarArquivo(DIRETORIO_ANEXOS_ARC, anexoARC.getLink(),
	                        MSG_ERRO_EXCLUIR_ARQUIVO_DE_ANEXO + anexoARC.getLink(), arqSubDiretorio);
	            }
	        }
        } catch (Exception e) {
			// TODO: handle exception
		}
    }

    @Transactional(readOnly = true)
    public AnexoARC buscarAnexoPk(Integer pkAnexo) {
        return anexoArcDao.load(pkAnexo);
    }

    @Transactional(readOnly = true)
    public AnexoARC buscarAnexoPkInicializado(Integer pkAnexo) {
        AnexoARC anexo = anexoArcDao.load(pkAnexo);
        if (anexo.getAvaliacaoRiscoControle() != null) {
            Hibernate.initialize(anexo.getAvaliacaoRiscoControle());
        }
        return anexo;
    }

    @Transactional(readOnly = true)
    public List<AnexoARC> buscar(AvaliacaoRiscoControle arc) {
        return anexoArcDao.buscarAnexosArc(arc);
    }

    @Transactional
    public void anexarArquivo(Ciclo ciclo, AvaliacaoRiscoControle arc, String nomeArquivo, InputStream inputStream,
            boolean isDuplicacaoARC, AnexoARC anexoArc, boolean isCopiarUsuarioAnexoAnterior) {
        if (!isDuplicacaoARC) {
            Util.setIncluirBufferAnexos(true);
            BufferAnexos.resetLocalThreadBufferInclusao();
        }
        AnexoARC anexo = criarAnexoARC(arc, nomeArquivo, inputStream, anexoArc, isCopiarUsuarioAnexoAnterior);
        try {
            if (inputStream != null) {
                SmbFile smbFile =
                        AnexoArvoreDiretorioMediator.get().obterArvoreDiretoriosAnexoArc(ciclo, arc,
                                DIRETORIO_ANEXOS_ARC, ciclo.getEntidadeSupervisionavel());
                GeradorAnexoMediator.get().gerarArquivoAnexoUpload(anexo, nomeArquivo, DIRETORIO_ANEXOS_ARC,
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
        if (!isDuplicacaoARC) {
            avaliacaoRiscoControleMediator.alterarEstadoARCParaEmEdicao(arc);
            GeradorAnexoMediator.get().incluirAnexosBuffer();
        }
    }

    @Transactional
    private void salvar(AnexoARC anexo) {
        anexoArcDao.save(anexo);
    }

    private AnexoARC criarAnexoARC(AvaliacaoRiscoControle arc, String link, InputStream inputStream, 
            AnexoARC anexoArc, boolean isCopiarUsuarioAnexoAnterior) {
        AnexoARC anexo = null;
        if (arc.getPk() != null) {
            anexo = anexoArcDao.buscarAnexoArcMesmoNome(arc, link);
        }
        if (anexo == null) {
            anexo = new AnexoARC();
            anexo.setLink(link);
            anexo.setAvaliacaoRiscoControle(arc);
            anexo.setInputStream(inputStream);
            if (isCopiarUsuarioAnexoAnterior) {
                anexo.setUltimaAtualizacao(anexoArc.getUltimaAtualizacao());
                anexo.setOperadorAtualizacao(anexoArc.getOperadorAtualizacao());
                anexo.setAlterarDataUltimaAtualizacao(false);
            }
        } else {
            SisapsUtil.lancarNegocioException(new ErrorMessage(ConstantesMensagens.MSG_APS_ANEXO_ERROR_001));
        }
        return anexo;
    }

    @Transactional
    public void duplicarAnexosARCConclusaoAnalise(Ciclo ciclo, AvaliacaoRiscoControle arc,
            AvaliacaoRiscoControle novoARC, boolean isCopiarUsuarioAnexoAnterior) {
        for (AnexoARC anexoARC : arc.getAnexosArc()) {
            ByteArrayInputStream inputStream = null;
            if (!SisapsUtil.isExecucaoTeste()) {
                byte[] arquivoAnexo = recuperarArquivo(anexoARC.getLink(), ciclo, arc);
                inputStream = new ByteArrayInputStream(arquivoAnexo);
            }
            anexarArquivo(ciclo, novoARC, anexoARC.getLink(), inputStream, true, anexoARC, isCopiarUsuarioAnexoAnterior);
        }
    }
    
    @Transactional
    public void duplicarAnexosARCConclusaoCorec(Ciclo cicloAnterior, Ciclo cicloNovo, 
            AvaliacaoRiscoControle arc, AvaliacaoRiscoControle novoARC) {
        for (AnexoARC anexoARC : arc.getAnexosArc()) {
            ByteArrayInputStream inputStream = null;
            if (!SisapsUtil.isExecucaoTeste()) {
                byte[] arquivoAnexo = recuperarArquivo(anexoARC.getLink(), cicloAnterior, arc);
                inputStream = new ByteArrayInputStream(arquivoAnexo);
            }
            anexarArquivo(cicloNovo, novoARC, anexoARC.getLink(), inputStream, true, anexoARC, true);
        }
    }

    @Transactional
    public void copiarAnexosARCAnterior(Ciclo ciclo, AvaliacaoRiscoControle arcPerfilRiscoVigente) {
        for (AnexoARC anexo : arcPerfilRiscoVigente.getAnexosArc()) {
            excluirAnexo(anexo, ciclo, arcPerfilRiscoVigente);
        }
        anexoArcDao.flush();
        duplicarAnexosARCConclusaoAnalise(ciclo, 
                arcPerfilRiscoVigente.getAvaliacaoRiscoControleVigente(), arcPerfilRiscoVigente, true);
    }

    @Transactional(readOnly = true)
    public List<AnexoARC> listarAnexos() {
        return anexoArcDao.listarAnexos();
    }

}
