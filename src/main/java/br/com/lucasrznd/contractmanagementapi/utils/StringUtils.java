package br.com.lucasrznd.contractmanagementapi.utils;


import br.com.lucasrznd.contractmanagementapi.entities.Contract;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public abstract class StringUtils {

    private final static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static String formatDateToLocal(LocalDate date) {
        return dtf.format(date);
    }

    public static String formatPhoneNumber(String phoneNumber) {
        return "(" + phoneNumber.substring(0, 2) + ") " + phoneNumber.charAt(2) + " " + phoneNumber.substring(3, 7) + "-" + phoneNumber.substring(7);
    }

    public static String currentDate() {
        Locale locale = new Locale("pt", "BR");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("'Jacarezinho,' dd 'de' MMMM 'de' yyyy", locale);

        return dtf.format(LocalDate.now());
    }

    public static String generateFileName(Contract contract, String pdfTarget) {
        LocalDate now = LocalDate.now();
        DateTimeFormatter fileNameDtf = DateTimeFormatter.ofPattern("dd_MM_yyyy");
        String date = fileNameDtf.format(now);

        String companyName = contract.getClientCompany().getTradeName().replaceAll("([^a-zA-Z]|\\s)+", "_");

        return pdfTarget + companyName.toLowerCase() + "_" + date + ".pdf";
    }

}
