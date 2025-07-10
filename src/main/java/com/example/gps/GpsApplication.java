package com.example.gps;

import com.example.gps.model.PontosInteresse;
import com.example.gps.repositoy.PontosInteresseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GpsApplication implements CommandLineRunner {

	@Autowired
	private PontosInteresseRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(GpsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		repository.save(PontosInteresse.builder().nome("Lanchonete").x(27L).y(12L).build());
		repository.save(PontosInteresse.builder().nome("Posto").x(31L).y(18L).build());
		repository.save(PontosInteresse.builder().nome("Joalheria").x(15L).y(12L).build());
		repository.save(PontosInteresse.builder().nome("Floricultura").x(19L).y(21L).build());
		repository.save(PontosInteresse.builder().nome("Pub").x(12L).y(8L).build());
		repository.save(PontosInteresse.builder().nome("Supermercado").x(23L).y(6L).build());
		repository.save(PontosInteresse.builder().nome("Churrascaria").x(28L).y(2L).build());
	}
}
