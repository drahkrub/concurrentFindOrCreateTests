# Concurrent find-or-create tests in MySQL

See the comments below Vlad's answer to
[Find or insert based on unique key with Hibernate](https://stackoverflow.com/questions/5022812/find-or-insert-based-on-unique-key-with-hibernate/47095145#47095145)
on Stack Overflow.

I tried the solution proposed by Vlad Mihalcea, but I think the "find part"
is not really working this way.

* to run the tests you have to create a database 'demo' with user 'demo' and password 'demo'
(or alter `src/test/resources/application.properties`)

* run all the tests (meanwhile there are three) with: `mvn clean test`
(lot's of stuff is downloaded because `spring-boot-starter-data-jpa` etc. is used)

* the tests `testWithInsertOnDuplicateUpdate` and `testWithInsertIgnore` should fail

* the test `testWithTableLock` should succeed with some output similar to

<pre>
    Fred: no bean found by findByName("dummy")
    Fred: lock table
          Marcey: no bean found by findByName("dummy")
          Marcey: lock table
      Pamela: no bean found by findByName("dummy")
  Alice: no bean found by findByName("dummy")
      Pamela: lock table
  Alice: lock table
    Fred: lock aquired
    Fred: no bean found by second findByName("dummy")
    Fred: bean created!
    Fred: unlock table
          Marcey: lock aquired
          Marcey: found bean by second findByName("dummy")
          Marcey: unlock table
      Pamela: lock aquired
      Pamela: found bean by second findByName("dummy")
      Pamela: unlock table
  Alice: lock aquired
  Alice: found bean by second findByName("dummy")
  Alice: unlock table
Bob: found bean by findByName("dummy")
        Harvey: found bean by findByName("dummy")
</pre>

* Of course table locking has its price, but in a situation as described in
[Find or insert based on unique key with Hibernate](https://stackoverflow.com/questions/5022812/find-or-insert-based-on-unique-key-with-hibernate/47095145#47095145)
its a viable solution, I think.
