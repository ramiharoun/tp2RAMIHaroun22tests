package ma.emsi.RAMIHaroun22;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;



public class Test6 {

    interface Assistant {
        String chat(String userMessage);
    }

    public static void main(String[] args) {

        // Logs comme dans Test4
        Logger.getLogger("ai.djl").setLevel(Level.SEVERE);
        Logger.getLogger("ai.djl.huggingface").setLevel(Level.SEVERE);
        Logger.getLogger("ai.djl.huggingface.tokenizers").setLevel(Level.SEVERE);
        Logger root = Logger.getLogger("");
        root.setLevel(Level.SEVERE);
        for (var h : root.getHandlers()) h.setLevel(Level.SEVERE);

        String llmKey = System.getenv("GEMINI_KEY");
        if (llmKey == null || llmKey.isBlank()) {
            System.err.println("Variable d'environnement GEMINI_KEY manquante.");
            return;
        }

        ChatModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(llmKey)
                .modelName("gemini-2.5-flash")
                .logRequestsAndResponses(true)
                .temperature(0.3) //j'essaie de faire un peu varier entre 0.3 et 0.4 selon le test
                .build();


        Assistant assistant =
                AiServices.builder(Assistant.class)
                        .chatModel(model)
                        .chatMemory(MessageWindowChatMemory.withMaxMessages(10)) // mémoire
                        .tools(new MeteoTool())                                   // outil météo wttr.in
                        .build();

        // Boucle interactive (Scanner)
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n==============================================");
                System.out.print("Pose ta question (ou 'fin' pour quitter) : ");
                String q = scanner.nextLine();
                if (q == null || q.isBlank()) continue;
                if ("fin".equalsIgnoreCase(q.trim())) {
                    System.out.println("Au revoir ");
                    break;
                }
                String reponse = assistant.chat(q);
                System.out.println("Assistant : " + reponse);
            }
        }
    }
}