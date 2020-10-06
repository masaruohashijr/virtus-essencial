package br.gov.bcb.sisaps.src.vo;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import br.gov.bcb.sisaps.src.dominio.PerspectivaES;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.CampoApresentacaoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.util.enumeracoes.NormalidadeEnum;

public class ApresentacaoVO extends ObjetoPersistenteAuditavelVO implements Serializable {

    // Dados de um ciclo.
    public static class DadosCicloVO implements Serializable{
        // Perpectivas da ES.
        private PerspectivaES perspectivaES;

        // Percentuais de neg�cio e corporativo.
        private String percentualNegocio;
        private String percentualCorporativo;

        // Atividades de neg�cio e corporativo.
        private List<LinhaAtividadeVO> atividades;

        public PerspectivaES getPerspectivaES() {
            return perspectivaES;
        }

        public void setPerspectivaES(PerspectivaES perspectivaES) {
            this.perspectivaES = perspectivaES;
        }

        public String getPercentualNegocio() {
            return percentualNegocio;
        }

        public void setPercentualNegocio(String percentualNegocio) {
            this.percentualNegocio = percentualNegocio;
        }

        public String getPercentualCorporativo() {
            return percentualCorporativo;
        }

        public void setPercentualCorporativo(String percentualCorporativo) {
            this.percentualCorporativo = percentualCorporativo;
        }

        public List<LinhaAtividadeVO> getAtividades() {
            return atividades;
        }

        public void setAtividades(List<LinhaAtividadeVO> atividades) {
            this.atividades = atividades;
        }
    }

    // Os anexos.
    private List<AnexoApresentacaoVO> anexosVO;

    // Os textos.
    private List<TextoApresentacaoVO> textosVO;

    // A situa��o da ES.
    private NormalidadeEnum situacaoNormalidade;
    private String situacaoNome;
    private String situacaoJustificativa;

    // Notas quantitativa  qualitativa.
    // [calculada, refinada, cor, ajustada]
    private final String[] notaQuantitativa = new String[5];
    private final String[] notaQualitativa = new String[5];

    // Unidades e atividades do ciclo.
    private DadosCicloVO dadosCicloVO;

    // Unidades e atividades do ciclo anterior.
    private DadosCicloVO dadosCicloAnteriorVO;

    // Os riscos.
    private List<RiscoVO> riscosVO;

    private String nomeEs;

    public String getNomeEs() {
        return nomeEs;
    }

    public void setNomeEs(String nomeEsString) {
        this.nomeEs = nomeEsString;
    }

    public List<AnexoApresentacaoVO> getAnexosVO() {
        return anexosVO;
    }

    public void setAnexosVO(List<AnexoApresentacaoVO> anexosVO) {
        this.anexosVO = anexosVO;
    }

    public List<TextoApresentacaoVO> getTextosVO() {
        return textosVO;
    }

    public void setTextosVO(List<TextoApresentacaoVO> textosVO) {
        this.textosVO = textosVO;
    }

    public NormalidadeEnum getSituacaoNormalidade() {
        return situacaoNormalidade;
    }

    public void setSituacaoNormalidade(NormalidadeEnum situacaoNormalidade) {
        this.situacaoNormalidade = situacaoNormalidade;
    }

    public String getSituacaoNome() {
        return situacaoNome;
    }

    public void setSituacaoNome(String situacaoNome) {
        this.situacaoNome = situacaoNome;
    }

    public String getSituacaoJustificativa() {
        return situacaoJustificativa;
    }

    public void setSituacaoJustificativa(String situacaoJustificativa) {
        this.situacaoJustificativa = situacaoJustificativa;
    }

    public String[] getNotaQuantitativa() {
        return notaQuantitativa;
    }

    public String[] getNotaQualitativa() {
        return notaQualitativa;
    }

    public List<RiscoVO> getRiscosVO() {
        return riscosVO;
    }

    public void setRiscosVO(List<RiscoVO> riscosVO) {
        this.riscosVO = riscosVO;
    }

    public DadosCicloVO getDadosCicloVO() {
        return dadosCicloVO;
    }

    public void setDadosCicloVO(DadosCicloVO dadosCicloVO) {
        this.dadosCicloVO = dadosCicloVO;
    }

    public DadosCicloVO getDadosCicloVOAnteriorVO() {
        return dadosCicloAnteriorVO;
    }

    public void setDadosCicloAnteriorVO(DadosCicloVO dadosCicloVO) {
        this.dadosCicloAnteriorVO = dadosCicloVO;
    }

    // Adiciona ou atualiza um valor.
    public void addOrReplace(TextoApresentacaoVO textoVO) {
        // Declara��es
        TextoApresentacaoVO textoApresentacaoVO;

        // Verifica se o texto j� existe.
        textoApresentacaoVO = getTexto(textoVO.getSecao(), textoVO.getCampo());
        if (textoApresentacaoVO != null) {
            // Remove o texto atual.
            getTextosVO().remove(textoApresentacaoVO);
        }

        // Adiciona o novo texto.
        getTextosVO().add(textoVO);
    }

