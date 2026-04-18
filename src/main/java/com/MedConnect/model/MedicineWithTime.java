package com.MedConnect.model;

import java.util.List;

public class MedicineWithTime {
    private String medicineName;
    private List<String> timeToTake;
    private String symptoms;
    private String diagnosis;

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
    public String getSymptoms() {
    return symptoms;
}

public void setSymptoms(String symptoms) {
    this.symptoms = symptoms;
}

public String getDiagnosis() {
    return diagnosis;
}

public void setDiagnosis(String diagnosis) {
    this.diagnosis = diagnosis;
}
}
