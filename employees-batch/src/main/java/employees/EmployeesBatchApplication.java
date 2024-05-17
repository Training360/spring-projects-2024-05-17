package employees;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;

@SpringBootApplication
@AllArgsConstructor
public class EmployeesBatchApplication implements CommandLineRunner {

	private JobLauncher jobLauncher;

	private Job job;


	public static void main(String[] args) {
		SpringApplication.run(EmployeesBatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		jobLauncher
				.run(job, new JobParametersBuilder().addLocalDateTime("when", LocalDateTime.now()).toJobParameters());
	}
}
