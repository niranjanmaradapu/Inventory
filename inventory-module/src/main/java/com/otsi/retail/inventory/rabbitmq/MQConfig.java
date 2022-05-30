package com.otsi.retail.inventory.rabbitmq;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {

	public static final String inventory_queue = "inventory_queue";
	public static final String inventory_exchange = "inventory_exchange";
	public static final String inventory_rk = "inventory_rk ";
	
	//return slip
	@Value("${returnslip_queue}")
	private String returnSlipinventoryUpdateQueue;

	@Value("${returnslip_exchange}")
	private String returnSlipupdateInventoryExchange;

	@Value("${returnslip_rk}")
	private String returnSlipupdateInventoryRK;
	

	@Bean
	public Queue queue() {
		return new Queue(inventory_queue);
	}

	@Bean
	public DirectExchange exchange() {
		return new DirectExchange(inventory_exchange);
	}

	@Bean
	public Binding binding(Queue queue, DirectExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(inventory_rk);

	}

	
	@Bean
	public Queue returnSlipinventoryUpdateQueue() {
		return new Queue(returnSlipinventoryUpdateQueue);
	}

	@Bean
	public DirectExchange returnSlipupdateInventoryExchange() {
		return new DirectExchange(returnSlipupdateInventoryExchange);
	}

	@Bean
	public Binding bindingReturnslipUpdateInventory(Queue returnSlipinventoryUpdateQueue,
			DirectExchange returnSlipupdateInventoryExchange) {

		return BindingBuilder.bind(returnSlipinventoryUpdateQueue).to(returnSlipupdateInventoryExchange)
				.with(returnSlipupdateInventoryRK);
	}
	
	@Bean
	public MessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public AmqpTemplate template(ConnectionFactory connection) {
		RabbitTemplate template = new RabbitTemplate(connection);
		template.setMessageConverter(messageConverter());
		return template;

	}

}
