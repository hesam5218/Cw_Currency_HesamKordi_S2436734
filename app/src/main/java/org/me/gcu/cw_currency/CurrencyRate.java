

package org.me.gcu.cw_currency;



public class CurrencyRate {


    private String title = "";

    private String link = "";

    private String pubDate = "";

    private String description = "";

    private String category = "";

    public CurrencyRate() {
    }




    //setter


    public void setTitle(String title) {

        this.title = title;
    }


    public void setLink(String link) {

        this.link = link;
    }


    public void setPubDate(String pubDate) {

        this.pubDate = pubDate;
    }


    public void setDescription(String description) {

        this.description = description;
    }



    public void setCategory(String category) {

        this.category = category;
    }




    //getter
    public String getTitle() {

        return title;
    }


    public String getLink() {

        return link;
    }


    public String getPubDate() {

        return pubDate;
    }


    public String getDescription() {
        return description;
    }


    public String getCategory() {
        return category;
    }



    @Override
    public String toString() {

        //to get currency code
        String codes = "";

        if (title.contains("(") && title.contains(")")) {

            String[] parts = title.split("\\)");

            StringBuilder sb = new StringBuilder();

            for (String part : parts) {

                if (part.contains("(")) {

                    sb.append(part.substring(part.indexOf("(") + 1)).append(" ");
                }
            }


            codes = sb.toString().trim();  // Example: "GBP USD"
        }



        return codes + "\n" + title + "\n" + description + "\n" + pubDate;
    }

}
