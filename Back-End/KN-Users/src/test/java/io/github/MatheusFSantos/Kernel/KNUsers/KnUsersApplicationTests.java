package io.github.MatheusFSantos.Kernel.KNUsers;

import io.github.MatheusFSantos.Kernel.KNUsers.model.entity.Users;
import io.github.MatheusFSantos.Kernel.KNUsers.model.enumeration.Roles;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
class KnUsersApplicationTests {

	@Test
	void rolesTest() {
		Users user = new Users(UUID.randomUUID(), "Matheus Ferreira Santos", "@matheus_jjson", "matheus.fs.contato@gmail.com", "d94ed3838", "São Paulo/SP", "Back-end developer júnior", Roles.ADMIN);

		System.out.println("Debug output :>> " + user);
		Assertions.assertNotNull(user.getRoles());
	}

	@Test
	void updateNicknameInstanceMethodTest() {
		Users user = new Users(UUID.randomUUID(), "Matheus Ferreira Santos", "@matheus_jjson", "matheus.fs.contato@gmail.com", "d94ed3838", "São Paulo/SP", "Back-end developer júnior", Roles.ADMIN);
		Users user2 = new Users(UUID.randomUUID(), "Matheus Ferreira Santos", "matheus_jjson", "matheus.fs.contato@gmail.com", "d94ed3838", "São Paulo/SP", "Back-end developer júnior", Roles.ADMIN);

		System.out.println("Debug output :>> User 1 nickname: " + user.getNickname() + ", User 2 nickname: " + user2.getNickname());
		Assertions.assertEquals(user.getNickname(), user2.getNickname());
	}

}
