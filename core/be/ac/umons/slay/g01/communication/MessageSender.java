package ac.umons.slay.g01.communication;

import ac.umons.slay.g01.communication.Messages.Message;

/**
 * Interface utilisé dans les classes OfflineMessageSender et OnlineMessageSender
 * L'interface graphique ne doit donc pas adapter son code si le jeu tourne en mode hors-ligne ou en ligne.
 */
public interface MessageSender {

    /**
     * Méthode qui permet d'envoyer un message vers un destinataire (room ou serveur selon le mode de jeu)
     * @param message le message à envoyer
     */
    void send(Message message);
}
