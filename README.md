# További Spring projektek

* Spring Modulith
* Spring Statemachine
* Spring Batch
* Spring Cloud Task

# Előkészítések

```shell
docker run -d -e POSTGRES_DB=employees -e POSTGRES_USER=employees -e POSTGRES_PASSWORD=employees -p 5432:5432  --name employees-postgres postgres
```

```shell
cd kafka
docker compose up -d
```

# Spring Modulith

https://www.jtechlog.hu/2022/12/19/spring-modulith.html

Modulok megalkotására megoldások:

* Csomagok használata
* Maven, Gradle multi-module project
* OSGi
* JPMS

Spring megoldás:

* Spring Modulith

Tulajdonságai:

* Csomagokra építkezik
* Tesztesetekkel ellenőrizhető
* Inspiráció: [ArchUnit](https://www.archunit.org/)

* `courses-init` projekt

```java
@Transactional
public void leave(LeaveResource leave) {

    courseRepository.findAll().forEach(course -> course.getEnrolledEmployees().remove(employee.getId()));
}
```

Funkcionálisan tökéletes, de:

* Modul körkörös függőség
* Demeter törvényének megsértése
* JPA N+1 probléma

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.modulith</groupId>
            <artifactId>spring-modulith-bom</artifactId>
            <version>1.1.4</version>
            <scope>import</scope>
            <type>pom</type>
        </dependency>
    </dependencies>
</dependencyManagement>
```

```xml
<dependency>
    <groupId>org.springframework.modulith</groupId>
    <artifactId>spring-modulith-core</artifactId>
</dependency>
```

```java
class ModuleIT {

    @Test
    void architecture() {
        var modules = ApplicationModules.of(CoursesApplication.class);
        modules.verify();
    }
}
```

```plain
org.springframework.modulith.core.Violations: - Cycle detected: Slice course -> 
                Slice employee -> 
                Slice course
```

## Dokumentáció

```xml
<dependency>
    <groupId>org.springframework.modulith</groupId>
    <artifactId>spring-modulith-docs</artifactId>
</dependency>
```

```java
new Documenter(modules)
                .writeModulesAsPlantUml()
                .writeIndividualModulesAsPlantUml();
```

`components.puml`

## További lehetőségek

* Application event és a tranzakció kapcsolata
* Event registry
* Event externalization
* Csak egy modul, kiválasztott modulok indítása tesztesetben
* Moments
    * [Patterns for Decoupling in Distributed Systems: Passage of Time Event](https://verraes.net/2019/05/patterns-for-decoupling-distsys-passage-of-time-event/)
* Tracing

# Spring Statemachine

* Véges automata, már 1940-ben
    * Állapotok halmaza
    * Inputok halmaza, ábécé
    * Átmeneti függvény
    * Kezdőállapot
    * Végállapotok halmaza
* UML állapotdiagram
    * PlantUML

Példák:

* Caps Lock, jelzőlámpa, lift, mosógép, megrendelés állapota, stb.

Microservice környezetben:

* Saga - Orchestration based

Spring Statemachine

* Túl sok feltétel kiváltására
* Átmenet (transition)
    * Forrás állapot (source state)
    * Cél állapot (target state)
    * Esemény (event)
    * Guard: feltétel
    * Action: művelet
* Hierarchikus állapotok
* Állapot régiók
* Builder pattern
* Distributed
* Event listeners
* Persistence
* Monitoring
* Tracing
* Eclipse Modeling Support
* Tesztelhetőség

Felépítés:

* Állapot: enum
* Esemény: enum

`enrollment.puml`

```
@startuml
'https://plantuml.com/state-diagram

hide empty description

[*] --> ENROLLED
ENROLLED --> COMPLETED: Complete
ENROLLED --> REFUSED: Refuse
COMPLETED --> [*]
REFUSED --> [*]
@enduml
```

```xml
<dependency>
    <groupId>org.springframework.statemachine</groupId>
    <artifactId>spring-statemachine-starter</artifactId>
    <version>4.0.0</version>
</dependency>
```

```java
@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<EnrollmentState, EnrollmentEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<EnrollmentState, EnrollmentEvent> states) throws Exception {
        states
                .withStates()
                .initial(EnrollmentState.ENROLLED)
                .states(EnumSet.allOf(EnrollmentState.class))
                .end(EnrollmentState.COMPLETED)
                .end(EnrollmentState.REFUSED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<EnrollmentState, EnrollmentEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(EnrollmentState.ENROLLED).event(EnrollmentEvent.COMPLETE).target(EnrollmentState.COMPLETED)
                .and()
                .withExternal()
                .source(EnrollmentState.ENROLLED).event(EnrollmentEvent.REFUSE).target(EnrollmentState.REFUSED);
    }

}
```

```java
private StateMachine<EnrollmentState, EnrollmentEvent> initStateMachine(long id, EnrollmentState enrollmentState) {
    var stateMachine = stateMachineFactory.getStateMachine(Long.toString(id));
    if (enrollmentState != null) {
        stateMachine.getStateMachineAccessor().doWithAllRegions(access ->
                access.resetStateMachineReactively(new DefaultStateMachineContext<>(enrollmentState, null, null, null)).subscribe());
    }
    stateMachine.startReactively().subscribe();

    return stateMachine;
}
```

```java
var stateMachine = initStateMachine(enrollment.getId(), enrollment.getEnrollmentState());
stateMachine
        .sendEvent(Mono.just(new GenericMessage<>(EnrollmentEvent.COMPLETE)))
        .subscribe();

