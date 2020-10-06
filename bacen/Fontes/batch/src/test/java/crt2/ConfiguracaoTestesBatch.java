package crt2;

import org.junit.BeforeClass;

import br.gov.bcb.sisaps.batch.AbstractBatchApplication;

public class ConfiguracaoTestesBatch extends ConfiguracaoTestesNegocio {

	@BeforeClass
	public static void preparaContexto() throws Exception {
		AbstractBatchApplication.EM_CONTEXTO_DE_TESTE = true;
	}
	
}
