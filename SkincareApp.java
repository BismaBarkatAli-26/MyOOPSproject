import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.*;

public class SkincareApp extends Application {

    private final Map<String, List<String>> recommendations = new HashMap<>();
    private final Map<String, List<QuestionOption>> concerns = new LinkedHashMap<>();
    private final List<ComboBox<String>> answerBoxes = new ArrayList<>();
    private VBox questionPane;
    private Stage primaryStage;
    private boolean isNightMode = false;
    private Scene currentScene;
    private ToggleButton themeToggle;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeData();
        showWelcomeScreen();
    }

    private void initializeData() {
        recommendations.put("Acne", Arrays.asList("Salicylic Acid Cleanser", "Benzoyl Peroxide Gel", "Niacinamide Serum"));
        recommendations.put("Dryness", Arrays.asList("Hydrating Moisturizer", "Hyaluronic Acid Serum", "Cream Cleanser"));
        recommendations.put("Sensitivity", Arrays.asList("Fragrance-Free Cleanser", "Ceramide Moisturizer", "Oat Extract Cream"));
        recommendations.put("Dark Spots", Arrays.asList("Vitamin C Serum", "Niacinamide Cream", "Retinol Treatment"));
        recommendations.put("Oily Skin", Arrays.asList("Clay Mask", "Oil-Free Moisturizer", "AHA Toner"));
        recommendations.put("Combination Skin", Arrays.asList("Balanced pH Cleanser", "Dual Action Moisturizer", "Lightweight Gel Cream"));
        recommendations.put("Dullness", Arrays.asList("Exfoliating Scrub", "Glycolic Acid Toner", "Brightening Mask"));

        concerns.put("Acne", getQuestions("Acne"));
        concerns.put("Dryness", getQuestions("Dryness"));
        concerns.put("Sensitivity", getQuestions("Sensitivity"));
        concerns.put("Dark Spots", getQuestions("Dark Spots"));
        concerns.put("Oily Skin", getQuestions("Oily Skin"));
        concerns.put("Combination Skin", getQuestions("Combination Skin"));
        concerns.put("Dullness", getQuestions("Dullness"));
    }

    private void showWelcomeScreen() {
        VBox welcomePane = new VBox(15);
        welcomePane.setAlignment(Pos.CENTER);
        welcomePane.setPadding(new Insets(50));
        welcomePane.getStyleClass().add("welcome-pane");

        Label title = new Label("Your Skincare Journey Starts Here");
        title.getStyleClass().add("welcome-title");

        Label description = new Label("Let’s help you find the perfect products tailored for your skin.");
        description.getStyleClass().add("welcome-description");

        Button startButton = new Button("Start Skin Check");
        startButton.getStyleClass().add("start-button");
        startButton.setOnAction(e -> showQuestionnaire());

        welcomePane.getChildren().addAll(title, description, startButton);

        BorderPane root = new BorderPane();
        root.setTop(createHeader());
        root.setCenter(welcomePane);
        root.getStyleClass().add("root");

        currentScene = new Scene(root, 750, 550);
        applyStylesheet();
        primaryStage.setScene(currentScene);
        primaryStage.setTitle("Skincare Recommendation App");
        primaryStage.show();
    }

    private void showQuestionnaire() {
        questionPane = new VBox(20);
        questionPane.setPadding(new Insets(20));
        Label instruction = new Label("Choose the best answers that match your skin condition:");
        instruction.getStyleClass().add("instruction");

        questionPane.getChildren().add(instruction);
        answerBoxes.clear();

        for (Map.Entry<String, List<QuestionOption>> entry : concerns.entrySet()) {
            Label concernLabel = new Label(entry.getKey());
            concernLabel.getStyleClass().add("concern-label");
            questionPane.getChildren().add(concernLabel);

            for (QuestionOption q : entry.getValue()) {
                Label questionLabel = new Label(q.question());
                ComboBox<String> options = new ComboBox<>();
                options.getItems().addAll(q.options());
                options.setPromptText("Select one");
                options.setMaxWidth(400);
                answerBoxes.add(options);

                questionPane.getChildren().addAll(questionLabel, options);
            }
        }

        Button submit = new Button("Get Recommendations");
        submit.getStyleClass().add("submit-button");
        submit.setOnAction(e -> showRecommendations());
        questionPane.getChildren().add(submit);

        ScrollPane scroll = new ScrollPane(questionPane);
        scroll.setFitToWidth(true);

        BorderPane root = new BorderPane();
        root.setTop(createHeader());
        root.setCenter(scroll);
        root.getStyleClass().add("root");

        currentScene = new Scene(root, 750, 550);
        applyStylesheet();
        primaryStage.setScene(currentScene);
    }

    private void showRecommendations() {
        VBox resultPane = new VBox(20);
        resultPane.setPadding(new Insets(30));

        Label heading = new Label("Your Skincare Recommendations");
        heading.getStyleClass().add("result-heading");

        Set<String> triggeredConcerns = new HashSet<>();
        int index = 0;

        for (Map.Entry<String, List<QuestionOption>> entry : concerns.entrySet()) {
            boolean concernTriggered = false;
            for (int i = 0; i < entry.getValue().size(); i++) {
                ComboBox<String> answerBox = answerBoxes.get(index++);
                String response = answerBox.getValue();
                if (response != null && (response.contains("Sometimes") || response.contains("Often") || response.contains("Yes") || response.contains("Significant"))) {
                    concernTriggered = true;
                }
            }
            if (concernTriggered) {
                triggeredConcerns.add(entry.getKey());
            }
        }

        resultPane.getChildren().add(heading);

        if (triggeredConcerns.isEmpty()) {
            resultPane.getChildren().add(new Label("No concerns selected. Please try again!"));
        } else {
            for (String concern : triggeredConcerns) {
                VBox card = new VBox(8);
                card.getStyleClass().add("recommendation-card");

                Label concernTitle = new Label(concern);
                concernTitle.getStyleClass().add("card-title");

                VBox productList = new VBox();
                for (String product : recommendations.get(concern)) {
                    Label productLabel = new Label("• " + product);
                    productList.getChildren().add(productLabel);
                }

                card.getChildren().addAll(concernTitle, productList);
                resultPane.getChildren().add(card);
            }
        }

        Button restart = new Button("Start Over");
        restart.setOnAction(e -> showWelcomeScreen());
        resultPane.getChildren().add(restart);

        ScrollPane scroll = new ScrollPane(resultPane);
        scroll.setFitToWidth(true);

        BorderPane root = new BorderPane();
        root.setTop(createHeader());
        root.setCenter(scroll);
        root.getStyleClass().add("root");

        currentScene = new Scene(root, 750, 550);
        applyStylesheet();
        primaryStage.setScene(currentScene);
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(10));
        header.setAlignment(Pos.CENTER_RIGHT);
        header.setSpacing(10);
        header.setStyle("-fx-background-color: transparent;");

        themeToggle = new ToggleButton("🌙 Night Mode");
        themeToggle.setOnAction(e -> {
            isNightMode = !isNightMode;
            themeToggle.setText(isNightMode ? "☀ Day Mode" : "🌙 Night Mode");
            applyStylesheet();
        });

        header.getChildren().add(themeToggle);
        return header;
    }

    private void applyStylesheet() {
        if (currentScene != null) {
            currentScene.getStylesheets().clear();
            String themeFile = isNightMode ? "style-night.css" : "style-day.css";
            var url = getClass().getResource(themeFile);
            System.out.println("Loading stylesheet: " + url);
            currentScene.getStylesheets().add(url.toExternalForm());
        }
    }

    private List<QuestionOption> getQuestions(String concern) {
        return switch (concern) {
            case "Acne" -> Arrays.asList(
                    new QuestionOption("How often do you experience pimples or breakouts?", Arrays.asList("Rarely", "Sometimes", "Often")),
                    new QuestionOption("Do you notice blackheads or whiteheads?", Arrays.asList("No", "Occasionally", "Frequently")));
            case "Dryness" -> Arrays.asList(
                    new QuestionOption("Does your skin feel tight after washing?", Arrays.asList("Never", "Sometimes", "Always")),
                    new QuestionOption("Do you notice flaking or rough patches?", Arrays.asList("No", "A few areas", "Yes, many areas")));
            case "Sensitivity" -> Arrays.asList(
                    new QuestionOption("Does your skin react easily to new products?", Arrays.asList("No", "Sometimes", "Yes")),
                    new QuestionOption("Do you feel burning or itching after applying skincare?", Arrays.asList("Never", "Sometimes", "Often")));
            case "Dark Spots" -> Arrays.asList(
                    new QuestionOption("Do you have post-acne marks or pigmentation?", Arrays.asList("None", "Mild", "Significant")),
                    new QuestionOption("Do you use sunscreen regularly?", Arrays.asList("Always", "Sometimes", "Never")));
            case "Oily Skin" -> Arrays.asList(
                    new QuestionOption("Is your skin shiny by mid-day?", Arrays.asList("No", "Some areas", "Yes, very oily")),
                    new QuestionOption("Do you feel the need to blot or wash often?", Arrays.asList("Rarely", "Sometimes", "Frequently")));
            case "Combination Skin" -> Arrays.asList(
                    new QuestionOption("Do you have both dry and oily areas?", Arrays.asList("No", "Mildly", "Yes, clearly defined")),
                    new QuestionOption("Do you struggle to balance your skincare routine?", Arrays.asList("No", "Sometimes", "Yes")));
            case "Dullness" -> Arrays.asList(
                    new QuestionOption("Does your skin lack a healthy glow?", Arrays.asList("No", "Sometimes", "Yes")),
                    new QuestionOption("Do you exfoliate regularly?", Arrays.asList("Yes", "Occasionally", "Never")));
            default -> new ArrayList<>();
        };
    }

    private record QuestionOption(String question, List<String> options) {}
}