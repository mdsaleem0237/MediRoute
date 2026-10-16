package project;

public class HospitalRequest {
    int hospitalId;
    String drugName;
    int monthlyQuota;
    int fulfilledUnits;

    public HospitalRequest(int hospitalId, String drugName, int monthlyQuota) {
        this.hospitalId = hospitalId;
        this.drugName = drugName;
        this.monthlyQuota = monthlyQuota;
        this.fulfilledUnits = 0;
    }
}
