/*
 * Sistema APS
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arqui contém informações proprietárias.
 */
package br.gov.bcb.sisaps.src.validacao;

import java.util.ArrayList;

import br.gov.bcb.sisaps.adaptadores.pessoa.BcPessoaAdapter;
import br.gov.bcb.sisaps.src.dominio.RegraPerfilAcesso;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.PerfilAcessoEnum;
import br.gov.bcb.sisaps.src.mediator.RegraPerfilAcessoMediator;
import br.gov.bcb.sisaps.src.vo.ComponenteOrganizacionalVO;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ConstantesMensagens;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

public class RegraPerfilAcessoValidacaoCampos {

    private RegraPerfilAcesso regraPerfilAcesso;

    public RegraPerfilAcessoValidacaoCampos(RegraPerfilAcesso regraPerfilAcesso) {
        this.regraPerfilAcesso = regraPerfilAcesso;
    }

    public void validar() {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();

        SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_REGRA_PERFIL_ACESSO_ERRO_001),
                isNenhumCampoPreenchido());

        if (regraPerfilAcesso.getLocalizacao() != null) {
            ComponenteOrganizacionalVO buscarComponenteOrganizacionalPorRotulo =
                    BcPessoaAdapter.get().buscarComponenteOrganizacionalPorRotulo(regraPerfilAcesso.getLocalizacao(),
                            null);
            if (buscarComponenteOrganizacionalPorRotulo == null) {
                SisapsUtil.adicionarErro(erros, new ErrorMessage(
                        ConstantesMensagens.MSG_APS_REGRA_PERFIL_ACESSO_ERRO_003), true);
                SisapsUtil.lancarNegocioException(erros);
            } else {

                SisapsUtil.validarObrigatoriedade(regraPerfilAcesso.getLocalizacoesSubordinadas(),
                        "Localizações subordinadas", erros);
            }

        }

        if (regraPerfilAcesso.getCodigoFuncao() != null) {
            SisapsUtil.validarObrigatoriedade(regraPerfilAcesso.getSubstitutoEventualFuncao(), "Substituto eventual",
                    erros);
            SisapsUtil.validarObrigatoriedade(regraPerfilAcesso.getSubstitutoPrazoCerto(), "Substituto prazo certo",
                    erros);
        }

        if (regraPerfilAcesso.getMatricula() != null) {
            SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_REGRA_PERFIL_ACESSO_ERRO_004),
                    !BcPessoaAdapter.get().matriculaValida(regraPerfilAcesso.getMatricula()));
        }

        SisapsUtil.adicionarErro(erros, new ErrorMessage(ConstantesMensagens.MSG_APS_REGRA_PERFIL_ACESSO_ERRO_002),
                !RegraPerfilAcessoMediator.get().isAcessoPermitido(PerfilAcessoEnum.ADMINISTRADOR));

        SisapsUtil.lancarNegocioException(erros);

    }

    private boolean isNenhumCampoPreenchido() {
        boolean isDadosLocalizacaoNaoPreenchidos =
                regraPerfilAcesso.getLocalizacao() == null && regraPerfilAcesso.getLocalizacoesSubordinadas() == null;
        boolean isDadosFuncaoNaoPreenchidos =
                regraPerfilAcesso.getCodigoFuncao() == null && regraPerfilAcesso.getSubstitutoEventualFuncao() == null
                        && regraPerfilAcesso.getSubstitutoPrazoCerto() == null;
        return isDadosLocalizacaoNaoPreenchidos && isDadosFuncaoNaoPreenchidos
                && (regraPerfilAcesso.getSituacao() == null && regraPerfilAcesso.getMatricula() == null);
    }
}