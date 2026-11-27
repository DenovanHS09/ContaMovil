from pathlib import Path
from PIL import Image

ROOT = Path(__file__).resolve().parents[2]
SOURCE = ROOT / "branding" / "contamovil-icon.png"
RES = ROOT / "app" / "src" / "main" / "res"
BG = "#001B3D"

if not SOURCE.is_file():
    raise SystemExit(f"No se encuentra el icono fuente: {SOURCE}")

with Image.open(SOURCE) as im:
    image = im.convert("RGB")
    # Normaliza a cuadrado sin deformar el arte.
    side = min(image.size)
    left = (image.width - side) // 2
    top = (image.height - side) // 2
    image = image.crop((left, top, left + side, top + side))

    # Icono adaptativo: el arte se mantiene dentro de la zona segura central.
    adaptive_size = 432
    canvas = Image.new("RGB", (adaptive_size, adaptive_size), BG)
    art = image.resize((388, 388), Image.Resampling.LANCZOS)
    canvas.paste(art, ((adaptive_size - 388) // 2, (adaptive_size - 388) // 2))
    (RES / "drawable-nodpi").mkdir(parents=True, exist_ok=True)
    canvas.save(RES / "drawable-nodpi" / "ic_launcher_foreground.png", optimize=True)

    # Iconos clásicos para Android y launchers que aún consultan mipmap por densidad.
    densities = {"mdpi": 48, "hdpi": 72, "xhdpi": 96, "xxhdpi": 144, "xxxhdpi": 192}
    for density, size in densities.items():
        folder = RES / f"mipmap-{density}"
        folder.mkdir(parents=True, exist_ok=True)
        icon = image.resize((size, size), Image.Resampling.LANCZOS)
        icon.save(folder / "ic_launcher.png", optimize=True)
        icon.save(folder / "ic_launcher_round.png", optimize=True)

# Color de fondo del icono adaptativo.
values = RES / "values"
values.mkdir(parents=True, exist_ok=True)
(values / "ic_launcher_background.xml").write_text(
    '<?xml version="1.0" encoding="utf-8"?>\n'
    '<resources>\n'
    f'    <color name="ic_launcher_background">{BG}</color>\n'
    '</resources>\n', encoding="utf-8")

# Mantiene referencias del Manifest: @mipmap/ic_launcher y @mipmap/ic_launcher_round.
for name in ("ic_launcher.xml", "ic_launcher_round.xml"):
    (RES / "mipmap-anydpi-v26" / name).write_text(
        '<?xml version="1.0" encoding="utf-8"?>\n'
        '<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">\n'
        '    <background android:drawable="@color/ic_launcher_background" />\n'
        '    <foreground android:drawable="@drawable/ic_launcher_foreground" />\n'
        '</adaptive-icon>\n', encoding="utf-8")

print("Icono ContaMovil aplicado: adaptive + mipmap mdpi/hdpi/xhdpi/xxhdpi/xxxhdpi")
  
