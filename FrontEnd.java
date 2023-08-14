import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class FrontEnd extends Application{
	BorderPane mainPane = new BorderPane();
	int addPaneSize = 800;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
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
		add.setOnAction(event -> {
			try {
				addSample();
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		});
		edit.setOnAction(event -> editSample());
		view.setOnAction(event -> viewSamples());
		exit.setOnAction(event -> System.exit(0));
	}

	public BorderPane buildAddSamplePane() throws FileNotFoundException {
		int vGap = 10;
		int hGap = 5;
		Insets padding = new Insets(5);
		BorderPane borderPane = new BorderPane();
		GridPane gridPane = new GridPane();
		Label addSample = new Label("Add New Find:");
		addSample.setFont(Font.font(18));

		// Labels
//		Label locationFoundLb = new Label("Found:");
//		Label idLb = new Label("Id:");
//		Label nameLb = new Label("Name:");
//		Label colorLb = new Label("Color:");
//		Label compositionLb = new Label("Composition:");
//		Label textureLb = new Label("Texture:");
//		Label structuresLb = new Label("Structures:");
//		Label roundingLb = new Label("Rounding:");
//		Label lusterLb = new Label("Luster:");
//		Label grainSizeLb = new Label("Grain Size:");
//		Label cleavageLb = new Label("Cleavage:");
//		Label mineralSizeLb = new Label("Mineral Size:");
//		Label fossilDescriptionLb = new Label("Fossil descriptions:");
//		Label sizeLb = new Label("Size:");
//		Label otherFeaturesLb = new Label("Other Cool Features:");



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
		unknownRb.setSelected(true);

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

		locationFoundTF.setPromptText("Location found...");
		idTf.setPromptText("Custom id. If none entered, id will be generated and assigned.");
		nameTf.setPromptText("Sample name...");
		colorTf.setPromptText("Sample color...");
		compositionTf.setPromptText("Sample composition...");
		textureTf.setPromptText("Sample texture...");
		structuresTf.setPromptText("Sample structures...");
		roundingTf.setPromptText("Clast rounding...");
		lusterTf.setPromptText("Sample luster...");
		grainSizeTf.setPromptText("Grain sizes...");
		cleavageTf.setPromptText("Mineral cleavages...");
		mineralSizeTf.setPromptText("Mineral sizes...");
		fossilDescriptionTf.setPromptText("Fossil descriptions...");
		sizeTf.setPromptText("Sample size...");
		otherFeatures.setPromptText("Add any other notes here...");



		HBox rockTypesPane = new HBox();
		VBox leftCenterPane = new VBox();
		SplitPane centerPane = new SplitPane();
		leftCenterPane.getChildren().addAll(rockTypesPane, fossilContent, gridPane);
		gridPane.addColumn(0, locationFoundTF, idTf, nameTf, colorTf, compositionTf,
				textureTf, structuresTf, roundingTf, lusterTf, grainSizeTf, cleavageTf, mineralSizeTf,
				sizeTf);

		rockTypesPane.getChildren().addAll(igneousRb, metaRb, sedRb, unknownRb);

		// Build picture pane
		BorderPane rightPane = new BorderPane();
		Label addImage = new Label("Add Sample Photo");
		addImage.setFont(new Font(16));
		String fileName = "Images/placeholder.jpg";
		FileInputStream imageInputStream = new FileInputStream(new File(fileName));
		Image image = new Image(imageInputStream);
		ImageView imageView = new ImageView(image);
		Button submitBt = new Button("Add photo");
		TextField newImageFileName = new TextField();
		TextField imageDescription = new TextField();
		Label addImageInstructions = new Label("To add a photo, manually add image file to the Images folder, and give the image file name (including the file type). " +
				"You can add multiple images to one sample.");
		addImageInstructions.setWrapText(true);

		VBox picturePaneHeader = new VBox();
		picturePaneHeader.getChildren().addAll(addImage, addImageInstructions);
		picturePaneHeader.setAlignment(Pos.CENTER);

		imageDescription.setPromptText("Add photo caption");
		newImageFileName.setPromptText("Photo file name");
		imageDescription.setMaxWidth(300);
		newImageFileName.setMaxWidth(300);
		addImageInstructions.setTextAlignment(TextAlignment.CENTER);

		VBox picturePaneCenter = new VBox();
		StackPane picturePaneBottom = new StackPane(submitBt);
		picturePaneBottom.setAlignment(Pos.CENTER_LEFT);
		picturePaneCenter.getChildren().addAll(imageView, newImageFileName, imageDescription, submitBt);

		addImage.setPadding(new Insets(40, 0, 0 , 0));
		picturePaneCenter.setSpacing(vGap);
		picturePaneHeader.setSpacing(vGap);
		rightPane.setPadding(padding);
		rightPane.setTranslateX(-20);
		rightPane.setTop(picturePaneHeader);
		rightPane.setCenter(picturePaneCenter);
		rightPane.setBottom(picturePaneBottom);
		rightPane.setMaxWidth(addPaneSize/2);
		rightPane.setPrefWidth(addPaneSize/2);
		rightPane.setMinWidth(addPaneSize/2);
		rightPane.setMinHeight(addPaneSize);

		// Build bottom pane
		StackPane bottomPane = new StackPane(otherFeatures);
		bottomPane.setAlignment(Pos.TOP_LEFT);

		// Formatting
		leftCenterPane.setAlignment(Pos.CENTER_LEFT);
		gridPane.setVgap(vGap);
		gridPane.setHgap(hGap);
		leftCenterPane.setPadding(padding);
		leftCenterPane.setSpacing(vGap);
		rockTypesPane.setSpacing(hGap);
		fossilContent.setAlignment(Pos.CENTER_LEFT);

		// Set actions
		fossilContent.setOnAction(event -> {
			if (fossilContent.isSelected() == false) {
				gridPane.getChildren().clear();
//				gridPane.addColumn(0, locationFoundLb, idLb, nameLb, colorLb, compositionLb,
//						textureLb, structuresLb, roundingLb, lusterLb, grainSizeLb, cleavageLb,  mineralSizeLb,
//						sizeLb, otherFeaturesLb);
				gridPane.addColumn(0, locationFoundTF, idTf, nameTf, colorTf, compositionTf,
						textureTf, structuresTf, roundingTf, lusterTf, grainSizeTf, cleavageTf, mineralSizeTf,
						sizeTf, otherFeatures);
			}
			else {
				gridPane.getChildren().clear();
//				gridPane.addColumn(0, locationFoundLb, idLb, nameLb, colorLb, compositionLb,
//						textureLb, structuresLb, roundingLb, lusterLb, grainSizeLb, cleavageLb,  mineralSizeLb,
//						fossilDescriptionLb, sizeLb, otherFeaturesLb);
				gridPane.addColumn(0, locationFoundTF, idTf, nameTf, colorTf, compositionTf,
						textureTf, structuresTf, roundingTf, lusterTf, grainSizeTf, cleavageTf, mineralSizeTf,
						fossilDescriptionTf, sizeTf, otherFeatures);
			}

		});

		sedRb.setOnAction(event -> {
			gridPane.getChildren().clear();
//			gridPane.addColumn(0, locationFoundLb, idLb, nameLb, colorLb, compositionLb,
//					textureLb, structuresLb, roundingLb, grainSizeLb, sizeLb, otherFeaturesLb);
			gridPane.addColumn(0, locationFoundTF, idTf, nameTf, colorTf, compositionTf,
					textureTf, structuresTf, roundingTf, grainSizeTf, sizeTf, otherFeatures);
			if (leftCenterPane.getChildren().contains(fossilContent) == false) {
				leftCenterPane.getChildren().clear();
				leftCenterPane.getChildren().addAll(rockTypesPane, fossilContent, gridPane);
			}
			if (fossilContent.isSelected()) {
				gridPane.getChildren().clear();
//				gridPane.addColumn(0, locationFoundLb, idLb, nameLb, colorLb, compositionLb,
//					textureLb, structuresLb, roundingLb, grainSizeLb, sizeLb, otherFeaturesLb);
				gridPane.addColumn(0, locationFoundTF, idTf, nameTf, colorTf, compositionTf,
						textureTf, structuresTf, roundingTf, grainSizeTf, fossilDescriptionTf, sizeTf, otherFeatures);
			}
		});

		igneousRb.setOnAction(event -> {
			gridPane.getChildren().clear();
//			gridPane.addColumn(0, locationFoundLb, idLb, nameLb, colorLb, compositionLb,
//					textureLb, structuresLb, lusterLb, cleavageLb,  mineralSizeLb,
//					sizeLb, otherFeaturesLb);
			gridPane.addColumn(0, locationFoundTF, idTf, nameTf, colorTf, compositionTf,
					textureTf, structuresTf, lusterTf, cleavageTf, mineralSizeTf,
					sizeTf, otherFeatures);
			leftCenterPane.getChildren().remove(fossilContent);
		});

		metaRb.setOnAction(event -> {
			gridPane.getChildren().clear();
//			gridPane.addColumn(0, locationFoundLb, idLb, nameLb, colorLb, compositionLb,
//					textureLb, structuresLb, lusterLb, cleavageLb,  mineralSizeLb,
//					sizeLb, otherFeaturesLb);
			gridPane.addColumn(0, locationFoundTF, idTf, nameTf, colorTf, compositionTf,
					textureTf, structuresTf, lusterTf, cleavageTf, mineralSizeTf,
					sizeTf, otherFeatures);
			leftCenterPane.getChildren().remove(fossilContent);
		});

		unknownRb.setOnAction(event -> {
			gridPane.getChildren().clear();
			gridPane.addColumn(0, locationFoundTF, idTf, nameTf, colorTf, compositionTf,
					textureTf, structuresTf, roundingTf, lusterTf, grainSizeTf, cleavageTf, mineralSizeTf,
					sizeTf, otherFeatures);
			if (leftCenterPane.getChildren().contains(fossilContent) == false) {
				leftCenterPane.getChildren().clear();
				leftCenterPane.getChildren().addAll(rockTypesPane, fossilContent, gridPane);
			}
			if (fossilContent.isSelected()) {
				gridPane.getChildren().clear();
//				gridPane.addColumn(0, locationFoundLb, idLb, nameLb, colorLb, compositionLb,
//					textureLb, structuresLb, roundingLb, grainSizeLb, sizeLb, otherFeaturesLb);
				gridPane.addColumn(0, locationFoundTF, idTf, nameTf, colorTf, compositionTf,
						textureTf, structuresTf, roundingTf, lusterTf, grainSizeTf, cleavageTf, mineralSizeTf,
						fossilDescriptionTf, sizeTf, otherFeatures);
			}
		});

		centerPane.getItems().addAll(leftCenterPane, rightPane);
		centerPane.getStyleClass().add("container");
		borderPane.getStyleClass().add("container");
		borderPane.setPadding(padding);
		borderPane.setTop(addSample);
		borderPane.setCenter(centerPane);
		borderPane.setBottom(bottomPane);
		return borderPane;
	}

	// TODO Menu action methods
	public void addSample() throws FileNotFoundException {
		BorderPane borderPane = buildAddSamplePane();
		ScrollPane scrollPane = new ScrollPane(borderPane);
		scrollPane.getStyleClass().add("container");

		borderPane.setMinWidth(addPaneSize);
		borderPane.setMinHeight(addPaneSize);
		scrollPane.getStyleClass().add("container");
		Scene addScene = new Scene(scrollPane, addPaneSize, addPaneSize);
		Stage stage = new Stage();

		addScene.getStylesheets().add("addSceneStyles.css");
		stage.setScene(addScene);
		stage.setTitle("New Sample");
		stage.show();
	}

	public void editSample() {

	}

	public void viewSamples() {

	}



}
