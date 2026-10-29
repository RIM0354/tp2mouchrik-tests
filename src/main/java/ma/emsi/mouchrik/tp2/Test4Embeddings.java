package ma.emsi.mouchrik.tp2;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
// Depuis le test 5, la dépendance langchain4j-embeddings-bge-small-en-v15 est en commentaire dans pom.xml :
// on utilise la version quantifiée du même modèle, fournie par easy-rag.
// import dev.langchain4j.model.embedding.onnx.bgesmallenv15.BgeSmallEnV15EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.google.genai.GoogleGenAiEmbeddingModel;
import dev.langchain4j.store.embedding.CosineSimilarity;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Test 4 : similarité cosinus entre les embeddings de couples de phrases,
 * calculée avec le modèle d'embeddings intégré à LangChain4j (BgeSmallEnV15, entraîné en anglais)
 * et avec le modèle d'embeddings de Google (gemini-embedding-001).
 */
public class Test4Embeddings {

    /**
     * Couples de phrases à comparer.
     */
    private static final String[][] COUPLES = {
            {"Le chat dort sur le canapé.", "Le félin fait la sieste sur le sofa."},
            {"J'adore le thé à la menthe.", "Je déteste le thé à la menthe."},
            {"Il pleut beaucoup à Paris aujourd'hui.", "La programmation Java utilise des classes."},
            {"Quelle est la capitale du Maroc ?", "Rabat est la capitale du Maroc."},
            {"Le médecin soigne le patient.", "Le docteur traite le malade."},
            {"La banque a fermé ses portes.", "Je me suis assis sur un banc."},
            {"The cat sleeps on the couch.", "The feline is napping on the sofa."}
    };

    public static void main(String[] args) {
        // Modèle d'embeddings intégré à LangChain4j : ne consomme pas de tokens.
        // EmbeddingModel modeleIntegre = new BgeSmallEnV15EmbeddingModel();
        EmbeddingModel modeleIntegre = new BgeSmallEnV15QuantizedEmbeddingModel();

        String cle = System.getenv("GEMINI_KEY");
        if (cle == null) {
            throw new IllegalStateException("La variable d'environnement GEMINI_KEY n'a pas de valeur");
        }
        // Modèle d'embeddings externe de Google, meilleur pour le français.
        EmbeddingModel modeleGoogle = GoogleGenAiEmbeddingModel.builder()
                .apiKey(cle)
                .modelName("gemini-embedding-001")
                .taskType(GoogleGenAiEmbeddingModel.TaskTypeEnum.SEMANTIC_SIMILARITY)
                // 100 ms ne suffit pas (InterruptedIOException: timeout)
                .timeout(Duration.ofSeconds(10))
                .build();

        List<Double> similaritesIntegre = similarites(modeleIntegre);
        List<Double> similaritesGoogle = similarites(modeleGoogle);

        for (int i = 0; i < COUPLES.length; i++) {
            System.out.println("Phrase 1 : " + COUPLES[i][0]);
            System.out.println("Phrase 2 : " + COUPLES[i][1]);
            System.out.printf("Similarité BgeSmallEnV15 : %.4f%n", similaritesIntegre.get(i));
            System.out.printf("Similarité gemini-embedding-001 : %.4f%n", similaritesGoogle.get(i));
            System.out.println();
        }
    }

    /**
     * Calcule la similarité cosinus pour chaque couple de phrases.
     * Toutes les phrases sont envoyées en une seule fois au modèle (embedAll)
     * pour limiter le nombre de requêtes.
     *
     * @param modele le modèle d'embeddings.
     * @return la similarité de chaque couple, dans l'ordre des couples.
     */
    private static List<Double> similarites(EmbeddingModel modele) {
        List<TextSegment> segments = new ArrayList<>();
        for (String[] couple : COUPLES) {
            segments.add(TextSegment.from(couple[0]));
            segments.add(TextSegment.from(couple[1]));
        }
        List<Embedding> embeddings = modele.embedAll(segments).content();
        List<Double> resultat = new ArrayList<>();
        for (int i = 0; i < COUPLES.length; i++) {
            resultat.add(CosineSimilarity.between(embeddings.get(2 * i), embeddings.get(2 * i + 1)));
        }
        return resultat;
    }
}
