import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;

import javax.swing.*;


public class FrontEnd extends Application{
	BorderPane mainPane = new BorderPane();
	Stage primaryStage = new Stage();

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		buildTopPane();

		// TODO Build add/edit context menu

		Scene scene = new Scene(mainPane, 500, 500);
		primaryStage.setTitle("RockBuddy");
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}

	public void buildTopPane() {
		Label title = new Label("Rock Buddy");
		Label subTitle = new Label("Your rock collection companion app.\nAdd, edit, and view samples in your collection!");
		MenuBar menuBar = new MenuBar();
		Menu menu = new Menu("Menu");
		MenuItem add = new MenuItem("Add Sample");
		MenuItem edit = new MenuItem("Edit Sample");
		MenuItem view = new MenuItem("View Collection");
		MenuItem exit = new MenuItem("Exit");
		VBox topPane = new VBox();

		topPane.getChildren().addAll(menuBar, title, subTitle);
		topPane.setAlignment(Pos.CENTER);
		title.setFont(new Font(30));
		subTitle.setFont(new Font(18));
		subTitle.setTextAlignment(TextAlignment.CENTER);
		menuBar.getMenus().add(menu);
		menu.getItems().addAll(view, add, edit, exit);

		mainPane.setTop(topPane);

		//Set action
		add.setOnAction(event -> addSample(primaryStage));
		edit.setOnAction(event -> editSample());
		view.setOnAction(event -> viewSamples());
		exit.setOnAction(event -> System.exit(0));
	}

	public BorderPane buildAddSamplePane() {
		Insets padding = new Insets(5);
		BorderPane borderPane = new BorderPane();
		GridPane gridPane = new GridPane();
		Label addSample = new Label("Add New Find:");
		addSample.setFont(Font.font(15));

		// Labels
		Label locationFoundLb = new Label("Found:");
		Label igneousLb = new Label("Igneous");
		Label sedLb = new Label("Sedimentary");
		Label metaLb = new Label("Metamorphic");
		Label uknownLb = new Label("Unknown");
		Label idLb = new Label("Id:");
		Label nameLb = new Label("Name:");
		Label colorLb = new Label("Color:");
		Label compositionLb = new Label("Composition:");
		Label textureLb = new Label("Texture:");
		Label structuresLb = new Label("Structures");
		Label roundingLb = new Label("Rounding");
		Label lusterLb = new Label("Luster:");
		Label grainSizeLb = new Label("Grain Size:");
		Label cleavageLb = new Label("Cleavage:");
		Label mineralSizeLb = new Label("Mineral Size:");
		Label fossilDescriptionLb = new Label("Fossil descriptions:");
		Label sizeLb = new Label("Size:");
		Label otherFeaturesLb = new Label("Other Cool Features:");

		// Inputs
		RadioButton igneousRb = new RadioButton("Igneous");
		RadioButton sedRb = new RadioButton("Sedimentary");
		RadioButton metaRb = new RadioButton("Metamorphic");
		RadioButton unknownRb = new RadioButton("Unknown");
		ToggleGroup typeGroup = new ToggleGroup();
		igneousRb.setToggleGroup(typeGroup);
		sedRb.setToggleGroup(typeGroup);
		metaRb.setToggleGroup(typeGroup);
		unknownRb.setToggleGroup(typeGroup);

		CheckBox fossilContent = new CheckBox("Fossil Content?");

		TextField locationFoundTF = new TextField();
		TextField idTf = new TextField();
		TextField nameTf = new TextField();
		TextField colorTf = new TextField();
		TextField compositionTf = new TextField();
		TextField textureTf = new TextField();
		TextField structuresTf = new TextField();
		TextField roundingTf = new TextField();
		TextField lusterTf = new TextField();
		TextField grainSizeTf = new TextField();
		TextField cleavageTf = new TextField();
		TextField mineralSizeTf = new TextField();
		TextField fossilDescriptionTf = new TextField();
		TextField sizeTf = new TextField();

		TextArea otherFeatures = new TextArea();

		HBox rockTypesPane = new HBox();
		VBox centerPane = new VBox();
		centerPane.getChildren().addAll(rockTypesPane, gridPane);
		gridPane.addColumn(0, locationFoundLb, idLb, nameLb, colorLb, compositionLb,
				textureLb, structuresLb, roundingLb, lusterLb, grainSizeLb, cleavageLb,  mineralSizeLb,
				fossilDescriptionLb, sizeLb, otherFeaturesLb);
		gridPane.addColumn(1, locationFoundTF, idTf, nameTf, colorTf, compositionTf,
				textureTf, structuresTf, roundingTf, lusterTf, grainSizeTf, cleavageTf, mineralSizeTf,
				fossilDescriptionTf, sizeTf, otherFeatures);

		rockTypesPane.getChildren().addAll(igneousRb, metaRb, sedRb, unknownRb);





		borderPane.setPadding(padding);
		borderPane.setTop(addSample);
		borderPane.setCenter(centerPane);
		return borderPane;
	}

	// TODO Menu action methods
	public void addSample(Stage primaryStage) {
		BorderPane borderPane = buildAddSamplePane();
		Scene addScene = new Scene(borderPane, 300, 300);
		Stage stage = new Stage();

		stage.setScene(addScene);
		stage.setTitle("New Sample");
		stage.show();
	}

	public void editSample() {

	}

	public void viewSamples() {

	}



}
