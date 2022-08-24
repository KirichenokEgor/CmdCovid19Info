package org.study;

import org.study.entity.CountryCovidData;
import org.study.service.Covid19InfoService;
import org.study.utils.IOUtils;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        String country = IOUtils.getNotBlankLineFromCmd("Please, enter the country: ");

        Covid19InfoService infoService = null;
        try {
            infoService = new Covid19InfoService();
        } catch (IOException e) {
            System.out.println("Can't read properties");
            return;
        }

        //fill result object
        CountryCovidData ccd = infoService.getCountryCovidData(country);
        System.out.println(ccd);
    }
}
