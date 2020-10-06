package br.gov.bcb.sisaps.src.mediator;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.hibernate.ObjetoPersistente;
import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.comum.excecoes.BCInfraException;
import br.gov.bcb.sisaps.adaptadores.bcjcifs.IBcJcifs;
import br.gov.bcb.sisaps.src.dao.AnexoDocumentoDao;
import br.gov.bcb.sisaps.src.dominio.AnexoDocumento;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ConclusaoES;
import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.ItemElemento;
import br.gov.bcb.sisaps.src.dominio.PerfilAtuacaoES;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ItemElementoAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEntidadeAnexoDocumentoEnum;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ItemElementoAQTMediator;
import br.gov.bcb.sisaps.src.util.AnexoBuffer;
import br.gov.bcb.sisaps.src.util.BufferAnexos;
import br.gov.bcb.sisaps.src.vo.AnexoDocumentoVo;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

@Service
@Scope("singleton")
public class AnexoDocumentoMediator {

    private static final String CAUSA_EXCECAO = "org.hibernate.TransientObjectException: object references an unsaved "
            + "transient instance - save the transient instance before flushing: "
            + "br.gov.bcb.sisaps.src.dominio.Documento";
    private static final String DIRETORIO_ANEXOS_DOCUMENTO = "APS.ANEXO.DOCUMENTO.DIR";
    private static final String MSG_ERRO_EXCLUIR_ARQUIVO_DE_ANEXO = "Erro ao tentar excluir arquivo de anexo: ";

    @Autowired
    private AnexoDocumentoDao anexoDocumentoDao;
    @Autowired
    private PerfilAtuacaoESMediator perfilAtuacaoESMediator;
    @Autowired
    private ConclusaoESMediator conclusaoESMediator;
    @Autowired
    private ItemElementoMediator itemElementoMediator;
    @Autowired
    private IBcJcifs bcJcifs;

    public static AnexoDocumentoMediator get() {
        return SpringUtils.get().getBean(AnexoDocumentoMediator.class);
    }

