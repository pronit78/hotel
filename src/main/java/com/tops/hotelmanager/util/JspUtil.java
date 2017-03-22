package com.tops.hotelmanager.util;

import org.springframework.stereotype.Component;

@Component
public class JspUtil {

    public String getImageName(String name) {
        return name.toLowerCase().replaceAll(" ", "-").replaceAll("[^a-z0-9 -]", "");
    }
}