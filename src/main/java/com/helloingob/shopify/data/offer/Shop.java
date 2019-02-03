package com.helloingob.shopify.data.offer;

public enum Shop {
    //@formatter:off
    ALDI("/images/aldi.png"),
    EDEKA("/images/edeka.png"),
    LIDL("/images/lidl.png"),
    REWE("/images/rewe.png"),
    PENNY("/images/penny.png"),
    SKY("/images/sky.png");
    //@formatter:on

    private final String imageSrc;

    Shop(final String imageSrc) {
        this.imageSrc = imageSrc;
    }

    public String getImageSrc() {
        return imageSrc;
    }

}
