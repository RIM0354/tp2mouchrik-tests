package ma.emsi.mouchrik.tp2;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

/**
 * Le RAG facile !
 */
public class Test5Rag1 {

  // Assistant conversationnel
  interface Assistant {
    // Prend un message de l'utilisateur et retourne une réponse du LLM.
    String chat(String userMessage);
  }

  public static void main(String[] args) {
    String llmKey = System.getenv("GEMINI_KEY");
    if (llmKey == null) {
      System.out.println("La variable d'environnement GEMINI_KEY n'est pas définie.");
      return;
    }
    // Mettre une température qui ne dépasse pas 0,3.
    // Le RAG sert à mieux contrôler l'exactitude des informations données par le LLM
    // et il est donc logique de diminuer la température.
    ChatModel model = GoogleGenAiChatModel.builder()
            .apiKey(llmKey)
            .modelName("gemini-3.5-flash-lite")
            .temperature(0.3)
            .build();

    // Chargement du document, sous la forme d'embeddings, dans une base vectorielle en mémoire
    String nomDocument = "infos.txt";
    Document document = FileSystemDocumentLoader.loadDocument(nomDocument);
    EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore();
    // Phase 1 du RAG.
    // Si le document a une grande taille , il est découpé en segments.
    // Les embeddings de ces segments sont calculés (avec un modèle d'embeddings par défaut)
    // Les segments et leurs embeddings seront enregistrés dans la base vectorielle.
    // Base de données vectorielle
    EmbeddingStoreIngestor.ingest(document, embeddingStore);

    // Création de l'assistant conversationnel, avec une mémoire.
    // L'implémentation de Assistant est faite par LangChain4j.
    // L'assistant gardera en mémoire les 10 derniers messages.
    // Indique à l'assistant qu'il utilisera les segments enrigistrés dans la base vectorielle pour répondre aux questions.
    Assistant assistant =
        AiServices.builder(Assistant.class)
            .chatModel(model)
            .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
            .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
            .build();

    // Phase 2 : répondre à une question en utilisant les segments les plus pertinents pour y répondre.
    String question = "Comment s'appelle le chat de Pierre ?";
    // L'assistant recherche dans la base vectorielle les segments les plus pertinents
    // pour répondre à la question, en comparant les embeddings de la base et celui de la question.
    // Ces informations sont ajoutées à la question et le tout est envoyé au LLM qui a maintenant
    // les bonnes informations (contenues dans le fichier lu dans la phase 1) pour répondre.
    String reponse = assistant.chat(question);
    // Affiche la réponse du LLM.
    System.out.println(reponse);
  }
}
