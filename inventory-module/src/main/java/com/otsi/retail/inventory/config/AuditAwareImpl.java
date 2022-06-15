package com.otsi.retail.inventory.config;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component
public class AuditAwareImpl implements AuditorAware<Long> {

	@Autowired
	private HttpServletRequest request;

	@Override
	public Optional<Long> getCurrentAuditor() {
		String userId = request.getHeader("userId");
		if (StringUtils.isNotBlank(userId)) {
			return Optional.of(Long.valueOf(userId));
		}
		return Optional.empty();
	}

}
