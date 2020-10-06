package crt2.dominio.analisequantitativa.alterarpercentuais;

import java.util.ArrayList;
import java.util.List;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.PesoAQT;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.PesoAQTMediator;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import crt2.ConfiguracaoTestesNegocio;

public class TestR001AlterarPercentuaisANEF extends ConfiguracaoTestesNegocio {

    private final List<PesoAQT> listaPesoNovos = new ArrayList<PesoAQT>();
    private final List<PesoAQT> listaPesoVigente = new ArrayList<PesoAQT>();
    private final List<PesoAQT> listaPesoRascunhos = new ArrayList<PesoAQT>();
    private final List<String> nomeParamentro = new ArrayList<String>();
    private final List<Short> novoPeso = new ArrayList<Short>();

    public void alteraAnef(String param1, String param2, String param3) {
        atualizarParametro(param1, param2, param3);
        setNomePeso();
        alterarPercentual();
        listaPesoNovos.clear();
    }

    private void alterarPercentual() {
        try {
            PesoAQTMediator.get().alterarPercentual(listaPesoNovos, listaPesoVigente, listaPesoRascunhos);
        } catch (NegocioException e) {
            erro = e;
        }

    }

    public void percentualMaiorZero(short valor1, short valor2, short valor3) {
        erro.getMensagens().clear();
        listaPesoVigente.clear();
        listaPesoRascunhos.clear();
        listaPesoNovos.clear();
        atualizarPeso(valor1, valor2, valor3);
        setNomePeso();
        alterarPercentual();
        listaPesoNovos.clear();
        novoPeso.clear();
    }

    public void percentualVigente(Integer pkCiclo, short valor1, short valor2, short valor3) {
        atualizarPeso(valor1, valor2, valor3);
        setNomePeso();
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(pkCiclo);
        AnaliseQuantitativaAQTMediator analiseQuantitativaAQTMediator = AnaliseQuantitativaAQTMediator.get();
        List<AnaliseQuantitativaAQT> anefsRascunhos = analiseQuantitativaAQTMediator.buscarANEFsRascunho(ciclo);
        for (AnaliseQuantitativaAQT anef : anefsRascunhos) {
            AnaliseQuantitativaAQT aqtVigente = analiseQuantitativaAQTMediator.buscarAQTVigente(anef);
            PesoAQT pesoVigente =
                    PesoAQTMediator.get().obterPesoVigente(aqtVigente.getParametroAQT(), aqtVigente.getCiclo());
            listaPesoVigente.add(pesoVigente);
        }
        alterarPercentual();
        listaPesoVigente.clear();
        novoPeso.clear();
        listaPesoNovos.clear();
    }

    public void percentualEmEdicao(Integer pkCiclo, short valor1, short valor2, short valor3) {
        novoPeso.clear();
        listaPesoNovos.clear();
        atualizarPeso(valor1, valor2, valor3);
        setNomePeso();
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(pkCiclo);
        AnaliseQuantitativaAQTMediator analiseQuantitativaAQTMediator = AnaliseQuantitativaAQTMediator.get();
        List<AnaliseQuantitativaAQT> anefsRascunhos = analiseQuantitativaAQTMediator.buscarANEFsRascunho(ciclo);
        for (AnaliseQuantitativaAQT anef : anefsRascunhos) {
            PesoAQT pesoRascunho = PesoAQTMediator.get().obterPesoRascunho(anef.getParametroAQT(), anef.getCiclo());
            listaPesoRascunhos.add(pesoRascunho);
        }
        alterarPercentual();
        listaPesoVigente.clear();
        novoPeso.clear();
        listaPesoNovos.clear();
        listaPesoRascunhos.clear();
    }

    private void setNomePeso() {
        for (int i = 0; i < 3; i++) {
            PesoAQT peso = new PesoAQT();
            ParametroAQT parametro = new ParametroAQT();
            if (!nomeParamentro.isEmpty()) {
                parametro.setDescricao(nomeParamentro.get(i));
                peso.setParametroAQT(parametro);
            }
            if (!novoPeso.isEmpty()) {
                peso.setValor(novoPeso.get(i));
            }
            listaPesoNovos.add(peso);
        }
    }

    private void atualizarParametro(String param1, String param2, String param3) {
        nomeParamentro.add(param1);
        nomeParamentro.add(param2);
        nomeParamentro.add(param3);
    }

    private void atualizarPeso(Short peso1, Short peso2, Short peso3) {
        novoPeso.add(peso1);
        novoPeso.add(peso2);
        novoPeso.add(peso3);
    }
}
