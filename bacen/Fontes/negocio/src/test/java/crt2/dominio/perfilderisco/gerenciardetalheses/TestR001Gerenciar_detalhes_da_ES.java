package crt2.dominio.perfilderisco.gerenciardetalheses;

import java.math.BigDecimal;

import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ConclusaoES;
import br.gov.bcb.sisaps.src.dominio.Documento;
import br.gov.bcb.sisaps.src.dominio.GrauPreocupacaoES;
import br.gov.bcb.sisaps.src.dominio.PerfilAtuacaoES;
import br.gov.bcb.sisaps.src.dominio.PerspectivaES;
import br.gov.bcb.sisaps.src.dominio.SituacaoES;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.ConclusaoESMediator;
import br.gov.bcb.sisaps.src.mediator.GrauPreocupacaoESMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroNotaMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroPerspectivaMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroSituacaoMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilAtuacaoESMediator;
import br.gov.bcb.sisaps.src.mediator.PerspectivaESMediator;
import br.gov.bcb.sisaps.src.mediator.SituacaoESMediator;
import br.gov.bcb.sisaps.util.enumeracoes.SimNaoEnum;
import crt2.ConfiguracaoTestesNegocio;

@Transactional
public class TestR001Gerenciar_detalhes_da_ES extends ConfiguracaoTestesNegocio {

    private static final String SITUACAO = "Situação";
    private static final String PERSPECTIVA = "Perspectiva";
    private static final String CONCLUSAO = "Conclusão";
    private static final String SALVAR_INFORMACOES = "Salvar informações";
    private static final String PERFIL_DE_ATUACAO = "Perfil de atuação";
    private static final String NOTA_FINAL = "Nota final";
    private String ciclo;
    private String perfil;
    private String secao;
    private String selecione;
    private String justificativa;
    private String execute;
    private PerfilAtuacaoES perfilES;
    private PerspectivaES perspectivaES;
    private GrauPreocupacaoES grauES;
    private ConclusaoES conclusaoES;
    private SituacaoES situacaoES;
    private boolean naoMostrar;

