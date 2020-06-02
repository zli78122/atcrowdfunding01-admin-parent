package com.atguigu.crowd.mvc.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.atguigu.crowd.constant.CrowdConstant;

// 表示当前类是一个配置类
@Configuration
// 启用Web环境下权限控制功能
@EnableWebSecurity
// 启用全局方法权限控制功能，并且设置prePostEnabled = true，保证@PreAuthority、@PostAuthority、@PreFilter、@PostFilter生效
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebAppSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Override
	protected void configure(AuthenticationManagerBuilder builder) throws Exception {
		// 基于内存的认证
//		builder.inMemoryAuthentication().withUser("zhengyu").password("123456").roles("ADMIN");
		
		// 基于数据库的认证
		builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
	}
	
	@Override
	protected void configure(HttpSecurity security) throws Exception {
		security
			.authorizeRequests()						// 对请求进行授权
			.antMatchers("/admin/to/login/page.html")	// 对登录页进行设置
			.permitAll()								// 无条件访问
			.antMatchers("/bootstrap/**")				// 对静态资源进行设置
			.permitAll()                    			// 无条件访问
			.antMatchers("/crowd/**")       			// 对静态资源进行设置
			.permitAll()                    			// 无条件访问
			.antMatchers("/css/**")         			// 对静态资源进行设置
			.permitAll()                    			// 无条件访问
			.antMatchers("/fonts/**")       			// 对静态资源进行设置
			.permitAll()                    			// 无条件访问
			.antMatchers("/img/**")         			// 对静态资源进行设置
			.permitAll()                    			// 无条件访问
			.antMatchers("/jquery/**")      			// 对静态资源进行设置
			.permitAll()                    			// 无条件访问
			.antMatchers("/layer/**")       			// 对静态资源进行设置
			.permitAll()                    			// 无条件访问
			.antMatchers("/script/**")      			// 对静态资源进行设置
			.permitAll()                    			// 无条件访问
			.antMatchers("/ztree/**")       			// 对静态资源进行设置
			.permitAll()								// 无条件访问
			.antMatchers("/admin/to/add/page.html")		// 对admin-add.jsp页面进行设置
			.hasAuthority("user:add")					// 要求具备“user:add”权限
			.anyRequest()								// 对其他任意请求进行设置
			.authenticated()							// 认证后访问
			.and()
			.exceptionHandling()								// 指定异常处理器
			.accessDeniedHandler(new AccessDeniedHandler() {	// 访问被拒绝时触发的handler
				@Override
				public void handle(HttpServletRequest request, HttpServletResponse response,
						AccessDeniedException accessDeniedException) throws IOException, ServletException {
					request.setAttribute("exception", new Exception(CrowdConstant.MESSAGE_ACCESS_DENIED));
					request.getRequestDispatcher("/WEB-INF/system-error.jsp").forward(request, response);
				}
			})
			.and()
			.csrf()											// Cross-Site Request Forgery (CSRF)
			.disable()										// 禁用CSRF
			.formLogin()									// 开启表单登录的功能
			.loginPage("/admin/to/login/page.html")			// 指定登录页面
			.loginProcessingUrl("/security/do/login.html")	// 指定处理登录请求的地址
			.defaultSuccessUrl("/admin/to/main/page.html")	// 指定登录成功后前往的地址
			.usernameParameter("loginAcct")					// 账号的请求参数名称
			.passwordParameter("userPswd")					// 密码的请求参数名称
			.and()
			.logout()										// 开启退出登录功能
			.logoutUrl("/seucrity/do/logout.html")			// 指定退出登录地址
			.logoutSuccessUrl("/admin/to/login/page.html")	// 指定退出成功以后前往的地址
			;
	}
}
