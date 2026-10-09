# AforoControl IoT — Sistema de Control de Aforo en Tiempo Real

Proyecto desarrollado para la **Evaluación Sumativa N°2** de la asignatura **Aplicaciones Móviles para IoT (TI3V42)**.  
**INACAP Sede Puente Alto** — Primavera 2026.

---

## 👥 Integrantes y Datos Académicos
* **Samuel Morales**
* **Camilo Candia**
* **Sección:** 114-4B-F1
* **Docente:** Michael Arjel
* **Asignatura:** Aplicaciones Móviles para IoT

---

## 📱 Descripción del Proyecto
**AforoControl IoT** es una aplicación móvil Android diseñada para el monitoreo y control inteligente de aforo de personas en recintos cerrados de la Sede INACAP (Auditorio, Biblioteca, Laboratorios IoT, Casino Central, etc.).

La aplicación se conecta en tiempo real a **Firebase** para simular la captura de datos de sensores IoT de conteo de personas, garantizando comunicación segura bajo estándares de la familia ISO 27000.

---

## 🚀 Características Principales
1. **Autenticación Segura (Firebase Auth + Firestore):**
   * Registro con validaciones locales estrictas (formato de correo con dominio, contraseña mínima de 6 caracteres y confirmación).
   * Guardado automático del perfil en la colección `usuarios` de Firestore.
   * Inicio de sesión con correo/contraseña y soporte para Google Sign-In.
2. **Identidad del Negocio:**
   * Nombre e icono personalizado de control de aforo / IoT.
   * Pantalla de bienvenida personalizada con el correo del usuario y resumen del servicio.
3. **Preferencias del Negocio:**
   * Switch: *"Activar alerta acústica por sobrecupo"*.
   * Spinner: Selección de salas/zonas monitoreadas de la Sede Puente Alto.
   * RadioGroup: Umbrales de alerta temprana (80%) y crítica (100%).
4. **CRUD en Tiempo Real (Cloud Firestore):**
   * **Colección Firestore:** `aforos`
   * **Modelo de Datos:** `RegistroAforo` con `@DocumentId`, `zona`, `cantidadPersonas` (campo numérico del sensor IoT), `nivelAforo`, `fechaHora` y `uid`.
   * **Actualización en Tiempo Real:** Implementación de `addSnapshotListener` con RecyclerView para reflejar altas, bajas y modificaciones al instante sin recargar.
   * **Validación de Formularios:** Prevención de guardado con campos vacíos.
   * **Eliminación Segura:** Diálogo de confirmación antes de eliminar documentos.

---

## 🏗️ Arquitectura del Código
El código se organiza siguiendo las mejores prácticas en tres paquetes principales:
```text
com.example.aplicacionconteodepersonas
├── models/
│   ├── RegistroAforo.kt      # Modelo de aforo con anotación @DocumentId
│   └── Usuario.kt            # Modelo de perfil de usuario
├── views/
│   ├── MainActivity.kt       # Pantalla de Login
│   ├── RegistroActivity.kt   # Pantalla de Registro
│   ├── BienvenidaActivity.kt # Pantalla de Bienvenida
│   ├── PreferenciasActivity.kt# Preferencias del negocio
│   └── ListaActivity.kt      # Lista y CRUD de aforo en tiempo real
└── adapters/
    └── AforoAdapter.kt       # Adaptador para RecyclerView
```

---

## 🛠️ Tecnologías Utilizadas
* **Lenguaje:** Kotlin
* **Entorno:** Android Studio Ladybug / Koala
* **SDK:** Min SDK 24 / Target SDK 37 / Java 11
* **Bases de Datos & Backend:** Firebase Authentication, Cloud Firestore
* **Diseño:** Material Design 3, ViewBinding, ConstraintLayout, RecyclerView
