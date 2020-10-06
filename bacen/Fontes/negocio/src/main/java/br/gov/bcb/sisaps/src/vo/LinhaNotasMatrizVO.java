/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.src.vo;

import br.gov.bcb.sisaps.src.dominio.ParametroGrupoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoGrupoEnum;

public class LinhaNotasMatrizVO extends ObjetoPersistenteVO {


    private String nota;
    private ParametroGrupoRiscoControle grupo;
    private TipoGrupoEnum tipo;
   

    public LinhaNotasMatrizVO() {
        super();
    }

    public LinhaNotasMatrizVO(Integer pk) {
        this.pk = pk;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public ParametroGrupoRiscoControle getGrupo() {
        return grupo;
    }

    public void setGrupo(ParametroGrupoRiscoControle grupo) {
        this.grupo = grupo;
    }

    public TipoGrupoEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoGrupoEnum tipo) {
        this.tipo = tipo;
    }
    
    
 

  
    

}
