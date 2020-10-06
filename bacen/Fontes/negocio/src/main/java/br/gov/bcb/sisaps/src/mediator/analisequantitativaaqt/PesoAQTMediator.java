package br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.PerfilRiscoDAO;
import br.gov.bcb.sisaps.src.dao.analisequantitativaaqt.PesoAQTDAO;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ParametroAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.PesoAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.EventoConsolidadoMediator;
import br.gov.bcb.sisaps.src.mediator.MatrizCicloMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.VersaoPerfilRiscoMediator;
import br.gov.bcb.sisaps.src.validacao.RegraAlterarPercentualANEFValidacao;
import br.gov.bcb.sisaps.util.enumeracoes.TipoSubEventoPerfilRiscoSRC;
import br.gov.bcb.sisaps.util.geral.DataUtil;

@Service
@Transactional(readOnly = true)
public class PesoAQTMediator {

    @Autowired
    private PesoAQTDAO pesoAQTDAO;

    @Autowired
    private VersaoPerfilRiscoMediator versaoPerfilRiscoMediator;

    @Autowired
    private PerfilRiscoDAO perfilRiscoDAO;

    @Autowired
    private EventoConsolidadoMediator eventoConsolidadoMediator;

    public static PesoAQTMediator get() {
        return SpringUtils.get().getBean(PesoAQTMediator.class);
    }

    public PesoAQT buscarPorPk(Integer pk) {
        PesoAQT pesoAqt = pesoAQTDAO.load(pk);
        return pesoAqt;
    }

    public PesoAQT buscarPesoAQT(ParametroAQT parametroAQT, Ciclo ciclo, PerfilRisco perfilRisco) {
        List<VersaoPerfilRisco> versoes =
                versaoPerfilRiscoMediator.buscarVersoesPerfilRisco(perfilRisco.getPk(),
                        TipoObjetoVersionadorEnum.PESO_AQT);
        return pesoAQTDAO.buscarPesoAQT(parametroAQT, ciclo, versoes);
    }

    public void criarPesoAQT(ParametroAQT parametroAQT, Ciclo ciclo, List<PesoAQT> listaPesosAQTs) {
        for (int i = 0; i < 2; i++) {
            PesoAQT pesoAQT = new PesoAQT();
            pesoAQT.setCiclo(ciclo);
            pesoAQT.setParametroAQT(parametroAQT);
            pesoAQT.setValor(parametroAQT.getPesoAQT());
            if (i == 0) {
                VersaoPerfilRiscoMediator.get().criarVersao(pesoAQT, TipoObjetoVersionadorEnum.PESO_AQT);
            }
            pesoAQTDAO.save(pesoAQT);
            pesoAQTDAO.flush();
            listaPesosAQTs.add(pesoAQT);
        }

    }

    public PesoAQT obterPesoRascunho(ParametroAQT parametroAQT, Ciclo ciclo) {
        return pesoAQTDAO.obterPesoRascunho(parametroAQT, ciclo);
    }

