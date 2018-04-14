# Coroutines vs Futures comparison

Comparing coroutines and futures for async operations.
- basic async
- multiple async
- async with control flow

# Coroutines
## Pros
- Handles asynchronicity with less boilerplate than "Promise-like" solutions
- Is more expressive than Futures thanks to it's async-abstracting nature

## Cons
- You have to be *very careful* to not execute coroutines sequentially (see "Multiple Coroutines" test code). 
This issue does *not* happen with Futures since the return type makes multiple async operations 
handling explicit (aka: having a List<Future<T>>).
- Coroutines are not types so they can't be abstracted over. (is this a con are a pro ?)
- They need language-level syntax (suspend). Green-thread seem to be a more elegant solution since they represent only 
data/types/primitives at the language-level and the parallelism is effective are in effect only at runtime. 

# Futures
## Pros
- They are just simple types
- Parallelism is very explicit (sequence, traverse, applyN ...)

## Cons
- Needs some getting used to
- Lots of boilerplate compared to synchonous code