package br.gov.bcb.sisaps.src.mediator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.VersaoPerfilRiscoDAO;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.util.objetos.IObjetoVersionador;

@Service
public class VersaoPerfilRiscoMediator {

    @Autowired
    private VersaoPerfilRiscoDAO versaoPerfilRiscoDAO;

    public static VersaoPerfilRiscoMediator get() {
        return SpringUtils.get().getBean(VersaoPerfilRiscoMediator.class);
    }

    @Transactional
    public VersaoPerfilRisco criarVersao(IObjetoVersionador objetoVersionador,
            TipoObjetoVersionadorEnum tipoObjetoVersionador) {
        VersaoPerfilRisco novaVersao = new VersaoPerfilRisco();
        novaVersao.setTipoObjetoVersionador(tipoObjetoVersionador);
        versaoPerfilRiscoDAO.save(novaVersao);
        objetoVersionador.setVersaoPerfilRisco(novaVersao);
        return novaVersao;
    }

    @Transactional(readOnly = true)
    public List<VersaoPerfilRisco> buscarVersoesPerfilRisco(Integer pkPerfilRisco,
            TipoObjetoVersionadorEnum tipoObjetoVersionador) {
        return versaoPerfilRiscoDAO.buscarVersoesPerfilRisco(pkPerfilRisco, tipoObjetoVersionador);
    }

    @Transactional(readOnly = true)
    public VersaoPerfilRisco buscarVersaoPerfilRisco(Integer pkPerfilRisco,
            TipoObjetoVersionadorEnum tipoObjetoVersionadorEnum) {
        return versaoPerfilRiscoDAO.buscarVersaoPerfilRisco(pkPerfilRisco, tipoObjetoVersionadorEnum);
    }

    @Transactional
    public void excluir(VersaoPerfilRisco versaoPerfilRisco) {
        for (PerfilRisco perfilRisco : versaoPerfilRisco.getPerfisRisco()) {
            perfilRisco.getVersoesPerfilRisco().remove(versaoPerfilRisco);
        }
        versaoPerfilRiscoDAO.delete(versaoPerfilRisco);
    }

    public VersaoPerfilRisco load(Integer pk) {
        return versaoPerfilRiscoDAO.load(pk);
    }

    public List<Integer> buscarCodigosVersoesPerfilRisco(Integer pkPerfilRisco) {
        return versaoPerfilRiscoDAO.buscarCodigosVersoesPerfilRisco(pkPerfilRisco);
    }

    public List<VersaoPerfilRisco> buscarVersoesCriadasPerfilAtual(Integer pkPerfilRisco,
            List<Integer> versoesPerfilAnterior) {
        return versaoPerfilRiscoDAO.buscarVersoesCriadasPerfilAtual(pkPerfilRisco, versoesPerfilAnterior);
    }
    
    public List<VersaoPerfilRisco> buscarVersaoAnefPerfilRisco(Integer pkPerfilRisco) {
        return versaoPerfilRiscoDAO.buscarVersaoAnefPerfilRisco(pkPerfilRisco);
    }

}