    public PesoAQT obterPesoVigente(ParametroAQT parametroAQT, Ciclo ciclo) {
        PerfilRisco perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());
        return pesoAQTDAO.obterPesoVigente(parametroAQT, perfilRiscoAtual);
    }

    public void alterarPercentual(List<PesoAQT> listaPesoNovos, List<PesoAQT> listaPesoVigente,
            List<PesoAQT> listaPesoRascunhos) {
        new RegraAlterarPercentualANEFValidacao(listaPesoNovos, listaPesoVigente, listaPesoRascunhos).validar();
        for (int i = 0; i < listaPesoRascunhos.size(); i++) {
            PesoAQT pesoRascunho = listaPesoRascunhos.get(i);
            pesoRascunho.setValor(listaPesoNovos.get(i).getValor());
            pesoAQTDAO.update(pesoRascunho);
            pesoAQTDAO.flush();
        }
    }

    public boolean exibirBotaoConfirmar(List<PesoAQT> listaPesoVigente, List<PesoAQT> listaPesoRascunhos) {
        List<Boolean> pesosIguaisVigente = new ArrayList<Boolean>();

        for (int i = 0; i < listaPesoVigente.size(); i++) {
            if (listaPesoVigente.get(i).getValor().equals(listaPesoRascunhos.get(i).getValor())) {
                pesosIguaisVigente.add(true);
            } else {
                pesosIguaisVigente.add(false);
            }
        }

        return pesosIguaisVigente.contains(false);
    }

    public void confirmarPercentual(Integer pkCiclo) {
        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(pkCiclo);
        List<PesoAQT> listaPesoRascunhos = new ArrayList<PesoAQT>();

        AnaliseQuantitativaAQTMediator analiseQuantitativaAQTMediator = AnaliseQuantitativaAQTMediator.get();
        List<AnaliseQuantitativaAQT> anefsRascunhos = analiseQuantitativaAQTMediator.buscarANEFsRascunho(ciclo);
        for (AnaliseQuantitativaAQT anef : anefsRascunhos) {
            PesoAQT pesoRascunho = PesoAQTMediator.get().obterPesoRascunho(anef.getParametroAQT(), anef.getCiclo());
            listaPesoRascunhos.add(pesoRascunho);
        }

        for (PesoAQT pesoRascunho : listaPesoRascunhos) {
            VersaoPerfilRiscoMediator.get().criarVersao(pesoRascunho, TipoObjetoVersionadorEnum.PESO_AQT);
            pesoAQTDAO.update(pesoRascunho);
            pesoAQTDAO.flush();
        }

        PerfilRisco novoPerfilRisco = new PerfilRisco();
        PerfilRisco perfilRiscoAtual = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());
        for (VersaoPerfilRisco versaoOriginal : perfilRiscoAtual.getVersoesPerfilRisco()) {
            if (!TipoObjetoVersionadorEnum.PESO_AQT.equals(versaoOriginal.getTipoObjetoVersionador())) {
                novoPerfilRisco.addVersaoPerfilRisco(versaoOriginal);
            }
        }
        for (PesoAQT pesoAQT : listaPesoRascunhos) {
            if (pesoAQT.getVersaoPerfilRisco() != null) {
                novoPerfilRisco.addVersaoPerfilRisco(pesoAQT.getVersaoPerfilRisco());
            }
        }
        novoPerfilRisco.setCiclo(ciclo);
        novoPerfilRisco.setDataCriacao(DataUtil.getDateTimeAtual());
        perfilRiscoDAO.save(novoPerfilRisco);

        for (int i = 0; i < 3; i++) {
            PesoAQT pesoAQTRascunho = listaPesoRascunhos.get(i);
            ParametroAQT parametroAQT = pesoAQTRascunho.getParametroAQT();
            PesoAQT pesoAQT = new PesoAQT();
            pesoAQT.setCiclo(ciclo);
            pesoAQT.setParametroAQT(parametroAQT);
            pesoAQT.setValor(pesoAQTRascunho.getValor());
            pesoAQTDAO.save(pesoAQT);
            pesoAQTDAO.flush();
        }

        eventoConsolidadoMediator.incluirEventoPerfilDeRisco(ciclo, TipoSubEventoPerfilRiscoSRC.PESO_ANEF);

    }

    public List<PesoAQT> montarListaPesoEmEdicao(Ciclo ciclo) {
        List<PesoAQT> lista = new ArrayList<PesoAQT>();
        List<AnaliseQuantitativaAQT> anefsRascunhos = AnaliseQuantitativaAQTMediator.get().buscarANEFsRascunho(ciclo);
        for (AnaliseQuantitativaAQT anef : anefsRascunhos) {
            PesoAQT pesoRascunho = PesoAQTMediator.get().obterPesoRascunho(anef.getParametroAQT(), anef.getCiclo());
            lista.add(pesoRascunho);
        }
        return lista;
    }

    public List<PesoAQT> montarListaNovoPeso(Ciclo ciclo) {
        List<PesoAQT> lista = new ArrayList<PesoAQT>();
        List<AnaliseQuantitativaAQT> anefsRascunhos = AnaliseQuantitativaAQTMediator.get().buscarANEFsRascunho(ciclo);
        for (AnaliseQuantitativaAQT anef : anefsRascunhos) {
            PesoAQT pesoRascunho = new PesoAQT();
            pesoRascunho.setParametroAQT(anef.getParametroAQT());
            lista.add(pesoRascunho);
        }
        return lista;
    }

    public List<PesoAQT> montarListaPesoVigente(Ciclo ciclo) {
        List<PesoAQT> lista = new ArrayList<PesoAQT>();
        List<AnaliseQuantitativaAQT> anefsRascunhos = AnaliseQuantitativaAQTMediator.get().buscarANEFsRascunho(ciclo);
        for (AnaliseQuantitativaAQT anef : anefsRascunhos) {
            AnaliseQuantitativaAQT aqtVigente = AnaliseQuantitativaAQTMediator.get().buscarAQTVigente(anef);
            PesoAQT pesoVigente =
                    PesoAQTMediator.get().obterPesoVigente(aqtVigente.getParametroAQT(), aqtVigente.getCiclo());
            lista.add(pesoVigente);
        }
        return lista;
    }
    
    public PesoAQT getPesoAqtPorVersaoPerfil(ParametroAQT parametroAQT, Ciclo ciclo,
            List<VersaoPerfilRisco> listaVersao) { 
        return pesoAQTDAO.getPesoAqtPorVersaoPerfil(parametroAQT, ciclo, listaVersao);
    }
    
    public List<PesoAQT> buscarPorPerfilRisco(Integer pkPerfilRisco) {
        return pesoAQTDAO.buscarPorPerfilRisco(pkPerfilRisco);
    }
    
    public List<PesoAQT> buscarPesosRascunho(Ciclo ciclo) {
        return pesoAQTDAO.buscarPesosRascunho(ciclo);
    }

    @Transactional
    public void criarPesosNovoCiclo(PerfilRisco perfilRiscoCicloAtual, Ciclo novoCiclo) {
        // Criar percentuais vigentes e de rascunho com os mesmos dados dos percentuais vigentes do ciclo Corec.
        List<PesoAQT> pesosVigentesCicloAtual = buscarPorPerfilRisco(perfilRiscoCicloAtual.getPk());
        for (PesoAQT pesoVigenteCicloAtual : pesosVigentesCicloAtual) {
            PesoAQT novoPesoVigente = new PesoAQT();
            criarPesoNovoCiclo(novoCiclo, pesoVigenteCicloAtual, novoPesoVigente);
            PerfilRiscoMediator.get().incluirVersaoPerfilRiscoAtual(novoCiclo, novoPesoVigente, 
                    TipoObjetoVersionadorEnum.PESO_AQT);
            pesoAQTDAO.save(novoPesoVigente);
        }
        
        List<PesoAQT> pesosRascunhosCicloAtual = buscarPesosRascunho(perfilRiscoCicloAtual.getCiclo());
        for (PesoAQT pesoRascunhoCicloAtual : pesosRascunhosCicloAtual) {
            PesoAQT novoPesoRascunho = new PesoAQT();
            criarPesoNovoCiclo(novoCiclo, pesoRascunhoCicloAtual, novoPesoRascunho);
            pesoAQTDAO.save(novoPesoRascunho);
        }
    }

    private void criarPesoNovoCiclo(Ciclo novoCiclo, PesoAQT pesoVigenteCicloAtual, PesoAQT novoPesoVigente) {
        novoPesoVigente.setCiclo(novoCiclo);
        novoPesoVigente.setParametroAQT(pesoVigenteCicloAtual.getParametroAQT());
        novoPesoVigente.setValor(pesoVigenteCicloAtual.getValor());
        novoPesoVigente.setOperadorAtualizacao(pesoVigenteCicloAtual.getOperadorAtualizacao());
        novoPesoVigente.setUltimaAtualizacao(pesoVigenteCicloAtual.getUltimaAtualizacao());
        novoPesoVigente.setAlterarDataUltimaAtualizacao(false);
    }
    
    public boolean validarPercentuaisVigenteIguaisRascunhos(Ciclo ciclo) {
        List<PesoAQT> listaPesosRascunhos = montarListaPesoEmEdicao(ciclo);
        List<PesoAQT> listaPesosVigente = montarListaPesoVigente(ciclo);

        List<Boolean> listPesos = new ArrayList<Boolean>();
        for (int i = 0; i < listaPesosRascunhos.size(); i++) {
            if (listaPesosRascunhos.get(i).getValor() != null && listaPesosVigente.get(i).getValor() != null
                    && listaPesosRascunhos.get(i).getValor().equals(listaPesosVigente.get(i).getValor())) {
                listPesos.add(true);
            } else {
                listPesos.add(false);
            }
        }
        return listPesos.contains(false);
    }

    public boolean validarPercentuaisVigenteIguaisRascunhosAE(Ciclo ciclo) {
        PerfilRisco perfilRisco = PerfilRiscoMediator.get().obterPerfilRiscoAtual(ciclo.getPk());
        Matriz matrizVigente = PerfilRiscoMediator.get().getMatrizAtualPerfilRisco(perfilRisco);
        Matriz matrizEsbocada = null;
        if(matrizVigente != null){
            matrizEsbocada = MatrizCicloMediator.get().getMatrizEmEdicao(matrizVigente.getCiclo());
            return matrizEsbocada != null  && matrizVigente != null && matrizVigente.getPercentualGovernancaCorporativoInt() != matrizEsbocada.getPercentualGovernancaCorporativoInt();
        }
        return false;
    }

}
