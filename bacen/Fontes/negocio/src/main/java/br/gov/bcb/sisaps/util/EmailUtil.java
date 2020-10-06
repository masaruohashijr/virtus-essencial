/*
] * Sistema: Sisaps
 *
 * Copyright (c) Banco Central do Brasil.
 *
 * Este software é confidencial e propriedade do Banco Central do Brasil.
 * Não é permitida sua distribuição ou divulgação do seu conteúdo sem
 * expressa autorização do Banco Central.
 * Este arquivo contém informações proprietárias.
 */
package br.gov.bcb.sisaps.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.gov.bcb.app.stuff.exception.NegocioException;

/**
 * Classe utilitária para envio de e-mail.
 */
public final class EmailUtil {

    private static final String TEXT_HTML = "text/html; charset=\"ISO-8859-1\"";

    private static final String PT_BR = "pt-br";

    private static final Logger LOG = LoggerFactory.getLogger(EmailUtil.class.getName());

    private static final String SEPARADORES = ",;";
    private static final int TAMANHO_BUFFER = 2048;
    private static final String ERRO_AO_ENVIAR_E_MAIL = "Erro ao enviar e-mail.";
    private static final String APPLICATION_VND_MS_EXCEL = "application/vnd.ms-excel";
    private static final String APPLICATION_MSPOWERPOINT = "application/mspowerpoint";
    private static final String IMAGE_JPEG = "image/jpeg";
    private static final String APPLICATION_PDF = "application/pdf";
    private static final Map<String, String> MAPA_MIME = new HashMap<String, String>();
    private static final String APPLICATION_MSWORD = "application/msword";

    static {
        MAPA_MIME.put(".doc", APPLICATION_MSWORD);
        MAPA_MIME.put(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        MAPA_MIME.put(".gif", "image/gif");
        MAPA_MIME.put(".jpeg", IMAGE_JPEG);
        MAPA_MIME.put(".jpg", IMAGE_JPEG);
        MAPA_MIME.put(".pdf", APPLICATION_PDF);
        MAPA_MIME.put(".png", "image/png");
        MAPA_MIME.put(".ppt", APPLICATION_MSPOWERPOINT);
        MAPA_MIME.put(".pptx", APPLICATION_MSPOWERPOINT);
        MAPA_MIME.put(".rar", "application/x-rar-compressed");
        MAPA_MIME.put(".rtf", "text/rtf");
        MAPA_MIME.put(".txt", "text/plain");
        MAPA_MIME.put(".xls", APPLICATION_VND_MS_EXCEL);
        MAPA_MIME.put(".xlsx", APPLICATION_VND_MS_EXCEL);
        MAPA_MIME.put(".zip", "application/zip");
    }

    public static String getContentType(IFile arquivo) {
        String separador = ".";
        String[] nomeArquivo = StringUtils.split(arquivo.getNome(), separador);
        String extArquivo = nomeArquivo[nomeArquivo.length - 1];
        return MAPA_MIME.get(separador + extArquivo);
    }

    /**
     * Método responsável por enviar e-mail.
     * 
     * @param eMailDestinatarios array com os e-mails
     * @param assunto título do e-mail enviado
     * @param corpo corpo da mensagem do e-mail
     * @param ambienteProducao
     */
    public static void enviarEmail(String remetente, List<String> eMailDestinatarios, String assunto, String corpo,
            List<IFile> arquivos) {
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", "smtp");
        for (String destinatario : eMailDestinatarios) {
            try {
                Session session = Session.getDefaultInstance(properties);
                MimeMessage message = new MimeMessage(session);
                prepararRemetente(remetente, message);
                prepararDestinatario(destinatario, message);
                prepararAssunto(assunto, destinatario, message);
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                System.out.println("Destinatário" + destinatario);
                System.out.println("Assunto" + assunto);
                System.out.println("Corpo" + corpo);
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                enviarCopiaOculta(message);
                // MENSAGEM EM PARTES
                Multipart multipart = new MimeMultipart();
                prepararTexto(corpo, multipart);

                if (arquivos != null && !arquivos.isEmpty()) {
                    for (IFile arquivo : arquivos) {
                        prepararAnexo(arquivo, multipart);
                    }
                }
                message.setContentLanguage(new String[] {PT_BR});
                message.setContent(multipart);
                // ENVIANDO A MENSAGEM
                Transport.send(message);
                logFinal(message);
                // CHECKSTYLE:OFF
            } catch (MessagingException e) {
                logEnableMessaging(e);
            } catch (Exception e) {
                logEnableException(e);
            }
        }
        // CHECKSTYLE:ON
    }

    private static void logEnableException(Exception e) {
        if (LOG.isInfoEnabled()) {
            LOG.info(ERRO_AO_ENVIAR_E_MAIL + ": " + e + " " + e.getMessage());
        }
    }

    private static void logEnableMessaging(MessagingException e) {
        if (LOG.isInfoEnabled()) {
            LOG.info(ERRO_AO_ENVIAR_E_MAIL + " >>> " + e + " >> " + e.getMessage());
        }
    }

    private static void prepararRemetente(String remetente, MimeMessage message) throws MessagingException,
            UnsupportedEncodingException {
        // DE
        message.setFrom(new InternetAddress(remetente, "Sistema APS-SRC"));
    }

    private static void prepararDestinatario(String destinatario, MimeMessage message) throws MessagingException {
        // PARA 
        StringTokenizer st = new StringTokenizer(destinatario, SEPARADORES);
        while (st.hasMoreTokens()) {
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(st.nextToken().trim()));
        }
    }

