package project;

public class MedicineBatch {
    String batchCode;
    String drugName;
    int quantity;
    String expiryDate;
    int warehouseId;
    boolean quarantined;

    public MedicineBatch(String batchCode, String drugName, int quantity, String expiryDate, int warehouseId) {
        this.batchCode = batchCode;
        this.drugName = drugName;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.warehouseId = warehouseId;
        this.quarantined = false;
    }
}
