from pathlib import Path
required = [
    'README.md', 'AI_DISCLOSURE.md', 'PARITY.md', 'GAPS.md', 'AGENTS.md',
    'docs/remotes.md', 'docs/provenance.md', 'docs/toolchain.md', 'docs/traceability/first-push-families.md',
    '.githooks/commit-msg', '.githooks/pre-push', '.github/CODEOWNERS', '.github/PULL_REQUEST_TEMPLATE.md',
    '.github/dependabot.yml', '.github/workflows/actionlint.yml', '.github/workflows/dependency-review.yml',
    '.github/workflows/ci.yml', '.github/workflows/ci-native.yml', '.github/workflows/docker-smoke.yml',
    'flake.nix', 'Dockerfile', 'flake.lock', 'build.sbt', 'project/build.properties', 'project/plugins.sbt',
    'src/main/scala/stakeholder/App.scala', 'src/main/scala/stakeholder/Catalog.scala',
    'src/test/scala/stakeholder/AppSuite.scala'
]
missing = [p for p in required if not Path(p).exists()]
if missing:
    raise SystemExit('missing tranche files: ' + ', '.join(missing))
print('scala tranche validated')
