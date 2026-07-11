package com.poverty.modules.volunteer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.poverty.modules.volunteer.entity.VolunteerCertificate;
import com.poverty.modules.volunteer.mapper.VolunteerCertificateMapper;
import com.poverty.modules.volunteer.service.VolunteerCertificateService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class VolunteerCertificateServiceImpl extends ServiceImpl<VolunteerCertificateMapper, VolunteerCertificate> implements VolunteerCertificateService {

    @Override
    public VolunteerCertificate issueCertificate(Long userId, String certType, Long sourceId, String certName) {
        VolunteerCertificate cert = new VolunteerCertificate();
        cert.setUserId(userId);
        cert.setCertType(certType);
        cert.setSourceId(sourceId);
        cert.setCertName(certName);
        cert.setCertNumber("VOL-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        cert.setIssueDate(LocalDate.now());
        save(cert);
        return cert;
    }
}
