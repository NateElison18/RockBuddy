import java.io.Serializable;
/**
 * <h1>SamplePhoto</h1>
 * The SamplePhoto class creates SamplePhotos, which are saved in an ArrayList in a Sample. SamplePhotos contain the file path to the image and the caption to that image.
 *
 * <p>Last updated 9/12/23</p>
 *
 * @author Nate Elison
 */
public class SamplePhoto implements Serializable {
    String photoPathName;
    String photoDescription;

    SamplePhoto(String photoPathName, String photoDescription) {
        this.photoDescription = photoDescription;
        this.photoPathName = photoPathName;
    }

    public String getPhotoDescription() {
        return photoDescription;
    }

    public String getPhotoPathName() {
        return photoPathName;
    }
}
