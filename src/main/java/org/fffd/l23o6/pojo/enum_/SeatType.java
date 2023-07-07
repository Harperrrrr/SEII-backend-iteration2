package org.fffd.l23o6.pojo.enum_;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SeatType {
    @JsonProperty("商务座") BUSINESS_SEAT("商务座"),
    @JsonProperty("一等座") FIRST_CLASS_SEAT("一等座"),
    @JsonProperty("二等座") SECOND_CLASS_SEAT("二等座"),
    @JsonProperty("硬座") HARD_SEAT("硬座"),
    @JsonProperty("软座") SOFT_SEAT("软座"),
    @JsonProperty("硬卧") HARD_SLEEP_SEAT("硬卧"),
    @JsonProperty("软卧") SOFT_SLEEP_SEAT("软卧");

    private String text;

    SeatType(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }
}