    public String mensagem() {
        String msg = "";
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(Integer.valueOf(getCiclo()));
        try {

            if (getSecao().equals(NOTA_FINAL)) {
                    grauES = GrauPreocupacaoESMediator.get().buscarGrauPreocupacaoESRascunho(ciclo.getPk());
                if (grauES == null) {
                    grauES = new GrauPreocupacaoES();
                    grauES.setCiclo(ciclo);
                }
                if ("Selecione".equals(getSelecione()) || "".equals(getSelecione())) {
                    grauES.setParametroNota(null);
                } else {
                    grauES.setParametroNota(ParametroNotaMediator.get()
                            .buscarParametrosMetodologiaEDescricao(
                                    ciclo.getMetodologia().getPk(), getSelecione()));
                    grauES.setJustificativa(justificativa);
                    grauES.setNumeroFatorRelevanciaAnef(new BigDecimal(0.3));
                }

                String encaminharNovoGrauPreocupacao;
                if (getExecute().equals(SALVAR_INFORMACOES)) {
                    encaminharNovoGrauPreocupacao =
                            GrauPreocupacaoESMediator.get().salvarNovoGrauPreocupacao(grauES);
                    
                } else {
                    encaminharNovoGrauPreocupacao =
                            GrauPreocupacaoESMediator.get().confirmarNovoGrauPreocupacao(grauES, "2");
                }
                if ("Nota final da ES encaminhada para aprovação do gerente com sucesso."
                        .equals(encaminharNovoGrauPreocupacao)) {
                    naoMostrar = true;
                }

                return encaminharNovoGrauPreocupacao;

            }

            if (getSecao().equals(PERFIL_DE_ATUACAO)) {
                perfilES = PerfilAtuacaoESMediator.get().getPerfilAtuacaoESSemPerfilRisco(ciclo);
                if (perfilES == null) {
                    perfilES = new PerfilAtuacaoES();
                    perfilES.setDocumento(new Documento());
                    perfilES.setCiclo(ciclo);
                }
                if (getExecute().equals(SALVAR_INFORMACOES)) {
                    perfilES.getDocumento().setJustificativa(justificativa);
                    return PerfilAtuacaoESMediator.get().salvarNovoPerfilAtuacao(perfilES, true);
                } else {
                    return PerfilAtuacaoESMediator.get().encaminharNovoPerfilAtuacao(perfilES);
                }
            }

            if (getSecao().equals(CONCLUSAO)) {
                conclusaoES = ConclusaoESMediator.get().getConclusaoESSemPerfilRisco(ciclo);
                if (conclusaoES == null) {
                    conclusaoES = new ConclusaoES();
                    conclusaoES.setDocumento(new Documento());
                    conclusaoES.setCiclo(ciclo);
                }
                if (getExecute().equals(SALVAR_INFORMACOES)) {
                    conclusaoES.getDocumento().setJustificativa(justificativa);
                    return ConclusaoESMediator.get().salvarNovaConclusao(conclusaoES, true);
                } else {
                    return ConclusaoESMediator.get().encaminharNovaConclusao(conclusaoES);
                }
            }

            if (getSecao().equals(PERSPECTIVA)) {
                perspectivaES = PerspectivaESMediator.get().getPerspectivaESSemPerfilRisco(ciclo);
                if (perspectivaES == null) {
                    perspectivaES = new PerspectivaES();
                    perspectivaES.setCiclo(ciclo);
                }
                if (getExecute().equals(SALVAR_INFORMACOES)) {
                    perspectivaES.setParametroPerspectiva(ParametroPerspectivaMediator.get()
                            .buscarParametrosMetodologiaEDescricao(
                                    ciclo.getMetodologia().getPk(), getSelecione()));
                    perspectivaES.setDescricao(getJustificativa());
                    return PerspectivaESMediator.get().salvarNovaPerspectiva(perspectivaES);
                } else {
                    return PerspectivaESMediator.get().encaminharNovaPerspectiva(perspectivaES);
                }
            }

            if (getSecao().equals(SITUACAO)) {
                situacaoES = SituacaoESMediator.get().getSituacaoESSemPerfilRisco(ciclo);
                if (situacaoES == null) {
                    situacaoES = new SituacaoES();
                    situacaoES.setCiclo(ciclo);
                }
                if (getExecute().equals(SALVAR_INFORMACOES)) {
                    situacaoES.setParametroSituacao(ParametroSituacaoMediator.get()
                            .buscarParametrosMetodologiaEDescricao(
                                    ciclo.getMetodologia().getPk(), getSelecione()));
                    situacaoES.setDescricao(getJustificativa());
                    return SituacaoESMediator.get().salvarNovaSituacao(situacaoES);
                } else {
                    return SituacaoESMediator.get().encaminharNovaSituacao(situacaoES);
                }
            }
        } catch (Exception e) {
            msg = e.getMessage().replace("[", "").replace("]", "");
        }

        return msg;
    }

    public String botaoConfirmarHabilitado() {
        if (getSecao().equals(NOTA_FINAL)) {
            return SimNaoEnum.getTipo(!naoMostrar).getDescricao();
        }
        if (getSecao().equals(PERFIL_DE_ATUACAO)) {
            return SimNaoEnum.getTipo(perfilES.getPendente() == null || SimNaoEnum.NAO.equals(perfilES.getPendente()))
                    .getDescricao();
        }
        if (getSecao().equals(CONCLUSAO)) {
            return SimNaoEnum.getTipo(
                    conclusaoES.getPendente() == null || SimNaoEnum.NAO.equals(conclusaoES.getPendente()))
                    .getDescricao();
        }
        if (getSecao().equals(PERSPECTIVA)) {
            return SimNaoEnum.getTipo(
                    perspectivaES.getPendente() == null || SimNaoEnum.NAO.equals(perspectivaES.getPendente()))
                    .getDescricao();
        }
        if (getSecao().equals(SITUACAO)) {
            return SimNaoEnum.getTipo(
                    situacaoES.getPendente() == null || SimNaoEnum.NAO.equals(situacaoES.getPendente())).getDescricao();
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

}
