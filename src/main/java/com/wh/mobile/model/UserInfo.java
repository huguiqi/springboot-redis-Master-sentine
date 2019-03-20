package com.wh.mobile.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by sam on 2019/3/17.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo implements Serializable{
    private String name;
    private String sex;
    private String age;
    private String size;

    private Integer gradeLevel;


}
