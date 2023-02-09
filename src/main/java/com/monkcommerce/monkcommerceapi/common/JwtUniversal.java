package com.monkcommerce.monkcommerceapi.common;

import com.monkcommerce.monkcommerceapi.constants.JwtS;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

public class JwtUniversal
{
    public static String getTokenFromHeaders()
    {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest().getHeader("Authorization");
    }
    public static String getRemovedAuthTypeTokenBearer()
    {
        String token = getTokenFromHeaders();
        token = token.replace(JwtS.Bearer,"");
        return token;
    }
}