log.info("New state: {}", stateMachine.getState().getId());
enrollment.setEnrollmentState(stateMachine.getState().getId());
```

## Listener

```java
stateMachine.addStateListener(new StateMachineListenerAdapter<>() {
    @Override
    public void stateEntered(State<EnrollmentState, EnrollmentEvent> state) {
        log.info("Enter state: {}", state.getId());
    }
});
```

# Spring Batch

Batch feldolgozás:

* Nagy tömegű adatfeldolgozás
* Ahol nincs szükség felhasználói beavatkozásra
* Tipikusan időzített indítás
* Különböző adatforrásokból dolgozik
* Különböző helyekre ír
* Lépésekből áll
* Tipikus példák: ETL, napi zárás

Spring Batch

* Deklaratív I/O
* Robosztus, hibatűrő megoldás
* Chunk based
* Nem scheduler, hanem együttműködik vele

https://docs.spring.io/spring-batch/reference/job.html

További lehetőségek:

* Tranzakciókezelés
* Start/stop/restart
* Retry/skip
* Partitioning
* Statisztikák
* Logging
* Tracing
* Tesztelhető

Projekt létrehozása: `employees-batch`
Függőségek: Lombok, Spring Data JPA, Postgres, Batch

[Mockaroo](https://www.mockaroo.com/)

* First Name, Last Name, Email address

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/employees
spring.datasource.username=employees
spring.datasource.password=employees

spring.batch.jdbc.initialize-schema=always
spring.batch.job.enabled=false

spring.jpa.generate-ddl=true
```

```java
@Configuration
//@EnableBatchProcessing: ha ezt használom, nem megy az application.properties-ben lévő konfigok felolvasása
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
```

```java
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
```

* Futtatás
* Adatbázis (diagram)

```java
.listener(new JobExecutionListener() {
    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("Job has finished: {}", jobExecution);
    }
})
```

## Enum

```java
public enum EnrollmentState {

    ENROLLED {
        @Override
        public EnrollmentState sendEvent(EnrollmentEvent event) {
            if (event == EnrollmentEvent.COMPLETE) {
                return EnrollmentState.COMPLETED;
            } else if (event == EnrollmentEvent.REFUSE) {
                return EnrollmentState.REFUSED;
            }
            return this;
        }
    }, REFUSED {
    }, COMPLETED {};

    public EnrollmentState sendEvent(EnrollmentEvent event) {
        return this;
    }

    public static EnrollmentState initial() {
        return ENROLLED;
    }
}
```

# Spring Cloud Task

## Spring Cloud Task használatba vétele

* Függőségek: Task, PostgreSQL Driver, Lombok

```java
@SpringBootApplication
@Slf4j
@EnableTask
public class TasksDemoApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(TasksDemoApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.debug("Random number: {}", new Random().nextInt(1, 7));
    }
}
```

```properties
logging.level.training=debug

spring.datasource.url=jdbc:postgresql://localhost:5432/employees
spring.datasource.username=employees
spring.datasource.password=employees
```

## Launcher használata

* Név: `task-launcher`
* Függőségek: Task, Cloud Stream, Spring for Apache Kafka, PostgreSQL
* `@EnableTaskLauncher`
* `pom.xml`

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-deployer-local</artifactId>
    <version>2.8.3</version>
</dependency>
```

* `application.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tasks
spring.datasource.username=employees
spring.datasource.password=employees
```

```json
{"applicationName": "tasks-demo", "uri": "maven:training:task-demo:0.0.1-SNAPSHOT", "commandlineArguments": []}
```

`Caused by: org.eclipse.aether.transfer.ArtifactNotFoundException: Could not find artifact training:task-demo:jar:0.0.1-SNAPSHOT`

* `TaskDemoApplicationTest` -> `TaskDemoApplicationIT`
* `mvnw install`