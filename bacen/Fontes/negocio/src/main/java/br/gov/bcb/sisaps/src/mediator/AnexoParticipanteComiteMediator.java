package br.gov.bcb.sisaps.src.mediator;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.bcb.app.stuff.util.SpringUtils;
import br.gov.bcb.sisaps.src.dao.AnexoParticipanteComiteDao;
import br.gov.bcb.sisaps.src.dominio.AnexoParticipanteComite;
import br.gov.bcb.sisaps.src.validacao.RegraAnexosValidacaoCSV;
import br.gov.bcb.sisaps.util.geral.DataUtil;

@Service
@Scope("singleton")
public class AnexoParticipanteComiteMediator {
    @Autowired
    private AnexoParticipanteComiteDao anexoComiteDao;
    @Autowired
    private CargaParticipanteMediator cargaParticipanteMediator;

    public static AnexoParticipanteComiteMediator get() {
        return SpringUtils.get().getBean(AnexoParticipanteComiteMediator.class);
    }

    @Transactional
    public String anexarArquivo(InputStream inputStream, File file) {
        new RegraAnexosValidacaoCSV().validar(inputStream, file);
        AnexoParticipanteComite anexo = incluirAnexo(inputStream, file);
        anexoComiteDao.save(anexo);
        cargaParticipanteMediator.limparCargaParticipantes();
        cargaParticipanteMediator.atualizarListaParticipantes(file);
        return "Arquivo incluído com sucesso.";
    }

    private AnexoParticipanteComite incluirAnexo(InputStream inputStream, File file) {
        AnexoParticipanteComite anexoComite = new AnexoParticipanteComite();
        anexoComite.setInputStream(inputStream);
        anexoComite.setNome(file.getName());
        anexoComite.setDataHoraUpload(DataUtil.getDateTimeAtual());
        return anexoComite;
    }

    public List<AnexoParticipanteComite> listarAnexos() {
        return anexoComiteDao.listarAnexos();
    }

}
