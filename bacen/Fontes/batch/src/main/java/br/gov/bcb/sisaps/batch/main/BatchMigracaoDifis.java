package br.gov.bcb.sisaps.batch.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


import jcifs.Config;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import br.gov.bcb.comum.bcjcifs.JCIFSHelper;
import br.gov.bcb.comum.excecoes.BCInfraException;
import br.gov.bcb.sisaps.adaptadores.bcmail.BcMailAdapter;
import br.gov.bcb.sisaps.adaptadores.pessoa.Email;
import br.gov.bcb.sisaps.batch.AbstractBatchApplication;
import br.gov.bcb.sisaps.src.dominio.Atividade;
import br.gov.bcb.sisaps.src.dominio.CelulaRiscoControle;
import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.Matriz;
import br.gov.bcb.sisaps.src.dominio.ParametroEmailAPS;
import br.gov.bcb.sisaps.src.dominio.PerfilRisco;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.batchmigracaodifis.NotasGeraisFinal;
import br.gov.bcb.sisaps.src.dominio.enumeracoes.TipoObjetoVersionadorEnum;
import br.gov.bcb.sisaps.src.mediator.AtividadeMediator;
import br.gov.bcb.sisaps.src.mediator.CelulaRiscoControleMediator;
import br.gov.bcb.sisaps.src.mediator.CicloMediator;
import br.gov.bcb.sisaps.src.mediator.GeradorAnexoMediator;
import br.gov.bcb.sisaps.src.mediator.LinhaMatrizMediator;
import br.gov.bcb.sisaps.src.mediator.MigracaoDifisMediator;
import br.gov.bcb.sisaps.src.mediator.ParametroEmailAPSMediator;
import br.gov.bcb.sisaps.src.mediator.PerfilRiscoMediator;
import br.gov.bcb.sisaps.src.mediator.VersaoPerfilRiscoMediator;
import br.gov.bcb.sisaps.src.util.AnexoBuffer;
import br.gov.bcb.sisaps.util.geral.DataUtil;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.utils.logging.BCLogFactory;
import br.gov.bcb.utils.logging.BCLogger;

public class BatchMigracaoDifis extends AbstractBatchApplication {

    private static final String APS_BATCH_DIFIS = "APS.BATCH.DIFIS";
    private static final String BR = " \n";
    private static final String CICLO_MEDIATOR = "cicloMediator";
    private static final BCLogger LOG = BCLogFactory.getLogger("BatchMigracaoDifis");
    private static final String NOMEARQUIVO = "relatorioErrosBatch" + DataUtil.dataFormatadaBacth();
    protected List<String> erros; 
    protected Boolean possuiErros = false;

    public static void main(String[] args) {
        new BatchMigracaoDifis().init();
    }

