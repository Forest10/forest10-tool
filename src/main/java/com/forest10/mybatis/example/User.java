package com.forest10.mybatis.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author Forest10
 * @date 2018/9/18 下午3:03
 */
@Data
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class User implements Serializable {

    private String name;
    private Integer age;
}
