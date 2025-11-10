# Challenges

## 1) Flaky JavaFX UI Tests (Timing / TestFX)

**Challenge description**  
Some UI tests failed intermittently with `EmptyNodeQueryException`. The root cause was **async JavaFX events** (layout, alerts) not finishing before TestFX queries/assertions ran, which created non-deterministic failures in CI.

**Actions and solutions attempted**  
- Added **explicit waits** for FX application/thread and node visibility before assertions.  
- Extracted shared **helper methods** (e.g., open dialog, fill fields, wait for table rows) to remove duplication and ensure consistent timing.  
- Wrote two focused tests to validate edge cases (missing repetitions alert; multiple row additions) and verify the new helpers.  
- Kept PRs small so any remaining flakes were easy to isolate and fix.

**Lessons learned**  
GUI tests must synchronize with the UI toolkits they drive. Centralized helpers and deterministic waits make tests robust and easier to extend. Flakiness dropped, reviews sped up, and failures now point to real regressions rather than timing noise.

---

## 2) Quality Gates Friction (SpotBugs / CI)
**Challenge description**  
Static analysis and CI checks (SpotBugs, style, and tests) occasionally turned small changes into long fix–rebuild cycles. Findings were valid, but the feedback loop sometimes slowed the team.

**Actions and solutions attempted**  

- Tuned SpotBugs: used **targeted suppressions/annotations** for false positives instead of broad disables.  
- Preferred **small, focused PRs** so issues were easier to fix and review.  
- Adopted a “Definition of Done” checklist (build, tests, SpotBugs clean) and shared quick-fix snippets in the wiki.  
- Ran checks **locally** before pushing (pre-commit/IDE tasks) to reduce CI back-and-forth.

**Lessons learned**  
Quality gates work best when they’re **predictable** and **close to the developer**. Clear rules, fast local checks, and small PRs turn static analysis from a blocker into a safety net. The team also built a shared understanding of patterns that pass on the first try.
