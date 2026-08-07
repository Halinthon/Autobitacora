# Bitácora Vehicular

App Android nativa (Kotlin + Jetpack Compose + Room) para llevar el control completo de tus vehículos: matrícula, mantenimiento, incidentes, documentos (SOAT y tecnomecánica), impuestos, otros pagos y enlaces de interés.

## Funcionalidades

- Registro de uno o varios vehículos (moto/auto)
- Mantenimiento: cambios de aceite (con cálculo automático del próximo cambio a +5.000 km), reparaciones y compra de autopartes
- Registro de incidentes (pinchazo, mecánica, eléctrico, golpe)
- Documentos: SOAT y tecnomecánica, con vencimiento automático a 1 año de la expedición
- Impuestos y otros pagos
- Enlaces de interés a modo de bitácora
- Fotos/comprobantes adjuntos en cada registro
- Registro de kilometraje (odómetro)
- Notificaciones automáticas de vencimientos próximos (documentos y cambio de aceite)
- Resumen de gastos por categoría
- Exportar e importar un respaldo completo en JSON
- Todo el almacenamiento es 100% local (Room/SQLite), sin necesidad de internet ni servidores

## Cómo generar el APK

### Opción A: GitHub Actions (recomendado, no requiere instalar nada)

1. Sube este proyecto a un repositorio de GitHub (ver instrucciones más abajo).
2. Ve a la pestaña **Actions** del repositorio.
3. El workflow **"Compilar APK"** se ejecuta automáticamente con cada `push` a la rama `main`, o puedes lanzarlo manualmente con el botón **"Run workflow"**.
4. Al finalizar (2-4 minutos), entra al resumen de la ejecución y descarga el artefacto **bitacora-vehicular-apk**, que contiene el `app-debug.apk`.
5. Instala ese APK en tu celular (activa "Instalar apps de fuentes desconocidas" si te lo pide).

### Opción B: Android Studio

1. Abre Android Studio → **Open** → selecciona la carpeta del proyecto.
2. Espera a que sincronice Gradle (la primera vez descarga dependencias, requiere internet).
3. Conecta tu celular por USB (con depuración USB activada) o usa un emulador.
4. Presiona **Run ▶**.

## Estructura del proyecto

```
app/src/main/java/com/bitacora/vehicular/
 ├─ data/          Entidades, DAOs y base de datos (Room)
 ├─ viewmodel/      Lógica de negocio y estado (MVVM)
 ├─ notification/   Recordatorios de vencimientos (WorkManager)
 ├─ util/           Manejo de fotos y respaldo (backup JSON)
 └─ ui/
     ├─ theme/       Colores y tema Material3
     ├─ components/  Componentes reutilizables (campos, tarjetas, diálogos)
     ├─ screens/     Pantallas de cada sección
     └─ navigation/  Navegación y barra inferior
```

## Notas

- Todos los datos viven únicamente en el dispositivo. Usa la sección **Más → Respaldo de datos** para exportar un backup periódicamente.
- El intervalo del próximo cambio de aceite (5.000 km) está definido en `MantenimientoScreen.kt` como `KM_INTERVALO_ACEITE`, por si quieres ajustarlo.
