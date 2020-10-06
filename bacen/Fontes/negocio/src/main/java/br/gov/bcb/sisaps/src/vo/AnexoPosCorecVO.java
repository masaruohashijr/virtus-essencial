package br.gov.bcb.sisaps.src.vo;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import br.gov.bcb.sisaps.src.dominio.AnexoPosCorec;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;

public class AnexoPosCorecVO extends ObjetoPersistenteVO implements Serializable {
    private String nome;
    private String link;
    private String tipo;
    private String descricao;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNomeArquivoAnexo() {
        return this.nome + "." + SisapsUtil.extrairExtensaoNomeArquivo(this.link);
    }

    public static List<AnexoPosCorecVO> converterParaListaVo(List<AnexoPosCorec> anexos) {
        List<AnexoPosCorecVO> listaVo = new LinkedList<AnexoPosCorecVO>();
        AnexoPosCorecVO anexoVo = null;
        int contador = 1;
        for (AnexoPosCorec anexoPosCorec : anexos) {
            anexoVo = new AnexoPosCorecVO();
            anexoVo.setPk(anexoPosCorec.getPk());
            anexoVo.setTipo(anexoPosCorec.getTipo());
            anexoVo.setLink(anexoPosCorec.getLink());
            anexoVo.setDescricao("Anexo " + contador);
            contador++;
            listaVo.add(anexoVo);
        }
        return listaVo;
    }

    public static AnexoPosCorecVO converterParaVo(AnexoPosCorec anexos) {

        AnexoPosCorecVO anexoVo = new AnexoPosCorecVO();
        if (anexos != null) {
            anexoVo.setPk(anexos.getPk());
            anexoVo.setTipo(anexos.getTipo());
            anexoVo.setLink(anexos.getLink());
        }

        return anexoVo;
    }

}
