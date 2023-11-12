package com.teamProj.serviceImpl;

import com.teamProj.entity.LoginUser;
import com.teamProj.service.AdministratorService;
import com.teamProj.utils.HttpResult;
import com.teamProj.utils.JwtUtil;
import com.teamProj.utils.RedisCache;
import com.teamProj.utils.ResultCodeEnum;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class AdministratorImpl implements AdministratorService {
    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private RedisCache redisCache;

    public HttpResult administratorLogin(String account, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(account, password);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if (Objects.isNull(authenticate)) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND);
        }
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = String.valueOf(loginUser.getUser().getUserId());
        String jwt = JwtUtil.createJWT(userId);
        Map<String, String> map = new HashMap<>();
        map.put("token", jwt);
        redisCache.setCacheObject(userId, loginUser);
        return HttpResult.success(map, "登录成功");
    }

    public HttpResult administratorLogout() {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authenticationToken.getPrincipal();
        int adminId = loginUser.getUser().getUserId();
        redisCache.deleteObject(String.valueOf(adminId));
        return HttpResult.success(null, "用户注销");
    }
}
