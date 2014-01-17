package com.theguardian.guardianglass;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;
import java.util.Locale;

public class GuardianCard {
    public final String uri;
    public final String title;
    public final String trailText;
    public final DisplayImage[] displayImages;
    public final Date lastModified;

    @JsonCreator
    public GuardianCard(
            @JsonProperty("uri") String uri,
            @JsonProperty("title") String title,
            @JsonProperty("trailText") String trailText,
            @JsonProperty("displayImages") DisplayImage[] displayImages,
            @JsonProperty("lastModified") Date lastModified) {
        this.uri = uri;
        this.title = title;
        this.trailText = trailText;
        this.displayImages = displayImages;
        this.lastModified = lastModified;
    }

    public String getImageUri(){
        if(displayImages == null || displayImages.length == 0)
            return "";
        return displayImages[0].getUrl();
    }

    public String getDisplayTime(){
        public static String prettyFormatTime(Date time) {
            PrettyTime pt = new PrettyTime(new Locale("gdn"));
            return pt.format(time);
        }
    }
}
