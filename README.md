# core-cs-lab

A BOSS plugin that gives AI agents, and the students working with them, small verifiable computer science lab tools. Built for the BOSS Contributor Hackathon 2026 (Apply track).

## Tools

### check_schedule

Checks whether a database transaction schedule is conflict-serializable.

Tool name in BOSS: `mcp__com_example_core_cs_lab__check_schedule`

Input: `schedule`, operations separated by spaces. R = read, W = write, the number is the transaction, the letter is the data item.

    R1(X) W2(X) W1(X)

Output is JSON:

    {"serializable":false,"edges":[{"from":1,"to":2,"item":"X"},{"from":2,"to":1,"item":"X"}],"cycle":[1,2,1],"serialOrder":null}

How it works: two operations conflict when they come from different transactions, touch the same item, and at least one is a write. If Ti's operation comes first, an edge Ti -> Tj is added to the precedence graph. The schedule is conflict-serializable exactly when the graph has no cycle. When it is serializable, a topological order of the graph is returned as an equivalent serial order. When it is not, the cycle is returned.

Errors: a malformed operation returns an error result naming the bad token, and a missing `schedule` argument returns an error result.

### detect_deadlock

Detects deadlock in a sequence of lock requests using a wait-for graph.

Tool name in BOSS: `mcp__com_example_core_cs_lab__detect_deadlock`

Input: `locks`, lock requests separated by spaces. LS = shared lock, LX = exclusive lock, the number is the transaction, the letter is the data item.

    LX1(A) LX2(B) LX1(B) LX2(A)

Output is JSON:

    {"deadlock":true,"cycle":[1,2,1],"waits":[{"txn":1,"item":"B","mode":"EXCLUSIVE","blockedBy":[2]},{"txn":2,"item":"A","mode":"EXCLUSIVE","blockedBy":[1]}]}

How it works: requests are processed in order. A request is granted when it is compatible with the locks other transactions currently hold (two shared locks are compatible, anything involving an exclusive lock is not). Otherwise the transaction waits, and an edge Ti -> Tj is added to the wait-for graph for each holder Tj it is blocked by. A cycle in that graph is a deadlock, and the cycle is returned.

Assumptions: locks are never released during the sequence, a transaction that is waiting issues no further requests (they are ignored), and a transaction that already holds a lock on an item can request a stronger one (an upgrade), which waits if another transaction holds a lock on that item.

### lab_ping

A connectivity check that confirms the plugin is loaded.

## Development

    ./gradlew test
    ./gradlew build
    boss plugin validate .
    boss plugin link .

The checker logic (parser, precedence graph, cycle detection, formatter) is plain Kotlin with no dependency on BOSS, so it is covered by ordinary unit tests.

## Status

Unit tests cover the parser, graph, checker, formatter and the tool handler. The plugin builds, and passes `boss plugin validate`. It has not yet been verified from an agent inside a running BOSS workspace.

## Planned

A networks lab (lossy link simulator for reliable transfer protocols) and an OS lab (scheduling and page replacement). Not implemented yet.
