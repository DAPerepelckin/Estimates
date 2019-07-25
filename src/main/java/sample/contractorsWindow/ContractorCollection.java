package sample.contractorsWindow;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ContractorCollection {
    private ObservableList<ContractorRow> rowList = FXCollections.observableArrayList();

    void add(ContractorRow row){rowList.add(row);}

    ObservableList<ContractorRow> getRowList() {return rowList;}

    void remove(int index){
        rowList.remove(index);
    }
    void clear(){rowList.clear();}
}
