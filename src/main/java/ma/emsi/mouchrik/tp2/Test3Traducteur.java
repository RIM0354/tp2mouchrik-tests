package ma.emsi.mouchrik.tp2;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;

import java.util.Map;

/**
 * Test 3 : un traducteur, avec PromptTemplate et Prompt.
 */
public class Test3Traducteur {

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

        // Template avec 1 paramètre : le texte à traduire
        PromptTemplate template1 = PromptTemplate.from("Traduis le texte suivant en anglais : {{texte}}");
        Prompt prompt1 = template1.apply(Map.of("texte", "Le thé à la menthe est servi sur la terrasse."));
        System.out.println("Prompt : " + prompt1.text());
        System.out.println("Réponse : " + modele.chat(prompt1.text()));
        System.out.println();

        // Template avec 2 paramètres : la langue de traduction et le texte à traduire
        PromptTemplate template2 = PromptTemplate.from("Traduis le texte suivant en {{langue}} : {{texte}}");
        Prompt prompt2 = template2.apply(Map.of(
                "langue", "espagnol",
                "texte", "Le thé à la menthe est servi sur la terrasse."));
        System.out.println("Prompt : " + prompt2.text());
        System.out.println("Réponse : " + modele.chat(prompt2.text()));
    }
}
