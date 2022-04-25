package com.otsi.retail.inventory.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Configuration
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Config {

	@Value("${getUserDetails_url}")
	private String userDetails;

}
