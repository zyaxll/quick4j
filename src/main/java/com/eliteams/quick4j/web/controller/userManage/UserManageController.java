package com.eliteams.quick4j.web.controller.userManage;

import com.eliteams.quick4j.core.message.SystemMessage;
import com.eliteams.quick4j.web.controller.common.BaseController;
import com.eliteams.quick4j.web.model.User;
import com.eliteams.quick4j.web.security.RoleSign;
import com.eliteams.quick4j.web.service.UserService;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

import javax.annotation.Resource;

/**
 * 用户操作controller
 * Created by zya on 16/10/24.
 */
@Controller
@RequestMapping(value = "/userManage")
public class UserManageController extends BaseController{

    @Resource
    private UserService userService;

    @RequestMapping(value = "/insert")
    @ResponseBody
    @RequiresRoles(value = RoleSign.ADMIN)
    public SystemMessage insert(User user){
        SystemMessage systemMessage = new SystemMessage();
        user.setCreateTime(new Date());
        user.setState("1");
        int num =  userService.insert(user);
        if(num > 0){
            systemMessage.setStatus(1);
            systemMessage.setMessage("用户添加成功");
        }else{
            systemMessage.setStatus(-1);
            systemMessage.setMessage("用户添加失败!");
        }
        return systemMessage;
    }

}
