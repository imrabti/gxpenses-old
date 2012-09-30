package com.nuvola.gxpenses.shared.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PageRequest implements Dto {

    private Integer pageNumber;
    private Integer length;

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

}
