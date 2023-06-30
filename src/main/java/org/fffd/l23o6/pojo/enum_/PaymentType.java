package org.fffd.l23o6.pojo.enum_;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PaymentType {
    @JsonProperty("支付宝") Alipay("支付宝"), @JsonProperty("微信") WeChat("微信");

    public String text;

    PaymentType(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }
}
