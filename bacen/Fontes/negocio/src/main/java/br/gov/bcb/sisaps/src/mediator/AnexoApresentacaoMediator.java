package br.gov.bcb.sisaps.src.mediator;

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
import br.gov.bcb.sisaps.src.dao.AnexoApresentacaoDao;
import br.gov.bcb.sisaps.src.dominio.AnexoApresentacao;
import br.gov.bcb.sisaps.src.dominio.Apresentacao;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.CampoApresentacaoEnum;
import br.gov.bcb.sisaps.src.vo.AnexoApresentacaoVO;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

@Service
@Scope("singleton")
public class AnexoApresentacaoMediator {

    private static final String DIRETORIO_ANEXOS_DOCUMENTO = "APS.ANEXO.APRESENTACAO.DIR";
    private static final String MSG_ERRO_EXCLUIR_ARQUIVO_DE_ANEXO = "Erro ao tentar excluir arquivo de anexo: ";

    @Autowired
    private AnexoApresentacaoDao anexoApresentacaoDao;
    @Autowired
    private IBcJcifs bcJcifs;

    // Retorna a instância do mediator.
    public static AnexoApresentacaoMediator get() {
        return SpringUtils.get().getBean(AnexoApresentacaoMediator.class);
    }

    @Transactional
    public AnexoApresentacaoVO anexarArquivo(Integer apresentacaoPk, String link, CampoApresentacaoEnum campo,
            InputStream inputStream) {
        // Declarações
        Apresentacao apresentacao;
        AnexoApresentacao anexoApresentacao;
        SmbFile smbFile;

        // Recupera a apresentação.
        apresentacao = ApresentacaoMediator.get().buscarPk(apresentacaoPk);

        // Cria o anexo.
        anexoApresentacao = criarAnexoApresentacao(apresentacao, link, campo, inputStream);

        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(apresentacao.getCiclo().getPk());

        try {
            // Valida os dados.
            if (inputStream != null) {
                // Recupera o diretório onde o arquivo será salvo.

                smbFile = AnexoArvoreDiretorioMediator.get().obterArvoreDiretoriosAnexoApresentacao(apresentacao,
                        ciclo.getEntidadeSupervisionavel(), DIRETORIO_ANEXOS_DOCUMENTO);

                // Salva o arquivo.
                GeradorAnexoMediator.get().gerarArquivoAnexoUpload(anexoApresentacao, link, DIRETORIO_ANEXOS_DOCUMENTO,
                        smbFile.getCanonicalPath());
            }
        } catch (BCInfraException e) {
            throw new NegocioException(e.getMessage());
        } catch (SmbException e) {
            throw new NegocioException(e.getMessage());
            // CHECKSTYLE:OFF Exception deve ser tratada
        } catch (Exception e) {
            // CHECKSTYLE:ON
            throw new NegocioException(e.getMessage()); // NOPMD
        }

        // Salva/atualiza o anexo.
        anexoApresentacaoDao.evict(anexoApresentacao);
        anexoApresentacaoDao.saveOrUpdate(anexoApresentacao);

        // Reconsulta o objeto para atualizá-lo.
        anexoApresentacaoDao.flush();
        anexoApresentacao = anexoApresentacaoDao.load(anexoApresentacao.getPk());

        return anexoApresentacao.toVO();
    }

    // Cria o anexo.
    private AnexoApresentacao criarAnexoApresentacao(Apresentacao apresentacao, String link,
            CampoApresentacaoEnum campo, InputStream inputStream) {
        // Declarações
        AnexoApresentacao anexoApresentacao;

        // Inicializações
        anexoApresentacao = null;

        // Verifica se já existe um anexo com o mesmo nome na mesma seção.
        anexoApresentacao = anexoApresentacaoDao.buscarAnexoDocumentoMesmoNome(apresentacao, link, campo);

        if (anexoApresentacao == null) {
            // Novo anexo.
            anexoApresentacao = new AnexoApresentacao();
            anexoApresentacao.setLink(link);
            anexoApresentacao.setSecao(campo.getSecao());
            anexoApresentacao.setApresentacao(apresentacao);
        }
        anexoApresentacao.setInputStream(inputStream);

        return anexoApresentacao;
    }

    // Exclui um anexo.
    @Transactional
    public void excluirAnexo(AnexoApresentacaoVO anexoVo) {
        // Declarações
        List<AnexoApresentacao> anexos;
        AnexoApresentacao anexo;
        SmbFile arqSubDiretorio;

        // Recupera o anexo.
        anexo = anexoApresentacaoDao.load(anexoVo.getPk());

        // Verifica quantos anexos apontam para o arquivo.
        anexos = anexoApresentacaoDao.buscarAnexoDocumentoMesmoNome(anexo.getApresentacao(), anexo.getLink());
        
        try {
        	anexoApresentacaoDao.delete(anexo);
            // Se for apenas um.
            if (anexos.size() <= 1) {
                // Apaga o arquivo.
                arqSubDiretorio = getSmbFileApresentacao(anexoVo);
                bcJcifs.apagarArquivo(DIRETORIO_ANEXOS_DOCUMENTO, anexo.getLink(), MSG_ERRO_EXCLUIR_ARQUIVO_DE_ANEXO
                        + anexo.getLink(), arqSubDiretorio);
            }
		} catch (Exception e) {
			// TODO: handle exception
		}
        
        
        
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("RR_NOT_CHECKED")
    public byte[] recuperarArquivo(AnexoApresentacaoVO anexoVo) {
        SmbFile arqSubDiretorio = getSmbFileApresentacao(anexoVo);

        try {
            return bcJcifs.recuperarArquivo(arqSubDiretorio.getCanonicalPath(), anexoVo.getLink(), arqSubDiretorio);
        } catch (NegocioException e) {
            throw new NegocioException(e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public String caminhoArquivo(AnexoApresentacaoVO anexoVo) {
        try {
            SmbFile arqSubDiretorio = getSmbFileApresentacao(anexoVo);
            return arqSubDiretorio.getCanonicalPath();
        } catch (NegocioException e) {
            throw new NegocioException(e.getMessage(), e);
        }
    }

    private SmbFile obterArquivoSubDiretorio(String nomeArquivo, Apresentacao apresentacao,
            EntidadeSupervisionavel es) {
        SmbFile arqSubDiretorio = null;
        try {
            SmbFile smbFile =
                    AnexoArvoreDiretorioMediator.get().obterArvoreDiretoriosAnexoApresentacao(apresentacao, es,
                            DIRETORIO_ANEXOS_DOCUMENTO);
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
    public List<AnexoApresentacao> listarAnexos() {
        return anexoApresentacaoDao.listarAnexos();
    }
    
	private SmbFile getSmbFileApresentacao(AnexoApresentacaoVO anexoVo) {
        AnexoApresentacao anexo = anexoApresentacaoDao.load(anexoVo.getPk());
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(anexo.getApresentacao().getCiclo().getPk());
        List<EntidadeSupervisionavel> listaESs = EntidadeSupervisionavelMediator.get()
                .buscarEssPorCNPJ(ciclo.getEntidadeSupervisionavel().getConglomeradoOuCnpj(), ciclo.getPk() < 0);
        SmbFile arqSubDiretorio = null;
        for (EntidadeSupervisionavel es : listaESs) {
            arqSubDiretorio = obterArquivoSubDiretorio(anexoVo.getLink(), anexo.getApresentacao(), es);
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
