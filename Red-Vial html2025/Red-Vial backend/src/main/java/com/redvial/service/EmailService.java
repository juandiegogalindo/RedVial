package com.redvial.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    // correo remitente (el mismo de spring.mail.username)
    private final String from;

    public EmailService(JavaMailSender mailSender,
                        @Value("${spring.mail.username}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    /**
     * SOLO se encarga de enviar el correo de VERIFICACIÓN de registro.
     */
    public void enviarCorreoRegistro(String destino, String token) {
        try {
            System.out.println("ENVIANDO CORREO DE VERIFICACION A: " + destino);

            String urlVerificacion =
                    "http://localhost:8080/api/auth/confirmar?token=" + token;

            String html = """
                    <h2>Bienvenido a Red Vial</h2>
                    <p>Gracias por registrarte en <b>Red Vial</b>.</p>
                    <p>Para activar tu cuenta, haz clic en el siguiente botón:</p>
                    <p style="text-align:center; margin:20px 0;">
                      <a href="%s"
                         style="background-color:#007bff;color:white;
                                padding:10px 20px;
                                text-decoration:none;
                                border-radius:4px;
                                font-weight:bold;">
                        Verificar cuenta
                      </a>
                    </p>
                    <p>Si el botón no funciona, copia y pega este enlace en tu navegador:</p>
                    <p>%s</p>
                    """.formatted(urlVerificacion, urlVerificacion);

            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, "utf-8");
            helper.setTo(destino);
            helper.setFrom(from);
            helper.setSubject("Verifica tu cuenta en Red Vial");
            helper.setText(html, true); // cuerpo HTML

            mailSender.send(mime);

            System.out.println("CORREO DE VERIFICACION ENVIADO CORRECTAMENTE.");

        } catch (Exception e) {
            System.out.println("ERROR ENVIANDO CORREO DE VERIFICACION: " + e.getMessage());
            e.printStackTrace();
        }
    }
}