# Scala Toolchain

- State: deterministic first tranche implemented locally
- Toolchain source: `built-in`

## Native commands
- `scala -version`
- `sbt --script-version`
- `python3 scripts/validate_scaffold.py`
- `sbt test stage`

## Docker commands
- `docker build -t scala-stakeholder .`
- `docker run --rm scala-stakeholder --list-values`

## Current limitation
- The deterministic tranche is implemented; live-provider/runtime work remains deferred.
