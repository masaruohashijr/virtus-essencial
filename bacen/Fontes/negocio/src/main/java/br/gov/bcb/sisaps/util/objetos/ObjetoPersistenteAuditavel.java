/*
 * Sistema APS.
 * ObjetoPersistenteAuditavel.java
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.util.objetos;

import java.lang.reflect.InvocationTargetException;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import br.gov.bcb.sisaps.integracao.seguranca.Usuario;
import br.gov.bcb.sisaps.integracao.seguranca.UsuarioCorrente;
import br.gov.bcb.sisaps.util.validacao.InfraException;

@MappedSuperclass
public abstract class ObjetoPersistenteAuditavel extends ObjetoPersistente {
    public static final String PROP_DATA_HORA_ATUALIZACAO = "dataHoraAtualizacao";

    public static final String PROP_IDENTIFICACAO_USUARIO = "usuarioAtualizacao";

    public static final String PROP_DATA_FORMATADA = "dd/MM/yyyy";

    private static final long serialVersionUID = 1L;

    private static final String PROP_DATA_HORA_FORMATADA = "dd/MM/yyyy' 'HH:mm:ss";

    private String usuarioAtualizacao;

    private DateTime dataHoraAtualizacao;

    @Type(type = JODA_DATE_TIME)
    public DateTime getDataHoraAtualizacao() {
        return dataHoraAtualizacao;
    }

    public void setDataHoraAtualizacao(DateTime dataHoraAtualizacao) {
        this.dataHoraAtualizacao = dataHoraAtualizacao;
    }

    public String getUsuarioAtualizacao() {
        return usuarioAtualizacao;
    }

    public void setUsuarioAtualizacao(String usuarioAtualizacao) {
        this.usuarioAtualizacao = usuarioAtualizacao;
    }

    public void onSaveEvent(Object[] currentState, String[] propertyNames, org.hibernate.type.Type[] types) {
        onSave();
        synchronizeState(currentState, propertyNames);
    }

    public void onUpdateEvent(Object[] currentState, Object[] previousState, String[] propertyNames,
            org.hibernate.type.Type[] types) {
        onUpdate();
        synchronizeState(currentState, propertyNames);
    }

    private void onSave() {
        preencherInformacoesAuditoria();
    }

    private void onUpdate() {
        preencherInformacoesAuditoria();
    }

    protected void preencherInformacoesAuditoria() {
        Usuario usuarioLogado = UsuarioCorrente.get();
        setUsuarioAtualizacao(usuarioLogado.getLogin());

        setDataHoraAtualizacao(new DateTime());
    }

    private void synchronizeState(Object[] currentState, String[] propertyNames) {

        try {
            for (int i = 0; i < propertyNames.length; i++) {

                String name = propertyNames[i];
                if ((!PROP_ID.equals(name)) && (!name.contains("Backref")) && (!"versao".equals(name))) {
                    currentState[i] = PropertyUtils.getProperty(this, name);
                }
            }
        } catch (IllegalAccessException e) {
            throw new InfraException(e);
        } catch (InvocationTargetException e) {
            throw new InfraException(e);
        } catch (NoSuchMethodException e) {
            throw new InfraException(e);
        }
    }

    @Transient
    public String getOperador() {
        StringBuilder odh = new StringBuilder();
        odh.append(StringUtils.trim(this.usuarioAtualizacao));
        return odh.toString();
    }

    @Transient
    public String getOperadorDataHora() {
        StringBuilder odh = new StringBuilder(getOperador());
        odh.append(" ");
        odh.append(this.dataHoraAtualizacao.toString(PROP_DATA_HORA_FORMATADA));
        return odh.toString();
    }

    @Transient
    public String getDataHoraFormatada() {
        return this.dataHoraAtualizacao == null ? "" : this.dataHoraAtualizacao.toString(PROP_DATA_HORA_FORMATADA);
    }
}
