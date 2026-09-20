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
