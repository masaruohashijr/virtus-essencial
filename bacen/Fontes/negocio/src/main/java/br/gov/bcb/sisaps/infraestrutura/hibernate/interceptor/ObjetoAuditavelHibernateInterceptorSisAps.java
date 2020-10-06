package br.gov.bcb.sisaps.infraestrutura.hibernate.interceptor;

import br.gov.bcb.app.stuff.hibernate.IObjetoPersistenteAuditavel;
import br.gov.bcb.app.stuff.hibernate.interceptor.ObjetoAuditavelHibernateInterceptor;
import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.sisaps.util.Util;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.util.objetos.IObjetoPersistenteAuditavelManipulavel;

public class ObjetoAuditavelHibernateInterceptorSisAps extends ObjetoAuditavelHibernateInterceptor {
    
    @Override
    protected void preencherInformacoesAuditoria(IObjetoPersistenteAuditavel<?> objetoPersistenteAuditavel) {
        if (!(objetoPersistenteAuditavel instanceof IObjetoPersistenteAuditavelManipulavel 
                && !((IObjetoPersistenteAuditavelManipulavel) objetoPersistenteAuditavel)
                .isAlterarDataUltimaAtualizacao())) {
            Boolean contextoBatch = Util.isBatch();
            if (!(contextoBatch != null && Boolean.TRUE.equals(contextoBatch))) { 
                objetoPersistenteAuditavel.setOperadorAtualizacao(UsuarioCorrente.get().getLogin());
                objetoPersistenteAuditavel.setUltimaAtualizacao(DataUtil.getDateTimeAtual());
            }
        }
    }

}