    @Override
    protected void executar() {
      
        erros = new LinkedList<String>();
        String inicio = "INÍCIO DA ROTINA DE MIGRAÇÃO DE DADOS DO APS PARA SISTEMA BI DA DIFIS.";
        LOG.info(inicio);
        erros.add(inicio + BR);
        CicloMediator cicloMediator = (CicloMediator) getSpringContext().getBean(CICLO_MEDIATOR);
        // Ciclos diferentes de excluído.
        List<Ciclo> ciclos = cicloMediator.consultarCiclosMigracao();
        
        String qtdCiclos = ciclos.size() + " Ciclos foram encontrados.";
        LOG.info(qtdCiclos);
        erros.add(qtdCiclos  + BR);
        try {
            criarArquivosDadosAPS(ciclos);
            // CHECKSTYLE:OFF
        } catch (Throwable e) {
            LOG.info("ERRO: " + e.getClass());
            LOG.info("CAUSA: " + e.getCause());
            LOG.info(e.getMessage());
            LOG.info(e.getLocalizedMessage());
        } finally {
            LOG.info("FIM DA ROTINA DE MIGRAÇÃO DE DADOS DO APS PARA SISTEMA BI DA DIFIS.");
            
            if(possuiErros){
                
                AnexoBuffer anexo = new AnexoBuffer();
                anexo.setInputStream(criarAquivoErro(erros));
                anexo.setNome(NOMEARQUIVO + ".txt");
                
                try {
                    
                    GeradorAnexoMediator.get().gerarArquivoAnexoUpload(anexo.getInputStream(), anexo.getNome(), APS_BATCH_DIFIS,
                            criarDiretorio().getCanonicalPath());
                    
                    ParametroEmailAPSMediator parametroEmailMediator = ParametroEmailAPSMediator.get();
                    ParametroEmailAPS parametroEmailAPSCon = parametroEmailMediator.buscarEmailParticipante("BATCHDIFIS");
                    List<String> destinatario = Arrays.asList(parametroEmailAPSCon.getValor().split(";")); 
                    Email email = new Email();
                    email.setDestinatarios(destinatario);
                    email.setAssunto("ERROR NA MIGRAÇÃO DE DADOS DO APS PARA SISTEMA BI DA DIFIS.");
                    email.setCorpo("SEGUE ANEXO DOCUMENTO");
                    email.setRemetente("BATCH APS");
                    email.getAnexos().add(anexo);
                    BcMailAdapter.get().enviarEmail(email);
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
                
            }
            // retirar para rodar teste local
            //System.exit(0);
        }
    }
    
    private SmbFile criarDiretorio() throws BCInfraException, SmbException {
        JCIFSHelper jcifs = JCIFSHelper.getInstance();
        //Config.setProperty("jcifs.smb.client.domain", "BCNET");
        String nomeDirRaiz = "DIFIS/";
        SmbFile smbFile = jcifs.getSmbFile(APS_BATCH_DIFIS, nomeDirRaiz);
        if (!smbFile.exists()) {
            smbFile.mkdir();
        }
        return smbFile;
    }
    
    
    private InputStream criarAquivoErro( List<String>  listaErros) {
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

    private void criarArquivosDadosAPS(List<Ciclo> ciclos) {
        PerfilRisco perfilRisco;
        Matriz matriz = null;
        List<VersaoPerfilRisco> versoesPerfilRiscoCelulas;
        List<Atividade> listaAtividade;
        List<CelulaRiscoControle> listaChoices;
        PerfilRiscoMediator perfilRiscoMediator =
                (PerfilRiscoMediator) getSpringContext().getBean("perfilRiscoMediator");
        VersaoPerfilRiscoMediator versaoPerfilRiscoMediator =
                (VersaoPerfilRiscoMediator) getSpringContext().getBean("versaoPerfilRiscoMediator");
        AtividadeMediator atividadeMediator = (AtividadeMediator) getSpringContext().getBean("atividadeMediator");
        LinhaMatrizMediator linhaMatrizMediator =
                (LinhaMatrizMediator) getSpringContext().getBean("linhaMatrizMediator");
        CelulaRiscoControleMediator celulaRiscoControleMediator =
                (CelulaRiscoControleMediator) getSpringContext().getBean("celulaRiscoControleMediator");
        MigracaoDifisMediator migracaoDifisMediator =
                (MigracaoDifisMediator) getSpringContext().getBean("migracaoDifisMediator");
        CicloMediator cicloMediator = (CicloMediator) getSpringContext().getBean(CICLO_MEDIATOR);
        // Excluir registros de notas.
        migracaoDifisMediator.excluirNotas();
        Ciclo cicloInicializado = null;
        boolean erro = false;
        for (Ciclo ciclo : ciclos) {
            cicloInicializado = cicloMediator.buscarCicloPorPK(ciclo.getPk());
            LOG.info("PROCESSANDO CICLO: " + cicloInicializado.getPk());
            try {
                // Perfil de risco atual
                perfilRisco = perfilRiscoMediator.obterPerfilRiscoAtual(cicloInicializado.getPk());
                LOG.info("PERFIL RISCO: " + perfilRisco.getPk());
                if (SisapsUtil.isNaoNuloOuVazio(perfilRisco)) {
                    // Matriz vigente
                    matriz = perfilRiscoMediator.getMatrizAtualPerfilRisco(perfilRisco);
                    // Versões perfil de risco celulas arcs
                    versoesPerfilRiscoCelulas = versaoPerfilRiscoMediator.buscarVersoesPerfilRisco(perfilRisco.getPk(),
                            TipoObjetoVersionadorEnum.CELULA_RISCO_CONTROLE);
                    listaAtividade = new LinkedList<Atividade>();
                    if (SisapsUtil.isNaoNuloOuVazio(matriz)) {
                        LOG.info("MATRIZ: " + matriz.getPk());
                        listaAtividade = atividadeMediator.buscarAtividadesMatriz(matriz);
                        linhaMatrizMediator.consultar(matriz, false, listaAtividade);
                        linhaMatrizMediator.consultar(matriz, true, listaAtividade);
                    }
                    // Células
                    listaChoices = celulaRiscoControleMediator.buscarParametroDaMatriz(listaAtividade,
                            versoesPerfilRiscoCelulas);
                    NotasGeraisFinal notasGeraisFinal = migracaoDifisMediator.criarDadosNotasGerais(cicloInicializado,
                            perfilRisco, matriz, versoesPerfilRiscoCelulas, listaChoices);
                    LOG.info("NOTA GERAL: " + notasGeraisFinal.getPk());
                    if (SisapsUtil.isNaoNuloOuVazio(matriz)) {
                        migracaoDifisMediator.criarNotasGruposArcsQlt(perfilRisco, matriz, listaChoices,
                                versoesPerfilRiscoCelulas, notasGeraisFinal);
                    }
                    migracaoDifisMediator.criarComponentesElementosQnt(perfilRisco, notasGeraisFinal);
                    migracaoDifisMediator.criarDadosPerfilRiscoCiclo(cicloInicializado, notasGeraisFinal);
                }
            } catch (Throwable e) {
                possuiErros = true;
                String errorCiclo= "ERRO DURANTE PROCESSAMENTO: \nCICLO ID: " + ciclo.getPk() + " \nES: " + cicloInicializado.getEntidadeSupervisionavel().getNomeConglomeradoFormatado();
                LOG.info(errorCiclo);
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                String treceError = "ERRO: " + sw.toString();
                LOG.info(treceError);
                StringBuffer errociclo = new StringBuffer(errorCiclo + BR);
                errociclo.append(treceError + BR);
                erros.add(errociclo.toString());
            }
        }
        
        
     

        
        
       
    }

}
