package worlds.server;

import java.io.IOException;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class RealtorVerification{
    /**
     * RequestVerificationToken, and EntityType were both part of form data when inspecting the code on the website
     * RequestVerificationToken likely for CSRF protection
     * EntityType likely refers to the user type for the serach
     */
    String RequestVerificationToken = "wA5ke9KMy0ZJxugGYZo1d2qj50W3Go0GiEWVKaqDPlMPsoqps66As1ZdnSAhMQs4R4iCRj1F_tMgVVrNOb4z9i9ER2Of4B0EZzQaZqH2mMc1";
    String EntityType = "RE-PR";
    String submit = "Search";
    String url = "https://ata.grec.state.ga.us/Account/Search";


    RealtorVerification(){}

    /**
     * POSTs and parses HTML response
     * @param firstName
     * @param lastName
     * @param realtorId
     * @return Boolean wheter or not the realtor's information has been verified
     * @throws IOException
     */
    public Boolean verifyUser(String firstName, String lastName, String realtorId) throws IOException {
        if (firstName == null || lastName == null || realtorId == null) {
            return false;
        }
        Document document = Jsoup.connect(url)
        .data("__RequestVerificationToken", RequestVerificationToken)
        .data("EntityType", EntityType)
        .data("Name", firstName)
        .data("LastName", lastName)
        .data("AuthorizationNumber", realtorId)
        .data("submit", submit)
        .post();

        //only the results table has this class styling for now 03/2020
        Elements tableRows = document.getElementsByClass("bg-light text-center");

        //check to see if no results
        if(tableRows.isEmpty()) {
            return false;
        }

        //should not have more than one result given unique realtor Id
        Element desiredRow = tableRows.first();

        //corresponds to license number field
        String licenseNumber = desiredRow.child(1).text();
        if (!licenseNumber.equals(realtorId)) {
            return false;
        }

        //corresponds to License Status field
        Element stillActive = desiredRow.child(3);
        String active = stillActive.text();
        return active.equals("ACTIVE");
    }
}