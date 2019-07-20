package com.ccnu.bbs.forms;

import lombok.Data;

@Data
public class UserInfoForm {

    String signature;
    String rawData;
    String encryptedData;
    String iv;
}
