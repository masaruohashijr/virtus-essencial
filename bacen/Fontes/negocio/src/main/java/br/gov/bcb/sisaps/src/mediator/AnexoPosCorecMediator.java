package br.gov.bcb.sisaps.src.mediator;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Hibernate;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.comum.excecoes.BCInfraException;
import br.gov.bcb.sisaps.adaptadores.bcjcifs.IBcJcifs;
import br.gov.bcb.sisaps.src.dao.AnexoPosCorecDAO;
import br.gov.bcb.sisaps.src.dominio.AnexoPosCorec;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.util.Constantes;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

@Service
@Scope("singleton")
public class AnexoPosCorecMediator {
    public static final String ATA = "Ata";

    public static final String OFICIO = "Ofício";
    private static final String PATTERN = "dd/MM/yyyy";
    private static final String DIRETORIO_ANEXOS_POS_COREC_ATA = "APS.ANEXO.POSCOREC.ATA.DIR";
    private static final String DIRETORIO_ANEXOS_POS_COREC_OFICIO = "APS.ANEXO.POSCOREC.OFICIO.DIR";
    private static final String MSG_ERRO_EXCLUIR_ARQUIVO_DE_ANEXO = "Erro ao tentar excluir arquivo de anexo: ";

    @Autowired
    private IBcJcifs bcJcifs;

    @Autowired
    private AnexoPosCorecDAO anexoPosCorecDao;

