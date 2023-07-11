package com.example.pdftools.dto;

public class PageSwitch {

    private Integer fromIndex;
    private Integer toIndex;

    PageSwitch(Integer fromIndex, Integer toIndex) {
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    public Integer getFromIndex() {
        return fromIndex;
    }

    public void setFromIndex(Integer fromIndex) {
        this.fromIndex = fromIndex;
    }

    public Integer getToIndex() {
        return toIndex;
    }

    public void setToIndex(Integer toIndex) {
        this.toIndex = toIndex;
    }

}
