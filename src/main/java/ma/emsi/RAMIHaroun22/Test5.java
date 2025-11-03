package ma.emsi.RAMIHaroun22;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * RAG sur un PDF (ml.pdf) + session interactive.
 */
public class Test5 {

    // Contrat minimal pour l'assistant
    interface Bot {
        String chat(String message);
    }

    private static final String FICHIER_SOURCE = "ml.pdf";

    public static void main(String[] args) {
        couperLesLogsVerbeux();

        ChatModel llm = construireModele();
        EmbeddingStore<TextSegment> vecteurs = construireIndex(FICHIER_SOURCE);

        Bot assistant = AiServices.builder(Bot.class)
                .chatModel(llm)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(EmbeddingStoreContentRetriever.from(vecteurs))
                .build();

        // Mode interactif – tu peux poser plusieurs questions
        boucleConversation(assistant);
    }

    // --------- Helpers ---------

    private static ChatModel construireModele() {
        String apiKey = System.getenv("GEMINI_KEY");
        return GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-2.5-flash")
                .temperature(0.4)
                .build();
    }

    private static EmbeddingStore<TextSegment> construireIndex(String cheminFichier) {
        // Charge le PDF
        Document doc = FileSystemDocumentLoader.loadDocument(cheminFichier);

        // Base vectorielle en mémoire
        EmbeddingStore<TextSegment> store = new InMemoryEmbeddingStore<>();

        // Ingestion « simple » : découpe + embeddings + stockage
        EmbeddingStoreIngestor.ingest(doc, store);
        return store;
    }

    private static void boucleConversation(Bot assistant) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n==================================================");
                System.out.print("Votre question (ou 'fin' pour quitter) : ");
                String q = scanner.nextLine();
                if (q == null || q.isBlank()) continue;
                if ("fin".equalsIgnoreCase(q.trim())) {
                    System.out.println("Fin ");
                    break;
                }
                String reponse = assistant.chat(q);
                System.out.println("--------------------------------------------------");
                System.out.println("Assistant : " + reponse);
            }
        }
    }

    private static void couperLesLogsVerbeux() {
        Logger.getLogger("ai.djl").setLevel(Level.SEVERE);
        Logger.getLogger("ai.djl.huggingface").setLevel(Level.SEVERE);
        Logger.getLogger("ai.djl.huggingface.tokenizers").setLevel(Level.SEVERE);
        Logger root = Logger.getLogger("");
        root.setLevel(Level.SEVERE);
        for (var h : root.getHandlers()) h.setLevel(Level.SEVERE);
    }
}