# Shade

Клиентский мод для Minecraft 1.16.5 (Fabric). Утилиты, визуал, косметика.

## Возможности

**Combat** — KillAura, SilentAim, Reach, Velocity
**Render** — ESP, XRay, Tracers, Nametags, NoRender
**Cosmetic** — Snow, Halo
**Movement** — Sprint
**GUI** — меню по Right Shift, настройки в слайдерах, сохранение в JSON

## Установка

1. Fabric Loader 0.11.6 для 1.16.5
2. Fabric API 0.42.0+1.16 — в `.minecraft/mods/`
3. Скачать `shade-1.0.0.jar` из [Releases](../../releases) — в `.minecraft/mods/`
4. Запустить через профиль `fabric-loader-0.11.6-1.16.5`

## Управление

| Клавиша | Что |
|---|---|
| Right Shift | Открыть/закрыть меню |
| ЛКМ в меню | Включить модуль |
| ПКМ в меню | Раскрыть настройки |

## Сборка из исходников

```bash
./gradlew build
```

Готовый jar — в `build/libs/shade-1.0.0.jar`.

## Требования

- JDK 17
- Minecraft 1.16.5 + Fabric Loader 0.11.6
- Fabric API 0.42.0+1.16
