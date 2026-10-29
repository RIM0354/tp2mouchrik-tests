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

import java.util.Scanner;

/**
 * Le RAG facile, avec le PDF du support de cours sur le machine learning et une conversation.
 */
public class Test6Rag2 {

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
    String nomDocument = "ml.pdf";
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

    // Phase 2 : répondre aux questions de l'utilisateur en utilisant les segments les plus pertinents.
    conversationAvec(assistant);
  }

  /**
   * Conversation avec l'assistant : l'utilisateur pose plusieurs questions dans la même session.
   * La conversation s'arrête quand l'utilisateur tape "fin".
   */
  private static void conversationAvec(Assistant assistant) {
    try (Scanner scanner = new Scanner(System.in)) {
      while (true) {
        System.out.println("==================================================");
        System.out.println("Posez votre question : ");
        String question = scanner.nextLine();
        if (question.isBlank()) {
          continue;
        }
        System.out.println("==================================================");
        if ("fin".equalsIgnoreCase(question)) {
          break;
        }
        String reponse = assistant.chat(question);
        System.out.println("Assistant : " + reponse);
        System.out.println("==================================================");
      }
    }
  }
}
