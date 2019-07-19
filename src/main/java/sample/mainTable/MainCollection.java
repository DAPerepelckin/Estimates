package sample.mainTable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

class MainCollection {
    private ObservableList<MainRow> rowList = FXCollections.observableArrayList();

    void add(MainRow row){rowList.add(row);}

    ObservableList<MainRow> getRowList() {return rowList;}

    void clear(){rowList.clear();}

    void updateCosts(String price){
        rowList.get(0).setPrice(price);
        rowList.get(1).setPrice(price);
    }

    double getSum(){
        double k = 0.0;
        for (MainRow mainRow : rowList) {
            if (!mainRow.getSum().isEmpty()) {
                k += Double.parseDouble(mainRow.getSum().replaceAll(",", "."));
            }
        }
        return k;
    }




}