    public static AnexoPosCorecMediator get() {
        return SpringUtils.get().getBean(AnexoPosCorecMediator.class);
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings("RR_NOT_CHECKED")
    public byte[] recuperarArquivo(String nomeArquivo, Ciclo ciclo, String tipo) {

        try {
            SmbFile arqSubDiretorio = getSmbFilePosCorec(nomeArquivo, ciclo, tipo);
            return bcJcifs.recuperarArquivo(arqSubDiretorio.getCanonicalPath(), nomeArquivo, arqSubDiretorio);
        } catch (NegocioException e) {
            throw new NegocioException(e.getMessage(), e);
        }
    }

    public String caminhoArquivo(String nomeArquivo, Ciclo ciclo, String tipo) {
        try {
            SmbFile arqSubDiretorio = getSmbFilePosCorec(nomeArquivo, ciclo, tipo);
            return arqSubDiretorio.getCanonicalPath();
        } catch (NegocioException e) {
            throw new NegocioException(e.getMessage(), e);
        }
    }

    private SmbFile obterArquivoSubDiretorio(String nomeArquivo, Ciclo ciclo, String tipo, EntidadeSupervisionavel es) {
        SmbFile arqSubDiretorio = null;
        try {
            SmbFile smbFile =
                    AnexoArvoreDiretorioMediator.get().obterArvoreDiretoriosAnexoPosCorec(ciclo, es,
                            OFICIO.equals(tipo) ? DIRETORIO_ANEXOS_POS_COREC_OFICIO : DIRETORIO_ANEXOS_POS_COREC_ATA);
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
    public AnexoPosCorec buscarAnexoPk(Integer pkAnexo) {
        return anexoPosCorecDao.load(pkAnexo);
    }

    @Transactional(readOnly = true)
    public AnexoPosCorec buscarAnexoPkInicializado(Integer pkAnexo) {
        AnexoPosCorec anexo = anexoPosCorecDao.load(pkAnexo);
        if (anexo.getCiclo() != null) {
            Hibernate.initialize(anexo.getCiclo());
        }
        return anexo;
    }

    @Transactional(readOnly = true)
    public List<AnexoPosCorec> buscar(PerfilRisco perfilRisco) {
        List<VersaoPerfilRisco> versoesPerfilRisco =
                VersaoPerfilRiscoMediator.get().buscarVersoesPerfilRisco(perfilRisco.getPk(),
                        TipoObjetoVersionadorEnum.ANEXO_CICLO);
        if (CollectionUtils.isNotEmpty(versoesPerfilRisco)) {
            //return anexoPosCorecDao.buscarAnexosCicloPerfilRisco(versoesPerfilRisco);
            return null;
        } else {
            return new ArrayList<AnexoPosCorec>();
        }
    }

    @Transactional(readOnly = true)
    public List<AnexoPosCorec> listarAnexos(Ciclo ciclo, String tipo) {
        return anexoPosCorecDao.listarAnexos(ciclo, tipo);
    }

    public boolean existeAnexo(Ciclo ciclo, String tipo) {
        return !listarAnexos(ciclo, tipo).isEmpty();
    }

    @Transactional(readOnly = true)
    public AnexoPosCorec buscarAnexo(Ciclo ciclo, String tipo) {
        return anexoPosCorecDao.buscarAnexo(ciclo, tipo);
    }

    @Transactional
    public String anexarArquivo(Ciclo ciclo, String tipo, String dataEntrega, String nomeArquivo,
            InputStream inputStream) {
        Date date = null;
        if (OFICIO.equals(tipo)) {
            validarData(dataEntrega);
            date = DataUtil.dateFromString(dataEntrega);
        }

        AnexoPosCorec anexo = criarAnexo(ciclo, tipo, date, nomeArquivo, inputStream);

        // Salva/atualiza o anexo.
        anexoPosCorecDao.evict(anexo);
        anexoPosCorecDao.saveOrUpdate(anexo);

        // Reconsulta o objeto para atualizá-lo.
        anexoPosCorecDao.flush();
        anexo = anexoPosCorecDao.load(anexo.getPk());

        if (!SisapsUtil.isExecucaoTeste()) {
            try {
                if (inputStream != null) {
                    SmbFile smbFile =
                            AnexoArvoreDiretorioMediator.get().obterArvoreDiretoriosAnexoPosCorec(
                                    ciclo, ciclo.getEntidadeSupervisionavel(),
                                    OFICIO.equals(tipo) ? DIRETORIO_ANEXOS_POS_COREC_OFICIO
                                            : DIRETORIO_ANEXOS_POS_COREC_ATA);
                    GeradorAnexoMediator.get().gerarArquivoAnexoUpload(anexo, nomeArquivo,
                            OFICIO.equals(tipo) ? DIRETORIO_ANEXOS_POS_COREC_OFICIO : DIRETORIO_ANEXOS_POS_COREC_ATA,
                            smbFile.getCanonicalPath());
                }
            } catch (BCInfraException e) {
                throw new NegocioException(e.getMessage());
            } catch (SmbException e) {
                throw new NegocioException(e.getMessage());
                //CHECKSTYLE:OFF Exception deve ser tratada
            } catch (Exception e) {
                //CHECKSTYLE:ON
                throw new NegocioException("" + e.getMessage(), e); //NOPMD
            }
        }

        if (ATA.equals(tipo)) {
            return "Ata anexada com sucesso.";
        }
        return "Ofício anexado com sucesso.";
    }

    private void validarData(String dataEntrega) {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
        if ("".equals(dataEntrega) || dataEntrega == null) {
            SisapsUtil.adicionarErro(erros, new ErrorMessage(
                    "Campo 'Ofício entregue em' é de preenchimento obrigatório."), true);
        } else {
            SisapsUtil
                    .validarDataInvalida(dataEntrega, "Ofício entregue em", Constantes.FORMATO_DATA_COM_BARRAS, erros);
            SisapsUtil.lancarNegocioException(erros);

            LocalDate date = DateTimeFormat.forPattern(PATTERN).parseLocalDate(dataEntrega);
            SisapsUtil.adicionarErro(erros, new ErrorMessage(
                    "Campo 'Ofício entregue em' deve ser menor ou igual à data corrente."), (date).isAfter(DataUtil
                    .getDateTimeAtual().toLocalDate()));
        }
        SisapsUtil.lancarNegocioException(erros);

    }

    private AnexoPosCorec criarAnexo(Ciclo ciclo, String tipo, Date dataEntrega, String link, InputStream inputStream) {
        AnexoPosCorec anexo = null;
        anexo = new AnexoPosCorec();
        anexo.setLink(link);
        anexo.setTipo(tipo);
        anexo.setDataEntrega(dataEntrega);
        anexo.setInputStream(inputStream);
        anexo.setCiclo(ciclo);
        return anexo;
    }

    @Transactional
    public String excluirAnexo(Ciclo ciclo, String tipo) {
        if (ATA.equals(tipo)) {
            AgendaCorecMediator.get().excluirDataAssinaturaParticipantes(ciclo.getPk());
        }

        AnexoPosCorec anexo = buscarAnexo(ciclo, tipo);
        
        try {
	        anexoPosCorecDao.delete(anexo);
	        if (!SisapsUtil.isExecucaoTeste()) {
	            SmbFile arqSubDiretorio = getSmbFilePosCorec(anexo.getLink(), ciclo, anexo.getTipo());
	            bcJcifs.apagarArquivo(DIRETORIO_ANEXOS_POS_COREC_ATA, anexo.getLink(), MSG_ERRO_EXCLUIR_ARQUIVO_DE_ANEXO
	                    + anexo.getLink(), arqSubDiretorio);
	        }
        } catch (Exception e) {
			// TODO: handle exception
		}
        String msg = tipo + " excluída com sucesso.";
        if (OFICIO.equals(tipo)) {
            msg = tipo + " excluído com sucesso.";
        }

        return msg;
    }

    public void validarAnexosEncerrarCiclo(Ciclo ciclo) throws NegocioException {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
        if (isPossivelAnexarAta(ciclo) && buscarAnexo(ciclo, ATA) == null) {
            SisapsUtil.adicionarErro(erros, new ErrorMessage("O anexo da Ata é obrigatório."), true);
        }
        if (buscarAnexo(ciclo, OFICIO) == null) {
            SisapsUtil.adicionarErro(erros, new ErrorMessage("O anexo do Ofício é obrigatório."), true);
        }
        AgendaCorecMediator.get().ataNaoAssinadaParticipante(ciclo, erros);
        SisapsUtil.lancarNegocioException(erros);
    }

    public boolean isPossivelAnexarAta(Ciclo ciclo) {
        return !ciclo.getDataPrevisaoCorec().after(Constantes.DATA_LIMITE_ANEXO_ATA);
    }

    @Transactional(readOnly = true)
    public List<AnexoPosCorec> listarAnexos() {
        return anexoPosCorecDao.listarAnexos();
    }

    private SmbFile getSmbFilePosCorec(String nomeArquivo, Ciclo ciclo, String tipo) {
        List<EntidadeSupervisionavel> listaESs = EntidadeSupervisionavelMediator.get()
                .buscarEssPorCNPJ(ciclo.getEntidadeSupervisionavel().getConglomeradoOuCnpj(), ciclo.getPk() < 0);
        SmbFile arqSubDiretorio = null;
        for (EntidadeSupervisionavel es : listaESs) {
            arqSubDiretorio = obterArquivoSubDiretorio(nomeArquivo, ciclo, tipo, es);
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
