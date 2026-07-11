package com.poverty.modules.poverty.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("family_member")
public class FamilyMember implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long memberId;

    private Long familyId;

    private String name;

    private String relationship;

    private String idCard;

    private String phone;

    private String gender;

    private Integer age;

    private String healthStatus;

    private Integer hasJob;

    private String education;

    private String remarks;
}
