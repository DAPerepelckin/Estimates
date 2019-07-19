package sample.workBaseWindow;

public class WorkRow {
        private int id;
        private String num;
        private String name;
        private String price;
        private String unit;

        public WorkRow(int id,String num,String name, String price, String unit) {
            this.id = id;
            this.num = num;
            this.name = name;
            this.price = price;
            this.unit = unit;
        }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }



}
