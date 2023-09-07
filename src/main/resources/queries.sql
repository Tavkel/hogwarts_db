select * from hogwarts.students;
select * from hogwarts.students where age between 12 and 15;
select name from hogwarts.students;
select * from hogwarts.students where lower(name) like '%f%';
select * from hogwarts.students where age < id order by id;
select * from hogwarts.students order by age, name;