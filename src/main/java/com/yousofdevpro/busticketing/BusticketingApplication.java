package com.yousofdevpro.busticketing;

import com.yousofdevpro.busticketing.auth.model.Role;
import com.yousofdevpro.busticketing.auth.model.User;
import com.yousofdevpro.busticketing.auth.repository.UserRepository;
import com.yousofdevpro.busticketing.reservation.model.Bus;
import com.yousofdevpro.busticketing.reservation.model.Route;
import com.yousofdevpro.busticketing.reservation.repository.AppointmentRepository;
import com.yousofdevpro.busticketing.reservation.repository.BusRepository;
import com.yousofdevpro.busticketing.reservation.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class BusticketingApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(BusticketingApplication.class, args);
	}
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BusRepository busRepository;
	
	@Autowired
	private RouteRepository routeRepository;
	
	@Autowired
	private AppointmentRepository appointmentRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public void run(String... args) throws Exception {
		
		var admin = User.builder()
				.firstName("Mohamed")
				.lastName("Khalid")
				.phone("011457789")
				.email("mohamed@mail.com")
				.password(passwordEncoder.encode("123456"))
				.role(Role.valueOf("ADMIN"))
				.isConfirmed(true)
				.build();
		
		admin = userRepository.save(admin);
		
		var driver = User.builder()
				.firstName("Ramy")
				.lastName("Ahmed")
				.phone("015457789")
				.email("ramy@mail.com")
				.password(passwordEncoder.encode("123456"))
				.role(Role.valueOf("DRIVER"))
				.isConfirmed(true)
				.build();
		
		driver = userRepository.save(driver);
		
		// Creating Bus
		var bus = Bus.builder()
				.name("Blue bus")
				.busNumber("B-1")
				.totalSeats(5)
				.isActive(true)
				.build();
		
		bus = busRepository.save(bus);
		
		// Creating Route:
		var route = Route.builder()
				.departurePoint("Cairo")
				.destinationPoint("Luxor")
				.isActive(true)
				.build();
		
		route = routeRepository.save(route);
		
		// Creating an appointment:
		
	}
}
