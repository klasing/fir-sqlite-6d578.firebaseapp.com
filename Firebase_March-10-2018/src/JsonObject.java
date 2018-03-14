package src;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "photoUrl",
    "text",
    "imageUrl",
    "name"
})
public class JsonObject {
    protected JsonObject() {
        Log.d(LOG_TAG, "<<constructor>> JsonObject()");
    }

    @JsonSetter("photoUrl")
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
    @JsonGetter("photoUrl")
    public String getPhotoUrl() {
        return photoUrl;
    }

    @JsonSetter("text")
    public void setText(String text) {
        this.text = text;
    }
    @JsonGetter("text")
    public String getText() {
        return text;
    }

    @JsonSetter("imageUrl")
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    @JsonGetter("imageUrl")
    public String getImageUrl() {
        return imageUrl;
    }

    @JsonSetter("name")
    public void setName(String name) {
        this.name = name;
    }
    @JsonGetter("name")
    public String getName() {
        return name;
    }

    @JsonIgnore()
    @Override
    public String toString() {
        return photoUrl + " " + text + " " + imageUrl + " " + name;
    }
    //************************************************************************
    //*                 declare
    //************************************************************************
    private static final String LOG_TAG =
        "*** " + JsonObject.class.getSimpleName();

    @JsonProperty("photoUrl")
    String photoUrl;

    @JsonProperty("text")
    String text;

    @JsonProperty("imageUrl")
    String imageUrl;

    @JsonProperty("name")
    String name;

}