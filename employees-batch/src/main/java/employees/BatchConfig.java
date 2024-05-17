package employees;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean
    public ItemReader<EmployeeDto> itemReader() {
        return new FlatFileItemReaderBuilder<EmployeeDto>()
                .name("employees-file-reader")
                .resource(new FileSystemResource("./MOCK_DATA.csv"))
                .delimited()
                .names("firstName", "lastName", "email")
                .targetType(EmployeeDto.class)
                .build();
    }

    @Bean
    public ItemProcessor<EmployeeDto, Employee> itemProcessor() {
        return item -> new Employee(item.firstName() + " " + item.lastName(), item.email());
    }

    @Bean
    public ItemWriter<Employee> itemWriter() {
        return new RepositoryItemWriterBuilder<Employee>()
                .repository(employeeRepository)
                .build();
    }

    @Bean
    public Step step() {
        return new StepBuilder("read-and-save-step", jobRepository)
                .<EmployeeDto, Employee>chunk(10, transactionManager)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job job() {
        return new JobBuilder("read-and-save-job", jobRepository)
                .start(step())
                .build();
    }
}
