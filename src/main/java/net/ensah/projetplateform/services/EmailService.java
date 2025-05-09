package net.ensah.projetplateform.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordEmail(String to, String login, String password) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("Vos identifiants de connexion à la plateforme");

        String emailContent = """
            <html>
            <body>
                <h2>Bienvenue sur la plateforme d'annotation</h2>
                <p>Voici vos identifiants de connexion :</p>
                <p><strong>Login :</strong> %s</p>
                <p><strong>Mot de passe :</strong> %s</p>
                <p>Veuillez conserver ces informations en lieu sûr.</p>
                <p>Cordialement,<br>L'équipe de la plateforme</p>
            </body>
            </html>
            """.formatted(login, password);

        helper.setText(emailContent, true);
        mailSender.send(message);
    }
}