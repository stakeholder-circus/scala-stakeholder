# scala-stakeholder Status

- Role: tranche-A deterministic Scala parity port
- Parity class: full-parity
- State: native-validated local deterministic tranche
- Rewrite completeness: 72%
- Functionality completeness: 56%
- Branch: `main`
- Origin: `git@github.com:stakeholder-circus/scala-stakeholder.git`
- Upstream: `https://github.com/giacomo-b/rust-stakeholder`

## Evidence
- `python3 scripts/validate_scaffold.py`
- `sbt --batch 'test; stage'`
- `./target/universal/stage/bin/scala-stakeholder --list-values`
- same-seed deterministic JSON diff for `platform-engineering`
- explicit `--experimental-provider local-demo` fail-fast smoke

## Blockers
- Full live-provider/runtime support is deferred to the second-pass provider rollout wave.
- Publication remains blocked until the publication/governance wave can actually access `stakeholder-circus`.

## Next
- Keep the deterministic family-focused contract stable.
- Preserve grouped fallback later families until the next widening wave.
- Bind remote CI checks only after publication is possible.

## Canonical references
- `/Users/davidsupan/shareholder/stakeholder-core/docs/program/index.md`
- `/Users/davidsupan/shareholder/stakeholder-core/docs/program/next-20-wave.md`
