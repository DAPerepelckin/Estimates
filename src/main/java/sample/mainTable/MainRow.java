package sample.mainTable;

public class MainRow {
    private Integer num;
    private String name;
    private String unit;
    private String count;
    private String price;
    private String sum;
    private String comment;

    MainRow(Integer num,String name, String unit, String count, String price, String sum, String comment) {
        this.num = num;
        this.name = name;
        this.unit = unit;
        this.count = count;
        this.price = price;
        this.sum = sum;
        this.comment = comment;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

