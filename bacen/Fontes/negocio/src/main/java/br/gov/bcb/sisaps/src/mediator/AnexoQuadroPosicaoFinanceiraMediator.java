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
import br.gov.bcb.sisaps.src.dao.AnexoQPFDao;
import br.gov.bcb.sisaps.src.dominio.AnexoQuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.QuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.mediator.analisequantitativa.QuadroPosicaoFinanceiraMediator;
import br.gov.bcb.sisaps.src.util.AnexoBuffer;
import br.gov.bcb.sisaps.src.util.BufferAnexos;
import br.gov.bcb.sisaps.src.vo.AnexoQPFVo;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ConstantesMensagens;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

@Service
@Scope("singleton")
public class AnexoQuadroPosicaoFinanceiraMediator {

    private static final String MSG_ERRO_EXCLUIR_ARQUIVO_DE_ANEXO = "Erro ao tentar excluir arquivo de anexo: ";
    private static final String DIRETORIO_ANEXOS_QUADRO = "APS.ANEXO.QUADRO.DIR";
    @Autowired
    private IBcJcifs bcJcifs;
    @Autowired
    private AnexoQPFDao anexoQPFDao;

    public static AnexoQuadroPosicaoFinanceiraMediator get() {
        return SpringUtils.get().getBean(AnexoQuadroPosicaoFinanceiraMediator.class);
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings("RR_NOT_CHECKED")
    public byte[] recuperarArquivo(String nomeArquivo, QuadroPosicaoFinanceira quadro) {
        try {
            SmbFile arqSubDiretorio = getSmbFileQuadro(nomeArquivo, quadro);
            System.out.println("##ANEXOQUADRO: " + getCaminhoArquivoTrim(arqSubDiretorio));
            return bcJcifs.recuperarArquivo(getCaminhoArquivoTrim(arqSubDiretorio), nomeArquivo, arqSubDiretorio);
        } catch (NegocioException e) {
            throw new NegocioException(e.getMessage(), e);
        }
    }

    private SmbFile getSmbFileQuadro(String nomeArquivo, QuadroPosicaoFinanceira quadro) {
        List<EntidadeSupervisionavel> listaESs = EntidadeSupervisionavelMediator.get()
                .buscarEssPorCNPJ(quadro.getCiclo().getEntidadeSupervisionavel().getConglomeradoOuCnpj(), quadro.getCiclo().getPk() < 0);
        SmbFile arqSubDiretorio = null;
        for (EntidadeSupervisionavel es : listaESs) {
            arqSubDiretorio = obterArquivoSubDiretorio(nomeArquivo, quadro, es);
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

    public String caminhoArquivo(String nomeArquivo, QuadroPosicaoFinanceira quadro) {

        try {
            SmbFile arqSubDiretorio = getSmbFileQuadro(nomeArquivo, quadro);
            System.out.println("##ANEXOQUADRO: " + getCaminhoArquivoTrim(arqSubDiretorio));
            return getCaminhoArquivoTrim(arqSubDiretorio);
        } catch (NegocioException e) {
            throw new NegocioException(e.getMessage(), e);
        }
    }

    private SmbFile obterArquivoSubDiretorio(String nomeArquivo, QuadroPosicaoFinanceira quadro,
            EntidadeSupervisionavel es) {
        SmbFile arqSubDiretorio = null;
        try {
            SmbFile smbFile =
                    AnexoArvoreDiretorioMediator.get().obterArvoreDiretoriosAnexoQPF(quadro, es,
                            DIRETORIO_ANEXOS_QUADRO,
                            false);
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
    public void excluirAnexo(AnexoQPFVo anexoQPFVo, QuadroPosicaoFinanceira quadro) {
        AnexoQuadroPosicaoFinanceira anexoQPF = buscarAnexoPk(anexoQPFVo.getPk());
        excluirAnexo(anexoQPF, quadro);
    }

    @Transactional
    public void excluirAnexo(AnexoQuadroPosicaoFinanceira anexoQPF, QuadroPosicaoFinanceira quadro) {
        anexoQPFDao.evict(anexoQPF);
        
        try {
        	anexoQPFDao.delete(anexoQPF);
	        if (!SisapsUtil.isExecucaoTeste()) {
	            SmbFile arqSubDiretorio = getSmbFileQuadro(anexoQPF.getLink(), quadro);
	            if (Util.isIncluirBufferAnexos()) {
	                AnexoBuffer anexoBuffer = new AnexoBuffer();
	                anexoBuffer.setAlias(DIRETORIO_ANEXOS_QUADRO);
	                anexoBuffer.setNome(anexoQPF.getLink());
	                anexoBuffer.setArquivoSmbFile(arqSubDiretorio);
	                BufferAnexos.getBufferExclusaoAnexos().add(anexoBuffer);
	            } else {
	                System.out.println("##ANEXOQUADRO: " + getCaminhoArquivoTrim(arqSubDiretorio));
	                bcJcifs.apagarArquivo(DIRETORIO_ANEXOS_QUADRO, anexoQPF.getLink(), MSG_ERRO_EXCLUIR_ARQUIVO_DE_ANEXO
	                        + anexoQPF.getLink(), arqSubDiretorio);
	            }
	        }
        } catch (Exception e) {
			// TODO: handle exception
		}
    }

    @Transactional(readOnly = true)
    public AnexoQuadroPosicaoFinanceira buscarAnexoPk(Integer pkAnexo) {
        return anexoQPFDao.load(pkAnexo);
    }

    @Transactional(readOnly = true)
    public AnexoQuadroPosicaoFinanceira buscarAnexoPkInicializado(Integer pkAnexo) {
        AnexoQuadroPosicaoFinanceira anexo = anexoQPFDao.load(pkAnexo);
        if (anexo.getQuadroPosicaoFinanceira() != null) {
            Hibernate.initialize(anexo.getQuadroPosicaoFinanceira());
        }
        return anexo;
    }

    @Transactional
    public void excluir(AnexoQuadroPosicaoFinanceira anexoQPF) {
        anexoQPFDao.evict(anexoQPF);
        anexoQPFDao.delete(anexoQPF);
    }

    @Transactional(readOnly = true)
    public List<AnexoQuadroPosicaoFinanceira> buscar(QuadroPosicaoFinanceira quadro) {
        return anexoQPFDao.buscarAnexosQuadro(quadro);
    }

    @Transactional
    public void anexarArquivo(QuadroPosicaoFinanceira quadro, String nomeArquivo, InputStream inputStream,
            AnexoQuadroPosicaoFinanceira anexoQPF, boolean isCopiarUsuarioAnexoAnterior, boolean isConcluirCorec) {
        AnexoQuadroPosicaoFinanceira anexo =
                criarAnexoQPF(quadro, nomeArquivo, inputStream, anexoQPF, isCopiarUsuarioAnexoAnterior);
        salvar(anexo);
        try {
            if (inputStream != null) {
                SmbFile smbFile =
                        AnexoArvoreDiretorioMediator.get().obterArvoreDiretoriosAnexoQPF(quadro,
                                quadro.getCiclo().getEntidadeSupervisionavel(),
                                DIRETORIO_ANEXOS_QUADRO, isConcluirCorec);
                System.out.println("##ANEXOQUADRO: " + getCaminhoArquivoTrim(smbFile));
                GeradorAnexoMediator.get().gerarArquivoAnexoUpload(anexo, nomeArquivo, DIRETORIO_ANEXOS_QUADRO,
                        getCaminhoArquivoTrim(smbFile));
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
    }

    @Transactional
    public void anexarArquivorRascunhoParaVigente(QuadroPosicaoFinanceira quadro, String nomeArquivo,
            InputStream inputStream) {
        AnexoQuadroPosicaoFinanceira anexo = new AnexoQuadroPosicaoFinanceira();
        anexo.setInputStream(inputStream);
        try {
            if (inputStream != null) {
                SmbFile smbFile =
                        AnexoArvoreDiretorioMediator.get().obterArvoreDiretoriosAnexoQPF(quadro,
                                quadro.getCiclo().getEntidadeSupervisionavel(),
                                DIRETORIO_ANEXOS_QUADRO, true);
                System.out.println("##ANEXOQUADRO: " + getCaminhoArquivoTrim(smbFile));
                GeradorAnexoMediator.get().gerarArquivoAnexoUpload(anexo, nomeArquivo, DIRETORIO_ANEXOS_QUADRO,
                        getCaminhoArquivoTrim(smbFile));
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
    }

    @Transactional
    public void salvar(AnexoQuadroPosicaoFinanceira anexoQPF) {
        anexoQPFDao.save(anexoQPF);
    }

    private AnexoQuadroPosicaoFinanceira criarAnexoQPF(QuadroPosicaoFinanceira quadro, String link,
            InputStream inputStream, AnexoQuadroPosicaoFinanceira anexoQPF, boolean isCopiarUsuarioAnexoAnterior) {
        AnexoQuadroPosicaoFinanceira anexo = null;
        if (quadro.getPk() != null) {
            anexo = anexoQPFDao.buscarAnexoQuadroMesmoNome(quadro, link);
        }
        if (anexo == null) {
            anexo = new AnexoQuadroPosicaoFinanceira();
            anexo.setLink(link);
            anexo.setQuadroPosicaoFinanceira(quadro);
            anexo.setInputStream(inputStream);
            if (isCopiarUsuarioAnexoAnterior) {
                anexo.setUltimaAtualizacao(anexoQPF.getUltimaAtualizacao());
                anexo.setOperadorAtualizacao(anexoQPF.getOperadorAtualizacao());
                anexo.setAlterarDataUltimaAtualizacao(false);
            }
        } else {
            System.out.println("###### criarAnexoQPF");
            System.out.println("#### quadro: " + quadro.getPk());
            System.out.println("#### anexo: " + link);
            SisapsUtil.lancarNegocioException(new ErrorMessage(ConstantesMensagens.MSG_APS_ANEXO_ERROR_001));
        }
        return anexo;
    }

    @Transactional
    public void duplicarAnexos(QuadroPosicaoFinanceira quadroAnterior, QuadroPosicaoFinanceira quadroNovo,
            boolean isCopiarUsuarioAnexoAnterior, boolean isConcluirCorec) {
        for (AnexoQuadroPosicaoFinanceira anexoQPF : buscar(quadroAnterior)) {
            if (buscar(quadroNovo) != null) {
                ByteArrayInputStream inputStream = null;
                if (!SisapsUtil.isExecucaoTeste()) {
                    byte[] arquivoAnexo = recuperarArquivo(anexoQPF.getLink(), quadroAnterior);
                    inputStream = new ByteArrayInputStream(arquivoAnexo);
                }
                anexarArquivo(quadroNovo, anexoQPF.getLink(), inputStream, anexoQPF, isCopiarUsuarioAnexoAnterior,
                        isConcluirCorec);
            }
            QuadroPosicaoFinanceiraMediator.get().desconectar(anexoQPF.getQuadroPosicaoFinanceira());
        }
    }

    @Transactional
    public void duplicarAnexosConclusaoRascunho(QuadroPosicaoFinanceira quadroRascunho) {
        for (AnexoQuadroPosicaoFinanceira anexoQPF : buscar(quadroRascunho)) {
            ByteArrayInputStream inputStream = null;
            if (!SisapsUtil.isExecucaoTeste()) {
                byte[] arquivoAnexo = recuperarArquivo(anexoQPF.getLink(), quadroRascunho);
                inputStream = new ByteArrayInputStream(arquivoAnexo);
            }
            anexarArquivorRascunhoParaVigente(quadroRascunho, anexoQPF.getLink(), inputStream);
        }
    }

    @Transactional
    public List<AnexoQuadroPosicaoFinanceira> listarAnexos() {
        return anexoQPFDao.listarAnexos();
    }

    private String getCaminhoArquivoTrim(SmbFile arqSubDiretorio) {
        return arqSubDiretorio != null && arqSubDiretorio.getCanonicalPath() != null
                ? arqSubDiretorio.getCanonicalPath().trim()
                : "";
    }

}
