package br.gov.bcb.sisaps.util.objetos;


public interface IObjetoPersistenteAuditavelManipulavel {

    void setAlterarDataUltimaAtualizacao(boolean alterarData);
    
    boolean isAlterarDataUltimaAtualizacao();
}