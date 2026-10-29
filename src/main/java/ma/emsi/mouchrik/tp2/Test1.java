package ma.emsi.mouchrik.tp2;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;

/**
 * Test 1 : pose une question simple au LLM (Gemini) et affiche sa réponse.
 * Les questions peuvent être passées en arguments du programme ; chaque argument
 * est envoyé dans une requête séparée.
 */
public class Test1 {

    public static void main(String[] args) {
        // Clé secrète de l'API de Gemini, rangée dans une variable d'environnement
        String cle = System.getenv("GEMINI_KEY");
        if (cle == null) {
            throw new IllegalStateException("La variable d'environnement GEMINI_KEY n'a pas de valeur");
        }

        // Création du modèle avec le pattern "builder".
        // gemini-2.5-flash n'est plus disponible pour les nouveaux utilisateurs (erreur 404),
        // d'où le choix de gemini-3.5-flash-lite, le modèle le moins coûteux.
        ChatModel modele = GoogleGenAiChatModel.builder()
                .apiKey(cle)
                .modelName("gemini-3.5-flash-lite")
                .temperature(0.7)
                .build();

        String[] questions = args.length > 0 ? args : new String[] {"Quelle est la capitale du Maroc ?"};
        for (String question : questions) {
            String reponse = modele.chat(question);
            System.out.println("Question : " + question);
            System.out.println("Réponse : " + reponse);
            System.out.println();
        }
    }
}
