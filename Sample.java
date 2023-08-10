
public class Sample {
	int generalType;
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

	boolean fossilContent;
	double size;
	
	// Constructor for everything
	Sample(int generalType, String rockName, String id, String color, String composition, String texture, String structures, String rounding, 
			String luster, String grainSize, String cleavage, String mineralSize, String otherFeatures, String fossilDescription, boolean fossilContent, double size) {
		this.generalType = generalType;
		this.rockName = rockName;
		this.id = id;
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
	}
	
	// Igneous constructor
	Sample(int generalType, String rockName, String id, String color, String composition, String texture, 
			String structures, String luster, String cleavage, String mineralSize, String otherFeatures, double size) {
		this.generalType = generalType;
		this.rockName = rockName;
		this.id = id;
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
	Sample(int generalType, String rockName, String id, String color, String composition, String texture, String structures, String rounding, String grainSize, String otherFeatures, String fossilDescription, boolean fossilContent, double size) {
		
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
	double getSize() {
		return size;
	}	
}
