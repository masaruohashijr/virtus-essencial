package br.gov.bcb.sisaps.batch.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

import org.apache.commons.collections.CollectionUtils;

import br.gov.bcb.comum.bcjcifs.JCIFSHelper;
import br.gov.bcb.comum.excecoes.BCInfraException;
import br.gov.bcb.sisaps.batch.AbstractBatchApplication;
import br.gov.bcb.sisaps.src.dominio.AnexoARC;
import br.gov.bcb.sisaps.src.dominio.AnexoApresentacao;
import br.gov.bcb.sisaps.src.dominio.AnexoCiclo;
import br.gov.bcb.sisaps.src.dominio.AnexoPosCorec;
import br.gov.bcb.sisaps.src.dominio.AnexoQuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.AvaliacaoRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.ConclusaoES;
import br.gov.bcb.sisaps.src.dominio.Elemento;
import br.gov.bcb.sisaps.src.dominio.ItemElemento;
import br.gov.bcb.sisaps.src.dominio.PerfilAtuacaoES;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.QuadroPosicaoFinanceira;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnaliseQuantitativaAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.AnexoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ElementoAQT;
import br.gov.bcb.sisaps.src.dominio.analisequantitativaaqt.ItemElementoAQT;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoEntidadeAnexoDocumentoEnum;
import br.gov.bcb.sisaps.src.mediator.AnexoApresentacaoMediator;
import br.gov.bcb.sisaps.src.mediator.AnexoArcMediator;
import br.gov.bcb.sisaps.src.mediator.AnexoCicloMediator;
import br.gov.bcb.sisaps.src.mediator.AnexoDocumentoMediator;
import br.gov.bcb.sisaps.src.mediator.AnexoPosCorecMediator;
import br.gov.bcb.sisaps.src.mediator.AnexoQuadroPosicaoFinanceiraMediator;
import br.gov.bcb.sisaps.src.mediator.AvaliacaoRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.ConclusaoESMediator;
import br.gov.bcb.sisaps.src.mediator.ElementoMediator;
import br.gov.bcb.sisaps.src.mediator.GeradorAnexoMediator;
import br.gov.bcb.sisaps.src.mediator.ItemElementoMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilAtuacaoESMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativa.QuadroPosicaoFinanceiraMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnaliseQuantitativaAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.AnexoAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ElementoAQTMediator;
import br.gov.bcb.sisaps.src.mediator.analisequantitativaaqt.ItemElementoAQTMediator;
import br.gov.bcb.sisaps.src.util.AnexoBuffer;
import br.gov.bcb.sisaps.src.vo.AnexoApresentacaoVO;
import br.gov.bcb.sisaps.src.vo.AnexoDocumentoVo;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.util.validacao.NegocioException;
import br.gov.bcb.utils.logging.BCLogFactory;
import br.gov.bcb.utils.logging.BCLogger;

public class BatchVerificacaoAnexos extends AbstractBatchApplication {

    private static final String DIRETORIO_ANEXOS_CICLO = "APS.ANEXO.CICLO.DIR";
    private static final BCLogger LOG = BCLogFactory.getLogger("BatchVerificacaoAnexos");
    private static final String NOMEARQUIVO = "relatorioAnexosBatch" + DataUtil.dataFormatadaBacth();
    private static final String BR = " \n";
    protected List<String> saida;

    public static void main(String[] args) {
        new BatchVerificacaoAnexos().init();
    }

