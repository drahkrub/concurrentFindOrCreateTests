# onDuplicateKeyUpdate

See the comments below [Find or insert based on unique key with Hibernate](https://stackoverflow.com/questions/5022812/find-or-insert-based-on-unique-key-with-hibernate/47095145#47095145)
on Stack Overflow. I tried the solution proposed by Vlad Mihalcea, but I think
the "find part" is not really working this way, at least if MySQL 5.5.x is used.

* create a database 'demo' with user 'demo' and password 'demo', see `src/test/resources/application.properties`

* run all the tests (meanwhile there are two ;-)) with: `mvn clean test`

* the tests should fail with some output similar to the following:

<pre>
### testConcurrentFindOrCreateWithInsertIgnore ###
    Alice: no bean found by findByName("dummy")
Bob: no bean found by findByName("dummy")
    Alice: numChanged: 1
    Alice: bean was created.
    Alice: found bean by findByName("dummy")
Bob: numChanged: 0
Bob: bean was created in some other thread.
Bob: no bean found by findByName("dummy")

### testConcurrentFindOrCreate ###
    Alice: no bean found by findByName("dummy")
Bob: no bean found by findByName("dummy")
    Alice: numChanged: 1
    Alice: bean was created with id: 21
    Alice: found bean by findOne(21)
Bob: numChanged: 1
Bob: bean was created with id: 21
Bob: no bean found by findOne(21)
Bob: no bean found by findByName("dummy")
Tests run: 2, Failures: 2, Errors: 0, Skipped: 0, Time elapsed: 2.203 sec <<< FAILURE! - in com.example.demo.OnDuplicateKeyUpdateTests
testConcurrentFindOrCreateWithInsertIgnore(com.example.demo.OnDuplicateKeyUpdateTests)  Time elapsed: 0.296 sec  <<< FAILURE!
java.lang.AssertionError:
Expected size:<2> but was:<1> in:
<[com.example.demo.domain.Bean@5383d718]>
	at com.example.demo.OnDuplicateKeyUpdateTests.testConcurrentFindOrCreateWithInsertIgnore(OnDuplicateKeyUpdateTests.java:79)

testConcurrentFindOrCreate(com.example.demo.OnDuplicateKeyUpdateTests)  Time elapsed: 0.033 sec  <<< FAILURE!
java.lang.AssertionError:
Expected size:<2> but was:<1> in:
<[com.example.demo.domain.Bean@62e7f372]>
	at com.example.demo.OnDuplicateKeyUpdateTests.testConcurrentFindOrCreate(OnDuplicateKeyUpdateTests.java:53)
</pre>
