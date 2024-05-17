create table course (id bigint generated always as identity, name varchar(255), primary key (id));
create table course_enrolled_employees (courses_enrolled_id bigint not null, enrolled_employees_id bigint not null);
create table employee (id bigint generated always as identity, leaved_at date, name varchar(255), primary key (id));
alter table if exists course_enrolled_employees add constraint fk_employee foreign key (enrolled_employees_id) references employee;
alter table if exists course_enrolled_employees add constraint fk_course foreign key (courses_enrolled_id) references course;