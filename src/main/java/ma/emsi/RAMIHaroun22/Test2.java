package ma.emsi.RAMIHaroun22;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;

import java.util.Map;

public class Test2 {

    public static void main(String[] args) {
        // Lecture de la clé d'API depuis les variables d'environnement
        String apiKey = System.getenv("GEMINI_KEY");

        // Construction du modèle Gemini avec le pattern builder
        ChatModel chatModel = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-2.5-flash")
                .temperature(0.7)
                .build();

        // Création du template de prompt pour la traduction
        PromptTemplate promptTemplate =
                PromptTemplate.from("Peux-tu traduire ce texte en japonais : {{texte}}");

        // Application du template avec le texte à traduire
        Prompt prompt = promptTemplate.apply(
                Map.of("texte", "La capitale du Japon est okinawa.")
        );

        // Préparation de la requête à envoyer au modèle
        ChatRequest request = ChatRequest.builder()
                .messages(prompt.toUserMessage())
                .temperature(0.7)
                .build();

        // Envoi de la requête et affichage de la réponse
        ChatResponse response = chatModel.chat(request);
        System.out.println("Traduction : " + response.aiMessage().text());
    }
}