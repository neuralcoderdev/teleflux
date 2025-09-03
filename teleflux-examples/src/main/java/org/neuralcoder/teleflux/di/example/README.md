# Teleflux DI — Lightweight Dependency Injection

Minimalistic, compile-time assisted DI for Teleflux.  
Focus: simple annotations, fast startup, no reflection-heavy magic at runtime.

- ✅ Constructor & field injection
- ✅ `Provider<T>` / collections (`List<T>`, `Set<T>`)
- ✅ Qualifiers (including meta-annotations)
- ✅ `@Primary`, `@Order`
- ✅ Profiles (`-Dteleflux.profiles=...`)
- ✅ Modules (`@Module` + `@Provides`)
- ✅ Config binding (`@Config`, `@ConfigProperty`)
- ✅ `@Lazy` proxies (interfaces)
- ✅ Eager init (`@Eager`)
- ✅ Clear errors: `NoSuchBeanException`, `AmbiguousBeanException`, `CyclicDependencyException`

---

## Quick start

**Bootstrap**

```java
BeanFactory bf = DefaultBeanFactory();
bf.start();

var app = bf.get(AppService.class);
app.run();

bf.stop();
```

**Run with profiles & config:**

```bash
java \
-Dteleflux.profiles=dev \
-Dteleflux.example.greetingPrefix=Hola \
-Dteleflux.example.maxRetries=5
```
---

## Annotations
`@Component` / `@Service`
--
Mark classes as DI-managed. value = optional qualifier.

```java
@Service
@Order(10)
public class PlainGreeter implements Greeter {
public String greet(String name) { return "Hello, " + name + "!"; }
}

@Service
@Primary
@Order(5)
public class FancyGreeter implements Greeter {
public String greet(String name) { return "✨ Hello, " + name + " ✨"; }
}
```

`@Inject`
--
On constructor or field.

```java
import org.neuralcoder.teleflux.di.api.annotations.Inject;
import org.neuralcoder.teleflux.di.example.service.Greeter;
import org.neuralcoder.teleflux.di.example.service.Notifier;

public class GreeterService {

    private final Greeter greeter;

    @Inject
    Notifier greeter;

    @Inject
    public GreeterService(Greeter greeter) {
        this.greeter = greeter;
    }
}
```

`@Qualifier` **(meta-annotation)**
--
Define your own qualifier and use it on beans/params.
```java
@Qualifier
public @interface Channel { String value(); }

@Service @Channel("primary")
public class PrimaryNotifier implements Notifier { /* ... */ }

@Service @Channel("backup")
public class BackupNotifier implements Notifier { /* ... */ }
```

`@Primary` / `@Order`
--
`@Primary`: wins when multiple beans implement same contract.
`@Order(int)`: lower = higher priority, used in lists and as tie-breaker.

`@Profile("name")`
--
Bean is only active if profile enabled.
```java
@Service
@Profile("dev")
public class DevOnlyService { /* ... */ }
```

Run: `-Dteleflux.profiles=dev`

`@Eager`
--
Force pre-instantiation at container startup.


`@Lazy`
--
Inject lazy JDK proxy (only for interfaces).

```java
public class AppService {
  @Lazy
  private Notifier notifier;

  public void run() {
    notifier.notify("[app started]"); // created on first call
  }
}
```

For classes: use `Provider<T>`


`@Module` + `@Provides`
--
Factory-style beans.

```java
@Module
public class ExampleModule {

  @Provides
  public String banner(Greeter g) {
    return "<<< TELEFLUX >>>\n" + g.greet("Teleflux User");
  }

  @Provides
  public Notifier defaultNotifier(
      @Channel("primary") Notifier primary,
      @Channel("backup") Notifier backup) {
    return primary != null ? primary : backup;
  }
}
```

`@Config` + `@ConfigProperty`
---
Bind config fields.

```java
@Config(prefix = "teleflux.example")
public class AppConfig {
  @ConfigProperty("teleflux.example.greetingPrefix")
  String greetingPrefix = "Hello";

  @ConfigProperty(defaultValue = "3")
  int maxRetries;
}
```

---
## Injection types

`T dep` — plain bean

`Provider<T>` — lazy retrieval

`List<T>` — all beans, ordered

`Set<T>` — all beans, no duplicates

`@Lazy T` — proxy for interface

---
## Error handling

`NoSuchBeanException` — no provider for type

`AmbiguousBeanException` — multiple candidates, no clear winner

`CyclicDependencyException` — cycle detected in graph

---
## License

Teleflux © NeuralCoder. All rights reserved.

---