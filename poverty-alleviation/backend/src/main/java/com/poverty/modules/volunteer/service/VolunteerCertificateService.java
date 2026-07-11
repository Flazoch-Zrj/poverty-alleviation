package com.poverty.modules.volunteer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.poverty.modules.volunteer.entity.VolunteerCertificate;

public interface VolunteerCertificateService extends IService<VolunteerCertificate> {

    /**
     * 为用户生成证书
     */
    VolunteerCertificate issueCertificate(Long userId, String certType, Long sourceId, String certName);
}
