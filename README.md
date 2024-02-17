## Concurrency in Java

I wanted to understand concurrency primitives in Java from basics. In 2020, I started reading
the book [Java Concurrency In Practice](https://jcip.net/), which I highly recommend. The first four chapters
are particularly helpful to understand the basics.

Since that time, I have been practicing problems from that book and other sources, and sharing it with the
community hoping that it would benefit you all. If you're looking at this repository, I will recommend to 
start with `OddEven` problem which has been implemented using 4-5 different approaches with comments.

Each package under concurrency has a different problem. Most if not all the problems have a driver class, which
contains the main method and has test examples for the problems. Contributions are highly welcome, and early apologies
for incomplete code. You can open an issue if you'd like better documentation on something, and I'll try to address them
to the best of my time.

## Running a problem

Right now Gradle tasks are yet to be added for each problem. For now, can run it through an IDE or by:

On project root, to run the OddEven example:

```
./gradlew build
 java -cp build/classes/java/main concurrency.oddeven.OddEvenDriver
```


