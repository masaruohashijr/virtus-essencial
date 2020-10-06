package br.gov.bcb.sisaps.web.page.painelGerente;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import br.gov.bcb.app.stuff.seguranca.UsuarioCorrente;
import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.seguranca.UsuarioAplicacao;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.vo.ComponenteOrganizacionalVO;
import br.gov.bcb.sisaps.web.page.PainelSisAps;

public class PainelCiclosEquipeGerente extends PainelSisAps {

    public PainelCiclosEquipeGerente(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        addLocalizacao();
        addPaineisLocalizacao();
    }

    private void addLocalizacao() {
        UsuarioAplicacao usuario = ((UsuarioAplicacao) UsuarioCorrente.get());
        Label localizacao = new Label("idLocalizacao", 
        		usuario.getServidorVO().getLocalizacaoAtual(PerfilAcessoEnum.GERENTE));
        addOrReplace(localizacao);
    }

    private void addPaineisLocalizacao() {
        UsuarioAplicacao usuario = ((UsuarioAplicacao) UsuarioCorrente.get());
        List<ComponenteOrganizacionalVO> vo =
                BcPessoaAdapter.get().consultarComponentesOrganizacionais(
                		usuario.getServidorVO().getLocalizacaoAtual(PerfilAcessoEnum.GERENTE));

        vo = removerLocalizacaoAtual(vo, usuario.getServidorVO().getLocalizacaoAtual(PerfilAcessoEnum.GERENTE));
        
        addOrReplace(new ListView<ComponenteOrganizacionalVO>("idPaineisCiclo", vo) {
            // Monta os painéis de seção.
            @Override
            protected void populateItem(ListItem<ComponenteOrganizacionalVO> item) {
                // Adiciona o painel de seção.  idPainelCiclo
                item.addOrReplace(new PainelCicloSubLocalizao("idPainelCiclo", item.getModelObject()));
            }

        });
    }

    private List<ComponenteOrganizacionalVO> removerLocalizacaoAtual(
            List<ComponenteOrganizacionalVO> componentes, String localizacaoAtual) {
        List<ComponenteOrganizacionalVO> retorno = new ArrayList<ComponenteOrganizacionalVO>();
        for (ComponenteOrganizacionalVO componente : componentes) {
            if (!componente.getRotulo().trim().equals(localizacaoAtual.trim())) {
                retorno.add(componente);
            }
        }
        return retorno;
    }

}
