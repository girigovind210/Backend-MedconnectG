package com.MedConnect.model;

import java.util.List;

public class MedicineWithTime {
    private String medicineName;
    private List<String> timeToTake;

   public MedicineWithTime() {}

    // All-args constructor
    public MedicineWithTime(String medicineName, List<String> timeToTake) {
        this.medicineName = medicineName;
        this.timeToTake = timeToTake;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public List<String> getTimeToTake() {
        return timeToTake;
    }

    public void setTimeToTake(List<String> timeToTake) {
        this.timeToTake = timeToTake;
    }
}
