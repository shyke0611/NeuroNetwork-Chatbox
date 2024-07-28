package org.vaadin.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class ChatbotModel {
    private MultiLayerNetwork model;
    private List<Intent> intents;

    public ChatbotModel() {
        try {
            loadIntents();
            buildModel();
        } catch (IOException e) {
            // Log the exception and handle it appropriately
            System.err.println("Error initializing ChatbotModel: " + e.getMessage());
            // Optionally, rethrow as a runtime exception if you want to fail fast
            throw new RuntimeException(e);
        }
    }

    private void loadIntents() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream intentsStream = getClass().getClassLoader().getResourceAsStream("intents.json");

        if (intentsStream == null) {
            throw new IOException("intents.json file not found in the classpath.");
        }

        System.out.println("Loading intents from classpath.");

        IntentsWrapper intentsWrapper = mapper.readValue(intentsStream, IntentsWrapper.class);
        this.intents = intentsWrapper.getIntents();

        if (this.intents == null || this.intents.isEmpty()) {
            throw new IOException("No intents found in the intents.json file.");
        }
    }

    private void buildModel() {
        int inputSize = 100;
        int outputSize = intents.size();

        NeuralNetConfiguration.ListBuilder builder = new NeuralNetConfiguration.Builder()
                .seed(123)
                .updater(new Adam(0.001))
                .list();

        builder.layer(new LSTM.Builder().nIn(inputSize).nOut(256).activation(Activation.TANH).build());
        builder.layer(new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
                .activation(Activation.SOFTMAX)
                .nIn(256).nOut(outputSize).build());

        model = new MultiLayerNetwork(builder.build());
        model.init();
        model.setListeners(new ScoreIterationListener(10));
    }

    public void train(DataSet trainingData) {
        model.fit(trainingData);
    }

    public String predict(String userInput) {
        INDArray input = preprocessInput(userInput);
        INDArray output = model.output(input);
        int predictedClass = Nd4j.argMax(output, 1).getInt(0);
        return fetchResponse(predictedClass);
    }

    private INDArray preprocessInput(String userInput) {
        float[] vectorizedInput = new float[100];
        for (int i = 0; i < vectorizedInput.length; i++) {
            vectorizedInput[i] = 0.0f;
        }
        INDArray input2D = Nd4j.create(vectorizedInput, new int[]{1, 100});
        INDArray input3D = input2D.reshape(1, 100, 1);
        return input3D;
    }

    private String fetchResponse(int predictedClass) {
        String tag = intents.get(predictedClass).getTag();
        for (Intent intent : intents) {
            if (intent.getTag().equals(tag)) {
                List<String> responses = intent.getResponses();
                return responses.get((int) (Math.random() * responses.size()));
            }
        }
        return "I didn't understand that.";
    }
}
