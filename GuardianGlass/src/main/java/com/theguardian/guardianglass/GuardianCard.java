package com.theguardian.guardianglass;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class GuardianCard {
    public final String uri;
    public final String title;
    public final String trailText;
    public final DisplayImage[] displayImages;

    @JsonCreator
    public GuardianCard(
            @JsonProperty("uri") String uri,
            @JsonProperty("title") String title,
            @JsonProperty("trailText") String trailText,
            @JsonProperty("displayImages") DisplayImage[] displayImages) {
        this.uri = uri;
        this.title = title;
        this.trailText = trailText;
        this.displayImages = displayImages;
    }

    public String getImageUri(){
        if(displayImages == null || displayImages.length == 0)
            return "";
        return displayImages[0].getUrl();
    }
}
