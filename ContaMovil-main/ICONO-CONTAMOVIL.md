# ContaMovil – actualización del icono

Este paquete incorpora un icono de launcher basado en la imagen enviada: libro contable, calculadora y bolígrafo.

## Integración
- Sustituye los recursos de `app/src/main/res/` por los incluidos aquí (o copia esos mismos archivos conservando las rutas).
- El `AndroidManifest.xml` ya apunta a `@mipmap/ic_launcher` y `@mipmap/ic_launcher_round`, por lo que no necesita cambios.
- Se mantienen los nombres de recursos para no modificar el identificador de la aplicación ni la lógica Kotlin.
- Incluye icono adaptativo para Android 8.0+ y PNG por densidad.

## Nota
El icono se ha compuesto para legibilidad en tamaños pequeños, con fondo azul marino, libro contable verde azulado, calculadora y acento naranja. Revisa el resultado en el launcher de varios dispositivos antes de publicar.
