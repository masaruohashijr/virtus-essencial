package br.gov.bcb.sisaps.src.util;

import java.util.ArrayList;
import java.util.List;

public class BufferAnexos {
    
    private static ThreadLocal<List<AnexoBuffer>> bufferInclusaoAnexos = new ThreadLocal<List<AnexoBuffer>>() {
        protected java.util.List<AnexoBuffer> initialValue() {
            return new ArrayList<AnexoBuffer>();
        };
    };
    
    private static ThreadLocal<List<AnexoBuffer>> bufferExclusaoAnexos = new ThreadLocal<List<AnexoBuffer>>() {
        protected java.util.List<AnexoBuffer> initialValue() {
            return new ArrayList<AnexoBuffer>();
        };
    };

    public static List<AnexoBuffer> getBufferInclusaoAnexos() {
        return bufferInclusaoAnexos.get();
    }
    
    public static void resetLocalThreadBufferInclusao() {
        bufferInclusaoAnexos.remove();
    }
    
    public static List<AnexoBuffer> getBufferExclusaoAnexos() {
        return bufferExclusaoAnexos.get();
    }
    
    public static void resetLocalThreadBufferExclusao() {
        bufferExclusaoAnexos.remove();
    }

}
