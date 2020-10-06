package crt2.dominio.paineldosupervisor.test;

import org.springframework.beans.factory.annotation.Autowired;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.comum.util.string.StringUtil;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.dominio.Metodologia;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.EntidadeSupervisionavelMediator;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;
import crt2.dominio.ciclo.test.CicloUtil;

public class TestAPSFW0201_Painel_do_supervisor extends ConfiguracaoTestesNegocio {

    @Autowired
    private CicloMediator cicloMediator;

    @Autowired
    private EntidadeSupervisionavelMediator entidadeSupervisionavelMediator;

    public void salvar(String idES, String dataInicio) {
        EntidadeSupervisionavel entidadeSupervisionavel;
        Metodologia metodologia;
        if (StringUtil.isVazioOuNulo(idES)) {
            entidadeSupervisionavel = null;
            metodologia = null;

        } else {
            entidadeSupervisionavel =
                    entidadeSupervisionavelMediator.buscarEntidadeSupervisionavelPorPK(Integer.valueOf(idES));
            metodologia = entidadeSupervisionavel.getMetodologia();
        }
        salvar(CicloUtil.criar("", entidadeSupervisionavel, metodologia, DataUtil.dateFromString(dataInicio)),
                dataInicio);
    }

    protected void salvar(Ciclo ciclo, String strDataInicio) {
        UsuarioAplicacao usuario = (UsuarioAplicacao) (UsuarioCorrente.get());
        usuario.getLogin();
        erro = null;
        try {
            cicloMediator.incluir(ciclo, strDataInicio, false);
        } catch (NegocioException e) {
            erro = e;
        }
    }

}
