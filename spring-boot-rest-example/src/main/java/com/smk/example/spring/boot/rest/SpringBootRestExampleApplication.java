package com.smk.example.spring.boot.rest;

import java.util.Arrays;

import javax.json.JsonPatch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;

@SpringBootApplication
@EnableJpaAuditing
public class SpringBootRestExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootRestExampleApplication.class, args);
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();
		return mapper;
	}
	
	@Bean
	public OpenAPI customOpenAPI(@Value("${application-description}") String appDesciption, @Value("${application-version}") String appVersion) {
		ArraySchema patchRequest = new ArraySchema();
		
		StringSchema pathSchema = new StringSchema();
		pathSchema.example("/pages");
		pathSchema.setDescription("A JSON Pointer to the field to patch");
		ObjectSchema valueSchema = new ObjectSchema();
		valueSchema.example(120);
		valueSchema.setDescription("The value to be used within the operations");
		Schema<JsonPatch.Operation> opSchema = new Schema<JsonPatch.Operation>();
		opSchema.setType("string");
		opSchema.setEnum(Arrays.asList(JsonPatch.Operation.values()));
		opSchema.setDescription("The operation to be performed");
		StringSchema fromSchema = new StringSchema();
		fromSchema.setDescription("A string containing a JSON Pointer value");
		
		@SuppressWarnings("unchecked")
		Schema<String> patchDocument = new Schema<String>()
                .addProperties("path",pathSchema)
                .addProperties("value",valueSchema)
                .addProperties("op", opSchema)
                .addProperties("from", fromSchema)
                .required(Arrays.asList("op","path"))
                .description("A JSONPatch document as defined by RFC 6902");
		
		patchRequest.setItems(patchDocument);
		
		return new OpenAPI().info(new Info().title("Book Service API").version(appVersion).description(appDesciption)).components(new Components()
                .addSchemas("JSONPatch" , patchRequest));
	}
	
}
