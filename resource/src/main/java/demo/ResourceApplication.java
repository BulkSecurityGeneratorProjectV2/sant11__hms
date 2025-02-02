package demo;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.trace.TraceProperties;
import org.springframework.boot.actuate.trace.TraceRepository;
import org.springframework.boot.actuate.trace.WebRequestTraceFilter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@RestController
@EnableDiscoveryClient
@Slf4j
public class ResourceApplication extends WebSecurityConfigurerAdapter {

	private String message = "Hello World";
	private List<Change> changes = new ArrayList<>();

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public Message home() {
		log.info("getting message");
		
		String user = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
		
		Set<String> roles = AuthorityUtils.authorityListToSet( SecurityContextHolder.getContext().getAuthentication().getAuthorities());
		
		log.info("----------> getting message - logged user: {}, roles: {}", user, roles.stream().collect(Collectors.joining(",")));
		
		return new Message(message);
	}

	@RequestMapping(value = "/changes", method = RequestMethod.GET)
	public List<Change> changes() {
		return changes;
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public Message update(@RequestBody Message map, Principal principal) {
		if (map.getContent() != null) {
			message = map.getContent();
			changes.add(new Change(principal.getName(), message));
			while (changes.size() > 10) {
				changes.remove(0);
			}
		}
		return new Message(message);
	}

	public static void main(String[] args) {
		SpringApplication.run(ResourceApplication.class, args);
	}
	
    @Autowired
    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
      // @formatter:off
    	
			auth.inMemoryAuthentication()
				.withUser("user").password("password").roles("USER")
			.and()
				.withUser("admin").password("admin").roles("USER", "ADMIN", "READER", "WRITER")
			.and()
				.withUser("audit").password("audit").roles("USER", "ADMIN", "READER");
// @formatter:on
    }	

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// We need this to prevent the browser from popping up a dialog on a 401
		
//		 http.httpBasic().disable().authorizeRequests()
		http.httpBasic().and().authorizeRequests()
		  .antMatchers("/**").hasAnyRole("USER","ADMIN", "ROLE_WRITER")
	      .antMatchers(HttpMethod.POST, "/**").hasRole("WRITER").anyRequest()
	      .authenticated().and().csrf().disable();
		 
		/*
		http.httpBasic().disable().csrf()
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/**").hasRole("WRITER")
				.anyRequest().authenticated();
		*/
		
//		http.authorizeRequests().antMatchers("/**").permitAll();
	}

	@Bean
	public WebRequestTraceFilter webRequestLoggingFilter(ErrorAttributes errorAttributes,
			TraceRepository traceRepository, TraceProperties traceProperties) {
		WebRequestTraceFilter filter = new WebRequestTraceFilter(traceRepository,
				traceProperties);
		if (errorAttributes != null) {
			filter.setErrorAttributes(errorAttributes);
		}
		filter.setOrder(SecurityProperties.DEFAULT_FILTER_ORDER - 1);
		return filter;
	}
}

class Message {
	private String id = UUID.randomUUID().toString();
	private String content;

	Message() {
	}

	public Message(String content) {
		this.content = content;
	}

	public String getId() {
		return id;
	}

	public String getContent() {
		return content;
	}
}

class Change {
	private Date timestamp = new Date();
	private String user;
	private String message;

	Change() {
	}

	public Change(String user, String message) {
		this.user = user;
		this.message = message;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public String getUser() {
		return user;
	}

	public String getMessage() {
		return message;
	}
}