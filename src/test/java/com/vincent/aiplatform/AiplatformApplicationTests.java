package com.vincent.aiplatform;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:testdb",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"app.jwt.secret=a-test-secret-that-is-longer-than-thirty-two-bytes"
})
class AiplatformApplicationTests {

	@Test
	void contextLoads() {
	}

}
