package com.theguardian.guardianglass;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

public class GuardianGroup implements Serializable{
    public final GuardianCard[] cards;

    @JsonCreator
    public GuardianGroup(
            @JsonProperty("cards") GuardianCard[] cards
    ) {
        this.cards = cards;
    }
}
