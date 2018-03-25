# Coroutines vs Futures comparison

Comparing coroutines and futures for async operations.
- basic async
- multiple async
- async with control flow


Coroutines are reflected in the signature (suspend) but not in the returned type and thus cannot be abstracted over.
You have to be very careful to not execute coroutines sequentially. This issue does not happen with Futures since
the return type makes multiple async operations handling explicit (aka: having a List<Future<T>>).