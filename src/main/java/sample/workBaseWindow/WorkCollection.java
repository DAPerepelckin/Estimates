package sample.workBaseWindow;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

class WorkCollection {
    private ObservableList<WorkRow> rowList = FXCollections.observableArrayList();

    void add(WorkRow row){rowList.add(row);}

    ObservableList<WorkRow> getRowList() {return rowList;}

    void remove(int index){
        rowList.remove(index);
    }
    void clear(){rowList.clear();}

}
