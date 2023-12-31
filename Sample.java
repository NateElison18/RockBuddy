import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * <h1>Sample</h1>
 * The Sample class creates Sample objects. Multiple constructors are available and used in the program: one that takes only the generalType, one with everything,
 * and a final that takes everything but the SamplePhotos array.
 *
 * <p>Last updated 9/12/23</p>
 *
 * @author Nate Elison
 */
public class Sample implements Serializable {
	int generalType; // 0 == sed, 1 == ign, 2 == meta, 3 == unknown
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
	ArrayList<SamplePhoto> samplePhotos = new ArrayList<SamplePhoto>();

	boolean fossilContent;
	String dateLogged;

	Sample(){}

	// Constructor for sending codes to the backend
	Sample(int code) {
		generalType = code;
	}
	// Constructor for everything
	Sample(int generalType, String rockName, String id, String location, String color, String composition, String texture, String structures, String rounding,
			String luster, String grainSize, String cleavage, String mineralSize, String otherFeatures, String fossilDescription, boolean fossilContent,
		   	String size, ArrayList<SamplePhoto> samplePhotos) {
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
		this.samplePhotos = samplePhotos;
		dateLogged = new Date().toString();

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
			case 3:
				rockTypeString = "Unknown";
				break;
		}


	}
	// Constructor w everything but the samplePhotos
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
		this.samplePhotos = samplePhotos;
		dateLogged = new Date().toString();

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
			case 3:
				rockTypeString = "Unknown";
				break;
		}
	}

	// Getters
	int getGeneralType() {
		return generalType;
	}
	public String getId() {
		return id;
	}
	public String getComposition() {
		return composition;
	}
	public String getTexture() {
		return texture;
	}
	public String getStructures() {
		return structures;
	}
	public String getRounding() {
		return rounding;
	}
	public String getLuster() {
		return luster;
	}
	public String getGrainSize() {
		return grainSize;
	}
	public String getCleavage() {
		return cleavage;
	}
	public String getMineralSize() {
		return mineralSize;
	}	
	boolean getFossilContent() {
		return fossilContent;
	}
	public String getSize() {
		return size;
	}
	public String getLocation() {
		return location;
	}
	public String getRockName() {
		return rockName;
	}
	public String getFossilDescription() {
		return fossilDescription;
	}
	public String getColor() {
		return color;
	}
	public String getOtherFeatures() {
		return otherFeatures;
	}
	public String getDateLogged() {
		return dateLogged;
	}
	public ArrayList<SamplePhoto> getSamplePhotos() {
		return samplePhotos;
	}

	public String getRockTypeString() {
		return rockTypeString;
	}
	public void setSamplePhotos(ArrayList<SamplePhoto> samplePhotos) {
		this.samplePhotos = samplePhotos;
	}
	public void setDateLogged(String dateLogged){
		this.dateLogged = dateLogged;
	}
}