    // Adiciona ou atualiza um valor.
    public void addOrReplace(AnexoApresentacaoVO novoAnexoVO) {
        // Declara��es
        AnexoApresentacaoVO anexoApresentacaoVO;

        // Inicializa��es
        anexoApresentacaoVO = null;

        // Procura pelo anexo.
        for (AnexoApresentacaoVO anexoVO : this.getAnexosVO()) {
            // Verifica se � o anexo.
            if (anexoVO.getPk().compareTo(novoAnexoVO.getPk()) == 0) {
                // Encontrado!
                anexoApresentacaoVO = anexoVO;
                break;
            }
        }

        // Verifica se o anexo j� existe.
        if (anexoApresentacaoVO != null) {
            // Remove o atual.
            getAnexosVO().remove(anexoApresentacaoVO);
        }

        // Adiciona o novo anexo.
        getAnexosVO().add(novoAnexoVO);
    }

    // Procura pelos anexos de uma se��o.
    public List<AnexoApresentacaoVO> getAnexosVO(SecaoApresentacaoEnum secao) {
        // Declara��es
        List<AnexoApresentacaoVO> anexosApresentacaoVO;

        // Inicializa��es
        anexosApresentacaoVO = new LinkedList<AnexoApresentacaoVO>();

        // Procura pelos anexos das se��o.
        for (AnexoApresentacaoVO anexoVO : this.getAnexosVO()) {
            // Verifica se � da se��o.
            if (anexoVO.getSecao() == secao) {
                // Adiciona � lista.
                anexosApresentacaoVO.add(anexoVO);
            }
        }

        return anexosApresentacaoVO;
    }

    // Retorna o anexo mais recente de uma se��o.
    private AnexoApresentacaoVO getAnexoMaisRecente(SecaoApresentacaoEnum secao) {
        // Declara��es
        List<AnexoApresentacaoVO> anexosApresentacaoVO;
        AnexoApresentacaoVO anexoApresentacaoVO;

        // Recupera os anexos da se��o.
        anexosApresentacaoVO = getAnexosVO(secao);

        // Verifica se h� anexos.
        if (anexosApresentacaoVO.size() != 0) {
            // Come�a pelo primeiro.
            anexoApresentacaoVO = anexosApresentacaoVO.get(0);

            // Encontra o mais recente da lista.
            for (AnexoApresentacaoVO anexoVO : anexosApresentacaoVO) {
                // Verifica se � mais recente.
                if (anexoVO.getUltimaAtualizacao().isAfter(anexoApresentacaoVO.getUltimaAtualizacao())) {
                    // Este anexo � mais recente.
                    anexoApresentacaoVO = anexoVO;
                }
            }

        } else {
            // Sem anexos.
            anexoApresentacaoVO = null;
        }

        return anexoApresentacaoVO;
    }

    // Procura pelos textos de uma se��o.
    public List<TextoApresentacaoVO> getTextosVO(SecaoApresentacaoEnum secao) {
        // Declara��es
        List<TextoApresentacaoVO> textosApresentacaoVO;

        // Inicializa��es
        textosApresentacaoVO = new LinkedList<TextoApresentacaoVO>();

        // Procura pelo texto na apresenta��o.
        for (TextoApresentacaoVO textoVO : this.getTextosVO()) {
            // Verifica se � este e se h� valor.
            if (textoVO.getSecao() == secao && textoVO.getTexto().length() > 0) {
                // Adiciona � lista.
                textosApresentacaoVO.add(textoVO);
            }
        }

        return textosApresentacaoVO;
    }

    // Procura por um texto.
    private TextoApresentacaoVO getTexto(SecaoApresentacaoEnum secao, CampoApresentacaoEnum campo) {
        // Declara��es
        TextoApresentacaoVO textoApresentacaoVO;

        // Inicializa��es
        textoApresentacaoVO = null;

        // Procura pelo texto na apresenta��o.
        for (TextoApresentacaoVO textoVO : this.getTextosVO()) {
            // Verifica se � este.
            if (textoVO.getSecao() == secao && textoVO.getCampo() == campo) {
                // Encontrado!
                textoApresentacaoVO = textoVO;
                break;
            }
        }

        return textoApresentacaoVO;
    }

    // Retornao valor de um campo da apresenta��o.
    public ObjetoPersistenteAuditavelVO getValor(CampoApresentacaoEnum campo) {
        // Declara��es
        ObjetoPersistenteAuditavelVO valor;

        // Inicializa��es
        valor = null;

        // Verifica o tipo do campo.
        if (campo.getTipo() == CampoApresentacaoEnum.TC_ANEXO) {
            return getAnexoMaisRecente(campo.getSecao());

        } else if (campo.getTipo() == CampoApresentacaoEnum.TC_TEXTO) {
            return getTexto(campo.getSecao(), campo);
        } else if (campo.getTipo() == CampoApresentacaoEnum.TC_TEXTO_ANEXO) {
            ObjetoPersistenteAuditavelVO anexo = getAnexoMaisRecente(campo.getSecao());
            ObjetoPersistenteAuditavelVO texto = getTexto(campo.getSecao(), campo);
            if (anexo == null) {
                return texto;
            } else {
                if (texto == null) {
                    return anexo;
                } else {
                    int retorno = anexo.getUltimaAtualizacao().compareTo(texto.getUltimaAtualizacao());
                    if (retorno == -1) {
                        return texto;
                    } else {
                        return anexo;
                    }
                }
            }

        }
        return valor;

    }

    // Retornao valor de um campo da apresenta��o.
    public ObjetoPersistenteAuditavelVO getValor(CampoApresentacaoEnum campo, ObjetoPersistenteAuditavelVO class1) {
        if (class1 instanceof TextoApresentacaoVO) {
            if (campo.getTipo() == CampoApresentacaoEnum.TC_TEXTO_ANEXO) {
                return getTexto(campo.getSecao(), campo);
            }
        }
        return null;
    }

}
