package br.gov.bcb.sisaps.src.vo.analisequantitativa;

import br.gov.bcb.sisaps.src.dominio.Ciclo;
import br.gov.bcb.sisaps.src.dominio.VersaoPerfilRisco;
import br.gov.bcb.sisaps.src.dominio.analisequantitativa.DataBaseES;
import br.gov.bcb.sisaps.util.consulta.Consulta;

public class ConsultaQuadroPosicaoFinanceiraVO extends Consulta<QuadroPosicaoFinanceiraVO> {
    private DataBaseES dataBaseES;
    private VersaoPerfilRisco versaoPerfilRisco;
    private Ciclo ciclo;

    public DataBaseES getDataBaseES() {
        return dataBaseES;
    }

    public void setDataBaseES(DataBaseES dataBaseES) {
        this.dataBaseES = dataBaseES;
    }

    public VersaoPerfilRisco getVersaoPerfilRisco() {
        return versaoPerfilRisco;
    }

    public void setVersaoPerfilRisco(VersaoPerfilRisco versaoPerfilRisco) {
        this.versaoPerfilRisco = versaoPerfilRisco;
    }

    public Ciclo getCiclo() {
        return ciclo;
    }

    public void setCiclo(Ciclo ciclo) {
        this.ciclo = ciclo;
    }

}
