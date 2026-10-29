package ma.emsi.mouchrik.tp2;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.model.output.TokenUsage;

/**
 * Test 2 : même question que le test 1, avec affichage du nombre de tokens
 * et du coût de la requête en dollars.
 */
public class Test2Couts {

    /**
     * Prix en dollars pour 1 million de tokens en entrée, pour gemini-3.5-flash-lite.
     */
    private static final double PRIX_MILLION_TOKENS_ENTREE = 0.30;
    /**
     * Prix en dollars pour 1 million de tokens en sortie, pour gemini-3.5-flash-lite.
     */
    private static final double PRIX_MILLION_TOKENS_SORTIE = 2.50;

    public static void main(String[] args) {
        String cle = System.getenv("GEMINI_KEY");
        if (cle == null) {
            throw new IllegalStateException("La variable d'environnement GEMINI_KEY n'a pas de valeur");
        }

        ChatModel modele = GoogleGenAiChatModel.builder()
                .apiKey(cle)
                .modelName("gemini-3.5-flash-lite")
                .temperature(0.7)
                .build();

        String question = "Quelle est la capitale du Maroc ?";
        // chat(ChatMessage...) retourne une ChatResponse qui contient aussi les nombres de tokens
        ChatResponse chatResponse = modele.chat(UserMessage.from(question));
        System.out.println("Question : " + question);
        System.out.println("Réponse : " + chatResponse.aiMessage().text());

        TokenUsage tokenUsage = chatResponse.tokenUsage();
        int tokensEntree = tokenUsage.inputTokenCount();
        int tokensSortie = tokenUsage.outputTokenCount();
        System.out.println("Tokens en entrée : " + tokensEntree);
        System.out.println("Tokens en sortie : " + tokensSortie);
        System.out.println("Total des tokens : " + tokenUsage.totalTokenCount());

        double cout = tokensEntree * PRIX_MILLION_TOKENS_ENTREE / 1_000_000
                + tokensSortie * PRIX_MILLION_TOKENS_SORTIE / 1_000_000;
        System.out.printf("Coût de la requête : %.8f $%n", cout);
        System.out.printf("Nombre de requêtes similaires pour dépenser 1 $ : %.0f%n", 1 / cout);
    }
}
