package br.gov.bcb.sisaps.src.mediator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.comum.excecoes.BCInfraException;
import br.gov.bcb.sisaps.src.dao.AnexoDocumentoDao;
import br.gov.bcb.sisaps.src.dominio.AnexoDocumento;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ItemElemento;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEntidadeAnexoDocumentoEnum;
import br.gov.bcb.sisaps.src.util.BufferAnexos;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ConstantesMensagens;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

@Service
@Scope("singleton")
public class AnexoItemElementoMediator {

    private static final String DIRETORIO_ANEXOS_ITEM_ELEMENTO = "APS.ANEXO.DOCUMENTO.DIR";
    @Autowired
    private AnexoDocumentoDao anexoItemElementoDao;

    @Autowired
    private ItemElementoMediator itemElementoMediator;

    public static AnexoItemElementoMediator get() {
        return SpringUtils.get().getBean(AnexoItemElementoMediator.class);
    }
    
    private void anexarArquivo(AvaliacaoRiscoControle arc, Ciclo ciclo, ItemElemento itemElemento, String link,
            InputStream inputStream, boolean isDuplicacaoARC, AnexoDocumento anexoItem, 
            boolean isCopiarUsuarioAnexoAnterior) {
        if (!isDuplicacaoARC) {
            Util.setIncluirBufferAnexos(true);
            BufferAnexos.resetLocalThreadBufferInclusao();
        }
        AnexoDocumento anexo = salvar(ciclo, itemElemento, link, inputStream, 
                isDuplicacaoARC, anexoItem, isCopiarUsuarioAnexoAnterior);

        try {
            if (inputStream != null) {
                SmbFile smbFile =
                        AnexoArvoreDiretorioMediator.get().obterArvoreDiretoriosItemElemento(ciclo,
                                ciclo.getEntidadeSupervisionavel(), itemElemento,
                                DIRETORIO_ANEXOS_ITEM_ELEMENTO);
                GeradorAnexoMediator.get().gerarArquivoAnexoUpload(anexo, link, DIRETORIO_ANEXOS_ITEM_ELEMENTO,
                        smbFile.getCanonicalPath());
                if (!isDuplicacaoARC) {
                    AvaliacaoRiscoControleMediator.get().alterarEstadoARCParaEmEdicao(arc);
                }
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
        if (!isDuplicacaoARC) {
            GeradorAnexoMediator.get().incluirAnexosBuffer();
        }
    }

    @Transactional
    public void anexarArquivo(AvaliacaoRiscoControle arc, Ciclo ciclo, ItemElemento itemElemento, String link,
            InputStream inputStream, boolean isDuplicacaoARC) {
        anexarArquivo(arc, ciclo, itemElemento, link, inputStream, isDuplicacaoARC, null, false);
    }

    @Transactional
    private AnexoDocumento salvar(Ciclo ciclo, ItemElemento itemElemento, String link, InputStream inputStream,
            boolean isDuplicacaoARC, AnexoDocumento anexoItem, boolean isCopiarUsuarioAnexoAnterior) {
        itemElementoMediator.salvarJustificativaItemElementoARC(ciclo, itemElemento.getElemento()
                .getAvaliacaoRiscoControle(), itemElemento, isDuplicacaoARC);
        return salvarAnexoItem(itemElemento, link, inputStream, anexoItem, isCopiarUsuarioAnexoAnterior);
    }

    private AnexoDocumento salvarAnexoItem(ItemElemento itemElemento, String link, InputStream inputStream, 
            AnexoDocumento anexoItem, boolean isCopiarUsuarioAnexoAnterior) {
        AnexoDocumento anexo = null;
        if (itemElemento.getDocumento().getPk() != null) {
            anexo = anexoItemElementoDao.buscarAnexoDocumentoMesmoNome(itemElemento.getDocumento(), link);
        }
        if (anexo == null) {
            anexo = new AnexoDocumento();
            anexo.setLink(link);
            if (isCopiarUsuarioAnexoAnterior) {
                anexo.setOperadorAtualizacao(anexoItem.getOperadorAtualizacao());
                anexo.setUltimaAtualizacao(anexoItem.getUltimaAtualizacao());
                anexo.setAlterarDataUltimaAtualizacao(false);
            }
            anexo.setDocumento(itemElemento.getDocumento());
            anexo.setInputStream(inputStream);
            anexoItemElementoDao.save(anexo);
        } else {
            System.out.println("###### salvarAnexoItem");
            System.out.println("#### itemElemento: " + itemElemento.getPk());
            System.out.println("#### anexo: " + link);
            SisapsUtil.lancarNegocioException(new ErrorMessage(ConstantesMensagens.MSG_APS_ANEXO_ERROR_001));
        }
        return anexo;
    }

    @Transactional
    public void duplicarAnexosItemElementoARCConclusaoAnalise(Ciclo ciclo, ItemElemento itemElemento,
            ItemElemento novoItemElemento, boolean isCopiarUsuarioAnexoAnterior) {
        if (itemElemento.getDocumento() != null) {
            for (AnexoDocumento anexoDocumento : itemElemento.getDocumento().getAnexosItemElemento()) {
                ByteArrayInputStream inputStream = null;
                if (!SisapsUtil.isExecucaoTeste()) {
                    byte[] arquivoAnexo =
                            AnexoDocumentoMediator.get().recuperarArquivo(anexoDocumento.getLink(), itemElemento,
                                    TipoEntidadeAnexoDocumentoEnum.ITEM_ELEMENTO, ciclo);
                    inputStream = new ByteArrayInputStream(arquivoAnexo);
                }
                anexarArquivo(null, ciclo, novoItemElemento, anexoDocumento.getLink(), inputStream, 
                        true, anexoDocumento, isCopiarUsuarioAnexoAnterior);
            }
        }
    }
    
    @Transactional
    public void duplicarAnexosItemElementoARCConclusaoCorec(Ciclo cicloAnterior, Ciclo cicloNovo, ItemElemento itemElemento,
            ItemElemento novoItemElemento, boolean isCopiarUsuarioAnexoAnterior) {
        if (itemElemento.getDocumento() != null) {
            List<AnexoDocumento> anexos = AnexoDocumentoMediator.get().buscar(itemElemento.getDocumento());
            for (AnexoDocumento anexoDocumento : anexos) {
                ByteArrayInputStream inputStream = null;
                if (!SisapsUtil.isExecucaoTeste()) {
                    byte[] arquivoAnexo =
                            AnexoDocumentoMediator.get().recuperarArquivo(anexoDocumento.getLink(), itemElemento,
                                    TipoEntidadeAnexoDocumentoEnum.ITEM_ELEMENTO, cicloAnterior);
                    inputStream = new ByteArrayInputStream(arquivoAnexo);
                }
                anexarArquivo(null, cicloNovo, novoItemElemento, anexoDocumento.getLink(), inputStream, 
                        true, anexoDocumento, isCopiarUsuarioAnexoAnterior);
            }
        }
    }

}
