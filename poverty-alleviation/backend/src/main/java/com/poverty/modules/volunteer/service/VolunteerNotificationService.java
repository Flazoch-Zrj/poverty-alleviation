package com.poverty.modules.volunteer.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.poverty.modules.volunteer.entity.VolunteerNotification;
import com.poverty.modules.volunteer.mapper.VolunteerNotificationMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VolunteerNotificationService extends ServiceImpl<VolunteerNotificationMapper, VolunteerNotification> {

    public long getUnreadCount(Long userId) {
        return count(new LambdaQueryWrapper<VolunteerNotification>()
                .eq(VolunteerNotification::getUserId, userId)
                .eq(VolunteerNotification::getIsRead, 0));
    }

    public List<VolunteerNotification> getMyNotifications(Long userId, Integer limit) {
        LambdaQueryWrapper<VolunteerNotification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VolunteerNotification::getUserId, userId);
        wrapper.orderByDesc(VolunteerNotification::getCreateTime);
        if (limit != null) wrapper.last("LIMIT " + limit);
        return list(wrapper);
    }

    public void markAsRead(Long notifId) {
        VolunteerNotification n = new VolunteerNotification();
        n.setNotifId(notifId);
        n.setIsRead(1);
        updateById(n);
    }

    public void markAllAsRead(Long userId) {
        List<VolunteerNotification> unread = list(new LambdaQueryWrapper<VolunteerNotification>()
                .eq(VolunteerNotification::getUserId, userId)
                .eq(VolunteerNotification::getIsRead, 0));
        for (VolunteerNotification n : unread) {
            n.setIsRead(1);
            updateById(n);
        }
    }
}
