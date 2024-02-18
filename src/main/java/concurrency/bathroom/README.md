# Problem statement

You are given a bathroom which has 3 toilets. You have to implement a thread safe API that makes sure that at any
point in time, there are only single type of people (either type A or type B). Like if the bathroom state is AA,
you cannot allow B until the bathroom state resets to empty. Similarly you cannot allow a fourth person if the
state is already full like AAA, and you can only make that fourth person wait until the bathroom is empty again.
The requests keep on coming and every person takes a constant amount of time to use the bathroom and then leaves.
