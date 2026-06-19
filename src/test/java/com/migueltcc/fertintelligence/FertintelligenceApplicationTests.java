package com.migueltcc.fertintelligence;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(resolver = TestProfileResolver.class)
class FertintelligenceApplicationTests {

	@Test
	void contextLoads() {
	}

}
