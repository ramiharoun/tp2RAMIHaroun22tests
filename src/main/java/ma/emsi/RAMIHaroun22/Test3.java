package ma.emsi.RAMIHaroun22;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiEmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiEmbeddingModel.TaskType;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.CosineSimilarity;

import java.time.Duration;

public class Test3 {

    public static void main(String[] args) {
        String cle = System.getenv("GEMINI_KEY");

        // Création du modèle d'embeddings Gemini
        EmbeddingModel modele = GoogleAiEmbeddingModel.builder()
                .apiKey(cle)
                .modelName("text-embedding-004")
                .taskType(TaskType.SEMANTIC_SIMILARITY)
                .outputDimensionality(300)
                .timeout(Duration.ofSeconds(30))
                .build();

        // Trois phrases pour comparer
        String phraseA = "Les japonais aiment respecter les traditions.";
        String phraseB = "Les américains ne sont pas trop attachés aux traditions.";
        String phraseC = "Les pays de l'hémisphère nord sont chaleureux.";

        // Génération des embeddings
        Response<Embedding> eA = modele.embed(phraseA);
        Response<Embedding> eB = modele.embed(phraseB);
        Response<Embedding> eC = modele.embed(phraseC);

        Embedding embA = eA.content();
        Embedding embB = eB.content();
        Embedding embC = eC.content();

        // Calcul des similarités
        double simAB = CosineSimilarity.between(embA, embB);
        double simAC = CosineSimilarity.between(embA, embC);
        double simBC = CosineSimilarity.between(embB, embC);

        System.out.println("Similarité(A, B) ≈ " + simAB);
        System.out.println("Similarité(A, C) ≈ " + simAC);
        System.out.println("Similarité(B, C) ≈ " + simBC);
    }
}