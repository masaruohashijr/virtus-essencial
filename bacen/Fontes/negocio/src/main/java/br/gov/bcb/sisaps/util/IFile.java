package br.gov.bcb.sisaps.util;

import java.io.IOException;
import java.io.InputStream;

public interface IFile {


    String getNome();

    InputStream getInputStream() throws IOException;

}
