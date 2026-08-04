# FloodGuard

FloodGuard is a PaperMC plugin that helps protect Minecraft servers from fluid-based griefing by detecting flying machines and limiting suspicious fluid propagation.

## Features

- 🛡️ Prevents lava/water griefing using flying machines
- 🚀 Detects piston-based flying machines
- 🌊 Limits fluid spread when suspicious activity is detected
- ⚙️ Fully configurable detection radius and spread limits
- 📊 Statistics and status commands
- 🔄 Reload configuration without restarting the server
- 🌍 Per-world disable support
- 🧩 Supports custom fluids from other mods/plugins

## Requirements

- **Minecraft:** 1.21+
- **Server:** Paper

## Installation

1. Download `FloodGuard.jar`.
2. Place it inside your server's `plugins/` folder.
3. Restart the server.
4. Edit `plugins/FloodGuard/config.yml` if needed.
5. Reload using:

```text
/floodguard reload
```

## Commands

| Command | Description |
|---------|-------------|
| `/floodguard status` | Show plugin status |
| `/floodguard stats` | Display protection statistics |
| `/floodguard reload` | Reload the configuration |
| `/floodguard toggle` | Enable or disable FloodGuard |

Alias:

```
/fg
```

## Permissions

| Permission | Description |
|------------|-------------|
| `floodguard.admin` | Full access |
| `floodguard.command.reload` | Reload configuration |
| `floodguard.command.status` | View plugin status |
| `floodguard.command.stats` | View statistics |
| `floodguard.command.toggle` | Enable/disable the plugin |
| `floodguard.notify` | Receive FloodGuard notifications |
| `floodguard.bypass` | Ignore FloodGuard protection |

## Configuration

Example:

```yaml
enabled: true
debug: false

detection:
  fluid-radius: 3
  machine-radius: 3

spread:
  max-blocks: 2

block:
  pistons: true
  tnt: true

worlds:
  disabled: []

fluids:
  custom:
    - "create:honey"
    - "create:chocolate"
    - "twilightforest:fiery_essence"
```

### Configuration Options

| Option | Description |
|--------|-------------|
| `enabled` | Enables or disables FloodGuard |
| `debug` | Enables debug logging |
| `detection.fluid-radius` | Radius to search for nearby fluids |
| `detection.machine-radius` | Radius to detect flying machines |
| `spread.max-blocks` | Maximum allowed fluid spread |
| `block.pistons` | Block piston-based machines |
| `block.tnt` | Block TNT-assisted griefing |
| `worlds.disabled` | Worlds where FloodGuard is disabled |
| `fluids.custom` | Register additional custom fluids |

## How It Works

FloodGuard monitors:

- Piston-powered flying machines
- Nearby fluid sources
- Suspicious fluid spread patterns

When a flying machine attempts to transport fluids for griefing, the plugin can stop the machine and limit the fluid spread before significant damage occurs.

## Supported Fluids

By default, FloodGuard works with vanilla fluids and can also monitor custom fluids such as:

- Create: Honey
- Create: Chocolate
- Twilight Forest: Fiery Essence

Additional fluids can be added through the configuration.

## Compatibility

- Paper 1.21+
- Vanilla fluids
- Configurable support for custom modded fluids

## License

This project is provided as-is. Modify and distribute according to your chosen license.

---

Made for Paper servers to help prevent automated fluid griefing.
