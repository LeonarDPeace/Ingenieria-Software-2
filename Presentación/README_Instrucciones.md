# 📋 Instrucciones para Crear Presentación PowerPoint

## 🎯 Cómo Usar los Archivos Markdown

Los archivos Markdown creados están optimizados para convertirse fácilmente en diapositivas de PowerPoint. Aquí tienes varias opciones:

---

## 🔧 Métodos de Conversión

### **Método 1: Pandoc (Recomendado)**

1. **Instalar Pandoc**:
   ```bash
   # Windows (con Chocolatey)
   choco install pandoc
   
   # O descargar desde: https://pandoc.org/installing.html
   ```

2. **Convertir archivos individuales**:
   ```bash
   pandoc 01_Introduccion_Singleton.md -o 01_Introduccion_Singleton.pptx
   ```

3. **Convertir todos los archivos a la vez**:
   ```bash
   # PowerShell
   Get-ChildItem *.md | ForEach-Object { 
       pandoc $_.Name -o ($_.BaseName + ".pptx") 
   }
   ```

---

### **Método 2: Marp (Para Presentaciones Web)**

1. **Instalar Marp**:
   ```bash
   npm install -g @marp-team/marp-cli
   ```

2. **Convertir a HTML**:
   ```bash
   marp 01_Introduccion_Singleton.md --html
   ```

3. **Convertir a PDF**:
   ```bash
   marp 01_Introduccion_Singleton.md --pdf
   ```

---

### **Método 3: Copiar y Pegar Manual**

1. **Abrir PowerPoint**
2. **Crear diapositiva nueva**
3. **Copiar contenido** del archivo .md
4. **Pegar en PowerPoint**
5. **Aplicar formato** según sea necesario

---

## 🎨 Estructura de los Archivos

Cada archivo Markdown sigue esta estructura:

```markdown
# Título Principal (Se convierte en título de diapositiva)

## Subtítulo (Se convierte en subtítulo)

### Secciones (Se convierten en bullets o nuevas diapositivas)

| Tablas | (Se mantienen como tablas en PowerPoint) |

```java
// Bloques de código (Se formatean como código)
```

**Texto en negrita** (Se mantiene el formato)
*Texto en cursiva* (Se mantiene el formato)
```

---

## 🎯 Orden de Presentación

Los archivos deben presentarse en este orden:

1. `01_Introduccion_Singleton.md`
2. `02_Cuando_Usar_Singleton.md`
3. `03_Eager_Initialization.md`
4. `04_Lazy_Initialization.md`
5. `05_Synchronized_Method.md`
6. `06_Double_Checked_Locking.md`
7. `07_Bill_Pugh_Pattern.md`
8. `08_Enum_Singleton.md`
9. `09_Comparacion_Implementaciones.md`
10. `10_Problemas_Alternativas.md`
11. `11_Casos_Reales_Empresariales.md`
12. `12_Best_Practices_Recomendaciones.md`
13. `13_Conclusiones_Preguntas.md`

---

## 🎨 Consejos de Diseño

### **Para PowerPoint**:
- Usar **tema oscuro** para código
- **Fuente monospace** para bloques de código
- **Colores contrastantes** para destacar puntos importantes
- **Animaciones simples** para bullets

### **Iconos y Emojis**:
- Los emojis se mantienen automáticamente
- Reemplazar con iconos de PowerPoint si prefieres

### **Tablas**:
- Las tablas se convierten automáticamente
- Ajustar ancho de columnas según necesidad

---

## ⚡ Script Automático de Conversión

También puedes usar este script de PowerShell para automatizar todo:

```powershell
# Navegar a la carpeta de presentación
Set-Location ".\Presentación"

# Convertir todos los archivos MD a PPTX
Get-ChildItem "*.md" | Sort-Object Name | ForEach-Object {
    Write-Host "Convirtiendo $($_.Name)..." -ForegroundColor Green
    pandoc $_.Name -o ($_.BaseName + ".pptx") --slide-level=2
}

Write-Host "¡Conversión completada!" -ForegroundColor Yellow
```

---

## 🎯 Resultado Final

Después de la conversión tendrás:
- **13 archivos .pptx** individuales (uno por diapositiva)
- **Formato consistente** en todas las diapositivas
- **Código bien formateado** y legible
- **Tablas y elementos visuales** preservados

---

## 📝 Notas Adicionales

- **Timing**: Cada archivo está diseñado para 3-5 minutos de presentación
- **Interactividad**: Incluye preguntas y puntos de discusión
- **Flexibilidad**: Puedes modificar el contenido según tu audiencia
- **Complemento**: Usar junto con el guión de exposición para mejor flujo

---

¡Listo para crear una presentación profesional del Patrón Singleton! 🚀