    private static void prepararAssunto(String assunto, String destinatario, MimeMessage message)
            throws MessagingException {
         message.setSubject(assunto);
    }
    
    private static void enviarCopiaOculta(MimeMessage message) throws MessagingException {
        // CÓPIA OCULTA SE CONFIGURADO
        Properties propriedades = AmbienteEmail.propriedadesAmbiente();
        String destinos = (String) propriedades.get("emailCopiaOculta");
        if (destinos != null) {
            StringTokenizer st = new StringTokenizer(destinos, SEPARADORES);
            while (st.hasMoreTokens()) {
                message.addRecipient(Message.RecipientType.BCC, new InternetAddress(st.nextToken().trim()));
            }
        }
    }
 

    private static void prepararTexto(String corpo, Multipart multipart) throws MessagingException {
        // PARTE TEXTUAL em HTML
        MimeBodyPart trechoMensagem = new MimeBodyPart();
        String linha = "<br/>";
        trechoMensagem.setContentLanguage(new String[] {PT_BR});
        trechoMensagem.setContent(corpo.replaceAll("\r\n", linha).replaceAll("\n", linha), TEXT_HTML);
        multipart.addBodyPart(trechoMensagem);
    }

    private static void prepararAnexo(IFile arquivo, Multipart multipart) throws IOException, MessagingException {

        // PARTE ANEXADA SE EXISTIR
        if (arquivo != null ) {
            BodyPart trechoAnexo = new MimeBodyPart();
            DataSource source = new ByteArrayDataSource(lerConteudo(arquivo), getContentType(arquivo));
            trechoAnexo.setDataHandler(new DataHandler(source));
            trechoAnexo.setFileName(arquivo.getNome());
            multipart.addBodyPart(trechoAnexo);
        }
    }

    private static byte[] lerConteudo(IFile arquivo) {
        byte[] resultado = null;
        ByteArrayOutputStream bout = null;
        InputStream in = null;
        try {
            bout = new ByteArrayOutputStream();
            in = arquivo.getInputStream();
            readBuffer(bout, in);
            resultado = bout.toByteArray();
        } catch (IOException e) {
            throw new NegocioException(ERRO_AO_ENVIAR_E_MAIL, e);
        } finally {
            inNotNull(in);
            boutNotNull(bout);
        }
        return resultado;
    }

    private static void readBuffer(ByteArrayOutputStream bout, InputStream in) throws IOException {
        byte[] b = new byte[TAMANHO_BUFFER];
        int i = in.read(b);
        while (i > 0) {
            bout.write(b, 0, i);
            i = in.read(b);
        }
    }

    private static void boutNotNull(ByteArrayOutputStream bout) {
        if (bout != null) {
            try {
                bout.close();
            } catch (IOException e) {
                throw new NegocioException(ERRO_AO_ENVIAR_E_MAIL, e);
            }
        }
    }

    private static void inNotNull(InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                throw new NegocioException(ERRO_AO_ENVIAR_E_MAIL, e);
            }
        }
    }

    private static void logFinal(MimeMessage message) throws MessagingException {
        if (LOG.isInfoEnabled()) {
            LOG.info("Email enviado: '" + message.getSubject() + "'");
            obterRecipients(message);
        }
    }

    private static void obterRecipients(MimeMessage message) throws MessagingException {
        Address[] lista = message.getRecipients(Message.RecipientType.CC);
        if (lista != null) {
            gerarLogCC(lista);
        }
        lista = message.getRecipients(Message.RecipientType.BCC);
        if (lista != null) {
            gerarLogBCC(lista);
        }
    }

    private static void gerarLogBCC(Address[] lista) {
        for (Address a : lista) {
            if (a != null) {
                LOG.info("BCC:" + a.toString());
            }
        }
    }

    private static void gerarLogCC(Address[] lista) {
        for (Address a : lista) {
            if (a != null) {
                LOG.info("CC:" + a.toString());
            }
        }
    }
}