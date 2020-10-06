package br.gov.bcb.sisaps.src.vo;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import br.gov.bcb.sisaps.src.dominio.AnexoCiclo;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;

public class AnexoCicloVO extends ObjetoPersistenteVO implements Serializable {

    private String nome;
    private String link;
    private String descricao;
    private VersaoPerfilRisco versaoPerfilRisco;

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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public VersaoPerfilRisco getVersaoPerfilRisco() {
        return versaoPerfilRisco;
    }

    public void setVersaoPerfilRisco(VersaoPerfilRisco versaoPerfilRisco) {
        this.versaoPerfilRisco = versaoPerfilRisco;
    }
    
    public String getNomeArquivoAnexo() {
        return this.nome + "." + SisapsUtil.extrairExtensaoNomeArquivo(this.link);
    }

    public static List<AnexoCicloVO> converterParaListaVo(List<AnexoCiclo> anexos) {
        List<AnexoCicloVO> listaVo = new LinkedList<AnexoCicloVO>();
        AnexoCicloVO anexoVo = null;
        int contador = 1;
        for (AnexoCiclo anexoCiclo : anexos) {
            anexoVo = new AnexoCicloVO();
            anexoVo.setPk(anexoCiclo.getPk());
            anexoVo.setNome(anexoCiclo.getNome());
            anexoVo.setLink(anexoCiclo.getLink());
            anexoVo.setVersaoPerfilRisco(anexoCiclo.getVersaoPerfilRisco());
            anexoVo.setDescricao("Anexo " + contador);
            contador++;
            listaVo.add(anexoVo);
        }
        return listaVo;
    }

}
