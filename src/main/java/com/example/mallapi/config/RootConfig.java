package com.example.mallapi.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class RootConfig {
    
    @Bean
    public ModelMapper modelMapper () {

        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
            .setFieldMatchingEnabled(true)// 필드 맵핑 활성화
            .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)//  private 수준
            .setMatchingStrategy(MatchingStrategies.LOOSE);

        return modelMapper; // DTO -> Entity, Entity -> DTO 역할
    }


}
