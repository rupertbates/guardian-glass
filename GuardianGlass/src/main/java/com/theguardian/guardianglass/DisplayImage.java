package com.theguardian.guardianglass;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

public class DisplayImage implements Serializable {

    private static final String BASE_IMAGE_URL = "http://images.mobile-apps.guardianapis.com/450/270";


    public final String id;
    public final int height;
    public final int width;
    public final String orientation;
    public final String caption;
    public final String credit;

    @JsonCreator
    public DisplayImage(@JsonProperty("id") String id,
                        @JsonProperty("height") int height,
                        @JsonProperty("width") int width,
                        @JsonProperty("orientation") String orientation,
                        @JsonProperty("caption") String caption,
                        @JsonProperty("credit") String credit) {
        this.id = id;
        this.height = height;
        this.width = width;
        this.orientation = orientation;
        this.caption = caption;
        this.credit = credit;
    }

    @JsonIgnore
    public String getUrl() {
        return BASE_IMAGE_URL + id;
    }
}

