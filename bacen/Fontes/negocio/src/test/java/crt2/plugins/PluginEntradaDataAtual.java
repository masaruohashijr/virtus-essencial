package crt2.plugins;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.AbstractPlugin;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;

import br.gov.bcb.sisaps.util.geral.DataUtil;

public class PluginEntradaDataAtual extends AbstractPlugin {

    private String valor; 
    
    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }
    
    @Override
    public void doEnd(IContext context, IResultSet result) throws PluginException {
        final DateTimeFormatter format = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss").withZoneUTC();
        DataUtil.setDateTimeAtual(format.parseDateTime(valor));
    }

}
