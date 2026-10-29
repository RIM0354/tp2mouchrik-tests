package ma.emsi.mouchrik.tp2;

/**
 * Assistant IA qui peut utiliser l'outil météo. L'implémentation est fournie par AiServices de LangChain4j.
 */
public interface AssistantMeteo {

    // Prend un message de l'utilisateur et retourne une réponse du LLM.
    String chat(String userMessage);
}
