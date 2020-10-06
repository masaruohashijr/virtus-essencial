package crt2.plugins;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.flow.PluginPause;
import org.specrunner.result.IResultSet;

import br.gov.bcb.sisaps.util.geral.DataUtil;

public class PluginPauseAps extends PluginPause {

       
    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        DataUtil.setDateTimeAtual(null);
        return super.doStart(context, result);
    }

   
}
