package demo;

import java.security.Principal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
//@RestController
@Controller
@EnableDiscoveryClient
public class AdminApplication {
/*
  @GetMapping(value = "/{path:[^\\.]*}")
  public String redirect() {
    return "forward:/";
  }

  @GetMapping("/user")
  @ResponseBody
  public Map<String, Object> user(Principal user) {
    Map<String, Object> map = new LinkedHashMap<String, Object>();
    map.put("name", user.getName());
    map.put("roles", AuthorityUtils.authorityListToSet(((Authentication) user).getAuthorities()));
    return map;
  }

  public static void main(String[] args) {
    SpringApplication.run(AdminApplication.class, args);
  }

  @Configuration
  protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
      // @formatter:off
			http
				.httpBasic()
			.and()
				.authorizeRequests()
					.antMatchers("/index.html", "/").permitAll()
					.anyRequest().hasRole("ADMIN")
			.and()
				.csrf().disable();
			// @formatter:on
    }
  }*/

	  @GetMapping(value = "/{path:[^\\.]*}")
	  public String redirect() {
	      return "forward:/";
	  }

	  @RequestMapping("/user")
	  @ResponseBody
	  public Map<String, String> user(Principal user) {
	    return Collections.singletonMap("name", user.getName());
	  }

	  public static void main(String[] args) {
	    SpringApplication.run(AdminApplication.class, args);
	  }

	  @Configuration
	  @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
	  protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	    @Override
	    protected void configure(HttpSecurity http) throws Exception {
	      // @formatter:off
	      http
	        .httpBasic().and()
	        .authorizeRequests()
	          .antMatchers("/index.html", "/app.html", "/").permitAll()
	          .anyRequest().hasRole("USER");
	      // @formatter:on
	    }
	  }	
	
}
