package br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.comum.excecoes.BCInfraException;
import br.gov.bcb.sisaps.adaptadores.bcjcifs.IBcJcifs;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dao.AnexoDocumentoDao;
import br.gov.bcb.sisaps.src.dao.analisequantitativaaqt.ItemElementoAQTDAO;
import br.gov.bcb.sisaps.src.dominio.AnexoDocumento;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ItemElementoAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEntidadeAnexoDocumentoEnum;
import br.gov.bcb.sisaps.src.mediator.AnexoArvoreDiretorioMediator;
import br.gov.bcb.sisaps.src.mediator.AnexoDocumentoMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.DocumentoMediator;
import br.gov.bcb.sisaps.src.mediator.EntidadeSupervisionavelMediator;
import br.gov.bcb.sisaps.src.mediator.GeradorAnexoMediator;
import br.gov.bcb.sisaps.src.util.BufferAnexos;
import br.gov.bcb.sisaps.src.validacao.RegraEdicaoANEFInspetorPermissaoAlteracao;
import br.gov.bcb.sisaps.src.vo.AnexoDocumentoVo;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ConstantesMensagens;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

@Service
@Scope("singleton")
public class AnexoItemElementoAQTMediator {

    private static final String DIRETORIO_ANEXOS_ITEM_ELEMENTO = "APS.ANEXO.DOCUMENTO.DIR";
    private static final String MSG_ERRO_EXCLUIR_ARQUIVO_DE_ANEXO = "Erro ao tentar excluir arquivo de anexo: ";

    @Autowired
    private AnexoDocumentoDao anexoDocumentoDao;

    @Autowired
    private ItemElementoAQTMediator itemElementoAQTMediator;

    @Autowired
    private AnaliseQuantitativaAQTMediator analiseQuantitativaAQTMediator;

    @Autowired
    private ItemElementoAQTDAO itemElementoAQTDAO;

    @Autowired
    private IBcJcifs bcJcifs;

    public static AnexoItemElementoAQTMediator get() {
        return SpringUtils.get().getBean(AnexoItemElementoAQTMediator.class);
    }

    @Transactional(readOnly = true)
    public AnexoDocumento buscarAnexoPk(Integer pkAnexo) {
        return anexoDocumentoDao.load(pkAnexo);
    }
    
    @Transactional
    public String anexarArquivo(ItemElementoAQT itemElemento, String link, InputStream inputStream) {
        new RegraEdicaoANEFInspetorPermissaoAlteracao(itemElemento.getElemento().getAnaliseQuantitativaAQT()).validar();
        anexarArquivo(itemElemento, link, inputStream, false);
        return "Arquivo anexado com sucesso.";
    }

