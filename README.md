# core-cs-lab

[![build](https://github.com/shineyrbl-byte/boss-plugin-core-cs-lab/actions/workflows/build.yml/badge.svg)](https://github.com/shineyrbl-byte/boss-plugin-core-cs-lab/actions/workflows/build.yml)

A BOSS plugin that gives AI agents, and the students working with them, small verifiable computer science lab tools. Built for the BOSS Contributor Hackathon 2026 (Apply track).

## Tools

### check_schedule

Checks whether a database transaction schedule is conflict-serializable.

Tool name in BOSS: `mcp__io_github_shineyrbl_byte_core_cs_lab__check_schedule`

Input: `schedule`, operations separated by spaces. R = read, W = write, the number is the transaction, the letter is the data item.

    R1(X) W2(X) W1(X)

Output is JSON:

    {"serializable":false,"edges":[{"from":1,"to":2,"item":"X"},{"from":2,"to":1,"item":"X"}],"cycle":[1,2,1],"serialOrder":null}

How it works: two operations conflict when they come from different transactions, touch the same item, and at least one is a write. If Ti's operation comes first, an edge Ti -> Tj is added to the precedence graph. The schedule is conflict-serializable exactly when the graph has no cycle. When it is serializable, a topological order of the graph is returned as an equivalent serial order. When it is not, the cycle is returned.

Errors: a malformed operation returns an error result naming the bad token, and a missing `schedule` argument returns an error result.

### detect_deadlock

Detects deadlock in a sequence of lock requests using a wait-for graph.

Tool name in BOSS: `mcp__io_github_shineyrbl_byte_core_cs_lab__detect_deadlock`

Input: `locks`, lock requests separated by spaces. LS = shared lock, LX = exclusive lock, the number is the transaction, the letter is the data item.

    LX1(A) LX2(B) LX1(B) LX2(A)

Output is JSON:

    {"deadlock":true,"cycle":[1,2,1],"waits":[{"txn":1,"item":"B","mode":"EXCLUSIVE","blockedBy":[2]},{"txn":2,"item":"A","mode":"EXCLUSIVE","blockedBy":[1]}]}

How it works: requests are processed in order. A request is granted when it is compatible with the locks other transactions currently hold (two shared locks are compatible, anything involving an exclusive lock is not). Otherwise the transaction waits, and an edge Ti -> Tj is added to the wait-for graph for each holder Tj it is blocked by. A cycle in that graph is a deadlock, and the cycle is returned.

Assumptions: locks are never released during the sequence, a transaction that is waiting issues no further requests (they are ignored), and a transaction that already holds a lock on an item can request a stronger one (an upgrade), which waits if another transaction holds a lock on that item.

### check_recoverability

Classifies a transaction schedule with commits as strict, cascadeless, recoverable or not recoverable.

Tool name in BOSS: `mcp__io_github_shineyrbl_byte_core_cs_lab__check_recoverability`

Input: `schedule`, operations separated by spaces. R = read, W = write, C = commit, the number is the transaction, the letter is the data item.

    W1(X) R2(X) C1 C2

Output is JSON:

    {"level":"recoverable","recoverable":true,"cascadeless":false,"strict":false,"readsFrom":[{"reader":2,"writer":1,"item":"X"}],"violations":[{"type":"dirty_read","txn":2,"dependsOn":1,"item":"X"}]}

How it works: the schedule is scanned in order while tracking who last wrote each item and which transactions have committed. The levels nest: every strict schedule is cascadeless, and every cascadeless schedule is recoverable.

- Reads from: Tj reads the value last written by a different transaction Ti.
- Recoverable: whenever Tj reads from Ti and Tj commits, Ti must have committed first. Violation type `commit_before_source`.
- Cascadeless: transactions only read data written by committed transactions. Violation type `dirty_read`.
- Strict: transactions neither read nor overwrite data written by uncommitted transactions. Violation type `dirty_overwrite`.

Assumptions: the input has no aborts, a transaction with no commit is treated as still running, and a transaction cannot act after it commits.

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

Two more labs are sketched but not built yet: a networks lab (a lossy-link simulator for reliable transfer protocols like stop-and-wait or go-back-N) and an OS lab (CPU scheduling and page replacement algorithms). Neither is part of this submission.