    @Override
    protected void executar() {
        LOG.info("ROTINA DE VERIFICACAO DE ANEXOS");
        try {
            saida = new LinkedList<String>();
            saida.add("VERIFICACAO DE ANEXOS:" + BR);
            verificaAnexosApresentacao();
        } catch (Exception e) {
            LOG.info(e.toString());
        } finally {
            AnexoBuffer anexo = new AnexoBuffer();
            anexo.setInputStream(criarAquivoErro(saida));
            anexo.setNome(NOMEARQUIVO + ".txt");
            try {
                GeradorAnexoMediator.get().gerarArquivoAnexoUpload(anexo.getInputStream(), anexo.getNome(),
                        DIRETORIO_ANEXOS_CICLO, criarDiretorio().getCanonicalPath());
            } catch (BCInfraException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SmbException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //retirar para rodar teste local
            System.exit(0);
        }
    }

    private InputStream criarAquivoErro(List<String> listaErros) {
        File tempFile = null;
        FileWriter arquivo = null;
        try {
            tempFile = File.createTempFile(NOMEARQUIVO, ".txt");
            arquivo = new FileWriter(tempFile);
            for (String string : listaErros) {
                arquivo.write(string);
            }
            arquivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (arquivo != null) {
                    arquivo.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (tempFile != null) {
            try {
                return new FileInputStream(tempFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private SmbFile criarDiretorio() throws BCInfraException, SmbException {
        JCIFSHelper jcifs = JCIFSHelper.getInstance();
        String nomeDirRaiz = "RELATORIO_ANEXOS/";
        SmbFile smbFile = jcifs.getSmbFile(DIRETORIO_ANEXOS_CICLO, nomeDirRaiz);
        if (!smbFile.exists()) {
            smbFile.mkdir();
        }
        return smbFile;
    }

    private void verificaAnexosApresentacao() {

        // Anexos apresentação
        List<AnexoApresentacao> anexosApresentacao = AnexoApresentacaoMediator.get().listarAnexos();
        if (CollectionUtils.isNotEmpty(anexosApresentacao)) {
            LOG.info("QUANTIDADE APRESENTACAO: " + anexosApresentacao.size());
            for (AnexoApresentacao anexoApresentacao : anexosApresentacao) {
                AnexoApresentacaoVO anexoVO = anexoApresentacao.toVO();
                try {
                    byte[] arquivoBytes = AnexoApresentacaoMediator.get().recuperarArquivo(anexoVO);
                } catch (NegocioException e) {
                    String caminho = AnexoApresentacaoMediator.get().caminhoArquivo(anexoVO);
                    saida.add(caminho);
                    saida.add(BR);
                    continue;
                }
            }
        }

        // Anexos AQT
        List<AnexoAQT> anexosAQT = AnexoAQTMediator.get().listarAnexos();
        LOG.info("QUANTIDADE AQT: " + anexosAQT.size());
        if (CollectionUtils.isNotEmpty(anexosAQT)) {
            for (AnexoAQT anexoAQT : anexosAQT) {
                AnaliseQuantitativaAQT aqt =
                        AnaliseQuantitativaAQTMediator.get().buscar(anexoAQT.getAnaliseQuantitativaAQT().getPk());
                if (aqt != null) {
                    Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(aqt.getCiclo().getPk());
                    if (ciclo != null) {
                        try {
                            byte[] arquivoBytes =
                                    AnexoAQTMediator.get().recuperarArquivo(anexoAQT.getLink(), ciclo, aqt);
                        } catch (NegocioException e) {
                            String caminho = AnexoAQTMediator.get().caminhoArquivo(anexoAQT.getLink(), ciclo, aqt);
                            saida.add(caminho);
                            saida.add(BR);
                            continue;
                        }
                    }
                }
            }
        }

        // Anexos ARC
        List<AnexoARC> anexosARC = AnexoArcMediator.get().listarAnexos();
        if (CollectionUtils.isNotEmpty(anexosARC)) {
            LOG.info("QUANTIDADE ARC: " + anexosARC.size());
            for (AnexoARC anexoARC : anexosARC) {
                AnexoARC anexoInicializado = AnexoArcMediator.get().buscarAnexoPkInicializado(anexoARC.getPk());
                if (anexoInicializado != null && anexoInicializado.getAvaliacaoRiscoControle() != null) {
                    AvaliacaoRiscoControle arcInicializado =
                            AvaliacaoRiscoControleMediator.get().buscar(
                                    anexoInicializado.getAvaliacaoRiscoControle().getPk());
                    if (arcInicializado != null) {
                        Ciclo cicloArc = AvaliacaoRiscoControleMediator.get().buscarCicloARC(arcInicializado);
                        if (cicloArc != null) {
                            Ciclo cicloInicializado = CicloMediator.get().buscarCicloPorPK(cicloArc.getPk());
                            if (cicloInicializado != null) {
                                try {
                                    byte[] arquivoBytes =
                                            AnexoArcMediator.get().recuperarArquivo(anexoInicializado.getLink(),
                                                    cicloInicializado, arcInicializado);
                                } catch (NegocioException e) {
                                    String caminho =
                                            AnexoArcMediator.get().caminhoArquivo(anexoInicializado.getLink(),
                                                    cicloInicializado, arcInicializado);
                                    saida.add(caminho);
                                    saida.add(BR);
                                    continue;
                                }
                            }
                        }
                    }
                }
            }
        }

        // Anexos Conclusão
        List<AnexoDocumentoVo> anexosConclusao = AnexoDocumentoMediator.get().listarAnexosConclusao();
        if (CollectionUtils.isNotEmpty(anexosConclusao)) {
            LOG.info("QUANTIDADE CONCLUSAO: " + anexosConclusao.size());
            for (AnexoDocumentoVo anexo : anexosConclusao) {
                if (anexo.getSequencial() > 0) {
                    ConclusaoES conclusao = ConclusaoESMediator.get().buscarPorPk(anexo.getSequencial());
                    if (conclusao != null) {
                        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(conclusao.getCiclo().getPk());
                        if (ciclo != null) {
                            try {
                                byte[] arquivoBytes =
                                        AnexoDocumentoMediator.get().recuperarArquivo(anexo.getLink(), conclusao,
                                                TipoEntidadeAnexoDocumentoEnum.CONCLUSAO, ciclo);
                            } catch (NegocioException e) {
                                String caminho =
                                        AnexoDocumentoMediator.get().caminhoArquivo(anexo.getLink(), conclusao,
                                                TipoEntidadeAnexoDocumentoEnum.CONCLUSAO, ciclo);
                                saida.add(caminho);
                                saida.add(BR);
                                continue;
                            }
                        }
                    }
                }
            }
        }

        // Anexos Perfil atuação
        List<AnexoDocumentoVo> anexosPerfilAtuacao = AnexoDocumentoMediator.get().listarAnexosPerfilAtuacao();
        if (CollectionUtils.isNotEmpty(anexosPerfilAtuacao)) {
            LOG.info("QUANTIDADE PERFIL ATUACAO: " + anexosPerfilAtuacao.size());
            for (AnexoDocumentoVo anexo : anexosPerfilAtuacao) {
                if (anexo.getSequencial() > 0) {
                    PerfilAtuacaoES perfilAtuacao = PerfilAtuacaoESMediator.get().buscarPorPk(anexo.getSequencial());
                    if (perfilAtuacao != null) {
                        Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(perfilAtuacao.getCiclo().getPk());
                        if (ciclo != null) {
                            try {
                                byte[] arquivoBytes =
                                        AnexoDocumentoMediator.get().recuperarArquivo(anexo.getLink(), perfilAtuacao,
                                                TipoEntidadeAnexoDocumentoEnum.PERFIL_ATUACAO, ciclo);
                            } catch (NegocioException e) {
                                String caminho =
                                        AnexoDocumentoMediator.get().caminhoArquivo(anexo.getLink(), perfilAtuacao,
                                                TipoEntidadeAnexoDocumentoEnum.PERFIL_ATUACAO, ciclo);
                                saida.add(caminho);
                                saida.add(BR);
                                continue;
                            }
                        }
                    }
                }
            }
        }

        // Anexos Item AQT
        List<AnexoDocumentoVo> anexosItemAQT = AnexoDocumentoMediator.get().listarAnexosItemAQT();
        if (CollectionUtils.isNotEmpty(anexosItemAQT)) {
            LOG.info("QUANTIDADE ITEM AQT: " + anexosItemAQT.size());
            for (AnexoDocumentoVo anexo : anexosItemAQT) {
                if (anexo.getSequencial() > 0) {
                    ItemElementoAQT itemAQT = ItemElementoAQTMediator.get().buscarPorPk(anexo.getSequencial());
                    if (itemAQT != null) {
                        ElementoAQT elemento = ElementoAQTMediator.get().buscarPorPk(itemAQT.getElemento().getPk());
                        if (elemento != null) {
                            AnaliseQuantitativaAQT analise =
                                    AnaliseQuantitativaAQTMediator.get().buscar(
                                            elemento.getAnaliseQuantitativaAQT().getPk());
                            if (analise != null) {
                                Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(analise.getCiclo().getPk());
                                if (ciclo != null) {
                                    try {
                                        byte[] arquivoBytes =
                                                AnexoDocumentoMediator.get().recuperarArquivo(anexo.getLink(), itemAQT,
                                                        TipoEntidadeAnexoDocumentoEnum.ITEM_ELEMENTO_AQT, ciclo);
                                    } catch (NegocioException e) {
                                        String caminho =
                                                AnexoDocumentoMediator.get().caminhoArquivo(anexo.getLink(), itemAQT,
                                                        TipoEntidadeAnexoDocumentoEnum.ITEM_ELEMENTO_AQT, ciclo);
                                        saida.add(caminho);
                                        saida.add(BR);
                                        continue;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Anexos Item ARC
        List<AnexoDocumentoVo> anexosItemARC = AnexoDocumentoMediator.get().listarAnexosItemARC();
        if (CollectionUtils.isNotEmpty(anexosItemARC)) {
            LOG.info("QUANTIDADE ITEM ARC: " + anexosItemARC.size());
            for (AnexoDocumentoVo anexo : anexosItemARC) {
                if (anexo.getSequencial() > 0) {
                    ItemElemento itemARC = ItemElementoMediator.get().buscarPorPk(anexo.getSequencial());
                    if (itemARC != null) {
                        Elemento elemento = ElementoMediator.get().buscarPorPk(itemARC.getElemento().getPk());
                        if (elemento != null) {
                            Ciclo ciclo =
                                    AvaliacaoRiscoControleMediator.get().buscarCicloARC(
                                            elemento.getAvaliacaoRiscoControle());
                            if (ciclo != null) {
                                Ciclo cicloInicializado = CicloMediator.get().buscarCicloPorPK(ciclo.getPk());
                                if (cicloInicializado != null) {
                                    try {
                                        byte[] arquivoBytes =
                                                AnexoDocumentoMediator.get()
                                                        .recuperarArquivo(anexo.getLink(), itemARC,
                                                                TipoEntidadeAnexoDocumentoEnum.ITEM_ELEMENTO,
                                                                cicloInicializado);
                                    } catch (NegocioException e) {
                                        String caminho =
                                                AnexoDocumentoMediator.get()
                                                        .caminhoArquivo(anexo.getLink(), itemARC,
                                                                TipoEntidadeAnexoDocumentoEnum.ITEM_ELEMENTO,
                                                                cicloInicializado);
                                        saida.add(caminho);
                                        saida.add(BR);
                                        continue;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Anexos Pós Corec
        List<AnexoPosCorec> anexosPosCorec = AnexoPosCorecMediator.get().listarAnexos();
        if (CollectionUtils.isNotEmpty(anexosPosCorec)) {
            LOG.info("QUANTIDADE POSCOREC: " + anexosPosCorec.size());
            for (AnexoPosCorec anexoPosCorec : anexosPosCorec) {
                AnexoPosCorec anexoIncializado =
                        AnexoPosCorecMediator.get().buscarAnexoPkInicializado(anexoPosCorec.getPk());
                if (anexoIncializado != null) {
                    Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(anexoIncializado.getCiclo().getPk());
                    if (ciclo != null) {
                        try {
                            byte[] arquivoBytes =
                                    AnexoPosCorecMediator.get().recuperarArquivo(anexoPosCorec.getLink(), ciclo,
                                            anexoPosCorec.getTipo());
                        } catch (NegocioException e) {
                            String caminho =
                                    AnexoPosCorecMediator.get().caminhoArquivo(anexoPosCorec.getLink(), ciclo,
                                            anexoPosCorec.getTipo());
                            saida.add(caminho);
                            saida.add(BR);
                            continue;
                        }
                    }
                }
            }
        }

        // Anexos Quadro da posição financeira
        List<AnexoQuadroPosicaoFinanceira> anexosQuadro = AnexoQuadroPosicaoFinanceiraMediator.get().listarAnexos();
        if (CollectionUtils.isNotEmpty(anexosQuadro)) {
            LOG.info("QUANTIDADE QUADRO: " + anexosPosCorec.size());
            for (AnexoQuadroPosicaoFinanceira anexoQuadroPosicaoFinanceira : anexosQuadro) {
                AnexoQuadroPosicaoFinanceira anexoInicializado =
                        AnexoQuadroPosicaoFinanceiraMediator.get().buscarAnexoPkInicializado(
                                anexoQuadroPosicaoFinanceira.getPk());
                if (anexoInicializado != null) {
                    QuadroPosicaoFinanceira quadro =
                            QuadroPosicaoFinanceiraMediator.get().buscarQuadroPorPkInicializado(
                                    anexoInicializado.getQuadroPosicaoFinanceira().getPk());
                    if (quadro != null) {
                        try {
                            byte[] arquivoBytes =
                                    AnexoQuadroPosicaoFinanceiraMediator.get().recuperarArquivo(
                                            anexoInicializado.getLink(), quadro);
                        } catch (NegocioException e) {
                            String caminho =
                                    AnexoQuadroPosicaoFinanceiraMediator.get().caminhoArquivo(
                                            anexoInicializado.getLink(), quadro);
                            saida.add(caminho);
                            saida.add(BR);
                            continue;
                        }
                    }
                }
            }
        }

        // Anexos Ciclo
        List<AnexoCiclo> anexosCiclo = AnexoCicloMediator.get().listarAnexos();
        if (CollectionUtils.isNotEmpty(anexosQuadro)) {
            LOG.info("QUANTIDADE CICLO: " + anexosCiclo.size());
            for (AnexoCiclo anexoCiclo : anexosCiclo) {
                AnexoCiclo anexoCicloInicializado =
                        AnexoCicloMediator.get().buscarAnexoPkInicializado(anexoCiclo.getPk());
                if (anexoCicloInicializado != null) {
                    Ciclo ciclo = CicloMediator.get().buscarCicloPorPK(anexoCicloInicializado.getCiclo().getPk());
                    if (ciclo != null) {
                        try {
                            byte[] arquivoBytes =
                                    AnexoCicloMediator.get().recuperarArquivo(anexoCicloInicializado.getLink(), ciclo,
                                            anexoCicloInicializado.getVersaoPerfilRisco());
                        } catch (NegocioException e) {
                            String caminho =
                                    AnexoCicloMediator.get().caminhoArquivo(anexoCicloInicializado.getLink(), ciclo,
                                            anexoCicloInicializado.getVersaoPerfilRisco());
                            saida.add(caminho);
                            saida.add(BR);
                            continue;
                        }
                    }
                }
            }
        }

        LOG.info("FIM");
    }
}