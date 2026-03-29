package com.MedConnect.doclogin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.MedConnect.doclogin.entity.Appointment;
import com.MedConnect.doclogin.entity.Medicine;
@Repository
public interface AppointmentsRepository  extends JpaRepository<Appointment,Long>
{

}
