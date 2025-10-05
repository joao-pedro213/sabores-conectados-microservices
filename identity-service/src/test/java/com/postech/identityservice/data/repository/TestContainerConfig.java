package com.postech.identityservice.data.repository;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class TestContainerConfig {

    public static final MongoDBContainer MONGO_DB_CONTAINER;

    static {
        MONGO_DB_CONTAINER = new MongoDBContainer(DockerImageName.parse("mongodb/mongodb-community-server:7.0-ubi8"));
        MONGO_DB_CONTAINER.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO_DB_CONTAINER::getReplicaSetUrl);
    }
}
