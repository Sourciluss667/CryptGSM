package fr.intech.cormand.cryptgsm;

public class Msg {
    private String address;
    private String body;
    private String date_sent;

    public Msg () {

    }

    public Msg (String address, String body, String date_sent) {
        this.address = address;
        this.body = body;
        this.date_sent = date_sent;
    }

    public String getAddress() {
        return address;
    }

    public String getBody() {
        return body;
    }

    public String getDate_sent() {
        return date_sent;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setDate_sent(String date_sent) {
        this.date_sent = date_sent;
    }
}
