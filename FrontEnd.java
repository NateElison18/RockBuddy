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
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.control.CustomMenuItem;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <h1>FrontEnd</h1>
 * This class contains all the front end/client code for Rock Buddy
 *
 * <p>Last Updated 9/11/23</p>
 * @throws IOException
 *
 * @author Nate Elison
 */
public class FrontEnd extends Application{
	BorderPane mainPane = new BorderPane();
	HashMap<String, Sample> collection = new HashMap<>();
	Sample selectedSample = new Sample();
	Socket socket = new Socket("localhost", 8000);

	static BorderPane viewCollectionBorderPane = new BorderPane();
	static int terminateCode = 100;
	static int sendCollectionCode = 200;
	static int editSampleCode = 300;
	static int deleteSampleCode = 400;

	int mainPaneSize = 320;
	int addPaneSize = 900;
	int viewCollectionPaneSize = 600;
	int viewCollectionPaneWidth = 1200;
	int photoToDisplayIndex = 0;


	public FrontEnd() throws IOException {
	}

	/**
	 * This method is used to launch the JavaFx program, if needed by the compiler.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * This method is used to build the nodes used in the JavaFx program.
	 * The methods put together the global variable mainPane's top, center, and bottom sections.
	 * It then places that mainPane in a scene, that scene on a stage, and finally that stage is displayed.
	 *
	 * @param primaryStage (Stage; stage the scene is placed on)
	 */
	@Override
	public void start(Stage primaryStage) {
		buildTopPane();
		buildCenterPane();
		buildBottomPane();

		Scene scene = new Scene(mainPane, mainPaneSize, mainPaneSize);
		primaryStage.setTitle("RockBuddy");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * This method builds the top pane of the global mainPane BorderPane
	 */
	public void buildTopPane() {
		Label title = new Label("Rock Buddy");
		Label subTitle = new Label("Your rock collection companion app.\n" +
				"Add, edit, and view samples in your collection!");
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
		menu.getItems().addAll(add, edit, view, exit);

		mainPane.setTop(topPane);

		//Set action
		add.setOnAction(event -> {
			try {
				addSample();
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		});
		edit.setOnAction(event -> {
			selectASample();
		});
		view.setOnAction(event -> {
			try {
				viewCollection();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		exit.setOnAction(event -> System.exit(0));
	}

	/**
	 * This method builds the center pane of the global mainPane BorderPane
	 */
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
		editSampleBt.setOnAction(event -> {
			try {
				ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
				toServer.writeObject(new Sample(sendCollectionCode));
				toServer.flush();
				updateCollection();
			} catch (IOException e) {
				e.printStackTrace();
			}

			selectASample();
		});
		viewCollectionBt.setOnAction(event -> {
			try {
				viewCollection();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		exitBt.setOnAction(event -> System.exit(0));
	}

	/**
	 * This method builds the bottom pane of the global mainPane BorderPane
	 */
	public void buildBottomPane() {
		Label byLine = new Label("By Nate Elison");
		StackPane pane = new StackPane(byLine);
		pane.setPadding(new Insets(5));
		pane.setAlignment(Pos.BASELINE_RIGHT);

		mainPane.setBottom(pane);
	}

	/**
	 * This method builds and returns the BorderPane that displays the add sample form. It includes all
	 * input objects, labels, and textfields.
	 * the TextFields are displayed based on the general type selected, and the buttons call their
	 * respective methods.
	 *
	 * @return borderPane (BorderPane; the pane that the add sample form is built on)
	 * @throws FileNotFoundException
	 */
	public BorderPane buildAddSamplePane() throws FileNotFoundException {
		int vGap = 10;
		int hGap = 5;
		ArrayList<SamplePhoto> samplePhotos = new ArrayList<>();
		Insets padding = new Insets(5);
		BorderPane borderPane = new BorderPane();
		GridPane gridPane = new GridPane();
		Label addSample = new Label("Add New Find:");
		addSample.setFont(Font.font(18));
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
		otherFeatures.setEditable(true);

		locationFoundTF.setPromptText("Location found...");
		idTf.setPromptText("Sample id. If none entered, id will be generated");
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
		gridPane.addColumn(0, nameTf, idTf, locationFoundTF, colorTf, compositionTf,
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
		Button photoSubmitBt = new Button("Add photo");
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
//		imageDescription.getStyleClass().add("#photo-text-field");
//		newImageFileName.getStyleClass().add("photo-text-field");
		imageDescription.setMaxWidth(100);
		newImageFileName.setMaxWidth(360);
		addImageInstructions.setTextAlignment(TextAlignment.LEFT);

		VBox picturePaneCenter = new VBox();
		StackPane picturePaneBottom = new StackPane(photoSubmitBt);
		picturePaneBottom.setAlignment(Pos.CENTER_LEFT);
		picturePaneCenter.getChildren().addAll(imageView, newImageFileName, imageDescription, photoSubmitBt);

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
		VBox bottomPane = new VBox(otherFeatures, buttonPane);
		bottomPane.setAlignment(Pos.TOP_LEFT);
		bottomPane.setMaxHeight(200);

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

		photoSubmitBt.setOnAction(event -> {
			try{
				String filePath = "Images/" + newImageFileName.getText();
				String imageCaption = imageDescription.getText();
				FileInputStream updatedImageInputStream = new FileInputStream(new File(filePath));
				Image updatedImage = new Image(updatedImageInputStream);
				imageView.setFitHeight(360);
				imageView.setFitWidth(360);
				imageView.setImage(updatedImage);
				samplePhotos.add(new SamplePhoto(filePath, imageCaption));

				addPhotoWindow(true);

			}
			catch (FileNotFoundException e) {
				addPhotoWindow(false);
			}
		});

		finalSubmitBt.setOnAction(event -> {
			// Pull info from form
			int generalType = 0;
			if (igneousRb.isSelected())
				generalType = 1;
			else if (metaRb.isSelected())
				generalType = 2;
			else if (unknownRb.isSelected())
				generalType = 3;

			String rockName = " ";
			if (nameTf.getText() != null)
				rockName = nameTf.getText();

			String id = idTf.getText();
			if (collection.containsKey(id) || id.equals(""))
				id = generateId(generalType);

			String location = " ";
			if (locationFoundTF.getText() != null)
				location = locationFoundTF.getText();

			String color = " ";
			if (colorTf.getText() != null)
				color = colorTf.getText();

			String composition = " ";
			if (compositionTf.getText() != null)
				composition = compositionTf.getText();

			String texture = " ";
			if (textureTf.getText() != null)
				texture = textureTf.getText();

			String structures = " ";
			if (structuresTf.getText() != null)
				structures = structuresTf.getText();

			String rounding = " ";
			if (roundingTf.getText() != null)
				rounding = roundingTf.getText();

			String luster = " ";
			if (lusterTf.getText() != null)
				luster = lusterTf.getText();

			String grainSize = " ";
			if (grainSizeTf.getText() != null)
				grainSize = grainSizeTf.getText();

			String cleavage = " ";
			if (cleavageTf.getText() != null)
				cleavage = cleavageTf.getText();

			String mineralSize = " ";
			if (mineralSizeTf.getText() != null)
				mineralSize = mineralSizeTf.getText();

			String otherFeaturesString = " ";
			if (otherFeatures.getText() != null)
				otherFeaturesString = otherFeatures.getText();

			String fossilDescription = " ";
			if (fossilDescriptionTf.getText() != null)
				fossilDescription = fossilDescriptionTf.getText();

			boolean fossilContentBool = false;
			if (fossilContent.isSelected())
				fossilContentBool = true;

			String size  = " ";
			if (sizeTf.getText() != null)
				size = sizeTf.getText();

			// Create sample based on info provided, submit to server
			Sample newSample = new Sample(generalType, rockName, id, location, color, composition,
					texture, structures, rounding, luster, grainSize, cleavage, mineralSize,
					otherFeaturesString, fossilDescription, fossilContentBool, size, samplePhotos);
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

	/**
	 * This method builds and returns the BorderPane that displays the view collection table.
	 * It includes the table, and the inputs associated such as the edit, remove, and view buttons,
	 * the filter menu, and the fossil filter radio buttons.
	 * @return pane (BorderPane; the pane that the table and inputs are built upon)
	 */
	public BorderPane buildCollectionPane() {
		BorderPane pane = new BorderPane();
		Label title = new Label("Collection:");
		TableView<Sample> tableView = new TableView<>();
		StackPane tablePane = new StackPane(tableView);
		ScrollPane scrollPane = new ScrollPane(tablePane);
		StackPane centerPane = new StackPane(scrollPane);
		ObservableList<Sample> collection = FXCollections.observableArrayList();
		ArrayList<Sample> samplesArraylist = new ArrayList<>(this.collection.values());
		ArrayList<Sample> samplesWFossilContent = new ArrayList<>();
		ArrayList<Sample> samplesWOFossilContent = new ArrayList<>();
		// Initialize ArrayLists of samples w and wo fossil content
		for (int i = 0; i < samplesArraylist.size(); i++){
			if (samplesArraylist.get(i).fossilContent)
				samplesWFossilContent.add(samplesArraylist.get(i));
			else
				samplesWOFossilContent.add(samplesArraylist.get(i));
		}
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
		dateLoggedCl.setCellValueFactory(new PropertyValueFactory<Sample, String>("dateLogged"));
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

		tableView.getColumns().addAll(rockNameCl, rockIdCl, rockTypeCl, locationFoundCl, rockCompositionCl,
				rockTextureCl, rockStructureCl, rockSizeCl, dateLoggedCl);
		tableView.setMaxWidth(902);

		HBox bottomPane = new HBox();
		CheckBox name = new CheckBox("Name");
		CheckBox id = new CheckBox("Id");
		CheckBox type = new CheckBox("Type");
		CheckBox location = new CheckBox("Location Found");
		CheckBox composition = new CheckBox("Composition");
		CheckBox textures = new CheckBox("Textures");
		CheckBox structures = new CheckBox("Structures");
		CheckBox rounding = new CheckBox("Rounding");
		CheckBox luster = new CheckBox("Luster");
		CheckBox mineralSize = new CheckBox("Mineral Size");
		CheckBox grainSize = new CheckBox("Grain Size");
		CheckBox cleavages = new CheckBox("Cleavages");
		CheckBox date = new CheckBox("Date Logged");

		CheckBox size = new CheckBox("Size");

		MenuButton columnsMenu = 			new MenuButton("Show/Hide Columns");
		CustomMenuItem nameCb = 			new CustomMenuItem(name);
		CustomMenuItem idCb = 				new CustomMenuItem(id);
		CustomMenuItem typeCb = 			new CustomMenuItem(type);
		CustomMenuItem locationCb = 		new CustomMenuItem(location);
		CustomMenuItem compositionCb = 		new CustomMenuItem(composition);
		CustomMenuItem textureCb = 			new CustomMenuItem(textures);
		CustomMenuItem structureCb = 		new CustomMenuItem(structures);
		CustomMenuItem roundingCB = 		new CustomMenuItem(rounding);
		CustomMenuItem lusterCb = 			new CustomMenuItem(luster);
		CustomMenuItem mineralSizeCb = 		new CustomMenuItem(mineralSize);
		CustomMenuItem grainSizesCb = 		new CustomMenuItem(grainSize);
		CustomMenuItem cleavagesCb = 		new CustomMenuItem(cleavages);
		CustomMenuItem sampleSizeCb = 		new CustomMenuItem(size);
		CustomMenuItem dateCb = 			new CustomMenuItem(date);
		MenuItem apply = 					new MenuItem("Apply");

		name.setSelected(true);
		id.setSelected(true);
		type.setSelected(true);
		location.setSelected(true);
		composition.setSelected(true);
		textures.setSelected(true);
		structures.setSelected(true);
		size.setSelected(true);
		date.setSelected(true);

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

		columnsMenu.getItems().addAll(nameCb, idCb, typeCb, locationCb, compositionCb, textureCb,
				structureCb, roundingCB, lusterCb, mineralSizeCb, grainSizesCb, cleavagesCb,
				sampleSizeCb, dateCb, apply);

		// Build filter elements
		VBox filtersBox = new VBox();
		Label filterLb = new Label("Filters:");
		HBox radioButtonBox = new HBox();
		RadioButton sedRb = new RadioButton("Sedimentary");
		RadioButton metaRb = new RadioButton("Metamorphic");
		RadioButton ignRb = new RadioButton("Igneous");
		RadioButton unkRb = new RadioButton("Unknown");
		RadioButton allRb = new RadioButton("All");
		ToggleGroup typeTG = new ToggleGroup();
		RadioButton filterFossilContent = new RadioButton("With Fossil Content");
		RadioButton filterNoFossilContent = new RadioButton("Without Fossil Content");
		RadioButton noFilter = new RadioButton("No Fossil Filter");
		ToggleGroup fossilFilterTG = new ToggleGroup();


		filterFossilContent.setText("With Fossil Content");
		filterFossilContent.setToggleGroup(fossilFilterTG);
		filterNoFossilContent.setToggleGroup(fossilFilterTG);
		noFilter.setToggleGroup(fossilFilterTG);
		noFilter.setSelected(true);

		sedRb.setToggleGroup(typeTG);
		metaRb.setToggleGroup(typeTG);
		ignRb.setToggleGroup(typeTG);
		unkRb.setToggleGroup(typeTG);
		allRb.setToggleGroup(typeTG);
		allRb.setSelected(true);
		radioButtonBox.getChildren().addAll(filterFossilContent, filterNoFossilContent, noFilter);
		radioButtonBox.setSpacing(10);

		filtersBox.getChildren().addAll(filterLb, radioButtonBox);
		filtersBox.setAlignment(Pos.TOP_LEFT);
		filtersBox.setSpacing(5);

		Button viewBt = new Button("View Sample");
		Button editBt = new Button("Edit Sample");
		Button deleteBt = new Button("Remove");
		HBox buttonHbox = new HBox();

		buttonHbox.getChildren().addAll(viewBt, editBt, deleteBt);
		buttonHbox.setAlignment(Pos.BOTTOM_RIGHT);
		buttonHbox.setSpacing(15);
		buttonHbox.setTranslateX(220);

		columnsMenu.setAlignment(Pos.BOTTOM_LEFT);
		bottomPane.getChildren().addAll(columnsMenu, filtersBox, buttonHbox);
		bottomPane.setMinWidth(viewCollectionPaneWidth);
		bottomPane.setSpacing(50);

		// Filter actions
		filterFossilContent.setOnAction(event -> {
			collection.clear();
			collection.addAll(samplesWFossilContent);
			tableView.setItems(collection);
		});
		filterNoFossilContent.setOnAction(event -> {
			collection.clear();
			collection.addAll(samplesWOFossilContent);
			tableView.setItems(collection);
		});
		noFilter.setOnAction(event -> {
			collection.clear();
			collection.addAll(samplesArraylist);
			tableView.setItems(collection);
		});

		// Show/Hide columns Apply action
		apply.setOnAction(event -> {
			int count = 0;
			tableView.getColumns().clear();
			if (name.isSelected()){
				count++;
				tableView.getColumns().add(rockNameCl);
			}
			if (id.isSelected()){
				count++;
				tableView.getColumns().add(rockIdCl);
			}
			if (type.isSelected()){
				count++;
				tableView.getColumns().add(rockTypeCl);
			}
			if (location.isSelected()){
				count++;
				tableView.getColumns().add(locationFoundCl);
			}
			if (composition.isSelected()){
				count++;
				tableView.getColumns().add(rockCompositionCl);
			}
			if (textures.isSelected()){
				count++;
				tableView.getColumns().add(rockTextureCl);
			}
			if (structures.isSelected()){
				count++;
				tableView.getColumns().add(rockStructureCl);
			}
			if (rounding.isSelected()){
				count++;
				tableView.getColumns().add(clastRoundingCl);
			}
			if (luster.isSelected()){
				count++;
				tableView.getColumns().add(rockLusterCl);
			}
			if (mineralSize.isSelected()){
				count++;
				tableView.getColumns().add(mineralSizesCl);
			}
			if (grainSize.isSelected()){
				count++;
				tableView.getColumns().add(clastSizeCl);
			}
			if (cleavages.isSelected()){
				count++;
				tableView.getColumns().add(mineralCleavagesCl);
			}
			if (size.isSelected()){
				count++;
				tableView.getColumns().add(rockSizeCl);
			}
			if (date.isSelected()){
				count++;
				tableView.getColumns().add(dateLoggedCl);
			}
			tableView.setMaxWidth(count * 100.3);
			if (count < 12) {
				scrollPane.setFitToWidth(true);
				scrollPane.setFitToHeight(true);
			}
			else {
				scrollPane.setFitToHeight(false);
				scrollPane.setFitToWidth(false);
			}
			tableView.setMinHeight(scrollPane.getHeight());
		});

		editBt.setOnAction(event -> {
			selectedSample = tableView.getSelectionModel().getSelectedItem();
			try {
				editSample();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			Stage stage = (Stage) editBt.getScene().getWindow();
			stage.close();

		});
		deleteBt.setOnAction(event -> {
			//		viewCollectionScene.getStylesheets().add("addSceneStyles.css");
			VBox areYouSurePane = areYouSurePane((tableView.getSelectionModel().getSelectedItem()));
			Scene areYouSure = new Scene(areYouSurePane);
			Stage stage = new Stage();
			stage.setScene(areYouSure);
			stage.setTitle("Remove Sample");
			stage.show();
			areYouSure.getStylesheets().add("addSceneStyles.css");


			Stage collectionStage = (Stage) editBt.getScene().getWindow();
			collectionStage.close();

		});
		viewBt.setOnAction(event -> {
			try {
				viewSample(tableView.getSelectionModel().getSelectedItem());
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		});

		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);
		tableView.setMinHeight(scrollPane.getHeight());
		centerPane.setMaxWidth(1512);
		bottomPane.setPadding(new Insets(5));
		centerPane.setPadding(new Insets(5));
		pane.setTop(title);
		pane.setCenter(centerPane);
		pane.setBottom(bottomPane);
		return pane;
	}

	/**
	 * This method builds and returns the BorderPane that displays the edit sample form. It includes all
	 * input objects, labels, and textfields.
	 * The TextFields are displayed based on the general type selected, and the buttons call their
	 * respective methods.
	 *
	 * @param sample (Sample; the sample to be edited)
	 * @return borderPane (BorderPane; the pane the form is built upon)
	 * @throws FileNotFoundException
	 */
	public BorderPane buildEditSamplePane(Sample sample) throws FileNotFoundException {
		int vGap = 10;
		int hGap = 5;
		String originalId = sample.getId();
		ArrayList<SamplePhoto> samplePhotos = sample.getSamplePhotos();
		Insets padding = new Insets(5);
		BorderPane borderPane = new BorderPane();
		borderPane.getStyleClass().add("container");
		GridPane gridPane = new GridPane();
		Label editSample = new Label("Edit Sample:");
		editSample.setFont(Font.font(18));

		// Labels
		Label nameLb = new Label("Name:");
		Label idLb = new Label("Id:");
		Label locationFoundLb = new Label("Found:");
		Label colorLb = new Label("Color:");
		Label compositionLb = new Label("Composition:");
		Label textureLb = new Label("Texture:");
		Label structuresLb = new Label("Structures:");
		Label roundingLb = new Label("Rounding:");
		Label lusterLb = new Label("Luster:");
		Label grainSizeLb = new Label("Grain Size:");
		Label cleavageLb = new Label("Cleavage:");
		Label mineralSizeLb = new Label("Mineral Size:");
		Label fossilDescriptionLb = new Label("Fossil descriptions:");
		Label sizeLb = new Label("Size:");
		Label otherFeaturesLb = new Label("Notes:");

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
		if(sample.getFossilContent())
			fossilContent.setSelected(true);

		// TextFields
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

		locationFoundTF.setText(sample.getLocation());
		idTf.setText(sample.getId());
		nameTf.setText(sample.getRockName());
		colorTf.setText(sample.getColor());
		compositionTf.setText(sample.getComposition());
		textureTf.setText(sample.getTexture());
		structuresTf.setText(sample.getStructures());
		roundingTf.setText(sample.getRounding());
		lusterTf.setText(sample.getLuster());
		grainSizeTf.setText(sample.getGrainSize());
		cleavageTf.setText(sample.getCleavage());
		mineralSizeTf.setText(sample.getMineralSize());
		fossilDescriptionTf.setText(sample.getFossilDescription());
		sizeTf.setText(sample.getSize());
		otherFeatures.setText(sample.getOtherFeatures());
		otherFeatures.setWrapText(true);

		idTf.setEditable(false); //Changing the id is screwing things up. Will work around this in a future update.

		// VBoxes to hold labels and TFs
		VBox nameBox = new VBox();
		VBox idBox = new VBox();
		VBox locationBox = new VBox();
		VBox colorBox = new VBox();
		VBox compositionBox = new VBox();
		VBox textureBox = new VBox();
		VBox structuresBox = new VBox();
		VBox roundingBox = new VBox();
		VBox lusterBox = new VBox();
		VBox grainSizeBox = new VBox();
		VBox cleavageBox = new VBox();
		VBox mineralSizeBox = new VBox();
		VBox fossilDescriptionBox = new VBox();
		VBox sizeBox = new VBox();
		VBox otherFeaturesBox = new VBox();

		nameBox.getChildren().addAll(nameLb, nameTf);
		idBox.getChildren().addAll(idLb, idTf);
		locationBox.getChildren().addAll(locationFoundLb, locationFoundTF);
		colorBox.getChildren().addAll(colorLb, colorTf);
		compositionBox.getChildren().addAll(compositionLb, compositionTf);
		textureBox.getChildren().addAll(textureLb, textureTf);
		structuresBox.getChildren().addAll(structuresLb, structuresTf);
		roundingBox.getChildren().addAll(roundingLb, roundingTf);
		lusterBox.getChildren().addAll(lusterLb, lusterTf);
		grainSizeBox.getChildren().addAll(grainSizeLb, grainSizeTf);
		cleavageBox.getChildren().addAll(cleavageLb, cleavageTf);
		mineralSizeBox.getChildren().addAll(mineralSizeLb, mineralSizeTf);
		fossilDescriptionBox.getChildren().addAll(fossilDescriptionLb, fossilDescriptionTf);
		sizeBox.getChildren().addAll(sizeLb, sizeTf);
		otherFeaturesBox.getChildren().addAll(otherFeaturesLb, otherFeatures);

		HBox rockTypesPane = new HBox();
		VBox leftCenterPane = new VBox();
		SplitPane centerPane = new SplitPane();

		gridPane.addColumn(0, nameBox, idBox, locationBox, colorBox, compositionBox, textureBox,
				structuresBox, roundingBox, grainSizeBox, fossilDescriptionBox, sizeBox);
		leftCenterPane.getChildren().addAll(rockTypesPane, fossilContent, gridPane);

		// Set textfields to display according to rock type
		switch (sample.getGeneralType()) {
			case 0:
				sedRb.setSelected(true);
				if (fossilContent.isSelected() == false) {
					gridPane.getChildren().clear();
					gridPane.addColumn(0, nameBox, idBox, locationBox, colorBox, compositionBox, textureBox,
							structuresBox, roundingBox, grainSizeBox, sizeBox);
				}

				break;
			case 1:
				igneousRb.setSelected(true);
				leftCenterPane.getChildren().remove(fossilContent);
				gridPane.getChildren().clear();
				gridPane.addColumn(0, nameBox, idBox, locationBox, colorBox, compositionBox, textureBox,
						structuresBox, lusterBox, cleavageBox, mineralSizeBox, sizeBox);
				break;
			case 2:
				metaRb.setSelected(true);
				leftCenterPane.getChildren().remove(fossilContent);
				gridPane.getChildren().clear();
				gridPane.addColumn(0, nameBox, idBox, locationBox, colorBox, compositionBox, textureBox,
						structuresBox, lusterBox, cleavageBox, mineralSizeBox, sizeBox);
				break;
			case 3:
				unknownRb.setSelected(true);
				if (fossilContent.isSelected() == false) {
					gridPane.getChildren().clear();
					gridPane.addColumn(0, nameBox, idBox, locationBox, colorBox, compositionBox, textureBox,
							structuresBox, roundingBox, grainSizeBox, sizeBox);
				}
				break;
		}

		rockTypesPane.getChildren().addAll(igneousRb, metaRb, sedRb, unknownRb);

		// Build picture pane
		BorderPane rightPane = new BorderPane();
		Label addImage = new Label("Add and Remove Photos");
		addImage.setFont(new Font(16));
		FileInputStream imageInputStream = null;
		try {
			String fileName = sample.getSamplePhotos().get(0).photoPathName;
			imageInputStream = new FileInputStream(new File(fileName));
		}
		catch (FileNotFoundException | NullPointerException | IndexOutOfBoundsException e) {
			String fileName = "Images/placeholder.jpg";
			imageInputStream = new FileInputStream(new File(fileName));
		}
		Image image = new Image(imageInputStream);
		ImageView imageView = new ImageView(image);
		Button photoSubmitBt = new Button("Add photo");
		Button photoRemoveBt = new Button("Remove photo");
		TextField newImageFileName = new TextField();
		TextField imageDescription = new TextField();
		Label addImageInstructions = new Label("To add a photo, manually add image file to the Images folder, " +
				"and give the image file name (including the file type). " +
				"You can add multiple images to one sample.\n" +
				"To remove a photo, only the photo file name is required.");
		addImageInstructions.setWrapText(true);
		addImageInstructions.setMaxWidth(image.getWidth());
		imageView.setFitHeight(360);
		imageView.setFitWidth(360);

		VBox picturePaneHeader = new VBox();
		picturePaneHeader.getChildren().addAll(addImage, addImageInstructions);
		picturePaneHeader.setAlignment(Pos.CENTER_LEFT);
		picturePaneHeader.setPadding(new Insets(5));

		imageDescription.setPromptText("Add photo caption");
		newImageFileName.setPromptText("Photo file name");
		imageDescription.setMaxWidth(100);
		newImageFileName.setMaxWidth(100);
		addImageInstructions.setTextAlignment(TextAlignment.LEFT);

		VBox picturePaneCenter = new VBox();
		HBox photoButtons = new HBox(photoSubmitBt, photoRemoveBt);
		picturePaneCenter.getChildren().addAll(imageView, newImageFileName, imageDescription, photoButtons);
		photoButtons.setSpacing(10);

		addImage.setPadding(new Insets(40, 0, 0 , 0));
		picturePaneCenter.setSpacing(vGap);
		picturePaneHeader.setSpacing(vGap);
		rightPane.setPadding(padding);
		rightPane.setTop(picturePaneHeader);
		rightPane.setCenter(picturePaneCenter);
		rightPane.setMaxWidth(addPaneSize/2);
		rightPane.setPrefWidth(addPaneSize/2);
		rightPane.setMinWidth(addPaneSize/2);

		HBox bottomPane = new HBox();
		Button applyBt = new Button("Apply Changes");

		bottomPane.getChildren().addAll(otherFeaturesBox, applyBt);
		bottomPane.setAlignment(Pos.CENTER_LEFT);
		bottomPane.setSpacing(30);
		bottomPane.setPadding(new Insets(5));

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
					gridPane.addColumn(0, nameBox, idBox, locationBox,  colorBox, compositionBox,
							textureBox, structuresBox, roundingBox, lusterBox, grainSizeBox, cleavageBox, mineralSizeBox,
							fossilDescriptionBox, sizeBox);
				}
				else {
					gridPane.getChildren().clear();
					gridPane.addColumn(0, nameBox, idBox, locationBox,  colorBox, compositionBox,
							textureBox, structuresBox, roundingBox, lusterBox, grainSizeBox, cleavageBox, mineralSizeBox,
							sizeBox);
				}
			}
			// Only sedimentary textfields to display
			else {
				if (fossilContent.isSelected()) {
					gridPane.getChildren().clear();
					gridPane.addColumn(0, nameBox, idBox, locationBox, colorBox, compositionBox,
							textureBox, structuresBox, roundingBox, grainSizeBox,
							fossilDescriptionBox, sizeBox);
				}
				else {
					gridPane.getChildren().clear();
					gridPane.addColumn(0, nameBox, idBox, locationBox, colorBox, compositionBox,
							textureBox, structuresBox, roundingBox, grainSizeBox, sizeBox);
				}

			}


		});

		sedRb.setOnAction(event -> {
			gridPane.getChildren().clear();
			gridPane.addColumn(0, nameBox, idBox, locationBox, colorBox, compositionBox, textureBox,
					structuresBox, roundingBox, grainSizeBox, sizeBox);
			// Re-add fossilContent checkbox if needed
			leftCenterPane.getChildren().clear();
			leftCenterPane.getChildren().addAll(rockTypesPane, fossilContent, gridPane);

			if (fossilContent.isSelected()) {
				gridPane.getChildren().clear();
				gridPane.addColumn(0, nameBox, idBox, locationBox, colorBox, compositionBox,
						textureBox, structuresBox, roundingBox, grainSizeBox,
						fossilDescriptionBox, sizeBox);
			}
		});

		igneousRb.setOnAction(event -> {
			gridPane.getChildren().clear();
			gridPane.addColumn(0, nameBox, idBox, locationBox, colorBox, compositionBox, textureBox,
					structuresBox, lusterBox, cleavageBox, mineralSizeBox, sizeBox);
			leftCenterPane.getChildren().remove(fossilContent);
		});

		metaRb.setOnAction(event -> {
			gridPane.getChildren().clear();
			gridPane.addColumn(0, nameBox, idBox, locationBox, colorBox, compositionBox, textureBox,
					structuresBox, lusterBox, cleavageBox, mineralSizeBox, sizeBox);
			leftCenterPane.getChildren().remove(fossilContent);
		});

		unknownRb.setOnAction(event -> {
			gridPane.getChildren().clear();
			gridPane.addColumn(0, nameBox, idBox, locationBox,  colorBox, compositionBox,
					textureBox, structuresBox, roundingBox, lusterBox, grainSizeBox, cleavageBox, mineralSizeBox,
					sizeBox);
			// Re-add fossilContent checkbox if needed
			leftCenterPane.getChildren().clear();
			leftCenterPane.getChildren().addAll(rockTypesPane, fossilContent, gridPane);

			if (fossilContent.isSelected()) {
				gridPane.getChildren().clear();
				gridPane.addColumn(0, nameBox, idBox, locationBox,  colorBox, compositionBox,
						textureBox, structuresBox, roundingBox, lusterBox, grainSizeBox, cleavageBox, mineralSizeBox,
						fossilDescriptionBox, sizeBox);
			}
		});

		photoSubmitBt.setOnAction(event -> {
			try{
				String filePath = "Images/" + newImageFileName.getText();
				String imageCaption = imageDescription.getText();
				FileInputStream updatedImageInputStream = new FileInputStream(new File(filePath));
				Image updatedImage = new Image(updatedImageInputStream);
				imageView.setFitHeight(360);
				imageView.setFitWidth(360);
				imageView.setImage(updatedImage);
				samplePhotos.add(new SamplePhoto(filePath, imageCaption));

				addPhotoWindow(true);

			}
			catch (FileNotFoundException e) {
				addPhotoWindow(false);
			}
		});
		photoRemoveBt.setOnAction(event -> {
			String filePath = "Images/" + newImageFileName.getText();
			boolean found = false;
			for (int i = 0; i < samplePhotos.size(); i++) {
				if (samplePhotos.get(i).getPhotoPathName().equals(filePath)){
					found = true;
					samplePhotos.remove(i);
					break;
				}
			}
			removeImageWindow(found);
			if (found){
			}
			else {
			}

		});

		applyBt.setOnAction(event -> {
			int generalType = 0;
			if (igneousRb.isSelected())
				generalType = 1;
			else if (metaRb.isSelected())
				generalType = 2;
			else if (unknownRb.isSelected())
				generalType = 3;

			String rockName = "";
			if (nameTf.getText() != null)
				rockName = nameTf.getText();

			String id = idTf.getText();

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
					otherFeaturesString, fossilDescription, fossilContentBool, size, samplePhotos);
			collection.remove(originalId);
			collection.put(idTf.getText(), newSample);
			try {
				sendEditedSample(newSample, originalId);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Stage stage = (Stage) applyBt.getScene().getWindow();

			stage.close();
			try {
				viewCollection();
			} catch (IOException e) {
				e.printStackTrace();
			}
			messageAndOKButtonPane("Sample Edited Successfully!", "Sample Edited");

		});

		centerPane.getItems().addAll(leftCenterPane, rightPane);
		borderPane.setTop(editSample);
		borderPane.setCenter(centerPane);
		borderPane.setBottom(bottomPane);
		return borderPane;
	}

	/**
	 * This method builds and returns the BorderPane that displays the view sample form. It includes all
	 * input objects, labels, and textfields.
	 *
	 * @param sample (Sample; the sample to be viewed)
	 * @return borderPane (BorderPane; the pane the form is built upon)
	 * @throws FileNotFoundException
	 */
	public BorderPane buildViewSamplePane(Sample sample) throws FileNotFoundException {
		int vGap = 10;
		int hGap = 5;
		photoToDisplayIndex = 0;
		String id = sample.getId();
		ArrayList<SamplePhoto> samplePhotos = collection.get(id).getSamplePhotos();
		System.out.println("SamplePHotos size: " + samplePhotos.size());
		Insets padding = new Insets(5);
		BorderPane borderPane = new BorderPane();
		borderPane.getStyleClass().add("container");
		GridPane gridPane = new GridPane();
		Label sampleName = new Label(sample.getRockName());
		sampleName.setFont(Font.font(30));

		// Labels
		Label idLb = new Label("Id:");
		Label locationFoundLb = new Label("Found:");
		Label colorLb = new Label("Color:");
		Label compositionLb = new Label("Composition:");
		Label textureLb = new Label("Texture:");
		Label structuresLb = new Label("Structures:");
		Label roundingLb = new Label("Rounding:");
		Label lusterLb = new Label("Luster:");
		Label grainSizeLb = new Label("Grain Size:");
		Label cleavageLb = new Label("Cleavage:");
		Label mineralSizeLb = new Label("Mineral Size:");
		Label fossilDescriptionLb = new Label("Fossil descriptions:");
		Label sizeLb = new Label("Size:");
		Label otherFeaturesLb = new Label("Notes:");

		// TextFields
		TextField locationFoundTF = new TextField();
		TextField idTf = new TextField();
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

		locationFoundTF.setText(sample.getLocation());
		idTf.setText(sample.getId());
		colorTf.setText(sample.getColor());
		compositionTf.setText(sample.getComposition());
		textureTf.setText(sample.getTexture());
		structuresTf.setText(sample.getStructures());
		roundingTf.setText(sample.getRounding());
		lusterTf.setText(sample.getLuster());
		grainSizeTf.setText(sample.getGrainSize());
		cleavageTf.setText(sample.getCleavage());
		mineralSizeTf.setText(sample.getMineralSize());
		fossilDescriptionTf.setText(sample.getFossilDescription());
		sizeTf.setText(sample.getSize());
		otherFeatures.setText(sample.getOtherFeatures());
		otherFeatures.setWrapText(true);
		locationFoundTF.setEditable(false);
		idTf.setEditable(false);
		colorTf.setEditable(false);
		compositionTf.setEditable(false);
		textureTf.setEditable(false);
		structuresTf.setEditable(false);
		roundingTf.setEditable(false);
		lusterTf.setEditable(false);
		grainSizeTf.setEditable(false);
		cleavageTf.setEditable(false);
		mineralSizeTf.setEditable(false);
		fossilDescriptionTf.setEditable(false);
		sizeTf.setEditable(false);
		otherFeatures.setEditable(false);

		// VBoxes to hold labels and TFs
		VBox nameBox = new VBox();
		VBox idBox = new VBox();
		VBox locationBox = new VBox();
		VBox colorBox = new VBox();
		VBox compositionBox = new VBox();
		VBox textureBox = new VBox();
		VBox structuresBox = new VBox();
		VBox roundingBox = new VBox();
		VBox lusterBox = new VBox();
		VBox grainSizeBox = new VBox();
		VBox cleavageBox = new VBox();
		VBox mineralSizeBox = new VBox();
		VBox fossilDescriptionBox = new VBox();
		VBox sizeBox = new VBox();
		VBox otherFeaturesBox = new VBox();

		nameBox.getChildren().addAll(sampleName);
		idBox.getChildren().addAll(idLb, idTf);
		locationBox.getChildren().addAll(locationFoundLb, locationFoundTF);
		colorBox.getChildren().addAll(colorLb, colorTf);
		compositionBox.getChildren().addAll(compositionLb, compositionTf);
		textureBox.getChildren().addAll(textureLb, textureTf);
		structuresBox.getChildren().addAll(structuresLb, structuresTf);
		roundingBox.getChildren().addAll(roundingLb, roundingTf);
		lusterBox.getChildren().addAll(lusterLb, lusterTf);
		grainSizeBox.getChildren().addAll(grainSizeLb, grainSizeTf);
		cleavageBox.getChildren().addAll(cleavageLb, cleavageTf);
		mineralSizeBox.getChildren().addAll(mineralSizeLb, mineralSizeTf);
		fossilDescriptionBox.getChildren().addAll(fossilDescriptionLb, fossilDescriptionTf);
		sizeBox.getChildren().addAll(sizeLb, sizeTf);
		otherFeaturesBox.getChildren().addAll(otherFeaturesLb, otherFeatures);

		HBox rockTypesPane = new HBox();
		VBox leftCenterPane = new VBox();
		SplitPane centerPane = new SplitPane();

		gridPane.addColumn(0, nameBox, idBox, locationBox, colorBox, compositionBox, textureBox,
				structuresBox, roundingBox, grainSizeBox, fossilDescriptionBox, sizeBox);
		leftCenterPane.getChildren().addAll(rockTypesPane, gridPane);

		// Display textfields according to rock type
		switch (sample.getGeneralType()) {
			case 0:
				if (sample.fossilContent == false) {
					gridPane.getChildren().clear();
					gridPane.addColumn(0, nameBox, idBox, locationBox, colorBox, compositionBox, textureBox,
							structuresBox, roundingBox, grainSizeBox, sizeBox);
				}

				break;
			case 1:
				gridPane.getChildren().clear();
				gridPane.addColumn(0, nameBox, idBox, locationBox, colorBox, compositionBox, textureBox,
						structuresBox, lusterBox, cleavageBox, mineralSizeBox, sizeBox);
				break;
			case 2:
				gridPane.getChildren().clear();
				gridPane.addColumn(0, nameBox, idBox, locationBox, colorBox, compositionBox, textureBox,
						structuresBox, lusterBox, cleavageBox, mineralSizeBox, sizeBox);
				break;
			case 3:
				if (sample.fossilContent == false) {
					gridPane.getChildren().clear();
					gridPane.addColumn(0, nameBox, idBox, locationBox, colorBox, compositionBox, textureBox,
							structuresBox, lusterBox, cleavageBox, mineralSizeBox, roundingBox, grainSizeBox, sizeBox);
				}
				else {
					gridPane.getChildren().clear();
					gridPane.addColumn(0, nameBox, idBox, locationBox, colorBox, compositionBox, textureBox,
							structuresBox, lusterBox, cleavageBox, mineralSizeBox, roundingBox, grainSizeBox, fossilDescriptionBox, sizeBox);
				}
				break;
		}

		// Build picture pane
		BorderPane rightPane = new BorderPane();
		Label addImage = new Label("Sample Photos");
		addImage.setFont(new Font(20));
		FileInputStream imageInputStream;
		Label imageName = new Label();
		Label imageCaption = new Label();
		try {
			String fileName = samplePhotos.get(0).photoPathName;
			imageInputStream = new FileInputStream(fileName);
			imageName.setText(samplePhotos.get(photoToDisplayIndex).getPhotoPathName());
			imageCaption.setText(samplePhotos.get(photoToDisplayIndex).getPhotoDescription());
		}
		catch (FileNotFoundException | NullPointerException | IndexOutOfBoundsException e) {
			String fileName = "Images/placeholder.jpg";
			imageInputStream = new FileInputStream(fileName);
			imageName.setText("Placeholder Image");
			imageCaption.setText("No custom photo found.");
			e.printStackTrace();
		}
		Image image = new Image(imageInputStream);
		ImageView imageView = new ImageView(image);
		Button leftBt = new Button("<--");
		Button rightBt = new Button("-->");

		imageView.setFitHeight(360);
		imageView.setFitWidth(360);

		VBox picturePaneHeader = new VBox();
		picturePaneHeader.getChildren().addAll(addImage);
		picturePaneHeader.setAlignment(Pos.CENTER_LEFT);
		picturePaneHeader.setPadding(new Insets(5));

		imageCaption.setMaxWidth(360);
		imageName.setMaxWidth(360);

		VBox picturePaneCenter = new VBox();
		HBox photoButtons = new HBox(leftBt, rightBt);
		picturePaneCenter.getChildren().addAll(imageView, imageName, imageCaption, photoButtons);
		photoButtons.setSpacing(10);
		photoButtons.setMinWidth(360);
		photoButtons.setMaxWidth(360);
		photoButtons.setAlignment(Pos.CENTER_LEFT);

		addImage.setPadding(new Insets(40, 0, 0 , 0));
		picturePaneCenter.setSpacing(5);
		picturePaneHeader.setSpacing(vGap);
		rightPane.setPadding(padding);
		rightPane.setTop(picturePaneHeader);
		rightPane.setCenter(picturePaneCenter);
		rightPane.setMaxWidth(addPaneSize/2);
		rightPane.setPrefWidth(addPaneSize/2);
		rightPane.setMinWidth(addPaneSize/2);

		HBox bottomPane = new HBox();
		Button editBt = new Button("Edit Sample");

		bottomPane.getChildren().addAll(otherFeaturesBox, editBt);
		bottomPane.setAlignment(Pos.CENTER_LEFT);
		bottomPane.setSpacing(30);
		bottomPane.setPadding(new Insets(5));

		// Formatting
		leftCenterPane.setAlignment(Pos.TOP_LEFT);
		gridPane.setVgap(vGap);
		gridPane.setHgap(hGap);
		leftCenterPane.setPadding(padding);
		leftCenterPane.setSpacing(vGap);
		rockTypesPane.setSpacing(hGap);
		centerPane.getStyleClass().add("container");
		// Remove the buttons if there are less than 2 rock pics
		if (samplePhotos.size() < 2)
			photoButtons.getChildren().clear();

		// Set actions
		rightBt.setOnAction(event -> {
			photoToDisplayIndex++;
			// Check the index is not out of bounds
			if (photoToDisplayIndex >= samplePhotos.size())
				photoToDisplayIndex = 0;
			try {
				String updatedFilePath = samplePhotos.get(photoToDisplayIndex).getPhotoPathName();
				String updatedImageCaption = samplePhotos.get(photoToDisplayIndex).getPhotoDescription();
				FileInputStream updatedImageInputStream = new FileInputStream(updatedFilePath);
				Image updatedImage = new Image(updatedImageInputStream);
				imageView.setFitHeight(360);
				imageView.setFitWidth(360);
				imageView.setImage(updatedImage);
				imageName.setText(updatedFilePath);
				imageCaption.setText(updatedImageCaption);
			}
			catch (NullPointerException | FileNotFoundException e) {
				System.out.println("ERROR on right button press. This should never happen. Ur code sucks dicks");
			}

		});
		leftBt.setOnAction(event -> {
			photoToDisplayIndex--;
			if (photoToDisplayIndex < 0) {
				photoToDisplayIndex = samplePhotos.size() - 1;
			}
			try {
				String updatedFilePath = samplePhotos.get(photoToDisplayIndex).getPhotoPathName();
				String updatedImageCaption = samplePhotos.get(photoToDisplayIndex).getPhotoDescription();
				FileInputStream updatedImageInputStream = new FileInputStream(updatedFilePath);
				Image updatedImage = new Image(updatedImageInputStream);
				imageView.setFitHeight(360);
				imageView.setFitWidth(360);
				imageView.setImage(updatedImage);
				imageName.setText(updatedFilePath);
				imageCaption.setText(updatedImageCaption);
			}
			catch (NullPointerException | FileNotFoundException e) {
				System.out.println("ERROR on left button press. This should never happen. Ur code sucks dicks");
			}
		});
		editBt.setOnAction(event -> {
			try {
				selectedSample = sample;
				editSample();
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
			Stage stage = (Stage) editBt.getScene().getWindow();
			stage.close();
		});

		centerPane.getItems().addAll(leftCenterPane, rightPane);
		borderPane.setCenter(centerPane);
		borderPane.setBottom(bottomPane);
		return borderPane;
	}

	// Main Menu action methods

	/**
	 * This method is called when the add sample button is clicked. It builds a borderPane using the
	 * buildAddSamplePane method, places that pane on a scrollpane, styles it, adds it to a scene,
	 * adds that scene to a stage, which is finally displayed.
	 *
	 * @throws FileNotFoundException
	 */
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

	/**
	 * This method is called when a view collection button is clicked. It updates the global Object collection, by
	 * sending a sample to the server w the corresponding sendCollectionCode, so the backend knows to start sending samples.
	 * The updateCollection method is called to accept those samples.
	 * Once the collection has been updated, the buildCollectionPane is called, which returns a BorderPane. That pane
	 * is added displayed.
	 *
	 * @throws IOException
	 */
	public void viewCollection() throws IOException {
        ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
		toServer.writeObject(new Sample(sendCollectionCode));
        toServer.flush();
        updateCollection();
		viewCollectionBorderPane = buildCollectionPane();
		viewCollectionBorderPane.getStyleClass().add("container");

		viewCollectionBorderPane.setMinWidth(viewCollectionPaneSize);
		viewCollectionBorderPane.setMinHeight(viewCollectionPaneSize);
		Scene viewCollectionScene = new Scene(viewCollectionBorderPane, viewCollectionPaneWidth, viewCollectionPaneSize);
		Stage stage = new Stage();

//		viewCollectionScene.getStylesheets().add("addSceneStyles.css");
		stage.setScene(viewCollectionScene);
		stage.setTitle("View Collection");
		stage.show();
	}

	/**
	 * This method is called when an edit Sample button is clicked. It calls upon the buildEditSamplePane and
	 *  displays the returned BorderPane.
	 * @throws FileNotFoundException
	 */
	public void editSample() throws FileNotFoundException {

		BorderPane borderPane = buildEditSamplePane(selectedSample);
		ScrollPane scrollPane = new ScrollPane(borderPane);
		scrollPane.getStyleClass().add("container");

		borderPane.setMinWidth(addPaneSize);
		borderPane.setMinHeight(addPaneSize);
		borderPane.getStyleClass().add("container");
		Scene addScene = new Scene(scrollPane, addPaneSize + 15, addPaneSize);
		Stage stage = new Stage();

		addScene.getStylesheets().add("editSceneStyles.css");
		stage.setScene(addScene);
		stage.setTitle("Edit Sample");
		stage.show();
	}

	/**
	 * This method is called when a view sample button is clicked. It calls upon the buildViewSamplePane and
	 *  displays the returned BorderPane.
	 * @param sample (Sample; this
	 * @throws FileNotFoundException
	 */
	public void viewSample(Sample sample) throws FileNotFoundException{

		BorderPane borderPane = buildViewSamplePane(sample);
		ScrollPane scrollPane = new ScrollPane(borderPane);
		scrollPane.getStyleClass().add("container");

		borderPane.setMinWidth(addPaneSize);
		borderPane.setMinHeight(addPaneSize);
		scrollPane.getStyleClass().add("container");
		Scene addScene = new Scene(scrollPane, addPaneSize + 15, addPaneSize);
		Stage stage = new Stage();

		addScene.getStylesheets().add("addSceneStyles.css");
		stage.setScene(addScene);
		stage.setTitle("View Sample");
		stage.show();
	}

	/**
	 * This method is called when the submit sample button is clicked. It sends the input sample to the server and
	 * displays a window saying the sample was submitted.
	 *
	 * @param sample (Sample; the sample to be sent to the server)
	 */
	public void submitSample(Sample sample) {
		try {
			ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
			toServer.writeObject(sample);

		} catch (IOException e) {
			e.printStackTrace();
		}
		messageAndOKButtonPane("Sample Submitted Successfully!", "Sample Submission");

	}

	/**
	 * This method is called when a user adds a sample. It generates and returns an id to be used if no id is input or
	 * if the id matches one already used in the collection. The Id is generated by the number of samples in the collection
	 * with the same rock type as the sample.
	 *
	 * @param rockType (int; the rocktype of the sample in int form (0 = sed, 1 = ign, 2 = meta, 3 = unknown))
	 * @return generatedId (String; the auto generated id.)
	 */
	public String generateId(int rockType) {
		try {
			ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
			toServer.writeObject(new Sample(sendCollectionCode));
			toServer.flush();
			updateCollection(); // Ensure collection has been updated.
		} catch (IOException e) {
			e.printStackTrace();
		}

		String generatedId = "";
		AtomicInteger count = new AtomicInteger();
		collection.forEach((id, sample) -> {
			if (sample.getGeneralType() == rockType)
				count.getAndIncrement();
		});
		count.getAndIncrement();

		System.out.println(count);
		switch (rockType) {
			case 0:
				generatedId = "sed" + count;
				break;
			case 1:
				generatedId = "ign" + count;
				break;
			case 2:
				generatedId = "meta" + count;
				break;
			case 3:
				generatedId = "ukn" + count;
				break;
		}

		// Ensure generated key is not already in the collection
		while (collection.containsKey(generatedId)) {
			count.getAndIncrement();
			switch (rockType) {
				case 0:
					generatedId = "sed" + count;
					break;
				case 1:
					generatedId = "ign" + count;
					break;
				case 2:
					generatedId = "meta" + count;
					break;
				case 3:
					generatedId = "ukn" + count;
					break;
			}
		}

		return generatedId;
	}

	/**
	 * This method displays a new window with a short message and an OK button. The message and the window title are
	 * passed as parameters.
	 *
	 * @param message
	 * @param title
	 */
	public void messageAndOKButtonPane(String message, String title) {
		VBox vBox = new VBox();
		Label submitted = new Label(message);
		Button okBt = new Button("OK");
		vBox.getChildren().addAll(submitted, okBt);
		vBox.setSpacing(10);
		vBox.setAlignment(Pos.CENTER_LEFT);
		vBox.setPadding(new Insets(5));
		submitted.setFont(new Font(14));

		Scene addScene = new Scene(vBox, 230, 100);
		Stage stage = new Stage();
		vBox.getStyleClass().add("container");

		addScene.getStylesheets().add("addSceneStyles.css");
		stage.setScene(addScene);
		stage.setTitle(title);
		stage.show();

		okBt.setOnAction(event -> stage.close());

	}
	
	/**
	 * This method builds and returns a VBox that contains an are you sure message along with a final delete and a go back button.
	 * The final delete calls the method to send the sample to be deleted and reopens the view collection window. 
	 * The go back button closes the window and reopens the view collection window.
	 * 
	 * @param sampleToBeDeleted (Sample; the sample to be deleted.)
	 * @return vBox (VBox; the box containing the label and buttons.)
	 */
	public VBox areYouSurePane(Sample sampleToBeDeleted) {
		VBox vBox = new VBox();
		Label areYouSureLb = new Label("Are you sure you want to delete this sample?");
		Label finalLb = new Label("This cannot be undone.");
		VBox labelBox = new VBox();
		Button deleteBt = new Button("Delete");
		Button noGoBackBt = new Button("No, wait!");
		HBox buttonBox = new HBox();

		labelBox.getChildren().addAll(areYouSureLb, finalLb);
		labelBox.setSpacing(2);
		buttonBox.getChildren().addAll(deleteBt, noGoBackBt);
		buttonBox.setSpacing(20);
		vBox.getChildren().addAll(labelBox, buttonBox);
		vBox.setSpacing(10);
		vBox.setAlignment(Pos.CENTER_LEFT);
		vBox.setPadding(new Insets(15));
		areYouSureLb.setFont(new Font(14));
		finalLb.setFont(new Font(14));
		vBox.getStyleClass().add("container");


		noGoBackBt.setOnAction(event -> {
			Stage stage = (Stage) noGoBackBt.getScene().getWindow();
			stage.close();
			try {
				viewCollection();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		deleteBt.setOnAction(event -> {
			try {
				sendSampleForDeletion(sampleToBeDeleted);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Stage stage = (Stage) noGoBackBt.getScene().getWindow();
			stage.close();
			try {
				viewCollection();
			} catch (IOException e) {
				e.printStackTrace();
			}
			messageAndOKButtonPane("Sample " + sampleToBeDeleted.getRockName() + " deleted.", "Sample Deleted");
		});

		return vBox;
	}
	
	/**
	 * This method displays a window that lets the user know if the photo they tried to add to the sample was successful or not.
	 * @param addSuccessful (boolean; true if the action was successful, false if the file path entered could not be found.)
	 */
	public void addPhotoWindow(boolean addSuccessful) {
		VBox vBox = new VBox();
		Scene addScene = new Scene(vBox, 300, 100);
		Stage stage = new Stage();
		vBox.getStyleClass().add("container");
		addScene.getStylesheets().add("addSceneStyles.css");
		stage.setScene(addScene);
		Button closeBt = new Button("Close");
		Label label = new Label("Photo added to the sample and ready to be saved.");
		vBox.getChildren().addAll(label, closeBt);
		vBox.setSpacing(5);
		vBox.setPadding(new Insets(10));
		label.setWrapText(true);

		if (addSuccessful) {
			stage.setTitle("Success!");
			stage.show();
		}
		else {
			label.setText("Image file not found. Please ensure the file has been added to the images folder and the file name is correct and includes the file type (ex: rockPic.png)");
			stage.setTitle("Please Try Again");
			stage.show();
		}
		closeBt.setOnAction(event -> stage.close());
	}
	
	/**
	 * This method displays a window that lets the user know if the photo they tried to remove from the sample was successful or not.
	 * @param removeSuccessful (boolean; true if the action was successful, false if the file path entered could not be found.)
	 */
	public void removeImageWindow(boolean removeSuccessful) {
		VBox vBox = new VBox();
		Scene addScene = new Scene(vBox, 300, 100);
		Stage stage = new Stage();
		vBox.getStyleClass().add("container");
		addScene.getStylesheets().add("addSceneStyles.css");
		stage.setScene(addScene);
		Button closeBt = new Button("Close");
		Label label = new Label("Photo removed from the sample. The image file still exists, but is no longer linked to this sample.");
		vBox.getChildren().addAll(label, closeBt);
		vBox.setSpacing(5);
		vBox.setPadding(new Insets(10));
		label.setWrapText(true);

		if (removeSuccessful) {
			stage.setTitle("Image removed Successfully!");
			stage.show();
		}
		else {
			label.setText("Image file not found. Please ensure the file name is correct and includes the file type (ex: rockPic.png).");
			stage.setTitle("Image Not Found");
			stage.show();
		}
		closeBt.setOnAction(event -> stage.close());
	}
	
	/**
	 * This method displays a window with a table containing all the samples' names and id. The user selects an id and clicks the button.
	 * When the button is clicked, the global selectedSample is updated to that sample and the editSample method is called.
	*/
	public void selectASample() {
		BorderPane pane = new BorderPane();
		TableView<Sample> tableView = new TableView<>();
		StackPane tablePane = new StackPane(tableView);
		ScrollPane scrollPane = new ScrollPane(tablePane);
		StackPane centerPane = new StackPane(scrollPane);
		ObservableList<Sample> collection = FXCollections.observableArrayList();
		ArrayList<Sample> samplesArraylist = new ArrayList<>(this.collection.values());
		TableColumn rockNameCl = new TableColumn("Name");
		TableColumn rockIdCl = new TableColumn("Id");

		for (int i = 0; i < samplesArraylist.size(); i++)
			collection.add(samplesArraylist.get(i));
		tableView.setItems(collection);

		rockNameCl.setCellValueFactory(new PropertyValueFactory<Sample, String>("rockName"));
		rockIdCl.setCellValueFactory(new PropertyValueFactory<Sample, String>("id"));

		tableView.getColumns().addAll(rockNameCl, rockIdCl);
		tableView.setMaxWidth(201);
		rockIdCl.setMinWidth(100);
		rockNameCl.setMinWidth(100);

		HBox buttonPane = new HBox();
		Button selectBt = new Button("Select");

		buttonPane.getChildren().add(selectBt);
		buttonPane.setMinWidth(205);
		buttonPane.setPadding(new Insets(5));
		buttonPane.setAlignment(Pos.BOTTOM_RIGHT);

		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);
		pane.setCenter(centerPane);
		pane.setBottom(buttonPane);

		Scene viewCollectionScene = new Scene(pane, 205, 400);
		Stage stage = new Stage();
//		viewCollectionScene.getStylesheets().add("addSceneStyles.css");
		stage.setScene(viewCollectionScene);
		stage.setTitle("Select Sample");
		stage.show();

		selectBt.setOnAction(event -> {
			 selectedSample = tableView.getSelectionModel().getSelectedItem();
			try {
				editSample();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			stage.close();
		});

	}
	
	/**
	 * This method updates the collection HashMap. It first clears the collection and then listens for data to be sent by the server.
	 * Once the type received by the client equals 100, the method ends as all the samples have been sent.
	 * The method creates a samplePhotos arrayList of Sample Photos and populates that as well depending on the number of photos
	 * (which is also sent as an int by the server), which is then saved to the sample as it's SamplePhotos arraylist, before the sample 
	 * is saved to the collection HashMap with the sample's ID as the key. 
	 * @throws IOException
	 */
	public void updateCollection() throws IOException {
		collection.clear();
		DataInputStream fromServer = new DataInputStream(socket.getInputStream());
			while (true) {
				ArrayList<SamplePhoto> samplePhotos = new ArrayList<>();
				samplePhotos.clear();
				int type = fromServer.readInt(); // Outside Sample constructor method below, to break out of the loop once the key int is sent (100).
				if (type == terminateCode) return; // Check for terminate code, end method if received.
				Sample newSample = new Sample(type, fromServer.readUTF(),
						fromServer.readUTF(), fromServer.readUTF(), fromServer.readUTF(),
						fromServer.readUTF(), fromServer.readUTF(), fromServer.readUTF(),
						fromServer.readUTF(), fromServer.readUTF(), fromServer.readUTF(),
						fromServer.readUTF(), fromServer.readUTF(), fromServer.readUTF(),
						fromServer.readUTF(), fromServer.readBoolean(), fromServer.readUTF());
				// Build the samplePhotos ArrayList
				int samplePhotoArraySize = fromServer.readInt();
				for (int i = 0; i < samplePhotoArraySize; i++)
					samplePhotos.add(new SamplePhoto(fromServer.readUTF(), fromServer.readUTF()));
				newSample.setSamplePhotos(samplePhotos);
				// Get date logged and add it to the sample
				String dateLogged = fromServer.readUTF();
				newSample.setDateLogged(dateLogged);
				collection.put(newSample.getId(), newSample);
			}
	}
	
	/**
	 * This method sends the server a sample with the key that indicates to the server to expect a sample to be edited and a string.
	 * It then sends a Sample that has been edited and that sample's original ID. The server (BackEnd) removes the sample
	 *  with the originalId as a key, before adding the editedSample to the .txt file.
	 *  
	 * @param editedSample (Sample; the sample that has been edited, to be sent to the server)
	 * @param originalId (String; the original sample's id)
	 * @throws IOException
	*/
	public void sendEditedSample(Sample editedSample, String originalId) throws IOException {
		ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
		toServer.writeObject(new Sample(editSampleCode)); // Send Sample w code so backend knows to expect a Sample that has been edited.
		toServer.writeObject(editedSample);
		toServer.writeUTF(originalId); // Send the original id, just in case the id was edited
		toServer.flush();
	}
	
	/**
	 * This method sends a sample to the server with the deletedSampleCode so the backend expects another sample that it then removes 
	 * from the collection. The method then sends the sample to be deleted.
	 * 
	 * @param sampleToBeDeleted (Sample; the sample that is sent for deletion.)
	 * @throws IOException
	*/
	public void sendSampleForDeletion(Sample sampleToBeDeleted) throws IOException {
		ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
		toServer.writeObject(new Sample(deleteSampleCode)); // Send Sample w code so backend knows to expect the id of a sample to be deleted.
		toServer.writeUTF(sampleToBeDeleted.getId());
		toServer.flush();
	}
}
