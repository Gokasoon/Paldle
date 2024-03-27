package uqac.dim.pallll;

import java.util.List;

public class Pal {
    private String key;
    private String name;
    private String description;
    private List<String> types;
    private String image;
    private String silhouette;
    private String size;


    public Pal(String key, String name, String description, List<String> types, String image, String silhouette, String size) {
        this.key = key;
        this.name = name;
        this.description = description;
        this.types = types;
        this.image = image;
        this.silhouette = silhouette;
        this.size = size;
    }

    public Pal() {
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSilhouette() {
        return silhouette;
    }

    public void setSilhouette(String silhouette) {
        this.silhouette = silhouette;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
