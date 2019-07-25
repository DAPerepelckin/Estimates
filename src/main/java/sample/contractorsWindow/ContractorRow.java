package sample.contractorsWindow;

public class ContractorRow {
    private int id;
    private String position;
    private String fio;
    private String orgName;
    private String address;
    private int okpo;

    public ContractorRow(int id, String position, String fio, String orgName, String address, int okpo) {
        this.id = id;
        this.position = position;
        this.fio = fio;
        this.orgName = orgName;
        this.address = address;
        this.okpo = okpo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getOkpo() {
        return okpo;
    }

    public void setOkpo(int okpo) {
        this.okpo = okpo;
    }
}
