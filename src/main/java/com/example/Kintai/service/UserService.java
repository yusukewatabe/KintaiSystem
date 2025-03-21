package com.example.Kintai.service;

import com.example.Kintai.model.User;
import com.example.Kintai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;

	public boolean authenticate(String id, String pass) {
		Optional<User> user = userRepository.findById(id);

		String decoded = null;
		// Base64デコード
		Base64.Encoder encoder = Base64.getEncoder();
		decoded = encoder.encodeToString(pass.getBytes(StandardCharsets.UTF_8));
		return user.isPresent() && user.get().getPassword().equals(decoded);
	}

	public boolean overridePassword(String id, String pass) {
		Optional<User> optionalUser = userRepository.findById(id);

		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			// トークン情報をDBへ保存
			user.setPassword(pass);
			userRepository.save(user);
			return true;
		} else {
			return false;
		}

	}
}