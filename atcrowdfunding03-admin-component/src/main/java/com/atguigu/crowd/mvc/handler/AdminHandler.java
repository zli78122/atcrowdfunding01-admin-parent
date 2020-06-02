package com.atguigu.crowd.mvc.handler;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.atguigu.crowd.constant.CrowdConstant;
import com.atguigu.crowd.entity.Admin;
import com.atguigu.crowd.service.api.AdminService;
import com.github.pagehelper.PageInfo;

@Controller
public class AdminHandler {

	@Autowired
	private AdminService adminService;
	
	@PreAuthorize("hasAuthority('user:update')")
	@RequestMapping("/admin/update.html")
	public String update(Admin admin, @RequestParam("pageNum") Integer pageNum, @RequestParam("keyword") String keyword) {
		adminService.update(admin);
		return "redirect:/admin/get/page.html?pageNum=" + pageNum + "&keyword=" + keyword;
	}
	
	@PreAuthorize("hasAuthority('user:update')")
	@RequestMapping("/admin/to/edit/page.html")
	public String toEditPage(@RequestParam("adminId") Integer adminId, ModelMap modelMap) {
		
		// 1.根据adminId查询Admin对象
		Admin admin = adminService.getAdminById(adminId);
		
		// 2.将Admin对象存入模型
		modelMap.addAttribute("admin", admin);
		
		return "admin-edit";
	}
	
	@PreAuthorize("hasAuthority('user:add')")
	@RequestMapping("/admin/save.html")
	public String save(Admin admin) {
		adminService.saveAdmin(admin);
		return "redirect:/admin/get/page.html?pageNum=" + Integer.MAX_VALUE;
	}
	
	@PreAuthorize("hasAuthority('user:delete')")
	@RequestMapping("/admin/remove/{adminId}/{pageNum}/{keyword}.html")
	public String remove(@PathVariable("adminId") Integer adminId, @PathVariable("pageNum") Integer pageNum, 
			@PathVariable("keyword") String keyword) {
	
		// 执行删除
		adminService.remove(adminId);
		
		// 重定向到 /admin/get/page.html
		// 同时为了保持原本所在的页面和查询关键词，需要再附加pageNum和keyword两个请求参数
		return "redirect:/admin/get/page.html?pageNum=" + pageNum + "&keyword=" + keyword;
	}
	
	/**
	 * 使用@RequestParam注解的defaultValue属性，指定默认值，在请求中没有携带对应参数时使用默认值
	 * 
	 * @param keyword 默认值使用空字符串，和SQL语句配合使用实现 两种情况(keyword有值 和 keyword无值) 的适配
	 * @param pageNum 默认值使用1
	 * @param pageSize 默认值使用5
	 * @param modelMap
	 * @return
	 */
	@PreAuthorize("hasAuthority('user:get')")
	@RequestMapping("/admin/get/page.html")
	public String getPageInfo(@RequestParam(value="keyword", defaultValue="") String keyword, 
			@RequestParam(value="pageNum", defaultValue="1") Integer pageNum,
			@RequestParam(value="pageSize", defaultValue="5") Integer pageSize,
			ModelMap modelMap) {
		
		// 调用Service方法获取PageInfo对象
		PageInfo<Admin> pageInfo = adminService.getPageInfo(keyword, pageNum, pageSize);
		
		// 将PageInfo对象存入模型
		modelMap.addAttribute(CrowdConstant.ATTR_NAME_PAGE_INFO, pageInfo);
		
		return "admin-page";
	}
	
	@RequestMapping("/admin/do/logout.html")
	public String doLogout(HttpSession session) {
		
		// 强制Session失效
		session.invalidate();
		
		return "redirect:/admin/to/login/page.html";
	}

	@RequestMapping("/admin/do/login.html")
	public String doLogin(@RequestParam("loginAcct") String loginAcct, @RequestParam("userPswd") String userPswd,
			HttpSession session) {

		// 调用Service方法执行登录检查
		// 这个方法如果能够返回admin对象说明登录成功，如果账号、密码不正确则会抛出异常
		Admin admin = adminService.getAdminByLoginAcct(loginAcct, userPswd);

		// 将登录成功返回的admin对象存入Session域
		session.setAttribute(CrowdConstant.ATTR_NAME_LOGIN_ADMIN, admin);

		return "redirect:/admin/to/main/page.html";
	}
}
