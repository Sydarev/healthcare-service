package ru.netology.patient;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.service.alert.SendAlertServiceImpl;
import ru.netology.patient.service.medical.MedicalService;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TestMedicalService {
    @Test
    public void test_Medical_Services() {
        PatientInfo patientInfo = new PatientInfo("Иван", "Петров", LocalDate.of(1980, 11, 26),
                new HealthInfo(new BigDecimal("36.65"), new BloodPressure(120, 80)));
        PatientInfoFileRepository patientInfoFileRepository = Mockito.mock(PatientInfoFileRepository.class);
//        PatientInfoFileRepository patientInfoFileRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoFileRepository.getById(patientInfo.getId()))
                .thenReturn(patientInfo);

        SendAlertServiceImpl sender = Mockito.mock(SendAlertServiceImpl.class);
        MedicalService medicalService = new MedicalServiceImpl(patientInfoFileRepository, sender);

        BigDecimal currentTemperature = new BigDecimal("37.9");
        BloodPressure bloodPressure =  new BloodPressure(60, 120);
        Assertions.assertDoesNotThrow(() -> medicalService.checkTemperature(patientInfo.getId(), currentTemperature));
        Mockito.verify(sender, Mockito.never()).send(String.format("Warning, patient with id: %s, need help", patientInfo.getId()));
        Assertions.assertDoesNotThrow(() -> medicalService.checkBloodPressure(patientInfo.getId(), bloodPressure));
        Mockito.verify(sender, Mockito.atLeastOnce()).send(String.format("Warning, patient with id: %s, need help", patientInfo.getId()));
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(sender).send(argumentCaptor.capture());
        Assertions.assertEquals(String.format("Warning, patient with id: %s, need help", patientInfo.getId()) , argumentCaptor.getValue());


    }
}
