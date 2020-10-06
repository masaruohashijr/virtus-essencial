package crt2.dominio.perfilderisco.gerenciardetalheses;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import br.gov.bcb.sisaps.src.dominio.AnexoDocumento;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ConclusaoES;
import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.GrauPreocupacaoES;
import br.gov.bcb.sisaps.src.dominio.PerfilAtuacaoES;
import br.gov.bcb.sisaps.src.dominio.PerspectivaES;
import br.gov.bcb.sisaps.src.dominio.SituacaoES;
import br.gov.bcb.sisaps.src.mediator.AnexoDocumentoMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.ConclusaoESMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilAtuacaoESMediator;
import br.gov.bcb.sisaps.src.validacao.RegraAnexosValidacaoPDFA4;
import br.gov.bcb.sisaps.src.vo.AnexoDocumentoVo;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestR004Gerenciar_detalhes_da_ES_Anexos extends ConfiguracaoTestesNegocio {

    private static final String EXCLUIR_ANEXO = "Excluir anexo";
    private static final String ANEXAR_NOVO_ARQUIVO = "Anexar novo arquivo";
    private static final String SITUACAO = "Situação";
    private static final String PERSPECTIVA = "Perspectiva";
    private static final String CONCLUSAO = "Conclusão";
    private static final String PERFIL_DE_ATUACAO = "Perfil de atuação";
    private static final String GRAU_DE_PREOCUPACAO = "Grau de preocupação";
    private String ciclo;
    private String perfil;
    private String secao;
    private String selecione;
    private String justificativa;
    private String execute;
    private String selecionaOArquivo;
    private PerfilAtuacaoES perfilES;
    private PerspectivaES perspectivaES;
    private GrauPreocupacaoES grauES;
    private ConclusaoES conclusaoES;
    private SituacaoES situacaoES;
    private boolean naoMostrar;
    private String arquivo;
    
    public String mensagem() {
        String msg = "";
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(Integer.valueOf(getCiclo()));
        try {
            Util.setIncluirBufferAnexos(true);
            String nomeArquivo = null;
            if (!getExecute().equals(EXCLUIR_ANEXO)) {
                 nomeArquivo = arquivo;
            }

            if (getSecao().equals(PERFIL_DE_ATUACAO)) {
                perfilES = PerfilAtuacaoESMediator.get().getPerfilAtuacaoESSemPerfilRisco(ciclo);
                if (perfilES == null) {
                    perfilES = new PerfilAtuacaoES();
                    perfilES.setDocumento(new Documento());
                    perfilES.setCiclo(ciclo);
                }
                if (getExecute().equals(ANEXAR_NOVO_ARQUIVO)) {
                    RegraAnexosValidacaoPDFA4 validacao = new RegraAnexosValidacaoPDFA4();
                    validacao.validar(getInputStream(getSelecionaOArquivo()), nomeArquivo);
                    PerfilAtuacaoESMediator.get().salvarAnexo(ciclo, perfilES,
                            nomeArquivo, getInputStream(getSelecionaOArquivo()), true);
                }
                if (getExecute().equals(EXCLUIR_ANEXO)) {
                    AnexoDocumento anexo =
                            AnexoDocumentoMediator.get().buscarAnexoDocumentoMesmoNome(perfilES.getDocumento(),
                                    getSelecionaOArquivo());
                    AnexoDocumentoVo anexoVo = new AnexoDocumentoVo();
                    anexoVo.setPk(anexo.getPk());
                    PerfilAtuacaoESMediator.get().excluirAnexo(anexoVo, perfilES,
                            ciclo, true);
                }
            }

            if (getSecao().equals(CONCLUSAO)) {
                conclusaoES = ConclusaoESMediator.get().getConclusaoESSemPerfilRisco(ciclo);
                if (conclusaoES == null) {
                    conclusaoES = new ConclusaoES();
                    conclusaoES.setDocumento(new Documento());
                    conclusaoES.setCiclo(ciclo);
                }
                if (getExecute().equals(ANEXAR_NOVO_ARQUIVO)) {
                    RegraAnexosValidacaoPDFA4 validacao = new RegraAnexosValidacaoPDFA4();
                    validacao.validar(getInputStream(getSelecionaOArquivo()), nomeArquivo);
                    ConclusaoESMediator.get().salvarAnexo(ciclo,
                            conclusaoES, nomeArquivo, getInputStream(getSelecionaOArquivo()), true);
                }
                if (getExecute().equals(EXCLUIR_ANEXO)) {
                    AnexoDocumento anexo =
                            AnexoDocumentoMediator.get().buscarAnexoDocumentoMesmoNome(conclusaoES.getDocumento(),
                                    getSelecionaOArquivo());
                    AnexoDocumentoVo anexoVo = new AnexoDocumentoVo();
                    anexoVo.setPk(anexo.getPk());
                    ConclusaoESMediator.get().excluirAnexo(anexoVo, conclusaoES,
                            ciclo, true);
                }

            }

        } catch (FileNotFoundException e) {
            msg = "Nenhum arquivo foi selecionado.";
        } catch (NegocioException e) {
            msg = e.getMessage().replace("[", "").replace("]", "");
        }
        if (msg == null || "null".equals(msg)) {
            msg = "";
        }
        return msg;
    }

    private InputStream getInputStream(String caminhoAnexo) throws FileNotFoundException {
        File arquivo = new File(caminhoAnexo);
        return new FileInputStream(arquivo);
    }

    public String botaoConfirmarHabilitado() {
        if (getSecao().equals(GRAU_DE_PREOCUPACAO)) {
            return SimNaoEnum.getTipo(!naoMostrar).getDescricao();
        }
        if (getSecao().equals(PERFIL_DE_ATUACAO)) {
            return SimNaoEnum.getTipo(SimNaoEnum.NAO.equals(perfilES.getPendente())).getDescricao();
        }
        if (getSecao().equals(CONCLUSAO)) {
            return SimNaoEnum.getTipo(SimNaoEnum.NAO.equals(conclusaoES.getPendente())).getDescricao();
        }
        if (getSecao().equals(PERSPECTIVA)) {
            return SimNaoEnum.getTipo(SimNaoEnum.NAO.equals(perspectivaES.getPendente())).getDescricao();
        }
        if (getSecao().equals(SITUACAO)) {
            return SimNaoEnum.getTipo(SimNaoEnum.NAO.equals(situacaoES.getPendente())).getDescricao();
        }
        return "";
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public String getSecao() {
        return secao;
    }

    public void setSecao(String secao) {
        this.secao = secao;
    }

    public String getSelecione() {
        return selecione;
    }

    public void setSelecione(String selecione) {
        this.selecione = selecione;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public String getExecute() {
        return execute;
    }

    public void setExecute(String execute) {
        this.execute = execute;
    }

    public PerfilAtuacaoES getPerfilES() {
        return perfilES;
    }

    public void setPerfilES(PerfilAtuacaoES perfilES) {
        this.perfilES = perfilES;
    }

    public PerspectivaES getPerspectivaES() {
        return perspectivaES;
    }

    public void setPerspectivaES(PerspectivaES perspectivaES) {
        this.perspectivaES = perspectivaES;
    }

    public GrauPreocupacaoES getGrauES() {
        return grauES;
    }

    public void setGrauES(GrauPreocupacaoES grauES) {
        this.grauES = grauES;
    }

    public ConclusaoES getConclusaoES() {
        return conclusaoES;
    }

    public void setConclusaoES(ConclusaoES conclusaoES) {
        this.conclusaoES = conclusaoES;
    }

    public SituacaoES getSitiacaoES() {
        return situacaoES;
    }

    public void setSitiacaoES(SituacaoES situacaoES) {
        this.situacaoES = situacaoES;
    }

    public String getSelecionaOArquivo() {
        return selecionaOArquivo;
    }

    public void setSelecionaOArquivo(String selecionaOArquivo) {
        this.selecionaOArquivo = selecionaOArquivo;
    }
    
    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

}
