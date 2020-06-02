package com.atguigu.crowd.mvc.handler;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.atguigu.crowd.entity.Auth;
import com.atguigu.crowd.entity.Role;
import com.atguigu.crowd.service.api.AdminService;
import com.atguigu.crowd.service.api.AuthService;
import com.atguigu.crowd.service.api.RoleService;
import com.atguigu.crowd.util.ResultEntity;

@Controller
public class AssignHandler {
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private AuthService authService;
	
	@PreAuthorize("hasAuthority('role:assign_auth')")
	@ResponseBody
	@RequestMapping("/assign/do/role/assign/auth.json")
	public ResultEntity<String> saveRoleAuthRelathinship(@RequestBody Map<String, List<Integer>> map) {
		authService.saveRoleAuthRelathinship(map);
		return ResultEntity.successWithoutData();
	}
	
	@PreAuthorize("hasAuthority('role:assign_auth')")
	@ResponseBody
	@RequestMapping("/assign/get/assigned/auth/id/by/role/id.json")
	public ResultEntity<List<Integer>> getAssignedAuthIdByRoleId(@RequestParam("roleId") Integer roleId) {
		List<Integer> authIdList = authService.getAssignedAuthIdByRoleId(roleId);
		return ResultEntity.successWithData(authIdList);
	}
	
	@PreAuthorize("hasAuthority('role:assign_auth')")
	@ResponseBody
	@RequestMapping("/assgin/get/all/auth.json")
	public ResultEntity<List<Auth>> getAllAuth() {
		List<Auth> authList = authService.getAll();
		return ResultEntity.successWithData(authList);
	}
	
	/**
	 * @param adminId
	 * @param pageNum
	 * @param keyword
	 * @param roleIdList 我们允许用户在页面上取消所有已分配角色再提交表单，所以可以不提供roleIdList请求参数，设置required=false表示这个请求参数不是必须的
	 * @return
	 */
	@PreAuthorize("hasAuthority('user:assign_role')")
	@RequestMapping("/assign/do/role/assign.html")
	public String saveAdminRoleRelationship(@RequestParam("adminId") Integer adminId, 
			@RequestParam("pageNum") Integer pageNum, 
			@RequestParam("keyword") String keyword, 
			@RequestParam(value="roleIdList", required=false) List<Integer> roleIdList) {
		adminService.saveAdminRoleRelationship(adminId, roleIdList);
		return "redirect:/admin/get/page.html?pageNum=" + pageNum + "&keyword=" + keyword;
	}
	
	@PreAuthorize("hasAuthority('user:assign_role')")
	@RequestMapping("/assign/to/assign/role/page.html")
	public String toAssignRolePage(@RequestParam("adminId") Integer adminId, ModelMap modelMap){
		// 1.查询已分配角色
		List<Role> assignedRoleList = roleService.getAssignedRole(adminId);
		// 2.查询未分配角色
		List<Role> unAssignedRoleList = roleService.getUnAssignedRole(adminId);
		// 3.存入模型，本质上其实是: request.setAttribute(attrName, attrValue);
		modelMap.addAttribute("assignedRoleList", assignedRoleList); 
		modelMap.addAttribute("unAssignedRoleList", unAssignedRoleList);
		return "assign-role";
	}
}
