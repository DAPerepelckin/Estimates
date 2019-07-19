package sample.welcomeWindow;

public class WelcomeRow {
    private int id;
    private String date;
    private String name;
    private String sum;

    WelcomeRow(int id, String date, String name, String sum){
        this.id = id;
        this.date = date;
        this.name = name;
        this.sum = sum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }


}
