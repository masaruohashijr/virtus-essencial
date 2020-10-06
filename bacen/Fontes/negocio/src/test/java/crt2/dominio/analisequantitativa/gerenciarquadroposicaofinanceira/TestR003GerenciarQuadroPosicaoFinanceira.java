package crt2.dominio.analisequantitativa.gerenciarquadroposicaofinanceira;

import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.DataBaseES;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoConta;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoInformacaoEnum;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativa.DataBaseESMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativa.OutraInformacaoQuadroPosicaoFinanceiraMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativa.QuadroPosicaoFinanceiraMediator;
import br.gov.bcb.sisaps.src.vo.analisequantitativa.QuadroPosicaoFinanceiraVO;
import br.gov.bcb.sisaps.stubs.BcMailStub;
import crt2.ConfiguracaoTestesNegocio;

public class TestR003GerenciarQuadroPosicaoFinanceira extends ConfiguracaoTestesNegocio {

    private Integer perfil;
    private Integer database;

    public void salvar() {
        QuadroPosicaoFinanceiraMediator mediator = QuadroPosicaoFinanceiraMediator.get();
        OutraInformacaoQuadroPosicaoFinanceiraMediator outraInfoMediator = 
                OutraInformacaoQuadroPosicaoFinanceiraMediator.get();
        mediator.descartarAlteracoesNovoQuadroAtual(getDatabase(), getPerfilRisco());
        mediator.salvarContas(getVo(), TipoConta.ATIVO);
        mediator.salvarContas(getVo(), TipoConta.PASSIVO);
        outraInfoMediator.salvarAjustesOutrasInformacoes(getVo(), TipoInformacaoEnum.PATRIMONIO);
        outraInfoMediator.salvarAjustesOutrasInformacoes(getVo(), TipoInformacaoEnum.RESULTADO);
        outraInfoMediator.salvarAjustesOutrasInformacoes(getVo(), TipoInformacaoEnum.INDICE);
    }

    public void salvarConclusao() {
        BcMailStub.limpaListaEMails();
        QuadroPosicaoFinanceiraMediator mediator = QuadroPosicaoFinanceiraMediator.get();
        mediator.incluirAjustesQuadroVigenteNovoModelo(null, getPerfilRisco());
        mediator.salvarConclusao(getVo(), getPerfilRisco());
    }

    public QuadroPosicaoFinanceiraVO getVo() {
        QuadroPosicaoFinanceiraVO vo =
                QuadroPosicaoFinanceiraMediator.get().preencherNovoQuadroNovoModelo(getDatabase(), getPerfilRisco());
        return vo;
    }

    public PerfilRisco getPerfilRisco() {
        return PerfilRiscoMediator.get().obterPerfilRiscoPorPk(perfil);
    }

    public void setPerfil(Integer perfil) {
        this.perfil = perfil;
    }

    public DataBaseES getDatabase() {
        return DataBaseESMediator.get().buscarPorIdentificador(database,
                getPerfilRisco().getCiclo().getEntidadeSupervisionavel().getConglomeradoOuCnpj());
    }

    public void setDatabase(Integer database) {
        this.database = database;
    }

}
