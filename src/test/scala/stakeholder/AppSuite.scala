package stakeholder

class AppSuite extends munit.FunSuite {
  test("list-values exposes renderer metadata for the full registry") {
    val payload = Catalog.listValues.obj
    val families = payload("generatorFamilies").arr
    assertEquals(families.length, 45)
    assertEquals(families.head.obj("id").str, "code_analyzer")
    assertEquals(families.head.obj("rendererKey").str, "classic-six.code_analyzer")
  }

  test("deterministic same-seed json stays stable") {
    val first = ujson.write(App.focusPayload("platform_engineering", "41", "json"), indent = 2)
    val second = ujson.write(App.focusPayload("platform_engineering", "41", "json"), indent = 2)
    assertEquals(first, second)
  }

  test("cli list-values path returns registry metadata") {
    val result = App.run(List("--list-values"))
    assertEquals(result.exitCode, 0)
    assert(result.stdout.contains("\"generatorFamilies\""))
  }

  test("cli json focus-family path normalizes dashed family names") {
    val result = App.run(List("--focus-family", "platform-engineering", "--output-format", "json", "--seed", "41"))
    assertEquals(result.exitCode, 0)
    assert(result.stdout.contains("\"family\": \"platform_engineering\""))
  }

  test("focus-family is required") {
    val result = App.run(List("--output-format", "json"))
    assertEquals(result.exitCode, 2)
    assert(result.stderr.contains("focus-family is required"))
  }

  test("experimental provider fails fast") {
    val result = App.run(List("--experimental-provider", "local-demo"))
    assertEquals(result.exitCode, 2)
    assert(result.stderr.contains("experimental provider"))
  }

  test("orphan experimental flags fail fast") {
    val result = App.run(List("--experimental-mode", "api"))
    assertEquals(result.exitCode, 2)
    assert(result.stderr.contains("experimental flags require --experimental-provider"))
  }
}
