# Problem Statement

A barber shop has 1 barber and 1 chair for a customer. It has `n` other chairs for customers to wait. We have
to implement two APIs `addCustomerToWaitList(Customer c)` and `serveCustomer(Customer c)` that indicate actions associated
a customer waiting and getting served respectively. Below are the constraints:

A customer shows up:
- If barber chair and all chairs are occupied, customer leaves
- If a chair is available to sit or wait, then the customer will wait
- If a barber doesn't have anyone to service, he/she goes to sleep
- If a barber is asleep and customer walks in, they'd wake up the barber
