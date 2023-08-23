import java.io.Serializable;

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