    @Transactional
    public AnexoDocumento anexarArquivo(Ciclo ciclo, ObjetoPersistente<Integer> entidadeAnexoDocumento,
            Documento documento, TipoEntidadeAnexoDocumentoEnum tipoEntidadeAnexoDocumento, String link,
            InputStream inputStream, boolean salvarEntidade) {
        AnexoDocumento anexo = criarAnexoDocumento(documento, link, inputStream);
        if (salvarEntidade) {
            salvarEntidadeAnexoDocumento(ciclo, entidadeAnexoDocumento, tipoEntidadeAnexoDocumento, !salvarEntidade);
        }

        try {
            if (inputStream != null) {
                SmbFile smbFile =
                        AnexoArvoreDiretorioMediator.get().obterArvoreDiretorios(ciclo, ciclo.getEntidadeSupervisionavel(), 
                                entidadeAnexoDocumento,
                                tipoEntidadeAnexoDocumento, DIRETORIO_ANEXOS_DOCUMENTO);
                GeradorAnexoMediator.get().gerarArquivoAnexoUpload(anexo, link, DIRETORIO_ANEXOS_DOCUMENTO,
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
        anexoDocumentoDao.saveOrUpdate(anexo);
        return anexo;
    }

    private void salvarEntidadeAnexoDocumento(Ciclo ciclo, ObjetoPersistente<Integer> entidadeAnexoDocumento,
            TipoEntidadeAnexoDocumentoEnum tipoEntidadeAnexoDocumento, boolean isSalvar) {
        switch (tipoEntidadeAnexoDocumento) {
            case ITEM_ELEMENTO:
                ItemElemento itemElemento = (ItemElemento) entidadeAnexoDocumento;
                itemElementoMediator.salvarJustificativaItemElementoARC(ciclo, itemElemento.getElemento()
                        .getAvaliacaoRiscoControle(), itemElemento, false);
                break;
            case PERFIL_ATUACAO:
                perfilAtuacaoESMediator.salvarNovoPerfilAtuacao((PerfilAtuacaoES) entidadeAnexoDocumento, isSalvar);
                break;
            case CONCLUSAO:
                conclusaoESMediator.salvarNovaConclusao((ConclusaoES) entidadeAnexoDocumento, isSalvar);
                break;
            default:
                break;
        }
    }

    private AnexoDocumento criarAnexoDocumento(Documento documento, String link, InputStream inputStream) {
        AnexoDocumento anexoDocumento = null;
        if (documento.getPk() != null) {
            anexoDocumento = anexoDocumentoDao.buscarAnexoDocumentoMesmoNome(documento, link);
        }
        if (anexoDocumento == null) {
            anexoDocumento = new AnexoDocumento();
            anexoDocumento.setLink(link);
            anexoDocumento.setDocumento(documento);
        }
        anexoDocumento.setInputStream(inputStream);
        return anexoDocumento;
    }

    @Transactional(readOnly = true)
    public List<AnexoDocumento> buscar(Documento documento) {
        if (documento != null && documento.getPk() != null) {
            try {
                return anexoDocumentoDao.buscarAnexos(documento);
            } catch (InvalidDataAccessApiUsageException e) {
                return tratarExcecao(documento, e);
            }
        }
        return new ArrayList<AnexoDocumento>();
    }

    public List<AnexoDocumento> buscar(ItemElemento item) {
        if (item != null && item.getPk() != null) {
            ItemElemento itemElemento = ItemElementoMediator.get().buscarPorPk(item.getPk());
            return buscar(itemElemento.getDocumento());
        }
        return new ArrayList<AnexoDocumento>();
    }

    private List<AnexoDocumento> tratarExcecao(Documento documento, InvalidDataAccessApiUsageException e) {
        // super gambiarra
        if (e.getCause().toString().equals(CAUSA_EXCECAO)) {
            return anexoDocumentoDao.buscarAnexos(documento);
        } else {
            throw e;
        }
    }

    public List<AnexoDocumento> buscar(ItemElementoAQT item) {
        if (item != null && item.getPk() != null) {
            ItemElementoAQT itemElementoAQT = ItemElementoAQTMediator.get().buscarPorPk(item.getPk());
            return buscar(itemElementoAQT.getDocumento());
        }
        return new ArrayList<AnexoDocumento>();
    }

    @Transactional
    public void excluirAnexo(AnexoDocumentoVo anexoVo, ObjetoPersistente<Integer> entidadeAnexoDocumento,
            TipoEntidadeAnexoDocumentoEnum tipoEntidadeAnexoDocumento, Ciclo ciclo) {
        Util.setIncluirBufferAnexos(true);
        BufferAnexos.resetLocalThreadBufferExclusao();
        AnexoDocumento anexo = buscarAnexoPk(anexoVo.getPk());
        excluirAnexo(anexo, entidadeAnexoDocumento, tipoEntidadeAnexoDocumento, ciclo);
        GeradorAnexoMediator.get().excluirAnexosBuffer();
    }

    @Transactional
    public void excluirAnexo(AnexoDocumento anexo, ObjetoPersistente<Integer> entidadeAnexoDocumento,
            TipoEntidadeAnexoDocumentoEnum tipoEntidadeAnexoDocumento, Ciclo ciclo) {
        if (!SisapsUtil.isExecucaoTeste()) {
            SmbFile arqSubDiretorio =
                    getSmbFileDocumento(anexo.getLink(), entidadeAnexoDocumento, tipoEntidadeAnexoDocumento, ciclo);
            if (Util.isIncluirBufferAnexos()) {
                AnexoBuffer anexoBuffer = new AnexoBuffer();
                anexoBuffer.setAlias(DIRETORIO_ANEXOS_DOCUMENTO);
                anexoBuffer.setNome(anexo.getLink());
                anexoBuffer.setArquivoSmbFile(arqSubDiretorio);
                BufferAnexos.getBufferExclusaoAnexos().add(anexoBuffer);
            } else {
                try {
                    if (arqSubDiretorio.exists()) {
                        bcJcifs.apagarArquivo(DIRETORIO_ANEXOS_DOCUMENTO, anexo.getLink(), MSG_ERRO_EXCLUIR_ARQUIVO_DE_ANEXO
                                + anexo.getLink(), arqSubDiretorio);
                    }
                } catch (SmbException e) {
                    throw new NegocioException(e.getMessage(), e);
                }
            }
        }
        anexoDocumentoDao.delete(anexo);
        anexo.getDocumento().getAnexosItemElemento().remove(anexo);
        DocumentoMediator.get().salvar(anexo.getDocumento());
    }

    @Transactional(readOnly = true)
    public AnexoDocumento buscarAnexoPk(Integer pkAnexo) {
        return anexoDocumentoDao.load(pkAnexo);
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings("RR_NOT_CHECKED")
    public byte[] recuperarArquivo(String nomeArquivo, ObjetoPersistente<Integer> entidadeAnexoDocumento,
            TipoEntidadeAnexoDocumentoEnum tipoEntidadeAnexoDocumento, Ciclo ciclo) {
        try {
            SmbFile arqSubDiretorio =
                    getSmbFileDocumento(nomeArquivo, entidadeAnexoDocumento, tipoEntidadeAnexoDocumento, ciclo);
            return bcJcifs.recuperarArquivo(arqSubDiretorio.getCanonicalPath(), nomeArquivo, arqSubDiretorio);
        } catch (NegocioException e) {
            throw new NegocioException(e.getMessage(), e);
        }

    }

    public String caminhoArquivo(String nomeArquivo, ObjetoPersistente<Integer> entidadeAnexoDocumento,
            TipoEntidadeAnexoDocumentoEnum tipoEntidadeAnexoDocumento, Ciclo ciclo) {
        try {
            SmbFile arqSubDiretorio =
                    getSmbFileDocumento(nomeArquivo, entidadeAnexoDocumento, tipoEntidadeAnexoDocumento, ciclo);
            return arqSubDiretorio.getCanonicalPath();
        } catch (NegocioException e) {
            throw new NegocioException(e.getMessage(), e);
        }
    }

    private SmbFile obterArquivoSubDiretorio(String nomeArquivo, ObjetoPersistente<Integer> entidadeAnexoDocumento,
            TipoEntidadeAnexoDocumentoEnum tipoEntidadeAnexoDocumento, Ciclo ciclo, EntidadeSupervisionavel es) {
        SmbFile arqSubDiretorio = null;
        try {
            SmbFile smbFile =
                    AnexoArvoreDiretorioMediator.get().obterArvoreDiretorios(ciclo, es, entidadeAnexoDocumento,
                            tipoEntidadeAnexoDocumento, DIRETORIO_ANEXOS_DOCUMENTO);
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
    public void salvar(Documento documento, String link, InputStream inputStream) {
        AnexoDocumento anexo = new AnexoDocumento();
        anexo.setLink(link);
        anexo.setDocumento(documento);
        anexo.setInputStream(inputStream);
        anexoDocumentoDao.saveOrUpdate(anexo);
    }

    @Transactional
    public void copiarDadosAnexoARCAnterior(Ciclo ciclo, ItemElemento item, ItemElemento itemAnterior) {
        excluirAnexosDocumento(ciclo, item);
        anexoDocumentoDao.flush();
        AnexoItemElementoMediator.get().duplicarAnexosItemElementoARCConclusaoAnalise(ciclo, itemAnterior, item, true);
    }

    @Transactional
    public void excluirAnexosDocumento(Ciclo ciclo, ItemElemento item) {
        List<AnexoDocumento> anexosItemElemento = new ArrayList<AnexoDocumento>();
        anexosItemElemento.addAll(item.getDocumento().getAnexosItemElemento());
        for (AnexoDocumento anexo : anexosItemElemento) {
            excluirAnexo(anexo, item, TipoEntidadeAnexoDocumentoEnum.ITEM_ELEMENTO, ciclo);
        }
    }

    @Transactional
    public void saveOrUpdate(AnexoDocumento anexoDocumento) {
        anexoDocumentoDao.saveOrUpdate(anexoDocumento);
    }

    @Transactional(readOnly = true)
    public AnexoDocumento buscarAnexoDocumentoMesmoNome(Documento documento, String link) {
        return anexoDocumentoDao.buscarAnexoDocumentoMesmoNome(documento, link);
    }

    @Transactional(readOnly = true)
    public List<AnexoDocumentoVo> listarAnexosConclusao() {
        return anexoDocumentoDao.listarAnexosConclusao();
    }

    @Transactional(readOnly = true)
    public List<AnexoDocumentoVo> listarAnexosPerfilAtuacao() {
        return anexoDocumentoDao.listarAnexosPerfilAtuacao();
    }

    @Transactional(readOnly = true)
    public List<AnexoDocumentoVo> listarAnexosItemAQT() {
        return anexoDocumentoDao.listarAnexosItemAQT();
    }

    @Transactional(readOnly = true)
    public List<AnexoDocumentoVo> listarAnexosItemARC() {
        return anexoDocumentoDao.listarAnexosItemARC();
    }

    private SmbFile getSmbFileDocumento(String nomeArquivo, ObjetoPersistente<Integer> entidadeAnexoDocumento,
            TipoEntidadeAnexoDocumentoEnum tipoEntidadeAnexoDocumento, Ciclo ciclo) {
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
                    arqSubDiretorio = obterArquivoSubDiretorio(nomeArquivo, entidadeAnexoDocumento,
                            tipoEntidadeAnexoDocumento, cicloAnterior, esAnterior);
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