    @Transactional
    public AnexoDocumento anexarArquivo(ItemElementoAQT itemElemento, String link, InputStream inputStream,
            boolean isDuplicacaoAQT) {
        if (!isDuplicacaoAQT) {
            Util.setIncluirBufferAnexos(true);
            BufferAnexos.resetLocalThreadBufferInclusao();
        }
        AnexoDocumento anexo = criarAnexoDocumento(itemElemento, link, inputStream);

        try {
            if (inputStream != null) {
                SmbFile smbFile =
                        AnexoArvoreDiretorioMediator.get().obterArvoreDiretoriosItemElementoAQT(
                                itemElemento.getElemento().getAnaliseQuantitativaAQT().getCiclo(),
                                itemElemento.getElemento().getAnaliseQuantitativaAQT().getCiclo()
                                        .getEntidadeSupervisionavel(),
                                itemElemento,
                                DIRETORIO_ANEXOS_ITEM_ELEMENTO);
                GeradorAnexoMediator.get().gerarArquivoAnexoUpload(anexo, link, DIRETORIO_ANEXOS_ITEM_ELEMENTO,
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

        anexo = salvar(itemElemento, link, inputStream, isDuplicacaoAQT);
        if (!isDuplicacaoAQT) {
            GeradorAnexoMediator.get().incluirAnexosBuffer();
            BufferAnexos.resetLocalThreadBufferInclusao();
        }
        return anexo;
    }

    @Transactional
    private AnexoDocumento salvar(ItemElementoAQT itemElemento, String link, InputStream inputStream,
            boolean isDuplicacaoARC) {
        AnexoDocumento salvarAnexoItem = salvarAnexoItem(itemElemento, link, inputStream);
        itemElementoAQTMediator.salvarJustificativaItemElementoAQT(itemElemento.getElemento()
                .getAnaliseQuantitativaAQT(), itemElemento, isDuplicacaoARC);
        return salvarAnexoItem;
    }

    private AnexoDocumento salvarAnexoItem(ItemElementoAQT itemElemento, String link, InputStream inputStream) {
        AnexoDocumento anexo = null;
        if (itemElemento.getDocumento() != null && itemElemento.getDocumento().getPk() != null) {
            anexo = anexoDocumentoDao.buscarAnexoDocumentoMesmoNome(itemElemento.getDocumento(), link);
        }
        if (anexo == null) {
            anexo = new AnexoDocumento();
            anexo.setLink(link);
            anexo.setOperadorAtualizacao(UsuarioCorrente.get().getLogin());
            anexo.setUltimaAtualizacao(new DateTime());
            anexo.setDocumento(itemElemento.getDocumento());
            anexo.setInputStream(inputStream);
            anexoDocumentoDao.save(anexo);
            anexo.getDocumento();
        } else {
            SisapsUtil.lancarNegocioException(new ErrorMessage(ConstantesMensagens.MSG_APS_ANEXO_ERROR_001));
        }
        return anexo;
    }

    @Transactional
    public void duplicarAnexosItemElementoARCConclusaoAnalise(Ciclo ciclo, ItemElementoAQT itemElemento,
            ItemElementoAQT novoItemElemento, boolean isConclusaoCorec) {
        if (itemElemento.getDocumento() != null) {
            List<AnexoDocumento> anexos = AnexoDocumentoMediator.get().buscar(itemElemento.getDocumento());
            for (AnexoDocumento anexoDocumento : anexos) {
                duplicarAnexo(ciclo, itemElemento, novoItemElemento, anexoDocumento, isConclusaoCorec);
            }
        }
    }

    private void duplicarAnexo(Ciclo ciclo, ItemElementoAQT itemElemento, ItemElementoAQT novoItemElemento,
            AnexoDocumento anexoItem, boolean isConclusaoCorec) {
        ByteArrayInputStream inputStream = null;
        if (!SisapsUtil.isExecucaoTeste()) {
            byte[] arquivoAnexo =
                    AnexoDocumentoMediator.get().recuperarArquivo(anexoItem.getLink(), itemElemento,
                            TipoEntidadeAnexoDocumentoEnum.ITEM_ELEMENTO_AQT, 
                            itemElemento.getElemento().getAnaliseQuantitativaAQT().getCiclo());
            inputStream = new ByteArrayInputStream(arquivoAnexo);
        }
        AnexoDocumento novoAnexo = anexarArquivo(novoItemElemento, anexoItem.getLink(), inputStream, true);
        if (isConclusaoCorec) {
            novoAnexo.setUltimaAtualizacao(anexoItem.getUltimaAtualizacao());
            novoAnexo.setOperadorAtualizacao(anexoItem.getOperadorAtualizacao());
            novoAnexo.setAlterarDataUltimaAtualizacao(false);
            anexoDocumentoDao.update(novoAnexo);
            novoAnexo.getDocumento();
        }
    }

    @Transactional
    public String excluirAnexo(AnexoDocumentoVo anexoAqtVo, ItemElementoAQT itemElemento, Ciclo ciclo) {
        new RegraEdicaoANEFInspetorPermissaoAlteracao(itemElemento.getElemento().getAnaliseQuantitativaAQT()).validar();
        AnexoDocumento anexoARC = buscarAnexoPk(anexoAqtVo.getPk());

        atualizarItem(itemElemento);
        analiseQuantitativaAQTMediator.alterarEstadoANEFParaEmEdicao(itemElemento.getElemento()
                .getAnaliseQuantitativaAQT());
        anexoDocumentoDao.evict(anexoARC);
        
        try {
	        anexoDocumentoDao.delete(anexoARC);
	        
	        if (!SisapsUtil.isExecucaoTeste()) {
	            SmbFile arqSubDiretorio = getSmbFileDocumento(anexoARC.getLink(), itemElemento, ciclo);
	            bcJcifs.apagarArquivo(DIRETORIO_ANEXOS_ITEM_ELEMENTO, anexoARC.getLink(), MSG_ERRO_EXCLUIR_ARQUIVO_DE_ANEXO
	                    + anexoARC.getLink(), arqSubDiretorio);
	        }
	        } catch (Exception e) {
				// TODO: handle exception
			}
        return "Arquivo excluído com sucesso.";
    }

    private void atualizarItem(ItemElementoAQT item) {
        UsuarioAplicacao usuario = (UsuarioAplicacao) UsuarioCorrente.get();
        item.setOperadorAlteracao(usuario.getLogin());
        item.setDataAlteracao(DataUtil.getDateTimeAtual());
        item.getElemento().getAnaliseQuantitativaAQT();
        itemElementoAQTDAO.update(item);
        itemElementoAQTDAO.flush();

    }

    private SmbFile getSmbFileDocumento(String nomeArquivo, ItemElementoAQT itemElemento, Ciclo ciclo) {
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
                    arqSubDiretorio = obterArquivoSubDiretorio(nomeArquivo, itemElemento, cicloAnterior, esAnterior);
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

    private SmbFile obterArquivoSubDiretorio(String nomeArquivo, ItemElementoAQT itemElemento, Ciclo ciclo,
            EntidadeSupervisionavel es) {
        SmbFile arqSubDiretorio = null;
        try {
            SmbFile smbFile = AnexoArvoreDiretorioMediator.get().obterArvoreDiretoriosItemElementoAQT(ciclo, es,
                    itemElemento, DIRETORIO_ANEXOS_ITEM_ELEMENTO);
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

    private AnexoDocumento criarAnexoDocumento(ItemElementoAQT itemElemento, String link, InputStream inputStream) {
        AnexoDocumento anexoDocumento = null;
        if (itemElemento.getDocumento() == null || itemElemento.getDocumento().getPk() == null) {
            itemElemento.setDocumento(new Documento());
            itemElemento.getDocumento().setOperadorAtualizacao(UsuarioCorrente.get().getLogin());
            itemElemento.getDocumento().setOperadorAlteracao(UsuarioCorrente.get().getLogin());
            itemElemento.getDocumento().setUltimaAtualizacao(new DateTime());
            itemElemento.getDocumento().setDataAlteracao(new DateTime());
            DocumentoMediator.get().salvar(itemElemento.getDocumento());
            itemElementoAQTDAO.saveOrUpdate(itemElemento);
        }
        if (itemElemento.getDocumento() != null && itemElemento.getDocumento().getPk() != null) {
            anexoDocumento = anexoDocumentoDao.buscarAnexoDocumentoMesmoNome(itemElemento.getDocumento(), link);
        }
        if (anexoDocumento == null) {
            anexoDocumento = new AnexoDocumento();
            anexoDocumento.setLink(link);
            anexoDocumento.setDocumento(itemElemento.getDocumento());
        }
        
     
        anexoDocumento.setInputStream(inputStream);
        return anexoDocumento;
    }
}
