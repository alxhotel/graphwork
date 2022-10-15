GraphWork
=================

GraphWork is a project for URJC.

Warning: there are 3 corrupted graph files, from the "large" set.

## Usage

Edit the file `Main.java` to run any graph file you want.

Format of the graph file:

```
[num of vertices] [num of edges]
[vertex A] [vertex B] [weight of edge]
[vertex C] [vertex D] [weight of edge]
[vertex E] [vertex F] [weight of edge]
....
[vertex N] [vertex M] [weight of edge]
```

And then run `Main.java` to see the results.

## Tests

If you want to test all of the current graphs available in the repo, you can run all the JUnit tests.

Tests check if the resulting graph is a tree and cover of the original.

For now they are only executed based on "the most connected" criteria.

It take aprox. 5 sec. in a normal specs laptop.

## License

MIT. Copyright (c) [Alex](http://github.com/alxhotel)
