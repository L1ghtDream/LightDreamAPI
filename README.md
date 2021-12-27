# LightDream API

![Build](https://github.com/L1ghtDream/LightDreamAPI/actions/workflows/build.yml/badge.svg)

```xml
<repositories>
    <repository>
        <id>lightdream-repo</id>
        <url>https://repo.lightdream.dev/repository/LightDream-API/</url>
    </repository>
    <!-- Other repositories -->
</repositories>
```

```xml
<dependencies>
    <dependency>
        <groupId>dev.lightdream</groupId>
        <artifactId>LightDreamAPI</artifactId>
        <version>VERSION</version>
        <scope>provided</scope>
    </dependency>
    <!-- Other dependencies -->
</dependencies>
```

## Dependencies

- Vault - 1.7+
- PlaceholderAPI - 2.10.10+
- WorldEdit - 6.1.3+

## Versioning

- All versions can be found in the [repository](https://repo.lightdream.dev/#browse/browse:LightDream-API:dev%2Flightdream%2FLightDreamAPI)
- 1.x only support provided jar in /plugins as a limitation of the API
- 2.x support both provided jar in /plugins and shaded jar into the plugin
- 3.x support both provided jar in /plugins and shaded jar into the plugin and have more default additions like:
    - Multi-lang support
    - More base commands
    - More optimized database access
- 4.x Added hikari database manager


