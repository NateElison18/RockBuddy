import java.util.ArrayList;
import java.util.Date;

public class Sample {
	int generalType; // 0 == sed, 1 == ign, 2 == meta
	String rockTypeString;
	String location;
	String rockName;
	String id;
	String color;
	String composition;
	String texture;
	String structures;
	String rounding;
	String luster;
	String grainSize;
	String cleavage;
	String mineralSize;
	String fossilDescription;
	String otherFeatures;
	String size;
	//TODO implement imageFilePaths
	ArrayList<String> imageFilePaths;

	boolean fossilContent;
	Date dateLogged;

	// Constructor for everything
	Sample(int generalType, String rockName, String id, String location, String color, String composition, String texture, String structures, String rounding,
			String luster, String grainSize, String cleavage, String mineralSize, String otherFeatures, String fossilDescription, boolean fossilContent,
		   	String size) {
		this.generalType = generalType;
		this.location = location;
		this.rockName = rockName;
		this.id = id;
		this.color = color;
		this.composition = composition;
		this.texture = texture;
		this.structures = structures;
		this.rounding = rounding;
		this.luster = luster;
		this.grainSize = grainSize;
		this.cleavage = cleavage;
		this.mineralSize = mineralSize;
		this.otherFeatures = otherFeatures;
		this.fossilContent = fossilContent;
		this.fossilDescription = fossilDescription;
		this.size = size;
		dateLogged = new Date();

		switch (generalType){
			case 0:
				rockTypeString = "Sedimentary";
				break;
			case 1:
				rockTypeString = "Igneous";
				break;
			case 2:
				rockTypeString = "Metamorphic";
				break;
		}
	}
	
	// Igneous constructor
	Sample(int generalType, String location, String rockName, String id, String color, String composition, String texture,
			String structures, String luster, String cleavage, String mineralSize, String otherFeatures, String size) {
		this.location = location;
		this.generalType = generalType;
		this.rockName = rockName;
		this.id = id;
		this.color = color;
		this.composition = composition;
		this.texture = texture;
		this.structures = structures;
		this.luster = luster;
		this.cleavage = cleavage;
		this.mineralSize = mineralSize;
		this.otherFeatures = otherFeatures;
		this.size = size;
	}
	
	// Sedimentary constructor
	Sample(int generalType, String location, String rockName, String id, String color, String composition, String texture, String structures,
		   String rounding, String grainSize, String otherFeatures, String fossilDescription, boolean fossilContent, String size) {
		this.location = location;
		this.generalType = generalType;
		this.rockName = rockName;
		this.id = id;
		this.color = color;
		this.composition = composition;
		this.texture = texture;
		this.structures = structures;
		this.rounding = rounding;
		this.grainSize = grainSize;
		this.otherFeatures = otherFeatures;
		this.fossilContent = fossilContent;
		this.fossilDescription = fossilDescription;
		this.size = size;
	}
	
	int getGeneralType() {
		return generalType;
	}
	String getId() {
		return id;
	}
	String getComposition() {
		return composition;
	}
	String getTexture() {
		return texture;
	}	
	String getStructures() {
		return structures;
	}	
	String getRounding() {
		return rounding;
	}	
	String getLuster() {
		return luster;
	}	
	String getGrainSize() {
		return grainSize;
	}	
	String getCleavage() {
		return cleavage;
	}	
	String getMineralSize() {
		return mineralSize;
	}	
	boolean getFossilContent() {
		return fossilContent;
	}	
	String getSize() {
		return size;
	}	
}
