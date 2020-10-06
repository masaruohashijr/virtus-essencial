package br.gov.bcb.sisaps.web.page.dominio.perfilderisco.apresentacao.slide;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.SecaoApresentacaoEnum;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.mediator.AtividadeCicloMediator;
import br.gov.bcb.sisaps.src.mediator.VersaoPerfilRiscoMediator;
import br.gov.bcb.sisaps.src.vo.ApresentacaoVO;
import br.gov.bcb.sisaps.src.vo.AtividadeCicloVO;
import br.gov.bcb.sisaps.src.vo.ConsultaAtividadeCicloVO;
import br.gov.bcb.sisaps.util.geral.DataUtil;

public class Slide03 extends Slide {

    @SpringBean
    private AtividadeCicloMediator atividadeCicloMediator;
    @SpringBean
    private VersaoPerfilRiscoMediator versaoPerfilRiscoMediator;

    // Construtor
    public Slide03(PerfilRisco perfilRisco, Ciclo ciclo, ApresentacaoVO apresentacaoVO) {
        super(SecaoApresentacaoEnum.TRABALHOS_REALIZADOS, apresentacaoVO.getNomeEs());

        // Adiciona os componentes.
        montarComponentes(perfilRisco, ciclo);
    }

    // Monta os componentes do painel.
    private void montarComponentes(PerfilRisco perfilRisco, Ciclo ciclo) {
        // Declarações
        List<AtividadeCicloVO> atividadesCicloVO;
        ConsultaAtividadeCicloVO consulta;

        // Consulta as atividades do ciclo.
        consulta = montarConsulta(perfilRisco, ciclo);
        atividadesCicloVO = atividadeCicloMediator.consultar(consulta);

        // Adiciona o ano atual.
        add(new Label("idAnoAtual", consulta.getAno()));

        // Adiciona as atividades ao painel.
        add(new ListView<AtividadeCicloVO>("idListaAnoAtual", atividadesCicloVO) {
            // Monta os painéis de seção.
            @Override
            protected void populateItem(ListItem<AtividadeCicloVO> item) {
                // Adiciona os dados da atividade.
                item.add(new Label("idAtividadeAno", item.getModelObject().getAno()));
                item.add(new Label("idAtividadeDataBase", item.getModelObject().getDataBaseFormatada()));
                item.add(new Label("idAtividadeDescricao", item.getModelObject().getDescricao()));
                item.add(new Label("idAtividadeSituacao", item.getModelObject().getSituacao()));
            }
        });

        // E do ano anterior.
        consulta.setAno((short) (consulta.getAno() - 1));
        atividadesCicloVO = atividadeCicloMediator.consultar(consulta);

        // Adiciona o ano anterior.
        add(new Label("idAnoAnterior", consulta.getAno()));

        // Adiciona as atividades ao painel.
        add(new ListView<AtividadeCicloVO>("idListaAnoAnterior", atividadesCicloVO) {
            // Monta os painéis de seção.
            @Override
            protected void populateItem(ListItem<AtividadeCicloVO> item) {
                // Adiciona os dados da atividade.
                item.add(new Label("idAtividadeAno", item.getModelObject().getAno()));
                item.add(new Label("idAtividadeDataBase", item.getModelObject().getDataBaseFormatada()));
                item.add(new Label("idAtividadeDescricao", item.getModelObject().getDescricao()));
                item.add(new Label("idAtividadeSituacao", item.getModelObject().getSituacao()));
            }
        });

    }

    // Monta a consulta das atividades.
    private ConsultaAtividadeCicloVO montarConsulta(PerfilRisco perfilRisco, Ciclo ciclo) {
        // Declarações
        ConsultaAtividadeCicloVO consulta;

        // Inicializações
        consulta = new ConsultaAtividadeCicloVO();

        // Monta a consulta.
        consulta.setCnpjES(ciclo.getEntidadeSupervisionavel().getConglomeradoOuCnpj());
        consulta.setAno((short) DataUtil.getDateTimeAtual().getYear());
        consulta.setVersoesPerfilRisco(versaoPerfilRiscoMediator.buscarVersoesPerfilRisco(perfilRisco.getPk(),
                TipoObjetoVersionadorEnum.ATIVIDADE_CICLO));

        return consulta;
    }
}
