# Loot Log

**The most feature-rich and configurable pickup notifier available for Minecraft.**

Loot Log is a highly customizable pickup notifier HUD for items and XP. Smart item stacking, live count updates, smooth animations, custom banner textures with animation support, per-item overrides, visual effects, filtering, extensive layout control, and EmbersTextAPI compatibility for rich text effects -- all configurable down to the last detail.

---

## Features

### Pickup Tracking
- Real-time item and XP pickup notifications
- Smart stacking of duplicate items with pickup count (+3) and total inventory count (x64)
- Three combine modes: **Always** merge duplicates, **Never** merge, or **Exclude Named** items
- Count abbreviation for large numbers (1,500 → "1.5K")

### Animated & Polished
- Smooth fade-in/fade-out with configurable duration
- Slide-in animation from screen edges
- Scale pop-in entrance effect
- Stagger delay for cascading item appearance
- Four easing functions: Quad Out, Cubic Out, Back Out, Elastic Out

### Highly Customizable Visuals
- **5 background styles:** None, Solid, Tooltip (vanilla style), Texture (9-slice, resource pack friendly), Banner (multi-layer textured, default), and Flat
- **Custom banner textures** with multi-layer support (body + accent), independent offsets, and animated texture strips
- Banner accent anchoring options (icon, center, edge) for precise visual control
- **Icon glow** with configurable color, radius, shape (circle, square, diamond, item silhouette), softness, and pulsing
- **Icon shadow** with offset and blur
- **Icon bounce** on pickup
- **Pickup pulse** animation with per-element strength controls
- **Progress bar** showing remaining display time
- Rarity-based text coloring (Common, Uncommon, Rare, Epic)
- Full text shadow control

### Positioning & Layout
- Anchor to any screen corner (top-left, top-right, bottom-left, bottom-right)
- Configurable X/Y offset, entry spacing, and growth direction
- Global HUD scaling (0.25x to 4x)
- Reorder layout elements: icon, name, pickup count, total count
- Screen clamping to prevent off-screen rendering

### Filtering
- Item blacklist and whitelist by item ID
- Mod blacklist and whitelist by namespace
- Force-show specific items that would otherwise be filtered

### Sound Notifications
- Optional sound on pickup with configurable sound ID, volume, and pitch

### Per-Item Overrides (JSON)
Create JSON files in `config/lootlog/overrides/` to customize behavior for specific items:
- Match by **item ID**, **tag**, **mod namespace**, **rarity**, or **regex**
- Override display duration, text, sound, scale, background style, combine mode, and all visual effects per item
- Priority system for controlling which overrides take precedence

### EmbersTextAPI Support
Compatible with [EmbersTextAPI](https://www.curseforge.com/minecraft/mc-mods/emberstextapi) for rich text effects in item names via per-item overrides -- use tags like `<rainbow>`, color codes, and more to make specific pickups stand out.

### Config GUI
Full in-game configuration screen when [YACL](https://www.curseforge.com/minecraft/mc-mods/yacl) is installed. On Fabric, also integrates with [Mod Menu](https://www.curseforge.com/minecraft/mc-mods/modmenu).

---

## Supported Versions

| Minecraft | Forge | NeoForge | Fabric |
|-----------|-------|----------|--------|
| 1.20.1    | Yes   | -        | Yes    |
| 1.21.1    | -     | Yes      | Yes    |

---

## Dependencies

**Required:** None (beyond Minecraft and your mod loader)

**Optional:**
- [YACL v3.3+](https://www.curseforge.com/minecraft/mc-mods/yacl) — In-game config GUI
- [Mod Menu](https://www.curseforge.com/minecraft/mc-mods/modmenu) — Config button in the mod list (Fabric)
- [EmbersTextAPI](https://www.curseforge.com/minecraft/mc-mods/emberstextapi) — Rich text effects (rainbow, colors, etc.) in pickup names

---

## Configuration

All settings are stored as JSON in your config folder and are auto-generated on first launch. Categories include:
- **General** — Display duration, max entries, combine mode, item/XP toggles
- **Position** — Anchor corner, offsets, spacing, scale
- **Animation** — Fade, slide, scale entrance, easing, stagger
- **Appearance** — Background style, colors, text options, icon position
- **Banner Layout** — Element ordering and gaps for Banner/Flat styles
- **Effects** — Icon glow, shadow, bounce, pickup pulse, progress bar
- **Sound** — Toggle, sound ID, volume, pitch
- **Filtering** — Item and mod blacklists/whitelists

---

*Client-side only. Does not require installation on servers.*
