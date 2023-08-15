import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.control.CustomMenuItem;


import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

public class FrontEnd extends Application{
	BorderPane mainPane = new BorderPane();
	int mainPaneSize = 320;
	int addPaneSize = 1000;
	int viewCollectionPaneSize = 600;
	HashMap<String, Sample> collection = new HashMap<>();


	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		collection = BackEnd.getCollection();
		buildTopPane();
		buildCenterPane();
		buildBottomPane();

		Scene scene = new Scene(mainPane, mainPaneSize, mainPaneSize);
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
		title.setFont(new Font(25));
		subTitle.setFont(new Font(15));
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

	public void buildCenterPane() {
		VBox mainCenterpane = new VBox();
		Button addSampleBt = new Button("Add Sample");
		Button editSampleBt = new Button("Edit Sample");
		Button viewCollectionBt	= new Button("View Collection");
		Button exitBt = new Button("Exit");
		StackPane exitBtPane = new StackPane(exitBt);

		exitBtPane.setPadding(new Insets(10));
		mainCenterpane.getChildren().addAll(addSampleBt, editSampleBt, viewCollectionBt, exitBtPane);
		mainCenterpane.setSpacing(10);
		mainCenterpane.setAlignment(Pos.TOP_CENTER);
		mainCenterpane.setPadding(new Insets(20));

		mainPane.setCenter(mainCenterpane);

		// Set action
		addSampleBt.setOnAction(event -> {
			try {
				addSample();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		});
		editSampleBt.setOnAction(event -> editSample());
		viewCollectionBt.setOnAction(event -> viewSamples());
		exitBt.setOnAction(event -> System.exit(0));
	}

	public void buildBottomPane() {
		Label byLine = new Label("By Nate Elison");
		StackPane pane = new StackPane(byLine);
		pane.setPadding(new Insets(5));
		pane.setAlignment(Pos.BASELINE_RIGHT);

		mainPane.setBottom(pane);
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
		idTf.setPromptText("Sample id. If none entered, id will be auto-assigned.");
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
		otherFeatures.setWrapText(true);

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
		Label addImageInstructions = new Label("To add a photo, manually add image file to the Images folder, " +
				"and give the image file name (including the file type). " +
				"You can add multiple images to one sample.");
		addImageInstructions.setWrapText(true);
		addImageInstructions.setMaxWidth(image.getWidth());

		VBox picturePaneHeader = new VBox();
		picturePaneHeader.getChildren().addAll(addImage, addImageInstructions);
		picturePaneHeader.setAlignment(Pos.CENTER_LEFT);

		imageDescription.setPromptText("Add photo caption");
		newImageFileName.setPromptText("Photo file name");
		imageDescription.setMaxWidth(100);
		newImageFileName.setMaxWidth(100);
		addImageInstructions.setTextAlignment(TextAlignment.LEFT);

		VBox picturePaneCenter = new VBox();
		StackPane picturePaneBottom = new StackPane(submitBt);
		picturePaneBottom.setAlignment(Pos.CENTER_LEFT);
		picturePaneCenter.getChildren().addAll(imageView, newImageFileName, imageDescription, submitBt);

		addImage.setPadding(new Insets(40, 0, 0 , 0));
		picturePaneCenter.setSpacing(vGap);
		picturePaneHeader.setSpacing(vGap);
		rightPane.setPadding(padding);
		rightPane.setTop(picturePaneHeader);
		rightPane.setCenter(picturePaneCenter);
		rightPane.setBottom(picturePaneBottom);
		rightPane.setMaxWidth(addPaneSize/2);
		rightPane.setPrefWidth(addPaneSize/2);
		rightPane.setMinWidth(addPaneSize/2);

		// Build bottom pane
		Button finalSubmitBt = new Button("Add Sample");
		StackPane buttonPane = new StackPane(finalSubmitBt);
		buttonPane.setAlignment(Pos.BOTTOM_RIGHT);
		buttonPane.setPadding(padding);
		StackPane bottomPane = new StackPane(otherFeatures, buttonPane);
		bottomPane.setAlignment(Pos.TOP_LEFT);

		// Formatting
		leftCenterPane.setAlignment(Pos.TOP_LEFT);
		gridPane.setVgap(vGap);
		gridPane.setHgap(hGap);
		leftCenterPane.setPadding(padding);
		leftCenterPane.setSpacing(vGap);
		rockTypesPane.setSpacing(hGap);
		fossilContent.setAlignment(Pos.CENTER_LEFT);

		// Set actions
		fossilContent.setOnAction(event -> {
			if (unknownRb.isSelected()) {
				if (fossilContent.isSelected()) {
					gridPane.getChildren().clear();
					gridPane.addColumn(0, nameTf, idTf, locationFoundTF,  colorTf, compositionTf,
							textureTf, structuresTf, roundingTf, lusterTf, grainSizeTf, cleavageTf, mineralSizeTf,
							fossilDescriptionTf, sizeTf);
				}
				else {
					gridPane.getChildren().clear();
					gridPane.addColumn(0, nameTf,  idTf, locationFoundTF, colorTf, compositionTf,
							textureTf, structuresTf, roundingTf, lusterTf, grainSizeTf, cleavageTf, mineralSizeTf,
							sizeTf);
				}
			}
			// Only sedimentary textfields to display
			else {
				if (fossilContent.isSelected()) {
					gridPane.getChildren().clear();
					gridPane.addColumn(0, nameTf, idTf, locationFoundTF, colorTf, compositionTf,
							textureTf, structuresTf, roundingTf, grainSizeTf,
							fossilDescriptionTf, sizeTf);
				}
				else {
					gridPane.getChildren().clear();
					gridPane.addColumn(0, nameTf, idTf, locationFoundTF, colorTf, compositionTf,
							textureTf, structuresTf, roundingTf, grainSizeTf, sizeTf);
				}

			}


		});

		sedRb.setOnAction(event -> {
			gridPane.getChildren().clear();
			gridPane.addColumn(0, nameTf, idTf, locationFoundTF, colorTf, compositionTf,
					textureTf, structuresTf, roundingTf, grainSizeTf, sizeTf);
			// Re-add fossilContent checkbox if needed
			leftCenterPane.getChildren().clear();
			leftCenterPane.getChildren().addAll(rockTypesPane, fossilContent, gridPane);

			if (fossilContent.isSelected()) {
				gridPane.getChildren().clear();
				gridPane.addColumn(0, nameTf, idTf, locationFoundTF, colorTf, compositionTf,
						textureTf, structuresTf, roundingTf, grainSizeTf, fossilDescriptionTf, sizeTf);
			}
		});

		igneousRb.setOnAction(event -> {
			gridPane.getChildren().clear();
			gridPane.addColumn(0, nameTf, idTf, locationFoundTF, colorTf, compositionTf,
					textureTf, structuresTf, lusterTf, cleavageTf, mineralSizeTf, sizeTf);
			leftCenterPane.getChildren().remove(fossilContent);
		});

		metaRb.setOnAction(event -> {
			gridPane.getChildren().clear();
			gridPane.addColumn(0, nameTf, idTf, locationFoundTF, colorTf, compositionTf,
					textureTf, structuresTf, lusterTf, cleavageTf, mineralSizeTf, sizeTf);
			leftCenterPane.getChildren().remove(fossilContent);
		});

		unknownRb.setOnAction(event -> {
			gridPane.getChildren().clear();
			gridPane.addColumn(0, nameTf, idTf, locationFoundTF, colorTf, compositionTf,
					textureTf, structuresTf, roundingTf, lusterTf, grainSizeTf, cleavageTf, mineralSizeTf,
					sizeTf);
			// Re-add fossilContent checkbox if needed
			leftCenterPane.getChildren().clear();
			leftCenterPane.getChildren().addAll(rockTypesPane, fossilContent, gridPane);

			if (fossilContent.isSelected()) {
				gridPane.getChildren().clear();
				gridPane.addColumn(0, nameTf, idTf, locationFoundTF, colorTf, compositionTf,
						textureTf, structuresTf, roundingTf, lusterTf, grainSizeTf, cleavageTf, mineralSizeTf,
						fossilDescriptionTf, sizeTf);
			}
		});

		finalSubmitBt.setOnAction(event -> {
			// Pull info from form
			int generalType = 0;
			if (igneousRb.isSelected())
				generalType = 1;
			else if (metaRb.isSelected())
				generalType = 2;

			String rockName = "";
			if (nameTf.getText() != null)
				rockName = nameTf.getText();

			String id = generateId(generalType);
			if (idTf.getText() != null)
				id = idTf.getText();

			String location = "";
			if (locationFoundTF.getText() != null)
				location = locationFoundTF.getText();

			String color = "";
			if (colorTf.getText() != null)
				color = colorTf.getText();

			String composition = "";
			if (compositionTf.getText() != null)
				composition = compositionTf.getText();

			String texture = "";
			if (textureTf.getText() != null)
				texture = textureTf.getText();

			String structures = "";
			if (structuresTf.getText() != null)
				structures = structuresTf.getText();

			String rounding = "";
			if (roundingTf.getText() != null)
				rounding = roundingTf.getText();

			String luster = "";
			if (lusterTf.getText() != null)
				luster = lusterTf.getText();

			String grainSize = "";
			if (grainSizeTf.getText() != null)
				grainSize = grainSizeTf.getText();

			String cleavage = "";
			if (cleavageTf.getText() != null)
				cleavage = cleavageTf.getText();

			String mineralSize = "";
			if (mineralSizeTf.getText() != null)
				mineralSize = mineralSizeTf.getText();

			String otherFeaturesString = "";
			if (otherFeatures.getText() != null)
				otherFeaturesString = otherFeatures.getText();

			String fossilDescription = "";
			if (fossilDescriptionTf.getText() != null)
				fossilDescription = fossilDescriptionTf.getText();

			boolean fossilContentBool = false;
			if (fossilContent.isSelected())
				fossilContentBool = true;

			String size  = "";
			if (sizeTf.getText() != null)
				size = sizeTf.getText();

			// Create sample based on info provided, submit to server
			Sample newSample = new Sample(generalType, rockName, id, location, color, composition,
					texture, structures, rounding, luster, grainSize, cleavage, mineralSize,
					otherFeaturesString, fossilDescription, fossilContentBool, size);
			submitSample(newSample);

			// Close new sample window
			Stage stage = (Stage) finalSubmitBt.getScene().getWindow();
			stage.close();
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

	public BorderPane buildCollectionPane() {
		BorderPane pane = new BorderPane();
		Label title = new Label("Collection:");
		TableView<Sample> tableView = new TableView<>();
		StackPane centerPane = new StackPane(tableView);
		ObservableList<Sample> collection = FXCollections.observableArrayList();
		ArrayList<Sample> samplesArraylist = new ArrayList<>(this.collection.values());
		TableColumn rockNameCl = new TableColumn("Name");
		TableColumn rockIdCl = new TableColumn("Id");
		TableColumn rockTypeCl = new TableColumn("Type");
		TableColumn locationFoundCl = new TableColumn("Location Found");
		TableColumn rockCompositionCl = new TableColumn("Composition");
		TableColumn rockTextureCl = new TableColumn("Texture");
		TableColumn rockStructureCl = new TableColumn("Structure");
		TableColumn clastRoundingCl = new TableColumn("Clast Rounding");
		TableColumn rockLusterCl = new TableColumn("Luster");
		TableColumn mineralSizesCl = new TableColumn("Mineral Sizes");
		TableColumn clastSizeCl = new TableColumn("Grain Sizes");
		TableColumn mineralCleavagesCl = new TableColumn("Cleavages");
		TableColumn rockSizeCl = new TableColumn("Sample Size");
		TableColumn dateLoggedCl = new TableColumn("Date Logged");
		TableColumn fossilContentCl = new TableColumn("Fossil Content?");

		title.setPadding(new Insets(5));
		title.setFont(new Font(18));
		for (int i = 0; i < samplesArraylist.size(); i++)
			collection.add(samplesArraylist.get(i));
		tableView.setItems(collection);

		rockNameCl.setCellValueFactory(new PropertyValueFactory<Sample, String>("rockName"));
		rockIdCl.setCellValueFactory(new PropertyValueFactory<Sample, String>("id"));
		rockTypeCl.setCellValueFactory(new PropertyValueFactory<Sample, String>("rockTypeString"));
		locationFoundCl.setCellValueFactory(new PropertyValueFactory<Sample, String>("location"));
		rockCompositionCl.setCellValueFactory(new PropertyValueFactory<Sample, String>("composition"));
		rockTextureCl.setCellValueFactory(new PropertyValueFactory<Sample, String>("texture"));
		rockStructureCl.setCellValueFactory(new PropertyValueFactory<Sample, String>("structures"));
		clastRoundingCl.setCellValueFactory(new PropertyValueFactory<Sample, String>("rounding"));
		rockLusterCl.setCellValueFactory(new PropertyValueFactory<Sample, String>("luster"));
		mineralSizesCl.setCellValueFactory(new PropertyValueFactory<Sample, String>("mineralSize"));
		clastSizeCl.setCellValueFactory(new PropertyValueFactory<Sample, String>("grainSize"));
		mineralCleavagesCl.setCellValueFactory(new PropertyValueFactory<Sample, String>("cleavage"));
		rockSizeCl.setCellValueFactory(new PropertyValueFactory<Sample, String>("size"));
		dateLoggedCl.setCellValueFactory(new PropertyValueFactory<Sample, Date>("dateLogged"));
		fossilContentCl.setCellValueFactory(new PropertyValueFactory<Sample, Boolean>("fossilContent"));
		rockNameCl.setMinWidth(100);
		rockIdCl.setMinWidth(100);
		rockTypeCl.setMinWidth(100);
		locationFoundCl.setMinWidth(100);
		rockCompositionCl.setMinWidth(100);
		rockTextureCl.setMinWidth(100);
		rockStructureCl.setMinWidth(100);
		clastRoundingCl.setMinWidth(100);
		rockLusterCl.setMinWidth(100);
		mineralSizesCl.setMinWidth(100);
		clastSizeCl.setMinWidth(100);
		mineralCleavagesCl.setMinWidth(100);
		rockSizeCl.setMinWidth(100);
		dateLoggedCl.setMinWidth(100);
		fossilContentCl.setMinWidth(100);

		tableView.getColumns().addAll(rockNameCl, rockIdCl, rockTypeCl, locationFoundCl, rockCompositionCl,
				rockTextureCl, rockStructureCl, clastRoundingCl, rockLusterCl, mineralSizesCl, clastSizeCl,
				mineralCleavagesCl, rockSizeCl, dateLoggedCl, fossilContentCl);

		// TODO build filter pane
		HBox bottomPane = new HBox();
		MenuButton columnsMenu = 			new MenuButton("Show/Hide Columns");
		CustomMenuItem nameCb = 			new CustomMenuItem(new CheckBox("Name"));
		CustomMenuItem idCb = 				new CustomMenuItem(new CheckBox("ID"));
		CustomMenuItem typeCb = 			new CustomMenuItem(new CheckBox("Type"));
		CustomMenuItem locationCb = 		new CustomMenuItem(new CheckBox("Location Found"));
		CustomMenuItem compositionCb = 		new CustomMenuItem(new CheckBox("Composition"));
		CustomMenuItem textureCb = 			new CustomMenuItem(new CheckBox("Texture"));
		CustomMenuItem structureCb = 		new CustomMenuItem(new CheckBox("Structure"));
		CustomMenuItem roundingCB = 		new CustomMenuItem(new CheckBox("Clast Rounding"));
		CustomMenuItem lusterCb = 			new CustomMenuItem(new CheckBox("Luster"));
		CustomMenuItem mineralSizeCb = 		new CustomMenuItem(new CheckBox("Mineral Sizes"));
		CustomMenuItem grainSizesCb = 		new CustomMenuItem(new CheckBox("Grain Sizes"));
		CustomMenuItem cleavagesCb = 		new CustomMenuItem(new CheckBox("Cleavages"));
		CustomMenuItem sampleSizeCb = 		new CustomMenuItem(new CheckBox("Sample Size"));
		CustomMenuItem dateCb = 			new CustomMenuItem(new CheckBox("Date Logged"));
		CustomMenuItem fossilContentCb = 	new CustomMenuItem(new CheckBox("Fossil Content"));
		MenuItem apply = 					new MenuItem("Apply");

		nameCb.setHideOnClick(false);
		idCb.setHideOnClick(false);
		typeCb.setHideOnClick(false);
		locationCb.setHideOnClick(false);
		compositionCb.setHideOnClick(false);
		textureCb.setHideOnClick(false);
		structureCb.setHideOnClick(false);
		roundingCB.setHideOnClick(false);
		lusterCb.setHideOnClick(false);
		mineralSizeCb.setHideOnClick(false);
		grainSizesCb.setHideOnClick(false);
		cleavagesCb.setHideOnClick(false);
		sampleSizeCb.setHideOnClick(false);
		dateCb.setHideOnClick(false);
		fossilContentCb.setHideOnClick(false);

		columnsMenu.getItems().addAll(nameCb, idCb, typeCb, locationCb, compositionCb, textureCb,
				structureCb, roundingCB, lusterCb, mineralSizeCb, grainSizesCb, cleavagesCb,
				sampleSizeCb, dateCb, fossilContentCb, apply);
		bottomPane.getChildren().add(columnsMenu);

		centerPane.setMaxWidth(1512);
		bottomPane.setPadding(new Insets(5));
		centerPane.setPadding(new Insets(5));
		pane.setTop(title);
		pane.setCenter(centerPane);
		pane.setBottom(bottomPane);
		return pane;
	}

	// TODO Menu action methods
	public void addSample() throws FileNotFoundException {
		BorderPane borderPane = buildAddSamplePane();
		ScrollPane scrollPane = new ScrollPane(borderPane);
		scrollPane.getStyleClass().add("container");

		borderPane.setMinWidth(addPaneSize);
		borderPane.setMinHeight(addPaneSize);
		scrollPane.getStyleClass().add("container");
		Scene addScene = new Scene(scrollPane, addPaneSize + 15, addPaneSize);
		Stage stage = new Stage();

		addScene.getStylesheets().add("addSceneStyles.css");
		stage.setScene(addScene);
		stage.setTitle("New Sample");
		stage.show();
	}
	public void viewSamples() {
		BorderPane borderPane = buildCollectionPane();
		ScrollPane scrollPane = new ScrollPane(borderPane);
		borderPane.getStyleClass().add("container");

		borderPane.setMinWidth(viewCollectionPaneSize);
		borderPane.setMinHeight(viewCollectionPaneSize);
		Scene viewCollectionScene = new Scene(borderPane, viewCollectionPaneSize * 2, viewCollectionPaneSize);
		Stage stage = new Stage();

//		viewCollectionScene.getStylesheets().add("addSceneStyles.css");
		stage.setScene(viewCollectionScene);
		stage.setTitle("View Collection");
		stage.show();
	}
	public void editSample() {

	}

	public void submitSample(Sample sample) {
		String host = "localhost";

		try {
			Socket socket = new Socket(host, 8000);
			ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
			toServer.writeObject(sample);

		} catch (IOException e) {
			e.printStackTrace();
		}

		VBox vBox = submitSamplePane();
		Scene addScene = new Scene(vBox, 230, 100);
		Stage stage = new Stage();
		vBox.getStyleClass().add("container");

		addScene.getStylesheets().add("addSceneStyles.css");
		stage.setScene(addScene);
		stage.setTitle("Sample Submission");
		stage.show();

	}

	// TODO generate ID
	public String generateId(int rockType) {
		String id = "";
		return id;
	}

	public VBox submitSamplePane() {
		VBox vBox = new VBox();
		Label submitted = new Label("Sample Submitted Successfully!");
		Button okBt = new Button("OK");
		vBox.getChildren().addAll(submitted, okBt);
		vBox.setSpacing(10);
		vBox.setAlignment(Pos.CENTER_LEFT);
		vBox.setPadding(new Insets(5));
		submitted.setFont(new Font(14));

		okBt.setOnAction(event -> {
			Stage stage = (Stage) okBt.getScene().getWindow();
			stage.close();
		});

		return vBox;
	}



}
