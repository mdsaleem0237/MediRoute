package project;

public class ShipmentRecord {
    String shipmentId;
    String fromFacility;
    String toHospital;
    String drugName;
    int units;
    String status;

    public ShipmentRecord(String shipmentId, String fromFacility, String toHospital, String drugName, int units, String status) {
        this.shipmentId = shipmentId;
        this.fromFacility = fromFacility;
        this.toHospital = toHospital;
        this.drugName = drugName;
        this.units = units;
        this.status = status;
    }
}
