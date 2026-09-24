# Lista zmian
Wszystkie istotne zmiany w projekcie są dokumentowane w tym pliku.

Format oparty na [Keep a Changelog](https://keepachangelog.com/pl-PL/1.1.0/),
zgodny z zasadami [Semantic Versioning](https://semver.org/lang/pl/spec/v2.0.0.html).

---

## [Następna wersja]
### Planowane

- Opcjonalny tryb „Compact Mode” dla ekranu konfiguracji.
- Wsparcie dla lokalizacji (automatyczne wykrywanie polski / angielski).
- Drobne poprawki wizualne w opisie moda w Mod Menu.

---
## [1.2.2] - 2026-09-24

### Zaktualizowano

* Dodano pełną kompatybilność z Minecraft **26.3**.
* Jeden plik JAR obsługuje teraz Minecraft **26.1.2, 26.2 oraz 26.3**.

### Naprawiono

* Naprawiono brak wizualnego odświeżania stanu globalnych przycisków konfiguracji w Minecraft 26.1.2.
* Naprawiono brak natychmiastowego odświeżania ekranu po użyciu **Restore Vanilla** w Minecraft 26.1.2.
* Poprawiono działanie **Save & Quit** we wszystkich obsługiwanych wersjach Minecrafta.

### Kompatybilność

* Minecraft **26.1.2 – 26.3**
* Fabric Loader **0.19.3+**
* Mod wyłącznie po stronie klienta.
---

## [1.2.1] - 2026-07-03

### Zaktualizowano

* Zaktualizowano kompatybilność do Minecraft **26.2**.
---
## [1.2.0] - 2026-05-08
### Zaktualizowano
- Pełna migracja do systemów renderowania i GUI Minecraft 26.1.2.
- Przeniesiono cały projekt z mappingów Yarn na oficjalne mappingi Mojang.
- Przebudowano cały ekran konfiguracji w oparciu o nową architekturę `ContainerObjectSelectionList`.
- Unowocześniono układ oraz zachowanie scrollbara dla lepszej kompatybilności z przyszłymi zmianami GUI Minecrafta.
- Ulepszono integrację z Mod Menu oraz responsywność interfejsu.
---

## [1.1.2] - 2026-01-08
### Zaktualizowano
- Aktualizacja kompatybilności do Minecraft 1.21.11
- Brak zmian w mechanice lub zachowaniu moda
- Poprawki techniczne i aktualizacja środowiska Fabric

## [1.1.0] - 2025-10-11
### Zaktualizowano
- Pełna kompatybilność z **Minecraft 1.21.9 / 1.21.10**.
- Aktualizacja do **Fabric Loader**, **Fabric API** oraz **Mod Menu 11.0.1**.
- Migracja obsługi interfejsu (`Click` events, scrollbary) do nowych mapowań Fabric.
- Poprawiono konfigurację Gradle — numer wersji moda jest teraz automatycznie wstawiany do `fabric.mod.json`.

### Naprawiono
- Wersja moda w Mod Menu jest teraz poprawnie wyświetlana (zamiast placeholdera).
- Usunięto błąd powodujący crash klienta przy wywołaniu `World.isClient` w nowych mapowaniach.

### Uwagi
- Mod wyłącznie po stronie klienta.
- Przetestowano z **Mod Menu 11.0.1** i **Fabric API** dla **MC 1.21.9+**.

### Kompatybilność
- W pełni kompatybilny z **Minecraft 1.21.9 – 1.21.10**
- Może częściowo działać na wcześniejszych wersjach 1.21.x (1.21.1–1.21.8), lecz nie jest to oficjalnie wspierane.
- Wymaga Fabric Loader 0.16.0 lub nowszego.

### Środowisko testowe
- Minecraft **1.21.9**
- Fabric Loader **0.16.14**
- Fabric API **0.110.0+1.21.9**
- Mod Menu **11.0.1**
- Java **21 (Temurin 21.0.5 LTS)**
- IDE: **IntelliJ IDEA 2024.2 / VS Code**
---

## [1.0.0] - 2025-08-17
### Dodano
- Ekran konfiguracji z przełącznikami **dla każdego bloku** i **dla efektu** (Płomień / Dym):  
  Pochodnia, Pochodnia ścienna, Czerwona pochodnia, Czerwona pochodnia ścienna, Duszna pochodnia, Duszna pochodnia ścienna, Świece, Piec, Wędzarka, Piec hutniczy.
- Przełączniki globalne: „Wszystkie płomienie”, „Wszystkie dymy”, **Zastosuj dla wszystkich**, **Przywróć oryginał**.
- Integracja z Mod Menu (opcjonalna).
- Interfejs przyjazny paczkom zasobów (vanilla separator i suwak przewijania).

### Uwagi
- Mod wyłącznie po stronie klienta.
- Działa z wersjami 1.21.x.