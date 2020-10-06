package crt2.plugins;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.core.objects.AbstractPluginObject;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.util.xom.node.RowAdapter;

import br.gov.bcb.sisaps.adaptadores.pessoa.IBcPessoa;
import br.gov.bcb.sisaps.adaptadores.pessoa.ServidorVO;
import br.gov.bcb.sisaps.stubs.integracao.pessoa.BcPessoaProviderStub;
import br.gov.bcb.sisaps.util.spring.SpringUtilsExtended;

public class PluginServidor extends AbstractPluginObject {

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    protected boolean isMapped() {
        return false;
    }

    @Override
    protected void action(IContext context, Object instance, RowAdapter row, IResultSet result) throws Exception {
        BcPessoaProviderStub bean = (BcPessoaProviderStub) SpringUtilsExtended.get().getBean(IBcPessoa.NOME);
        bean.incluirServidor((ServidorVO) instance);
    }
}