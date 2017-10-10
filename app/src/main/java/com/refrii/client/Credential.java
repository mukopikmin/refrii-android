package com.refrii.client;

import java.util.Date;

/**
 * Created by yusuke on 2017/08/30.
 */

public class Credential {
    private String jwt;
    private Date expiresAt;

    public String getJwt() {
        return jwt;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }
}
