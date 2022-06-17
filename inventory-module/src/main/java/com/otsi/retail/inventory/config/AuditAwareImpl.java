package com.otsi.retail.inventory.config;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component
public class AuditAwareImpl implements AuditorAware<Long> {

	private final Logger log = LogManager.getLogger(AuditAwareImpl.class);

	@Autowired
	private HttpServletRequest request;

	@Override
	public Optional<Long> getCurrentAuditor() {
		String userId = null;
		try {
			userId = request.getHeader("userId");
		} catch (Exception ex) {
			log.error("exception in current auditor " + ex);
		}
		if (StringUtils.isNotBlank(userId)) {
			return Optional.of(Long.valueOf(userId));
		}
		return Optional.empty();
	}

}
