package br.gov.bcb.sisaps.src.mediator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.CargaParticipanteDao;
import br.gov.bcb.sisaps.src.dominio.AgendaCorec;
import br.gov.bcb.sisaps.src.dominio.CargaParticipante;
import br.gov.bcb.sisaps.src.dominio.EntidadeSupervisionavel;
import br.gov.bcb.sisaps.src.vo.ParticipanteComiteVO;
import br.gov.bcb.sisaps.util.geral.SisapsUtil;
import br.gov.bcb.sisaps.util.validacao.ErrorMessage;

@Service
@Scope("singleton")
public class CargaParticipanteMediator {

    private static final String EMPTY = "";
    @Autowired
    private CargaParticipanteDao cargaParticipantesDao;

    public static CargaParticipanteMediator get() {
        return SpringUtils.get().getBean(CargaParticipanteMediator.class);
    }

    public List<CargaParticipante> carregarParticpantes() {
        return cargaParticipantesDao.carregarParticipantes();
    }

    public void limparCargaParticipantes() {
        List<CargaParticipante> carregarParticpantes = carregarParticpantes();
        for (CargaParticipante cargaParticipantes : carregarParticpantes) {
            cargaParticipantesDao.delete(cargaParticipantes);
        }
        cargaParticipantesDao.flush();
    }

    public void atualizarListaParticipantes(File arquivo)  {
        BufferedReader br = null;
        String linha = EMPTY;
        String csvDivisorVirgula = ",";
       
        boolean erro = false;
        boolean priori = false;
        int cont = 1;

        try {
            br = new BufferedReader(new FileReader(arquivo));
            br.readLine();
            while (linha != null) {
                linha = br.readLine();
                if (linha != null) {
                    try {
                        String[] valor = linha.split(csvDivisorVirgula, 10);
                        if (!valor[0].equals(EMPTY)) {
                            String prioridade =  valor[0].equals(EMPTY) ? null : valor[0];
                            String email = valor[1].equals(EMPTY) ? null : valor[1];
                            String matricula = valor[2].equals(EMPTY) ? null : valor[2];
                            String nome = valor[3].equals(EMPTY) ? null : valor[3];
                            String equipe = valor[4].equals(EMPTY) ? null : valor[4];
                            String funcao = valor[5].equals(EMPTY) ? null : valor[5];
                            String equipeExcluir = valor[6].equals(EMPTY) ? null : valor[6];
                            String subordinadaExcluir = valor[7].equals(EMPTY) ? null : valor[7];
                            String equipeIncluir = valor[8].equals(EMPTY) ? null : valor[8];
                            String subordinadaIncluir = valor[9].equals(EMPTY) ? null : valor[9];
                            cont++;
                            if (subordinadaExcluir != null) {
                                if (compararString(subordinadaExcluir)) {
                                    erro = true;
                                    break;
                                }
                            }
                            if (subordinadaIncluir != null) {
                                if (compararString(subordinadaIncluir)) {
                                    erro = true;
                                    break;
                                }
                            }
                            
                            if (!prioridade.isEmpty()) {
                                if (prioridade.matches("[0-9]*")) {
                                    boolean isPkValida =
                                            ParametroPrioridadeMediator.get().buscarParametro(
                                                    Integer.parseInt(prioridade));
                                    if (!isPkValida) {
                                        priori = true;
                                        break;
                                    }

                                } else {
                                    priori = true;
                                    break;
                                }
                            }
                   
                            salvarParticipante(prioridade, email, matricula, nome, equipe, funcao, equipeExcluir,
                                    subordinadaExcluir, equipeIncluir, subordinadaIncluir);
                        }
                    } catch (Exception e) {
                        msgErroLinhaArquivo(cont, "tamanho da(s) coluna(s) maior do que o suportado no banco de dados.");
                        System.out.println(linha);
                        System.out.println(cont);
                        cargaParticipantesDao.getSessionFactory().getCurrentSession().getTransaction().rollback();
                    } finally {
                        if (erro) {
                            System.out.println(linha);
                            System.out.println(cont);
                            msgErroLinhaArquivo(cont, "as colunas 'considerar_subordinadas_excluir' e/ou "
                                    + "'considerar_subordinadas_incluir' estão"
                                    + " com valores diferentes de: 'S'|'N'|nulo.");
                            cargaParticipantesDao.getSessionFactory().getCurrentSession().getTransaction().rollback();
                        }
                        if (priori) {
                            msgErroLinhaArquivo(cont, "valor inválido para a coluna 'prioridade'.");
                            System.out.println(linha);
                            System.out.println(cont);
                            cargaParticipantesDao.getSessionFactory().getCurrentSession().getTransaction().rollback();
                        }
                    }

                }
                
            }
                cargaParticipantesDao.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void salvarParticipante(String prioridade, String email, String matricula, String nome, String equipe,
            String funcao, String equipeExcluir, String subordinadaExcluir, String equipeIncluir,
            String subordinadaIncluir) {
        CargaParticipante participante = new CargaParticipante();
        participante.setPrioridade(prioridade);
        participante.setEmail(email);
        participante.setMatricula(matricula);
        participante.setNome(nome);
        participante.setEquipe(equipe);
        participante.setFuncao(funcao);
        participante.setEquipeExcluir(equipeExcluir);
        participante.setSubordinadasExcluir(subordinadaExcluir);
        participante.setEquipeIncluir(equipeIncluir);
        participante.setSubordinadasIncluir(subordinadaIncluir);
        cargaParticipantesDao.save(participante);
    }

    private void msgErroLinhaArquivo(int cont, String texto) {
        ArrayList<ErrorMessage> erros = new ArrayList<ErrorMessage>();
        SisapsUtil.adicionarErro(erros, new ErrorMessage("Erro na linha " + cont + " do arquivo, " + texto));
        SisapsUtil.lancarNegocioException(erros);
    }

    private boolean compararString(String subordinadaExcluir) {
        return !subordinadaExcluir.equals("S") && !subordinadaExcluir.equals("N");
    }

    public List<ParticipanteComiteVO> buscarParticipantesPossiveisES(EntidadeSupervisionavel entidadeSupervisionavel) {
        return cargaParticipantesDao.buscarParticipantesPossiveisES(entidadeSupervisionavel);
    }
    
    public List<ParticipanteComiteVO> buscarParticipantesPossiveisESSemEfetivos(
            EntidadeSupervisionavel entidadeSupervisionavel, AgendaCorec agenda) {
        return cargaParticipantesDao.buscarParticipantesPossiveisESSemEfetivos(entidadeSupervisionavel, agenda);
    }

    @Transactional
    public CargaParticipante buscarCargaPorMatricula(String matricula) {
        return cargaParticipantesDao.buscarCargaPorMatricula(matricula);
    }
    
}
