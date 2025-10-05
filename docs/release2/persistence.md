# Persistence

Denne filen dokumenterer filformatet for brukerdata og den valgte persistensmodellen i **Workout Tracker**-applikasjonen.

## Oversikt

- **Kode som håndterer lagring**: `workout-tracker/persistence/src/main/java/persistence/WorkoutFileManager.java`
- **UI som trigger lagring/lasting**: 
  - Lasting ved oppstart i `ui/AppController.initialize()` (via `WorkoutFileManager.loadWorkouts()`).
  - Manuell lagring via knapp i `ui/App.fxml` → `AppController.handleSave()`.
- **Standard filsti for data**: `workout-tracker/data/workouts.json`
- **Innstillinger**: Ingen egen settings-fil per nå.

## Filformat (brukerdata)

- **Format**: JSON
- **Struktur**: Hele filen er en **liste** av `Workout`-objekter.
- **Felter per element** (fra `core/Workout.java`):
  - `type`: `string`
  - `notes`: `string`
  - `date`: `array[int,int,int]` = `[YYYY, MM, DD]` (fra Jackson `JavaTimeModule` sin standard for `LocalDate`)
  - `time`: `number` (timer)

**Eksempel** (fra `data/workouts.json`):

```json
[
  {
    "type": "Pull",
    "notes": "Workout Session 2",
    "date": [
      2025,
      10,
      2
    ],
    "time": 1.0
  },
  {
    "type": "Push",
    "notes": "Workout Session 1",
    "date": [
      2025,
      9,
      30
    ],
    "time": 3.0
  }
]
```

## Persistensmodell

- **Valg**: **Hybrid**
  - **Implicit last**: Ved oppstart lastes eventuelle eksisterende økter automatisk fra `workouts.json`.
  - **Explicit save**: Brukeren må trykke **Save**-knappen for å skrive nåværende liste til fil (ingen autosave).
- **Begrunnelse**:
  - Minimal friksjon ved oppstart (data dukker opp uten brukerhandling).
  - Tydelig kontroll og “milepæl”-følelse ved lagring (bruker vet når filen oppdateres).
  - Reduserer risikoen for å overskrive med halvferdige endringer sammenlignet med ren autosave.

### Trade-offs og alternativer

- **Document/explicit (kun manuelt)**  
  + Klare lagringspunkt, godt for versjonskontroll.  
  − Krever at brukeren husker å lagre; fare for datatap ved krasj.

- **Implicit/app (kun autosave)**  
  + Null friksjon; lav risiko for tap ved krasj.  
  − Mindre forutsigbarhet; vanskeligere å “forkaste” arbeid.

- **Hybrid (valgt)**  
  + Kombinerer enkel oppstart med bevisst lagring.  
  − Endringer som ikke blir lagret før avslutning kan gå tapt.

## Hvorfor JSON?

- **Fordeler**: Lesbart, diff-vennlig i Git, enkel eksport/import, bred biblioteksstøtte (Jackson).
- **Ulemper**: Ingen kommentarer i formatet; kan bli tungt som monofil ved veldig store datamengder.
- **Mulige alternativer (kort)**:
  - **YAML**: Mer menneskevennlig syntaks, men mer skjør parsing.
  - **SQLite**: Skaler godt og muliggjør spørringer; dårligere git-diff og “dokument”-flyt.
  - **Binary/Protobuf**: Kompakt og versjonerbart; ikke menneskelesbart.

## Tekniske detaljer

- **Skriving/lesing**: Utføres i `WorkoutFileManager` med `ObjectMapper`. `LocalDate` støttes via `JavaTimeModule`.
- **Filoppløsning**: `AppController.initialize()` velger `workouts.json` relativt til prosjektkatalogen.
- **Validering**: `core.Workout` validerer felter i konstruktør/settere; innleste objekter må passere denne valideringen.

