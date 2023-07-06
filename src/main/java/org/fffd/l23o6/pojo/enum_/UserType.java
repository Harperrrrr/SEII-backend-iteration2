package org.fffd.l23o6.pojo.enum_;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum UserType {
    @JsonProperty("客户") USER("客户"), @JsonProperty("铁路管理员")TRAIN_MANAGER("铁路管理员"),
    @JsonProperty("票务员")TICKET_MANAGER("票务员"), @JsonProperty("余票管理员")REMAINING_TICKET_MANAGER("余票管理员");

    public String text;

    UserType(String text) {
        this.text = text;
    }

    public String getText(){
        return this.text;
    }
}
